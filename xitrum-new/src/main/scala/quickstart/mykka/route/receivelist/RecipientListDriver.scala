package quickstart.mykka.route.receivelist

import akka.actor.Props
import quickstart.mykka.util.CompletableApp
import akka.actor.ActorRef

//通过提供价格范围获取具体商品报价的请求
case class RequestForQuotation(rfqId: String, retailItems: Seq[RetailItem]) {
  val totalRetailPrice: Double = retailItems.map(retailItem => retailItem.retailPrice).sum
}
//零售条目
case class RetailItem(itemId: String, retailPrice: Double)
//感兴趣的价格范围
case class PriceQuoteInterest(path: String, quoteProcessor: ActorRef, lowTotalRetail: Double, highTotalRetail: Double)
//请求价格报价（零售价格、订单总的零售价格）
case class RequestPriceQuote(rfqId: String, itemId: String, retailPrice: Double, orderTotalRetailPrice: Double)
//价格报价（零售价格、折扣价格）
case class PriceQuote(rfqId: String, itemId: String, retailPrice: Double, discountPrice: Double)

object RecipientListDriver extends CompletableApp(5) {
  val orderProcessor = system.actorOf(Props[MountaineeringSuppliesOrderProcessor], "orderProcessor")

  //创建不同供货商的报价引擎
  system.actorOf(Props(classOf[BudgetHikersPriceQuotes], orderProcessor), "budgetHikers")
  system.actorOf(Props(classOf[HighSierraPriceQuotes], orderProcessor), "highSierra")
  system.actorOf(Props(classOf[MountainAscentPriceQuotes], orderProcessor), "mountainAscent")
  system.actorOf(Props(classOf[PinnacleGearPriceQuotes], orderProcessor), "pinnacleGear")
  system.actorOf(Props(classOf[RockBottomOuterwearPriceQuotes], orderProcessor), "rockBottomOuterwear")

  //请求报价
  orderProcessor ! RequestForQuotation("123",
      Vector(RetailItem("1", 29.95),
             RetailItem("2", 99.95),
             RetailItem("3", 14.95)))

  orderProcessor ! RequestForQuotation("125",
      Vector(RetailItem("4", 39.99),
             RetailItem("5", 199.95),
             RetailItem("6", 149.95),
             RetailItem("7", 724.99)))

  orderProcessor ! RequestForQuotation("129",
      Vector(RetailItem("8", 119.99),
             RetailItem("9", 499.95),
             RetailItem("10", 519.00),
             RetailItem("11", 209.50)))

  orderProcessor ! RequestForQuotation("135",
      Vector(RetailItem("12", 0.97),
             RetailItem("13", 9.50),
             RetailItem("14", 1.99)))

  orderProcessor ! RequestForQuotation("140",
      Vector(RetailItem("15", 107.50),
             RetailItem("16", 9.50),
             RetailItem("17", 599.99),
             RetailItem("18", 249.95),
             RetailItem("19", 789.99)))
}







