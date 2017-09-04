package quickstart.mykka.route.splitter

import akka.actor.Props
import quickstart.mykka.util.CompletableApp

/**
 * 分离器
 * <p>分离器主要用于将较大的消息分割成多个独立的部分，并将这些独立的部分
 * <p>作为消息发送。分离器与基于内容的路由器有相似的地方，因为发送各个
 * <p>独立部分方式是由各个独立部分的内容决定。但内容路由器主要用于根据消息
 * <p>的类型将整个消息传输给指定的子系统。分离器是将构成一条消息的各个独立部分
 * <p>分发给不同的子系统。
 */
class Splitter {}

//订单对象，包含多个商品
case class Order(orderItems: Map[String, OrderItem]) {
  // val grandTotal: Double = orderItems.values.map(orderItem => orderItem.price).sum
  // val grandTotal: Double = orderItems.values.foldLeft(0.0)((
  //  grandTotal: Double, orderItem: OrderItem) => grandTotal + orderItem.price)
  val grandTotal: Double = orderItems.values.map(_.price).sum
    
  override def toString = {
    s"Order(Order Items: $orderItems Totaling: $grandTotal)"
  }
}

//商品
case class OrderItem(id: String, itemType: String, description: String, price: Double) {
  override def toString = {
    s"OrderItem($id, $itemType, '$description', $price)"
  }
}
//订单分配
case class OrderPlaced(order: Order)

//订单类型
case class TypeAItemOrdered(orderItem: OrderItem)
case class TypeBItemOrdered(orderItem: OrderItem)
case class TypeCItemOrdered(orderItem: OrderItem)

object SplitterDriver extends CompletableApp(4) {
  //拿到订单分离器OrderRouter的引用
  val orderRouter = system.actorOf(Props[OrderRouter], "orderRouter")
  //创建三个商品
  val orderItem1 = OrderItem("1", "TypeA", "An item of type A.", 23.95)
  val orderItem2 = OrderItem("2", "TypeB", "An item of type B.", 99.95)
  val orderItem3 = OrderItem("3", "TypeC", "An item of type C.", 14.95)
  
  val orderItems = Map(orderItem1.itemType -> orderItem1, 
                        orderItem2.itemType -> orderItem2,
                        orderItem3.itemType -> orderItem3)
  //向订单分离器发送数据
  orderRouter ! OrderPlaced(Order(orderItems))
  awaitCompletion
  println("Splitter: is completed.")
}


