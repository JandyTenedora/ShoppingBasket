package com.adthena.shoppingbasket.pricing

import com.adthena.shoppingbasket.models.Basket

object PricingEngine {

  type BasketDiscount = Basket => Basket

  class Engine(discountProvider: DiscountProvider) {

    // Applies all available discounts from the provider
    def applyDiscounts(basket: Basket): Basket = {
      discountProvider.getDiscounts.foldLeft(basket)((b, discount) => discount(b))
    }
  }
}
