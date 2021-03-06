/**
 * Copyright (C) 2010, Mikko Peltonen, Jon-Anders Teigen
 * Licensed under the new BSD License.
 * See the LICENSE file for details.
 */

import sbt._

class SbtSourcesProject(info: ProjectInfo) extends ParentProject(info) with IdeaProject with posterous.Publish {
  override def managedStyle = ManagedStyle.Maven

  lazy val olegychRepo = "olegychRepo" at "https://bitbucket.org/olegych/mvn/raw/default"

  lazy val publishTo = Resolver.file("OlegYch repo wc", new java.io.File("../../mvn/mvn/"))

  lazy val core = project("sbt-sources-core", "sbt-sources-core", new Core(_))
  lazy val plugin = project("sbt-sources-plugin", "sbt-sources-plugin", new PluginProject(_) with IdeaProject, core)
  lazy val processor = project("sbt-sources-processor", "sbt-sources-processor",
    new ProcessorProject(_) with IdeaProject, core)
  lazy val tests = project("sbt-sources-tests", "sbt-sources-tests", new ScriptedTests(_), plugin)

  override def deliverProjectDependencies = super.deliverProjectDependencies.toList - tests.projectID

  class Core(info: ProjectInfo) extends DefaultProject(info) with IdeaProject {
    override def unmanagedClasspath = super.unmanagedClasspath +++ info.sbtClasspath

    override def libraryDependencies = super.libraryDependencies ++ Set("org.apache.ivy" % "ivy" % "2.2.0")
  }

  class ScriptedTests(info: ProjectInfo) extends DefaultProject(info) with test.SbtScripted with IdeaProject {
    val commonsIo = "commons-io" % "commons-io" % "1.4"
    val scalatest = "org.scalatest" % "scalatest" % "1.1"

    override def scriptedSbt = "0.7.5.RC0"

    override def publishAction = task {
      None
    }

    override def deliverAction = task {
      None
    }
  }

}
