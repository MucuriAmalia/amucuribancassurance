package com.brokersystems.brokerapp.trans.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brokersystems.brokerapp.trans.model.ReceiptTrans;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;


public interface ReceiptRepository extends  PagingAndSortingRepository<ReceiptTrans, Long>, QueryDslPredicateExecutor<ReceiptTrans> {

    @Query("select t from ReceiptTrans t where t.receiptId=:receiptId")
    ReceiptTrans getReceiptDetails(@Param("receiptId")Long receiptId);

    @Query(value = "select distinct rect_receipt_no  from sys_brk_receipt_dtls join sys_brk_receipts on sys_brk_receipt_dtls.rect_receipt_no=sys_brk_receipts.receipt_id where rect_pol_id =:polId and coalesce(receipt_cancelled,'N') = 'N'",nativeQuery = true)
    List<BigInteger> getLifePolReceipts(@Param("polId") Long polId);




    @Query(value = "SELECT x.*, COUNT(*) OVER() AS total_rows FROM (" +
            "    SELECT trans_no                                        AS transno, " +
            "           null                                             AS transTempNo, " +
            "           trans_date                                       AS transDate, " +
            "           concat(c.client_fname, ' ', c.client_onames)     AS client, " +
            "           trans_control_acc                                AS controlAcc, " +
            "           trans_ref_no                                     AS refNo, " +
            "           trans_type                                       AS transType, " +
            "           trans_balance                                    AS balance, " +
            "           p.pol_no                                         AS polNo " +
            "    FROM sys_brk_main_transactions sbmt " +
            "         JOIN sys_brk_policies p ON sbmt.trans_pol_id = p.pol_id " +
            "         JOIN sys_brk_clients c ON p.pol_client_id = c.client_id " +
            "    WHERE sbmt.trans_dc = 'D' " +
            "      AND p.pol_agent_id = :agentId " +
            "      AND sbmt.trans_clnt_type = 'C' " +
            "      AND sbmt.trans_type != 'SAG' " +
            "      AND sbmt.trans_balance > 0 " +
            "      AND (sbmt.trans_ref_no LIKE coalesce(:search, sbmt.trans_ref_no) " +
            "           OR p.pol_no LIKE coalesce(:search, p.pol_no) " +
            "           OR p.pol_ref_no LIKE coalesce(:search, p.pol_ref_no) " +
            "           OR p.pol_rev_no LIKE coalesce(:search, p.pol_rev_no)) " +

            "    UNION ALL " +

            "    SELECT null                                             AS transno, " +
            "           trans_no                                         AS transTempNo, " +
            "           trans_date                                       AS transDate, " +
            "           concat(c.client_fname, ' ', c.client_onames)     AS client, " +
            "           trans_control_acc                                AS controlAcc, " +
            "           trans_ref_no                                     AS refNo, " +
            "           trans_type                                       AS transType, " +
            "           trans_balance                                    AS balance, " +
            "           p.pol_no                                         AS polNo " +
            "    FROM sys_brk_temp_transactions sbmt " +
            "         INNER JOIN sys_brk_policies p ON sbmt.trans_pol_id = p.pol_id " +
            "         INNER JOIN sys_brk_risks s2 ON p.pol_id = s2.risk_pol_id " +
            "         INNER JOIN sys_brk_clients c ON p.pol_client_id = c.client_id " +
            "    WHERE sbmt.trans_dc = 'D' " +
            "      AND p.pol_agent_id = :agentId " +
            "      AND sbmt.trans_clnt_type = 'C' " +
            "      AND sbmt.trans_type != 'SAG' " +
            "      AND sbmt.trans_balance > 0 " +
            "      AND (sbmt.trans_ref_no LIKE coalesce(:search, sbmt.trans_ref_no) " +
            "           OR p.pol_no LIKE coalesce(:search, p.pol_no) " +
            "           OR p.pol_ref_no LIKE coalesce(:search, p.pol_ref_no) " +
            "           OR p.pol_rev_no LIKE coalesce(:search, p.pol_rev_no)) " +
            ") x " +
            "ORDER BY x.transDate DESC " +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findDebitTransactions(@Param("search") String search,
                                    @Param("pageNo") int pageNo,
                                    @Param("agentId") Long agentId,
                                    @Param("limit") int limit);

    @Query(value = "select sbr.receipt_no,sbr.receipt_date,rf.lrct_dc ,sbr.receipt_amount,rf.lrct_alloc_amt,rf.lrct_balance,sbr.receipt_id,rf.lrct_id," +
            "sblra.alloc_comm_amt, sblra.alloc_subagent_comm_amt, sblra.alloc_marketer_comm_amt,sblra.alloc_life_whtx_amt, COUNT(*) OVER() as total_rows  from sys_brk_life_rcts rf\n" +
            "join sys_brk_receipts sbr on sbr.receipt_id =rf.lrct_receipt_id  \n" +
            "left join sys_brk_life_rct_allocs sblra on rf.lrct_id = sblra.alloc_lrct_id " +
            "where lrct_policy_id =:polId\n" +
            "order by 1 desc OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findPolicyReceipts(@Param("polId") Long polId,
                                      @Param("pageNo") int pageNo,
                                      @Param("limit") int limit);

    @Query(value = "SELECT pa_trans_no \n" +
            "FROM sys_brk_payment_audit\n" +
            "WHERE pa_other_trans = :pa_other_trans", nativeQuery = true)
    List<BigInteger> getTransId(@Param("pa_other_trans") Long transNo);

    @Query(value = "select pol_no,pol_cover_to,client_fname,client_onames,pol_basic_premium_amt,pol_business_type, sum(coalesce(rd.rect_amount, 0)) as total_receipts, sum(coalesce(li.install_prem,0)) as total_life_inst from sys_brk_policies p " +
            "inner join sys_brk_clients c on p.pol_client_id = c.client_id " +
            "left join sys_brk_receipt_dtls rd on p.pol_id = rd.rect_pol_id " +
            "left join sys_brk_receipts r on rd.rect_receipt_no = r.receipt_id " +
            "left join sys_brk_life_rcts lr on rd.rect_receipt_no = lr.lrct_receipt_id " +
            "left join sys_brk_life_installments li on p.pol_id = li.lrct_policy_id " +
            "where(lower(pol_no) like lower(:search) or lower(client_fname) like lower(:search) or lower(client_onames) like lower(:search)) " +
            "and ((pol_wet_date between :dateFrom and :dateTo) or (pol_wef_date between :dateFrom and :dateTo) " +
            "or (r.receipt_date between :dateFrom and :dateTo) or (lr.receipt_date between :dateFrom and :dateTo))" +
            "and (pol_agent_id = :accountCode or :accountCode is null)" +
            "group by client_onames, client_fname, pol_no, pol_basic_premium_amt, pol_business_type, pol_cover_to " +
            "having (sum(coalesce(rd.rect_amount,0)) - coalesce(p.pol_basic_premium_amt,0)) >= 1 " +
            "or (sum(coalesce(rd.rect_amount,0)) - coalesce(sum(coalesce(li.install_prem,0)),0)) >= 1 " +
            "order by pol_no desc " +
            "offset :pageNo * :limit limit :limit", nativeQuery = true)
    List<Object[]> findOverpaidPolicies(@Param("search") String search,
                                        @Param("dateFrom") Date dateFrom,
                                        @Param("dateTo") Date dateTo,
                                        @Param("accountCode") Long accountCode,
                                        @Param("pageNo") int pageNo,
                                        @Param("limit") int limit);

    @Query(value = "select pol_basic_premium_amt, sum(coalesce(rd.rect_amount, 0)) as total_receipts, sum(coalesce(li.install_prem,0)) as total_life_inst from sys_brk_policies p " +
            "inner join sys_brk_clients c on p.pol_client_id = c.client_id " +
            "left join sys_brk_receipt_dtls rd on p.pol_id = rd.rect_pol_id " +
            "left join sys_brk_receipts r on rd.rect_receipt_no = r.receipt_id " +
            "left join sys_brk_life_rcts lr on rd.rect_receipt_no = lr.lrct_receipt_id " +
            "left join sys_brk_life_installments li on p.pol_id = li.lrct_policy_id " +
            "where pol_id = :polId " +
            "group by pol_basic_premium_amt ",nativeQuery = true)
    List<Object[]> findPremiumSettlementAndBalance(@Param("polId") Long polId);
}
