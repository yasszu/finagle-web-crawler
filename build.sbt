// Common settings
lazy val commonSettings = Seq(
  organization := "yasszu",
  version := "1.0-SNAPSHOT",
  scalaVersion := "2.12.4"
)

// SBT assembly
lazy val assemblySettings = Seq(
  mainClass in assembly := Some("app.Server"),

  // Excluding specific files
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs@_*) => MergeStrategy.discard
    case x => MergeStrategy.first
  }
)

// Docker settings
lazy val dockerSettings = Seq(
  maintainer in Docker := "yasszu",
  dockerBaseImage in Docker := "openjdk:8u131-jdk-alpine",
  dockerExposedPorts in Docker := Seq(8080, 8080)
)

// Library
lazy val dependencies = Seq(
  "org.scala-lang.modules" %% "scala-xml" % "1.0.6",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
  "org.scala-lang.modules" %% "scala-swing" % "2.0.0-M2",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.twitter" %% "finagle-mysql" % "17.11.0",
  "com.twitter" %% "twitter-server" % "17.11.0",
  "com.github.finagle" %% "finch-core" % "0.16.0-M5",
  "com.github.finagle" %% "finch-circe" % "0.16.0-M5",
  "io.circe" %% "circe-generic" % "0.9.0-M2",
  "io.circe" %% "circe-core" % "0.9.0-M2",
  "io.circe" %% "circe-parser" % "0.9.0-M2",
  "com.typesafe" % "config" % "1.3.1",
  "com.typesafe.akka" %% "akka-actor" % "2.5.10",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.10" % Test,
  "joda-time" % "joda-time" % "2.10"
)

lazy val root = (project in file("."))
  .enablePlugins(JavaServerAppPackaging, DockerPlugin)
  .settings(
    commonSettings,
    name := "finagle-web-crawler",
    libraryDependencies ++= dependencies,
    assemblySettings,
    dockerSettings
  )