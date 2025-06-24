package org.aicart.blog.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.aicart.blog.dto.BlogCommentDTO;
import org.aicart.blog.dto.BlogCommentReplyDTO;
import org.aicart.blog.entity.Blog;
import org.aicart.blog.entity.BlogComment;
import org.aicart.blog.entity.BlogCommentReply;
import org.aicart.blog.mapper.BlogCommentMapper;
import org.aicart.store.user.entity.Shop;
import org.aicart.store.user.entity.User;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class BlogCommentService {
    
    @Inject
    EntityManager em;
    
    public List<BlogCommentDTO> findByBlogAndShop(Long blogId, Shop shop, int page, int size, String sortField, boolean ascending) {
        StringBuilder jpql = new StringBuilder("SELECT c FROM BlogComment c WHERE c.blog.shop.id = :shopId");
        
        if (blogId != null) {
            jpql.append(" AND c.blog.id = :blogId");
        }
        
        jpql.append(" ORDER BY c.").append(sortField);
        if (!ascending) {
            jpql.append(" DESC");
        }
        
        TypedQuery<BlogComment> query = em.createQuery(jpql.toString(), BlogComment.class)
                .setParameter("shopId", shop.id)
                .setFirstResult(page * size)
                .setMaxResults(size);
        
        if (blogId != null) {
            query.setParameter("blogId", blogId);
        }
        
        List<BlogComment> comments = query.getResultList();
        return comments.stream().map(BlogCommentMapper::toDto).collect(Collectors.toList());
    }
    
    public long countByBlogAndShop(Long blogId, Shop shop) {
        StringBuilder jpql = new StringBuilder("SELECT COUNT(c) FROM BlogComment c WHERE c.blog.shop.id = :shopId");
        
        if (blogId != null) {
            jpql.append(" AND c.blog.id = :blogId");
        }
        
        TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class)
                .setParameter("shopId", shop.id);
        
        if (blogId != null) {
            query.setParameter("blogId", blogId);
        }
        
        return query.getSingleResult();
    }
    
    public List<BlogCommentDTO> findApprovedByBlog(Long blogId, int page, int size) {
        String jpql = "SELECT c FROM BlogComment c WHERE c.blog.id = :blogId AND c.isApproved = true ORDER BY c.createdAt DESC";
        
        List<BlogComment> comments = em.createQuery(jpql, BlogComment.class)
                .setParameter("blogId", blogId)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
        
        return comments.stream().map(BlogCommentMapper::toDto).collect(Collectors.toList());
    }
    
    public long countApprovedByBlog(Long blogId) {
        String jpql = "SELECT COUNT(c) FROM BlogComment c WHERE c.blog.id = :blogId AND c.isApproved = true";
        
        return em.createQuery(jpql, Long.class)
                .setParameter("blogId", blogId)
                .getSingleResult();
    }
    
    public BlogCommentDTO findById(Long id, Shop shop) {
        BlogComment comment = BlogComment.findById(id);
        if (comment == null || !comment.blog.shop.id.equals(shop.id)) {
            throw new NotFoundException("Comment not found");
        }
        
        return BlogCommentMapper.toDto(comment);
    }
    
    @Transactional
    public BlogCommentDTO create(BlogCommentDTO dto, Long userId) {
        Blog blog = Blog.findById(dto.getBlogId());
        if (blog == null) {
            throw new IllegalArgumentException("Blog not found");
        }
        
        BlogComment comment = new BlogComment();
        comment.blog = blog;
        comment.content = dto.getContent();
        comment.rating = dto.getRating();
        
        // Set user or guest info
        if (userId != null) {
            User user = User.findById(userId);
            if (user != null) {
                comment.user = user;
            } else {
                setGuestInfo(comment, dto);
            }
        } else {
            setGuestInfo(comment, dto);
        }

        // comment.user.shop
        Shop shop = Shop.findById(1); // TODO: get shop from user
        
        // Auto-approve if user is admin or shop owner
        if (userId != null && comment.user != null && 
            (shop != null && shop.id.equals(blog.shop.id))) {
            comment.isApproved = true;
        } else {
            comment.isApproved = false;
        }
        
        comment.persist();
        return BlogCommentMapper.toDto(comment);
    }
    
    @Transactional
    public BlogCommentDTO approve(Long id, Shop shop) {
        BlogComment comment = BlogComment.findById(id);
        if (comment == null) {
            throw new IllegalArgumentException("Comment not found");
        }
        
        if (!comment.blog.shop.id.equals(shop.id)) {
            throw new SecurityException("You don't have permission to approve this comment");
        }
        
        comment.isApproved = true;
        comment.persist();
        
        return BlogCommentMapper.toDto(comment);
    }
    
    @Transactional
    public void delete(Long id, Shop shop) {
        BlogComment comment = BlogComment.findById(id);
        if (comment == null) {
            throw new IllegalArgumentException("Comment not found");
        }
        
        if (!comment.blog.shop.id.equals(shop.id)) {
            throw new SecurityException("You don't have permission to delete this comment");
        }
        
        comment.delete();
    }
    
    @Transactional
    public BlogCommentReplyDTO addReply(Long commentId, BlogCommentReplyDTO dto, Long userId) {
        BlogComment comment = BlogComment.findById(commentId);
        if (comment == null) {
            throw new IllegalArgumentException("Comment not found");
        }
        
        BlogCommentReply reply = new BlogCommentReply();
        reply.comment = comment;
        reply.content = dto.getContent();
        
        // Set user or guest info
        if (userId != null) {
            User user = User.findById(userId);
            if (user != null) {
                reply.user = user;
            } else {
                setGuestInfo(reply, dto);
            }
        } else {
            setGuestInfo(reply, dto);
        }
        
        // Auto-approve if user is admin or shop owner
        // reply.user.shop
        Shop shop = Shop.findById(1); // TODO: get shop from user
        if (userId != null && reply.user != null && 
            (shop != null && shop.id.equals(comment.blog.shop.id))) {
            reply.isApproved = true;
        } else {
            reply.isApproved = false;
        }
        
        reply.persist();
        return BlogCommentMapper.replyToDto(reply);
    }
    
    @Transactional
    public BlogCommentReplyDTO approveReply(Long id, Shop shop) {
        BlogCommentReply reply = BlogCommentReply.findById(id);
        if (reply == null) {
            throw new IllegalArgumentException("Reply not found");
        }
        
        if (!reply.comment.blog.shop.id.equals(shop.id)) {
            throw new SecurityException("You don't have permission to approve this reply");
        }
        
        reply.isApproved = true;
        reply.persist();
        
        return BlogCommentMapper.replyToDto(reply);
    }
    
    @Transactional
    public void deleteReply(Long id, Shop shop) {
        BlogCommentReply reply = BlogCommentReply.findById(id);
        if (reply == null) {
            throw new IllegalArgumentException("Reply not found");
        }
        
        if (!reply.comment.blog.shop.id.equals(shop.id)) {
            throw new SecurityException("You don't have permission to delete this reply");
        }
        
        reply.delete();
    }
    
    private void setGuestInfo(BlogComment comment, BlogCommentDTO dto) {
        comment.guestName = dto.getGuestName();
        comment.guestEmail = dto.getGuestEmail();
    }
    
    private void setGuestInfo(BlogCommentReply reply, BlogCommentReplyDTO dto) {
        reply.guestName = dto.getGuestName();
        reply.guestEmail = dto.getGuestEmail();
    }
}