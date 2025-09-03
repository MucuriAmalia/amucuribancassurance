package com.brokersystems.brokerapp.accounts.repository;

import com.brokersystems.brokerapp.accounts.model.FinalReportFormatGroupAccounts;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FinalReportFormatGroupAccountsRepo extends PagingAndSortingRepository<FinalReportFormatGroupAccounts, Long>, QueryDslPredicateExecutor<FinalReportFormatGroupAccounts> {

    @Query(value = "select rfga_id,rfa_acct_no,rfga_sign,COUNT(*) OVER() AS total_rows from sys_brk_rpt_format_grp_accts where rfga_rf_id=:rfId \n" +
            "            and lower(rfa_acct_no) like lower(:search)\n" +
            "            order by rfa_acct_no\n" +
            "            OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
    List<Object[]> searchAllReportFormatsGroupAccts(@Param("rfId") Long rfId,
                                               @Param("search") String search,
                                               @Param("pageNo") int pageNo,
                                               @Param("limit") int limit);

    @Query(value = "select count(1) from sys_brk_rpt_format_grp_accts\n" +
            "            where rfa_acct_no =:acctNo\n" +
            "            and rfga_rf_id =:fmtId",nativeQuery = true)
    long countDuplicateAccts(@Param("acctNo") String acctNo,@Param("fmtId") long fmtId);

    @Query(value = "select count(1) from sys_brk_coa_main where co_code=:account",nativeQuery = true)
    long validateAccount(@Param("account") String account);

}
