import java.io.File
import org.apache.ivy.core.retrieve.RetrieveOptions
import org.apache.ivy.Ivy
import sbt._

/**
 * @author OlegYch
 */

trait UpdateSourcesTask {
  val self: BasicManagedProject

  import self._

  val UpdateDescription =
    "Resolves and retrieves automatically managed dependencies (including sources)."

  lazy val updateSources = updateSourcesTask(updateIvyModule, ivyUpdateConfiguration) describedAs
          UpdateDescription

  //explicitly recurse into children, so that only parent project should have this task
  def updateSourcesTask(module: => IvySbt#Module, configuration: => UpdateConfiguration) = ivyTask {
    update(module, configuration)
  } dependsOn task {
    subProjects.values.filter(self !=).foreach(UpdateSourcesTask.apply)
    None
  }

  import scala.collection.jcl.Buffer
  import java.{util => ju}
  import org.apache.ivy.core.module.descriptor._
  import org.apache.ivy.core.resolve._
  import org.apache.ivy.core.report._
  import org.apache.ivy.core.module.descriptor.{DefaultDependencyDescriptor => DDD}
  import org.apache.ivy.core.module.descriptor.{DependencyArtifactDescriptor => DAD}

  def addSources(descriptors: List[DDD], dependencyArtifacts: List[DAD]) {
    val descriptor = descriptors.head
    log.info("artifacts for %s : %s".format(descriptors, dependencyArtifacts))
    for (conf <- descriptor.getModuleConfigurations.filter(List("compile", "provided", "test") contains)
            .take(1)) {
      def ddad(t: String, attrs: Map[String, String]) = new
                      DefaultDependencyArtifactDescriptor(descriptor, descriptor.getDependencyId.getName, t,
                        "jar", null,
                        new ju.HashMap[String, String] {
                          attrs.foreach(e => put(e._1, e._2))
                        })
      def addArtifact(artifactDescriptor: DAD) {
        if (!dependencyArtifacts.exists((d: DAD) => artifactDescriptor.getType == d.getType)) {
          log.info("Adding artifact %s".format(artifactDescriptor))
          descriptor.addDependencyArtifact(conf, artifactDescriptor)
        }
      }
      addArtifact(ddad("jar", Map()))
      addArtifact(ddad("src", Map("classifier" -> "sources")))
    }
  }

  def addSources(getReport: => ResolveReport, md: DefaultModuleDescriptor) {
    val initialReport = getReport
    if (initialReport.hasError) {
      throw new ResolveException(
        initialReport.getAllProblemMessages.toArray.map(_.toString).toList.removeDuplicates)
    }
    def deps(report: ResolveReport): Seq[IvyNode] = Buffer(
      report.getDependencies.asInstanceOf[ju.List[IvyNode]])
    def artifacts(report: ResolveReport): Seq[Artifact] = Buffer(
      report.getArtifacts.asInstanceOf[ju.List[Artifact]])
    log.info("Adding sources for " + artifacts(initialReport).toString)
    val nodesByIdWOVersion = deps(initialReport)
            .map((node: IvyNode) => ((node.getId.getName, node.getId.getOrganisation), node))
    log.info("Nodes " + nodesByIdWOVersion)
    type DepsWithArtifacts = (List[DDD], List[DAD])
    type IdWOVersion = (String, String)
    type MapForDeps = Map[IdWOVersion, DepsWithArtifacts]
    val descriptors = (Map.empty[IdWOVersion, DepsWithArtifacts] /: nodesByIdWOVersion) {
      (map: MapForDeps, item: (IdWOVersion, IvyNode)) =>
        val d = item._2.getAllCallers.map(_.getDependencyDescriptor().asInstanceOf[DDD]).toList
        val newArtifacts: List[DAD] = d.flatMap(_.getAllDependencyArtifacts).toList
        val previousArtifacts: List[DAD] = map.getOrElse(item._1, (d, Nil))._2
        map + (item._1 -> (d, previousArtifacts ::: newArtifacts))
    }
    descriptors.values.foreach(p => addSources(p._1, p._2))
    val newReport = getReport
    log.info("Added sources " + artifacts(newReport).toString)
    return newReport
  }

  def update(module: IvySbt#Module, configuration: UpdateConfiguration) {
    module.withModule {
      case (ivy, md: DefaultModuleDescriptor, default) =>
        import configuration._
        def report = resolve(logging)(ivy, md, default)
        addSources(report, md)
        val retrieveOptions = new RetrieveOptions
        retrieveOptions.setSync(synchronize)
        val patternBase = retrieveDirectory.getAbsolutePath
        val pattern =
          if (patternBase.endsWith(File.separator)) {
            patternBase + configuration.outputPattern
          }
          else {
            patternBase + File.separatorChar + configuration.outputPattern
          }
        ivy.retrieve(md.getModuleRevisionId, pattern, retrieveOptions)
    }
  }

  private def resolve(logging: UpdateLogging.Value)
                     (ivy: Ivy, module: DefaultModuleDescriptor, defaultConf: String) = {
    val resolveOptions = new ResolveOptions
    resolveOptions.setLog(ivyLogLevel(logging))
    val resolveReport = ivy.resolve(module, resolveOptions)
    resolveReport
  }

  import UpdateLogging.{Quiet, Full, DownloadOnly}
  import org.apache.ivy.core.LogOptions.{LOG_QUIET, LOG_DEFAULT, LOG_DOWNLOAD_ONLY}

  private def ivyLogLevel(level: UpdateLogging.Value) =
    level match {
      case Quiet => LOG_QUIET
      case DownloadOnly => LOG_DOWNLOAD_ONLY
      case Full => LOG_DEFAULT
    }
}

object UpdateSourcesTask {
  def apply(project: Project) {
    project match {
      case p: BasicManagedProject => apply(p)
    }
  }

  def apply(project: BasicManagedProject) {
    new UpdateSourcesTask {
      val self = project

      this.updateSources.run
    }
  }
}

final class ResolveException(messages: List[String]) extends RuntimeException(messages.mkString("\n"))
