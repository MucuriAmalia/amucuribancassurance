package com.brokersystems.brokerapp.reinsurance.repo;

import com.brokersystems.brokerapp.reinsurance.model.TreatyDefinition;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface TreatyDefinitionRepository extends
        PagingAndSortingRepository<TreatyDefinition, Long>, QueryDslPredicateExecutor<TreatyDefinition> {

    @Query(value = "select td_id,td_treaty,td_wef,td_wet,td_cash_call,td_cession_rate,td_cession_rate_type,td_limit,\n" +
            "sbc.cur_iso_code cur_name ,td_status,sbu.user_username raised_by,sbu2.user_username auth_by,td_sum_insured_from, COUNT(*) OVER() as total_rows  from  sys_brk_treaty_definition\n" +
            "join sys_brk_currencies sbc  on sbc.cur_code  = sys_brk_treaty_definition.td_cur_code \n" +
            " join sys_brk_users sbu  on sbu.user_id  = sys_brk_treaty_definition.td_raised_by \n" +
            " left join sys_brk_users sbu2  on sbu2.user_id  = sys_brk_treaty_definition.td_auth_by  \n" +
            " where  (td_treaty like :search)\n" +
            " order by td_wef DESC \n" +
            " OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findTreaties( @Param("search") String search,
                                  @Param("pageNo") int pageNo,
                                  @Param("limit") int limit);
}
