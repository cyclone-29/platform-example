package cn.posinda.phoenix.job.importConcerned.repo

import cn.posinda.mysql.base.MysqlCrudRepo
import cn.posinda.phoenix.job.importConcerned.entity.ImportFailure
import org.springframework.stereotype.Repository

@Repository
trait ImportFailureRepo extends MysqlCrudRepo[ImportFailure]{

  def deleteAllByTableName(tableName:String)
}
