package com.shoppingbasket.pricing.discount

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalacheck.{Arbitrary, Gen}
import com.shoppingbasket.models.{Basket, Item}

class DiscountFunctionsSpec extends AnyFunSpec with Matchers with ScalaCheckPropertyChecks {

  // Define an implicit Arbitrary instance for Item
  implicit val arbitraryItem: Arbitrary[Item] = Arbitrary {
    for {
      name <- Gen.alphaStr
      price <- Gen.choose(0.0, 100.0).map(BigDecimal(_))
    } yield Item(name, price)
  }

  // Define an implicit Arbitrary instance for List[Item]
  implicit val arbitraryItemList: Arbitrary[List[Item]] = Arbitrary {
    Gen.sized { size =>
      Gen.listOfN(size min 10, arbitraryItem.arbitrary) // Limit the list size to 10
    }
  }

  describe("DiscountFunctions") {

    describe("percentageDiscount") {
      it("should apply the correct percentage discount to items matching the predicate") {
        forAll(minSuccessful(50)) { (items: List[Item], discountRate: BigDecimal) =>
          whenever(discountRate >= 0 && discountRate <= 1) {
            val predicate: Item => Boolean = _.name == "DiscountedItem"
            val basket = Basket(items)
            val discount = DiscountFunctions.percentageDiscount(predicate, discountRate, "Test Discount")
            val discountedBasket = discount(basket)

            discountedBasket.items.foreach { item =>
              if (predicate(item)) {
                item.price shouldEqual item.price * (1 - discountRate)
              } else {
                item.price shouldEqual item.price
              }
            }
          }
        }
      }
    }
  }
}