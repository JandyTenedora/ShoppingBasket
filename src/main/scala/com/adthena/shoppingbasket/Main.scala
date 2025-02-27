package com.adthena.shoppingbasket

import com.adthena.shoppingbasket.models.{Basket, Item}
import com.adthena.shoppingbasket.pricing.{DefaultDiscountProvider, DiscountProvider, PricingEngine}

object Main extends App {
  private val defaultDiscountProvider: DiscountProvider = new DefaultDiscountProvider
  private val pricingEngine = new PricingEngine.Engine(defaultDiscountProvider)
  val basket = Basket(List.empty[Item])
  pricingEngine.applyDiscounts(basket = basket)
}
