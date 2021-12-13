package bissetii.effect

import org.atnos.eff._
import io.circe._
import java.util.UUID

case class Job[Tag](id: UUID, req: Json)

sealed trait Computation[+A]

case class Dispatch[Tag, Req](req: Req) extends Computation[Job[Tag]]
case class GetResult[Tag, Res](job: Job[Tag]) extends Computation[Res]

object Computation {
  type _computation[R] = Computation |= R

  def dispatch[Tag, Req, R :_computation](req: Req): Eff[R, Job[Tag]] =
    Eff.send[Computation, R, Job[Tag]](Dispatch(req))

  def getResult[Tag, Res, R :_computation](job: Job[Tag]): Eff[R, Res] =
    Eff.send[Computation, R, Res](GetResult(job))
}
