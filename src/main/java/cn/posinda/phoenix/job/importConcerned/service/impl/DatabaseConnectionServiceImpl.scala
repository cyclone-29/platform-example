package cn.posinda.phoenix.job.importConcerned.service.impl

import cn.posinda.phoenix.job.importConcerned.entity.DatabaseConnection
import cn.posinda.phoenix.job.importConcerned.service.DatabaseConnectionService
import cn.posinda.phoenix.tools.ArgumentValidation
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.{Isolation, Transactional}

@Service
class DatabaseConnectionServiceImpl extends DatabaseConnectionService{

  @Transactional(transactionManager = "mysqlTransactionManager", readOnly = true)
  override def getByUrl(url: String): DatabaseConnection = {
    ArgumentValidation.checkNotEmpty(Array(url))
    this.repo.getByUrl(url)
  }

  @Transactional(transactionManager = "mysqlTransactionManager",isolation = Isolation.READ_COMMITTED,timeout = 60,rollbackFor = Array(classOf[Exception]))
  override def changeToNormal(url: String): Unit = {
    ArgumentValidation.checkNotEmpty(Array(url))
    this.repo.changeToNormal(url)
  }

  @Transactional(transactionManager = "mysqlTransactionManager",isolation = Isolation.READ_COMMITTED,timeout = 60,rollbackFor = Array(classOf[Exception]))
  override def changeToAbnormal(url: String): Unit = {
    ArgumentValidation.checkNotEmpty(Array(url))
    this.repo.changeToAbnormal(url)
  }
}
