package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.PremRatesTable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by HP on 9/20/2017.
 */
public interface PremRatesTableRepo extends PagingAndSortingRepository<PremRatesTable, Long>, QueryDslPredicateExecutor<PremRatesTable> {

    @Query(value = "select rate_table  from sys_brk_prem_rates_tbl where rate_wef_date in (select max(rate_wef_date) from sys_brk_prem_rates_tbl \n" +
            "where rate_det_code =:detId)",nativeQuery = true)
    String getRatesLocation(@Param("detId") Long detId);

    @Query(value = "select rate_id,rate_wef_date,rate_file_name,COUNT(*) OVER() as total_rows  from sys_brk_prem_rates_tbl \n" +
            "where rate_det_code =:detId\n" +
            "order by rate_wef_date  DESC \n" +
            "OFFSET :pageNo*:limit LIMIT :limit ",nativeQuery = true)
    List<Object[]> findPremiumRatesTbl(@Param("detId") Long detId,
                                       @Param("pageNo") int pageNo,
                                    @Param("limit") int limit);
}
