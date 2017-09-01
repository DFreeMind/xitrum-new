package quickstart.mykka.route

import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorSystem

/**
 * 基于消息的路由器
 */
case class Order(id:String, orderType:String, 
    orderItems:Map[String,OrderItem]){
  //计算所有订单的总额
  val grandTotal:Double = orderItems.values.map(
     orderItem => orderItem.price   
   ).sum
   
   override def toString = {
    s"Order($id, $orderType, $orderItems, Totaing: $grandTotal)"
  }
}
case class OrderItem(id:String, itemType:String, 
    description:String, price:Double){
   override def toString = {
    s"Order($id, $itemType, description, $price)"
  }
}

case class OrderPlaced(order:Order)

class SystemA extends Actor {
  def receive: Actor.Receive = {
    case OrderPlaced(order) => {
      println(s"SystemA : Handling $order")
    }
    case _ => 
      println(s"SystemA: Unexcepted message")
  }
}
class SystemX extends Actor {
	def receive: Actor.Receive = {
  	case OrderPlaced(order) => {
  		println(s"SystemX : Handling $order")
  	}
  	case _ => 
  	println(s"SystemX: Unexcepted message")
  }
}

class OrderRouter extends Actor {
  val systemA = context.actorOf(Props[SystemA], "SystemA")
	val systemX = context.actorOf(Props[SystemX], "systemX")
  def receive: Actor.Receive = {
    //匹配消息进行过滤
    case orderPlaced: OrderPlaced => {
      orderPlaced.order.orderType match {
        //A类订单转给systemA进行处理
        case "TypeABC" => {
          println(s"OrderRouter: routing $orderPlaced")
          systemA ! orderPlaced
        }
        //X类订单转给systemX处理
        case "TypeXYZ" => {
          println(s"OrderRouter: routing $orderPlaced")
          systemX ! orderPlaced
        }
      }
    }
  }
}
class ContentRouter {}
object ContentRouter{
  def main(args: Array[String]): Unit = {
    val actorSystem = ActorSystem("OrderRouterSystem")
    val orderRouter = actorSystem.actorOf(Props[OrderRouter], "OrderRouter")
    val item1 = OrderItem("1", "TypeABC.1", "an item of type ABC.1", 29.95)
    val item2 = OrderItem("2", "TypeABC.2", "an item of type ABC.2", 99.95)
    val item3 = OrderItem("3", "TypeABC.3", "an item of type ABC.3", 14.95)
    val orderTypeA = Map(
      item1.itemType -> item1,
      item2.itemType -> item2,
      item3.itemType -> item3
    )
    //通过路由发送A订单
    orderRouter ! OrderPlaced(Order("123", "TypeABC", orderTypeA))
    
    val item4 = OrderItem("4", "TypeXYZ.4", "an item of type XYZ.4", 29.95)
    val item5 = OrderItem("5", "TypeXYZ.5", "an item of type XYZ.5", 99.95)
    val item6 = OrderItem("6", "TypeXYZ.6", "an item of type XYZ.6", 14.95)
    val item7 = OrderItem("7", "TypeXYZ.7", "an item of type XYZ.7", 14.95)
    
    val orderTypeX = Map(
      item4.itemType -> item4,
      item5.itemType -> item5,
      item6.itemType -> item6,
      item7.itemType -> item7
    )
    //通过路由发送X订单
    orderRouter ! OrderPlaced(Order("4567", "TypeXYZ", orderTypeX))
    
    actorSystem.terminate()
  }
}










