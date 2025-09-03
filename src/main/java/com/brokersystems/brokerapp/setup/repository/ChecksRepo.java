package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.Checks;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by HP on 10/4/2017.
 */
public interface ChecksRepo extends PagingAndSortingRepository<Checks, Long>, QueryDslPredicateExecutor<Checks> {
}
