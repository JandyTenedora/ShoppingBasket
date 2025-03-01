package com.adthena.shoppingbasket.pricing

import com.adthena.shoppingbasket.models.Basket
import com.adthena.shoppingbasket.pricing.discount.DiscountProvider

object PricingEngine {

  type BasketDiscount = Basket => Basket

  class Engine(discountProvider: DiscountProvider) {

    // Applies all available discounts from the provider
    def applyDiscounts(basket: Basket): Basket = {
      val initialItems = basket.items
      val initialItemCount = basket.items.size
      val discountedBasket = discountProvider.getDiscounts.foldLeft(basket)((b, discount) => {
        val newBasket = discount(b)
        require(newBasket.items.map(_.name) == initialItems.map(_.name), "The items in the basket must remain the same after applying discounts")
        require(newBasket.items.size == initialItemCount, "The number of items in the basket must remain the same after applying discounts")
        newBasket
      })
      discountedBasket
    }
  }
}
