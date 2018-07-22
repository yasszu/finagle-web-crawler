package app.model

import app.util.DateUtil._
import com.twitter.finagle.mysql.{Client, _}
import com.twitter.util.Future

case class Article(
    id: Option[Long],
    published: String,
    title: String,
    content: String,
    thumbnail: String,
    link: String,
    organization_id: Long
) {

  def biggerThan(that: Article): Boolean = {
    val target = convertToTimestamp(that.published)
    val self = convertToTimestamp(this.published)
    self after target
  }

}

object Article {

  def insert(article: Article)(implicit client: Client): Future[Long] = {
    val sql = "INSERT INTO articles (published, title, content, thumbnail, link, organization_id) VALUES (?, ?, ?, ?, ?, ?)"
    val ps = client.prepare(sql)
    val published = convertToMysqlFormat(article.published)
    ps(published, article.title, article.content, article.thumbnail, article.link, article.organization_id) map { result =>
      result.asInstanceOf[OK].insertId
    } onFailure { throwable =>
      throwable.printStackTrace()
    }
  }

  def findAll(orgId: Int, limit: Int = 100, offset: Int = 0)(implicit client: Client): Future[Seq[Article]] = {
    select(s"SELECT * FROM articles a WHERE a.del_flg = 0 AND a.organization_id = $orgId ORDER BY a.published DESC LIMIT $offset, $limit")
  }

  def select(sql: String)(implicit client: Client): Future[Seq[Article]] = {
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