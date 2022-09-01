import api.client.OrderClient;
import api.client.UserClient;
import io.qameta.allure.junit4.DisplayName;
import models.CreateOrderRequest;
import models.CreateUserRequest;
import models.GetOrdersResponse;
import models.UserLoginRequest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

public class GetOrdersTests {

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
    @DisplayName("Get orders with authorized user")
    public void testGetOrdersWithAuthorizedUserSuccess() {
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

        GetOrdersResponse getOrdersResponse = orderClient.orders(token)
                .as(GetOrdersResponse.class);
        Assert.assertTrue(getOrdersResponse.isSuccess());
        Assert.assertEquals(1, getOrdersResponse.getOrders().length);
    }

    @Test
    @DisplayName("Error getting orders with unauthorized user")
    public void testErrorGettingOrdersWithUnauthorizedUserSuccess() {
        given()
                .header("Content-type", "application/json")
                .and()
                .when()
                .get("/api/orders")
                .then().statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}