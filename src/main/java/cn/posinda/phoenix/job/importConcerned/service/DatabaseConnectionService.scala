package cn.posinda.phoenix.job.importConcerned.service

import cn.posinda.mysql.base.MysqlCrudService
import cn.posinda.phoenix.job.importConcerned.entity.DatabaseConnection
import cn.posinda.phoenix.job.importConcerned.repo.DatabaseConnectionRepo

abstract class DatabaseConnectionService extends MysqlCrudService[DatabaseConnectionRepo,DatabaseConnection]{

  def getByUrl(url:String):DatabaseConnection

  def changeToNormal(url:String):Unit

  def changeToAbnormal(url:String):Unit

}
