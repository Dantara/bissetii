package bissetii

import cats.effect._
import cats.effect.IO._
import org.http4s._
import org.http4s.dsl.io._
import bissetii.JobPool
import scala.concurrent.ExecutionContext
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.implicits._
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec._

case class Receiver(listenerFib: FiberIO[Nothing])

object Receiver {
  def startReceiver(pool: JobPool, port: Int, host: String)(implicit ec: ExecutionContext): IO[Receiver] = {
    val receiverService = HttpRoutes.of[IO] {
      case req @ POST -> Root / "result" => for {
        jobResp <- req.as[JobResponse]
        _ <- pool.push(jobResp.id, jobResp.body)
        resp <- Ok()
      } yield resp
    }.orNotFound

    for {
    fib <- BlazeServerBuilder[IO]
      .withExecutionContext(ec)
      .bindHttp(port, host)
      .withHttpApp(receiverService)
      .resource
      .use(_ => IO.never).start
    } yield Receiver(fib)
  }

  def closeReceiver(rec: Receiver): IO[Unit] = rec.listenerFib.join >> pure(())

  def withReceiver[A](pool: JobPool, port: Int, host: String)(action: IO[A])(implicit ec: ExecutionContext): IO[Unit] = for {
    rec <- startReceiver(pool, port, host)
    _ <- action
    r <- closeReceiver(rec)
  } yield r
}
