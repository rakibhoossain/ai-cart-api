package org.aicart.store.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import org.aicart.PaymentTypeEnum;
import org.aicart.store.order.OrderStatusEnum;

import java.math.BigInteger;
import java.util.List;

public class OrderCreateRequestDTO {
    
    // Customer information
    private Long customerId; // Optional - for guest orders
    
    @Valid
    @NotNull
    private CustomerInfoRequestDTO customerInfo;
    
    @Valid
    @NotNull
    private OrderBillingRequestDTO billing;
    
    @Valid
    private OrderShippingRequestDTO shipping; // Optional for digital products
    
    // Order items
    @NotEmpty
    @Valid
    private List<OrderItemRequestDTO> items;
    
    // Pricing
    @NotNull
    @Positive
    private BigInteger subTotal;
    
    private BigInteger totalDiscount = BigInteger.ZERO;
    private BigInteger shippingCost = BigInteger.ZERO;
    private BigInteger totalTax = BigInteger.ZERO;
    
    @NotNull
    @Positive
    private BigInteger totalPrice;
    
    @NotNull
    private String currency = "USD";
    
    // Payment
    @NotNull
    private PaymentTypeEnum paymentType;
    
    // Order settings
    private OrderStatusEnum initialStatus = OrderStatusEnum.PENDING;
    private String notes; // Admin notes
    private String customerNotes; // Customer notes
    private String couponCode;
    private String referenceNumber; // External reference
    
    // Shipping
    private String shippingMethod;
    private String deliveryInstructions;
    
    // Constructors
    public OrderCreateRequestDTO() {}

    // Getters and Setters
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public CustomerInfoRequestDTO getCustomerInfo() { return customerInfo; }
    public void setCustomerInfo(CustomerInfoRequestDTO customerInfo) { this.customerInfo = customerInfo; }

    public OrderBillingRequestDTO getBilling() { return billing; }
    public void setBilling(OrderBillingRequestDTO billing) { this.billing = billing; }

    public OrderShippingRequestDTO getShipping() { return shipping; }
    public void setShipping(OrderShippingRequestDTO shipping) { this.shipping = shipping; }

    public List<OrderItemRequestDTO> getItems() { return items; }
    public void setItems(List<OrderItemRequestDTO> items) { this.items = items; }

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

    public OrderStatusEnum getInitialStatus() { return initialStatus; }
    public void setInitialStatus(OrderStatusEnum initialStatus) { this.initialStatus = initialStatus; }

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

    // Nested DTOs
    public static class CustomerInfoRequestDTO {
        private String firstName;
        private String lastName;
        @NotNull
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

    public static class OrderBillingRequestDTO {
        @NotNull
        private String fullName;
        @NotNull
        private String email;
        private String phone;
        @NotNull
        private String line1;
        private String line2;
        @NotNull
        private String city;
        @NotNull
        private String state;
        @NotNull
        private String country;
        @NotNull
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

    public static class OrderShippingRequestDTO {
        @NotNull
        private String fullName;
        private String phone;
        @NotNull
        private String line1;
        private String line2;
        @NotNull
        private String city;
        @NotNull
        private String state;
        @NotNull
        private String country;
        @NotNull
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

    public static class OrderItemRequestDTO {
        @NotNull
        private Long productId;
        @NotNull
        private Long variantId;
        @NotNull
        @Positive
        private Integer quantity;
        @NotNull
        @Positive
        private BigInteger price; // Unit price
        private BigInteger tax = BigInteger.ZERO;
        private BigInteger discount = BigInteger.ZERO;
        private String notes; // Item-specific notes

        // Getters and Setters
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
    }
}
