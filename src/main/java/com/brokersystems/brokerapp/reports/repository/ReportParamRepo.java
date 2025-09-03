package com.brokersystems.brokerapp.reports.repository;

import com.brokersystems.brokerapp.reports.model.ReportParameters;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by peter on 2/12/2017.
 */
public interface ReportParamRepo extends PagingAndSortingRepository<ReportParameters, Long>, QueryDslPredicateExecutor<ReportParameters> {
}
