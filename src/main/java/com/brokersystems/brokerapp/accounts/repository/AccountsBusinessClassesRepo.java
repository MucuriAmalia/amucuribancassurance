package com.brokersystems.brokerapp.accounts.repository;

import com.brokersystems.brokerapp.accounts.model.AccountsBusinessClasses;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AccountsBusinessClassesRepo extends PagingAndSortingRepository<AccountsBusinessClasses, Long>, QueryDslPredicateExecutor<AccountsBusinessClasses> {
}
