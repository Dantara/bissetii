package bissetii.effect

import org.atnos.eff._

sealed trait Transport[Mode, +A]

case class Send[Mode, T](key: String, value: T) extends Transport[Mode, Unit]
case class Receive[Mode, T](key: String) extends Transport[Mode, T]

sealed trait Async
sealed trait Sync

object Transport {
  type AsyncTrans[T] = Transport[Async, T]
  type SyncTrans[T]  = Transport[Sync, T]

  type _asyncTrans[R] = AsyncTrans |= R
  type _syncTrans[R]  = SyncTrans  |= R

  def sendAsync[T, R :_asyncTrans](key: String, value: T): Eff[R, Unit] =
    Eff.send[AsyncTrans, R, Unit](Send(key, value))

  def receiveAsync[T, R :_asyncTrans](key: String): Eff[R, T] =
    Eff.send[AsyncTrans, R, T](Receive(key))

  def sendSync[T, R :_syncTrans](key: String, value: T): Eff[R, Unit] =
    Eff.send[SyncTrans, R, Unit](Send(key, value))

  def receiveSync[T, R :_syncTrans](key: String): Eff[R, T] =
    Eff.send[SyncTrans, R, T](Receive(key))
}
