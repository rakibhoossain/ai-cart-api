package org.aicart.store.product;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.aicart.store.product.dto.ProductCollectionDTO;
import org.aicart.store.product.entity.ProductCollection;
import org.aicart.store.product.entity.ProductCollectionCondition;
import org.aicart.store.product.mapper.ProductCollectionMapper;

import java.util.Map;
import java.util.List;
import java.lang.Long;

@Path("/product/collections")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductCollectionResource {

    @Inject
    ProductCollectionService service;

    @Inject
    ProductCollectionRepository collectionRepo;

    @GET
    @Path("/")
    public Response getAll() {
        return Response.ok(service.getAll()).build();
    }


    @GET
    @Path("/test")
    public Response test() {


        try {

            List<Long> collectionIds = collectionRepo
                    .getEntityManager()
                    .createNativeQuery("SELECT id FROM product_collections")
                    .getResultList();

//            var collections = collectionRepo.list(
//                    "SELECT c.id FROM ProductCollection c"); //  WHERE c.collection_type = true
//
//            System.out.println(collections);

            collectionIds.forEach(collectionRepo::updateSmartCollection);


//            for (ProductCollection collection : collections) {
//                collectionRepo.updateSmartCollection(1L);
//            }

//        if(request == null) {
//            return Response.status(Response.Status.BAD_REQUEST)
//                    .entity(Map.of("message", "Request body is required"))
//                    .build();
//        }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return Response.ok().build();
    }


    @POST
    @Path("/")
    public Response create(@Valid ProductCollectionDTO request) {

        if(request == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Request body is required"))
                    .build();
        }

        return Response.ok(ProductCollectionMapper.toDTO(service.create(request))).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long collectionId, @Valid ProductCollectionDTO request) {

        if(request == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Request body is required"))
                    .build();
        }

        return Response.ok(ProductCollectionMapper.toDTO(service.update(collectionId, request))).build();
    }
}
