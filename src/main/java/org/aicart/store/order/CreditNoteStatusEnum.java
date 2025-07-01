package org.aicart.store.order;

public enum CreditNoteStatusEnum {
    PENDING,        // Credit note created but not issued
    ISSUED,         // Credit note issued to customer
    PARTIALLY_USED, // Credit note partially used
    FULLY_USED,     // Credit note fully used
    EXPIRED,        // Credit note expired
    CANCELLED       // Credit note cancelled
}
