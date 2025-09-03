package com.brokersystems.brokerapp.bulktransactions.repositories;

import com.brokersystems.brokerapp.bulktransactions.models.Timiza;
import com.brokersystems.brokerapp.bulktransactions.models.WezeshaStockCreation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TimizaRepository extends PagingAndSortingRepository<Timiza, Long>, QueryDslPredicateExecutor<Timiza> {

    @Query(value = "SELECT sbtt.timiza_id, sbtt.timiza_cover_type, sbtt.timiza_client_fname, \n" +
            "sbtt.timiza_client_othernames, sbtt.timiza_premium,\n" +
            "sbtt.timiza_start_date,sbtt.timiza_end_date, sbtt.timiza_uploaded_date, COUNT(*) OVER() as total_rows \n" +
            "FROM sys_brk_trans_timiza sbtt \n" +
            "WHERE sbtt.timiza_trans_status = 'N' \n" +
            "AND sbtt.timiza_uploaded_by = :currentUserId \n" +
            "AND (LOWER(sbtt.timiza_client_fname) like LOWER(:search) \n" +
            "OR LOWER(sbtt.timiza_client_othernames) like LOWER(:search) \n" +
            "OR LOWER(sbtt.timiza_cover_type) like LOWER(:search) \n" +
            "OR CAST(sbtt.timiza_uploaded_date AS TEXT) like :search \n" +
            "OR CAST(sbtt.timiza_start_date AS TEXT) like :search \n" +
            "OR CAST(sbtt.timiza_end_date AS TEXT) like :search) \n" +
            "ORDER BY sbtt.timiza_id desc \n" +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findUnProcessedTimizaPol(@Param("search") String search,
                                             @Param("pageNo") int pageNo,
                                             @Param("limit") int limit,
                                            @Param("currentUserId") Long currentUserId);

    @Query(value = "SELECT sbp.pol_id,sbc2.cov_desc,sbp.pol_no, \n" +
            "       sbc.client_fname, sbc.client_onames, \n" +
            "       sbp.pol_wef_date, sbp.pol_wet_date, sbp.pol_date,\n" +
            "                   CASE WHEN sbp.pol_current_status = 'A' THEN 'Active' \n" +
            "                        WHEN sbp.pol_current_status = 'D' THEN 'Draft' \n" +
            "                        WHEN sbp.pol_current_status = 'CN' THEN 'Cancelled' \n" +
            "                        WHEN sbp.pol_current_status = 'CO' THEN 'Converted' \n" +
            "                        WHEN sbp.pol_current_status = 'R' THEN 'Ready' \n" +
            "                        WHEN sbp.pol_current_status = 'PL' THEN 'Pre-Loaded' \n" +
            "                        WHEN sbp.pol_current_status = 'PD' THEN 'Loaded Policy' \n" +
            "                        WHEN sbp.pol_current_status = 'LD' THEN 'Loaded Data' \n" +
            "                        ELSE sbp.pol_current_status \n" +
            "                   END AS pol_current_status , COUNT(*) OVER() as total_rows\n" +
            "            FROM sys_brk_policies sbp \n" +
            "            INNER JOIN sys_brk_clients sbc ON sbc.client_id = sbp.pol_client_id \n" +
            "            INNER JOIN sys_brk_trans_timiza sbtt ON sbtt.timiza_pol_id = sbp.pol_id \n" +
            "            INNER JOIN sys_brk_risks sbr ON sbr.risk_pol_id = sbp.pol_id \n" +
            "            INNER JOIN sys_brk_covertypes sbc2 ON sbc2.cov_id = sbr.risk_cover_id \n" +
//            "            WHERE pol_auth_status = 'LD' \n" +
//            "            AND sbtt.timiza_trans_status = 'Y' \n" +
            "WHERE  sbtt.bulk_policy_authorized = 'N' \n" +
            "            AND sbtt.timiza_processed_by <> :currentUserId \n" +
            "            AND (LOWER(sbc2.cov_desc) like LOWER(:search) \n" +
            "            OR LOWER(sbp.pol_no) like LOWER(:search) \n" +
            "            OR LOWER(sbc.client_fname) like LOWER(:search) \n" +
            "            OR LOWER(sbc.client_onames) like LOWER(:search) \n" +
            "            OR CAST(sbp.pol_wef_date AS TEXT) like :search \n" +
            "            OR CAST(sbp.pol_wet_date AS TEXT) like :search \n" +
            "            OR CAST(sbp.pol_date AS TEXT) like :search) \n" +
            "            ORDER BY sbp.pol_id desc \n" +
            "            OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> viewUnprocessedTimizaBulkPol(@Param("search") String search,
                                                 @Param("pageNo") int pageNo,
                                                 @Param("limit") int limit,
                                                @Param("currentUserId") Long currentUserId);


    @Query(value = "select  * from sys_brk_trans_timiza where timiza_pol_id = :id", nativeQuery = true)
    Timiza findBulkByPolicyId(@Param("id") Long id);
}
