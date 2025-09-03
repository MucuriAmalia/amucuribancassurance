package com.brokersystems.brokerapp.setup.repository;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brokersystems.brokerapp.setup.model.SecShortPeriodRates;

public interface SecShortPeriodsRepo extends  PagingAndSortingRepository<SecShortPeriodRates, Long>, QueryDslPredicateExecutor<SecShortPeriodRates> {

}
