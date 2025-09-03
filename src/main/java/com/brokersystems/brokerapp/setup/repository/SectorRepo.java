package com.brokersystems.brokerapp.setup.repository;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brokersystems.brokerapp.setup.model.SectorDef;

public interface SectorRepo extends  PagingAndSortingRepository<SectorDef, Long>, QueryDslPredicateExecutor<SectorDef> {

}
