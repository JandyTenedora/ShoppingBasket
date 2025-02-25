package com.adthena.shoppingbasket.models

case class Item(name: String, price: BigDecimal, discounts: List[Discount] = List.empty[Discount]) {
  def applyDiscounts(): BigDecimal = {
    discounts.foldLeft(price)((currentPrice, discount) => discount.discountFunction(currentPrice))
  }

  // Add a new discount
  def addDiscount(discount: Discount): Item = {
    copy(discounts = discounts :+ discount)
  }
}