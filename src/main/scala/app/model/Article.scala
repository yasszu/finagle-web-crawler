package app.model

import com.twitter.finagle.mysql.Client
import com.twitter.finagle.mysql._
import com.twitter.util.Future

case class Article(
    id: Option[Long],
    published: String,
    title: String,
    content: String,
    thumbnail: String,
    link: String,
    organization_id: Long
)

object Article {

  def insert(article: Article)(implicit client: Client): Future[Long] = {
    val sql = "INSERT INTO articles (published, title, content, thumbnail, link, organization_id) VALUES (?, ?, ?, ?, ?, ?)"
    val ps = client.prepare(sql)
    ps(article.published, article.title, article.content, article.thumbnail, article.link, article.organization_id) map { result =>
      result.asInstanceOf[OK].insertId
    } onFailure { throwable =>
      throwable.printStackTrace()
    }
  }

  def findAll(orgId: Int)(implicit client: Client): Future[Seq[Article]] = {
    val sql = s"SELECT * FROM articles a WHERE a.del_flg = 0 AND a.organization_id = $orgId ORDER BY a.id DESC LIMIT 0, 100"
    client.select(sql) { row =>
      val LongValue(id) = row("id").get
      val published = getStringValue(row, "published")
      val title = getStringValue(row, "title")
      val content = getStringValue(row, "content")
      val thumbnail = getStringValue(row, "thumbnail")
      val link = getStringValue(row, "link")
      val LongValue(organizationId) = row("organization_id").get
      Article(Some(id), published, title, content, thumbnail, link, organizationId)
    } onFailure { throwable =>
      throwable.printStackTrace()
    }
  }

  def findAll(orgId: Int, limit: Int, offset: Int)(implicit client: Client): Future[Seq[Article]] = {
    val sql = s"SELECT * FROM articles a WHERE a.del_flg = 0 AND a.organization_id = $orgId ORDER BY a.id DESC LIMIT $offset, $limit"
    client.select(sql) { row =>
      val LongValue(id) = row("id").get
      val published = getStringValue(row, "published")
      val title = getStringValue(row, "title")
      val content = getStringValue(row, "content")
      val thumbnail = getStringValue(row, "thumbnail")
      val link = getStringValue(row, "link")
      val LongValue(organizationId) = row("organization_id").get
      Article(Some(id), published, title, content, thumbnail, link, organizationId)
    } onFailure { throwable =>
      throwable.printStackTrace()
    }
  }

}