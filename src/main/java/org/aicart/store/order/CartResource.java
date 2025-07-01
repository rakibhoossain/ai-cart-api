package org.aicart.store.order;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.aicart.store.context.ShopContext;
import org.aicart.store.customer.entity.Customer;
import org.aicart.store.order.dto.*;
import org.aicart.sslcommerz.SslcommerzResponse;
import org.aicart.sslcommerz.SslcommerzService;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.aicart.store.order.entity.Cart;
import org.aicart.store.order.entity.CartDeliveryRequestDTO;
import org.aicart.store.order.entity.CartItem;
import org.aicart.store.order.entity.Order;

import java.util.HashMap;

@Path("/carts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CartResource {

    @Inject
    CartService cartService;

    @Inject
    OrderService orderService;

    @Inject
    SslcommerzService sslcommerzService;

    @Inject
    JsonWebToken jwt;

    @Context
    SecurityContext securityContext;

    @Inject
    ShopContext shopContext;

    private final String sessionKey = "cart-session";

    @GET
    public Response getCart(@Context HttpHeaders headers) {
        String sessionId = cartService.getSessionId(headers);
        CartResponseDTO cart = cartService.getCart(sessionId);

        if(cart == null)
        {
            return Response.noContent().build();
        }

        return Response.ok(cart).build();
    }


    @POST
    public Response addItemToCart(@Context HttpHeaders headers, AddToCartDTO addToCartDTO) {
        String sessionId = cartService.getSessionId(headers);

        String subject = jwt.getSubject();

        Cart cart = cartService.firstOrCreate(sessionId, subject != null ? Long.valueOf(subject) : null);

        if (cart == null) return Response.status(Response.Status.NOT_FOUND).build();

        boolean result = cartService.addToCart(cart, addToCartDTO.getProductId(), addToCartDTO.getVariantId(), addToCartDTO.getQuantity());

        if (!result) return Response.status(Response.Status.NOT_FOUND).build();

        NewCookie cookie = new NewCookie. Builder(cartService.getSessionKey())
                .value(sessionId)
                .path("/")
                .sameSite(NewCookie.SameSite.LAX)
                .build();

        CartDTO data = new CartDTO(cart.id, cart.sessionId);

        HashMap<String, String> response = new HashMap<>();
        response.put("sessionId", data.getSessionId());
        response.put("id", data.getId().toString());

        return Response.ok(response).cookie(cookie).build();
    }

    @DELETE
    @Path("/delete/{itemId}")
    public Response removeItemFromCart(@Context HttpHeaders headers, @PathParam("itemId") Long itemId) {

        String sessionId = cartService.getSessionId(headers);
        String subject = jwt.getSubject();

        Cart cart = cartService.firstOrCreate(sessionId, subject != null ? Long.valueOf(subject) : null);
        if (cart == null) return Response.status(Response.Status.NOT_FOUND).build();

        boolean affected = cartService.removeItemFromCart(cart, itemId);
        return Response.ok(affected).build();
    }

    @PUT
    @Path("/update-quantity/{itemId}")
    public Response updateCartQuantity(@Context HttpHeaders headers, @PathParam("itemId") Long itemId, CartUpdateDTO cartUpdateDTO) {

        String sessionId = cartService.getSessionId(headers);

        String subject = jwt.getSubject();
        Cart cart = cartService.firstOrCreate(sessionId, subject != null ? Long.valueOf(subject) : null);
        if (cart == null) return Response.status(Response.Status.NOT_FOUND).build();

        boolean affected = cartService.updateCartQuantity(cart, itemId, cartUpdateDTO.getQuantity());
        return Response.ok(affected).build();
    }


    @PUT
    @Path("/update-cart-address/{cartId}")
    public Response updateCartAddress(@PathParam("cartId") Long cartId, @Valid CartAddressRequestDTO addressRequest) {

        Cart cart = Cart.find("id = ?1 AND shop.id = ?2", cartId, shopContext.getShopId()).firstResult();
        if (cart == null) return Response.status(Response.Status.NOT_FOUND).build();

        CartAddressRequestDTO results = cartService.updateCartAddress(cart, addressRequest);

        return Response.ok(results).build();
    }


    @GET
    @Path("/verify-coupon/{cartId}")
    public Response verifyCoupon(@PathParam("cartId") Long cartId, @QueryParam("couponCode") String couponCode) {
        Cart cart = Cart.find("id = ?1 AND shop.id = ?2", cartId, shopContext.getShopId()).firstResult();
        if (cart == null) return Response.status(Response.Status.NOT_FOUND).build();

        return Response.ok(couponCode).build();
    }

    @PUT
    @Path("/update-delivery-info/{cartId}")
    public Response updateDeliveryInfo(@PathParam("cartId") Long cartId, CartDeliveryRequestDTO deliveryRequest) {
        Cart cart = Cart.find("id = ?1 AND shop.id = ?2", cartId, shopContext.getShopId()).firstResult();
        if (cart == null) return Response.status(Response.Status.NOT_FOUND).build();

        CartDeliveryRequestDTO results = cartService.updateDeliveryInfo(cart, deliveryRequest);

        return Response.ok(results).build();
    }


    @POST
    @Path("/confirm/{cartId}")
    public Response confirmOrder(@PathParam("cartId") Long cartId) {
        Cart cart = Cart.find("id = ?1 AND shop.id = ?2", cartId, shopContext.getShopId()).firstResult();
        if (cart == null) return Response.status(Response.Status.NOT_FOUND).build();

        long remainingItems = CartItem.count("cart.id = ?1", cart.id);
        if (remainingItems == 0) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        String subject = securityContext.getUserPrincipal() != null ?
                jwt.getSubject()
                : null;

        Order order = orderService.convertCartToOrder(cart, cart.billing, cart.shipping, subject);
        orderService.clearCart(cart);
        return Response.ok(order.id).status(Response.Status.CREATED).build();

    }


    @GET
    @Path("/sslcommerz-payment-verify/{valId}")
    public Response sslcommerzPaymentVerify(@PathParam("valId") String valId) {

        SslcommerzResponse response = sslcommerzService.sslcommerzPaymentVerify(valId);
        if(response.isValid() && response.getTran_id() != null)
        {
            String cartId = response.getTran_id();
            Cart cart = Cart.find("id = ?1", cartId).firstResult();
            if (cart == null) return Response.status(Response.Status.NOT_FOUND).build();

            orderService.convertCartToOrder(cart, cart.billing, cart.shipping, null);

            return Response.ok(response).status(Response.Status.CREATED).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("/merge-guest")
    @Authenticated
    @Transactional
    public Response mergeGuest(@Context HttpHeaders headers) {
        String sessionId = cartService.getSessionId(headers);
        Cart cart = cartService.getCartEntity(sessionId);

        String subject = jwt.getSubject();

        if(cart != null && cart.customer == null && subject != null) {
            Customer customer = Customer.find("id = ?1 AND shop.id = ?2", subject, shopContext.getShopId()).firstResult();
            if(customer != null) {
                cart.customer = customer;
                cart.persist();
            }
        }

        return Response.ok().build();
    }
}
