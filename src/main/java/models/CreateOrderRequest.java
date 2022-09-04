package models;

public class CreateOrderRequest {

    private String[] ingredients;

    public CreateOrderRequest(String[] ingridients) {
        this.ingredients = ingridients;
    }
}