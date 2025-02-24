package com.adthena.shoppingbasket.models

import DiscountType.DiscountType

case class Discount(discountType: DiscountType, discountFunction: BigDecimal => BigDecimal)