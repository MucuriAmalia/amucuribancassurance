package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.ProductCodes;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductCodesRepo extends PagingAndSortingRepository<ProductCodes, Long>, QueryDslPredicateExecutor<ProductCodes> {
}
