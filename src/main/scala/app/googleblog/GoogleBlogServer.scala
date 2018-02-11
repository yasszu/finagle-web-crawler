package app.googleblog

import com.twitter.finagle.{Http, Service, http}
import com.twitter.server.TwitterServer
import com.twitter.util.{Await, Future}

/**
  * Created by Yasuhiro Suzuki on 2018/01/21.
  *
  * GoogleDevelopersBlog
  */
object GoogleBlogServer extends TwitterServer {

  implicit val client: GoogleBlogClient = GoogleBlogClient()

  val googleBlogService = GoogleBlogService()

  val service = new Service[http.Request, http.Response] {
    def apply(req: http.Request): Future[http.Response] = Future {
      googleBlogService.getDevelopersBlogFromRemote.onSuccess { html =>
        println(html)
      }
      http.Response(req.version, http.Status.Ok)
    }
  }

  val server = Http.serve(":8080", service)

  def main() {
    onExit {
      server.close()
    }
    Await.ready(server)
  }

}
