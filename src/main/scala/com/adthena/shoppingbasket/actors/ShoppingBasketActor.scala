package com.adthena.shoppingbasket.actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import com.adthena.shoppingbasket.models.{Basket, Item}
import com.adthena.shoppingbasket.pricing.PricingEngine

object ShoppingBasketActor {
  // Messages that the actor can handle
  sealed trait Command
  case class ProcessBasket(items: List[Item], replyTo: ActorRef[Response]) extends Command

  sealed trait Response
  case class BasketTotal(total: BigDecimal) extends Response

  def apply(pricingEngine: PricingEngine.Engine): Behavior[Command] = Behaviors.receive { (_, message) =>
    message match {
      case ProcessBasket(items, replyTo) =>
        val basket = Basket(items)

        val newBasket = pricingEngine.applyDiscounts(basket)
        val totalPrice = newBasket.calculatePrice

        replyTo ! BasketTotal(totalPrice)
        Behaviors.same
    }
  }
}
