package cn.posinda.mysql.service

import cn.posinda.mysql.base.MysqlCrudService
import cn.posinda.mysql.entity.InterceptorField
import cn.posinda.mysql.repo.InterceptorFieldRepo

abstract class InterceptorFieldService extends MysqlCrudService[InterceptorFieldRepo,InterceptorField]{

  def getAllByApiUrlAndKeyName(apiUrl:String,keyName:String):java.util.List[InterceptorField]
}
