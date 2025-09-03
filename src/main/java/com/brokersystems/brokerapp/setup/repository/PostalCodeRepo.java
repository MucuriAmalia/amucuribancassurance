package com.brokersystems.brokerapp.setup.repository;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brokersystems.brokerapp.setup.model.PostalCodesDef;

public interface PostalCodeRepo  extends  PagingAndSortingRepository<PostalCodesDef, Long>, QueryDslPredicateExecutor<PostalCodesDef> {

}
