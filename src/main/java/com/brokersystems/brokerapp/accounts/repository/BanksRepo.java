package com.brokersystems.brokerapp.accounts.repository;

import com.brokersystems.brokerapp.accounts.model.Banks;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by peter on 4/10/2017.
 */
public interface BanksRepo extends PagingAndSortingRepository<Banks, Long>, QueryDslPredicateExecutor<Banks> {





}
