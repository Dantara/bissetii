package bissetii.interpreter

import org.atnos.eff._, all._
import org.atnos.eff.addon.cats.effect.IOEffect._
import org.atnos.eff.interpret._
import bissetii.effect._
import io.circe._
import io.circe.syntax._
import cats.effect._
import org.http4s.dsl.io._
import org.http4s.client._
import org.http4s.circe._
import org.http4s.client.dsl.io._
import org.http4s.circe.CirceEntityCodec._
import cats.data._

object HttpJsonClientI {
  type _readerHttp4sClient[R] = Reader[Client[IO], *] |= R

  def runHttpClient[Req: Encoder, Resp: Decoder, R, U, A](effects: Eff[R, A])
                   (implicit m: Member.Aux[HttpClient[Req, *], R, U],
                    readerClient: _readerHttp4sClient[U],
                    io: _Io[U]): Eff[U, A] = {
    translate(effects)(new Translate[HttpClient[Req, *], U] {
      def apply[X](client: HttpClient[Req, X]): Eff[U, X] =
        client match {
          case HttpSend(path, value) => {
            val req = POST(value.asInstanceOf[Req].asJson, path)
            for {
              client <- ask[U, Client[IO]]
              resp <- fromIO(client.expect(req)(jsonOf[IO, Resp]))
            } yield resp.asInstanceOf[X]
          }
        }
    })
  }
}
