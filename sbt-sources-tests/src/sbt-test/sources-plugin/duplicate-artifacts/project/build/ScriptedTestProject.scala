import collection.immutable.TreeSet
import sbt._

class ScriptedTestProject(info: ProjectInfo)
        extends DefaultProject(info) with ScriptedTestAssertTasks with ProjectWithSources with
        ExpectedManagedLib {

  override def libraryDependencies = Set(
    "org.seleniumhq.selenium" % "selenium-server" % "2.0a6" % "compile"
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
