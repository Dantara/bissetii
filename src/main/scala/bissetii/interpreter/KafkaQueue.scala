package bissetii.interpreter

import org.atnos.eff._, all._
import org.atnos.eff.addon.cats.effect.IOEffect._
import org.atnos.eff.interpret._
import bissetii.effect._
import io.circe._
import io.circe.syntax._
import cats.data._
import org.apache.kafka.clients.producer._
import org.apache.kafka.clients.consumer._
import cats.effect.IO
import scala.jdk.CollectionConverters._
import io.circe.parser.decode
import java.time.Duration

sealed trait KafkaQueueError extends Throwable

case object NoKafkaProducer extends KafkaQueueError
case object NoKafkaConsumer extends KafkaQueueError

case class TopicPrefix(prefix: String)

object KafkaQueueI {
  type _readerKafkaProducer[R] = Reader[Option[KafkaProducer[Null, String]], *] |= R
  type _readerKafkaConsumer[R] = Reader[Option[KafkaConsumer[Null, String]], *] |= R
  type _readerKafkaTopicPrefix[R] = Reader[TopicPrefix, *] |= R

  def runKafkaQueue[V: Encoder: Decoder, R, U, A](effects: Eff[R, A])
                   (implicit m: Member.Aux[Queue[V, *], R, U],
                    readerProducer: _readerKafkaProducer[U],
                    readerConsumer: _readerKafkaConsumer[U],
                    readerTopicPrefix: _readerKafkaTopicPrefix[U],
                    io: _Io[U]): Eff[U, A] = {
    translate(effects)(new Translate[Queue[V, *], U] {
      def apply[X](queue: Queue[V, X]): Eff[U, X] =
        queue match {
          case Push(key, value) => for {
            mProducer <- ask[U, Option[KafkaProducer[Null, String]]]
            topic <- ask[U, TopicPrefix]
            producer <- mProducer match {
              case Some(p) => pure[U, KafkaProducer[Null, String]](p)
              case None => fromIO(IO.raiseError(NoKafkaProducer))
            }
            record = new ProducerRecord[Null, String](topic.prefix + '.' + key, null, value.asInstanceOf[V].asJson.noSpaces)
            _ <- fromIO(IO { producer.send(record) })
          } yield ().asInstanceOf[X]

          case Pull() => for {
            mConsumer <- ask[U, Option[KafkaConsumer[Null, String]]]
            consumer <- mConsumer match {
              case Some(p) => pure[U, KafkaConsumer[Null, String]](p)
              case None => fromIO(IO.raiseError(NoKafkaConsumer))
            }
            mRecord <- fromIO(IO { consumer.poll(Duration.ofMillis(100)).asScala.headOption })
            res = mRecord.flatMap((r) => decode[V](r.value()).toOption)
          } yield res.asInstanceOf[X]
        }
    })
  }
}
