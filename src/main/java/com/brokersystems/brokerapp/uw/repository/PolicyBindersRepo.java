package com.brokersystems.brokerapp.uw.repository;

import com.brokersystems.brokerapp.uw.model.PolicyBinders;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PolicyBindersRepo extends PagingAndSortingRepository<PolicyBinders, Long>, QueryDslPredicateExecutor<PolicyBinders> {


}
