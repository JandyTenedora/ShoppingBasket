# Use the sbt and scala image
FROM sbtscala/scala-sbt:eclipse-temurin-jammy-11.0.22_7_1.9.9_2.13.12

# Set the working directory in the container
WORKDIR /app

# Copy the project files to the container
COPY . /app

# Run sbt build command
RUN sbt clean compile assembly

# Run the application
ENTRYPOINT ["java", "-jar", "target/scala-2.13/ShoppingBasket-assembly-0.1.0-SNAPSHOT.jar"]