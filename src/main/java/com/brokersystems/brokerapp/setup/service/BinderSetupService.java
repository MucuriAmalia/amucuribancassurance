
package com.brokersystems.brokerapp.setup.service;

import java.util.List;

import com.brokersystems.brokerapp.life.model.LifeCommissionRates;
import com.brokersystems.brokerapp.life.model.LifeSubAgentCommissionRates;
import com.brokersystems.brokerapp.medical.model.BinderMedicalCards;
import com.brokersystems.brokerapp.medical.model.BinderRatesTable;
import com.brokersystems.brokerapp.medical.model.CardTypes;
import com.brokersystems.brokerapp.medical.model.MedicalCovers;
import com.brokersystems.brokerapp.setup.dto.AccountsDTO;
import com.brokersystems.brokerapp.setup.dto.AdminFeeDTO;
import com.brokersystems.brokerapp.setup.dto.PremRatesDTO;
import com.brokersystems.brokerapp.setup.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;

public interface BinderSetupService {
	Page<AccountsDTO> findInsuranceAccounts(String term, String insuranceType, Pageable pageable);


//	public Page<AccountDef> findInsuranceAccounts(String paramString,  String insuranceType, Pageable paramPageable);
	public Page<AccountDef> findInsuranceAccountsReport(String paramString, Pageable paramPageable);
	public Page<AccountDef> findInsuranceAgentAccounts(String paramString, Pageable paramPageable);

	DataTablesResult<BindersDef> findInsuranceBinders(DataTablesRequest request, Long accCode)
			throws IllegalAccessException;

	void createBinder(BindersDef binder) throws BadRequestException;


	@Transactional(readOnly = true)
	BindersDef getBinderDetails(Long binId) throws BadRequestException;

	long binCount(Long binId);

	DataTablesResult<BinderQuestionnaire> findQuestionnaire(DataTablesRequest request, Long binCode) throws IllegalAccessException;

	DataTablesResult<BinderQuestionnaireChoices> findQuestionsChoices(DataTablesRequest request, Long quizId) throws IllegalAccessException;

	void defineQuestionnaire(BinderQuestionnaire questionnaire) throws BadRequestException;

	void defineQuestionnaireChoices(BinderQuestionnaireChoices questionnaireChoices);

	void cloneBinder(BindersDef binder) throws BadRequestException;

	DataTablesResult<BinderDetails> findBinderDetails(DataTablesRequest request, Long bindCode)
			throws IllegalAccessException;

	DataTablesResult<BinderRatesTable> findBinderRates(DataTablesRequest request, Long bindCode)
			throws IllegalAccessException;

	void createBinderDetails(BinderDetails det) throws BadRequestException;

	Page<BindersDef> findAllBinders(String paramString, Pageable paramPageable);

	DataTablesResult<PremRatesDef> findDetPremRates(DataTablesRequest request, Long detCode)
			throws IllegalAccessException;

	void createPremRates(PremRatesDef rates) throws BadRequestException;

	public Page<ProductSubclasses> findProdSubclassSel(String paramString, Pageable paramPageable, Long prodCode);

	public Page<CoverTypesDef> findCoverTypesSel(String paramString, Pageable paramPageable, Long subCode);

	public Page<CardTypes> findCardTypesSel(String paramString, Pageable paramPageable, Long cardId);

	public Page<SubCoverTypeSections> findCoverSectionSel(String paramString, Pageable paramPageable, Long covCode);

	public Page<ProductsDef> findProductsSel(String paramString, Pageable paramPageable);
	public Page<ProductsDef> findGenProductsSel(String paramString, Pageable paramPageable);

	public Page<ProductsDef> findGrpProductsSel(String paramString, String grpType, Pageable paramPageable);

	public Page<Currencies> findActiveCurrencies(String paramString, Pageable paramPageable);

	public void deleteBinder(long binCode);

	List<Object[]> findUnassignedSubCoverTypes(Long prodCode, Long bindCode) throws IllegalAccessException;

	void createBinderDetails(BinderDetailsBean binderBean);

	public void deleteBinderDetails(long detId);

	public void deletePremRates(long premId) throws BadRequestException;

	DataTablesResult<BinderClauses> findBinderClauses(DataTablesRequest request, Long detCode)
			throws IllegalAccessException;

	public List<SubclassClauses> findUnassignedSubClauses(Long detCode, Long subCode, String subName) throws IllegalAccessException;

	public void createBinderClauses(SubclassClauseBean subclassClause);

	public void deletePremRatesTable(Long id) throws BadRequestException;

	public void updateBinderClause(BinderClauses binderClauses) throws BadRequestException;

	public void deleteBinderClause(long clauseId);

	DataTablesResult<CommissionRates> findCommRates(DataTablesRequest request, Long binId) throws IllegalAccessException;

	DataTablesResult<SubAgentCommissionRates> findSubCommRates(DataTablesRequest request, Long binId) throws IllegalAccessException;

	DataTablesResult<LifeCommissionRates> findLifeCommRates(DataTablesRequest request, Long premItemCode) throws IllegalAccessException;

	DataTablesResult<LifeSubAgentCommissionRates> findSubAgentLifeCommRates(DataTablesRequest request, Long premItemCode) throws IllegalAccessException;

