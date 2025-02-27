package com.adthena.shoppingbasket.pricing.discount

import com.adthena.shoppingbasket.models.Item
import com.adthena.shoppingbasket.pricing.PricingEngine
import com.adthena.shoppingbasket.util.CurrencyUtil

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
class DefaultDiscountProvider extends DiscountProvider {

  import PricingEngine.BasketDiscount

  override def getDiscounts: List[BasketDiscount] = List(
    appleDiscounts,
    buyTwoTinsGetLoafHalfPrice
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
    val discountAmount = CurrencyUtil.formatCurrency(originalBasketPrice - discountedBasketPrice)
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
    val discountedItems = {
      var modifiedCount = 0
      basket.items.map {
        case item @ Item("Bread", currentPrice) if modifiedCount < potentialLoafDiscounts =>
          modifiedCount += 1
          item.copy(price = twoTinsLoafHalfPriceDiscountFunction(currentPrice))
        case other => other
      }
    }
    val discountedBasketPrice = discountedItems.map(_.price).sum
    val discountAmount = CurrencyUtil.formatCurrency(originalBasketPrice - discountedBasketPrice)
    println(s"Buy two Tins get one Loaf half price: $discountAmount")
    basket.copy(items = discountedItems)
  }
}
