package cn.posinda.phoenix.job.importConcerned.web

import javax.annotation.Resource

import cn.posinda.base.{PageQuery, WebResult}
import cn.posinda.phoenix.job.importConcerned.entity.ImportFailure
import cn.posinda.phoenix.job.importConcerned.service.ImportFailureService
import cn.posinda.phoenix.tools.ArgumentValidation
import org.springframework.web.bind.annotation.{GetMapping, RequestMapping, RestController}


@RestController
@RequestMapping(value = Array("/importFailure"))
class ImportFailureController{

  @Resource
  var service:ImportFailureService = _

  @GetMapping(value = Array("/getSpecified"))
  def getSpecified(id:String):WebResult = {
    ArgumentValidation.checkNotEmpty(Array(id))
    WebResult.success().put("entity",service.get(id))
  }

  @GetMapping(value = Array("/getPage"))
  def getPage(pageQuery: PageQuery):WebResult = {
    WebResult.success().put("page",service.findPage(new ImportFailure,pageQuery))
  }

  @GetMapping(value = Array("/getAll"))
  def getAll:WebResult = WebResult.success().put("list",service.findList(new ImportFailure))

}
