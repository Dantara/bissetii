package bissetii.effect

import org.atnos.eff._
import io.circe._
import bissetii.Job

sealed trait Computation[+A]

case class Dispatch[Tag, Req](key: String, req: Req) extends Computation[Job[Req]]
case class GetResult[Req, Res](job: Job[Req]) extends Computation[Res]

object Computation {
  type _computation[R] = Computation |= R

  def dispatch[Req, R :_computation](key: String, req: Req): Eff[R, Job[Req]] =
    Eff.send[Computation, R, Job[Req]](Dispatch(key, req))

  def getResult[Req, Res, R :_computation](job: Job[Req]): Eff[R, Res] =
    Eff.send[Computation, R, Res](GetResult(job))
}
