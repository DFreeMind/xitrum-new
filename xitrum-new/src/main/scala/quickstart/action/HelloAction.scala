package quickstart.action

import xitrum.ActorAction
import xitrum.annotation.GET
import xitrum.Action

/**
 * @author weduoo
 */

@GET("myAction")
class MyAction extends Action{
  def execute(){
    respondView()
  }
  def hello(what: String) = "Hello %s".format(what)
}
