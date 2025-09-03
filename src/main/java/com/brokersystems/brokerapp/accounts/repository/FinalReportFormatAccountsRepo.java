package com.brokersystems.brokerapp.accounts.repository;

import com.brokersystems.brokerapp.accounts.model.FinalReportFormatAccounts;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FinalReportFormatAccountsRepo extends PagingAndSortingRepository<FinalReportFormatAccounts, Long>, QueryDslPredicateExecutor<FinalReportFormatAccounts> {


    @Query(value = "select rfa_id,rfa_acct_no,rfa_sign,rfa_acct_name,COUNT(*) OVER() AS total_rows from sys_brk_rpt_format_accts where rfa_rf_id=:rfId \n" +
            "and lower(rfa_acct_no) like lower(:search)\n" +
            "order by rfa_acct_no\n" +
            "OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
    List<Object[]> searchAllReportFormatsAccts(@Param("rfId") Long rfId,
                                          @Param("search") String search,
                                          @Param("pageNo") int pageNo,
                                          @Param("limit") int limit);

    @Query(value = "select count(1) from sys_brk_rpt_format_accts\n" +
            "            where rfa_acct_no =:acctNo\n" +
            "            and rfa_rf_id =:fmtId",nativeQuery = true)
    long countDuplicateAccts(@Param("acctNo") String acctNo,@Param("fmtId") long fmtId);

}
