package com.brokersystems.brokerapp.trans.repository;

import com.brokersystems.brokerapp.trans.model.ReceiptPrint;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by HP on 3/24/2018.
 */
public interface ReceiptPrintRepo  extends PagingAndSortingRepository<ReceiptPrint, Long>, QueryDslPredicateExecutor<ReceiptPrint> {
}
