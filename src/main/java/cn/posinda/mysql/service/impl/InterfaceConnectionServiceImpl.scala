package cn.posinda.mysql.service.impl

import cn.posinda.mysql.service.InterfaceConnectionService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.{Isolation, Propagation, Transactional}

@Service
class InterfaceConnectionServiceImpl extends InterfaceConnectionService {

  @Transactional(transactionManager = "mysqlTransactionManager", isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, timeout = 5, rollbackFor = Array(classOf[Exception]))
  override def changeToAbNormal(url: String): Unit = repo.changeToAbNormal(url)

  @Transactional(transactionManager = "mysqlTransactionManager", isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, timeout = 5, rollbackFor = Array(classOf[Exception]))
  override def changeToNormal(url: String): Unit = repo.changeToNormal(url)
}
