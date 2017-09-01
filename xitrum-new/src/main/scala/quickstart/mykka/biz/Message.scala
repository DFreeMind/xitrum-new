package quickstart.mykka.biz

import akka.actor.ActorPath

//用于传递消息
trait Message {
  val content: String
}

//发现商机
case class Business(content: String) extends Message{}
//开会讨论
case class Meeting(content: String) extends Message{}
//确认执行
case class Confirm(content: String, actorPath: ActorPath) extends Message{}
//开始执行计划
case class DoAction(content: String) extends Message{}
//完成计划
case class Done(content: String) extends Message{}
