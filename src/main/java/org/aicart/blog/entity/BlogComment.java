package org.aicart.blog.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.store.user.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "blog_comments")
public class BlogComment extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id", nullable = false)
    public Blog blog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User user;

    @Column(name = "guest_name", length = 100)
    public String guestName;

    @Column(name = "guest_email", length = 100)
    public String guestEmail;

    @Column(nullable = false, columnDefinition = "TEXT")
    public String content;

    @Column(name = "rating")
    public Integer rating; // 1-5 star rating

    @Column(name = "is_approved", nullable = false)
    public Boolean isApproved = false;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<BlogCommentReply> replies = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public void addReply(BlogCommentReply reply) {
        replies.add(reply);
        reply.comment = this;
    }
}