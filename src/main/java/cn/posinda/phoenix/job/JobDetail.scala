package cn.posinda.phoenix.job

import scala.beans.BeanProperty

class JobDetail(@BeanProperty var name: String = "",@BeanProperty var id: String = "",@BeanProperty var state:String = ""){
  override def equals(obj: scala.Any): Boolean = {
    obj match {
      case jobDetail: JobDetail => this.name == jobDetail.name
      case _ => false
    }
  }
}

object JobDetail {

  def getIndex(arr: Array[JobDetail], jobName:String): Int = {
    arr.indexOf(apply(jobName))
  }

  def apply(name:String):JobDetail = new JobDetail(name)
}


