package com.adthena.shoppingbasket.pricing.discount

import akka.actor.typed.{ActorSystem, Scheduler}
import akka.actor.typed.scaladsl.AskPattern.Askable
import com.adthena.shoppingbasket.actors.ItemDiscountActor
import com.adthena.shoppingbasket.models.{Basket, Item}
import com.adthena.shoppingbasket.pricing.PricingEngine
import com.adthena.shoppingbasket.util.CurrencyUtil

import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}
import scala.concurrent.duration._

/**
 * The `DefaultDiscountProvider` class provides a set of predefined discounts
 * that can be applied to a shopping basket. It implements the `DiscountProvider`
 * interface and defines three types of discounts:
 *
 * 1. `appleDiscounts`: Applies a 10% discount to all apples in the basket.
 * 2. `buyTwoTinsGetLoafHalfPrice`: For every two tins of soup, applies a 50% discount to one loaf of bread.
 * 3. `applesFlatDrop`: Applies a flat 5p discount to all apples in the basket.
 *
 * These discounts are applied in the order they are defined in the getDiscounts method.
 *
 * DefaultDiscountProvider represents the discounts listed in Adthena's Current Special Offers.
 */


class DefaultDiscountProvider(implicit actorSystem: ActorSystem[_]) extends DiscountProvider {
  override implicit val system: ActorSystem[_] = actorSystem
  override implicit val scheduler: Scheduler = system.scheduler
  override implicit val ec: ExecutionContextExecutor = system.executionContext

  import PricingEngine.BasketDiscount

  override def getDiscounts: List[BasketDiscount] = List(
    appleDiscounts,
    buyTwoTinsGetLoafHalfPrice
  )

  private[pricing] val appleDiscounts: BasketDiscount = basket => {
    val appleDiscountFunction: Item => Item = {
      case item @ Item("Apples", currentPrice) =>
        val discountFunction: BigDecimal => BigDecimal = (x: BigDecimal) => x * 0.9
        item.copy(price = discountFunction(currentPrice))
      case other => other
    }

    val itemDiscountActor = createDiscountActor(appleDiscountFunction)

    val futureDiscountedItems: Future[List[Item]] = Future.sequence {
      basket.items.map { item =>
        itemDiscountActor.ask(ItemDiscountActor.ApplyItemDiscount(item, _))
      }
    }

    val discountedItems = Await.result(futureDiscountedItems, 3.seconds)
    val discountedBasket = basket.copy(items = discountedItems)
    val discountAmount = CurrencyUtil.formatCurrency(basket.calculatePrice - discountedBasket.calculatePrice)
    println(s"Apples 10% off: $discountAmount")
    discountedBasket
  }

  private[pricing] val buyTwoTinsGetLoafHalfPrice: BasketDiscount = basket => {
    val tinCount = basket.items.count(_.name == "Soup")
    val loafBonus = tinCount / 2

    val itemDiscountFunction: Item => Item = {
      case item @ Item("Bread", currentPrice) =>
        val twoTinsLoafHalfPriceDiscountFunction: BigDecimal => BigDecimal = (x: BigDecimal) => x * 0.5
        item.copy(price = twoTinsLoafHalfPriceDiscountFunction(currentPrice))
      case other => other
    }

    val itemDiscountActor = createDiscountActor(itemDiscountFunction)

    val futureDiscountedItems: Future[List[Item]] = {
      val (breadItems, otherItems) = basket.items.partition(_.name == "Bread")
      val discountedBreadItems = breadItems.take(loafBonus).map { item =>
        itemDiscountActor.ask(ItemDiscountActor.ApplyItemDiscount(item, _))
      }
      val remainingBreadItems = breadItems.drop(loafBonus).map(Future.successful)
      val otherItemsFutures = otherItems.map(Future.successful)

      Future.sequence(discountedBreadItems ++ remainingBreadItems ++ otherItemsFutures)
    }

    val discountedItems = Await.result(futureDiscountedItems, 3.seconds)
    val discountedBasket = basket.copy(items = discountedItems)
    val discountAmount = CurrencyUtil.formatCurrency(basket.calculatePrice - discountedBasket.calculatePrice)
    println(s"Buy two Tins get one Loaf half price: $discountAmount")
    discountedBasket
  }
}