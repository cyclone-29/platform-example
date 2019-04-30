package cn.posinda.aop

import java.lang.reflect.Field
import java.{util => ju}
import javax.annotation.Resource

import cn.posinda.base.WebResult
import cn.posinda.mysql.entity.InterceptorField
import cn.posinda.mysql.service.InterceptorFieldService
import cn.posinda.mysql.utils.StringEncode
import cn.posinda.phoenix.base.PhoenixDataEntity
import com.github.pagehelper.PageInfo
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.{Around, Aspect, Pointcut}
import org.springframework.stereotype.Component
import org.springframework.web.context.request.{RequestContextHolder, ServletRequestAttributes}

@Component
@Aspect
class FieldsFilter {

  @Resource
  var interceptorFieldService: InterceptorFieldService = _

  @Pointcut("execution(* cn.posinda.phoenix.controller.*.*(..))")
  def pointcut(): Unit = ()

  @Around("pointcut()")
  def handle(joinPoint: ProceedingJoinPoint): WebResult = {
    val request = RequestContextHolder.getRequestAttributes.asInstanceOf[ServletRequestAttributes].getRequest
    val obj = joinPoint.proceed()
    if (obj == null) return null
    val result = classOf[WebResult].cast(obj)
    val apiUrl = request.getServletPath
    val keyName = if (request.getParameter("keyName") == null) "default" else request.getParameter("keyName")
    import scala.collection.convert.wrapAsScala.asScalaBuffer
    val filterMaps = interceptorFieldService.getAllByApiUrlAndKeyName(apiUrl, keyName).map { (x: InterceptorField) => (x.fieldName, x.interceptorType) }.toMap
    if (filterMaps.isEmpty) return WebResult.error("keyName错误")
    proceed(result, filterMaps)
    result
  }

  private def proceed(result: WebResult, filterMaps: Map[String, Integer]): Unit = {
    if (result.containsKey("entity") && result.get("entity") != null) proceedEntity(result.get("entity").asInstanceOf[PhoenixDataEntity], filterMaps)
    if (result.containsKey("list") && result.get("list").asInstanceOf[ju.List[_]].size() > 0) proceedList(result.get("list").asInstanceOf[ju.List[PhoenixDataEntity]], filterMaps)
    if (result.containsKey("page") && result.get("page").asInstanceOf[PageInfo[_]].getList.size() > 0) proceedList(result.get("page").asInstanceOf[PageInfo[PhoenixDataEntity]].getList, filterMaps)
  }

  private def proceedEntity(obj: PhoenixDataEntity, filterMaps: Map[String, Integer]): Unit = {
    val fields = obj.getClass.getDeclaredFields
    fields.foreach { (x: Field) => {
      x.setAccessible(true)
      val index = getInterceptorType(x.getName, filterMaps)
      proceedByType(index, x, obj)
    }
    }
  }

  private def proceedList(obj: ju.List[PhoenixDataEntity], filterMaps: Map[String, Integer]): Unit = {
    import scala.collection.convert.wrapAsScala.asScalaBuffer
    val fields = obj.get(0).getClass.getDeclaredFields
    fields.foreach { (x: Field) =>
      val index = getInterceptorType(x.getName, filterMaps)
      x.setAccessible(true)
      obj.foreach { (o: PhoenixDataEntity) => {
        proceedByType(index, x, o)
      }
      }
    }
  }

  private def getInterceptorType(fieldName: String, filterMaps: Map[String, Integer]): java.lang.Integer = {
    if (filterMaps.contains(fieldName)) filterMaps(fieldName) else -1

  }

  private def proceedByType(index: Integer, field: Field, obj: PhoenixDataEntity): Unit = {
    index.intValue() match {
      case 0 => proceedByType0(field, obj)
      case 1 => proceedByType1(field, obj)
      case 2 => proceedByType2(field, obj)
      case _ => ()
    }
  }

  private def proceedByType0(f: Field, entity: PhoenixDataEntity): Unit = {
    val res = f.get(entity)
    if (res != null && classOf[String].isAssignableFrom(res.getClass)) f.set(entity, "***")
  }


  private def proceedByType1(f: Field, entity: PhoenixDataEntity): Unit = {
    import java.lang.{StringBuilder => Jbuilder}
    val res = f.get(entity)
    if (res != null && classOf[String].isAssignableFrom(res.getClass)) {
      val str = res.asInstanceOf[String]
      if (str.length <= 4) f.set(entity, "***") else f.set(entity, new Jbuilder(str).delete(2, str.length - 2).insert(2, "***").toString)
    }
  }

  private def proceedByType2(f: Field, entity: PhoenixDataEntity): Unit = {
    val res = f.get(entity)
    if (res != null && classOf[String].isAssignableFrom(res.getClass)) f.set(entity, StringEncode.encode(res.asInstanceOf[String]))
  }

}
