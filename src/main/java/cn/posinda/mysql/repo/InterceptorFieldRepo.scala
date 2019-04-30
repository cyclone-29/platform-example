package cn.posinda.mysql.repo

import cn.posinda.mysql.base.MysqlCrudRepo
import cn.posinda.mysql.entity.InterceptorField
import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Repository

@Repository
trait InterceptorFieldRepo extends MysqlCrudRepo[InterceptorField]{

  def getAllByApiUrlAndKeyName(@Param("apiUrl") apiUrl:String,@Param("keyName") keyName:String):java.util.List[InterceptorField]
}
