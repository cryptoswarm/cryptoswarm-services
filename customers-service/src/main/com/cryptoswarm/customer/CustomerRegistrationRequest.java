package com.cryptoswarm.customer;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public record CustomerRegistrationRequest(
        @NotBlank(message = "firstName is required") String firstName,
        @NotBlank(message = "lastName is required") String lastName,
        @NotBlank(message = "email is required") @Email(message = "email must be valid") String email) {
}
