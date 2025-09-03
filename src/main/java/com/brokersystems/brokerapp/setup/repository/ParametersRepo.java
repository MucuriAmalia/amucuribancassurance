package com.brokersystems.brokerapp.setup.repository;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brokersystems.brokerapp.setup.model.ParametersDef;

public interface ParametersRepo extends  PagingAndSortingRepository<ParametersDef, Long>, QueryDslPredicateExecutor<ParametersDef> {

}
