package com.brokersystems.brokerapp.audit.repository;

import com.brokersystems.brokerapp.audit.DeletedTransAudit;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DeletedTransAuditRepo extends PagingAndSortingRepository<DeletedTransAudit, Long>, QueryDslPredicateExecutor<DeletedTransAudit> {
}
