package org.example.inventory.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.inventory.model.Item;
import org.example.inventory.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Tag(name = "Inventory Management API", description = "APIs for managing inventory items")
@CrossOrigin(origins = "*")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new item")
    public Item createItem(@Valid @RequestBody Item item) {
        return itemService.createItem(item);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an item by ID")
    public Item getItem(@PathVariable UUID id) {
        return itemService.getItemById(id);
    }

    @GetMapping
    public List<Item> getAllItems() {
        return itemService.getAllItems();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing item")
    public Item updateItem(@PathVariable UUID id, @Valid @RequestBody Item item) {
        return itemService.updateItem(id, item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an item")
    public void deleteItem(@PathVariable UUID id) {
        itemService.deleteItem(id);
    }
}
