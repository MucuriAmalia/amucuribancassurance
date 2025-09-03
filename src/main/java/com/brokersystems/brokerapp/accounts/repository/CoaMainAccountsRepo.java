package com.brokersystems.brokerapp.accounts.repository;

import com.brokersystems.brokerapp.accounts.model.CoaMainAccounts;
import com.brokersystems.brokerapp.accounts.model.PaymentAudit;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by peter on 4/7/2017.
 */
public interface CoaMainAccountsRepo extends PagingAndSortingRepository<CoaMainAccounts, Long>, QueryDslPredicateExecutor<CoaMainAccounts> {



}
