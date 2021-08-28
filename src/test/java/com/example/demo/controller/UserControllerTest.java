package com.example.demo.controller;


import com.example.demo.TestUtils;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }

    @Test
    public void createUserSuccess() {
        when(bCryptPasswordEncoder.encode("testing#123")).thenReturn("hashedPassword");

        CreateUserRequest request = new CreateUserRequest();
        request.setPassword("testing#123");
        request.setConfirmPassword("testing#123");
        request.setUsername("admin");

        ResponseEntity<User> response = userController.createUser(request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        Assertions.assertNotNull(user);
        Assertions.assertEquals(0, user.getId());
        Assertions.assertEquals("admin", user.getUsername());
        Assertions.assertEquals("hashedPassword", user.getPassword());
    }

    @Test
    public void createUserFail() {
        CreateUserRequest request = new CreateUserRequest();
        request.setPassword("testing#123");
        request.setConfirmPassword("testing#12345");
        request.setUsername("admin");

        ResponseEntity<User> response = userController.createUser(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void getUserByUsernameFail() {
        when(userRepository.findByUsername("admin")).thenReturn(null);

        ResponseEntity<User> user = userController.findByUsername("admin");
        Assertions.assertEquals(404, user.getStatusCodeValue());
    }


    @Test
    public void getUserByUsernameSuccess() {
        User entity = new User();
        entity.setId(1);
        entity.setUsername("admin");
        entity.setPassword("testing#123");
        entity.setCart(new Cart());

        when(userRepository.findByUsername("admin")).thenReturn(entity);
        ResponseEntity<User> user = userController.findByUsername("admin");

        Assertions.assertEquals(200, user.getStatusCodeValue());
    }

    @Test
    public void getUserByIdFail() {
        when(userRepository.findById((long) 1)).thenReturn(Optional.empty());

        ResponseEntity<User> user = userController.findById((long) 1);
        Assertions.assertEquals(404, user.getStatusCodeValue());
    }

    @Test
    public void getUserByIdSuccess() {
        User entity = new User();
        entity.setId(1);
        entity.setUsername("admin");
        entity.setPassword("testing#123");
        entity.setCart(new Cart());

        when(userRepository.findById((long) 1)).thenReturn(Optional.of(entity));

        ResponseEntity<User> user = userController.findById((long) 1);
        Assertions.assertEquals(200, user.getStatusCodeValue());
    }
}
