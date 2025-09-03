package com.brokersystems.brokerapp.medical.service.impl;

import com.brokersystems.brokerapp.enums.RevenueItems;
import com.brokersystems.brokerapp.medical.model.*;
import com.brokersystems.brokerapp.medical.repository.*;
import com.brokersystems.brokerapp.medical.service.MedicalEndorseService;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.exception.EndorsementsException;
import com.brokersystems.brokerapp.server.utils.DateUtilities;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.setup.model.QTaxRates;
import com.brokersystems.brokerapp.setup.model.TaxRates;
import com.brokersystems.brokerapp.setup.repository.TaxRatesRepo;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.brokersystems.brokerapp.trans.model.SystemTrans;
import com.brokersystems.brokerapp.trans.repository.SystemTransRepo;
import com.brokersystems.brokerapp.uw.model.*;
import com.brokersystems.brokerapp.uw.repository.PolClausesRepo;
import com.brokersystems.brokerapp.uw.repository.PolTaxesRepo;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import com.brokersystems.brokerapp.uw.repository.RiskDocsRepo;
import com.brokersystems.brokerapp.uw.service.EndorseService;
import com.brokersystems.brokerapp.workflow.docs.DocType;
import com.brokersystems.brokerapp.workflow.utils.WorkflowService;
import com.google.common.collect.Maps;
import com.mysema.query.types.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by peter on 7/11/2017.
 */
@Service
public class MedicalEndorseServiceImpl implements MedicalEndorseService {

    @Autowired
    private PolicyTransRepo policyRepo;

    @Autowired
    private EndorseService endorseService;

    @Autowired
    private DateUtilities dateUtils;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private PolTaxesRepo polTaxesRepo;

    @Autowired
    private PolClausesRepo polClausesRepo;

    @Autowired
    private SystemTransRepo transRepo;

    @Autowired
    private MedicalCategoryRepo categoryRepo;

    @Autowired
    private CategoryBenefitRepo benefitRepo;

    @Autowired
    private CategoryMembersRepo membersRepo;

    @Autowired
    private CategoryClausesRepo clausesRepo;

    @Autowired
    private CatExclusionsRepo exclusionsRepo;

    @Autowired
    private CatProvidersRepo catProvidersRepo;

    @Autowired
    private CatLoadingRepo catLoadingRepo;

    @Autowired
    private CategoryRulesRepo rulesRepo;

    @Autowired
    private SelfFundParamsRepo selfFundParamsRepo;

    @Autowired
    private MedicalCardsRepo cardsRepo;

    @Autowired
    private TaxRatesRepo taxRatesRepo;

    @Autowired
    private ParamService paramService;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private CategoryMemberBenefitsRepo memberBenefitsRepo;

    @Autowired
    private RiskDocsRepo riskDocsRepo;

