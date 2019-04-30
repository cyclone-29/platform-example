package cn.posinda.phoenix.service.impl

import cn.posinda.base.PageQuery
import cn.posinda.phoenix.entity.ControlAlarm
import cn.posinda.phoenix.service.ControlAlarmService
import com.github.pagehelper.PageInfo
import com.github.pagehelper.page.PageMethod
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ControlAlarmServiceImpl extends ControlAlarmService {

  @Transactional(transactionManager = "phoenixTransactionManager", readOnly = true)
  override def getPageInTimeRange(arg: java.util.Map[String, Object], pageQuery: PageQuery): PageInfo[ControlAlarm] = {
    if (StringUtils.isBlank(pageQuery.getOrderBy)) PageMethod.startPage[ControlAlarm](pageQuery.getPageNum, pageQuery.getPageSize)
    else PageMethod.startPage[ControlAlarm](pageQuery.getPageNum, pageQuery.getPageSize, pageQuery.getOrderBy + " " + pageQuery.getOrderFlag)
    new PageInfo[ControlAlarm](repo.getPageInTimeRange(arg))
  }
}
