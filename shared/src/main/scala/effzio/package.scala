import org.atnos.eff.Eff

package object effzio extends ZIOEffect {
  implicit final def toZIOOps[R, ENV, E, A](e: Eff[R, A]): ZIOOps[R, ENV, E, A] = new ZIOOps[R, ENV, E, A](e)
}
