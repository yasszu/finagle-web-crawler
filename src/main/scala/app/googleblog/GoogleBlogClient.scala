package app.googleblog

import app.util._
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Http, Service, http}
import com.twitter.util.Future
import GoogleBlogClient._

/**
  * Created by Yasuhiro Suzuki on 2018/01/21.
  *
  * GoogleDevelopersBlog
  */

class GoogleBlogClient extends Service[Request, Response] {

  val dest = s"$HostName:443"

  val service: Service[Request, Response] = Http.client
    .withTls(HostName)
    .newService(dest)

  override def apply(req: Request): Future[Response] = {
    req.host = dest
    log.info(s"[GoogleBlogClient] Request: ${req.uri}")
    service(req)
  }

  def request(path: Path): Future[Response] = {
    val method = http.Method.Get
    apply(http.Request(method, path.value))
  }

}

object GoogleBlogClient {

  val HostName = "feeds.feedburner.com"

  sealed abstract class Path(val value: String)
  case object GetGoogleDevelopersBlog extends Path("/GDBcode")
  case object GetGoogleDevelopersJapan extends Path("/GoogleJapanDeveloperRelationsBlog")
  case object GetAndroidDevelopersBlog extends Path("/blogspot/hsDu")
  case object GetGoogleSearchBlog extends Path("/blogspot/gJZg")

  def apply(): GoogleBlogClient = new GoogleBlogClient()

}
