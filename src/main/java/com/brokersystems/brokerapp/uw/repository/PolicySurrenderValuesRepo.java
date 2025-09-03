package com.brokersystems.brokerapp.uw.repository;

import com.brokersystems.brokerapp.uw.model.PolicySurrenderValues;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PolicySurrenderValuesRepo extends PagingAndSortingRepository<PolicySurrenderValues, Long>, QueryDslPredicateExecutor<PolicySurrenderValues> {

    @Query(value = "select surrender_yr ,surrender_value,COUNT(*) OVER() as total_rows  from sys_brk_pol_surrender_values " +
            "where surr_pol_id =:polId " +
            "order by surr_id  " +
            "OFFSET :pageNo*:limit limit :limit", nativeQuery = true)
    List<Object[]> findPolSurrenderValues(@Param("polId") Long polId,
                                         @Param("pageNo") int pageNo,
                                         @Param("limit") int limit);

}
