package com.brokersystems.brokerapp.uw.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.brokersystems.brokerapp.setup.model.PremRatesDef;
import com.brokersystems.brokerapp.setup.model.SubClassDef;
import com.brokersystems.brokerapp.uw.model.SectionTrans;


public interface SectionTransRepo extends PagingAndSortingRepository<SectionTrans, Long>, QueryDslPredicateExecutor<SectionTrans> {
	@Query("select p from PremRatesDef p where p.binderDet.detId=:detId and p.active = true and lower(p.section.desc) like %:searchVal%  and p NOT IN(select s.premRates from p.sectionTrans s where s.risk.riskId=:riskId)")
	List<PremRatesDef> getUnassignedPremItems(@Param("detId") Long var1, @Param("riskId") Long var2, @Param("searchVal") String var3);

	@Query("select p from PremRatesDef p where p.binderDet.detId=:detId and p.active = true and lower(p.section.desc) like %:searchVal% and :rate between p.rangeFrom and p.rangeTo and  p NOT IN(select s.premRates from p.sectionTrans s where s.risk.riskId=:riskId)")
	List<PremRatesDef> getRangePremRates(@Param("detId") Long var1, @Param("riskId") Long var2, @Param("searchVal") String var3, @Param("rate") BigDecimal var4);

	@Query("select s from SectionTrans s where s.sectId=:id")
	SectionTrans findSectionTransInfo(@Param("id") Long var1);

	@Query(value = " select sect_id ,sec.sc_desc  " +
			" from sys_brk_rsk_limits sbrl  " +
			" inner join sys_brk_risks sbr on sbrl.sect_risk_id = sbr.risk_id  " +
			" inner join sys_brk_bind_sect_prls sbbsp on sbbsp.bsp_det_code = sbr.risk_binder_det_id and sbrl.sect_sec_id = sbbsp.bsp_sect_code  " +
			" inner join sys_brk_sections sec on  sbrl.sect_sec_id = sec.sc_id  " +
			" where sect_risk_id = :riskId  and sbbsp.bsp_id = :peril",nativeQuery = true)
	List<Object[]> findExpireSection(@Param("riskId") Long riskId, @Param("peril") Long perilId);

	@Query(value = "select  sect_id,sect_amount,sect_calc_prem,sect_div_fact,sect_free_limit,sect_multi_rate,\n" +
			"sect_prem,sect_rate,sect_sec_id,sect_prem_id,sect_risk_id, sbs.sc_desc,sbs.sc_sht_desc,sbs.sc_type \n" +
			"from sys_brk_rsk_limits s join sys_brk_risks sbr on sect_risk_id=risk_id \n" +
			"join sys_brk_sections sbs on sbs.sc_id =sect_sec_id where" +
			" s.sect_risk_id=:id ",nativeQuery = true)
	List<Object[]> findRiskSectionTrans(@Param("id") Long var1);

	@Query(value = "select  sect_id,sect_amount,sect_calc_prem,sect_div_fact,sect_free_limit,sect_multi_rate,\n" +
			"sect_prem,sect_rate,sect_sec_id,sect_prem_id,sect_risk_id, sbs.sc_desc,sbs.sc_sht_desc,sbs.sc_type,sbr.risk_pol_id,sbr.risk_subclass_id \n" +
			"from sys_brk_rsk_limits s join sys_brk_risks sbr on sect_risk_id=risk_id \n" +
			"join sys_brk_sections sbs on sbs.sc_id =sect_sec_id where" +
			" s.sect_id=:id ",nativeQuery = true)
	List<Object[]> findSectionTransById(@Param("id") Long var1);

	@Modifying
	@Query(value = "update sys_brk_rsk_limits set sect_prem = :prem, sect_calc_prem = :calcPrem where sect_id = :sectId", nativeQuery = true)
	void updateSectionDetails(@Param("prem") BigDecimal prem,
							  @Param("calcPrem") BigDecimal calcPrem,
							  @Param("sectId") Long sectId);

	@Modifying
	@Query(value = "update sys_brk_rsk_limits set sect_prem = :prem, sect_calc_prem = :calcPrem, sect_amount =:sumAssured where sect_id = :sectId", nativeQuery = true)
	void updateSectionSumAssuredDetails(@Param("prem") BigDecimal prem,
							  @Param("calcPrem") BigDecimal calcPrem,
							  @Param("sumAssured") BigDecimal sumAssured,
							  @Param("sectId") Long sectId);

	@Modifying
	@Query(value = "update sys_brk_rsk_limits set sect_amount =:sumAssured where sect_id = :sectId", nativeQuery = true)
	void updateSectionSumAssured(@Param("sumAssured") BigDecimal sumAssured,
										@Param("sectId") Long sectId);

	@Modifying
	@Query("delete from SectionTrans s where s.premRates.Id = :premId")
	void deleteByPremRates_PremId(@Param("premId") Long premId);


}

