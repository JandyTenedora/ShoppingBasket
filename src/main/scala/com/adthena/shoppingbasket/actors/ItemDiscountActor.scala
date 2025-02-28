package com.adthena.shoppingbasket.actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.adthena.shoppingbasket.models.Item
import org.slf4j.LoggerFactory

object ItemDiscountActor {
  sealed trait Command
  case class ApplyItemDiscount(item: Item, discountFunction: Item => Item, replyTo: akka.actor.typed.ActorRef[Item]) extends Command

  val log = LoggerFactory.getLogger(this.getClass)

  def apply(): Behavior[Command] =
    Behaviors.setup { context =>
      log.debug(s"ItemDiscountActor started: ${context.self.path}")
      Behaviors.receiveMessage {
        case ApplyItemDiscount(item, discountFunction, replyTo) =>
          val discountedItem = discountFunction(item)
          replyTo ! discountedItem
          Behaviors.same
      }
    }
}