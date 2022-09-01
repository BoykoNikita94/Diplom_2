import api.client.UserClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.CreateUserRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;

@RunWith(Parameterized.class)
public class CreateUserParametrizedTests {

    private final UserClient userClient = new UserClient();
    private final List<String> tokens = new ArrayList<>();

    private final String email;
    private final String password;
    private final String name;

    public CreateUserParametrizedTests(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @After
    public void deleteUser() {
        for (String token : tokens) {
            userClient.deleteUser(token);
        }
        tokens.clear();
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
        Response response = userClient.createUser(createUserRequest);
        if (response.getStatusCode() == 200) {
            String token = response
                    .then().extract().body().path("accessToken");
            tokens.add(token);
        }

        userClient.createUser(createUserRequest)
                .then().statusCode(403).assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"))
                .log().all();
    }
}