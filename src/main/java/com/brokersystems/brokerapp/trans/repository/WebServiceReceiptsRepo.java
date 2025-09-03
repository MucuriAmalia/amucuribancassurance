package com.brokersystems.brokerapp.trans.repository;

import com.brokersystems.brokerapp.trans.model.WebServiceReceipts;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface WebServiceReceiptsRepo extends PagingAndSortingRepository<WebServiceReceipts, Long>, QueryDslPredicateExecutor<WebServiceReceipts> {

}
