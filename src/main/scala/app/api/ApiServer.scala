package app.api

import java.net.InetSocketAddress

import app.googleblog.GoogleBlogApi
import app.util.DDL
import com.twitter.finagle.mysql._
import com.twitter.finagle.{Http, Mysql}
import com.twitter.server.TwitterServer
import com.twitter.util.{Await, Future}
import com.typesafe.config.ConfigFactory
import io.circe.generic.auto._
import io.finch.circe._

object ApiServer extends TwitterServer {

  override def failfastOnFlagsNotParsed: Boolean = true

  val conf = ConfigFactory.load()
  val host = flag("db.host", new InetSocketAddress("localhost", 3306), "Mysql server address")
  val user = conf.getString("mysql.user")
  val password = conf.getString("mysql.password")
  val db = conf.getString("mysql.db")

  implicit lazy val mysqlClient: Client with Transactions = Mysql.client
    .withCredentials(user, password)
    .withDatabase(db)
    .newRichClient("%s:%d".format(host().getHostName, host().getPort))

  def createTables()(implicit client: Client): Future[Result] = {
    client.query(DDL.createArticlesTable).onSuccess { r => println("Create Articles table") }
    client.query(DDL.createCategoriesTable).onSuccess { r => println("Create Categories table") }
  }

  def main() {
    val server = Http.server.serve(":8080", GoogleBlogApi().endpoints.toService)
    onExit {
      server.close()
    }
    Await.result(createTables())
    Await.ready(server)
  }

}