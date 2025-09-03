package com.brokersystems.brokerapp.accounts.repository;

import com.brokersystems.brokerapp.accounts.model.ReportProcessedValues;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ReportProcessedValuesRepo extends PagingAndSortingRepository<ReportProcessedValues,Long>, QueryDslPredicateExecutor<ReportProcessedValues> {
}