    @Override
    @Transactional(readOnly = false, rollbackFor = { EndorsementsException.class })
    public Long reviseTransaction(RevisionForm revisionForm) throws EndorsementsException {
        if (revisionForm.getPolicyId() == null || revisionForm.getPolicyId() == null)
            throw new EndorsementsException("Policy to Revise cannot be Empty....");

        if (revisionForm.getRevisionType() == null || StringUtils.isBlank(revisionForm.getRevisionType()))
            throw new EndorsementsException("Select Revision Type...");



        PolicyTrans currentTrans = policyRepo.findOne(revisionForm.getPolicyId());

        Predicate activePred = QPolicyTrans.policyTrans.polNo.eq(currentTrans.getPolNo())
                .and(QPolicyTrans.policyTrans.currentStatus.eq("A"));

        if (endorseService.countUnauthTransactions(currentTrans.getPolNo()) > 0) {
            throw new EndorsementsException(
                    "The Policy has unfinished Transaction");
        }

        if(policyRepo.count(activePred)> 1){
            throw new EndorsementsException("The current Policy has an active Endorsement. Only One Active Endorsement can be allowed");
        }

        if(!("EX".equalsIgnoreCase(revisionForm.getRevisionType()))){
            if("SP".equalsIgnoreCase( currentTrans.getTransType())) throw new EndorsementsException("Endorsement Type not Supported on Short Period Policy Transactions");
        }


        if(revisionForm.getEffectiveDate().after(currentTrans.getWetDate()) ||
                revisionForm.getEffectiveDate().before(currentTrans.getWefDate())){
            throw new EndorsementsException("Effective Date must be within Policy Period for Revision of Cover Transaction("+ dateUtils.formatDate(currentTrans.getWefDate()) +" to "+ dateUtils.formatDate(currentTrans.getWetDate()) +")");
        }
        if(revisionForm.getRevisionType().equalsIgnoreCase("NB")
                || revisionForm.getRevisionType().equalsIgnoreCase("SP")){
            throw new EndorsementsException("Endorsement Type not catered For. Contact the Vendor");
        }

        long endorsementCount = policyRepo.count(QPolicyTrans.policyTrans.polNo.eq(currentTrans.getPolNo()))+1;
        int remainingPeriod = dateUtils.daysBetweenUsingJoda(revisionForm.getEffectiveDate(),currentTrans.getWetDate())+1;

        PolicyTrans destination = new PolicyTrans();
        destination.setWefDate(revisionForm.getEffectiveDate());
        destination.setWetDate(currentTrans.getWetDate());
        destination.setCoverFrom(currentTrans.getCoverFrom());
        destination.setCoverTo(currentTrans.getCoverTo());
        destination.setRenewalDate(currentTrans.getRenewalDate());
        destination.setUwYear(currentTrans.getUwYear());
        destination.setPolRevStatus(revisionForm.getRevisionType());
        destination.setPolRevNo(currentTrans.getRevisionFormat()+"/"+endorsementCount);
        destination.setPolNo(currentTrans.getPolNo());
        destination.setAgent(currentTrans.getAgent());
        if("CN".equalsIgnoreCase(revisionForm.getRevisionType())){
            destination.setAuthStatus("R");
        }else
            destination.setAuthStatus("D");
        destination.setBinder(currentTrans.getBinder());
        destination.setBranch(currentTrans.getBranch());
        destination.setClient(currentTrans.getClient());
        destination.setClientPolNo(currentTrans.getClientPolNo());
        destination.setCommAllowed(currentTrans.isCommAllowed());
        destination.setCreatedUser(userUtils.getCurrentUser());
        destination.setCurrentStatus("D");
        destination.setFrequency(currentTrans.getFrequency());
        destination.setInterfaceType(currentTrans.getInterfaceType());
        destination.setOldpolNo(currentTrans.getOldpolNo());
        destination.setPaymentMode(currentTrans.getPaymentMode());
        destination.setPolCreateddt(new Date());
        destination.setPreviousTrans(currentTrans);
        destination.setProduct(currentTrans.getProduct());
        destination.setRenewable(currentTrans.isRenewable());
        destination.setBusinessType(currentTrans.getBusinessType());
        destination.setRevisionFormat(currentTrans.getRevisionFormat());
        destination.setTransCurrency(currentTrans.getTransCurrency());
        destination.setTransType(revisionForm.getRevisionType());
        destination.setCancellationType(revisionForm.getCancellationType());
       // destination.setCardType(currentTrans.getCardType());
        destination.setBinCardType(currentTrans.getBinCardType());
//        destination.setIssueCard(currentTrans.getIssueCard());
        destination.setMedicalCoverType(currentTrans.getMedicalCoverType());
        destination.setPrevBasicPrem(currentTrans.getBasicPrem());
        destination.setPrevEndosbasicPremium(currentTrans.getEndosbasicPremium());
        destination.setPrevNetPrem(currentTrans.getNetPrem());
        destination.setPrevPrem(currentTrans.getPremium());
        destination.setPrevFuturePrem(currentTrans.getFuturePrem());

        SystemTrans transaction = new SystemTrans();
        transaction.setDoneDate(new Date());
        transaction.setDoneBy(userUtils.getCurrentUser());
        transaction.setPolicy(destination);
        transaction.setTransLevel("U");
        transaction.setTransCode("APD"); //A way to setup and look up for transaction transcode
        transaction.setTransAuthorised("N");
        List<PolicyTaxes> newTaxes = new ArrayList<>();
        if("AD".equalsIgnoreCase(revisionForm.getRevisionType())) {
            Iterable<PolicyTaxes> taxes = polTaxesRepo.findAll(QPolicyTaxes.policyTaxes.policy.policyId.eq(currentTrans.getPolicyId()));
            for (PolicyTaxes tax : taxes) {
                if (tax.getRevenueItems().getItem() == RevenueItems.SD)
                    continue;

                PolicyTaxes newTax = new PolicyTaxes();
                newTax.setDivFactor(tax.getDivFactor());
                newTax.setPolicy(destination);
                newTax.setRateType(tax.getRateType());
                newTax.setRevenueItems(tax.getRevenueItems());
                newTax.setSubclass(tax.getSubclass());
                newTax.setTaxLevel(tax.getTaxLevel());
                newTax.setTaxRate(tax.getTaxRate());
                newTaxes.add(newTax);
            }
        }

        Iterable<PolicyClauses> clauses = polClausesRepo.findAll(QPolicyClauses.policyClauses.policy.policyId.eq(currentTrans.getPolicyId()));
        List<PolicyClauses> newClauses = new ArrayList<>();
        for(PolicyClauses clause:clauses){
            PolicyClauses newClause = new PolicyClauses();
            newClause.setClauHeading(clause.getClauHeading());
            newClause.setClause(clause.getClause());
            newClause.setClauWording(clause.getClauWording());
            newClause.setEditable(clause.isEditable());
            newClause.setNewClause("N");
            newClause.setPolicy(destination);
            newClauses.add(newClause);
        }

        Iterable<SelfFundParams> selfFundParams = selfFundParamsRepo.findAll(QSelfFundParams.selfFundParams.policyTrans.policyId.eq(currentTrans.getPolicyId()));
        List<SelfFundParams> newSelfFundParams = new ArrayList<>();
        for(SelfFundParams fundParam:selfFundParams){
            SelfFundParams newfundParam = new SelfFundParams();
            newfundParam.setApplicableLevel(fundParam.getApplicableLevel());
            newfundParam.setApplicableValue(fundParam.getApplicableValue());
            newfundParam.setBillingFrequency(fundParam.getBillingFrequency());
            newfundParam.setCarryForwardBalances(fundParam.isCarryForwardBalances());
            newfundParam.setDeductAdminFeeFromFund(fundParam.isDeductAdminFeeFromFund());
            newfundParam.setFundDepositAmount(fundParam.getFundDepositAmount());
            newfundParam.setMinDeposit(fundParam.getMinDeposit());
            newfundParam.setFundResetAmount(fundParam.getFundResetAmount());
            newfundParam.setPayWhenBenefitExhausted(fundParam.isPayWhenBenefitExhausted());
            newfundParam.setPayWhenFundExhausted(fundParam.isPayWhenFundExhausted());
            newfundParam.setSelfFundBalance(fundParam.getSelfFundBalance());
            newfundParam.setPolicyTrans(fundParam.getPolicyTrans());
            newSelfFundParams.add(newfundParam);
        }

        selfFundParamsRepo.save(newSelfFundParams);

        PolicyTrans savedPolicy = policyRepo.save(destination);
        polClausesRepo.save(newClauses);
        polTaxesRepo.save(newTaxes);
        transRepo.save(transaction);
        Iterable<MedicalCategory> categories = categoryRepo.findAll(QMedicalCategory.medicalCategory.policy.policyId.eq(currentTrans.getPolicyId())
        .and(QMedicalCategory.medicalCategory.categoryStatus.notEqualsIgnoreCase("D")));
        List<MedicalCategory> newCategories = new ArrayList<>();
        Set<PolicyTaxes> policyTaxes = new HashSet<>();
        for(MedicalCategory category:categories){
            MedicalCategory cat = new MedicalCategory();
            cat.setBedCost(category.getBedCost());
            cat.setBedTypes(category.getBedTypes());
            cat.setBinderDetails(category.getBinderDetails());
            cat.setDesc(category.getDesc());
            cat.setLoadingFactor(category.getLoadingFactor());
            cat.setPolicy(savedPolicy);
            cat.setShtDesc(category.getShtDesc());
            cat.setPrevLoadingPrem(category.getLoadingPrem());
            cat.setPrevPremium(category.getComputedPremium());
            cat.setCategoryStatus("E");
            MedicalCategory savedCat =  categoryRepo.save(cat);

//            if("CR".equalsIgnoreCase(revisionForm.getRevisionType())) {
//                Iterable<TaxRates> taxRates = taxRatesRepo.findAll((QTaxRates.taxRates.active.eq(true).and(QTaxRates.taxRates.mandatory.eq(Boolean.TRUE)))
//                        .and(QTaxRates.taxRates.revenueItems.item.eq(RevenueItems.RE).or(QTaxRates.taxRates.revenueItems.item.eq(RevenueItems.SC)))
//                        .and(QTaxRates.taxRates.subclass.eq(category.getBinderDetails().getSubCoverTypes().getSubclass()))
//                        .and(QTaxRates.taxRates.productsDef.proCode.eq(category.getPolicy().getProduct().getProCode())));
//               for(TaxRates tax:taxRates){
//                    if("B".equalsIgnoreCase(category.getPolicy().getBinder().getBinType()) || ("EN".equalsIgnoreCase(category.getPolicy().getTransType()))|| ("RN".equalsIgnoreCase(category.getPolicy().getTransType())) || ("EX".equalsIgnoreCase(category.getPolicy().getTransType()))){
//                        if(tax.getRevenueItems().getItem() == RevenueItems.SD)
//                            continue;
//                    }
//                    PolicyTaxes policyTax = new PolicyTaxes();
//                    policyTax.setPolicy(destination);
//                    policyTax.setRateType(tax.getRateType());
//                    policyTax.setRevenueItems(tax.getRevenueItems());
//                    policyTax.setSubclass(tax.getSubclass());
//                    policyTax.setTaxLevel(tax.getTaxLevel());
//                    policyTax.setTaxRate(tax.getTaxRate());
//                    policyTax.setDivFactor(tax.getDivFactor());
//                    policyTaxes.add(policyTax);
//                }
//            }
            Iterable<MedCategoryBenefits> benefits = benefitRepo.findAll(QMedCategoryBenefits.medCategoryBenefits.category.id.eq(category.getId())
            .and(QMedCategoryBenefits.medCategoryBenefits.status.notEqualsIgnoreCase("D")));
            List<MedCategoryBenefits> newBenefits = new ArrayList<>();
            for(MedCategoryBenefits benefit:benefits){
               // BigDecimal unusedprem= BigDecimal.ZERO;
//                BigDecimal prevusedprem= BigDecimal.ZERO;
//                if (benefit.getUsedPremium()==null)
//                    usedprem= BigDecimal.ZERO;
//                else
//                    usedprem=benefit.getUsedPremium();
//                if(benefit.getPrevusedPremium()==null)
//                    prevusedprem=BigDecimal.ZERO;
//                else
//                    prevusedprem = benefit.getPrevusedPremium();
//                if(!("CN".equalsIgnoreCase(revisionForm.getRevisionType()))) {
//                    unusedprem = new BigDecimal((benefit.getUnitPremium().setScale(2, BigDecimal.ROUND_HALF_EVEN).multiply(new BigDecimal(remainingPeriod).setScale(2, BigDecimal.ROUND_HALF_EVEN)).divide(new BigDecimal(365), 2, BigDecimal.ROUND_HALF_EVEN)).toPlainString());
//                }
                MedCategoryBenefits medCategoryBenefit = new MedCategoryBenefits();
                medCategoryBenefit.setApplicableAt(benefit.getApplicableAt());
                medCategoryBenefit.setCategory(savedCat);
                medCategoryBenefit.setCover(benefit.getCover());
                medCategoryBenefit.setFundLimit(benefit.getFundLimit());
                medCategoryBenefit.setLimit(benefit.getLimit());
                medCategoryBenefit.setWaitPeriod(benefit.getWaitPeriod());
                medCategoryBenefit.setPrevPremium(benefit.getComputedPremium());
                medCategoryBenefit.setWefDate(benefit.getWefDate());
                medCategoryBenefit.setWetDate(benefit.getWetDate());
                medCategoryBenefit.setPrevUnitPremium(benefit.getUnitPremium());
                //medCategoryBenefit.setUnusedPremium(unusedprem);
                medCategoryBenefit.setStatus("E");
                newBenefits.add(medCategoryBenefit);
            }
            benefitRepo.save(newBenefits);
            Iterable<CategoryMembers> categoryMembers = membersRepo.findAll(QCategoryMembers.categoryMembers.category.id.eq(category.getId())
          .and(QCategoryMembers.categoryMembers.memberStatus.equalsIgnoreCase("A")));
            List<MedicalCards> medicalCards = new ArrayList<>();
            for(CategoryMembers categoryMember:categoryMembers){
                CategoryMembers member = new CategoryMembers();
                member.setCategory(savedCat);
                member.setAge(categoryMember.getAge());
                member.setChildType(categoryMember.getChildType());
                member.setClient(categoryMember.getClient());
                member.setDependentTypes(categoryMember.getDependentTypes());
                member.setMainClient(categoryMember.getMainClient());
                member.setMemberShipNo(categoryMember.getMemberShipNo());
                member.setCardNo(categoryMember.getCardNo());
                member.setWefDate(categoryMember.getWefDate());
                member.setWetDate(categoryMember.getWetDate());
                member.setPrevPremium(categoryMember.getComputedPremium());
                member.setPrevsectId(categoryMember.getSectId());
                member.setPrevwefDate(categoryMember.getWefDate());
                member.setPrevwetDate(categoryMember.getWetDate());
                member.setMemberStatus("E");

                CategoryMembers savedMember = membersRepo.save(member);
                MedicalCards medicalCard  = cardsRepo.findOne(QMedicalCards.medicalCards.member.sectId.eq(categoryMember.getSectId()));
                if(medicalCard!=null && "Dispatched".equalsIgnoreCase(medicalCard.getStatus())) {
                    MedicalCards newMedCard = new MedicalCards();
                    newMedCard.setMember(savedMember);
                    newMedCard.setCardNo(medicalCard.getCardNo());
                    newMedCard.setUserReceived(null);
                    newMedCard.setWefDate(revisionForm.getEffectiveDate());
                    newMedCard.setWetDate(medicalCard.getWetDate());
                    newMedCard.setProcessedBy(userUtils.getCurrentUser());
                    newMedCard.setDateProcessed(new Date());
                    newMedCard.setStatus(medicalCard.getStatus());
                    medicalCards.add(newMedCard);
                }
                Iterable<RiskDocs> riskDocs = riskDocsRepo.findAll(QRiskDocs.riskDocs.member.sectId.eq(categoryMember.getSectId()));
                List<RiskDocs> newRiskDocs = new ArrayList<>();
                for (RiskDocs docs:riskDocs){
                    RiskDocs newDocs = new RiskDocs();
                    newDocs.setMember(savedMember);
                    newDocs.setCheckSum(docs.getCheckSum());
                    newDocs.setContentType(docs.getContentType());
                    newDocs.setReqdDocs(docs.getReqdDocs());
                    newDocs.setUploadedFileName(docs.getUploadedFileName());
                    newRiskDocs.add(newDocs);
                }
                riskDocsRepo.save(newRiskDocs);

                Iterable<CategoryMemberBenefits> prevMemBenefits =memberBenefitsRepo.findAll(QCategoryMemberBenefits.categoryMemberBenefits.member.sectId.eq(categoryMember.getSectId())
                .and(QCategoryMemberBenefits.categoryMemberBenefits.benefit.status.notEqualsIgnoreCase("D")));
                List<CategoryMemberBenefits> memBenefits = new ArrayList<>();
                for(CategoryMemberBenefits benefit:prevMemBenefits){
                    MedCategoryBenefits newBenefits1 = benefitRepo.findOne(QMedCategoryBenefits.medCategoryBenefits.category.id.eq(savedCat.getId())
                    .and(QMedCategoryBenefits.medCategoryBenefits.cover.id.eq(benefit.getBenefit().getCover().getId())));
//                   MedCategoryBenefits oldbenefits = benefitRepo.findOne(QMedCategoryBenefits.medCategoryBenefits.sectId.eq(benefit.getBenefit().getSectId()));
                   BigDecimal unusedprem= BigDecimal.ZERO;
                    BigDecimal unitPremium= BigDecimal.ZERO;
                    if (benefit.getUnitPremium()==null) {
                        unitPremium= BigDecimal.ZERO;
                    }else {
                        unitPremium =benefit.getUnitPremium();
                    }

//
//                    BigDecimal prevusedprem= BigDecimal.ZERO;
//                    if (benefit.getUsedPremium()==null)
//                        usedprem= BigDecimal.ZERO;
//                    else
//                        usedprem=benefit.getUsedPremium();
//                    if(benefit.getPrevusedPremium()==null)
//                        prevusedprem=BigDecimal.ZERO;
//                    else
//                        prevusedprem = benefit.getPrevusedPremium();
                    if(!("CN".equalsIgnoreCase(revisionForm.getRevisionType()) && "NR".equalsIgnoreCase(revisionForm.getCancellationType()))) {
                       int yeardays = dateUtils.daysBetweenUsingJoda(destination.getCoverFrom(),destination.getCoverTo())+1;
                        unusedprem = new BigDecimal((unitPremium.setScale(2, BigDecimal.ROUND_HALF_EVEN).multiply(new BigDecimal(remainingPeriod).setScale(2, BigDecimal.ROUND_HALF_EVEN)).divide(new BigDecimal(yeardays), 2, BigDecimal.ROUND_HALF_EVEN)).toPlainString());
                    }
                    CategoryMemberBenefits memBenefit = new CategoryMemberBenefits();
                    memBenefit.setComputedPremium(BigDecimal.ZERO);
                    memBenefit.setMember(savedMember);
                    memBenefit.setBenefit(newBenefits1);
                    memBenefit.setPrevPremium(benefit.getComputedPremium());
                    memBenefit.setPrevUnitPremium(benefit.getUnitPremium());
                    memBenefit.setPremium(BigDecimal.ZERO);
                    memBenefit.setWefDate(benefit.getWefDate());
                    memBenefit.setWetDate(benefit.getWetDate());
                    memBenefit.setPrevwetDate(benefit.getWetDate());
                    memBenefit.setPrevcmbId(benefit.getCmbId());
                    memBenefit.setPrevwefDate(benefit.getWefDate());
                    memBenefit.setUnusedPremium(unusedprem);
                    memBenefits.add(memBenefit);
                }
                memberBenefitsRepo.save(memBenefits);

            }
            if("CR".equalsIgnoreCase(revisionForm.getRevisionType())){
                cardsRepo.save(medicalCards);
            }


            Iterable<CategoryClauses> categoryClauses = clausesRepo.findAll(QCategoryClauses.categoryClauses.category.id.eq(category.getId()));
            List<CategoryClauses> catClauses = new ArrayList<>();
            for(CategoryClauses clause:categoryClauses){
                CategoryClauses newClause = new CategoryClauses();
                newClause.setCategory(savedCat);
                newClause.setClauHeading(clause.getClauHeading());
                newClause.setClause(clause.getClause());
                newClause.setClauWording(clause.getClauWording());
                newClause.setEditable(clause.isEditable());
                newClause.setNewClause(clause.getNewClause());
                catClauses.add(newClause);
            }
            clausesRepo.save(catClauses);
            Iterable<CategoryExclusions> categoryExclusions = exclusionsRepo.findAll(QCategoryExclusions.categoryExclusions.category.id.eq(category.getId()));
            List<CategoryExclusions> newExclusions = new ArrayList<>();
            for(CategoryExclusions exclusion:categoryExclusions){
                CategoryExclusions newExclusion = new CategoryExclusions();
                newExclusion.setCategory(savedCat);
                newExclusion.setAilment(exclusion.getAilment());
                newExclusions.add(newExclusion);
            }

            exclusionsRepo.save(newExclusions);

            Iterable<CatalogueProviders> catalogueProviders = catProvidersRepo.findAll(QCatalogueProviders.catalogueProviders.category.id.eq(category.getId()));
            List<CatalogueProviders> newCatProviders = new ArrayList<>();
            for(CatalogueProviders provider:catalogueProviders){
                CatalogueProviders newProvider = new CatalogueProviders();
                newProvider.setCategory(savedCat);
                newProvider.setProviders(provider.getProviders());
                newCatProviders.add(newProvider);
            }

            catProvidersRepo.save(newCatProviders);

            Iterable<CategoryLoadings> categoryLoadings = catLoadingRepo.findAll(QCategoryLoadings.categoryLoadings.category.id.eq(category.getId()));
            List<CategoryLoadings> newLoadings = new ArrayList<>();
            for(CategoryLoadings loading:categoryLoadings){
                CategoryLoadings newCategoryLoading = new CategoryLoadings();
                newCategoryLoading.setAilment(loading.getAilment());
                newCategoryLoading.setCategory(savedCat);
                newCategoryLoading.setLoadingAmt(loading.getLoadingAmt());
                newCategoryLoading.setRate(loading.getRate());
                newCategoryLoading.setRateType(loading.getRateType());
                newLoadings.add(newCategoryLoading);
            }

            catLoadingRepo.save(newLoadings);

            Iterable<CategoryRules> categoryRules = rulesRepo.findAll(QCategoryRules.categoryRules.category.id.eq(category.getId()));
            List<CategoryRules> newRules = new ArrayList<>();
            for(CategoryRules rule:categoryRules){
                CategoryRules newRule = new CategoryRules();
                newRule.setDesc(rule.getDesc());
                newRule.setCategory(savedCat);
                newRule.setBinderRules(rule.getBinderRules());
                newRule.setShtDesc(rule.getShtDesc());
                newRule.setValue(rule.getValue());
                newRules.add(newRule);
            }

            rulesRepo.save(newRules);


        }
        polTaxesRepo.save(policyTaxes);
        workflowService.startNewWorkFlow(DocType.GEN_UW_DOCUMENT,String.valueOf(savedPolicy.getPolicyId()),savedPolicy,"N",null,null,null, null);
//        workflowService.startNewWorkFlow(DocType.GEN_UW_DOCUMENT,String.valueOf(savedPolicy.getPolicyId()),savedPolicy,"N",null,null,null,savedPolicy.getClient());
        Long polCode = savedPolicy.getPolicyId();
        boolean medicalProduct = false;
        if (savedPolicy.getProduct().getProGroup().getPrgType() == null || !savedPolicy.getProduct().getProGroup().getPrgType().equalsIgnoreCase("MD")) {
            medicalProduct = false;
        } else if (savedPolicy.getProduct().getProGroup().getPrgType().equalsIgnoreCase("MD")) {
            medicalProduct = true;
        }
        // below code was added since cancellation lands to the next screen when it already in ready state
        if("CN".equalsIgnoreCase(revisionForm.getRevisionType()) && "R".equalsIgnoreCase(savedPolicy.getAuthStatus())){
            Map<String, Object> processVariables = Maps.newHashMap();
            processVariables.put("canAuthorize", true);
            workflowService.completeTask(String.valueOf(polCode), processVariables, savedPolicy, DocType.GEN_UW_DOCUMENT, (medicalProduct) ? "Y" : "N", null, null, null,null);

        }
        return savedPolicy.getPolicyId();
    }


    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void replaceCard(Long cardId, Long polCode) throws BadRequestException {
        if(cardId==null) throw new BadRequestException("Card Not found");
        MedicalCards medicalCard = cardsRepo.findOne(cardId);
        if("Cancelled".equalsIgnoreCase(medicalCard.getStatus()))
            throw new BadRequestException("Card Already Cancelled...Cannot be Replaced");
        if(!"Dispatched".equalsIgnoreCase(medicalCard.getStatus())){
            throw new BadRequestException("Cannot Replace A card that is not dispatched");
        }
        CategoryMembers member = membersRepo.findOne(QCategoryMembers.categoryMembers.category.policy.policyId.eq(polCode)
        .and(QCategoryMembers.categoryMembers.client.eq(medicalCard.getMember().getClient())));
        MedicalCards newMedCard = new MedicalCards();
        newMedCard.setMember(member);
        newMedCard.setCardNo(medicalCard.getCardNo());
        newMedCard.setUserReceived(null);
        newMedCard.setWefDate(medicalCard.getWefDate());
        newMedCard.setWetDate(medicalCard.getWetDate());
        newMedCard.setProcessedBy(userUtils.getCurrentUser());
        newMedCard.setDateProcessed(new Date());
        newMedCard.setStatus("Draft");
        newMedCard.setPrevCard(medicalCard);
        medicalCard.setStatus("Cancelled");
        cardsRepo.save(medicalCard);
        MedicalCards savedCard =  cardsRepo.save(newMedCard);
        long count = cardsRepo.count(QMedicalCards.medicalCards.cardNo.eq(medicalCard.getCardNo()).and(QMedicalCards.medicalCards.status.eq("Draft")));
        if(count > 1){
           throw new BadRequestException("Error Replacing cards...Contact Administrator");
        }
        computeReplacementTaxes(savedCard.getMember().getCategory().getPolicy().getPolicyId());

    }

