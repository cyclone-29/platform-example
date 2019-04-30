package cn.posinda.phoenix.tools

import java.text.SimpleDateFormat
import java.util.Calendar

/**
  * 日期辅助对象，辅助处理获取指定时间区间内数据的请求
  * 在处理获取时间区间内数据的请求时，如果表的数据量较大，需要加上索引，可以在
  * 时间列上加索引，加快数据查找
  */
object DateUtil {

  /**
    * 获取指定年份和指定月份的第一天,月份不需要减去1，如果需要查找一月份的第一天，直接传入month=1即可
    * eg:year=2019，month=1,return 2019-01-01
    *
    * @param year  查找的指定年份
    * @param month 查找的指定月份
    * @return
    */
  def getFirstDayOfMonth(year: Int, month: Int): String = {
    val monthStr = if (month < 10) "0" + month
    else String.valueOf(month)
    year + "-" + monthStr + "-" + "01"
  }

  /**
    * 获取指定年份和指定月份的最后一天,月份不需要减去1，如果需要查找一月份的最后一天，直接传入month=1即可
    * eg:year=2019，month=1,return 2019-01-31
    *
    * @param year  查找的指定年份
    * @param month 查找的指定月份
    * @return
    */
  def getLastDayOfMonth(year: Int, month: Int): String = {
    val calendar = Calendar.getInstance
    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, month - 1)
    calendar.set(Calendar.DATE, 1)
    calendar.add(Calendar.MONTH, 1)
    calendar.add(Calendar.DAY_OF_YEAR, -1)
    calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH)
  }

  private def getCalendarFormYear(year: Int) = {
    val cal = Calendar.getInstance
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    cal.set(Calendar.YEAR, year)
    cal
  }

  /**
    * 获取指定年份，指定星期的第一天日期
    * eg:year=2019,weekNo=1 return 2018-12-31
    *
    * @param year   指定的年份
    * @param weekNo 指定的星期
    * @return
    */
  def getStartDayOfWeekNo(year: Int, weekNo: Int): String = {
    val cal = getCalendarFormYear(year)
    cal.set(Calendar.WEEK_OF_YEAR, weekNo)
    cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH)
  }

  /**
    * 获取指定年份，指定星期的最后一天的日期
    * eg:year=2019,weekNo=1 return 2019-1-6
    *
    * @param year   指定的年份
    * @param weekNo 指定的星期
    * @return
    */
  def getEndDayOfWeekNo(year: Int, weekNo: Int): String = {
    val cal = getCalendarFormYear(year)
    cal.set(Calendar.WEEK_OF_YEAR, weekNo)
    cal.add(Calendar.DAY_OF_WEEK, 6)
    cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH)
  }


  /**
    * 返回过去指定小时的时间元组(pastString,nowString)，
    *
    * @param minusHour 减去的小时数，为负数
    * @param pattern 返回的时间字符串的格式,默认为yyyy-MM-dd HH:mm:ss
    * @return
    */
  def getTimeTupleMinusHour(minusHour: Int, pattern: String = "yyyy-MM-dd HH:mm:ss"): (String, String) = {
    val cal = Calendar.getInstance()
    val format = new SimpleDateFormat(pattern)
    val now = cal.getTime
    cal.add(Calendar.HOUR, minusHour)
    val past = cal.getTime
    (format.format(past), format.format(now))
  }
}
