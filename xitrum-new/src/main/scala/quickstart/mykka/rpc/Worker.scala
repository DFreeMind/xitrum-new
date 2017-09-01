package quickstart.mykka.rpc

import java.util.UUID

import com.typesafe.config.ConfigFactory

import akka.actor.Actor
import akka.actor.ActorSelection
import akka.actor.ActorSystem
import akka.actor.Props

class Worker(val cores: Int, val memory: Int,
             val masterHost: String, val masterPort: Int) extends Actor {

  //Master引用
  var master: ActorSelection = _
  val workerId = UUID.randomUUID().toString()
  var masterUrl: String = _
  val HEARTBEAT_INTERVAL = 10000

  //preStart在构造器之后receive之前执行
  override def preStart(): Unit = {
    //建立到Master的连接，通过actorSelection对象找到远程的Actor
    //并向其委派工作，这种方式只能使用远程Actor提供的服务
    val path = s"akka.tcp://${Master.MASTER_SYSTEM}@$masterHost:$masterPort/user/${Master.MASTER_NAME}"
    //是异步操作
    master = context.actorSelection(path)

    //向Master发送注册消息
    master ! RegisterWorker(workerId, cores, memory)
  }

  def receive: Actor.Receive = {
    //接收到注册成功的消息
    case RegisteredWorker(maseterUrl) => {
      this.masterUrl = masterUrl
      //启动定时任务,定期向Master汇报
      import context.dispatcher
      import scala.concurrent.duration._
      context.system.scheduler.schedule(0 millis, HEARTBEAT_INTERVAL millis, self, SendHeartbeat)
    }
    case SendHeartbeat => {
      //向Master发送心跳
      master ! Heartbeat(workerId)
    }
  }
}
object Worker {
  def main(args: Array[String]) {

    //Worker的地址和端口
    val host = "127.0.0.1"
    val port = 9489
    val cores = 4
    val memory = 1024
    //Master的地址和端口
    val masterHost = "127.0.0.1"
    val masterPort = 9487

    val confStr =
      s"""
      |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
      |akka.remote.netty.tcp.hostname = "$host"
      |akka.remote.netty.tcp.port = "$port"
      """.stripMargin
    val conf = ConfigFactory.parseString(confStr)
    //单例的ActorSystem
    val actorSystem = ActorSystem("WorkerActorSystem", conf)
    //通过actorSystem来创建Actor
    val worker = actorSystem.actorOf(Props(new Worker(cores, memory, masterHost, masterPort)), "Worker")
    actorSystem.terminate()
  } 
}
