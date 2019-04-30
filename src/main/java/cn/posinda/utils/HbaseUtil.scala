package cn.posinda.utils

import java.io.{ByteArrayOutputStream, ObjectOutputStream}
import javax.annotation.Resource

import com.alibaba.druid.pool.DruidDataSource
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.client.{Admin, ConnectionFactory}
import org.apache.hadoop.hbase.{HBaseConfiguration, ServerName}
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
class HbaseUtil {

  @Qualifier("phoenixDataSource")
  @Resource
  var dataSource: DruidDataSource = _

  val config: Configuration = {
    val configuration = HBaseConfiguration.create()
    configuration.addResource("core-site.xml")
    configuration.addResource("hbase-site.xml")
    configuration.addResource("hdfs-site.xml")
    configuration
  }

  /**
    * 自定义序列化方法
    *
    * @param obj 需要序列化的对象
    * @tparam T obj对应的class
    * @return
    */
  def getBytes[T <: Serializable](obj: T): Array[Byte] = {
    val byteArrayOutputStream = new ByteArrayOutputStream()
    val objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)
    objectOutputStream.writeObject(obj)
    objectOutputStream.close()
    byteArrayOutputStream.toByteArray
  }

  /**
    * 获取hbase管理对象
    */
  val admin: Admin = {
    val connection = ConnectionFactory.createConnection(config)
    connection.getAdmin
  }

  /**
    * 获取集群regionServer服务器名称
    *
    * @return
    */
  def getServerNames: Array[String] = {
    val status = admin.getClusterStatus
    val servers: java.util.Collection[ServerName] = status.getServers
    val serverArr = new Array[ServerName](servers.size)
    servers.toArray(serverArr).map(_.getServerName)
  }

}
