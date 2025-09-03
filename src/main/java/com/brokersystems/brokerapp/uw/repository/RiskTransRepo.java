package com.brokersystems.brokerapp.uw.repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.brokersystems.brokerapp.uw.model.RiskTrans;
import com.brokersystems.brokerapp.uw.model.SectionTrans;
import org.springframework.transaction.annotation.Transactional;


public interface RiskTransRepo  extends  PagingAndSortingRepository<RiskTrans, Long>, QueryDslPredicateExecutor<RiskTrans>{

	   @Query("select t from RiskTrans t where t.policy.policyId=:polId")
		public List<RiskTrans> getRiskDetails(@Param("polId") Long polId);

	@Query("select t from RiskTrans t where t.riskId=:riskId")
	  RiskTrans QueryRiskTrans(@Param("riskId") Long riskId);
	   
	   @Query(value = "select t.* from sys_brk_rsk_limits t where t.sect_risk_id=:riskId", nativeQuery = true)
		public List<Object[]> getSectionDetails(@Param("riskId") Long riskId);

	@Query(value = "select t.risk_subclass_id from sys_brk_risks t where t.risk_id=:riskId",nativeQuery = true)
	 Long getSubclassCode(@Param("riskId") Long riskId);

	@Query(value = "select t.risk_code from sys_brk_risks t where t.risk_id=:riskId",nativeQuery = true)
	Long getRiskIdentifier(@Param("riskId") Long riskId);

	@Query(value = "select risk_id,risk_code,risk_pol_id,risk_subclass_id from sys_brk_risks s where s.risk_id=:riskId ",nativeQuery = true)
	List<Object[]> findRiskTrans(@Param("riskId") Long riskId);

	@Query(value = "select risk_subclass_id,sbp.pol_prod_id  from sys_brk_risks " +
			"                join sys_brk_policies sbp on risk_pol_id = sbp.pol_id " +
			"                where risk_pol_id  = :polId ",nativeQuery = true)
	List<Object[]> findPolicyRisks(@Param("polId") Long polId);

	@Query(value = "select risk_id,risk_code,risk_pol_id,risk_subclass_id from sys_brk_risks s where s.risk_id=:riskId ",nativeQuery = true)
	List<Object[]> findRiskDocTrans(@Param("riskId") Long riskId);

	@Query(value = "select risk_binder_det_id,risk_id,risk_code,risk_pol_id,\n" +
			"risk_subclass_id,sbc.client_dob,sbr.risk_prorata,sbr.risk_wef_date,sbr.risk_wet_date,sbr.risk_but_charge_prem," +
			" sbr.risk_basic_premium,sbr.risk_comm_rate,sbr.risk_installment_percentage,sbr.risk_install_amount,risk_pol_bind_id,risk_sht_desc,risk_binder_id," +
			"risk_subclass_id subclass_id,risk_whtx,risk_ph_fund,risk_tl,risk_stamp_duty,risk_extras,risk_subagent_comm_amt,risk_comm_amt," +
			"risk_subagent_comm_rate,risk_marketer_comm_rate,risk_introducer_comm_rate,risk_marketer_comm_amt,risk_introducer_comm_amt   from sys_brk_risks sbr\n" +
			"join sys_brk_clients sbc on sbr.risk_insured_id  = sbc.client_id \n" +
			"where risk_pol_id = :polId ",nativeQuery = true)
	List<Object[]> findPolicyRiskTrans(@Param("polId") Long polId);

	@Query(value = "select distinct pol_binder_id,pol_id,sbs.sub_id,sbc.client_dob,pol_wef_date,pol_wet_date," +
			"pol_total_prem,pol_total_whtx,pol_phcf,pol_training_levy,pol_stamp_duty,pol_extras," +
			"pol_sub_agent_amt,pol_comm_amt, pol_marketer_agent_amt,coalesce(sbp.pol_admin_fee_amt,0) admn_fee," +
			"coalesce(pol_admin_vat_amt,0) admin_whtx from sys_brk_policies sbp " +
			"left join sys_brk_clients sbc on sbp.pol_client_id = sbc.client_id " +
			"left join sys_brk_binder_det sbbd on sbp.pol_binder_id = sbbd.bdet_bin_code " +
			"left join sys_brk_sub_covertypes sbsc on sbbd.bdet_sub_covt_code = sbsc.sc_id " +
			"left join sys_brk_subclasses sbs on sbsc.sc_sub_code = sbs.sub_id " +
			"where pol_id = :polId",nativeQuery = true)
	List<Object[]> findPolicyTrans(@Param("polId") Long polId);
	public RiskTrans findFirstByRiskShtDesc(String riskId);
	public RiskTrans findFirstByInsured_TenIdAndPolicy_PolNoAndRiskShtDesc(Long idNo,String polNo,String riskId);
	public RiskTrans findFirstByPolicy_PolNoAndRiskShtDesc(String polNo,String riskId);
	public RiskTrans findFirstByInsured_TenIdAndRiskShtDesc(Long idNo,String riskId);

