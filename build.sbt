name := "masterCore"

version := "0.1"

scalaVersion := "2.11.0"

checksums := Seq("")
updateOptions := updateOptions.value.withLatestSnapshots(false)

resolvers += "LiquidEngine snapshots" at "https://raw.github.com/LiquidEngine/repo/snapshots"
resolvers += "LiquidEngine releases" at "https://raw.github.com/LiquidEngine/repo/releases"
resolvers += "LiquidEngine develop" at "https://raw.github.com/LiquidEngine/repo/develop"
resolvers += "LWJGL snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
resolvers += "Xuggle Repo" at "https://www.dcm4che.org/maven2/"

val sparkVersion = "2.3.0"
val lwjglVersion = "3.1.7-SNAPSHOT"
val jomlVersion = "1.9.9-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.scalaj" %% "scalaj-http" % "2.4.0",
  "org.json4s" %% "json4s-jackson" % "3.2.11",
  "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-mllib" % sparkVersion % "provided",
  "commons-io" % "commons-io" % "2.4",
  "org.l33tlabs.twl" % "pngdecoder" % "1.0"
)

val log4j_version = "2.3"
val commons_version = "3.4"
val commons_collections = "4.1"
val guava_version = "20.0"
val gson_version = "2.7"

libraryDependencies ++= Seq(
  "org.apache.logging.log4j" % "log4j-core" % log4j_version,
  "org.apache.commons" % "commons-lang3" % commons_version,
  "org.apache.commons" % "commons-collections4" % commons_collections,
  "com.google.guava" % "guava" % guava_version,
  "com.google.code.gson" % "gson" % gson_version
)
libraryDependencies += "xuggle" % "xuggle-xuggler" % "5.4"
libraryDependencies += "org.jcodec" % "jcodec" % "0.1.9"
libraryDependencies += "log4j" % "log4j" % "1.2.14"
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.25"
libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.25" % Test
libraryDependencies += "org.slf4j" % "slf4j-log4j12" % "1.7.25" % Test

libraryDependencies ++= Seq(
  "org.lwjgl" % "lwjgl" % lwjglVersion,
  "org.lwjgl" % "lwjgl-stb" % lwjglVersion,
  "org.lwjgl" % "lwjgl-glfw" % lwjglVersion,
  "org.lwjgl" % "lwjgl-nanovg" % lwjglVersion,
  "org.lwjgl" % "lwjgl-opengl" % lwjglVersion,
  "org.lwjgl" % "lwjgl-yoga" % lwjglVersion,
  "org.joml" % "joml" % jomlVersion
)

libraryDependencies ++= Seq(
  "org.liquidengine" % "legui" % "1.4.5-204",
  "org.liquidengine" % "leutil" % "1.0.0",
  "org.liquidengine" % "cbchain" % "1.0.0"
)
