name := "web-clawler"

version := "1.0"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % "1.0.6",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
  "org.scala-lang.modules" %% "scala-swing" % "2.0.0-M2",
  "com.typesafe" % "config" % "1.3.1",
  "com.twitter" %% "finagle-http" % "17.11.0",
  "com.twitter" %% "finagle-mysql" % "17.11.0",
  "com.twitter" %% "twitter-server" % "17.11.0",
  "com.github.finagle" %% "finch-core" % "0.16.0-M5",
  "com.github.finagle" %% "finch-circe" % "0.16.0-M5",
  "io.circe" %% "circe-generic" % "0.9.0-M2",
  "io.circe" %% "circe-core" % "0.9.0-M2",
  "io.circe" %% "circe-parser" % "0.9.0-M2"
)