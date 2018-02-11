package app.model

import com.twitter.finagle.mysql.{Client, OK}
import com.twitter.util.Future

case class Category(
    id: Option[Long],
    articleId: Long,
    term: String
)

object Category {

  def insert(category: Category)(implicit client: Client): Future[Long] = {
    val sql = "INSERT INTO categories (term, article_id) VALUES (?, ?)"
    val ps = client.prepare(sql)
    ps(category.term, category.articleId) map { result =>
      result.asInstanceOf[OK].insertId
    } onFailure { throwable =>
      throwable.printStackTrace()
    }
  }

}