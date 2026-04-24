package com.cryptoswarm.customer.integration;

import com.cryptoswarm.customer.CustomerRegistrationRequest;
import com.cryptoswarm.customer.CustomerRegistrationResponse;
import com.cryptoswarm.customer.CustomerRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerControllerTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    TestRestTemplate restTemplate;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
    }

    @Test
    void shouldCreateCustomer() {
        CustomerRegistrationRequest request = new CustomerRegistrationRequest("foo", "bar", "test@foo.io");
        ResponseEntity<CustomerRegistrationResponse> response = restTemplate.postForEntity("/api/v1/customers", request, CustomerRegistrationResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test@foo.io", response.getBody().email());
    }

    @Test
    void shouldReturnConflictForDuplicateEmail() {
        CustomerRegistrationRequest request = new CustomerRegistrationRequest("foo", "bar", "test@foo.io");
        restTemplate.postForEntity("/api/v1/customers", request, CustomerRegistrationResponse.class);

        ResponseEntity<Void> duplicate = restTemplate.postForEntity("/api/v1/customers", request, Void.class);

        assertEquals(HttpStatus.CONFLICT, duplicate.getStatusCode());
    }

    @Test
    void shouldReturnBadRequestForInvalidEmail() {
        CustomerRegistrationRequest request = new CustomerRegistrationRequest("foo", "bar", "not-an-email");
        ResponseEntity<Void> response = restTemplate.postForEntity("/api/v1/customers", request, Void.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void shouldFindCustomerWhenEmailValid() {
        CustomerRegistrationRequest request = new CustomerRegistrationRequest("foo", "bar", "test@foo.io");
        restTemplate.postForEntity("/api/v1/customers", request, CustomerRegistrationResponse.class);

        ResponseEntity<CustomerRegistrationResponse> response = restTemplate.exchange("/api/v1/customers/test@foo.io", HttpMethod.GET, null, CustomerRegistrationResponse.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
