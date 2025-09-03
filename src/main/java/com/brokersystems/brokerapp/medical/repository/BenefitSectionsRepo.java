package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.MedicalNetworks;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by peter on 4/21/2017.
 */
public interface BenefitSectionsRepo extends PagingAndSortingRepository<MedicalNetworks, Long>, QueryDslPredicateExecutor<MedicalNetworks> {
}
