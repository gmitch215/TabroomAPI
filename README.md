# TabroomAPI

> Multiplatform API for Tabroom.com

## Overview

[**Tabroom.com**](https://tabroom.com) is a platform used by the Speech and Debate community to manage tournaments and post results.
Since it does not have an official API, this repository scrapes the website to provide an API for developers to use.

## Installation

Maven
```xml
<!-- Add Calculus Games Repository -->

<repositories>
    <repository>
        <id>calculus-games</id>
        <url>https://repo.calcugames.xyz/repository/maven-public/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>xyz.gmitch215</groupId>
        <artifactId>tabroom-api</artifactId>
        <version>[VERSION]</version>
    </dependency>
</dependencies>
```

Gradle (Groovy)
```groovy
// Add Calculus Games Repository
repositories {
    maven { url 'https://repo.calcugames.xyz/repository/maven-public/' }
}

dependencies {
    implementation 'xyz.gmitch215:tabroom-api:[VERSION]'
}
```

Gradle (Kotlin DSL)
```kts
// Add Calculus Games Repository
repositories {
    maven("https://repo.calcugames.xyz/repository/maven-public/")
}

dependencies {
    implementation("xyz.gmitch215:tabroom-api:[VERSION]")
}
```