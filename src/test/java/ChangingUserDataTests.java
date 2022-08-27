import api.client.UserClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import models.ChangingUserDataRequest;
import models.CreateUserRequest;
import models.UserLoginRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class ChangingUserDataTests {
    UserClient userClient = new UserClient();

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    @DisplayName("Check changing user email")
    public void testChangingUserEmail() {
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

        email = RandomStringUtils.randomAlphanumeric(8) + "@mail.ru";
        ChangingUserDataRequest changingUserDataRequest = new ChangingUserDataRequest(email, null, null);
        userClient.changingUserData(token, changingUserDataRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(email.toLowerCase()))
                .body("user.name", equalTo(name))
                .log().all();

        userClient.deleteUser(token);
    }

    @Test
    @DisplayName("Check changing user email to existing email")
    public void testChangingUserEmailToExistingEmail() {
        String email = RandomStringUtils.randomAlphanumeric(8) + "@mail.ru";
        String password = RandomStringUtils.randomAlphanumeric(8);
        String name = RandomStringUtils.randomAlphanumeric(8);
        CreateUserRequest createUserRequest = new CreateUserRequest(email, password, name);
        userClient.createUser(createUserRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .log().all();

        UserLoginRequest userLoginRequest = new UserLoginRequest(email, password);
        String firstToken = userClient.userLogin(userLoginRequest)
                .then().statusCode(200).extract().body().path("accessToken");

        email = RandomStringUtils.randomAlphanumeric(8) + "@mail.ru";
        createUserRequest = new CreateUserRequest(email, password, name);
        String secondToken = userClient.createUser(createUserRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .log().all().extract().body().path("accessToken");

        ChangingUserDataRequest changingUserDataRequest = new ChangingUserDataRequest(email, null, null);
        userClient.changingUserData(firstToken, changingUserDataRequest)
                .then().statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User with such email already exists"))
                .log().all();

        userClient.deleteUser(firstToken);
        userClient.deleteUser(secondToken);
    }

    @Test
    @DisplayName("Check changing user password")
    public void testChangingUserPassword() {
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

        password = RandomStringUtils.randomAlphanumeric(8);
        ChangingUserDataRequest changingUserDataRequest = new ChangingUserDataRequest(null, password, null);
        userClient.changingUserData(token, changingUserDataRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(email.toLowerCase()))
                .body("user.name", equalTo(name))
                .log().all();

        userLoginRequest = new UserLoginRequest(email, password);
        userClient.userLogin(userLoginRequest)
                .then().statusCode(200);

        userClient.deleteUser(token);

    }

    @Test
    @DisplayName("Check changing user name")
    public void testChangingUserName() {
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

        name = RandomStringUtils.randomAlphanumeric(8);
        ChangingUserDataRequest changingUserDataRequest = new ChangingUserDataRequest(null, null, name);
        userClient.changingUserData(token, changingUserDataRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(email.toLowerCase()))
                .body("user.name", equalTo(name))
                .log().all();

        userClient.deleteUser(token);
    }

    @Test
    @DisplayName("Check error changing data unauthorized user")
    public void testErrorChangingUnauthorizedUserData() {
        String email = RandomStringUtils.randomAlphanumeric(8) + "@mail.ru";
        String password = RandomStringUtils.randomAlphanumeric(8);
        String name = RandomStringUtils.randomAlphanumeric(8);
        CreateUserRequest createUserRequest = new CreateUserRequest(email, password, name);
        String token = userClient.createUser(createUserRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .log().all().extract().body().path("accessToken");

        email = RandomStringUtils.randomAlphanumeric(8) + "@mail.ru";
        ChangingUserDataRequest changingUserDataRequest = new ChangingUserDataRequest(email, null, null);
        userClient.changingUserDataWithoutAuthorization(changingUserDataRequest)
                .then().statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"))
                .log().all();

        password = RandomStringUtils.randomAlphanumeric(8);
        changingUserDataRequest = new ChangingUserDataRequest(null, password, null);
        userClient.changingUserDataWithoutAuthorization(changingUserDataRequest)
                .then().statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"))
                .log().all();

        name = RandomStringUtils.randomAlphanumeric(8);
        changingUserDataRequest = new ChangingUserDataRequest(null, null, name);
        userClient.changingUserDataWithoutAuthorization(changingUserDataRequest)
                .then().statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"))
                .log().all();

        userClient.deleteUser(token);
    }
}