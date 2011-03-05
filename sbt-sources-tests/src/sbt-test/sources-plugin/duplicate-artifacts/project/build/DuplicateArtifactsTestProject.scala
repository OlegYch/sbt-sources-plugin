import collection.immutable.TreeSet
import sbt._

class DuplicateArtifactsTestProject(info: ProjectInfo)
        extends DefaultProject(info) with ScriptedTestAssertTasks with ProjectWithSources with
        ExpectedManagedLib {
  val _ = "mysema" at "http://source.mysema.com/maven2/releases"
  val __ = "jboss" at "https://repository.jboss.org/nexus/content/groups/public-jboss/"

  override def libraryDependencies = Set(
    "com.mysema.querydsl" % "querydsl-core" % "2.1.2"
    , "commons-lang" % "commons-lang" % "2.5" % "compile" withSources
  )

  val compileEntries = TreeSet(
    "xml-apis-1.3.04.jar",
    "xml-apis-1.3.04-sources.jar",
    "serializer-2.7.1.jar",
    "commons-io-1.4-sources.jar",
    "commons-io-1.4.jar",
    "commons-lang-2.5-sources.jar",
    "commons-lang-2.5.jar",
    "xml-apis-1.3.04.jar"
  )

  val testEntries = TreeSet[String]()
}
