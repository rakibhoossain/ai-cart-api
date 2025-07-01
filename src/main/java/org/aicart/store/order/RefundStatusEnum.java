package org.aicart.store.order;

public enum RefundStatusEnum {
    PENDING,        // Refund request submitted
    APPROVED,       // Refund approved by admin
    PROCESSING,     // Refund being processed
    COMPLETED,      // Refund completed
    REJECTED,       // Refund rejected
    CANCELLED       // Refund cancelled
}
