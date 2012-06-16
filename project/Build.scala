import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

  val appName = "HubStep"
  val appVersion = "0.0.1-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
  )

  val secureSocial = PlayProject(
    appName + "-securesocial", appVersion, mainLang = SCALA, path = file("modules/securesocial")
  )

  val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
    // Add your own project settings here
  ).dependsOn(secureSocial).aggregate(secureSocial)

}
