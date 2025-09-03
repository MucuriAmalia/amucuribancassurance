package com.brokersystems.brokerapp.trans.repository;

import com.brokersystems.brokerapp.trans.model.ReceiptSettlementDetails;
import com.mysema.query.types.expr.BooleanExpression;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;


public interface SettlementRepo extends  PagingAndSortingRepository<ReceiptSettlementDetails, Long>, QueryDslPredicateExecutor<ReceiptSettlementDetails> {

    String postgresQuery = "select x.* from (SELECT pol_no,pol_client_pol_no,client_fname,client_onames,trans.trans_ref_no,trans.rtransrefno,balances.trans_auth_date,\n" +
            "              case  balances.trans_balance\n" +
            "              when 0 then\n" +
            "                'FULL'\n" +
            "              else\n" +
            "                'PARTIAL'\n" +
            "                end as pay_status,balances.trans_net_amt,\n" +
            "              trans.trans_comm *  (rsettledamount/balances.trans_net_amt) as commission ,\n" +
            "              trans.trans_whtx *  (rsettledamount/balances.trans_net_amt) as whtx,trans.rsettledamount,balances.trans_balance,trans.trans_settle_amt,\n" +
            "              balances.trans_pol_id,'NML'\n" +
            "            from (select A.trans_type,A.trans_dc,A.trans_ref_no,A.trans_balance ,A.trans_comm, A.trans_whtx,receipt.*,A.trans_agent_code,A.trans_settle_amt\n" +
            "                  from sys_brk_main_transactions A,\n" +
            "            (SELECT replace(rec_settle_cr_ref,'/CN','')rtransrefno ,a.trans_pol_id POLID, b.trans_auth_date transauthdate,\n" +
            "                                        SUM(CASE WHEN b.trans_dc= 'C' THEN 1*rec_alloc_amt ELSE -1*rec_alloc_amt END) rsettledamount\n" +
            "                                      FROM sys_receipts_settlements,sys_brk_main_transactions a , sys_brk_main_transactions b\n" +
            "                                      WHERE rec_settle_dr = a.trans_no\n" +
            "                                            and rec_settle_cr =b.trans_no\n" +
            "                                            and B.trans_clnt_type = 'C'\n" +
            "                                            and B.trans_type not in ('NBD','APD','RND')\n" +
            "                                            and  B.trans_type='RC'\n" +
            "                                            and B.trans_ref_no not in (select  receipt_no from sys_brk_receipts where coalesce(receipt_direct,'N')   = 'Y')\n" +
            "                                      group by replace(rec_settle_cr_ref,'/CN','') ,a.trans_pol_id,b.trans_auth_date \n" +
            "            union \n" +
            "             SELECT replace(rec_settle_cr_ref,'/CN','')rtransrefno ,a.trans_pol_id POLID, b.trans_auth_date transauthdate,\n" +
            "                    SUM(CASE WHEN b.trans_dc= 'C' THEN -1*rec_alloc_amt ELSE 1*rec_alloc_amt END) rsettledamount\n" +
            "             FROM sys_receipts_settlements,sys_brk_main_transactions a , sys_brk_main_transactions b\n" +
            "             WHERE rec_settle_dr = a.trans_no\n" +
            "                   and rec_settle_cr =b.trans_no\n" +
            "                   and B.trans_clnt_type = 'C'\n" +
            "                   and B.trans_type not in ('NBD','APD','RND')\n" +
            "                   and  B.trans_type ='APC'\n" +
            "                   and B.trans_ref_no not in (select  receipt_no from sys_brk_receipts where coalesce(receipt_direct,'N')   = 'Y')\n" +
            "             group by replace(rec_settle_cr_ref,'/CN','') ,a.trans_pol_id,b.trans_auth_date) receipt \n" +
            "                  where (A.trans_balance < 0 OR (A.trans_balance > 0 AND A.trans_type IN ('APC') ))\n" +
            "                        and A.trans_type not in ('SAG')\n" +
            "                        and A.trans_clnt_type = 'A'\n" +
            "                      --  and trans_pol_id = 305\n" +
            "                        and a.trans_pol_id = receipt.POLID)trans,\n" +
            "              sys_brk_policies,sys_brk_clients,(select * from sys_brk_main_transactions where\n" +
            "                trans_clnt_type = 'C'  and  trans_type in ('NBD','APD','RND','APC') ) balances \n" +
            "            where trans.polid=pol_id\n" +
            "                  and pol_client_id = client_id\n" +
            "                  and balances.trans_pol_id = trans.polid\n" +
            "                  and trans.rsettledamount <> 0\n" +
            "                  and trans.trans_agent_code = :acctId\n" +
            "                  and transauthdate between :wef  and :wet   \n" +
            "                   and round(rsettledamount,:round)<> 0  \n" +
            "                  AND rtransrefno NOT IN (SELECT pa_settle_rec_ref_no\n" +
            "                                    FROM sys_brk_payment_audit a\n" +
            "                                           inner join sys_receipts_settlements s on s.rec_settle_id = a.pa_settle_cr_id\n" +
            "                                           inner join sys_brk_main_transactions s1 on s1.trans_no = s.rec_settle_client_cr\n" +
            "                                    WHERE  pa_posted = 'Y' \n" +
            "                                    AND pa_settle_dr_ref_no= trans.trans_ref_no )\n" +
            "                  union\n" +
            "            select pol_no,pol_client_pol_no,client_fname,client_onames,trans_ref_no,trans_ref_no,trans_auth_date,'FULL' as pay_status, trans_net_amt, trans_comm,trans_whtx,trans_settle_amt,trans_balance,trans_settle_amt,trans_pol_id,'CLB' from sys_brk_main_transactions,\n" +
            "              sys_brk_policies,sys_brk_clients\n" +
            "            where trans_clnt_type='A' and trans_dc='D' and trans_type='AGR'\n" +
            "                  and trans_pol_id = pol_id\n" +
            "                  and pol_client_id= client_id\n" +
            "                  AND trans_agent_code = :acctId\n" +
            "                  AND trans_balance > 0\n" +
            "                  and trans_auth_date between :wef  and :wet\n" +
            "                  AND trans_ref_no NOT IN (SELECT  pa_settle_dr_ref_no FROM sys_brk_payment_audit WHERE pa_settle_rec_ref_no=trans_ref_no))x where x.pay_status=coalesce(CASE WHEN :pstatus = 'ALL' then NULL else :pstatus end, pay_status)";


