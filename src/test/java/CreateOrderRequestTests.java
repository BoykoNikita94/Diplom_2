import api.client.OrderClient;
import api.client.UserClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import models.CreateOrderRequest;
import models.CreateUserRequest;
import models.UserLoginRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

public class CreateOrderRequestTests {

    UserClient userClient = new UserClient();
    OrderClient orderClient = new OrderClient();


    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    @DisplayName("Create order with authorized user")
    public void testCreatingOrderWithAuthorizedUserSuccess() {
        String email = RandomStringUtils.randomAlphanumeric(8) + "@mail.ru";
        String password = RandomStringUtils.randomAlphanumeric(8);
        String name = RandomStringUtils.randomAlphanumeric(8);
        CreateUserRequest createUserRequest = new CreateUserRequest(email, password, name);
        userClient.createUser(createUserRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .log().all();

        UserLoginRequest userLoginRequest = new UserLoginRequest(email, password);
        String token = userClient.userLogin(userLoginRequest)
                .then().statusCode(200).extract().body().path("accessToken");

        String ingredientId = orderClient.ingredients().then().extract().body().path("data[0]._id");
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(new String[]{ingredientId});
        orderClient.createOrder(token, createOrderRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", not(nullValue()));

        userClient.deleteUser(token);
    }

    @Test
    @DisplayName("Create order with unauthorized user")
    public void testCreatingOrderWithUnauthorizedUserSuccess() {
        String ingredientId = orderClient.ingredients().then().extract().body().path("data[0]._id");
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(new String[]{ingredientId});
        orderClient.createOrderWithoutAuthorization(createOrderRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", not(nullValue()));
    }

    @Test
    @DisplayName("Create order without ingredients")
    public void testErrorCreatingOrderWithoutIngredients() {
        String email = RandomStringUtils.randomAlphanumeric(8) + "@mail.ru";
        String password = RandomStringUtils.randomAlphanumeric(8);
        String name = RandomStringUtils.randomAlphanumeric(8);
        CreateUserRequest createUserRequest = new CreateUserRequest(email, password, name);
        userClient.createUser(createUserRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .log().all();

        UserLoginRequest userLoginRequest = new UserLoginRequest(email, password);
        String token = userClient.userLogin(userLoginRequest)
                .then().statusCode(200).extract().body().path("accessToken");

        CreateOrderRequest createOrderRequest = new CreateOrderRequest(null);
        orderClient.createOrder(token, createOrderRequest)
                .then().statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));

        userClient.deleteUser(token);
    }

    @Test
    @DisplayName("Create order with wrong ingredient")
    public void testErrorCreatingOrderWithWrongIngredient() {
        String email = RandomStringUtils.randomAlphanumeric(8) + "@mail.ru";
        String password = RandomStringUtils.randomAlphanumeric(8);
        String name = RandomStringUtils.randomAlphanumeric(8);
        CreateUserRequest createUserRequest = new CreateUserRequest(email, password, name);
        userClient.createUser(createUserRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .log().all();

        UserLoginRequest userLoginRequest = new UserLoginRequest(email, password);
        String token = userClient.userLogin(userLoginRequest)
                .then().statusCode(200).extract().body().path("accessToken");

        CreateOrderRequest createOrderRequest = new CreateOrderRequest(new String[]{RandomStringUtils.randomAlphanumeric(8)});
        orderClient.createOrder(token, createOrderRequest)
                .then().statusCode(500);

        userClient.deleteUser(token);
    }
}