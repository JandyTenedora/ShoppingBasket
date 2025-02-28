ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

enablePlugins(AssemblyPlugin)

assembly / mainClass := Some("com.adthena.shoppingbasket.Main")

lazy val root = (project in file("."))
  .settings(
    name := "ShoppingBasket",
    libraryDependencies ++= commonDependencies ++ akkaDependencies ++ testDependencies
  )

val commonDependencies = Seq(
  "com.typesafe" % "config" % "1.4.2",
  "ch.qos.logback" % "logback-classic" % "1.2.11",
)

val akkaDependencies = Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.6.19",
  "com.typesafe.akka" %% "akka-actor-typed" % "2.6.19",
  "com.typesafe.akka" %% "akka-stream" % "2.6.19",
  "com.typesafe.akka" %% "akka-protobuf-v3" % "2.6.19"
)

val testDependencies = Seq(
  "org.scalatestplus" %% "scalacheck-1-15" % "3.2.10.0" % Test,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % "2.6.19" % Test
)