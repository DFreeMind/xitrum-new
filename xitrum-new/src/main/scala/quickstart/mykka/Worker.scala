package quickstart.mykka

import akka.actor.Actor
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import akka.actor.Props

/**
 * @author weduoo
 */
class Worker extends Actor {

  override def preStart():Unit = {
    //首先与Master建立连接
    val path = "akka.tcp://MasterActorSystem@localhost:9487/user/Master"
    val master = context.actorSelection(path)
    
    //通过Master的引用向Master发送消息
    master ! "connect"
  }
  
  def receive: Actor.Receive = {
    case "success" => {
      println("a success message from myself: success!")
    }
    case "successed" => {
    	println("a success message from master: successed!")
    }
  }
  
}

object Worker{
  def main(args: Array[String]): Unit = {
    val host = "localhost"
    val port = 9489
    
    val confStr =
      s"""
      |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
      |akka.remote.netty.tcp.hostname = "$host"
      |akka.remote.netty.tcp.port = "$port"
      """.stripMargin
      val conf = ConfigFactory.parseString(confStr)
      val workerSystem = ActorSystem("WorkerActorSystem",conf)
      val worker = workerSystem.actorOf(Props[Worker], "Worker")
      worker ! "success"
      
  }
}


