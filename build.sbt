name := """MinesweeperWebT"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += Resolver.sonatypeRepo("snapshots")

scalaVersion := "2.12.2"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies += "com.h2database" % "h2" % "1.4.196"
libraryDependencies += "io.spray" %%  "spray-json" % "1.3.3"

unmanagedJars in Compile += file("../MinesweeperSoEn/build/libs/MinesweeperSoEn-all.jar")
