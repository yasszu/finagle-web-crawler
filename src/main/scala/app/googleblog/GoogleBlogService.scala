package app.googleblog

import app.model._
import com.twitter.finagle.http.Response
import com.twitter.finagle.mysql.Client
import com.twitter.util.{Await, Future}


/**
  * Created by Yasuhiro Suzuki on 2018/01/21.
  *
  * GoogleDevelopersBlog
  */
class GoogleBlogService(client: GoogleBlogClient) {

  def getDevelopersBlogFromRemote: Future[Seq[Article]] = {
    val response = client.getGoogleDevelopersBlog
    getFromRemote(response, GoogleDevelopersBlog)
  }

  def getDevelopersBlogJapanFormRemote: Future[Seq[Article]] = {
    val response = client.getGoogleDevelopersJapan
    getFromRemote(response, GoogleDevelopersJapan)
  }

  def getDevelopersBlogFromDB()(implicit mysql: Client): Future[Seq[Article]] = {
    Article.findAll(GoogleDevelopersBlog.id)
  }

  def getDevelopersJapanFromDB()(implicit mysql: Client): Future[Seq[Article]] = {
    Article.findAll(GoogleDevelopersJapan.id)
  }

  def getAndroidDevelopersBlogFromDB()(implicit mysql: Client): Future[Seq[Article]] = {
    Article.findAll(AndroidDevelopersBlog.id)
  }

  def scrapeDevelopersBlog()(implicit mysql: Client): Future[Unit] = {
    val response = client.getGoogleDevelopersBlog
    scrape(response, GoogleDevelopersBlog)
  }

  def scrapeDevelopersJapan()(implicit mysql: Client): Future[Unit] = {
    val response = client.getGoogleDevelopersJapan
    scrape(response, GoogleDevelopersJapan)
  }

  def scrapeAndroidDevelopersBlog()(implicit mysql: Client): Future[Unit] = {
    val response = client.getAndroidDevelopersBlog
    scrape(response, AndroidDevelopersBlog)
  }

  def getFromRemote(response: Future[Response], org: Organization): Future[Seq[Article]] = {
    response map { rep =>
      val source = rep.getContentString()
      parseArticles(source, org.id) map {
        case (article, categories) => article
      }
    }
  }

  def scrape(response: Future[Response], org: Organization)(implicit mysql: Client): Future[Unit] = {
    response map { rep =>
      val source = rep.getContentString()
      val articles = parseArticles(source, org.id)
      println(s"Fetch ${articles.size} articles")
      articles.reverseMap {
        case (article, categories) =>
          saveArticle(org, article, categories)
      }
    }
  }

  def saveArticle(org: Organization, article: Article, categories: Seq[String])(implicit mysql: Client): Unit = {
    getLatestArticle(org) match {
      case None =>
        createArticle(article) map { id =>
          createCategories(id, categories)
        }
      case Some(latest) =>
        if (compare(article, latest)) {
          createArticle(article) map { id =>
            createCategories(id, categories)
          }
        }
    }
  }

  def compare(target: Article, latest: Article): Boolean = {
    val latestDate = parseDateFormat(latest.published)
    val targetDate = parseDateFormat(target.published)
    targetDate > latestDate
  }

  def createArticle(article: Article)(implicit mysql: Client): Future[Long] = {
    Article.insert(article).onSuccess { id =>
      println(s"Add $id: ${article.title}")
    }
  }

  def createCategories(id: Long, terms: Seq[String])(implicit mysql: Client): Unit = {
    terms.map { term =>
      Category.insert(Category(None, id, term))
    }
  }

  def getLatestArticle(organization: Organization)(implicit mysql: Client): Option[Article] = {
    Await.result(Article.findAll(organization.id, 1, 0)).headOption
  }

}

object GoogleBlogService {

  def apply()(implicit client: GoogleBlogClient): GoogleBlogService = new GoogleBlogService(client)

}