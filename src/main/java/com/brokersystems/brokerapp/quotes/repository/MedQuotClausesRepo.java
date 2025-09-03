package com.brokersystems.brokerapp.quotes.repository;

import com.brokersystems.brokerapp.quotes.model.MedQuoteClauses;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by waititu on 13/08/2017.
 */
public interface MedQuotClausesRepo extends PagingAndSortingRepository<MedQuoteClauses,Long>,QueryDslPredicateExecutor<MedQuoteClauses> {

}
