package com.brokersystems.brokerapp.certs.repository;

import com.brokersystems.brokerapp.certs.model.BranchCerts;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by usert1 on 1/28/2017.
 */
public interface BranchCertsRepo extends PagingAndSortingRepository<BranchCerts, Long>, QueryDslPredicateExecutor<BranchCerts> {
}
