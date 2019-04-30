package cn.posinda.mysql.entity

import cn.posinda.mysql.base.MysqlDataEntity

import scala.beans.BeanProperty

/**
  * 接口连接异常信息
  */
@SerialVersionUID(1L)
class InterfaceConnectionError extends MysqlDataEntity {

  @BeanProperty var id: String = _ //逻辑主键，32位uuid

  @BeanProperty var checkTime: String = _ //检测到的时间

  @BeanProperty var interfaceName: String = _ //接口名称，即是url地址名称
}


