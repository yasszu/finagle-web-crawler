package app.api

import app.api.GoogleBlogApi.Page
import app.googleblog.{GoogleBlogClient, GoogleBlogService}
import app.model._
import com.twitter.finagle.mysql.Client
import io.finch._


/**
  * Created by Yasuhiro Suzuki on 2018/01/31.
  */
class GoogleBlogApi()(implicit mysql: Client) {

  implicit val client: GoogleBlogClient = GoogleBlogClient()

  val googleBlogService = GoogleBlogService()

  val page: Endpoint[Page] = (param("page").as[Int] :: param("count").as[Int]).as[Page]

  /**
    * GET feed/googleblog/developers
    */
  val getDevelopersBlog: Endpoint[Seq[Article]] = get("feed" :: "googleblog" :: "developers") {
    googleBlogService.getDevelopersBlogFromRemote map { articles =>
      articles.foreach(a => print(a.title + ","))
      Ok(articles)
    }
  } handle {
    case e: Exception => InternalServerError(e)
  }

  /**
    * GET feed/googleblog/developers_jp
    */
  val getDevelopersJapan: Endpoint[Seq[Article]] = get("feed" :: "googleblog" :: "developers_jp") {
    googleBlogService.getDevelopersBlogJapanFormRemote map { articles =>
      articles.foreach(a => print(a.title + ","))
      Ok(articles)
    }
  } handle {
    case e: Exception => InternalServerError(e)
  }

  /**
    * GET api/googleblog/developers
    */
  val getDevelopersBlogFormDb: Endpoint[Seq[Article]] = get("api" :: "googleblog" :: "developers" :: page) { p: Page =>
    googleBlogService.getArticlesFromDB(GoogleDevelopersBlog, p.count, p.page) map { articles =>
      println(s"[INFO] [API] GET api/googleblog/developers: ${articles.size} articles")
      Ok(articles)
    }
  } handle {
    case e: Exception => InternalServerError(e)
  }

  /**
    * GET api/googleblog/developers_jp
    */
  val getDevelopersJapanFormDb: Endpoint[Seq[Article]] = get("api" :: "googleblog" :: "developers_jp" :: page) { p: Page =>
    googleBlogService.getArticlesFromDB(GoogleDevelopersJapan, p.count, p.page) map { articles =>
      println(s"[INFO] [API] GET api/googleblog/developers_jp: ${articles.size} articles")
      Ok(articles)
    }
  } handle {
    case e: Exception => InternalServerError(e)
  }

  /**
    * GET api/developers/android
    */
  val getAndroidDevelopersBlogFormDb: Endpoint[Seq[Article]] = get("api" :: "developers" :: "android" :: page) { p: Page =>
    googleBlogService.getArticlesFromDB(AndroidDevelopersBlog, p.count, p.page) map { articles =>
      println(s"[INFO] [API] GET api/developers/android: ${articles.size} articles")
      Ok(articles)
    }
  } handle {
    case e: Exception => InternalServerError(e)
  }

  /**
    * GET scrape/googleblog/developers
    */
  val scrapeDevelopersBlog: Endpoint[String] = get("scrape" :: "googleblog" :: "developers") {
    googleBlogService.scrapeDevelopersBlog map { _ =>
      Ok("Success")
    }
  } handle {
    case e: Exception => InternalServerError(e)
  }

  /**
    * GET scrape/googleblog/developers_jp
    */
  val scrapeDevelopersJapan: Endpoint[String] = get("scrape" :: "googleblog" :: "developers_jp") {
    googleBlogService.scrapeDevelopersJapan() map { _ =>
      Ok("success")
    }
  } handle {
    case e: Exception => InternalServerError(e)
  }

  /**
    * GET scrape/googleblog/android
    */
  val scrapeAndroidDevelopersBlog: Endpoint[String] = get("scrape" :: "googleblog" :: "android") {
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

  case class Page(page: Int, count: Int)

  def apply()(implicit mysql: Client): GoogleBlogApi = new GoogleBlogApi()

}
