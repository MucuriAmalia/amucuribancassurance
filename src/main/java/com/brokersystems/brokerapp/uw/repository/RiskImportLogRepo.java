package com.brokersystems.brokerapp.uw.repository;

import com.brokersystems.brokerapp.uw.model.RiskImportationLog;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by HP on 10/15/2017.
 */
public interface RiskImportLogRepo extends PagingAndSortingRepository<RiskImportationLog, Long>, QueryDslPredicateExecutor<RiskImportationLog> {
}
