package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.SelfFundParams;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by peter on 5/23/2017.
 */
public interface SelfFundParamsRepo extends PagingAndSortingRepository<SelfFundParams, Long>, QueryDslPredicateExecutor<SelfFundParams> {

}
