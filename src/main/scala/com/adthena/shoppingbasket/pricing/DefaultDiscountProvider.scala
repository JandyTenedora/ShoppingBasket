package com.adthena.shoppingbasket.pricing

import com.adthena.shoppingbasket.models.Item

class DefaultDiscountProvider extends DiscountProvider {

  import PricingEngine.BasketDiscount

  override def getDiscounts: List[BasketDiscount] = List(
    appleDiscounts,
    buyTwoTinsGetLoafHalfPrice,
    applesFlatDrop
  )

  // Prescribed discount: All apples are 10% off
  private[pricing] val appleDiscounts: BasketDiscount = basket => {
    val appleDiscountFunction: BigDecimal => BigDecimal = (x: BigDecimal) => x * 0.9
    val originalBasketPrice = basket.calculatePrice
    val discountedItems = basket.items.map {
      case item @ Item("Apple", currentPrice) => item.copy(price = appleDiscountFunction(currentPrice))
      case other => other
    }
    val discountedBasketPrice = discountedItems.map(_.price).sum
    val discountAmount = originalBasketPrice - discountedBasketPrice
    println(s"Apples 10% off: $discountAmount")
    basket.copy(items = discountedItems)
  }

  // Prescribed discount: Buy 2 tins, get 1 loaf half price
  private[pricing] val buyTwoTinsGetLoafHalfPrice: BasketDiscount = basket => {
    val twoTinsLoafHalfPriceDiscountFunction: BigDecimal => BigDecimal = (x: BigDecimal) => x * 0.5
    val tinCount = basket.items.count(_.name == "Soup")
    val loafBonus = tinCount / 2
    val loafCount = basket.items.count(_.name == "Bread")
    val potentialLoafDiscounts = loafBonus min loafCount
    val originalBasketPrice = basket.calculatePrice
    val discountedItems = basket.items.collect {
      case item @ Item("Bread", currentPrice) => item.copy(price = twoTinsLoafHalfPriceDiscountFunction(currentPrice))
      case other => other
    }.take(potentialLoafDiscounts)
    val discountedBasketPrice = discountedItems.map(_.price).sum
    val discountAmount = originalBasketPrice - discountedBasketPrice
    println(s"Buy two tins get one loaf half price: $discountAmount")
    basket.copy(items = discountedItems)
  }

  // Example discount: All apples are a flat 5p off
  private[pricing] val applesFlatDrop: BasketDiscount = basket => {
    val apples5cLessDiscountFunction: BigDecimal => BigDecimal = (x: BigDecimal) => (x - 0.05) max 0  //can not go negative
    val discountedItems = basket.items.map {
      case item @ Item("Apple", currentPrice) => item.copy(price = apples5cLessDiscountFunction(currentPrice))
      case other => other
    }
    println()
    basket.copy(items = discountedItems)
  }
}
