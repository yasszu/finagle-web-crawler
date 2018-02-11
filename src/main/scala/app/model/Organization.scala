package app.model

sealed abstract class Organization(val id: Int, val name: String)
case object GoogleDevelopersBlog extends Organization(1, "Google Developers Blog")
case object GoogleDevelopersJapan extends Organization(2, "Google Developers Japan")
case object AndroidDevelopersBlog extends Organization(3, "Android Developers Blog")