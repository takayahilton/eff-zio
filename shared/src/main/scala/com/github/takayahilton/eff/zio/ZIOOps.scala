package com.github.takayahilton.eff.zio

import org.atnos.eff._
import zio.ZIO

final class ZIOOps[R, ENV, E, A](private val e: Eff[R, A]) extends AnyVal {
  def runAsync(implicit m: Member.Aux[ZIO[ENV, E, ?], R, NoFx]): ZIO[ENV, E, A] =
    ZIOInterpretation.runAsync(e)

  def runSequential(implicit m: Member.Aux[ZIO[ENV, E, ?], R, NoFx]): ZIO[ENV, E, A] =
    ZIOInterpretation.runSequential(e)

  def either(implicit zio: ZIO[ENV, E, ?] /= R): Eff[R, Either[E, A]] =
    ZIOInterpretation.either(e)
}
