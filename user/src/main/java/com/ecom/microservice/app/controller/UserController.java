package com.ecom.microservice.app.controller;

import com.ecom.microservice.app.dto.UserRequest;
import com.ecom.microservice.app.dto.UserResponse;
import com.ecom.microservice.app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    //    private static Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> userResponses = userService.fetchALlList();
        return new ResponseEntity<>(userResponses,
                HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUsers(@PathVariable String id) {
        log.info("Request Received for user: {}", id);
        log.trace("This is TRACE level - very detailed logs");
        log.debug("This is DEBUG level - use for development debugging");
        log.info("This is INFO level - General System Info");
        log.warn("This is WARN level - Something might be wrong");
        log.error("This is ERROR level - Something Failed");
        return userService.getUser(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound()
                        .build());
    }

    @PostMapping("/users")
    public ResponseEntity<String> createUsers(@RequestBody UserRequest userRequest) {
        userService.createUsers(userRequest);
        return new ResponseEntity<>("User Created Successfully!",
                HttpStatus.CREATED);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<String> updateUsers(@PathVariable String id,
                                              @RequestBody UserRequest userRequest) {
        boolean updated = userService.updateUser(id,
                userRequest);
        if (updated) {
            return ResponseEntity.ok("User Updated Successfully!");
        }
        return ResponseEntity.notFound()
                .build();
    }
}
