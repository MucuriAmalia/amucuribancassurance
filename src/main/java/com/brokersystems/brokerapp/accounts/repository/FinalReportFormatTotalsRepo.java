package com.brokersystems.brokerapp.accounts.repository;

import com.brokersystems.brokerapp.accounts.model.FinalReportFormatTotals;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FinalReportFormatTotalsRepo extends PagingAndSortingRepository<FinalReportFormatTotals, Long>, QueryDslPredicateExecutor<FinalReportFormatTotals> {

    @Query(value = "select total.rft_id  ,total.rft_sign,sbrf.rf_description rft_column,sbrf2.rf_description rft_total ,COUNT(*) OVER() as total_rows from sys_brk_rpt_format_totals total\n" +
            "join sys_brk_rpt_formats sbrf on sbrf.rf_id =  total.rft_column \n" +
            "join sys_brk_rpt_formats sbrf2 on sbrf2.rf_id =total.rft_total  \n" +
            "where (sbrf.rf_rpt_type =:rfId and sbrf2.rf_rpt_type =:rfId) \n" +
            "            and (lower(sbrf.rf_description) like lower(:search) or lower(sbrf2.rf_description) like lower(:search))\n" +
            "            order by total.rft_id asc\n" +
            "            OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
    List<Object[]> searchAllReportTotals(@Param("rfId") String rfId,
                                               @Param("search") String search,
                                               @Param("pageNo") int pageNo,
                                               @Param("limit") int limit);

}
