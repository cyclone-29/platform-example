package cn.posinda.phoenix.job.importConcerned.service.impl

import cn.posinda.phoenix.job.importConcerned.service.ImportFailureService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.{Isolation, Transactional}

@Service
class ImportFailureServiceImpl extends ImportFailureService {

  @Transactional(transactionManager = "mysqlTransactionManager", isolation = Isolation.READ_COMMITTED, timeout = 60, rollbackFor = Array(classOf[Exception]))
  override def deleteAllByTableName(tableName: String): Unit = this.repo.deleteAllByTableName(tableName)
}
