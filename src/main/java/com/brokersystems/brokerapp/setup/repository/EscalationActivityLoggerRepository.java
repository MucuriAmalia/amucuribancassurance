package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.EscalationActivityLogger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EscalationActivityLoggerRepository extends JpaRepository<EscalationActivityLogger, Long> {

    @Query("SELECT eal FROM EscalationActivityLogger eal WHERE eal.task.id = :taskId")
    List<EscalationActivityLogger> findByTaskId(@Param("taskId") Long taskId);

    @Modifying
    @Query("DELETE FROM EscalationActivityLogger eal WHERE eal.task.id = :taskId")
    void deleteByTaskId(@Param("taskId") Long taskId);
}