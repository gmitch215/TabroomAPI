# TabroomAPI

> Multiplatform API for Tabroom.com

## Overview

[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.21-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
![GitHub License](https://img.shields.io/github/license/gmitch215/TabroomAPI)

![badge-jvm](http://img.shields.io/badge/platform-jvm-DB413D.svg?style=flat)
![badge-c](http://img.shields.io/badge/platform-c-044F88.svg?style=flat)
![badge-android](http://img.shields.io/badge/platform-android-6EDB8D.svg?style=flat)
![badge-js](https://img.shields.io/badge/platform-js-F8DB5D.svg?style=flat)
![badge-mac](http://img.shields.io/badge/platform-macos-cccccc.svg?style=flat)
![badge-ios](http://img.shields.io/badge/platform-ios-aaaaaa.svg?style=flat)
![badge-watchos](http://img.shields.io/badge/platform-watchos-222222.svg?style=flat)
![badge-tvos](http://img.shields.io/badge/platform-tvos-eaeaea.svg?style=flat)
![badge-linux-x64](http://img.shields.io/badge/platform-linux--x64-2D3F6C.svg?style=flat)
![badge-windows](http://img.shields.io/badge/platform-windows-4D76CD.svg?style=flat)

[**Tabroom.com**](https://tabroom.com) is a platform used by the Speech and Debate community to manage tournaments and post results.
Since it does not have an official API, this repository scrapes the website to provide an API for developers to use.

### What about `api.tabroom.com`?

- `api.tabroom.com` is a private API used by the Tabroom website that requires authentication.
- It's also missing a lot of the stuff you would need (like ballot information, judge paradigms, etc.)

It's a good resource for getting surface-level information about yourself when you log in, but it doesn't provide the same level of detail as this API.

## Installation

Maven
```xml
<dependencies>
    <dependency>
        <groupId>dev.gmitch215</groupId>
        <artifactId>tabroom-api</artifactId>
        <version>[VERSION]</version>
    </dependency>
</dependencies>
```

Gradle (Groovy)
```groovy
dependencies {
    implementation 'dev.gmitch215:tabroom-api:[VERSION]'
}
```

Gradle (Kotlin DSL)
```kts
dependencies {
    implementation("dev.gmitch215:tabroom-api:[VERSION]")
}
```

NPM
```bash
npm install @gmitch215/tabroom-api
```

C/C++
```c
#include <libtabroom_api_api.h>
```

```bash
gcc -o my_program my_program.c -ltabroom_api
```

## Usage

```kotlin
import dev.gmitch215.tabroom.api.getTournament
import dev.gmitch215.tabroom.api.Entry
import dev.gmitch215.tabroom.api.Event
import dev.gmitch215.tabroom.api.Tournament

fun main() {
    val tourney = getTournament(30082) // IDC Varsity State Championships 2024
    
    val description = tourney.description
    
    for (event in tourney.events) {
        println("${event.name} Entries:") // Public Forum, Lincoln Douglas, Policy, Extemporanous Speaking, etc.
        
        for (entry in event.entries) {
            val school = entry.school // School name
            val fullName = entry.name // Full name of the student
            
            println("$fullName from $school is competing!")
        }
    }
}
```

### Java

```java

import dev.gmitch215.tabroom.api.Entry;
import dev.gmitch215.tabroom.api.Event;
import dev.gmitch215.tabroom.api.TabroomAPI;
import dev.gmitch215.tabroom.api.Tournament;

public class Main {
    public static void main(String[] args) {
        // IDC Varsity State Championships 2024
        Tournament tourney = TabroomAPI.getTournament(30082);
        
        String description = tourney.getDescription();
        
        for (Event event: tourney.getEvents()) {
            System.out.println(event.getName() + " Entries:"); // Public Forum, Lincoln Douglas, Policy, Extemporanous Speaking, etc.
            
            for (Entry entry : event.getEntries()) {
                String school = entry.getSchool(); // School name
                String fullName = entry.getName(); // Full name of the student
                
                System.out.println(fullName + " from " + school + " is competing!");
            }
        }
    }
}
```

### JavaScript

#### Browser

```html
<script src="https://cdn.gmitch215.dev/lib/TabroomAPI/tabroom-api-<version>.js"></script>
```

```js
tabroom.dev.gmitch215.tabroom.api.getTournament(30082)
    .then(tournament => {
        alert(tournament.description);
        tournament.events.forEach(event => {
            alert(event.name + " Entries:");
            event.entries.forEach(entry => {
                alert(entry.name + " from " + entry.school + " is competing!");
            });
        });
    })
    .catch(err => {
        alert(err);
    });
```

#### NodeJS

```js
import * as tabroom from '@gmitch215/tabroom-api';

tabroom.dev.gmitch215.tabroom.api.getTournament(30082)
    .then(tournament => {
        console.log(tournament.description);
        tournament.events.forEach(event => {
            console.log(event.name + " Entries:");
            event.entries.forEach(entry => {
                console.log(entry.name + " from " + entry.school + " is competing!");
            });
        });
    })
    .catch(err => {
        console.error(err);
    });
```

## Contributing

Contributions are welcome! Feel free to open an issue or submit a pull request.
