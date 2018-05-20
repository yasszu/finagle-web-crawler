package app.actor

import akka.actor.{Actor, ActorLogging, Props}
import app.googleblog.{GoogleBlogClient, GoogleBlogService}
import com.twitter.finagle.mysql.Client

class GoogleBlogActor()(implicit mysql: Client) extends Actor with ActorLogging {

  import GoogleBlogActor._

  implicit val client: GoogleBlogClient = GoogleBlogClient()

  val googleBlogService = GoogleBlogService()

  override def receive = {
    case ScrapeDevelopersBlog => googleBlogService.scrapeDevelopersBlog
    case ScrapeDevelopersJapan => googleBlogService.scrapeDevelopersJapan()
    case ScrapeAndroidDevelopersBlog => googleBlogService.scrapeAndroidDevelopersBlog()
  }

}

object GoogleBlogActor {

  case object ScrapeDevelopersBlog

  case object ScrapeDevelopersJapan

  case object ScrapeAndroidDevelopersBlog

  def props()(implicit mysql: Client): Props = Props(new GoogleBlogActor())

}