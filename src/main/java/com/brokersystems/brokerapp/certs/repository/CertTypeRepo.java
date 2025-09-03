package com.brokersystems.brokerapp.certs.repository;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brokersystems.brokerapp.certs.model.CertTypes;

public interface CertTypeRepo extends  PagingAndSortingRepository<CertTypes, Long>, QueryDslPredicateExecutor<CertTypes>  {

}
