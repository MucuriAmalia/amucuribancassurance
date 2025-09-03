package com.brokersystems.brokerapp.uw.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.brokersystems.brokerapp.claims.model.ClaimBookings;
import com.brokersystems.brokerapp.claims.model.ClaimPerils;
import com.brokersystems.brokerapp.life.model.PolicyInstallments;
import com.brokersystems.brokerapp.schedules.model.ScheduleTrans;
import com.brokersystems.brokerapp.server.exception.AdminFeeException;
import com.brokersystems.brokerapp.setup.dto.*;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.trans.model.ReceiptTrans;
import com.brokersystems.brokerapp.trans.model.ReceiptTransDtls;
import com.brokersystems.brokerapp.trans.model.TransChecks;
import com.brokersystems.brokerapp.users.dto.MakerCheckDTO;
import com.brokersystems.brokerapp.uw.dtos.*;
import com.brokersystems.brokerapp.uw.model.*;
import com.brokersystems.brokerapp.webservices.model.VehicleDetails;
import com.brokersystems.brokerapp.workflow.docs.SysWfDocs;
import com.brokersystems.brokerapp.workflow.dto.WorkFlowDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

public interface PolicyTransService {

	MileageDTO findMileageDetails(String riskId);
	
	 DataTablesResult<WorkFlowDTO> findUserPolicies(DataTablesRequest request);

	public DataTablesResult<SysWfDocs> findSearchTickets(DataTablesRequest request, String policyNo,String quoteNo, String preparedBy)
			throws IllegalAccessException;

	public DataTablesResult<PolicyTrans> findUserMedicalTrans(DataTablesRequest request)
			throws IllegalAccessException;
	
	public DataTablesResult<EndorsementsDTO> findEnquiryPolicies(DataTablesRequest request, String searchTerm);

	DataTablesResult<PolicyTrans> findActiveEnquiryPolicies(DataTablesRequest request, String drNumber,
													  Long clientCode,String polNo,String endorseNumber,Long agentCode,Long prodCode) throws IllegalAccessException;


	public DataTablesResult<PolicyTrans> findEnquiryMedPolicies(DataTablesRequest request, String drNumber,
																Long clientCode, String polNo, String endorseNumber, Long agentCode,
																Long prodCode) throws IllegalAccessException;

	DataTablesResult<LapsePoliciesDTO> findEnquiryActiveorLapsedMedPolicies(DataTablesRequest request,
																			Long clientCode, String polNo, Long agentCode,
																			Long prodCode, String riskShtDesc, String drNumber) throws IllegalAccessException;

	public DataTablesResult<PolicyTrans> findPendingPolicies(DataTablesRequest request, String drNumber,
															 Long clientCode,String polNo,String endorseNumber,Long agentCode,Long prodCode) throws IllegalAccessException;
	
	Page<ClientsDto> findActiveClients(String paramString, Pageable paramPageable);

	Page<ProspectsDTO> findActiveProspect(String paramString, Pageable paramPageable);

	Page<BinderDTO> findInsuranceBinder(String paramString, Pageable paramPageable,String bindType, Long productId);
	Page<BinderDTO> findLifeInsuranceBinder(String paramString, Pageable paramPageable,String bindType, Long productId);
	Page<BinderDTO> findCompInsuranceBinder(String paramString, Pageable paramPageable,String bindType, Long quoteId);

	public Page<ProductsDef> findMultiProducts(String paramString, Pageable paramPageable);

	public Page<PolicyTrans> findClientPolicies(String paramString, Pageable paramPageable, Long clientId);

	public Page<BindersDef> findLifeBinder(String paramString, Pageable paramPageable, String bindType);

	public Page<BindersDef> findAllBinders(String paramString, Pageable paramPageable);

	 Page<CurrencyDTO> findCurrencies(String paramString, Pageable paramPageable);

	 Page<CurrencyDTO> findOtherCurrencies(String paramString, Pageable paramPageable);

	 Page<PaymentModesDTO> findPaymentModes(String paramString, Pageable paramPageable);

	 Page<AccountDef> findInhouseAgents(String paramString, Pageable paramPageable);

	Page<AccountsDTO> findInhouseAgentsDto(String paramString, Pageable paramPageable);

	 Page<AccountDef> findIntroducerAgents(String paramString, Pageable paramPageable);

