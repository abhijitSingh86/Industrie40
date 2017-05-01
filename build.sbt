
name := "dikschedulingapp"

version := "1.0"

lazy val `dikschedulingapp` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"


PlayKeys.playRunHooks <+= baseDirectory.map(Webpack(_))

excludeFilter in (Assets, JshintKeys.jshint) := "*.js"

watchSources ~= { (ws: Seq[File]) =>
  ws filterNot { path =>
    path.getName.endsWith(".js") || path.getName == ("build")
  }
}

pipelineStages := Seq(digest, gzip)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"



libraryDependencies ++= Seq( cache , ws   , specs2 % Test )


libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.34"


libraryDependencies += "com.typesafe.play" %% "play-slick" % "2.0.0"


libraryDependencies += "com.typesafe.slick" %% "slick-codegen" % "3.0.0"


libraryDependencies += "com.h2database" % "h2" % "1.4.187" %  "test"

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )




