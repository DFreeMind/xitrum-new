package quickstart.mykka.route.scatter

import akka.actor.Actor
import akka.actor.ActorRef

/**
 * 订单处理器
 */
class MountaineeringSuppliesOrderProcessor(priceQuoteAggregator: ActorRef) extends Actor {
  val subscribers = scala.collection.mutable.Map[String, SubscribeToPriceQuoteRequests]()

  def dispatch(rfq: RequestForQuotation) = {
    subscribers.values.foreach { subscriber =>
      val quoteProcessor = subscriber.quoteProcessor
      rfq.retailItems.foreach { retailItem =>
        println("OrderProcessor: " + rfq.rfqId + " item: " + retailItem.itemId + " to: " + subscriber.quoterId)
        quoteProcessor ! RequestPriceQuote(rfq.rfqId, retailItem.itemId, retailItem.retailPrice, rfq.totalRetailPrice)
      }
    }
  }
  
  def receive = {
    case subscriber: SubscribeToPriceQuoteRequests =>
      subscribers(subscriber.quoterId) = subscriber
    case priceQuote: PriceQuote =>
      priceQuoteAggregator ! PriceQuoteFulfilled(priceQuote)
      println(s"OrderProcessor: received: $priceQuote")
    case rfq: RequestForQuotation =>
      priceQuoteAggregator ! RequiredPriceQuotesForFulfillment(rfq.rfqId, subscribers.size * rfq.retailItems.size)
      dispatch(rfq)
    case bestPriceQuotation: BestPriceQuotation =>
      println(s"OrderProcessor: received: $bestPriceQuotation")
      ScatterGatherDriver.completedStep()
    case message: Any =>
      println(s"OrderProcessor: received unexpected message: $message")
  }
}
