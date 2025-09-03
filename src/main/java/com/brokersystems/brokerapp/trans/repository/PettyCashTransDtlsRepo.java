package com.brokersystems.brokerapp.trans.repository;

import com.brokersystems.brokerapp.trans.model.ChequeTransDtls;
import com.brokersystems.brokerapp.trans.model.PettyCashDtls;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PettyCashTransDtlsRepo extends PagingAndSortingRepository<PettyCashDtls, Long>, QueryDslPredicateExecutor<PettyCashDtls> {


}
