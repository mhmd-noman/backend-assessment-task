package com.midas.app.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StripeIntegrationTest {

  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

  @LocalServerPort private int port;

  private final TestRestTemplate restTemplate = new TestRestTemplate();

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void connectionEstablished() {
    assertTrue(postgres.isCreated());
    assertTrue(postgres.isRunning());
  }

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
    registry.add("spring.sql.init.schema-locations", () -> "classpath:sql/schema.sql");
  }

  @Test
  public void testCreateAndGetAccount() throws JsonProcessingException {
    // Account Creation
    String createUrl = "http://localhost:" + port + "/accounts";
    HttpHeaders createHeaders = new HttpHeaders();
    createHeaders.add("Content-Type", "application/json");
    String createBody =
        "{\"firstName\":\"Muhammad\",\"lastName\":\"Noman\",\"email\":\"mhmd-noman@gmail.com\"}";
    HttpEntity<String> createRequest = new HttpEntity<>(createBody, createHeaders);
    ResponseEntity<String> createResponse =
        restTemplate.exchange(createUrl, HttpMethod.POST, createRequest, String.class);
    assertEquals(201, createResponse.getStatusCodeValue());

    // Get Account
    String getUrl = "http://localhost:" + port + "/accounts";
    HttpHeaders getHeaders = new HttpHeaders();
    getHeaders.add("Content-Type", "application/json");
    HttpEntity<String> getRequest = new HttpEntity<>(getHeaders);
    ResponseEntity<String> getResponse =
        restTemplate.exchange(getUrl, HttpMethod.GET, getRequest, String.class);
    assertEquals(200, getResponse.getStatusCodeValue());

    // Parse the response body
    List<Map<String, String>> accounts =
        objectMapper.readValue(
            getResponse.getBody(),
            objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class));

    // Verify the response
    assertNotNull(accounts);
    assertEquals(1, accounts.size());
    Map<String, String> account = accounts.get(0);
    assertEquals("Muhammad", account.get("firstName"));
    assertEquals("Noman", account.get("lastName"));
    assertEquals("mhmd-noman@gmail.com", account.get("email"));
    assertEquals("STRIPE", account.get("providerType"));
    assertNotNull(account.get("providerId"));
  }
}
