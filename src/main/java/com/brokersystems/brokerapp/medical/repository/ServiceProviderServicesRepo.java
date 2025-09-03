package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.ServiceProviderServices;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by waititu on 17/10/2017.
 */
public interface ServiceProviderServicesRepo extends PagingAndSortingRepository<ServiceProviderServices ,Long> ,QueryDslPredicateExecutor<ServiceProviderServices> {
}
