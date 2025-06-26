package org.aicart.blog.resource;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.aicart.blog.dto.BlogCommentDTO;
import org.aicart.blog.dto.BlogCommentReplyDTO;
import org.aicart.blog.service.BlogCommentService;
import org.aicart.store.user.entity.Shop;
import org.aicart.store.user.entity.User;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;
import java.util.Map;

@Path("/blog-comments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BlogCommentResource {
    
    @Inject
    BlogCommentService commentService;
    
    @Inject
    JsonWebToken jwt;
    
    @GET
    @Authenticated
    public Response list(
            @QueryParam("blogId") Long blogId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("sort") @DefaultValue("createdAt") String sortField,
            @QueryParam("direction") @DefaultValue("desc") String sortDirection) {
        
        User user = User.findById(Long.parseLong(jwt.getSubject()));
        Shop shop = Shop.findById(1); // TODO: get shop from user
        
        List<BlogCommentDTO> comments = commentService.findByBlogAndShop(
                blogId,
                shop, 
                page, 
                size, 
                sortField, 
                "asc".equalsIgnoreCase(sortDirection));
        
        long total = commentService.countByBlogAndShop(blogId, shop);
        
        return Response.ok(Map.of(
                "data", comments,
                "total", total,
                "page", page,
                "size", size
        )).build();
    }
    
    @GET
    @Path("/{id}")
    @Authenticated
    public Response get(@PathParam("id") Long id) {
        User user = User.findById(Long.parseLong(jwt.getSubject()));
        Shop shop = Shop.findById(1); // TODO: get shop from user
        
        BlogCommentDTO comment = commentService.findById(id, shop);
        return Response.ok(comment).build();
    }
    
    @POST
    public Response create(@Valid BlogCommentDTO dto) {
        Long userId = null;
        try {
            if (jwt != null && jwt.getSubject() != null) {
                userId = Long.parseLong(jwt.getSubject());
            }
        } catch (Exception e) {
            // Ignore if not authenticated
        }
        
        try {
            BlogCommentDTO created = commentService.create(dto, userId);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }
    
    @PUT
    @Path("/{id}/approve")
    @Authenticated
    public Response approve(@PathParam("id") Long id) {
        User user = User.findById(Long.parseLong(jwt.getSubject()));
        Shop shop = Shop.findById(1); // TODO: get shop from user
        
        try {
            BlogCommentDTO updated = commentService.approve(id, shop);
            return Response.ok(updated).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    @Authenticated
    public Response delete(@PathParam("id") Long id) {
        User user = User.findById(Long.parseLong(jwt.getSubject()));
        Shop shop = Shop.findById(1); // TODO: get shop from user
        
        try {
            commentService.delete(id, shop);
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }
    
    @POST
    @Path("/{commentId}/replies")
    public Response addReply(@PathParam("commentId") Long commentId, @Valid BlogCommentReplyDTO dto) {
        Long userId = null;
        try {
            if (jwt != null && jwt.getSubject() != null) {
                userId = Long.parseLong(jwt.getSubject());
            }
        } catch (Exception e) {
            // Ignore if not authenticated
        }
        
        try {
            BlogCommentReplyDTO created = commentService.addReply(commentId, dto, userId);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }
    
    @PUT
    @Path("/replies/{id}/approve")
    @Authenticated
    public Response approveReply(@PathParam("id") Long id) {
        User user = User.findById(Long.parseLong(jwt.getSubject()));
        Shop shop = Shop.findById(1); // TODO: get shop from user
        
        try {
            BlogCommentReplyDTO updated = commentService.approveReply(id, shop);
            return Response.ok(updated).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }
    
    @DELETE
    @Path("/replies/{id}")
    @Authenticated
    public Response deleteReply(@PathParam("id") Long id) {
        User user = User.findById(Long.parseLong(jwt.getSubject()));
        Shop shop = Shop.findById(1); // TODO: get shop from user
        
        try {
            commentService.deleteReply(id, shop);
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }
    
    @GET
    @Path("/public")
    public Response listPublic(
            @QueryParam("blogId") Long blogId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {
        
        List<BlogCommentDTO> comments = commentService.findApprovedByBlog(
                blogId, 
                page, 
                size);
        
        long total = commentService.countApprovedByBlog(blogId);
        
        return Response.ok(Map.of(
                "data", comments,
                "total", total,
                "page", page,
                "size", size
        )).build();
    }
}