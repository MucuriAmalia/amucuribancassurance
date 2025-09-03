package com.brokersystems.brokerapp.claims.repository;

import com.brokersystems.brokerapp.claims.model.ClaimBookings;
import com.brokersystems.brokerapp.claims.model.ClaimClaimants;
import com.brokersystems.brokerapp.claims.model.ClaimantsDef;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by peter on 3/8/2017.
 */
public interface ClaimClaimantsRepo extends PagingAndSortingRepository<ClaimClaimants, Long>, QueryDslPredicateExecutor<ClaimClaimants> {


//    @Query(value = "SELECT x.*, COUNT(*) OVER() as total_rows FROM (\n" +
//            "SELECT \n" + "clm_clmnt_id,\n" + "clm_clmnt_tp,\n" + "CONCAT(sbc.client_fname, ' ', COALESCE(sbc.client_onames, '')) AS self_claimant,\n" + "CONCAT(clmnt_surname, ' ', clmnt_othernames) AS tp_claimant,\n" + "clm_clmnt_status,\n" + "sys_brk_clm_claimants.created_date,\n" + "sbu.user_username,\n" + "sbcp.clm_prl_limit_amt,\n" + "sbp.p_desc,\n" + "clm_prl_type,\n" +
//            "clmants.clmnt_email\n" +
//            "FROM sys_brk_clm_claimants\n" + "LEFT JOIN sys_brk_clients sbc ON sbc.client_id = clm_clmnt_client_id \n" + "LEFT JOIN sys_brk_claimants clmants ON clmants.clmnt_id = clm_clmnt_clmnt_id\n" + "LEFT JOIN sys_brk_users sbu ON sbu.user_id = clm_created_user\n" + "JOIN sys_brk_clm_perils sbcp ON sbcp.clm_prl_clmnt_id = clm_clmnt_id\n" + "JOIN sys_brk_perils sbp ON sbp.p_code = sbcp.clm_prl_peril_id\n" + "    WHERE clm_clmnt_clm_id = :clmId\n" + "\n" + "UNION\n" + "\n" + "SELECT \n" + "csp_spr_id,\n" + "'S',\n" + "sbsp.provd_name,\n" + "NULL,\n" + "'1' AS spr_status,\n" + "csp_created_dt,\n" + "sbu.user_username,\n" + "sbcp.clm_prl_limit_amt,\n" + "sbp.p_desc,\n" + "clm_prl_type,\n" + "NULL AS clmnt_email from sys_brk_clm_srv_provider\n" +"JOIN sys_brk_serv_providers sbsp ON sbsp.provd_id = csp_spr_id\n" +"JOIN sys_brk_clm_perils sbcp ON sbcp.clm_prl_spr_id = csp_id\n" +"JOIN sys_brk_perils sbp ON sbp.p_code = sbcp.clm_prl_peril_id\n" +"LEFT JOIN sys_brk_users sbu ON sbu.user_id = csp_created_user\n" +"WHERE csp_clm_id = :clmId\n" +
//            ") x\n" +
//            "ORDER BY x.created_date DESC\n" +
//            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
@Query(value = "SELECT x.*, COUNT(*) OVER() as total_rows FROM (\n" +
        "SELECT \n" +
        "clm_clmnt_id,\n" +
        "clm_clmnt_tp,\n" +
        "CONCAT(sbc.client_fname, ' ', COALESCE(sbc.client_onames, '')) AS self_claimant,\n" +
        "CONCAT(clmnt_surname, ' ', clmnt_othernames) AS tp_claimant,\n" +
        "clm_clmnt_status,\n" +
        "sys_brk_clm_claimants.created_date,\n" +
        "sbu.user_username,\n" +
        "sys_brk_clm_claimants.clm_clmnt_amount,\n" +
        "sbp.p_desc,\n" +
        "clm_prl_type,\n" +
        "clmants.clmnt_email,\n" +
        "ct.cov_desc AS cover_type_desc\n" +
        "FROM sys_brk_clm_claimants\n" +
        "LEFT JOIN sys_brk_clients sbc ON sbc.client_id = clm_clmnt_client_id \n" +
        "LEFT JOIN sys_brk_claimants clmants ON clmants.clmnt_id = clm_clmnt_clmnt_id\n" +
        "LEFT JOIN sys_brk_users sbu ON sbu.user_id = clm_created_user\n" +
        "JOIN sys_brk_clm_perils sbcp ON sbcp.clm_prl_clmnt_id = clm_clmnt_id\n" +
        "JOIN sys_brk_perils sbp ON sbp.p_code = sbcp.clm_prl_peril_id\n" +
        "-- Direct join to get cover type\n" +
        "JOIN sys_brk_clm_bookings cb ON cb.clm_id = clm_clmnt_clm_id\n" +
        "LEFT JOIN sys_brk_risks rt ON rt.risk_id = cb.clm_risk_id\n" +
        "LEFT JOIN sys_brk_covertypes ct ON ct.cov_id = rt.risk_cover_id\n" +
        "WHERE clm_clmnt_clm_id = :clmId\n" +
        "\n" +
        "UNION\n" +
        "\n" +
        "SELECT \n" +
        "csp_spr_id,\n" +
        "'S',\n" +
        "CASE \n" +
        "    WHEN EXISTS (SELECT 1 FROM sys_brk_clm_claimants cc WHERE cc.clm_clmnt_clm_id = csp_clm_id AND cc.clm_clmnt_tp = 'S')\n" +
        "    THEN (\n" +
        "        SELECT CONCAT(sbc2.client_fname, ' ', COALESCE(sbc2.client_onames, ''))\n" +
        "        FROM sys_brk_clm_claimants cc2\n" +
        "        JOIN sys_brk_clients sbc2 ON sbc2.client_id = cc2.clm_clmnt_client_id\n" +
        "        WHERE cc2.clm_clmnt_clm_id = csp_clm_id AND cc2.clm_clmnt_tp = 'S'\n" +
        "        LIMIT 1\n" +
        "    )\n" +
        "    ELSE sbsp.provd_name\n" +
        "END AS self_claimant,\n" +
        "NULL AS tp_claimant,\n" +
        "'1' AS spr_status,\n" +
        "csp_created_dt,\n" +
        "sbu.user_username,\n" +
        "COALESCE(cc.clm_clmnt_amount, sbcp.clm_prl_reserve, 0) AS clm_clmnt_amount,\n" +
        "sbp.p_desc,\n" +
        "clm_prl_type,\n" +
        "NULL AS clmnt_email,\n" +
        "ct.cov_desc AS cover_type_desc\n" +
        "FROM sys_brk_clm_srv_provider\n" +
        "JOIN sys_brk_serv_providers sbsp ON sbsp.provd_id = csp_spr_id\n" +
        "JOIN sys_brk_clm_perils sbcp ON sbcp.clm_prl_spr_id = csp_id\n" +
        "JOIN sys_brk_perils sbp ON sbp.p_code = sbcp.clm_prl_peril_id\n" +
        "LEFT JOIN sys_brk_users sbu ON sbu.user_id = csp_created_user\n" +
        "LEFT JOIN sys_brk_clm_claimants cc ON cc.clm_clmnt_clm_id = csp_clm_id\n" +
        "-- Direct join to get the cover type description\n" +
        "JOIN sys_brk_clm_bookings cb ON cb.clm_id = csp_clm_id\n" +
        "LEFT JOIN sys_brk_risks rt ON rt.risk_id = cb.clm_risk_id\n" +
        "LEFT JOIN sys_brk_covertypes ct ON ct.cov_id = rt.risk_cover_id\n" +
        "WHERE csp_clm_id = :clmId\n" +
        ") x\n" +
        "ORDER BY x.created_date DESC\n" +
        "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
List<Object[]> findClmClaimants(@Param("clmId") Long clmId,
                                @Param("pageNo") int pageNo,
                                @Param("limit") int limit);

}