	Page<AccountsDTO> findIntroducerAgentsDto(String paramString, Pageable paramPageable);

	 Page<AccountDef> findMarketerAgents(String paramString, Pageable paramPageable);

	 Page<OrgRegionsDTO> findOrgRegions(String paramString, Pageable paramPageable);

	 Page<MakerCheckDTO> findTaskTypes(String paramString, Pageable paramPageable);

	 Page<MakerCheckDTO> findAllCheckers(String paramString, Pageable paramPageable);

	 Page<User> findleadsMan(String paramString, Pageable paramPageable);

	Page<LeadmanDto> findleadsManDto(String paramString, Pageable paramPageable);

	Page<BranchDTO> findUserBranches(String paramString, Pageable paramPageable);

	Page<BranchDTO> findAllBranches(String paramString, Pageable paramPageable);

	public Page<PolicyTrans> findAllPolicies(String paramString, Pageable paramPageable);
	
	public Page<SubClassDef> findBinderSubclasses(String paramString, Pageable paramPageable,Long bindCode);
	
	public Page<CoverTypesDef> findBinderCoverTypes(String paramString, Pageable paramPageable,Long bindCode,Long subCode);

	public Page<CoverTypesDef> findBinderSubCoverTypes(String paramString, Pageable paramPageable,Long bindCode);
	
	public Set<RiskSectionBean> getBinderPremRates(Long detId);

	 Set<RiskSectionBean> getBinderClientPremRates(Long detId,Long insuredAge);

	public List<PremRatesDef> getNewSectPremiumItems(Long detId,Long riskId,String searchVal,Long insuredAge);

	public List<PremRatesDef> getNewPremiumItems(Long detId,Long riskId,String searchVal); 
	
	 PolicyTrans createPolicy(PolicyCreateDTO policy, boolean isApproved) throws BadRequestException;

	public PolicyTrans createLifePolicy(PolicyCreateDTO policy, boolean isApproved) throws BadRequestException;

	public DataTablesResult<PolicyBinders> findPolicyBinders(DataTablesRequest request, Long polCode)
			throws IllegalAccessException;
	
	DataTablesResult<RiskTransDTO> findRiskTransactions(DataTablesRequest request, Long polCode, Long bindCode)
			throws IllegalAccessException;

	DataTablesResult<RiskTransDTO> findActiveRiskTransactions(DataTablesRequest request, Long polCode, Long insuredId)
			throws IllegalAccessException;
	
	public DataTablesResult<SectionTrans> findRiskSections(DataTablesRequest request, Long riskId)
			throws IllegalAccessException;

    
	public PolicyTrans getPolicyDetails(Long polCode) throws BadRequestException;
	
	public Page<SectionBean> findPremSections(String paramString, Pageable paramPageable,Long detId);
	
	void createRiskSection(SectionTransDTO section) throws BadRequestException;
	
	public void deleteRiskSection(Long sectid,HttpServletRequest request) throws BadRequestException;
	
	public DataTablesResult<PolicyTaxes> findPolicyTaxes(DataTablesRequest request, Long polCode)
			throws IllegalAccessException;

	 DataTablesResult<PolicyClauses> findPolicyClauses(DataTablesRequest request, Long polCode)
			throws IllegalAccessException;
	
	 void populateTaxes(PolicyTrans policy) throws BadRequestException;
	
	 List<ClauseDTO> getNewClauses(Long polId) throws BadRequestException;

	public Set<PolicyTaxes> getNewTaxes(Long polId) throws BadRequestException;
	
	public void populateClauses(PolicyTrans policy) throws BadRequestException;
	
	void createRisk(CreateRiskDTO risk, HttpServletRequest request) throws BadRequestException;

	 void createLifeRisk(CreateRiskDTO risk, HttpServletRequest request) throws BadRequestException;

	public void deleteRisk(Long riskId, HttpServletRequest request) throws BadRequestException;
	
	public String makeReady(Long polCode) throws BadRequestException;

	public String makeLifeReady(Long polCode) throws BadRequestException;
    public String makeBulkLifeReady(Long polCode, Long userID) throws BadRequestException;

	public void makeRenewalReady(Long polCode) throws BadRequestException;

	public void makeMedicalReady(Long polCode) throws BadRequestException;

