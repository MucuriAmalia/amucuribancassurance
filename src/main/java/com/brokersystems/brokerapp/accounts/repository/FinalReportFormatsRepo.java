package com.brokersystems.brokerapp.accounts.repository;

import com.brokersystems.brokerapp.accounts.model.FinalReportFormats;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FinalReportFormatsRepo extends PagingAndSortingRepository<FinalReportFormats, Long>, QueryDslPredicateExecutor<FinalReportFormats> {

    @Query(value = "select rf_row_code,rf_description,rf_detail_format,rf_summary_format,rf_type,rf_order,\n" +
            "rf_picked_from,rf_asst_liabl,rf_asst_liabl_sign,rf_allocation_perc,rf_id,COUNT(*) OVER() as total_rows\n" +
            "from sys_brk_rpt_formats where rf_rpt_type =:rptType\n" +
            "and lower(rf_description) like lower(:search)\n" +
            "order by rf_id \n" +
            "OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
    List<Object[]> searchAllReportFormats(@Param("rptType") String rptType,
                                   @Param("search") String search,
                                  @Param("pageNo") int pageNo,
                                  @Param("limit") int limit);

}
