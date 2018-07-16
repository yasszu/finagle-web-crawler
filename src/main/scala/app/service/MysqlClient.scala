package app.service

import java.net.InetSocketAddress

import app.util.DDL
import app.util._
import com.twitter.finagle.Mysql
import com.twitter.finagle.mysql._
import com.twitter.util.Future

/**
  * Created by Yasuhiro Suzuki on 2018/03/06.
  */
trait MysqlClient {

  val host: String
  val port: Int
  val user: String
  val password: String
  val db: String

  lazy val addr: InetSocketAddress = new InetSocketAddress(host, port)

  implicit lazy val mysqlClient: Client with Transactions = Mysql.client
    .withCredentials(user, password)
    .withDatabase(db)
    .newRichClient("%s:%d".format(addr.getHostName, addr.getPort))

  def createArticlesTables()(implicit client: Client): Future[Result] = {
    client.query(DDL.createArticlesTable).onSuccess { _ =>
      log.info("Create Articles table")
    }
  }

  def createCategoriesTables()(implicit client: Client): Future[Result] = {
    client.query(DDL.createCategoriesTable).onSuccess { _ =>
      log.info("Create Categories table")
    }
  }

}
