package com.brokersystems.brokerapp.uw.repository;

import com.brokersystems.brokerapp.uw.model.PolicyMiscInfo;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.jpa.repository.Query;

public interface PolicyMiscInfoRepo extends JpaRepository<PolicyMiscInfo, Long>, QueryDslPredicateExecutor<PolicyMiscInfo> {

    @Query(value = "SELECT * FROM sys_brk_pol_misc_info WHERE policy_id = :policyId", nativeQuery = true)
    PolicyMiscInfo findByPolicyId(@Param("policyId") Long policyId);
}