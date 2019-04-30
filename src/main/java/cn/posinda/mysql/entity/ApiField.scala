package cn.posinda.mysql.entity

import cn.posinda.mysql.base.MysqlDataEntity

import scala.beans.BeanProperty

/**
  * 接口返回的字段名称，同一个接口不能含有相同的字段
  */
@SerialVersionUID(1L)
class ApiField extends MysqlDataEntity {

  @BeanProperty var id: String = _  // 逻辑主键

  @BeanProperty var fieldName: String = _ // 该接口返回的字段名称

  @BeanProperty var apiUrl: String = _  // 接口名称,controller mapping + method mapping,例如：/road/getPage

  @BeanProperty var description: String = _ // 过滤字段的中文描述信息

}
