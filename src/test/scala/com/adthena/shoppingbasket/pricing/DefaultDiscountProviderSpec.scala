package com.adthena.shoppingbasket.pricing

import com.adthena.shoppingbasket.models.{Basket, Item}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class DefaultDiscountProviderSpec extends AnyFunSpec with Matchers {

  describe("DefaultDiscountProvider") {
    val discountProvider = new DefaultDiscountProvider

    describe("appleDiscounts") {
      it("should apply a 10% discount to all apples") {
        val basket = Basket(List(Item("Apple", 1.00), Item("Apple", 2.00), Item("Bread", 1.50)))
        val discountedBasket = discountProvider.appleDiscounts(basket)
        discountedBasket.items should contain allOf (Item("Apple", 0.90), Item("Apple", 1.80), Item("Bread", 1.50))
      }

      it("should handle an empty basket") {
        val basket = Basket(List.empty[Item])
        val discountedBasket = discountProvider.appleDiscounts(basket)
        discountedBasket.items shouldBe empty
      }

      it("should not apply a discount if there are no apples") {
        val basket = Basket(List(Item("Bread", 1.50), Item("Milk", 1.00)))
        val discountedBasket = discountProvider.appleDiscounts(basket)
        discountedBasket.items should contain allOf (Item("Bread", 1.50), Item("Milk", 1.00))
      }
    }

    describe("buyTwoTinsGetLoafHalfPrice") {
      it("should apply a half price discount to one loaf for every two tins") {
        val basket = Basket(List(Item("Soup", 1.00), Item("Soup", 1.00), Item("Bread", 1.50)))
        val discountedBasket = discountProvider.buyTwoTinsGetLoafHalfPrice(basket)
        discountedBasket.items should contain allOf (Item("Soup", 1.00), Item("Soup", 1.00), Item("Bread", 0.75))
      }

      it("should handle an empty basket") {
        val basket = Basket(List.empty[Item])
        val discountedBasket = discountProvider.buyTwoTinsGetLoafHalfPrice(basket)
        discountedBasket.items shouldBe empty
      }

      it("should not apply a discount if there are no tins or loaves") {
        val basket = Basket(List(Item("Milk", 1.00), Item("Cheese", 2.00)))
        val discountedBasket = discountProvider.buyTwoTinsGetLoafHalfPrice(basket)
        discountedBasket.items should contain allOf (Item("Milk", 1.00), Item("Cheese", 2.00))
      }

      it("should not apply a discount if there are less than two tins") {
        val basket = Basket(List(Item("Soup", 1.00), Item("Bread", 1.50)))
        val discountedBasket = discountProvider.buyTwoTinsGetLoafHalfPrice(basket)
        discountedBasket.items should contain allOf (Item("Soup", 1.00), Item("Bread", 1.50))
      }
    }

    describe("applesFlatDrop") {
      it("should apply a flat 5p discount to all apples") {
        val basket = Basket(List(Item("Apple", 1.00), Item("Apple", 0.10), Item("Bread", 1.50)))
        val discountedBasket = discountProvider.applesFlatDrop(basket)
        discountedBasket.items should contain allOf (Item("Apple", 0.95), Item("Apple", 0.05), Item("Bread", 1.50))
      }

      it("should handle an empty basket") {
        val basket = Basket(List.empty[Item])
        val discountedBasket = discountProvider.applesFlatDrop(basket)
        discountedBasket.items shouldBe empty
      }

      it("should not apply a discount if there are no apples") {
        val basket = Basket(List(Item("Bread", 1.50), Item("Milk", 1.00)))
        val discountedBasket = discountProvider.applesFlatDrop(basket)
        discountedBasket.items should contain allOf (Item("Bread", 1.50), Item("Milk", 1.00))
      }

      it("should not reduce the price of apples below 0") {
        val basket = Basket(List(Item("Apple", 0.03), Item("Apple", 0.02)))
        val discountedBasket = discountProvider.applesFlatDrop(basket)
        discountedBasket.items should contain allOf (Item("Apple", 0.00), Item("Apple", 0.00))
      }
    }
  }
}