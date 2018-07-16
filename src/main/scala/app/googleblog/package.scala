package app

import app.model.Article

import scala.xml._

/**
  * Created by Yasuhiro Suzuki on 2018/02/11.
  */
package object googleblog {

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
