package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.ProductChecks;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by HP on 11/5/2017.
 */
public interface ProductChecksRepo extends PagingAndSortingRepository<ProductChecks, Long>, QueryDslPredicateExecutor<ProductChecks> {



}
