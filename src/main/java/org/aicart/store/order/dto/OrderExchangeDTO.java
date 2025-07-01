package org.aicart.store.order.dto;

import org.aicart.store.order.ExchangeStatusEnum;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

public class OrderExchangeDTO {
    private Long id;
    private String exchangeNumber;
    private ExchangeStatusEnum status;
    private String reason;
    private String adminNotes;
    private BigInteger priceDifference;
    private String processedBy;
    private LocalDateTime processedAt;
    private List<OrderExchangeItemDTO> exchangeItems;
    private LocalDateTime createdAt;

    // Constructors
    public OrderExchangeDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getExchangeNumber() { return exchangeNumber; }
    public void setExchangeNumber(String exchangeNumber) { this.exchangeNumber = exchangeNumber; }

    public ExchangeStatusEnum getStatus() { return status; }
    public void setStatus(ExchangeStatusEnum status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }

    public BigInteger getPriceDifference() { return priceDifference; }
    public void setPriceDifference(BigInteger priceDifference) { this.priceDifference = priceDifference; }

    public String getProcessedBy() { return processedBy; }
    public void setProcessedBy(String processedBy) { this.processedBy = processedBy; }

    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }

    public List<OrderExchangeItemDTO> getExchangeItems() { return exchangeItems; }
    public void setExchangeItems(List<OrderExchangeItemDTO> exchangeItems) { this.exchangeItems = exchangeItems; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
