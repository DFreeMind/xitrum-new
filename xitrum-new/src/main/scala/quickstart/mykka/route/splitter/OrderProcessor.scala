package quickstart.mykka.route.splitter

import akka.actor.Actor

/**
 * 三种订单处理器A、B、C负责处理不同的消息
 */
class OrderProcessor {}

class OrderItemTypeAProcessor extends Actor {
  def receive = {
    case TypeAItemOrdered(orderItem) =>
      println(s"OrderItemTypeAProcessor: handling $orderItem")
      SplitterDriver.completedStep()
    case _ =>
      println("OrderItemTypeAProcessor: received unexpected message")
  }
}

class OrderItemTypeBProcessor extends Actor {
  def receive = {
    case TypeBItemOrdered(orderItem) =>
      println(s"OrderItemTypeBProcessor: handling $orderItem")
      SplitterDriver.completedStep()
    case _ =>
      println("OrderItemTypeBProcessor: received unexpected message")
  }
}

class OrderItemTypeCProcessor extends Actor {
  def receive = {
    case TypeCItemOrdered(orderItem) =>
      println(s"OrderItemTypeCProcessor: handling $orderItem")
      SplitterDriver.completedStep()
    case _ =>
      println("OrderItemTypeCProcessor: received unexpected message")
  }
}