package cn.posinda.phoenix.job.importConcerned.web

import java.util.concurrent.ScheduledFuture
import javax.annotation.Resource

import cn.posinda.base.{PageQuery, WebResult}
import cn.posinda.phoenix.job.importConcerned.Monitor
import cn.posinda.phoenix.job.importConcerned.entity.{DatabaseConnection => Entity}
import cn.posinda.phoenix.job.importConcerned.service.DatabaseConnectionService
import cn.posinda.phoenix.tools.{ArgumentValidation, UUIDs}
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.support.CronTrigger
import org.springframework.web.bind.annotation._
import cn.posinda.phoenix.tools.{MyDES}

/**
  * 数据库连接操作，支持连接的增删改查，以及
  * 是否进行监控的更改
  */
@RestController
@RequestMapping(value = Array("/databaseConnection"))
class DatabaseConnectionController {

  @Resource
  var service: DatabaseConnectionService = _

  @Qualifier("taskScheduler")
  @Resource
  var taskScheduler: ThreadPoolTaskScheduler = _

 private[this] var future: ScheduledFuture[_] = _

  @GetMapping(value = Array("/getSpecified"))
  def getSpecified(id: String): WebResult = {
    ArgumentValidation.checkNotEmpty(Array(id))
    WebResult.success().put("entity", service.get(id.trim))
  }

  @GetMapping(value = Array("/getAll"))
  def getAll: WebResult = WebResult.success().put("list", service.findList(Entity.instance))


  @GetMapping(value = Array("/getPage"))
  def getPage(pageQuery: PageQuery): WebResult = {
    WebResult.success().put("page", service.findPage(Entity.instance, pageQuery))
  }

  @PostMapping(value = Array("/insert"))
  def insert(@RequestBody entity: Entity): WebResult = {
    
    ArgumentValidation.checkNotEmpty(Array(entity.getSystemFrom, entity.getUrl, entity.getUsername)) 
    val id = UUIDs.getInstance
    val password = MyDES.encryptBasedDes(entity.getPassword)
    entity.setPassword(password)
    entity.setId(id)
    service.insert(entity)
    WebResult.success()
  }

  @PostMapping(value = Array("/update"))
  def update(@RequestBody entity: Entity): WebResult = {
    ArgumentValidation.checkNotEmpty(Array(entity.getPassword, entity.getSystemFrom, entity.getUrl, entity.getUsername))
    service.update(entity)
    WebResult.success()
  }

  @DeleteMapping(value = Array("/delete"))
  def delete(id: String): WebResult = {
    ArgumentValidation.checkNotEmpty(Array(id))
    service.delete(id)
    WebResult.success()
  }

  /**
    * 开启数据库连接的监控
    *
    * @return
    */
  @GetMapping(value = Array("/startMonitoring"))
  def startMonitoring: WebResult = {
    if (future == null) {
      val monitor = new Monitor
      monitor.setService(service)
      future = taskScheduler.schedule(monitor, new CronTrigger("0 */30 * * * ?"))
    }
    WebResult.success()
  }

  /**
    * 关闭数据库连接监控
    *
    * @return
    */
  @GetMapping(value = Array("/cancel"))
  def cancelMonitoring: WebResult = {
    if (java.util.Objects.nonNull(future)) {
      future.cancel(true)
      future = null
    }
    WebResult.success()
  }
}
