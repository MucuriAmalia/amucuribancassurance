
package com.brokersystems.brokerapp.setup.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.brokersystems.brokerapp.enums.AccountTypeEnum;
import com.brokersystems.brokerapp.life.model.LifeCommissionRates;
import com.brokersystems.brokerapp.life.model.LifeSubAgentCommissionRates;
import com.brokersystems.brokerapp.life.model.QLifeCommissionRates;
import com.brokersystems.brokerapp.life.model.QLifeSubAgentCommissionRates;
import com.brokersystems.brokerapp.life.repository.LifeCommissionRatesRepo;
import com.brokersystems.brokerapp.life.repository.LifeSubAgentCommissionRatesRepo;
import com.brokersystems.brokerapp.medical.model.*;
import com.brokersystems.brokerapp.medical.repository.*;
import com.brokersystems.brokerapp.quotes.repository.QuotRiskLimitsRepo;
import com.brokersystems.brokerapp.server.utils.Streamable;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.setup.dto.AccountsDTO;
import com.brokersystems.brokerapp.setup.dto.AdminFeeDTO;
import com.brokersystems.brokerapp.setup.dto.PremRatesDTO;
import com.brokersystems.brokerapp.setup.dto.SubclassMappingValidation;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.*;
import com.brokersystems.brokerapp.setup.service.SetupsService;
import com.brokersystems.brokerapp.uw.repository.SectionTransRepo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brokersystems.brokerapp.enums.RevenueItems;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.service.BinderSetupService;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.BooleanExpression;

import javax.servlet.http.HttpServletResponse;

import com.brokersystems.brokerapp.setup.dto.SubclassMappingValidation;
import com.brokersystems.brokerapp.setup.service.SetupsService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.Modifying;

@Service
public class BinderSetupServiceImpl implements BinderSetupService {

    @PersistenceContext
    private EntityManager entityManager;

	@Autowired
	private BindersRepo binderRepo;

	@Autowired
	private BinderDetRepo binderDetRepo;

	@Autowired
	private PremRatesRepo premRepo;

	@Autowired
	private AccountRepo accountRepo;

	@Autowired
	private CoverTypesRepo coverRepo;

	@Autowired
	private ProdSubclassRepo prodSubRepo;

	@Autowired
	private ProductsRepo prodRepo;

	@Autowired
	private CardTypesRepo cardTypesRepo;

	@Autowired
	private CurrencyRepository currencyRepo;

	@Autowired
	private SubClassCoverRepo subCoverRepo;

	@Autowired
	private SubCoverSectRepo subcoverSecRepo;

	@Autowired
	private BinderClauseRepo clauseRepo;

	@Autowired
	private SubClausesRepo subclausRepo;

	@Autowired
	private CommRatesRepo commRatesRepo;

	@Autowired
	private SubAgentCommRepo subAgentCommRepo;

	@Autowired
	private RevenueItemsRepo revenueItemsRepo;

	@Autowired
	private SubSectionRepo subSectionRepo;

	@Autowired
	private BinderSectPerilsRepo binderSectPerilsRepo;

	@Autowired
	private SectionRepo sectionRepo;

	@Autowired
	private BinderLoadingsRepo loadingsRepo;

	@Autowired
	private ServiceProviderContractRepo providerContractRepo;


	@Autowired
	private SubClassPerilsRepo subClassPerilsRepo;

	@Autowired
	private BinderRatesTblRepo binderRatesTblRepo;

	@Autowired
	private MedicalCoversRepo medicalCoversRepo;

	@Autowired
	private BinderReqrdDocsRepo reqrdDocsRepo;

	@Autowired
	private SubclassReqDocRepo subclassReqDocRepo;

	@Autowired
	private PremRatesTableRepo premRatesTableRepo;

	@Autowired
	private AdminFeeSetUpRepo adminFeeSetUpRepo;

	@Autowired
	private BinderExclusionsRepo exclusionsRepo;


	@Autowired
	private UserUtils userUtils;

	@Autowired
	private MedicalRulesRepo medicalRulesRepo;

	@Autowired
	private ChecksRepo checksRepo;

	@Autowired
	private MedicalRulesRepo rulesRepo;

	@Autowired
	private ProductChecksRepo productChecksRepo;

	@Autowired
	private SectionLimitsRepo sectionLimitsRepo;


	@Autowired
	private BinderMedicalCardsRepo binderCardsRepo;

	@Autowired
	private LifeCommissionRatesRepo lifeCommissionRatesRepo;

	@Autowired
	private BinderQuestionnaireChoicesRepo questionnaireChoicesRepo;

	@Autowired
	private BinderQuestionnaireRepo questionnaireRepo;

	@Autowired
	private SubclassCompSheetNamesRepo sheetNamesRepo;
	@Autowired
	private LifeSubAgentCommissionRatesRepo lifeSubAgentCommissionRatesRepo;

	@Autowired
	private SetupsService setupsService;

	@Autowired
	private SectionTransRepo sectionTransRepo;

	@Autowired
	private QuotRiskLimitsRepo quotRiskLimitsRepo;

