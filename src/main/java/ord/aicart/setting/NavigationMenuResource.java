package ord.aicart.setting;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ord.aicart.setting.dto.CreateNavigationMenuRequestDTO;
import ord.aicart.setting.dto.NavigationMenuDTO;
import ord.aicart.setting.dto.PublicNavigationMenuItemDTO;
import ord.aicart.setting.entity.NavigationMenu;

import java.util.List;

@Path("/navigation-menus")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NavigationMenuResource {

    @Inject
    NavigationMenuService service;

    @GET
    public List<NavigationMenu> listMenus() {
        return service.listAllMenus();
    }

    @GET
    @Path("/{id}")
    public Response getMenu(@PathParam("id") Long id) {
        NavigationMenu menu = service.getMenuById(id);

        if (menu == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        NavigationMenuDTO menuDto = NavigationMenuDTO.fromEntity(menu);

        return Response.ok(menuDto).build();
    }

    @GET
    @Path("/public/{name}")
    public List<PublicNavigationMenuItemDTO> getMenu(
            @PathParam("name") String name,
            @QueryParam("lang") @DefaultValue("en") String lang
    ) {
        return service.getNavigationMenu(name, lang);
    }

    @POST
    public Response createMenu(CreateNavigationMenuRequestDTO request) {
        if (request.name == null || request.name.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Menu name is required").build();
        }

        if (request.value == null || request.value.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Menu items are required").build();
        }

        service.createOrUpdateMenu(null, request.name, request.value);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateMenu(@PathParam("id") Long id, CreateNavigationMenuRequestDTO request) {
        service.createOrUpdateMenu(id, request.name, request.value);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteMenu(@PathParam("id") Long id) {
        service.deleteMenu(id);
        return Response.noContent().build();
    }
}
