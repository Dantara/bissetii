package bissetii

import io.circe.Json
import cats.effect.IO
import cats.effect.IO._
import cats.effect.kernel.Ref
import cats.effect.kernel.Deferred
import cats.effect.std.Semaphore
import org.http4s._
import java.util.UUID

case class Job[Req](id: UUID, req: Req, callbackUri: Uri)

case class JobResponse(id: UUID, body: Json)

case class JobPool(pool: Ref[IO, Map[UUID, Deferred[IO, Json]]], sem: Semaphore[IO]) {
  def push(id: UUID, resp: Json): IO[Unit] = {
    def updatePool(poolMap: Map[UUID, Deferred[IO, Json]]):
        IO[(Map[UUID, Deferred[IO, Json]], Deferred[IO, Json])] = {
      poolMap.get(id) match {
        case None => for {
          d <- Deferred[IO, Json]
        } yield (poolMap + (id -> d), d)
        case Some(d) => pure((poolMap, d))
      }
    }
    for {
      _ <- sem.acquire
      poolM <- pool.get
      updatedP <- updatePool(poolM)
      _ <- pool.set(updatedP._1)
      _ <- sem.release
      _ <- updatedP._2.complete(resp)
    } yield ()
  }

  def pull(id: UUID): IO[Json] = {
    def updatePool(poolMap: Map[UUID, Deferred[IO, Json]]):
        IO[(Map[UUID, Deferred[IO, Json]], Deferred[IO, Json])] = {
      poolMap.get(id) match {
        case None => for {
          d <- Deferred[IO, Json]
        } yield (poolMap - id, d)
        case Some(d) => pure((poolMap, d))
      }
    }
    for {
      _ <- sem.acquire
      poolM <- pool.get
      updatedP <- updatePool(poolM)
      _ <- pool.set(updatedP._1)
      _ <- sem.release
      res <- updatedP._2.get
    } yield res
  }
}

object JobPool {
  def initJobPool: IO[JobPool] = for {
    ref <- Ref.of(Map.empty[UUID, Deferred[IO, Json]])
    sem <- Semaphore.in(1)
  } yield JobPool(ref, sem)
}
