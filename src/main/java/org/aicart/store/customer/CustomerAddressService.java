package org.aicart.store.customer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.aicart.store.customer.dto.CustomerAddressDTO;
import org.aicart.store.customer.dto.CustomerAddressCreateDTO;
import org.aicart.store.customer.dto.CustomerAddressUpdateDTO;
import org.aicart.store.customer.entity.Customer;
import org.aicart.store.customer.entity.CustomerAddress;
import org.aicart.store.customer.mapper.CustomerAddressMapper;
import org.aicart.store.user.entity.Shop;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class CustomerAddressService {

    @Inject
    CustomerAddressRepository addressRepository;

    @Inject
    CustomerRepository customerRepository;

    @Inject
    CustomerAddressMapper customerAddressMapper;

    // Legacy methods for backward compatibility
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

    // New comprehensive address management methods

    /**
     * Get all addresses for a customer
     */
    public List<CustomerAddressDTO> getCustomerAddresses(Shop shop, Long customerId) {
        Customer customer = customerRepository.findByIdAndShop(customerId, shop);
        if (customer == null) {
            throw new RuntimeException("Customer not found");
        }

        return customer.addresses.stream()
                .map(customerAddressMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific address by ID
     */
    public CustomerAddressDTO getAddress(Shop shop, Long customerId, Long addressId) {
        Customer customer = customerRepository.findByIdAndShop(customerId, shop);
        if (customer == null) {
            throw new RuntimeException("Customer not found");
        }

        CustomerAddress address = CustomerAddress.find("id = ?1 and customer.id = ?2", addressId, customerId)
                .firstResult();
        if (address == null) {
            throw new RuntimeException("Address not found");
        }

        return customerAddressMapper.toDTO(address);
    }

    /**
     * Create a new address for a customer
     */
    @Transactional
    public CustomerAddressDTO createAddress(Shop shop, Long customerId, CustomerAddressCreateDTO createRequest) {
        Customer customer = customerRepository.findByIdAndShop(customerId, shop);
        if (customer == null) {
            throw new RuntimeException("Customer not found");
        }

        // If this is set as default, unset other default addresses of the same type
        if (createRequest.isDefault()) {
            unsetDefaultAddresses(customer, createRequest.getType());
        }

        CustomerAddress address = new CustomerAddress();
        address.customer = customer;
        address.type = createRequest.getType();
        address.firstName = createRequest.getFirstName();
        address.lastName = createRequest.getLastName();
        address.company = createRequest.getCompany();
        address.line1 = createRequest.getLine1();
        address.line2 = createRequest.getLine2();
        address.city = createRequest.getCity();
        address.state = createRequest.getState();
        address.country = createRequest.getCountry();
        address.countryCode = createRequest.getCountryCode();
        address.postalCode = createRequest.getPostalCode();
        address.phone = createRequest.getPhone();
        address.isDefault = createRequest.isDefault();
        address.createdAt = LocalDateTime.now();
        address.updatedAt = LocalDateTime.now();

        address.persist();

        return customerAddressMapper.toDTO(address);
    }

    /**
     * Update an existing address
     */
    @Transactional
    public CustomerAddressDTO updateAddress(Shop shop, Long customerId, Long addressId, CustomerAddressUpdateDTO updateRequest) {
        Customer customer = customerRepository.findByIdAndShop(customerId, shop);
        if (customer == null) {
            throw new RuntimeException("Customer not found");
        }

        CustomerAddress address = CustomerAddress.find("id = ?1 and customer.id = ?2", addressId, customerId)
                .firstResult();
        if (address == null) {
            throw new RuntimeException("Address not found");
        }

        // If this is being set as default, unset other default addresses of the same type
        if (updateRequest.isDefault() != null && updateRequest.isDefault()) {
            unsetDefaultAddresses(customer, updateRequest.getType() != null ? updateRequest.getType() : address.type);
        }

        // Update fields
        if (updateRequest.getType() != null) address.type = updateRequest.getType();
        if (updateRequest.getFirstName() != null) address.firstName = updateRequest.getFirstName();
        if (updateRequest.getLastName() != null) address.lastName = updateRequest.getLastName();
        if (updateRequest.getCompany() != null) address.company = updateRequest.getCompany();
        if (updateRequest.getLine1() != null) address.line1 = updateRequest.getLine1();
        if (updateRequest.getLine2() != null) address.line2 = updateRequest.getLine2();
        if (updateRequest.getCity() != null) address.city = updateRequest.getCity();
        if (updateRequest.getState() != null) address.state = updateRequest.getState();
        if (updateRequest.getCountry() != null) address.country = updateRequest.getCountry();
        if (updateRequest.getCountryCode() != null) address.countryCode = updateRequest.getCountryCode();
        if (updateRequest.getPostalCode() != null) address.postalCode = updateRequest.getPostalCode();
        if (updateRequest.getPhone() != null) address.phone = updateRequest.getPhone();
        if (updateRequest.isDefault() != null) address.isDefault = updateRequest.isDefault();

        address.updatedAt = LocalDateTime.now();
        address.persist();

        return customerAddressMapper.toDTO(address);
    }

    /**
     * Delete an address
     */
    @Transactional
    public void deleteAddress(Shop shop, Long customerId, Long addressId) {
        Customer customer = customerRepository.findByIdAndShop(customerId, shop);
        if (customer == null) {
            throw new RuntimeException("Customer not found");
        }

        CustomerAddress address = CustomerAddress.find("id = ?1 and customer.id = ?2", addressId, customerId)
                .firstResult();
        if (address == null) {
            throw new RuntimeException("Address not found");
        }

        // Don't allow deletion if it's the only address
        long addressCount = CustomerAddress.count("customer.id = ?1", customerId);
        if (addressCount <= 1) {
            throw new RuntimeException("Cannot delete the only address. Customer must have at least one address.");
        }

        // If this was the default address, set another address as default
        if (address.isDefault) {
            CustomerAddress newDefault = CustomerAddress.find("customer.id = ?1 and id != ?2 and type = ?3",
                    customerId, addressId, address.type)
                    .firstResult();
            if (newDefault != null) {
                newDefault.isDefault = true;
                newDefault.persist();
            }
        }

        address.delete();
    }

    /**
     * Set an address as default
     */
    @Transactional
    public CustomerAddressDTO setAsDefault(Shop shop, Long customerId, Long addressId) {
        Customer customer = customerRepository.findByIdAndShop(customerId, shop);
        if (customer == null) {
            throw new RuntimeException("Customer not found");
        }

        CustomerAddress address = CustomerAddress.find("id = ?1 and customer.id = ?2", addressId, customerId)
                .firstResult();
        if (address == null) {
            throw new RuntimeException("Address not found");
        }

        // Unset other default addresses of the same type
        unsetDefaultAddresses(customer, address.type);

        // Set this address as default
        address.isDefault = true;
        address.updatedAt = LocalDateTime.now();
        address.persist();

        return customerAddressMapper.toDTO(address);
    }

    /**
     * Get default address for a customer by type
     */
    public CustomerAddressDTO getDefaultAddress(Shop shop, Long customerId, String type) {
        Customer customer = customerRepository.findByIdAndShop(customerId, shop);
        if (customer == null) {
            throw new RuntimeException("Customer not found");
        }

        CustomerAddress address = CustomerAddress.find("customer.id = ?1 and type = ?2 and isDefault = true",
                customerId, type)
                .firstResult();

        if (address == null) {
            // If no default address, return the first address of that type
            address = CustomerAddress.find("customer.id = ?1 and type = ?2", customerId, type)
                    .firstResult();
        }

        return address != null ? customerAddressMapper.toDTO(address) : null;
    }

    /**
     * Get addresses by type
     */
    public List<CustomerAddressDTO> getAddressesByType(Shop shop, Long customerId, String type) {
        Customer customer = customerRepository.findByIdAndShop(customerId, shop);
        if (customer == null) {
            throw new RuntimeException("Customer not found");
        }

        List<CustomerAddress> addresses = CustomerAddress.find("customer.id = ?1 and type = ?2", customerId, type)
                .list();

        return addresses.stream()
                .map(customerAddressMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Helper method to unset default addresses of a specific type
     */
    private void unsetDefaultAddresses(Customer customer, String type) {
        CustomerAddress.update("isDefault = false where customer.id = ?1 and type = ?2",
                customer.id, type);
    }

    /**
     * Validate address for checkout
     */
    public boolean validateAddressForCheckout(CustomerAddressDTO address) {
        return address != null &&
               address.firstName != null && !address.firstName.trim().isEmpty() &&
               address.line1 != null && !address.line1.trim().isEmpty() &&
               address.city != null && !address.city.trim().isEmpty() &&
               address.state != null && !address.state.trim().isEmpty() &&
               address.country != null && !address.country.trim().isEmpty() &&
               address.postalCode != null && !address.postalCode.trim().isEmpty();
    }
}
