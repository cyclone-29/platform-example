package cn.posinda.phoenix.helper

import scala.beans.BeanProperty

/**
  * 下载检索条件
  *
  * @param index       对象下标
  * @param columns     查询条件的列,数据库列,不是实体类的属性列
  * @param cNames      列的中文描述
  * @param description 传递参数时的注意事项
  */
case class QueryCondition(@BeanProperty index: Int, @BeanProperty columns: Array[String], @BeanProperty cNames: Array[String], @BeanProperty description: String)
