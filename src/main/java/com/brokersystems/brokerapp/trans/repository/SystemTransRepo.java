package com.brokersystems.brokerapp.trans.repository;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brokersystems.brokerapp.trans.model.SystemTrans;

public interface SystemTransRepo extends  PagingAndSortingRepository<SystemTrans, Long>, QueryDslPredicateExecutor<SystemTrans> {

}
