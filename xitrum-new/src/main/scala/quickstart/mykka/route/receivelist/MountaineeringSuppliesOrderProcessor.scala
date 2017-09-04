package quickstart.mykka.route.receivelist

import akka.actor.Actor
import akka.actor.ActorRef
/**
 * 体育用品供货商订单处理器
 */
class MountaineeringSuppliesOrderProcessor extends Actor {
  
  val interestRegistry = scala.collection.mutable.Map[String, PriceQuoteInterest]()
  
  def receive = {
    case interest: PriceQuoteInterest =>
      //供货商注册自己，告知将自己感兴趣的几个范围
      interestRegistry(interest.path) = interest
      
    case priceQuote: PriceQuote =>
      //打印供货商的报价
      println(s"OrderProcessor: received: $priceQuote")
      
    case rfq: RequestForQuotation =>
      //计算接收者列表
      val recipientList = calculateRecipientList(rfq)
      //
      dispatchTo(rfq, recipientList)
      
    case message: Any =>
      println(s"OrderProcessor: received unexpected message: $message")
  }
  
  //计算接收者列表
  //根据供货商注册的感兴趣的价格范围
  //匹配出再次范围内的供货商Actor
  def calculateRecipientList(rfq: RequestForQuotation): Iterable[ActorRef] = {
    for {
      interest <- interestRegistry.values
      if (rfq.totalRetailPrice >= interest.lowTotalRetail)
      if (rfq.totalRetailPrice <= interest.highTotalRetail)
    } yield interest.quoteProcessor
  }
  
  //对此报价感兴趣的供货商进行处理
  def dispatchTo(rfq: RequestForQuotation, recipientList: Iterable[ActorRef]) = {
    recipientList.foreach { recipient =>
      rfq.retailItems.foreach { retailItem =>
        println("OrderProcessor: " + rfq.rfqId + " item: " 
            + retailItem.itemId + " to: " + recipient.path.toString)
        //向此报价上请求报价单，供货商会给出折扣之后的价格
        recipient ! RequestPriceQuote(rfq.rfqId, retailItem.itemId, 
            retailItem.retailPrice, rfq.totalRetailPrice)
      }
    }
  }
}