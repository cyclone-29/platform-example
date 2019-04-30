package cn.posinda.phoenix.job.importConcerned.repo

import cn.posinda.mysql.base.MysqlCrudRepo
import cn.posinda.phoenix.job.importConcerned.entity.DatabaseConnection
import org.springframework.stereotype.Repository

@Repository
trait DatabaseConnectionRepo extends MysqlCrudRepo[DatabaseConnection]{

  def getByUrl(url:String):DatabaseConnection

  def changeToNormal(url:String):Unit

  def changeToAbnormal(url:String):Unit
}
