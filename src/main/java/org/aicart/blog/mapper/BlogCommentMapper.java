package org.aicart.blog.mapper;

import org.aicart.blog.dto.BlogCommentDTO;
import org.aicart.blog.dto.BlogCommentReplyDTO;
import org.aicart.blog.entity.BlogComment;
import org.aicart.blog.entity.BlogCommentReply;

import java.util.stream.Collectors;

public class BlogCommentMapper {
    
    public static BlogCommentDTO toDto(BlogComment entity) {
        if (entity == null) {
            return null;
        }
        
        BlogCommentDTO dto = new BlogCommentDTO();
        dto.setId(entity.id);
        dto.setBlogId(entity.blog.id);
        
        if (entity.user != null) {
            dto.setUserId(entity.user.id);
            dto.setUserName(entity.user.name);
        } else {
            dto.setGuestName(entity.guestName);
            dto.setGuestEmail(entity.guestEmail);
        }
        
        dto.setContent(entity.content);
        dto.setRating(entity.rating);
        dto.setIsApproved(entity.isApproved);
        dto.setCreatedAt(entity.createdAt);
        dto.setUpdatedAt(entity.updatedAt);
        
        // Map replies
        dto.setReplies(entity.replies.stream()
                .map(BlogCommentMapper::replyToDto)
                .collect(Collectors.toList()));
        
        return dto;
    }
    
    public static BlogCommentReplyDTO replyToDto(BlogCommentReply entity) {
        if (entity == null) {
            return null;
        }
        
        BlogCommentReplyDTO dto = new BlogCommentReplyDTO();
        dto.setId(entity.id);
        dto.setCommentId(entity.comment.id);
        
        if (entity.user != null) {
            dto.setUserId(entity.user.id);
            dto.setUserName(entity.user.name);
        } else {
            dto.setGuestName(entity.guestName);
            dto.setGuestEmail(entity.guestEmail);
        }
        
        dto.setContent(entity.content);
        dto.setIsApproved(entity.isApproved);
        dto.setCreatedAt(entity.createdAt);
        dto.setUpdatedAt(entity.updatedAt);
        
        return dto;
    }
}