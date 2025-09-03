package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.CardTypes;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by waititu on 13/04/2018.
 */
public interface CardTypesRepo extends PagingAndSortingRepository<CardTypes, Long> , QueryDslPredicateExecutor<CardTypes> {
}
