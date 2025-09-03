package com.brokersystems.brokerapp.uw.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.brokersystems.brokerapp.uw.model.PolicyActiveRisks;
import com.brokersystems.brokerapp.uw.model.RiskTrans;

import java.util.Date;
import java.util.List;

public interface PolActiveRisksRepo extends  PagingAndSortingRepository<PolicyActiveRisks, Long>, QueryDslPredicateExecutor<PolicyActiveRisks> {
	
	@Query("select s from PolicyActiveRisks s where lower(s.risk.riskShtDesc) like %:riskId% and s.policy.policyId=:polCode  and s.risk.riskId NOT IN (select p.riskId from RiskTrans p where p.policy.policyId=:polCode)")
	 Page<PolicyActiveRisks> getUnendorsedRisks(@Param("polCode")Long polCode,@Param("riskId")String riskId,Pageable pageable);
	
	@Query("select s from PolicyActiveRisks s where lower(s.risk.riskShtDesc) like %:riskId% and s.policy.policyId=:polCode and s.risk.insured.tenId = :insured and s.risk.riskId NOT IN(select p.riskId from RiskTrans p where p.policy.policyId=:polCode)")
	 Page<PolicyActiveRisks> getUnendorsedRisksByInsured(@Param("insured")Long insured,@Param("polCode")Long polCode,@Param("riskId")String riskId,Pageable pageable);

	@Query("select s from PolicyActiveRisks s where s.arId = :riskId")
	 PolicyActiveRisks getActiveRisks(@Param("riskId")Long riskId);

	@Query(value = "select risk_code,risk_status,ar_pol_id,ar_prev_risk_id,ar_risk_id  from sys_brk_act_risks where sys_brk_act_risks.ar_pol_id =:polId",nativeQuery = true)
	List<Object[]> getPolicyActRisks(@Param("polId") Long polId);

	@Query(value = "select distinct sbc.client_id ,concat(sbc.client_fname,' ',sbc.client_onames) as names ,COUNT(*) OVER() as total_rows  from sys_brk_act_risks\n" +
			"join sys_brk_risks sbr on sbr.risk_id  = ar_risk_id \n" +
			"join sys_brk_clients sbc on sbc.client_id  = sbr.risk_insured_id \n" +
			"where ar_pol_id  = :polId \n" +
			"and (concat(sbc.client_fname,' ',sbc.client_onames) like :search)\n" +
			"order by sbc.client_id\n" +
			" OFFSET :pageNo*:limit limit :limit ",nativeQuery = true)
	List<Object[]> findActiveRiskInsured(@Param("polId") Long polId,
								  @Param("search") String search,
								  @Param("pageNo") int pageNo,
								  @Param("limit") int limit);





}
