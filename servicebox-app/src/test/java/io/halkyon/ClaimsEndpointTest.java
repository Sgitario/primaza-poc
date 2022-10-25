package io.halkyon;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import java.util.function.UnaryOperator;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.IsNot.not;

@QuarkusTest
public class ClaimsEndpointTest {

    private static final UnaryOperator<String> CLAIM = fruit ->  String.format("{\"name\":\"%s\"}", fruit);

    @Test
    public void testAddFruit(){

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/json")
                .body(CLAIM.apply("Oracle"))
                .when().post("/claims")
                .then()
                .statusCode(201)
                .body(containsString("Oracle"))
                .body("id", notNullValue())
                .extract().body().jsonPath().getString("id");
    }

    @Test
    public void testFindByName(){

        given()
                .when().get("/claims")
                .then()
                .statusCode(200)
                .body(
                        containsString("mysql-demo"),
                        containsString("postgresql-team-dev"),
                        containsString("postgresql-team-test"),
                        containsString("mariadb-demo"),
                        containsString("postgresql-13"));

        given()
                .when().get("/claims/mysql-demo")
                .then()
                .statusCode(200)
                .body(containsString("mysql-demo"));
    }

    @Test
    public void testClaimEntity() {
        final String path="/claims";
        //List all
        given()
                .when().get(path)
                .then()
                .statusCode(200)
                .body(
                        containsString("mysql-demo"),
                        containsString("postgresql-team-dev"),
                        containsString("postgresql-team-test"),
                        containsString("mariadb-demo"),
                        containsString("postgresql-13"));

        //Delete the Cherry:
        given()
                .when().delete(path + "/1")
                .then()
                .statusCode(204);

        //List all, cherry should be missing now:
        given()
                .when().get(path)
                .then()
                .statusCode(200)
                .body(
                        not(containsString("mysql-demo")),
                                containsString("postgresql-team-dev"),
                                containsString("postgresql-team-test"),
                                containsString("mariadb-demo"),
                                containsString("postgresql-13"));
    }

}
