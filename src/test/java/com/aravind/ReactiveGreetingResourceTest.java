package com.aravind;

import com.aravind.repository.Data;
import com.aravind.repository.DataRepository;
import com.aravind.repository.Utils;

import org.hibernate.reactive.mutiny.Mutiny;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.vertx.RunOnVertxContext;
import io.quarkus.test.junit.vertx.UniAsserter;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import io.vertx.core.Vertx;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class ReactiveGreetingResourceTest {

    @Inject
    DataRepository dataRepository;

    @Inject
    Vertx vertx;

    @Inject
    Mutiny.SessionFactory mutinySessionFactory;

    @Inject
    Utils utils;

    @Test
    void testHelloEndpoint() {
        given()
                .when().get("/hello")
                .then()
                .statusCode(200);
    }

    @Test
    void testDbOperationWithException() {
        Data data = new Data();
        data.setId(2L);
        data.setName("test name");
        Data receivedData = dataRepository.persist(data).await().indefinitely();
        Assertions.assertEquals(data, receivedData);
    }

    @Test
    void testDbOperationWithVertx() {
        vertx.runOnContext(unused -> {
            Data data = new Data();
            data.setId(2L);
            data.setName("test name");
            dataRepository.persist(data).await().indefinitely();
            Assertions.assertEquals(data, "failure");
        });
    }

    @Test
    void testDbOperationWithSessionFactory() {
        Data data = new Data();
        data.setId(2L);
        data.setName("test name");
        Data receivedData = mutinySessionFactory.withSession(session ->
                dataRepository.persist(data)).await().indefinitely();
        Assertions.assertEquals(data, receivedData);
    }

    @Test
    void testUtilInsertWithException() {
        Data data = new Data();
        data.setId(2L);
        data.setName("UtilName");
        Data receivedData = utils.insertData(2L).await().indefinitely();
        Assertions.assertEquals(data, receivedData);
    }

    @Test
    @RunOnVertxContext
    void testUtilInsertWorking(UniAsserter uniAsserter) {
        Data data = new Data();
        data.setId(2L);
        data.setName("UtilName");
        uniAsserter.assertThat(() -> utils.insertData(2L), data1 -> {
            Assertions.assertEquals(data1.getName(), data.getName());
        });
    }

    @Test
    void testUtilInsertWithUniAssertSubscriber() {
        Data data = new Data();
        data.setId(2L);
        data.setName("UtilName");
        UniAssertSubscriber<Data> subscriber = utils.insertData(2L).subscribe().withSubscriber(UniAssertSubscriber.create());
        subscriber.awaitItem().assertItem(data).assertCompleted().assertTerminated();
    }

    @Test
    void testUtilInsertWithExecuteBlocking() {
        Data data = new Data();
        data.setId(8L);
        data.setName("UtilName1");
        vertx.executeBlocking(promise -> {
            Data receivedData = utils.insertData(2L).await().indefinitely();
            Assertions.assertEquals(data, receivedData);
        });
    }

    @Test
    void testUtilInsertWithVertx() {
        vertx.runOnContext(unused -> {
            Data data = new Data();
            data.setId(2L);
            data.setName("UtilName");
            utils.insertData(2L).await().indefinitely();
            Assertions.assertEquals(data, null);
        });
    }

}