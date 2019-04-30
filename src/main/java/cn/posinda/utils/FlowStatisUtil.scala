package cn.posinda.utils

import java.text.SimpleDateFormat
import java.util.Calendar

import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

import scala.annotation.varargs

@Component
class FlowStatisUtil {

  val dateFormat = new SimpleDateFormat("yyyy-MM-dd")

  def checkSpecifiedDateString(dateString: String): String = {
    val date = dateFormat.parse(dateString)
    if (date.after(currentDate)) currentDateString else dateString
  }


  private def currentDateString = dateFormat.format(Calendar.getInstance().getTime)

  private def currentDate = Calendar.getInstance().getTime

  def getMaxWeeksInSpecifiedYear(year: Int): Int = {
    val cal = Calendar.getInstance()
    cal.set(year, Calendar.DECEMBER, 31, 23, 59, 59)
    cal.setFirstDayOfWeek(Calendar.MONDAY)
    cal.setMinimalDaysInFirstWeek(7)
    cal.get(Calendar.WEEK_OF_YEAR)
  }

  def checkYearAndWeek(year: Int, week: Int): Unit = {
    if (year < 1900 || year > 2500) throw new IllegalArgumentException(s"错误的年份$year:小于1900或者大于2500")
    val totalWeeks = getMaxWeeksInSpecifiedYear(year)
    if (week < 1 || week > totalWeeks) throw new IllegalArgumentException(s"错误的星期设置$week,小于1或者大于在指定年份 $year 中的最大星期数")
  }

  def checkYearAndMonth(year: Int, month: Int): Unit = {
    if (year < 1900 || year > 2500) throw new IllegalArgumentException(s"错误的年份$year:小于1900或者大于2500")
    if (month < 1 || month > 12) throw new IllegalArgumentException(s"错误的月份$month,错误信息:大于12或者小于1")
  }


  @varargs
  def checkStringParam(params: String*): Unit = {
    val regex = "[\\u4e00-\\u9fa5]+"
    val replaceRegex = "[-()]"
    for (param <- params) {
      val result = param.replaceAll(replaceRegex, "")
      if (!StringUtils.hasText(result)) throw new IllegalArgumentException(s"无效的参数$param")
      if (!result.matches(regex)) throw new Exception(s"参数:$param,含有非法字符")
    }
  }

  def checkTimeBetween(start: String, end: String): Unit = {
    val startTime = dateFormat.parse(start)
    val endTime = dateFormat.parse(end)
    if (startTime.after(endTime)) throw new IllegalArgumentException(s"错误的开始时间和结束时间,开始时间$start 大于结束时间$end")
    if (start.equals(end)) throw new IllegalArgumentException(s"相等的开始时间$start 和结束时间$end")
  }
}
