package com.brokersystems.brokerapp.claims.repository;

import com.brokersystems.brokerapp.claims.model.ClaimPerilPayments;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by waititu on 09/09/2019.
 */
public interface ClaimPerilPaymentsRepo extends PagingAndSortingRepository<ClaimPerilPayments, Long>,QueryDslPredicateExecutor<ClaimPerilPayments> {
}
