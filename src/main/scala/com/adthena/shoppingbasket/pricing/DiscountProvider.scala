package com.adthena.shoppingbasket.pricing

trait DiscountProvider {
  def getDiscounts: List[PricingEngine.BasketDiscount]
}
