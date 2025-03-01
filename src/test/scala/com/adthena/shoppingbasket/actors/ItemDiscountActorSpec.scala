package com.adthena.shoppingbasket.actors

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import com.adthena.shoppingbasket.models.Item
import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.should.Matchers

class ItemDiscountActorSpec extends ScalaTestWithActorTestKit with AnyFunSpecLike with Matchers {

  describe("ItemDiscountActor") {
    it("should apply the discount function to the item") {
      val discountFunction: Item => Item = item => item.copy(price = item.price * 0.9)
      val itemDiscountActor = testKit.spawn(ItemDiscountActor())
      val probe = testKit.createTestProbe[Item]()

      val item = Item("Apples", BigDecimal(1.00))
      itemDiscountActor ! ItemDiscountActor.ApplyItemDiscount(item, discountFunction, probe.ref)

      val discountedItem = probe.expectMessageType[Item]
      discountedItem shouldBe Item("Apples", BigDecimal(0.90))
    }

    it("should handle an item with zero price") {
      val discountFunction: Item => Item = item => item.copy(price = item.price * 0.9)
      val itemDiscountActor = testKit.spawn(ItemDiscountActor())
      val probe = testKit.createTestProbe[Item]()

      val item = Item("Free Sample", BigDecimal(0.00))
      itemDiscountActor ! ItemDiscountActor.ApplyItemDiscount(item,discountFunction, probe.ref)

      val discountedItem = probe.expectMessageType[Item]
      discountedItem shouldBe Item("Free Sample", BigDecimal(0.00))
    }

    it("should handle an item with a negative price") {
      val discountFunction: Item => Item = item => item.copy(price = item.price * 0.9)
      val itemDiscountActor = testKit.spawn(ItemDiscountActor())
      val probe = testKit.createTestProbe[Item]()

      val item = Item("Refund", BigDecimal(-1.00))
      itemDiscountActor ! ItemDiscountActor.ApplyItemDiscount(item,discountFunction, probe.ref)

      val discountedItem = probe.expectMessageType[Item]
      discountedItem shouldBe Item("Refund", BigDecimal(-0.90))
    }
  }
}