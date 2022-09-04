package models;

public class ChangingUserDataRequest {

    private String email;
    private String password;
    private String name;

    public ChangingUserDataRequest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
}