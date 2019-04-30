package cn.posinda.mysql.utils

object StringEncode {

  /**
    * 字符串处理
    * 当字符串为null或者字符串的长度为0的时候，随机返回一个可打印的字符
    * 当字符串的长度不为0的时候，字符元素加上10以内的随机数
    * 返回新的字符元素组成的字符串
    *
    * @param param 字符串参数，可为null
    */
  def encode(param: String): String = {
    val random = scala.util.Random
    if (param == null || param.length == 0) String.valueOf(random.nextPrintableChar())
    else {
      val chars = param.toCharArray
      val res = chars.map { (x: Char) => (x + random.nextInt(10)).toChar }
      new String(res)
    }
  }
}
