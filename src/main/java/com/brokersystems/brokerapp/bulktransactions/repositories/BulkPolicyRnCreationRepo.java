package com.brokersystems.brokerapp.bulktransactions.repositories;

import com.brokersystems.brokerapp.bulktransactions.models.BulkPolEnCreation;
import com.brokersystems.brokerapp.bulktransactions.models.BulkPolicyCreation;
import com.brokersystems.brokerapp.bulktransactions.models.BulkRenewal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BulkPolicyRnCreationRepo extends PagingAndSortingRepository<BulkRenewal, Long>, QueryDslPredicateExecutor<BulkRenewal> {

//    @Query(value = "SELECT sbcbp.bulk_renewal_id, sbcbp.pol_no, sbcbp.bulk_policy_clnt_other_names, sbcbp.pol_proposal_no, sbcbp.bulk_policy_sum_insured, \n" +
//            "sbcbp.bulk_policy_premium, sbcbp.bulk_policy_wef, sbcbp.bulk_policy_date_uploaded,COUNT(*) OVER() as total_rows  \n"+
//            "FROM sys_brk_bulk_pol_renewals sbcbp \n" +
//            "WHERE  sbcbp.bulk_policy_authorized ='N' and sbcbp.bulk_policy_processed = 'N' \n" +
//            "AND (LOWER(sbcbp.bulk_policy_clnt_fname) like LOWER(:search) \n" +
//            "OR LOWER(sbcbp.bulk_policy_clnt_other_names) like LOWER(:search) \n" +
////            "OR LOWER(sbcbp.bulk_policy_insured_fname) like LOWER(:search) \n" +
////            "OR LOWER(sbcbp.bulk_policy_insured_other_names) like LOWER(:search) \n" +
//            "OR LOWER(sbcbp.bulk_policy_cover_type) like LOWER(:search) \n" +
//            "OR CAST(sbcbp.bulk_policy_date_uploaded AS TEXT) like :search \n" +
//            "OR CAST(sbcbp.bulk_policy_wef AS TEXT) like :search) \n" +
//            "ORDER BY sbcbp.bulk_renewal_id desc \n" +
//            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
//    List<Object[]> findUnprocessedBulkPol(@Param("search") String search,
//                                          @Param("pageNo") int pageNo,
//                                          @Param("limit") int limit);


@Query(value = "SELECT sbcbp.bulk_renewal_id, sbcbp.pol_no, sbcbp.bulk_policy_clnt_other_names, sbcbp.pol_proposal_no, sbcbp.new_sum_insured, \n" +
        "sbcbp.new_prem, sbcbp.bulk_policy_wef, sbcbp.new_interface, sbcbp.accrual_payment_type , sbcbp.accrual_instl_date , sbcbp.bulk_renewal_dt ,sbcbp.bulk_policy_date_uploaded,COUNT(*) OVER() as total_rows  \n"+
        "FROM sys_brk_bulk_pol_renewals sbcbp \n" +
        "WHERE  sbcbp.bulk_policy_authorized ='N' and sbcbp.bulk_policy_processed = 'N' \n" +
        "AND (LOWER(sbcbp.bulk_policy_clnt_fname) like LOWER(:search) \n" +
        "OR LOWER(sbcbp.bulk_policy_clnt_other_names) like LOWER(:search) \n" +
//            "OR LOWER(sbcbp.bulk_policy_insured_fname) like LOWER(:search) \n" +
//            "OR LOWER(sbcbp.bulk_policy_insured_other_names) like LOWER(:search) \n" +
        //            "OR LOWER(sbcbp.bulk_policy_insured_fname) like LOWER(:search) \n" +
        //            "OR LOWER(sbcbp.bulk_policy_insured_other_names) like LOWER(:search) \n" +
        "OR LOWER(sbcbp.bulk_policy_cover_type) like LOWER(:search) \n" +
        "OR CAST(sbcbp.bulk_policy_date_uploaded AS TEXT) like :search \n" +
        "OR CAST(sbcbp.bulk_policy_wef AS TEXT) like :search) \n" +
        "ORDER BY sbcbp.bulk_renewal_id desc \n" +
        "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
List<Object[]> findUnprocessedBulkPol(@Param("search") String search,
                                      @Param("pageNo") int pageNo,
                                      @Param("limit") int limit);

    @Query(value = "SELECT sbcbp.bulk_renewal_id,sbc2.cov_desc,sbp.pol_no,\n" +
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
            "INNER JOIN sys_brk_bulk_pol_renewals sbcbp ON sbcbp.new_pol_id = sbp.pol_id \n" +
            "INNER JOIN sys_brk_risks sbr ON sbr.risk_pol_id = sbp.pol_id \n" +
            "INNER JOIN sys_brk_covertypes sbc2 ON sbc2.cov_id = sbr.risk_cover_id \n" +
            //"WHERE pol_auth_status = 'D' \n" +
            //"AND sbcbp.bulk_policy_processed = 'Y' \n" +
            "WHERE sbp.pol_auth_status != 'RCV' and sbcbp.bulk_policy_processed ='Y' AND sbcbp.bulk_policy_authorized = 'N' \n" +
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
                                          @Param("limit") int limit);

    @Query(value = "select  * from sys_brk_bulk_pol_renewals where bulk_renewal_id = :bulkId", nativeQuery = true)
    BulkRenewal findByBulkId(@Param("bulkId") Long bulkId);
}
