package com.shoppingbasket.pricing.discount

import com.shoppingbasket.models.{Basket, Item}
import com.shoppingbasket.pricing.PricingEngine.BasketDiscount
import com.shoppingbasket.util.CurrencyUtil

object DiscountFunctions {

  // Example discount: Apply a percentage discount to items matching a predicate
  def percentageDiscount(predicate: Item => Boolean, discountRate: BigDecimal, discountName: String): BasketDiscount = basket => {
    val discountFunction: BigDecimal => BigDecimal = price => price * (1 - discountRate)
    val originalBasketPrice = basket.calculatePrice
    val discountedItems = basket.items.map {
      case item if predicate(item) => item.copy(price = discountFunction(item.price))
      case other => other
    }
    val discountedBasketPrice = discountedItems.map(_.price).sum
    val discountAmount = originalBasketPrice - discountedBasketPrice
    val discountString = s"$discountName: ${CurrencyUtil.formatCurrency(discountAmount)}"
    val updatedDiscountString = if (discountAmount != 0) basket.appendDiscountString(discountString) else basket.discountString
    basket.copy(items = discountedItems, discountString = updatedDiscountString)
  }

  // Example discount: Buy X get Y Discounted
  def buyXGetYDiscounted(xItem: String, yItem: String, xCount: Int, yCount: Int, discountFunction: BigDecimal => BigDecimal, discountName: String): BasketDiscount = basket => {
    val xItemCount = basket.items.count(_.name == xItem)

    val yItemCount = basket.items.count(_.name == yItem)
    var discountedYItems = (xItemCount / xCount) * yCount min yItemCount

    val originalBasketPrice = basket.calculatePrice

    // Ensure modifiedCount is applied correctly by using `foldLeft`
    val discountedItems = basket.items.zipWithIndex.map {
      case (item @ Item(`yItem`, originalPrice), index) if discountedYItems > 0 =>
        val newItem = item.copy(price = discountFunction(originalPrice))
        discountedYItems -= 1 // Decrease discountedYItems count
        newItem
      case (item, _) => item
    }

    val discountedBasketPrice = discountedItems.map(_.price).sum
    val discountAmount = originalBasketPrice - discountedBasketPrice
    val discountString = s"$discountName: ${CurrencyUtil.formatCurrency(discountAmount)}"
    val updatedDiscountString = if (discountAmount != 0) basket.appendDiscountString(discountString) else basket.discountString

    basket.copy(items = discountedItems, discountString = updatedDiscountString)
  }
}