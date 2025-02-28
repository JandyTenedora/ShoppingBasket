package com.adthena.shoppingbasket.actors

import akka.actor.testkit.typed.scaladsl.{ActorTestKit, ScalaTestWithActorTestKit}
import akka.actor.typed.ActorSystem
import com.adthena.shoppingbasket.models.{Basket, Item}
import com.adthena.shoppingbasket.pricing.PricingEngine
import com.adthena.shoppingbasket.pricing.discount.DefaultDiscountProvider
import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.should.Matchers

class ShoppingBasketActorSpec extends ScalaTestWithActorTestKit with AnyFunSpecLike with Matchers {
  override implicit val system: ActorSystem[_] = testKit.system

  describe("ShoppingBasketActor") {
    val pricingEngine = new PricingEngine.Engine(new DefaultDiscountProvider()(system))
    val shoppingBasketActor = testKit.spawn(ShoppingBasketActor(pricingEngine))

    it("should calculate the total price of a basket with discounts applied") {
      val probe = testKit.createTestProbe[ShoppingBasketActor.Response]()
      val items = List(
        Item("Apples", BigDecimal(1.00)),
        Item("Apples", BigDecimal(1.00)),
        Item("Soup", BigDecimal(0.65)),
        Item("Soup", BigDecimal(0.65)),
        Item("Bread", BigDecimal(0.80))
      )

      shoppingBasketActor ! ShoppingBasketActor.ProcessBasket(items, probe.ref)
      val response = probe.expectMessageType[ShoppingBasketActor.BasketTotal]

      response.total shouldBe 3.50
    }

    it("should handle an empty basket") {
      val probe = testKit.createTestProbe[ShoppingBasketActor.Response]()
      val items = List.empty[Item]

      shoppingBasketActor ! ShoppingBasketActor.ProcessBasket(items, probe.ref)
      val response = probe.expectMessageType[ShoppingBasketActor.BasketTotal]

      response.total shouldBe 0
    }

    it("should calculate the total price of a basket without discounts applied") {
      val probe = testKit.createTestProbe[ShoppingBasketActor.Response]()
      val items = List(
        Item("Milk", BigDecimal(1.00)),
        Item("Cheese", BigDecimal(2.00))
      )

      shoppingBasketActor ! ShoppingBasketActor.ProcessBasket(items, probe.ref)
      val response = probe.expectMessageType[ShoppingBasketActor.BasketTotal]

      response.total shouldBe 3.00
    }
  }
}