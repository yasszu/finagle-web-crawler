package app

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.util.Timeout
import app.actor.GoogleBlogActor
import app.api.GoogleBlogApi
import app.util.DDL
import com.twitter.app.Flag
import com.twitter.finagle.mysql._
import com.twitter.finagle.{Http, Mysql}
import com.twitter.server.TwitterServer
import com.twitter.util.{Await, Future}
import com.typesafe.config.{Config, ConfigFactory}
import io.circe.generic.auto._
import io.finch.circe._

import scala.concurrent.duration._
import scala.language.postfixOps

object Server extends TwitterServer {

  override def failfastOnFlagsNotParsed: Boolean = true

  val conf: Config = ConfigFactory.load()
  val host: Flag[InetSocketAddress] = flag("db.host", new InetSocketAddress("localhost", 3306), "Mysql server address")
  val user: String = conf.getString("mysql.user")
  val password: String = conf.getString("mysql.password")
  val db: String = conf.getString("mysql.db")

  implicit lazy val mysqlClient: Client with Transactions = Mysql.client
    .withCredentials(user, password)
    .withDatabase(db)
    .newRichClient("%s:%d".format(host().getHostName, host().getPort))

  implicit val timeout: Timeout = Timeout(180 seconds)

  lazy val system = ActorSystem("mySystem")

  lazy val googleBlogActor = system.actorOf(GoogleBlogActor.props(), "googleBlogActor")

  lazy val server = Http.server.serve(":8080", GoogleBlogApi().endpoints.toService)

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

  def startActors = Future {
    import scala.concurrent.ExecutionContext.Implicits.global
    import GoogleBlogActor._
    system.scheduler.schedule(1 seconds, 6 hours) {
      googleBlogActor ! ScrapeDevelopersBlog
      googleBlogActor ! ScrapeDevelopersJapan
      googleBlogActor ! ScrapeAndroidDevelopersBlog
    }
  }

  def readyApi = Future {
    onExit {
      server.close()
    }
    Await.ready(server)
  }

  def main() {
    for {
      _ <- createSchema()
      _ <- createArticlesTables()
      _ <- createCategoriesTables()
    }
    startActors
    readyApi
  }

}