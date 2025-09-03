package com.brokersystems.brokerapp.setup.repository;

import com.brokersystems.brokerapp.setup.model.ClausesDef;
import com.brokersystems.brokerapp.setup.model.SectionLimits;
import com.brokersystems.brokerapp.setup.model.SubclassClauses;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by HP on 3/16/2018.
 */
public interface SectionLimitsRepo  extends PagingAndSortingRepository<SectionLimits, Long>, QueryDslPredicateExecutor<SectionLimits> {

    @Query(value = "select subcl_cl_id,clau_sht_desc,clau_header from sys_brk_sub_clauses,sys_brk_clauses where subcl_cl_code = clau_id and subcl_cl_id not in (select sl_clause_id from sys_brk_section_limits where sl_prem_id=:id) and subcl_sub_code = :subId",nativeQuery = true)
    public List<Object[]> getUnassignedClauses(@Param("id")Long id, @Param("subId")Long subId);


}
