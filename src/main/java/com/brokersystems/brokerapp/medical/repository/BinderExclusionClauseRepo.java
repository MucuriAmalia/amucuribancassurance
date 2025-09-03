package com.brokersystems.brokerapp.medical.repository;

import com.brokersystems.brokerapp.setup.model.ClausesDef;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by waititu on 06/06/2017.
 */
public interface BinderExclusionClauseRepo extends PagingAndSortingRepository<ClausesDef,Long>, QueryDslPredicateExecutor<ClausesDef> {
    @Query(value = "select clau_id,clau_header,clau_sht_desc from sys_brk_clauses,sys_brk_subclasses,sys_brk_sub_clauses where (lower(clau_header) like %:subName% or lower(clau_sht_desc) like %:subName%) and subcl_sub_code =sub_id and subcl_cl_code =clau_id and clau_type = 'X' and clau_id  " +
            " not in (select be_clau_id from sys_brk_bin_exclusions where be_bin_id=:binCode  and be_clau_id is  not null) and sub_id in ( select sc_sub_code\n" +
            "    from sys_brk_binder_det,sys_brk_sub_covertypes\n" +
            "    where bdet_sub_covt_code=sc_id\n" +
            "    and bdet_bin_code = :binCode  ) ",nativeQuery = true)
    public List<Object[]> getBinderExclusions(@Param("binCode") Long binCode,@Param("subName")String subName );
}
