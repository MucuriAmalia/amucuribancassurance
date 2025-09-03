package com.brokersystems.brokerapp.life.repository;

import com.brokersystems.brokerapp.life.model.LifeCommissionRates;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by waititu on 03/12/2017.
 */
public interface LifeCommissionRatesRepo extends PagingAndSortingRepository<LifeCommissionRates, Long>, QueryDslPredicateExecutor<LifeCommissionRates> {
}
