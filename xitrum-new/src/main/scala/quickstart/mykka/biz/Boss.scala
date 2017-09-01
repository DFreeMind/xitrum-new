package quickstart.mykka.biz

import scala.concurrent.duration._
import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.event.Logging
import akka.util.Timeout
import scala.tools.scalap.Main

//公司老板
class Boss extends Actor {
  val log = Logging(context.system, this)
  implicit val askTimeout = Timeout(5 seconds)
  import context.dispatcher
  var taskCount = 0
  def receive: Receive = {
    case b: Business =>
      log.info("发现商机我一定要做点事情，go go go!")
      println(self.path.address)
      //创建Actor得到ActorRef的另一种方式，利用ActorContext.actorOf
      val managerActors = (1 to 3).map(i =>
        context.actorOf(Props[Managers], s"manager${i}")) //这里我们召唤3个主管
      //告诉他们开会商量大计划
      import akka.pattern.ask
      managerActors foreach {
        _ ? Meeting("全部过来商量计划") map {
          case c: Confirm =>
            //为什么这里可以知道父级Actor的信息？
            //熟悉树结构的同学应该知道每个节点有且只有一个父节点（根节点除外）
            //akka://Company-System/user/Boss
            log.info(c.actorPath.parent.toString)
            ////akka://Company-System/user/Boss/manager1
            log.info(c.actorPath.toString())
            //根据Actor路径查找已经存在的Actor获得ActorRef，即获取 manager actor引用
            //这里c.actorPath是绝对路径,你也可以根据相对路径得到相应的ActorRef
            val manager = context.actorSelection(c.actorPath)
            manager ! DoAction("Do thing")
        }
      }
    case d: Done => {
      taskCount += 1
      if (taskCount == 3) {
        log.info("the project is done, we will earn much money")
        context.system.terminate()
      }
    }
  }
}

object Boss{
  def main(args: Array[String]): Unit = {
    val actorSystem = ActorSystem("Company-System")
    val boss = actorSystem.actorOf(Props[Boss], "Boss")
    val boss2= actorSystem.actorOf(Props[Boss], "Boss2")
    boss ! Business("发现商机") 
    //boss2 ! Business("发现商机") 
  }
} 