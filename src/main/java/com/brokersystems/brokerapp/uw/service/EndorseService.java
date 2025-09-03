package com.brokersystems.brokerapp.uw.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import com.brokersystems.brokerapp.uw.dtos.ClientsDto;
import com.brokersystems.brokerapp.uw.dtos.EndorsementsDTO;
import com.brokersystems.brokerapp.uw.dtos.PolicyEnquiryDTO;
import com.brokersystems.brokerapp.uw.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.exception.EndorsementsException;
import com.brokersystems.brokerapp.setup.model.User;

public interface EndorseService {

	 DataTablesResult<EndorsementsDTO> findActivePolicyTrans(DataTablesRequest request, String drNumber,
			Long clientCode,String polNo,String endorseNumber,Long agentCode, String endorseType) throws IllegalAccessException;

	DataTablesResult<EndorsementsDTO> findCancelledActivePolicyTrans(DataTablesRequest request, String drNumber,
															Long clientCode,String polNo,String endorseNumber,Long agentCode, String endorseType) throws IllegalAccessException;

	public DataTablesResult<EndorsementsDTO> findActiveMedicalPolicyTrans(DataTablesRequest request, String drNumber,
															   Long clientCode,String polNo,String endorseNumber,Long agentCode) throws IllegalAccessException;

	 DataTablesResult<PolicyTrans> findActiveLifePolicyTrans(DataTablesRequest request,  String clientName, String polNo, String agentName) throws IllegalAccessException;
	 Long countUnauthTransactions(String policyNumber);

	 Long reviseTransaction(RevisionForm revisionForm) throws EndorsementsException;

	 Long reinstatate(ReinstateForm reinstateForm) throws EndorsementsException;

	 Long contraPolicy(RevisionForm revisionForm) throws EndorsementsException;

	Long contraLifePolicy(RevisionForm revisionForm) throws EndorsementsException;


	DataTablesResult<PolicyEnquiryDTO> findUnauthorisedPolicies(DataTablesRequest request, String policyNumber) throws IllegalAccessException;

	void deletePolicyRecord(Long polCode,boolean renewals) throws BadRequestException;


	public Page<ClientsDto> findActiveInsureds(String paramString, Pageable paramPageable, Long polCode);

	public Long findRiskExpiredSections(Long polCode) throws BadRequestException;

	public DataTablesResult<PolicyActiveRisks> findActiveRisks(DataTablesRequest request, Long insured,
			String riskId, Long polCode) throws IllegalAccessException;

	 void endorseRisk(Long activeRiskCode, String endorseType, BigDecimal amount) throws BadRequestException;

	public void endorseContraRisk(Long activeRiskCode) throws BadRequestException;


	public void endorseReuseContraRisk(Long activeRiskCode) throws BadRequestException;

	public DataTablesResult<EndorsementsDTO> findActiveAndCancelledTrans(DataTablesRequest request, String drNumber,
                                                                         String clientName, String polNo, String endorseNumber, String agentName) throws IllegalAccessException;

	public DataTablesResult<EndorsementsDTO> findActiveAndCancelledLifeTrans(DataTablesRequest request, String drNumber,
																		 String clientName, String polNo, String endorseNumber, String agentName) throws IllegalAccessException;


	public DataTablesResult<EndorsementsDTO> findContradTransactions(DataTablesRequest request, String drNumber,
			String clientName,String polNo,String endorseNumber,String agentName) throws IllegalAccessException;


	public PolicyTrans reuseOfContra(RevisionForm revisionForm) throws EndorsementsException;

	public Boolean getProdutGroup(Long polCode);

	 Long renewPolicy(RenewalsForm renForm) throws EndorsementsException;

	 Long renewPolicy(RenewalsForm renForm,User user) throws EndorsementsException;

	public DataTablesResult<PolicyTrans> findRenewalPolicies(DataTablesRequest request, Date wefDate,Date wetDate,Long productCode,
			Long branchId,Long  agentId,Long  bindCode,Long riskId,Long clientId) throws IllegalAccessException;

	public DataTablesResult<PolicyTrans> findRenewalProgress(DataTablesRequest request, Date wefDate,Date wetDate,Long productCode,
			Long branchId,Long  agentId,Long  bindCode,Long riskId,Long clientId) throws IllegalAccessException;


	 String processBatchRenewals(BatchRenewalForm renewalForm) throws BadRequestException, InterruptedException, ExecutionException;

	public String makeReadyBatchRenewals(BatchRenewalForm renewalForm) throws BadRequestException, InterruptedException, ExecutionException;

	public String authorizeBatchRenewals(BatchRenewalForm renewalForm) throws BadRequestException, InterruptedException, ExecutionException;

	public Page<RiskTrans> findRenewalRisks(String paramString, Pageable paramPageable);

    PolicyTrans getErrorsPol(Long policyId);
}
