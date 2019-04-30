package cn.posinda.phoenix.tools

import java.io.{File, FileInputStream, FileOutputStream}
import java.sql.ResultSet
import java.util
import javax.annotation.Resource
import javax.servlet.http.HttpServletResponse

import cn.posinda.mysql.entity.InterceptorField
import cn.posinda.mysql.utils.StringEncode
import cn.posinda.utils.makeQualifiedExcelFileName
import com.alibaba.druid.pool.DruidDataSource
import org.apache.hadoop.io.IOUtils
import org.apache.poi.ss.usermodel._
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

import scala.collection.mutable

@Component
class Excels {

  import org.apache.poi.hssf.util.HSSFColor
  import org.apache.poi.ss.usermodel.CellStyle
  import org.apache.poi.xssf.streaming.SXSSFWorkbook
  import org.apache.poi.xssf.usermodel.XSSFFont

  @Qualifier("phoenixDataSource")
  @Resource
  var dataSource: DruidDataSource = _


  /**
    * 获取excel文件标题格式
    *
    * @return
    */
  def getHeadStyle(workbook: SXSSFWorkbook): CellStyle = {
    val cellStyle = workbook.createCellStyle
    cellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex)
    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND)
    cellStyle.setAlignment(HorizontalAlignment.GENERAL)
    cellStyle.setVerticalAlignment(VerticalAlignment.CENTER)
    val font = workbook.createFont.asInstanceOf[XSSFFont]
    font.setBold(false)
    font.setFontName("宋体")
    font.setFontHeight(14)
    cellStyle.setFont(font)
    cellStyle.setBorderLeft(BorderStyle.THIN)
    cellStyle.setBorderBottom(BorderStyle.THIN)
    cellStyle.setBorderRight(BorderStyle.THIN)
    cellStyle.setBorderTop(BorderStyle.THIN)
    cellStyle
  }

  /**
    * 获取excel文件内容部分单元格格式
    *
    * @return
    */
  def getCellStyle(workbook: SXSSFWorkbook): CellStyle = {
    val cellStyle = workbook.createCellStyle
    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND)
    cellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex)
    cellStyle.setAlignment(HorizontalAlignment.GENERAL)
    cellStyle.setVerticalAlignment(VerticalAlignment.CENTER)
    val font = workbook.createFont.asInstanceOf[XSSFFont]
    font.setBold(false)
    font.setFontName("宋体")
    font.setFontHeight(12)
    cellStyle.setFont(font)
    cellStyle.setBorderLeft(BorderStyle.THIN)
    cellStyle.setBorderBottom(BorderStyle.THIN)
    cellStyle.setBorderRight(BorderStyle.THIN)
    cellStyle.setBorderTop(BorderStyle.THIN)
    cellStyle
  }

  /**
    * 下载excel文件，可以指定下载的字段，暂时只支持下载指定的条数，不支持其他限制条件
    * 同时存在以下的限制，考虑到大数据量的文件可能会引起内存溢出，所以下载的内容是通过特殊处理，预先下载到本机的excel文件，存储在本地，然后再将excel文件
    * 传递给请求方，最后删除本地文件
    *
    * @param response 请求返回对象
    * @param heads    下载字段对应的中文名称
    * @param sql      生成的查询SQL语句
    */
  def downLoadExcel(response: HttpServletResponse, heads: Array[String], sql: String, fields: Array[String], interceptorFields: util.List[InterceptorField]): Unit = {
    val connection = dataSource.getConnection
    val preparedStatement = connection.prepareStatement(sql)
    val resultSet = preparedStatement.executeQuery()
    val fileName = makeQualifiedExcelFileName()
    import scala.collection.convert.wrapAsScala.asScalaBuffer
    val fieldsMap: Map[String, Integer] = interceptorFields.map { (x: InterceptorField) => (x.fieldName, x.interceptorType) }.toMap
    resultSetToFile(resultSet, fileName, heads, fields, fieldsMap)
    resultSet.close()
    preparedStatement.close()
    downToClient(response, fileName)
  }


  /**
    * 将查询到所有数据保存为一个本地的excel文件
    *
    * @param resultSet 查询到的结果集
    * @param fileName  生成的本地excel文件名称
    * @param heads     excel文件的标题列
    */
  private def resultSetToFile(resultSet: ResultSet, fileName: String, heads: Array[String], fields: Array[String], fieldsMap: Map[String, Integer]): Unit = {
    val isEmpty = fieldsMap.isEmpty
    val rowNoPerSheet = 300000
    val wb = new SXSSFWorkbook(100)
    val headStyle = this.getHeadStyle(wb)
    val cellStyle = this.getCellStyle(wb)
    var sheet: Sheet = null
    var row: Row = null
    var rowNo = 0
    var pageNo = 0
    while (resultSet.next) {
      if (rowNo % rowNoPerSheet == 0) {
        sheet = wb.createSheet()
        sheet = wb.getSheetAt(rowNo / rowNoPerSheet)
        pageNo = 0
        row = sheet.createRow(pageNo)
        for (x <- heads.indices) {
          val cell = row.createCell(x)
          cell.setCellValue(heads(x))
          cell.setCellStyle(headStyle)
        }
      }
      rowNo += 1
      pageNo += 1
      row = sheet.createRow(pageNo)
      if (isEmpty) {
        for (x <- heads.indices) {
          val cell = row.createCell(x)
          cell.setCellValue(if (resultSet.getString(fields(x)) == null) "" else resultSet.getString(fields(x)))
          cell.setCellStyle(cellStyle)
        }
      } else {
        for (x <- heads.indices) {
          val cell = row.createCell(x)
          val typeIndex: Int = getInterceptorType(fields(x), fieldsMap)
          cell.setCellValue(if (resultSet.getString(fields(x)) == null) "" else process(resultSet.getString(fields(x)), typeIndex))
          cell.setCellStyle(cellStyle)

        }
      }
    }
    val fileOutputStream = new FileOutputStream(fileName)
    wb.write(fileOutputStream)
    wb.close()
    fileOutputStream.close()
  }

  private def getInterceptorType(fieldName: String, fieldsMap: Map[String, Integer]): Int = {
    if (fieldsMap.contains(fieldName)) fieldsMap(fieldName) else -1
  }

  import java.lang.{StringBuilder => Jbuilder}

  private def process(result: String, typeIndex: Int): String = {
    typeIndex match {
      case 0 => "***"
      case 1 => if (result.length <= 4) "***" else new Jbuilder(result).delete(2, result.length - 2).insert(2, "***").toString
      case 2 => StringEncode.encode(result)
      case _ => result
    }

  }

  /**
    * 保存为本地文件之后，传递给请求方
    *
    * @param response 请求返回对象
    * @param fileName 生成的本地文件的名称
    */
  private def downToClient(response: HttpServletResponse, fileName: String) = {
    val out = response.getOutputStream
    val in = new FileInputStream(fileName)
    IOUtils.copyBytes(in, out, 8192, false)
    out.flush()
    IOUtils.closeStream(in)
    new File(fileName).delete()
  }

  /**
    * 使用指定的下载列和下载条数组成sql语句
    *
    * @param heads           下载excel文件的标题
    * @param fieldNames      实体字段和数据库字段的映射
    * @param headsToDownload 需要下载的字段，以逗号分割的字符串
    * @param tableName       实体对应的表名称
    * @param count           需要下载的条数
    * @return
    */
  def makeSql(heads: Array[String], fieldNames: mutable.LinkedHashMap[String, String], headsToDownload: String, tableName: String, count: Int): (String, Array[String]) = {
    val res = fieldNames.unzip
    val databaseFieldNames = res._1.toArray
    val specifiedHeads = headsToDownload.split(",").distinct
    val specifiedFields = new Array[String](specifiedHeads.length)
    var index = 0
    var fieldIndex = 0
    for (x <- specifiedHeads) {
      index = heads.indexOf(x)
      specifiedFields(fieldIndex) = databaseFieldNames(index)
      fieldIndex += 1
    }
    val tmp = specifiedFields.clone()
    for (x <- specifiedFields.indices) specifiedFields.update(x, specifiedFields(x) + " AS " + fieldNames(specifiedFields(x)))
    val str = specifiedFields.mkString(",")
    val result = "SELECT " + str + " FROM " + tableName + "   LIMIT " + count.toString
    for (x <- tmp.indices) tmp.update(x, fieldNames(tmp(x)))
    (result, tmp)
  }

}
