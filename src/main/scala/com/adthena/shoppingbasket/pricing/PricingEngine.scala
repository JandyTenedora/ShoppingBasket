package com.adthena.shoppingbasket.pricing

import com.adthena.shoppingbasket.models.Basket
import com.adthena.shoppingbasket.pricing.discount.DiscountProvider

object PricingEngine {

  type BasketDiscount = Basket => Basket

  class Engine(discountProvider: DiscountProvider) {

    // Applies all available discounts from the provider
    def applyDiscounts(basket: Basket): Basket = {
      discountProvider.getDiscounts.foldLeft(basket)((b, discount) => discount(b))
    }
  }
}
