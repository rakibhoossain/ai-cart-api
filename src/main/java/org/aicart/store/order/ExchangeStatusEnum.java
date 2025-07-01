package org.aicart.store.order;

public enum ExchangeStatusEnum {
    PENDING,        // Exchange request submitted
    APPROVED,       // Exchange approved by admin
    PROCESSING,     // Exchange being processed
    COMPLETED,      // Exchange completed
    REJECTED,       // Exchange rejected
    CANCELLED       // Exchange cancelled
}
