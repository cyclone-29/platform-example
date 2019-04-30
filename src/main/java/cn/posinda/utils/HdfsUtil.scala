package cn.posinda.utils

import cn.posinda.phoenix.helper.SimpleFileStatus
import org.apache.hadoop.fs.{Path, PathFilter}
import org.apache.hadoop.io.IOUtils
import org.springframework.beans.BeanUtils
import org.springframework.stereotype.Component

/**
  * HDFS文件和目录操作
  */
@Component
class HdfsUtil {


  /**
    * 指定用户获取指定目录下的所有文件和目录名称
    *
    * @param dir      指定的目录,例如格式为/test
    * @param userName 指定的用户
    * @return
    */
  def getFilesInSpecifiedDir(dir: String, userName: String): Array[SimpleFileStatus] = {
    val defaultFilter = new PathFilter {
      override def accept(path: Path): Boolean = !path.getName.startsWith(".") && !path.getName.startsWith("_")
    }
    val pathString = configuration.get("fs.defaultFS") + "/user/" + userName + dir
    val fileStatus = fileSystem.listStatus(new Path(pathString), defaultFilter)
    fileStatus.map(x=>{
      new SimpleFileStatus(x.getPath.getName,x.getLen,x.getModificationTime,x.getAccessTime,x.isDirectory)
    })
  }

  /**
    * 获取指定文件的大小，是否是文件，最后修改时间，最后访问时间
    *
    * @param fileName     指定的文件名称
    * @param userLocation 用户指定的目录 例如：/lily/ex
    * @return
    */
  def getStatus(fileName: String, userLocation: String): SimpleFileStatus = {
    checkQualifiedPath(fileName, userLocation)
    val pathString = makeQualified(fileName, userLocation)
    val simpleFileStatus = new SimpleFileStatus
    val status = fileSystem.getFileStatus(new Path(pathString))
    BeanUtils.copyProperties(status, simpleFileStatus)
    simpleFileStatus
  }

  def renameFile(formerFileName: String, userLocation: String, newFileName: String): Boolean = {
    val formerPathString = makeQualified(formerFileName, userLocation)
    val formerPath = new Path(formerPathString)
    if (!fileSystem.exists(formerPath)) throw new IllegalArgumentException(s"不存在指定的文件${userLocation + "/" + formerFileName}")
    val newPathString = makeQualified(newFileName, userLocation)
    val newPath = new Path(newPathString)
    if (fileSystem.exists(newPath)) throw new IllegalArgumentException(s"文件名${newFileName}以存在")
    fileSystem.rename(formerPath, newPath)
  }


  def renameDir(formerDirName: String, newDirName: String): Boolean = {
    val formerDirPathString = uploadLocation + formerDirName
    val newDirPathString = uploadLocation + newDirName
    val formerPath = new Path(formerDirPathString)
    if (!fileSystem.exists(formerPath)) throw new IllegalArgumentException(s"指定的文件夹${formerDirName}不存在")
    val newPath = new Path(newDirPathString)
    if (fileSystem.exists(newPath)) throw new IllegalArgumentException(s"重复的文件夹名$newDirName")
    fileSystem.rename(formerPath, newPath)
  }

  def createDir(dirName: String): Boolean = {
    val dirString = uploadLocation + dirName
    val path = new Path(dirString)
    if (fileSystem.exists(path)) throw new IllegalArgumentException(s"文件夹${dirName}已存在")
    fileSystem.mkdirs(path)
  }

  def moveFile(formerDir: String, fileName: String, newDir: String): Boolean = {
    val formerDirString = uploadLocation + formerDir
    val formerPath = new Path(formerDirString)
    if (!fileSystem.exists(formerPath)) throw new IllegalArgumentException(s"文件所在的文件夹${formerDir}不存在")
    val fileLocation = uploadLocation + formerDir + "/" + fileName
    val filePath = new Path(fileLocation)
    if (!fileSystem.exists(filePath)) throw new IllegalArgumentException(s"指定的文件${fileName}不存在")
    val newDirString = uploadLocation + newDir
    if (!fileSystem.exists(new Path(newDirString))) throw new IllegalArgumentException(s"目标文件夹${newDir}不存在")
    val newFileLocation = uploadLocation + newDir + "/" + fileName
    val newFilePath = new Path(newFileLocation)
    if (fileSystem.exists(newFilePath)) throw new IllegalArgumentException(s"${fileName}在目标文件夹${newDir}已存在")
    val stream = fileSystem.open(filePath)
    val stream1 = fileSystem.create(newFilePath)
    IOUtils.copyBytes(stream, stream1, 8192, true)
    fileSystem.delete(filePath, true)

  }

  def deleteFile(dirName: String, fileName: String): Boolean = {
    val fileLocation = uploadLocation + dirName + fileName
    val path = new Path(fileLocation)
    if(!fileSystem.exists(path)) throw new IllegalArgumentException(s"指定的文件${fileName}不存在")
    fileSystem.delete(path,true)
  }

  def deleteDir(dirName:String):Boolean = {
    val dirLocation = uploadLocation + dirName
    val dirPath = new Path(dirLocation)
    if(!fileSystem.exists(dirPath)) throw  new IllegalArgumentException(s"指定的文件夹${dirName}不存在")
    fileSystem.delete(dirPath,true)
  }


  private def checkQualifiedPath(fileName: String, userLocation: String): Unit = {
    def checkDirPath(userLocation: String): Unit = {
      val dirPath = uploadLocation + userLocation
      if (!fileSystem.exists(new Path(dirPath))) throw new IllegalArgumentException(s"指定的目录${userLocation}不存在")
    }

    checkDirPath(userLocation)
    val path = makeQualified(fileName, userLocation)
    if (!fileSystem.exists(new Path(path))) throw new IllegalArgumentException(s"指定的文件$fileName")
  }

}
