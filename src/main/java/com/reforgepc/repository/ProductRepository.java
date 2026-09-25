package com.reforgepc.repository;

import com.reforgepc.entity.Product;
import com.reforgepc.entity.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByProductType(ProductType productType);

    Optional<Product> findByName(String name);
}