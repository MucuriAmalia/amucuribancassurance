package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.MedicalCards;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by waititu on 09/07/2017.
 */
public interface MedicalCardsCrudRepo extends PagingAndSortingRepository<MedicalCards, Long>, QueryDslPredicateExecutor<MedicalCards>{
}
