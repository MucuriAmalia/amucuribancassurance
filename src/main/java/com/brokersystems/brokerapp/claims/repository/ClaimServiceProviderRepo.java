package com.brokersystems.brokerapp.claims.repository;

import com.brokersystems.brokerapp.claims.model.ClaimServiceProvider;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ClaimServiceProviderRepo extends PagingAndSortingRepository<ClaimServiceProvider, Long>, QueryDslPredicateExecutor<ClaimServiceProvider> {


}