	@Query(value = "select sbc.client_dob,sbc.client_gender   from sys_brk_risks sbr  join sys_brk_clients sbc on sbr.risk_insured_id  = sbc.client_id \n" +
			"where sbr.risk_id =:riskId", nativeQuery = true)
	List<Object[]> getInsuredDate(@Param("riskId") Long riskId);

    RiskTrans findFirstByRiskId(Long riskId);

	//@Query(value = "select risk_id,pol_no,risk_sht_desc,concat(client_fname,' ',client_onames) as names,risk_binder_det_id,pol_binder_id,pol_id,COUNT(*) OVER() AS total_rows  from  sys_brk_policies\n" +
			//"                    join sys_brk_risks s2 on sys_brk_policies.pol_id = s2.risk_pol_id\n" +
			//"                     join sys_brk_clients client on s2.risk_insured_id = client.client_id\n" +
			//"                     where pol_current_status not in ('CO','D','CN')\n" +
			//"                     and pol_rev_status not in ('CN','CO','DC')\n" +
			//"                    and :clmdate between risk_wef_date and risk_wet_date\n" +
			//"                     and risk_sht_desc is not nulL\n" +
			//"                     and (risk_sht_desc like :search or pol_no like :search or  concat(client.client_fname,' ',client_onames) like :search)\n" +
			//"                     where (risk_sht_desc like :search or pol_no like :search or  concat(client.client_fname,' ',client_onames) like :search)\n" +
			//"                     order by risk_sht_desc DESC \n" +


	//peters
//	@Query(value = "SELECT \n" +
//			"    risk_id,\n" +
//			"    pol_no,\n" +
//			"    risk_sht_desc,\n" +
//			"    CONCAT(client_fname, ' ', client_onames) AS names,\n" +
//			"    risk_binder_det_id,\n" +
//			"    pol_binder_id,\n" +
//			"    pol_id,\n" +
//			"    COUNT(*) OVER() AS total_rows\n" +
//			"FROM sys_brk_policies\n" +
//			"JOIN sys_brk_risks s2 ON sys_brk_policies.pol_id = s2.risk_pol_id\n" +
//			"JOIN sys_brk_clients client ON s2.risk_insured_id = client.client_id\n" +
//			"where risk_sht_desc LIKE :search \n" +
//			"OR pol_no LIKE :search \n" +
//			"ORDER BY risk_sht_desc DESC OFFSET :pageNo*:limit LIMIT :limit ", nativeQuery = true)
//	List<Object[]> findClaimRisks(//@Param("clmdate") Date clmdate,
//									@Param("search") String search,
//									@Param("pageNo") int pageNo,
//									@Param("limit") int limit);


	@Query(value = "SELECT \n" +
			"    s2.risk_id,\n" +
			"    pol.pol_no,\n" +
			"    s2.risk_sht_desc,\n" +
			"    CONCAT(client.client_fname, ' ', client.client_onames) AS names,\n" +
			"    s2.risk_binder_det_id,\n" +
			"    pol.pol_binder_id,\n" +
			"    pol.pol_id,\n" +
			"    COUNT(*) OVER() AS total_rows\n" +
			"FROM sys_brk_policies pol\n" +
			"JOIN sys_brk_risks s2 ON pol.pol_id = s2.risk_pol_id\n" +
			"JOIN sys_brk_clients client ON s2.risk_insured_id = client.client_id\n" +
			"WHERE pol.pol_id IN (\n" +
			"    SELECT MAX(pol_inner.pol_id)\n" +
			"    FROM sys_brk_policies pol_inner\n" +
			"    JOIN sys_brk_risks s2_inner ON pol_inner.pol_id = s2_inner.risk_pol_id\n" +
			"    WHERE (s2_inner.risk_sht_desc LIKE :search OR pol_inner.pol_no LIKE :search)\n" +
			"    GROUP BY pol_inner.pol_no\n" +
			")\n" +
			"AND (s2.risk_sht_desc LIKE :search OR pol.pol_no LIKE :search)\n" +
			"ORDER BY s2.risk_sht_desc DESC\n" +
			"OFFSET :pageNo*:limit LIMIT :limit", nativeQuery = true)
	List<Object[]> findClaimRisks(
			@Param("search") String search,
			@Param("pageNo") int pageNo,
			@Param("limit") int limit);



