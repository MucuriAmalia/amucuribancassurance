package com.brokersystems.brokerapp.trans.repository;

import com.brokersystems.brokerapp.trans.model.TransChecks;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by HP on 10/5/2017.
 */
public interface TransChecksRepo extends PagingAndSortingRepository<TransChecks, Long>, QueryDslPredicateExecutor<TransChecks> {
}
