package org.aicart.media;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.aicart.media.dto.FileRequestDTO;
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

        FileStorage file = mediaService.store(fileRequestDTO);

        return Response.ok(file).build();
    }
}
