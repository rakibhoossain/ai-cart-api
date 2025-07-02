package org.aicart.store.order;

public enum TrackingEventTypeEnum {
    STATUS_CHANGE,      // Order status changed
    SHIPMENT_CREATED,   // Shipping label created
    PACKAGE_PICKED_UP,  // Package picked up by carrier
    IN_TRANSIT,         // Package in transit
    OUT_FOR_DELIVERY,   // Package out for delivery
    DELIVERED,          // Package delivered
    DELIVERY_ATTEMPTED, // Delivery attempted but failed
    EXCEPTION,          // Shipping exception occurred
    RETURNED,           // Package returned to sender
    REFUND_PROCESSED,   // Refund processed
    EXCHANGE_INITIATED, // Exchange initiated
    CREDIT_NOTE_ISSUED, // Credit note issued
    PAYMENT_RECEIVED,   // Payment received
    PAYMENT_FAILED,     // Payment failed
    INVENTORY_ALLOCATED,// Inventory allocated
    INVENTORY_RELEASED, // Inventory released
    ADMIN_NOTE,         // Admin added a note
    CUSTOMER_CONTACTED, // Customer was contacted
    SYSTEM_UPDATE       // System automated update
}
