package app

import app.util.DateUtil._
import com.twitter.finagle.mysql._

package object model {

  def getStringValue(row: Row, columnName: String): String = {
    row(columnName).get match {
      case StringValue(t) => t
      case TimestampValue(timestamp) => convertToSimpleDate(timestamp)
      case _ => ""
    }
  }

}
