package com.brokersystems.brokerapp.claims.repository;

import com.brokersystems.brokerapp.claims.model.ClaimRevisionTrans;
import com.brokersystems.brokerapp.claims.model.ClaimRevisions;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by peter on 3/8/2017.
 */
public interface ClaimRevisionTransRepo extends PagingAndSortingRepository<ClaimRevisionTrans, Long>, QueryDslPredicateExecutor<ClaimRevisionTrans> {
}
