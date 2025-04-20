package org.aicart.store.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.aicart.store.product.ProductCollectionFieldEnum;
import org.aicart.store.product.ProductCollectionOperatorEnum;
import org.aicart.store.product.validation.ValidCondition;

@ValidCondition
public class ProductCollectionConditionDTO {
        @NotNull(message = "Field required")
        @JsonProperty("field")
        public ProductCollectionFieldEnum field;

        @NotNull(message = "Operator required")
        @JsonProperty("operator")
        public ProductCollectionOperatorEnum operator;

        @NotNull(message = "Value required")
        @JsonProperty("value")
        public Object value; // Can be String or Number

        // Custom validation
        public boolean isValid() {
            if (field.equals(ProductCollectionFieldEnum.PRICE)) {
                return value instanceof Number;
            }
            return value instanceof String && !((String) value).isEmpty();
        }
}
