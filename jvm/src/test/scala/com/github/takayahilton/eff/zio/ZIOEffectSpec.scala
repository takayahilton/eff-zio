package com.github.takayahilton.eff.zio

import org.scalatest.funsuite.AnyFunSuite
import org.atnos.eff._
import org.atnos.eff.all._
import org.atnos.eff.syntax.all._
import zio.{Runtime, Task, UIO}

class ZIOEffectSpec extends AnyFunSuite {

  val runtime = Runtime.default

  test("ZIO can work as normal values") {
    type S1 = Fx.fx2[UIO, Option]

    def action[R: _uio: _option]: Eff[R, Int] =
      for {
        a <- effectTotal(10)
        b <- effectTotal(20)
      } yield a + b

    val zio = action[S1].runOption.runSequential
    assert(runtime.unsafeRun(zio) == Some(30))
  }

  test("ZIO effects can be attemptedEither") {
    type S2 = Fx.fx2[Task, Option]

    def action[R: _task: _option]: Eff[R, Int] =
      for {
        a <- effect(10)
        b <- effect {
          boom(); 20
        }
      } yield a + b

    val zio = action[S2].either.runOption.runSequential
    assert(runtime.unsafeRun(zio) == Some(Left(boomException)))
  }

  /** HELPERS
    */
  def boom(): Unit = throw boomException
  val boomException: Throwable = new Exception("boom")
}
