package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.GlobalAdminFeeSetup;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GlobalAdminFeeSetupRepository extends PagingAndSortingRepository<GlobalAdminFeeSetup, Long>, QueryDslPredicateExecutor<GlobalAdminFeeSetup> {
}
