import org.scalatest.propspec.AnyPropSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalacheck.Gen
import com.adthena.shoppingbasket.models.{Basket, Item}

class BasketSpec extends AnyPropSpec with ScalaCheckPropertyChecks {

  val itemGen: Gen[Item] = for {
    name <- Gen.alphaStr.suchThat(_.nonEmpty)
    price <- Gen.choose(0.0, 1000.0).map(BigDecimal(_))
  } yield Item(name, price)

  val basketGen: Gen[Basket] = for {
    items <- Gen.listOf(itemGen)
  } yield Basket(items)

  property("Basket should calculate the total price of items correctly") {
    forAll(basketGen) { basket =>
      val expectedPrice = basket.items.map(_.price).sum
      assert(basket.calculatePrice == expectedPrice)
    }
  }

  property("Basket with no items should have a total price of zero") {
    val emptyBasket = Basket(List.empty)
    assert(emptyBasket.calculatePrice == BigDecimal(0))
  }
}