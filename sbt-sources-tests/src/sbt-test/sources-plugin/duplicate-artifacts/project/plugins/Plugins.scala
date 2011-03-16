import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
  val testedVersion = "0.3.0-SNAPSHOT"
  val groupId = "com.olegych"
  val sourcesPlugin = groupId % "sbt-sources-plugin" % testedVersion
  val scriptedTestUtils = groupId % "sbt-sources-tests_2.7.7" % testedVersion
}
