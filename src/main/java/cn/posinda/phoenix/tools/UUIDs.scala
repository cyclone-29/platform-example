package cn.posinda.phoenix.tools

import java.util.UUID

object UUIDs {

  def getInstance: String = UUID.randomUUID().toString.replaceAll("-", "")

  def getInstance(num:Int):Array[String] = {
    if(num <=0) throw new IllegalArgumentException(s"num must be positive")
    val arr = new Array[String](num)
    for(x <- arr.indices) arr.update(x,getInstance)
    arr
  }

}
