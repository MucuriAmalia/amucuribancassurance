package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.MedicalParTrans;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by peter on 5/12/2017.
 */
public interface MedicalParRepo extends PagingAndSortingRepository<MedicalParTrans, Long>, QueryDslPredicateExecutor<MedicalParTrans> {
}
