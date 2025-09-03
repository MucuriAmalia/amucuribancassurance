package com.brokersystems.brokerapp.claims.repository;

import com.brokersystems.brokerapp.claims.model.ClaimBookings;
import com.brokersystems.brokerapp.claims.model.ClaimRequiredDocs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by peter on 3/4/2017.
 */
public interface ClaimsBookingRepo extends PagingAndSortingRepository<ClaimBookings, Long>, QueryDslPredicateExecutor<ClaimBookings> {

//    @Query(value = "select clm_id,sbu.user_username,clm_loss_date,clm_date,clm_status,clm_no,sbr.risk_sht_desc,clm_next_rvw_dt,sbp.pol_no,\n" +
//            "sbc.client_fname,sbc.client_onames,clm.risk_identifier,clm.balance_approved_by,clm.balance_approval_date,COUNT(*) OVER() AS total_rows  from sys_brk_clm_bookings clm \n" +
//            "join sys_brk_users sbu on clm_booked_by = sbu.user_id \n" +
//            "join sys_brk_risks sbr on clm_risk_id = sbr.risk_id \n" +
//            "join sys_brk_policies sbp on sbr.risk_pol_id  = sbp.pol_id \n" +
//            "join sys_brk_clients sbc on sbc.client_id =sbr.risk_insured_id  where lower(sbr.risk_sht_desc) like :riskId " +
//            "and lower(sbp.pol_no) like :polNo " +
//            "and  coalesce(sbc.client_id  ,-2000) = case when :clientCode=-2000 then coalesce(sbc.client_id,-2000) else :clientCode end and lower(clm_no) like :claimNo order by clm_date desc  OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
//     List<Object[]> getClaimBookings(@Param("riskId") String riskId,
//                                            @Param("polNo") String polNo,
//                                            @Param("clientCode") Long clientCode,
//                                            @Param("claimNo") String claimNo,
//                                          @Param("pageNo") int pageNo,
//                                          @Param("limit") int limit);
@Query(value = "select clm_id,clm_no,sbu.user_username,clm_loss_date,clm_date,clm_status," +
        "sbr.risk_sht_desc,clm_next_rvw_dt,sbp.pol_no," +
        "sbc.client_fname,sbc.client_onames,clm.risk_identifier," +
        "clm.balance_approved_by,clm.balance_approval_date," +
        "sbr.risk_id," +                           // Risk ID (position 14)
        "sbprod.pr_desc," +                    // Product name (position 15)
        "sbins.acct_acc_code," +                   // Branch account code (position 16)
        "ins.acct_name," +                         // Insurer name (position 17)
        "(select act.clm_act_insurer_ref from sys_brk_clm_activities act " +
        " where act.clm_act_clm_id = clm.clm_id and act.clm_act_insurer_ref is not null " +
        " order by act.clm_act_dt desc limit 1) as insurer_ref," +
        "COUNT(*) OVER() AS total_rows " +         // Total rows (position 18)
        "from sys_brk_clm_bookings clm " +
        "left join sys_brk_users sbu on clm.clm_booked_by = sbu.user_id " +
        "left join sys_brk_risks sbr on clm.clm_risk_id = sbr.risk_id " +
        "left join sys_brk_policies sbp on sbr.risk_pol_id = sbp.pol_id " +
        "left join sys_brk_clients sbc on sbc.client_id = sbr.risk_insured_id " +
        "left join sys_brk_products sbprod on sbp.pol_prod_id = sbprod.pr_code " +
        "left join sys_brk_accounts sbins on sbp.pol_branch_id = sbins.acct_acc_code " +
        "left join sys_brk_accounts ins on sbp.pol_agent_id = ins.acct_id " +
        "where clm.clm_status != 'D' " +
        "and lower(coalesce(sbr.risk_sht_desc,'')) like :riskId " +
        "and lower(sbp.pol_no) like :polNo " +
        "and coalesce(sbc.client_id, -2000) = case when :clientCode=-2000 then coalesce(sbc.client_id,-2000) else :clientCode end " +
        "and lower(clm.clm_no) like :claimNo " +
        "order by clm.clm_date desc OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
List<Object[]> getClaimBookings(@Param("riskId") String riskId,
                                @Param("polNo") String polNo,
                                @Param("clientCode") Long clientCode,
                                @Param("claimNo") String claimNo,
                                @Param("pageNo") int pageNo,
                                @Param("limit") int limit);



    @Query(value = "select clm.clm_id, clm.clm_no, concat(sbc.client_fname, ' ', coalesce(sbc.client_onames, '')) insured, clm.clm_loss_desc, cs.ca_desc, clm.clm_loss_date, " +
            "clm.clm_next_rvw_dt, clm.clm_booked_dt, clm.clm_date, clm.clm_liability_adm, sbp.pol_no, " +
            "concat(sbc2.client_fname, ' ', coalesce(sbc2.client_onames, '')) client, sbp2.pr_desc, sbr.risk_sht_desc, sbr.risk_sum_insur_amt, " +
            "sbr.risk_wef_date, sbr.risk_wet_date, sbr.risk_binder_id, sbp.pol_binder_id, clm.clm_status, clm.clm_risk_id, clm.approval_status, " +
            "clm.risk_identifier, sbu.user_name, clm.balance_approval_date, " +
            "(select act2.clm_act_insurer_ref from sys_brk_clm_activities act2 " +
            " where act2.clm_act_clm_id = clm.clm_id and act2.clm_act_insurer_ref is not null " +
            " order by act2.clm_act_dt desc limit 1) as insurer_ref, " +  // Latest insurer ref
            "ins.acct_name as insurer_name, " +
            "(select coalesce(sum(case when trans_dc = 'D' then trans_balance end), 0) from sys_brk_main_transactions where trans_pol_id = sbp.pol_id) as ins_balance, " +
            "(select coalesce(sum(case when trans_dc = 'C' then trans_balance end) * -1, 0) from sys_brk_main_transactions where trans_pol_id = sbp.pol_id) as client_balance " +
            "from sys_brk_clm_bookings clm " +
            "left join sys_brk_risks sbr on clm.clm_risk_id = sbr.risk_id " +
            "left join sys_brk_clients sbc on sbc.client_id = sbr.risk_insured_id " +
            "left join sys_brk_causations cs on cs.ca_id = clm.clm_status_id " +
            "left join sys_brk_policies sbp on sbp.pol_id = sbr.risk_pol_id " +
            "left join sys_brk_clients sbc2 on sbc2.client_id = sbp.pol_client_id " +
            "left join sys_brk_products sbp2 on sbp2.pr_code = sbp.pol_prod_id " +
            "left join sys_brk_accounts ins on sbp.pol_agent_id = ins.acct_id " +
            "left join sys_brk_users sbu on clm.balance_approved_by = sbu.user_id " +
            "where clm.clm_id = :clmId", nativeQuery = true)
    List<Object[]> getClaimDetails(@Param("clmId") Long clmId);

    @Query(value = "SELECT clm_id, clm_no, event_type, event_date, event_user_id, u.user_name as username, event_description, " +
            "maker_made_on, checker_checked_on, action, rejected_reason, resubmission_comment, " +
            "COUNT(*) OVER() as total_rows " +
            "FROM (" +
            "SELECT * FROM (" +
            "    SELECT cb.clm_id, cb.clm_no, CAST('CLAIM_CREATED' AS TEXT) as event_type, " +
            "           cb.clm_booked_dt as event_date, cb.clm_booked_by as event_user_id, " +
            "           CAST('Claim created' AS TEXT) as event_description, " +
            "           CAST(null AS TIMESTAMP) as maker_made_on, CAST(null AS TIMESTAMP) as checker_checked_on, " +
            "           CAST(null AS TEXT) as action, CAST(null AS TEXT) as rejected_reason, " +
            "           CAST(null AS TEXT) as resubmission_comment " +
            "    FROM sys_brk_clm_bookings cb WHERE cb.clm_id = :clmId " +

            "    UNION ALL " +

            "    SELECT cb.clm_id, cb.clm_no, " +
            "           CASE WHEN (SELECT COUNT(*) FROM sys_brk_maker_checker mc2 " +
            "                      WHERE mc2.maker_task_code = cb.clm_id AND mc2.maker_task_type = 'CL' " +
            "                      AND mc2.made_on_date < mc.made_on_date) = 0 " +
            "                THEN CAST('INITIAL_SUBMISSION_TO_CHECKER' AS TEXT) " +
            "                ELSE CAST('RESUBMISSION' AS TEXT) END as event_type, " +
            "           mc.made_on_date, mc.maker_id, " +
            "           CASE WHEN (SELECT COUNT(*) FROM sys_brk_maker_checker mc2 " +
            "                      WHERE mc2.maker_task_code = cb.clm_id AND mc2.maker_task_type = 'CL' " +
            "                      AND mc2.made_on_date < mc.made_on_date) = 0 " +
            "                THEN CAST('Initial submission to Checker' AS TEXT) " +
            "                ELSE CAST(CONCAT('Resubmitted after changes: ', COALESCE(mc.resubmission_comment, 'No comment')) AS TEXT) END as event_description, " +
            "           mc.made_on_date, CAST(null AS TIMESTAMP), CAST('SUBMITTED' AS TEXT), CAST(null AS TEXT), " +
            "           mc.resubmission_comment " +
            "    FROM sys_brk_maker_checker mc " +
            "    JOIN sys_brk_clm_bookings cb ON mc.maker_task_code = cb.clm_id " +
            "    WHERE mc.maker_task_type = 'CL' AND cb.clm_id = :clmId " +

            "    UNION ALL " +

            "    SELECT cb.clm_id, cb.clm_no, " +
            "           CASE WHEN mc.check_status = 'A' THEN CAST('CLAIM_APPROVED' AS TEXT) " +
            "                WHEN mc.check_status = 'R' THEN CAST('CLAIM_REJECTED' AS TEXT) END as event_type, " +
            "           mc.check_on_date, mc.checker_id, " +
            "           CASE WHEN mc.check_status = 'A' THEN CAST('Claim approved by checker' AS TEXT) " +
            "                WHEN mc.check_status = 'R' THEN CAST('Claim rejected by checker' AS TEXT) END as event_description, " +
            "           mc.made_on_date, mc.check_on_date, mc.check_status, mc.rejected_reason, " +
            "           CAST(null AS TEXT) as resubmission_comment " +
            "    FROM sys_brk_maker_checker mc " +
            "    JOIN sys_brk_clm_bookings cb ON mc.maker_task_code = cb.clm_id " +
            "    WHERE mc.maker_task_type = 'CL' AND cb.clm_id = :clmId AND mc.check_on_date IS NOT NULL " +

            "    UNION ALL " +

            "    SELECT docs.clm_reqrd_clm_id, cb.clm_no, " +
            "           CASE WHEN EXISTS(SELECT 1 FROM sys_brk_maker_checker mc " +
            "                           WHERE mc.maker_task_code = cb.clm_id AND mc.maker_task_type = 'CL' " +
            "                           AND mc.check_status = 'R' AND mc.check_on_date < docs.clm_reqd_dt_received) " +
            "                THEN CAST('DOCUMENT_UPLOADED_POST_REJECTION' AS TEXT) " +
            "                ELSE CAST('DOCUMENT_UPLOADED' AS TEXT) END as event_type, " +
            "           docs.clm_reqd_dt_received, docs.clm_reqrd_user_id, " +
            "           CAST('Document uploaded' AS TEXT), " +
            "           CAST(null AS TIMESTAMP), CAST(null AS TIMESTAMP), CAST(null AS TEXT), CAST(null AS TEXT), " +
            "           CAST(null AS TEXT) as resubmission_comment " +
            "    FROM sys_brk_clm_req_docs docs " +
            "    JOIN sys_brk_clm_bookings cb ON docs.clm_reqrd_clm_id = cb.clm_id " +
            "    WHERE docs.clm_reqrd_clm_id = :clmId AND docs.clm_reqd_dt_received IS NOT NULL " +

            "    ) audit_events ORDER BY event_date DESC" +
            ") audit_logs " +
            "LEFT JOIN sys_brk_users u ON audit_logs.event_user_id = u.user_id " +
            "WHERE event_description LIKE :search " +
            "ORDER BY event_date DESC " +
            "OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> getClaimAuditLogs(@Param("clmId") Long clmId,
                                     @Param("pageNo") int pageNo,
                                     @Param("search") String search,
                                     @Param("limit") int limit);

}

