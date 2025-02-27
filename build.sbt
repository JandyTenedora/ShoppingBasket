ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.16"

enablePlugins(AssemblyPlugin)

assembly / mainClass := Some("com.adthena.shoppingbasket.Main")

lazy val root = (project in file("."))
  .settings(
    name := "ShoppingBasket",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.17" % Test,
      "org.scalatestplus" %% "scalacheck-1-15" % "3.2.10.0" % "test",
      "com.typesafe" % "config" % "1.4.2"
    )
  )
