package com.brokersystems.brokerapp.certs.repository;

import com.brokersystems.brokerapp.certs.model.CertPrint;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by peter on 2/18/2017.
 */
public interface CertPrintRepo extends PagingAndSortingRepository<CertPrint, Long>, QueryDslPredicateExecutor<CertPrint> {


}