    @Query(value =postgresQuery, nativeQuery = true)
    List<Object[]> getPostgresSettlementDetails(@Param("acctId")Long acctId, @Param("wef")Date wef, @Param("wet")Date wet,@Param("round")Integer round,
                                                @Param("pstatus") String pstatus);

    @Query(value ="SELECT x.* FROM (SELECT pol_no,pol_client_pol_no,client_fname,client_onames,trans.trans_ref_no,trans.rtransrefno,balances.trans_auth_date,\n" +
            "case  balances.trans_balance\n" +
            "when 0 then\n" +
            "'FULL'\n" +
            "else\n" +
            "'PARTIAL'\n" +
            "\tend as pay_status,balances.trans_net_amt,\n" +
            "\ttrans.trans_comm *  (rsettledamount/balances.trans_net_amt) as commission ,\n" +
            "\ttrans.trans_whtx *  (rsettledamount/balances.trans_net_amt) as whtx,trans.rsettledamount,balances.trans_balance,trans.trans_settle_amt,balances.trans_pol_id,'NML' as ttype\n" +
            "from (select A.trans_type,A.trans_dc,A.trans_ref_no,A.trans_balance ,A.trans_comm, A.trans_whtx,receipt.*,A.trans_agent_code,A.trans_settle_amt\n" +
            "from sys_brk_main_transactions A,\n" +
            "(SELECT replace(rec_settle_cr_ref,'/CN','')rtransrefno ,a.trans_pol_id POLID, b.trans_auth_date transauthdate,\n" +
            "                            SUM(CASE WHEN b.trans_dc= 'C' THEN 1*rec_alloc_amt ELSE -1*rec_alloc_amt END) rsettledamount\n" +
            "                          FROM sys_receipts_settlements,sys_brk_main_transactions a , sys_brk_main_transactions b\n" +
            "                          WHERE rec_settle_dr = a.trans_no\n" +
            "                                and rec_settle_cr =b.trans_no\n" +
            "                                and B.trans_clnt_type = 'C'\n" +
            "                                and B.trans_type not in ('NBD','APD','RND')\n" +
            "                                and  B.trans_type='RC'\n" +
            "                                and B.trans_ref_no not in (select  receipt_no from sys_brk_receipts where coalesce(receipt_direct,'N')   = 'Y')\n" +
            "                          group by replace(rec_settle_cr_ref,'/CN','') ,a.trans_pol_id,b.trans_auth_date " +
            " union " +
            "SELECT replace(rec_settle_cr_ref,'/CN','')rtransrefno ,a.trans_pol_id POLID,b.trans_auth_date transauthdate ,\n" +
            "        SUM(CASE WHEN b.trans_dc= 'C' THEN -1*rec_alloc_amt ELSE 1*rec_alloc_amt END) rsettledamount\n" +
            " FROM sys_receipts_settlements,sys_brk_main_transactions a , sys_brk_main_transactions b\n" +
            " WHERE rec_settle_dr = a.trans_no\n" +
            "       and rec_settle_cr =b.trans_no\n" +
            "       and B.trans_clnt_type = 'C'\n" +
            "       and B.trans_type not in ('NBD','APD','RND')\n" +
            "       and  B.trans_type ='APC'\n" +
            "       and B.trans_ref_no not in (select  receipt_no from sys_brk_receipts where coalesce(receipt_direct,'N')   = 'Y')\n" +
            " group by replace(rec_settle_cr_ref,'/CN','') ,a.trans_pol_id,b.trans_auth_date) receipt\n" +
            "where (A.trans_balance < 0 OR (A.trans_balance > 0 AND A.trans_type IN ('APC') )) \n" +
            "and A.trans_type not in ('SAG')\n" +
            "and A.trans_clnt_type = 'A'\n" +
            "and a.trans_pol_id = receipt.POLID)trans,\n" +
            "sys_brk_policies,sys_brk_clients,(select * from sys_brk_main_transactions where \n" +
            " trans_clnt_type = 'C'  and  trans_type in ('NBD','APD','RND','APC') ) balances \n" +
            "where trans.polid=pol_id\n" +
            "and pol_client_id = client_id " +
            "and balances.trans_pol_id = trans.polid\n" +
            "and trans.rsettledamount <> 0\n" +
            "and trans.trans_agent_code = :acctId\n" +
            "and transauthdate between :wef  and :wet  " +
            "and round(rsettledamount,:round)<> 0 " +
            "AND rtransrefno NOT IN (SELECT pa_settle_rec_ref_no\n" +
            "                        FROM sys_brk_payment_audit a\n" +
            "                               inner join sys_receipts_settlements s on s.rec_settle_id = a.pa_settle_cr_id\n" +
            "                               inner join sys_brk_main_transactions s1 on s1.trans_no = s.rec_settle_client_cr\n" +
            "                        WHERE  pa_posted = 'Y'" +
            "                       AND pa_settle_dr_ref_no= trans.trans_ref_no) " +
            "union "+
            "select pol_no,pol_client_pol_no,client_fname,client_onames,trans_ref_no,trans_ref_no,trans_auth_date,'FULL' as pay_status, trans_net_amt, trans_comm,trans_whtx,trans_settle_amt,trans_balance,trans_settle_amt,trans_pol_id,'CLB' as ttype " +
            "from sys_brk_main_transactions,\n" +
            "  sys_brk_policies,sys_brk_clients\n" +
            "where trans_clnt_type='A' and trans_dc='D' and trans_type='AGR'\n" +
            "      and trans_pol_id = pol_id\n" +
            "      and pol_client_id= client_id\n" +
            "      AND trans_agent_code = :acctId\n" +
            "      AND trans_balance > 0\n" +
            "      AND trans_ref_no NOT IN (SELECT  pa_settle_dr_ref_no FROM sys_brk_payment_audit WHERE pa_settle_rec_ref_no=trans_ref_no)\n" +
            "      and trans_auth_date between :wef  and :wet)x where x.pay_status=coalesce(CASE WHEN :pstatus = 'ALL' then NULL else :pstatus end, pay_status) ", nativeQuery = true)
    List<Object[]> getSqlServerSettlementDetails(@Param("acctId")Long acctId, @Param("wef")Date wef, @Param("wet")Date wet,@Param("round")Integer round,
                                                 @Param("pstatus") String pstatus);

