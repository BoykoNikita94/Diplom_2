package api.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.ChangingUserDataRequest;
import models.CreateUserRequest;
import models.UserLoginRequest;

import static io.restassured.RestAssured.given;

public class UserClient extends RestAssuredClient {

    private final String REGISTER_URI = "/api/auth/register";
    private final String LOGIN_URI = "/api/auth/login";
    private final String USER_URI = "/api/auth/user";

    @Step("Create user")
    public Response createUser(CreateUserRequest createUserRequest) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(createUserRequest)
                .when()
                .post(REGISTER_URI);
    }

    @Step("User login")
    public Response userLogin(UserLoginRequest userLoginRequest) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(userLoginRequest)
                .post(LOGIN_URI);
    }

    @Step("Changing user data")
    public Response changingUserData(String token, ChangingUserDataRequest changingUserDataRequest) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .header("Authorization", token)
                .body(changingUserDataRequest)
                .when()
                .patch(USER_URI);
    }

    @Step("Changing user data without authorization")
    public Response changingUserDataWithoutAuthorization(ChangingUserDataRequest changingUserDataRequest) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(changingUserDataRequest)
                .when()
                .patch(USER_URI);
    }

    @Step("Delete user")
    public void deleteUser(String token) {
        given()
                .header("Content-type", "application/json")
                .and()
                .header("Authorization", token)
                .when()
                .delete(USER_URI)
                .then().statusCode(202);
    }
}