package quickstart.mykka.route.splitter

import akka.actor.Props
import akka.actor.Actor

//订单分离器，收到OrderPlaced之后，迭代每一个OrderItem
class OrderRouter extends Actor {
  val orderItemTypeAProcessor = context.actorOf(Props[OrderItemTypeAProcessor], "orderItemTypeAProcessor")
  val orderItemTypeBProcessor = context.actorOf(Props[OrderItemTypeBProcessor], "orderItemTypeBProcessor")
  val orderItemTypeCProcessor = context.actorOf(Props[OrderItemTypeCProcessor], "orderItemTypeCProcessor")

  def receive = {
    case OrderPlaced(order) =>
      println(order)
      //将订单分离成不同的部分（A、B、C三类），交由不同的处理器（订单子系统）进行处理
      order.orderItems foreach { case (itemType, orderItem) => itemType match {
        case "TypeA" =>
          println(s"OrderRouter: routing $itemType")
          //OrderItem被封装成新的消息
          orderItemTypeAProcessor ! TypeAItemOrdered(orderItem)
        case "TypeB" =>
          println(s"OrderRouter: routing $itemType")
          orderItemTypeBProcessor ! TypeBItemOrdered(orderItem)
        case "TypeC" =>
          println(s"OrderRouter: routing $itemType")
          orderItemTypeCProcessor ! TypeCItemOrdered(orderItem)
      }}
      
      SplitterDriver.completedStep()
    case _ =>
      println("OrderRouter: received unexpected message")
  }
}