	void createCommissionRates(CommissionRates commission) throws BadRequestException;

	void createBinderAdminFeeConfig(AdminFeeDTO adminFeeDTO) throws BadRequestException;

	void createSubAgentCommRates(SubAgentCommissionRates commission) throws BadRequestException;

	void createLifeCommissionRates(LifeCommissionRates commission) throws BadRequestException;

	void createLifeSubAgentCommissionRates(LifeSubAgentCommissionRates commission) throws BadRequestException;

	public void deleteCommRates(long commId);

	public void deleteSubAgentCommRates(long commId);

	void deleteLifeCommRates(long commId);

	void deleteAdminFeeRates(long commId);

	void deleteSubAgentLifeCommRates(long commId);

	public Page<RevenueItemsDef> getCommRatesRevenueItems(String paramString, Pageable paramPageable, Long binId);

	public Page<SectionsDef> findSubclassSections(String paramString, Pageable paramPageable, Long subCode);

	DataTablesResult<BinderSectionPerils> findBinderSectionPerils(DataTablesRequest request, Long detCode, Long sectCode)
			throws IllegalAccessException;

	public List<SubclassPerils> findUnassignedPerils(Long detCode, Long subCode, Long sectCode, String perilName) throws IllegalAccessException;

	void createBinderSectPerils(BinderPerilsBean perilsBean);

	public void deleteBinderSectPeril(long sectPerilId);

	void defineBinderRatesTable(BinderRatesTable ratesTable) throws BadRequestException;

	DataTablesResult<MedicalCovers> findDetMedCovers(DataTablesRequest request, Long detCode)
			throws IllegalAccessException;

	public DataTablesResult<BinderMedicalCards> findDetBinCardTypes(DataTablesRequest request, Long detCode) throws IllegalAccessException;

	void createMedicalCover(MedicalCovers medicalCover) throws BadRequestException;

	public void createBinderCardTypes(BinderMedicalCards medicalCards) throws BadRequestException;

	void deleteMedCover(long coverId) throws BadRequestException;

	void deleteBinderCard(long binCardId) throws BadRequestException;

	DataTablesResult<BinderReqrdDocs> findBinderReqDocs(DataTablesRequest request, Long detCode)
			throws IllegalAccessException;

	List<Object[]> findUnassignedReqDocs(Long subCode, Long detCode) throws IllegalAccessException;

	void createBinderRequiredDocs(RequiredDocsBean docsBean);

	public void updateBinderReqrdDocs(BinderReqrdDocs reqrdDocs) throws BadRequestException;

	void deleteBinderReqDoc(long docId) throws BadRequestException;

	void defineBinderPremRatesTable(PremRatesTable ratesTable) throws BadRequestException;

	DataTablesResult<PremRatesDTO> findBinderPremRates(DataTablesRequest request, Long detCode);

	DataTablesResult<AdminFeeDTO> findAdminFeeConfig(DataTablesRequest request, Long detCode);

	public Page<Checks> findBinderChecks(String paramString, Pageable paramPageable, Long bindCode);

	public List<Object[]> findClausesDef(String paramString, long subId, long sectCode);

	public DataTablesResult<SectionLimits> findPremRatesSectionLimits(DataTablesRequest request, Long premId) throws IllegalAccessException;

	void createPremLimits(ClausesBean docsBean);

	public void updatePremLimits(SectionLimits limits) throws BadRequestException;

	void deletePremLimits(long limitId) throws BadRequestException;

	void deleteQuestionChoice(Long choiceCode) throws BadRequestException;

	void deleteQuestion(Long quizCode) throws BadRequestException;


	List<SingleQuizModel> findAllQuestions(Long binCode) throws IllegalAccessException;

	public Page<BinderQuestionnaire> findBinderQuestions(String paramString, Long binCode, Pageable paramPageable);

	public Page<BinderQuestionnaireChoices> findBinderQuestionChoice(String paramString, Long quizCode, Pageable paramPageable);

	void makeReady(Long binId) throws BadRequestException;

	void makeBinUndo(Long binId);

	void makeBinAuthorise(Long binId);

	void makeBinUnAuthorise(Long id);

	void downloadRatesTable(Long id, HttpServletResponse response) throws BadRequestException;

	BindersDef getDeactivate(Long id);

	@Transactional
	void createCompSheetNames(SubclassComputationSheetNames sheetNames) throws BadRequestException;

	@Transactional(readOnly = true)
	DataTablesResult<SubclassComputationSheetNames> findSheetNames(DataTablesRequest request, Long detCode) throws IllegalAccessException;

	@Transactional
	void deleteCompSheets(Long shtId);

	/**
	 * Validates that all subclasses of a product have proper revenue item mappings
	 * @param productCode The product code to validate
	 * @throws BadRequestException if any subclass lacks proper revenue mappings
	 */
	void validateProductRevenueMapping(Long productCode) throws BadRequestException;

	/**
	 * Gets all subclasses for a product and their mapping status
	 * @param productCode The product code
	 * @return List of subclass IDs that belong to this product
	 */
	List<Long> getProductSubclassIds(Long productCode);
}

