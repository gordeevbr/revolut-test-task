# Revolut Test Task

A test task for Revolut.

## Conditions

Design and implement a RESTful API (including data model and the backing implementation) for
money transfers between accounts.

Explicit requirements:

1. You can use Java or Kotlin.
2. Keep it simple and to the point (e.g. no need to implement any authentication).
3. Assume the API is invoked by multiple systems and services on behalf of end users.
4. You can use frameworks/libraries if you like (except Spring), but don't forget about
requirement #2 and keep it simple and avoid heavy frameworks.
5. The datastore should run in-memory for the sake of this test.
6. The final result should be executable as a standalone program (should not require a
pre-installed container/server).
7. Demonstrate with tests that the API works as expected.

Implicit requirements:

1. The code produced by you is expected to be of high quality.
2. There are no detailed requirements, use common sense.

## Comments on implementation

##### 1. Time and status

This solution has been implemented in a bit less than 16 hours, of which almost half of the time was spent setting up 
the technologies and the framework. The solution should be satisfying the requirements completely. 

##### 2. API and usage

The solution uses gradle for build/dependencies and should be runnable from the main class: 
`com.gordeevbr.revolut.RevolutApplication`. You can find the application's Feign API in the 
`com.gordeevbr.revolut.apis` package. 

##### 3. Improvements and considerations

Framework. Since there was an explicit requirement to not use any Spring technologies, I've decided to experiment and make a 
frameworkless solution. Thinking back on it, it was a bad decision. It is obviously as minimalistic as it can get, but 
it would take a whole lot more time than that to make it a production-ready solution.

Configuration. The configuration is hard-coded right now. It should be externalized.

Tests. These E2E tests should be enough to completely cover the 'business requirements' and the business logic,
but there should be unit tests also, especially for the framework stuff. Unfortunately, these would be more technical 
than informative, and I did not have enough time in this example to implement them.

API. It would make sense to put all the API-related files in a separate module, but it's out of scope.

Database. H2 is not a production-ready solution. It should be enough to prove the point, but it doesn't cut it for a 
range of reasons that it would be too long to list.