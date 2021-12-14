package bissetii.effect

import org.atnos.eff._
import bissetii.Job

sealed trait Dispatcher[+A]

case class Dispatch[Req](key: String, req: Req) extends Dispatcher[Job[Req]]
case class GetResult[Req, Res](job: Job[Req]) extends Dispatcher[Res]

object Dispatcher {
  type _dispatcher[R] = Dispatcher |= R

  def dispatch[Req, R :_dispatcher](key: String, req: Req): Eff[R, Job[Req]] =
    Eff.send[Dispatcher, R, Job[Req]](Dispatch(key, req))

  def getResult[Req, Res, R :_dispatcher](job: Job[Req]): Eff[R, Res] =
    Eff.send[Dispatcher, R, Res](GetResult(job))
}
