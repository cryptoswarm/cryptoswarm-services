package com.cryptoswarm.customer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{email}")
    public ResponseEntity<CustomerRegistrationResponse> retrieveCustomer(
            @PathVariable("email") @Email(message = "Email must be valid") String email) {
        log.info("Retrieving customer by email {}", email);
        var response = customerService.retrieve(email);

        return ResponseEntity.ok(response);
    }
}
