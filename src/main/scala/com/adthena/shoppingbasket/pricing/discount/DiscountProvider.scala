package com.adthena.shoppingbasket.pricing.discount

import com.adthena.shoppingbasket.pricing.PricingEngine

trait DiscountProvider {
  def getDiscounts: List[PricingEngine.BasketDiscount]
}
