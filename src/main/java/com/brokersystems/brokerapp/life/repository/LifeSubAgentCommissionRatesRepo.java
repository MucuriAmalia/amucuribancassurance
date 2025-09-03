package com.brokersystems.brokerapp.life.repository;

import com.brokersystems.brokerapp.life.model.LifeSubAgentCommissionRates;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface LifeSubAgentCommissionRatesRepo extends PagingAndSortingRepository<LifeSubAgentCommissionRates, Long>, QueryDslPredicateExecutor<LifeSubAgentCommissionRates> {
}
