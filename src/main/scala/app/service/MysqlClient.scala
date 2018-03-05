package app.service

import java.net.InetSocketAddress

import app.util.DDL
import com.twitter.app.Flag
import com.twitter.finagle.Mysql
import com.twitter.finagle.mysql._
import com.twitter.util.Future

/**
  * Created by Yasuhiro Suzuki on 2018/03/06.
  */
trait MysqlClient {

  val host: Flag[InetSocketAddress]
  val user: String
  val password: String
  val db: String

  implicit lazy val mysqlClient: Client with Transactions = Mysql.client
    .withCredentials(user, password)
    .withDatabase(db)
    .newRichClient("%s:%d".format(host().getHostName, host().getPort))

  def createSchema(): Future[Result] = {
    Mysql.client
      .withCredentials(user, password)
      .newRichClient("%s:%d".format(host().getHostName, host().getPort))
      .query(DDL.createSchema).onSuccess { _ =>
      println("[INFO] Create schema finagle_web_crawler")
    }
  }

  def createArticlesTables()(implicit client: Client): Future[Result] = {
    client.query(DDL.createArticlesTable).onSuccess { _ =>
      println("[INFO] Create Articles table")
    }
  }

  def createCategoriesTables()(implicit client: Client): Future[Result] = {
    client.query(DDL.createCategoriesTable).onSuccess { _ =>
      println("[INFO] Create Categories table")
    }
  }

}
