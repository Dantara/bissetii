organization := "com.github.dantara"
name := "bissetii"
version := "0.1"

scalaVersion := "2.13.3"

lazy val scalaTestVersion = "3.2.0-M2"
lazy val circeVersion = "0.14.1"

libraryDependencies ++= Seq(
  "org.atnos" %% "eff" % "5.22.0",
  "org.typelevel" %% "cats-core" % "2.3.0",
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "org.scalatest" %% "scalatest-freespec" % scalaTestVersion % "test",
  "org.scalatest" %% "scalatest-mustmatchers" % scalaTestVersion % "test",
  "org.scalacheck" %% "scalacheck" % "1.14.2" % """|test""".stripMargin
)

// addSbtPlugin("org.lyranthe.sbt" % "partial-unification" % "1.1.2")
addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.13.2" cross CrossVersion.full)

// scalacOptions += "-Ypartial-unification"

testOptions in Test += Tests.Argument(
  TestFrameworks.ScalaCheck, "-maxSize", "5", "-minSuccessfulTests", "33",
  "-workers", s"${java.lang.Runtime.getRuntime.availableProcessors - 1}" ,
  "-verbosity", "1"
)
