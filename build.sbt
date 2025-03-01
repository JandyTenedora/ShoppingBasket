ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

enablePlugins(AssemblyPlugin)

assembly / mainClass := Some("com.adthena.shoppingbasket.Main")

// Common dependencies
lazy val commonDependencies = Seq(
  "com.typesafe" % "config" % "1.4.2",
  "ch.qos.logback" % "logback-classic" % "1.2.11"
)

// Akka dependencies
lazy val akkaDependencies = Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.6.19",
  "com.typesafe.akka" %% "akka-actor-typed" % "2.6.19",
  "com.typesafe.akka" %% "akka-stream" % "2.6.19",
  "com.typesafe.akka" %% "akka-protobuf-v3" % "2.6.19"
)

// Test dependencies
lazy val testDependencies = Seq(
  "org.scalatest" %% "scalatest" % "3.2.17" % Test,
  "org.scalatestplus" %% "scalacheck-1-15" % "3.2.10.0" % Test,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % "2.6.19" % Test
)

lazy val root = (project in file("."))
  .settings(
    name := "ShoppingBasket",
    libraryDependencies ++= commonDependencies ++ akkaDependencies ++ testDependencies
  )
