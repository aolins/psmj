name := """play-scala-mongo"""
version := "1.0-SNAPSHOT"
scalaVersion := "2.11.7"
lazy val root = (project in file(".")).enablePlugins(PlayScala)
resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
routesGenerator := InjectedRoutesGenerator
libraryDependencies += filters
libraryDependencies += "org.mongodb" %% "casbah" % "2.8.2"
libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-simple" % "1.6.4"
  ,ws
)

libraryDependencies += specs2 % Test