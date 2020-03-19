package example

import cats.effect.{Blocker, ExitCode, IO, IOApp}
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts
import doobie.util.query.Query0
import doobie.util.transactor.Transactor

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {

    val xa: Transactor[IO] = Transactor.fromDriverManager[IO](
      "org.postgresql.Driver",     // driver classname
      "jdbc:postgresql:postgres",  // connect URL (driver-specific)
      "postgres",                  // user
      "postgres",                  // password
      Blocker.liftExecutionContext(ExecutionContexts.synchronous)
    )

    val query: ConnectionIO[Unit] = sleep100Seconds.unique

    query.transact(xa).as(ExitCode.Success)
  }

  private val sleep100Seconds: Query0[Unit] =
    sql"""SELECT pg_sleep(100)""".query[Unit]

}