	@PreAuthorize("hasAnyAuthority('MAKE_POLICY_READY')")
	@Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
	void undoProposalConversion(Long polCode, Long reasonId, String reason) throws BadRequestException;

	public void undoMakeReady(Long polCode, Long reasonId, String reason) throws BadRequestException;

    public void convertBulkPropToPolicy(Long polCode) throws BadRequestException ;


	public void convertPropToPolicy(Long polCode) throws BadRequestException ;
	
	public void createRiskSections(RiskBean sections,HttpServletRequest request) throws BadRequestException;
	
	public void deletePolicyClause(Long clauseId) throws BadRequestException;
	
	public void deletePolicyTax(Long taxId);
	
	public void createClause(PolicyClausesBean clause) throws BadRequestException;

	public void createTaxes(PolicyTaxBean taxBean) throws BadRequestException;
	
	public void createPolicyClause(PolicyClauses clause) throws BadRequestException;
	
	public void createPolicyTaxes(PolicyTaxes policyTax) throws BadRequestException;
	
	public DataTablesResult<EndorsementRemarks> findEndorsementRemarks(DataTablesRequest request, Long polCode)
			throws IllegalAccessException;

	DataTablesResult<PolicyInstallments> findPolicyInstallments(DataTablesRequest request, Long polCode)
			throws IllegalAccessException;
	
	public PolicyRemarks getPolicyRemarks(Long polCode);

	String getRefundComments(Long polCode);
	
	public void saveEndorsementRemarks(PolicyRemarks remarks) throws BadRequestException;

	public DataTablesResult<ScheduleTrans> findRiskSchedules(DataTablesRequest request, Long riskId)
			throws IllegalAccessException;

	void createRiskSchedules(VehicleDetails scheduleTrans) throws BadRequestException;

	Map<String, Object> getRiskSchedules(Long riskId);

	@Transactional
	void insertOrUpdateDataIntoCustomTable(String tableName, Map<String, Object> data) throws BadRequestException;

	@Transactional
	void deleteScheduleData(Integer priCode, String tableName) throws BadRequestException;

	public void deleteRiskSchedule(Long scheduleId);

	public void populateRiskScheduleDetails(Long riskId) throws BadRequestException;

	public BigDecimal getCommissionRate(Long binId) throws BadRequestException;

	public BigDecimal getSubAgentCommissionRate(Long binId, Long accId) throws BadRequestException;

	public BigDecimal getMarketerCommissionRate(Long binId, Long accId) throws BadRequestException;

	Long createAdminFeeTrans(AdminFeeForm adminFeeForm) throws AdminFeeException;

	public DataTablesResult<AdminFee> findUnauthTrans(DataTablesRequest request)
			throws IllegalAccessException;

	public DataTablesResult<AdminFee> findAuthorisedTrans(DataTablesRequest request)
			throws IllegalAccessException;

	public AdminFee getAdminFeeDetails(Long adminFeeId);

	public DataTablesResult<AdminFeePolicies> findAdminFeePolicies(DataTablesRequest request, Long adminFeeId)
			throws IllegalAccessException;

	public void addAdminFeePolicies(AdminFeePolicyBean feePolicyBean) throws BadRequestException;

	public List<Object[]> getAdminFeePolicies(Long clientId,Long adminFeeId) throws IllegalAccessException;

	public void authorizeAdminFee(Long adminFeeId) throws BadRequestException;

	public void dispatchDocuments(Long polCode);

	DataTablesResult<RiskDocsDTO> findRiskDocs(DataTablesRequest request, Long riskId)
			throws IllegalAccessException;

	DataTablesResult<RiskDocsDTO> findRiskRefundDocs(DataTablesRequest request, Long riskId)
			throws IllegalAccessException;

	public void validateRiskIdFormat(Long subCode,String riskId) throws BadRequestException;

	DataTablesResult<InterestedPartiesDTO> findRiskInterestedParties(DataTablesRequest request, Long riskId)
			throws IllegalAccessException;

	 List<InterestedParties> getNewInterestedParties(Long riskId) throws BadRequestException;

	public void createIntParties(RiskIntPartiesBean partiesBean) throws BadRequestException;

	public void deleteRiskIntParty(Long partId);

	void importExcelRiskTemplate(RiskUploadForm uploadForm) throws BadRequestException;

