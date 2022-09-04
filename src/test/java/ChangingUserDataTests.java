import api.client.UserClient;
import io.qameta.allure.junit4.DisplayName;
import models.ChangingUserDataRequest;
import models.CreateUserRequest;
import models.UserLoginRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;

public class ChangingUserDataTests {

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
    @DisplayName("Check changing user email")
    public void testChangingUserEmail() {
        CreateUserRequest createUserRequest = CreateUserRequest.createRandom();
        userClient.createUser(createUserRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .log().all();

        UserLoginRequest userLoginRequest = new UserLoginRequest(createUserRequest.getEmail(), createUserRequest.getPassword());
        String token = userClient.userLogin(userLoginRequest)
                .then().statusCode(200).extract().body().path("accessToken");

        tokens.add(token);
        String email = RandomStringUtils.randomAlphanumeric(8) + "@mail.ru";
        ChangingUserDataRequest changingUserDataRequest = new ChangingUserDataRequest(email, null, null);
        userClient.changingUserData(token, changingUserDataRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(email.toLowerCase()))
                .body("user.name", equalTo(createUserRequest.getName()))
                .log().all();
    }

    @Test
    @DisplayName("Check changing user email to existing email")
    public void testChangingUserEmailToExistingEmail() {
        CreateUserRequest createUserRequest = CreateUserRequest.createRandom();
        userClient.createUser(createUserRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .log().all();

        UserLoginRequest userLoginRequest = new UserLoginRequest(createUserRequest.getEmail(), createUserRequest.getPassword());
        String firstToken = userClient.userLogin(userLoginRequest)
                .then().statusCode(200).extract().body().path("accessToken");
        tokens.add(firstToken);

        createUserRequest = CreateUserRequest.createRandom();
        String secondToken = userClient.createUser(createUserRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .log().all().extract().body().path("accessToken");
        tokens.add(secondToken);

        ChangingUserDataRequest changingUserDataRequest = new ChangingUserDataRequest(createUserRequest.getEmail(), null, null);
        userClient.changingUserData(firstToken, changingUserDataRequest)
                .then().statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User with such email already exists"))
                .log().all();
    }

    @Test
    @DisplayName("Check changing user password")
    public void testChangingUserPassword() {
        CreateUserRequest createUserRequest = CreateUserRequest.createRandom();
        userClient.createUser(createUserRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .log().all();

        UserLoginRequest userLoginRequest = new UserLoginRequest(createUserRequest.getEmail(), createUserRequest.getPassword());
        String token = userClient.userLogin(userLoginRequest)
                .then().statusCode(200).extract().body().path("accessToken");
        tokens.add(token);

        String password = RandomStringUtils.randomAlphanumeric(8);
        ChangingUserDataRequest changingUserDataRequest = new ChangingUserDataRequest(null, password, null);
        userClient.changingUserData(token, changingUserDataRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(createUserRequest.getEmail().toLowerCase()))
                .body("user.name", equalTo(createUserRequest.getName()))
                .log().all();

        userLoginRequest = new UserLoginRequest(createUserRequest.getEmail(), password);
        userClient.userLogin(userLoginRequest)
                .then().statusCode(200);
    }

    @Test
    @DisplayName("Check changing user name")
    public void testChangingUserName() {
        CreateUserRequest createUserRequest = CreateUserRequest.createRandom();
        userClient.createUser(createUserRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .log().all();

        UserLoginRequest userLoginRequest = new UserLoginRequest(createUserRequest.getEmail(), createUserRequest.getPassword());
        String token = userClient.userLogin(userLoginRequest)
                .then().statusCode(200).extract().body().path("accessToken");
        tokens.add(token);

        String name = RandomStringUtils.randomAlphanumeric(8);
        ChangingUserDataRequest changingUserDataRequest = new ChangingUserDataRequest(null, null, name);
        userClient.changingUserData(token, changingUserDataRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(createUserRequest.getEmail().toLowerCase()))
                .body("user.name", equalTo(name))
                .log().all();
    }

    @Test
    @DisplayName("Check error changing email with unauthorized user")
    public void testErrorChangingEmailWithUnauthorizedUser() {
        CreateUserRequest createUserRequest = CreateUserRequest.createRandom();
        String token = userClient.createUser(createUserRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .log().all().extract().body().path("accessToken");
        tokens.add(token);

        String email = RandomStringUtils.randomAlphanumeric(8) + "@mail.ru";
        ChangingUserDataRequest changingUserDataRequest = new ChangingUserDataRequest(email, null, null);
        userClient.changingUserDataWithoutAuthorization(changingUserDataRequest)
                .then().statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"))
                .log().all();
    }

    @Test
    @DisplayName("Check error changing password with unauthorized user")
    public void testErrorChangingPasswordWithUnauthorizedUser() {
        CreateUserRequest createUserRequest = CreateUserRequest.createRandom();
        String token = userClient.createUser(createUserRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .log().all().extract().body().path("accessToken");
        tokens.add(token);

        String password = RandomStringUtils.randomAlphanumeric(8);
        ChangingUserDataRequest changingUserDataRequest = new ChangingUserDataRequest(null, password, null);
        userClient.changingUserDataWithoutAuthorization(changingUserDataRequest)
                .then().statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"))
                .log().all();
    }

    @Test
    @DisplayName("Check error changing name with unauthorized user")
    public void testErrorChangingNameWithUnauthorizedUser() {
        CreateUserRequest createUserRequest = CreateUserRequest.createRandom();
        String token = userClient.createUser(createUserRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .log().all().extract().body().path("accessToken");
        tokens.add(token);

        String name = RandomStringUtils.randomAlphanumeric(8);
        ChangingUserDataRequest changingUserDataRequest = new ChangingUserDataRequest(null, null, name);
        userClient.changingUserDataWithoutAuthorization(changingUserDataRequest)
                .then().statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"))
                .log().all();
    }
}