package models;

public class GetOrdersResponse {

    private boolean success;
    private Order[] orders;
    private int total;
    private int totalToday;

    public GetOrdersResponse(boolean success, Order[] orders, int total, int totalToday) {
        this.success = success;
        this.orders = orders;
        this.total = total;
        this.totalToday = totalToday;
    }

    public boolean isSuccess() {
        return success;
    }

    public Order[] getOrders() {
        return orders;
    }

    public int getTotal() {
        return total;
    }

    public int getTotalToday() {
        return totalToday;
    }
}