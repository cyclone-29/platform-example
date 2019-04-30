package cn.posinda.phoenix.job.importConcerned

import cn.posinda.phoenix.job.importConcerned.entity.{DatabaseConnection => Entity}
import cn.posinda.phoenix.job.importConcerned.service.DatabaseConnectionService
import org.slf4j.LoggerFactory
import org.springframework.jdbc.datasource.DriverManagerDataSource
import cn.posinda.phoenix.tools.{MyDES}
import scala.beans.BeanProperty


/**
  * 数据库连接检测线程 30分钟运行一次
  */
class Monitor extends java.lang.Runnable {

 @BeanProperty var service: DatabaseConnectionService = _

  private val logger = LoggerFactory.getLogger(classOf[Monitor])

  override def run(): Unit = {
    import scala.collection.convert.wrapAsScala.asScalaBuffer
    val list = service.findList(Entity.instance)
    val tuple3List = list.map { (x: Entity) => (x.url, x.username, x.password) }
    val dataSource = new DriverManagerDataSource
    for ((url, username, password) <- tuple3List) {
      val password1 = MyDES.decryptBasedDes(password)
      dataSource.setUrl(url)
      dataSource.setUsername(username)
      dataSource.setPassword(password1)
      var connection: java.sql.Connection = null
      try {
        connection = dataSource.getConnection
        service.changeToNormal(url)
      } catch {
        case _: Exception =>
          service.changeToAbnormal(url)
          logger.error(s"${url}连接异常")
      } finally {
        if(connection!=null) {
          connection.close()
          connection = null
        }
      }
    }
  }
}
