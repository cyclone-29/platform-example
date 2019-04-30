package cn.posinda.mysql.utils

import java.security.cert.X509Certificate
import javax.net.ssl.{SSLContext, TrustManager, X509TrustManager}

import org.apache.http.client.methods.{CloseableHttpResponse, HttpTrace}
import org.apache.http.config.RegistryBuilder
import org.apache.http.conn.socket.{ConnectionSocketFactory, PlainConnectionSocketFactory}
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.apache.http.util.EntityUtils

import scala.collection.mutable.ArrayBuffer

/**
  * http连接测试类
  */
object Https {

  private def createIgnoreVerifySSL: SSLContext = {
    val sc = SSLContext.getInstance("SSLv3")
    sc.init(null, Array[TrustManager](CustomTrustManager), null)
    sc
  }

  private object CustomTrustManager extends X509TrustManager {

    override def checkServerTrusted(x509Certificates: Array[X509Certificate], s: String): Unit = ()

    override def checkClientTrusted(x509Certificates: Array[X509Certificate], s: String): Unit = ()

    override def getAcceptedIssuers: Array[X509Certificate] = null
  }

  /**
    * 网络连接状况测试
    *
    * @param args 需要测试的网络连接url数组,支持https
    * @return 连接异常的url数组
    */
  def testInterfaceConnectionStatus(args: Array[String]): Array[String] = {
    val context = createIgnoreVerifySSL
    val socketFactoryRegistry = RegistryBuilder.create[ConnectionSocketFactory]()
      .register("http", PlainConnectionSocketFactory.INSTANCE)
      .register("https", new SSLConnectionSocketFactory(context)).build()
    val connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry)
    HttpClients.custom.setConnectionManager(connectionManager)
    val client = HttpClients.custom.setConnectionManager(connectionManager).build
    val errorBuffer = new ArrayBuffer[String]
    for (x <- args) {
      var response: CloseableHttpResponse = null
      try {
        response = client.execute(new HttpTrace(x))
        val entity = response.getEntity
        if (entity != null) EntityUtils.consume(entity)
      } catch {
        case _: Exception => errorBuffer.append(x)
      } finally {
        if (response != null) response.close()
      }
    }
    client.close()
    errorBuffer.toArray
  }

}
