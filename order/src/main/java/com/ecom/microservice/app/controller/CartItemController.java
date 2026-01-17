package com.ecom.microservice.app.controller;

import com.ecom.microservice.app.dto.CartItemRequest;
import com.ecom.microservice.app.model.CartItem;
import com.ecom.microservice.app.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;

    @PostMapping
    public ResponseEntity<String> addToCart(@RequestHeader("X-User-ID") String userId,
                                            @RequestBody CartItemRequest request) {
        if (!cartItemService.addToCart(userId,
                request)) {
            return ResponseEntity.badRequest()
                    .body("Not able to complete the request");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeFromCart(@RequestHeader("X-User-ID") String userId,
                                               @PathVariable String productId) {
        boolean deleted = cartItemService.deleteItemFromCart(userId,
                productId);
        return deleted ? ResponseEntity.noContent()
                .build() : ResponseEntity.notFound()
                .build();
    }

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(@RequestHeader("X-User-ID") String userId) {
        return ResponseEntity.ok(cartItemService.getCart(userId));
    }
}
