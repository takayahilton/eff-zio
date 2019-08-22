import org.scalajs.jsenv.nodejs._
import sbtcrossproject.CrossPlugin.autoImport.crossProject

lazy val zioVersion = "1.0.0-RC10-1"
lazy val effVersion = "5.5.0"

lazy val zioLib = "dev.zio" %% "zio" % zioVersion
lazy val effLib = "org.atnos" %% "eff" % effVersion

lazy val zio = crossProject(JSPlatform, JVMPlatform).in(file("zio"))
  .settings(
    scalacOptions ++= commonScalacOptions.value,
    addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3"),
    libraryDependencies ++= Seq(
      effLib,
      zioLib
    )
)


lazy val commonScalacOptions = Def.setting {
  Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-language:_",
    "-unchecked",
    "-Xlint",
    "-Xlint:-nullary-unit",
    "-Ywarn-numeric-widen",
    "-Ywarn-value-discard"
  ) ++ {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, v)) if v >= 13 =>
        Seq(
          "-Ymacro-annotations"
        )
      case _ =>
        Seq(
          "-Xfatal-warnings",
          "-Yno-adapted-args",
          "-Ypartial-unification",
          "-Xfuture"
        )
    }
  }
}