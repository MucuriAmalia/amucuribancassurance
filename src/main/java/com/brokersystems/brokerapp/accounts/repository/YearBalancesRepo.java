package com.brokersystems.brokerapp.accounts.repository;

import com.brokersystems.brokerapp.accounts.model.YearBalances;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface YearBalancesRepo extends PagingAndSortingRepository<YearBalances,Long>, QueryDslPredicateExecutor<YearBalances> {
}
