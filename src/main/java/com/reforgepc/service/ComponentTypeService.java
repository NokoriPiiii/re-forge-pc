package com.reforgepc.service;

import com.reforgepc.entity.ComponentType;
import com.reforgepc.repository.ComponentTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComponentTypeService {

    private final ComponentTypeRepository componentTypeRepository;

    public ComponentTypeService(ComponentTypeRepository componentTypeRepository) {
        this.componentTypeRepository = componentTypeRepository;
    }

    public List<ComponentType> getAll() {
        return componentTypeRepository.findAll();
    }

    public ComponentType getById(Long id) {
        return componentTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Component type not found: " + id));
    }

    public ComponentType save(ComponentType componentType) {
        return componentTypeRepository.save(componentType);
    }

    public void delete(Long id) {
        componentTypeRepository.deleteById(id);
    }
}