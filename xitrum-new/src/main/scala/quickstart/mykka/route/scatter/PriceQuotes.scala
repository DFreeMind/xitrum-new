package quickstart.mykka.route.scatter

import akka.actor.Actor
import akka.actor.ActorRef

class BudgetHikersPriceQuotes(priceQuoteRequestPublisher: ActorRef) extends Actor {
  val quoterId = self.path.name
  priceQuoteRequestPublisher ! SubscribeToPriceQuoteRequests(quoterId, self)
  
  def receive = {
    case rpq: RequestPriceQuote =>
      if (rpq.orderTotalRetailPrice < 1000.00) {
        val discount = discountPercentage(rpq.orderTotalRetailPrice) * rpq.retailPrice
        sender ! PriceQuote(quoterId, rpq.rfqId, rpq.itemId, rpq.retailPrice, rpq.retailPrice - discount)
      } else {
        println(s"BudgetHikersPriceQuotes: ignoring: $rpq")
      }

    case message: Any =>
      println(s"BudgetHikersPriceQuotes: received unexpected message: $message")
  }
  
  def discountPercentage(orderTotalRetailPrice: Double) = {
    if (orderTotalRetailPrice <= 100.00) 0.02
    else if (orderTotalRetailPrice <= 399.99) 0.03
    else if (orderTotalRetailPrice <= 499.99) 0.05
    else if (orderTotalRetailPrice <= 799.99) 0.07
    else 0.075
  }
}

class HighSierraPriceQuotes(priceQuoteRequestPublisher: ActorRef) extends Actor {
  val quoterId = self.path.name
  priceQuoteRequestPublisher ! SubscribeToPriceQuoteRequests(quoterId, self)
  
  def receive = {
    case rpq: RequestPriceQuote =>
      val discount = discountPercentage(rpq.orderTotalRetailPrice) * rpq.retailPrice
      sender ! PriceQuote(quoterId, rpq.rfqId, rpq.itemId, rpq.retailPrice, rpq.retailPrice - discount)

    case message: Any =>
      println(s"HighSierraPriceQuotes: received unexpected message: $message")
  }
  
  def discountPercentage(orderTotalRetailPrice: Double): Double = {
    if (orderTotalRetailPrice <= 150.00) 0.015
    else if (orderTotalRetailPrice <= 499.99) 0.02
    else if (orderTotalRetailPrice <= 999.99) 0.03
    else if (orderTotalRetailPrice <= 4999.99) 0.04
    else 0.05
  }
}

class MountainAscentPriceQuotes(priceQuoteRequestPublisher: ActorRef) extends Actor {
  val quoterId = self.path.name
  priceQuoteRequestPublisher ! SubscribeToPriceQuoteRequests(quoterId, self)
  
  def receive = {
    case rpq: RequestPriceQuote =>
      val discount = discountPercentage(rpq.orderTotalRetailPrice) * rpq.retailPrice
      sender ! PriceQuote(quoterId, rpq.rfqId, rpq.itemId, rpq.retailPrice, rpq.retailPrice - discount)

    case message: Any =>
      println(s"MountainAscentPriceQuotes: received unexpected message: $message")
  }
  
  def discountPercentage(orderTotalRetailPrice: Double) = {
    if (orderTotalRetailPrice <= 99.99) 0.01
    else if (orderTotalRetailPrice <= 199.99) 0.02
    else if (orderTotalRetailPrice <= 499.99) 0.03
    else if (orderTotalRetailPrice <= 799.99) 0.04
    else if (orderTotalRetailPrice <= 999.99) 0.045
    else if (orderTotalRetailPrice <= 2999.99) 0.0475
    else 0.05
  }
}

class PinnacleGearPriceQuotes(priceQuoteRequestPublisher: ActorRef) extends Actor {
  val quoterId = self.path.name
  priceQuoteRequestPublisher ! SubscribeToPriceQuoteRequests(quoterId, self)
  
  def receive = {
    case rpq: RequestPriceQuote =>
      val discount = discountPercentage(rpq.orderTotalRetailPrice) * rpq.retailPrice
      sender ! PriceQuote(quoterId, rpq.rfqId, rpq.itemId, rpq.retailPrice, rpq.retailPrice - discount)

    case message: Any =>
      println(s"PinnacleGearPriceQuotes: received unexpected message: $message")
  }
  
  def discountPercentage(orderTotalRetailPrice: Double) = {
    if (orderTotalRetailPrice <= 299.99) 0.015
    else if (orderTotalRetailPrice <= 399.99) 0.0175
    else if (orderTotalRetailPrice <= 499.99) 0.02
    else if (orderTotalRetailPrice <= 999.99) 0.03
    else if (orderTotalRetailPrice <= 1199.99) 0.035
    else if (orderTotalRetailPrice <= 4999.99) 0.04
    else if (orderTotalRetailPrice <= 7999.99) 0.05
    else 0.06
  }
}

class RockBottomOuterwearPriceQuotes(priceQuoteRequestPublisher: ActorRef) extends Actor {
  val quoterId = self.path.name
  priceQuoteRequestPublisher ! SubscribeToPriceQuoteRequests(quoterId, self)
  
  def receive = {
    case rpq: RequestPriceQuote =>
      if (rpq.orderTotalRetailPrice < 2000.00) {
        val discount = discountPercentage(rpq.orderTotalRetailPrice) * rpq.retailPrice
        sender ! PriceQuote(quoterId, rpq.rfqId, rpq.itemId, rpq.retailPrice, rpq.retailPrice - discount)
      } else {
        println(s"RockBottomOuterwearPriceQuotes: ignoring: $rpq")
      }

    case message: Any =>
      println(s"RockBottomOuterwearPriceQuotes: received unexpected message: $message")
  }
  
  def discountPercentage(orderTotalRetailPrice: Double) = {
    if (orderTotalRetailPrice <= 100.00) 0.015
    else if (orderTotalRetailPrice <= 399.99) 0.02
    else if (orderTotalRetailPrice <= 499.99) 0.03
    else if (orderTotalRetailPrice <= 799.99) 0.04
    else if (orderTotalRetailPrice <= 999.99) 0.05
    else if (orderTotalRetailPrice <= 2999.99) 0.06
    else if (orderTotalRetailPrice <= 4999.99) 0.07
    else if (orderTotalRetailPrice <= 5999.99) 0.075
    else 0.08
  }
}