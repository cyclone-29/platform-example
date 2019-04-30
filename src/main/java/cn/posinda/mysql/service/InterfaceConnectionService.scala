package cn.posinda.mysql.service

import cn.posinda.mysql.base.MysqlCrudService
import cn.posinda.mysql.entity.InterfaceConnection
import cn.posinda.mysql.repo.InterfaceConnectionRepo

abstract class InterfaceConnectionService extends MysqlCrudService[InterfaceConnectionRepo, InterfaceConnection] {

  def changeToAbNormal(url: String): Unit

  def changeToNormal(url: String): Unit
}
