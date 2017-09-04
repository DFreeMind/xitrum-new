package quickstart.mykka.route.aggregate

import akka.actor.Actor
import akka.actor.ActorRef

//比前面的示例多了一个消息聚合器Actor引用的参数
class MountaineeringSuppliesOrderProcessor(priceQuoteAggregator: ActorRef) extends Actor {
  val interestRegistry = scala.collection.mutable.Map[String, PriceQuoteInterest]()

  def calculateRecipientList(rfq: RequestForQuotation): Iterable[ActorRef] = {
    for {
      interest <- interestRegistry.values
      if (rfq.totalRetailPrice >= interest.lowTotalRetail)
      if (rfq.totalRetailPrice <= interest.highTotalRetail)
    } yield interest.quoteProcessor
  }
  
  def dispatchTo(rfq: RequestForQuotation, recipientList: Iterable[ActorRef]) = {
    var totalRequestedQuotes = 0
    recipientList.foreach { recipient =>
      rfq.retailItems.foreach { retailItem =>
        println("OrderProcessor: " + rfq.rfqId + " item: " + retailItem.itemId + " to: " + recipient.path.toString)
        /**
         * 第五步：处理接受者列表，向接收者列表发送消息，请求价格报价
         * 接受者属于PriceQuotes类
         */
        recipient ! RequestPriceQuote(rfq.rfqId, retailItem.itemId, retailItem.retailPrice, rfq.totalRetailPrice)
      }
    }
  }
  
  def receive = {
    //供货商发来自己接收和查询的价格范围
    case interest: PriceQuoteInterest =>
      interestRegistry(interest.quoterId) = interest
    
    //第六步：处理供货商返回的价格报价
    case priceQuote: PriceQuote =>
      /**
       * 第七步：向订单聚合器发送订单聚合消息。
       */
      priceQuoteAggregator ! PriceQuoteFulfilled(priceQuote)
      println(s"OrderProcessor: received: $priceQuote")
    
     /**
      * 第三步：处理报价请求  
      */
    case rfq: RequestForQuotation =>
      //返回在查询范围内的接收者列表
      val recipientList = calculateRecipientList(rfq)
      
      //商品报价聚合器
      /**
       * 第四步：对商品报价单进行聚合
       */
      priceQuoteAggregator ! RequiredPriceQuotesForFulfillment(rfq.rfqId, recipientList.size * rfq.retailItems.size)
      
      dispatchTo(rfq, recipientList)
      
    case fulfillment: QuotationFulfillment =>
      println(s"OrderProcessor: received: $fulfillment")
      AggregatorDriver.completedStep()
    case message: Any =>
      println(s"OrderProcessor: received unexpected message: $message")
  }
}

