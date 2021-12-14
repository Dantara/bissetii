organization := "com.github.dantara"
name := "bissetii"
version := "0.1"

scalaVersion := "2.13.3"

val scalaTestVersion = "3.2.0-M2"
val circeVersion = "0.14.1"
val http4sVersion = "0.23.7"

// resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  "org.atnos" %% "eff" % "5.22.0",
  "org.atnos" %% "eff-cats-effect" % "5.17.0",
  "org.typelevel" %% "cats-effect" % "2.5.3",
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "io.circe" %% "circe-literal" % circeVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.apache.kafka" %% "kafka" % "3.0.0",
  "org.scalatest" %% "scalatest-freespec" % scalaTestVersion % "test",
  "org.scalatest" %% "scalatest-mustmatchers" % scalaTestVersion % "test",
  "org.scalacheck" %% "scalacheck" % "1.14.2" % """|test""".stripMargin
)

// addSbtPlugin("org.lyranthe.sbt" % "partial-unification" % "1.1.2")
addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.13.2" cross CrossVersion.full)
// addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

// scalacOptions += "-Ypartial-unification"

testOptions in Test += Tests.Argument(
  TestFrameworks.ScalaCheck, "-maxSize", "5", "-minSuccessfulTests", "33",
  "-workers", s"${java.lang.Runtime.getRuntime.availableProcessors - 1}" ,
  "-verbosity", "1"
)
