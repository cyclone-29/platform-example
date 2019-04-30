package cn.posinda.utils

import java.io.{File, FileInputStream, InputStream}
import javax.annotation.Resource

import cn.posinda.phoenix.entity.TableInfo
import cn.posinda.phoenix.service.TableInfoService
import com.alibaba.druid.pool.DruidDataSource
import org.apache.hadoop.fs.Path
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.io.IOUtils
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.{Cell, CellType, Workbook}
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
class UploadUtil {

  val EXCEL_XLS: String = "xls"

  val EXCEL_XLSX: String = "xlsx"

  val tmp: String = "/tmp"

  if (!new File(tmp).exists()) new File(tmp).mkdirs()

  @Qualifier("phoenixDataSource")
  @Resource
  var dataSource: DruidDataSource = _

  @Resource
  var tableInfoService: TableInfoService = _

  @Resource
  var hbaseUtil:HbaseUtil = _


  /**
    * 文件上传处理
    *
    * @param inputStream  上传文件输入流
    * @param fileName     文件名称
    * @param userLocation 存放文件的目录，一般由用户名加上用户指定的目录构成，例如：/LILY/EX,不能包含中文名称
    */
  def uploadFile(inputStream: InputStream, fileName: String, userLocation: String): Unit = {
    checkExists(userLocation)
    val path = makeQualified(fileName, userLocation)
    val location = new Path(path)
    val stream = fileSystem.create(location)
    IOUtils.copyBytes(inputStream, stream, 8192, true)
  }


  /**
    * 检查指定的文件夹是否存在,如果不存在则创建
    *
    * @param userLocation 用户指定的上传目录
    */
  private def checkExists(userLocation: String): Unit = {
    val pathStr = uploadLocation + userLocation
    val path = new Path(pathStr)
    if (!fileSystem.exists(path)) fileSystem.mkdirs(path)
  }

  /**
    * 将指定的excel文件导入到数据库中，暂时支持以下情况：
    * 如果需要导入到数据库，那么会完全删除以前的数据，不支持增量导入
    *
    * @param filePath     excel文件的绝对路径
    * @param tableName    需要导入的表名称
    * @param filesCount   表的字段的数量
    */
  def uploadExcelToDataBase(filePath: String, tableName: String, filesCount: Int): Unit = {
    checkFile(filePath)
    checkDataBase(tableName)
    val sql = makeQualifiedSql(tableName, filesCount)
    loadToDataBase(filePath, sql, filesCount, tableName)

  }

  /**
    * 清空原本的数据表
    *
    * @param tableName 清空数据库表名
    */
  private def checkDataBase(tableName: String): Unit = {
    val table = TableName.valueOf(tableName)
    val exists = hbaseUtil.admin.tableExists(table)
    if(!exists) throw new IllegalArgumentException(s"specified table $tableName not exist")
    hbaseUtil.admin.truncateTable(table,true)
  }

  /**
    * 检查需要导入的excel文件的正确性
    *
    * @param filePath 文件的绝对路径
    */
  private def checkFile(filePath: String): Unit = {
    val file = new File(filePath)
    if (!file.exists()) throw new Exception(s"specified file not found, expected file is$filePath")
    if (!file.isFile) throw new Exception("expect file,found dir")
    if (!(file.getName.endsWith(EXCEL_XLS) || file.getName.endsWith(EXCEL_XLSX))) throw new Exception("specified file not found")
  }

  private def getWorkBook(file: File, fileInputStream: FileInputStream): Workbook = {
    if (file.getName.endsWith(EXCEL_XLS)) new HSSFWorkbook(fileInputStream) else new XSSFWorkbook(fileInputStream)
  }

  /**
    * 根据表的名称以及字段的总数创建sql preparedStatement语句，字段的数量决定？的数量
    *
    * @param tableName   表名称
    * @param fieldsCount 字段的总数
    * @return
    */
  private def makeQualifiedSql(tableName: String, fieldsCount: Int): String = {
    val arr = new Array[String](fieldsCount)
    for (x <- arr.indices) arr(x) = "?"
    val valuesStr = arr.mkString("(", ",", ")")
    "UPSERT INTO " + tableName + " VALUES" + valuesStr
  }

  private def loadToDataBase(filePath: String, sql: String, fieldsCount: Int, tableName: String): Unit = {
    val file = new File(filePath)
    val inputStream = new FileInputStream(file)
    val wb = getWorkBook(file, inputStream)
    val connection = dataSource.getConnection
    connection.setAutoCommit(false)
    val preparedStatement = connection.prepareStatement(sql)
    val numOfSheets = wb.getNumberOfSheets
    var totalRowNum = 0L
    for (x <- 0 until numOfSheets) {
      val sheet = wb.getSheetAt(x)
      if (sheet.getLastRowNum > 0) {
        preparedStatement.clearBatch()
        val rowNo = sheet.getLastRowNum
        totalRowNum += rowNo - 1
        for (x <- 2 to rowNo) {
          val row = sheet.getRow(x)
          for (x <- 0 until fieldsCount) {
            val value = getResult(row.getCell(x))
            preparedStatement.setString(x + 1, value)
          }
          preparedStatement.addBatch()
        }
        preparedStatement.executeBatch()
        connection.commit()
      }
    }
    connection.setAutoCommit(true)
    wb.close()
    inputStream.close()
    file.delete()
    val tableInfo = TableInfo.builder().tableName(tableName).recordCount(totalRowNum).build()
    tableInfoService.updateRowNum(tableInfo)
  }

  private def getResult(cell: Cell): String = {
    if (cell == null) "" else
      cell.getCellType match {
        case CellType.NUMERIC => cell.getNumericCellValue.toInt.toString
        case CellType.BOOLEAN => cell.getBooleanCellValue.toString
        case CellType.STRING => cell.getStringCellValue
        case _ => ""
      }
  }

}
