package com.cryptoswarm.customer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api/v1/customers")
public record CustomerController(CustomerService customerService) {

    @PostMapping("")
    public ResponseEntity<CustomerRegistrationResponse> registerCustomer(
            @RequestBody @Valid CustomerRegistrationRequest customerRegistrationRequest) {
        log.info("New customer registration {}", customerRegistrationRequest);
        var response = customerService.register(customerRegistrationRequest);

        return ResponseEntity.created(URI.create("/api/v1/customers/something")).body(response);
    }
}
