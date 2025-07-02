package org.aicart.store.order;

public enum OrderLogTypeEnum {
    // Order lifecycle
    ORDER_CREATED,
    ORDER_UPDATED,
    ORDER_CANCELLED,
    ORDER_DELETED,
    
    // Status changes
    STATUS_CHANGED,
    PAYMENT_STATUS_CHANGED,
    
    // Financial operations
    PAYMENT_RECEIVED,
    PAYMENT_FAILED,
    PAYMENT_REFUNDED,
    REFUND_CREATED,
    REFUND_PROCESSED,
    REFUND_CANCELLED,
    
    // Exchange operations
    EXCHANGE_REQUESTED,
    EXCHANGE_APPROVED,
    EXCHANGE_PROCESSED,
    EXCHANGE_CANCELLED,
    
    // Credit operations
    CREDIT_NOTE_ISSUED,
    CREDIT_NOTE_USED,
    CREDIT_NOTE_EXPIRED,
    CREDIT_NOTE_CANCELLED,
    
    // Shipping operations
    SHIPPING_LABEL_CREATED,
    PACKAGE_SHIPPED,
    TRACKING_UPDATED,
    DELIVERY_ATTEMPTED,
    PACKAGE_DELIVERED,
    PACKAGE_RETURNED,
    
    // Inventory operations
    INVENTORY_ALLOCATED,
    INVENTORY_RELEASED,
    INVENTORY_ADJUSTED,
    
    // Customer interactions
    CUSTOMER_CONTACTED,
    CUSTOMER_RESPONSE,
    CUSTOMER_COMPLAINT,
    CUSTOMER_FEEDBACK,
    
    // Admin actions
    ADMIN_NOTE_ADDED,
    ADMIN_OVERRIDE,
    BULK_ACTION_APPLIED,
    
    // System events
    SYSTEM_UPDATE,
    AUTOMATED_ACTION,
    INTEGRATION_SYNC,
    ERROR_OCCURRED,
    
    // Fraud and security
    FRAUD_CHECK_PASSED,
    FRAUD_CHECK_FAILED,
    SECURITY_ALERT,
    
    // Notifications
    EMAIL_SENT,
    SMS_SENT,
    NOTIFICATION_SENT,
    
    // Other
    CUSTOM_EVENT,
    EXTERNAL_UPDATE
}
