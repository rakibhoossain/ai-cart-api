package org.aicart.auth.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "email_verifications")
public class EmailVerification extends PanacheEntityBase {

    @Id
    @Column(name = "user_id", nullable = false)
    public long userId;

    @Column(nullable = false, length = 8)
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
