package cn.posinda.phoenix.tools

import java.text.SimpleDateFormat
import java.util.Calendar

object ArgumentValidation {


  /**
    * 验证所有的字符串参数都不为null且不全是空格
    *
    * @param args 待校验的字符串数组
    * @return
    */
  def checkNotEmpty(args: Array[String]): Unit = {
    if (!args.forall { (x: String) => x != null && x.trim.length != 0 }) throw new IllegalArgumentException("非法参数")
  }

  /**
    * 验证所有的数字参数按照升序排列
    *
    * @param args 待校验的数字参数数组
    * @return
    */
  def checkVal(args: Array[Int]): Unit = {
    val arr = args.clone()
    scala.util.Sorting.quickSort(arr)
    if (!arr.sameElements(args)) throw new IllegalArgumentException("错误的参数顺序")
  }

  /**
    * 验证所有的时间字符串符合特定的格式，排序方式可选
    *
    * @param args    待校验的时间字符串
    * @param pattern 校验时间字符串格式
    * @param sorting 时间字符串的排序方式，默认为升序
    * @return
    */
  def checkDate(args: Array[String], pattern: String = "yyyy-MM-dd HH:mm:ss", sorting: Boolean = true): Unit = {
    val format = new SimpleDateFormat(pattern)
    val dates = args.map(format.parse)
    if (sorting) {
      val tmp = dates.clone()
      scala.util.Sorting.quickSort(tmp)
      if (!tmp.sameElements(dates)) throw new IllegalArgumentException("错误的参数顺序")
    }
  }

  /**
    * 验证查询时间间隔在三个月之内,且符合指定的格式,要求参数arg的长度为2
    *
    * @param args    时间字符串数组
    * @param pattern 时间字符串格式
    * @return
    */
  def checkBetweenThreeMonths(args: Array[String], pattern: String = "yyyy-MM-dd HH:mm:ss"): Unit = {
    if (args.length != 2) throw new IllegalArgumentException("错误的参数个数,需要为2")
    val format = new SimpleDateFormat(pattern)
    val start = format.parse(args(0))
    val end = format.parse(args(1))
    val cal = Calendar.getInstance()
    cal.setTime(end)
    cal.add(Calendar.MONTH, -3)
    if (cal.getTime.after(start)) throw new IllegalArgumentException("查询时间间隔必须在三个月之内")
  }

}
