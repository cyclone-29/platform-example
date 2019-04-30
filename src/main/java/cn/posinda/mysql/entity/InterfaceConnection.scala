package cn.posinda.mysql.entity

import cn.posinda.mysql.base.MysqlDataEntity

import scala.beans.BeanProperty

/**
  * 接口连接
  */
@SerialVersionUID(1L)
class InterfaceConnection extends MysqlDataEntity {

  @BeanProperty var id: String = _ //逻辑主键,32位uuid

  @BeanProperty var url: String = _ //接口对应的url地址,支持https,后面不要加上查询参数,例如http://192.168.1.10/shert/getLat,https://192.500.26.3:2300/lo/pl

  @BeanProperty var systemFrom: String = _ //接口来源的系统

  @BeanProperty var status:Integer = 0 //连接状态 0 正常 1 异常
}
