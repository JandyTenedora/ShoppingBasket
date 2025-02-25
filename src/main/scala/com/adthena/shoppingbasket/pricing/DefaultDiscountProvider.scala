package com.adthena.shoppingbasket.pricing

import com.adthena.shoppingbasket.models.{Discount, DiscountType, Item}

class DefaultDiscountProvider extends DiscountProvider {

  import PricingEngine.BasketDiscount

  override def getDiscounts: List[BasketDiscount] = List(
    appleDiscounts,
    buyTwoTinsGetLoafHalfPrice,
    applesFlatDrop
  )

  // Prescribed discount: All apples are 10% off
  private val appleDiscounts: BasketDiscount = basket => {
    val appleDiscountFunction: BigDecimal => BigDecimal = (x: BigDecimal) => x * 0.9
    val appleDiscount = Discount(DiscountType.Percentage, appleDiscountFunction)
    val discountedItems = basket.items.map {
      case item @ Item("Apple", _, _) => item.addDiscount(appleDiscount)
      case other => other
    }
    basket.copy(items = discountedItems)
  }

  // Prescribed discount: Buy 2 tins, get 1 loaf half price
  private val buyTwoTinsGetLoafHalfPrice: BasketDiscount = basket => {
    val twoTinsLoafHalfPriceDiscountFunction: BigDecimal => BigDecimal = (x: BigDecimal) => x * 0.5
    val twoTinsLoafHalfPriceDiscount = Discount(DiscountType.Final, twoTinsLoafHalfPriceDiscountFunction)
    val tinCount = basket.items.count(_.name == "Soup")
    val loafBonus = tinCount / 2
    val loafCount = basket.items.count(_.name == "Bread")
    val potentialLoafDiscounts = loafBonus min loafCount
    val newItems = basket.items.collect {
      case item @ Item("Bread", _, discounts) => item.addDiscount(twoTinsLoafHalfPriceDiscount)
      case other => other
    }.take(potentialLoafDiscounts)
    basket.copy(items = newItems)
  }

  // Example discount: All apples are a flat 5p off
  private val applesFlatDrop: BasketDiscount = basket => {
    val apples5cLessDiscountFunction: BigDecimal => BigDecimal = (x: BigDecimal) => x - 0.05
    val apples5cLessDiscount = Discount(DiscountType.FlatAmount, apples5cLessDiscountFunction)
    val discountedItems = basket.items.map {
      case item @ Item("Apple", _, _) => item.addDiscount(apples5cLessDiscount)
      case other => other
    }
    basket.copy(items = discountedItems)
  }
}
