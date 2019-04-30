package cn.posinda.phoenix.repo

import cn.posinda.phoenix.base.PhoenixCrudRepo
import cn.posinda.phoenix.entity.ControlAlarm
import org.springframework.stereotype.Repository

@Repository
trait ControlAlarmRepo extends PhoenixCrudRepo[ControlAlarm]{

  def getPageInTimeRange(arg: java.util.Map[String, Object]):java.util.List[ControlAlarm]
}
