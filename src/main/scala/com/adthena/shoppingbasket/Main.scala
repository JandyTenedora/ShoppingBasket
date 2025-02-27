package com.adthena.shoppingbasket

import com.adthena.shoppingbasket.models.{Basket, Item}
import com.adthena.shoppingbasket.pricing.{DefaultDiscountProvider, DiscountProvider, PricingEngine}
import com.adthena.shoppingbasket.util.CurrencyUtil

object Main extends App {
  val items = Map(
    "Soup" -> BigDecimal(0.65),
    "Bread" -> BigDecimal(0.80),
    "Milk" -> BigDecimal(1.30),
    "Apples" -> BigDecimal(1.00)
  )

  // Create a list of items based on command-line arguments
  private val basketItems = args.map(item => Item(item, items(item)))

  val basket = Basket(basketItems.toList)

  private val defaultDiscountProvider: DiscountProvider = new DefaultDiscountProvider
  private val pricingEngine = new PricingEngine.Engine(defaultDiscountProvider)

  val subTotal = CurrencyUtil.formatCurrency(basket.calculatePrice)
  println(s"Subtotal: $subTotal")
  val newBasket = pricingEngine.applyDiscounts(basket = basket)
  val totalPrice = CurrencyUtil.formatCurrency(newBasket.calculatePrice)
  println(s"Total Price: $totalPrice")
}
