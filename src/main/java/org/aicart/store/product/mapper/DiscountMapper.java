package org.aicart.store.product.mapper;

import org.aicart.store.product.dto.DiscountDTO;
import org.aicart.store.product.entity.Discount;
import org.aicart.store.product.entity.ProductCollection;
import org.aicart.store.product.entity.ProductVariant;
import org.aicart.store.user.entity.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DiscountMapper {

    public static DiscountDTO toDTO(Discount discount) {
        DiscountDTO dto = new DiscountDTO();
        dto.id = discount.id;
        dto.discountType = discount.discountType;
        dto.isAutomatic = discount.isAutomatic;
        dto.coupon = discount.coupon;
        dto.title = discount.title;
        dto.startAt = discount.startAt;
        dto.endAt = discount.endAt;
        dto.amountType = discount.amountType;
        dto.amount = discount.amount;
        dto.isActive = discount.isActive;
        dto.purchaseType = discount.purchaseType;
        dto.appliesTo = discount.appliesTo;
        dto.minAmount = discount.minAmount;
        dto.minQuantity = discount.minQuantity;
        dto.combinations = discount.combinations;
        dto.locations = discount.locations;
        dto.maxUse = discount.maxUse;
        dto.maxCustomerUse = discount.maxCustomerUse;
        dto.eligibilityType = discount.eligibilityType;
        return dto;
    }

    public static List<DiscountDTO> toDTOList(List<Discount> discounts) {
        return discounts.stream().map(DiscountMapper::toDTO).collect(Collectors.toList());
    }

    public static Discount toEntity(DiscountDTO dto) {
        Discount discount = new Discount();
        discount.discountType = dto.discountType;
        discount.isAutomatic = dto.isAutomatic;
        discount.coupon = dto.coupon;
        discount.title = dto.title;
        discount.startAt = dto.startAt;
        discount.endAt = dto.endAt;
        discount.amountType = dto.amountType;
        discount.amount = dto.amount;
        discount.isActive = dto.isActive;
        discount.purchaseType = dto.purchaseType;
        discount.appliesTo = dto.appliesTo;
        discount.minAmount = dto.minAmount;
        discount.minQuantity = dto.minQuantity;
        discount.combinations = dto.combinations;
        discount.locations = dto.locations;
        discount.maxUse = dto.maxUse;
        discount.maxCustomerUse = dto.maxCustomerUse;
        discount.eligibilityType = dto.eligibilityType;

        // Fetch variants by IDs and set them
        if (dto.variantIds != null && !dto.variantIds.isEmpty()) {
            List<ProductVariant> variants = ProductVariant.list("id IN ?1", dto.variantIds);
            discount.variants = Set.copyOf(variants);
        }

        // Fetch collections by IDs and set them
        if (dto.collectionIds != null && !dto.collectionIds.isEmpty()) {
            List<ProductCollection> collections = ProductCollection.list("id IN ?1", dto.collectionIds);
            discount.collections = Set.copyOf(collections);
        }

        // Fetch customers by IDs and set them
        if (dto.customerIds != null && !dto.customerIds.isEmpty()) {
            List<User> customers = User.list("id IN ?1", dto.customerIds);
            discount.customers = Set.copyOf(customers);
        }

        return discount;
    }


    public static Discount toUpdate(Discount discount, DiscountDTO dto) {
        discount.coupon = dto.coupon;
        discount.title = dto.title;
        discount.startAt = dto.startAt;
        discount.endAt = dto.endAt;
        discount.amountType = dto.amountType;
        discount.amount = dto.amount;
        discount.isActive = dto.isActive;
        discount.purchaseType = dto.purchaseType;
        discount.appliesTo = dto.appliesTo;
        discount.minAmount = dto.minAmount;
        discount.minQuantity = dto.minQuantity;
        discount.combinations = dto.combinations;
        discount.locations = dto.locations;
        discount.maxUse = dto.maxUse;
        discount.maxCustomerUse = dto.maxCustomerUse;
        discount.eligibilityType = dto.eligibilityType;

        // Fetch variants by IDs and set them
        if (dto.variantIds != null && !dto.variantIds.isEmpty()) {
            List<ProductVariant> variants = ProductVariant.list("id IN ?1", dto.variantIds);
            discount.variants = Set.copyOf(variants);
        }

        // Fetch collections by IDs and set them
        if (dto.collectionIds != null && !dto.collectionIds.isEmpty()) {
            List<ProductCollection> collections = ProductCollection.list("id IN ?1", dto.collectionIds);
            discount.collections = Set.copyOf(collections);
        }

        // Fetch customers by IDs and set them
        if (dto.customerIds != null && !dto.customerIds.isEmpty()) {
            List<User> customers = User.list("id IN ?1", dto.customerIds);
            discount.customers = Set.copyOf(customers);
        }

        return discount;
    }
}