	@Query(value = "select count(1) from sys_brk_risks where upper(risk_sht_desc) = :riskId",nativeQuery = true)
	Long checkDuplicateRisks(@Param("riskId") String riskId);

	@Query(value = "select pol_id,pol_no from sys_brk_risks join sys_brk_policies on risk_pol_id = pol_id  where upper(risk_sht_desc)= :riskId",nativeQuery = true)
	List<Object[]> getDuplicateRisks(@Param("riskId") String riskId);

	@Query(value = "select risk_id from sys_brk_risks where upper(risk_sht_desc) = :riskId",nativeQuery = true)
	List<BigDecimal> findDuplicateRisks(@Param("riskId") String riskId);

	@Query(value = "select risk_id,risk_sht_desc from sys_brk_risks where upper(risk_sht_desc) = :riskId and :dt between risk_wef_date and risk_wet_date ",nativeQuery = true)
	List<Object[]> getPolicyRisks(@Param("riskId") String riskId, @Param("dt") Date dt);


	@Query(value = "select risk_sht_desc from sys_brk_risks where risk_pol_id =:policyId",nativeQuery = true)
	List<String> getPolicyRegistrations(@Param("policyId") Long policyId);

	@Query(value = "select risk_id,risk_sht_desc,risk_desc,risk_wef_date,risk_wet_date,risk_subclass_id,\n" +
			"sbs.sub_desc,risk_cover_id,sbc.cov_desc,sbr.risk_sum_insur_amt,sbr.risk_basic_premium,pol_auth_status,risk_insured_id,risk_binder_det_id,\n" +
			"sbc2.client_fname,sbc2.client_onames,sbc2.client_id,risk_trans_type,risk_comm_rate,risk_but_charge_prem,risk_install_amount,risk_installment_no," +
			" risk_installment_percentage,risk_code,risk_auto_gen_cert,risk_prorata,risk_pol_bind_id,TO_CHAR(risk_wef_date, 'dd/mm/yyyy') wef_date," +
			"TO_CHAR(risk_wet_date, 'dd/mm/yyyy') wet_date,COUNT(*) OVER() as total_rows from sys_brk_risks sbr \n" +
			"join sys_brk_subclasses sbs on sbs.sub_id  = risk_subclass_id \n" +
			"join sys_brk_covertypes sbc on sbc.cov_id  = sbr.risk_cover_id \n" +
			"join sys_brk_policies sbp on sbp.pol_id  = sbr.risk_pol_id \n" +
			"join sys_brk_clients sbc2 on sbc2.client_id  = sbr.risk_insured_id \n" +
			"where (lower(coalesce(sbr.risk_sht_desc,'-2000')) like :search or lower(sbr.risk_desc) like :search)\n" +
			"and risk_pol_id=:polId\n" +
			"and coalesce(sbr.risk_binder_id ,-2000) = case when :binderId=-2000 then coalesce(sbr.risk_binder_id,-2000) else :binderId end\n" +
			"order by risk_id asc\n" +
			"OFFSET :pageNo*:limit limit :limit", nativeQuery = true)
	List<Object[]> findPolicyRisks(@Param("search") String search,
								   @Param("polId") Long polId,
								   @Param("binderId") Long binderId,
							   @Param("pageNo") int pageNo,
							   @Param("limit") int limit);

