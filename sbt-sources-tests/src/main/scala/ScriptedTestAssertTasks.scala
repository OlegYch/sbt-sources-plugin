import sbt.Project

trait ScriptedTestAssertTasks extends Project {
  lazy val assertExpectedSources = task {
    None
  }
}
