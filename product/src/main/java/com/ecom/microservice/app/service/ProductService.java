package com.ecom.microservice.app.service;

import com.ecom.microservice.app.dto.ProductRequest;
import com.ecom.microservice.app.dto.ProductResponse;
import com.ecom.microservice.app.model.Product;
import com.ecom.microservice.app.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final ModelMapper modelMapper;

    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = modelMapper.map(productRequest,
                Product.class);
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct,
                ProductResponse.class);
    }

    public Optional<ProductResponse> updateProduct(ProductRequest productRequest,
                                                   Long id) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    modelMapper.map(productRequest,
                            existingProduct);
                    Product save = productRepository.save(existingProduct);
                    return modelMapper.map(save,
                            ProductResponse.class);
                });
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> allProducts = productRepository.findByActiveTrue();
        return allProducts.stream()
                .map(product -> modelMapper.map(product,
                        ProductResponse.class))
                .toList();
    }

    public boolean deleteProduct(Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setActive(false);
                    productRepository.save(product);
                    return true;
                })
                .orElse(false);
    }

    public List<ProductResponse> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword)
                .stream()
                .map((element) -> modelMapper.map(element,
                        ProductResponse.class))
                .toList();

    }

    public Optional<ProductResponse> getProductsById(String id) {
        return productRepository.findByIdAndActiveTrue(Long.valueOf(id)).map((element) -> modelMapper.map(element, ProductResponse.class));
    }
}
