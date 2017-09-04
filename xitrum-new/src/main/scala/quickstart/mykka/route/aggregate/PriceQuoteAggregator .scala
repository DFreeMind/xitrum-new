package quickstart.mykka.route.aggregate

import akka.actor.Actor

class PriceQuoteAggregator extends Actor {
  val fulfilledPriceQuotes = scala.collection.mutable.Map[String, QuotationFulfillment]()
  
  def receive = {
    /**
     * 第四步：对商品报价单进行聚合
     */
    //处理MountaineeringSuppliesOrderProcessor发来的消息
    case required: RequiredPriceQuotesForFulfillment =>
      //sender中保存的是orderProcessor
      fulfilledPriceQuotes(required.rfqId) = QuotationFulfillment(required.rfqId, required.quotesRequested, Vector(), sender)
    
    //第七步：对订单进行聚合
    case priceQuoteFulFilled: PriceQuoteFulfilled =>
      val previousFulfillment = fulfilledPriceQuotes(priceQuoteFulFilled.priceQuote.rfqId)
      val currentPriceQuotes = previousFulfillment.priceQuotes :+ priceQuoteFulFilled.priceQuote
      val currentFulfillment =
        QuotationFulfillment(
            previousFulfillment.rfqId,
            previousFulfillment.quotesRequested,
            currentPriceQuotes,
            previousFulfillment.requester)

      if (currentPriceQuotes.size >= currentFulfillment.quotesRequested) {
        /**
         * 第八步：向orderProcessor发送当前满足的订单 currentFulfillment
         */
        currentFulfillment.requester ! currentFulfillment
        fulfilledPriceQuotes.remove(priceQuoteFulFilled.priceQuote.rfqId)
      } else {
    	  fulfilledPriceQuotes(priceQuoteFulFilled.priceQuote.rfqId) = currentFulfillment
      }
      
      println(s"PriceQuoteAggregator: fulfilled price quote: $priceQuoteFulFilled")
    case message: Any =>
      println(s"PriceQuoteAggregator: received unexpected message: $message")
  }
}
