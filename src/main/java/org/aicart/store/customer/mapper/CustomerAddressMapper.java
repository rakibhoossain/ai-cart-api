package org.aicart.store.customer.mapper;

import org.aicart.store.customer.dto.CustomerAddressDTO;
import org.aicart.store.customer.entity.CustomerAddress;

public class CustomerAddressMapper {

    public static CustomerAddressDTO toDto(CustomerAddress customerAddress) {
        if (customerAddress == null) return null;

        CustomerAddressDTO dto = new CustomerAddressDTO();
        dto.id = String.valueOf(customerAddress.id);
        dto.firstName = customerAddress.firstName;
        dto.lastName = customerAddress.lastName;
        dto.line1 = customerAddress.line1;
        dto.line2 = customerAddress.line2;
        dto.city = customerAddress.city;
        dto.state = customerAddress.state;
        dto.postalCode = customerAddress.postalCode;
        dto.countryCode = customerAddress.countryCode;
        dto.phone = customerAddress.phone;

        return dto;
    }
}
