package quickstart.mykka.route

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.ActorRef

//只处理自己可以处理的信息
//内容过滤直接硬编码到业务Actor中
//不利用扩展过滤的规则，不如使用独立的Actor进行过滤
class FilterSystemA extends Actor {
  def receive: Actor.Receive = {
    case OrderPlaced(order) if(order.orderType == "TypeABC") => {
      println(s"FilterSystemA : Handling $order")
    }
    case other => 
      println(s"FilterSystemA: filtering $other")
  }
}
class FilterSystemX extends Actor {
	def receive: Actor.Receive = {
	  //处理自己可以处理的数据，由代理来判断哪些需要自己处理
  	case OrderPlaced(order) => {
  		println(s"FilterSystemX : Handling $order")
  	}
  	case other => 
  	  println(s"FilterSystemX: filtering $other")
  }
}

//FilterSystemX 的代理对象，会持有 FilterSystemX 的引用
//当需要 FilterSystemX 处理数据时，将数据转发给FilterSystemX
//处理。
//使用此方式可以更加灵活的更换规则，只需要维护消息过滤器Actor即可，
//唯一的缺点是增加了系统的开销，但这些开销都是极小的
class ActualFilterSystemX(filterSystemX: ActorRef) extends Actor {
  def receive: Actor.Receive = {
    case orderPlaced: OrderPlaced 
      if(orderPlaced.order.orderType == "TypeXYZ") => {
      //将数据转发到FilterSystemX
      filterSystemX forward orderPlaced
    }
    case other => {
       println(s"ActualFilterSystemX: filtering $other")
    }
  }
}

class ContentFilterRouter {}

object ContentFilterRouter{
  def main(args: Array[String]): Unit = {
    val actorSystem = ActorSystem("OrderFilterRouterSystem")
    val systemA = actorSystem.actorOf(Props[FilterSystemA], "SystemA")
    //用于传入代理Actor对象中
    val ActualSystemX = actorSystem.actorOf(Props[FilterSystemX], "SystemX")
    val systemX = actorSystem.actorOf(Props(
        new ActualFilterSystemX(ActualSystemX)),"ActualFilterSystem")
        
    val item1 = OrderItem("1", "TypeABC.1", "an item of type ABC.1", 29.95)
    val item2 = OrderItem("2", "TypeABC.2", "an item of type ABC.2", 99.95)
    val item3 = OrderItem("3", "TypeABC.3", "an item of type ABC.3", 14.95)
    val orderTypeA = Map(
      item1.itemType -> item1,
      item2.itemType -> item2,
      item3.itemType -> item3
    )
    //通过路由发送A订单
    systemA ! OrderPlaced(Order("123", "TypeABC", orderTypeA))
    systemX ! OrderPlaced(Order("123", "TypeABC", orderTypeA))
    
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
    systemA ! OrderPlaced(Order("4567", "TypeXYZ", orderTypeX))
    systemX ! OrderPlaced(Order("4567", "TypeXYZ", orderTypeX))

    actorSystem.terminate()
  }
}







