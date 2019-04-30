package cn.posinda.phoenix.job.importConcerned.service

import cn.posinda.mysql.base.MysqlCrudService
import cn.posinda.phoenix.job.importConcerned.entity.ImportFailure
import cn.posinda.phoenix.job.importConcerned.repo.ImportFailureRepo

abstract class ImportFailureService extends MysqlCrudService[ImportFailureRepo,ImportFailure]{

  def deleteAllByTableName(tableName:String)
}
