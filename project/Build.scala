import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

  val appName = "HubStep"
  val appVersion = "0.0.1-SNAPSHOT"

  override def settings = super.settings ++ org.sbtidea.SbtIdeaPlugin.settings

  val appDependencies = Seq(
    "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
    "com.github.twitter" % "bootstrap" % "2.0.2",
    "org.mockito" % "mockito-all" % "1.9.0" % "test",
    "org.hamcrest" % "hamcrest-all" % "1.1" % "test",
    "org.pegdown" % "pegdown" % "1.0.2" % "test",
    "com.typesafe" %% "play-plugins-mailer" % "2.0.4",
    "com.typesafe.akka" % "akka-testkit" % "2.0.2" % "test"
  )

  val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
    resolvers ++= Seq(
      "webjars" at "http://webjars.github.com/m2"
    )
  )
}
