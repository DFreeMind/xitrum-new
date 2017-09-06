package quickstart.mykka

import akka.actor.Actor
import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem
import akka.actor.Props

/**
 * @author weduoo
 */
class Master extends Actor{
  override def receive:Receive = {
    case "start" => {
      println("starting")
      println("started")
    }
    
    case "stop" => {
      println("stopping")
      println("stopped")
    }
    
    case "connect" => {
      println("a client conneting")
      println("connected")
      println("connect sender:" + sender)
      sender ! "successed"
    }
    
    case "successed" => {
      println("Master successed!")
      println("Master sender:" + sender)
    }
    case _ => {
      println("hehe you are a bad boy!")
    }
    
  }
}

object Master{
  def main(args: Array[String]): Unit = {
    val host = "localhost"
    val port = "9487"
    
    val confStr =
      s"""
      |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
      |akka.remote.netty.tcp.hostname = "$host"
      |akka.remote.netty.tcp.port = "$port"
      """.stripMargin
    
    val conf = ConfigFactory.parseString(confStr)
    val actorSystem = ActorSystem("MasterActorSystem",conf)
    //通过ActorSystem创建actor
    val master = actorSystem.actorOf(Props[Master], "Master")
    
    master ! "hello"
    master ! "connect"
    master ! "stop"
    
    
  }
}










