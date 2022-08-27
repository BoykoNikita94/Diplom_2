package models;

public class CreateOrderRequest {

    String[] ingredients;

    public CreateOrderRequest(String[] ingridients) {
        this.ingredients = ingridients;
    }
}
