lazy val commonSettings = Seq(
  organization := "net.cakesolutions",
  scalaVersion := "2.13.4",
  crossScalaVersions := Seq("2.11.12", "2.12.10", "2.13.1"),
  publishMavenStyle := true,
  bintrayOrganization := Some("cakesolutions"),
  bintrayPackageLabels := Seq("scala", "kafka"),
  //  resolvers += "Apache Staging" at "https://repository.apache.org/content/groups/staging/",
  resolvers += Resolver.bintrayRepo("mockito", "maven"),
  scalacOptions in Compile ++= Seq(
    "-encoding", "UTF-8",
    "-target:jvm-11",
    "-feature",
    "-deprecation",
    "-unchecked",
    "-Xlint",
    "-Ywarn-dead-code",
    "-Ywarn-unused"
  ) ++ (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, 13)) => Seq()
    case _ => Seq("-Xfuture", "-Ywarn-unused-import", "-Ywarn-nullary-unit")
  }),
  scalacOptions in(Compile, doc) ++= Seq("-groups", "-implicits"),
  javacOptions in(Compile, doc) ++= Seq("-notimestamp", "-linksource"),
  autoAPIMappings := true,

  //  publishTo :=
  //TODO publish snapshots to OSS
  //  if (Version.endsWith("-SNAPSHOT"))
  //    Seq(
  //      publishTo := Some("Artifactory Realm" at "http://oss.jfrog.org/artifactory/oss-snapshot-local"),
  //      bintrayReleaseOnPublish := false,
  //      // Only setting the credentials file if it exists (#52)
  //      credentials := List(Path.userHome / ".bintray" / ".artifactory").filter(_.exists).map(Credentials(_))
  //    )
  //  else

  parallelExecution in Test := false,
  parallelExecution in IntegrationTest := true,

  publishArtifact in Test := false,

  pomExtra := <scm>
    <url>git@github.com:cakesolutions/scala-kafka-client.git</url>
    <connection>scm:git:git@github.com:cakesolutions/scala-kafka-client.git</connection>
  </scm>
    <developers>
      <developer>
        <id>simon</id>
        <name>Simon Souter</name>
        <url>https://github.com/simonsouter</url>
      </developer>
      <developer>
        <id>jkpl</id>
        <name>Jaakko Pallari</name>
        <url>https://github.com/jkpl</url>
      </developer>
    </developers>,

  licenses := ("MIT", url("http://opensource.org/licenses/MIT")) :: Nil
)

lazy val kafkaTestkit = project.in(file("testkit"))
  .settings(commonSettings: _*)

lazy val scalaKafkaClient = project.in(file("client"))
  .settings(commonSettings: _*)
  .dependsOn(kafkaTestkit % "test")
  .configs(IntegrationTest extend Test)

lazy val scalaKafkaClientAkka = project.in(file("akka"))
  .settings(commonSettings: _*)
  .dependsOn(scalaKafkaClient)
  .dependsOn(kafkaTestkit % "test")
  .configs(IntegrationTest extend Test)

lazy val scalaKafkaClientExamples = project.in(file("examples"))
  .settings(commonSettings: _*)
  .dependsOn(scalaKafkaClientAkka)

lazy val root = project.in(file("."))
  .settings(commonSettings: _*)
  .enablePlugins(ScalaUnidocPlugin)
  .settings(name := "scala-kafka-client-root", publishArtifact := false, publish := {}, publishLocal := {})
  .aggregate(scalaKafkaClient, scalaKafkaClientAkka, kafkaTestkit)