package com.brokersystems.brokerapp.accounts.repository;

import com.brokersystems.brokerapp.accounts.model.AccountsTrialBalances;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AccountsTrialBalancesRepo extends PagingAndSortingRepository<AccountsTrialBalances, Long>, QueryDslPredicateExecutor<AccountsTrialBalances> {
}
