package com.brokersystems.brokerapp.quotes.repository;

import com.brokersystems.brokerapp.quotes.model.MedicalQuoteCategory;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by waititu on 09/08/2017.
 */
public interface MedicalQuoteCategoryRepo extends PagingAndSortingRepository<MedicalQuoteCategory ,Long>, QueryDslPredicateExecutor<MedicalQuoteCategory> {
}
