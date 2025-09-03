package com.brokersystems.brokerapp.bulktransactions.repositories;

import com.brokersystems.brokerapp.bulktransactions.models.TransProcessRisks;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TransProcessRisksRepo extends PagingAndSortingRepository<TransProcessRisks, Long>, QueryDslPredicateExecutor<TransProcessRisks> {
}
