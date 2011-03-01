import org.scalatest.FlatSpec
import sbt.BasicDependencyProject
import org.scalatest.matchers.ShouldMatchers

trait ScriptedTestAssertTasks {
  self: BasicDependencyProject =>

  implicit def pathToStrings(p: sbt.PathFinder): Set[String] = p.getFiles.map(_.getName)

  def assertedProjects = (self :: subProjects.values.toList).flatMap {
    _ match {
      case p: ExpectedManagedLib => Some(p)
      case _ => None
    }
  }

  lazy val assertExpectedSources = task {
    assertedProjects.foreach(p => {
      new FlatSpec with ShouldMatchers {
        "project dependencies " should "contain sources" in {
          pathToStrings(p.compileCp) should equal(p.compileEntries)
          pathToStrings(p.testCp) should equal(p.testEntries)
        }
        execute
      }
    })
    None
  }
}

trait ExpectedManagedLib extends BasicDependencyProject {
  val compileEntries: Set[String]
  val testEntries: Set[String]

  def compileCp = projectClasspath(config("compile"))

  def testCp = projectClasspath(config("test"))
}
