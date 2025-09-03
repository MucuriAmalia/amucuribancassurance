package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.GlobalCommissionRates;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GlobalCommissionRatesRepository extends PagingAndSortingRepository<GlobalCommissionRates, Long>, QueryDslPredicateExecutor<GlobalCommissionRates> {
}

