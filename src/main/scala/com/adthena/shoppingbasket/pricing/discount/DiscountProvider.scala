package com.adthena.shoppingbasket.pricing.discount

import akka.actor.typed.{ActorRef, ActorSystem, Scheduler}
import akka.util.Timeout
import com.adthena.shoppingbasket.actors.ItemDiscountActor
import com.adthena.shoppingbasket.pricing.PricingEngine

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.DurationInt

trait DiscountProvider {
  def getDiscounts: List[PricingEngine.BasketDiscount]
  implicit val system: ActorSystem[_]

  implicit val timeout: Timeout = 3.seconds
  implicit val scheduler: Scheduler
  implicit val ec: ExecutionContextExecutor

  implicit val itemDiscountRouter: ActorRef[ItemDiscountActor.Command]
}
