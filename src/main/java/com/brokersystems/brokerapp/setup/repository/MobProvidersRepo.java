package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.MobProviders;
import com.brokersystems.brokerapp.setup.model.MobilePrefixDef;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by HP on 9/11/2017.
 */
public interface MobProvidersRepo extends PagingAndSortingRepository<MobProviders, Long>, QueryDslPredicateExecutor<MobProviders> {
}
