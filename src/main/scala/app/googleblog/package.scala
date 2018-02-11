package app

import app.model.Article

import scala.xml._

/**
  * Created by Yasuhiro Suzuki on 2018/02/11.
  */
package object googleblog {

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

  def parseArticles(xmlString: String, organizationId: Int): Seq[(Article, Seq[String])] = {
    val entry = XML.loadString(xmlString) \\ "entry"
    entry map { entry =>
      val title = (entry \ "title").text
      val content = (entry \ "content").text
      val published = (entry \ "published").text
      val categories = (entry \ "category") map (category => (category \ "@term").text)
      val thumbnail = (entry \ "thumbnail" \ "@url").text
      val originalUrl = (entry \ "origLink").text
      val article = Article(None, published, title, content, thumbnail, originalUrl, organizationId)
      (article, categories)
    }
  }

}
