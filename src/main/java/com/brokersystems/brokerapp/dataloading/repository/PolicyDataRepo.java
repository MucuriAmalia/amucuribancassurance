package com.brokersystems.brokerapp.dataloading.repository;

import com.brokersystems.brokerapp.dataloading.model.PolicyData;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PolicyDataRepo extends PagingAndSortingRepository<PolicyData,Long>, QueryDslPredicateExecutor<PolicyData> {
}
