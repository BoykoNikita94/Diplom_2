package api.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.ChangingUserDataRequest;
import models.CreateUserRequest;
import models.UserLoginRequest;

import static io.restassured.RestAssured.given;

public class UserClient {
    @Step("Create user")
    public Response createUser(CreateUserRequest createUserRequest) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(createUserRequest)
                .when()
                .post("/api/auth/register");
    }

    @Step("User login")
    public Response userLogin(UserLoginRequest userLoginRequest) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(userLoginRequest)
                .post("/api/auth/login");
    }

    @Step("Changing user data")
    public Response changingUserData(String token, ChangingUserDataRequest changingUserDataRequest) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .header("Authorization", token)
                .body(changingUserDataRequest)
                .when()
                .patch("/api/auth/user");
    }

    @Step("Changing user data without authorization")
    public Response changingUserDataWithoutAuthorization(ChangingUserDataRequest changingUserDataRequest) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(changingUserDataRequest)
                .when()
                .patch("/api/auth/user");
    }

    @Step("Delete user")
    public void deleteUser(String token) {
        given()
                .header("Content-type", "application/json")
                .and()
                .header("Authorization", token)
                .when()
                .delete("/api/auth/user")
                .then().statusCode(202);

    }
}