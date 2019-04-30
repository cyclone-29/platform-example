package cn.posinda.phoenix.service

import cn.posinda.base.PageQuery
import cn.posinda.phoenix.base.PhoenixCrudService
import cn.posinda.phoenix.entity.ControlAlarm
import cn.posinda.phoenix.repo.ControlAlarmRepo
import com.github.pagehelper.PageInfo

abstract class ControlAlarmService extends PhoenixCrudService[ControlAlarmRepo, ControlAlarm] {

  def getPageInTimeRange(arg: java.util.Map[String, Object],pageQuery:PageQuery): PageInfo[ControlAlarm]
}
