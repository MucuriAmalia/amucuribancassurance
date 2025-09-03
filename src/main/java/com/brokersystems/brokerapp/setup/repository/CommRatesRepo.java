package com.brokersystems.brokerapp.setup.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brokersystems.brokerapp.setup.model.CommissionRates;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommRatesRepo extends  PagingAndSortingRepository<CommissionRates, Long>, QueryDslPredicateExecutor<CommissionRates> {

    @Query("select c from CommissionRates c where c.bindersDef.binId = :binId")
    List<CommissionRates> findCommIds(@Param("binId") Long binId);
}
