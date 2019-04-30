package cn.posinda.phoenix.job.importConcerned.entity

import cn.posinda.mysql.base.MysqlDataEntity

import scala.beans.BeanProperty

/**
  * mysql
  * oracle
  * sqlserver
  * postgresql
  * sqlite
  * 数据库连接监控
  */
@SerialVersionUID(1L)
class DatabaseConnection extends MysqlDataEntity {

  @BeanProperty var id: String = _  //主键

  @BeanProperty var url: String = _ //数据库连接url

  @BeanProperty var username: String = _ //数据库用户

  @BeanProperty var password: String = _ //数据库密码

  @BeanProperty var systemFrom:String = _ //对应的系统

  @BeanProperty var status:Integer = 0 //连接状态 0 正常 1 异常
}

object DatabaseConnection {
  val instance = new DatabaseConnection
}
