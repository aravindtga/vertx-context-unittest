# Fixing Unit Test cases when using Mutiny.Session

In repository class Mutiny.Session is used to do the DB operations (**_DataRepository.class_**)

## Exception when testing the DB operations in Unit tests

When the repository method being verified in unit test below exception occurs (**_ReactiveGreetingResourceTest#testDbOperationWithException_**)

```text
java.lang.IllegalStateException: HR000068: This method should exclusively be invoked from a Vert.x EventLoop thread; currently running on thread 'main'

	at org.hibernate.reactive.common.InternalStateAssertions.assertUseOnEventLoop(InternalStateAssertions.java:40)
	at org.hibernate.reactive.mutiny.impl.MutinySessionFactoryImpl.proxyConnection(MutinySessionFactoryImpl.java:150)
	at org.hibernate.reactive.mutiny.impl.MutinySessionFactoryImpl.openSession(MutinySessionFactoryImpl.java:69)
	at io.quarkus.hibernate.reactive.runtime.ReactiveSessionProducer.createMutinySession(ReactiveSessionProducer.java:23)
	at io.quarkus.hibernate.reactive.runtime.ReactiveSessionProducer_ProducerMethod_createMutinySession_1321d110ee9e92bda147899150401e0a136779c7_Bean.create(ReactiveSessionProducer_ProducerMethod_createMutinySession_1321d110ee9e92bda147899150401e0a136779c7_Bean.zig:247)
	at io.quarkus.hibernate.reactive.runtime
```

## Fixing the unit using session factory

The exception shown above can be fixed by using Mutiny.SessionFactory(**_ReactiveGreetingResourceTest#testDbOperationWithSessionFactory_**)
and it works as expected

## Fixing using unit test using vertx.runOnContext

When trying to fix the same exception using vertx.runOnContext(**_ReactiveGreetingResourceTest#testDbOperationWithVertx_**), 
It shows test has been successful independent of what is happening in the test.

Fixing using vertx.runOnContext is required when the DB operation is not invoked directly from UT(But invoked internally)
(**_ReactiveGreetingResourceTest#testUtilInsertWithException_**). At these cases the exception shown above occurs in UT.

Fixing that with vertx.runOnContext doesn't report the actual failures, it returns success. (_**ReactiveGreetingResourceTest#testUtilInsertWithVertx**_)
