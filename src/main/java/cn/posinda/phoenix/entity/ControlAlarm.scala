package cn.posinda.phoenix.entity

import cn.posinda.phoenix.base.PhoenixDataEntity

import scala.beans.BeanProperty

/**
  * 电警布控⻋辆⽇告警信息,随时间递增数据
  */
@SerialVersionUID(1L)
class ControlAlarm extends PhoenixDataEntity {
  @BeanProperty var pk: String = _
  //  PK 逻辑主键
  @BeanProperty var tollgateCode: String = _
  //  TOLLGATE_CODE 卡口编号
  @BeanProperty var laneNo: String = _
  //  LANE_NO 车道号
  @BeanProperty var capTime: String = _
  //  CAP_TIME 抓拍时间,格式为yyyy-MM-dd HH:mm:ss
  @BeanProperty var plateCode: String = _
  //  PLATE_CODE 车牌号码
  @BeanProperty var plateColor: String = _
  //  PLATE_COLOR 车牌颜色
  @BeanProperty var logo: String = _
  //  LOGO 车辆LOGO
  @BeanProperty var vehicleType: String = _
  //  VEHICLE_TYPE 车辆类型
  @BeanProperty var vehicleColor: String = _
  //  VEHICLE_COLOR 车身颜色
  @BeanProperty var imgUrl: String = _
  //  IMG_URL 合成图片URL
  @BeanProperty var location: String = _
  //  LOCATION 卡口名称
  @BeanProperty var direction: String = _
  //  DIRECTION 行驶方向
  @BeanProperty var plateCodeUrl: String = _
  //  PLATE_CODE_URL 车牌图片URL
  @BeanProperty var plateConfidence: String = _
  //  PLATE_CONFIDENCE 车牌识别的置信度
  @BeanProperty var speed: String = _
  //  SPEED 车速
  @BeanProperty var alarmType: String = _
  //  ALARM_TYPE 告警类型
  @BeanProperty var alarmControlType: String = _
  //  ALARM_CONTROL_TYPE 布控告警类型
  @BeanProperty var alarmControlContent: String = _
  //  ALARM_CONTROL_CONTENT 布控告警内容
  @BeanProperty var alarmControlDesc: String = _ //  ALARM_CONTROL_DESC 布控告警描述
}
