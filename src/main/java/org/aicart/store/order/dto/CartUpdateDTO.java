package org.aicart.store.order.dto;

public class CartUpdateDTO {
    private int quantity;

    public CartUpdateDTO(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
