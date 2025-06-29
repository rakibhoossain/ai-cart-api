package org.aicart.media;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.aicart.media.dto.FileRequestDTO;
import org.aicart.media.dto.MediaFileDTO;
import org.aicart.media.dto.MediaListResponse;
import org.aicart.media.dto.MediaUpdateDTO;
import org.aicart.media.entity.FileStorage;

import java.util.Map;

@Path("/media")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MediaResource {

    @Inject
    MediaService mediaService;

    @POST
    @Path("store")
    public Response store(@Valid FileRequestDTO fileRequestDTO) {

        if (fileRequestDTO == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Request body is required"))
                    .build();
        }

        try {
            FileStorage file = mediaService.store(fileRequestDTO);
            return Response.ok(file).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }

    @GET
    public Response list(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("search") String search,
            @QueryParam("fileType") String fileType,
            @QueryParam("sortBy") @DefaultValue("createdAt") String sortBy,
            @QueryParam("order") @DefaultValue("desc") String order) {

        try {
            MediaListResponse response = mediaService.findAllWithFilters(search, fileType, page, size, sortBy, order);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        try {
            MediaFileDTO file = mediaService.findById(id);
            if (file == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("message", "Media file not found"))
                        .build();
            }
            return Response.ok(file).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid MediaUpdateDTO updateDTO) {
        try {
            MediaFileDTO updatedFile = mediaService.updateMedia(id, updateDTO);
            return Response.ok(updatedFile).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        try {
            boolean deleted = mediaService.deleteMedia(id);
            if (!deleted) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("message", "Media file not found"))
                        .build();
            }
            return Response.ok(Map.of("message", "Media file deleted successfully")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }
}