    String postgresCommQuery = "select x.* from (SELECT pol_no,pol_client_pol_no,client_fname,client_onames,trans.trans_ref_no,trans.rtransrefno,balances.trans_auth_date,\n" +
            "              case  balances.trans_balance\n" +
            "              when 0 then\n" +
            "                'FULL'\n" +
            "              else\n" +
            "                'PARTIAL'\n" +
            "                end as pay_status,balances.trans_net_amt,\n" +
            "              trans.trans_comm *  (rsettledamount/balances.trans_net_amt) as commission ,\n" +
            "              trans.trans_whtx *  (rsettledamount/balances.trans_net_amt) as whtx,trans.rsettledamount,balances.trans_balance,trans.trans_settle_amt,\n" +
            "              balances.trans_pol_id,'NML'\n" +
            "            from (select A.trans_type,A.trans_dc,A.trans_ref_no,A.trans_balance ,A.trans_comm, A.trans_whtx,receipt.*,A.trans_agent_code,A.trans_settle_amt\n" +
            "                  from sys_brk_main_transactions A,\n" +
            "            (SELECT replace(rec_settle_cr_ref,'/CN','')rtransrefno ,a.trans_pol_id POLID, b.trans_auth_date transauthdate,\n" +
            "                                        SUM(CASE WHEN b.trans_dc= 'C' THEN 1*rec_alloc_amt ELSE -1*rec_alloc_amt END) rsettledamount\n" +
            "                                      FROM sys_receipts_settlements,sys_brk_main_transactions a , sys_brk_main_transactions b\n" +
            "                                      WHERE rec_settle_dr = a.trans_no\n" +
            "                                            and rec_settle_cr =b.trans_no\n" +
            "                                            and B.trans_clnt_type = 'C'\n" +
            "                                            and B.trans_type not in ('NBD','APD','RND')\n" +
            "                                            and  B.trans_type='RC'\n" +
            "                                            and B.trans_ref_no not in (select  receipt_no from sys_brk_receipts where coalesce(receipt_direct,'N')   = 'Y')\n" +
            "                                      group by replace(rec_settle_cr_ref,'/CN','') ,a.trans_pol_id,b.trans_auth_date \n" +
            "            union \n" +
            "             SELECT replace(rec_settle_cr_ref,'/CN','')rtransrefno ,a.trans_pol_id POLID, b.trans_auth_date transauthdate,\n" +
            "                    SUM(CASE WHEN b.trans_dc= 'C' THEN -1*rec_alloc_amt ELSE 1*rec_alloc_amt END) rsettledamount\n" +
            "             FROM sys_receipts_settlements,sys_brk_main_transactions a , sys_brk_main_transactions b\n" +
            "             WHERE rec_settle_dr = a.trans_no\n" +
            "                   and rec_settle_cr =b.trans_no\n" +
            "                   and B.trans_clnt_type = 'C'\n" +
            "                   and B.trans_type not in ('NBD','APD','RND')\n" +
            "                   and  B.trans_type ='APC'\n" +
            "                   and B.trans_ref_no not in (select  receipt_no from sys_brk_receipts where coalesce(receipt_direct,'N')   = 'Y')\n" +
            "             group by replace(rec_settle_cr_ref,'/CN','') ,a.trans_pol_id,b.trans_auth_date) receipt \n" +
            "                  where (A.trans_balance =0)\n" +
            "                        and A.trans_type not in ('SAG')\n" +
            "                        and A.trans_clnt_type = 'A'\n" +
            "                      --  and trans_pol_id = 305\n" +
            "                        and a.trans_pol_id = receipt.POLID)trans,\n" +
            "              sys_brk_policies,sys_brk_clients,(select * from sys_brk_main_transactions where\n" +
            "                trans_clnt_type = 'C'  and  trans_type in ('NBD','APD','RND','APC') ) balances \n" +
            "            where trans.polid=pol_id\n" +
            "                  and pol_client_id = client_id\n" +
            "                  and balances.trans_pol_id = trans.polid\n" +
            "                  and trans.rsettledamount <> 0\n" +
            "                  and trans.trans_agent_code = :acctId\n" +
            "                  and transauthdate between :wef  and :wet   \n" +
            "                   and round(rsettledamount,:round)<> 0  \n" +
            "                  AND rtransrefno NOT IN (SELECT pa_settle_rec_ref_no\n" +
            "                                    FROM sys_brk_payment_audit a\n" +
            "                                           inner join sys_receipts_settlements s on s.rec_settle_id = a.pa_settle_cr_id\n" +
            "                                           inner join sys_brk_main_transactions s1 on s1.trans_no = s.rec_settle_client_cr\n" +
            "                                    WHERE  pa_posted = 'Y' \n" +
            "                                    AND pa_settle_dr_ref_no= trans.trans_ref_no )\n" +
            "                  union\n" +
            "            select pol_no,pol_client_pol_no,client_fname,client_onames,trans_ref_no,trans_ref_no,trans_auth_date,'FULL' as pay_status, trans_net_amt, trans_comm,trans_whtx,trans_settle_amt,trans_balance,trans_settle_amt,trans_pol_id,'CLB' from sys_brk_main_transactions,\n" +
            "              sys_brk_policies,sys_brk_clients\n" +
            "            where trans_clnt_type='A' and trans_dc='D' and trans_type='AGR'\n" +
            "                  and trans_pol_id = pol_id\n" +
            "                  and pol_client_id= client_id\n" +
            "                  AND trans_agent_code = :acctId\n" +
            "                  AND trans_balance > 0\n" +
            "                  and trans_auth_date between :wef  and :wet\n" +
            "                  AND trans_ref_no NOT IN (SELECT  pa_settle_dr_ref_no FROM sys_brk_payment_audit WHERE pa_settle_rec_ref_no=trans_ref_no))x";


