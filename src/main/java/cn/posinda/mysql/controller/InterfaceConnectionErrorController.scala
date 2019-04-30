package cn.posinda.mysql.controller

import java.util.concurrent.ScheduledFuture
import javax.annotation.Resource

import cn.posinda.base.{PageQuery, WebResult}
import cn.posinda.mysql.entity.{InterfaceConnectionError => Entity}
import cn.posinda.mysql.job.InterfaceConnectionMonitor
import cn.posinda.mysql.service.{InterfaceConnectionErrorService, InterfaceConnectionService}
import cn.posinda.phoenix.tools.ArgumentValidation
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.support.CronTrigger
import org.springframework.web.bind.annotation._

/**
  * 接口连接异常信息的相关操作，包含了接口连接监控的开启和关闭设置
  */
@RestController
@RequestMapping(value = Array("/interfaceError"))
class InterfaceConnectionErrorController {

  @Resource
  var errorService: InterfaceConnectionErrorService = _

  @Qualifier("taskScheduler")
  @Resource
  var taskScheduler: ThreadPoolTaskScheduler = _

  @Resource
  var connectionService: InterfaceConnectionService = _

  private[this] var future: ScheduledFuture[_] = _

  @GetMapping(value = Array("/getSpecified"))
  def getSpecified(id: String): WebResult = {
    ArgumentValidation.checkNotEmpty(Array(id))
    WebResult.success().put("entity", errorService.get(id.trim))
  }

  @GetMapping(value = Array("/getPage"))
  def getPage(pageQuery: PageQuery): WebResult = WebResult.success().put("page", errorService.findPage(new Entity, pageQuery))

  @GetMapping(value = Array("/getAll"))
  def getAll: WebResult = WebResult.success().put("list", errorService.findList(new Entity))

  @DeleteMapping(value = Array("/delete"))
  def delete(id: String): WebResult = {
    ArgumentValidation.checkNotEmpty(Array(id))
    errorService.delete(id.trim)
    WebResult.success
  }

  private val logger = LoggerFactory.getLogger(classOf[InterfaceConnectionErrorController])

  @PutMapping(value = Array("/startMonitor"))
  def startMonitor: WebResult = {
    if (future == null) {
      val monitor = new InterfaceConnectionMonitor
      monitor.setConnectionService(connectionService)
      monitor.setErrorService(errorService)
      future = taskScheduler.schedule(monitor, new CronTrigger("0 */30 * * * ?"))
      logger.info("start monitoring on interface connection")
    }
    WebResult.success()
  }

  @PutMapping(value = Array("/cancel"))
  def cancelMonitor: WebResult = {
    if (java.util.Objects.nonNull(future)) {
      future.cancel(true)
      future = null
      logger.info("cancel monitoring on interface connection")
    }
    WebResult.success()
  }
}
