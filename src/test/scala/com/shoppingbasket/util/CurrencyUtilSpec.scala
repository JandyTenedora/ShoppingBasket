package com.shoppingbasket.util

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalacheck.Gen

class CurrencyUtilSpec extends AnyFunSpec with Matchers with ScalaCheckPropertyChecks {

  describe("CurrencyUtil") {

    describe("formatCurrency") {
      it("should format amounts less than 1 as pence") {
        forAll(Gen.choose(0.0, 0.99)) { amount =>
          val formatted = CurrencyUtil.formatCurrency(BigDecimal(amount))
          formatted should endWith("p")
          formatted should not include "£"
        }
      }

      it("should format amounts of 1 or more as pounds with two decimal places") {
        forAll(Gen.choose(1.0, 10000.0)) { amount =>
          val formatted = CurrencyUtil.formatCurrency(BigDecimal(amount))
          formatted should startWith("£")
          formatted should fullyMatch regex """£\d+\.\d{2}"""
        }
      }

      it("should handle zero correctly") {
        CurrencyUtil.formatCurrency(BigDecimal(0)) shouldEqual "0p"
      }
    }
  }
}
