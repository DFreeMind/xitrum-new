package quickstart.mykka.route.dynamic

import akka.actor.ActorRef
import akka.actor.Actor

/**
 * 对不同消息类型感兴趣的Actor
 */
class Interested {}

//处理没有任何Actor感兴趣的消息
class DunnoInterested extends Actor {
  def receive: Actor.Receive = {
    case message: Any => {
      println(s"DunnoInterested: received undeliverabe message $message")
      DynamicRouter.completedStep()
    }
  }
}

class TypeAInterested(interestRouter: ActorRef) extends Actor {
  //注册感兴趣的消息类型
	interestRouter ! InterestedIn(TypeAMessage.getClass.getName)
	def receive: Actor.Receive = {
  	case message: TypeAMessage => {
  		println(s"TypeAInterested：received $message")
  		DynamicRouter.completedStep()
  	}
  	case message: Any =>{
  		println(s"TypeAInterested: unexcepted $message")
  	}
	}
}
class TypeBInterested(interestRouter: ActorRef) extends Actor {
  interestRouter ! InterestedIn(TypeBMessage.getClass.getName)
  def receive: Actor.Receive = {
    case message: TypeBMessage => {
      println(s"TypeBInterested：received $message")
      DynamicRouter.completedStep()
    }
    case message: Any =>{
      println(s"TypeBInterested: unexcepted $message")
    }
  }
}

//此处有两个对 C 类消息感兴趣，第一个注册的是主要接受者
//第二个注册的是次要接收者
class TypeCInterested(interestRouter: ActorRef) extends Actor {
	interestRouter ! InterestedIn(TypeCMessage.getClass.getName)
	def receive: Actor.Receive = {
  	case message: TypeCMessage => {
  		println(s"TypeCInterested：received $message")
  		interestRouter ! NoLongerInterestedIn(TypeCMessage.getClass.getName)
  		DynamicRouter.completedStep()
  	}
  	case message: Any =>{
  		println(s"TypeCInterested: unexcepted $message")
  	}
	}
}
//次要接收者
class TypeCAlsoInterested(interestRouter: ActorRef) extends Actor {
	interestRouter ! InterestedIn(TypeCMessage.getClass.getName)
	def receive: Actor.Receive = {
  	case message: TypeCMessage => {
  		println(s"TypeCAlsoInterested：received $message")
  		interestRouter ! NoLongerInterestedIn(TypeCMessage.getClass.getName)
  		DynamicRouter.completedStep()
  	}
  	case message: Any => {
  		println(s"TypeCAlsoInterested: unexcepted $message")
  	}
	}
}















