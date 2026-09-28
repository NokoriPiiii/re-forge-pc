package com.reforgepc.service;

import com.reforgepc.entity.ProductAttributeValue;
import com.reforgepc.repository.ProductAttributeValueRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductAttributeValueService {

    private final ProductAttributeValueRepository repository;

    public ProductAttributeValueService(ProductAttributeValueRepository repository) {
        this.repository = repository;
    }

    public List<ProductAttributeValue> getByProductId(Long productId) {
        return repository.findByProductId(productId);
    }

    public ProductAttributeValue save(ProductAttributeValue value) {
        return repository.save(value);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}