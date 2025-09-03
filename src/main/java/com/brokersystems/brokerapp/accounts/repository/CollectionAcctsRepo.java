package com.brokersystems.brokerapp.accounts.repository;

import com.brokersystems.brokerapp.accounts.model.CollectionAccounts;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by HP on 8/21/2017.
 */
public interface CollectionAcctsRepo extends PagingAndSortingRepository<CollectionAccounts, Long>, QueryDslPredicateExecutor<CollectionAccounts> {
}
