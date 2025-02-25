package com.adthena.shoppingbasket.models

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ItemSpec extends AnyFunSpec with Matchers {

  describe("An Item") {

    it("should apply discounts correctly") {
      val item = Item("Apple", BigDecimal(1.00))
      val discount = (price: BigDecimal) => price * 0.9
      val discountedItem = item.addDiscount(discount)
      discountedItem.applyDiscounts() shouldEqual BigDecimal(0.90)
    }

    it("should add multiple discounts and apply them in order") {
      val item = Item("Apple", BigDecimal(1.00))
      val discount1 = (price: BigDecimal) => price * 0.9
      val discount2 = (price: BigDecimal) => price - 0.05
      val discountedItem = item.addDiscount(discount1).addDiscount(discount2)
      discountedItem.applyDiscounts() shouldEqual BigDecimal(0.85)
    }

    it("should return the original price if no discounts are applied") {
      val item = Item("Apple", BigDecimal(1.00))
      item.applyDiscounts() shouldEqual BigDecimal(1.00)
    }
  }
}