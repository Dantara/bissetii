organization := "com.github.dantara"
name := "bissetii"
version := "0.1"

scalaVersion := "2.13.6"

lazy val scalaTestVersion = "3.2.0-M2"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest-freespec" % scalaTestVersion % "test",
  "org.scalatest" %% "scalatest-mustmatchers" % scalaTestVersion % "test",
  "org.scalacheck" %% "scalacheck" % "1.14.2" % "test"
)

testOptions in Test += Tests.Argument(
  TestFrameworks.ScalaCheck, "-maxSize", "5", "-minSuccessfulTests", "33",
  "-workers", s"${java.lang.Runtime.getRuntime.availableProcessors - 1}" ,
  "-verbosity", "1"
)