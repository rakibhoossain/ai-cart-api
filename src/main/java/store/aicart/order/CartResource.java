package store.aicart.order;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import store.aicart.order.dto.AddToCartDTO;
import store.aicart.order.dto.CartDTO;
import store.aicart.order.dto.CartItemDTO;
import store.aicart.order.dto.CartUpdateDTO;
import store.aicart.order.entity.Cart;

import java.util.Collections;
import java.util.List;

@Path("/carts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CartResource {

    @Inject
    CartService cartService;

    private final String sessionKey = "cart-session";

    @GET
    public Response getCart(@Context HttpHeaders headers) {
        String sessionId = cartService.getSessionId(headers);
        List<CartItemDTO> cart = cartService.getCart(sessionId, null);

        return Response.ok(cart != null ? cart : Collections.emptyList()).build();
    }


    @POST
    public Response addItemToCart(@Context HttpHeaders headers, AddToCartDTO addToCartDTO) {
        String sessionId = cartService.getSessionId(headers);
        Cart cart = cartService.firstOrCreate(sessionId, null);

        if (cart == null) return Response.status(Response.Status.NOT_FOUND).build();

        boolean result = cartService.addToCart(cart, addToCartDTO.getProductId(), addToCartDTO.getVariantId(), addToCartDTO.getQuantity());

        if (!result) return Response.status(Response.Status.NOT_FOUND).build();

        NewCookie cookie = new NewCookie. Builder(cartService.getSessionKey())
                .value(sessionId)
                .path("/")
                .sameSite(NewCookie.SameSite.LAX)
                .build();

        CartDTO data = new CartDTO(cart.id, cart.sessionId);

        return Response.ok(data).cookie(cookie).build();
    }

    @DELETE
    @Path("/delete/{itemId}")
    public Response removeItemFromCart(@Context HttpHeaders headers, @PathParam("itemId") Long itemId) {

        String sessionId = cartService.getSessionId(headers);
        Cart cart = cartService.firstOrCreate(sessionId, null);
        if (cart == null) return Response.status(Response.Status.NOT_FOUND).build();

        boolean affected = cartService.removeItemFromCart(cart, itemId);
        return Response.ok(affected).build();
    }

    @PUT
    @Path("/update-quantity/{itemId}")
    public Response updateCartQuantity(@Context HttpHeaders headers, @PathParam("itemId") Long itemId, CartUpdateDTO cartUpdateDTO) {

        String sessionId = cartService.getSessionId(headers);
        Cart cart = cartService.firstOrCreate(sessionId, null);
        if (cart == null) return Response.status(Response.Status.NOT_FOUND).build();

        boolean affected = cartService.updateCartQuantity(cart, itemId, cartUpdateDTO.getQuantity());
        return Response.ok(affected).build();
    }
}
