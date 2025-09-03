package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.CategoryMemberBenefits;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by waititu on 20/03/2018.
 */
public interface CategoryMemberBenefitsRepo extends PagingAndSortingRepository<CategoryMemberBenefits,Long>, QueryDslPredicateExecutor<CategoryMemberBenefits> {
}
