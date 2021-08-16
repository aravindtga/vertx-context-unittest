package com.aravind;

import com.aravind.repository.Data;
import com.aravind.repository.DataRepository;
import com.aravind.repository.Utils;

import org.hibernate.reactive.mutiny.Mutiny;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
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
    void testUtilInsertWithUniAssertSubscriber() {
        Data data = new Data();
        data.setId(2L);
        data.setName("UtilName");
        UniAssertSubscriber<Data> subscriber = utils.insertData(2L).subscribe().withSubscriber(UniAssertSubscriber.create());
        subscriber.awaitItem().assertItem(data).assertCompleted().assertTerminated();
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