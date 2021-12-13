package bissetii.effect

import org.atnos.eff._

sealed trait Queue[+A]

case class Push[T](value: T) extends Queue[Unit]
case class Pull[T]() extends Queue[Option[T]]

object Queue {
  type _queue[R] = Queue |= R

  def push[T, R :_queue](value: T): Eff[R, Unit] =
    Eff.send[Queue, R, Unit](Push(value))

  def pull[T, R :_queue]: Eff[R, Option[T]] =
    Eff.send[Queue, R, Option[T]](Pull())
}
