package com.brokersystems.brokerapp.claims.repository;

import com.brokersystems.brokerapp.claims.model.ClaimPayments;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ClaimPaymentsRepo extends PagingAndSortingRepository<ClaimPayments, Long>, QueryDslPredicateExecutor<ClaimPayments> {

    @Query(value = "select count(1) from sys_brk_clm_pymnts join sys_brk_serv_providers sbcsp   on sbcsp.provd_id  =clm_pymt_spd_id\n" +
            "where clm_pymt_invoice_no=:invNumber\n" +
            "and clm_pymt_spd_id = :providerId",nativeQuery = true)
    int countInvoiceNoExists(@Param("providerId") Long providerId,
                             @Param("invNumber") String invNumber);


    @Query(value = "select clm_pymnt_id,sbp.pay_full_name,clm_pymt_invoice_no,sbpm.pm_desc,clm_pymt_trans_type,sbc.cur_name,clm_pymt_amount,coalesce(clm_pymt_auth,'N') clm_pymt_auth,\n" +
            "sbu.user_username created_by,clm_pymt_created_dt,clm_pymt_auth_date,sbu2.user_username auth_by,COUNT(*) OVER()  AS total_rows\n" +
            "from sys_brk_clm_pymnts sbcp \n" +
            "join sys_brk_payees sbp on sbp.pay_id =sbcp.clm_pymt_payee \n" +
            "join sys_brk_payment_modes sbpm on sbpm.pm_id  = clm_pymt_pm_id\n" +
            "join sys_brk_currencies sbc on sbc.cur_code =clm_pymt_cur_id\n" +
            "join sys_brk_users sbu on sbu.user_id =clm_pymt_created_by\n" +
            "left join sys_brk_users sbu2 on sbu2.user_id  = clm_pymt_auth_by\n" +
            "where  coalesce(clm_pymt_clmnt_id  ,-2000) = case when clm_pymt_trans_type = 'SP' then :sprId  else coalesce(clm_pymt_clmnt_id,-2000)  end\n" +
            "and clm_pymt_clm_id = :clmId\n" +
            "and (lower(pay_full_name) like :search)\n" +
            "order by clm_pymt_created_dt desc OFFSET :pageNo*:limit LIMIT :limit ", nativeQuery = true)
    List<Object[]> getClmPayments(@Param("search") String search,
                                  @Param("sprId") Long sprId,
                                  @Param("clmId") Long clmId,
                                  @Param("pageNo") int pageNo,
                                  @Param("limit") int limit);

    @Query(value = "select clm_pymt_payee,clm_pymt_cur_id,clm_pymt_pm_id,sbp.pol_branch_id,clm_pymt_ba_acc,risk_subclass_id,clm_no from sys_brk_clm_pymnts \n" +
            "join sys_brk_clm_bookings sbcb \n" +
            "on clm_id  = clm_pymt_clm_id\n" +
            "join sys_brk_risks sbr ON sbr.risk_id =clm_risk_id\n" +
            "join sys_brk_policies sbp on sbp.pol_id =sbr.risk_pol_id \n" +
            "where clm_pymnt_id =:pymtId  ",nativeQuery = true)
    List<Object[]> getPymentDetails(@Param("pymtId") Long pymtId);

    @Query(value = "select cpd_amount  from sys_brk_clm_pymnts_dtls where clm_pymt_spd_id =:pymtId ",nativeQuery = true)
    List<BigDecimal> findPymentDetailsAmounts(@Param("pymtId") Long pymtId);

    @Query(value = "select sum(case when gl_dc='C' then gl_amount else -1*gl_amount end) from sys_brk_gl_trans \n" +
            "where gl_account =:acctId",nativeQuery = true)
    BigDecimal getAccountBalance(@Param("acctId") Long acctId);

}
