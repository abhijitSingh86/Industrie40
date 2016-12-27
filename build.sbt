name := "dikschedulingapp"

version := "1.0"

lazy val `dikschedulingapp` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"


libraryDependencies ++= Seq( cache , ws   , specs2 % Test )


libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.34"


libraryDependencies += "com.typesafe.play" %% "play-slick" % "2.0.0"


libraryDependencies += "com.typesafe.slick" %% "slick-codegen" % "3.0.0"


unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )


resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"


