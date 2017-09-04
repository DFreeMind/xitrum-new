package quickstart.mykka.route.reseq

import akka.actor.Props
import quickstart.mykka.util.CompletableApp

//一组相关联消息的关联ID、消息索引、这组消息的总个数
case class SequencedMessage(correlationId: String, index: Int, total: Int)

//派遣索引、序列消息
case class ResequencedMessages(dispatchableIndex: Int, sequencedMessages: Array[SequencedMessage]) {
  def advancedTo(dispatchableIndex: Int) = {
    ResequencedMessages(dispatchableIndex, sequencedMessages)
  }
}
/**
 * 重新定序器
 */
object ResequencerDriver extends CompletableApp(10) {
  val sequencedMessageConsumer = system.actorOf(Props[SequencedMessageConsumer], "sequencedMessageConsumer")
  val resequencerConsumer = system.actorOf(Props(classOf[ResequencerConsumer], sequencedMessageConsumer), "resequencerConsumer")
  val chaosRouter = system.actorOf(Props(classOf[ChaosRouter], resequencerConsumer), "chaosRouter")
  
  //向chaosRouter发送序列化消息
  for (index <- 1 to 5) chaosRouter ! SequencedMessage("ABC", index, 5)
  for (index <- 1 to 5) chaosRouter ! SequencedMessage("XYZ", index, 5)
  
  awaitCompletion
  println("Resequencer: is completed.")
}
