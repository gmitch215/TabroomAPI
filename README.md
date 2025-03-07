# TabroomAPI

> Multiplatform API for Tabroom.com

## Overview

[**Tabroom.com**](https://tabroom.com) is a platform used by the Speech and Debate community to manage tournaments and post results.
Since it does not have an official API, this repository scrapes the website to provide an API for developers to use.

## Installation

Maven
```xml
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
dependencies {
    implementation 'xyz.gmitch215:tabroom-api:[VERSION]'
}
```

Gradle (Kotlin DSL)
```kts
dependencies {
    implementation("xyz.gmitch215:tabroom-api:[VERSION]")
}
```

NPM
```bash
npm install @gmitch215/tabroom-api
```

## Usage

```kotlin
import xyz.gmitch215.tabroom.api.getTournament
import xyz.gmitch215.tabroom.api.Entry
import xyz.gmitch215.tabroom.api.Event
import xyz.gmitch215.tabroom.api.Tournament

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

import xyz.gmitch215.tabroom.api.Entry;
import xyz.gmitch215.tabroom.api.Event;
import xyz.gmitch215.tabroom.api.TabroomAPI;
import xyz.gmitch215.tabroom.api.Tournament;

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

```js
import * as tabroom from '@gmitch215/tabroom-api';

tabroom.xyz.gmitch215.tabroom.api.getTournament(30082)
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