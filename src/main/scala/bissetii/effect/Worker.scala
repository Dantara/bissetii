package bissetii.effect

import org.atnos.eff._

sealed trait Worker[Req, Resp, +A]

case class RunWorker[Req, Resp](handler: Req => Resp) extends Worker[Req, Resp, Unit]

object Worker {
  type _worker[Req, Resp, R] = Worker[Req, Resp, *] |= R

  def runWorker[Req, Resp, R :_worker[Req, Resp, *]](handler: Req => Resp): Eff[R, Unit] =
    Eff.send[Worker[Req, Resp, *], R, Unit](RunWorker(handler))
}
