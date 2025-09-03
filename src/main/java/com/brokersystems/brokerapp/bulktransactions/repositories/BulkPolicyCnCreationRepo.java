package com.brokersystems.brokerapp.bulktransactions.repositories;

import com.brokersystems.brokerapp.bulktransactions.models.BulkPolCnCreation;
import com.brokersystems.brokerapp.bulktransactions.models.BulkRenewal;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BulkPolicyCnCreationRepo extends PagingAndSortingRepository<BulkPolCnCreation, Long>, QueryDslPredicateExecutor<BulkPolCnCreation> {

    @Query(value = "SELECT sbcbp.bulk_policy_id, sbcbp.pol_no, sbcbp.pol_proposal_no, sbcbp.policy_current_status, \n" +
            "sbcbp.bulk_policy_sum_insured,sbcbp.bulk_policy_premium, sbcbp.bulk_policy_clnt_fname, sbcbp.cancelation_amt, sbcbp.bulk_policy_cn_effective_date, sbcbp.bulk_policy_date_uploaded, \n" +
            " COUNT(*) OVER() as total_rows  \n"+
            "FROM sys_brk_bulk_cn_policy sbcbp \n" +
            "WHERE sbcbp.bulk_policy_processed = 'N' AND bulk_policy_authorized ='N' \n" +
            "AND (LOWER(sbcbp.bulk_policy_clnt_fname) like LOWER(:search) \n" +
            "OR LOWER(sbcbp.bulk_policy_clnt_other_names) like LOWER(:search) \n" +
//            "OR LOWER(sbcbp.bulk_policy_insured_fname) like LOWER(:search) \n" +
            "OR LOWER(sbcbp.pol_no) like LOWER(:search) \n" +
            "OR LOWER(sbcbp.pol_proposal_no) like LOWER(:search) \n" +
            "OR LOWER(sbcbp.bulk_policy_cover_type) like LOWER(:search) \n" +
            "OR CAST(sbcbp.bulk_policy_date_uploaded AS TEXT) like :search \n" +
            "OR CAST(sbcbp.bulk_policy_wef AS TEXT) like :search) \n" +
            "ORDER BY sbcbp.bulk_policy_id desc \n" +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findUnprocessedBulkPol(@Param("search") String search,
                                          @Param("pageNo") int pageNo,
                                          @Param("limit") int limit);

    @Query(value = "SELECT sbcbp.bulk_policy_id,sbp.pol_no, sbp.pol_proposal_no,\n" +
            "     sbp.pol_trans_type, sbp.pol_sum_insur_amt, sbp.pol_basic_premium_amt, sbc.client_fname, sbcbp.bulk_policy_date_uploaded, \n" +
            "       sbp.pol_wef_date, sbp.pol_wet_date, sbp.pol_date, \n" +
            "       CASE WHEN sbp.pol_current_status = 'A' THEN 'Active' \n" +
            "            WHEN sbp.pol_current_status = 'D' THEN 'Draft' \n" +
            "            WHEN sbp.pol_current_status = 'CN' THEN 'Cancelled' \n" +
            "            WHEN sbp.pol_current_status = 'CO' THEN 'Converted' \n" +
            "            WHEN sbp.pol_current_status = 'R' THEN 'Ready' \n" +
            "            WHEN sbp.pol_current_status = 'PL' THEN 'Pre-Loaded' \n" +
            "            WHEN sbp.pol_current_status = 'PD' THEN 'Loaded Policy' \n" +
            "            ELSE sbp.pol_current_status \n" +
            "       END AS pol_current_status , COUNT(*) OVER() as total_rows\n" +
            "FROM sys_brk_policies sbp \n" +
            "INNER JOIN sys_brk_clients sbc ON sbc.client_id = sbp.pol_client_id \n" +
            "INNER JOIN sys_brk_bulk_cn_policy sbcbp ON sbcbp.bulk_cn_pol_id = sbp.pol_id \n" +
//            "INNER JOIN sys_brk_risks sbr ON sbr.risk_pol_id = sbp.pol_id \n" +
//            "INNER JOIN sys_brk_covertypes sbc2 ON sbc2.cov_id = sbr.risk_cover_id \n" +
            //"WHERE pol_auth_status = 'D' \n" +
            //"AND sbcbp.bulk_policy_processed = 'Y' \n" +
            "WHERE  sbcbp.bulk_policy_processed = 'Y' AND bulk_policy_authorized ='N' \n" +
        //    "AND (LOWER(sbc2.cov_desc) like LOWER(:search) \n" +
            "AND (LOWER(sbp.pol_no) like LOWER(:search) \n" +
            "OR LOWER(sbc.client_fname) like LOWER(:search) \n" +
            "OR LOWER(sbc.client_onames) like LOWER(:search) \n" +
            "OR CAST(sbp.pol_wef_date AS TEXT) like :search \n" +
            "OR CAST(sbp.pol_wet_date AS TEXT) like :search \n" +
            "OR CAST(sbp.pol_date AS TEXT) like :search) \n" +
            "ORDER BY sbp.pol_id desc \n" +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> viewUnprocessedBulkPol(@Param("search") String search,
                                          @Param("pageNo") int pageNo,
                                          @Param("limit") int limit);

        @Query(value = "select  * from sys_brk_bulk_cn_policy where bulk_policy_id = :bulkId", nativeQuery = true)
        BulkPolCnCreation findByBulkCNId(@Param("bulkId") Long bulkId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM sys_brk_bulk_cn_policy WHERE bulk_policy_id = :bulkId", nativeQuery = true)
    void deleteByBulkCNId(@Param("bulkId") Long bulkId);

}
