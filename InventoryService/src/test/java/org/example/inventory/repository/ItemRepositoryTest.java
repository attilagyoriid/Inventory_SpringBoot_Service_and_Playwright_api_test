package org.example.inventory.repository;


import org.example.inventory.model.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository repository;

    @Test
    void shouldSaveAndRetrieveItem() {
        Item item = Item.builder()
                .name("Test Item")
                .quantity(10)
                .price(99.99f)
                .build();

        Item saved = repository.save(item);
        Optional<Item> found = repository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Item");
    }
}