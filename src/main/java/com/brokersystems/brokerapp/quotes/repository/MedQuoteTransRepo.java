package com.brokersystems.brokerapp.quotes.repository;

import com.brokersystems.brokerapp.quotes.model.QuoteTrans;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by waititu on 06/08/2017.
 */
public interface MedQuoteTransRepo extends PagingAndSortingRepository<QuoteTrans, Long> , QueryDslPredicateExecutor<QuoteTrans> {
}
