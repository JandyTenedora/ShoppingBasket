package com.adthena.shoppingbasket.models

case class Basket(items: List[Item]) {
  def calculatePrice: BigDecimal = items.map(_.price).sum
}