# eff-zio 
[![Build Status](https://travis-ci.org/takayahilton/eff-zio.png?branch=master)](https://travis-ci.org/takayahilton/eff-zio)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.takayahilton/eff-zio_2.12.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.takayahilton/eff-zio_2.12)

This library is [eff](https://github.com/atnos-org/eff) extension for [ZIO](https://github.com/zio/zio) effects.

# Installation

```sbt
// check maven badge above for latest version
libraryDependencies += "com.github.takayahilton" %% "eff-zio" % "1.0.0"

// to write types like Reader[String, *]
addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.13.0")

// to get types like Reader[String, *] (with more than one type parameter) correctly inferred for scala 2.12.x
scalacOptions += "-Ypartial-unification"
```

# Usage

```scala
import com.github.takayahilton.eff.zio._
import org.atnos.eff._
import org.atnos.eff.all._
import org.atnos.eff.syntax.all._
import zio.{Runtime, UIO}

val runtime = Runtime.default

type S1 = Fx.fx2[UIO, Option]

def action[R: _uio: _option]: Eff[R, Int] =
  for {
    a <- succeedLazy(10)
    b <- succeedLazy(20)
  } yield a + b

val zio = action[S1].runOption.runAsync

runtime.unsafeRun(zio) // Some(30)
```
