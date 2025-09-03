package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.FamilySizes;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by peter on 4/24/2017.
 */
public interface FamilySizeRepo extends PagingAndSortingRepository<FamilySizes, Long>, QueryDslPredicateExecutor<FamilySizes> {
}
