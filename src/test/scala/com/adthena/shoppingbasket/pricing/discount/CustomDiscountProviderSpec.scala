package com.adthena.shoppingbasket.pricing.discount

import com.adthena.shoppingbasket.models.{Basket, Item}
import org.scalacheck.Gen
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class CustomDiscountProviderSpec extends AnyFunSpec with Matchers with ScalaCheckPropertyChecks {
  describe("CustomerDiscountProviderSpec") {
    val discountProvider = new CustomDiscountProvider
    describe("applesFlatDrop") {
      it("should apply a flat 5p discount to all apples") {
        forAll(Gen.listOf(Gen.oneOf(Item("Apple", 1.00), Item("Apple", 0.10), Item("Bread", 1.50)))) { items =>
          val basket = Basket(items)
          val discountedBasket = discountProvider.applesFlatDrop(basket)
          val expectedItems = items.map {
            case Item("Apple", price) => Item("Apple", (price - 0.05) max 0)
            case other => other
          }
          discountedBasket.items should contain theSameElementsAs expectedItems
        }
      }

      it("should handle an empty basket") {
        val basket = Basket(List.empty[Item])
        val discountedBasket = discountProvider.applesFlatDrop(basket)
        discountedBasket.items shouldBe empty
      }

      it("should not apply a discount if there are no apples") {
        forAll(Gen.listOf(Gen.oneOf(Item("Bread", 1.50), Item("Milk", 1.00)))) { items =>
          val basket = Basket(items)
          val discountedBasket = discountProvider.applesFlatDrop(basket)
          discountedBasket.items should contain theSameElementsAs items
        }
      }

      it("should not reduce the price of apples below 0") {
        forAll(Gen.listOf(Gen.oneOf(Item("Apple", 0.03), Item("Apple", 0.02)))) { items =>
          val basket = Basket(items)
          val discountedBasket = discountProvider.applesFlatDrop(basket)
          val expectedItems = items.map {
            case Item("Apple", price) => Item("Apple", 0)
            case other => other
          }
          discountedBasket.items should contain theSameElementsAs expectedItems
        }
      }
    }
  }
}