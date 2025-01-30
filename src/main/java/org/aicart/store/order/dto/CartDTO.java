package org.aicart.store.order.dto;

public class CartDTO {
    private Long id;
    private String sessionId;

    public CartDTO(Long id, String sessionId) {
        this.id = id;
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
