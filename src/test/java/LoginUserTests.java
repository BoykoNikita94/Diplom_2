import api.client.UserClient;
import io.qameta.allure.junit4.DisplayName;
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

public class LoginUserTests {

    private final UserClient userClient = new UserClient();
    private final List<String> tokens = new ArrayList<>();

    @After
    public void deleteUser() {
        for (String token : tokens) {
            userClient.deleteUser(token);
        }
        tokens.clear();
    }

    @Test
    @DisplayName("Check user authorization")
    public void testUserAuthorizationSuccess() {
        CreateUserRequest createUserRequest = CreateUserRequest.createRandom();
        String token = userClient.createUser(createUserRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .log().all().extract().body().path("accessToken");
        tokens.add(token);

        UserLoginRequest userLoginRequest = new UserLoginRequest(createUserRequest.getEmail(), createUserRequest.getPassword());
        userClient.userLogin(userLoginRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", not(nullValue()))
                .log().all();
    }

    @Test
    @DisplayName("Error authorization without email")
    public void testErrorMessageAuthorizationWithoutEmail() {
        CreateUserRequest createUserRequest = CreateUserRequest.createRandom();
        String token = userClient.createUser(createUserRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .log().all().extract().body().path("accessToken");
        tokens.add(token);

        UserLoginRequest userLoginRequest = new UserLoginRequest(null, createUserRequest.getPassword());
        userClient.userLogin(userLoginRequest)
                .then().statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"))
                .log().all();
    }

    @Test
    @DisplayName("Error authorization without password")
    public void testErrorMessageAuthorizationWithoutPassword() {
        CreateUserRequest createUserRequest = CreateUserRequest.createRandom();
        String token = userClient.createUser(createUserRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .log().all().extract().body().path("accessToken");
        tokens.add(token);

        UserLoginRequest userLoginRequest = new UserLoginRequest(createUserRequest.getEmail(), null);
        userClient.userLogin(userLoginRequest)
                .then().statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"))
                .log().all();
    }

    @Test
    @DisplayName("Error authorization with wrong email")
    public void testErrorMessageAuthorizationWithWrongEmail() {
        CreateUserRequest createUserRequest = CreateUserRequest.createRandom();
        String token = userClient.createUser(createUserRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .log().all().extract().body().path("accessToken");
        tokens.add(token);

        UserLoginRequest userLoginRequest = new UserLoginRequest(RandomStringUtils.randomAlphanumeric(8) + "@mail.ru", createUserRequest.getPassword());
        userClient.userLogin(userLoginRequest)
                .then().statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"))
                .log().all();
    }

    @Test
    @DisplayName("Error authorization with wrong password")
    public void testErrorMessageAuthorizationWithWrongPassword() {
        CreateUserRequest createUserRequest = CreateUserRequest.createRandom();
        String token = userClient.createUser(createUserRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .log().all().extract().body().path("accessToken");
        tokens.add(token);

        UserLoginRequest userLoginRequest = new UserLoginRequest(createUserRequest.getEmail(), RandomStringUtils.randomAlphanumeric(8));
        userClient.userLogin(userLoginRequest)
                .then().statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"))
                .log().all();
    }
}