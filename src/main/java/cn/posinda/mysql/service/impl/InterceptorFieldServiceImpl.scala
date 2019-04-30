package cn.posinda.mysql.service.impl

import java.util

import cn.posinda.mysql.entity.InterceptorField
import cn.posinda.mysql.service.InterceptorFieldService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class InterceptorFieldServiceImpl extends InterceptorFieldService {

  @Transactional(transactionManager = "mysqlTransactionManager", readOnly = true)
  override def getAllByApiUrlAndKeyName(apiUrl: String,keyName:String): util.List[InterceptorField] = repo.getAllByApiUrlAndKeyName(apiUrl,keyName)
}
