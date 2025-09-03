package com.brokersystems.brokerapp.setup.repository;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brokersystems.brokerapp.setup.model.ClientTitle;

public interface ClientTitleRepo extends  PagingAndSortingRepository<ClientTitle, Long>, QueryDslPredicateExecutor<ClientTitle> {

}
