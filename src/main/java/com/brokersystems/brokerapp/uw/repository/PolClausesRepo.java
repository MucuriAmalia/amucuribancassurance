package com.brokersystems.brokerapp.uw.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brokersystems.brokerapp.uw.model.PolicyClauses;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PolClausesRepo extends  PagingAndSortingRepository<PolicyClauses, Long>, QueryDslPredicateExecutor<PolicyClauses> {


    @Query(value = "select distinct x.cl_code,x.clau_header,x.bc_cl_code from (\n" +
            "select bc_cl_code cl_code,bc_clau_header clau_header,bc_cl_code  from sys_brk_binder_clauses sbbc \n" +
            "where sbbc.bc_bd_code not in (select coalesce(risk_binder_det_id,-2000)  from sys_brk_risks where risk_pol_id= :polId)\n" +
            "and bc_mandatory ='Y'\n" +
            "union all \n" +
            "select sbsc.subcl_cl_code cl_code,sbc.clau_header,subcl_cl_id as bc_cl_code  from sys_brk_sub_clauses sbsc \n" +
            "join sys_brk_clauses sbc on sbsc.subcl_cl_code = sbc.clau_id)x\n" +
            "where x.bc_cl_code not in (select coalesce(pol_clau_sub_code,-2000)  from sys_brk_pol_clauses where pol_clau_pol_id = :polId)",nativeQuery = true)
    List<Object[]> getSubClauses(@Param("polId") Long polId);

}
