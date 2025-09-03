package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.BusinessSources;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by waititu on 02/10/2017.
 */
public interface BusinessSourcesRepo extends PagingAndSortingRepository<BusinessSources,Long>,QueryDslPredicateExecutor<BusinessSources> {
}
