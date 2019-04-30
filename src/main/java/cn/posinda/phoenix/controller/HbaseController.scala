package cn.posinda.phoenix.controller

import javax.annotation.Resource

import cn.posinda.base.WebResult
import cn.posinda.phoenix.entity.TableInfo
import cn.posinda.phoenix.service.TableInfoService
import cn.posinda.phoenix.tools.ArgumentValidation
import cn.posinda.utils.HbaseUtil
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.Scan
import org.apache.hadoop.hbase.client.coprocessor.LongColumnInterpreter
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation._

@RestController
@RequestMapping(Array("/hbaseTable"))
class HbaseController {

  @Resource
  var hbaseUtil: HbaseUtil = _

  @Resource
  var tableInfoService: TableInfoService = _


  /**
    * 获取所有表的属性信息
    *
    * @return
    */
  @GetMapping(value = Array("/AllTableProperties"))
  def tableProperties: WebResult = {
    val tableProperties = tableInfoService.findList(TableInfo.builder().build())
    WebResult.success.put("list", tableProperties)
  }

  /**
    * 获取指定表的属性信息
    *
    * @param tableName 指定的表名称
    * @return
    */
  @GetMapping(value = Array("/specifiedTableProperty"))
  def specifiedTableProperty(tableName: String): WebResult = {
    if (!StringUtils.hasText(tableName)) throw new IllegalArgumentException(s"错误的表名称参数")
    val tableInfo = tableInfoService.get(tableName.trim)
    WebResult.success().put("result", if (tableInfo == null) "不存在指定名称的表" else tableInfo)
  }

  /**
    * 获取hbase表的名称,只包含表的名称，不包含其他的信息
    *
    * @return
    */
  @GetMapping(value = Array("/tableNames"))
  def tableNames: WebResult = {
    import scala.collection.convert.wrapAsScala.asScalaBuffer
    WebResult.success().put("tableNames", tableInfoService.findList(TableInfo.builder().build()).map(_.getTableName))
  }

  /**
    * 获取TABLE_INFO表中所有表记录数总和（资源接入时，需在TABLE_INFO表中添加记录）
    *
    * @return
    */
  @GetMapping(value = Array("/getRowSumOfAllTables"))
  def rowSumOfAllTables: WebResult = {
    WebResult.success().put("result", tableInfoService.getRowSumOfAllTables)
  }

  /**
    * 查询指定的表的条数，如果表很大可能会花费较长的时间，进行此方法的前提时进行查询的hbase
    * 添加了AggregationClient协处理器，如果没有添加，查询异常,查询完成之后，因为这是一个准确
    * 值，可以将其更新到TBL_TABLE_INFO表中
    *
    * @param tableName 需要查询条数的hbase表名称
    * @return
    */
  @GetMapping(value = Array("/getRowNumOfSpecifiedTable"))
  def getRowNumOfSpecifiedTable(tableName: String): WebResult = {
    import org.apache.hadoop.hbase.client.coprocessor.AggregationClient
    if (!StringUtils.hasText(tableName)) throw new IllegalArgumentException(s"错误的表名称参数")
    import scala.collection.convert.wrapAsScala._
    val tables = tableInfoService.findList(TableInfo.builder().build()).map(_.getTableName)
    if (!tables.contains(tableName.trim)) throw new IllegalArgumentException(s"错误的表名称参数")
    val scan = new Scan()
    val table = TableName.valueOf(tableName.trim)
    val aggregationClient = new AggregationClient(hbaseUtil.config)
    try {
      val result = aggregationClient.rowCount(table, new LongColumnInterpreter(), scan)
      val tableInfo = TableInfo.builder().tableName(tableName.trim).recordCount(result).build()
      tableInfoService.updateRowNum(tableInfo)
      WebResult.success().put("result", result)
    } catch {
      case _: Exception => WebResult.error("数据表过大，查询超时")
    } finally {
      if (aggregationClient != null) aggregationClient.close()
    }
  }


  /**
    * 更新或者增加表的描述信息
    *
    * @return
    */
  @PostMapping(value = Array("/addOrUpdateTableDesc"))
  def addOrUpdateTableDesc(tableName: String,
                           description: String): WebResult = {
    if (!StringUtils.hasText(tableName) || !StringUtils.hasText(description)) throw new IllegalArgumentException("非法参数")
    val tableInfo = TableInfo.builder().tableName(tableName.trim).description(description.trim).build()
    tableInfoService.updateDescription(tableInfo)
    WebResult.success()
  }

  /**
    * 插入一条表描述信息
    *
    * @param tableName   表名称
    * @param recordCount 记录条数，默认值为0
    * @param description 表描述信息，默认值为""
    * @param systemFrom  数据来源系统名称，需要完全匹配
    * @return
    */
  @PostMapping(value = Array("/addEntity"))
  def addEntity(tableName: String,
                @RequestParam(required = false, defaultValue = "0") recordCount: Long,
                @RequestParam(required = false, defaultValue = "") description: String,
                systemFrom: String,url:String): WebResult = {
    ArgumentValidation.checkNotEmpty(Array(systemFrom,url))
    import scala.collection.convert.wrapAsScala._
    val tables = tableInfoService.findList(TableInfo.builder().build()).map(_.getTableName)
    if (tables.contains(tableName.trim)) throw new IllegalArgumentException("已存在的表名称")
    val tmp = TableInfo.SystemFrom.fromDesc(systemFrom.trim)
    val tableInfo = TableInfo.builder().tableName(tableName.trim).recordCount(recordCount).description(description.trim).systemFrom(tmp.getDesc).url(url).build()
    tableInfoService.insert(tableInfo)
    WebResult.success()
  }

  /**
    * 获取表来源系统名称和简写映射信息
    * @return
    */
  @GetMapping(value = Array("/getSystemMapping"))
  def getSystemMapping:WebResult = WebResult.success().put("result",TableInfo.SystemFrom.values().map(_.toString))
}
