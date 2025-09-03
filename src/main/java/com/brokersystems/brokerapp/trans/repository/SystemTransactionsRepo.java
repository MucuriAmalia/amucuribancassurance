package com.brokersystems.brokerapp.trans.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;


import com.brokersystems.brokerapp.trans.model.SystemTransactions;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface SystemTransactionsRepo extends PagingAndSortingRepository<SystemTransactions, Long>, QueryDslPredicateExecutor<SystemTransactions> {

    String query = "select pol_no,trans_ref_no,client_fname,client_onames,pol_comm_amt,trans_settle_amt ,pol_sub_agent_amt,trans_balance,pol_sub_agent_id \n" +
            "from sys_brk_main_transactions join sys_brk_policies on sys_brk_main_transactions.trans_pol_id = sys_brk_policies.pol_id\n" +
            "join sys_brk_accounts on sys_brk_policies.pol_sub_agent_id = sys_brk_accounts.acct_id\n" +
            "join sys_brk_clients on sys_brk_policies.pol_client_id = sys_brk_clients.client_id\n" +
            "where trans_agent_code in (select pol_sub_agent_id\n" +
            "from sys_brk_policies join  sys_brk_main_transactions on pol_id = trans_pol_id\n" +
            "where pol_auth_status = 'A'\n" +
            "and pol_sub_agent_id is not null\n" +
            "and trans_auth_date between :wef and :wet\n" +
            "and trans_clnt_type='C'\n" +
            "and trans_type = 'RC'\n" +
            "group by  pol_sub_agent_id\n" +
            "having  abs(sum(trans_settle_amt)) >=100)\n" +
            "and trans_auth_date between :wef and :wet\n" +
            "and  pol_sub_agent_id = case when :agent=-2000 then pol_sub_agent_id else :agent end \n" +
            " and trans_ref_no not in (select pa.pa_settle_dr_ref_no from sys_brk_payment_audit pa where pa.pa_trans_type = 'SAG')\n"+
            "and sys_brk_accounts.acct_sub_account = :subacct";
    @Query(value =query, nativeQuery = true)
    List<Object[]>   getSubAgentTrans(@Param("agent")Long acctId, @Param("wef") Date wef, @Param("wet")Date wet, @Param("subacct")Long subacct);



    String querysqlServer = "select pol_no,trans_ref_no,client_fname,client_onames,pol_comm_amt,trans_settle_amt ,pol_sub_agent_amt,trans_balance,pol_sub_agent_id \n" +
            "from sys_brk_main_transactions join sys_brk_policies on sys_brk_main_transactions.trans_pol_id = sys_brk_policies.pol_id\n" +
            "join sys_brk_accounts on sys_brk_policies.pol_sub_agent_id = sys_brk_accounts.acct_id\n" +
            "join sys_brk_clients on sys_brk_policies.pol_client_id = sys_brk_clients.client_id\n" +
            "where trans_agent_code in (select pol_sub_agent_id\n" +
            "from sys_brk_policies join  sys_brk_main_transactions on pol_id = trans_pol_id\n" +
            "where pol_auth_status = 'A'\n" +
            "and pol_sub_agent_id is not null\n" +
            "and trans_auth_date between :wef and :wet\n" +
            "and trans_clnt_type='C'\n" +
            "and trans_type = 'RC'\n" +
            "group by  pol_sub_agent_id\n" +
            "having  abs(sum(trans_settle_amt)) >=100)\n" +
            "and trans_auth_date between :wef and :wet\n" +
            "and   pol_sub_agent_id = coalesce(:agent,pol_sub_agent_id)\n" +
            " and trans_ref_no not in (select pa.pa_settle_dr_ref_no from sys_brk_payment_audit pa where pa.pa_trans_type = 'SAG')\n"+
            "and sys_brk_accounts.acct_sub_account = :subacct";

    @Query(value =querysqlServer, nativeQuery = true)
    List<Object[]> getSqlServerSubAgentTrans(@Param("agent")Long acctId, @Param("wef") Date wef, @Param("wet")Date wet, @Param("subacct")Long subacct);

    @Query(value = "select *, COUNT(*) OVER() as total_rows from(\n" +
            "select trans_no                               transno,\n" +
            "                               trans_date             transDate,\n" +
            "                               trans_origin                           origin,\n" +
            "                               concat(c.client_fname , ' ' , c.client_onames) client,\n" +
            "                               a.acct_name                            agent,\n" +
            "                               trans_control_acc                      controlAcc,\n" +
            "                               trans_ref_no                           refNo,\n" +
            "                               b.ob_name                              branch,\n" +
            "                               trans_type                             transType,\n" +
            "                               trans_dc                               transdc,\n" +
            "                               trans_amount                           amount,\n" +
            "                               trans_net_amt                          netAmount,\n" +
            "                               trans_balance                          balance,\n" +
            "                               p.pol_no                               polNo,\n" +
            "                               trans_payee                            payeeName\n" +
            "                        from sys_brk_main_transactions sbmt\n" +
            "                               left join sys_brk_policies p on sbmt.trans_pol_id = p.pol_id\n" +
            "                               inner join sys_brk_accounts a on sbmt.trans_agent_code = a.acct_id\n" +
            "                               left outer join sys_brk_branches b on sbmt.trans_brn_id = b.ob_id\n" +
            "                               left outer join sys_brk_clients c on sbmt.trans_clnt_code = c.client_id\n" +
            "                        where (trans_authorised is null or trans_authorised = 'N')\n" +
            "                          and trans_type not in ('RFC', 'RFD')\n" +
            "                          union ALL \n" +
            "                           select trans_no                  transno,\n" +
            "                               max(cast(sbcpt.trans_date as date)) transDate,\n" +
            "                               'COMM'                        origin,\n" +
            "                               ''                            client,\n" +
            "                               a.acct_name                   agent,\n" +
            "                               a.acct_sht_desc               controlAcc,\n" +
            "                               trans_receipt_no              refNo,\n" +
            "                               sbb.ob_name                   branch,\n" +
            "                               'CP'                          transType,\n" +
            "                               'C'                           transdc,\n" +
            "                               SUM(sbcpt.trans_amount)             amount,\n" +
            "                               R.receipt_amount              netAmount,\n" +
            "                                0                             balance,\n" +
            "                               ''                            polNo,\n" +
            "                               org.org_sht_desc              payeeName\n" +
            "                        from sys_brk_organization org,\n" +
            "                             sys_brk_comm_process_trans sbcpt\n" +
            "                               inner join sys_brk_accounts a ON a.acct_id = sbcpt.trans_insurance\n" +
            "                               inner join sys_brk_receipts r on sbcpt.trans_receipt_no = r.receipt_no\n" +
            "                               inner join sys_brk_branches sbb on r.receipt_brn_code = sbb.ob_id\n" +
            "                               inner join sys_brk_main_transactions sbmt2 on trans_ref_no = r.receipt_no\n" +
            "                        where trans_status = 'R'\n" +
            "                        group by trans_no, a.acct_name, a.acct_sht_desc, trans_receipt_no, sbb.ob_name, R.receipt_amount,\n" +
            "                                 org.org_sht_desc ) as result order by transDate desc\n" +
            "OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findAuthTransactions(@Param("pageNo") int pageNo,
                                         @Param("limit") int limit);

    @Query(value = "select trans_pol_id from sys_brk_main_transactions where trans_no=:transNo",nativeQuery = true)
    BigInteger getPolicyId(@Param("transNo") Long transNo);

    @Query(value = "SELECT trans_no,trans_date,trans_type,trans_clnt_code,\n" +
                    "trans_amount,trans_net_amt,trans_agent_code,\n " +
                    "trans_posted_dt,trans_pol_id,trans_narrations,trans_auth_date,\n"+
                    "trans_payee\n"+
                    "FROM sys_brk_main_transactions WHERE trans_no = :tranNo", nativeQuery = true)
    SystemTransactions findTransactionByTranNo(@Param("tranNo") Long tranNo);

    SystemTransactions findByTransno(Long transno);


    @Query(value = "SELECT trans_ref_no FROM sys_brk_main_transactions " +
            "JOIN sys_brk_policies ON sys_brk_main_transactions.trans_pol_id = sys_brk_policies.pol_id " +
            "WHERE pol_no = :policyNumber " +
            "ORDER BY trans_date DESC", nativeQuery = true)
    String getRefNo(@Param("policyNumber") String policyNumber);

    //    @Query("SELECT st FROM SystemTransaction st WHERE :clientType IS NULL OR st.clientType = :clientType")
    //    Page<SystemTransactions> findByClientType(@Param("clientType") String clientType, Pageable pageable);

    @Query(value = "select trans_no \n" +
            "from sys_brk_main_transactions pol join sys_brk_policies sbp on pol.trans_pol_id =sbp.pol_id " +
            "where trans_clnt_type='C' and trans_balance >= 0 and trans_authorised = 'Y' and trans_dc = 'D' AND trans_pol_id = :polId", nativeQuery = true)
    List<BigInteger> findBulkUploadReceiptItem(@Param("polId") Long polId);

    @Query(value = "select trans_net_amt,pol.trans_amount,pol.trans_curr_id \n" +
            "from sys_brk_main_transactions pol join sys_brk_policies sbp on pol.trans_pol_id =sbp.pol_id" +
            " where trans_ref_no=:debitRef and trans_type=:transType and trans_clnt_type=:clientType and trans_dc=:debitCredit",nativeQuery = true)
    List<Object[]> getAllocationTrans(@Param("debitRef") String debitRef, @Param("transType") String transType,
                                      @Param("clientType") String clientType,
                                      @Param("debitCredit") String debitCredit);

    @Query(value = "select * from sys_brk_main_transactions where trans_lf_endors_no=:endorseNo",nativeQuery = true)
    SystemTransactions getTransactions(@Param("endorseNo") String endorseNo);

    @Query(value = "select trans_no  from sys_brk_main_transactions sbmt where trans_ref_no =:transNo and trans_clnt_type ='A'\n" +
            "and trans_narrations ='Posting Agent Credit Note'",nativeQuery = true)
    BigInteger getAgentTransaction(@Param("transNo") String transNo);

    @Query(value = "select trans_no  from sys_brk_main_transactions sbmt where trans_ref_no =:transNo and trans_clnt_type ='A'\n" +
            "and trans_narrations ='POLICY INSTALMENT'",nativeQuery = true)
    BigInteger getAgentLTransaction(@Param("transNo") String transNo);


    @Query(value = "select sum(trans_amount) from sys_brk_main_transactions join sys_brk_policies sbp on trans_pol_id = pol_id " +
            " and pol_no=:policyNo " +
            " and trans_type='SAG' and trans_authorised ='Y' and coalesce(trans_balance,0) <> 0", nativeQuery = true)
    BigDecimal getTotalSubAgentFee(@Param("policyNo") String policyNo);

    @Query(value = "select sum(trans_amount) from sys_brk_main_transactions join sys_brk_policies sbp on trans_pol_id = pol_id " +
            " and pol_no=:policyNo and trans_authorised ='Y' " +
            " and trans_clnt_type ='C' " +
            " and trans_dc ='D'", nativeQuery = true)
    BigDecimal getTotalDebitAmt(@Param("policyNo") String policyNo);

    @Query(value = "select sbmt from sys_brk_main_transactions sbmt where sbmt.trans_clnt_type = 'C' and " +
            "sbmt.trans_dc = 'D' and sbmt.trans_pol_id = :policyId", nativeQuery = true)
    Optional<SystemTransactions> findByPolicyIdAndClientTypeAndTransdc(@Param("policyId") Long policyId);

}
