import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

scalaVersion in ThisBuild       := "2.12.9"
crossScalaVersions in ThisBuild := Seq("2.12.9", "2.11.12")

lazy val commonScalacOptions = Def.setting {
  Seq(
    "-deprecation",
    "-encoding",
    "UTF-8",
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

lazy val sharedSettings = Seq(
  scalafmtOnCompile := true,
  scalacOptions ++= commonScalacOptions.value,
  (scalacOptions in Test) ~= (_.filterNot(_ == "-Xfatal-warnings")),
  addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3"),
  libraryDependencies ++= Seq(
    "dev.zio"       %%% "zio"       % "1.0.0-RC11-1",
    "org.atnos"     %%% "eff"       % "5.5.0",
    "org.scalatest" %%% "scalatest" % "3.0.8" % "test"
  )
)

val publishingSettings = Seq(
  name                      := "eff-zio",
  organization              := "com.github.takayahilton",
  publishMavenStyle         := true,
  publishArtifact in Test   := false,
  pomIncludeRepository := { _ =>
    false
  },
  publishTo := Some(
    if (isSnapshot.value)
      Opts.resolver.sonatypeSnapshots
    else
      Opts.resolver.sonatypeStaging
  ),
  licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/takayahilton/eff-zio"),
      "scm:git@github.com:takayahilton/eff-zio.git"
    )
  ),
  developers := List(
    Developer(
      id = "takayahilton",
      name = "Takaya Tanaka",
      email = "takayahilton@gmail.com",
      url = url("https://github.com/takayahilton")
    )
  )
)

lazy val `eff-zio` = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .in(file("."))
  .settings(sharedSettings)
  .settings(publishingSettings)

import ReleaseTransformations._

releaseCrossBuild := true
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("+publishSigned"),
  setNextVersion,
  commitNextVersion,
  releaseStepCommand("sonatypeReleaseAll"),
  pushChanges
)

addCommandAlias("check", ";scalafmtCheckAll;scalafmtSbtCheck")
addCommandAlias("fmt", ";scalafmtAll;scalafmtSbt")
