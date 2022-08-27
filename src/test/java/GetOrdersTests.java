import api.client.OrderClient;
import api.client.UserClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import models.CreateOrderRequest;
import models.CreateUserRequest;
import models.GetOrdersResponse;
import models.UserLoginRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

public class GetOrdersTests {

    UserClient userClient = new UserClient();
    OrderClient orderClient = new OrderClient();


    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    @DisplayName("Get orders with authorized user")
    public void testGetOrdersWithAuthorizedUserSuccess() {
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

        GetOrdersResponse getOrdersResponse = orderClient.orders(token)
                .as(GetOrdersResponse.class);
        Assert.assertTrue(getOrdersResponse.isSuccess());
        Assert.assertEquals(1, getOrdersResponse.getOrders().length);

        userClient.deleteUser(token);
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