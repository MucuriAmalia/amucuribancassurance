package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.EscalationRecord;
import com.brokersystems.brokerapp.users.model.MakerChecker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EscalationRecordRepository extends JpaRepository<EscalationRecord, Long> {

    // Find escalation records by task
    List<EscalationRecord> findByTask(MakerChecker task);

    // Find all records where checkTime is null and madeTime exceeds the user-defined limit
    List<EscalationRecord> findByCheckTimeIsNullAndMadeTimeBefore(Date cutoffTime);

    List<EscalationRecord> findByCheckTimeIsNull();

    List<EscalationRecord> findByCheckTimeIsNullAndTaskInitiationTimeBefore(Date cutOffInitiationTime);

    @Query(value =
            "select er.* " +
                    "from sys_brk_escalation_record er " +
                    "join sys_brk_user_branches ub ON ub.bub_user_id = er.owner_id " +
                    "where ub.bub_ob_id = :branchId " +
                    "and er.task_initiation_time between :startDate and :endDate " +
                    "ORDER BY er.task_initiation_time DESC",
            nativeQuery = true)
    List<EscalationRecord> findEscalationRecordsByBranchAndDateRange(
            @Param("branchId") Long branchId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

    //  delete escalation records by MakerChecker ID
    @Modifying
    @Query("DELETE FROM EscalationRecord er WHERE er.task.id = :taskId")
    void deleteByTaskId(@Param("taskId") Long taskId);
}