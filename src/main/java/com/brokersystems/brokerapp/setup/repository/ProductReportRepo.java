package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.ProductReportGroup;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductReportRepo extends PagingAndSortingRepository<ProductReportGroup,Long>, QueryDslPredicateExecutor<ProductReportGroup> {
}
