package com.shoppingbasket.models

case class Basket(items: List[Item], discountString: String = "") {
  def calculatePrice: BigDecimal = items.map(_.price).sum
  def appendDiscountString(newDiscountString: String): String = {
    discountString.concat(s"$newDiscountString\n")
  }
}