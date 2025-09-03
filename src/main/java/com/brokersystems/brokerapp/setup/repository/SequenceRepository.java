package com.brokersystems.brokerapp.setup.repository;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brokersystems.brokerapp.setup.model.SystemSequence;



public interface SequenceRepository extends  PagingAndSortingRepository<SystemSequence, Long>, QueryDslPredicateExecutor<SystemSequence> {

}
