package org.aicart.store.order;

public enum OrderStatusEnum {
        PENDING,           // Order is created but not processed
        CONFIRMED,         // Payment confirmed
        PROCESSING,        // Order is being processed
        PACKED,            // Order is packed and ready to ship
        SHIPPED,           // Order is shipped
        OUT_FOR_DELIVERY,  // Order is out for delivery
        DELIVERED,         // Order delivered to customer
        COMPLETED,         // Order completed successfully
        CANCELED,          // Order canceled
        REFUNDED,          // Order payment refunded
        PARTIALLY_REFUNDED,// Order partially refunded
        RETURNED,          // Order returned by customer
        EXCHANGED,         // Order exchanged
        FAILED             // Order failed (payment failed, etc.)
}
