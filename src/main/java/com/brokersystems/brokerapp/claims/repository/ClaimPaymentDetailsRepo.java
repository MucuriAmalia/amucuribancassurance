package com.brokersystems.brokerapp.claims.repository;

import com.brokersystems.brokerapp.claims.model.ClaimPaymentDetails;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ClaimPaymentDetailsRepo extends PagingAndSortingRepository<ClaimPaymentDetails, Long>, QueryDslPredicateExecutor<ClaimPaymentDetails> {


}
