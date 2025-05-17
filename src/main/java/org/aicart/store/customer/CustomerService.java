package org.aicart.store.customer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.aicart.store.customer.dto.CustomerDTO;
import org.aicart.store.customer.entity.Customer;

import java.util.Optional;

@ApplicationScoped
public class CustomerService {
    @Inject
    CustomerRepository customerRepository;

    @Inject
    CustomerAddressService addressService;

    public Optional<Customer> getCustomer(Long id) {
        return customerRepository.findByIdOptional(id);
    }

    public Customer getCustomerOrThrow(Long id) {
        return customerRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
    }

    @Transactional
    public Customer createCustomer(CustomerDTO dto) {
        Customer customer = new Customer();
        updateCustomerFromDto(customer, dto);
        customerRepository.persist(customer);
        return customer;
    }

    public Customer updateCustomer(Long id, CustomerDTO dto) {
        Customer customer = customerRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
        updateCustomerFromDto(customer, dto);
        return customer;
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    public Customer setPrimaryAddress(Long customerId, Long addressId) {
        Customer customer = getCustomerOrThrow(customerId);
        customer.primaryAddress = addressService.getAddressForCustomer(addressId, customerId);
        customerRepository.persist(customer);
        return customer;
    }

    private void updateCustomerFromDto(Customer customer, CustomerDTO dto) {
        customer.email = dto.email;
        customer.firstName = dto.firstName;
        customer.lastName = dto.lastName;
        customer.phone = dto.phone;
    }
}
