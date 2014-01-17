name := "forecast-io-api"

organization := "com.film42"

version := "0.0.1"

scalaVersion := "2.10.3"

resolvers += "spray" at "http://repo.spray.io/"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.10" % "2.0" % "test" withSources() withJavadoc(),
  "org.scalacheck" %% "scalacheck" % "1.10.0" % "test" withSources() withJavadoc(),
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.0",
  "io.spray" %%  "spray-json" % "1.2.5",
  "com.eclipsesource.minimal-json" % "minimal-json" % "0.9.1"
)

initialCommands := "import com.film42.forecastioapi._"

