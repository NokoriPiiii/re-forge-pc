package com.reforgepc.service;

import com.reforgepc.entity.AttributeOption;
import com.reforgepc.repository.AttributeOptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttributeOptionService {

    private final AttributeOptionRepository attributeOptionRepository;

    public AttributeOptionService(AttributeOptionRepository attributeOptionRepository) {
        this.attributeOptionRepository = attributeOptionRepository;
    }

    public List<AttributeOption> getByAttributeId(Long attributeId) {
        return attributeOptionRepository.findByAttributeId(attributeId);
    }

    public AttributeOption save(AttributeOption option) {
        return attributeOptionRepository.save(option);
    }

    public void delete(Long id) {
        attributeOptionRepository.deleteById(id);
    }
}