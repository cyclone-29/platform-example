package cn.posinda.mysql.service

import cn.posinda.mysql.base.MysqlCrudService
import cn.posinda.mysql.entity.InterfaceConnectionError
import cn.posinda.mysql.repo.InterfaceConnectionErrorRepo

abstract class InterfaceConnectionErrorService extends MysqlCrudService[InterfaceConnectionErrorRepo,InterfaceConnectionError]
