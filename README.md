# Shopping Basket

This project is a Scala application that calculates the total price of items in a shopping basket, applying discounts as specified.

## Requirements

- Scala 2.13
- sbt 1.9.9 (Scala Build Tool)
- Java 11.0.25-ms

## Dependencies

The project uses the following libraries:

- Typesafe Config
- ScalaTest
- ScalaCheck

These dependencies are managed via `sbt` and are specified in the `build.sbt` file.

## Building the Project

To build the project, run the following command:

```sh
sbt clean compile assembly
```

This will clean, compile, and create a fat JAR file for the project.

## Running the Application

To run the application, use the following command:

```sh
java -jar target/scala-2.13/ShoppingBasket-assembly-0.1.0-SNAPSHOT.jar <item1> <item2> ...
```

Replace `<item1>`, `<item2>`, etc., with the names of the items you want to add to the basket. For example:

```sh
java -jar target/scala-2.13/ShoppingBasket-assembly-0.1.0-SNAPSHOT.jar Soup Bread Milk
```

## Configuration

The application uses a configuration file (`application.conf`) to define the prices of items. The configuration file should be placed in the `src/main/resources` directory and should look like this:

```hocon
shoppingbasket {
  items {
    Soup = "0.65"
    Bread = "0.80"
    Milk = "1.30"
    Apples = "1.00"
  }
}
```

## Testing

To run the tests, use the following command:

```sh
sbt test
```

This will execute all the unit and property-based tests defined in the project.

## Modules and Classes

### Models

- `Item`: Represents an item in the shopping basket with a name and price.
- `Basket`: Represents a shopping basket containing a list of items. It includes a method to calculate the total price of the items in the basket.

### Pricing Module

The pricing module is responsible for calculating the total price of items in the shopping basket. It includes the following classes:

- `Item`: This class is defined in `src/main/scala/com/adthena/shoppingbasket/models/Item.scala`. It has two properties:
    - `name`: The name of the item.
    - `price`: The price of the item as a `BigDecimal`.

```scala
package com.adthena.shoppingbasket.models

case class Item(name: String, price: BigDecimal)
```

- `Basket`: This class is defined in `src/main/scala/com/adthena/shoppingbasket/models/Basket.scala`. It has one property:
    - `items`: A list of `Item` objects. It also includes a method `calculatePrice` to calculate the total price of the items in the basket.

```scala
package com.adthena.shoppingbasket.models

case class Basket(items: List[Item]) {
  def calculatePrice: BigDecimal = items.map(_.price).sum
}
```

The `calculatePrice` method sums up the prices of all items in the basket to return the total price. This method is tested using both unit tests and property-based tests to ensure its correctness.

### Tests

- `BasketSpec`: This class is defined in `src/test/scala/com/adthena/shoppingbasket/models/BasketSpec.scala`. It includes property-based tests using ScalaCheck to verify the correctness of the `calculatePrice` method in various scenarios.