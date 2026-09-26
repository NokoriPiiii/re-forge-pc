package com.reforgepc.service;

import com.reforgepc.entity.ComponentTypeAttribute;
import com.reforgepc.repository.ComponentTypeAttributeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComponentTypeAttributeService {

    private final ComponentTypeAttributeRepository repository;

    public ComponentTypeAttributeService(ComponentTypeAttributeRepository repository) {
        this.repository = repository;
    }

    public List<ComponentTypeAttribute> getByComponentTypeId(Long componentTypeId) {
        return repository.findByComponentTypeId(componentTypeId);
    }

    public ComponentTypeAttribute save(ComponentTypeAttribute componentTypeAttribute) {
        return repository.save(componentTypeAttribute);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}