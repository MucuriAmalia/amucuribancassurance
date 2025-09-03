package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.CoverLimits;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by peter on 5/9/2017.
 */
public interface CoverLimitsRepo extends PagingAndSortingRepository<CoverLimits, Long>, QueryDslPredicateExecutor<CoverLimits> {
}
