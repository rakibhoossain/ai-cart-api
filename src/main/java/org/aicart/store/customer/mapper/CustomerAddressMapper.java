package org.aicart.store.customer.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.aicart.store.customer.dto.CustomerAddressDTO;
import org.aicart.store.customer.entity.CustomerAddress;

@ApplicationScoped
public class CustomerAddressMapper {

    public static CustomerAddressDTO toDto(CustomerAddress customerAddress) {
        if (customerAddress == null) return null;

        CustomerAddressDTO dto = new CustomerAddressDTO();
        dto.id = customerAddress.id;
        dto.type = customerAddress.type;
        dto.firstName = customerAddress.firstName;
        dto.lastName = customerAddress.lastName;
        dto.company = customerAddress.company;
        dto.line1 = customerAddress.line1;
        dto.line2 = customerAddress.line2;
        dto.city = customerAddress.city;
        dto.state = customerAddress.state;
        dto.country = customerAddress.country;
        dto.postalCode = customerAddress.postalCode;
        dto.countryCode = customerAddress.countryCode;
        dto.phone = customerAddress.phone;
        dto.isDefault = customerAddress.isDefault;
        dto.createdAt = customerAddress.createdAt;
        dto.updatedAt = customerAddress.updatedAt;

        return dto;
    }

    public CustomerAddressDTO toDTO(CustomerAddress customerAddress) {
        return toDto(customerAddress);
    }
}
