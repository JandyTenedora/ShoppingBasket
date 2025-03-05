package com.shoppingbasket.pricing.discount

import com.shoppingbasket.pricing.PricingEngine.BasketDiscount
import com.shoppingbasket.models.Item
import com.shoppingbasket.util.CurrencyUtil

/**
 * The `CustomDiscountProvider` class extends the `DefaultDiscountProvider` class
 * and provides an additional custom discount to the predefined discounts.
 *
 * This class serves as an example to the extensibility of this shopping basket application,
 * and demonstrates how to add custom discounts
 */
class CustomDiscountProvider extends DefaultDiscountProvider {
  override def getDiscounts: List[BasketDiscount] = List(
    appleDiscounts,
    buyTwoTinsGetLoafHalfPrice,
    applesFlatDrop
  )
  // Example discount: All apples are a flat 5p off
  private[pricing] val applesFlatDrop: BasketDiscount = basket => {
    val apples5cLessDiscountFunction: BigDecimal => BigDecimal = (x: BigDecimal) => (x - 0.05) max 0  //can not go negative
    val discountedItems = basket.items.map {
      case item @ Item("Apples", currentPrice) => item.copy(price = apples5cLessDiscountFunction(currentPrice))
      case other => other
    }
    val originalBasketPrice = basket.calculatePrice
    val discountedBasketPrice = discountedItems.map(_.price).sum
    val discountAmount = originalBasketPrice - discountedBasketPrice
    if (discountAmount != 0) println(s"All Apples 5p off: ${CurrencyUtil.formatCurrency(discountAmount)}")
    basket.copy(items = discountedItems)
  }

}
