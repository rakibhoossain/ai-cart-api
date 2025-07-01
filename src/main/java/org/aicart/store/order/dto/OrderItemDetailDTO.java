package org.aicart.store.order.dto;

import java.math.BigInteger;

public class OrderItemDetailDTO {
    private Long id;
    private Long productId;
    private String productName;
    private String productSlug;
    private Long variantId;
    private String variantName;
    private String variantSku;
    private Integer quantity;
    private BigInteger price;
    private BigInteger tax;
    private BigInteger discount;
    private BigInteger totalPrice;
    private String productImage;

    // Constructors
    public OrderItemDetailDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductSlug() { return productSlug; }
    public void setProductSlug(String productSlug) { this.productSlug = productSlug; }

    public Long getVariantId() { return variantId; }
    public void setVariantId(Long variantId) { this.variantId = variantId; }

    public String getVariantName() { return variantName; }
    public void setVariantName(String variantName) { this.variantName = variantName; }

    public String getVariantSku() { return variantSku; }
    public void setVariantSku(String variantSku) { this.variantSku = variantSku; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigInteger getPrice() { return price; }
    public void setPrice(BigInteger price) { this.price = price; }

    public BigInteger getTax() { return tax; }
    public void setTax(BigInteger tax) { this.tax = tax; }

    public BigInteger getDiscount() { return discount; }
    public void setDiscount(BigInteger discount) { this.discount = discount; }

    public BigInteger getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigInteger totalPrice) { this.totalPrice = totalPrice; }

    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }
}
