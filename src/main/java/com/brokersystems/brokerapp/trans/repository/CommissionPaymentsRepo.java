package com.brokersystems.brokerapp.trans.repository;

import com.brokersystems.brokerapp.trans.model.CommissionPayments;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public interface CommissionPaymentsRepo extends PagingAndSortingRepository<CommissionPayments, Long>, QueryDslPredicateExecutor<CommissionPayments> {



    @Query(value = "SELECT ct_id from sys_brk_commission_trans sbct  join sys_brk_main_transactions sbmt on sbct.ct_debit_trans  = sbmt.trans_no " +
            " join sys_brk_policies sbp on sbp.pol_id  = sbmt.trans_pol_id  " +
            " where sbp.pol_no=:policyNo", nativeQuery = true)
    List<BigInteger> findauthCommissions(@Param("policyNo") String policyNo);

    @Query(value = "select ct_credit_trans,ct_debit_trans from sys_brk_commission_trans where ct_id=:transNo",nativeQuery = true)
    List<Object[]> getCommissionDebits(@Param("transNo") Long transNo);


    @Query(value = "SELECT ct_net_amt,ct_whtx, " +
            "            ct_amount,ct_id   FROM sys_brk_commission_trans " +
            "        join sys_brk_main_transactions sbmt on ct_credit_trans = sbmt.trans_no  " +
            "        join sys_brk_main_transactions debit on ct_debit_trans  = debit.trans_no " +
            "    where sbmt.trans_ref_no = :crref and debit.trans_ref_no = :drref" +
            "    and ct_trans_type =:transType", nativeQuery = true)
    List<Object[]> getCreditCommissionByReference(@Param("crref") String crref,@Param("drref") String drref,  @Param("transType") String transType);


    @Query(value = "  SELECT distinct ct_cur_code  FROM sys_brk_commission_trans " +
            "        join sys_brk_main_transactions sbmt on ct_debit_trans = sbmt.trans_no " +
            "        where sbmt.trans_ref_no = :ref " +
            "        and ct_trans_type = :transType",nativeQuery = true)
    List<BigInteger> getTransCurrency(@Param("ref") String ref, @Param("transType") String transType);

    @Query(value = "  SELECT distinct ct_cur_code  FROM sys_brk_commission_trans " +
            "        join sys_brk_main_transactions sbmt on ct_credit_trans = sbmt.trans_no " +
            "        where sbmt.trans_ref_no = :ref " +
            "        and ct_trans_type = :transType",nativeQuery = true)
    List<BigInteger> getDebitTransCurrency(@Param("ref") String ref, @Param("transType") String transType);


    @Query(value = " SELECT  ct_debit_trans  from sys_brk_commission_trans where ct_id  = :commId",nativeQuery = true)
    BigInteger getDebitTransNo(@Param("commId") Long commId);

    @Query(value = " SELECT  ct_credit_trans  from sys_brk_commission_trans where ct_id  = :commId",nativeQuery = true)
    BigInteger getCreditTransNo(@Param("commId") Long commId);

    @Query(value = " SELECT  ct_id\n" +
            "                    FROM sys_brk_commission_trans  " +
            "                    join sys_brk_main_transactions sbmt on ct_debit_trans = sbmt.trans_no  " +
            "                    where sbmt.trans_ref_no = :ref  " +
            "                    and ct_trans_type = :transType " +
            "                    and coalesce (ct_loaded,'N') = 'N'",nativeQuery = true)
    List<BigInteger> getAllActiveComms(@Param("ref") String ref, @Param("transType") String transType);

    @Query(value = " SELECT  ct_id\n" +
            "                    FROM sys_brk_commission_trans  " +
            "                    join sys_brk_main_transactions sbmt on ct_credit_trans = sbmt.trans_no  " +
            "                    where sbmt.trans_ref_no = :ref  " +
            "                    and ct_trans_type = :transType " +
            "                    and coalesce (ct_loaded,'N') = 'N'",nativeQuery = true)
    List<BigInteger> getAllCreditActiveComms(@Param("ref") String ref, @Param("transType") String transType);

    @Query(value = "select sum(coalesce(ct_amount,0))  from sys_brk_commission_trans  " +
            "where ct_trans_type=:transType  " +
            "and ct_credit_trans =:transNo", nativeQuery = true)
    BigDecimal getCreditTotalCommission(@Param("transNo") Long commId, @Param("transType") String transType);

    @Query(value = "select sum(coalesce(ct_whtx,0))  from sys_brk_commission_trans  " +
            "where ct_trans_type=:transType " +
            "and ct_credit_trans =:transNo", nativeQuery = true)
    BigDecimal getCreditTotalWhtx(@Param("transNo") Long commId, @Param("transType") String transType);

}
