package com.cryptoswarm.customer;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;



@Service
@Slf4j
public record CustomerService(CustomerRepository customerRepository) {

    public CustomerRegistrationResponse register(CustomerRegistrationRequest request) {
        var existingCustomer = customerRepository.findByEmail(request.email());
        if(existingCustomer.isPresent()){
            log.error("Registering customer failed. Email {} already taken", request.email());
            throw new EntityExistsException("Customer with email " +request.email() + "already exists");
        }

        Customer customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .build();

        var createdCustomer = customerRepository.save(customer);

        return new CustomerRegistrationResponse(
                createdCustomer.getFirstName(), createdCustomer.getLastName(), createdCustomer.getEmail()
        );
    }

    public CustomerRegistrationResponse retrieve(String email) {
        var existingCustomer = customerRepository.findByEmail(email);
        if(existingCustomer.isEmpty()){
            log.error("Customer by email {} not found", email);
            throw new EntityNotFoundException("Customer with email " + email+ "does not exist");
        }

        Customer customer = existingCustomer.get();

        return new CustomerRegistrationResponse(
                customer.getFirstName(), customer.getLastName(), customer.getEmail()
        );
    }
}
