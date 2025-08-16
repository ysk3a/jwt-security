package com.example.jwt_security.service;


import com.example.jwt_security.model.Product;
import com.example.jwt_security.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductService {

    @Autowired
    private final ProductRepository productRepository;

    // Get all the product
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    // Get product by id
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // Save/Update product
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    // Delete product
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }
}
