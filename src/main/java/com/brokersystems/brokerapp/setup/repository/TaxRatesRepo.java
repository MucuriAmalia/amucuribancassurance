package com.brokersystems.brokerapp.setup.repository;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brokersystems.brokerapp.setup.model.TaxRates;

public interface TaxRatesRepo extends  PagingAndSortingRepository<TaxRates, Long>, QueryDslPredicateExecutor<TaxRates> {

}
