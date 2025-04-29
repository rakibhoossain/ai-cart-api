package org.aicart.store.user;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.aicart.store.user.dto.BannerDto;
import org.aicart.store.user.entity.Banner;
import org.aicart.store.user.entity.Shop;
import org.aicart.store.user.mapper.BannerMapper;

import java.util.List;

@Path("/api/banners")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BannerResource {

    @GET
    public List<BannerDto> list() {
        return Banner.<Banner>listAll().stream()
                .map(BannerMapper::toDto)
                .toList();
    }

    @GET
    @Path("/{id}")
    public BannerDto get(@PathParam("id") Long id) {
        Banner banner = Banner.findById(id);
        if (banner == null) {
            throw new NotFoundException("Banner not found");
        }
        return BannerMapper.toDto(banner);
    }

    @POST
    @Transactional
    public Response create(@Valid BannerDto dto) {
        Banner banner = new Banner();
        banner.shop = Shop.findById(dto.shopId);
        if (banner.shop == null) {
            throw new NotFoundException("Shop not found");
        }
        BannerMapper.updateEntity(banner, dto);
        banner.persist();
        return Response.status(Response.Status.CREATED).entity(BannerMapper.toDto(banner)).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public BannerDto update(@PathParam("id") Long id, @Valid BannerDto dto) {
        Banner banner = Banner.findById(id);
        if (banner == null) {
            throw new NotFoundException("Banner not found");
        }
        if (!banner.shop.id.equals(dto.shopId)) {
            throw new BadRequestException("Shop ID cannot be changed");
        }
        BannerMapper.updateEntity(banner, dto);
        return BannerMapper.toDto(banner);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        Banner banner = Banner.findById(id);
        if (banner == null) {
            throw new NotFoundException("Banner not found");
        }
        banner.delete();
        return Response.noContent().build();
    }

}