    @Query(value =postgresCommQuery, nativeQuery = true)
    List<Object[]> getPostgresSettlementCommDetails(@Param("acctId")Long acctId, @Param("wef")Date wef, @Param("wet")Date wet,
                                                    @Param("round")Integer round);

    @Query(value ="select  debit.trans_pol_id,sbp.pol_no,debit.trans_type,debit.trans_settle_amt,debit.trans_balance,debit.trans_ref_no as debitRef,\n" +
            "credit.trans_ref_no as receiptRef,sbp.pol_basic_premium_amt,ct_amount,ct_whtx,ct_cur_code,sbp.pol_agent_id,sbp.pol_sub_agent_id ,\n" +
            " sbc.client_fname AS client_fname, sbc.client_onames AS client_onames,ct_id,ct_net_amt\n" +
            "from sys_brk_commission_trans stmt\n" +
            "join sys_brk_main_transactions debit on stmt.ct_debit_trans  = debit.trans_no \n" +
            "join sys_brk_main_transactions credit on stmt.ct_credit_trans  = credit.trans_no\n" +
            "join sys_brk_policies sbp on sbp.pol_id  = debit.trans_pol_id \n" +
            "join sys_brk_clients sbc on sbc.client_id  = sbp.pol_client_id \n" +
            "where coalesce(ct_authorised,'N') = 'N'\n" +
            "AND ct_cur_code = :round\n" +
            "AND sbp.pol_agent_id = :acctId\n" +
            "AND ct_date between :wef  and :wet and ct_trans_type= :commtype", nativeQuery = true)
    List<Object[]> getPostgresUnsettledCommDetails(@Param("acctId")Long acctId, @Param("wef")Date wef, @Param("wet")Date wet,
                                                    @Param("round")Long round, @Param("commtype")String commtype);
    @Query(value ="SELECT a.trans_pol_id AS polid, MAX(sbp.pol_no) AS pol_no, MAX(a.trans_type),\n" +
            "                SUM(CASE WHEN b.trans_dc = 'C' THEN rec_alloc_amt ELSE -rec_alloc_amt END ) AS rsettledamount,\n" +
            "                SUM(a.trans_balance) AS total,MAX(a.trans_ref_no) AS debitRef,MAX(b.trans_ref_no) AS receiptRef,\n" +
            "                MAX(sbp.pol_basic_premium_amt) AS pol_basic_premium_amt,\n" +
            "                case when max(coalesce(sbp.pol_comm_amt,0))<> 0\n" +
            "                then  max(coalesce(sbp.pol_comm_amt,0)) else  MAX(coalesce (a.trans_comm,0)) end AS pol_comm_amt,\n" +
            "                MAX(sbp.pol_whtx) AS pol_whtx,MAX(b.trans_curr_id) AS trans_curr_id,MAX(sbp.pol_agent_id) AS pol_agent_id,\n" +
            "                MAX(sbc.client_fname) AS client_fname, MAX(sbc.client_onames) AS client_onames\n" +
            "            FROM sys_receipts_settlements\n" +
            "            INNER JOIN sys_brk_main_transactions a ON rec_settle_dr = a.trans_no\n" +
            "            INNER JOIN sys_brk_main_transactions b ON rec_settle_cr = b.trans_no\n" +
            "            INNER JOIN sys_brk_policies sbp ON sbp.pol_id = b.trans_pol_id\n" +
            "            INNER JOIN sys_brk_clients sbc ON sbc.client_id = sbp.pol_client_id \n" +
            "            WHERE b.trans_clnt_type = 'C'\n" +
            "            AND a.trans_type NOT IN ('CN')\n" +
            "            AND sbp.pol_current_status NOT IN ('CN')\n" +
            "            GROUP BY a.trans_pol_id\n" +
            "            HAVING SUM(a.trans_balance) = 0", nativeQuery = true)
    List<Object[]> getAllUnsettledBulkCommDetails();

