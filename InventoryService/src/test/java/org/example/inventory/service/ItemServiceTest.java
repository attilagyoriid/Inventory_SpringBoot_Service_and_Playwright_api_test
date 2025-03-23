package org.example.inventory.service;

import org.example.inventory.exception.ItemNotFoundException;
import org.example.inventory.model.Item;
import org.example.inventory.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @Mock
    private ItemRepository repository;

    @InjectMocks
    private ItemService service;

    @Test
    void shouldCreateItem() {
        Item item = Item.builder()
                .name("Test Item")
                .quantity(10)
                .price(99.99f)
                .build();

        when(repository.save(any(Item.class))).thenReturn(item);

        Item result = service.createItem(item);
        assertThat(result).isNotNull();
        verify(repository).save(any(Item.class));
    }

    @Test
    void shouldThrowExceptionWhenItemNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.getItemById(id));
    }


    @Test
    void shouldUpdateItem() {
        UUID id = UUID.randomUUID();
        Item existingItem = Item.builder()
                .id(id)
                .name("Existing Item")
                .quantity(5)
                .price(50.0f)
                .build();
        Item updatedItem = Item.builder()
                .name("Updated Item")
                .quantity(10)
                .price(100.0f)
                .build();

        when(repository.findById(id)).thenReturn(Optional.of(existingItem));
        when(repository.save(any(Item.class))).thenReturn(updatedItem);

        Item result = service.updateItem(id, updatedItem);
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Item");
        assertThat(result.getQuantity()).isEqualTo(10);
        assertThat(result.getPrice()).isEqualTo(100.0f);
        verify(repository).findById(id);
        verify(repository).save(existingItem);
    }

    @Test
    void shouldDeleteItem() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(true);

        service.deleteItem(id);
        verify(repository).deleteById(id);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingItem() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(false);

        assertThrows(ItemNotFoundException.class, () -> service.deleteItem(id));
    }

    @Test
    void shouldGetAllItems() {
        Item item1 = Item.builder()
                .name("Item 1")
                .quantity(10)
                .price(99.99f)
                .build();
        Item item2 = Item.builder()
                .name("Item 2")
                .quantity(20)
                .price(199.99f)
                .build();

        when(repository.findAll()).thenReturn(Arrays.asList(item1, item2));

        List<Item> items = service.getAllItems();
        assertThat(items).hasSize(2);
        assertThat(items).contains(item1, item2);
        verify(repository).findAll();
    }
}
