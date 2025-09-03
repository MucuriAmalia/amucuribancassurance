package com.brokersystems.brokerapp.bulktransactions.repositories;

import com.brokersystems.brokerapp.bulktransactions.models.CreditLife;
import com.brokersystems.brokerapp.bulktransactions.models.WezeshaStockCreation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CreditLifeRepository extends PagingAndSortingRepository<CreditLife, Long>, QueryDslPredicateExecutor<CreditLife> {

    @Query(value = "SELECT sbcl.credit_life_id , sbcl.credit_cover_type, sbcl.credit_product_name, sba.acct_name ,\n" +
            "sbcl.credit_client_fname, sbcl.credit_client_othernames , sbcl.credit_cover_from , sbcl.credit_cover_to ,sbcl.credit_upload_date," +
            "COUNT(*) OVER() as total_rows \n" +
            "FROM sys_brk_credit_life sbcl \n" +
            "INNER JOIN sys_brk_accounts sba on sba.acct_id = sbcl.credit_insurer_code \n" +
            "WHERE sbcl.credit_trans_status = 'N'\n" +
            "AND sbcl.credit_uploaded_by = :currentUserId \n" +
            "AND (LOWER(sbcl.credit_client_fname) like LOWER(:search) \n" +
            "AND LOWER(sbcl.credit_client_othernames) like LOWER(:search) \n" +
            "OR LOWER(sbcl.credit_cover_type) like LOWER(:search) \n" +
            "OR LOWER(sbcl.credit_product_name) like LOWER(:search) \n" +
            "OR LOWER(sba.acct_name) like LOWER(:search) \n" +
            "OR CAST(sbcl.credit_cover_from AS TEXT) like :search \n" +
            "OR CAST(sbcl.credit_cover_to AS TEXT) like :search \n" +
            "OR CAST(sbcl.credit_upload_date AS TEXT) like :search) \n" +
            "ORDER BY sbcl.credit_life_id desc \n" +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findUnprocessedCreditLife(@Param("search") String search,
                                                @Param("pageNo") int pageNo,
                                                @Param("limit") int limit,
                                                @Param("currentUserId") Long currentUserId);

    @Query(value = "select distinct sbp.pol_id,sbc2.cov_desc,sbp.pol_no, sbp.pol_basic_premium_amt ,\n" +
            " sbc.client_fname, sbc.client_onames,sbp.pol_wef_date, sbp.pol_wet_date, sbp.pol_date,\n" +
            "    CASE WHEN sbp.pol_current_status = 'A' THEN 'Active' \n" +
            "    WHEN sbp.pol_current_status = 'D' THEN 'Draft' \n" +
            "    WHEN sbp.pol_current_status = 'CN' THEN 'Cancelled' \n" +
            "    WHEN sbp.pol_current_status = 'CO' THEN 'Converted' \n" +
            "    WHEN sbp.pol_current_status = 'R' THEN 'Ready' \n" +
            "    WHEN sbp.pol_current_status = 'PL' THEN 'Pre-Loaded' \n" +
            "    WHEN sbp.pol_current_status = 'PD' THEN 'Loaded Policy' \n" +
            "    WHEN sbp.pol_current_status = 'LD' THEN 'Loaded Data' \n" +
            "            ELSE sbp.pol_current_status \n" +
            "       END AS pol_current_status, COUNT(*) OVER() as total_rows \n" +
            "FROM sys_brk_policies sbp \n" +
            "INNER JOIN sys_brk_clients sbc ON sbc.client_id = sbp.pol_client_id \n" +
            "INNER JOIN sys_brk_credit_life sbcl ON sbcl.credit_life_pol_id = sbp.pol_id \n" +
            "INNER JOIN sys_brk_risks sbr ON sbr.risk_pol_id = sbp.pol_id \n" +
            "INNER JOIN sys_brk_covertypes sbc2 ON sbc2.cov_id = sbr.risk_cover_id \n" +
            "WHERE sbp.pol_auth_status != 'A' \n" +
            "AND sbcl.credit_trans_status = 'Y'\n" +
            //"WHERE  sbcl.bulk_policy_authorized = 'N' \n" +
            "AND sbcl.credit_processed_by <> :currentUserId \n" +
            "AND (LOWER(sbc2.cov_desc) like LOWER(:search) \n" +
            "OR LOWER(sbp.pol_no) like LOWER(:search) \n" +
            "OR LOWER(sbc.client_fname) like LOWER(:search) \n" +
            "OR LOWER(sbc.client_onames) like LOWER(:search) \n" +
            "OR CAST(sbp.pol_wef_date AS TEXT) like :search\n" +
            "OR CAST(sbp.pol_wet_date AS TEXT) like :search \n" +
            "OR CAST(sbp.pol_date AS TEXT) like :search)\n" +
            "ORDER BY sbp.pol_id desc \n" +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> viewUnprocessedCreditLife(@Param("search") String search,
                                                    @Param("pageNo") int pageNo,
                                                    @Param("limit") int limit,
                                                    @Param("currentUserId") Long currentUserId);

    @Query(value = "select  * from sys_brk_credit_life where credit_life_pol_id = :id", nativeQuery = true)
    CreditLife findCreditLifeByPolicyId(@Param("id") Long id);
}
