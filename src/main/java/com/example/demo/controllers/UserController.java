package com.example.demo.controllers;

import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final static Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @GetMapping("/id/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        log.info("Looking up User by id");
        User u = userService.findById(id);
        return u == null ?
                ResponseEntity.badRequest().body("Unknown user") :
                ResponseEntity.ok(u);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> findByUserName(@PathVariable String username) {
        log.info("Looking up User by username");
        User u = userService.findByUsername(username);
        return u == null ?
                ResponseEntity.badRequest().body("Unknown user") :
                ResponseEntity.ok(u);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        log.info("Attempting to create user");
        log.debug(request.toString());
        try {
            if (!passwordMatch(request)){
                log.info("Create user failure");
                return ResponseEntity.badRequest().body("Passwords do not match");
            }
            if (!hasMinimumLength(request)){
                log.info("Create user failure");
                return ResponseEntity.badRequest().body("Password is too short");
            }
            log.info("Create user successful");
            return ResponseEntity.ok(userService.save(
                    request.getUsername(), request.getPassword()));
        } catch (Exception e) {
            log.error(e.getMessage());
            log.info("Create user failure");
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private boolean passwordMatch(CreateUserRequest request) {
        return request.getPassword().equals(request.getConfirmPassword());
    }

    private boolean hasMinimumLength(CreateUserRequest request) {
        return request.getPassword().length() > 7;
    }
}
