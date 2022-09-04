import api.client.UserClient;
import io.qameta.allure.junit4.DisplayName;
import models.CreateUserRequest;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;

public class CreateUserTests {

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
    @DisplayName("Check user creation")
    public void testCreateUserSuccess() {
        CreateUserRequest createUserRequest = CreateUserRequest.createRandom();
        String token = userClient.createUser(createUserRequest)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .log().all().extract().body().path("accessToken");
        tokens.add(token);
    }

    @Test
    @DisplayName("Error creating identical users")
    public void testErrorMessageCreatingIdenticalUsers() {
        CreateUserRequest createUserRequest = CreateUserRequest.createRandom();
        String token = userClient.createUser(createUserRequest)
                .then().statusCode(200)
                .log().all().extract().body().path("accessToken");
        tokens.add(token);

        userClient.createUser(createUserRequest)
                .then().statusCode(403).assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"))
                .log().all();
    }
}