ThisBuild / scalaVersion     := "2.13.1"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "doobie-cancellation-test",
    libraryDependencies += "org.tpolecat" %% "doobie-core" % "0.8.8",
    libraryDependencies += "org.tpolecat" %% "doobie-postgres" % "0.8.8"
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
