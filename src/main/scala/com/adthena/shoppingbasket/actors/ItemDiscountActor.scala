package com.adthena.shoppingbasket.actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.adthena.shoppingbasket.models.Item

object ItemDiscountActor {
  sealed trait Command
  case class ApplyItemDiscount(item: Item, replyTo: akka.actor.typed.ActorRef[Item]) extends Command

  def apply(discountFunction: Item => Item): Behavior[Command] = Behaviors.receiveMessage {
    case ApplyItemDiscount(item, replyTo) =>
      val discountedItem = discountFunction(item)
      replyTo ! discountedItem
      Behaviors.same
  }
}