package org.aicart.authentication.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "password_resets",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"entity_id", "identifier_name"})
        })
public class PasswordReset extends PanacheEntity {

    @Column(name = "entity_id", nullable = false)
    public long entityId;

    @Column(name = "identifier_name", nullable = false, length = 10)
    public String identifierName;

    @Column(nullable = false)
    public String token;

    @Column(name = "created_at", nullable = false, updatable = false)
    public long createdAt = System.currentTimeMillis() / 1000L;

    @Column(name = "updated_at", nullable = false)
    public long updatedAt = System.currentTimeMillis() / 1000L;

    @Column(name = "expired_at", nullable = false)
    public long expiredAt = System.currentTimeMillis() / 1000L + 10 * 60L; // 10 minutes in seconds

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = System.currentTimeMillis() / 1000L;
    }
}