    @Transactional(readOnly = false,propagation = Propagation.REQUIRED)
    public void computeReplacementTaxes(Long policyCode) throws BadRequestException{
        PolicyTrans policyTrans = policyRepo.findOne(policyCode);
        if(policyTrans.getBinCardType()!=null) {
            Long memberCount = cardsRepo.countPolicyMedicalCards(policyCode);
//            Iterable<PolicyTaxes> memberTaxes = polTaxesRepo.findAll(QPolicyTaxes.policyTaxes.policy.policyId.eq(policyTrans.getPolicyId())
//                    .and(QPolicyTaxes.policyTaxes.taxLevel.eq("R")).and(QPolicyTaxes.policyTaxes.revenueItems.item.eq(RevenueItems.RE)));
            BigDecimal reissueCharge = BigDecimal.ZERO;
            BigDecimal serviceCharge = BigDecimal.ZERO;
            BigDecimal polIssueCard = BigDecimal.ZERO;
            BigDecimal polServiceCharge = BigDecimal.ZERO;
            BigDecimal polVatAmt = BigDecimal.ZERO;
//            if ("P".equalsIgnoreCase(policyTrans.getCardType())) {
//                reissueCharge = paramService.getParamValue("PHOTO_REISSUE_FEE");
//            } else if ("E".equalsIgnoreCase(policyTrans.getCardType())) {
//                reissueCharge = paramService.getParamValue("ELECTRONIC_REISSUE_FEE");
//                serviceCharge = paramService.getParamValue("SERVICE_CHARGE");
//            }
            reissueCharge=policyTrans.getBinCardType().getCardReissueFee();
            //serviceCharge=policyTrans.getBinCardType().getServiceCharge();
            int remainingPeriod = dateUtils.daysBetweenUsingJoda(policyTrans.getWefDate(),policyTrans.getWetDate())+1;
            // service charge is not applicable on card replacement as guided by Francis Wachira
//            if ("Y".equalsIgnoreCase(policyTrans.getBinCardType().getServiceProrated()))
//                serviceCharge= new BigDecimal((policyTrans.getBinCardType().getServiceCharge().setScale(2, BigDecimal.ROUND_HALF_EVEN).multiply(new BigDecimal(remainingPeriod).setScale(2, BigDecimal.ROUND_HALF_EVEN)).divide(new BigDecimal(365),2,BigDecimal.ROUND_HALF_EVEN)).toPlainString());
//            else
//                serviceCharge=policyTrans.getBinCardType().getServiceCharge();
            if(policyTrans.getBinCardType().getVatApplicable()!=null) {
                if ("Y".equalsIgnoreCase(policyTrans.getBinCardType().getVatApplicable())) {
                    polVatAmt = polVatAmt.add(polServiceCharge.multiply(policyTrans.getAgent().getAccountType().getVatRate().divide(BigDecimal.valueOf(100))));
                    polVatAmt = polVatAmt.add(polIssueCard.multiply(policyTrans.getAgent().getAccountType().getVatRate().divide(BigDecimal.valueOf(100))));
                }
            }
            polIssueCard = reissueCharge.multiply(BigDecimal.valueOf(memberCount));
            polServiceCharge = serviceCharge.multiply(BigDecimal.valueOf(memberCount));
            if(policyTrans.getBinCardType().getVatApplicable()!=null) {
                if ("Y".equalsIgnoreCase(policyTrans.getBinCardType().getVatApplicable())) {
                    polVatAmt = polVatAmt.add(polServiceCharge.multiply(policyTrans.getAgent().getAccountType().getVatRate().divide(BigDecimal.valueOf(100))));
                    polVatAmt = polVatAmt.add(polIssueCard.multiply(policyTrans.getAgent().getAccountType().getVatRate().divide(BigDecimal.valueOf(100))));
                }
            }
//            for (PolicyTaxes polTaxes : memberTaxes) {
//                if (polTaxes.getRevenueItems().getItem().equals(RevenueItems.RE)) {
//                    polIssueCard = reissueCharge.multiply(BigDecimal.valueOf(memberCount));
//                    polTaxes.setTaxAmount(polIssueCard);
//                    polTaxes.setTaxRate(reissueCharge);
//                } else if (polTaxes.getRevenueItems().getItem().equals(RevenueItems.SC)) {
//                    polServiceCharge = serviceCharge.multiply(BigDecimal.valueOf(memberCount));
//                    polTaxes.setTaxAmount(polServiceCharge);
//                    polTaxes.setTaxRate(serviceCharge);
//                }
//            }
           // polTaxesRepo.save(memberTaxes);
            policyTrans.setReissueCardFee(polIssueCard);
            policyTrans.setVatAmount(polVatAmt);
            policyTrans.setServiceCharge(polServiceCharge);
            policyTrans.setBasicPrem((polIssueCard).add(polServiceCharge).add(polVatAmt));
            policyTrans.setNetPrem((polIssueCard).add(polServiceCharge).add(polVatAmt));
            policyRepo.save(policyTrans);
        }
    }
}