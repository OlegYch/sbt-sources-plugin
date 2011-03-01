import sbt._

class ScriptedTestProject(info: ProjectInfo)
  extends ParentProject(info) with ScriptedTestAssertTasks with ProjectWithSources with ExpectedManagedLib {

  val compileEntries = Set(
    "scalaj-http_2.8.0-0.2.5.jar",
    "time_2.8.0-0.2.jar",
    "commons-codec-1.4.jar",
    "joda-time-1.6.jar"
  )

  val testEntries = Set(
    "junit-4.8.1.jar"
  )

  override def libraryDependencies = super.libraryDependencies ++ Set(
    "org.scala-tools.time" % "time_2.8.0" % "0.2",
    "org.scalaj" % "scalaj-http_2.8.0" % "0.2.5",
    "junit" % "junit" % "4.8.1" % "test"
  )

  lazy val subproject1 = project("subproject1", "subproject1", new DefaultProject(_) {
    override def libraryDependencies = super.libraryDependencies ++ Set(
      "commons-io" % "commons-io" % "1.4" withSources,
      "commons-lang" % "commons-lang" % "2.5",
      "xalan" % "serializer" % "2.7.1"
    )
  })

  lazy val subproject2 = project("subproject2", "subproject2", new DefaultProject(_) with ExpectedManagedLib {
    val _ = "mysema" at "http://source.mysema.com/maven2/releases"
    val __ = "jboss" at "https://repository.jboss.org/nexus/content/groups/public-jboss/"

    override def libraryDependencies = super.libraryDependencies ++ Set(
      "com.mysema.querydsl" % "querydsl-jpa" % "2.1.2"
    )

    val compileEntries = Set(
      "scalaj-http_2.8.0-0.2.5.jar",
      "time_2.8.0-0.2.jar",
      "commons-codec-1.4.jar",
      "joda-time-1.6.jar"
    )

    val testEntries = Set(
      "junit-4.8.1.jar"
    )
  }, subproject1)
}
