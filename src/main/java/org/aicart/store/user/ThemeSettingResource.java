package org.aicart.store.user;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.aicart.store.user.dto.ThemeRequestDTO;
import org.aicart.store.user.entity.ShopThemeSetting;

import java.util.Map;

@Path("/theme-settings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ThemeSettingResource {

    @Inject
    ThemeSettingService themeSettingService;

    @POST
    @Path("/")
    public Response createTheme(@Valid ThemeRequestDTO dto) {
        ShopThemeSetting setting = themeSettingService.updateTheme(null, dto);
        return Response.ok(Map.of("setting", setting.sections, "id", setting.id)).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateTheme(@PathParam("id") String id, @Valid ThemeRequestDTO dto) {
        ShopThemeSetting setting = themeSettingService.updateTheme(id, dto);
        return Response.ok(Map.of("setting", setting.sections, "id", setting.id)).build();
    }
}
