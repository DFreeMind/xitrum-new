package quickstart.mykka.route.reseq

import akka.actor.Actor
import java.util.Random
import scala.concurrent._
import scala.concurrent.duration._
import java.util.Date
import akka.actor.ActorRef
import java.util.concurrent.TimeUnit
import ExecutionContext.Implicits.global

/**
 * 接收次序消息并将其打乱
 * ChaosRouter被赋予了ResequencerConsumer的引用
 */
class ChaosRouter(consumer: ActorRef) extends Actor {
  val random = new Random((new Date()).getTime)
  
  def receive = {
    //打乱消息的传递顺序
    case sequencedMessage: SequencedMessage =>
      val millis = random.nextInt(100) + 1
      println(s"ChaosRouter: delaying delivery of $sequencedMessage for $millis milliseconds")
      val duration = Duration.create(millis, TimeUnit.MILLISECONDS)
      //延迟随机时间，向ResequencerConsumer发送消息
      context.system.scheduler.scheduleOnce(duration, consumer, sequencedMessage)
      
    case message: Any =>
      println(s"ChaosRouter: received unexpected: $message")
  }
}
