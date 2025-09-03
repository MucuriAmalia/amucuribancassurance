package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.MedServiceProviders;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by peter on 4/26/2017.
 */
public interface ServiceProvidersRepo extends PagingAndSortingRepository<MedServiceProviders, Long>, QueryDslPredicateExecutor<MedServiceProviders> {
}