	@Query(value = "select risk_id,risk_sht_desc,risk_desc,risk_wef_date,risk_wet_date,risk_subclass_id,\n" +
			"sbs.sub_desc,risk_cover_id,sbc.cov_desc,sbr.risk_sum_insur_amt,sbr.risk_basic_premium,pol_auth_status,risk_insured_id,risk_binder_det_id,\n" +
			"sbc2.client_fname,sbc2.client_onames,sbc2.client_id,risk_trans_type,risk_comm_rate,risk_but_charge_prem,risk_install_amount,risk_installment_no," +
			" risk_installment_percentage,risk_code,risk_auto_gen_cert,risk_prorata,risk_pol_bind_id,TO_CHAR(risk_wef_date, 'dd/mm/yyyy') wef_date," +
			"TO_CHAR(risk_wet_date, 'dd/mm/yyyy') wet_date,client_dob,risk_compute_type, sbc2.client_idno,  COUNT(*) OVER() as total_rows from sys_brk_risks sbr \n" +
			"join sys_brk_subclasses sbs on sbs.sub_id  = risk_subclass_id \n" +
			"join sys_brk_covertypes sbc on sbc.cov_id  = sbr.risk_cover_id \n" +
			"join sys_brk_policies sbp on sbp.pol_id  = sbr.risk_pol_id \n" +
			"join sys_brk_clients sbc2 on sbc2.client_id  = sbr.risk_insured_id \n" +
			"where (lower(coalesce(sbr.risk_sht_desc,'-2000')) like :search or lower(sbr.risk_desc) like :search)\n" +
			"and risk_pol_id=:polId\n" +
			"and coalesce(sbr.risk_binder_id ,-2000) = case when :binderId=-2000 then coalesce(sbr.risk_binder_id,-2000) else :binderId end\n" +
			"order by risk_id asc\n" +
			"OFFSET :pageNo*:limit limit :limit", nativeQuery = true)
	List<Object[]> findLifePolicyRisks(@Param("search") String search,
								   @Param("polId") Long polId,
								   @Param("binderId") Long binderId,
								   @Param("pageNo") int pageNo,
								   @Param("limit") int limit);

	@Query(value = "select risk_id,risk_sht_desc,risk_desc,risk_wef_date,risk_wet_date,risk_subclass_id,\n" +
			"sbs.sub_desc,risk_cover_id,sbc.cov_desc,sbr.risk_sum_insur_amt,sbr.risk_basic_premium,pol_auth_status,risk_insured_id,risk_binder_det_id,\n" +
			"sbc2.client_fname,sbc2.client_onames,sbc2.client_id,risk_trans_type,risk_comm_rate,risk_but_charge_prem,risk_install_amount,risk_installment_no," +
			" risk_installment_percentage,risk_code,risk_auto_gen_cert,risk_prorata,risk_pol_bind_id,TO_CHAR(risk_wef_date, 'dd/mm/yyyy') wef_date," +
			"TO_CHAR(risk_wet_date, 'dd/mm/yyyy') wet_date,client_dob,risk_compute_type, sbc2.client_idno,  COUNT(*) OVER() as total_rows from sys_brk_risks sbr \n" +
			"join sys_brk_subclasses sbs on sbs.sub_id  = risk_subclass_id \n" +
			"join sys_brk_covertypes sbc on sbc.cov_id  = sbr.risk_cover_id \n" +
			"join sys_brk_policies sbp on sbp.pol_id  = sbr.risk_pol_id \n" +
			"join sys_brk_clients sbc2 on sbc2.client_id  = sbr.risk_insured_id \n" +
			"where risk_pol_id=:polId", nativeQuery = true)
	List<Object[]> findGeneralAndLifePolicyRisks(@Param("polId") Long polId);

	@Query(value = "select sbar.ar_id ,risk_sht_desc,risk_desc,risk_wef_date,risk_wet_date,risk_subclass_id,\n" +
			"sbs.sub_desc,risk_cover_id,sbc.cov_desc,sbr.risk_sum_insur_amt,sbr.risk_basic_premium,pol_auth_status,\n" +
			"COUNT(*) OVER() as total_rows from sys_brk_risks sbr \n" +
			"join sys_brk_act_risks sbar on sbar.ar_risk_id  = sbr.risk_id \n" +
			"join sys_brk_subclasses sbs on sbs.sub_id  = risk_subclass_id \n" +
			"join sys_brk_covertypes sbc on sbc.cov_id  = sbr.risk_cover_id \n" +
			"join sys_brk_policies sbp on sbp.pol_id  = sbr.risk_pol_id \n" +
			"where (lower(sbr.risk_sht_desc) like :search or lower(sbr.risk_desc) like :search)\n" +
			"and sbar.ar_pol_id   = :polId\n" +
			"and sbr.risk_id not in (select risk_id from sys_brk_risks where risk_pol_id = :polId )\n" +
			"and coalesce(sbr.risk_insured_id  ,-2000) = case when :insuredId=-2000 then coalesce(sbr.risk_insured_id,-2000) else :insuredId end\n" +
			"order by risk_id asc\n" +
			"OFFSET :pageNo*:limit limit :limit", nativeQuery = true)
	List<Object[]> findPolicyActiveRisks(@Param("search") String search,
								   @Param("polId") Long polId,
								   @Param("insuredId") Long insuredId,
								   @Param("pageNo") int pageNo,
								   @Param("limit") int limit);

