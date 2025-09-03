package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.MedicalParRequest;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by peter on 5/16/2017.
 */
public interface ParRequestRepo extends PagingAndSortingRepository<MedicalParRequest, Long>, QueryDslPredicateExecutor<MedicalParRequest> {
}
