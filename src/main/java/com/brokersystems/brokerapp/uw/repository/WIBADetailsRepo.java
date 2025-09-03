package com.brokersystems.brokerapp.uw.repository;

import com.brokersystems.brokerapp.uw.model.WIBADetails;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface WIBADetailsRepo  extends PagingAndSortingRepository<WIBADetails, Long>, QueryDslPredicateExecutor<WIBADetails> {
}
