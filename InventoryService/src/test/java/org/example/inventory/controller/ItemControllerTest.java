package org.example.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.inventory.exception.ItemNotFoundException;
import org.example.inventory.model.Item;
import org.example.inventory.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ItemController.class)
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    private Item testItem;
    private UUID uuid;

    @BeforeEach
    void setUp() {

        testItem = new Item();
        uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        testItem.setId(uuid);
        testItem.setName("Test Item");
        testItem.setQuantity(10);
        testItem.setPrice(99.99f);
    }

    @Test
    void createItem_WithValidData_ShouldReturnCreated() throws Exception {
        when(itemService.createItem(any(Item.class))).thenReturn(testItem);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testItem)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testItem.getId().toString()))
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.quantity").value(10))
                .andExpect(jsonPath("$.price").value(99.99))
                .andDo(print());

        verify(itemService).createItem(any(Item.class));
    }

    @Test
    void createItem_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        Item invalidRequest = new Item();
        invalidRequest.setName(""); // Invalid name
        invalidRequest.setQuantity(-1); // Invalid quantity
        invalidRequest.setPrice(-10.0f); // Invalid price

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors[?(@.field == 'name')].error").value("Name is required"))
                .andExpect(jsonPath("$.errors[?(@.field == 'quantity')].error").value("Quantity must be greater than or equal to 0"))
                .andExpect(jsonPath("$.errors[?(@.field == 'price')].error").value("Price must be greater than or equal to 0"))
                .andDo(print());

        verify(itemService, never()).createItem(any(Item.class));
    }

    @Test
    void getItem_WithExistingId_ShouldReturnItem() throws Exception {
        when(itemService.getItemById(uuid)).thenReturn(testItem);

        mockMvc.perform(get("/items/{id}", uuid.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(uuid.toString()))
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.quantity").value(10))
                .andExpect(jsonPath("$.price").value(99.99))
                .andDo(print());

        verify(itemService).getItemById(uuid);
    }

    @Test
    void getItem_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        when(itemService.getItemById(uuid))
                .thenThrow(new ItemNotFoundException("Item not found with id: non-existent-id"));

        mockMvc.perform(get("/items/{id}", uuid.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Item not found with id: non-existent-id"))
                .andDo(print());

        verify(itemService).getItemById(uuid);
    }

    @Test
    void updateItem_WithValidData_ShouldReturnUpdatedItem() throws Exception {
        when(itemService.updateItem(eq(uuid), any(Item.class))).thenReturn(testItem);

        mockMvc.perform(put("/items/{id}", uuid.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(uuid.toString()))
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.quantity").value(10))
                .andExpect(jsonPath("$.price").value(99.99))
                .andDo(print());

        verify(itemService).updateItem(eq(uuid), any(Item.class));
    }

    @Test
    void updateItem_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        Item invalidRequest = new Item();
        invalidRequest.setName("");
        invalidRequest.setQuantity(-1);
        invalidRequest.setPrice(-10.0f);

        mockMvc.perform(put("/items/{id}", uuid.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors[?(@.field == 'name')].error").value("Name is required"))
                .andExpect(jsonPath("$.errors[?(@.field == 'quantity')].error").value("Quantity must be greater than or equal to 0"))
                .andExpect(jsonPath("$.errors[?(@.field == 'price')].error").value("Price must be greater than or equal to 0"))
                .andDo(print());

        verify(itemService, never()).updateItem(any(UUID.class), any(Item.class));
    }

    @Test
    void updateItem_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        when(itemService.updateItem(eq(uuid), any(Item.class)))
                .thenThrow(new ItemNotFoundException("Item not found with id: non-existent-id"));

        mockMvc.perform(put("/items/{id}", uuid.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testItem)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Item not found with id: non-existent-id"))
                .andDo(print());

        verify(itemService).updateItem(eq(uuid), any(Item.class));
    }

    @Test
    void deleteItem_WithExistingId_ShouldReturnNoContent() throws Exception {
        doNothing().when(itemService).deleteItem(uuid);

        mockMvc.perform(delete("/items/{id}", uuid.toString()))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(itemService).deleteItem(uuid);
    }

    @Test
    void deleteItem_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        doThrow(new ItemNotFoundException("Item not found with id: non-existent-id"))
                .when(itemService).deleteItem(uuid);

        mockMvc.perform(delete("/items/{id}", uuid.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Item not found with id: non-existent-id"))
                .andDo(print());

        verify(itemService).deleteItem(uuid);
    }

    @Test
    void handleMethodArgumentNotValid() throws Exception {
        Item invalidRequest = new Item();

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").isArray())
                .andDo(print());
    }
}
