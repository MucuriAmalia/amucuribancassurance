package com.brokersystems.brokerapp.setup.repository;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brokersystems.brokerapp.setup.model.PerilsDef;

public interface PerilsRepo extends  PagingAndSortingRepository<PerilsDef, Long>, QueryDslPredicateExecutor<PerilsDef>{

}
