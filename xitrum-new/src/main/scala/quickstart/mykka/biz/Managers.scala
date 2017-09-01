package quickstart.mykka.biz

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging

//经理
class Managers extends Actor {
  val log = Logging(context.system,this)
  def receive: Actor.Receive = {
    //开会通知
    case m: Meeting => {
      log.info("经理打印：" + self.path.toString())
      //sender是当前ActorContext提供的方法
      //可以获取到消息发送者的引用，此处即为 Boss Actor的引用
      //获取到Boss 的 ActorRef 之后，就可以向Boss发送消息
      sender ! Confirm("收到通知", self.path)
    }
    //开完会开始分给工人任务
    case d: DoAction => {
      val worker = context.actorOf(Props[Worker], "Worker")
      println("Manager's ActorSsytem : " + context.system)
      worker forward d
    }
  }
}