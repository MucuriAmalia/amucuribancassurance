package com.brokersystems.brokerapp.setup.repository;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brokersystems.brokerapp.setup.model.SubclassSections;

public interface SubSectionRepo extends  PagingAndSortingRepository<SubclassSections, Long>, QueryDslPredicateExecutor<SubclassSections>{

}
