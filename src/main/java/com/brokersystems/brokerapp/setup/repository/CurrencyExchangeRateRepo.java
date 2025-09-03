package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.CurrencyExchangeRate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CurrencyExchangeRateRepo  extends PagingAndSortingRepository<CurrencyExchangeRate, Long>, QueryDslPredicateExecutor<CurrencyExchangeRate> {

    @Query(value = "select ce_code, ce_rate,ce_created_date,ce_exchange_date,ce_cur_code,cur_name,COUNT(*) OVER() as total_rows from sys_brk_currency_rates sbcr\n" +
            "join sys_brk_currencies sbc on sbcr.ce_cur_code  = sbc.cur_code \n" +
            "where (lower(cur_name) like :search)\n" +
            "and ce_base_cur_code = :curCode\n" +
            "order by ce_created_date desc OFFSET :pageNo*:limit LIMIT :limit ",nativeQuery = true)
    List<Object[]> findExchangeRates(@Param("curCode") Long curCode,
                                 @Param("search") String search,
                               @Param("pageNo") int pageNo,
                               @Param("limit") int limit);

}
