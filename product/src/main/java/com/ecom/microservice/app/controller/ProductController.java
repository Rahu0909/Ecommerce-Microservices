package com.ecom.microservice.app.controller;

import com.ecom.microservice.app.dto.ProductRequest;
import com.ecom.microservice.app.dto.ProductResponse;
import com.ecom.microservice.app.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/simulate")
    public ResponseEntity<String> simulateFailure(@RequestParam(defaultValue = "false") boolean fail) {
        if (fail) {
            throw new RuntimeException("Simulated Failure for Testing");
        }
        return ResponseEntity.ok("Product Service is OK");
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProducts() {
        return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductsById(@PathVariable String id) {
        return productService.getProductsById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping()
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest productRequest) {
        return new ResponseEntity<>(productService.createProduct(productRequest), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@RequestBody ProductRequest productRequest, @PathVariable Long id) {
        return productService.updateProduct(productRequest, id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        boolean deleted = productService.deleteProduct(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String keyword) {
        return ResponseEntity.ok(productService.searchProducts(keyword));
    }
}
