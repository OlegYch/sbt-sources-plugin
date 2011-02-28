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

  lazy val updateSources = updateSourcesTask(updateIvyModule, ivyUpdateConfiguration) describedAs UpdateDescription

  def updateSourcesTask(module: => IvySbt#Module, configuration: => UpdateConfiguration) = ivyTask {
    update(module, configuration)
  }

  import scala.collection.jcl.Buffer
  import java.{util => ju}
  import org.apache.ivy.core.module.descriptor._
  import org.apache.ivy.core.resolve._
  import org.apache.ivy.core.report._

  def srcDependency(node: IvyNode): DependencyDescriptor = {
    val descriptor = node.getAllCallers()(0).getDependencyDescriptor().asInstanceOf[DefaultDependencyDescriptor]
    for (conf <- descriptor.getModuleConfigurations) {
      def ddad(t: String, attrs: Map[String, String]) = new
                      DefaultDependencyArtifactDescriptor(descriptor, node.getId.getName, t, "jar", null,
                        new ju.HashMap[String, String] {
                          attrs.foreach(e => put(e._1, e._2))
                        })
      descriptor.addDependencyArtifact(conf, ddad("jar", Map.empty))
      descriptor.addDependencyArtifact(conf, ddad("src", Map("classifier" -> "sources")))
    }
    descriptor
  }

  def addSources(getReport: => ResolveReport, md: DefaultModuleDescriptor): Unit = {
    val initialReport = getReport
    def deps(report: ResolveReport): Seq[IvyNode] = Buffer(report.getDependencies.asInstanceOf[ju.List[IvyNode]])
    def artifacts(report: ResolveReport): Seq[Artifact] = Buffer(report.getArtifacts.asInstanceOf[ju.List[Artifact]])
    log.info("Adding sources for " + artifacts(initialReport).toString)
    deps(initialReport).foreach((node: IvyNode) => md.addDependency(srcDependency(node)))
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

  private def resolve(logging: UpdateLogging.Value)(ivy: Ivy, module: DefaultModuleDescriptor, defaultConf: String) = {
    val resolveOptions = new ResolveOptions
    resolveOptions.setLog(ivyLogLevel(logging))
    val resolveReport = ivy.resolve(module, resolveOptions)
    if (resolveReport.hasError) {
      throw new ResolveException(resolveReport.getAllProblemMessages.toArray.map(_.toString).toList.removeDuplicates)
    }
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

final class ResolveException(messages: List[String]) extends RuntimeException(messages.mkString("\n"))
