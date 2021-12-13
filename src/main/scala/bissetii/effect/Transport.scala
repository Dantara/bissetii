package bissetii.effect

import org.atnos.eff._

sealed trait Transport[Mode, +A]

case class Send[Mode, K, V](key: K, value: V) extends Transport[Mode, Unit]
case class Receive[Mode, K, V](key: K) extends Transport[Mode, V]

sealed trait Async
sealed trait Sync

object Transport {
  type AsyncTrans[T] = Transport[Async, T]
  type SyncTrans[T]  = Transport[Sync, T]

  type _asyncTrans[R] = AsyncTrans |= R
  type _syncTrans[R]  = SyncTrans  |= R

  def sendAsync[K, V, R :_asyncTrans](key: K, value: V): Eff[R, Unit] =
    Eff.send[AsyncTrans, R, Unit](Send(key, value))

  def receiveAsync[K, V, R :_asyncTrans](key: K): Eff[R, V] =
    Eff.send[AsyncTrans, R, V](Receive(key))

  def sendSync[K, V, R :_syncTrans](key: K, value: V): Eff[R, Unit] =
    Eff.send[SyncTrans, R, Unit](Send(key, value))

  def receiveSync[K, V, R :_syncTrans](key: K): Eff[R, V] =
    Eff.send[SyncTrans, R, V](Receive(key))
}
