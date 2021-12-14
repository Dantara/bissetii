package bissetii.effect

import org.atnos.eff._
import org.http4s.Uri

sealed trait HttpClient[Req, Resp]

case class HttpSend[Req, Resp](path: Uri, value: Req) extends HttpClient[Req, Resp]

object HttpClient {
  type _httpClient[Req, R] = HttpClient[Req, *] |= R

  def send[Req, Resp, R :_httpClient[Req, *]](path: Uri, value: Req): Eff[R, Resp] =
    Eff.send[HttpClient[Req, *], R, Resp](HttpSend(path, value))
}
