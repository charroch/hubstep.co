import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

  val appName = "HubStep"
  val appVersion = "0.0.1-SNAPSHOT"

  val appDependencies = Seq(
    "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
    "org.mockito" % "mockito-all" % "1.9.0" % "test"
  )

  val secureSocial = PlayProject(
    appName + "-securesocial", appVersion, mainLang = SCALA, path = file("modules/securesocial")
  )

  val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
    // Add your own project settings here
  ).dependsOn(secureSocial)

}
