package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.CategoryClauses;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by peter on 5/1/2017.
 */
public interface CategoryClausesRepo extends PagingAndSortingRepository<CategoryClauses, Long>, QueryDslPredicateExecutor<CategoryClauses> {
}
