package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.medical.model.CategoryRules;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by peter on 4/28/2017.
 */
public interface CategoryRulesRepo extends PagingAndSortingRepository<CategoryRules, Long>, QueryDslPredicateExecutor<CategoryRules> {

    @Query(value = "select rule_id,rule_desc,rule_value from sys_brk_med_bin_rules \n" +
            "where rule_bd_code=:binCode \n" +
            "and rule_id not in (select cr_rule_id from sys_brk_cat_rules where cr_cat_id=:catId)",nativeQuery = true)
    public List<Object[]> getBinderRules(@Param("binCode") Long binCode, @Param("catId") Long catId);


}
