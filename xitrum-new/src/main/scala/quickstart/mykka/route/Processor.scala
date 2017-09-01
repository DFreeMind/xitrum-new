package quickstart.mykka.route

import akka.actor.Actor

class Processor extends Actor {
  def receive: Actor.Receive = {
    case message: Any => {
      println(s"Processor: ${self.path.name} receive $message")
    }
  }
}