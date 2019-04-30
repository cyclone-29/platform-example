package cn.posinda.mysql.job

import cn.posinda.mysql.entity.{InterfaceConnection => Entity, InterfaceConnectionError => ErrorEntity}
import cn.posinda.mysql.service.{InterfaceConnectionErrorService, InterfaceConnectionService}
import cn.posinda.mysql.utils.Https
import cn.posinda.phoenix.tools.UUIDs
import org.slf4j.LoggerFactory

import scala.beans.BeanProperty

/**
  * 接口连接监控任务
  */
class InterfaceConnectionMonitor extends java.lang.Runnable {

  @BeanProperty var connectionService: InterfaceConnectionService = _

  @BeanProperty var errorService: InterfaceConnectionErrorService = _

  private val logger = LoggerFactory.getLogger(classOf[InterfaceConnectionMonitor])

  override def run(): Unit = {
    import scala.collection.convert.wrapAsScala.asScalaBuffer
    val connections = connectionService.findList(new Entity).map(_.url).toArray
    val errorConnections = Https.testInterfaceConnectionStatus(connections)
    if (errorConnections.length > 0) errorConnections.foreach(insert)
    val normalConnections = connections.diff(errorConnections)
    errorConnections.foreach(changeToAbNormal)
    normalConnections.foreach(changeToNormal)
  }

  private def insert(errorUrl: String): Unit = {
    val errorConnection = new ErrorEntity
    errorConnection.setInterfaceName(errorUrl)
    errorConnection.setId(UUIDs.getInstance)
    errorConnection.setCheckTime(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new java.util.Date()))
    errorService.insert(errorConnection)
    logger.error(s"some error occurs on connection $errorUrl")
  }

  private def changeToNormal(url: String): Unit = {
    connectionService.changeToNormal(url)
  }

  private def changeToAbNormal(url: String): Unit = {
    connectionService.changeToAbNormal(url)
  }
}
