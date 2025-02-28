package com.adthena.shoppingbasket.pricing.discount

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.ActorSystem
import com.adthena.shoppingbasket.models.{Basket, Item}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class CustomDiscountProviderSpec extends AnyFunSpec with Matchers with BeforeAndAfterAll {

  // Create an ActorTestKit instance
  private val testKit = ActorTestKit()

  // Implicit ActorSystem
  implicit lazy val system: ActorSystem[_] = testKit.system

  override def afterAll(): Unit = {
    // Shutdown the ActorTestKit
    testKit.shutdownTestKit()
  }

  describe("CustomDiscountProvider") {
    lazy val discountProvider = new CustomDiscountProvider()(actorSystem = system)
    describe("applesFlatDrop") {
      it("should apply a flat 5p discount to all apples") {
        val items = List(Item("Apples", 1.00), Item("Apples", 0.10), Item("Bread", 1.50))
        val basket = Basket(items)
        val discountedBasket = discountProvider.applesFlatDrop(basket)
        val expectedItems = List(Item("Apples", 0.95), Item("Apples", 0.05), Item("Bread", 1.50))
        discountedBasket.items should contain theSameElementsAs expectedItems
      }

      it("should handle an empty basket") {
        val basket = Basket(List.empty[Item])
        val discountedBasket = discountProvider.applesFlatDrop(basket)
        discountedBasket.items shouldBe empty
      }

      it("should not apply a discount if there are no apples") {
        val items = List(Item("Bread", 1.50), Item("Milk", 1.00))
        val basket = Basket(items)
        val discountedBasket = discountProvider.applesFlatDrop(basket)
        discountedBasket.items should contain theSameElementsAs items
      }

      it("should not reduce the price of apples below 0") {
        val items = List(Item("Apples", 0.03), Item("Apples", 0.02))
        val basket = Basket(items)
        val discountedBasket = discountProvider.applesFlatDrop(basket)
        val expectedItems = List(Item("Apples", 0), Item("Apples", 0))
        discountedBasket.items should contain theSameElementsAs expectedItems
      }
    }
  }
}