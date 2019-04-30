package cn.posinda.mysql.controller

import javax.annotation.Resource

import cn.posinda.base.{PageQuery, WebResult}
import cn.posinda.mysql.entity.{InterfaceConnection => Entity}
import cn.posinda.mysql.service.InterfaceConnectionService
import cn.posinda.phoenix.tools.{ArgumentValidation, UUIDs}
import org.springframework.web.bind.annotation._

/**
  * 接口连接信息的相关操作，增删改查
  */
@RestController
@RequestMapping(value = Array("/interfaceConnection"))
class InterfaceConnectionController {

  @Resource
  var service: InterfaceConnectionService = _

  @GetMapping(value = Array("/getSpecified"))
  def getSpecified(id: String): WebResult = {
    ArgumentValidation.checkNotEmpty(Array(id))
    WebResult.success().put("entity", service.get(id.trim))
  }

  @GetMapping(value = Array("/getAll"))
  def getAll: WebResult = WebResult.success().put("list", service.findList(new Entity))

  @GetMapping(value = Array("/getPage"))
  def getPage(pageQuery: PageQuery, entity: Entity): WebResult = WebResult.success().put("page", service.findPage(entity, pageQuery))

  @DeleteMapping(value = Array("/delete"))
  def delete(id: String): WebResult = {
    ArgumentValidation.checkNotEmpty(Array(id))
    service.delete(id.trim)
    WebResult.success()
  }

  @PostMapping(value = Array("/update"))
  def update(@RequestBody entity: Entity): WebResult = {
    ArgumentValidation.checkNotEmpty(Array(entity.id, entity.url, entity.systemFrom))
    service.update(entity)
    WebResult.success()
  }

  @PostMapping(value = Array("/insert"))
  def insert(@RequestBody entity: Entity): WebResult = {
    ArgumentValidation.checkNotEmpty(Array(entity.url, entity.systemFrom))
    entity.setId(UUIDs.getInstance)
    service.insert(entity)
    WebResult.success()
  }
}
