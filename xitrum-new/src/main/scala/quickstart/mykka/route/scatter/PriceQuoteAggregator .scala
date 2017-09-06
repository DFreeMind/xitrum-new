package quickstart.mykka.route.scatter

import akka.actor.Actor
import scala.concurrent._
import scala.concurrent.duration._
import ExecutionContext.Implicits.global
import java.util.concurrent.TimeUnit

/**
 * 消息聚集处理
 */
class PriceQuoteAggregator extends Actor {
  val fulfilledPriceQuotes = scala.collection.mutable.Map[String, QuotationFulfillment]()

  def bestPriceQuotationFrom(quotationFulfillment: QuotationFulfillment): BestPriceQuotation = {
    val bestPrices = scala.collection.mutable.Map[String, PriceQuote]()
    
    quotationFulfillment.priceQuotes.foreach { priceQuote =>
      if (bestPrices.contains(priceQuote.itemId)) {
        if (bestPrices(priceQuote.itemId).discountPrice > priceQuote.discountPrice) {
          bestPrices(priceQuote.itemId) = priceQuote
        }
      } else {
        bestPrices(priceQuote.itemId) = priceQuote
      }
    }
    
    BestPriceQuotation(quotationFulfillment.rfqId, bestPrices.values.toVector)
  }
  
  def receive = {
    case required: RequiredPriceQuotesForFulfillment =>
      fulfilledPriceQuotes(required.rfqId) = QuotationFulfillment(required.rfqId, required.quotesRequested, Vector(), sender)
      val duration = Duration.create(2, TimeUnit.SECONDS)
      context.system.scheduler.scheduleOnce(duration, self, PriceQuoteTimedOut(required.rfqId))
    case priceQuoteFulfilled: PriceQuoteFulfilled =>
      priceQuoteRequestFulfilled(priceQuoteFulfilled)
      println(s"PriceQuoteAggregator: fulfilled price quote: $PriceQuoteFulfilled")
    case priceQuoteTimedOut: PriceQuoteTimedOut =>
      priceQuoteRequestTimedOut(priceQuoteTimedOut.rfqId)
    case message: Any =>
      println(s"PriceQuoteAggregator: received unexpected message: $message")
  }
  
  def priceQuoteRequestFulfilled(priceQuoteFulfilled: PriceQuoteFulfilled) = {
    if (fulfilledPriceQuotes.contains(priceQuoteFulfilled.priceQuote.rfqId)) {
      val previousFulfillment = fulfilledPriceQuotes(priceQuoteFulfilled.priceQuote.rfqId)
      val currentPriceQuotes = previousFulfillment.priceQuotes :+ priceQuoteFulfilled.priceQuote
      val currentFulfillment =
          QuotationFulfillment(
              previousFulfillment.rfqId,
              previousFulfillment.quotesRequested,
              currentPriceQuotes,
              previousFulfillment.requester)

      if (currentPriceQuotes.size >= currentFulfillment.quotesRequested) {
        quoteBestPrice(currentFulfillment)
      } else {
        fulfilledPriceQuotes(priceQuoteFulfilled.priceQuote.rfqId) = currentFulfillment
      }
    }
  }

  def priceQuoteRequestTimedOut(rfqId: String) = {
    if (fulfilledPriceQuotes.contains(rfqId)) {
      quoteBestPrice(fulfilledPriceQuotes(rfqId))
    }
  }
  
  def quoteBestPrice(quotationFulfillment: QuotationFulfillment) = {
    if (fulfilledPriceQuotes.contains(quotationFulfillment.rfqId)) {
      quotationFulfillment.requester ! bestPriceQuotationFrom(quotationFulfillment)
      fulfilledPriceQuotes.remove(quotationFulfillment.rfqId)
    }
  }
}



