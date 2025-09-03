package com.brokersystems.brokerapp.auditlogs.repositories;

import com.brokersystems.brokerapp.auditlogs.model.AuditLog;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AuditLogRepository extends PagingAndSortingRepository<AuditLog, Long>, QueryDslPredicateExecutor<AuditLog> {
}
