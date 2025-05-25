package org.aicart.authentication.dto;

public class TokenUser {
    private long entityId;
    private String email;
    private String identifierName;

    public TokenUser(long entityId, String email, String identifierName) {
        this.entityId = entityId;
        this.email = email;
        this.identifierName = identifierName;
    }

    public long getUserId() {
        return entityId;
    }

    public void setUserId(long entityId) {
        this.entityId = entityId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdentifierName() {return identifierName;}

    public void setIdentifierName(String identifierName) {this.identifierName = identifierName;}
}
