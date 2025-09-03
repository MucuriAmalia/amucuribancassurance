package com.brokersystems.brokerapp.trans.repository;

import com.brokersystems.brokerapp.trans.model.IntegrationDtls;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by HP on 5/27/2018.
 */
public interface IntegrationDtlsRepo extends PagingAndSortingRepository<IntegrationDtls, Long>, QueryDslPredicateExecutor<IntegrationDtls> {
}
