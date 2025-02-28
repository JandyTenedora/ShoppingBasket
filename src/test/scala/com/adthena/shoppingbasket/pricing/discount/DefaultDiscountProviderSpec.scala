package com.adthena.shoppingbasket.pricing.discount

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.ActorSystem
import com.adthena.shoppingbasket.models.{Basket, Item}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class DefaultDiscountProviderSpec extends AnyFunSpec with Matchers with BeforeAndAfterAll {
  // Create an ActorTestKit instance
  private val testKit = ActorTestKit()

  // Implicit ActorSystem
  implicit val system: ActorSystem[_] = testKit.system

  override def afterAll(): Unit = {
    // Shutdown the ActorTestKit
    testKit.shutdownTestKit()
  }

  describe("DefaultDiscountProvider") {
    val discountProvider = new DefaultDiscountProvider

    describe("appleDiscounts") {
      it("should apply a 10% discount to all apples") {
        val items = List(Item("Apples", 1.00), Item("Apples", 2.00), Item("Bread", 1.50))
        val basket = Basket(items)
        val discountedBasket = discountProvider.appleDiscounts(basket)
        val expectedItems = items.map {
          case Item("Apples", price) => Item("Apples", price * 0.9)
          case other => other
        }
        discountedBasket.items should contain theSameElementsAs expectedItems
      }

      it("should handle an empty basket") {
        val basket = Basket(List.empty[Item])
        val discountedBasket = discountProvider.appleDiscounts(basket)
        discountedBasket.items shouldBe empty
      }

      it("should not apply a discount if there are no apples") {
        val items = List(Item("Bread", 1.50), Item("Milk", 1.00))
        val basket = Basket(items)
        val discountedBasket = discountProvider.appleDiscounts(basket)
        discountedBasket.items should contain theSameElementsAs items
      }
    }

    describe("buyTwoTinsGetLoafHalfPrice") {
      it("should apply a half price discount to one loaf for every two tins") {
        val items = List(Item("Soup", 1.00), Item("Soup", 1.00), Item("Bread", 1.50))
        val basket = Basket(items)
        val discountedBasket = discountProvider.buyTwoTinsGetLoafHalfPrice(basket)
        val expectedItems = List(Item("Soup", 1.00), Item("Soup", 1.00), Item("Bread", 0.75))
        discountedBasket.items should contain theSameElementsAs expectedItems
      }

      it("should handle an empty basket") {
        val basket = Basket(List.empty[Item])
        val discountedBasket = discountProvider.buyTwoTinsGetLoafHalfPrice(basket)
        discountedBasket.items shouldBe empty
      }

      it("should not apply a discount if there are no tins or loaves") {
        val items = List(Item("Milk", 1.00), Item("Cheese", 2.00))
        val basket = Basket(items)
        val discountedBasket = discountProvider.buyTwoTinsGetLoafHalfPrice(basket)
        discountedBasket.items should contain theSameElementsAs items
      }

      it("should not apply a discount if there are less than two tins") {
        val items = List(Item("Soup", 1.00), Item("Bread", 1.50))
        val basket = Basket(items)
        val discountedBasket = discountProvider.buyTwoTinsGetLoafHalfPrice(basket)
        discountedBasket.items should contain theSameElementsAs items
      }
    }
  }
}