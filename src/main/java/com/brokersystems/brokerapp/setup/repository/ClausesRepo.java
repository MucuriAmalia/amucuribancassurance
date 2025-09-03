package com.brokersystems.brokerapp.setup.repository;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brokersystems.brokerapp.setup.model.ClausesDef;

public interface ClausesRepo extends  PagingAndSortingRepository<ClausesDef, Long>, QueryDslPredicateExecutor<ClausesDef> {

}
