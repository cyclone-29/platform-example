package cn.posinda.phoenix.job.importConcerned.entity

import cn.posinda.mysql.base.MysqlDataEntity

import scala.beans.BeanProperty

/**
  * sqoop拉取数据失败信息
  */
@SerialVersionUID(1L)
class ImportFailure extends MysqlDataEntity {

  @BeanProperty var id: String = _

  @BeanProperty var checkTime: String = _ //检测到的时间

  @BeanProperty var taskName: String = _ //任务名称

  @BeanProperty var tableName: String = _ //所要拉取的表的名称
}
