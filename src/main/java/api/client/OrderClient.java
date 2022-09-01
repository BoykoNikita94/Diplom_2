package api.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.CreateOrderRequest;

import static io.restassured.RestAssured.given;

public class OrderClient extends RestAssuredClient {

    private final String ORDER_URI = "/api/orders";
    private final String INGREDIENTS_URI = "/api/ingredients";

    @Step("Create order")
    public Response createOrder(String token, CreateOrderRequest createOrderRequest) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .header("Authorization", token)
                .body(createOrderRequest)
                .when()
                .post(ORDER_URI);
    }

    @Step("Create order without authorization")
    public Response createOrderWithoutAuthorization(CreateOrderRequest createOrderRequest) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(createOrderRequest)
                .when()
                .post(ORDER_URI);
    }

    @Step("Get ingredients")
    public Response ingredients() {
        return given()
                .header("Content-type", "application/json")
                .and()
                .when()
                .get(INGREDIENTS_URI);
    }

    @Step("Get orders")
    public Response orders(String token) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .header("Authorization", token)
                .when()
                .get(ORDER_URI);
    }
}