package com.brokersystems.brokerapp.uw.repository;

import com.brokersystems.brokerapp.uw.model.AdminFee;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by peter on 6/6/2017.
 */
public interface AdminFeeRepo  extends PagingAndSortingRepository<AdminFee, Long>, QueryDslPredicateExecutor<AdminFee> {
}