	@Modifying
	@Query(value = "update sys_brk_risks set risk_basic_premium= :riskPrem, risk_cal_premium = :calcPrem, risk_sum_insur_amt = :riskInsured," +
			"risk_install_amount=:installAmt, risk_wet_date = :wetDate,risk_tot_perc=:totalPercent, risk_comm_amt = :comm, risk_subagent_comm_amt = :subAgentComm," +
			"risk_net_premium = :netPremium, risk_extras = :riskextras, risk_ph_fund = :riskphfFund, risk_tl = :riskTl, risk_stamp_duty = :riskstampDuty, risk_future_prem = :riskFuturePrem," +
			" risk_installment_percentage = :installmentPercentage where risk_id = :riskId",nativeQuery = true)
	 void updateRiskDetails(@Param("riskPrem") BigDecimal riskPrem,
								  @Param("calcPrem") BigDecimal calcPrem,
								  @Param("riskInsured") BigDecimal riskInsured,
								  @Param("installAmt") BigDecimal installAmt,
								  @Param("wetDate") Date wetDate,
								  @Param("totalPercent") BigDecimal totalPercent,
								  @Param("comm") BigDecimal comm,
								  @Param("subAgentComm") BigDecimal subAgentComm,
								  @Param("netPremium") BigDecimal netPremium,
								  @Param("riskextras") BigDecimal riskextras,
								  @Param("riskphfFund") BigDecimal riskphfFund,
								  @Param("riskTl") BigDecimal riskTl,
								  @Param("riskstampDuty") BigDecimal riskstampDuty,
								  @Param("riskFuturePrem") BigDecimal riskFuturePrem,
								  @Param("installmentPercentage") String installmentPercentage,
								  @Param("riskId") Long riskId
								  );

	@Modifying
	@Query(value = "update sys_brk_risks set risk_but_charge_prem= :riskPrem where risk_id = :riskId",nativeQuery = true)
	void updateRiskOverridePrem(@Param("riskPrem") BigDecimal riskPrem,
						   @Param("riskId") Long riskId
	);

	@Modifying
	@Query(value = "update sys_brk_risks set risk_wef_date= :wefDate,risk_wet_date=:wetDate where risk_id = :riskId",nativeQuery = true)
	void updateRiskDates(@Param("wefDate") Date wefDate,
								@Param("wetDate") Date wetDate,
						       @Param("riskId") Long riskId
	);

	@Query(value = "SELECT registered_table_name FROM x_registered_table " +
			"WHERE app_table_key_value = (SELECT risk_subclass_id FROM sys_brk_risks WHERE risk_id = :riskId)", nativeQuery = true)
	String getScheduleTable(@Param("riskId") Long riskId);

	@Query(value = "SELECT column_name, data_type, character_maximum_length, is_nullable FROM information_schema.columns WHERE table_name = :tableName", nativeQuery = true)
	List<Object[]> getTableModel(@Param("tableName") String tableName);

	@Query(value = "SELECT * FROM sys_brk_risks WHERE risk_pol_id = :policyId LIMIT 1", nativeQuery = true)
	RiskTrans findByPolicyId(@Param("policyId") Long policyId);

	@Query(value = "SELECT * FROM sys_brk_risks WHERE risk_pol_id = :bulkId ", nativeQuery = true)
	RiskTrans findByBulkPolicyId(@Param("bulkId") Long bulkId);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM sys_brk_act_risks " +
			"WHERE ar_risk_id IN ( " +
			"SELECT sbr.risk_id FROM sys_brk_risks sbr WHERE sbr.risk_pol_id = :bulkId)",
			nativeQuery = true)
	void deleteActRisksByBulkId(@Param("bulkId") Long bulkId);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM sys_brk_rsk_limits " +
			"WHERE sect_risk_id IN ( " +
			"SELECT r.risk_id FROM sys_brk_risks r WHERE r.risk_pol_id = :bulkId)",
			nativeQuery = true)
	void deleteLimitsByBulkId(@Param("bulkId") Long bulkId);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM \"MOTOR PRIVATE SCHEDULE\" mps " +
			"WHERE mps.s_brk_risks_id IN ( " +
			"SELECT r.risk_id FROM sys_brk_risks r WHERE r.risk_pol_id = :bulkId)",
			nativeQuery = true)
	void deleteFromMotorPrivateScheduleByBulkId(@Param("bulkId") Long bulkId);







}