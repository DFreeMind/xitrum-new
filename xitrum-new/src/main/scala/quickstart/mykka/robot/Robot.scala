package quickstart.mykka.robot

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props

class Robot extends Actor {
  def receive: Actor.Receive = {
    case t: TurnOnLinght => println(s"${t.message}  用了 ${t.time} ")
    case b: BoilWater => println(s"${b.message}  用了 ${b.time} ")
    case _ => println("主人你说(・◇・)？")
  }
}

object Robot extends App{
  val actorSystem = ActorSystem("Robot-System")
  //默认使用的LogLevel是INFO
  //日志的级别是在akka核心包下的reference.conf 中配置
  
//  # Log level used by the configured loggers (see "loggers") as soon
//  # as they have been started; before that, see "stdout-loglevel"
//  # Options: OFF, ERROR, WARNING, INFO, DEBUG
//  loglevel = "INFO"
  //然后通过final val LogLevel: String = getString("akka.loglevel")方法获取
  //通过在修改conf目录下的akka.conf 文件可以修改日志的级别
  //配置方式可以参考akka中的reference.conf 文件
  println(s"the ActorSystem logLevel is ${actorSystem.settings.LogLevel}")
  val robot = actorSystem.actorOf(Props[Robot], "RobotSystem")
  val robot2 = actorSystem.actorOf(Props[Robot], "RobotSystem2")
  robot ! TurnOnLinght(1)
  robot ! BoilWater(3)
  robot2 ! "wtf"
  actorSystem.terminate()
}



