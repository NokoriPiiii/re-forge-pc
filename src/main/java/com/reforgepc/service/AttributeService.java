package com.reforgepc.service;

import com.reforgepc.entity.Attribute;
import com.reforgepc.repository.AttributeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttributeService {

    private final AttributeRepository attributeRepository;

    public AttributeService(AttributeRepository attributeRepository) {
        this.attributeRepository = attributeRepository;
    }

    public List<Attribute> getAll() {
        return attributeRepository.findAll();
    }

    public Attribute getById(Long id) {
        return attributeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Attribute not found: " + id));
    }

    public Attribute save(Attribute attribute) {
        return attributeRepository.save(attribute);
    }

    public void delete(Long id) {
        attributeRepository.deleteById(id);
    }
}