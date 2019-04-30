package cn.posinda.mysql.controller

import javax.annotation.Resource

import cn.posinda.base.{PageQuery, WebResult}
import cn.posinda.mysql.entity.{ApiField => Entity}
import cn.posinda.mysql.service.ApiFieldService
import cn.posinda.mysql.utils.InterceptorType
import cn.posinda.phoenix.tools.{ArgumentValidation, UUIDs}
import org.springframework.web.bind.annotation._

/**
  * 接口返回的字段相关操作
  */
@RestController
@RequestMapping(value = Array("/apiField"))
class ApiFieldController {

  @Resource
  var service: ApiFieldService = _


  @GetMapping(value = Array("/getSpecified"))
  def getSpecified(id: String): WebResult = {
    ArgumentValidation.checkNotEmpty(Array(id))
    WebResult.success().put("entity", service.get(id.trim))
  }

  // 暂使用原API风格，支持POST、GET方式，减少集成任务；
  @RequestMapping(value = Array("/getFieldsInSpecifiedApi"), method = Array(RequestMethod.GET, RequestMethod.POST))
  def getAll(@RequestParam apiUrl: String): WebResult = {
    ArgumentValidation.checkNotEmpty(Array(apiUrl))
    val entity = new Entity
    if (apiUrl != null) entity.setApiUrl(apiUrl)
    WebResult.success().put("list", service.findList(entity))
  }

  @GetMapping(value = Array("/getPage"))
  def getPage(pageQuery: PageQuery, @RequestParam(required = false) apiUrl: String
             ): WebResult = {
    val entity = new Entity
    if (apiUrl != null) entity.setApiUrl(apiUrl)
    WebResult.success().put("page", service.findPage(entity, pageQuery))
  }

  // 暂使用原API风格，支持POST、GET方式，减少集成任务；
  @RequestMapping(value = Array("/addFiledInSpecifiedApi"), method = Array(RequestMethod.GET, RequestMethod.POST))
  def insert(@RequestParam apiUrl: String,
             @RequestParam fieldName: String,
             @RequestParam description: String): WebResult = {
    ArgumentValidation.checkNotEmpty(Array(apiUrl, description, fieldName))
    val entity = new Entity
    entity.setId(UUIDs.getInstance)
    entity.setApiUrl(apiUrl)
    entity.setFieldName(fieldName)
    entity.setDescription(description)
    try {
      service.insert(entity)
      WebResult.success()
    } catch {
      case _: Exception => WebResult.error("此数据已存在，插入失败")
    }
  }

  @DeleteMapping(value = Array("/delete"))
  def delete(id: String): WebResult = {
    ArgumentValidation.checkNotEmpty(Array(id))
    service.delete(id.trim)
    WebResult.success()
  }


  /**
    * 获取所有的过滤方式
    *
    * @return
    */
  @GetMapping(value = Array("/getFilterTypes"))
  def getFilterTypes: WebResult = WebResult.success().put("types", InterceptorType.values().map { _.toString })


}
