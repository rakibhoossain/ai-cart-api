package org.aicart.store.order;

public enum OrderStatusEnum {
        PENDING,       // Order is created but not processed
        CONFIRMED,     // Payment confirmed
        PROCESSING,    // Order is being processed
        SHIPPED,       // Order is shipped
        DELIVERED,     // Order delivered to customer
        CANCELED,      // Order canceled
        REFUNDED       // Order payment refunded
}
