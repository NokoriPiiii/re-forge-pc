package com.reforgepc.repository;

import com.reforgepc.entity.AttributeOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttributeOptionRepository extends JpaRepository<AttributeOption, Long> {

    List<AttributeOption> findByAttributeId(Long attributeId);
}