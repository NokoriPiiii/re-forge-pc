package com.reforgepc.service;

import com.reforgepc.entity.ProductAttributeValue;
import com.reforgepc.repository.ProductAttributeValueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductAttributeValueService {

    private final ProductAttributeValueRepository repository;

    public ProductAttributeValueService(ProductAttributeValueRepository repository) {
        this.repository = repository;
    }

    public List<ProductAttributeValue> getByProductId(Long productId) {
        return repository.findByProductId(productId);
    }

    @Transactional(readOnly = true)
    public Map<String, List<ProductAttributeValue>> getGroupedByProductId(Long productId) {
        List<ProductAttributeValue> values = repository.findByProductId(productId);

        Map<String, List<ProductAttributeValue>> grouped = new LinkedHashMap<>();

        for (ProductAttributeValue value : values) {
            String groupName = value.getAttribute().getGroup().getName();

            grouped.computeIfAbsent(groupName, key -> new java.util.ArrayList<>())
                    .add(value);
        }

        return grouped;
    }

    public ProductAttributeValue save(ProductAttributeValue value) {
        return repository.save(value);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}