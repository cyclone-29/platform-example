package cn.posinda.mysql.controller

import java.text.SimpleDateFormat
import java.util.{Date, Objects}

import javax.annotation.Resource
import cn.posinda.base.{PageQuery, WebResult}
import cn.posinda.mysql.entity.{InterceptorField => Entity}
import cn.posinda.mysql.service.InterceptorFieldService
import cn.posinda.mysql.utils.InterceptorType
import cn.posinda.phoenix.tools.{ArgumentValidation, UUIDs}
import org.springframework.web.bind.annotation._

/**
  * 接口指定脱敏字段相关操作
  */
@RestController
@RequestMapping(value = Array("/interceptorByFiled"))
class InterceptorFieldController {

  @Resource
  var service: InterceptorFieldService = _

  // 暂使用原API风格，支持POST、GET方式，减少集成任务；
  @RequestMapping(value = Array("/addInterceptorItem"), method = Array(RequestMethod.POST, RequestMethod.GET))
  def insert(@RequestParam apiUrl: String,
             @RequestParam fieldName: String,
             @RequestParam(required = false, defaultValue = "0") interceptorTypeCode: String,
             @RequestParam(required = false, defaultValue = "default") keyName: String): WebResult = {
    ArgumentValidation.checkNotEmpty(Array(apiUrl, fieldName, keyName))
    val entity = new Entity
    entity.setApiUrl(apiUrl)
    entity.setFieldName(fieldName)
    entity.setKeyName(keyName)
    entity.setCreateDt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
    val index = InterceptorType.fromIndex(Integer.valueOf(interceptorTypeCode.trim())).index()
    entity.setInterceptorType(index)
    checkInterceptorType(entity)
    entity.setId(UUIDs.getInstance)
    try {
      service.insert(entity)
      WebResult.success().put("id", entity.getId)
    } catch {
      case _: Exception => WebResult.error("此数据已存在，插入失败")
    }
  }

  // 暂使用原API风格，支持POST、GET方式，减少集成任务；
  @RequestMapping(value = Array("/deleteInterceptorItem"), method = Array(RequestMethod.POST, RequestMethod.GET))
  def delete(id: String): WebResult = {
    ArgumentValidation.checkNotEmpty(Array(id))
    val entity = service.get(id.trim)
    if (Objects.isNull(entity)) throw new IllegalArgumentException("指定的数据不存在")
    entity.setUpdateDt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
    service.delete(entity)
    WebResult.success()
  }

  @GetMapping(value = Array("/getPage"))
  def getPage(pageQuery: PageQuery,
              @RequestParam(required = false) apiUrl: String,
              @RequestParam(required = false, defaultValue = "default") keyName: String): WebResult = {
    val entity = new Entity
    if (apiUrl != null) entity.setApiUrl(apiUrl.trim)
    entity.setKeyName(keyName)
    WebResult.success().put("page", service.findPage(entity, pageQuery))
  }

  // 暂使用原API风格，支持POST、GET方式，减少集成任务；
  @RequestMapping(value = Array("/getList"), method = Array(RequestMethod.POST, RequestMethod.GET))
  def getAll(@RequestParam apiUrl: String,
             @RequestParam keyName: String): WebResult = {
    val entity = new Entity
    if (apiUrl != null) entity.setApiUrl(apiUrl.trim)
    entity.setKeyName(keyName)
    WebResult.success().put("list", service.findList(entity))
  }

  // 暂使用原API风格，支持POST、GET方式，减少集成任务；
  @RequestMapping(value = Array("/updateInterceptorItem"), method = Array(RequestMethod.POST, RequestMethod.GET))
  def update(@RequestParam id: String,
             @RequestParam interceptorTypeCode: String): WebResult = {
    ArgumentValidation.checkNotEmpty(Array(id, interceptorTypeCode))
    val entity = service.get(id.trim)
    if (Objects.isNull(entity)) throw new IllegalArgumentException("指定的数据不存在")
    entity.setUpdateDt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
    val index = InterceptorType.fromIndex(Integer.valueOf(interceptorTypeCode.trim())).index()
    entity.setInterceptorType(index)
    checkInterceptorType(entity)
    service.update(entity)
    WebResult.success()
  }

  private def checkInterceptorType(entity: Entity): Unit = {
    val indices = InterceptorType.values().map(_.index())
    if (!indices.contains(entity.getInterceptorType)) throw new IllegalArgumentException(s"错误的脱敏方式 ,${entity.getInterceptorType}")
  }

}
