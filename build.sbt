name := "masterCore"

version := "0.1"

scalaVersion := "2.11.12"

val sparkVersion = "2.3.0"

libraryDependencies ++= Seq(
  "org.scalaj" %% "scalaj-http" % "2.4.0",
  "org.json4s" %% "json4s-jackson" % "3.2.11",
  "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-mllib" % sparkVersion % "provided",
  "com.google.guava" % "guava" % "11.0",
  "commons-io" % "commons-io" % "2.4",
  "org.apache.commons" % "commons-lang3" % "3.1"
)
//libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % "2.11.0"
//libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.11.0"
//libraryDependencies += "org.reflections" % "reflections" % "0.9.5-RC2"