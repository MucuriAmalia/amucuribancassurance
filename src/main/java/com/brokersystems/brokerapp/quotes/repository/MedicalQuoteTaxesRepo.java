package com.brokersystems.brokerapp.quotes.repository;

import com.brokersystems.brokerapp.quotes.model.MedicalQuoteTaxes;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by waititu on 09/08/2017.
 */
public interface MedicalQuoteTaxesRepo extends PagingAndSortingRepository<MedicalQuoteTaxes , Long>, QueryDslPredicateExecutor<MedicalQuoteTaxes> {
}
