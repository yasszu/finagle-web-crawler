package app

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.util.Timeout
import app.actor.GoogleBlogActor
import app.api.GoogleBlogApi
import app.service.MysqlClient
import com.twitter.app.Flag
import com.twitter.finagle.Http
import com.twitter.server.TwitterServer
import com.twitter.util.{Await, Future}
import com.typesafe.config.{Config, ConfigFactory}
import io.circe.generic.auto._
import io.finch.circe._

import scala.concurrent.duration._
import scala.language.postfixOps

object Server extends TwitterServer with MysqlClient {

  override def failfastOnFlagsNotParsed: Boolean = true

  val conf: Config = ConfigFactory.load()
  val host: Flag[InetSocketAddress] = flag("db.host", new InetSocketAddress("db", 3306), "Mysql server address")
  val user: String = conf.getString("mysql.user")
  val password: String = conf.getString("mysql.password")
  val db: String = conf.getString("mysql.db")

  implicit val timeout: Timeout = Timeout(180 seconds)

  lazy val system = ActorSystem("mySystem")

  lazy val googleBlogActor = system.actorOf(GoogleBlogActor.props(), "googleBlogActor")

  lazy val server = Http.server.serve(":8080", GoogleBlogApi().endpoints.toService)

  def startActors = Future {
    import GoogleBlogActor._

    import scala.concurrent.ExecutionContext.Implicits.global
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
    println("Listening for HTTP on /0.0.0.0:8080")
    for {
      _ <- createSchema()
      _ <- createArticlesTables()
      _ <- createCategoriesTables()
      _ <- startActors
    } yield ()
    readyApi
  }

}