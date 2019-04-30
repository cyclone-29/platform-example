package cn.posinda.mysql.repo

import cn.posinda.mysql.base.MysqlCrudRepo
import cn.posinda.mysql.entity.InterfaceConnection
import org.springframework.stereotype.Repository

@Repository
trait InterfaceConnectionRepo extends MysqlCrudRepo[InterfaceConnection]{

  def changeToAbNormal(url: String): Unit

  def changeToNormal(url: String): Unit
}
