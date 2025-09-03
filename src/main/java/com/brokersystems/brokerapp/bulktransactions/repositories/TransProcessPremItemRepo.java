package com.brokersystems.brokerapp.bulktransactions.repositories;

import com.brokersystems.brokerapp.bulktransactions.models.TransProcessPremItems;
import com.brokersystems.brokerapp.bulktransactions.models.TransactionProcessing;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface TransProcessPremItemRepo extends PagingAndSortingRepository<TransProcessPremItems, Long>, QueryDslPredicateExecutor<TransProcessPremItems> {
    List<TransProcessPremItems> findAllByTransProcessing(TransactionProcessing trans);

}
