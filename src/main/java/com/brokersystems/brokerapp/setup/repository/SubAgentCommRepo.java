package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.SubAgentCommissionRates;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SubAgentCommRepo extends PagingAndSortingRepository<SubAgentCommissionRates, Long>, QueryDslPredicateExecutor<SubAgentCommissionRates> {
}
