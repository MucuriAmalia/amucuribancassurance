package com.brokersystems.brokerapp.certs.repository;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brokersystems.brokerapp.certs.model.CertLots;

public interface CertLotsRepo extends  PagingAndSortingRepository<CertLots, Long>, QueryDslPredicateExecutor<CertLots> {

}
