package cn.posinda.mysql.repo

import cn.posinda.mysql.base.MysqlCrudRepo
import cn.posinda.mysql.entity.ApiField
import org.springframework.stereotype.Repository

@Repository
trait ApiFieldRepo extends MysqlCrudRepo[ApiField]
