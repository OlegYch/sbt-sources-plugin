import collection.immutable.TreeSet
import collection.SortedSet
import org.scalatest._
import org.scalatest.events._
import sbt.BasicDependencyProject
import org.scalatest.matchers.ShouldMatchers

trait ScriptedTestAssertTasks {
  self: BasicDependencyProject =>

  implicit def pathToStrings(p: sbt.PathFinder): SortedSet[String] = TreeSet.empty[String] ++
          (p.getFiles.map(_.getName))

  def assertedProjects = (self :: subProjects.values.toList).flatMap {
    _ match {
      case p: ExpectedManagedLib => Some(p)
      case _ => None
    }
  }

  lazy val assertExpectedSources = task {
    val results = assertedProjects.map(p => {
      val reporter = new PassFailReporter
      new FlatSpec with ShouldMatchers {
        "project dependencies " should "contain sources" in {
          pathToStrings(p.compileCp) should equal(p.compileEntries)
          pathToStrings(p.testCp) should equal(p.testEntries)
        }
        execute
        run(None, reporter, new Stopper {}, Filter(), Map(), None, new Tracker)
      }
      (p, reporter.allTestsPassed)
    })
    if (results.exists(res => !res._2)) {
      Some(results.toString)
    } else {
      None
    }
  }

  private class PassFailReporter extends Reporter {

    @volatile private var failedAbortedOrStopped = false

    override def apply(event: Event) {
      event match {
        case _: TestFailed =>
          failedAbortedOrStopped = true

        case _: RunAborted =>
          failedAbortedOrStopped = true

        case _: SuiteAborted =>
          failedAbortedOrStopped = true

        case _: RunStopped =>
          failedAbortedOrStopped = true

        case _: RunCompleted =>

        case _ =>
      }
    }

    def allTestsPassed = {
      !failedAbortedOrStopped
    }
  }

}

trait ExpectedManagedLib extends BasicDependencyProject {
  val compileEntries: SortedSet[String]
  val testEntries: SortedSet[String]

  def compileCp = projectClasspath(config("compile"))

  def testCp = projectClasspath(config("test"))
}
