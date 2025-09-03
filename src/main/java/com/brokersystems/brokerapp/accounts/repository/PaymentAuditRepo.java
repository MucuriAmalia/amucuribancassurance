package com.brokersystems.brokerapp.accounts.repository;

import com.brokersystems.brokerapp.accounts.model.PaymentAudit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by peter on 3/27/2017.
 */
public interface PaymentAuditRepo extends PagingAndSortingRepository<PaymentAudit, Long>, QueryDslPredicateExecutor<PaymentAudit> {

    @Query(value = "select distinct pa_settle_id from sys_brk_payment_audit",nativeQuery = true)
    List<BigInteger> getAudits();

    @Query(value = "select pa_comm_trans_id from sys_brk_payment_audit where pa_id=:transId",nativeQuery = true)
    List<BigInteger> findCommissionTrans(@Param("transId") Long transId);

    @Query(value = "select pa_trans_type,pa_settle_rec_ref_no,pa_settle_dr_ref_no,pa_other_trans,pa_trans_no, pa_amount,pa_comm_trans_id,pa_whtx,pa_Id from sys_brk_payment_audit\n" +
            "where pa_other_trans=:transNo", nativeQuery = true)
    List<Object[]> getOtherTransAudits(@Param("transNo") Long transNo);

    @Query(value = "WITH pa_trans_numbers AS (\n" +
            "    SELECT DISTINCT pa_trans_no\n" +
            "    FROM sys_brk_payment_audit\n" +
            "    WHERE pa_other_trans = :pa_other_trans\n" +
            ")\n" +
            "SELECT sbt.trans_ref_no as refNo, sbp.pol_no as clientPolNo, sbc.client_fname as fname,\n" +
            "sbc.client_onames as otherNames,sbt.trans_control_acc as controlAcc, sbpr.pr_desc as proDesc, \n" +
            "sbp.pol_basic_premium_amt as paymentAmount, pa.pa_amount as commAmount, pa.pa_whtx as whtxAmount\n" +
            "FROM sys_brk_main_transactions sbt\n" +
            "JOIN pa_trans_numbers ptn ON sbt.trans_no = ptn.pa_trans_no\n" +
            "JOIN sys_brk_policies sbp on sbp.pol_id = sbt.trans_pol_id\n" +
            "join sys_brk_clients sbc on sbc.client_id = sbp.pol_client_id \n" +
            "join sys_brk_products sbpr on sbpr.pr_code =sbp.pol_prod_id\n" +
            "JOIN sys_brk_payment_audit pa ON pa.pa_trans_no = sbt.trans_no", nativeQuery = true)
    List<Object[]> getCreditorCommtAudits(@Param("pa_other_trans") Long transNo);
    @Query(value = "SELECT pa_id,pa_trans_no, pa_settle_cr_id, pa_settle_rec_ref_no, pa_settle_dr_ref_no," +
            "pa_other_trans,pa_comm,pa_whtx,pa_amount,pa_posted, pa_posted_by, pa_post_dt, pa_cancelled," +
            "pa_cancelled_by,pa_canc_dt,pa_trans_type,pa_comm_trans_id " +
            "FROM sys_brk_payment_audit " +
            "WHERE pa_other_trans = :pa_other_trans", nativeQuery = true)
    List<Object[]> getTransApprove(@Param("pa_other_trans") Long transId);

    @Query(value = "WITH pa_trans_numbers AS (\n" +
            "    SELECT DISTINCT pa_trans_no\n" +
            "    FROM sys_brk_payment_audit\n" +
            "    WHERE pa_other_trans = :pa_other_trans\n" +
            ")\n" +
            "SELECT sbt.trans_ref_no as refNo, sbp.pol_no as clientPolNo, sbc.client_fname as fname,\n" +
            "sbc.client_onames as otherNames,sbt.trans_control_acc as controlAcc, sbpr.pr_desc as proDesc, \n" +
            "sbp.pol_basic_premium_amt as paymentAmount, pa.pa_amount as commAmount, sbt.trans_whtx as whtxAmount\n" +
            "FROM sys_brk_main_transactions sbt\n" +
            "JOIN pa_trans_numbers ptn ON sbt.trans_no = ptn.pa_trans_no\n" +
            "JOIN sys_brk_policies sbp on sbp.pol_id = sbt.trans_pol_id\n" +
            "join sys_brk_clients sbc on sbc.client_id = sbp.pol_client_id \n" +
            "join sys_brk_products sbpr on sbpr.pr_code =sbp.pol_prod_id\n" +
            "JOIN sys_brk_payment_audit pa ON pa.pa_trans_no = sbt.trans_no", nativeQuery = true)
    List<Object[]> geSubAgentCommtAudits(@Param("pa_other_trans") Long transNo);

    @Query(value = "select pa_amount from sys_brk_payment_audit where pa_settle_rec_ref_no=:receiptRef and pa_settle_dr_ref_no=:debitRef",nativeQuery = true)
    List<BigDecimal> getSubAgentAmts(@Param("receiptRef") String receiptRef,@Param("debitRef") String debitRef);


}
