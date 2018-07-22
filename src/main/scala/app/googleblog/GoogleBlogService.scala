package app.googleblog

import app.googleblog.GoogleBlogClient.{GetAndroidDevelopersBlog, GetGoogleDevelopersBlog, GetGoogleDevelopersJapan}
import app.model._
import app.util._
import com.twitter.finagle.http.Response
import com.twitter.finagle.mysql.Client
import com.twitter.util.{Await, Future}


/**
  * Created by Yasuhiro Suzuki on 2018/01/21.
  *
  * GoogleDevelopersBlog
  */
class GoogleBlogService(client: GoogleBlogClient) {

  def scrapeDevelopersBlog()(implicit mysql: Client): Future[Unit] = {
    log.info("Scrape developers blog")
    val response = client.request(GetGoogleDevelopersBlog)
    scrape(response, GoogleDevelopersBlog).handle {
      case e: Exception => e.printStackTrace()
    }
  }

  def scrapeDevelopersJapan()(implicit mysql: Client): Future[Unit] = {
    log.info("Scrape developers japan")
    val response = client.request(GetGoogleDevelopersJapan)
    scrape(response, GoogleDevelopersJapan).handle {
      case e: Exception => e.printStackTrace()
    }
  }

  def scrapeAndroidDevelopersBlog()(implicit mysql: Client): Future[Unit] = {
    log.info("Scrape android developers blog")
    val response = client.request(GetAndroidDevelopersBlog)
    scrape(response, AndroidDevelopersBlog).handle {
      case e: Exception => e.printStackTrace()
    }
  }

  private def scrape(response: Future[Response], org: Organization)(implicit mysql: Client): Future[Unit] = {
    response map { res =>
      val articles = parseArticles(res.getContentString(), org.id)
      val latestArticles = articles.filterLatest(getLatestArticle(org))
      latestArticles.reverseMap { case (article, categories) => saveArticle(article, categories) }
      log.info(s"[${org.name}] Fetch ${articles.size} articles")
      log.info(s"[${org.name}] Save  ${latestArticles.size} articles")
    }
  }

  private def saveArticle(article: Article, categories: Seq[String])(implicit mysql: Client): Unit = {
    createArticle(article) map { id => createCategories(id, categories) }
  }

  private def createArticle(article: Article)(implicit mysql: Client): Future[Long] = {
    Article.insert(article).onSuccess { id =>
      log.info(s"[GoogleBlogClient] Add $id: ${article.title}")
    }
  }

  private def createCategories(id: Long, terms: Seq[String])(implicit mysql: Client): Unit = {
    terms map { term => Category.insert(Category(None, id, term)) }
  }

  private def getLatestArticle(organization: Organization)(implicit mysql: Client): Option[Article] = {
    Await.result(Article.findAll(organization.id, 1)).headOption
  }

}

object GoogleBlogService {

  def apply()(implicit client: GoogleBlogClient): GoogleBlogService = new GoogleBlogService(client)

}