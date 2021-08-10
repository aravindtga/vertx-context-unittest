package com.aravind;

import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.Vertx;

import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class ReactiveGreetingResourceTest {

    @Inject
    Vertx vertx;

    @Test
    public void testHelloEndpoint() {
        //The below test case should be a failure
        vertx.runOnContext(unused -> {
            given()
                    .when().get("/hello")
                    .then()
                    .statusCode(500) //This is 200
                    .body(is("Hello RESTEasy Reactive"));
        });
    }

}