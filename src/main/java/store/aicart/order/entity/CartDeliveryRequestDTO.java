package store.aicart.order.entity;

public class CartDeliveryRequestDTO {
    private String deliveryMethod;
    private String paymentMethod;
    private String couponCode;

    public CartDeliveryRequestDTO(String deliveryMethod, String paymentMethod, String couponCode) {
        this.deliveryMethod = deliveryMethod;
        this.paymentMethod = paymentMethod;
        this.couponCode = couponCode;
    }

    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }
}
