package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.RequestServiceLog;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by peter on 6/26/2017.
 */
public interface RequestServicesLogRepo extends PagingAndSortingRepository<RequestServiceLog, Long>, QueryDslPredicateExecutor<RequestServiceLog> {
}
