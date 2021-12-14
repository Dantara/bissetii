package bissetii.interpreter

import org.atnos.eff._, all._
import org.atnos.eff.interpret._
import bissetii.effect._
import bissetii.effect.Queue._
import java.util.UUID
import bissetii.Job
import bissetii.JobPool
import org.http4s._
import cats.data._
import cats.effect.IO
import io.circe._
import org.atnos.eff.addon.cats.effect.IOEffect._

object DispatcherI {
  type _readerHost[R] = Reader[Uri, *] |= R
  type _readerPool[R] = Reader[JobPool, *] |= R

  def runDispatcher[Req: Encoder, Resp: Decoder, R, U, A](effects: Eff[R, A])
                   (implicit m: Member.Aux[Dispatcher, R, U],
                    readerHost: _readerHost[U],
                    readerPool: _readerPool[U],
                    queue: _queue[Job[Req], U],
                    io: _Io[U]): Eff[U, A] = {
    translate(effects)(new Translate[Dispatcher, U] {
      def apply[X](dispatcher: Dispatcher[X]): Eff[U, X] =
        dispatcher match {
          case Dispatch(key, req) => for {
            uuid <- fromIO(IO { UUID.randomUUID })
            url <- ask[U, Uri]
            job = Job[Req](uuid, req.asInstanceOf[Req], url)
            _ <- Queue.push(key, job)
          } yield job.asInstanceOf[X]

          case GetResult(job) => for {
            pool <- ask[U, JobPool]
            respJ <- fromIO(pool.pull(job.asInstanceOf[Job[Req]].id))
            resp <- respJ.as[Resp] match {
              case Left(e) => fromIO(IO.raiseError(e))
              case Right(r) => pure[U, Resp](r)
            }
          } yield resp.asInstanceOf[X]
        }
    })
  }
}
