package quickstart.action

import xitrum.annotation.GET

@GET("")
class SiteIndex extends DefaultLayout {
  def execute() {
    respondText("hello weduoo")
  }
}