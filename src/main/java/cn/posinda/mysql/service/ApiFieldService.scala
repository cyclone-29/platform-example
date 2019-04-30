package cn.posinda.mysql.service

import cn.posinda.mysql.base.MysqlCrudService
import cn.posinda.mysql.entity.ApiField
import cn.posinda.mysql.repo.ApiFieldRepo

abstract class ApiFieldService extends MysqlCrudService[ApiFieldRepo,ApiField]
