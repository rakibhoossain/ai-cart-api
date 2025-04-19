package org.aicart.store.product.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CollectionTypeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCollectionType {
    String message() default "Invalid collection configuration";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}