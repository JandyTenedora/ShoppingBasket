package com.adthena.shoppingbasket.pricing.discount

import akka.actor.typed.{ActorSystem, Scheduler}
import akka.actor.typed.scaladsl.AskPattern.Askable
import com.adthena.shoppingbasket.actors.ItemDiscountActor
import com.adthena.shoppingbasket.models.Item
import com.adthena.shoppingbasket.pricing.PricingEngine.BasketDiscount
import com.adthena.shoppingbasket.util.CurrencyUtil

import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.concurrent.duration._

/**
 * The `CustomDiscountProvider` class extends the `DefaultDiscountProvider` class
 * and provides an additional custom discount to the predefined discounts.
 *
 * This class serves as an example to the extensibility of this shopping basket application,
 * and demonstrates how to add custom discounts
 */
class CustomDiscountProvider(implicit actorSystem: ActorSystem[_]) extends DefaultDiscountProvider {
  override def getDiscounts: List[BasketDiscount] = List(
    appleDiscounts,
    buyTwoTinsGetLoafHalfPrice,
    applesFlatDrop
  )

  // Example discount: All apples are a flat 5p off
  private[pricing] val applesFlatDrop: BasketDiscount = basket => {
    val apples5cLessDiscountFunction: Item => Item = {
      case item @ Item("Apples", currentPrice) =>
        val discountFunction: BigDecimal => BigDecimal = (x: BigDecimal) => (x - 0.05) max 0
        item.copy(price = discountFunction(currentPrice))
      case other => other
    }

    val itemDiscountActor = createDiscountActor(apples5cLessDiscountFunction)

    val futureDiscountedItems: Future[List[Item]] = Future.sequence {
      basket.items.map { item =>
        itemDiscountActor.ask(ItemDiscountActor.ApplyItemDiscount(item, _))
      }
    }

    val discountedItems = Await.result(futureDiscountedItems, 3.seconds)
    val discountedBasket = basket.copy(items = discountedItems)
    val discountAmount = basket.calculatePrice - discountedBasket.calculatePrice
    if (discountAmount != 0) println(s"All Apples 5p off: ${CurrencyUtil.formatCurrency(discountAmount)}")
    discountedBasket
  }
}