package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.RejectedReasons;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import java.util.List;

public interface RejectedReasonsRepo extends PagingAndSortingRepository<RejectedReasons, Long>, QueryDslPredicateExecutor<RejectedReasons> {
    List<RejectedReasons> findByActiveStatus(String status);
}