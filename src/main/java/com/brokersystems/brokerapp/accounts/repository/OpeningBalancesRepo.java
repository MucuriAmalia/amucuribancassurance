package com.brokersystems.brokerapp.accounts.repository;

import com.brokersystems.brokerapp.accounts.model.OpeningBalances;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface OpeningBalancesRepo extends PagingAndSortingRepository<OpeningBalances,Long>, QueryDslPredicateExecutor<OpeningBalances> {

    @Query(value = "select opb_acct_name,opb_acct_no,concat(opb_acct_name,'/',opb_acct_yr)peri,opb_balance,opb_cr_amt,opb_dr_amt,opb_processed_type,COUNT(*) OVER() as total_rows  \n" +
            "from sys_brk_opening_bals sbob \n" +
            "join sys_brk_branches sbb on sbb.ob_id = rpv_ob_id \n" +
            "where concat(opb_acct_name,' ',opb_acct_yr)=:period\n" +
            "order by opb_acct_yr desc\n" +
            "OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
    List<Object[]> searchAllOpeningBalances(@Param("period") String period,
                                          @Param("pageNo") int pageNo,
                                          @Param("limit") int limit);

    @Query(value = "select concat(yp_period_name,' ',sbay.bay_year),COUNT(*) OVER() AS total_rows from sys_brk_account_yr_prds \n" +
            "join sys_brk_account_years sbay on sbay.bay_id  = yp_bay_id\n" +
            "where sbay.bay_ob_id =:obId and  concat(yp_period_name,' ',sbay.bay_year) like :search\n" +
            "order by yp_wef " +
            "OFFSET :pageNo*:limit LIMIT :limit\n",nativeQuery = true)
    List<Object[]> searchAccountingPeriods(@Param("search") String search,
                                            @Param("obId") Long obId,
                                            @Param("pageNo") int pageNo,
                                            @Param("limit") int limit);


    @Query(value = " select x.*,COUNT(*) OVER() as total_rows from ( \n" +
            "               SELECT   SUM (coalesce (op_amt, 0)) ybl_op_bal, \n" +
            "                              0 ybl_dr, 0 ybl_cr, \n" +
            "                              SUM (coalesce (op_amt, 0) + coalesce (trans_amt, 0)) ybl_curr_bal, \n" +
            "                              co_code \n" +
            "                         FROM  \n" +
            "            (SELECT   coalesce(SUM (opb_balance), 0) op_amt, opb_acct_no \n" +
            "                                   FROM sys_brk_opening_bals  \n" +
            "                                  WHERE opb_acct_yr = :year\n" +
            "                               GROUP BY opb_acct_no \n" +
            "                               UNION \n" +
            "                               SELECT 0, co_code \n" +
            "                                 FROM sys_brk_coa_sub \n" +
            "                                WHERE co_code NOT IN ( \n" +
            "                                         SELECT opb_acct_no \n" +
            "                                           FROM sys_brk_opening_bals \n" +
            "                                          )) op_bals,                            \n" +
            "            (SELECT   SUM (case when gl_dc= 'D' then  1 else  -1 end * gl_amount  \n" +
            "                                            ) trans_amt, \n" +
            "                                        co_code  \n" +
            "                                   FROM sys_brk_gl_trans, sys_brk_coa_sub \n" +
            "                                  WHERE gl_account = co_id \n" +
            "                                    and gl_auth_date between :fromdate and :todate\n" +
            "                               GROUP BY co_code)cur_trans    \n" +
            "                            where co_code = opb_acct_no \n" +
            "                            group by co_code)x \n" +
            "                            LEFT JOIN sys_brk_coa_sub coa ON coa.co_code = x.co_code \n" +
            "                            WHERE (:searchValue = '' OR :searchValue IS NULL OR \n" +
            "                                   LOWER(x.co_code) LIKE LOWER(CONCAT('%', :searchValue, '%')) OR \n" +
            "                                   LOWER(coa.co_name) LIKE LOWER(CONCAT('%', :searchValue, '%'))) \n" +
            "                            order by x.co_code \n" +
            "                            OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
    List<Object[]> queryAcctYearOpeningBalances(@Param("year") Long year,
                                                @Param("fromdate") Date fromdate,
                                                @Param("todate") Date todate,
                                                @Param("searchValue") String searchValue,
                                                @Param("pageNo") int pageNo,
                                                @Param("limit") int limit);

    @Query(value = "select co_name from sys_brk_coa_sub where co_code = :glCode",nativeQuery = true)
    String getAccountName(@Param("glCode") String glCode);

    @Query(value = " SELECT ISNULL (SUM (rfa_sign * ISNULL (opb_balance, 0)), 0)\n" +
            "      FROM sys_brk_opening_bals,(\n" +
            "      SELECT rfa_acct_no,rfa_sign\n" +
            "      FROM sys_brk_rpt_formats ,\n" +
            "             sys_brk_rpt_format_accts\n" +
            "      WHERE  rfa_rf_id = rf_id \n" +
            "      AND rf_id = :v_rrw_code\n" +
            "      AND (rf_rpt_type = :v_rpt_code)\n" +
            "       AND (dbo.validate_acc_class (:scId, rfa_acct_no\n" +
            "                                              ) = 'Y'\n" +
            "                 \n" +
            "             )\n" +
            "       ) fmts\n" +
            "       WHERE opb_acct_yr = :v_year\n" +
            "       AND opb_acct_no = fmts.rfa_acct_no",nativeQuery = true)
    BigDecimal findOpBalance(@Param("v_rrw_code") Long v_rrw_code,
                             @Param("v_rpt_code") String v_rpt_code,
                             @Param("scId") Long scId);

    @Query(value = "SELECT ISNULL (SUM (rfga_sign * ISNULL (opb_balance, 0)), 0)\n" +
            "                FROM sys_brk_opening_bals,(\n" +
            "                SELECT rfa_acct_no,rfga_sign\n" +
            "                FROM sys_brk_rpt_formats ,\n" +
            "                       sys_brk_rpt_format_grp_accts\n" +
            "                WHERE  rfga_rf_id = rf_id \n" +
            "                AND rf_id = :v_rrw_code\n" +
            "                AND (rf_rpt_type = :v_rpt_code)\n" +
            "                 AND (dbo.validate_acc_class (:scId, rfa_acct_no\n" +
            "                                                        ) = 'Y'\n" +
            "                           \n" +
            "                       )\n" +
            "                 ) fmts\n" +
            "                 WHERE opb_acct_yr = :v_year\n" +
            "                 AND opb_acct_no = fmts.rfa_acct_no",nativeQuery = true)
    BigDecimal findGroupOpBalance(@Param("v_rrw_code") Long v_rrw_code,
                             @Param("v_rpt_code") String v_rpt_code,
                             @Param("scId") Long scId);


    @Query(value = " SELECT   isnull(sum(case when gl_dc = 'D' then  1 else  -1 end * gl_amount) ,0)  \n" +
            "           FROM sys_brk_gl_trans gltrans,\n" +
            "                sys_brk_coa_sub accts,\n" +
            "                sys_brk_rpt_formats fmts,\n" +
            "                sys_brk_rpt_format_accts fmtacct\n" +
            "          WHERE fmts.rf_id  = fmtacct.rfa_rf_id \n" +
            "            AND gltrans.gl_account  = accts.co_id \n" +
            "            and fmtacct.rfa_acct_no  = accts.co_code \n" +
            "            AND rf_id = :v_rrw_code\n" +
            "            AND gl_auth_date BETWEEN :v_from_date and :v_to_date\n" +
            "            AND (dbo.validate_acc_class (:scId, rfa_acct_no\n" +
            "                                                        ) = 'Y'\n" +
            "                                                        OR (:scId IS NULL)\n" +
            "                           \n" +
            "                       )",nativeQuery = true)
    BigDecimal findOperatingBal(@Param("v_rrw_code") Long v_rrw_code,
                                  @Param("v_from_date") Date v_from_date,
                                  @Param("v_to_date") Date v_to_date,
                                  @Param("scId") Long scId);


}
