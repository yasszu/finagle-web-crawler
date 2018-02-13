package app.googleblog

import app.model._
import com.twitter.finagle.mysql.Client
import io.finch._


/**
  * Created by Yasuhiro Suzuki on 2018/01/31.
  */
class GoogleBlogApi()(implicit mysql: Client) {

  implicit val client: GoogleBlogClient = GoogleBlogClient()

  val googleBlogService = GoogleBlogService()

  /**
    * GET feed/developers/googleblog
    */
  val getDevelopersBlog: Endpoint[Seq[Article]] = get("feed" :: "developers" :: "googleblog") {
    googleBlogService.getDevelopersBlogFromRemote map { articles =>
      articles.foreach(a => print(a.title + ","))
      Ok(articles)
    }
  } handle {
    case e: Exception => InternalServerError(e)
  }

  /**
    * GET feed/developers/googleblog/jp
    */
  val getDevelopersJapan: Endpoint[Seq[Article]] = get("feed" :: "developers" :: "googleblog" :: "jp") {
    googleBlogService.getDevelopersBlogJapanFormRemote map { articles =>
      articles.foreach(a => print(a.title + ","))
      Ok(articles)
    }
  } handle {
    case e: Exception => InternalServerError(e)
  }

  /**
    * GET api/developers/googleblog
    */
  val getDevelopersBlogFormDb: Endpoint[Seq[Article]] = get("api" :: "developers" :: "googleblog") {
    googleBlogService.getDevelopersBlogFromDB map { articles =>
      println(s"${GoogleDevelopersBlog.name}: ${articles.size} articles")
      Ok(articles)
    }
  } handle {
    case e: Exception => InternalServerError(e)
  }

  /**
    * GET api/developers/googleblog/jp
    */
  val getDevelopersJapanFormDb: Endpoint[Seq[Article]] = get("api" :: "developers" :: "googleblog" :: "jp") {
    googleBlogService.getDevelopersJapanFromDB map { articles =>
      println(s"${GoogleDevelopersJapan.name}: ${articles.size} articles")
      Ok(articles)
    }
  } handle {
    case e: Exception => InternalServerError(e)
  }

  /**
    * GET api/developers/android
    */
  val getAndroidDevelopersBlogFormDb: Endpoint[Seq[Article]] = get("api" :: "developers" :: "android") {
    googleBlogService.getAndroidDevelopersBlogFromDB() map { articles =>
      println(s"${AndroidDevelopersBlog.name}: ${articles.size} articles")
      Ok(articles)
    }
  } handle {
    case e: Exception => InternalServerError(e)
  }


  /**
    * GET scrape/developers/googleblog
    */
  val scrapeDevelopersBlog: Endpoint[String] = get("scrape" :: "developers" :: "googleblog") {
    googleBlogService.scrapeDevelopersBlog map { _ =>
      Ok("Success")
    }
  } handle {
    case e: Exception => InternalServerError(e)
  }

  /**
    * GET scrape/developers/googleblog/jp
    */
  val scrapeDevelopersJapan: Endpoint[String] = get("scrape" :: "developers" :: "googleblog" :: "jp") {
    googleBlogService.scrapeDevelopersJapan() map { _ =>
      Ok("success")
    }
  } handle {
    case e: Exception => InternalServerError(e)
  }

  /**
    * GET scrape/developers/android
    */
  val scrapeAndroidDevelopersBlog: Endpoint[String] = get("scrape" :: "developers" :: "android") {
    googleBlogService.scrapeAndroidDevelopersBlog() map { _ =>
      Ok("success")
    }
  } handle {
    case e: Exception => InternalServerError(e)
  }

  val endpoints = getDevelopersBlog :+: getDevelopersJapan :+: scrapeDevelopersBlog :+: scrapeDevelopersJapan :+:
    getDevelopersBlogFormDb :+: getDevelopersJapanFormDb :+: scrapeAndroidDevelopersBlog :+: getAndroidDevelopersBlogFormDb

}

object GoogleBlogApi {

  def apply()(implicit mysql: Client): GoogleBlogApi = new GoogleBlogApi()

}
