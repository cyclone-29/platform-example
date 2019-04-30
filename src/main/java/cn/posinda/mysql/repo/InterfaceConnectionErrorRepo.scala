package cn.posinda.mysql.repo

import cn.posinda.mysql.base.MysqlCrudRepo
import cn.posinda.mysql.entity.InterfaceConnectionError
import org.springframework.stereotype.Repository

@Repository
trait InterfaceConnectionErrorRepo extends MysqlCrudRepo[InterfaceConnectionError]
