package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.SubLimits;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by peter on 5/9/2017.
 */
public interface SubLimitsRepo extends PagingAndSortingRepository<SubLimits, Long>, QueryDslPredicateExecutor<SubLimits> {
}
