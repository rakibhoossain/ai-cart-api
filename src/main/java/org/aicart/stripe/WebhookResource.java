package org.aicart.stripe;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.*;
import com.stripe.net.Webhook;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import store.aicart.order.OrderService;
import store.aicart.order.entity.Cart;

import java.util.Map;

@Path("/stripe")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WebhookResource {

    @Inject
    OrderService orderService;

    final String WEBHOOK_SECRET_KEY = "whsec_95ce0281a7c58b3635b9b75ce69dfe90c99a5c8cfc8e6f9501704fdc3085cfaf";

    @POST
    @Path("/webhook")
    public Response webhook(String payload, @HeaderParam("Stripe-Signature") String sigHeader) {

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, WEBHOOK_SECRET_KEY);
        } catch (SignatureVerificationException e) {
            System.out.println("Failed signature verification");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        event.setApiVersion("2024-10-28.acacia");

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();

        if (dataObjectDeserializer.getObject().isPresent()) {
            StripeObject stripeObject = dataObjectDeserializer.getObject().get();

            // Handle event
            if (event.getType().equals("payment_intent.succeeded")) {
                handlePaymentIntentSucceeded((PaymentIntent) stripeObject);
            }

        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();

            // Deserialization failed, probably due to an API version mismatch.
            // Refer to the Javadoc documentation on `EventDataObjectDeserializer` for
            // instructions on how to handle this case, or return an error here.
        }

        return Response.ok("Success").build();
    }


    private void handlePaymentIntentSucceeded(PaymentIntent paymentIntent) {
        // Extract relevant details from the PaymentIntent
        String id = paymentIntent.getId();

        // Extract metadata
        Map<String, String> metadata = paymentIntent.getMetadata();
        String cartId = metadata.get("cartId");
        String paymentType = metadata.get("payment_type");

        if(paymentType != null && paymentType.equals("ecommerce_cart")
               && cartId != null

        ){
            cartCartPaymentConfirm(cartId);
        }
    }

    private void cartCartPaymentConfirm(String cartId)
    {
        Cart cart = Cart.findById(cartId);
        if (cart == null) return;
        orderService.convertCartToOrder(cart, cart.billing, cart.shipping);
    }
}
