package com.reforgepc.repository;

import com.reforgepc.entity.ProductAttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductAttributeValueRepository
        extends JpaRepository<ProductAttributeValue, Long> {

    List<ProductAttributeValue> findByProductId(Long productId);

    Optional<ProductAttributeValue> findByProductIdAndAttributeIdAndValue(
            Long productId,
            Long attributeId,
            String value
    );
}