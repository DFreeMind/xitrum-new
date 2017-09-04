package quickstart.mykka.rpc

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import akka.actor.Actor
import scala.collection._
import akka.actor.Props

class Master(val host: String, val port: Int) extends Actor {

  //保存WorkerID 到 WorkerInfo的映射
  val idToWorker = new mutable.HashMap[String, WorkerInfo]()
  //保存所有的WorkerInfo信息,使用set吧保证不重复
  val workers = new mutable.HashSet[WorkerInfo]()

  //监测间隔
  val CHECK_INTERVAL = 15000

  override def preStart(): Unit = {
    //导入定时转换,定时任务调度
    import context.dispatcher
    import scala.concurrent.duration._
    //schedule,第一个参数表示延迟时间,第二个参数表示间隔,第三个表示接收的程序,第四个表示发送的消息
    context.system.scheduler.schedule(0 millis, CHECK_INTERVAL millis, self, CheckTimeOutWorker)
  }

  def receive: Actor.Receive = {
    //Worker向Maser发送的消息
    case RegisterWorker(workerId, cores, memory) => {
      //表示第一次注册
      if (!idToWorker.contains(workerId)) {
        //封装worker发送的消息
        val workerInfo = new WorkerInfo(workerId, cores, memory)
        //保存workerInfo信息
        idToWorker(workerId) = workerInfo
        workers += workerInfo
        //Master向Worker发送注册成功的消息
        val url = s"akka.tcp://${Master.MASTER_SYSTEM}@$host:$port/user/${Master.MASTER_NAME}";
        //Worker，由worker向master发送消息
        println("the last message actor ref：" + sender)
        sender ! RegisteredWorker(url)
      }
    }

    //Worker向Master发送心跳消息
    case Heartbeat(workerId) => {
      if (idToWorker.contains(workerId)) {
        val workerInfo = idToWorker(workerId)
        val currentTime = System.currentTimeMillis()
        //更新上一次心跳时间
        workerInfo.lastHeartbeatTime = currentTime
      }
    }

    //超时时间监测
    case CheckTimeOutWorker => {
      val currentTime = System.currentTimeMillis()
      //过滤出超时的Worker
      val deadWorkers: mutable.HashSet[WorkerInfo] =
        workers.filter(w => currentTime - w.lastHeartbeatTime > CHECK_INTERVAL)
      //将不再发送心跳的Worker清除
      deadWorkers.foreach(w => {
        idToWorker -= w.id
        workers -= w
      })

      println("存活的worker数:" + workers.size)
    }
  }
}
object Master {
  val MASTER_SYSTEM: String = "MasterActorSystem"
  val MASTER_NAME: String = "Master"

  def main(args: Array[String]): Unit = {
    val host = "127.0.0.1"
    val port = 9487
    val confStr =
      s"""
      |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
      |akka.remote.netty.tcp.hostname = "$host"
      |akka.remote.netty.tcp.port = "$port"
    """.stripMargin
    val conf = ConfigFactory.parseString(confStr)
    //ActorSystem是单例的，用于创建Acotor并监控actor
    val actorSystem = ActorSystem(MASTER_SYSTEM, conf)
    //通过ActorSystem创建Actor
    actorSystem.actorOf(Props(new Master(host, port)), MASTER_NAME)
//    actorSystem.terminate()
  }
}
