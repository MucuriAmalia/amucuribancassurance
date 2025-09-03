package com.brokersystems.brokerapp.setup.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brokersystems.brokerapp.setup.model.PaymentModes;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface PaymentModeRepo extends  PagingAndSortingRepository<PaymentModes, Long>, QueryDslPredicateExecutor<PaymentModes>{

    @Query(value = "select pm_id,pm_desc,COUNT(*) OVER() as total_rows  from sys_brk_payment_modes \n" +
            "where (lower(pm_desc) like :search) \n" +
            "ORDER  by pm_desc \n" +
            "OFFSET :pageNo*:limit LIMIT :limit ",nativeQuery = true)
    List<Object[]> findPymentModes(@Param("search") String search,
                               @Param("pageNo") int pageNo,
                               @Param("limit") int limit);

    List<PaymentModes> findPaymentModesByPmShtDesc(String paymentMode);

}
