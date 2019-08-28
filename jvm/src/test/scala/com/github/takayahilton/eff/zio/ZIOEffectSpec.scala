package com.github.takayahilton.eff.zio

import org.scalatest.{FunSuite, Matchers}
import org.atnos.eff._
import org.atnos.eff.all._
import org.atnos.eff.syntax.all._
import zio.{DefaultRuntime, Task, UIO}

class ZIOEffectSpec extends FunSuite with Matchers {

  object Runtime extends DefaultRuntime

  test("ZIO can work as normal values") {
    type S1 = Fx.fx2[UIO, Option]

    def action[R: _uio: _option]: Eff[R, Int] =
      for {
        a <- succeedLazy(10)
        b <- succeedLazy(20)
      } yield a + b

    val zio = action[S1].runOption.runSequential

    Runtime.unsafeRun(zio) shouldBe Some(30)
  }

  test("ZIO effects can be attemptedEither") {
    type S2 = Fx.fx2[Task, Option]

    def action[R: _task: _option]: Eff[R, Int] =
      for {
        a <- effect(10)
        b <- effect {
          boom; 20
        }
      } yield a + b

    val zio = action[S2].either.runOption.runSequential
    Runtime.unsafeRun(zio) shouldBe Some(Left(boomException))
  }

  /**
    * HELPERS
    */
  def boom: Unit = throw boomException
  val boomException: Throwable = new Exception("boom")
}
