package com.shoppingbasket.pricing.discount

import com.shoppingbasket.models.{Basket, Item}
import org.scalacheck.Gen
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class DefaultDiscountProviderSpec extends AnyFunSpec with Matchers with ScalaCheckPropertyChecks {

  describe("DefaultDiscountProvider") {
    val discountProvider = new DefaultDiscountProvider

    describe("appleDiscounts") {
      it("should apply a 10% discount to all apples") {
        forAll(Gen.listOf(Gen.oneOf(Item("Apples", 1.00), Item("Apples", 2.00), Item("Bread", 1.50)))) { items =>
          val basket = Basket(items)
          val discountedBasket = discountProvider.appleDiscounts(basket)
          val expectedItems = items.map {
            case Item("Apples", price) => Item("Apples", price * 0.9)
            case other => other
          }
          discountedBasket.items should contain theSameElementsAs expectedItems
        }
      }

      it("should handle an empty basket") {
        val basket = Basket(List.empty[Item])
        val discountedBasket = discountProvider.appleDiscounts(basket)
        discountedBasket.items shouldBe empty
      }

      it("should not apply a discount if there are no apples") {
        forAll(Gen.listOf(Gen.oneOf(Item("Bread", 1.50), Item("Milk", 1.00)))) { items =>
          val basket = Basket(items)
          val discountedBasket = discountProvider.appleDiscounts(basket)
          discountedBasket.items should contain theSameElementsAs items
        }
      }
    }

    describe("buyTwoTinsGetLoafHalfPrice") {
      it("should apply a half price discount to one loaf for every two tins") {
        forAll(Gen.listOf(Gen.oneOf(Item("Soup", 1.00), Item("Bread", 1.50)))) { items =>
          val basket = Basket(items)
          val discountedBasket = discountProvider.buyTwoTinsGetLoafHalfPrice(basket)
          val tinCount = items.count(_.name == "Soup")
          val loafBonus = tinCount / 2
          val loafCount = items.count(_.name == "Bread")
          var potentialLoafDiscounts = loafBonus min loafCount
          val expectedItems = items.map {
            case item @ Item("Bread", price) if potentialLoafDiscounts > 0 =>
              potentialLoafDiscounts -= 1
              item.copy(price = price * 0.5)
            case other => other
          }
          discountedBasket.items should contain theSameElementsAs expectedItems
        }
      }

      it("should handle an empty basket") {
        val basket = Basket(List.empty[Item])
        val discountedBasket = discountProvider.buyTwoTinsGetLoafHalfPrice(basket)
        discountedBasket.items shouldBe empty
      }

      it("should not apply a discount if there are no tins or loaves") {
        forAll(Gen.listOf(Gen.oneOf(Item("Milk", 1.00), Item("Cheese", 2.00)))) { items =>
          val basket = Basket(items)
          val discountedBasket = discountProvider.buyTwoTinsGetLoafHalfPrice(basket)
          discountedBasket.items should contain theSameElementsAs items
        }
      }

      it("should not apply a discount if there are less than two tins") {
        val genFewSoups = for {
          soupCount <- Gen.choose(0, 1)  // Generate 0 or 1 Soup
          soups <- Gen.listOfN(soupCount, Gen.const(Item("Soup", 1.00)))
          breads <- Gen.listOf(Gen.const(Item("Bread", 1.50)))  // Any number of breads
        } yield soups ++ breads
        forAll(genFewSoups) { items =>
          val basket = Basket(items)
          val discountedBasket = discountProvider.buyTwoTinsGetLoafHalfPrice(basket)
          discountedBasket.calculatePrice shouldEqual basket.calculatePrice
        }
      }
    }


  }
}