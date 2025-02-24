package com.adthena.shoppingbasket.models

case class Basket(items: List[Item]) {
  def addItem(item: Item): Basket = copy(items = items :+ item)
}