    @Query(value = "SELECT COUNT(1)  from sys_brk_main_transactions where trans_ref_no =:refNo " +
            "and trans_clnt_type ='C' " +
            "and trans_dc ='D' " +
            "and trans_ref_no not in (select pa_settle_dr_ref_no  from sys_brk_payment_audit where pa_posted in ('Y') \n" +
            "and pa_trans_type  in ('NML'))", nativeQuery = true)
    long countIfProcessed(@Param("refNo") String refNo);


    @Query(value ="SELECT x.* FROM (SELECT pol_no,pol_client_pol_no,client_fname,client_onames,trans.trans_ref_no,trans.rtransrefno,balances.trans_auth_date,\n" +
            "case  balances.trans_balance\n" +
            "when 0 then\n" +
            "'FULL'\n" +
            "else\n" +
            "'PARTIAL'\n" +
            "\tend as pay_status,balances.trans_net_amt,\n" +
            "\ttrans.trans_comm *  (rsettledamount/balances.trans_net_amt) as commission ,\n" +
            "\ttrans.trans_whtx *  (rsettledamount/balances.trans_net_amt) as whtx,trans.rsettledamount,balances.trans_balance,trans.trans_settle_amt,balances.trans_pol_id,'NML' as ttype\n" +
            "from (select A.trans_type,A.trans_dc,A.trans_ref_no,A.trans_balance ,A.trans_comm, A.trans_whtx,receipt.*,A.trans_agent_code,A.trans_settle_amt\n" +
            "from sys_brk_main_transactions A,\n" +
            "(SELECT replace(rec_settle_cr_ref,'/CN','')rtransrefno ,a.trans_pol_id POLID, b.trans_auth_date transauthdate,\n" +
            "                            SUM(CASE WHEN b.trans_dc= 'C' THEN 1*rec_alloc_amt ELSE -1*rec_alloc_amt END) rsettledamount\n" +
            "                          FROM sys_receipts_settlements,sys_brk_main_transactions a , sys_brk_main_transactions b\n" +
            "                          WHERE rec_settle_dr = a.trans_no\n" +
            "                                and rec_settle_cr =b.trans_no\n" +
            "                                and B.trans_clnt_type = 'C'\n" +
            "                                and B.trans_type not in ('NBD','APD','RND')\n" +
            "                                and  B.trans_type='RC'\n" +
            "                                and B.trans_ref_no not in (select  receipt_no from sys_brk_receipts where coalesce(receipt_direct,'N')   = 'Y')\n" +
            "                          group by replace(rec_settle_cr_ref,'/CN','') ,a.trans_pol_id,b.trans_auth_date " +
            " union " +
            "SELECT replace(rec_settle_cr_ref,'/CN','')rtransrefno ,a.trans_pol_id POLID,b.trans_auth_date transauthdate ,\n" +
            "        SUM(CASE WHEN b.trans_dc= 'C' THEN -1*rec_alloc_amt ELSE 1*rec_alloc_amt END) rsettledamount\n" +
            " FROM sys_receipts_settlements,sys_brk_main_transactions a , sys_brk_main_transactions b\n" +
            " WHERE rec_settle_dr = a.trans_no\n" +
            "       and rec_settle_cr =b.trans_no\n" +
            "       and B.trans_clnt_type = 'C'\n" +
            "       and B.trans_type not in ('NBD','APD','RND')\n" +
            "       and  B.trans_type ='APC'\n" +
            "       and B.trans_ref_no not in (select  receipt_no from sys_brk_receipts where coalesce(receipt_direct,'N')   = 'Y')\n" +
            " group by replace(rec_settle_cr_ref,'/CN','') ,a.trans_pol_id,b.trans_auth_date) receipt\n" +
            "where (A.trans_balance < 0 OR (A.trans_balance > 0 AND A.trans_type IN ('APC') )) \n" +
            "and A.trans_type not in ('SAG')\n" +
            "and A.trans_clnt_type = 'A'\n" +
            "and a.trans_pol_id = receipt.POLID)trans,\n" +
            "sys_brk_policies,sys_brk_clients,(select * from sys_brk_main_transactions where \n" +
            " trans_clnt_type = 'C'  and  trans_type in ('NBD','APD','RND','APC') ) balances \n" +
            "where trans.polid=pol_id\n" +
            "and pol_client_id = client_id " +
            "and balances.trans_pol_id = trans.polid\n" +
            "and trans.rsettledamount <> 0\n" +
            "and trans.trans_agent_code = :acctId\n" +
            "and transauthdate between :wef  and :wet  " +
            "and round(rsettledamount,:round)<> 0 " +
            "AND rtransrefno NOT IN (SELECT pa_settle_rec_ref_no\n" +
            "                        FROM sys_brk_payment_audit a\n" +
            "                               inner join sys_receipts_settlements s on s.rec_settle_id = a.pa_settle_cr_id\n" +
            "                               inner join sys_brk_main_transactions s1 on s1.trans_no = s.rec_settle_client_cr\n" +
            "                        WHERE  pa_posted = 'Y'" +
            "                       AND pa_settle_dr_ref_no= trans.trans_ref_no) " +
            "union "+
            "select pol_no,pol_client_pol_no,client_fname,client_onames,trans_ref_no,trans_ref_no,trans_auth_date,'FULL' as pay_status, trans_net_amt, trans_comm,trans_whtx,trans_settle_amt,trans_balance,trans_settle_amt,trans_pol_id,'CLB' as ttype " +
            "from sys_brk_main_transactions,\n" +
            "  sys_brk_policies,sys_brk_clients\n" +
            "where trans_clnt_type='A' and trans_dc='D' and trans_type='AGR'\n" +
            "      and trans_pol_id = pol_id\n" +
            "      and pol_client_id= client_id\n" +
            "      AND trans_agent_code = :acctId\n" +
            "      AND trans_balance > 0\n" +
            "      AND trans_ref_no NOT IN (SELECT  pa_settle_dr_ref_no FROM sys_brk_payment_audit WHERE pa_settle_rec_ref_no=trans_ref_no)\n" +
            "      and trans_auth_date between :wef  and :wet)x", nativeQuery = true)
    List<Object[]> getSqlServerSettlementCommDetails(@Param("acctId")Long acctId, @Param("wef")Date wef, @Param("wet")Date wet,
                                                     @Param("round")Integer round);



