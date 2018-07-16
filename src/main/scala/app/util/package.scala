package app

import com.twitter.logging.Logger

/**
  * Created by Yasuhiro Suzuki on 2018/05/20.
  */
package object util {

  val log = Logger.get(getClass)

  /**
    * Parse date format
    *
    * @param source 'yyyy-MM-dd'T'HH:mm:ss'
    * @return yyyMMdd
    */
  def parseDateFormat(source: String): Int = {
    val format = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    val date = format.parse(source)
    new java.text.SimpleDateFormat("yyyyMMdd").format(date).toInt
  }

}
