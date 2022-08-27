package models;

import java.util.Date;

public class Order {

    String[] ingredients;
    String _id;
    String status;
    int number;
    Date createdAt;
    Date updatedAt;

    public Order(String[] ingredients, String _id, String status, int number, Date createdAt, Date updatedAt) {
        this.ingredients = ingredients;
        this._id = _id;
        this.status = status;
        this.number = number;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
