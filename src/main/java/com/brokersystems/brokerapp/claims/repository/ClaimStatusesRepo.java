package com.brokersystems.brokerapp.claims.repository;

import com.brokersystems.brokerapp.claims.model.ClaimStatuses;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by waititu on 10/09/2019.
 */
public interface ClaimStatusesRepo extends PagingAndSortingRepository<ClaimStatuses, Long> ,QueryDslPredicateExecutor<ClaimStatuses> {
}
