import api.client.UserClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import models.CreateUserRequest;
import models.UserLoginRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

public class LoginUserTests {

    UserClient userClient = new UserClient();

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    @DisplayName("Check user authorization")
    public void testUserAuthorizationSuccess() {
        String email = RandomStringUtils.randomAlphanumeric(8) + "@mail.ru";
        String password = RandomStringUtils.randomAlphanumeric(8);
        String name = RandomStringUtils.randomAlphanumeric(8);
        CreateUserRequest createUserRequest = new CreateUserRequest(email, password, name);
        String token = userClient.createUser(createUserRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .log().all().extract().body().path("accessToken");

        UserLoginRequest userLoginRequest = new UserLoginRequest(email, password);
        userClient.userLogin(userLoginRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", not(nullValue()))
                .log().all();

        userClient.deleteUser(token);
    }

    @Test
    @DisplayName("Error authorization without email")
    public void testErrorMessageAuthorizationWithoutEmail() {
        String email = RandomStringUtils.randomAlphanumeric(8) + "@mail.ru";
        String password = RandomStringUtils.randomAlphanumeric(8);
        String name = RandomStringUtils.randomAlphanumeric(8);
        CreateUserRequest createUserRequest = new CreateUserRequest(email, password, name);
        String token = userClient.createUser(createUserRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .log().all().extract().body().path("accessToken");

        UserLoginRequest userLoginRequest = new UserLoginRequest(null, password);
        userClient.userLogin(userLoginRequest)
                .then().statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"))
                .log().all();

        userClient.deleteUser(token);
    }

    @Test
    @DisplayName("Error authorization without password")
    public void testErrorMessageAuthorizationWithoutPassword() {
        String email = RandomStringUtils.randomAlphanumeric(8) + "@mail.ru";
        String password = RandomStringUtils.randomAlphanumeric(8);
        String name = RandomStringUtils.randomAlphanumeric(8);
        CreateUserRequest createUserRequest = new CreateUserRequest(email, password, name);
        String token = userClient.createUser(createUserRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .log().all().extract().body().path("accessToken");

        UserLoginRequest userLoginRequest = new UserLoginRequest(email, null);
        userClient.userLogin(userLoginRequest)
                .then().statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"))
                .log().all();

        userClient.deleteUser(token);
    }

    @Test
    @DisplayName("Error authorization with wrong email")
    public void testErrorMessageAuthorizationWithWrongEmail() {
        String email = RandomStringUtils.randomAlphanumeric(8) + "@mail.ru";
        String password = RandomStringUtils.randomAlphanumeric(8);
        String name = RandomStringUtils.randomAlphanumeric(8);
        CreateUserRequest createUserRequest = new CreateUserRequest(email, password, name);
        String token = userClient.createUser(createUserRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .log().all().extract().body().path("accessToken");

        UserLoginRequest userLoginRequest = new UserLoginRequest(RandomStringUtils.randomAlphanumeric(8) + "@mail.ru", password);
        userClient.userLogin(userLoginRequest)
                .then().statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"))
                .log().all();

        userClient.deleteUser(token);
    }

    @Test
    @DisplayName("Error authorization with wrong password")
    public void testErrorMessageAuthorizationWithWrongPassword() {
        String email = RandomStringUtils.randomAlphanumeric(8) + "@mail.ru";
        String password = RandomStringUtils.randomAlphanumeric(8);
        String name = RandomStringUtils.randomAlphanumeric(8);
        CreateUserRequest createUserRequest = new CreateUserRequest(email, password, name);
        String token = userClient.createUser(createUserRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .log().all().extract().body().path("accessToken");

        UserLoginRequest userLoginRequest = new UserLoginRequest(email, RandomStringUtils.randomAlphanumeric(8));
        userClient.userLogin(userLoginRequest)
                .then().statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"))
                .log().all();

        userClient.deleteUser(token);
    }
}