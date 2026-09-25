package com.reforgepc.service;

import com.reforgepc.entity.Product;
import com.reforgepc.entity.ProductType;
import com.reforgepc.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getComponents() {
        return productRepository.findByProductType(ProductType.COMPONENT);
    }

    public List<Product> getPrebuiltPCs() {
        return productRepository.findByProductType(ProductType.PREBUILT_PC);
    }
}