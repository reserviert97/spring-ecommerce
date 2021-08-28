package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private final static int PASSWORD_MINIMUM_SIZE = 7;

    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findByUsername(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            log.error("user " + username + " not found", new EntityNotFoundException());
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(user);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
        User user = new User();
        Cart cart = new Cart();
        cartRepository.save(cart);

        user.setCart(cart);
        user.setUsername(createUserRequest.getUsername());

        try {
            if (createUserRequest.getPassword() == null
                    || createUserRequest.getPassword().length() < PASSWORD_MINIMUM_SIZE
                    || !createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
                log.error("register failed, username " + createUserRequest.getUsername(), createUserRequest.getPassword());
                return ResponseEntity.badRequest().build();
            }

            user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
            userRepository.save(user);
            log.info("register success, username " + createUserRequest.getUsername());
            return ResponseEntity.ok(user);

        } catch (Exception e) {
            log.error("register failed, username " + createUserRequest.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

}
