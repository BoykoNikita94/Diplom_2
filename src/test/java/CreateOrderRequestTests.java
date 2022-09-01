import api.client.OrderClient;
import api.client.UserClient;
import io.qameta.allure.junit4.DisplayName;
import models.CreateOrderRequest;
import models.CreateUserRequest;
import models.UserLoginRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

public class CreateOrderRequestTests {

    private final UserClient userClient = new UserClient();
    private final OrderClient orderClient = new OrderClient();
    private final List<String> tokens = new ArrayList<>();

    @After
    public void deleteUser() {
        for (String token : tokens) {
            userClient.deleteUser(token);
        }
        tokens.clear();
    }

    @Test
    @DisplayName("Create order with authorized user")
    public void testCreatingOrderWithAuthorizedUserSuccess() {
        CreateUserRequest createUserRequest = CreateUserRequest.createRandom();
        userClient.createUser(createUserRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .log().all();

        UserLoginRequest userLoginRequest = new UserLoginRequest(createUserRequest.getEmail(), createUserRequest.getPassword());
        String token = userClient.userLogin(userLoginRequest)
                .then().statusCode(200).extract().body().path("accessToken");
        tokens.add(token);

        String ingredientId = orderClient.ingredients().then().extract().body().path("data[0]._id");
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(new String[]{ingredientId});
        orderClient.createOrder(token, createOrderRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", not(nullValue()));
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
        CreateUserRequest createUserRequest = CreateUserRequest.createRandom();
        userClient.createUser(createUserRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .log().all();

        UserLoginRequest userLoginRequest = new UserLoginRequest(createUserRequest.getEmail(), createUserRequest.getPassword());
        String token = userClient.userLogin(userLoginRequest)
                .then().statusCode(200).extract().body().path("accessToken");
        tokens.add(token);

        CreateOrderRequest createOrderRequest = new CreateOrderRequest(null);
        orderClient.createOrder(token, createOrderRequest)
                .then().statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Create order with wrong ingredient")
    public void testErrorCreatingOrderWithWrongIngredient() {
        CreateUserRequest createUserRequest = CreateUserRequest.createRandom();
        userClient.createUser(createUserRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .log().all();

        UserLoginRequest userLoginRequest = new UserLoginRequest(createUserRequest.getEmail(), createUserRequest.getPassword());
        String token = userClient.userLogin(userLoginRequest)
                .then().statusCode(200).extract().body().path("accessToken");
        tokens.add(token);

        CreateOrderRequest createOrderRequest = new CreateOrderRequest(new String[]{RandomStringUtils.randomAlphanumeric(8)});
        orderClient.createOrder(token, createOrderRequest)
                .then().statusCode(500);
    }
}