package example

import cats.effect.{Blocker, ExitCode, IO, IOApp}
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts
import doobie.util.query.Query0
import doobie.util.transactor.Transactor
import scala.concurrent.duration._

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {

    val xa: Transactor[IO] = Transactor.fromDriverManager[IO](
      "org.postgresql.Driver",     // driver classname
      "jdbc:postgresql:postgres",  // connect URL (driver-specific)
      "postgres",                  // user
      "postgres",                  // password
      Blocker.liftExecutionContext(ExecutionContexts.synchronous)
    )

    val query: ConnectionIO[Unit] = sleep10Seconds.unique

    val postgresSleep100Secons: IO[Unit] =
      query
        .transact(xa)

    val race: IO[Unit] =
      IO.race(sleep1Second, postgresSleep100Secons)
      .flatMap {
        case e: Either[Unit, Unit] => IO(println(">>> winner:" + e.toString))
      }

    for {
      _ <- IO(println(java.time.Instant.now))
      _ <- race
      _ <- IO(println(java.time.Instant.now))
    } yield ExitCode.Success


  }

  private val sleep1Second: IO[Unit] =
    IO.sleep(1.second)

  private val sleep10Seconds: Query0[Unit] =
    sql"""SELECT pg_sleep(10)""".query[Unit]

}
