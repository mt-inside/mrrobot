name := "mrrobot"
version := "1.0.0"
scalaVersion := "2.11.8"

scalacOptions := Seq( "-unchecked", "-deprecation", "-feature" )
mainClass in Compile := Some("mrrobot.Main")
fork in run := true

libraryDependencies ++= Seq(
  // Akka
  "com.typesafe.akka" %% "akka-actor" % "2.4.+",

  "com.typesafe.akka" %% "akka-slf4j" % "2.4.+", // akka logging handler that uses slf4j
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.+", // scala slf4j wrapper, replaces slf4s

  "ch.qos.logback" % "logback-classic" % "1.1.+", // slf4j implementation

  "org.asynchttpclient" % "async-http-client" % "2.+"
)
