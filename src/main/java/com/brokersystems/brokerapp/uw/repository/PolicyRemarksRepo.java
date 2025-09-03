package com.brokersystems.brokerapp.uw.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import com.brokersystems.brokerapp.setup.model.EndorsementRemarks;
import com.brokersystems.brokerapp.uw.model.PolicyRemarks;

import java.util.List;

public interface PolicyRemarksRepo extends JpaRepository<PolicyRemarks, Long>, QueryDslPredicateExecutor<PolicyRemarks> {
	
	@Query("select s from EndorsementRemarks s where lower(s.remarkShtDesc) like %:remark%  and s.remarkId NOT IN (select p.endRemarks.remarkId from PolicyRemarks p where p.policy.policyId=:polCode) and s.remarkShtDesc not in ('ENDDOWN', 'CONTRA', 'Cancellation', 'Upward revision', 'NON-FINANCIAL')")
	public Page<EndorsementRemarks> getEndorsementRemarks(@Param("polCode")Long polCode,@Param("remark")String remark,Pageable pageable);

	@Query(value = "SELECT sber.remarks, sber.sht_desc " +
			"FROM sys_brk_pol_remarks sbpr " +
			"JOIN sys_brk_end_remarks sber ON sbpr.pr_remark_id = sber.remark_id " +
			"WHERE sbpr.pr_policy_id = :policyId", nativeQuery = true)
	List<Object[]> findRemarksByPolicyId(@Param("policyId") Long policyId);

	@Query("select s from EndorsementRemarks s where upper(s.remarkShtDesc) = :remarkShtDesc")
	EndorsementRemarks findremarksByshtDesc(@Param("remarkShtDesc") String remarkShtDesc);

}
