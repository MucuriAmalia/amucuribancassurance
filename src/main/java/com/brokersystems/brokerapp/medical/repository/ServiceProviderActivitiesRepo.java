package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.ServiceProviderActivities;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by waititu on 02/11/2017.
 */
public interface ServiceProviderActivitiesRepo extends PagingAndSortingRepository<ServiceProviderActivities ,Long> ,QueryDslPredicateExecutor<ServiceProviderActivities> {
}
