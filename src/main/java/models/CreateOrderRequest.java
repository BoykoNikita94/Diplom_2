package models;

public class CreateOrderRequest {

    public String[] ingredients;

    public CreateOrderRequest(String[] ingridients) {
        this.ingredients = ingridients;
    }
}