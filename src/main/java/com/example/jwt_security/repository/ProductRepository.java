package com.example.jwt_security.repository;


import com.example.jwt_security.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}