# WireMock with delayed callbacks

Demonstration of extending [WireMock](http://wiremock.org) to trigger a delayed
callback.

Here you can find:

- A [WireMock extension](src/main/kotlin/mjs/wiremock/DelayedCallback.kt)
  that responds to a [contract for delayed 
  callback](src/main/kotlin/mjs/wiremock/Contract.kt) and calls back
  some time later as specified by its parameters.
  
- An [extension method on WireMock Parameters](src/main/kotlin/mjs/wiremock/Parameters.kt)
  class for reading Kotlin `Double` values.
  
- A functional test of delayed callback that can be run using
  [Batect](https://batect.dev) with `./batect callbackTest`

[Extending WireMock for delayed callbacks](https://blog.michaelstrasser.com/2021/03/extending-wiremock-for-delayed-callbacks/)
describes the extension in more detail.
