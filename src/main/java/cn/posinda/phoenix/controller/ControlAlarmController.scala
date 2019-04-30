package cn.posinda.phoenix.controller

import java.sql.ResultSet
import java.util
import javax.annotation.Resource
import javax.servlet.http.HttpServletResponse

import cn.posinda.base.{PageQuery, WebResult}
import cn.posinda.mysql.service.InterceptorFieldService
import cn.posinda.phoenix.helper.QueryCondition
import cn.posinda.phoenix.service.{ControlAlarmService, TableInfoService}
import cn.posinda.phoenix.tools.{ArgumentValidation, Excels}
import com.alibaba.druid.pool.DruidDataSource
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.{GetMapping, RequestMapping, RequestParam, RestController}

import scala.collection.mutable

@RestController
@RequestMapping(value = Array("/controlAlarm"))
class ControlAlarmController {

  @Resource
  var controlAlarmService: ControlAlarmService = _

  @Resource
  var excels: Excels = _

  @Resource
  var interceptorFieldService: InterceptorFieldService = _

  @Resource
  var tableInfoService: TableInfoService = _

  @Qualifier("phoenixDataSource")
  @Resource
  var dataSource: DruidDataSource = _

  /**
    * 获取指定详情
    *
    * @param pk 主键
    * @return
    */
  @GetMapping(value = Array("/getSpecified"))
  def getSpecified(pk: String): WebResult = {
    ArgumentValidation.checkNotEmpty(Array(pk))
    WebResult.success().put("entity", controlAlarmService.get(pk.trim))
  }


  /**
    * 分页获取指定时间间隔内的数据,为了防止数据量过大,数量统计超时,设置上下时间间隔不得超过三个月,查询条件为抓拍时间
    *
    * @param page       页数,默认为1
    * @param pageSize   页大小,默认为10
    * @param orderBy    排序列,为加快查询默认不排序
    * @param orderFlag  排序方式,默认为升序
    * @param startValue 查询开始时间,格式为yyyy-MM-dd HH:mm:ss,必须参数
    * @param endValue   查询结束时间,格式为yyyy-MM-dd HH:mm:ss,必须参数
    * @return
    */
  @GetMapping(value = Array("/getPageInTimeRange"))
  def getPageInTimeRange(@RequestParam(required = false, defaultValue = "1") page: Int,
                         @RequestParam(required = false, defaultValue = "10") pageSize: Int,
                         @RequestParam(required = false, defaultValue = "") orderBy: String,
                         @RequestParam(required = false, defaultValue = "asc") orderFlag: String,
                         startValue: String,
                         endValue: String): WebResult = {
    ArgumentValidation.checkNotEmpty(Array(startValue, endValue))
    ArgumentValidation.checkBetweenThreeMonths(Array(startValue, endValue))
    val pageQuery = new PageQuery(page, pageSize, orderBy, orderFlag)
    val map = Map("startValue" -> startValue, "endValue" -> endValue)
    import scala.collection.convert.wrapAsJava.mapAsJavaMap
    val value = controlAlarmService.getPageInTimeRange(map, pageQuery)
    WebResult.success().put("page", value)
  }

  import ControlAlarmController._


  /**
    * excel下载相关,获取表的总记录条数以及可以下载的字段
    *
    * @return
    */
  @GetMapping(value = Array("/downloadConcerned"))
  def downloadConcerned: WebResult = WebResult.success().put("numberOfRow", tableInfoService.get(tableName).getRecordCount).put("heads", heads)


  /**
    * 获取符合检索条件的记录条数
    *
    * @param conditionIndex 检索条件的序号
    * @param params         指定的检索条件
    * @return
    */
  @GetMapping(value = Array("/getCountByCondition"))
  def getCountByCondition(conditionIndex: Int, params: String): WebResult = {
    ArgumentValidation.checkNotEmpty(Array(params))
    val querySql = getQuerySqlByCondition(conditionIndex, params)
    if (querySql.contentEquals("")) WebResult.success().put("count", tableInfoService.get(tableName).getRecordCount) else {
      val connection = dataSource.getConnection
      val preparedStatement = connection.prepareStatement(querySql)
      val resultSet: ResultSet = preparedStatement.executeQuery()
      resultSet.next()
      val count = resultSet.getLong("count")
      resultSet.close()
      preparedStatement.close()
      WebResult.success().put("count", count).put("heads", heads)
    }
  }

  /**
    * 获取下载excel文件时允许的检索条件,这些条件预先设置好,通过序号的方式传递给客户端,当前只支持时间范围检索
    *
    * @return
    */
  @GetMapping(value = Array("/getDownloadConditions"))
  def getDownloadConditions: WebResult = {
    WebResult.success().put("conditions", downloadConditions)
  }

  import java.lang.{StringBuilder => Jbuilder}

