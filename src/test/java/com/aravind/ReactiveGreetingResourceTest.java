package com.aravind;

import com.aravind.repository.Data;
import com.aravind.repository.DataRepository;

import org.hibernate.reactive.mutiny.Mutiny;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.Vertx;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class ReactiveGreetingResourceTest {

    @Inject
    DataRepository dataRepository;

    @Inject
    Vertx vertx;

    @Inject
    Mutiny.SessionFactory mutinySessionFactory;

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/hello")
                .then()
                .statusCode(200);

    }

    @Test
    public void testDbOperationWithVertx() {
        vertx.runOnContext(unused -> {
            Data data = new Data();
            data.setId(2L);
            data.setName("test name");
            Data receivedData = dataRepository.persist(data).await().indefinitely();
            Assertions.assertEquals(data, "failure");
        });
    }

    @Test
    public void testDbOperationWithSessionFactory() {
        Data data = new Data();
        data.setId(2L);
        data.setName("test name");
        Data receivedData = mutinySessionFactory.withSession(session ->
            dataRepository.persist(data)).await().indefinitely();
        Assertions.assertEquals(data, receivedData);
    }

}