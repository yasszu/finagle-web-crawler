package app

import akka.actor.ActorSystem
import akka.util.Timeout
import app.actor.GoogleBlogActor
import app.api.GoogleBlogApi
import app.service.MysqlClient
import com.twitter.finagle.Http
import com.twitter.server.TwitterServer
import com.twitter.util.{Await, Future}
import app.util._
import com.twitter.app.Flag
import com.typesafe.config.{Config, ConfigFactory}
import io.circe.generic.auto._
import io.finch.circe._

import scala.concurrent.duration._
import scala.language.postfixOps

object Server extends TwitterServer with MysqlClient {

  override def failfastOnFlagsNotParsed: Boolean = true

  val conf: Config = ConfigFactory.load()
  val dockerHost: String = conf.getString("mysql.host")
  val dbHost: Flag[String] = flag("db.host", dockerHost, "Mysql server address")

  override def host: String = dbHost()
  override def db: String = conf.getString("mysql.db")
  override def port: Int = conf.getInt("mysql.port")
  override def user: String = conf.getString("mysql.user")
  override def password: String = conf.getString("mysql.password")

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
    log.info(s"db.host: ${dbHost()}")
    log.info("Listening for HTTP on /0.0.0.0:8080")
    for {
      _ <- createArticlesTables()
      _ <- createCategoriesTables()
      _ <- startActors
    } yield ()
    readyApi
  }

}