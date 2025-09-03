package com.brokersystems.brokerapp.trans.repository;

import com.brokersystems.brokerapp.trans.model.CommissionTrans;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CommissionTransRepo extends PagingAndSortingRepository<CommissionTrans, Long>, QueryDslPredicateExecutor<CommissionTrans> {




}
