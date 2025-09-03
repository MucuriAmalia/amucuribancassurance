package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.BedTypes;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by peter on 4/21/2017.
 */
public interface BedTypesRepo  extends PagingAndSortingRepository<BedTypes, Long>, QueryDslPredicateExecutor<BedTypes> {
}
