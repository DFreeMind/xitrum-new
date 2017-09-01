package quickstart.mykka.route.dynamic

import quickstart.mykka.util.CompletableApp
import akka.actor.Props

case class InterestedIn(messageType: String)
case class NoLongerInterestedIn(messageType: String)

case class TypeAMessage(desc: String)
case class TypeBMessage(desc: String)
case class TypeCMessage(desc: String)
case class TypeDMessage(desc: String)


object DynamicRouter extends CompletableApp(5){
  val dunnoInterested = system.actorOf(Props[DunnoInterested],"dunnoInterested")
  //动态消息
  val typeMessageInterestRouter = system.actorOf(
      Props(new TypedMessageInterestRouter(dunnoInterested,4,1)),
      "typeMessageInterestRouter")
      
  val typeAInterest = system.actorOf(
      //在 new 的过程中 TypeAInterested 就会去注册自己感兴趣的消息类型
      Props(new TypeAInterested(typeMessageInterestRouter)), "typeAInterest")
  val typeBInterest = system.actorOf(
		  Props(new TypeBInterested(typeMessageInterestRouter)), "typeBInterest")
  val typeCInterest = system.actorOf(
		  Props(new TypeCInterested(typeMessageInterestRouter)), "typeCInterest")
  val typeCAlsoInterest = system.actorOf(
		  Props(new TypeCAlsoInterested(typeMessageInterestRouter)), "typeCAlsoInterest")
	
	//在动态路由分发消息之前，让Actor有足够的时间
  //去注册感兴趣的消息
	awaitCanStartNow()
	
	typeMessageInterestRouter ! TypeAMessage("Message of TypeA.")
	typeMessageInterestRouter ! TypeBMessage("Message of TypeB.")
	typeMessageInterestRouter ! TypeCMessage("Message of TypeC.")
	
	//在 TypeCInterested 对象取消注册，使次要接受者 TypeCAlsoInterested 
	//有时间取代主要接受者 TypeCInterested
	awaitCanCompleteNow()
	
	typeMessageInterestRouter ! TypeCMessage("Another Message of TypeC.")
	typeMessageInterestRouter ! TypeDMessage("Message of TypeD.")
 
	awaitCompletion()	  
	
	println("DynamicRouter：is completed")	  
}












