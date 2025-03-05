package com.shoppingbasket.pricing.discount

import com.shoppingbasket.pricing.PricingEngine

trait DiscountProvider {
  def getDiscounts: List[PricingEngine.BasketDiscount]
}
