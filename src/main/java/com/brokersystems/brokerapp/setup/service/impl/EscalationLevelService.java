package com.brokersystems.brokerapp.setup.service.impl;

import com.brokersystems.brokerapp.setup.model.EscalationLevel;
import com.brokersystems.brokerapp.setup.repository.EscalationLevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EscalationLevelService {

    @Autowired
    private EscalationLevelRepository levelRepository;

    // Retrieve all levels in order of hierarchy
    public List<EscalationLevel> getAllLevels() {
        return levelRepository.findAll();
    }

    // Retrieve a specific level by hierarchy
    public EscalationLevel getLevelByHierarchy(Integer hierarchyLevel) {
        return levelRepository.findByHierarchyLevel(hierarchyLevel);
    }
}

