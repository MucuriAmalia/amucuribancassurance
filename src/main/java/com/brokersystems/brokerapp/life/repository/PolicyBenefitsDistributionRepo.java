package com.brokersystems.brokerapp.life.repository;

import com.brokersystems.brokerapp.life.model.PolicyBenefitsDistribution;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by waititu on 04/12/2017.
 */
public interface PolicyBenefitsDistributionRepo extends PagingAndSortingRepository<PolicyBenefitsDistribution, Long> , QueryDslPredicateExecutor<PolicyBenefitsDistribution> {
}
