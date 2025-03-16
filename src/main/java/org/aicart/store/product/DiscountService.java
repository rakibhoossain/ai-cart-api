package org.aicart.store.product;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.aicart.store.product.dto.DiscountDTO;
import org.aicart.store.product.entity.Discount;
import org.aicart.store.product.mapper.DiscountMapper;
import java.util.Optional;

@ApplicationScoped
public class DiscountService {


    @Transactional
    public DiscountDTO createDiscount(DiscountDTO discountDTO) {

        // Check if coupon already exists (and discount is not automatic)
        if (!discountDTO.isAutomatic && discountDTO.coupon != null) {
            Optional<Discount> existingDiscount = Discount.find("coupon", discountDTO.coupon).firstResultOptional();
            if (existingDiscount.isPresent()) {
                throw new IllegalArgumentException("Coupon already exists. Please use a different one.");
            }
        }

        Discount discount = DiscountMapper.toEntity(discountDTO);
        discount.persist();
        return DiscountMapper.toDTO(discount);
    }
}
