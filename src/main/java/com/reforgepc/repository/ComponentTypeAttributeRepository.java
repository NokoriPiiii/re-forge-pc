package com.reforgepc.repository;

import com.reforgepc.entity.ComponentTypeAttribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComponentTypeAttributeRepository extends JpaRepository<ComponentTypeAttribute, Long> {

    List<ComponentTypeAttribute> findByComponentTypeId(Long componentTypeId);
}