    @Query(value = "select a.rec_settle_id from sys_receipts_settlements a join sys_brk_main_transactions sbmt \n" +
            "on a.rec_settle_dr  = sbmt.trans_no \n" +
            "join sys_brk_main_transactions sbmt2 on sbmt2.trans_no  = a.rec_settle_cr \n" +
            "where sbmt.trans_ref_no  = :drrefNo\n" +
            "and sbmt2.trans_ref_no  = :crrefno\n" +
            "and sbmt2.trans_type = 'RC'", nativeQuery = true)
    List<BigInteger> getCreditSettlements(@Param("drrefNo") String drrefNo, @Param("crrefno") String crrefno);

    @Query(value = "                                          \n" +
            "                                          SELECT \n" +
            "                                              sbp.pol_no as clientPolNo, \n" +
            "                                              sba.acct_name AS fname, \n" +
            "                                              '' AS otherNames, \n" +
            "                                              debit.trans_ref_no AS drNo, \n" +
            "                                              credit.trans_ref_no AS crNo, \n" +
            "                                              sbpa.pr_desc AS product, \n" +
            "                                              sba2.acct_name AS insurer, \n" +
            "                                              stmt.ct_amount as commAmt, \n" +
            "                                              debit.trans_settle_amt as allocAmt, \n" +
            "                                              sbp.pol_basic_premium_amt as basicPrem, \n" +
            "                                              stmt.ct_whtx as whtx, \n" +
            "                                              'FULL' AS payStatus, \n" +
            "                                              sba2.acct_sht_desc  AS controlAcc, \n" +
            "                                              debit.trans_auth_date, \n" +
            "                                              sbp.pol_sub_agent_id,\n" +
            "                                              stmt.ct_id,\n" +
            "                                              stmt.ct_net_amt  \n" +
            "                                              FROM sys_brk_commission_trans stmt \n" +
            "                                              JOIN sys_brk_main_transactions debit ON stmt.ct_debit_trans = debit.trans_no \n" +
            "                                              JOIN sys_brk_main_transactions credit ON stmt.ct_credit_trans = credit.trans_no \n" +
            "                                              JOIN sys_brk_policies sbp ON sbp.pol_id = debit.trans_pol_id \n" +
            "                                              JOIN sys_brk_clients sbc ON sbc.client_id = sbp.pol_client_id \n" +
            "                                              join sys_brk_accounts sba on sbp.pol_sub_agent_id = sba.acct_id \n" +
            "                                              join sys_brk_products sbpa on sbp.pol_prod_id = sbpa.pr_code \n" +
            "                                              join sys_brk_accounts sba2 on sba2.acct_id = sbp.pol_agent_id \n" +
            "                                              where coalesce(ct_authorised,'N') = 'N'\n" +
            "                                              AND ct_cur_code = :round\n" +
            "                                              AND sbp.pol_sub_agent_id = :acctId\n" +
            "                                              AND ct_date between :wef  and :wet and ct_trans_type= 'Sub Agent Commission'\n" +
            "                                              union all \n" +
            "                                              select \n" +
            "                                              sbp.pol_no as clientPolNo, \n" +
            "                                              sba.acct_name AS fname, \n" +
            "                                              '' AS otherNames, \n" +
            "                                              debit.trans_ref_no AS drNo, \n" +
            "                                              credit.trans_ref_no AS crNo, \n" +
            "                                              sbpa.pr_desc AS product, \n" +
            "                                              sba2.acct_name AS insurer, \n" +
            "                                              stmt.ct_amount as commAmt, \n" +
            "                                              debit.trans_settle_amt as allocAmt, \n" +
            "                                              sbp.pol_basic_premium_amt as basicPrem, \n" +
            "                                              stmt.ct_whtx as whtx, \n" +
            "                                              'FULL' AS payStatus, \n" +
            "                                              sba2.acct_sht_desc  AS controlAcc, \n" +
            "                                              debit.trans_auth_date, \n" +
            "                                              sbp.pol_sub_agent_id,\n" +
            "                                              stmt.ct_id,\n" +
            "                                              stmt.ct_net_amt  \n" +
            "                                              FROM sys_brk_commission_trans stmt \n" +
            "                                              JOIN sys_brk_main_transactions debit ON stmt.ct_debit_trans = debit.trans_no \n" +
            "                                              JOIN sys_brk_main_transactions credit ON stmt.ct_credit_trans = credit.trans_no \n" +
            "                                              JOIN sys_brk_policies sbp ON sbp.pol_id = debit.trans_pol_id \n" +
            "                                              JOIN sys_brk_clients sbc ON sbc.client_id = sbp.pol_client_id \n" +
            "                                              join sys_brk_accounts sba on sbp.pol_marketer_agent_id  = sba.acct_id \n" +
            "                                              join sys_brk_products sbpa on sbp.pol_prod_id = sbpa.pr_code \n" +
            "                                              join sys_brk_accounts sba2 on sba2.acct_id = sbp.pol_agent_id \n" +
            "                                              where coalesce(ct_authorised,'N') = 'N'\n" +
            "                                              AND ct_cur_code = :round\n" +
            "                                              AND sbp.pol_marketer_agent_id = :acctId\n" +
            "                                              AND ct_date between :wef  and :wet and ct_trans_type= 'Sub Agent Commission'\n" +
            "                ", nativeQuery = true)
    List<Object[]> getSubAgentCommTrans(@Param("acctId") Long subAgentCode,
                                        @Param("wef") Date wef,
                                        @Param("wet") Date wet,
                                        @Param("round") Long round);

