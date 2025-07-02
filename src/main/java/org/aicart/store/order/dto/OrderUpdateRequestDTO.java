package org.aicart.store.order.dto;

import jakarta.validation.Valid;
import org.aicart.PaymentTypeEnum;
import org.aicart.store.order.OrderStatusEnum;

import java.math.BigInteger;
import java.util.List;

public class OrderUpdateRequestDTO {
    
    // Customer information (can be updated for guest orders)
    @Valid
    private CustomerInfoUpdateDTO customerInfo;
    
    @Valid
    private OrderBillingUpdateDTO billing;
    
    @Valid
    private OrderShippingUpdateDTO shipping;
    
    // Order items (can add/remove/update items)
    @Valid
    private List<OrderItemUpdateDTO> items;
    
    // Pricing (recalculated when items change)
    private BigInteger subTotal;
    private BigInteger totalDiscount;
    private BigInteger shippingCost;
    private BigInteger totalTax;
    private BigInteger totalPrice;
    
    private String currency;
    
    // Payment
    private PaymentTypeEnum paymentType;
    
    // Order settings
    private OrderStatusEnum status;
    private String notes; // Admin notes
    private String customerNotes; // Customer notes
    private String couponCode;
    private String referenceNumber; // External reference
    
    // Shipping
    private String shippingMethod;
    private String deliveryInstructions;
    
    // Update metadata
    private String updateReason; // Why the order was updated
    private Boolean notifyCustomer = false; // Whether to notify customer of changes

    // Constructors
    public OrderUpdateRequestDTO() {}

    // Getters and Setters
    public CustomerInfoUpdateDTO getCustomerInfo() { return customerInfo; }
    public void setCustomerInfo(CustomerInfoUpdateDTO customerInfo) { this.customerInfo = customerInfo; }

    public OrderBillingUpdateDTO getBilling() { return billing; }
    public void setBilling(OrderBillingUpdateDTO billing) { this.billing = billing; }

    public OrderShippingUpdateDTO getShipping() { return shipping; }
    public void setShipping(OrderShippingUpdateDTO shipping) { this.shipping = shipping; }

    public List<OrderItemUpdateDTO> getItems() { return items; }
    public void setItems(List<OrderItemUpdateDTO> items) { this.items = items; }

    public BigInteger getSubTotal() { return subTotal; }
    public void setSubTotal(BigInteger subTotal) { this.subTotal = subTotal; }

    public BigInteger getTotalDiscount() { return totalDiscount; }
    public void setTotalDiscount(BigInteger totalDiscount) { this.totalDiscount = totalDiscount; }

    public BigInteger getShippingCost() { return shippingCost; }
    public void setShippingCost(BigInteger shippingCost) { this.shippingCost = shippingCost; }

    public BigInteger getTotalTax() { return totalTax; }
    public void setTotalTax(BigInteger totalTax) { this.totalTax = totalTax; }

    public BigInteger getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigInteger totalPrice) { this.totalPrice = totalPrice; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public PaymentTypeEnum getPaymentType() { return paymentType; }
    public void setPaymentType(PaymentTypeEnum paymentType) { this.paymentType = paymentType; }

    public OrderStatusEnum getStatus() { return status; }
    public void setStatus(OrderStatusEnum status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCustomerNotes() { return customerNotes; }
    public void setCustomerNotes(String customerNotes) { this.customerNotes = customerNotes; }

    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public String getShippingMethod() { return shippingMethod; }
    public void setShippingMethod(String shippingMethod) { this.shippingMethod = shippingMethod; }

    public String getDeliveryInstructions() { return deliveryInstructions; }
    public void setDeliveryInstructions(String deliveryInstructions) { this.deliveryInstructions = deliveryInstructions; }

    public String getUpdateReason() { return updateReason; }
    public void setUpdateReason(String updateReason) { this.updateReason = updateReason; }

    public Boolean getNotifyCustomer() { return notifyCustomer; }
    public void setNotifyCustomer(Boolean notifyCustomer) { this.notifyCustomer = notifyCustomer; }

    // Nested DTOs
    public static class CustomerInfoUpdateDTO {
        private String firstName;
        private String lastName;
        private String email;
        private String phone;

        // Getters and Setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }

    public static class OrderBillingUpdateDTO {
        private String fullName;
        private String email;
        private String phone;
        private String line1;
        private String line2;
        private String city;
        private String state;
        private String country;
        private String postalCode;
        private String vatNumber;
        private String taxNumber;

        // Getters and Setters
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getLine1() { return line1; }
        public void setLine1(String line1) { this.line1 = line1; }

        public String getLine2() { return line2; }
        public void setLine2(String line2) { this.line2 = line2; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getState() { return state; }
        public void setState(String state) { this.state = state; }

        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }

        public String getPostalCode() { return postalCode; }
        public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

        public String getVatNumber() { return vatNumber; }
        public void setVatNumber(String vatNumber) { this.vatNumber = vatNumber; }

        public String getTaxNumber() { return taxNumber; }
        public void setTaxNumber(String taxNumber) { this.taxNumber = taxNumber; }
    }

    public static class OrderShippingUpdateDTO {
        private String fullName;
        private String phone;
        private String line1;
        private String line2;
        private String city;
        private String state;
        private String country;
        private String postalCode;

        // Getters and Setters
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getLine1() { return line1; }
        public void setLine1(String line1) { this.line1 = line1; }

        public String getLine2() { return line2; }
        public void setLine2(String line2) { this.line2 = line2; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getState() { return state; }
        public void setState(String state) { this.state = state; }

        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }

        public String getPostalCode() { return postalCode; }
        public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    }

    public static class OrderItemUpdateDTO {
        private Long id; // Existing item ID (null for new items)
        private Long productId;
        private Long variantId;
        private Integer quantity;
        private BigInteger price; // Unit price
        private BigInteger tax;
        private BigInteger discount;
        private String notes; // Item-specific notes
        private Boolean remove = false; // Mark item for removal

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public Long getVariantId() { return variantId; }
        public void setVariantId(Long variantId) { this.variantId = variantId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public BigInteger getPrice() { return price; }
        public void setPrice(BigInteger price) { this.price = price; }

        public BigInteger getTax() { return tax; }
        public void setTax(BigInteger tax) { this.tax = tax; }

        public BigInteger getDiscount() { return discount; }
        public void setDiscount(BigInteger discount) { this.discount = discount; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }

        public Boolean getRemove() { return remove; }
        public void setRemove(Boolean remove) { this.remove = remove; }
    }
}
