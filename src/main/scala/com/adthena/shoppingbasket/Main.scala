package com.adthena.shoppingbasket

import com.adthena.shoppingbasket.models.{Basket, Item}
import com.adthena.shoppingbasket.pricing.discount.{DefaultDiscountProvider, DiscountProvider}
import com.adthena.shoppingbasket.pricing.PricingEngine
import com.adthena.shoppingbasket.util.CurrencyUtil
import com.typesafe.config.ConfigFactory

import scala.collection.convert.ImplicitConversions.`map AsScala`

object Main extends App {
  // Load the configuration
  val config = ConfigFactory.load()
  val itemsConfig = config.getConfig("shoppingbasket.items")

  // Create a map of items from the configuration
  val items = itemsConfig.root().unwrapped().toMap.collect {
    case (key: String, value: String) =>
      key -> BigDecimal(value)
  }

  // Create a list of items based on command-line arguments
  private val basketItems = args.flatMap(item => items.get(item).map(price => Item(item, price)))

  val basket = Basket(basketItems.toList)

  private val defaultDiscountProvider: DiscountProvider = new DefaultDiscountProvider
  private val pricingEngine = new PricingEngine.Engine(defaultDiscountProvider)

  private val subTotal = basket.calculatePrice
  println(s"Subtotal: ${CurrencyUtil.formatCurrency(subTotal)}")

  private val newBasket = pricingEngine.applyDiscounts(basket = basket)
  private val totalPrice = newBasket.calculatePrice
  if (subTotal == totalPrice) println("(No offers available)")
  println(s"Total Price: ${CurrencyUtil.formatCurrency(totalPrice)}")
}
