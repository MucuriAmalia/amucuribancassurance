package com.brokersystems.brokerapp.trans.repository;

import com.brokersystems.brokerapp.trans.model.TransactionMapping;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by peter on 4/24/2017.
 */
public interface TransMappingRepo extends PagingAndSortingRepository<TransactionMapping, Long>, QueryDslPredicateExecutor<TransactionMapping> {


}
