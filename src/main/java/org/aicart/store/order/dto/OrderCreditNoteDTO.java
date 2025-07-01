package org.aicart.store.order.dto;

import org.aicart.store.order.CreditNoteStatusEnum;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

public class OrderCreditNoteDTO {
    private Long id;
    private String creditNoteNumber;
    private CreditNoteStatusEnum status;
    private BigInteger creditAmount;
    private String reason;
    private String adminNotes;
    private LocalDateTime expiryDate;
    private BigInteger usedAmount;
    private BigInteger remainingAmount;
    private String issuedBy;
    private LocalDateTime issuedAt;
    private List<OrderCreditNoteItemDTO> creditNoteItems;
    private LocalDateTime createdAt;

    // Constructors
    public OrderCreditNoteDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCreditNoteNumber() { return creditNoteNumber; }
    public void setCreditNoteNumber(String creditNoteNumber) { this.creditNoteNumber = creditNoteNumber; }

    public CreditNoteStatusEnum getStatus() { return status; }
    public void setStatus(CreditNoteStatusEnum status) { this.status = status; }

    public BigInteger getCreditAmount() { return creditAmount; }
    public void setCreditAmount(BigInteger creditAmount) { this.creditAmount = creditAmount; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }

    public BigInteger getUsedAmount() { return usedAmount; }
    public void setUsedAmount(BigInteger usedAmount) { this.usedAmount = usedAmount; }

    public BigInteger getRemainingAmount() { return remainingAmount; }
    public void setRemainingAmount(BigInteger remainingAmount) { this.remainingAmount = remainingAmount; }

    public String getIssuedBy() { return issuedBy; }
    public void setIssuedBy(String issuedBy) { this.issuedBy = issuedBy; }

    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }

    public List<OrderCreditNoteItemDTO> getCreditNoteItems() { return creditNoteItems; }
    public void setCreditNoteItems(List<OrderCreditNoteItemDTO> creditNoteItems) { this.creditNoteItems = creditNoteItems; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
