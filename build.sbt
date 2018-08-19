val commonJavaOptions = Seq("-source", "1.8")

val commonSettings = Seq(
  name                := "VirtualKeyboard",
  organization        := "de.sciss",
  version             := "1.0.0",
  scalaVersion        := "2.12.6",
  licenses            := Seq("MIT" -> url("https://raw.githubusercontent.com/Sciss/VirtualKeyboard/master/LICENSE")),
  crossPaths          := false,
  autoScalaLibrary    := false,
  homepage            := Some(url(s"https://github.com/Sciss/${name.value}")),
  description         := "A virtual keyboard in Swing for Java SE applications",
  javacOptions        := commonJavaOptions ++ Seq("-target", "1.8", "-g", "-Xlint:deprecation"),
  javacOptions in doc := commonJavaOptions  // cf. sbt issue #355
)

val publishSettings = Seq(
  publishMavenStyle := true,
  publishTo := Some(
    if (isSnapshot.value)
      "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
    else
      "Sonatype Releases"  at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
  ),
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  pomExtra := { val n = name.value
    <scm>
      <url>git@github.com:Sciss/{n}.git</url>
      <connection>scm:git:git@github.com:Sciss/{n}.git</connection>
    </scm>
    <developers>
      <developer>
        <id>wcmjunior</id>
        <name>Wilson de Carvalho</name>
        <url>https://br.linkedin.com/in/wilsondecarvalho</url>
      </developer>
      <developer>
        <id>sciss</id>
        <name>Hanns Holger Rutz</name>
        <url>https://www.sciss.de</url>
      </developer>
    </developers>
  }
)

lazy val root = project.in(file("."))
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(
    libraryDependencies ++= Seq(
      "de.sciss" % "submin" % "0.2.2" % Test
    ),
    mainClass in (Test, run) := Some("de.sciss.virtualkeyboard.Demo")
  )

