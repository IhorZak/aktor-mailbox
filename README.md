[![Maven Central](https://maven-badges.herokuapp.com/maven-central/ua.pp.ihorzak/aktor-mailbox/badge.svg)](https://search.maven.org/artifact/ua.pp.ihorzak/aktor-mailbox)

# aktor-mailbox
aktor-mailbox is a Kotlin language library that enables the creation of actors with input message preprocessing capabilities. Actors represent independent units of computation in concurrent and distributed systems that communicate and collaborate with each other to achieve a common goal.

Actors created with aktor-mailbox can process incoming messages before they are handled by the actor's logic. This preprocessing capability allows for the implementation of various behaviors, such as message filtering, transformation, and validation etc.

## Usage
New actor can be created using `CoroutineScope.aktor(...)` extension function, which takes mandatory `Mailbox` parameter and optional `CoroutineContext` parameter:
```kotlin
val sendChannel = scope.aktor(
    mailbox = Mailbox.priority(Int::compareTo),
    context = Dispatchers.Default,
) { message ->
    println(message)
}
```
`Mailbox` argument defines the behavior of actor input messages processing. Library contains implementation of priority queue mailbox (can be created using `Mailbox.priority(...)` extension function) and transformation mailbox (can be created using `Mailbox.transform(...)` extension function). Also custom `Mailbox` implementations are supported.

`CoroutineContext` argument allows to customize couroutines created by the actor.

`block` function argument defines message handling logic.

Returned by `CoroutineScope.aktor(...)` function `SendChannel` should be used to send messages to be processed by actor:
```kotlin
sendChannel.send(1)
sendChannel.send(2)
```

To cancel actor `SendChannel` must be closed:
```kotlin
sendChannel.close()
```

Another way to cancel actor is to close receiver scope:
```kotlin
scope.cancel()
```

## Download
The latest version is available via [Maven Central][1].
For example, to grab it via Gradle you can use next snippet:
```kotlin
buildscript {
    repositories {
        mavenCentral()
    }
}

dependencies {
    implementation("ua.pp.ihorzak:aktor-mailbox:0.1.0")
}
```

## API Reference
[aktor-mailbox API Reference][2]

# License
<pre>
Copyright 2023 Ihor Zakhozhyi

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
</pre>

  [1]: https://search.maven.org/artifact/ua.pp.ihorzak/aktor-mailbox/0.1.0/jar
  [2]: https://ihorzak.github.io/aktor-mailbox/