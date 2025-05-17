package org.aicart.store.customer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.aicart.store.customer.dto.CustomerAddressDTO;
import org.aicart.store.customer.entity.Customer;
import org.aicart.store.customer.entity.CustomerAddress;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CustomerAddressService {

    @Inject
    CustomerAddressRepository addressRepository;

    @Inject
    CustomerRepository customerRepository;

    public CustomerAddress getAddressForCustomer(Long addressId, Long customerId) {
        return addressRepository.find("id = ?1 and customer.id = ?2", addressId, customerId)
                .firstResultOptional()
                .orElseThrow(() -> new NotFoundException("Address not found for customer"));
    }

    public Optional<CustomerAddress> findAddressForCustomer(Long addressId, Long customerId) {
        return addressRepository.find("id = ?1 and customer.id = ?2", addressId, customerId)
                .firstResultOptional();
    }

    public List<CustomerAddress> getCustomerAddresses(Long customerId) {
        return addressRepository.listByCustomerId(customerId);
    }

    public CustomerAddress createAddress(Long customerId, CustomerAddressDTO dto) {
        Customer customer = customerRepository.findByIdOptional(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        CustomerAddress address = new CustomerAddress();
        mapDtoToEntity(dto, address);
        address.customer = customer;
        addressRepository.persist(address);
        return address;
    }

    public CustomerAddress updateAddress(Long customerId, Long addressId, CustomerAddressDTO dto) {
        CustomerAddress address = addressRepository.findByIdAndCustomerId(addressId, customerId)
                .orElseThrow(() -> new NotFoundException("Address not found"));

        mapDtoToEntity(dto, address);
        addressRepository.persist(address);
        return address;
    }

    public void deleteAddress(Long customerId, Long addressId) {
        boolean deleted = addressRepository.deleteByIdAndCustomerId(addressId, customerId);
        if (!deleted) {
            throw new NotFoundException("Address not found or doesn't belong to customer");
        }
    }

    private void mapDtoToEntity(CustomerAddressDTO dto, CustomerAddress entity) {
        entity.firstName = dto.firstName;
        entity.lastName = dto.lastName;
        entity.line1 = dto.line1;
        entity.line2 = dto.line2;
        entity.city = dto.city;
        entity.state = dto.state;
        entity.postalCode = dto.postalCode;
        entity.countryCode = dto.countryCode;
        entity.phone = dto.phone;
    }
}
