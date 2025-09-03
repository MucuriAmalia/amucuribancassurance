package com.brokersystems.brokerapp.reports.repository;

import com.brokersystems.brokerapp.reports.model.ReportHeaders;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by peter on 2/12/2017.
 */
public interface ReportHeaderRepo extends PagingAndSortingRepository<ReportHeaders, Long>, QueryDslPredicateExecutor<ReportHeaders> {
}
