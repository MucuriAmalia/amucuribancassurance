package com.brokersystems.brokerapp.trans.repository;

import com.brokersystems.brokerapp.trans.model.ChequeTrans;
import com.brokersystems.brokerapp.trans.model.ChequeTransDtls;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ChequeTransRepo extends PagingAndSortingRepository<ChequeTrans, Long>, QueryDslPredicateExecutor<ChequeTrans> {

    @Query(value = "select ct_no,ct_invoice_no,ct_narrative,ct_payment_type,ct_raised_date,ct_trans_ref,ct_trans_ref_date,ct_trans_amount,\n" +
            "sbba.ba_acct_name,sbc.cur_iso_code,sbb.ob_name,sbu.user_username,sbpm.pm_desc,sbp.pay_full_name,sbps.payc_account_no,COUNT(*) OVER() AS total_rows  from sys_chq_trans\n" +
            "join sys_brk_bnk_accounts sbba on sbba.ba_id =ct_ba_acc\n" +
            "join sys_brk_currencies sbc on sbc.cur_code =ct_cur_code\n" +
            "join sys_brk_branches sbb on sbb.ob_id =ct_branch\n" +
            "join sys_brk_users sbu on sbu.user_id =ct_raised_by\n" +
            "join sys_brk_payment_modes sbpm on sbpm.pm_id =ct_paymode_id\n" +
            "join sys_brk_payees sbp on sbp.pay_id =ct_payee_id\n" +
            "join sys_brk_payee_accounts sbps on sbps.payc_id =ct_payee_act_id\n" +
            "where ct_chq_status=:status \n" +
            "and (LOWER(ct_invoice_no) like :search or LOWER(pay_full_name) like :search or lower(ba_acct_name) like :search)\n" +
            "order by ct_no desc OFFSET :pageNo*:limit LIMIT :limit ",nativeQuery = true)
    List<Object[]> findRequstionTransactions(@Param("search") String search,
                                             @Param("status") String status,
                                     @Param("pageNo") int pageNo,
                                     @Param("limit") int limit);



    @Query(value = "select count(1) from sys_chq_trans sct  where ct_payee_id=:payId and ct_invoice_no =:invNo",nativeQuery = true)
    int countInvoiceNoExists(@Param("payId") Long payId,
                             @Param("invNo") String invNo);

    @Query(value = "select ct_ba_acc from sys_chq_trans where ct_no=:reqId",nativeQuery = true)
    Long getBankAcctDetails(@Param("reqId") Long reqId);

    @Query(value = "select ct_branch,ct_cur_code from sys_chq_trans where ct_no=:reqId",nativeQuery = true)
    List<Object[]> getTransactionDetails(@Param("reqId") Long reqId);

    @Query(value = "select ct_paymode_id from sys_chq_trans where ct_no=:reqId",nativeQuery = true)
    Long getPayMethod(@Param("reqId") Long reqId);


}