	@Autowired
	private PremRatesRepo premRatesRepo;


	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<PremRatesDef> findDetPremRates(DataTablesRequest request, Long detCode)
			throws IllegalAccessException {
		BooleanExpression pred = QPremRatesDef.premRatesDef.binderDet.detId.eq(detCode);
		Page<PremRatesDef> page = premRepo.findAll(pred.and(request.searchPredicate(QPremRatesDef.premRatesDef)), request);
		return new DataTablesResult<>(request, page);
	}
	@Override
	@Transactional(readOnly = false)
	public void deletePremRatesTable(Long id) throws BadRequestException {
		// Fetch the PremRatesTable by ID
		PremRatesTable premRatesTable = premRatesTableRepo.findOne(id);

		// Throw BadRequestException if the entity is not found
		if (premRatesTable == null) {
			throw new BadRequestException("Premium rates table with ID " + id + " not found");
		}

		// Delete the entity
		premRatesTableRepo.delete(id);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
	public void createPremRates(PremRatesDef rates) throws BadRequestException {
		if (rates.getRangeApplicable() != null && "on".equalsIgnoreCase(rates.getRangeApplicable())) {
			rates.setRangeApplicable("Y");
		} else rates.setRangeApplicable("N");
		if (rates.getMandatory() != null && "on".equalsIgnoreCase(rates.getMandatory())) {
			rates.setMandatory("Y");
		} else rates.setMandatory("N");

		long count = premRepo.count(QPremRatesDef.premRatesDef.active.eq(true)
				.and(QPremRatesDef.premRatesDef.section.id.eq(rates.getSection().getId()))
				.and(QPremRatesDef.premRatesDef.binderDet.eq(rates.getBinderDet()))
				.and(QPremRatesDef.premRatesDef.rangeApplicable.eq("N")));
		if(rates.getId()==null) {
			if ("Y".equalsIgnoreCase(rates.getRangeApplicable()) && count > 0) {
				throw new BadRequestException("Cannot Create Premium With Rate Applicable when A rate not applicable record already exists...");
			}
		}

		count = premRepo.count(QPremRatesDef.premRatesDef.active.eq(true)
				.and(QPremRatesDef.premRatesDef.section.id.eq(rates.getSection().getId()))
				.and(QPremRatesDef.premRatesDef.binderDet.eq(rates.getBinderDet()))
				.and(QPremRatesDef.premRatesDef.rangeApplicable.eq("Y")));
		if (("N".equalsIgnoreCase(rates.getRangeApplicable())||rates.getRangeApplicable() == null) && count > 0) {
			throw new BadRequestException("Cannot Create Premium With Rate Not Applicable when A rate applicable record already exists...");
		}

		if ("Y".equalsIgnoreCase(rates.getRangeApplicable())) {
			if (rates.getRangeFrom().compareTo(rates.getRangeTo()) == 1)
				throw new BadRequestException("Range from cannot be greater than Range to....");
		}
		if (rates.getRangeApplicable() != null && "Y".equalsIgnoreCase(rates.getRangeApplicable())) {
			if (rates.getRangeFrom().compareTo(BigDecimal.ZERO) == -1 || rates.getRangeTo().compareTo(BigDecimal.ZERO) == -1)
				throw new BadRequestException("Range From or Range To cannot be negative....");
			Predicate pred = QPremRatesDef.premRatesDef.binderDet.eq(rates.getBinderDet()).and(QPremRatesDef.premRatesDef.section.id.eq(rates.getSection().getId()))
					.and(QPremRatesDef.premRatesDef.rangeFrom.between(rates.getRangeFrom(), rates.getRangeTo())
							.or(QPremRatesDef.premRatesDef.rangeTo.between(rates.getRangeFrom(), rates.getRangeTo())));
			if (rates.getId() == null) {
				if (premRepo.count(pred) > 0) {
					throw new BadRequestException("Selected Section Premium Rates has already been setup");
				}
			} else {
				if (premRepo.count(pred) > 1) {
					throw new BadRequestException("Selected Section Premium Rates has already been setup");
				}
			}
		}else {
			count = premRepo.count(QPremRatesDef.premRatesDef.active.eq(true)
					.and(QPremRatesDef.premRatesDef.section.id.eq(rates.getSection().getId()))
					.and(QPremRatesDef.premRatesDef.binderDet.eq(rates.getBinderDet()))
					.and(QPremRatesDef.premRatesDef.rangeApplicable.eq("N")));
			if (rates.getId() == null) {
				if (count > 0) {
					throw new BadRequestException("Cannot Create Premium.Record already exists...");
				}
			}else
			if (count > 1 && rates.isActive()) {
				throw new BadRequestException("Cannot Create Premium.Record already exists...");
			}
		}
		if (rates.getRate().compareTo(BigDecimal.ZERO) == 0 || rates.getRate().compareTo(BigDecimal.ZERO) == -1) {
			throw new BadRequestException("Premium Rate cannot be zero or less than Zero");
		}

		premRepo.save(rates);
	}

//	@Override
//	@Transactional(readOnly = true)
//	public Page<AccountDef> findInsuranceAccounts(String term, String insuranceType, Pageable pageable) {
//		BooleanExpression predicate = QAccountDef.accountDef.accountType.accountType.eq(AccountTypeEnum.INS);
//
//		// Add name search if term is provided
//		if (StringUtils.isNotBlank(term)) {
//			predicate = predicate.and(QAccountDef.accountDef.name.containsIgnoreCase(term));
//		}
//
//		// Add insurance type filter
//		if (StringUtils.isNotBlank(insuranceType)) {
//			predicate = predicate.and(QAccountDef.accountDef.insuranceType.eq(insuranceType));
//		}
//
//		// Return filtered results
//		return accountRepo.findAll(predicate, pageable);
//	}

	@Override
	@Transactional(readOnly = true)
	public Page<AccountsDTO> findInsuranceAccounts(String term, String insuranceType, Pageable pageable) {
		// Handle search term - only search by name (matching your original logic)
		final String search = (StringUtils.isNotBlank(term)) ? "%" + term.toLowerCase() + "%" : null;

		List<Object[]> accounts = accountRepo.findInsuranceAccounts(search,
				StringUtils.isNotBlank(insuranceType) ? insuranceType : null,
				pageable.getPageNumber(),
				pageable.getPageSize());
		final List<AccountsDTO> accountDTOList = new ArrayList<>();
		long rowCount = 0l;

		if (!accounts.isEmpty()) rowCount = ((BigInteger) accounts.get(0)[4]).longValue();

		for (Object[] account : accounts) {
			AccountsDTO accountsDTO = new AccountsDTO();

			accountsDTO.setAcctId(((BigInteger) account[0]).longValue());    // acct_id
			accountsDTO.setName((String) account[1]);                       // acct_name
			accountsDTO.setShtDesc((String) account[2]);                    // acct_sht_desc
			accountsDTO.setAbsaNo((String) account[3]);                     // acct_absa_no
			// account[4] is total_rows

			accountDTOList.add(accountsDTO);
		}

		return new PageImpl<>(accountDTOList, pageable, rowCount);
	}
	@Override
	@Transactional(readOnly = true)
	public Page<AccountDef> findInsuranceAccountsReport(String paramString, Pageable paramPageable) {
		Predicate pred = null;
		if (paramString == null || StringUtils.isBlank(paramString)) {
			pred = QAccountDef.accountDef.isNotNull().and(QAccountDef.accountDef.accountType.accountType.eq(AccountTypeEnum.INS));
		} else {
			pred = QAccountDef.accountDef.name.containsIgnoreCase(paramString).and(QAccountDef.accountDef.accountType.accountType.eq(AccountTypeEnum.INS));
		}
		return accountRepo.findAll(pred, paramPageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<AccountDef> findInsuranceAgentAccounts(String paramString, Pageable paramPageable) {
		Predicate pred = null;
		if (paramString == null || StringUtils.isBlank(paramString)) {
			pred = QAccountDef.accountDef.isNotNull().and(QAccountDef.accountDef.accountType.accountType.eq(AccountTypeEnum.INS));
		} else {
			pred = QAccountDef.accountDef.name.containsIgnoreCase(paramString).and(QAccountDef.accountDef.accountType.accountType.eq(AccountTypeEnum.INS));
		}
		return accountRepo.findAll(pred, paramPageable);
	}

	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<BindersDef> findInsuranceBinders(DataTablesRequest request, Long accCode)
			throws IllegalAccessException {
		BooleanExpression pred = QBindersDef.bindersDef.account.acctId.eq(accCode);
		Page<BindersDef> page = binderRepo.findAll(pred.and(request.searchPredicate(QBindersDef.bindersDef)), request);
		return new DataTablesResult<>(request, page);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
	public void createBinder(BindersDef binder) throws BadRequestException {
		if (binder.getAdminFeeActiveStatus() == null || binder.getAdminFeeActiveStatus().isEmpty()) {
			binder.setAdminFeeActiveStatus("N");
		}
		if (binder.getFundBinder() == null) {
			binder.setFundBinder("N");
		} else if ("on".equalsIgnoreCase(binder.getFundBinder())) {
			if (binder.getBinId() == null) {
				if (binderRepo.count(QBindersDef.bindersDef.fundBinder.equalsIgnoreCase("Y")) > 0) {
					throw new BadRequestException("Default Medical Binder for Fund already defined");
				}
			} else {
				BindersDef bindersDef = binderRepo.findOne(binder.getBinId());
				if (bindersDef.getFundBinder() != null && "N".equalsIgnoreCase(bindersDef.getFundBinder())) {
					if (binderRepo.count(QBindersDef.bindersDef.fundBinder.equalsIgnoreCase("Y")) > 0) {
						throw new BadRequestException("Default Medical Binder for Fund already defined");
					}
				}

			}
			binder.setFundBinder("Y");
		} else binder.setFundBinder("N");

		if (binder.getProduct() == null) {
			throw new BadRequestException("Binder Product is compulsory");
		}
		// *** NEW VALIDATION: Check product revenue mappings ***
		try {
			validateProductRevenueMapping(binder.getProduct().getProCode());
		} catch (BadRequestException e) {
			// Re-throw with more context
			throw new BadRequestException("Product validation failed: " + e.getMessage());
		}

		if (!"MD".equalsIgnoreCase(binder.getProduct().getProGroup().getPrgType())) {
			if (binder.getFundBinder() != null && "Y".equalsIgnoreCase(binder.getFundBinder()))
				throw new BadRequestException("Fund is only applicable to Medical Product");
		}
		binderRepo.save(binder);

	}
	@Override
	@Transactional(readOnly = true)
	public BindersDef getBinderDetails(Long binId) throws BadRequestException {
		BindersDef bindersDef = binderRepo.findOne(binId);
		if (bindersDef != null) {
			bindersDef.setBinId(bindersDef.getBinId());
			bindersDef.setBinName(bindersDef.getBinName());
			bindersDef.setBinType(bindersDef.getBinType());
			bindersDef.setBinRemarks(bindersDef.getBinRemarks());
			bindersDef.setBinShtDesc(bindersDef.getBinShtDesc());
			bindersDef.setProduct(bindersDef.getProduct());
			bindersDef.setAccount(bindersDef.getAccount());
			bindersDef.setCurrency(bindersDef.getCurrency());
			bindersDef.setMinPrem(bindersDef.getMinPrem());
			bindersDef.setMinTerm(bindersDef.getMinTerm());
			bindersDef.setMaxTerm(bindersDef.getMaxTerm());
			bindersDef.setPremiumAgeType(bindersDef.getPremiumAgeType());
			bindersDef.setMedicalCoverType(bindersDef.getMedicalCoverType());
			bindersDef.setFundBinder(bindersDef.getFundBinder());
			bindersDef.setBinDefault(bindersDef.isBinDefault());
			bindersDef.setCalculatorType(bindersDef.getCalculatorType());
			bindersDef.setAdminFeeActiveStatus(bindersDef.getAdminFeeActiveStatus());
			bindersDef.setPremiumUrl(bindersDef.getPremiumUrl());
			bindersDef.setPolicyUrl(bindersDef.getPolicyUrl());
			return bindersDef;

		} else {
			return null;
		}

	}

	@Override
	public long binCount(Long binId) {
		long count = binderRepo.count(QBindersDef.bindersDef.binId.eq(binId).and(QBindersDef.bindersDef.isNotNull()));
		return count;
	}


	@Override
	public DataTablesResult<BinderQuestionnaire> findQuestionnaire(DataTablesRequest request, Long binCode) throws IllegalAccessException {
		BooleanExpression pred = QBinderQuestionnaire.binderQuestionnaire.binder.binId.eq(binCode);
		Page<BinderQuestionnaire> page = questionnaireRepo.findAll(pred.and(request.searchPredicate(QBinderQuestionnaire.binderQuestionnaire)), request);
		return new DataTablesResult<>(request, page);
	}


	@Override
	public DataTablesResult<BinderQuestionnaireChoices> findQuestionsChoices(DataTablesRequest request, Long quizId) throws IllegalAccessException {
		BooleanExpression pred = QBinderQuestionnaireChoices.binderQuestionnaireChoices.questions.bqdid.eq(quizId);
		Page<BinderQuestionnaireChoices> page = questionnaireChoicesRepo.findAll(pred.and(request.searchPredicate(QBinderQuestionnaireChoices.binderQuestionnaireChoices)), request);
		return new DataTablesResult<>(request, page);
	}


	@Override
	public void defineQuestionnaire(BinderQuestionnaire questionnaire) throws BadRequestException {
		if (questionnaire.getQuestionShtDesc() == null) {
			throw new BadRequestException("Provide question Id");
		}
		if (questionnaire.getQuestionname() == null) {
			throw new BadRequestException("Provide question");
		}
		if (questionnaire.getQuestionMandatory() == null) {
			questionnaire.setQuestionMandatory("N");
		}
		questionnaireRepo.save(questionnaire);
	}

	@Override
	public void defineQuestionnaireChoices(BinderQuestionnaireChoices questionnaireChoices) {
		questionnaireChoicesRepo.save(questionnaireChoices);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
	public void cloneBinder(BindersDef binder) throws BadRequestException {
//		if(binderRepo.countCheckIfProductExists(binder.getCloneAccId(),binder.getCloneProdId()) > 0){
//			throw new BadRequestException("Product Contract for the Selected Insurance Company already exists");
//		}
		BindersDef copyfrom = binderRepo.findOne(binder.getCloneFromBinCode());


		if (binder.getCloneProdId() == null) {
			throw new BadRequestException("Binder Product is compulsory");
		} else {
			binder.setProduct(prodRepo.findOne(binder.getCloneProdId()));
		}
		binder.setFundBinder(copyfrom.getFundBinder());
		if (!"MD".equalsIgnoreCase(binder.getProduct().getProGroup().getPrgType())) {
			if (binder.getFundBinder() != null && "Y".equalsIgnoreCase(binder.getFundBinder()))
				throw new BadRequestException("Fund is only applicable to Medical Product");
		}
		binder.setAccount(accountRepo.findOne(binder.getCloneAccId()));
		binder.setBinName(binder.getCloneBinName());
		binder.setBinRemarks(binder.getCloneBinRemarks());
		binder.setBinShtDesc(binder.getCloneBinShtDesc());
		binder.setBinType(binder.getCloneBinType());
		binder.setCreatedDate(new Date());
		binder.setCurrency(copyfrom.getCurrency());
		if (binder.getProduct().getProGroup().getPrgType().equalsIgnoreCase("L")) {
			binder.setMinTerm(binder.getMinTerm());
			binder.setMaxTerm(binder.getMaxTerm());
			binder.setActive(binder.isActive());
		}
		BindersDef createdBin = binderRepo.save(binder);
		Iterable<AdminFeeSetUp> adminFeeSetUps = adminFeeSetUpRepo.findAll(QAdminFeeSetUp.adminFeeSetUp.binder.binId.eq(binder.getCloneFromBinCode()));
		for (AdminFeeSetUp adminFeeSetUp:adminFeeSetUps) {
			AdminFeeSetUp adminFeeSetUp1 = new AdminFeeSetUp();
			adminFeeSetUp1.setStatus(adminFeeSetUp.getStatus());
			adminFeeSetUp1.setAdminFeeRateType(adminFeeSetUp.getAdminFeeRateType());
			adminFeeSetUp1.setBinder(binderRepo.findOne(createdBin.getBinId()));
			adminFeeSetUp1.setVateRateType(adminFeeSetUp.getVateRateType());
			adminFeeSetUp1.setExciseRateType(adminFeeSetUp.getExciseRateType());
			adminFeeSetUp1.setAdminFeeRate(adminFeeSetUp.getAdminFeeRate());
			adminFeeSetUp1.setVatRate(adminFeeSetUp.getVatRate());
			adminFeeSetUp1.setExciseRate(adminFeeSetUp.getExciseRate());
			adminFeeSetUpRepo.save(adminFeeSetUp1);
		}
		if (!binder.getProduct().getProGroup().getPrgType().equalsIgnoreCase("L")) {
			Iterable<CommissionRates> commissionRatesList = commRatesRepo.findAll(QCommissionRates.commissionRates.bindersDef.binId.eq(binder.getCloneFromBinCode()));
			List<CommissionRates> commissionRatesList1 = new ArrayList<>();
			for (CommissionRates commissionRates:commissionRatesList) {
				CommissionRates newCommRates = new CommissionRates();
				newCommRates.setCreatedDate(new Date());
				newCommRates.setBindersDef(binderRepo.findOne(createdBin.getBinId()));
				newCommRates.setActive(commissionRates.isActive());
				newCommRates.setCommDivFactor(commissionRates.getCommDivFactor());
				newCommRates.setCommRangeFrom(commissionRates.getCommRangeFrom());
				newCommRates.setRateType(commissionRates.getRateType());
				newCommRates.setCommRangeTo(commissionRates.getCommRangeTo());
				newCommRates.setCommRate(commissionRates.getCommRate());
				newCommRates.setRateDesc(commissionRates.getRateDesc());
				newCommRates.setRevenueItems(commissionRates.getRevenueItems());
				commissionRatesList1.add(newCommRates);
			}
			commRatesRepo.save(commissionRatesList1);

			Iterable<SubAgentCommissionRates> subAgentCommissionRatesList = subAgentCommRepo.findAll(QSubAgentCommissionRates.subAgentCommissionRates.bindersDef.binId.eq(binder.getCloneFromBinCode()));
			List<SubAgentCommissionRates> subAgentCommissionRatesList1 = new ArrayList<>();
			for (SubAgentCommissionRates subAgentCommissionRates:subAgentCommissionRatesList) {
				SubAgentCommissionRates newSubAgentCommRates = new SubAgentCommissionRates();
				newSubAgentCommRates.setCreatedDate(new Date());
				newSubAgentCommRates.setBindersDef(binderRepo.findOne(createdBin.getBinId()));
				newSubAgentCommRates.setActive(subAgentCommissionRates.isActive());
				newSubAgentCommRates.setCommDivFactor(subAgentCommissionRates.getCommDivFactor());
				newSubAgentCommRates.setCommRangeFrom(subAgentCommissionRates.getCommRangeFrom());
				newSubAgentCommRates.setRateType(subAgentCommissionRates.getRateType());
				newSubAgentCommRates.setCommRangeTo(subAgentCommissionRates.getCommRangeTo());
				newSubAgentCommRates.setCommRate(subAgentCommissionRates.getCommRate());
				newSubAgentCommRates.setRateDesc(subAgentCommissionRates.getRateDesc());
				newSubAgentCommRates.setRevenueItems(subAgentCommissionRates.getRevenueItems());
				newSubAgentCommRates.setAccountTypes(subAgentCommissionRates.getAccountTypes());
				subAgentCommissionRatesList1.add(newSubAgentCommRates);
			}
			subAgentCommRepo.save(subAgentCommissionRatesList1);
		} else {
			Iterable<LifeCommissionRates> commissionRatesList = lifeCommissionRatesRepo.findAll(QLifeCommissionRates.lifeCommissionRates.binderDef.binId.eq(binder.getCloneFromBinCode()));
			List<LifeCommissionRates> commissionRatesList1 = new ArrayList<>();
			for (LifeCommissionRates commissionRates: commissionRatesList) {
				LifeCommissionRates newCommRates = new LifeCommissionRates();
				newCommRates.setCreatedDate(new Date());
				newCommRates.setBinderDef(binderRepo.findOne(createdBin.getBinId()));
				newCommRates.setCommTermFrom(commissionRates.getCommTermFrom());
				newCommRates.setCommDivFactor(commissionRates.getCommDivFactor());
				newCommRates.setCommTermTo(commissionRates.getCommTermTo());
				newCommRates.setCommYearFrom(commissionRates.getCommYearFrom());
				newCommRates.setCommYearTo(commissionRates.getCommYearTo());
				newCommRates.setCommRate(commissionRates.getCommRate());
				newCommRates.setFrequency(commissionRates.getFrequency());
				newCommRates.setWefDate(commissionRates.getWefDate());
				newCommRates.setWetDate(commissionRates.getWetDate());
				commissionRatesList1.add(newCommRates);
			}
			lifeCommissionRatesRepo.save(commissionRatesList1);
			Iterable<LifeSubAgentCommissionRates> lifeSubAgentCommissionRatesList= lifeSubAgentCommissionRatesRepo.findAll(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.binderDef.binId.eq(binder.getCloneFromBinCode()));
			List<LifeSubAgentCommissionRates> lifeSubAgentCommissionRatesList1 = new ArrayList<>();
			for (LifeSubAgentCommissionRates lifeSubAgentCommissionRates :lifeSubAgentCommissionRatesList) {
				LifeSubAgentCommissionRates newCommRates = new LifeSubAgentCommissionRates();
				newCommRates.setCreatedDate(new Date());
				newCommRates.setBinderDef(binderRepo.findOne(createdBin.getBinId()));
				newCommRates.setCommTermFrom(lifeSubAgentCommissionRates.getCommTermFrom());
				newCommRates.setCommDivFactor(lifeSubAgentCommissionRates.getCommDivFactor());
				newCommRates.setCommTermTo(lifeSubAgentCommissionRates.getCommTermTo());
				//newCommRates.setCommYearFrom(lifeSubAgentCommissionRates.getCommYearFrom());
				//newCommRates.setCommYearTo(lifeSubAgentCommissionRates.getCommYearTo());
				newCommRates.setCommRate(lifeSubAgentCommissionRates.getCommRate());
				newCommRates.setFrequency(lifeSubAgentCommissionRates.getFrequency());
				newCommRates.setWefDate(lifeSubAgentCommissionRates.getWefDate());
				newCommRates.setWetDate(lifeSubAgentCommissionRates.getWetDate());
				lifeSubAgentCommissionRatesList1.add(newCommRates);
			}
			lifeSubAgentCommissionRatesRepo.save(lifeSubAgentCommissionRatesList1);

		}
		Iterable<BinderDetails> copyfromdetails = binderDetRepo.findAll(QBinderDetails.binderDetails.binder.binId.eq(binder.getCloneFromBinCode()));
		List<BinderDetails> newBinderDetails = new ArrayList<>();
		for (BinderDetails details : copyfromdetails) {
			BinderDetails newBinDetails = new BinderDetails();
			newBinDetails.setCreatedDate(new Date());
			newBinDetails.setBinder(binderRepo.findOne(createdBin.getBinId()));
			newBinDetails.setRemarks(details.getRemarks());
			newBinDetails.setSubCoverTypes(details.getSubCoverTypes());
			BinderDetails createdBinderDetails = binderDetRepo.save(newBinDetails);
			Iterable<PremRatesDef> premRatesDefs = premRepo.findAll(QPremRatesDef.premRatesDef.binderDet.detId.eq(details.getDetId()));
			List<PremRatesDef> premRt = new ArrayList<>();
			for (PremRatesDef premRates : premRatesDefs) {
				PremRatesDef newpremRatesDefs = new PremRatesDef();
				newpremRatesDefs.setCreatedDate(new Date());
				newpremRatesDefs.setActive(premRates.isActive());
				newpremRatesDefs.setBinderDet(binderDetRepo.findOne(createdBinderDetails.getDetId()));
				newpremRatesDefs.setDivFactor(premRates.getDivFactor());
				newpremRatesDefs.setFreeLimit(premRates.getFreeLimit());
				newpremRatesDefs.setMandatory(premRates.getMandatory());
				newpremRatesDefs.setMaxrate(premRates.getMaxrate());
				newpremRatesDefs.setMinPremium(premRates.getMinPremium());
				newpremRatesDefs.setMinRate(premRates.getMinRate());
				newpremRatesDefs.setMultiDivFactor(premRates.getMultiDivFactor());
				newpremRatesDefs.setMultiRate(premRates.getMultiRate());
				newpremRatesDefs.setRangeType(premRates.getRangeType());
				newpremRatesDefs.setProratedFull(premRates.getProratedFull());
				newpremRatesDefs.setRangeFrom(premRates.getRangeFrom());
				newpremRatesDefs.setRangeTo(premRates.getRangeTo());
				newpremRatesDefs.setRate(premRates.getRate());
				newpremRatesDefs.setRangeApplicable(premRates.getRangeApplicable());
				newpremRatesDefs.setRateType(premRates.getRateType());
				newpremRatesDefs.setSection(premRates.getSection());
				newpremRatesDefs.setSubSections(premRates.getSubSections());
				premRt.add(newpremRatesDefs);

			}
			Iterable<PremRatesTable> ratesTables = premRatesTableRepo.findAll(QPremRatesTable.premRatesTable.binderDetails.detId.eq(details.getDetId()));
			List<PremRatesTable> ratesTables1 = new ArrayList<>();
			for (PremRatesTable rateTable : ratesTables) {
				PremRatesTable newRateTable = new PremRatesTable();
				newRateTable.setBinderDetails(binderDetRepo.findOne(createdBinderDetails.getDetId()));
				newRateTable.setEffDate(rateTable.getEffDate());
				newRateTable.setFile(rateTable.getFile());
				newRateTable.setFileName(rateTable.getFileName());
				newRateTable.setRate_table(rateTable.getRate_table());
				ratesTables1.add(newRateTable);


			}
			Iterable<MedicalCovers> medicalCov = medicalCoversRepo.findAll(QMedicalCovers.medicalCovers.binderDet.detId.eq(details.getDetId()));
			List<MedicalCovers> medicalCoversList = new ArrayList<>();
			for (MedicalCovers medcovers : medicalCov) {
				MedicalCovers newMedCovers = new MedicalCovers();
				newMedCovers.setBinderDet(binderDetRepo.findOne(createdBinderDetails.getDetId()));
				newMedCovers.setMinPremium(medcovers.getMinPremium());
				newMedCovers.setSection(medcovers.getSection());
				newMedCovers.setApplicableAt(medcovers.getApplicableAt());
				newMedCovers.setDependsOnGender(medcovers.isDependsOnGender());
				newMedCovers.setFundCoverMand(medcovers.getFundCoverMand());
				newMedCovers.setGender(medcovers.getGender());
				newMedCovers.setLimitAmount(medcovers.getLimitAmount());
				newMedCovers.setMainCover(medcovers.getMainCover());
				newMedCovers.setMainSection(medcovers.getMainSection());
				newMedCovers.setProratedFull(medcovers.getProratedFull());
				newMedCovers.setWaitPeriod(medcovers.getWaitPeriod());
				medicalCoversList.add(newMedCovers);

			}
			Iterable<BinderReqrdDocs> reqrdDocs = reqrdDocsRepo.findAll(QBinderReqrdDocs.binderReqrdDocs.binderDetail.detId.eq(details.getDetId()));
			List<BinderReqrdDocs> binderReqrdDocs = new ArrayList<>();
			for (BinderReqrdDocs reqrdDocs1 : reqrdDocs) {
				BinderReqrdDocs newReqrsDocs = new BinderReqrdDocs();
				newReqrsDocs.setBinderDetail(binderDetRepo.findOne(createdBinderDetails.getDetId()));
				newReqrsDocs.setBrdId(reqrdDocs1.getBrdId());
				newReqrsDocs.setMandatory(reqrdDocs1.isMandatory());
				newReqrsDocs.setRequiredDocs(reqrdDocs1.getRequiredDocs());
				binderReqrdDocs.add(newReqrsDocs);

			}
			Iterable<BinderClauses> clauses = clauseRepo.findAll(QBinderClauses.binderClauses.binderDet.detId.eq(details.getDetId()));
			List<BinderClauses> binderClauses1 = new ArrayList<>();

			for (BinderClauses binderClauses : clauses) {
				BinderClauses newClauses = new BinderClauses();
				newClauses.setCreatedDate(new Date());
				newClauses.setBinderDet(binderDetRepo.findOne(createdBinderDetails.getDetId()));
				newClauses.setClauHeading(binderClauses.getClauHeading());
				newClauses.setMandatory(binderClauses.getMandatory());
				newClauses.setClause(binderClauses.getClause());
				newClauses.setClauWording(binderClauses.getClauWording());
				newClauses.setEditable(binderClauses.getEditable());
				binderClauses1.add(newClauses);

			}

			Iterable<BinderSectionPerils> perils = binderSectPerilsRepo.findAll(QBinderSectionPerils.binderSectionPerils.binderDetail.detId.eq(details.getDetId()));
			List<BinderSectionPerils> binderPerils = new ArrayList<>();
			for (BinderSectionPerils peril : perils) {
				BinderSectionPerils newPeril = new BinderSectionPerils();
				newPeril.setBinderDetail(binderDetRepo.findOne(createdBinderDetails.getDetId()));
				newPeril.setSectionsDef(peril.getSectionsDef());
				newPeril.setSubclassPeril(peril.getSubclassPeril());
				binderPerils.add(newPeril);

			}
			premRepo.save(premRt);
			premRatesTableRepo.save(ratesTables1);
			medicalCoversRepo.save(medicalCoversList);
			reqrdDocsRepo.save(binderReqrdDocs);
			clauseRepo.save(binderClauses1);
			binderSectPerilsRepo.save(binderPerils);
		}
		Iterable<BinderRatesTable> binderRatesTables = binderRatesTblRepo.findAll(QBinderRatesTable.binderRatesTable.binder.binId.eq(binder.getCloneFromBinCode()));
		List<BinderRatesTable> newnewRateTables = new ArrayList<>();
		for (BinderRatesTable ratesTable : binderRatesTables) {
			BinderRatesTable newRateTable = new BinderRatesTable();
			newRateTable.setBinder(binderRepo.findOne(createdBin.getBinId()));
			newRateTable.setEffDate(ratesTable.getEffDate());
			newRateTable.setFile(ratesTable.getFile());
			newRateTable.setFileName(ratesTable.getFileName());
			newRateTable.setRate_table(ratesTable.getRate_table());
			newRateTable.setTableType(ratesTable.getTableType());
			newnewRateTables.add(newRateTable);
		}
		Iterable<MedicalBinderRules> binderRules = medicalRulesRepo.findAll(QMedicalBinderRules.medicalBinderRules.binder.binId.eq(binder.getCloneFromBinCode()));
		List<MedicalBinderRules> newBinderRules = new ArrayList<>();
		for (MedicalBinderRules binRules : binderRules) {
			MedicalBinderRules newBinRules = new MedicalBinderRules();
			newBinRules.setBinder(binderRepo.findOne(createdBin.getBinId()));
			newBinRules.setDesc(binRules.getDesc());
			newBinRules.setMandatory(binRules.getMandatory());
			newBinRules.setShtDesc(binRules.getShtDesc());
			newBinRules.setValue(binRules.getValue());
			newBinderRules.add(newBinRules);
		}
		Iterable<BinderExclusions> exclusions = exclusionsRepo.findAll(QBinderExclusions.binderExclusions.binder.binId.eq(binder.getCloneFromBinCode()));
		List<BinderExclusions> newExclusions = new ArrayList<>();
		for (BinderExclusions exclusions1 : exclusions) {
			BinderExclusions newExclusion = new BinderExclusions();
			newExclusion.setClausesDef(exclusions1.getClausesDef());
			newExclusion.setMedicalnetworks(exclusions1.getMedicalnetworks());
			newExclusion.setAilment(exclusions1.getAilment());
			newExclusion.setBinder(binderRepo.findOne(createdBin.getBinId()));
			newExclusion.setAilmentDesc(exclusions1.getAilmentDesc());
			newExclusion.setBenDesc(exclusions1.getBenDesc());
			newExclusion.setChronic(exclusions1.getChronic());
			newExclusion.setClauWording(exclusions1.getClauWording());
			newExclusion.setCostPerClaim(exclusions1.getCostPerClaim());
			newExclusion.setUpperLimit(exclusions1.getUpperLimit());
			newExclusion.setWaitingDays(exclusions1.getWaitingDays());
			newExclusions.add(newExclusion);
		}
		Iterable<BinderLoadings> loadings = loadingsRepo.findAll(QBinderLoadings.binderLoadings.binder.binId.eq(binder.getCloneFromBinCode()));
		List<BinderLoadings> newBinderLoading = new ArrayList<>();
		for (BinderLoadings load : loadings) {
			BinderLoadings newLoad = new BinderLoadings();
			newLoad.setBinder(binderRepo.findOne(createdBin.getBinId()));
			newLoad.setWaitingDays(load.getWaitingDays());
			newLoad.setUpperLimit(load.getUpperLimit());
			newLoad.setCostPerClaim(load.getCostPerClaim());
			newLoad.setChronic(load.getChronic());
			newLoad.setAilment(load.getAilment());
			newLoad.setAilmentDesc(load.getAilmentDesc());
			newLoad.setLoadingAmt(load.getLoadingAmt());
			newLoad.setRate(load.getRate());
			newLoad.setRateType(load.getRateType());
			newBinderLoading.add(newLoad);
		}
		Iterable<ServiceProviderContracts> contracts = providerContractRepo.findAll(QServiceProviderContracts.serviceProviderContracts.binder.binId.eq(binder.getCloneFromBinCode()));
		List<ServiceProviderContracts> newContracts = new ArrayList<>();
		for (ServiceProviderContracts contract : contracts) {
			ServiceProviderContracts newContract = new ServiceProviderContracts();
			newContract.setBinder(binderRepo.findOne(createdBin.getBinId()));
			newContract.setApprovalDate(contract.getApprovalDate());
			newContract.setApproved(contract.isApproved());
			newContract.setContractNo(contract.getContractNo());
			newContract.setContractType(contract.getContractType());
			newContract.setIssueDate(contract.getIssueDate());
			newContract.setNotes(contract.getNotes());
			newContract.setServiceProviders(contract.getServiceProviders());
			newContract.setStatus(contract.getStatus());
			newContract.setWefDate(contract.getWefDate());
			newContract.setWetDate(contract.getWetDate());
			newContracts.add(newContract);
		}

		medicalRulesRepo.save(newBinderRules);
		binderRatesTblRepo.save(newnewRateTables);
		exclusionsRepo.save(newExclusions);
		loadingsRepo.save(newBinderLoading);
		providerContractRepo.save(newContracts);


	}


	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<BinderDetails> findBinderDetails(DataTablesRequest request, Long bindCode)
			throws IllegalAccessException {
		BooleanExpression pred = QBinderDetails.binderDetails.binder.binId.eq(bindCode);
		Page<BinderDetails> page = binderDetRepo.findAll(pred.and(request.searchPredicate(QBinderDetails.binderDetails)), request);
		return new DataTablesResult<>(request, page);
	}

	@Override
	public Page<BindersDef> findAllBinders(String paramString, Pageable paramPageable) {
		Predicate pred = null;
		if (paramString == null || StringUtils.isBlank(paramString)) {
			pred = QBindersDef.bindersDef.isNotNull().and(QBindersDef.bindersDef.active.eq(Boolean.TRUE));

		} else {
			pred = QBindersDef.bindersDef.binName.containsIgnoreCase(paramString)
					.or(QBindersDef.bindersDef.binShtDesc.containsIgnoreCase(paramString))
					.and(QBindersDef.bindersDef.active.eq(Boolean.TRUE));
		}
		return binderRepo.findAll(pred, paramPageable);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
	public void createBinderDetails(BinderDetails det) throws BadRequestException {

		BinderDetails details = binderDetRepo.findOne(det.getDetId());
		if (details.getInstallmentsNo() != null) {
			if (det.getDistribution() == null)
				throw new BadRequestException("Provide Installment Percentages");

			if (!det.getDistribution().contains(":")) {
				try {
					BigDecimal val = new BigDecimal(det.getDistribution());
					if (val.compareTo(BigDecimal.valueOf(100)) != 0) {
						throw new BadRequestException("Percentage Value must be 100");
					}
					if (val.compareTo(BigDecimal.valueOf(100)) == 0 && det.getInstallmentsNo() != 1) {
						throw new BadRequestException("Percentage Value must match the number of installments");
					}
				} catch (NumberFormatException ex) {
					throw new BadRequestException("Invalid Number " + det.getDistribution());
				}
			}
			String[] percentages = det.getDistribution().split(":");
			if (percentages.length != det.getInstallmentsNo()) {
				throw new BadRequestException("Installment Percentage must match the number of installments...");
			}
			BigDecimal total = BigDecimal.ZERO;
			for (String perc : percentages) {
				try {
					BigDecimal dist = new BigDecimal(perc);
					if (dist.compareTo(BigDecimal.ZERO) == 0)
						throw new BadRequestException("Installment Percentage cannot be zero");
					total = total.add(dist);
				} catch (NumberFormatException ex) {
					throw new BadRequestException("Invalid Number " + perc);
				}
			}

			if (total.compareTo(BigDecimal.valueOf(100)) != 0) {
				throw new BadRequestException("Installment Percentage Value must add up to 100");
			}
		}
		details.setDistribution(det.getDistribution());
		details.setInstallmentsNo(det.getInstallmentsNo());
		details.setRemarks(det.getRemarks());
		details.setMinPrem(det.getMinPrem());
		if ("on".equalsIgnoreCase(det.getLimitsAllowed())) {
			details.setLimitsAllowed("Y");
		} else details.setLimitsAllowed("N");
		if ("on".equalsIgnoreCase(det.getSingleSectionCover())) {
			details.setSingleSectionCover("Y");
		} else details.setSingleSectionCover("N");
		if ("on".equalsIgnoreCase(det.getPrimary())) {
			details.setPrimary("Y");
		} else details.setPrimary("N");
		binderDetRepo.save(details);

	}

	@Override
	@Transactional(readOnly = true)
	public Page<ProductSubclasses> findProdSubclassSel(String paramString, Pageable paramPageable, Long prodCode) {
		Predicate pred = null;
		if (paramString == null || StringUtils.isBlank(paramString)) {
			pred = QProductSubclasses.productSubclasses.product.proCode.eq(prodCode).and(QProductSubclasses.productSubclasses.isNotNull());
		} else {
			pred = QProductSubclasses.productSubclasses.product.proCode.eq(prodCode).and(QProductSubclasses.productSubclasses.subclass.subDesc.containsIgnoreCase(paramString));
		}
		return prodSubRepo.findAll(pred, paramPageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<CoverTypesDef> findCoverTypesSel(String paramString, Pageable paramPageable, Long subCode) {
		Predicate pred = null;
		if (paramString == null || StringUtils.isBlank(paramString)) {
			pred = QCoverTypesDef.coverTypesDef.isNotNull();
		} else {
			pred = QCoverTypesDef.coverTypesDef.covName.containsIgnoreCase(paramString);
		}
		return coverRepo.findAll(pred, paramPageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<CardTypes> findCardTypesSel(String paramString, Pageable paramPageable, Long cardId) {
		Predicate pred = null;
		if (paramString == null || StringUtils.isBlank(paramString)) {
			pred = QCardTypes.cardTypes.isNotNull();
		} else {
			pred = QCardTypes.cardTypes.cardDesc.containsIgnoreCase(paramString);
		}
		return cardTypesRepo.findAll(pred, paramPageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<SubCoverTypeSections> findCoverSectionSel(String paramString, Pageable paramPageable, Long covCode) {
		Predicate pred = null;
		if (paramString == null || StringUtils.isBlank(paramString)) {
			pred = QSubCoverTypeSections.subCoverTypeSections.subcoverType.scId.eq(covCode).and(QSubCoverTypeSections.subCoverTypeSections.isNotNull());
		} else {
			pred = QSubCoverTypeSections.subCoverTypeSections.subcoverType.scId.eq(covCode).
					and(QSubCoverTypeSections.subCoverTypeSections.subSections.section.desc.containsIgnoreCase(paramString));
		}
		return subcoverSecRepo.findAll(pred, paramPageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ProductsDef> findProductsSel(String paramString, Pageable paramPageable) {
		Predicate pred = null;
		if (paramString == null || StringUtils.isBlank(paramString)) {
			pred = QProductsDef.productsDef.isNotNull();
		} else {
			pred = QProductsDef.productsDef.proDesc.containsIgnoreCase(paramString).or(QProductsDef.productsDef.proShtDesc.containsIgnoreCase(paramString));
		}
		return prodRepo.findAll(pred, paramPageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ProductsDef> findGenProductsSel(String paramString, Pageable pageable) {
		StringBuilder searchValue = new StringBuilder();
		if (paramString == null) searchValue = new StringBuilder("%%");
		else {
			String[] splitStr = paramString.trim().split("\\s+");
			for (String search : splitStr) {
				searchValue.append("%").append(search).append("%");
			}
		}
		List<ProductsDef> proDef = new ArrayList<>();
		final List<Object[]> products = prodRepo.searchProducts(searchValue.toString(), pageable.getPageNumber(),pageable.getPageSize());
		long rowCount = 0L;
		if(!products.isEmpty()) rowCount =((BigInteger) products.get(0)[2]).intValue();
		for (Object[] product:products){
			ProductsDef productsDef = new ProductsDef();
			productsDef.setProCode(((BigInteger) product[0]).longValue());
			productsDef.setProDesc((String) product[1]);
			proDef.add(productsDef);
		}
		return new PageImpl<>(proDef, pageable, rowCount);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ProductsDef> findGrpProductsSel(String paramString, String grpType, Pageable paramPageable) {
		Predicate pred = null;
		System.out.println("grpType=" + grpType);
		if (grpType == null || StringUtils.isBlank(grpType)) {
			if (paramString == null || StringUtils.isBlank(paramString)) {
				pred = QProductsDef.productsDef.isNotNull();
			} else
				pred = QProductsDef.productsDef.proDesc.containsIgnoreCase(paramString).or(QProductsDef.productsDef.proShtDesc.containsIgnoreCase(paramString));
		} else {
			System.out.println("here=" + grpType);
			if ("M".equalsIgnoreCase(grpType))
				pred = QProductsDef.productsDef.proGroup.prgType.eq("MD").and(QProductsDef.productsDef.proDesc.containsIgnoreCase(paramString).or(QProductsDef.productsDef.proShtDesc.containsIgnoreCase(paramString)));
			else
				pred = QProductsDef.productsDef.proGroup.prgType.ne("MD").and(QProductsDef.productsDef.proDesc.containsIgnoreCase(paramString).or(QProductsDef.productsDef.proShtDesc.containsIgnoreCase(paramString)));
		}

		return prodRepo.findAll(pred, paramPageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Currencies> findActiveCurrencies(String paramString, Pageable paramPageable) {
		Predicate pred = null;
		if (paramString == null || StringUtils.isBlank(paramString)) {
			pred = QCurrencies.currencies.isNotNull();
		} else {
			pred = QCurrencies.currencies.curName.containsIgnoreCase(paramString).or(QCurrencies.currencies.curIsoCode.containsIgnoreCase(paramString));
		}
		return currencyRepo.findAll(pred, paramPageable);
	}

    @Override
    @Modifying
    @Transactional(readOnly = false)
    public void deleteBinder(long binCode) {
        try {
            System.out.println("Starting deletion for binder ID: " + binCode);

            entityManager.createNativeQuery("SET session_replication_role = replica").executeUpdate();

            int deletedRows = entityManager.createNativeQuery(
                    "DELETE FROM sys_brk_binders WHERE bin_id = ?"
            ).setParameter(1, binCode).executeUpdate();

            entityManager.createNativeQuery("SET session_replication_role = DEFAULT").executeUpdate();

            if (deletedRows > 0) {
                System.out.println("Deleted binder " + binCode + " and all related records!");
            } else {
                System.out.println(" No binder found with ID: " + binCode);
            }

        } catch (Exception e) {
            try {
                entityManager.createNativeQuery("SET session_replication_role = DEFAULT").executeUpdate();
            } catch (Exception ex) {
                System.err.println("Failed to re-enable foreign key checks: " + ex.getMessage());
            }

            System.err.println("DELETION FAILED for binder " + binCode + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error deleting binder: " + e.getMessage());
        }
    }

	@Override
	@Transactional(readOnly = true)
	public List<Object[]> findUnassignedSubCoverTypes(Long prodCode, Long bindCode)
			throws IllegalAccessException {
		return binderDetRepo.getBinderSubclassCoverTypes(prodCode, bindCode);
	}

	@Override
	@Transactional(readOnly = false)
	public void createBinderDetails(BinderDetailsBean binderBean) {
		List<BinderDetails> binderDetails = new ArrayList<>();
		for (Long covtType : binderBean.getCoverTypes()) {
			BinderDetails binder = new BinderDetails();
			binder.setSubCoverTypes(subCoverRepo.findOne(covtType));
			binder.setBinder(binderRepo.findOne(binderBean.getBindCode()));
			binderDetails.add(binder);
		}
		binderDetRepo.save(binderDetails);

	}

    @Override
    @Modifying
    @Transactional(readOnly = false)
    public void deleteBinderDetails(long detId) {
        try {
            System.out.println("Starting deletion for binder details ID: " + detId);

            entityManager.createNativeQuery("SET session_replication_role = replica").executeUpdate();

            int deletedRows = entityManager.createNativeQuery(
                    "DELETE FROM sys_brk_binder_det WHERE bdet_id = ?"
            ).setParameter(1, detId).executeUpdate();

            // Re-enable foreign key checks
            entityManager.createNativeQuery("SET session_replication_role = DEFAULT").executeUpdate();

            if (deletedRows > 0) {
                System.out.println("SUCCESS: Deleted binder details " + detId + " and all related records!");
            } else {
                System.out.println("No binder details found with ID: " + detId);
            }

        } catch (Exception e) {
            try {
                entityManager.createNativeQuery("SET session_replication_role = DEFAULT").executeUpdate();
            } catch (Exception ex) {
                System.err.println("Failed to re-enable foreign key checks: " + ex.getMessage());
            }

            System.err.println(" DELETION FAILED for binder details " + detId + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error deleting binder details: " + e.getMessage());
        }
    }

	@Override
	@Transactional(readOnly = false)
	public void deletePremRates(long premId) throws BadRequestException {
		// Check if the record exists
		PremRatesDef premRatesDef = premRatesRepo.findOne(premId);
		if (premRatesDef == null) {
			throw new BadRequestException("Premium rates with ID " + premId + " not found");
		}

		// Delete dependent records in sys_brk_quot_limits
		quotRiskLimitsRepo.deleteByPremRates_PremId(premId);

		// Delete dependent records in sys_brk_rsk_limits
		sectionTransRepo.deleteByPremRates_PremId(premId);

		// Delete the main record
		premRatesRepo.delete(premId);
	}

	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<BinderClauses> findBinderClauses(DataTablesRequest request, Long detCode)
			throws IllegalAccessException {
		BooleanExpression pred = QBinderClauses.binderClauses.binderDet.detId.eq(detCode);
		Page<BinderClauses> page = clauseRepo.findAll(pred.and(request.searchPredicate(QBinderClauses.binderClauses)), request);
		return new DataTablesResult<>(request, page);
	}

	@Override
	@Transactional(readOnly = true)
	public List<SubclassClauses> findUnassignedSubClauses(Long detCode, Long subCode, String subName)
			throws IllegalAccessException {
		if (subName != null)
			subName = StringUtils.lowerCase(subName);
		return clauseRepo.getUnassignedClauses(detCode, subCode, subName);
	}

	@Override
	@Transactional(readOnly = false)
	public void createBinderClauses(SubclassClauseBean subclassClause) {
		List<BinderClauses> binderClauses = new ArrayList<>();
		for (Long claCode : subclassClause.getClauses()) {
			BinderClauses clause = new BinderClauses();
			clause.setBinderDet(binderDetRepo.findOne(subclassClause.getDetCode()));
			clause.setClause(subclausRepo.findOne(claCode));
			binderClauses.add(clause);
		}

		clauseRepo.save(binderClauses);

	}

	@Override
	@Transactional(readOnly = false)
	public void deleteBinderClause(long clauseId) {
		clauseRepo.delete(clauseId);
	}

	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<CommissionRates> findCommRates(DataTablesRequest request, Long binId)
			throws IllegalAccessException {
		BooleanExpression pred = QCommissionRates.commissionRates.bindersDef.binId.eq(binId);
		Page<CommissionRates> page = commRatesRepo.findAll(pred.and(request.searchPredicate(QCommissionRates.commissionRates)), request);
		return new DataTablesResult<>(request, page);
	}

	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<SubAgentCommissionRates> findSubCommRates(DataTablesRequest request, Long binId)
			throws IllegalAccessException {
		BooleanExpression pred = QSubAgentCommissionRates.subAgentCommissionRates.bindersDef.binId.eq(binId);
		Page<SubAgentCommissionRates> page = subAgentCommRepo.findAll(pred.and(request.searchPredicate(QSubAgentCommissionRates.subAgentCommissionRates)), request);
		System.out.println("Output data" +request);
		return new DataTablesResult<>(request, page);
	}


	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<LifeCommissionRates> findLifeCommRates(DataTablesRequest request, Long binId)
			throws IllegalAccessException {
		BooleanExpression pred = QLifeCommissionRates.lifeCommissionRates.binderDef.binId.eq(binId);
		Page<LifeCommissionRates> page = lifeCommissionRatesRepo.findAll(pred.and(request.searchPredicate(QLifeCommissionRates.lifeCommissionRates)), request);
		return new DataTablesResult<>(request, page);
	}

	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<LifeSubAgentCommissionRates> findSubAgentLifeCommRates(DataTablesRequest request, Long binId)
			throws IllegalAccessException {
		BooleanExpression pred = QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.binderDef.binId.eq(binId);
		Page<LifeSubAgentCommissionRates> page = lifeSubAgentCommissionRatesRepo.findAll(pred.and(request.searchPredicate(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates)), request);
		return new DataTablesResult<>(request, page);
	}

	@Override
	public void createBinderAdminFeeConfig(AdminFeeDTO adminFeeDTO) throws BadRequestException {
		long count = adminFeeSetUpRepo.count(QAdminFeeSetUp.adminFeeSetUp.binder.binId.eq(adminFeeDTO.getBinderId()).and(QAdminFeeSetUp.adminFeeSetUp.status.eq("Active")));
		if(count > 0 && adminFeeDTO.getAdminId()==null){
			throw new BadRequestException("Admin Fee Set up for binder already exists....");
		}
		if(adminFeeDTO.getBinderId()==null){
			throw new BadRequestException("Please Select Valid Contract....");
		}
		final BindersDef bindersDef = binderRepo.findOne(adminFeeDTO.getBinderId());
		if(bindersDef==null){
			throw new BadRequestException("Please Select Valid Contract....");
		}
		final AdminFeeSetUp adminFeeSetUp = new AdminFeeSetUp();
		adminFeeSetUp.setBinder(bindersDef);
		adminFeeSetUp.setFcId(adminFeeDTO.getAdminId());
		adminFeeSetUp.setStatus(adminFeeDTO.getStatus());
		adminFeeSetUp.setVatRate(adminFeeDTO.getVatRate());
		adminFeeSetUp.setVateRateType(adminFeeDTO.getVateRateType());
		adminFeeSetUp.setExciseRate(adminFeeDTO.getExciseRate());
		adminFeeSetUp.setExciseRateType(adminFeeDTO.getExciseRateType());
		adminFeeSetUp.setAdminFeeRate(adminFeeDTO.getAdminFeeRate());
		adminFeeSetUp.setAdminFeeRateType(adminFeeDTO.getAdminFeeRateType());
		if("on".equalsIgnoreCase(adminFeeDTO.getStatus())){
			adminFeeSetUp.setStatus("Active");
		}
		else {
			adminFeeSetUp.setStatus("Deactivated");

		}
		adminFeeSetUpRepo.save(adminFeeSetUp);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
	public void createCommissionRates(CommissionRates commission) throws BadRequestException {
		if (commission.getRevenueItems() == null)
			throw new BadRequestException("Revenue item is mandatory");
		if(commission.getApplicableAt()==null){
			throw new BadRequestException("Select Applicable At to continue...");
		}
		BigDecimal commRangeFrom = commission.getCommRangeFrom();
		BigDecimal commRangeTo = commission.getCommRangeTo();
		Long binId = commission.getBindersDef().getBinId();

		CommissionRates existingCommission = null;

		// Check if the commission range already exists for the same binder\
		if(commission.getApplicableAt()!=null && commission.getApplicableAt().equalsIgnoreCase("NB")){
			existingCommission = commRatesRepo.findOne(
					QCommissionRates.commissionRates.commRangeFrom.eq(commRangeFrom)
							.and(QCommissionRates.commissionRates.commRangeTo.eq(commRangeTo))
							.and(QCommissionRates.commissionRates.bindersDef.binId.eq(binId))
							.and((QCommissionRates.commissionRates.applicableAt.isNull().or(QCommissionRates.commissionRates.applicableAt.eq(commission.getApplicableAt())))));
		}
		else
			existingCommission = commRatesRepo.findOne(
					QCommissionRates.commissionRates.commRangeFrom.eq(commRangeFrom)
							.and(QCommissionRates.commissionRates.commRangeTo.eq(commRangeTo))
							.and(QCommissionRates.commissionRates.bindersDef.binId.eq(binId))
							.and(QCommissionRates.commissionRates.applicableAt.eq(commission.getApplicableAt())));

		if (existingCommission != null) {
			// Update the existing commission
			existingCommission.setRateType(commission.getRateType());
			existingCommission.setCommRate(commission.getCommRate());
			existingCommission.setCommDivFactor(commission.getCommDivFactor());
			existingCommission.setRevenueItems(commission.getRevenueItems());
			existingCommission.setApplicableAt(commission.getApplicableAt());
			commRatesRepo.save(existingCommission);
		} else {
			// Save a new commission
			commRatesRepo.save(commission);
		}

//		if (commRatesRepo.count(QCommissionRates.commissionRates.commRangeFrom.eq(commRangeFrom).and(QCommissionRates.commissionRates.commRangeTo.eq(commRangeTo)).and(QCommissionRates.commissionRates.bindersDef.binId.eq(commission.getBindersDef().getBinId()))) > 0) {
//			throw new BadRequestException("Commission Range already exists");
//		}
//		commRatesRepo.save(commission);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
	public void createSubAgentCommRates(SubAgentCommissionRates commission) throws BadRequestException {
		if (commission.getAccountTypes() == null)
			throw new BadRequestException("Account type is mandatory");
		if(commission.getApplicableAt()==null){
			throw new BadRequestException("Select Applicable At to continue...");
		}
		BigDecimal commRangeFrom = commission.getCommRangeFrom();
		BigDecimal commRangeTo = commission.getCommRangeTo();
		Long binId = commission.getBindersDef().getBinId();
		AccountTypes accountTypes = commission.getAccountTypes();

		// Check if the commission range already exists for the same binder
		SubAgentCommissionRates existingCommission;

		if (commission.getApplicableAt() != null && commission.getApplicableAt().equalsIgnoreCase("NB")) {
			existingCommission = subAgentCommRepo.findOne(
					QSubAgentCommissionRates.subAgentCommissionRates.commRangeFrom.eq(commRangeFrom)
							.and(QSubAgentCommissionRates.subAgentCommissionRates.commRangeTo.eq(commRangeTo))
							.and(QSubAgentCommissionRates.subAgentCommissionRates.bindersDef.binId.eq(binId))
							.and(QSubAgentCommissionRates.subAgentCommissionRates.accountTypes.eq(accountTypes))
							.and(QSubAgentCommissionRates.subAgentCommissionRates.applicableAt.isNull().or(QSubAgentCommissionRates.subAgentCommissionRates.applicableAt.eq(commission.getApplicableAt()))
							)
			);
		} else {
			existingCommission = subAgentCommRepo.findOne(
					QSubAgentCommissionRates.subAgentCommissionRates.commRangeFrom.eq(commRangeFrom)
							.and(QSubAgentCommissionRates.subAgentCommissionRates.commRangeTo.eq(commRangeTo))
							.and(QSubAgentCommissionRates.subAgentCommissionRates.bindersDef.binId.eq(binId))
							.and(QSubAgentCommissionRates.subAgentCommissionRates.accountTypes.eq(accountTypes))
							.and(QSubAgentCommissionRates.subAgentCommissionRates.applicableAt.eq(commission.getApplicableAt()))
			);
		}
//		SubAgentCommissionRates existingCommission = subAgentCommRepo.findOne(
//				QSubAgentCommissionRates.subAgentCommissionRates.commRangeFrom.eq(commRangeFrom)
//						.and(QSubAgentCommissionRates.subAgentCommissionRates.commRangeTo.eq(commRangeTo))
//						.and(QSubAgentCommissionRates.subAgentCommissionRates.bindersDef.binId.eq(binId))
//						.and(QSubAgentCommissionRates.subAgentCommissionRates.accountTypes.eq(accountTypes))
//		);

		if (existingCommission != null) {
			// Update the existing commission
			existingCommission.setRateType(commission.getRateType());
			existingCommission.setCommRate(commission.getCommRate());
			existingCommission.setCommDivFactor(commission.getCommDivFactor());
			existingCommission.setRevenueItems(commission.getRevenueItems());
			existingCommission.setApplicableAt(commission.getApplicableAt());
			subAgentCommRepo.save(existingCommission);
		} else {
			// Save a new commission
			subAgentCommRepo.save(commission);
		}

//		if (subAgentCommRepo.count(QSubAgentCommissionRates.subAgentCommissionRates.commRangeFrom.eq(commRangeFrom).and(QSubAgentCommissionRates.subAgentCommissionRates.commRangeTo.eq(commRangeTo))
//				.and(QSubAgentCommissionRates.subAgentCommissionRates.bindersDef.binId.eq(commission.getBindersDef().getBinId())).and(QSubAgentCommissionRates.subAgentCommissionRates.accountTypes.eq(accountTypes))) > 0) {
//			throw new BadRequestException("Commission Range for " +accountTypes.getAccName()+
//					" already exists");
////		}
//		subAgentCommRepo.save(commission);

	}

	@Override
	@Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
	public void createLifeCommissionRates(LifeCommissionRates commission) throws BadRequestException {
//		if (commission.getPremRatesDef() == null)
//			throw new BadRequestException("Revenue item is mandatory");
		lifeCommissionRatesRepo.save(commission);

	}

	@Override
	@Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
	public void createLifeSubAgentCommissionRates(LifeSubAgentCommissionRates commission) throws BadRequestException {
		if (commission.getAccountTypes() == null)
			throw new BadRequestException("Account Type  is mandatory");
		lifeSubAgentCommissionRatesRepo.save(commission);

	}

	@Override
	@Transactional(readOnly = false)
	public void deleteCommRates(long commId) {
		commRatesRepo.delete(commId);

	}

	@Override
	@Transactional(readOnly = false)
	public void deleteSubAgentCommRates(long commId) {
		subAgentCommRepo.delete(commId);

	}

    @Override
    @Modifying
    @Transactional(readOnly = false)
    public void deleteLifeCommRates(long commId) {
        try {
            System.out.println("Starting deletion for life commission rates ID: " + commId);

            entityManager.createNativeQuery("SET session_replication_role = replica").executeUpdate();

            int deletedRows = entityManager.createNativeQuery(
                    "DELETE FROM sys_brk_life_comm_rates WHERE comm_id = ?"
            ).setParameter(1, commId).executeUpdate();

            entityManager.createNativeQuery("SET session_replication_role = DEFAULT").executeUpdate();

            if (deletedRows > 0) {
                System.out.println("NUCLEAR SUCCESS: Deleted life commission rates " + commId + " and all related records!");
            } else {
                System.out.println("No life commission rates found with ID: " + commId);
            }

        } catch (Exception e) {
            //re-enable foreign key checks
            try {
                entityManager.createNativeQuery("SET session_replication_role = DEFAULT").executeUpdate();
            } catch (Exception ex) {
                System.err.println("Failed to re-enable foreign key checks: " + ex.getMessage());
            }

            System.err.println("DELETION FAILED for life commission rates " + commId + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error deleting life commission rates: " + e.getMessage());
        }
    }

	@Override
	public void deleteAdminFeeRates(long commId) {
		adminFeeSetUpRepo.delete(commId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteSubAgentLifeCommRates(long commId) {
		lifeSubAgentCommissionRatesRepo.delete(commId);

	}

	@Override
	@Transactional(readOnly = true)
	public Page<RevenueItemsDef> getCommRatesRevenueItems(String paramString, Pageable paramPageable, Long binId) {
		BindersDef binder = binderRepo.findOne(binId);
		Long prodCode = binder.getProduct().getProCode();
		ProductSubclasses productSubclasses = prodSubRepo.findOne(QProductSubclasses.productSubclasses.product.proCode.eq(prodCode));
		Long subId = productSubclasses.getSubclass().getSubId();
		Predicate pred = null;
		if (paramString == null || StringUtils.isBlank(paramString)) {
			pred = QRevenueItemsDef.revenueItemsDef.prodGroup.subId.eq(subId).and(QRevenueItemsDef.revenueItemsDef.item.eq(RevenueItems.UC).and(QRevenueItemsDef.revenueItemsDef.isNotNull()));
		} else {
			pred = QRevenueItemsDef.revenueItemsDef.prodGroup.subId.eq(subId).and(QRevenueItemsDef.revenueItemsDef.item.eq(RevenueItems.UC).and(QRevenueItemsDef.revenueItemsDef.item.stringValue().containsIgnoreCase(paramString)));
		}
		return revenueItemsRepo.findAll(pred, paramPageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<SectionsDef> findSubclassSections(String paramString, Pageable paramPageable, Long subCode) {
		Predicate pred = null;
		if (paramString == null || StringUtils.isBlank(paramString)) {
			pred = QSubclassSections.subclassSections.subclass.subId.eq(subCode).and(QSubclassSections.subclassSections.isNotNull());
		} else {
			pred = QSubclassSections.subclassSections.subclass.subId.eq(subCode).and(QSubclassSections.subclassSections.section.desc.containsIgnoreCase(paramString).
					or(QSubclassSections.subclassSections.section.shtDesc.containsIgnoreCase(paramString)));
		}
		List<SubclassSections> subclassSections = subSectionRepo.findAll(pred, paramPageable).getContent();
		List<SectionsDef> sections = new ArrayList<>();
		for (SubclassSections subclassSection : subclassSections) {
			SectionsDef sectionsDef = subclassSection.getSection();
			sections.add(sectionsDef);
		}
		Page<SectionsDef> sectionPage = new PageImpl<>(sections);
		return sectionPage;
	}

	@Override
	public Page<Checks> findBinderChecks(String paramString, Pageable paramPageable, Long bindCode) {
		Predicate pred = null;
		BindersDef bindersDef = binderRepo.findOne(bindCode);
		Iterable<MedicalBinderRules> binderRules = medicalRulesRepo.findAll(QMedicalBinderRules.medicalBinderRules.binder.binId.eq(bindCode));
		List<Long> checks = new ArrayList<>();
		if (binderRules.spliterator().getExactSizeIfKnown() > 0)
			checks = Streamable.streamOf(binderRules).filter(a -> a.getChecks() != null).map(a -> a.getChecks().getCheckId()).collect(Collectors.toList());
		if (paramString == null || StringUtils.isBlank(paramString)) {
			pred = QProductChecks.productChecks.checkId.notIn(checks).and(QProductChecks.productChecks.isNotNull()).and(QProductChecks.productChecks.product.proCode.eq(bindersDef.getProduct().getProCode()));
		} else {
			pred = QProductChecks.productChecks.checkId.notIn(checks).and(QProductChecks.productChecks.checks.checkName.containsIgnoreCase(paramString)).and(QProductChecks.productChecks.product.proCode.eq(bindersDef.getProduct().getProCode()));
		}
		List<ProductChecks> productChecks = productChecksRepo.findAll(pred, paramPageable).getContent();
		List<Checks> checkss = new ArrayList<>();
		for (ProductChecks productCheck : productChecks) {
			Checks checks1 = productCheck.getChecks();
			checks1.setProdCheckId(productCheck.getCheckId());
			checkss.add(checks1);
		}
		Page<Checks> checksPage = new PageImpl<>(checkss);
		return checksPage;
	}

	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<BinderSectionPerils> findBinderSectionPerils(DataTablesRequest request, Long detCode, Long sectCode) throws IllegalAccessException {
		BooleanExpression pred = QBinderSectionPerils.binderSectionPerils.binderDetail.detId.eq(detCode).
				and(QBinderSectionPerils.binderSectionPerils.sectionsDef.id.eq(sectCode));
		Page<BinderSectionPerils> page = binderSectPerilsRepo.findAll(pred.and(request.searchPredicate(QBinderSectionPerils.binderSectionPerils)), request);
		return new DataTablesResult<>(request, page);
	}


	@Override
	@Transactional(readOnly = true)
	public List<SubclassPerils> findUnassignedPerils(Long detCode, Long subCode, Long sectCode, String perilName) throws IllegalAccessException {
		return binderSectPerilsRepo.getUnassignedPerils(detCode, subCode, sectCode, perilName);
	}

	@Override
	@Transactional(readOnly = false)
	public void createBinderSectPerils(BinderPerilsBean perilsBean) {
		List<BinderSectionPerils> binderSectPerils = new ArrayList<>();
		for (Long perilCode : perilsBean.getPerils()) {
			BinderSectionPerils sectionPeril = new BinderSectionPerils();
			sectionPeril.setBinderDetail(binderDetRepo.findOne(perilsBean.getBinderDetail()));
			sectionPeril.setSectionsDef(sectionRepo.findOne(perilsBean.getSectId()));
			sectionPeril.setSubclassPeril(subClassPerilsRepo.findOne(perilCode));
			binderSectPerils.add(sectionPeril);
		}
		binderSectPerilsRepo.save(binderSectPerils);
	}

	@Override
	@Transactional(readOnly = false)
	public void createBinderRequiredDocs(RequiredDocsBean docsBean) {
		List<BinderReqrdDocs> binderReqrdDocs = new ArrayList<>();
		for (Long docs : docsBean.getRequiredDocs()) {
			BinderReqrdDocs reqrdDocs = new BinderReqrdDocs();
			reqrdDocs.setBinderDetail(binderDetRepo.findOne(docsBean.getBinderDetail()));
			reqrdDocs.setRequiredDocs(subclassReqDocRepo.findOne(docs));
			reqrdDocs.setMandatory(false);
			binderReqrdDocs.add(reqrdDocs);
		}
		reqrdDocsRepo.save(binderReqrdDocs);
	}

	@Override
	public void updateBinderReqrdDocs(BinderReqrdDocs reqrdDocs) throws BadRequestException {
		reqrdDocs.setBinderDetail(reqrdDocsRepo.findOne(reqrdDocs.getBrdId()).getBinderDetail());
		reqrdDocsRepo.save(reqrdDocs);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteBinderSectPeril(long sectPerilId) {
		binderSectPerilsRepo.delete(sectPerilId);
	}

	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<BinderRatesTable> findBinderRates(DataTablesRequest request, Long bindCode) throws IllegalAccessException {
		BooleanExpression pred = QBinderRatesTable.binderRatesTable.binder.binId.eq(bindCode);
		Page<BinderRatesTable> page = binderRatesTblRepo.findAll(pred.and(request.searchPredicate(QBinderRatesTable.binderRatesTable)), request);
		return new DataTablesResult<>(request, page);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
	public void defineBinderRatesTable(BinderRatesTable ratesTable) throws BadRequestException {
		ratesTable.setEffDate(new Date());
		ratesTable.setFileName(ratesTable.getFile().getOriginalFilename());
		binderRatesTblRepo.save(ratesTable);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
	public void defineBinderPremRatesTable(PremRatesTable ratesTable) throws BadRequestException {
		ratesTable.setEffDate(new Date());
		ratesTable.setFileName(ratesTable.getFile().getOriginalFilename());
		premRatesTableRepo.save(ratesTable);

	}

	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<MedicalCovers> findDetMedCovers(DataTablesRequest request, Long detCode) throws IllegalAccessException {
		BooleanExpression pred = QMedicalCovers.medicalCovers.binderDet.detId.eq(detCode);
		Page<MedicalCovers> page = medicalCoversRepo.findAll(pred.and(request.searchPredicate(QMedicalCovers.medicalCovers)), request);
		return new DataTablesResult<>(request, page);
	}

	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<BinderMedicalCards> findDetBinCardTypes(DataTablesRequest request, Long detCode) throws IllegalAccessException {
		BooleanExpression pred = QBinderMedicalCards.binderMedicalCards.binder.binId.eq(detCode);
		Page<BinderMedicalCards> page = binderCardsRepo.findAll(pred.and(request.searchPredicate(QBinderMedicalCards.binderMedicalCards)), request);
		return new DataTablesResult<>(request, page);
	}

	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<PremRatesDTO> findBinderPremRates(DataTablesRequest request, Long detCode)  {
		List<Object[]> premratesList = premRatesTableRepo.findPremiumRatesTbl(detCode,request.getPageNumber(), request.getPageSize());
		List<PremRatesDTO> premRatesDTOList = new ArrayList<>();
		long rowCount = 0l;
		if(!premratesList.isEmpty()) rowCount = ((BigInteger)premratesList.get(0)[3]).intValue();
		for(Object[] premrate:premratesList){
			PremRatesDTO premRatesDTO = new PremRatesDTO();
			premRatesDTO.setId(((BigInteger)premrate[0]).longValue());
			premRatesDTO.setEffDate((Date) premrate[1]);
			premRatesDTO.setFileName((String) premrate[2]);
			premRatesDTOList.add(premRatesDTO);
		}
		Page<PremRatesDTO>  page = new PageImpl<>(premRatesDTOList,request, rowCount);
		return new DataTablesResult<>(request, page);
	}

	@Override
	public DataTablesResult<AdminFeeDTO> findAdminFeeConfig(DataTablesRequest request, Long detCode) {
		List<Object[]> adminFeeList = adminFeeSetUpRepo.findAllAdminFeeConfig(detCode,request.getPageNumber(), request.getPageSize());
		List<AdminFeeDTO> adminFeeDTOList = new ArrayList<>();
		long rowCount = 0l;
		if(!adminFeeList.isEmpty()) rowCount = ((BigInteger)adminFeeList.get(0)[10]).intValue();
		for(Object[] admin:adminFeeList){
			AdminFeeDTO adminFeeDTO = new AdminFeeDTO();
			adminFeeDTO.setAdminId(((BigInteger)admin[0]).longValue());
			adminFeeDTO.setExciseRate((BigDecimal) admin[1]);
			adminFeeDTO.setExciseRateType((String) admin[2]);
			adminFeeDTO.setVatRate((BigDecimal) admin[3]);
			adminFeeDTO.setVateRateType((String) admin[4]);
			adminFeeDTO.setStatus((String) admin[5]);
			adminFeeDTO.setBinStatus((Boolean) admin[6]);
			adminFeeDTO.setBinderId(((BigInteger)admin[7]).longValue());
			adminFeeDTO.setAdminFeeRate((BigDecimal) admin[8]);
			adminFeeDTO.setAdminFeeRateType((String) admin[9]);
			adminFeeDTOList.add(adminFeeDTO);
		}
		Page<AdminFeeDTO>  page = new PageImpl<>(adminFeeDTOList,request, rowCount);
		return new DataTablesResult<>(request, page);
	}

	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<BinderReqrdDocs> findBinderReqDocs(DataTablesRequest request, Long detCode) throws IllegalAccessException {
		BooleanExpression pred = QBinderReqrdDocs.binderReqrdDocs.binderDetail.detId.eq(detCode);
		Page<BinderReqrdDocs> page = reqrdDocsRepo.findAll(pred.and(request.searchPredicate(QBinderReqrdDocs.binderReqrdDocs)), request);
		return new DataTablesResult<>(request, page);
	}


	@Override
	@Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
	public void createBinderCardTypes(BinderMedicalCards medicalCards) throws BadRequestException {
		if (medicalCards.getId() == null) {
			if (medicalCards.getCardTypes() == null)
				throw new BadRequestException("Select card type to continue...");
			if (medicalCards.getWefDate() == null)
				throw new BadRequestException("Select Date From to continue...");
			Long count = binderCardsRepo.count(QBinderMedicalCards.binderMedicalCards.cardTypes.cardId.eq(medicalCards.getCardTypes().getCardId())
					.and(QBinderMedicalCards.binderMedicalCards.binder.binId.eq(medicalCards.getBinder().getBinId())));
			if (count > 0) {
				Iterable<BinderMedicalCards> bincards = binderCardsRepo.findAll(QBinderMedicalCards.binderMedicalCards.cardTypes.cardId.eq(medicalCards.getCardTypes().getCardId())
						.and(QBinderMedicalCards.binderMedicalCards.binder.binId.eq(medicalCards.getBinder().getBinId())));
				for (BinderMedicalCards cards : bincards) {

					if (medicalCards.getWefDate().before(cards.getWefDate())) {
						if (medicalCards.getWetDate() == null)
							throw new BadRequestException("Enter Date To for this card type to continue ");

						if (medicalCards.getWetDate().after(cards.getWefDate()))
							throw new BadRequestException("Date To overlaps with the existing card types ");

					}
					if (cards.getWetDate() == null) {
						throw new BadRequestException("Enter Date To for the existing card types before adding a new card type...");

					}
					if (medicalCards.getWefDate().before(cards.getWetDate()) && medicalCards.getWefDate().after(cards.getWefDate())) {
						throw new BadRequestException("Date From for the new card type is overlapping with existing card types...");

					}


				}
			}
		} else {
			BinderMedicalCards existingCard = binderCardsRepo.findOne(medicalCards.getId());
			medicalCards.setCardTypes(existingCard.getCardTypes());
		}
		if (medicalCards.getWetDate() == null)
			medicalCards.setWetDate(null);


		binderCardsRepo.save(medicalCards);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
	public void createMedicalCover(MedicalCovers medicalCover) throws BadRequestException {
		if (medicalCover.getMainCover() == null || StringUtils.isBlank(medicalCover.getMainCover()))
			throw new BadRequestException("Select Main Cover Option to continue..");
		if (medicalCover.isDependsOnGender()) {
			if (medicalCover.getGender() == null) {
				throw new BadRequestException("Select Gender to continue....");
			}
		}
		String fundBinder = "N";
		if (medicalCover.getBinderDet().getBinder().getFundBinder() != null) {
			fundBinder = medicalCover.getBinderDet().getBinder().getFundBinder();
		}
		String fundCover = "N";
		if ("Y".equalsIgnoreCase(fundBinder)) {
			if (medicalCover.getFundCoverMand() != null && "on".equalsIgnoreCase(medicalCover.getFundCoverMand())) {
				fundCover = "Y";
			}
		}
		if (medicalCover.getId() == null) {
			if (medicalCover.getSection() == null)
				throw new BadRequestException("Select Cover to continue...");
			if ("Y".equalsIgnoreCase(medicalCover.getMainCover())) {
				medicalCover.setMainSection(medicalCover.getSection());
			} else if ("N".equalsIgnoreCase(medicalCover.getMainCover())) {
				if (medicalCover.getMainSection() != null) {
					if (medicalCover.getMainSection().getId() == medicalCover.getSection().getId()) {
						throw new BadRequestException("Cover and Main Cover cannot be the Same.....");
					}
				} else throw new BadRequestException("Select Main Cover to continue..");
			}
			Long count = medicalCoversRepo.count(QMedicalCovers.medicalCovers.binderDet.detId.eq(medicalCover.getBinderDet().getDetId()).and(QMedicalCovers.medicalCovers.section.id.eq(medicalCover.getSection().getId())));
			if (count > 0) throw new BadRequestException("Cover Details already exists..");
			if ("N".equalsIgnoreCase(medicalCover.getMainCover())) {
				Long mainCount = medicalCoversRepo.count(QMedicalCovers.medicalCovers.binderDet.detId.eq(medicalCover.getBinderDet().getDetId())
						.and(QMedicalCovers.medicalCovers.section.id.eq(medicalCover.getMainSection().getId()))
						.and(QMedicalCovers.medicalCovers.mainCover.eq("Y")));
				if (mainCount == 0)
					throw new BadRequestException("The main section selected has not been setup..Select another main section");
			}
			medicalCover.setFundCoverMand(fundCover);
		} else {
			MedicalCovers covers = medicalCoversRepo.findOne(medicalCover.getId());
			medicalCover.setMainSection(covers.getMainSection());
			medicalCover.setSection(covers.getSection());
			medicalCover.setBinderDet(covers.getBinderDet());
			medicalCover.setFundCoverMand(fundCover);
		}
		if (medicalCover.getApplicableAt() == null) throw new BadRequestException("Select Applicable At");
		//if(medicalCover.getLimitAmount()==null) throw new BadRequestException("Select Limit Amount");

		medicalCoversRepo.save(medicalCover);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
	public void deleteMedCover(long coverId) throws BadRequestException {
		MedicalCovers covers = medicalCoversRepo.findOne(coverId);
		if (("Y".equalsIgnoreCase(covers.getMainCover()))) {
			Long count = medicalCoversRepo.count(QMedicalCovers.medicalCovers.mainSection.id.eq(covers.getSection().getId()));
			if (count > 1)
				throw new BadRequestException("Cannot delete Main Cover. There are dependent covers on this cover");
		}
		medicalCoversRepo.delete(coverId);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
	public void deleteBinderCard(long binCardId) throws BadRequestException {
		binderCardsRepo.delete(binCardId);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
	public void updateBinderClause(BinderClauses binderClauses) throws BadRequestException {
		if (binderClauses.getMandatory() != null && "on".equalsIgnoreCase(binderClauses.getMandatory())) {
			binderClauses.setMandatory("Y");
		} else binderClauses.setMandatory("N");
		if (binderClauses.getEditable() != null && "on".equalsIgnoreCase(binderClauses.getEditable())) {
			binderClauses.setEditable("Y");
		} else binderClauses.setEditable("N");
		binderClauses.setBinderDet(clauseRepo.findOne(binderClauses.getClauId()).getBinderDet());
		clauseRepo.save(binderClauses);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Object[]> findUnassignedReqDocs(Long subCode, Long detCode) throws IllegalAccessException {
		return reqrdDocsRepo.getUnassignedReqDocs(subCode, detCode);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
	public void deleteBinderReqDoc(long docId) throws BadRequestException {
		reqrdDocsRepo.delete(docId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Object[]> findClausesDef(String paramString, long subId, long sectCode) {
		System.out.println("Sect Code " + sectCode);
		return sectionLimitsRepo.getUnassignedClauses(sectCode, subId);
	}

	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<SectionLimits> findPremRatesSectionLimits(DataTablesRequest request, Long premId) throws IllegalAccessException {
		BooleanExpression pred = QSectionLimits.sectionLimits.premRatesDef.Id.eq(premId);
		Page<SectionLimits> page = sectionLimitsRepo.findAll(pred.and(request.searchPredicate(QSectionLimits.sectionLimits)), request);
		return new DataTablesResult<>(request, page);
	}

	@Override
	public void createPremLimits(ClausesBean docsBean) {
		List<SectionLimits> sectionLimitses = new ArrayList<>();
		System.out.println(docsBean.getClauses());
		for (Long clId : docsBean.getClauses()) {
			System.out.println("Clause id " + clId);
			SectionLimits sectionLimits = new SectionLimits();
			sectionLimits.setClausesDef(subclausRepo.findOne(clId));
			sectionLimits.setPremRatesDef(premRepo.findOne(docsBean.getPremRatesId()));
			sectionLimitses.add(sectionLimits);
		}
		sectionLimitsRepo.save(sectionLimitses);
	}

	@Override
	public void updatePremLimits(SectionLimits limits) throws BadRequestException {
		sectionLimitsRepo.save(limits);
	}

	@Override
	public void deletePremLimits(long limitId) throws BadRequestException {
		sectionLimitsRepo.delete(limitId);
	}

	@Override
	public void deleteQuestion(Long quizCode) throws BadRequestException {
		Iterable<BinderQuestionnaireChoices> choices = questionnaireChoicesRepo.findAll(QBinderQuestionnaireChoices.binderQuestionnaireChoices.questions.bqdid.eq(quizCode));
		questionnaireChoicesRepo.delete(choices);
		questionnaireRepo.delete(quizCode);
	}

	@Override
	public void deleteQuestionChoice(Long choiceCode) throws BadRequestException {
		questionnaireChoicesRepo.delete(choiceCode);
	}

	@Override
	public List<SingleQuizModel> findAllQuestions(Long binCode) throws IllegalAccessException {
		List<SingleQuizModel> questionChoiceModels = new ArrayList<>();
		BooleanExpression pred = QBinderQuestionnaire.binderQuestionnaire.binder.binId.eq(binCode);
		Iterable<BinderQuestionnaire> binderQuestions = questionnaireRepo.findAll(pred, new Sort(Sort.Direction.ASC, "questionOrder"));
		for (BinderQuestionnaire questions : binderQuestions) {
			SingleQuizModel singleQuizModel = new SingleQuizModel();
			singleQuizModel.setName(questions.getQuestionname());
			singleQuizModel.setType(questions.getQuestiontype());
			singleQuizModel.setQuizCode(questions.getBqdid());

			if (questions.getQuestionMandatory() != null && questions.getQuestionMandatory().equalsIgnoreCase("Y")) {
				singleQuizModel.setIsRequired("True");
			} else {
				singleQuizModel.setIsRequired("False");
			}
			if (questions.getTriggerByQuiz() != null) {
				if (questions.getTriggerByQuizAnswer() != null) {
					singleQuizModel.setVisibleIf("{" + questions.getTriggerByQuiz().getQuestionname() + "}=" + questions.getTriggerByQuizAnswer().getChoice());
				}
			}

			Iterable<BinderQuestionnaireChoices> QuestionsChoices = questionnaireChoicesRepo.findAll(QBinderQuestionnaireChoices.binderQuestionnaireChoices.questions.bqdid.eq(questions.getBqdid()));
			Long size = QuestionsChoices.spliterator().getExactSizeIfKnown();
			String[] arr = new String[size.intValue()];
			int i = 0;
			for (BinderQuestionnaireChoices questionChoices : QuestionsChoices) {
				arr[i] = questionChoices.getChoice();
				i++;
			}
			singleQuizModel.setChoices(arr);
			questionChoiceModels.add(singleQuizModel);
		}
		return questionChoiceModels;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<BinderQuestionnaire> findBinderQuestions(String paramString, Long binCode, Pageable paramPageable) {
		Predicate pred = null;
		if (paramString == null || StringUtils.isBlank(paramString)) {
			pred = QBinderQuestionnaire.binderQuestionnaire.isNotNull().and(QBinderQuestionnaire.binderQuestionnaire.binder.binId.eq(binCode));
		} else {
			pred = QBinderQuestionnaire.binderQuestionnaire.questionname.containsIgnoreCase(paramString).and(QBinderQuestionnaire.binderQuestionnaire.binder.binId.eq(binCode));
		}
		return questionnaireRepo.findAll(pred, paramPageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<BinderQuestionnaireChoices> findBinderQuestionChoice(String paramString, Long quizCode, Pageable paramPageable) {
		Predicate pred = null;
		if (paramString == null || StringUtils.isBlank(paramString)) {
			pred = QBinderQuestionnaireChoices.binderQuestionnaireChoices.isNotNull().and(QBinderQuestionnaireChoices.binderQuestionnaireChoices.questions.bqdid.eq(quizCode));
		} else {
			pred = QBinderQuestionnaireChoices.binderQuestionnaireChoices.choice.containsIgnoreCase(paramString).and(QBinderQuestionnaireChoices.binderQuestionnaireChoices.questions.bqdid.eq(quizCode));
		}
		return questionnaireChoicesRepo.findAll(pred, paramPageable);
	}

	@Override
	public void makeReady(Long binId) throws BadRequestException {
		BindersDef bindersDef = binderRepo.findOne(binId);
		if (bindersDef == null) {
			throw new BadRequestException("Binder not found with ID: " + binId);
		}
		ProductGroupDef productGroupDef = bindersDef.getProduct().getProGroup();
		if (!productGroupDef.getPrgType().equalsIgnoreCase("L")) {
			List<CommissionRates> commissionRates = commRatesRepo.findCommIds(binId);
			System.out.println("split: " + commissionRates.spliterator().getExactSizeIfKnown());
			if (commissionRates.isEmpty()) {
				throw new BadRequestException("Please Set Up The Commission Rates For This Binder");
			}
		}
		bindersDef.setBinStatus("Authorised");
		binderRepo.save(bindersDef);
	}


	@Override
	public void makeBinUndo(Long binId) {
		BindersDef bindersDef = binderRepo.findOne(binId);
		String status = null;
		bindersDef.setBinStatus(status);
		binderRepo.save(bindersDef);
	}

	@Override
	public void makeBinAuthorise(Long binId) {
		BindersDef bindersDef = binderRepo.findOne(binId);
		String status = "Authorised";
		bindersDef.setBinStatus(status);
		binderRepo.save(bindersDef);
	}

	@Override
	public void makeBinUnAuthorise(Long id) {
		BindersDef bindersDef = binderRepo.findOne(id);
		String status = "Unauthorised";
		bindersDef.setBinStatus(status);
		binderRepo.save(bindersDef);
	}

	@Override
	public BindersDef getDeactivate(Long id) {
		return binderRepo.findOne(id);
	}

	@Override
	public void downloadRatesTable(Long id, HttpServletResponse response) throws BadRequestException {
		PremRatesTable premRatesTable=  premRatesTableRepo.findOne(id);
		if (premRatesTable!=null) {
			String name = premRatesTable.getFileName().substring(0,premRatesTable.getFileName().indexOf("."))+".xls";
			final String excel = premRatesTable.getRate_table();//Here.......   extract byte data from resultSet
			if(excel==null)
				throw new BadRequestException("Unable to get Premium Template to Download");
			// copy it to response's OutputStream
			try {
				byte[] bytes = Files.readAllBytes(new File(excel).toPath());
				response.setHeader("Content-Disposition", "attachment; filename=\"" + name);
				response.setContentType("application/vnd.ms-excel");

				OutputStream os = response.getOutputStream();
				os.write(bytes);
				os.flush();
				os.close();
			} catch (IOException ex) {
				ex.printStackTrace();
				//log.info("Error writing file to output stream. Filename was '{}'", fileName, ex);
				throw new RuntimeException("IOError writing file to output stream");
			}
		}
	}

	@Override
	@Transactional
	public void createCompSheetNames(SubclassComputationSheetNames sheetNames) throws BadRequestException {
		SubclassComputationSheetNames compSheet = new SubclassComputationSheetNames();
		compSheet.setShtId(sheetNames.getShtId());
		compSheet.setSheetName(sheetNames.getSheetName());
		if (sheetNames.getComputeType() != null && sheetNames.getComputeType().equalsIgnoreCase("S")){
			compSheet.setComputeType("S");
		}else compSheet.setComputeType("P");
		compSheet.setBinderDetails(sheetNames.getBinderDetails());
		sheetNamesRepo.save(compSheet);
	}

	@Override
	@Transactional(readOnly = true)
	public DataTablesResult<SubclassComputationSheetNames> findSheetNames(DataTablesRequest request, Long detCode) throws IllegalAccessException {
		BooleanExpression pred = QSubclassComputationSheetNames.subclassComputationSheetNames.binderDetails.detId.eq(detCode);
		Page<SubclassComputationSheetNames> page = sheetNamesRepo.findAll(pred.and(request.searchPredicate(QSubclassComputationSheetNames.subclassComputationSheetNames)), request);
		return new DataTablesResult<>(request, page);
	}

	@Override
	@Transactional
	public void deleteCompSheets(Long shtId) {
		sheetNamesRepo.delete(shtId);
	}

	@Override
	@Transactional(readOnly = true)
	public void validateProductRevenueMapping(Long productCode) throws BadRequestException {
		if (productCode == null) {
			throw new BadRequestException("Product code is required for validation");
		}

		// Get all subclass IDs for this product
		List<Long> subclassIds = getProductSubclassIds(productCode);

		if (subclassIds.isEmpty()) {
			throw new BadRequestException("No GL SetUps found for the selected product. Please configure product subclasses first.");
		}

		// Used the existing validation method from SetupsService
		List<SubclassMappingValidation> validations = setupsService.validateSubclassesMappings(subclassIds);

		// Check for any invalid subclasses
		List<SubclassMappingValidation> invalidSubclasses = validations.stream()
				.filter(v -> !v.isHasRevenueMapping())
				.collect(Collectors.toList());

		if (!invalidSubclasses.isEmpty()) {
			String invalidNames = invalidSubclasses.stream()
					.map(sc -> sc.getSubShtDesc() + " (" + sc.getSubDesc() + ")")
					.collect(Collectors.joining(", "));

			String errorMessage = "Cannot create contract. The following subclasses in the selected product " +
					"do not have revenue item/GL mappings configured: " + invalidNames +
					". Please configure revenue mappings before creating the contract.";

			throw new BadRequestException(errorMessage);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Long> getProductSubclassIds(Long productCode) {
		// Get all product subclasses for this product
		Iterable<ProductSubclasses> productSubclasses = prodSubRepo.findAll(
				QProductSubclasses.productSubclasses.product.proCode.eq(productCode)
		);

		return Streamable.streamOf(productSubclasses)
				.map(ps -> ps.getSubclass().getSubId())
				.collect(Collectors.toList());
	}
}
