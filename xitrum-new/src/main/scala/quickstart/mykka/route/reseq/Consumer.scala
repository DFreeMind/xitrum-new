package quickstart.mykka.route.reseq

import akka.actor.Actor
import akka.actor.ActorRef

class Consumer {}

/**
 * 接收随机消息并将其重新排序
 */
class ResequencerConsumer(actualConsumer: ActorRef) extends Actor {
  val resequenced = scala.collection.mutable.Map[String, ResequencedMessages]()

  def dispatchAllSequenced(correlationId: String) = {
    val resequencedMessages = resequenced(correlationId)
    var dispatchableIndex = resequencedMessages.dispatchableIndex
    
    resequencedMessages.sequencedMessages.foreach { sequencedMessage =>
      if (sequencedMessage.index == dispatchableIndex) {
        //将消息发送给 sequencedMessageConsumer
        actualConsumer ! sequencedMessage
        dispatchableIndex += 1
      }
    }
    
    //使用新的dispatchableIndex更新
    resequenced(correlationId) =
      resequencedMessages.advancedTo(dispatchableIndex)
  }
  
  def receive = {
    //处理接收的SequencedMessage消息（非有序）
    case unsequencedMessage: SequencedMessage =>
      println(s"ResequencerConsumer: received: $unsequencedMessage")
      resequence(unsequencedMessage)
      dispatchAllSequenced(unsequencedMessage.correlationId)
      removeCompleted(unsequencedMessage.correlationId)
    case message: Any =>
      println(s"ResequencerConsumer: received unexpected: $message")
  }
  
  def removeCompleted(correlationId: String) = {
    val resequencedMessages = resequenced(correlationId)
    
    if (resequencedMessages.dispatchableIndex > resequencedMessages.sequencedMessages(0).total) {
      resequenced.remove(correlationId)
      println(s"ResequencerConsumer: removed completed: $correlationId")
    }
  }
  
  def dummySequencedMessages(count: Int): Seq[SequencedMessage] = {
    for {
      index <- 1 to count
    } yield {
      SequencedMessage("", -1, count)
    }
  }
  
  //对无序消息进行排序
  def resequence(sequencedMessage: SequencedMessage) = {
    if (!resequenced.contains(sequencedMessage.correlationId)) {
      resequenced(sequencedMessage.correlationId) =
        ResequencedMessages(1, dummySequencedMessages(sequencedMessage.total).toArray)
    }
    
    resequenced(sequencedMessage.correlationId)
    	.sequencedMessages
    	.update(sequencedMessage.index - 1, sequencedMessage)
    println("resequenced save message：" + resequenced)
  }
}

/**
 * 最终的消息接收者
 */
class SequencedMessageConsumer extends Actor {
  def receive = {
    case sequencedMessage: SequencedMessage =>
      println(s"SequencedMessageConsumer: received: $sequencedMessage")
      ResequencerDriver.completedStep()
    case message: Any =>
      println(s"SequencedMessageConsumer: received unexpected: $message")
  }
}