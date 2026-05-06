# Development

This branch targets Minecraft `26.1` with Stonecutter prepared for future version nodes.

## Requirements

- Java 25
- Gradle wrapper from this repository

## Useful Tasks

```sh
./gradlew :26.1:build
./gradlew :26.1:runClient
./gradlew :26.1:buildAndCollect
```

## Adding Minecraft Versions

When adding another Minecraft version, add a new `versions/<minecraft>/gradle.properties` node and update the Stonecutter version matrix.
