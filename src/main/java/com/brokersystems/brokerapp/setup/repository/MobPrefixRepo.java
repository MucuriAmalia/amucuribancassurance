package com.brokersystems.brokerapp.setup.repository;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brokersystems.brokerapp.setup.model.MobilePrefixDef;

public interface MobPrefixRepo extends  PagingAndSortingRepository<MobilePrefixDef, Long>, QueryDslPredicateExecutor<MobilePrefixDef> {

}
