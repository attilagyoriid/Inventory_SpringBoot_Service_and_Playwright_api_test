package org.example.inventory.integration;

import lombok.extern.slf4j.Slf4j;
import org.example.inventory.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@SqlGroup({
        @Sql(scripts = "classpath:db/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = "classpath:db/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = "classpath:db/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
})
@Slf4j
public class InventoryApiIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @LocalServerPort
    private int port;

    private String baseUrl;
    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        baseUrl = String.format("http://localhost:%d/items", port);
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        log.info("Test setup complete with base URL: {}", baseUrl);
    }

    @Test
    @DisplayName("Should create new item")
    void createItem_Success() {
        // Given
        Item item = Item.builder()
                .name("New Test Item")
                .quantity(15)
                .price(149.99f)
                .build();

        HttpEntity<Item> requestEntity = new HttpEntity<>(item, headers);

        // When
        ResponseEntity<Item> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                requestEntity,
                Item.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("New Test Item");
        assertThat(response.getBody().getQuantity()).isEqualTo(15);
        assertThat(response.getBody().getPrice())
                .isEqualByComparingTo(149.99f);

        // Verify database
        Item savedItem = jdbcTemplate.queryForObject(
                "SELECT * FROM item WHERE id = ?",
                new BeanPropertyRowMapper<>(Item.class),
                response.getBody().getId()
        );
        assertThat(savedItem).isNotNull();
        assertThat(savedItem.getName()).isEqualTo("New Test Item");
        assertThat(savedItem.getQuantity()).isEqualTo(15);
    }

    @Test
    @DisplayName("Should get existing item")
    void getItem_Success() {
        // Given - Using predefined test data
        UUID existingItemId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        // When
        ResponseEntity<Item> response = restTemplate.getForEntity(
                baseUrl + "/{id}",
                Item.class,
                existingItemId
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(existingItemId);
        assertThat(response.getBody().getName()).isEqualTo("Test Item 1");
        assertThat(response.getBody().getQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should update existing item")
    void updateItem_Success() {
        // Given
        UUID existingItemId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        Item updateItem = Item.builder()
                .name("Updated Item Name")
                .quantity(25)
                .price(299.99f)
                .build();

        HttpEntity<Item> requestEntity = new HttpEntity<>(updateItem, headers);

        // When
        ResponseEntity<Item> response = restTemplate.exchange(
                baseUrl + "/{id}",
                HttpMethod.PUT,
                requestEntity,
                Item.class,
                existingItemId
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Updated Item Name");
        assertThat(response.getBody().getQuantity()).isEqualTo(25);
        assertThat(response.getBody().getPrice())
                .isEqualByComparingTo(299.99f);

        // Verify database
        Item updatedItem = jdbcTemplate.queryForObject(
                "SELECT * FROM item WHERE id = ?",
                new BeanPropertyRowMapper<>(Item.class),
                existingItemId
        );
        assertThat(updatedItem).isNotNull();
        assertThat(updatedItem.getName()).isEqualTo("Updated Item Name");
        assertThat(updatedItem.getQuantity()).isEqualTo(25);
    }

    @Test
    @DisplayName("Should delete existing item")
    void deleteItem_Success() {
        // Given
        UUID existingItemId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        // When
        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/{id}",
                HttpMethod.DELETE,
                null,
                Void.class,
                existingItemId
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify database
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM item WHERE id = ?",
                Integer.class,
                existingItemId
        );
        assertThat(count).isZero();
    }

    @Test
    @DisplayName("Should get all item")
    void getAllItems_Success() {
        // When
        ResponseEntity<List<Item>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Item>>() {
                }
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2); // Based on data.sql
        assertThat(response.getBody().get(0).getName()).isEqualTo("Test Item 1");
        assertThat(response.getBody().get(1).getName()).isEqualTo("Test Item 2");
    }

    @Test
    @DisplayName("Should handle validation errors")
    void createItem_ValidationError() {
        // Given
        Item invalidItem = Item.builder()
                .name("")
                .quantity(-1)
                .price(-10.99f)
                .build();

        HttpEntity<Item> requestEntity = new HttpEntity<>(invalidItem, headers);

        // When
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<Map<String, Object>>() {
                }
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("errors")).isNotNull();
    }

    @Test
    @DisplayName("Should handle not found error")
    void getItem_NotFound() {
        // When
        ResponseEntity<Map> response = restTemplate.getForEntity(
                baseUrl + "/{id}",
                Map.class,
                UUID.randomUUID()
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("message")).isNotNull();
    }
}
