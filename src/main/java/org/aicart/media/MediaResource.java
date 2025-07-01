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
import io.quarkus.security.Authenticated;
import org.aicart.store.user.entity.Shop;
import java.util.Map;

@Path("/media")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MediaResource {

    @Inject
    MediaService mediaService;

    private Long getShopId() {
        // In a real application, this would come from authentication context
        return 1L;
    }

    @POST
    @Authenticated
    @Path("store")
    public Response store(@Valid FileRequestDTO fileRequestDTO) {

        Shop shop = Shop.findById(getShopId());
        if (shop == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", "Shop not found"))
                    .build();
        }

        if (fileRequestDTO == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Request body is required"))
                    .build();
        }

        try {
            FileStorage file = mediaService.store(shop, fileRequestDTO);
            return Response.ok(file).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Authenticated
    public Response list(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("search") String search,
            @QueryParam("fileType") String fileType,
            @QueryParam("sortBy") @DefaultValue("createdAt") String sortBy,
            @QueryParam("order") @DefaultValue("desc") String order) {

        try {

            Shop shop = Shop.findById(getShopId());
            if (shop == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("message", "Shop not found"))
                        .build();
            }

            MediaListResponse response = mediaService.findAllWithFilters(shop, search, fileType, page, size, sortBy, order);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Authenticated
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        try {

            Shop shop = Shop.findById(getShopId());
            if (shop == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("message", "Shop not found"))
                        .build();
            }

            MediaFileDTO file = mediaService.findById(shop, id);
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
    @Authenticated
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid MediaUpdateDTO updateDTO) {
        try {

            Shop shop = Shop.findById(getShopId());
            if (shop == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("message", "Shop not found"))
                        .build();
            }

            MediaFileDTO updatedFile = mediaService.updateMedia(shop, id, updateDTO);
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
    @Authenticated
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        try {

            Shop shop = Shop.findById(getShopId());
            if (shop == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("message", "Shop not found"))
                        .build();
            }

            boolean deleted = mediaService.deleteMedia(shop, id);
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
