package com.brokersystems.brokerapp.life.repository;

import com.brokersystems.brokerapp.life.model.PolicyAcruals;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PolicyAccrualPayRepo extends PagingAndSortingRepository<PolicyAcruals, Long>, QueryDslPredicateExecutor<PolicyAcruals> {

    @Query(value = "select COALESCE(sum(sbrd.rect_amount),0) from sys_brk_receipt_dtls sbrd \n" +
            "join sys_brk_policies sbp on sbp.pol_id = sbrd.rect_pol_id \n" +
            "join sys_brk_receipts sbr on sbr.receipt_id = sbrd.rect_receipt_no \n" +
            "where sbp.pol_id = :polId and sbp.pol_interface_type = 'A' and sbp.pol_trans_type  != 'BU' \n" ,nativeQuery = true)
    BigDecimal findPolRcts(@Param("polId") Long polId);

    @Query(value = "select * from sys_brk_life_pi_accruals where lrct_policy_id = :polId", nativeQuery = true)
    PolicyAcruals findBypolId(@Param("polId") Long polId);
}
