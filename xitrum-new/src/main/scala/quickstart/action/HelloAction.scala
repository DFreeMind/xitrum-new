package quickstart.action



import xitrum.ActorAction
import xitrum.annotation.GET
import xitrum.Action

/**
 * @author weduoo
 */

@GET("/hello")
class HelloAction extends Action{
  def execute(){
    respondView()
  }
}

/*@GET("actor")
class HelloAction extends ActorAction {
  def execute() {
    // See Akka doc about scheduler
    import context.dispatcher
    //import scala.concurrent.duration._
    //context.system.scheduler.scheduleOnce(3 seconds, self, System.currentTimeMillis())
    // See Akka doc about "become"
    context.become {
      case pastTime =>
        respondInlineView(s"It’s $pastTime Unix ms 3s ago.")
    }
  }
}*/