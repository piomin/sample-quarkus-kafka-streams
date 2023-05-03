package pl.piomin.samples.streams.order;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class OrderServiceAppTests {

    @Test
    void startup() {
        given().get("/q/health")
                .then()
                .statusCode(200);
    }
}
