import sbt._

class ScriptedTestProject(info: ProjectInfo)
  extends ParentProject(info) with ScriptedTestAssertTasks with ProjectWithSources with ExpectedManagedLib {

  override def libraryDependencies = super.libraryDependencies ++ Set(
    "commons-io" % "commons-io" % "1.4" withSources,
    "commons-lang" % "commons-lang" % "2.5",
    "xalan" % "serializer" % "2.7.1"
  )

  val compileEntries = Set(
    "scalatest-1.1.jar1",
    "scalatest-1.1-sources.jar",
    "serializer-2.7.1.jar",
    "commons-io-1.4-sources.jar",
    "commons-io-1.4.jar",
    "commons-lang-2.5-sources.jar",
    "commons-lang-2.5.jar",
    "xml-apis-1.3.04.jar"
  )

  val testEntries = Set[String]()
}
