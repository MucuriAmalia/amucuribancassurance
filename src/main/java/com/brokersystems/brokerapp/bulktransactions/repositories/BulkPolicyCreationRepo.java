package com.brokersystems.brokerapp.bulktransactions.repositories;

import com.brokersystems.brokerapp.bulktransactions.models.BulkPolicyCreation;
import com.brokersystems.brokerapp.bulktransactions.models.WezeshaStockCreation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BulkPolicyCreationRepo extends PagingAndSortingRepository<BulkPolicyCreation, Long>, QueryDslPredicateExecutor<BulkPolicyCreation> {

    @Query(value = "SELECT sbcbp.bulk_policy_cover_type, sbcbp.bulk_policy_clnt_fname, sbcbp.bulk_policy_clnt_other_names, sbcbp.bulk_policy_insured_fname, \n" +
            "sbcbp.bulk_policy_insured_other_names, sbcbp.bulk_policy_wef, sbcbp.bulk_policy_date_uploaded, sbcbp.bulk_policy_id, COUNT(*) OVER() as total_rows, \n" +
            "sbcbp.bulk_policy_sum_insured  \n"+
            "FROM sys_brk_create_bulk_policy sbcbp \n" +
            "WHERE sbcbp.bulk_policy_processed = 'N' \n" +
            "AND sbcbp.bulk_policy_uploaded_by = :currentUserId \n" +
            "AND (LOWER(sbcbp.bulk_policy_clnt_fname) like LOWER(:search) \n" +
            "OR LOWER(sbcbp.bulk_policy_clnt_other_names) like LOWER(:search) \n" +
            "OR LOWER(sbcbp.bulk_policy_insured_fname) like LOWER(:search) \n" +
            "OR LOWER(sbcbp.bulk_policy_insured_other_names) like LOWER(:search) \n" +
            "OR LOWER(sbcbp.bulk_policy_cover_type) like LOWER(:search) \n" +
            "OR CAST(sbcbp.bulk_policy_date_uploaded AS TEXT) like :search \n" +
            "OR CAST(sbcbp.bulk_policy_wef AS TEXT) like :search) \n" +
            "ORDER BY sbcbp.bulk_policy_id desc \n" +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findUnprocessedBulkPol(@Param("search") String search,
                                          @Param("pageNo") int pageNo,
                                          @Param("limit") int limit,
                                          @Param("currentUserId") Long currentUserId);

    @Query(value = "SELECT sbp.pol_id,sbc2.cov_desc,sbp.pol_no,\n" +
            "       sbp.pol_basic_premium_amt, sbc.client_fname, sbc.client_onames, \n" +
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
            "INNER JOIN sys_brk_create_bulk_policy sbcbp ON sbcbp.bulk_policy_pol_id = sbp.pol_id \n" +
            "INNER JOIN sys_brk_risks sbr ON sbr.risk_pol_id = sbp.pol_id \n" +
            "INNER JOIN sys_brk_covertypes sbc2 ON sbc2.cov_id = sbr.risk_cover_id \n" +
            //"WHERE pol_auth_status = 'D' \n" +
            //"AND sbcbp.bulk_policy_processed = 'Y' \n" +
            //"WHERE  sbcbp.bulk_policy_authorized = 'N' \n" +
            "WHERE pol_auth_status != 'A' \n" +
            "AND sbcbp.bulk_policy_processed_by <> :currentUserId \n" +
            "AND (LOWER(sbc2.cov_desc) like LOWER(:search) \n" +
            "OR LOWER(sbp.pol_no) like LOWER(:search) \n" +
            "OR LOWER(sbc.client_fname) like LOWER(:search) \n" +
            "OR LOWER(sbc.client_onames) like LOWER(:search) \n" +
            "OR CAST(sbp.pol_wef_date AS TEXT) like :search \n" +
            "OR CAST(sbp.pol_wet_date AS TEXT) like :search \n" +
            "OR CAST(sbp.pol_date AS TEXT) like :search) \n" +
            "ORDER BY sbp.pol_id desc \n" +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> viewUnprocessedBulkPol(@Param("search") String search,
                                          @Param("pageNo") int pageNo,
                                          @Param("limit") int limit,
                                          @Param("currentUserId") Long currentUserId);



    @Query(value = "select * from sys_brk_create_bulk_policy where bulk_policy_pol_id = :policyId", nativeQuery = true)
    Optional<BulkPolicyCreation> policyFromUploadCheck(@Param("policyId") Long policyId);

    @Query(value = "select  * from sys_brk_create_bulk_policy where bulk_policy_pol_id = :id", nativeQuery = true)
    BulkPolicyCreation findBulkStockByPolicyId(@Param("id") Long id);

    @Query(value = "SELECT * FROM sys_brk_create_bulk_policy WHERE bulk_policy_pol_id = :bulkId", nativeQuery = true)
    BulkPolicyCreation findWithPolicyTransByBulkId(@Param("bulkId") Long bulkId);

}
