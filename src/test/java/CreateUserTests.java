import api.client.UserClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import models.CreateUserRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.Matchers.equalTo;

@RunWith(Parameterized.class)
public class CreateUserTests {

    UserClient userClient = new UserClient();

    String email;
    String password;
    String name;

    public CreateUserTests(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    @DisplayName("Check user creation")
    public void testCreateUserSuccess() {
        String email = RandomStringUtils.randomAlphanumeric(8) + "@mail.ru";
        String password = RandomStringUtils.randomAlphanumeric(8);
        String name = RandomStringUtils.randomAlphanumeric(8);
        CreateUserRequest createUserRequest = new CreateUserRequest(email, password, name);
        String token = userClient.createUser(createUserRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .log().all().extract().body().path("accessToken");

        userClient.deleteUser(token);
    }

    @Test
    @DisplayName("Error creating identical users")
    public void testErrorMessageCreatingIdenticalUsers() {
        String email = RandomStringUtils.randomAlphanumeric(8) + "@mail.ru";
        String password = RandomStringUtils.randomAlphanumeric(8);
        String name = RandomStringUtils.randomAlphanumeric(8);
        CreateUserRequest createUserRequest = new CreateUserRequest(email, password, name);
        String token = userClient.createUser(createUserRequest)
                .then().statusCode(200)
                .log().all().extract().body().path("accessToken");

        userClient.createUser(createUserRequest)
                .then().statusCode(403).assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"))
                .log().all();

        userClient.deleteUser(token);
    }

    @Parameterized.Parameters
    public static Object[][] getData() {
        return new Object[][]{
                {null, RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(8)},
                {RandomStringUtils.randomAlphanumeric(8) + "@mail.ru", null, RandomStringUtils.randomAlphanumeric(8)},
                {RandomStringUtils.randomAlphanumeric(8) + "@mail.ru", RandomStringUtils.randomAlphanumeric(8), null},
        };
    }

    @Test
    @DisplayName("Error creating identical users")
    public void testErrorMessageCreatingUserWithoutRequiredField() {
        CreateUserRequest createUserRequest = new CreateUserRequest(email, password, name);
        userClient.createUser(createUserRequest)
                .then().statusCode(403).assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"))
                .log().all();
    }
}