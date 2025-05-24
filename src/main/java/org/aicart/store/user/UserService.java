package org.aicart.store.user;

import jakarta.enterprise.context.ApplicationScoped;
import org.aicart.store.user.auth.dto.UserProfileDTO;
import org.aicart.store.order.dto.OrderBillingDTO;
import org.aicart.store.order.dto.OrderShippingDTO;
import org.aicart.store.user.entity.User;
import org.aicart.store.user.entity.UserBilling;
import org.aicart.store.user.entity.UserShipping;

@ApplicationScoped
public class UserService {


    public UserBilling storeUserBilling(User user, OrderBillingDTO orderBillingDTO) {

        // Find existing billing by user
        UserBilling billing = UserBilling.find("user", user).firstResult();

        if (billing == null) {
            // Create a new record if none exists
            billing = new UserBilling();
            billing.user = user;
        }

        billing.fullName = orderBillingDTO.getFullName();
        billing.email = orderBillingDTO.getEmail();
        billing.phone = orderBillingDTO.getPhone();
        billing.line1 = orderBillingDTO.getLine1();
        billing.line2 = orderBillingDTO.getLine2();
        billing.city = orderBillingDTO.getCity();
        billing.state = orderBillingDTO.getState();
        billing.country = orderBillingDTO.getCountry();
        billing.postalCode = orderBillingDTO.getPostalCode();
        billing.vatNumber = orderBillingDTO.getVatNumber();
        billing.taxNumber = orderBillingDTO.getTaxNumber();

        billing.persist();

        return billing;
    }

    public UserShipping storeUserShipping(User user, OrderShippingDTO orderShippingDTO) {


        // Find existing shipping by user
        UserShipping shipping = UserShipping.find("user", user).firstResult();

        if (shipping == null) {
            // Create a new record if none exists
            shipping = new UserShipping();
            shipping.user = user;
        }

        shipping.fullName = orderShippingDTO.getFullName();
        shipping.phone = orderShippingDTO.getPhone();
        shipping.line1 = orderShippingDTO.getLine1();
        shipping.line2 = orderShippingDTO.getLine2();
        shipping.city = orderShippingDTO.getCity();
        shipping.state = orderShippingDTO.getState();
        shipping.country = orderShippingDTO.getCountry();
        shipping.postalCode = orderShippingDTO.getPostalCode();

        shipping.persist();

        return shipping;
    }

    public User updateProfile(User user, UserProfileDTO userProfileDTO) {
        user.name = userProfileDTO.getName();
        user.persist();

        return user;
    }
}