	public DataTablesResult<TransChecks> findPolicyChecks(DataTablesRequest request, Long polCode)
			throws IllegalAccessException;

	@Transactional
	void savePendingRisks(RiskUploadForm uploadForm) throws BadRequestException, IOException;

	@Transactional
	void linkPendingRisksToPolicy(Long polCode);

	public void approveException(Long checkId, Long PolicyId) throws BadRequestException;

	List<ReqDocsDTO> findUnassignedRiskDocs(Long riskId, String docName)  throws IllegalAccessException;

	void createRiskRequiredDocs(RequiredDocBean requiredDocBean);

	public DataTablesResult<RiskImportationLog> findPolicyImportationLog(DataTablesRequest request, Long policyId)
			throws IllegalAccessException;

	int countPolicies(Long clientCode) throws BadRequestException;

	public void questionnaireCompleted(PolicyTrans policy) throws BadRequestException;

	void lapsePolicy(Long polCode) throws BadRequestException;

	void unLapsePolicy(Long polCode) throws BadRequestException;

	public void savePolicyQuiz(QuestionnaireDTO questionnaireDTO) throws BadRequestException;
	public void deletePolicyQuiz(Long polCode);

    DataTablesResult<RiskTrans> findEnquiryMaster(DataTablesRequest pageable, Long polNo, Long riskId, Long idNo) throws IllegalAccessException;

    DataTablesResult<PolicyTrans> masterEnqPI(DataTablesRequest pageable, Long polNo, Long idNo) throws  IllegalAccessException;

	DataTablesResult<RiskTrans> findEnquiryPR(DataTablesRequest request, Long polNo, Long riskId) throws IllegalAccessException;

	DataTablesResult<RiskTrans> findEnquiryRI(DataTablesRequest pageable, Long idNo, Long riskId);

    DataTablesResult<PolicyTrans> masterEnqPol(DataTablesRequest pageable, Long polNo);

	DataTablesResult<PolicyTrans> masterEnqIdNo(DataTablesRequest pageable, Long idNo);

	DataTablesResult<RiskTrans> masterEnqRisk(DataTablesRequest pageable, Long policyId);

	DataTablesResult<RiskTrans> masterEnqUniqueId(DataTablesRequest pageable, Long riskId);

    DataTablesResult<ClaimPerils> masterEnqUniqueClaim(DataTablesRequest pageable, Long riskId);

    DataTablesResult<RiskTrans> masterEnqUniqueRisk(DataTablesRequest pageable, Long riskId);

	PolicyTrans findEnquiryId(Long idNo);

	PolicyTrans findEnquiryPol(Long polNo);

	RiskTrans findEnquiryRisk(Long riskId);

    RiskTrans findEnquiryRiskPol(String riskId, String polNo);

    RiskTrans findEnquiryRiskId(String riskId, Long idNo);

	PolicyTrans findEnquiryPolAndId(String polNo, Long idNo);

	RiskTrans checkAllParam(String polNo, Long idNo, String riskId);

    ClientDef findClient(Long clId);

	DataTablesResult<ClientDef> masterIdNo(DataTablesRequest pageable, Long idNo);

	Page<ClientDef> findAllClients(String term, Pageable pageable);

    Page<PolicyTrans> findAllPols(String term, Pageable pageable);

	Page<RiskTrans> allRisksLov(String term, Pageable pageable);

    DataTablesResult<ReceiptTrans> masterReceipts(DataTablesRequest pageable, Long idNo);

    ReceiptTrans getReceiptDetails(Long id);

    DataTablesResult<ReceiptTransDtls> getReceiptsDets(DataTablesRequest pageable, Long receiptId);

    DataTablesResult<OverpaidPolicyDTO> getOverpaidPolicies(DataTablesRequest request, Date dateFrom, Date dateTo, Long accountCode);

    ClaimBookings checkClaim(Long claim);

	 byte[] getPolicyDocument(Long prodCode) throws BadRequestException;

	String getPolicyDocumentType(Long docId) throws BadRequestException;

	DataTablesResult<VehicleDetails> findVehicleDetails(DataTablesRequest request, Long ipuCode) throws IllegalAccessException;

	void createPolicyAddonsInfo(PolicyTrans created, PolicyCreateDTO request);
}
