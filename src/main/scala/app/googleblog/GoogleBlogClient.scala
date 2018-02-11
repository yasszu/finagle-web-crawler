package app.googleblog

import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Http, Service, http}
import com.twitter.util.Future

/**
  * Created by Yasuhiro Suzuki on 2018/01/21.
  *
  * GoogleDevelopersBlog
  */

class GoogleBlogClient extends Service[Request, Response] {

  val hostname = "feeds.feedburner.com"

  val dest = s"$hostname:443"

  val service: Service[Request, Response] = Http.client
    .withTls(hostname)
    .newService(dest)

  override def apply(req: Request): Future[Response] = {
    req.host = dest
    println(req.uri)
    service(req)
  }

  def getGoogleDevelopersBlog: Future[Response] = {
    val method = http.Method.Get
    val path = "/GDBcode"
    apply(http.Request(method, path))
  }

  def getGoogleDevelopersJapan: Future[Response] = {
    val method = http.Method.Get
    val path = "/GoogleJapanDeveloperRelationsBlog"
    apply(http.Request(method, path))
  }

  def getAndroidDevelopersBlog: Future[Response] = {
    val method = http.Method.Get
    val path = "/blogspot/hsDu"
    apply(http.Request(method, path))
  }

  def getGoogleResearchBlog: Future[Response] = {
    val method = http.Method.Get
    val path = "/blogspot/gJZg"
    apply(http.Request(method, path))
  }

}

object GoogleBlogClient {
  def apply(): GoogleBlogClient = new GoogleBlogClient()
}
