import collection.immutable.TreeSet
import sbt._

class ScriptedTestProject(info: ProjectInfo)
        extends ParentProject(info) with ScriptedTestAssertTasks with ProjectWithSources with
        ExpectedManagedLib {

  val compileEntries = TreeSet(
    "commons-codec-1.4-sources.jar",
    "commons-codec-1.4.jar",
    "joda-time-1.6-sources.jar",
    "joda-time-1.6.jar",
    "scalaj-http_2.8.0-0.2.5-sources.jar",
    "scalaj-http_2.8.0-0.2.5.jar",
    "time_2.8.0-0.2.jar"
  )

  val testEntries = TreeSet(
    "junit-4.8.1-sources.jar",
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

    val compileEntries = TreeSet[String](
      "annotations-1.3.2.jar",
      "asm-3.1.jar",
      "cglib-2.2-sources.jar",
      "cglib-2.2.jar",
      "codegen-0.3.1-sources.jar",
      "codegen-0.3.1.jar",
      "collections-generic-4.01-sources.jar", "collections-generic-4.01.jar", "commons-lang-2.4-sources.jar",
      "commons-lang-2.4.jar", "hibernate-jpa-2.0-api-1.0.0.Final-sources.jar",
      "hibernate-jpa-2.0-api-1.0.0.Final.jar", "jsr305-1.3.2.jar", "mysema-commons-lang-0.2.1-sources.jar",
      "mysema-commons-lang-0.2.1.jar", "querydsl-core-2.1.2-sources.jar", "querydsl-core-2.1.2.jar",
      "querydsl-jpa-2.1.2-sources.jar", "querydsl-jpa-2.1.2.jar", "slf4j-api-1.6.1-sources.jar",
      "slf4j-api-1.6.1.jar"
    )

    val testEntries = TreeSet[String](
    )
  }, subproject1)
}
