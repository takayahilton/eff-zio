import sbtcrossproject.CrossPlugin.autoImport.crossProject

name                            := "eff-zio"
organization                    := "com.github.takayahilton"
scalaVersion in ThisBuild       := "2.12.9"
crossScalaVersions in ThisBuild := Seq("2.12.9", "2.11.12")

lazy val zioVersion = "1.0.0-RC10-1"
lazy val effVersion = "5.5.0"

lazy val zioLib = "dev.zio"          %% "zio"       % zioVersion
lazy val effLib = "org.atnos"        %% "eff"       % effVersion
lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.8" % "test"

lazy val zio = crossProject(JSPlatform, JVMPlatform)
  .in(file("zio"))
  .settings(
    scalafmtOnCompile := true,
    scalacOptions ++= commonScalacOptions.value,
    (scalacOptions in Test) ~= (_.filterNot(_ == "-Xfatal-warnings")),
    addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3"),
    libraryDependencies ++= Seq(
      effLib,
      zioLib,
      scalaTest
    )
  )

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

addCommandAlias("check", ";scalafmtCheckAll;scalafmtSbtCheck")
addCommandAlias("fmt", ";scalafmtAll;scalafmtSbt")

publishMavenStyle       := true
publishArtifact in Test := false
pomIncludeRepository := { _ =>
  false
}

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/takayahilton/eff-zio"),
    "scm:git@github.com:takayahilton/eff-zio.git"
  )
)

developers := List(
  Developer(
    id = "takayahilton",
    name = "Takaya Tanaka",
    email = "takayahilton@gmail.com",
    url = url("https://github.com/takayahilton")
  )
)
