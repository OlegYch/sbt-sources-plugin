import sbt._

class ScriptedTestProject(info: ProjectInfo)
        extends ParentProject(info) with ScriptedTestAssertTasks with ProjectWithSources {
  lazy val subproject1 = project("subproject1", "subproject1", new DefaultProject(_) with ProjectWithSources)
  lazy val subproject2 = project("subproject2", "subproject2", new DefaultProject(_) with ProjectWithSources,
    subproject1)
}
