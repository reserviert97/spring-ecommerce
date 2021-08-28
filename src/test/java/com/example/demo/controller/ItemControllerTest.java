package com.example.demo.controller;


import com.example.demo.TestUtils;
import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void getItemByNameFail() {
        List<Item> entity = new ArrayList<Item>();
        when(itemRepository.findByName("not available widget")).thenReturn(entity);

        ResponseEntity<List<Item>> items = itemController.getItemsByName("not available widget");

        Assertions.assertEquals(404, items.getStatusCodeValue());
        Assertions.assertNotNull(items);
    }

    @Test
    public void getItemByNameSuccess() {
        List<Item> entity = new ArrayList<>();
        entity.add(new Item());
        when(itemRepository.findByName("Square Widget")).thenReturn(entity);

        ResponseEntity<List<Item>> items = itemController.getItemsByName("Square Widget");

        Assertions.assertNotNull(items);
        Assertions.assertEquals(200, items.getStatusCodeValue());
        Assertions.assertEquals(entity.size(), items.getBody().size());
    }

    @Test
    public void getItemByIdFail() {
        when(itemRepository.findById((long) 1)).thenReturn(Optional.empty());
        ResponseEntity<Item> item = itemController.getItemById((long) 1);

        Assertions.assertEquals(404, item.getStatusCodeValue());
        Assertions.assertNotNull(item);

    }

    @Test
    public void getItemByIdSuccess() {
        Item entity = new Item();
        entity.setId((long) 1);
        when(itemRepository.findById((long) 1)).thenReturn(Optional.of(entity));

        ResponseEntity<Item> item = itemController.getItemById((long) 1);

        Assertions.assertEquals(200, item.getStatusCodeValue());
        Assertions.assertNotNull(item);
    }


    @Test
    public void getItems() {
        ResponseEntity<List<Item>> items = itemController.getItems();

        Assertions.assertEquals(200, items.getStatusCodeValue());
        Assertions.assertNotNull(items);
    }
}
