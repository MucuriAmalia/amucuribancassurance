package com.brokersystems.brokerapp.setup.service.impl;

import com.brokersystems.brokerapp.setup.model.TaskEscalationConfig;
import com.brokersystems.brokerapp.setup.repository.TaskEscalationConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskEscalationConfigService {

    @Autowired
    private TaskEscalationConfigRepository configRepository;

    // Retrieve config for a specific task type
    public TaskEscalationConfig getConfigByTaskType(String taskType) {
        return configRepository.findByTaskType(taskType);
    }

    // Update time limits for a task type
    public TaskEscalationConfig updateTimeLimits(String taskType, Integer timePerUser, Integer timePerTransaction) {
        TaskEscalationConfig config = configRepository.findByTaskType(taskType);
        if (config == null) {
            config = new TaskEscalationConfig();
            config.setTaskType(taskType);
        }
        config.setTimeLimitPerUser(timePerUser);
        config.setTimeLimitPerTransaction(timePerTransaction);
        return configRepository.save(config);
    }
}

