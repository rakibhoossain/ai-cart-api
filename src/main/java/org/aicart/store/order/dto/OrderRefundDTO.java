package org.aicart.store.order.dto;

import org.aicart.store.order.RefundStatusEnum;
import org.aicart.store.order.RefundTypeEnum;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

public class OrderRefundDTO {
    private Long id;
    private String refundNumber;
    private RefundTypeEnum refundType;
    private RefundStatusEnum status;
    private BigInteger refundAmount;
    private String reason;
    private String adminNotes;
    private String processedBy;
    private LocalDateTime processedAt;
    private List<OrderRefundItemDTO> refundItems;
    private LocalDateTime createdAt;

    // Constructors
    public OrderRefundDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRefundNumber() { return refundNumber; }
    public void setRefundNumber(String refundNumber) { this.refundNumber = refundNumber; }

    public RefundTypeEnum getRefundType() { return refundType; }
    public void setRefundType(RefundTypeEnum refundType) { this.refundType = refundType; }

    public RefundStatusEnum getStatus() { return status; }
    public void setStatus(RefundStatusEnum status) { this.status = status; }

    public BigInteger getRefundAmount() { return refundAmount; }
    public void setRefundAmount(BigInteger refundAmount) { this.refundAmount = refundAmount; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }

    public String getProcessedBy() { return processedBy; }
    public void setProcessedBy(String processedBy) { this.processedBy = processedBy; }

    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }

    public List<OrderRefundItemDTO> getRefundItems() { return refundItems; }
    public void setRefundItems(List<OrderRefundItemDTO> refundItems) { this.refundItems = refundItems; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
