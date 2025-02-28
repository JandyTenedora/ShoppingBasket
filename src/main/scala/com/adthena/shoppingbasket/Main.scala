package com.adthena.shoppingbasket

import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, SpawnProtocol}
import akka.util.Timeout
import com.adthena.shoppingbasket.actors.ShoppingBasketActor
import com.adthena.shoppingbasket.models.Item
import com.adthena.shoppingbasket.pricing.PricingEngine
import com.adthena.shoppingbasket.pricing.discount.{DefaultDiscountProvider, DiscountProvider}
import com.typesafe.config.ConfigFactory

import scala.collection.convert.ImplicitConversions.`map AsScala`
import scala.concurrent.Await
import scala.concurrent.duration._

object Main extends App {
  // Load the configuration
  val config = ConfigFactory.load()
  val itemsConfig = config.getConfig("shoppingbasket.items")
  val akkaConfig = config.getConfig("akka")

  implicit val system: ActorSystem[SpawnProtocol.Command] =
    ActorSystem(Behaviors.setup[SpawnProtocol.Command](_ => SpawnProtocol()), "shoppingBasketSystem", akkaConfig)

  // Create a map of items from the configuration
  val items = itemsConfig.root().unwrapped().toMap.collect {
    case (key: String, value: String) =>
      key -> BigDecimal(value)
  }

  // Create a list of items based on command-line arguments
  private val basketItems = args.flatMap(item => items.get(item).map(price => Item(item, price)))

  // Create actor system and the shopping basket actor
  private val defaultDiscountProvider: DiscountProvider = new DefaultDiscountProvider
  private val pricingEngine = new PricingEngine.Engine(defaultDiscountProvider)

  private val shoppingBasketActor = system.systemActorOf(ShoppingBasketActor(pricingEngine), "shoppingBasketActor")

  val subTotal = basketItems.map(_.price).sum
  println(s"Subtotal: $subTotal")

  // Use Akka's Ask pattern to get a response from the actor
  implicit val timeout: Timeout = 3.seconds
  val responseFuture = shoppingBasketActor.ask(ref => ShoppingBasketActor.ProcessBasket(basketItems.toList, ref))

  // Await and print the response
  val response = Await.result(responseFuture, 3.seconds)
  response match {
    case ShoppingBasketActor.BasketTotal(totalPrice) =>
      println(s"Total Price: $totalPrice")
  }

  // Shutdown the system after processing
  system.terminate()
}
