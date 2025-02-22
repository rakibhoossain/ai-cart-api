package org.aicart.store.product.dto.product;

public class ProductShippingDTO {
    private int weight;

    private String weightUnit;

    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }

    public String getWeightUnit() { return weightUnit; }
    public void setWeightUnit(String weightUnit) { this.weightUnit = weightUnit; }
}
