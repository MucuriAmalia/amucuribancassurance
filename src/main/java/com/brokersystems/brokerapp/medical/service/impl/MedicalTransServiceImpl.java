
package com.brokersystems.brokerapp.medical.service.impl;

import com.brokersystems.brokerapp.certs.service.CertService;
import com.brokersystems.brokerapp.enums.RevenueItems;
import com.brokersystems.brokerapp.kie.rules.ClientRulesExecutor;
import com.brokersystems.brokerapp.medical.model.*;
import com.brokersystems.brokerapp.medical.repository.*;
import com.brokersystems.brokerapp.medical.service.MedicalComputePrem;
import com.brokersystems.brokerapp.medical.service.MedicalTransService;
import com.brokersystems.brokerapp.medical.service.RulesService;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.DateUtilities;
import com.brokersystems.brokerapp.server.utils.MemberUploadUtils;
import com.brokersystems.brokerapp.server.utils.StreamsHelper;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.*;
import com.brokersystems.brokerapp.trans.model.*;
import com.brokersystems.brokerapp.trans.repository.SystemTransRepo;
import com.brokersystems.brokerapp.trans.repository.SystemTransactionsRepo;
import com.brokersystems.brokerapp.uw.dtos.BinderDTO;
import com.brokersystems.brokerapp.uw.dtos.PolicyCreateDTO;
import com.brokersystems.brokerapp.uw.dtos.TaxesDTO;
import com.brokersystems.brokerapp.uw.model.*;
import com.brokersystems.brokerapp.uw.repository.*;
import com.brokersystems.brokerapp.uw.service.PolicyTransService;
import com.brokersystems.brokerapp.uw.service.PremComputeService;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.BooleanExpression;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by peter on 4/26/2017.
 */
@Service
public class MedicalTransServiceImpl implements MedicalTransService {

    @Autowired
    private BindersRepo binderRepo;

    @Autowired
    private MedicalCategoryRepo medicalCategoryRepo;

    @Autowired
    private ProductsRepo productRepo;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private OrgBranchRepository branchRepo;

    @Autowired
    private TaxRatesRepo taxRatesRepo;

    @Autowired
    private PremComputeService premComputeService;

    @Autowired
    private CategoryFamilySizeRepo familySizeRepo;

    @Autowired
    private DependentTypesRepo dependentTypesRepo;

    @Autowired
    private BinderDetRepo binderDetRepo;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private CategoryBenefitRepo benefitRepo;

    @Autowired
    BinderMedicalCardsRepo binderMedicalCardsRepo;

    @Autowired
    private PostalCodeRepo postalCodeRepo;

    @Autowired
    private PaymentModeRepo paymentModeRepo;

    @Autowired
    private MobPrefixRepo mobPrefixRepo;

 @Autowired
 private  CurrencyRepository currencyRepo;
    @Autowired
    private MedicalCoversRepo medicalCoversRepo;

    @Autowired
    private PolUnassignedTaxesRepo unassignedTaxesRepo;

    @Autowired
    private PolicyTransService policyTransService;

    @Autowired
    private CertService certService;

    @Autowired
    private CategoryMembersRepo membersRepo;

    @Autowired
    private MedicalComputePrem computePrem;

    @Autowired
    private SequenceRepository sequenceRepo;

    @Autowired
    private CategoryRulesRepo categoryRulesRepo;

    @Autowired
    private ClientRulesExecutor clientRulesExecutor;

    @Autowired
    private ClientRepository clientRepo;

    @Autowired
    private ClientTypeRepo clientTypeRepo;

    @Autowired
    private MedicalRulesRepo rulesRepo;

    @Autowired
    private DateUtilities dateUtils;

    @Autowired
    private RulesService rulesService;

    @Autowired
    private CategoryClausesRepo clausesRepo;

    @Autowired
    private SectionTransRepo sectionRepo;

    @Autowired
    private BinderClauseRepo binderClauseRepo;

    @Autowired
    private SubClausesRepo subclauseRepo;

    @Autowired
    private CatLoadingRepo loadingRepo;

    @Autowired
    private CatExclusionsRepo exclusionsRepo;

    @Autowired
    private BinderExclusionsRepo exclusionRepo;

    @Autowired
    private BinderLoadingsRepo loadingsRepo;

    @Autowired
    private CatProvidersRepo providersRepo;

    @Autowired
    private BinderProvidersRepo binderProvidersRepo;

    @Autowired
    private CoverLimitsRepo coverLimitsRepo;

    @Autowired
    private ServiceProviderContractRepo providerContractRepo;

    @Autowired
    private SelfFundParamsRepo selfFundParamsRepo;

    @Autowired
    private MedicalRulesRepo medicalRulesRepo;

    @Autowired
    private MemberUploadUtils uploadUtils;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private TownRepository townRepository;

    @Autowired
    private OccupationRepo occupationRepo;

    @Autowired
    private OrgBranchRepository branchRepository;

    @Autowired
    private ServiceProvidersRepo serviceProvidersRepo;

    @Autowired
    private PolClausesRepo polClausesRepo;

    @Autowired
    private BedTypesRepo bedTypesRepo;

    @Autowired
    private PolTaxesRepo polTaxesRepo;

    @Autowired
    private SystemTransactionsRepo transactionsRepo;

    @Autowired
    private SystemTransRepo transRepo;

    @Autowired
    private PolicyTransRepo policyRepo;

    @Autowired
    private RiskTransRepo riskRepo;

    @Autowired
    private MedicalParRepo medicalParRepo;

    @Autowired
    private BinderReqrdDocsRepo reqrdDocsRepo;

    @Autowired
    private RiskDocsRepo riskDocsRepo;

    @Autowired
    private SubclassReqDocRepo subclassReqDocRepo;

    @Autowired
    private CategoryMemberBenefitsRepo memberBenefitsRepo;

    @Autowired
    private ClientTitleRepo clientTitleRepo;


