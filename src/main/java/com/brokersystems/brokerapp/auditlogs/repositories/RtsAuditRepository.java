package com.brokersystems.brokerapp.auditlogs.repositories;

import com.brokersystems.brokerapp.auditlogs.model.RtsAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RtsAuditRepository extends JpaRepository<RtsAudit, Long> {

    List<RtsAudit> findByPolicyId(Long policyId);
}