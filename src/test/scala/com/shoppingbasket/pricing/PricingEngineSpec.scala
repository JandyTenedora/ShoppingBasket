package com.shoppingbasket.pricing

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import com.shoppingbasket.models.{Basket, Item}
import com.shoppingbasket.pricing.discount.{CustomDiscountProvider, DefaultDiscountProvider, DiscountProvider}

class PricingEngineSpec extends AnyFunSpec with Matchers {

  describe("PricingEngine") {
    describe("applyDiscounts") {
      it("should throw IllegalArgumentException if discount function reduces items") {
        val discountProvider = new DiscountProvider {
          override def getDiscounts: List[PricingEngine.BasketDiscount] = List(
            basket => basket.copy(items = List.empty[Item])
          )
        }
        val engine = new PricingEngine.Engine(discountProvider)

        val basket = Basket(List(
          Item("Apples", BigDecimal(1.00))
        ))

        intercept[IllegalArgumentException] {
          engine.applyDiscounts(basket)
        }
      }

      it("should throw IllegalArgumentException if items change after applying discounts") {
        val discountProvider = new DiscountProvider {
          override def getDiscounts: List[PricingEngine.BasketDiscount] = List(
            basket => basket.copy(items = List(Item("Oranges", BigDecimal(1.00))))
          )
        }
        val engine = new PricingEngine.Engine(discountProvider)

        val basket = Basket(List(
          Item("Apples", BigDecimal(1.00))
        ))

        intercept[IllegalArgumentException] {
          engine.applyDiscounts(basket)
        }
      }

      it("should apply all discounts correctly") {
        val discountProvider = new DefaultDiscountProvider
        val engine = new PricingEngine.Engine(discountProvider)

        val basket = Basket(List(
          Item("Apples", BigDecimal(1.00)),
          Item("Apples", BigDecimal(1.00)),
          Item("Soup", BigDecimal(0.65)),
          Item("Soup", BigDecimal(0.65)),
          Item("Bread", BigDecimal(0.80))
        ))

        val discountedBasket = engine.applyDiscounts(basket)

        val expectedItems = List(
          Item("Apples", BigDecimal(0.9)), // 10% off
          Item("Apples", BigDecimal(0.9)), // 10% off
          Item("Soup", BigDecimal(0.65)),
          Item("Soup", BigDecimal(0.65)),
          Item("Bread", BigDecimal(0.40)) // Half price
        )

        discountedBasket.items should contain theSameElementsAs expectedItems
      }

      it("should apply Apples discounts correctly") {
        val discountProvider = new DefaultDiscountProvider
        val engine = new PricingEngine.Engine(discountProvider)

        val basket = Basket(List(
          Item("Apples", BigDecimal(1.00))
        ))

        val discountedBasket = engine.applyDiscounts(basket)

        discountedBasket.items should contain theSameElementsAs List(
          Item("Apples", BigDecimal(0.9)) // 10% off
        )
      }

      it("should apply buy two tins get one loaf half price discount correctly") {
        val discountProvider = new CustomDiscountProvider
        val engine = new PricingEngine.Engine(discountProvider)

        val basket = Basket(List(
          Item("Soup", BigDecimal(0.65)),
          Item("Soup", BigDecimal(0.65)),
          Item("Bread", BigDecimal(0.80))
        ))

        val discountedBasket = engine.applyDiscounts(basket)

        discountedBasket.items should contain theSameElementsAs List(
          Item("Soup", BigDecimal(0.65)),
          Item("Soup", BigDecimal(0.65)),
          Item("Bread", BigDecimal(0.40)) // Half price
        )
      }

      it("should apply apples flat drop discount correctly") {
        val discountProvider = new DefaultDiscountProvider
        val engine = new PricingEngine.Engine(discountProvider)

        val basket = Basket(List(
          Item("Apples", BigDecimal(0.10))
        ))

        val discountedBasket = engine.applyDiscounts(basket)

        discountedBasket.items should contain theSameElementsAs List(
          Item("Apples", BigDecimal(0.09)) //10% off, -5p
        )
      }
    }
  }
}