    @Query(value = "SELECT rec_settle_dr, rec_alloc_amt, rec_settle_cr_ref, rec_settle_client_cr, rec_settle_id \n" +
            "FROM sys_receipts_settlements \n" +
            "WHERE rec_settle_cr = :rec_settle_id", nativeQuery = true)
    List<Object[]> findReceiptSettDtl(@Param("rec_settle_id") Long settlementId);

    @Modifying
    @Query(value = "update sys_receipts_settlements set rec_withdrawn = :withdrawn, rec_withdrawn_date = :withdrawndate, rec_withdrawn_by = :withdrawnuser where rec_settle_id = :settlementId", nativeQuery = true)
    void updateReceiptSettlStatus(  @Param("withdrawn") String withdrawn,
                                    @Param("withdrawndate") Date withdrawndate,
                                    @Param("withdrawnuser") Long withdrawnuser,
                                    @Param("settlementId") Long settlementId);

    @Query(value = "SELECT MAX(b.trans_ref_no) AS receiptRef\n" +
            "                        FROM sys_receipts_settlements\n" +
            "                        INNER JOIN sys_brk_main_transactions a ON rec_settle_dr = a.trans_no\n" +
            "                        INNER JOIN sys_brk_main_transactions b ON rec_settle_cr = b.trans_no\n" +
            "                        INNER JOIN sys_brk_policies sbp ON sbp.pol_id = b.trans_pol_id\n" +
            "                        WHERE b.trans_clnt_type = 'C'\n" +
            "                        AND a.trans_type NOT IN ('CN')\n" +
            "                        AND sbp.pol_current_status NOT IN ('CN') \n" +
            "            AND a.trans_ref_no =:debitRef\n" +
            "            GROUP BY a.trans_pol_id\n" +
            "            HAVING SUM(a.trans_balance) = 0",nativeQuery = true)
    String getReceiptDetails(@Param("debitRef") String debitRef);


}