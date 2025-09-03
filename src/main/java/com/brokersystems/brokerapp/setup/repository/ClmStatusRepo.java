package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.ClmCausations;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by peter on 3/5/2017.
 */
public interface ClmStatusRepo extends PagingAndSortingRepository<ClmCausations, Long>, QueryDslPredicateExecutor<ClmCausations> {

}
