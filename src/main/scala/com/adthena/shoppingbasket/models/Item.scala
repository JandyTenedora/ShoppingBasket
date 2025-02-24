package com.adthena.shoppingbasket.models

case class Item(name: String, price: BigDecimal, discounts: List[BigDecimal => BigDecimal] = List.empty[BigDecimal => BigDecimal]) {
  def applyDiscounts(): BigDecimal = {
    discounts.foldLeft(price)((currentPrice, discount) => discount(currentPrice))
  }

  // Add a new discount
  def addDiscount(discount: Discount): Item = {
    copy(discounts = discounts :+ discount)
  }
}