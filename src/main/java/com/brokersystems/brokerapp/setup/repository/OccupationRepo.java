package com.brokersystems.brokerapp.setup.repository;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brokersystems.brokerapp.setup.model.Occupation;

public interface OccupationRepo extends  PagingAndSortingRepository<Occupation, Long>, QueryDslPredicateExecutor<Occupation> {

}
