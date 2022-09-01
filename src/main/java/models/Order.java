package models;

import java.util.Date;

public class Order {

    private String[] ingredients;
    private String _id;
    private String status;
    private int number;
    private Date createdAt;
    private Date updatedAt;

    public Order(String[] ingredients, String _id, String status, int number, Date createdAt, Date updatedAt) {
        this.ingredients = ingredients;
        this._id = _id;
        this.status = status;
        this.number = number;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
