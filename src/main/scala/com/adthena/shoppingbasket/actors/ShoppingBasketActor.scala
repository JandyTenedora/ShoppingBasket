package com.adthena.shoppingbasket.actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import com.adthena.shoppingbasket.models.{Basket, Item}
import com.adthena.shoppingbasket.pricing.PricingEngine
import com.adthena.shoppingbasket.util.CurrencyUtil

object ShoppingBasketActor {
  // Messages that the actor can handle
  sealed trait Command
  case class ProcessBasket(items: List[Item], replyTo: ActorRef[Response]) extends Command

  sealed trait Response
  case class BasketTotal(total: String) extends Response

  def apply(pricingEngine: PricingEngine.Engine): Behavior[Command] = Behaviors.receive { (context, message) =>
    message match {
      case ProcessBasket(items, replyTo) =>
        val basket = Basket(items)

        val newBasket = pricingEngine.applyDiscounts(basket)
        val totalPrice = newBasket.calculatePrice
        val formattedTotalPrice = CurrencyUtil.formatCurrency(totalPrice)

        replyTo ! BasketTotal(formattedTotalPrice)
        Behaviors.same
    }
  }
}
