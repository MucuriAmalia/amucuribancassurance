package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.TaskEscalationConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskEscalationConfigRepository extends JpaRepository<TaskEscalationConfig, Long> {

    // Find configuration by task type
    TaskEscalationConfig findByTaskType(String taskType);
}
