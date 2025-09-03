package com.brokersystems.brokerapp.quotes.repository;

import com.brokersystems.brokerapp.quotes.model.QuoteCategoryRules;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by waititu on 13/08/2017.
 */
public interface QuoteCategoryRulesRepo extends PagingAndSortingRepository<QuoteCategoryRules, Long> ,QueryDslPredicateExecutor<QuoteCategoryRules>{
    @Query(value = "select rule_id,rule_desc,rule_value from sys_brk_med_bin_rules \n" +
            "where rule_bd_code=:binCode \n" +
            "and rule_id not in (select cr_rule_id from sys_brk_quot_cat_rules where cr_cat_id=:catId)",nativeQuery = true)
    public List<Object[]> getBinderRules(@Param("binCode") Long binCode, @Param("catId") Long catId);
}
