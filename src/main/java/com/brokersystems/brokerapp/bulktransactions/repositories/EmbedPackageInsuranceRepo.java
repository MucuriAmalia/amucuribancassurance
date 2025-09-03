package com.brokersystems.brokerapp.bulktransactions.repositories;

import com.brokersystems.brokerapp.bulktransactions.models.BulkPolicyCreation;
import com.brokersystems.brokerapp.bulktransactions.models.EmbedPackageInsurance;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmbedPackageInsuranceRepo extends PagingAndSortingRepository<EmbedPackageInsurance, Long>, QueryDslPredicateExecutor<EmbedPackageInsurance> {

    @Query(value = "SELECT sbepi.embed_package_id , sbepi.embed_cover_type, sbepi.embed_product_name, sba.acct_name ,\n" +
            "sbepi.embed_client_name, sbepi.embed_client_other_names , sbepi.embed_cover_from , sbepi.embed_cover_to ,sbepi.embed_upload_date," +
            "COUNT(*) OVER() as total_rows \n" +
            "FROM sys_brk_embed_package_insurance sbepi \n" +
            "INNER JOIN sys_brk_accounts sba on sba.acct_id = sbepi.embed_insurer_code \n" +
            "WHERE sbepi.embed_trans_status = 'N'\n" +
            "AND sbepi.embed_uploaded_by = :currentUserId \n" +
            "AND (LOWER(sbepi.embed_client_name) like LOWER(:search) \n" +
            "OR LOWER(sbepi.embed_client_other_names) like LOWER(:search) \n" +
            "OR LOWER(sbepi.embed_cover_type) like LOWER(:search) \n" +
            "OR LOWER(sbepi.embed_product_name) like LOWER(:search) \n" +
            "OR LOWER(sba.acct_name) like LOWER(:search) \n" +
            "OR CAST(sbepi.embed_cover_from AS TEXT) like :search \n" +
            "OR CAST(sbepi.embed_cover_to AS TEXT) like :search \n" +
            "OR CAST(sbepi.created_date AS TEXT) like :search) \n" +
            "ORDER BY sbepi.embed_package_id desc \n" +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findUnprocessedPackagedInsur(@Param("search") String search,
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
            "INNER JOIN sys_brk_embed_package_insurance sbepi ON sbepi.embed_policy_pol_id = sbp.pol_id \n" +
            "INNER JOIN sys_brk_risks sbr ON sbr.risk_pol_id = sbp.pol_id \n" +
            "INNER JOIN sys_brk_covertypes sbc2 ON sbc2.cov_id = sbr.risk_cover_id \n" +
            //"WHERE sbp.pol_auth_status = 'LD' \n" +
            //"AND sbepi.embed_trans_status = 'Y'\n" +
            "WHERE  sbepi.bulk_policy_authorized = 'N' \n" +
            "AND sbepi.embed_processed_by <> :currentUserId \n" +
            "AND (LOWER(sbc2.cov_desc) like LOWER(:search) \n" +
            "OR LOWER(sbp.pol_no) like LOWER(:search) \n" +
            "OR LOWER(sbc.client_fname) like LOWER(:search) \n" +
            "OR LOWER(sbc.client_onames) like LOWER(:search) \n" +
            "OR CAST(sbp.pol_wef_date AS TEXT) like :search\n" +
            "OR CAST(sbp.pol_wet_date AS TEXT) like :search \n" +
            "OR CAST(sbp.pol_date AS TEXT) like :search)\n" +
            "ORDER BY sbp.pol_id desc \n" +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> viewUnprocessedEmbedPackageInsur(@Param("search") String search,
                                                 @Param("pageNo") int pageNo,
                                                 @Param("limit") int limit,
                                                 @Param("currentUserId") Long currentUserId);


    @Query(value = "select  * from sys_brk_embed_package_insurance where embed_policy_pol_id = :id", nativeQuery = true)
    EmbedPackageInsurance findBulkStockByPolicyId(@Param("id") Long id);


    @Query(value = "select  * from sys_brk_embed_package_insurance where embed_policy_pol_id = :embedId", nativeQuery = true)
    EmbedPackageInsurance findWithPolicyTransByEmbedId(@Param("embedId") Long embedId);
}
