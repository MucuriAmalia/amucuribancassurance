package com.brokersystems.brokerapp.setup.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brokersystems.brokerapp.setup.model.RevenueItemsDef;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RevenueItemsRepo extends  PagingAndSortingRepository<RevenueItemsDef, Long>, QueryDslPredicateExecutor<RevenueItemsDef> {


    @Query(value = "select rev_id , rev_code,COUNT(*) OVER() as total_rows  from sys_brk_revenue_items \n" +
            "where rev_code  not in ('PHCF','TL','SD')\n" +
            "and rev_code like :search\n" +
            "order by rev_code asc\n" +
            "OFFSET :pageNo*:limit LIMIT :limit",nativeQuery = true)
    List<Object[]> findAllRevItems(@Param("search") String search,
                                       @Param("pageNo") int pageNo,
                                       @Param("limit") int limit);

}
