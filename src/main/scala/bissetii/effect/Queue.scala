package bissetii.effect

import org.atnos.eff._

sealed trait Queue[V, +A]

case class Push[V](key: String, value: V) extends Queue[V, Unit]
case class Pull[V]() extends Queue[V, Option[V]]

object Queue {
  type _queue[V, R] = Queue[V, *] |= R

  def push[V, R :_queue[V, *]](key: String, value: V): Eff[R, Unit] =
    Eff.send[Queue[V, *], R, Unit](Push(key, value))

  def pull[V, R :_queue[V, *]](): Eff[R, Option[V]] =
    Eff.send[Queue[V, *], R, Option[V]](Pull())
}
