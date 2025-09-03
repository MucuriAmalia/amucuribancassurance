package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.MedicalCategory;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by peter on 4/26/2017.
 */
public interface MedicalCategoryRepo extends PagingAndSortingRepository<MedicalCategory, Long>, QueryDslPredicateExecutor<MedicalCategory> {
}
