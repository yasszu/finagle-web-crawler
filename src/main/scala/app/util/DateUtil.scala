package app.util

import java.sql.Timestamp

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

object DateUtil {

  val defaultPattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
  val formatterPattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ"
  val mysqlPattern = "YYYY-MM-dd HH:mm:ss"

  def convertToMysqlFormat(dateTimeStr: String): String = {
    val format = new java.text.SimpleDateFormat(defaultPattern)
    val date = format.parse(dateTimeStr)
    new java.text.SimpleDateFormat(mysqlPattern).format(date)
  }

  def convertToSimpleDate(timestamp: Timestamp): String = {
    val date = new DateTime(timestamp).toDate
    new java.text.SimpleDateFormat(defaultPattern).format(date)
  }

  /**
    * Convert to Timestamp
    * e.g. 2018-07-20T09:55:00.000-07:00
    *
    * @param dateTimeStr yyyy-MM-dd'T'HH:mm:ss.SSSXXX
    * @return Timestamp
    */
  def convertToTimestamp(dateTimeStr: String): Timestamp = {
    val formatter = DateTimeFormat.forPattern(formatterPattern)
    val dateTime = formatter.parseDateTime(dateTimeStr)
    new Timestamp(dateTime.getMillis)
  }

}
