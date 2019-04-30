package cn.posinda.phoenix.controller

import java.text.SimpleDateFormat
import java.{util => ju}

import cn.posinda.base.WebResult
import com.mashape.unirest.http.exceptions.UnirestException
import com.mashape.unirest.http.{HttpResponse, Unirest}
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.{GetMapping, RequestMapping, RestController}

@RestController
@RequestMapping(value = Array("/cluster"))
class ClouderaController {

  val serverUrl: String = "cdh1.posinda.com:7180"

  val user = "admin"

  val password = "admin"

  val clusterName = "Cluster 1"

  /**
    * 获取集群主机的信息
    *
    * @return
    */
  @GetMapping(value = Array("/getHostsInfo"))
  def getHostsInfo: WebResult = {
    val url = new StringBuilder("http://").append(serverUrl).append("/api/v9/hosts")
    val response = Unirest.get(url.toString()).basicAuth(user, password).header("accept", MediaType.APPLICATION_JSON_UTF8_VALUE).asString()
    processResponse(response)
  }

  /**
    * 获取集群内各主机处于良好状况时间的百分比
    *
    * @return
    */
  @GetMapping(value = Array("/getClusterEffectiveness"))
  def getClusterEffectiveness: WebResult = {
    val queryString = "select health_good_rate*100 as percent from ENTITY_DATA where category=HOST"
    val timeTuple = getTimeTuple
    val queryParam = this.constructMap(queryString, timeTuple._1, timeTuple._2)
    val url = new StringBuilder("http://").append(serverUrl).append("/api/v6/timeseries")
    val response = Unirest.get(url.toString()).basicAuth(user, password).header("accept", MediaType.APPLICATION_JSON_UTF8_VALUE).queryString(queryParam).asString()
    processResponse(response)
  }

  /**
    * 获取各主机以及集群的磁盘使用率
    *
    * @return
    */
  @GetMapping(value = Array("/getDiskUsageRate"))
  def getDiskUsageRate: WebResult = {
    val url = new StringBuilder("http://").append(serverUrl).append("/api/v6/timeseries")
    val queryString = "select (total_capacity_used_across_filesystems/total_capacity_across_filesystems)*100 as percent"
    val timeTuple = getTimeTuple
    val queryParam = this.constructMap(queryString, timeTuple._1, timeTuple._2)
    val response = Unirest.get(url.toString()).basicAuth(user, password).header("accept", MediaType.APPLICATION_JSON_UTF8_VALUE).queryString(queryParam).asString()
    processResponse(response)
  }

  /**
    * 获取各主机以及集群的剩余磁盘量以及剩余比例
    *
    * @return
    */
  @GetMapping(value = Array("/getDiskEffectiveness"))
  def getDiskEffectiveness: WebResult = {
    val url = new StringBuilder("http://").append(serverUrl).append("/api/v6/timeseries")
    val queryString = "select total_capacity_free_across_filesystems, (total_capacity_free_across_filesystems/total_capacity_across_filesystems)*100 as percent"
    val timeTuple = getTimeTuple
    val queryParam = this.constructMap(queryString, timeTuple._1, timeTuple._2)
    val response = Unirest.get(url.toString()).basicAuth(user, password).header("accept", MediaType.APPLICATION_JSON_UTF8_VALUE).queryString(queryParam).asString()
    processResponse(response)
  }


  /**
    * 获取集群空间剩余
    *
    * @return
    */
  @GetMapping(value = Array("/getRemainingSpace"))
  def getRemainingSpace: WebResult = {
    val url = new StringBuilder("http://").append(serverUrl).append("/cmf/hardware/hosts/hostsOverview.json")
    val response = Unirest.get(url.toString()).basicAuth(user, password).header("accept", MediaType.APPLICATION_JSON_UTF8_VALUE).asString()
    processResponse(response)
  }

  /**
    * 获取节点内存，CPU使用率
    *
    * @return
    */
  @GetMapping(value = Array("/getMemoryAndCpuStatus"))
  def getMemoryAndCpuStatus: WebResult = {
    val queryString = "SELECT cpu_percent,physical_memory_used/physical_memory_total*100 as percent"
    val url = new StringBuilder("http://").append(serverUrl).append("/api/v6/timeseries")
    val timeTuple = getTimeTuple
    val queryParam = this.constructMap(queryString, timeTuple._1, timeTuple._2)
    val response = Unirest.get(url.toString()).basicAuth(user, password).header("accept", MediaType.APPLICATION_JSON_UTF8_VALUE).queryString(queryParam).asString()
    processResponse(response)
  }

  /**
    * 获取CPU使用率
    *
    * @return
    */
  @GetMapping(value = Array("/getCPUEffectiveness"))
  def getCPUEffectiveness: WebResult = {
    val queryString = "select cpu_percent"
    val url = new StringBuilder("http://").append(serverUrl).append("/api/v6/timeseries")
    val timeTuple = getTimeTuple
    val queryParam = this.constructMap(queryString, timeTuple._1, timeTuple._2)
    val response = Unirest.get(url.toString()).basicAuth(user, password).header("accept", MediaType.APPLICATION_JSON_UTF8_VALUE).queryString(queryParam).asString()
    processResponse(response)
  }


  /**
    * 获取内存使用率
    *
    * @return
    */
  @GetMapping(value = Array("/getMemoryEffectiveness"))
  def getMemoryEffectiveness: WebResult = {
    val queryString = "select physical_memory_used/physical_memory_total*100 as percent "
    val url = new StringBuilder("http://").append(serverUrl).append("/api/v6/timeseries")
    val timeTuple = getTimeTuple
    val queryParam = this.constructMap(queryString, timeTuple._1, timeTuple._2)
    val response = Unirest.get(url.toString()).basicAuth(user, password).header("accept", MediaType.APPLICATION_JSON_UTF8_VALUE).queryString(queryParam).asString()
    processResponse(response)
  }

  /**
    * 异常监控
    *
    * @return
    */
  @deprecated
  @GetMapping(value = Array("/abnormalMonitoring"))
  def abnormalMonitoring: WebResult = {
    val url = new StringBuilder("http://").append(serverUrl).append("/api/v1/clusters/" + clusterName + "/services")
    val response = Unirest.get(url.toString()).basicAuth(user, password).header("accept", MediaType.APPLICATION_JSON_UTF8_VALUE).asString()
    processResponse(response)
  }


  private def getTimeTuple: (String, String) = {
    import ju.Calendar
    val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
    val cal = Calendar.getInstance()
    cal.add(Calendar.HOUR, -8)
    val to = cal.getTime
    cal.add(Calendar.MINUTE, -30)
    val from = cal.getTime
    (format.format(from).concat("Z"), format.format(to).concat("Z"))
  }

  private def constructMap(query: String, from: String, to: String): ju.Map[String, Object] = {
    import scala.collection.convert.wrapAsJava.mapAsJavaMap
    scala.collection.mutable.Map("query" -> query, "from" -> from, "to" -> to)
  }

  private def processResponse(response: HttpResponse[String]): WebResult = {
    val statusCode = response.getStatus
    if (!ju.Objects.equals(statusCode, 200)) throw new UnirestException(s"数据请求异常,异常code:$statusCode")
    WebResult.success().put("result", response.getBody)
  }


}
