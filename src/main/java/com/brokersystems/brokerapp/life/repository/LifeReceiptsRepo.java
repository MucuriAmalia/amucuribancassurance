package com.brokersystems.brokerapp.life.repository;

import com.brokersystems.brokerapp.life.model.LifeReceipts;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by waititu on 18/03/2019.
 */
public interface LifeReceiptsRepo extends PagingAndSortingRepository<LifeReceipts,Long>, QueryDslPredicateExecutor<LifeReceipts> {
    @Query(value = "SELECT lrct_amt,lrct_dc,lrct_id,lrct_trans_no FROM sys_brk_life_rcts sblr " +
            "LEFT JOIN sys_brk_policies sbp on sblr.lrct_policy_id = sbp.pol_id " +
            "where pol_id = :polCode AND lrct_balance >= 0", nativeQuery = true)
    List<Object[]> findLifeReceipts(@Param("polCode") Long polCode);


    @Query(value = " SELECT lrct_id as id,lrct_amt as amount,lrct_dc as dc,lrct_balance as balance FROM sys_brk_life_rcts sblr \n" +
            "                        LEFT JOIN sys_brk_policies sbp on sblr.lrct_policy_id = sbp.pol_id \n" +
            "                        left join sys_brk_receipts sbr on sbr.receipt_id  = sblr.lrct_receipt_id \n" +
            "                        where pol_id = :polCode AND lrct_balance >= 0 and coalesce(sbr.receipt_cancelled,'N') = 'N'", nativeQuery = true)
    List<Object[]> findPolicyLifeReceipts(@Param("polCode") Long polCode);


    @Query(value = "select lrct_trans_no  from sys_brk_life_rcts where lrct_id=:id",nativeQuery = true)
    Long findPolicyLifeReceiptsId(@Param("id") Long id);

    @Query(value = "select lrct_receipt_id  from sys_brk_life_rcts where lrct_id=:id",nativeQuery = true)
    Long findPolicyLifeReceiptsRec(@Param("id") Long id);

    @Query(value = " SELECT sbr.receipt_id FROM sys_brk_life_rcts sblr \n" +
            "                        LEFT JOIN sys_brk_policies sbp on sblr.lrct_policy_id = sbp.pol_id \n" +
            "                        left join sys_brk_receipts sbr on sbr.receipt_id  = sblr.lrct_receipt_id \n" +
            "                        where pol_id = :polCode and coalesce(sbr.receipt_cancelled,'N') = 'N'", nativeQuery = true)
    List<BigInteger> findAllPolicyLifeReceipts(@Param("polCode") Long polCode);



}
