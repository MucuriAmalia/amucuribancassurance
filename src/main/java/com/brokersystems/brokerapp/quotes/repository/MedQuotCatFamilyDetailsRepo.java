package com.brokersystems.brokerapp.quotes.repository;

import com.brokersystems.brokerapp.quotes.model.MedQuotCatFamilyDetails;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by waititu on 16/09/2017.
 */
public interface MedQuotCatFamilyDetailsRepo extends PagingAndSortingRepository<MedQuotCatFamilyDetails , Long> ,QueryDslPredicateExecutor<MedQuotCatFamilyDetails> {
}
