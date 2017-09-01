package quickstart.mykka.biz

import akka.actor.Actor
import akka.event.Logging

//工人
class Worker extends Actor {
  val log = Logging(context.system,this)
  def receive: Actor.Receive = {
    case d: DoAction => {
      log.info("接收到经理指派的任务")
      sender ! Done("完成任务")
    }
  }
}