package bissetii

import java.util.UUID
import io.circe.Json
import cats.effect.IO
import cats.effect.IO._
import cats.effect.kernel.Ref
import cats.effect.kernel.Deferred

case class Job[Req](id: UUID, req: Req)

case class JobPool(pool: Ref[IO, Map[UUID, Deferred[IO, Json]]]) {
  def push(id: UUID, resp: Json): IO[Unit] = {
    def updatePool(poolMap: Map[UUID, Deferred[IO, Json]]): IO[Map[UUID, Deferred[IO, Json]]] = {
      poolMap.get(id) match {
        case None => for {
          d <- Deferred[IO, Json]
          _ <- d.complete(resp)
        } yield poolMap + (id -> d)
        case Some(d) => for {
          _ <- d.complete(resp)
        } yield poolMap
      }
    }
    // FIXME: Need to add synchronisation
    for {
      poolM <- pool.get
      updated <- updatePool(poolM)
      _ <- pool.set(updated)
    } yield ()
  }

  def pull(id: UUID): IO[Json] = {
    def updatePool(poolMap: Map[UUID, Deferred[IO, Json]]): IO[(Map[UUID, Deferred[IO, Json]], Deferred[IO, Json])] = {
      poolMap.get(id) match {
        case None => for {
          d <- Deferred[IO, Json]
        } yield (poolMap + (id -> d), d)
        case Some(d) => pure((poolMap, d))
      }
    }
    // FIXME: Need to add synchronisation
    for {
      poolM <- pool.get
      poolP <- updatePool(poolM)
      res <- poolP._2.get
    } yield res
  }
}

object JobPool {
  def initJobPool: IO[JobPool] = Ref.of(Map.empty[UUID, Deferred[IO, Json]])
    .flatMap((r) => pure(JobPool(r)))
}
