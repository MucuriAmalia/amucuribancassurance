package com.brokersystems.brokerapp.bulktransactions.repositories;

import com.brokersystems.brokerapp.bulktransactions.models.BulkPolCnCreation;
import com.brokersystems.brokerapp.bulktransactions.models.BulkRefunds;
import com.brokersystems.brokerapp.trans.model.SystemTransactions;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;

public interface BulkRefundsCreationRepo extends PagingAndSortingRepository<BulkRefunds, Long>, QueryDslPredicateExecutor<BulkRefunds> {

    @Query(value = "SELECT sbcbp.bulk_refund_id, sbcbp.pol_no, sbcbp.pol_proposal_no, sbcbp.pol_trans_type, \n" +
            "sbcbp.bulk_policy_sum_insured,sbcbp.bulk_policy_premium, sbcbp.policy_refundable_amount, \n" +
            "sbcbp.refund_amount,sbcbp.bulk_policy_wef, sbcbp.bulk_policy_wet, sbcbp.bulk_policy_date_uploaded, \n" +
            "sbcbp.bulk_policy_clnt_fname, COUNT(*) OVER() as total_rows  \n"+
            "FROM sys_brk_bulk_pol_refunds sbcbp \n" +
            "WHERE sbcbp.bulk_policy_processed = 'N' AND bulk_policy_authorized ='N' \n" +
            "AND (LOWER(sbcbp.bulk_policy_clnt_fname) like LOWER(:search) \n" +
            "OR LOWER(sbcbp.bulk_policy_clnt_other_names) like LOWER(:search) \n" +
//            "OR LOWER(sbcbp.bulk_policy_insured_fname) like LOWER(:search) \n" +
            "OR LOWER(sbcbp.pol_no) like LOWER(:search) \n" +
            "OR LOWER(sbcbp.pol_proposal_no) like LOWER(:search) \n" +
            "OR LOWER(sbcbp.bulk_policy_cover_type) like LOWER(:search) \n" +
            "OR CAST(sbcbp.bulk_policy_date_uploaded AS TEXT) like :search \n" +
            "OR CAST(sbcbp.bulk_policy_wef AS TEXT) like :search) \n" +
            "ORDER BY sbcbp.bulk_refund_id desc \n" +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findUnprocessedBulkPol(@Param("search") String search,
                                          @Param("pageNo") int pageNo,
                                          @Param("limit") int limit);

//    @Query(value = "SELECT sbcbp.bulk_policy_id,sbp.pol_no, sbp.pol_proposal_no,\n" +
//            "     sbp.pol_trans_type, sbp.pol_sum_insur_amt, sbp.pol_basic_premium_amt, sbc.client_fname, sbcbp.bulk_policy_date_uploaded, \n" +
//            "       sbp.pol_wef_date, sbp.pol_wet_date, sbp.pol_date, \n" +
//            "       CASE WHEN sbp.pol_current_status = 'A' THEN 'Active' \n" +
//            "            WHEN sbp.pol_current_status = 'D' THEN 'Draft' \n" +
//            "            WHEN sbp.pol_current_status = 'CN' THEN 'Cancelled' \n" +
//            "            WHEN sbp.pol_current_status = 'CO' THEN 'Converted' \n" +
//            "            WHEN sbp.pol_current_status = 'R' THEN 'Ready' \n" +
//            "            WHEN sbp.pol_current_status = 'PL' THEN 'Pre-Loaded' \n" +
//            "            WHEN sbp.pol_current_status = 'PD' THEN 'Loaded Policy' \n" +
//            "            ELSE sbp.pol_current_status \n" +
//            "       END AS pol_current_status , COUNT(*) OVER() as total_rows\n" +
//            "FROM sys_brk_policies sbp \n" +
//            "INNER JOIN sys_brk_clients sbc ON sbc.client_id = sbp.pol_client_id \n" +
//            "INNER JOIN sys_brk_bulk_pol_refunds sbcbp ON sbcbp.bulk_cn_pol_id = sbp.pol_id \n" +
//            "INNER JOIN sys_brk_risks sbr ON sbr.risk_pol_id = sbp.pol_id \n" +
//            "INNER JOIN sys_brk_covertypes sbc2 ON sbc2.cov_id = sbr.risk_cover_id \n" +
//            //"WHERE pol_auth_status = 'D' \n" +
//            //"AND sbcbp.bulk_policy_processed = 'Y' \n" +
//            "WHERE  sbcbp.bulk_policy_processed = 'Y' AND bulk_policy_authorized ='N' \n" +
//            "AND (LOWER(sbc2.cov_desc) like LOWER(:search) \n" +
//            "OR LOWER(sbp.pol_no) like LOWER(:search) \n" +
//            "OR LOWER(sbc.client_fname) like LOWER(:search) \n" +
//            "OR LOWER(sbc.client_onames) like LOWER(:search) \n" +
//            "OR CAST(sbp.pol_wef_date AS TEXT) like :search \n" +
//            "OR CAST(sbp.pol_wet_date AS TEXT) like :search \n" +
//            "OR CAST(sbp.pol_date AS TEXT) like :search) \n" +
//            "ORDER BY sbp.pol_id desc \n" +
//            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
//    List<Object[]> viewUnprocessedBulkPol(@Param("search") String search,
//                                          @Param("pageNo") int pageNo,
//                                          @Param("limit") int limit);


    @Query(value = "SELECT sbmc.mck_id, sbmc.task_name, COUNT(*) OVER() AS total_rows FROM sys_brk_maker_checker sbmc WHERE sbmc.maker_task_type = 'RF' AND sbmc.check_status = 'N' AND LOWER(sbmc.task_name) LIKE LOWER(:search) ORDER BY sbmc.mck_id DESC OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> viewUnprocessedBulkPol(@Param("search") String search, @Param("pageNo") int pageNo, @Param("limit") int limit);


        @Query(value = "select  * from sys_brk_bulk_pol_refunds where bulk_refund_id = :bulkId", nativeQuery = true)
        BulkRefunds findByBulkId(@Param("bulkId") Long bulkId);

//        @Query(value = "SELECT * FROM sys_brk_policies sbp WHERE LOWER(sbp.pol_no) = LOWER(:policyNo) and LOWER(sbp.pol_rev_no) = LOWER(:pol_rev_no)", nativeQuery = true)
//        PolicyTrans findByPolNo(@Param("policyNo") String policyNo, @Param("pol_rev_no") String pol_rev_no);

    @Query("SELECT p FROM PolicyTrans p WHERE LOWER(p.polNo) = LOWER(:policyNo) AND LOWER(p.polRevNo) = LOWER(:polRevNo)")
    PolicyTrans findByPolNo(@Param("policyNo") String policyNo, @Param("polRevNo") String polRevNo);

    @Query(value = "SELECT trans_no FROM sys_brk_main_transactions WHERE trans_balance < 0 " +
            "AND trans_clnt_type ='C' AND trans_dc = 'C' " +
            "AND trans_type NOT IN ('RC', 'SAG', 'RF') AND trans_pol_id = :polId", nativeQuery = true)
    List<BigInteger> findRefundableTransactionIds(@Param("polId") Long polId);

    @Query(value = "SELECT sbmc.mck_id FROM sys_brk_bulk_pol_refunds sbbpr " +
            "JOIN sys_brk_maker_checker sbmc ON sbbpr.refund_pol_id = sbmc.task_pol_id " +
            "WHERE sbmc.maker_task_type = 'RF' " +
            "AND sbbpr.bulk_policy_processed = 'Y' AND sbbpr.bulk_policy_authorized = 'N' AND sbmc.task_pol_id = :polId  ", nativeQuery = true)
    BigInteger findRefundableTransactionMakerCheckerIds(@Param("polId") Long polId);
}
