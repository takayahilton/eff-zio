import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}
import ReleaseTransformations._

scalaVersion in ThisBuild       := "2.12.10"
crossScalaVersions in ThisBuild := Seq("2.11.12", scalaVersion.value, "2.13.1")
organization in ThisBuild       := "com.github.takayahilton"
onChangedBuildSource in Global  := IgnoreSourceChanges

lazy val root = project
  .in(file("."))
  .settings(moduleName := "root")
  .settings(publishingSettings)
  .settings(noPublishSettings)
  .aggregate(eff_zioJVM, eff_zioJS)
  .dependsOn(eff_zioJVM, eff_zioJS)

lazy val eff_zio = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .in(file("."))
  .settings(moduleName := "eff-zio")
  .settings(sharedSettings)
  .settings(publishingSettings)

lazy val eff_zioJVM = eff_zio.jvm
lazy val eff_zioJS = eff_zio.js

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
    "dev.zio"       %%% "zio"       % "1.0.0-RC13",
    "org.atnos"     %%% "eff"       % "5.5.2",
    "org.scalatest" %%% "scalatest" % "3.0.8" % "test"
  )
)

lazy val publishingSettings = Seq(
  publishMavenStyle       := true,
  publishArtifact in Test := false,
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
  homepage := Some(url("https://github.com/takayahilton/eff-zio")),
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
) ++ sharedReleaseProcess

lazy val sharedReleaseProcess = Seq(
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    releaseStepCommandAndRemaining("check"),
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
)

lazy val noPublishSettings = Seq(
  publish         := {},
  publishLocal    := {},
  publishArtifact := false
)

addCommandAlias("check", ";scalafmtCheckAll;scalafmtSbtCheck")
addCommandAlias("fmt", ";scalafmtAll;scalafmtSbt")