  /**
    * excel文件下载,由于表数据随时间递增,下载全部的数据耗时较长,故支持时间范围检索条件,当前仅支持时间范围检索
    * 每次下载数据指定的时间间隔不能查过三个月,此外本方法还支持已经设置的字段过滤
    *
    * @param count         下载的条数,默认为1000
    * @param headsCondition 下载的字段名称,字段之间以","分割,默认为"",即下载所有字段
    * @param conditionIndex 下载检索条件的标识,可以通过getDownloadConditions方法得到
    * @param params         当指定了检索条件的标识后,按照要求传递的检索参数
    * @param keyName       字段过滤的keyName
    * @param response       请求返回对象
    * @return
    */
  @GetMapping(value = Array("/getExcel"))
  def getExcel(@RequestParam(required = false, defaultValue = "1000") count: Int,
               @RequestParam(required = false, defaultValue = "") headsCondition: String,
               @RequestParam(required = false, defaultValue = "0") conditionIndex: Int,
               @RequestParam(required = false) params: String,
               @RequestParam(required = false, defaultValue = "default") keyName: String,
               response: HttpServletResponse): WebResult = {
    val interceptorFields = interceptorFieldService.getAllByApiUrlAndKeyName("/controlAlarm/getExcel", keyName)
    val sql = getSql(conditionIndex, params)
    var stringTuple2: (String, Array[String]) = null
    if (headsCondition.contentEquals("")) {
      stringTuple2 = excels.makeSql(heads, fieldsName, heads.mkString(","), tableName, count)
      if (sql.contentEquals("")) excels.downLoadExcel(response, heads, stringTuple2._1, stringTuple2._2, interceptorFields)
      else {
        val index = stringTuple2._1.indexOf("LIMIT") - 2
        val builder = new Jbuilder(stringTuple2._1)
        builder.insert(index, sql)
        excels.downLoadExcel(response, heads, builder.toString, stringTuple2._2, interceptorFields)
      }
    } else {
      stringTuple2 = excels.makeSql(heads, fieldsName, headsCondition, tableName, count)
      if (sql.contentEquals("")) excels.downLoadExcel(response, headsCondition.split(",").distinct, stringTuple2._1, stringTuple2._2, interceptorFields)
      else {
        val index = stringTuple2._1.indexOf("LIMIT") - 2
        val builder = new Jbuilder(stringTuple2._1)
        builder.insert(index, sql)
        excels.downLoadExcel(response, headsCondition.split(",").distinct, builder.toString, stringTuple2._2, interceptorFields)
      }
    }
    null
  }

}

object ControlAlarmController {

  val heads = Array("逻辑主键", "卡口编号", "车道号", "抓拍时间", "车牌号码", "车牌颜色", "车辆LOGO", "车辆类型",
    "车身颜色", "合成图片URL", "卡口名称", "行驶方向", "车牌图片URL", "车牌识别的置信度", "车速", "告警类型",
    "布控告警类型", "布控告警内容", "布控告警描述")

  val fieldsName = mutable.LinkedHashMap("PK" -> "pk", "TOLLGATE_CODE" -> "tollgateCode", "LANE_NO" -> "laneNo", "CAP_TIME" -> "capTime", "PLATE_CODE" -> "plateCode", "PLATE_COLOR" -> "plateColor",
    "LOGO" -> "logo", "VEHICLE_TYPE" -> "vehicleType", "VEHICLE_COLOR" -> "vehicleColor", "IMG_URL" -> "imgUrl", "LOCATION" -> "location", "DIRECTION" -> "direction", "PLATE_CODE_URL" -> "plateCodeUrl", "PLATE_CONFIDENCE" -> "plateConfidence",
    "SPEED" -> "speed", "ALARM_TYPE" -> "alarmType", "ALARM_CONTROL_TYPE" -> "alarmControlType", "ALARM_CONTROL_CONTENT" -> "alarmControlContent", "ALARM_CONTROL_DESC" -> "alarmControlDesc")


  val downloadConditions: util.Map[Int, QueryCondition] = {
    import scala.collection.convert.wrapAsJava.mapAsJavaMap
    Map[Int, QueryCondition](1 -> QueryCondition(1, Array("CAP_TIME"), Array("抓拍时间"), "以抓拍时间为查询条件,需要传递两个参数,查询开始时间以及" +
      "查询结束时间,开始时间在前,两者的时间间隔不能超过三个月,时间字符串的格式为yyyy-MM-dd HH:mm:ss,例如2015-05-06 13:02:30"))
  }

  val tableName = "JCZH_TBL_UV_VEH_CONTROL_ALARM"


  /**
    * 使用检索条件的标识以及传递的筛选参数组成where条件
    *
    * @param conditionIndex 检索条件的标识
    * @param params         检索参数
    * @return
    */
  def getSql(conditionIndex: Int, params: String): String = {
    conditionIndex match {
      case 1 =>
        val strings = params.split(",")
        ArgumentValidation.checkBetweenThreeMonths(strings)
        StringBuilder.newBuilder.append(" WHERE CAP_TIME BETWEEN '").append(strings(0)).append("' AND '").append(strings(1)).append("'").toString
      case _ => ""
    }
  }


  /**
    * 使用检索条件的标识以及传递的筛选参数组成数量查询语句
    *
    * @param conditionIndex 检索条件的标识
    * @param params         检索参数
    * @return
    */
  def getQuerySqlByCondition(conditionIndex: Int, params: String): String = {
    conditionIndex match {
      case 1 =>
        val strings = params.split(",")
        ArgumentValidation.checkBetweenThreeMonths(strings)
        StringBuilder.newBuilder.append("SELECT COUNT(1) AS count FROM JCZH_TBL_UV_VEH_CONTROL_ALARM WHERE CAP_TIME BETWEEN '").append(strings(0)).append("' AND '").append(strings(1)).append("'").toString
      case _ => ""
    }
  }
}




