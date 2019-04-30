package cn.posinda.mysql.entity

import cn.posinda.mysql.base.MysqlDataEntity

import scala.beans.BeanProperty


@SerialVersionUID(1L)
class InterceptorField extends MysqlDataEntity {

  @BeanProperty var id: String = _  // 逻辑主键

  @BeanProperty var apiUrl: String = _  // 接口名称

  @BeanProperty var fieldName: String = _ // 接口返回的字段名称

  @BeanProperty var keyName: String = _ // 过滤方式对应的申请KEY

  @BeanProperty var createDt: String = _  // 创建时间

  @BeanProperty var updateDt: String = _  // 修改时间

  @BeanProperty var interceptorType: java.lang.Integer = _  //字段的过滤方式，只支持字符串过滤

}
