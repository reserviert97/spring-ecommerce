package com.example.demo.controller;


import com.example.demo.TestUtils;
import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    @Before
    public void setUp () {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void modifyCart(){
        User entityUser = new User();
        entityUser.setId(1);
        entityUser.setUsername("admin");
        entityUser.setPassword("testing#123");
        entityUser.setCart(new Cart());

        when(userRepository.findByUsername(entityUser.getUsername())).thenReturn(entityUser);
        when(cartRepository.save(any())).thenReturn(new Cart());

        Optional<Item> optionalItem = Optional.of(new Item());
        optionalItem.get().setPrice(BigDecimal.valueOf(50000));

        when(itemRepository.findById(any())).thenReturn(optionalItem);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1);
        request.setQuantity(1);
        request.setUsername(entityUser.getUsername());


        ResponseEntity<Cart> response = cartController.addToCart(request);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response);

        Cart cart= response.getBody();
        Assertions.assertNotNull(cart);
        Assertions.assertEquals(50000, cart.getTotal().intValue());
    }
}

