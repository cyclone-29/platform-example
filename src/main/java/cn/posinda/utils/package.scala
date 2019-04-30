package cn.posinda

import java.io.File
import java.util.UUID

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem

package object utils {

  val configuration: Configuration = new Configuration(true)

  val uploadLocation: String = configuration.get("fs.defaultFS")+"/user"

  val fileSystem: FileSystem = FileSystem.get(configuration)

  val excelTmpDir = "/tmp/excelTmp"

  /**
    * 根据文件名称和用户目录组成完整的路径
    *
    * @param fileName     文件名称
    * @param userLocation 用户指定的目录 例如：/lily/ex
    * @return
    */
  def makeQualified(fileName: String, userLocation: String): String = {
    uploadLocation + userLocation + "/" + fileName
  }

  /**
    * 将excel文件下载到本地的时候，为了防止多用户的同时调用
    * 生成的excel文件名会根据UUID生成字符串作为文件名
    *
    * @return
    */
  def makeQualifiedExcelFileName(): String = {
    if(!new File(excelTmpDir).exists()) new File(excelTmpDir).mkdirs()
    excelTmpDir+UUID.randomUUID().toString.replaceAll("-", "") + ".xlsx"
  }

}
