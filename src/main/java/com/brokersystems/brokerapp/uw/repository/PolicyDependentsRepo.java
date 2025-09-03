package com.brokersystems.brokerapp.uw.repository;

import com.brokersystems.brokerapp.uw.model.PolicyDependentsInfo;
import com.brokersystems.brokerapp.uw.model.PolicyMiscInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PolicyDependentsRepo extends JpaRepository<PolicyDependentsInfo, Long>, QueryDslPredicateExecutor<PolicyDependentsInfo> {

    @Query(value = "SELECT * FROM sys_brk_pol_dependant_misc_info WHERE policy_id = :policyId", nativeQuery = true)
    PolicyDependentsInfo findByPolicy(@Param("policyId") Long policyId);

    @Query(value = "SELECT * FROM sys_brk_pol_dependant_misc_info WHERE policy_id = :policyId", nativeQuery = true)
    List<PolicyDependentsInfo> findByPolicyId(@Param("policyId") Long policyId);

    @Query(value = "SELECT * FROM sys_brk_pol_dependant_misc_info WHERE policy_id = :policyId and dep_type = :depType", nativeQuery = true)
    List<PolicyDependentsInfo> findByPolicyIdAndDeptType(@Param("policyId") Long policyId, @Param("depType") String depType);

    @Query(value = "SELECT * FROM sys_brk_pol_dependant_misc_info WHERE policy_id = :policyId AND dep_full_name = :name AND dep_type = :deptType", nativeQuery = true)
    PolicyDependentsInfo findByPolicyIdNameDepType(@Param("policyId") Long policyId, @Param("name") String name, @Param("deptType") String deptType);

}
