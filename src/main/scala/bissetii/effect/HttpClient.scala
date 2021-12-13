package bissetii.effect

import org.atnos.eff._

sealed trait HttpClient[+A]

case class HttpSend[T](path: String, value: T) extends HttpClient[Unit]

object HttpClient {
  type _httpClient[R] = HttpClient |= R

  def send[T, R :_httpClient](path: String, value: T): Eff[R, Unit] =
    Eff.send[HttpClient, R, Unit](HttpSend(path, value))
}
