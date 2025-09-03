package com.brokersystems.brokerapp.dms.repo;

import com.brokersystems.brokerapp.dms.model.SybrinCases;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.QueryDslJpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface SybrinCasesRepo extends JpaRepository<SybrinCases, Long>, QueryDslPredicateExecutor<SybrinCases> {
}
