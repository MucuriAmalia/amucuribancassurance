package com.brokersystems.brokerapp.accounts.repository;

import com.brokersystems.brokerapp.accounts.model.PeriodBalances;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PeriodBalancesRepo extends PagingAndSortingRepository<PeriodBalances,Long>, QueryDslPredicateExecutor<PeriodBalances> {
}
