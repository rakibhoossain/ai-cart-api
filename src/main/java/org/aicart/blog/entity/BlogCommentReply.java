package org.aicart.blog.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.store.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "blog_comment_replies")
public class BlogCommentReply extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    public BlogComment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User user;

    @Column(name = "guest_name", length = 100)
    public String guestName;

    @Column(name = "guest_email", length = 100)
    public String guestEmail;

    @Column(nullable = false, columnDefinition = "TEXT")
    public String content;

    @Column(name = "is_approved", nullable = false)
    public Boolean isApproved = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}