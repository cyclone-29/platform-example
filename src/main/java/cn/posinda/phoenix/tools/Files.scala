package cn.posinda.phoenix.tools

import javax.servlet.http.HttpServletResponse

import cn.posinda.utils.{fileSystem, makeQualified}
import org.apache.hadoop.fs.{FileUtil, Path, PathFilter}
import org.apache.hadoop.io.IOUtils
import org.springframework.util.StringUtils

object Files {

  /**
    * 下载指定文件的通用方法
    *
    * @param fileName     需要下载的文件名称
    * @param userLocation 文件所在的目录，非绝对路径，一般由用户名加上用户指定的目录构成，例如：/LILY/EX
    * @param response     请求返回对象
    */
  def downLoadFile(fileName: String, userLocation: String, response: HttpServletResponse): Unit = {
    if (!StringUtils.hasText(fileName)) throw new IllegalArgumentException("错误的文件名称")
    if (!StringUtils.hasText(userLocation)) throw new IllegalArgumentException("错误的目录名称")
    val name = fileName.trim
    val location = makeQualified(name, userLocation)
    val path = new Path(location)
    if (!fileSystem.exists(path)) throw new Exception("指定的文件不存在")
    val status = fileSystem.getFileStatus(path)
    if (status.isDirectory) downLoadAndMergeFile(name, userLocation, response) else {
      val open = fileSystem.open(path)
      val out = response.getOutputStream
      IOUtils.copyBytes(open, out, 8192, false)
      IOUtils.closeStream(open)
      out.flush()
    }
  }

  /**
    * 本方法适用于数据处理情况，当hdfs上的数据按照一定的逻辑使用spark进行处理之后
    * 生成的结果为一个目录，将包含多个部分文件，本方法将多个部分文件同时下载，同时进行整合形成
    * 一个完整的文件,在大数据平台上表现为目录，但是在web端显示为文件
    *
    * @param dataDir      数据所在的目录
    * @param userLocation 用户指定的目录路径，非绝对路径，一般由用户名加上用户指定的目录名称构成，如:/lily/ex
    */
  def downLoadAndMergeFile(dataDir: String, userLocation: String, httpServletResponse: HttpServletResponse): Unit = {
    val location = makeQualified(dataDir, userLocation)
    val path = new Path(location)
    if (!fileSystem.exists(path)) throw new Exception("指定的文件或者文件夹不存在")
    val status = fileSystem.listStatus(path, new PathFilter {
      override def accept(path: Path): Boolean = !path.getName.startsWith(".") && !path.getName.startsWith("_")
    })
    val paths = FileUtil.stat2Paths(status)
    val out = httpServletResponse.getOutputStream
    for (x <- paths) {
      val in = fileSystem.open(x)
      IOUtils.copyBytes(in, out, 8192, false)
      IOUtils.closeStream(in)
    }
    out.flush()
  }

}
