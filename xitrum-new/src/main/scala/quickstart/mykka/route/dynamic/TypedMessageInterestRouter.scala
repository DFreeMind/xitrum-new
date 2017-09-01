package quickstart.mykka.route.dynamic

import akka.actor.Actor
import akka.actor.ActorRef
import reflect.runtime.currentMirror
import scala.collection.mutable.Map

/**
 * 对InterestedIn和NoLongerInterestedIn两类消息
 * 进行处理
 * @param dunnoInterested 无法处理消息的引用
 * @param canStartAfterRegistered 启动时可以注册都少个Actor（第一注册者）
 * @param canCompleteAfterUnregistered
 */
class TypedMessageInterestRouter(
    dunnoInterested: ActorRef,    
    canStartAfterRegistered: Int,    
    canCompleteAfterUnregistered: Int) extends Actor {
  
  val interestRegister = Map[String, ActorRef]()
	val secondaryInterestRegister = Map[String, ActorRef]()
  
  def receive: Actor.Receive = {
    case interestedIn: InterestedIn => {
      //注册感兴趣的消息
      registerInterest(interestedIn)
    }
    case noLongerInterestedIn: NoLongerInterestedIn => {
      //取消对关注消息的注册
      unregisterInterest(noLongerInterestedIn)
    }
    case message: Any => {
      //其他类型消息，转交给sendFor处理
      sendFor(message)
    }
  }

  //注册
  def registerInterest(interestedIn: InterestedIn) = {
      val messageType = typeOfMessage(interestedIn.messageType)
      //判断当前消息类型是否在主要接收里面
      //若还没有则将其加入，若已经存在则加入到
      //次要接收队列里面
      if(!interestRegister.contains(messageType)){
        interestRegister(messageType) = sender
      }else{
        secondaryInterestRegister(messageType) = sender
      }
      
      //若已注册的Actor ≥ 设置的参数则启动动态路由分发
      if(interestRegister.size + 
          secondaryInterestRegister.size 
          >= canStartAfterRegistered){
        DynamicRouter.canStartNow()
      }
    }
  
  def sendFor(message: Any) = {
    val messageType = typeOfMessage(
      currentMirror.reflect(message).symbol.toString
    )
    if(interestRegister.contains(messageType)){
      interestRegister(messageType) forward message
    }else{
      dunnoInterested ! message
    }
  }

  //获取Message类型
  def typeOfMessage(rawMessageType: String): String = {
      rawMessageType.replace('$', ' ')
      .replace('.', ' ')
      .split(' ')
      .last.trim()
    }

  var unregisterCount: Int = 0
  def unregisterInterest(noLongerInterestedIn: NoLongerInterestedIn) = {
      val messageType = typeOfMessage(noLongerInterestedIn.messageType)
      
      //取消注册时，判断在次要接收队列中是否含有对此消息仍然感兴趣的
      //Actor，如果没有则将其取消注册即可；若次要接收队列中含有对当前消息
      //感兴趣的Actor，则需要取消注册的Actor在主队列中移除，然后对此消息
      //感兴趣的Actor在次要独立中移除并加入到主要接收队列中
      if(interestRegister.contains(messageType)){
        val wasInterested = interestRegister(messageType)
        if(wasInterested.compareTo(sender) == 0){
          if(secondaryInterestRegister.contains(messageType)){
            val nowInterested = secondaryInterestRegister.remove(messageType)
            interestRegister(messageType) = nowInterested.get
          }else{
            interestRegister.remove(messageType)
          }
          unregisterCount = unregisterCount + 1
          if(unregisterCount >= this.canCompleteAfterUnregistered){
            DynamicRouter.canCompleteNow()
          }
        }
      }
    }
}













