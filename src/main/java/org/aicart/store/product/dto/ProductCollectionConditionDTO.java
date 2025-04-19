package org.aicart.store.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.aicart.store.product.validation.ValidCondition;

@ValidCondition
public class ProductCollectionConditionDTO {
        @NotBlank(message = "Field type required")
        @JsonProperty("field")
        public String field;

        @NotBlank(message = "Operator required")
        @JsonProperty("operator")
        public String operator;

        @NotNull(message = "Value required")
        @JsonProperty("value")
        public Object value; // Can be String or Number

        // Custom validation
        public boolean isValid() {
            if ("price".equals(field)) {
                return value instanceof Number;
            }
            return value instanceof String && !((String) value).isEmpty();
        }
}
