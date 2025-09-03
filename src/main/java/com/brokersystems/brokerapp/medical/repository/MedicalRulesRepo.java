package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.MedicalBinderRules;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by peter on 4/25/2017.
 */
public interface MedicalRulesRepo extends PagingAndSortingRepository<MedicalBinderRules, Long>, QueryDslPredicateExecutor<MedicalBinderRules> {


}
