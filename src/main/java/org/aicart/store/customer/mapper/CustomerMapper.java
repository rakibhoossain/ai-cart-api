package org.aicart.store.customer.mapper;

import org.aicart.store.customer.dto.CustomerDTO;
import org.aicart.store.customer.entity.Customer;

public class CustomerMapper {

    public static CustomerDTO toDto(Customer customer) {
        if (customer == null) return null;

        CustomerDTO dto = new CustomerDTO();
        dto.id = String.valueOf(customer.id);
        dto.firstName = customer.firstName;
        dto.lastName = customer.lastName;
        dto.email = customer.email;
        dto.phone = customer.phone;
        dto.shopId = customer.shop.id;

        return dto;
    }

}
