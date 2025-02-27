package com.adthena.shoppingbasket.util

object CurrencyUtil {
  def formatCurrency(amount: BigDecimal): String = {
    if (amount < 1) f"${(amount * 100).toInt}p"
    else f"£$amount%.2f"
  }
}
