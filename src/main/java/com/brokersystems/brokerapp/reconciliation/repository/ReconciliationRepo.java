package com.brokersystems.brokerapp.reconciliation.repository;

import com.brokersystems.brokerapp.reconciliation.Reconciliation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Repository
public interface ReconciliationRepo extends PagingAndSortingRepository<Reconciliation, Long> {
    @Query(value = "SELECT d.recon_data_pol_no, d.recon_data_pol_client, d.recon_data_pol_risk_note_no, d.recon_data_pol_underwriter_no, d.recon_data_pol_prem, d.recon_data_pol_comm, d.recon_data_pol_paid_comm, d.recon_data_pol_total_comm, r.recon_status, r.recon_unmatched_records, COUNT(r) " +
            "FROM sys_brk_reconciliation r " +
            "inner join sys_brk_reconciliation_data d ON r.recon_data_id = d.recon_data_id " +
            "left join sys_brk_policies sbp on d.recon_data_pol_no = sbp.pol_no " +
            "left join sys_brk_accounts sba on sbp.pol_agent_id = sba.acct_id " +
            "WHERE (LOWER(d.recon_data_pol_client) LIKE LOWER(:search)\n" +
            "    OR LOWER(d.recon_data_pol_no) LIKE LOWER(:search)\n" +
            "    OR LOWER(d.recon_data_pol_risk_note_no) LIKE LOWER(:search)\n" +
            "    OR LOWER(d.recon_data_pol_underwriter_no) LIKE LOWER(:search)) " +
            "AND (r.recon_date BETWEEN :dateFrom AND :dateTo) " +
            "AND (sba.acct_id = :accountCode OR :accountCode IS NULL) " +
            "GROUP BY r.recon_unmatched_records, d.recon_data_pol_no, d.recon_data_pol_client, d.recon_data_pol_risk_note_no, d.recon_data_pol_underwriter_no, d.recon_data_pol_prem, d.recon_data_pol_comm, d.recon_data_pol_paid_comm, d.recon_data_pol_total_comm, r.recon_status " +
            "ORDER BY d.recon_data_pol_no DESC " +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findReconciledData(@Param("search") String search,
                                      @Param("dateFrom") Date dateFrom,
                                      @Param("dateTo") Date dateTo,
                                      @Param("accountCode") Long accountCode,
                                      @Param("pageNo") int pageNo,
                                      @Param("limit") int limit);
    @Query(value = "WITH pa_trans_numbers AS (\n" +
            "    SELECT DISTINCT pa_trans_no\n" +
            "    FROM sys_brk_payment_audit\n" +
            "    WHERE pa_other_trans = :transNo\n" +
            "    and pa_posted = 'Y'\n" +
            ")\n" +
            "SELECT \n" +
            "    sbt.trans_ref_no AS refNo,\n" +
            "    sbp.pol_no AS clientPolNo,\n" +
            "    sbc.client_fname AS fname,\n" +
            "    sbc.client_onames AS otherNames,\n" +
            "    sbt.trans_control_acc AS controlAcc,\n" +
            "    sbpr.pr_desc AS proDesc,\n" +
            "    sbp.pol_basic_premium_amt AS paymentAmount,\n" +
            "    pa.pa_amount AS commAmount,\n" +
            "    sbt.trans_whtx AS whtxAmount,\n" +
            "    pa.pa_trans_no,\n" +
            "    pa.pa_posted,\n" +
            "    sbt.trans_balance \n" +
            "FROM sys_brk_main_transactions sbt\n" +
            "JOIN pa_trans_numbers ptn ON sbt.trans_no = ptn.pa_trans_no\n" +
            "JOIN sys_brk_policies sbp ON sbp.pol_id = sbt.trans_pol_id\n" +
            "JOIN sys_brk_clients sbc ON sbc.client_id = sbp.pol_client_id\n" +
            "JOIN sys_brk_products sbpr ON sbpr.pr_code = sbp.pol_prod_id\n" +
            "JOIN sys_brk_payment_audit pa ON pa.pa_trans_no = sbt.trans_no;", nativeQuery = true)
    List<Object[]> findChildTrans(@Param("transNo") Long transNo);

    @Query(value = "select trans_no from sys_brk_main_transactions sbmt where trans_type = 'COM'", nativeQuery = true)
    List<BigInteger>getMainTrans();
    @Query(value = "select pa_other_trans from sys_brk_payment_audit where pa_other_trans = :transNo", nativeQuery = true)
    List<BigInteger> getAudits(@Param("transNo") Long transNo);

}
