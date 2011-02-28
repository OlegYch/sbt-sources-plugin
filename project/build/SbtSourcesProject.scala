/**
 * Copyright (C) 2010, Mikko Peltonen, Jon-Anders Teigen
 * Licensed under the new BSD License.
 * See the LICENSE file for details.
 */

import sbt._

class SbtSourcesProject(info: ProjectInfo) extends ParentProject(info) with IdeaProject with posterous.Publish {
  override def managedStyle = ManagedStyle.Maven

  lazy val publishTo = Resolver.file("GitHub Pages", new java.io.File("../mpeltonen.github.com/maven/"))

  lazy val core = project("sbt-sources-core", "sbt-sources-core", new Core(_))
  lazy val plugin = project("sbt-sources-plugin", "sbt-sources-plugin", new PluginProject(_) with IdeaProject, core)
  lazy val processor = project("sbt-sources-processor", "sbt-sources-processor",
    new ProcessorProject(_) with IdeaProject, core)
  lazy val tests = project("sbt-sources-tests", "sbt-sources-tests", new ScriptedTests(_), plugin)

  override def deliverProjectDependencies = super.deliverProjectDependencies.toList - tests.projectID

  class Core(info: ProjectInfo) extends DefaultProject(info) with IdeaProject {
    override def unmanagedClasspath = super.unmanagedClasspath +++ info.sbtClasspath
  }

  class ScriptedTests(info: ProjectInfo) extends DefaultProject(info) with test.SbtScripted with IdeaProject {
    val commonsIo = "commons-io" % "commons-io" % "1.4" withSources ()

    override def scriptedSbt = "0.7.4"

    override def publishAction = task {
      None
    }

    override def deliverAction = task {
      None
    }
  }

}
