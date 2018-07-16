package app.googleblog

import app.googleblog.GoogleBlogClient.{GetGoogleDevelopersBlog, GetGoogleDevelopersJapan}
import app.model._
import com.twitter.finagle.http.Response
import com.twitter.finagle.mysql.Client
import com.twitter.util.Future

/**
  * Created by Yasuhiro Suzuki on 2018/07/16.
  */
class GoogleBlogRepository(client: GoogleBlogClient) {

  def getDevelopersBlogFromRemote: Future[Seq[Article]] = {
    val response = client.request(GetGoogleDevelopersBlog)
    getFromRemote(response, GoogleDevelopersBlog)
  }

  def getDevelopersBlogJapanFormRemote: Future[Seq[Article]] = {
    val response = client.request(GetGoogleDevelopersJapan)
    getFromRemote(response, GoogleDevelopersJapan)
  }

  private def getFromRemote(response: Future[Response], org: Organization): Future[Seq[Article]] = {
    response map { rep =>
      parseArticles(rep.getContentString(), org.id) map { case (article, _) => article }
    }
  }

  def getArticlesFromDB(org: Organization, limit: Int = 100, offset: Int = 0)(implicit mysql: Client): Future[Seq[Article]] = {
    Article.findAll(org.id, limit, offset)
  }

}

object GoogleBlogRepository {

  def apply()(implicit client: GoogleBlogClient): GoogleBlogRepository = new GoogleBlogRepository(client)

}
