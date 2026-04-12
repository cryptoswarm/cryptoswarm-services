package com.cyptoswarm.customer.unit;

import com.cryptoswarm.customer.Customer;
import com.cryptoswarm.customer.CustomerRegistrationRequest;
import com.cryptoswarm.customer.CustomerRepository;
import com.cryptoswarm.customer.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityExistsException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void should_create_user_success(){

        var request = new CustomerRegistrationRequest(
                "foo", "bar", "foo@bar.com"
        );

        when(customerRepository.save(any(Customer.class)))
                .thenReturn(Customer.builder()
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .id(1).build()
        );

        var registrationResponse = customerService.register(request);

        assertEquals("foo@bar.com", registrationResponse.email());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void should_throw_exception_when_customer_already_exists(){

        var request = new CustomerRegistrationRequest(
                "foo", "bar", "foo@bar.com"
        );

        when(customerRepository.findByEmail(request.email())).thenReturn(Optional.of(Customer.builder().email(request.email()).firstName(request.firstName()).lastName(request.lastName()).id(1).build()));

        var exception = assertThrows(EntityExistsException.class,
                () -> customerService.register(request));

        assertEquals("Customer with email " +request.email() + "already exists", exception.getMessage());
    }
}
