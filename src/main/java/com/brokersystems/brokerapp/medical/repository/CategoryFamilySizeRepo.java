package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.CategoryFamilySize;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by peter on 4/27/2017.
 */
public interface CategoryFamilySizeRepo extends PagingAndSortingRepository<CategoryFamilySize, Long>, QueryDslPredicateExecutor<CategoryFamilySize> {
}
