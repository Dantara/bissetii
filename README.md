Bissetii
------------

Bissetii is a library higly inspired by [Celery](https://docs.celeryproject.org/en/stable/getting-started/introduction.html) from python world.
Basically, it is a Task queue, mechanism to distribute work across threads or machines.

**Bissetii** communicates via messages using a broker (Kafka in current implementation) to mediate between clients and workers. To initiate a task the client adds a message to the queue, the broker then delivers that message to a worker. Worker on the other hand, performs task and returns result to client by http protocol.

Library is build on top of [Cats Effect](https://typelevel.org/cats-effect/) and [Eff](https://atnos-org.github.io/eff/). Eff is based on the “free-er” monad and an “open union” of effects described by Oleg Kiselyov in [Freer monads, more extensible effects](https://okmij.org/ftp/Haskell/extensible/more.pdf).

Library is divided in three main parts: effects, interpreters and internal abstractions. Effects and interpreters are separated, it means that client can implementat his (her) own interpreter for desired effect (for example substitute Kafka for any other broker or distributed queue implementation).

