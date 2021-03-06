package example

import cats.effect.{Blocker, ExitCode, IO, IOApp}
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts
import doobie.util.query.Query0
import doobie.util.transactor.Transactor

import scala.concurrent.duration._

object Main2 extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {

    val xa: Transactor[IO] = Transactor.fromDriverManager[IO](
      "org.postgresql.Driver",     // driver classname
      "jdbc:postgresql:postgres",  // connect URL (driver-specific)
      "postgres",                  // user
      "postgres",                  // password
      Blocker.liftExecutionContext(ExecutionContexts.synchronous)
    )

    val combined: ConnectionIO[Unit] =
      setTimeout.run >> sleep10SecondsWith5SecStatementTimeout.unique

    for {
      _ <- runWithTimings(
        combined
          .transact(xa)
          .attempt
          .void
      )
    } yield ExitCode.Success
  }

  private def runWithTimings[A](action: IO[A]): IO[A] =
    for {
      _ <- IO(println("start:" + java.time.Instant.now))
      a <- action
      _ <- IO(println("end  :" + java.time.Instant.now))
    } yield a

  private val setTimeout: Update0 =
    sql"""set statement_timeout = 5000""".update

  // See https://dba.stackexchange.com/a/164450/11153
  private val sleep10SecondsWith5SecStatementTimeout: Query0[Unit] =
    sql"""select pg_sleep(10);""".query[Unit]
}
