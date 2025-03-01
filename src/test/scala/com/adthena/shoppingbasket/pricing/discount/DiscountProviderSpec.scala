package com.adthena.shoppingbasket.pricing.discount

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.ActorSystem
import com.adthena.shoppingbasket.models.Item
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class DiscountProviderSpec extends AnyFunSpec with Matchers with BeforeAndAfterAll{
  private val testKit = ActorTestKit()
  implicit val system: ActorSystem[_] = testKit.system

  describe("DiscountProvider") {
    it("should throw an IllegalArgumentException if the discount function changes the item type") {
      val discountProvider = new DefaultDiscountProvider()(system)

      val invalidDiscountFunction: Item => Item = _ => Item("invalid", BigDecimal(0))

      val exception = intercept[IllegalArgumentException] {
        discountProvider.createDiscountActor(invalidDiscountFunction)
      }

      exception.getMessage should include("The discount function must return an Item of the same type")
    }

    it("should not throw an exception if the discount function returns an item of the same type") {
      val discountProvider = new DefaultDiscountProvider()(system)

      val validDiscountFunction: Item => Item = item => item.copy(price = item.price * 0.9)

      noException should be thrownBy {
        discountProvider.createDiscountActor(validDiscountFunction)
      }
    }
  }

  override def afterAll(): Unit = {
    testKit.shutdownTestKit()
  }
}