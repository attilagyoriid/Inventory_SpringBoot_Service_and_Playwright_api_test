package org.example.inventory.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.inventory.exception.ItemNotFoundException;
import org.example.inventory.model.Item;
import org.example.inventory.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public Item createItem(Item item) {
        log.info("Creating new item: {}", item);
        return itemRepository.save(item);
    }

    public Item getItemById(UUID id) {
        log.info("Fetching item with id: {}", id);
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found with id: " + id));
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Item updateItem(UUID id, Item updatedItem) {
        log.info("Updating item with id: {}", id);
        Item existingItem = getItemById(id);
        existingItem.setName(updatedItem.getName());
        existingItem.setQuantity(updatedItem.getQuantity());
        existingItem.setPrice(updatedItem.getPrice());
        return itemRepository.save(existingItem);
    }

    public void deleteItem(UUID id) {
        if (!itemRepository.existsById(id)) {
            throw new ItemNotFoundException("Item not found with id: " + id);
        }
        itemRepository.deleteById(id);
    }
}
