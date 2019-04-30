package cn.posinda.utils

import java.util

import org.springframework.stereotype.Component

import scala.annotation.varargs
import scala.collection.JavaConversions._

object Lists {

  /**
    * 获取java.util.List实例中元素的最大值，要求类型参数必须实现java.lang.Comparable接口
    * 和java.util.Collections.max(list)相等
    *
    * @param list 指定的list实例
    * @tparam T 类型参数
    * @return
    */
  def getMax[T <: Comparable[T]](list: util.List[T]): T = {
    list.max
  }

  /**
    * 获取java.util.List实例中元素的最小值，要求类型参数必须实现java.lang.Comparable接口
    * 和java.util.Collections.min(list)相等
    *
    * @param list 指定的list实例
    * @tparam T 类型参数
    * @return
    */
  def getMin[T <: Comparable[T]](list: util.List[T]): T = {
    list.min
  }

  /**
    * 判断指定的数组中是否存在指定的元素
    * 和java.util.Arrays.asList(arr).contains(ele)相等
    *
    * @param ele 需要判断存在的元素
    * @param arr 元素所在的数组
    * @tparam T 类型参数
    * @return
    */
  def exists[T](ele: T, arr: Array[T]): Boolean = {
    arr.contains(ele)
  }

  /**
    * 辅助构建mybatis查询对象，按照顺序传入查询参数，生成的key为arg0,arg1,arg2..
    * java-friendly method
    *
    * @param args 查询参数
    * @return
    */
  @varargs
  def parameterMap(args: Any*): java.util.Map[String, Any] = {
    val res = for(x <- args.indices) yield "arg"+x -> args(x)
    res.toMap[String,Any]
  }
}
