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
    "annotations-1.3.2.jar", "asm-3.1.jar", "cglib-2.2-sources.jar", "cglib-2.2.jar",
    "codegen-0.3.1-sources.jar", "codegen-0.3.1.jar", "collections-generic-4.01-sources.jar",
    "collections-generic-4.01.jar", "commons-lang-2.5-sources.jar", "commons-lang-2.5.jar",
    "jsr305-1.3.2.jar", "mysema-commons-lang-0.2.1-sources.jar", "mysema-commons-lang-0.2.1.jar",
    "querydsl-core-2.1.2-sources.jar", "querydsl-core-2.1.2.jar"
  )

  val testEntries = TreeSet[String]()
}
