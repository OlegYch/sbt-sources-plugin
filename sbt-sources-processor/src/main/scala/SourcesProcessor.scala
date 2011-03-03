/**
 * Copyright (C) 2010, Jon-Anders Teigen, Mikko Peltonen, Jason Zaugg
 * Licensed under the new BSD License.
 * See the LICENSE file for details.
 */

import sbt._
import processor._

class SourcesProcessor extends BasicProcessor {
  def apply(project: Project, args: String) {
    UpdateSourcesTask(project)
  }
}
