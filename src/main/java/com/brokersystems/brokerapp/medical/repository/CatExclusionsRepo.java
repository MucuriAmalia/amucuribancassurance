package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.CategoryExclusions;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by peter on 5/3/2017.
 */
public interface CatExclusionsRepo extends PagingAndSortingRepository<CategoryExclusions, Long>, QueryDslPredicateExecutor<CategoryExclusions> {
}