    @Transactional(readOnly = true)
    @Override
    public Page<BinderDTO> findMedicalInsuranceBinder(String paramString, Pageable pageable, String binType) {
//        StringBuilder searchValue = new StringBuilder();
//        if (paramString == null) searchValue = new StringBuilder("%%");
//        else {
//            String[] splitStr = paramString.trim().split("\\s+");
//            for (String search : splitStr) {
//                searchValue.append("%").append(search).append("%");
//            }
//        }
        final List<BinderDTO> binders = new ArrayList<>();
        List<Object[]> binderList = binderRepo.searchMedicalProductBinders(pageable.getPageNumber(), pageable.getPageSize());
        long rowCount = 0L;
        if (!binderList.isEmpty()) rowCount = ((BigInteger) binderList.get(0)[9]).intValue();
        for (Object[] binder : binderList) {
            BinderDTO binderDTO = new BinderDTO();
            binderDTO.setBinId(((BigInteger) binder[0]).longValue());
            binderDTO.setName((String) binder[1]);
            binderDTO.setProDesc((String) binder[2]);
            binderDTO.setAcctId(((BigInteger) binder[3]).longValue());
            binderDTO.setProCode(((BigInteger) binder[4]).longValue());
            binderDTO.setBinPolNo((String) binder[5]);
            binderDTO.setBinName((String) binder[6]);
            binderDTO.setAgeApplicable((String) binder[7]);
            binderDTO.setMotorProduct((Boolean) binder[8]);
            binders.add(binderDTO);
        }
        return new PageImpl<>(binders, pageable, rowCount);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BinderCardTypes> getBinderCardTypes(String paramString, Pageable paramPageable,Long binCode,Date wefDate) throws IllegalAccessException {
        Iterable<BinderMedicalCards> cardTypes= binderMedicalCardsRepo.findAll(QBinderMedicalCards.binderMedicalCards.binder.binId.eq(binCode));
        List<BinderCardTypes> cardTypes1= new ArrayList<>();
        for (BinderMedicalCards card:cardTypes){
            BinderCardTypes binderCardTypes = new BinderCardTypes();
            if (wefDate.after(card.getWefDate()) || wefDate.equals(card.getWefDate())){
                if (card.getWetDate()!=null){
                    if (wefDate.before(card.getWetDate())){

                        binderCardTypes.setBinCardDesc(card.getCardTypes().getCardDesc());
                        binderCardTypes.setBinCardId(card.getId());
                        cardTypes1.add(binderCardTypes);
                    }

                }else {
                    //cardTypes1.add(new BinderCardTypes(card.getId(),card.getCardTypes().getCardDesc()));
                    binderCardTypes.setBinCardDesc(card.getCardTypes().getCardDesc());
                    binderCardTypes.setBinCardId(card.getId());
                    cardTypes1.add(binderCardTypes);
                }
            }


        }
        Page<BinderCardTypes> checksPage = new PageImpl<>(cardTypes1);
        return checksPage;
        //binderMedicalCardsRepo.getBinderCardTypes(binCode,wefDate);
    }
    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<MedicalCategory> findPolicyCategories(DataTablesRequest request, Long polCode) throws IllegalAccessException {
        BooleanExpression pred = QMedicalCategory.medicalCategory.policy.policyId.eq(polCode);
        Page<MedicalCategory> page = medicalCategoryRepo.findAll(pred.and(request.searchPredicate(QMedicalCategory.medicalCategory)), request);
        return new DataTablesResult<>(request, page);

    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<MedicalCategory> findCategoryBedType(DataTablesRequest request, Long catId) throws IllegalAccessException{
        BooleanExpression pred =QMedicalCategory.medicalCategory.id.eq(catId);
        Page<MedicalCategory> page =medicalCategoryRepo.findAll(pred,request);
        return new DataTablesResult<>(request,page);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void defineMedicalCategories(MedicalCategory medicalCategory) throws BadRequestException {
        PolicyTrans policyTrans = medicalCategory.getPolicy();
        boolean newRecord = medicalCategory.getId()==null;
        if(medicalCategory.getId()==null) {
            if (policyTrans.getMedicalCoverType() != null && "I".equalsIgnoreCase(policyTrans.getMedicalCoverType())) {
                Long count = medicalCategoryRepo.count(QMedicalCategory.medicalCategory.policy.policyId.eq(policyTrans.getPolicyId()));
                if (count > 0)
                    throw new BadRequestException("Cannot Have more than one category for an Individual Policy");
            }
            medicalCategory.setCategoryStatus("N");
        }
        //if(medicalCategory.getBedTypes()==null)
          //  throw new BadRequestException("Select Bed Type for this category");
        //if(medicalCategory.getBedCost()==null || medicalCategory.getBedCost().compareTo(BigDecimal.ZERO) <=0)
         //   medicalCategory.setBedCost(medicalCategory.getBedTypes().getBedCost());

//        if(medicalCategory.getBedTypes()==null)
//            throw new BadRequestException("Select Bed Type for this category");
//        if(medicalCategory.getBedCost()==null || medicalCategory.getBedCost().compareTo(BigDecimal.ZERO) <=0)
//            medicalCategory.setBedCost(medicalCategory.getBedTypes().getBedCost());
        MedicalCategory category =  medicalCategoryRepo.save(medicalCategory);
        if(!newRecord) {
            return;
        }
        Iterable<MedicalCovers> medicalCovers = null;
        Iterable<MedicalBinderRules> binderRules = null;
        if(policyTrans.getBinder().getFundBinder()==null || "N".equalsIgnoreCase(policyTrans.getBinder().getFundBinder())){
            binderRules = rulesRepo.findAll(QMedicalBinderRules.medicalBinderRules.binder.binId.eq(medicalCategory.getBinderDetails().getBinder().getBinId()));
            medicalCovers = medicalCoversRepo.findAll(QMedicalCovers.medicalCovers.binderDet.detId.eq(medicalCategory.getBinderDetails().getDetId()));
        }
        else if("Y".equalsIgnoreCase(policyTrans.getBinder().getFundBinder())){
            binderRules = rulesRepo.findAll(QMedicalBinderRules.medicalBinderRules.binder.binId.eq(medicalCategory.getBinderDetails().getBinder().getBinId())
                    .and(QMedicalBinderRules.medicalBinderRules.mandatory.equalsIgnoreCase("Y")));
            medicalCovers = medicalCoversRepo.findAll(QMedicalCovers.medicalCovers.binderDet.detId.eq(medicalCategory.getBinderDetails().getDetId())
                    .and(QMedicalCovers.medicalCovers.fundCoverMand.eq("Y")));

        }

        Set<MedCategoryBenefits> categoryBenefits =StreamsHelper.stream(medicalCovers)
                .map(a -> {
                    Iterable<CoverLimits> coverLimits = coverLimitsRepo.findAll(QCoverLimits.coverLimits.covers.eq(a));
                    CoverLimits limit = null;
                    for(CoverLimits coverLimit:coverLimits){
                        limit = coverLimit;
                        break;
                    }
                    MedCategoryBenefits benefit = new MedCategoryBenefits();
                    if(limit!=null)
                        benefit.setLimit(limit);
                    benefit.setCover(a);
                    benefit.setCategory(category);
                    benefit.setWaitPeriod(a.getWaitPeriod());
                    benefit.setWefDate(category.getPolicy().getWefDate());
                    benefit.setWetDate(category.getPolicy().getWetDate());
                    benefit.setStatus("N");
                    return benefit;
                }).collect(Collectors.toSet());
        benefitRepo.save(categoryBenefits);
        Set<CategoryRules> categoryRules = StreamsHelper.stream(binderRules)
                .map( a -> {    CategoryRules rule = new CategoryRules();
                    rule.setShtDesc(a.getChecks().getChecks().getCheckShtDesc());
                    rule.setDesc(a.getDesc());
                    rule.setValue(a.getValue());
                    rule.setBinderRules(a);
                    rule.setCategory(category);
                    return rule;
                } ).collect(Collectors.toSet());
        categoryRulesRepo.save(categoryRules);
        populateCategoryClauses(policyTrans);
        populateCategoryTaxes(policyTrans);
        populateCategoryExclusions(policyTrans);
        populateCategoryLoadings(policyTrans);
        populateCategoryProviders(policyTrans);
    }
    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void defineMedicalCategoryBedType(MedicalCategory medicalCategory) throws BadRequestException {
        PolicyTrans policyTrans = medicalCategory.getPolicy();
        if(!(medicalCategory.getBedTypes()==null)) {
            if (medicalCategory.getBedCost() == null || medicalCategory.getBedCost().compareTo(BigDecimal.ZERO) <= 0)
                medicalCategory.setBedCost(medicalCategory.getBedTypes().getBedCost());
            MedicalCategory category =  medicalCategoryRepo.save(medicalCategory);
        }

    }
    @Override
    @Transactional(readOnly = false)
    public void deleteMedicalCategory(Long catId) {
        medicalCategoryRepo.delete(catId);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<CategoryFamilySize> getCategoryFamilySize(DataTablesRequest request, Long catId) throws IllegalAccessException {
        BooleanExpression pred = QCategoryFamilySize.categoryFamilySize.category.id.eq(catId);
        Page<CategoryFamilySize> page = familySizeRepo.findAll(pred.and(request.searchPredicate(QCategoryFamilySize.categoryFamilySize)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DependentTypes> findDependentTypes(String paramString, Pageable paramPageable) {
        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred = QDependentTypes.dependentTypes.isNotNull();
        } else {
            pred = QDependentTypes.dependentTypes.depDesc.containsIgnoreCase(paramString)
                    .or(QDependentTypes.dependentTypes.depShtDesc.containsIgnoreCase(paramString));
        }
        return dependentTypesRepo.findAll(pred, paramPageable);
    }

    @Override
    @Transactional(readOnly = false)
    public void defineCategoryFamilySize(CategoryFamilySize familySize) throws BadRequestException {
        familySizeRepo.save(familySize);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteFamSize(Long sizeId) {
        familySizeRepo.delete(sizeId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BinderDetails> findBinderDetails(String paramString, Pageable paramPageable, Long bindCode) {
        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred = QBinderDetails.binderDetails.binder.binId.eq(bindCode).and(QBinderDetails.binderDetails.isNotNull());
        } else {
            pred = QBinderDetails.binderDetails.binder.binId.eq(bindCode).and(QBinderDetails.binderDetails.subCoverTypes.subclass.subDesc.containsIgnoreCase(paramString)
                    .or(QBinderDetails.binderDetails.subCoverTypes.coverTypes.covName.containsIgnoreCase(paramString)));
        }
        return binderDetRepo.findAll(pred, paramPageable);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<MedCategoryBenefits> getCategoryBenefits(DataTablesRequest request, Long catId) throws IllegalAccessException {
        BooleanExpression pred = QMedCategoryBenefits.medCategoryBenefits.category.id.eq(catId);
        Page<MedCategoryBenefits> page = benefitRepo.findAll(pred.and(request.searchPredicate(QMedCategoryBenefits.medCategoryBenefits)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteCatBenefits(Long benId) {
        benefitRepo.delete(benId);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<CategoryMembers> getCategoryMembers(DataTablesRequest request, Long catId) throws IllegalAccessException {
        BooleanExpression pred = QCategoryMembers.categoryMembers.category.id.eq(catId);
        Page<CategoryMembers> page = membersRepo.findAll(pred.and(request.searchPredicate(QCategoryMembers.categoryMembers)), request);
        return new DataTablesResult<>(request, page);
    }


    @Override
    public List<SubClassReqdDocs> findUnassignedMemberDocs(Long memberId, String docName) throws IllegalAccessException {
        CategoryMembers member = membersRepo.findOne(memberId);
        PolicyTrans policy = member.getCategory().getPolicy();
        String transType = "";
        if (policy.getTransType() == null || StringUtils.isBlank(policy.getTransType())) {
            transType = "NB";
        } else
            transType = policy.getTransType();
        if ("CO".equalsIgnoreCase(policy.getTransType())) {
            transType = policy.getPreviousTrans().getTransType();
        }
        if ("NB".equalsIgnoreCase(transType)) {
            transType = "NB";
        } else if ("RN".equalsIgnoreCase(transType)) {
            transType = "RN";
        } else {
            transType = "EN";
        }
        List<SubClassReqdDocs> reqdDocses = subclassReqDocRepo.getUnassignedMemberReqDocs(member.getSectId(), member.getCategory().getBinderDetails().getSubCoverTypes().getSubclass().getSubId(), docName);

        if ("NB".equalsIgnoreCase(transType)) {
            return reqdDocses.stream().filter(a -> a.getRequiredDoc().isAppliesNewBusiness()).collect(Collectors.toList());
        } else if ("EN".equalsIgnoreCase(transType)) {
            return reqdDocses.stream().filter(a -> a.getRequiredDoc().isAppliesEndorsement()).collect(Collectors.toList());
        } else if ("RN".equalsIgnoreCase(transType)) {
            return reqdDocses.stream().filter(a -> a.getRequiredDoc().isAppliesRenewal()).collect(Collectors.toList());
        } else return new ArrayList<>();
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void defineMember(CategoryMembers member) throws BadRequestException {
        Boolean memExists = false;
        if (!(member.getSectId()==null)){
            memExists=true;
        }

        if(member.getClient() == null) throw new BadRequestException("Provide Member Details...");
        if(member.getClient().getGender()== null) throw new BadRequestException("Gender is compulsory....");
        if(member.getClient().getDob()==null)
            throw new BadRequestException("Member must have Date of Birth...");
        if(member.getDependentTypes()==null) throw new BadRequestException("Select Dependent Type");
        if("P".equalsIgnoreCase(member.getDependentTypes())){
            PolicyTrans policyTrans = member.getCategory().getPolicy();
            MedicalCategory category = member.getCategory();
            if(policyTrans.getMedicalCoverType()!=null && "I".equalsIgnoreCase(policyTrans.getMedicalCoverType())){
                Long count = membersRepo.count(QCategoryMembers.categoryMembers.category.id.eq(category.getId()).and(QCategoryMembers.categoryMembers.dependentTypes.eq("P")));
                if(count > 0) throw new BadRequestException("Cannot Have more than one Principal for an Individual Policy");
            }
            member.setMainClient(member.getClient());
        }
        else {
            if(member.getMainClient() == null) throw new BadRequestException("Provide Principal Details...");
            if(member.getClient().getTenId()  == member.getMainClient().getTenId())
                throw new BadRequestException("Member cannot be the same as principal");

            Long count = membersRepo.count(QCategoryMembers.categoryMembers.client.tenId.eq(member.getClient().getTenId()).and(QCategoryMembers.categoryMembers.category.id.eq(member.getCategory().getId())));
            if(count > 0 && member.getSectId()==null) throw new BadRequestException("Member selected already exists in this category...");
            Long mainCount = membersRepo.count(QCategoryMembers.categoryMembers.client.tenId.eq(member.getMainClient().getTenId()).and(QCategoryMembers.categoryMembers.dependentTypes.eq("P"))
                    .and(QCategoryMembers.categoryMembers.category.id.eq(member.getCategory().getId())));
            if(mainCount ==0) throw new BadRequestException("Principal selected has not been created for this category");
            if("S".equalsIgnoreCase(member.getDependentTypes())){
                String gender = member.getClient().getGender();
                String otherGender = member.getMainClient().getGender();
                Long spouseCount = membersRepo.count(QCategoryMembers.categoryMembers.mainClient.tenId.eq(member.getMainClient().getTenId())
                        .and(QCategoryMembers.categoryMembers.dependentTypes.eq("S"))
                        .and(QCategoryMembers.categoryMembers.category.id.eq(member.getCategory().getId())));
                if (member.getSectId()==null) {
                    if (spouseCount > 0)
                        throw new BadRequestException("Spouse has Already been created for this Principal");
                }else{

                    spouseCount = membersRepo.count(QCategoryMembers.categoryMembers.mainClient.tenId.eq(member.getMainClient().getTenId())
                            .and(QCategoryMembers.categoryMembers.dependentTypes.eq("S"))
                            .and(QCategoryMembers.categoryMembers.sectId.ne(member.getSectId()))
                            .and(QCategoryMembers.categoryMembers.category.id.eq(member.getCategory().getId())));
                    if (spouseCount > 0)
                    throw new BadRequestException("Spouse has Already been created for this Principal");
                }

                if(StringUtils.equalsIgnoreCase(gender,otherGender)){
                    throw new BadRequestException("Gender for Spouse and Principal cannot be the same....");
                }
            }
        }
        PolicyTrans policy = member.getCategory().getPolicy();
        Date polWef = dateUtils.removeTime(policy.getWefDate());
        Date polWet = dateUtils.removeTime(policy.getWetDate());
        Date riskWef = dateUtils.removeTime(member.getWefDate());
        Date riskWet = dateUtils.removeTime(member.getWetDate());
        if(riskWef.before(polWef) || riskWef.after(polWet)
                || riskWet.before(polWef) || riskWet.after(polWet)){
            throw new BadRequestException("Member Cover Dates outside Policy Cover Periods ");
        }
        if (member.getSectId()==null) {
            Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("M");
            if (sequenceRepo.count(seqPredicate) == 0)
                throw new BadRequestException("Sequence for Medical Membership has not been defined");
            SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
            Long seqNumber = sequence.getNextNumber();
            sequence.setLastNumber(seqNumber);
            sequence.setNextNumber(seqNumber + 1);
            sequenceRepo.save(sequence);
            member.setMemberShipNo(String.format("%016d", seqNumber));
        } else {
            CategoryMembers existingMember = membersRepo.findOne(QCategoryMembers.categoryMembers.sectId.eq(member.getSectId()));
            member.setMemberShipNo(existingMember.getMemberShipNo());
        }
        if (member.getMemberHasCard()==null)
            member.setMemberHasCard("N");
        if (member.getMemberHasCard().equalsIgnoreCase("N")&& member.getAutoGenerateCard().equalsIgnoreCase("Y")
                && policy.getBinder().getFundBinder().equalsIgnoreCase("Y")
                && (member.getCardNo()==null || org.apache.commons.lang.StringUtils.isBlank(member.getCardNo()))){
            Predicate seqCardPredicate = QSystemSequence.systemSequence.transType.eq("CRD");
            if (sequenceRepo.count(seqCardPredicate) == 0)
                throw new BadRequestException("Sequence for Medical Cards has not been defined");
            SystemSequence cardSequence = sequenceRepo.findOne(seqCardPredicate);
            Long seqCardNumber = cardSequence.getNextNumber();
            cardSequence.setLastNumber(seqCardNumber);
            cardSequence.setNextNumber(seqCardNumber + 1);
            sequenceRepo.save(cardSequence);
            member.setCardNo(String.format("%016d", seqCardNumber));
        }else if (member.getCardNo()==null || org.apache.commons.lang.StringUtils.isBlank(member.getCardNo()))
            member.setCardNo(null);

        if (policy.getTransType().equalsIgnoreCase("NB") ||policy.getTransType().equalsIgnoreCase("AD")) {
            member.setMemberStatus("N");
        }
        CategoryMembers savedMember  = membersRepo.save(member);
        if (!memExists) {
            Iterable<BinderReqrdDocs> reqdDocs = reqrdDocsRepo.findAll(QBinderReqrdDocs.binderReqrdDocs.binderDetail.detId.eq(savedMember.getCategory().getBinderDetails().getDetId())
                    .and(QBinderReqrdDocs.binderReqrdDocs.mandatory.eq(true))
                    .and(QBinderReqrdDocs.binderReqrdDocs.requiredDocs.requiredDoc.appliesNewBusiness.eq(true)));
            List<RiskDocs> riskDocs = new ArrayList<>();
            for (BinderReqrdDocs reqdDoc : reqdDocs) {
                RiskDocs riskDoc = new RiskDocs();
                riskDoc.setReqdDocs(reqdDoc.getRequiredDocs());
                riskDoc.setMember(savedMember);
                riskDocs.add(riskDoc);
            }
            riskDocsRepo.save(riskDocs);
            Iterable<MedCategoryBenefits> benefits = benefitRepo.findAll(QMedCategoryBenefits.medCategoryBenefits.category.id.eq(savedMember.getCategory().getId()));
            List<CategoryMemberBenefits> memberBenefits = new ArrayList<>();
            for (MedCategoryBenefits benefit : benefits) {
                CategoryMemberBenefits memBenefit = new CategoryMemberBenefits();
                memBenefit.setComputedPremium(BigDecimal.ZERO);
                memBenefit.setPremium(BigDecimal.ZERO);
                memBenefit.setPrevPremium(BigDecimal.ZERO);
                memBenefit.setBenefit(benefitRepo.findOne(benefit.getSectId()));
                memBenefit.setMember(savedMember);
                memBenefit.setWefDate(savedMember.getWefDate());
                memBenefit.setWetDate(savedMember.getWetDate());
                memberBenefits.add(memBenefit);
            }
            memberBenefitsRepo.save(memberBenefits);
      }
        try {
            computePrem.computePrem(savedMember.getCategory().getPolicy().getPolicyId());
        }
        catch (IOException ex){
        	ex.printStackTrace();
            throw new BadRequestException(ex.getMessage());
        }
        rulesService.validateMinimumAge(member.getCategory().getId());
        rulesService.validateMaximumAge(member.getCategory().getId());
        rulesService.validateNoDependents(member.getCategory().getId());
    }


    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<CategoryRules> getCategoryRules(DataTablesRequest request, Long catId) throws IllegalAccessException {
        BooleanExpression pred = QCategoryRules.categoryRules.category.id.eq(catId);
        Page<CategoryRules> page = categoryRulesRepo.findAll(pred.and(request.searchPredicate(QCategoryRules.categoryRules)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public void createMemberRequiredDocs(RequiredDocBean requiredDocBean) {
        List<RiskDocs> riskDocs =
                requiredDocBean.getRequiredDocs().stream().map(reqId -> {
                    RiskDocs riskDoc = new RiskDocs();
                    riskDoc.setReqdDocs(subclassReqDocRepo.findOne(reqId));
                    riskDoc.setMember(membersRepo.findOne(requiredDocBean.getSubCode()));
                    return riskDoc;
                }).collect(Collectors.toList());
        riskDocsRepo.save(riskDocs);
    }
    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<MedicalCategory> getCategoryBedTypes(DataTablesRequest request, Long catId) throws IllegalAccessException {
        BooleanExpression pred = QMedicalCategory.medicalCategory.id.eq(catId);
        Page<MedicalCategory> page = medicalCategoryRepo.findAll(pred, request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void defineCategoryRules(CategoryRules categoryRule) throws BadRequestException {
        if(categoryRule.getSectId()==null) throw new BadRequestException("Provide Rule to Update");
        CategoryRules rule = categoryRulesRepo.findOne(categoryRule.getSectId());
        categoryRule.setCategory(rule.getCategory());
        categoryRule.setShtDesc(rule.getShtDesc());
        categoryRule.setDesc(rule.getDesc());
        categoryRule.setBinderRules(rule.getBinderRules());
        categoryRulesRepo.save(categoryRule);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteCategoryRules(Long ruleId) {
        categoryRulesRepo.delete(ruleId);
    }

    @Override
    @Transactional(readOnly = false)
    public void deletePolTaxes(Long polTaxId) throws BadRequestException {
        PolicyTaxes tax = polTaxesRepo.findOne(polTaxId);
        polTaxesRepo.delete(polTaxId);
        try {
            Long policyId = tax.getPolicy().getPolicyId();
            computePrem.computePrem(policyId);
        }
        catch (IOException ex){
            throw new BadRequestException(ex.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void savePolTaxes(PolicyTaxes taxes) throws BadRequestException {

        PolicyTrans policyTrans = taxes.getPolicy();
        PolicyTaxes existing = polTaxesRepo.findOne(QPolicyTaxes.policyTaxes.polTaxId.eq(taxes.getPolTaxId()));
        taxes.setRateType(existing.getRateType());
        taxes.setRevenueItems(existing.getRevenueItems());
        taxes.setSubclass(existing.getSubclass());
        taxes.setTaxLevel(existing.getTaxLevel());
        polTaxesRepo.save(taxes);
        try {
            Long policyId = policyTrans.getPolicyId();
            computePrem.computePrem(policyId);
        }
        catch (IOException ex){
            throw new BadRequestException(ex.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void deletePolCategory(Long categoryId) throws BadRequestException {
        MedicalCategory category = medicalCategoryRepo.findOne(categoryId);
        Iterable<CategoryMembers> categoryMembers = membersRepo.findAll(QCategoryMembers.categoryMembers.category.id.eq(categoryId));

        if (category.getCategoryStatus().equalsIgnoreCase("N")) {
            Iterable<CategoryRules> categoryRules = categoryRulesRepo.findAll(QCategoryRules.categoryRules.category.id.eq(categoryId));
            categoryRulesRepo.delete(categoryRules);
            for (CategoryMembers members : categoryMembers) {
                riskDocsRepo.delete(riskDocsRepo.findAll(QRiskDocs.riskDocs.member.sectId.eq(members.getSectId())));
            }
            Iterable<CategoryMemberBenefits> memberBenefitses = memberBenefitsRepo.findAll(QCategoryMemberBenefits.categoryMemberBenefits.member.category.id.eq(categoryId));
            memberBenefitsRepo.delete(memberBenefitses);
            membersRepo.delete(categoryMembers);

            Iterable<MedCategoryBenefits> categoryBenefits = benefitRepo.findAll(QMedCategoryBenefits.medCategoryBenefits.category.id.eq(categoryId));
            benefitRepo.delete(categoryBenefits);
            Iterable<CategoryExclusions> categoryExclusions = exclusionsRepo.findAll(QCategoryExclusions.categoryExclusions.category.id.eq(categoryId));
            exclusionsRepo.delete(categoryExclusions);

            Iterable<CategoryLoadings> categoryLoadings = loadingRepo.findAll(QCategoryLoadings.categoryLoadings.category.id.eq(categoryId));
            loadingRepo.delete(categoryLoadings);
            Iterable<CatalogueProviders> catalogueProviders = providersRepo.findAll(QCatalogueProviders.catalogueProviders.category.id.eq(categoryId));
            providersRepo.delete(catalogueProviders);
            Iterable<CategoryClauses> categoryClauses = clausesRepo.findAll(QCategoryClauses.categoryClauses.category.id.eq(categoryId));
            clausesRepo.delete(categoryClauses);
            medicalCategoryRepo.delete(categoryId);
        } else {
            for (CategoryMembers members : categoryMembers) {
                if (members.getMemberStatus().equalsIgnoreCase("N")){
                    Iterable<CategoryMemberBenefits> memberBenefitses = memberBenefitsRepo.findAll(QCategoryMemberBenefits.categoryMemberBenefits.member.category.id.eq(categoryId));
                    memberBenefitsRepo.delete(memberBenefitses);
                    membersRepo.delete(members);
                }else {
                    members.setMemberStatus("D");
                    membersRepo.save(members);
                }
            }
            Iterable<MedCategoryBenefits> categoryBenefits = benefitRepo.findAll(QMedCategoryBenefits.medCategoryBenefits.category.id.eq(categoryId));
            for (MedCategoryBenefits benefits : categoryBenefits) {
                benefits.setStatus("D");
                benefitRepo.save(benefits);
            }

            category.setCategoryStatus("D");
            medicalCategoryRepo.save(category);
        }

        try {
            Long policyId = category.getPolicy().getPolicyId();
            computePrem.computePrem(policyId);
        }
        catch (IOException ex){
            throw new BadRequestException(ex.getMessage());
        }


    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientDef> findCategoryPrincipals(String paramString, Pageable paramPageable, Long catId) {
        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred = QCategoryMembers.categoryMembers.category.id.eq(catId).and(QCategoryMembers.categoryMembers.dependentTypes.eq("P"));
        }
        else{
            pred = QCategoryMembers.categoryMembers.category.id.eq(catId).and(QCategoryMembers.categoryMembers.dependentTypes.eq("P"))
                    .and(QCategoryMembers.categoryMembers.client.fname.containsIgnoreCase(paramString).
                            or(QCategoryMembers.categoryMembers.client.otherNames.containsIgnoreCase(paramString))
                            .or(QCategoryMembers.categoryMembers.client.tenantNumber.containsIgnoreCase(paramString)));
        }
        Function<CategoryMembers,ClientDef> membersClientDefFunction = new Function<CategoryMembers, ClientDef>() {
            @Override
            public ClientDef apply(CategoryMembers members) {
                ClientDef client = new ClientDef();
                client.setTenId(members.getClient().getTenId());
                client.setTenantNumber(members.getClient().getTenantNumber());
                client.setFname(members.getClient().getFname());
                client.setOtherNames(members.getClient().getOtherNames());
                return client;
            }
        };
        List<ClientDef> clients =  membersRepo.findAll(pred,paramPageable).getContent().stream().map(membersClientDefFunction).collect(Collectors.<ClientDef>toList());
        Page<ClientDef> clientPage = new PageImpl<ClientDef>(clients);
        return clientPage;
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<CategoryClauses> getCategoryClauses(DataTablesRequest request, Long catId) throws IllegalAccessException {
        BooleanExpression pred = QCategoryClauses.categoryClauses.category.id.eq(catId);
        Page<CategoryClauses> page = clausesRepo.findAll(pred.and(request.searchPredicate(QCategoryClauses.categoryClauses)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void populateCategoryClauses(PolicyTrans policy) throws BadRequestException {
        Set<CategoryClauses> policyClauses = new HashSet<>();
        Iterable<MedicalCategory> risks = medicalCategoryRepo.findAll(QMedicalCategory.medicalCategory.policy.policyId.eq(policy.getPolicyId()));
        for(MedicalCategory risk:risks){
            Iterable<CategoryClauses> polClauses = clausesRepo.findAll(QCategoryClauses.categoryClauses.category.id.eq(risk.getId()));
            polClauses.forEach(policyClauses::add);
            Iterable<BinderClauses> binderClauses = binderClauseRepo.findAll(QBinderClauses.binderClauses.binderDet.detId.eq(risk.getBinderDetails().getDetId()).and(QBinderClauses.binderClauses.mandatory.eq("Y")));
            for(BinderClauses clause:binderClauses){
                CategoryClauses polClause = new CategoryClauses();
                polClause.setClauHeading(clause.getClause().getClause().getClauHeading());
                polClause.setClause(clause.getClause());
                polClause.setClauWording(clause.getClause().getClause().getClauWording());
                polClause.setEditable(clause.getClause().getClause().isEditable());
                polClause.setNewClause("Y");
                polClause.setCategory(risk);
                policyClauses.add(polClause);
            }
//            Iterable<SubclassClauses> subClauses = subclauseRepo.findAll(QSubclassClauses.subclassClauses.subclass.subId.eq(risk.getBinderDetails().getSubCoverTypes().getSubclass().getSubId()).and(QSubclassClauses.subclassClauses.mandatory.eq(true)));
//            for(SubclassClauses clause:subClauses){
//                CategoryClauses polClause = new CategoryClauses();
//                polClause.setClauHeading(clause.getClause().getClauHeading());
//                polClause.setClause(clause);
//                polClause.setClauWording(clause.getClause().getClauWording());
//                polClause.setEditable(clause.getClause().isEditable());
//                polClause.setNewClause("Y");
//                polClause.setCategory(risk);
//                policyClauses.add(polClause);
//            }
        }

        clausesRepo.save(policyClauses);
    }

    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void populateCategoryTaxes(PolicyTrans policy) throws BadRequestException {
        Set<PolicyTaxes> policyTaxes = new HashSet<>();
        if(policy==null) throw new BadRequestException("Policy Cannot be null");
        Iterable<PolicyTaxes> polTaxes = polTaxesRepo.findAll(QPolicyTaxes.policyTaxes.policy.policyId.eq(policy.getPolicyId()));
        polTaxes.forEach(policyTaxes::add);
        Iterable<MedicalCategory> risks =medicalCategoryRepo.findAll(QMedicalCategory.medicalCategory.policy.policyId.eq(policy.getPolicyId()));
        for(MedicalCategory risk:risks){
            Iterable<TaxRates> taxRates = taxRatesRepo.findAll((QTaxRates.taxRates.active.eq(true).and(QTaxRates.taxRates.mandatory.eq(Boolean.TRUE)))
//                     .and(QTaxRates.taxRates.revenueItems.item.ne(RevenueItems.RE))
                     .and(QTaxRates.taxRates.subclass.eq(risk.getBinderDetails().getSubCoverTypes().getSubclass()))
                     .and(QTaxRates.taxRates.productsDef.proCode.eq(risk.getPolicy().getProduct().getProCode())));
            for(TaxRates tax:taxRates){
                if("B".equalsIgnoreCase(policy.getBinder().getBinType()) || ("EN".equalsIgnoreCase(policy.getTransType()))|| ("RN".equalsIgnoreCase(policy.getTransType())) || ("EX".equalsIgnoreCase(policy.getTransType()))){
                    if(tax.getRevenueItems().getItem() == RevenueItems.SD)
                        continue;
                }
                PolicyTaxes policyTax = new PolicyTaxes();
                policyTax.setPolicy(policy);
                policyTax.setRateType(tax.getRateType());
                policyTax.setRevenueItems(tax.getRevenueItems());
                policyTax.setSubclass(tax.getSubclass());
                policyTax.setTaxLevel(tax.getTaxLevel());
                policyTax.setTaxRate(tax.getTaxRate());
                policyTax.setDivFactor(tax.getDivFactor());
                policyTax.setTaxAmount(premComputeService.calculateTax(policy.getPremium(), tax.getTaxRate(), tax.getDivFactor(),tax.getRateType()));
                policyTaxes.add(policyTax);
            }
        }

        polTaxesRepo.save(policyTaxes);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<CategoryLoadings> getCategoryLoadings(DataTablesRequest request, Long catId) throws IllegalAccessException {
        BooleanExpression pred = QCategoryLoadings.categoryLoadings.category.id.eq(catId);
        Page<CategoryLoadings> page = loadingRepo.findAll(pred.and(request.searchPredicate(QCategoryLoadings.categoryLoadings)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<CategoryExclusions> getCategoryExclusions(DataTablesRequest request, Long catId) throws IllegalAccessException {
        BooleanExpression pred = QCategoryExclusions.categoryExclusions.category.id.eq(catId);
        Page<CategoryExclusions> page = exclusionsRepo.findAll(pred.and(request.searchPredicate(QCategoryExclusions.categoryExclusions)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void deleteMember(Long memberId) throws BadRequestException {
        CategoryMembers member = membersRepo.findOne(memberId);
        Long categoryId = member.getCategory().getId();
        PolicyTrans policy = member.getCategory().getPolicy();
        if (policy.getTransType().equalsIgnoreCase("AD")){
            if("P".equalsIgnoreCase(member.getDependentTypes())){
                Long count = membersRepo.count(QCategoryMembers.categoryMembers.mainClient.tenId.eq(member.getClient().getTenId())
                        .and(QCategoryMembers.categoryMembers.category.id.eq(categoryId)));
                if(count > 0) {
                    Iterable<CategoryMembers> members = membersRepo.findAll(QCategoryMembers.categoryMembers.mainClient.tenId.eq(member.getClient().getTenId())
                            .and(QCategoryMembers.categoryMembers.category.id.eq(categoryId)));
                    for(CategoryMembers mem:members){
                        Iterable<CategoryMemberBenefits> memberBenefits=memberBenefitsRepo.findAll(QCategoryMemberBenefits.categoryMemberBenefits.member.sectId.eq(mem.getSectId()));
                        Iterable<RiskDocs> riskDocs =riskDocsRepo.findAll(QRiskDocs.riskDocs.member.sectId.eq(mem.getSectId()));
                        if (mem.getMemberStatus().equalsIgnoreCase("N")){
                            memberBenefitsRepo.delete(memberBenefits);
                            riskDocsRepo.delete(riskDocs);
                            membersRepo.delete(mem.getSectId());
                        }else {
                            for (CategoryMemberBenefits memBen: memberBenefits)
                            {
                                memBen.setWefDate(policy.getWefDate());
                                memberBenefitsRepo.save(memBen);
                            }
                            mem.setMemberStatus("D");
                            mem.setWefDate(policy.getWefDate());
                            membersRepo.save(mem);
                        }
                    }
                }
            }else{
                Iterable<CategoryMemberBenefits> memberBenefits=memberBenefitsRepo.findAll(QCategoryMemberBenefits.categoryMemberBenefits.member.sectId.eq(member.getSectId()));
                if (member.getMemberStatus().equalsIgnoreCase("N")){

                    Iterable<RiskDocs> riskDocs =riskDocsRepo.findAll(QRiskDocs.riskDocs.member.sectId.eq(member.getSectId()));
                    memberBenefitsRepo.delete(memberBenefits);
                    riskDocsRepo.delete(riskDocs);
                    membersRepo.delete(member.getSectId());
                }else {
                    for (CategoryMemberBenefits memBen: memberBenefits)
                    {
                        memBen.setWefDate(policy.getWefDate());
                        memberBenefitsRepo.save(memBen);
                    }
                    member.setMemberStatus("D");
                    member.setWefDate(policy.getWefDate());
                    membersRepo.save(member);
                }
            }

        } else{
            if("P".equalsIgnoreCase(member.getDependentTypes())){
                Long count = membersRepo.count(QCategoryMembers.categoryMembers.mainClient.tenId.eq(member.getClient().getTenId())
                        .and(QCategoryMembers.categoryMembers.category.id.eq(categoryId)));
                if(count > 0) {
                    Iterable<CategoryMembers> members = membersRepo.findAll(QCategoryMembers.categoryMembers.mainClient.tenId.eq(member.getClient().getTenId())
                            .and(QCategoryMembers.categoryMembers.category.id.eq(categoryId)));
                    for(CategoryMembers mem:members) {
                        Iterable<CategoryMemberBenefits> memberBenefits = memberBenefitsRepo.findAll(QCategoryMemberBenefits.categoryMemberBenefits.member.sectId.eq(mem.getSectId()));
                        Iterable<RiskDocs> riskDocs = riskDocsRepo.findAll(QRiskDocs.riskDocs.member.sectId.eq(mem.getSectId()));
                        memberBenefitsRepo.delete(memberBenefits);
                        riskDocsRepo.delete(riskDocs);
                    }


                    membersRepo.delete(members);
                }
            }else{
                Iterable<CategoryMemberBenefits> memberBenefits=memberBenefitsRepo.findAll(QCategoryMemberBenefits.categoryMemberBenefits.member.sectId.eq(member.getSectId()));
                Iterable<RiskDocs> riskDocs =riskDocsRepo.findAll(QRiskDocs.riskDocs.member.sectId.eq(member.getSectId()));
                memberBenefitsRepo.delete(memberBenefits);
                riskDocsRepo.delete(riskDocs);
                membersRepo.delete(memberId);
            }
        }
        try {
            Long policyId = member.getCategory().getPolicy().getPolicyId();
            computePrem.computePrem(policyId);
        }
        catch (IOException ex){
            throw new BadRequestException(ex.getMessage());
        }
    }



    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void undoDeleteMember(Long memberId) throws BadRequestException {
        CategoryMembers member = membersRepo.findOne(memberId);
        Long categoryId = member.getCategory().getId();
        PolicyTrans policy = member.getCategory().getPolicy();
        if (policy.getTransType().equalsIgnoreCase("AD")){
            if("P".equalsIgnoreCase(member.getDependentTypes())){
                Long count = membersRepo.count(QCategoryMembers.categoryMembers.mainClient.tenId.eq(member.getClient().getTenId())
                        .and(QCategoryMembers.categoryMembers.category.id.eq(categoryId)));
                if(count > 0) {
                    Iterable<CategoryMembers> members = membersRepo.findAll(QCategoryMembers.categoryMembers.mainClient.tenId.eq(member.getClient().getTenId())
                            .and(QCategoryMembers.categoryMembers.category.id.eq(categoryId)));
                    for(CategoryMembers mem:members){
                        Iterable<CategoryMemberBenefits> memberBenefits=memberBenefitsRepo.findAll(QCategoryMemberBenefits.categoryMemberBenefits.member.sectId.eq(mem.getSectId()));
                        Iterable<RiskDocs> riskDocs =riskDocsRepo.findAll(QRiskDocs.riskDocs.member.sectId.eq(mem.getSectId()));
                        for (CategoryMemberBenefits memBen: memberBenefits)
                        {
                            memBen.setWefDate(memBen.getPrevwefDate());
                            memberBenefitsRepo.save(memBen);
                        }
                            mem.setMemberStatus("E");
                            mem.setWefDate(mem.getPrevwefDate());
                            membersRepo.save(mem);
                    }
                }
            }else{
                Iterable<CategoryMemberBenefits> memberBenefits=memberBenefitsRepo.findAll(QCategoryMemberBenefits.categoryMemberBenefits.member.sectId.eq(member.getSectId()));
                for (CategoryMemberBenefits memBen: memberBenefits)
                {
                    memBen.setWefDate(memBen.getPrevwefDate());
                    memberBenefitsRepo.save(memBen);
                }
                    member.setMemberStatus("E");
                    membersRepo.save(member);
            }

        }
        try {
            Long policyId = member.getCategory().getPolicy().getPolicyId();
            computePrem.computePrem(policyId);
        }
        catch (IOException ex){
            throw new BadRequestException(ex.getMessage());
        }
    }


    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void populateCategoryLoadings(PolicyTrans policy) throws BadRequestException {

        Set<CategoryLoadings> categoryLoadings = new HashSet<>();
        Iterable<MedicalCategory> risks = medicalCategoryRepo.findAll(QMedicalCategory.medicalCategory.policy.policyId.eq(policy.getPolicyId()));
        Iterable<BinderLoadings> loadings = loadingsRepo.findAll(QBinderLoadings.binderLoadings.binder.binId.eq(policy.getBinder().getBinId()));
        for(MedicalCategory risk:risks){
            Iterable<CategoryLoadings> catLoadings = loadingRepo.findAll(QCategoryLoadings.categoryLoadings.category.id.eq(risk.getId()));
            catLoadings.forEach(categoryLoadings::add);
            for(BinderLoadings loading:loadings){
                CategoryLoadings load = new CategoryLoadings();
                load.setAilment(loading.getAilment());
                load.setCategory(risk);
                load.setLoadingAmt(loading.getLoadingAmt());
                load.setRate(loading.getRate());
                load.setRateType(loading.getRateType());
                if(loading.getAilmentDesc()==null){
                    load.setAilmentDesc(loading.getAilment().getBaDesc());
                }
                else{
                    load.setAilmentDesc(loading.getAilmentDesc());
                }
                if(loading.getChronic()==null){
                    load.setChronic((loading.getAilment().isChronic()?"Y":"N"));
                }
                else{
                    load.setChronic(loading.getChronic());
                }
                if(loading.getCostPerClaim()==null){
                    load.setCostPerClaim(loading.getAilment().getCostPerClaim());
                }
                else{
                    load.setCostPerClaim(loading.getCostPerClaim());
                }
                if(loading.getUpperLimit()==null){
                    load.setUpperLimit(loading.getAilment().getUpperLimit());
                }
                else{
                    load.setUpperLimit(loading.getUpperLimit());
                }

                if(loading.getWaitingDays()==null){
                    load.setWaitingDays(loading.getAilment().getWaitingDays());
                }
                else{
                    load.setWaitingDays(loading.getWaitingDays());
                }
                categoryLoadings.add(load);
            }
        }
        loadingRepo.save(categoryLoadings);

    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void populateCategoryExclusions(PolicyTrans policy) throws BadRequestException {

        Set<CategoryExclusions> exclusions = new HashSet<>();
        Iterable<MedicalCategory> risks = medicalCategoryRepo.findAll(QMedicalCategory.medicalCategory.policy.policyId.eq(policy.getPolicyId()));
        Iterable<BinderExclusions> excludes = exclusionRepo.findAll(QBinderExclusions.binderExclusions.binder.binId.eq(policy.getBinder().getBinId()).and(QBinderExclusions.binderExclusions.ailment.isNotNull()));
        for(MedicalCategory risk:risks){
            Iterable<CategoryExclusions> catExclusions = exclusionsRepo.findAll(QCategoryExclusions.categoryExclusions.category.id.eq(risk.getId()));
            catExclusions.forEach(exclusions::add);
            for(BinderExclusions exclusion:excludes){
                CategoryExclusions exclude = new CategoryExclusions();
                exclude.setAilment(exclusion.getAilment());
                exclude.setCategory(risk);
                exclusions.add(exclude);
            }
        }
        exclusionsRepo.save(exclusions);

    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void populateCategoryProviders(PolicyTrans policy) throws BadRequestException {
        Set<CatalogueProviders> providers = new HashSet<>();
        Iterable<MedicalCategory> risks = medicalCategoryRepo.findAll(QMedicalCategory.medicalCategory.policy.policyId.eq(policy.getPolicyId()));
        Iterable<BinderProviders> binderProviders = binderProvidersRepo.findAll(QBinderProviders.binderProviders.binder.binId.eq(policy.getBinder().getBinId()));
        for(MedicalCategory risk:risks){
            Iterable< CatalogueProviders> catProvider = providersRepo.findAll(QCatalogueProviders.catalogueProviders.category.id.eq(risk.getId()));
            catProvider.forEach(providers::add);
            for(BinderProviders provider:binderProviders){
                CatalogueProviders catalogueProvider = new CatalogueProviders();
                catalogueProvider.setProviders(provider.getProvider());
                catalogueProvider.setCategory(risk);
                providers.add(catalogueProvider);
            }
        }
        providersRepo.save(providers);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void saveCategoryLoadings(CategoryLoadings loadings) throws BadRequestException {
        if(loadings.getRateType() ==null) throw new BadRequestException("Rate Type cannot be null");
        if(loadings.getRate() == null) throw new BadRequestException("Rate Cannot be null");
        if(loadings.getClId()==null) throw new BadRequestException("Select Loading to update");
        CategoryLoadings savedLoading = loadingRepo.findOne(loadings.getClId());
        loadings.setCategory(savedLoading.getCategory());
        loadings.setAilment(savedLoading.getAilment());
        loadingRepo.save(loadings);
        try {
            Long policyId = savedLoading.getCategory().getPolicy().getPolicyId();
            computePrem.computePrem(policyId);
        }
        catch (IOException ex){
            throw new BadRequestException(ex.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void savePolicyDetails(PolicyCreateDTO createDTO) throws BadRequestException {
        final PolicyTrans policy = new PolicyTrans();
        org.springframework.beans.BeanUtils.copyProperties(createDTO,policy);
       /* if(policy.getBinder()!=null && "S".equalsIgnoreCase(policy.getBinder().getBinType())){
            if(binderRepo.count(QBindersDef.bindersDef.fundBinder.equalsIgnoreCase("Y")) !=1){
                throw new BadRequestException("A Fund Binder has not been set up ..Check your configuration");
            }
            BindersDef fundBinder = binderRepo.findOne(QBindersDef.bindersDef.fundBinder.equalsIgnoreCase("Y"));
            policy.setBindCode(fundBinder.getBinId());
            policy.setProdId(fundBinder.getProduct().getProCode());
            policy.setAgentId(fundBinder.getAccount().getAcctId());
        }*/
        if (createDTO.getPolicyId() != null) {
            PolicyTrans existingPolicy = policyRepo.findOne(createDTO.getPolicyId());
            if (existingPolicy.getAgent()==null)
            {
                createDTO.setAgentId(existingPolicy.getBinder().getAccount().getAcctId());
            }else {
                createDTO.setAgentId(existingPolicy.getAgent().getAcctId());
            }

            createDTO.setProdId(existingPolicy.getProduct().getProCode());
        }
        if(createDTO.getClientId()==null)throw new BadRequestException("Client is Mandatory");
        if(createDTO.getBindCode()==null)throw new BadRequestException("Binder is Mandatory ");
        if(createDTO.getAgentId()==null)throw new BadRequestException("Intermediary is Mandatory");
        if(createDTO.getProdId()==null)throw new BadRequestException("Product is Mandatory");
        if(createDTO.getPaymentId()==null)throw new BadRequestException("Payment Mode is Mandatory");
        if(createDTO.getBranchId()==null)throw new BadRequestException("Branch is Mandatory");
        if(createDTO.getCurrencyId()==null)throw new BadRequestException("Currency is Mandatory");
        if (createDTO.getWefDate() == null) throw new BadRequestException("Policy Wef Date From is Mandatory");
        if(createDTO.getWetDate()==null)throw new BadRequestException("Policy Wet Date From is Mandatory");
        if(createDTO.getWefDate().after(createDTO.getWetDate())) throw new BadRequestException("Wef Date cannot be greater than Wet Date");
        Date polWetDate = dateUtils.getWetDate(createDTO.getWefDate());
        if(createDTO.getBusinessType()==null) throw new BadRequestException("Select Business Type");

        Predicate cardPred = QBinderMedicalCards.binderMedicalCards.binder.binId.eq(createDTO.getBindCode());
        Long cardCount = binderMedicalCardsRepo.count(cardPred);
        if (cardCount != 0 && createDTO.getCardId()==null) {
            throw new BadRequestException("Card type is Mandatory");
        } else {
            if (cardCount == 0 ) policy.setBinCardType(null);
        }


        if("S".equalsIgnoreCase(createDTO.getBusinessType()))
            if(createDTO.getWetDate().after(polWetDate)) throw new BadRequestException("The Short Period Policy Cover Period Cannot be more than a year");
        ProductsDef policyProduct = productRepo.findOne(createDTO.getProdId());
        if(createDTO.getPolicyId()!=null){
            PolicyTrans editPolicy = policyRepo.findOne(createDTO.getPolicyId());
            if (editPolicy == null)
                throw new BadRequestException("The Policy does not exist. Cannot Authorize");
            if ("A".equalsIgnoreCase(editPolicy.getAuthStatus())) {
                throw new BadRequestException("Cannot Save..Policy already authorized");
            }
            policy.setPolicyId(createDTO.getPolicyId());
            policy.setPolCreateddt(editPolicy.getPolCreateddt());

            policy.setPolRevNo(editPolicy.getPolRevNo());
            policy.setAuthStatus(editPolicy.getAuthStatus());
            policy.setCommAllowed(editPolicy.isCommAllowed());
            policy.setPolRevStatus(editPolicy.getPolRevStatus());
            policy.setRevisionFormat(editPolicy.getRevisionFormat());
            policy.setCreatedUser(editPolicy.getCreatedUser());
            policy.setBeneficiary(editPolicy.getBeneficiary());
            policy.setTrainingLevy(editPolicy.getTrainingLevy());
            policy.setTotTrainingLevy(editPolicy.getTotTrainingLevy());
            policy.setPhcf(editPolicy.getPhcf());
            policy.setTotPhcf(editPolicy.getTotPhcf());
            policy.setEndosbasicPremium(editPolicy.getEndosbasicPremium());
            policy.setEndosCommissions(editPolicy.getEndosCommissions());
            policy.setWhtx(editPolicy.getWhtx());
            policy.setTotTrainingLevy(editPolicy.getTotTrainingLevy());
            policy.setTrainingLevy(editPolicy.getTrainingLevy());
            policy.setStampDuty(editPolicy.getStampDuty());
            policy.setPremium(editPolicy.getPremium());
            policy.setNetPrem(editPolicy.getNetPrem());
            policy.setExtras(editPolicy.getExtras());
            policy.setBasicPrem(editPolicy.getBasicPrem());
            policy.setTransType(editPolicy.getTransType());
            policy.setSumInsured(editPolicy.getSumInsured());
            policy.setPolicyTaxes(editPolicy.getPolicyTaxes());
            policy.setFuturePrem(editPolicy.getFuturePrem());
            policy.setCurrentStatus(editPolicy.getCurrentStatus());
            policy.setOldpolNo(editPolicy.getOldpolNo());
            policy.setPolNo(editPolicy.getPolNo());
            policy.setPreviousTrans(editPolicy.getPreviousTrans());
            BindersDef binder = editPolicy.getBinder();
            BindersDef newBinder = binderRepo.findOne(createDTO.getBindCode());
            if(binder.getBinId()!=newBinder.getBinId()){
                Iterable<RiskTrans> binderRisks = riskRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(policy.getPolicyId()));
                List<RiskTrans> newRisks = new ArrayList<>();
                for(RiskTrans risk:binderRisks){
                    risk.setBinder(newBinder);
                    newRisks.add(risk);
                    Iterable<SectionTrans> sections = sectionRepo.findAll(QSectionTrans.sectionTrans.risk.riskId.eq(risk.getRiskId()));
                    sectionRepo.delete(sections);
                }
                riskRepo.save(newRisks);
            }


        }
        policy.setAgent(accountRepo.findOne(createDTO.getAgentId()));
        policy.setBinder(binderRepo.findOne(createDTO.getBindCode()));
        policy.setBranch(branchRepo.findOne(createDTO.getBranchId()));
        policy.setClient(clientRepo.findOne(createDTO.getClientId()));
        policy.setPaymentMode(paymentModeRepo.findOne(createDTO.getPaymentId()));
        if (!(createDTO.getCardId()==null)) {
            policy.setBinCardType(binderMedicalCardsRepo.findOne(createDTO.getCardId()));
        }
        policy.setProduct(policyProduct);
        policy.setTransCurrency(currencyRepo.findOne(createDTO.getCurrencyId()));

        if("N".equalsIgnoreCase(policy.getBusinessType()))
            policy.setRenewable(policyProduct.isRenewable());
        else
            policy.setRenewable(false);
        policy.setUwYear(dateUtils.getUwYear(policy.getWefDate()));
        if(policyProduct.isRenewable() && "N".equalsIgnoreCase(policy.getBusinessType())){
            policy.setRenewalDate(DateUtils.addDays(policy.getWetDate(), 1));
        }
        else
            policy.setRenewalDate(null);
        policy.setCoverFrom(policy.getWefDate());
        policy.setCoverTo(policy.getWetDate());
        PolicyTrans savedTrans = policyRepo.save(policy);

    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<CatalogueProviders> getCategoryProviders(DataTablesRequest request, Long catId) throws IllegalAccessException {
        BooleanExpression pred = QCatalogueProviders.catalogueProviders.category.id.eq(catId);
        Page<CatalogueProviders> page = providersRepo.findAll(pred.and(request.searchPredicate(QCatalogueProviders.catalogueProviders)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CoverLimits> findCoverLimits(String paramString, Pageable paramPageable, Long covId) throws BadRequestException {
        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred = QCoverLimits.coverLimits.covers.id.eq(covId).and(QCoverLimits.coverLimits.isNotNull());
        } else {
            BigDecimal amount = BigDecimal.ZERO;
            try {
                Double val = Double.parseDouble(paramString);
                amount = BigDecimal.valueOf(val);
            }
            catch (NumberFormatException ex){
                throw new BadRequestException(ex.getMessage());
            }
            pred = QCoverLimits.coverLimits.covers.id.eq(covId).and(QCoverLimits.coverLimits.limitAmount.eq(amount));
        }
        return coverLimitsRepo.findAll(pred, paramPageable);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void updateCategoryBenefits(MedCategoryBenefits benefits) throws BadRequestException {
        if(benefits.getSectId()==null) throw new BadRequestException("Select Benefit to update limit");
        if(benefits.getLimit()==null) throw new BadRequestException("Select Benefit to continue...");
        if(benefits.getApplicableAt()==null) throw new  BadRequestException("Selection benefit application at to continue..");
        MedCategoryBenefits benefit = benefitRepo.findOne(benefits.getSectId());
        if (benefit.getStatus().equalsIgnoreCase("D")){
            throw new  BadRequestException("Cannot change benefit for cover marked for removal..");
        }
        if (benefit.getCategory().getPolicy().getTransType().equalsIgnoreCase("AB") && benefit.getLimit().getId() != benefits.getLimit().getId())
            benefit.setStatus("C");
        benefit.setLimit(benefits.getLimit());
        benefit.setWaitPeriod(benefits.getWaitPeriod());
        benefit.setApplicableAt(benefits.getApplicableAt());

        benefitRepo.save(benefit);
        try {
            computePrem.computePrem(benefit.getCategory().getPolicy().getPolicyId());
        }
        catch (IOException ex){
            throw new BadRequestException(ex.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void updateFundBenefits(MedCategoryBenefits benefits ) throws BadRequestException {
        if(benefits.getSectId()==null) throw new BadRequestException("Choose Benefit to update limit");
        if(benefits.getApplicableAt()==null) throw new  BadRequestException("Selection benefit application at to continue..");
        MedCategoryBenefits benefit = benefitRepo.findOne(benefits.getSectId());
        benefit.setFundLimit(benefits.getFundLimit());
        benefit.setWaitPeriod(benefits.getWaitPeriod());
        benefit.setApplicableAt(benefits.getApplicableAt());
        benefitRepo.save(benefit);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceProviderContracts> findProviderContractsForSelect(String term, Pageable pageable) {
        Predicate pred = null;
        if (term == null || StringUtils.isBlank(term)) {
            pred = QServiceProviderContracts.serviceProviderContracts.isNotNull();
        } else {
            pred = QServiceProviderContracts.serviceProviderContracts.serviceProviders.name.containsIgnoreCase(term);
        }
        return providerContractRepo.findAll(pred, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<SelfFundParams> findSelfFundParams(DataTablesRequest request, Long polCode) throws IllegalAccessException {
        BooleanExpression pred = QSelfFundParams.selfFundParams.policyTrans.policyId.eq(polCode);
        Page<SelfFundParams> page = selfFundParamsRepo.findAll(pred.and(request.searchPredicate(QSelfFundParams.selfFundParams)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void defineSelfFundParams(SelfFundParams fundParams) throws BadRequestException {
        long count = selfFundParamsRepo.count(QSelfFundParams.selfFundParams.policyTrans.policyId.eq(fundParams.getPolicyTrans().getPolicyId()));
        if(fundParams.getSfpId()==null)
            if(count > 0)
                throw new BadRequestException("A Fund Transaction already exist");
            else
            if(count > 1)
                throw new BadRequestException("A Fund Transaction already exist");

        if(fundParams.getFundResetAmount().compareTo(fundParams.getMinDeposit())==-1){
            throw new BadRequestException("Reset Amount cannot be less than minimum deposit amount....");
        }

        if(fundParams.getFundDepositAmount().compareTo(fundParams.getFundResetAmount())==-1){
            throw new BadRequestException("Fund Deposit Amount cannot be less than Reset amount....");
        }

        selfFundParamsRepo.save(fundParams);
        try{
            computePrem.computePrem(fundParams.getPolicyTrans().getPolicyId());
        }
        catch (IOException ex){
            throw new BadRequestException(ex.getMessage());
        }

    }

    @Override
    @Transactional(readOnly = false)
    public void deleteSelfFundParams(Long fundParamId) throws BadRequestException {
        selfFundParamsRepo.delete(fundParamId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getUnassignedBinderRules(Long catId) throws IllegalAccessException {
        Long bindCode = 0l;
        if(catId==null) {
            catId = 0l;
        }
        else{
            if(medicalCategoryRepo.count(QMedicalCategory.medicalCategory.id.eq(catId))==0){
                bindCode = 0l;
            }else {
                MedicalCategory category = medicalCategoryRepo.findOne(catId);
                catId = category.getId();
                bindCode = category.getPolicy().getBinder().getBinId();
            }
        }
        return categoryRulesRepo.getBinderRules(bindCode,catId);
    }

    @Override
    @Transactional(readOnly = false)
    public void createBinderRules(RulesBean rulesBean) {
        List<CategoryRules> categoryRules = new ArrayList<>();
        for(Long ruleId:rulesBean.getRules()){
            MedicalBinderRules binderRule = medicalRulesRepo.findOne(ruleId);
            CategoryRules rule = new CategoryRules();
            rule.setBinderRules(binderRule);
            rule.setDesc(binderRule.getDesc());
            rule.setCategory(medicalCategoryRepo.findOne(rulesBean.getCatCode()));
            rule.setShtDesc(binderRule.getShtDesc());
            rule.setValue(binderRule.getValue());
            categoryRules.add(rule);
        }
        categoryRulesRepo.save(categoryRules);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<CategoryMembers> getSchemeMembers(DataTablesRequest request,Long polCode) throws IllegalAccessException {
        BooleanExpression pred = QCategoryMembers.categoryMembers.category.policy.policyId.eq(polCode);
        Page<CategoryMembers> page = membersRepo.findAll(pred.and(request.searchPredicate(QCategoryMembers.categoryMembers)), request);
        return new DataTablesResult<>(request, page);
    }


    @Override
    @Transactional(readOnly = true)
    public String getFamilyDetails(Long memberId) {
        CategoryMembers members = membersRepo.findOne(memberId);
        BooleanExpression pred =null;
        StringBuffer familyDetails = new StringBuffer();
        if(members.getClient()==members.getMainClient()){
            pred = QCategoryMembers.categoryMembers.sectId.eq(memberId);
        }
        else{
            Long mainClientId = members.getMainClient().getTenId();
            long sectId = membersRepo.findOne(QCategoryMembers.categoryMembers.client.tenId.eq(mainClientId)
                    .and(QCategoryMembers.categoryMembers.category.id.eq(members.getCategory().getId()))).getSectId();
            pred = QCategoryMembers.categoryMembers.sectId.eq(sectId);
        }
        Iterable<CategoryMembers> principals = membersRepo.findAll(pred);
        for(CategoryMembers principal:principals){
            familyDetails.append(principal.getClient().getFname()+" "+principal.getClient().getOtherNames());
        }
         pred = QCategoryMembers.categoryMembers.mainClient.tenId.eq(members.getMainClient().getTenId())
                .and(QCategoryMembers.categoryMembers.category.id.eq(members.getCategory().getId())
                        .and(QCategoryMembers.categoryMembers.dependentTypes.notEqualsIgnoreCase("P")));
        Iterable<CategoryMembers> dependents = membersRepo.findAll(pred);
        for(CategoryMembers dependent:dependents){
            familyDetails.append(",");
            familyDetails.append(dependent.getClient().getFname()+" "+dependent.getClient().getOtherNames());
        }

        return familyDetails.toString();
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<CategoryMembers> getPrincipalInfo(DataTablesRequest request, Long memberId) throws IllegalAccessException {
        CategoryMembers members = membersRepo.findOne(memberId);
        BooleanExpression pred =null;
        if(members.getClient()==members.getMainClient()){
            pred = QCategoryMembers.categoryMembers.sectId.eq(memberId);
        }
        else{
            Long mainClientId = members.getMainClient().getTenId();
            long sectId = membersRepo.findOne(QCategoryMembers.categoryMembers.client.tenId.eq(mainClientId)
                    .and(QCategoryMembers.categoryMembers.category.id.eq(members.getCategory().getId()))).getSectId();
            pred = QCategoryMembers.categoryMembers.sectId.eq(sectId);
        }
        Page<CategoryMembers> page = membersRepo.findAll(pred.and(request.searchPredicate(QCategoryMembers.categoryMembers)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<CategoryMembers> getDependantsInfo(DataTablesRequest request, Long memberId) throws IllegalAccessException {
        CategoryMembers members = membersRepo.findOne(memberId);
        BooleanExpression pred = QCategoryMembers.categoryMembers.mainClient.tenId.eq(members.getMainClient().getTenId())
                .and(QCategoryMembers.categoryMembers.category.id.eq(members.getCategory().getId())
                        .and(QCategoryMembers.categoryMembers.dependentTypes.notEqualsIgnoreCase("P")));
        Page<CategoryMembers> page = membersRepo.findAll(pred.and(request.searchPredicate(QCategoryMembers.categoryMembers)), request);
        return new DataTablesResult<>(request, page);
    }



    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public String uploadMembers(MemberUploadForm uploadForm,Long polCode, HttpServletRequest request) throws BadRequestException {
        try {
            Iterable<MedicalCategory> categories = medicalCategoryRepo.findAll(QMedicalCategory.medicalCategory.policy.policyId.eq(polCode));
           if (((ArrayList) categories).size()  ==0)  throw new BadRequestException("The Categories defined must be on the upload file!!");
            List<CategoryMembers> categoryMembers = new ArrayList<>();
            for(MedicalCategory category:categories){
                MemberBeanHolder memberBeanHolder = uploadUtils.uploadMembers(uploadForm.getFile(), category.getShtDesc(),request);
                System.out.println(memberBeanHolder);
                List<MemberUploadUtils.MemberBean> members = memberBeanHolder.getMemberBeans();
                if(members!=null && members.size()==0){
                    return memberBeanHolder.getExcelFile();
//                    throw new BadRequestException("No Member to upload");
                }
                List<String> keys = members.stream().map(a -> a.getKey()).distinct().collect(Collectors.toList());
                outer: for (String key : keys) {
                    List<MemberUploadUtils.MemberBean> principalMembers = members.stream().filter(a -> a.getKey().equalsIgnoreCase(key)).collect(Collectors.toList());
                    long mainClientId =-2000l;
                    CategoryMembers savedMember;
                    innerloop: for(MemberUploadUtils.MemberBean member:principalMembers){
                        if("Principal".equalsIgnoreCase(member.getType()) && member.getDob()==null) continue outer;
                        if("Principal".equalsIgnoreCase(member.getType())){
                            mainClientId = createClientFromMember(member);
                            if(mainClientId==-2000) continue innerloop;
                            CategoryMembers categoryMember = new CategoryMembers();
                            categoryMember.setMainClient(clientRepo.findOne(mainClientId));
                            categoryMember.setClient(clientRepo.findOne(mainClientId));
                            categoryMember.setCategory(category);
                            categoryMember.setDependentTypes("P");
                            categoryMember.setWefDate(category.getPolicy().getWefDate());
                            categoryMember.setWetDate(category.getPolicy().getWetDate());
                            categoryMember.setMemberStatus("N");
                            categoryMember.setMemberHasCard(member.getMemberHasCard());
                            categoryMember.setCardNo(member.getCardNo());
                            if (member.getMemberHasCard().equalsIgnoreCase("N")){
                                if (member.getAutoGenerateCardNo()==null){
                                    categoryMember.setAutoGenerateCard("N");
                                }else
                                    categoryMember.setAutoGenerateCard(member.getAutoGenerateCardNo());
                            }
                            Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("M");
                            if (sequenceRepo.count(seqPredicate) == 0)
                                throw new BadRequestException("Sequence for Medical Membership has not been defined");
                            SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
                            Long seqNumber = sequence.getNextNumber();
                            sequence.setLastNumber(seqNumber);
                            sequence.setNextNumber(seqNumber + 1);
                            sequenceRepo.save(sequence);
                            categoryMember.setMemberShipNo(String.format("%016d", seqNumber));
                            //categoryMembers.add(categoryMember);
                            savedMember =membersRepo.save(categoryMember);
                        }
                        else{
                            long clientId = createClientFromMember(member);
                            if(clientId==-2000) continue innerloop;
                            CategoryMembers categoryMember = new CategoryMembers();
                            categoryMember.setMainClient(clientRepo.findOne(mainClientId));
                            categoryMember.setClient(clientRepo.findOne(clientId));
                            categoryMember.setCategory(category);
                            categoryMember.setDependentTypes(member.getType());
                            categoryMember.setChildType(member.getSubType());
                            categoryMember.setWefDate(category.getPolicy().getWefDate());
                            categoryMember.setWetDate(category.getPolicy().getWetDate());
                            categoryMember.setMemberStatus("N");
                            categoryMember.setMemberHasCard(member.getMemberHasCard());
                            categoryMember.setCardNo(member.getCardNo());
                            Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("M");
                            if (sequenceRepo.count(seqPredicate) == 0)
                                throw new BadRequestException("Sequence for Medical Membership has not been defined");
                            SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
                            Long seqNumber = sequence.getNextNumber();
                            sequence.setLastNumber(seqNumber);
                            sequence.setNextNumber(seqNumber + 1);
                            sequenceRepo.save(sequence);
                            categoryMember.setMemberShipNo(String.format("%016d", seqNumber));
                            //categoryMembers.add(categoryMember);
                            savedMember =membersRepo.save(categoryMember);
                        }
                        Iterable<MedCategoryBenefits> benefits = benefitRepo.findAll(QMedCategoryBenefits.medCategoryBenefits.category.id.eq(savedMember.getCategory().getId()));
                        List<CategoryMemberBenefits> memBenefits = new ArrayList<>();
                        for (MedCategoryBenefits benefit:benefits){
                            CategoryMemberBenefits memBenefit = new CategoryMemberBenefits();
                            memBenefit.setPremium(BigDecimal.ZERO);
                            memBenefit.setMember(savedMember);
                            memBenefit.setBenefit(benefit);
                            memBenefit.setPrevPremium(BigDecimal.ZERO);
                            memBenefit.setComputedPremium(BigDecimal.ZERO);
                            memBenefits.add(memBenefit);
                        }
                        memberBenefitsRepo.save(memBenefits);
                    }
                }
                membersRepo.save(categoryMembers);
          //      Iterable<CategoryMembers> catMembers =membersRepo.findAll(QCategoryMembers.categoryMembers.);
                computePrem.computePrem(polCode);
            }
        }
        catch (IOException ex){
            throw new BadRequestException(ex.getMessage());
        }
        return "success";
    }


    private Long createClientFromMember(MemberUploadUtils.MemberBean memberBean) throws BadRequestException{
        long count =-2000;
        long titlecount =-2000;
        if(memberBean.getIdno()==null){
            count = 0;
        }else {
            count = clientRepo.count(QClientDef.clientDef.idNo.equalsIgnoreCase(memberBean.getIdno()));
        }

        ClientTitle clientTitle = new ClientTitle();
        if (memberBean.getClientTitle()==null){
            titlecount = 0;
        }else {
            titlecount = clientTitleRepo.count(QClientTitle.clientTitle.titleName.equalsIgnoreCase(memberBean.getClientTitle()));
            if (titlecount==0){
                clientTitle.setTitleName(memberBean.getClientTitle());
                ClientTitle savedClientTitle = clientTitleRepo.save(clientTitle);
                clientTitle = savedClientTitle;
            }else if (titlecount>0){
                clientTitle = clientTitleRepo.findOne(QClientTitle.clientTitle.titleName.equalsIgnoreCase(memberBean.getClientTitle()));
            }
    }

        if(count==1){
            return clientRepo.findOne(QClientDef.clientDef.idNo.equalsIgnoreCase(memberBean.getIdno())).getTenId();
        }
        else if(count > 1) return -2000l;
        else if(count ==0){
            ClientDef clientDef = new ClientDef();
            clientDef.setFname(memberBean.getFname());
            String  phoneNo="";
            String  smsNo="";
            if (memberBean.getPhoneNumber()==null && memberBean.getSmsNo()!=null){
                phoneNo=memberBean.getSmsNo();
            } else if (memberBean.getPhoneNumber()!=null){
                phoneNo=memberBean.getPhoneNumber();
            }
            if (memberBean.getSmsNo()==null && memberBean.getPhoneNumber()!=null){
                smsNo=memberBean.getPhoneNumber();
            } else if (memberBean.getSmsNo()!=null){
                smsNo=memberBean.getSmsNo();
            }

            if (!phoneNo.isEmpty()){
                String phonePrefix =phoneNo.substring(0,3);
                phoneNo =phoneNo.substring(3,phoneNo.length());
                clientDef.setPhoneNo(phoneNo);
                clientDef.setPhonePrefix(mobPrefixRepo.findOne(QMobilePrefixDef.mobilePrefixDef.prefixName.equalsIgnoreCase(phonePrefix)) );
            }
            if (!smsNo.isEmpty()){
                String smsPrefix =smsNo.substring(0,3);
                String smNo =smsNo.substring(3,smsNo.length());
                clientDef.setSmsNumber(smNo);
                clientDef.setSmsPrefix(mobPrefixRepo.findOne(QMobilePrefixDef.mobilePrefixDef.prefixName.equalsIgnoreCase(smsPrefix)) );
            }
            System.out.println("phoneNo="+phoneNo +";smsNo="+smsNo +";phonePrefix="+clientDef.getPhonePrefix()+";smsPrefix="+clientDef.getSmsPrefix());
            clientDef.setEmailAddress(memberBean.getEmail());
            clientDef.setOtherNames(memberBean.getOtherNames());
            clientDef.setIdNo(memberBean.getIdno());
            clientDef.setGender(memberBean.getGender());
            clientDef.setAddress(memberBean.getAddress());
            clientDef.setClientTitle(clientTitle);
            if (memberBean.getPostalCode()!=null){
                clientDef.setPostalCodesDef(postalCodeRepo.findOne(QPostalCodesDef.postalCodesDef.zipCode.eq(memberBean.getPostalCode())));
            }
            Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("C");
            if (sequenceRepo.count(seqPredicate) == 0)
                throw new BadRequestException("Sequence for Client Definition has not been setup");
            SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
            Long seqNumber = sequence.getNextNumber();
            final String clientNumber = sequence.getSeqPrefix() + String.format("%06d", seqNumber);
            sequence.setLastNumber(seqNumber);
            sequence.setNextNumber(seqNumber + 1);
            sequenceRepo.save(sequence);
            clientDef.setStatus("A");
            clientDef.setTenantType(clientTypeRepo.findOne(QClientTypes.clientTypes.typeDesc.equalsIgnoreCase("INDIVIDUAL")));
            clientDef.setTenantNumber(clientNumber);
            if(memberBean.getCountry()!=null){
                clientDef.setCountry(countryRepository.findOne(QCountry.country.couShtDesc.eq(memberBean.getCountry())));
            }
            if(memberBean.getTown()!=null){
                clientDef.setTown(townRepository.findOne(QTown.town.ctShtDesc.eq(memberBean.getTown())));
            }
            clientDef.setDob(memberBean.getDob());
            clientDef.setDateregistered(new Date());
            if(memberBean.getOccupation()!=null)
                clientDef.setOccupation(occupationRepo.findOne(QOccupation.occupation.shortDesc.eq(memberBean.getOccupation())));
            clientDef.setPinNo(memberBean.getPinNumber());
            OrgBranch defaultBranch = null;
            for(OrgBranch branch:branchRepository.findAll()){
                defaultBranch = branch;
                break;
            }
            clientDef.setRegisteredbrn(defaultBranch);
            clientRulesExecutor.handleClientChecks(clientDef);
            ClientDef savedClient = clientRepo.save(clientDef);
            return savedClient.getTenId();
        }
        return -2000l;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getUnassignedCovers(Long catId,Long bindCode) throws IllegalAccessException {
        return medicalCoversRepo.getUnassignedCovers(catId,bindCode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getUnassignPolTaxes(Long polId) throws IllegalAccessException {
        PolicyTrans policy = policyRepo.findOne(polId);
        Long proCode =policy.getProduct().getProCode();
        return unassignedTaxesRepo.getUnassignPolTaxes(polId,proCode);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void createCategoryBenefits(MedicalCoverDTO coverDTO) throws BadRequestException {
        List<MedCategoryBenefits> categoryBenefits = new ArrayList<>();
        for(Long benefitId:coverDTO.getBenefits()){
            MedicalCategory category = medicalCategoryRepo.findOne(coverDTO.getCatCode());
            MedicalCovers covers = medicalCoversRepo.findOne(benefitId);
            MedCategoryBenefits benefit = new MedCategoryBenefits();
            benefit.setCover(covers);
            benefit.setWaitPeriod(covers.getWaitPeriod());
            benefit.setCategory(medicalCategoryRepo.findOne(coverDTO.getCatCode()));
            benefit.setWefDate(category.getPolicy().getWefDate());
            benefit.setWetDate(category.getPolicy().getWetDate());
            categoryBenefits.add(benefit);
        }
        benefitRepo.save(categoryBenefits);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void createNewPolTax(TaxesDTO taxesDTO) throws BadRequestException {
        List<PolicyTaxes> policyTaxes = new ArrayList<>();
        PolicyTrans policy = policyRepo.findOne(taxesDTO.getTaxPolicyCode());
        for(Long taxId:taxesDTO.getTaxes()){
            TaxRates taxRates = unassignedTaxesRepo.findOne(taxId);
            PolicyTaxes policyTax = new PolicyTaxes();
            policyTax.setPolicy(policy);
            policyTax.setRateType(taxRates.getRateType());
            policyTax.setRevenueItems(taxRates.getRevenueItems());
            policyTax.setSubclass(taxRates.getSubclass());
            policyTax.setTaxLevel(taxRates.getTaxLevel());
            policyTax.setTaxRate(taxRates.getTaxRate());
            policyTax.setDivFactor(taxRates.getDivFactor());
            policyTax.setTaxAmount(premComputeService.calculateTax(policy.getPremium(), taxRates.getTaxRate(), taxRates.getDivFactor(),taxRates.getRateType()));
            policyTaxes.add(policyTax);
        }
        polTaxesRepo.save(policyTaxes);
        try {
            Long policyId = policy.getPolicyId();
            computePrem.computePrem(policyId);
        }
        catch (IOException ex){
            throw new BadRequestException(ex.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteCategoryBenefit(Long benefitId,Long polCode) throws BadRequestException {
        if(benefitRepo.findOne(benefitId).getCover().getFundCoverMand()!=null && "Y".equalsIgnoreCase(benefitRepo.findOne(benefitId).getCover().getFundCoverMand())){
            throw new BadRequestException("Cannot delete mandatory cover...");
        }

        MedCategoryBenefits categoryBenefits = benefitRepo.findOne(benefitId);
        if (categoryBenefits.getStatus().equalsIgnoreCase("C")){
            throw new BadRequestException("Cannot delete cover marked for change...");
        }
        if (categoryBenefits.getStatus().equalsIgnoreCase("N")) {
            Iterable<CategoryMemberBenefits> memBenefits = memberBenefitsRepo.findAll(QCategoryMemberBenefits.categoryMemberBenefits.benefit.sectId.eq(benefitId));
            for (CategoryMemberBenefits memBenefit : memBenefits) {
                memberBenefitsRepo.delete(memBenefit.getCmbId());
            }

            benefitRepo.delete(benefitId);
        }else {
            categoryBenefits.setStatus("D");
            benefitRepo.save(categoryBenefits);
        }
        try {
            computePrem.computePrem(categoryBenefits.getCategory().getPolicy().getPolicyId());
        }
        catch (IOException ex){
            ex.printStackTrace();
            throw new BadRequestException(ex.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getUnassignedBinderProviders(Long catId, Long bindCode) throws IllegalAccessException {
        System.out.println("cat id "+catId+" bind code "+bindCode);
        return providersRepo.getUnassignedProviders(catId,bindCode);
    }

    @Override
    @Transactional(readOnly = false)
    public void saveCategoryProviders(ProviderBean providerBean) throws BadRequestException {
        List<CatalogueProviders> providers = new ArrayList<>();
        for(Long providerId:providerBean.getProviders()){
            CatalogueProviders provider = new CatalogueProviders();
            provider.setCategory(medicalCategoryRepo.findOne(providerBean.getCatCode()));
            provider.setProviders(serviceProvidersRepo.findOne(providerId));
            providers.add(provider);
        }
        providersRepo.save(providers);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteCategoryProviders(Long providerId) throws BadRequestException {
        providersRepo.delete(providerId);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<PolicyClauses> getNewClauses(Long polId) throws BadRequestException {
        PolicyTrans policy = policyTransService.getPolicyDetails(polId);
        if(policy==null) throw new BadRequestException("No Policy Transaction");
        Set<PolicyClauses> policyClauses = new HashSet<>();
        Iterable<PolicyClauses> polClauses = polClausesRepo.findAll(QPolicyClauses.policyClauses.policy.policyId.eq(policy.getPolicyId()));
        polClauses.forEach(policyClauses::add);
        Set<PolicyClauses> newClauses = new HashSet<>();
        Iterable<MedicalCategory> categories = medicalCategoryRepo.findAll(QMedicalCategory.medicalCategory.policy.policyId.eq(policy.getPolicyId()));
        for(MedicalCategory risk:categories){
            Iterable<BinderClauses> binderClauses = binderClauseRepo.findAll(QBinderClauses.binderClauses.binderDet.detId.eq(risk.getBinderDetails().getDetId()));
            for(BinderClauses clause:binderClauses){
                PolicyClauses polClause = new PolicyClauses();
                polClause.setClauHeading(clause.getClause().getClause().getClauHeading());
                polClause.setClause(clause.getClause());
                polClause.setClauWording(clause.getClause().getClause().getClauWording());
                polClause.setEditable(clause.getClause().getClause().isEditable());
                polClause.setNewClause("Y");
                polClause.setPolicy(policy);
                newClauses.add(polClause);
            }
            Iterable<SubclassClauses> subClauses = subclauseRepo.findAll(QSubclassClauses.subclassClauses.subclass.subId.eq(risk.getBinderDetails().getSubCoverTypes().getSubclass().getSubId()));
            for(SubclassClauses clause:subClauses){
                PolicyClauses polClause = new PolicyClauses();
                polClause.setClauHeading(clause.getClause().getClauHeading());
                polClause.setClause(clause);
                polClause.setClauWording(clause.getClause().getClauWording());
                polClause.setEditable(clause.getClause().isEditable());
                polClause.setNewClause("Y");
                polClause.setPolicy(policy);
                newClauses.add(polClause);
            }
        }
        newClauses.removeAll(policyClauses);
        return newClauses;
    }

    @PreAuthorize("hasAnyAuthority('SAVE_POLICY')")
    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void createClause(PolicyClausesBean clause) throws BadRequestException{
        PolicyTrans policy = policyTransService.getPolicyDetails(clause.getPolId());
        List<PolicyClauses> createdClauses = new ArrayList<>();
        if(policy==null) throw new BadRequestException("No Policy Transaction");
        for(Long clauseId:clause.getClauses()){
            for (Iterator<PolicyClauses> it = getNewClauses(clause.getPolId()).iterator(); it.hasNext(); ) {
                PolicyClauses cl = (PolicyClauses)it.next();
                if(cl.getClause().getClauId().longValue()==clauseId.longValue()){
                    createdClauses.add(cl);
                }

            }
        }
        polClausesRepo.save(createdClauses);

    }

    @Override
    @Transactional(readOnly = true)
    public Page<BedTypes> findBedTypes(String paramString, Pageable paramPageable) {
        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred = QBedTypes.bedTypes.isNotNull();
        } else {
            pred = QBedTypes.bedTypes.bedShtDesc.containsIgnoreCase(paramString)
                    .or(QBedTypes.bedTypes.bedDesc.containsIgnoreCase(paramString));
        }
        return bedTypesRepo.findAll(pred, paramPageable);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<SystemTransactions> getFundReceipts(DataTablesRequest request, Long polCode) throws IllegalAccessException {
        Iterable<SelfFundParams> selfFundParams = selfFundParamsRepo.findAll(QSelfFundParams.selfFundParams.policyTrans.policyId.eq(polCode));
        Long selfFundCode = -2000l;
        for(SelfFundParams selfFundParam:selfFundParams){
            selfFundCode = selfFundParam.getSfpId();
            break;
        }
        BooleanExpression pred = QSystemTransactions.systemTransactions.fundParams.sfpId.eq(selfFundCode).and(QSystemTransactions.systemTransactions.transdc.eq("C"));
        Page<SystemTransactions> page = transactionsRepo.findAll(pred.and(request.searchPredicate(QSystemTransactions.systemTransactions)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryMembers getCategoryMemberDetails(Long catId) {
        if(catId==null) return new CategoryMembers();
        return membersRepo.findOne(catId);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalReceiptedAmt(Long polCode) {
        Iterable<SelfFundParams> selfFundParams = selfFundParamsRepo.findAll(QSelfFundParams.selfFundParams.policyTrans.policyId.eq(polCode));
        Long selfFundCode = -2000l;
        for(SelfFundParams selfFundParam:selfFundParams){
            selfFundCode = selfFundParam.getSfpId();
            break;
        }
        BigDecimal receiptedAmt = BigDecimal.ZERO;
        BooleanExpression pred = QSystemTransactions.systemTransactions.fundParams.sfpId.eq(selfFundCode).and(QSystemTransactions.systemTransactions.transdc.eq("C"));
        Iterable<SystemTransactions> transactions = transactionsRepo.findAll(pred);
        for(SystemTransactions trans:transactions){
            receiptedAmt = receiptedAmt.add(trans.getAmount());
        }

        return receiptedAmt;

    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void processClaimBatchTrans(MedicalTransDTO transDTO) throws BadRequestException {
        if(transDTO.getTrans().size()==0) throw new BadRequestException("No Transaction Selected to Process...");
        BigDecimal sumAmount = BigDecimal.ZERO;
        MedServiceProviders serviceProvider = null;
        Currencies currencies= null;
        List<MedicalParTrans> parTransactions = new ArrayList<>();
        for(Long transid:transDTO.getTrans()){
            MedicalParTrans parTrans = medicalParRepo.findOne(transid);
            currencies = parTrans.getCategory().getPolicy().getTransCurrency();
            serviceProvider = parTrans.getProviderContracts().getServiceProviders();
            sumAmount = sumAmount.add(parTrans.getTotalApprAmount());
            parTrans.setBatched("Y");
            parTransactions.add(parTrans);
        }

        medicalParRepo.save(parTransactions);
        Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("D");
        if (sequenceRepo.count(seqPredicate) == 0)
            throw new BadRequestException("Sequence for Debit Notes has not been defined");
        SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
        Long seqNumber = sequence.getNextNumber();
        final String refNo = String.format("%05d", seqNumber);
        sequence.setLastNumber(seqNumber);
        sequence.setNextNumber(seqNumber + 1);
        sequenceRepo.save(sequence);
        SystemTrans transaction = new SystemTrans();
        transaction.setDoneDate(new Date());
        transaction.setDoneBy(userUtils.getCurrentUser());
        transaction.setTransLevel("U");
        transaction.setTransCode("NBD"); //A way to setup and look up for transaction transcode
        transaction.setTransAuthorised("N");
        transRepo.save(transaction);
        SystemTransactions transactions = new SystemTransactions();
        transactions.setAmount(sumAmount);
        transactions.setBalance(sumAmount);
        transactions.setControlAcc(serviceProvider.getAccountNumber());
        transactions.setCurrency(currencies);
        transactions.setCurrRate(BigDecimal.ONE);
        transactions.setNarrations("Pay Service Provider");
        transactions.setOrigin("C");
        transactions.setNetAmount(sumAmount);
        transactions.setPayeeName(serviceProvider.getPayeeName());
        transactions.setPostedDate(new Date());
        transactions.setPostedUser(userUtils.getCurrentUser());
        transactions.setRefNo(refNo);
        transactions.setSettleAmt(BigDecimal.ZERO);
        transactions.setTransDate(new Date());
        transactions.setTransdc("C");
        transactions.setTransaction(transaction);
        transactionsRepo.save(transactions);
    }

    @Override
    public DataTablesResult<RiskDocs> findMemberDocs(DataTablesRequest request, Long memberId) throws IllegalAccessException {
        BooleanExpression pred = QRiskDocs.riskDocs.member.sectId.eq(memberId);
        Page<RiskDocs> page = riskDocsRepo.findAll(pred.and(request.searchPredicate(QRiskDocs.riskDocs)), request);
        return new DataTablesResult<>(request, page);
    }
}
