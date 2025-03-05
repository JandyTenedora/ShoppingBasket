package com.shoppingbasket.pricing.discount

import DiscountFunctions._
import com.shoppingbasket.models.Item
import com.shoppingbasket.pricing.PricingEngine

/**
 * The `DefaultDiscountProvider` class provides a set of predefined discounts
 * that can be applied to a shopping basket. It implements the `DiscountProvider`
 * interface and defines two types of discounts:
 *
 * 1. `appleDiscounts`: Applies a 10% discount to all apples in the basket.
 * 2. `buyTwoTinsGetLoafHalfPrice`: For every two tins of soup, applies a 50% discount to one loaf of bread.
 *
 * These discounts are applied in the order they are defined in the getDiscounts method.
 *
 */
class DefaultDiscountProvider extends DiscountProvider {

  import PricingEngine.BasketDiscount

  override def getDiscounts: List[BasketDiscount] = List(
    appleDiscounts,
    buyTwoTinsGetLoafHalfPrice
  )

  private[pricing] val appleDiscounts: BasketDiscount = percentageDiscount((item: Item) => item.name == "Apples", 0.1, "Apples 10% off")
  private[pricing] val buyTwoTinsGetLoafHalfPrice: BasketDiscount = buyXGetYDiscounted("Soup", "Bread", 2, 1, (x: BigDecimal) => x*0.5, "Buy two Tins get one Loaf half price")
}
