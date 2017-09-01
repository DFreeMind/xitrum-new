package quickstart.mykka.route

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props

class AlternatingRoute(p1: ActorRef, p2: ActorRef, p3: ActorRef) extends Actor {
  var alternate = 1
  def alternateProcessor() = {
    if(alternate == 1){
      alternate = 2
      p1
    }else if (alternate == 2) {
      alternate = 3
      p2
    }else{
      alternate = 1
      p3
    }
  }
  def receive: Actor.Receive = {
    case message: Any => {
      val processor = alternateProcessor
      println(s"AlternateRouter: routing $message to ${processor.path.name}")
      processor ! message
    }
  }
}

object AlternatingRoute{
  def main(args: Array[String]): Unit = {
    val actorSystem = ActorSystem("Router")
    val p1 = actorSystem.actorOf(Props[Processor],"processor1")
    val p2 = actorSystem.actorOf(Props[Processor],"processor2")
    val p3 = actorSystem.actorOf(Props[Processor],"processor3")
    val alternateRoute = actorSystem.actorOf(Props(classOf[AlternatingRoute],p1,p2,p3), "AlternatingRoute")
    for(count <- 1 to 10){
      alternateRoute ! "Message #" + count
    }
    actorSystem.terminate()
    println("MessageRoute： is Completed")
  }
}








