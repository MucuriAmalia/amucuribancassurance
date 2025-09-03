package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.SubAccountTypes;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SubAccountTypesRepo extends PagingAndSortingRepository<SubAccountTypes, Long>, QueryDslPredicateExecutor<SubAccountTypes> {
}
