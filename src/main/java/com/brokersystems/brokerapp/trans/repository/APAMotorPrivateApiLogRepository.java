package com.brokersystems.brokerapp.trans.repository;

import com.brokersystems.brokerapp.trans.model.APAMotorPrivateApiLog;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface APAMotorPrivateApiLogRepository extends PagingAndSortingRepository<APAMotorPrivateApiLog, Long>, QueryDslPredicateExecutor<APAMotorPrivateApiLog> {
}
