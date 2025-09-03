package com.brokersystems.brokerapp.medical.service.impl;

import com.brokersystems.brokerapp.accounts.model.BankBranches;
import com.brokersystems.brokerapp.accounts.model.QBankBranches;
import com.brokersystems.brokerapp.accounts.repository.BankBranchRepo;
import com.brokersystems.brokerapp.medical.model.*;
import com.brokersystems.brokerapp.medical.repository.*;
import com.brokersystems.brokerapp.medical.service.MedicalClmService;
import com.brokersystems.brokerapp.medical.service.MedicalSetupsService;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.model.ClausesDef;
import com.brokersystems.brokerapp.setup.model.SectionsDef;
import com.brokersystems.brokerapp.setup.repository.BindersRepo;
import com.brokersystems.brokerapp.setup.repository.ClausesRepo;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.BooleanExpression;
import de.siegmar.fastcsv.reader.CsvParser;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**fv
 * Created by peter on 4/21/2017.
 */
@Service
public class MedicalSetupsServiceImpl implements MedicalSetupsService {

    @Autowired
    private BedTypesRepo bedTypesRepo;

    @Autowired
    private CardTypesRepo cardTypesRepo;

    @Autowired
    private LabTestsRepo labTestsRepo;

    @Autowired
    private DependentTypesRepo dependentTypesRepo;

    @Autowired
    private MedActivitiesRepo medActivitiesRepo;

    @Autowired
    private BenefitSectionsRepo benefitSectionsRepo;

    @Autowired
    private SpecialistFeesRepo specialistFeesRepo;

    @Autowired
    private FamilySizeRepo familySizeRepo;

    @Autowired
    private  MedicalRulesRepo medicalRulesRepo;

    @Autowired
    private AilmentsRepo ailmentsRepo;

    @Autowired
    private BinderExclusionsRepo exclusionsRepo;

    @Autowired
    private BinderLoadingsRepo loadingsRepo;

    @Autowired
    private BindersRepo bindersRepo;

    @Autowired
    private ServiceProvidersRepo providersRepo;

    @Autowired
    private ServiceProviderTypesRepo providerTypesRepo;

    @Autowired
    private BankBranchRepo bankBranchRepo;

    @Autowired
    private BinderProvidersRepo binderProvidersRepo;

    @Autowired
    private CoverLimitsRepo coverLimitsRepo;

    @Autowired
    private SubLimitsRepo subLimitsRepo;

    @Autowired
    private CsvReader csvReader;

    @Autowired
    private MedEventsRepo medEventsRepo;

    @Autowired
    private ServiceProviderContractRepo providerContractRepo;

    @Autowired
    private BenefitSectionsRepo medicalNetworksRepo;

    @Autowired
    private BinderNetworkRepo networkRepo;

    @Autowired
    private BinderExclusionClauseRepo clausesRepo;

    @Autowired
    private MedicalCoversRepo medicalCoversRepo;


    @Autowired
    private ServiceProviderServicesRepo serviceProviderServicesRepo;

    @Autowired
    private ServiceProviderActivitiesRepo serviceProviderActivitiesRepo;


    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<BedTypes> findAllBedTypes(DataTablesRequest request) throws IllegalAccessException {
        Page<BedTypes> page = bedTypesRepo.findAll(request.searchPredicate(QBedTypes.bedTypes), request);
        return new DataTablesResult<>(request, page);
    }


    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<CardTypes> findAllCardTypes(DataTablesRequest request) throws IllegalAccessException {
        Page<CardTypes> page = cardTypesRepo.findAll(request.searchPredicate(QCardTypes.cardTypes), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = false)
    public void defineBedTypes(BedTypes bedTypes) throws BadRequestException {
       bedTypesRepo.save(bedTypes);
    }

    @Override
    @Transactional(readOnly = false)
    public void defineCardTypes(CardTypes cardTypes) throws BadRequestException {
        cardTypesRepo.save(cardTypes);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteBedTypes(Long bedId) {
     bedTypesRepo.delete(bedId);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteCardTypes(Long cardId) {
        cardTypesRepo.delete(cardId);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<LabTest> findAllLabtests(DataTablesRequest request) throws IllegalAccessException {
        Page<LabTest> page = labTestsRepo.findAll(request.searchPredicate(QLabTest.labTest), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = false)
    public void defineLabTests(LabTest labTest) throws BadRequestException {
        labTestsRepo.save(labTest);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteLabTests(Long labId) {
        labTestsRepo.delete(labId);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<DependentTypes> findAllDependentTypes(DataTablesRequest request) throws IllegalAccessException {
        Page<DependentTypes> page = dependentTypesRepo.findAll(request.searchPredicate(QDependentTypes.dependentTypes), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = false)
    public void defineDependentTypes(DependentTypes dependentTypes) throws BadRequestException {
        if(dependentTypes.getDepId()==null){
            Long countPrincipal = dependentTypesRepo.count(QDependentTypes.dependentTypes.mainMember.eq(true));
            if(countPrincipal > 0 && dependentTypes.isMainMember()){
                throw new BadRequestException("Can only have one Member Type as the main Member");
            }
        }
        dependentTypesRepo.save(dependentTypes);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteDependentTypes(Long dependentId) {
      dependentTypesRepo.delete(dependentId);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<MedicalNetworks> findAllBenefitSections(DataTablesRequest request) throws IllegalAccessException {
        Page<MedicalNetworks> page = benefitSectionsRepo.findAll(request.searchPredicate(QMedicalNetworks.medicalNetworks), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = false)
    public void defineBenefitSections(MedicalNetworks medicalNetworks) throws BadRequestException {
       benefitSectionsRepo.save(medicalNetworks);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteBenefitSections(Long sectionId) {
       benefitSectionsRepo.delete(sectionId);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<SpecialistFees> findAllSpecialistFees(DataTablesRequest request) throws IllegalAccessException {
        Page<SpecialistFees> page = specialistFeesRepo.findAll(request.searchPredicate(QSpecialistFees.specialistFees), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = false)
    public void defineSpecialistFees(SpecialistFees specialistFees) throws BadRequestException {
      specialistFeesRepo.save(specialistFees);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteSpecialistFees(Long specId) {
      specialistFeesRepo.delete(specId);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<MedActivities> findAllBenefits(DataTablesRequest request,Long serviceCode) throws IllegalAccessException {
        BooleanExpression pred = QMedActivities.medActivities.services.labId.eq(serviceCode);
        Page<MedActivities> page = medActivitiesRepo.findAll(pred.and(request.searchPredicate(QMedActivities.medActivities)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = false)
    public void defineBenefits(MedActivities benefits) throws BadRequestException {
        if(benefits.getSection()==null)
            throw new BadRequestException("Select Affected Benefit to continue");
        medActivitiesRepo.save(benefits);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteBenefits(Long benId) {
       medActivitiesRepo.delete(benId);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<FamilySizes> findFamilySize(DataTablesRequest request) throws IllegalAccessException {
        Page<FamilySizes> page = familySizeRepo.findAll(request.searchPredicate(QFamilySizes.familySizes), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = false)
    public void defineFamilySize(FamilySizes familySize) throws BadRequestException {
         familySizeRepo.save(familySize);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteFamilySize(Long famId) {
        familySizeRepo.delete(famId);
    }


    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<MedicalBinderRules> findMedBinderRules(DataTablesRequest request, Long bindCode) throws IllegalAccessException {
        BooleanExpression pred = QMedicalBinderRules.medicalBinderRules.binder.binId.eq(bindCode);
        Page<MedicalBinderRules> page = medicalRulesRepo.findAll(pred.and(request.searchPredicate(QMedicalBinderRules.medicalBinderRules)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = false)
    public void defineBinderRules(MedicalBinderRules binderRules) throws BadRequestException {
          if(binderRules.getChecks()==null)
              throw new BadRequestException("Rule is Mandatory");
          if(binderRules.getMandatory()!=null && "on".equalsIgnoreCase(binderRules.getMandatory())){
           binderRules.setMandatory("Y");
          }  else binderRules.setMandatory("N");
            binderRules.setShtDesc(binderRules.getChecks().getChecks().getCheckShtDesc());
           medicalRulesRepo.save(binderRules);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteBinderRules(Long ruleId) {
        medicalRulesRepo.delete(ruleId);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<Ailments> findAilments(DataTablesRequest request) throws IllegalAccessException {
        BooleanExpression pred = QAilments.ailments.parentAilment.isNull();
        Page<Ailments> page = ailmentsRepo.findAll(pred.and(request.searchPredicate(QAilments.ailments)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = false)
    public void defineAilments(Ailments ailments) throws BadRequestException {
          ailmentsRepo.save(ailments);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteAilments(Long ailId) {
        ailmentsRepo.delete(ailId);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<BinderExclusions> findBinderExclusions(DataTablesRequest request, Long bindCode) throws IllegalAccessException {
        BooleanExpression pred = QBinderExclusions.binderExclusions.binder.binId.eq(bindCode);
        Page<BinderExclusions> page = exclusionsRepo.findAll(pred.and(request.searchPredicate(QBinderExclusions.binderExclusions)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteBinderExclusions(Long beId) {
        exclusionsRepo.delete(beId);
    }

    @Override
    public void defineBinderExclusion(BinderExclusions binderExclusion) throws BadRequestException {
        exclusionsRepo.save(binderExclusion);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<BinderLoadings> findBinderLoadings(DataTablesRequest request, Long bindCode) throws IllegalAccessException {
        BooleanExpression pred = QBinderLoadings.binderLoadings.binder.binId.eq(bindCode);
        Page<BinderLoadings> page = loadingsRepo.findAll(pred.and(request.searchPredicate(QBinderLoadings.binderLoadings)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteBinderLoadings(Long blId) {
        loadingsRepo.delete(blId);
    }

    @Override
    public void defineBinderLoadings(BinderLoadings binderLoading) throws BadRequestException {
        if(binderLoading.getRateType()==null || "R".equalsIgnoreCase(binderLoading.getRateType())){
            binderLoading.setRateType("R");
        }
        else {
            binderLoading.setLoadingAmt(binderLoading.getRate());
        }
        if(binderLoading.getChronic()!=null && "on".equalsIgnoreCase(binderLoading.getChronic())){
            binderLoading.setChronic("Y");
        }
        else binderLoading.setChronic("N");
        binderLoading.setBinder(loadingsRepo.findOne(binderLoading.getClId()).getBinder());
        loadingsRepo.save(binderLoading);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LabTest> findUnassignedServices(Long providerId, String subName)
            throws IllegalAccessException {
        if (subName!=null)
            subName = StringUtils.lowerCase(subName);
        return labTestsRepo.getUnassignedServices(providerId,subName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedActivities> findUnassignedServiceActivities(Long serviceId, String subName)
            throws IllegalAccessException {
        if (subName!=null)
            subName = StringUtils.lowerCase(subName);
        System.out.println("serviceId="+serviceId +";subName"+subName);
        return medActivitiesRepo.getUnassignedActivities(serviceId,subName);
    }

    @Override
    public void defineBinderExclusions(BinderExclusions binderExclusions) throws BadRequestException {
        BinderExclusions exclusions =exclusionsRepo.findOne(binderExclusions.getBeId());
        binderExclusions.setBinder(exclusionsRepo.findOne(binderExclusions.getBeId()).getBinder());
        binderExclusions.setAilment(exclusions.getAilment());
        binderExclusions.setClausesDef(exclusions.getClausesDef());
        binderExclusions.setMedicalnetworks(exclusions.getMedicalnetworks());


        exclusionsRepo.save(binderExclusions);
    }

    @Override
    @Transactional(readOnly = false)
    public void createProviderServices(providerServicesBean servicesBean) {

        List<ServiceProviderServices> providerServices = new ArrayList<>();
        for(Long servId:servicesBean.getServices()){
            MedServiceProviders provider = providersRepo.findOne(servicesBean.getProviderCode());
            LabTest labTest = labTestsRepo.findOne(servId);
            ServiceProviderServices service = new ServiceProviderServices();
            service.setMedicalServices(labTest);
            service.setServiceProviders(provider);
            service.setWefDate(new Date());
            providerServices.add(service);
        }
        serviceProviderServicesRepo.save(providerServices);

    }

    @Override
    @Transactional(readOnly = false)
    public void createProviderServiceActvty(providerServicesActivitiesBean activityBean) {

        List<ServiceProviderActivities> providerActivities= new ArrayList<>();
        for(Long activityId:activityBean.getServiceactivities()){
            ServiceProviderServices services = serviceProviderServicesRepo.findOne(activityBean.getServiceCode());
            MedActivities activity = medActivitiesRepo.findOne(activityId);
            ServiceProviderActivities activities = new ServiceProviderActivities();
            activities.setActivities(activity);
            activities.setServices(services);
            activities.setWefDate(new Date());
            providerActivities.add(activities);
        }
        serviceProviderActivitiesRepo.save(providerActivities);

    }


    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getUnassignedLoadings(Long bindCode) throws IllegalAccessException {
        return loadingsRepo.getBinderAilments(bindCode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getUnassignedNetworks(Long bindCode, String subName) throws IllegalAccessException {
        if (subName!=null)
            subName = StringUtils.lowerCase(subName);
        return networkRepo.getBinderNetworks(bindCode,subName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getUnassignedClauseExclusions(Long bindCode, String subName) throws  IllegalAccessException{
        if (subName!=null)
            subName = StringUtils.lowerCase(subName);
        return clausesRepo.getBinderExclusions(bindCode,subName);
    }


    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getUnassignedExclusions(Long bindCode, String subName) throws IllegalAccessException {
        if (subName!=null)
            subName = StringUtils.lowerCase(subName);
        return exclusionsRepo.getBinderAilments(bindCode,subName);
    }

    @Override
    @Transactional(readOnly = false)
    public void createLoadings(AilmentsBean ailmentsBean) {
        List<BinderLoadings> loadings = new ArrayList<>();
        for(Long ailId:ailmentsBean.getAilments()){
            Ailments ailment = ailmentsRepo.findOne(ailId);
            BinderLoadings loading = new BinderLoadings();
            loading.setAilment(ailment);
            loading.setBinder(bindersRepo.findOne(ailmentsBean.getBindCode()));
            loadings.add(loading);
        }
        loadingsRepo.save(loadings);
    }

    @Override
    @Transactional(readOnly = false)
    public void defineProviderSerces(ServiceProviderServices serviceProviderServices) throws BadRequestException {
        serviceProviderServicesRepo.save(serviceProviderServices);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteProviderService(Long providerServiceId) {
        serviceProviderServicesRepo.delete(providerServiceId);
    }

    @Override
    @Transactional(readOnly = false)
    public DataTablesResult<ServiceProviderServices> findProviderServices(DataTablesRequest request, Long providerId) throws IllegalAccessException {
        BooleanExpression pred = QServiceProviderServices.serviceProviderServices.serviceProviders.mspId.eq(providerId);
        Page<ServiceProviderServices> page = serviceProviderServicesRepo.findAll(pred.and(request.searchPredicate(QServiceProviderServices.serviceProviderServices)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = false)
    public DataTablesResult<ServiceProviderActivities> findServiceActivities(DataTablesRequest request, Long serviceId) throws IllegalAccessException {
        BooleanExpression pred = QServiceProviderActivities.serviceProviderActivities.services.spsId.eq(serviceId);
        Page<ServiceProviderActivities> page = serviceProviderActivitiesRepo.findAll(pred.and(request.searchPredicate(QServiceProviderActivities.serviceProviderActivities)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = false)
    public void createExclusions(AilmentsBean ailmentsBean) {
        List<BinderExclusions> exclusions = new ArrayList<>();
        for(Long ailId:ailmentsBean.getAilments()){
            Ailments ailment = ailmentsRepo.findOne(ailId);
            BinderExclusions exclusion = new BinderExclusions();
            exclusion.setAilment(ailment);
            exclusion.setBinder(bindersRepo.findOne(ailmentsBean.getBindCode()));
            exclusions.add(exclusion);
        }
        exclusionsRepo.save(exclusions);
    }

    @Override
    @Transactional(readOnly = false)
    public void createNetworks(AilmentsBean ailmentsBean) {
        List<BinderExclusions> exclusions = new ArrayList<>();
        for(Long ailId:ailmentsBean.getClaimNetworks()){
            MedicalNetworks medicalnetworks =medicalNetworksRepo.findOne(ailId) ;
            BinderExclusions exclusion = new BinderExclusions();
            exclusion.setMedicalnetworks(medicalnetworks);
            exclusion.setBinder(bindersRepo.findOne(ailmentsBean.getBindCode()));
            exclusions.add(exclusion);
        }
        exclusionsRepo.save(exclusions);
    }

    @Override
    @Transactional(readOnly = false)
    public void createClauseExclusions(AilmentsBean ailmentsBean) {
        List<BinderExclusions> exclusions = new ArrayList<>();
        for(Long ailId:ailmentsBean.getClauseExclusions()){
            ClausesDef clausesDef = clausesRepo.findOne(ailId);
            BinderExclusions exclusion = new BinderExclusions();
            exclusion.setClausesDef(clausesDef);
            exclusion.setBinder(bindersRepo.findOne(ailmentsBean.getBindCode()));
            exclusions.add(exclusion);
        }
        exclusionsRepo.save(exclusions);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<MedServiceProviderTypes> findServiceProviderTypes(DataTablesRequest request) throws IllegalAccessException {
        Page<MedServiceProviderTypes> page = providerTypesRepo.findAll(request.searchPredicate(QMedServiceProviderTypes.medServiceProviderTypes), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = false)
    public void defineProviderTypes(MedServiceProviderTypes serviceProviderTypes) throws BadRequestException {
           providerTypesRepo.save(serviceProviderTypes);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteProviderTypes(Long providerId) {
           providerTypesRepo.delete(providerId);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<MedServiceProviders> findServiceProviders(DataTablesRequest request,Long providerId) throws IllegalAccessException {
        BooleanExpression pred = QMedServiceProviders.medServiceProviders.serviceProviderTypes.id.eq(providerId);
        Page<MedServiceProviders> page = providersRepo.findAll(pred.and(request.searchPredicate(QMedServiceProviders.medServiceProviders)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public void defineProviders(MedServiceProviders serviceProviders) throws BadRequestException {
          if(serviceProviders.getStatus()!=null || "on".equalsIgnoreCase(serviceProviders.getStatus())){
              serviceProviders.setStatus("Active");
          }
        else{
              serviceProviders.setStatus("Inactive");
          }
          providersRepo.save(serviceProviders);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteProviders(Long providerId) {
        providersRepo.delete(providerId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MedServiceProviderTypes> findProviderTypesForSelect(String term, Pageable pageable) {
        Predicate pred = null;
        if (term == null || StringUtils.isBlank(term)) {
            pred = QMedServiceProviderTypes.medServiceProviderTypes.isNotNull();
        } else {
            pred = QMedServiceProviderTypes.medServiceProviderTypes.desc.containsIgnoreCase(term)
                    .or(QMedServiceProviderTypes.medServiceProviderTypes.shtDesc.containsIgnoreCase(term));
        }
        return providerTypesRepo.findAll(pred, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BankBranches> findBankBranchesForSelect(String term, Pageable pageable) {
        Predicate pred = null;
        if (term == null || StringUtils.isBlank(term)) {
            pred = QBankBranches.bankBranches.isNotNull();
        } else {
            pred = QBankBranches.bankBranches.branchName.containsIgnoreCase(term)
                    .or(QBankBranches.bankBranches.branchShtDesc.containsIgnoreCase(term));
        }
        return bankBranchRepo.findAll(pred, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<BinderProviders> findBinderProviders(DataTablesRequest request, Long bindCode) throws IllegalAccessException {
        BooleanExpression pred = QBinderProviders.binderProviders.binder.binId.eq(bindCode);
        Page<BinderProviders> page = binderProvidersRepo.findAll(pred.and(request.searchPredicate(QBinderProviders.binderProviders)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly=true)
    public Page<MedServiceProviderTypes> findBinderProviderTypes(Long bincode, Pageable pageable)
    {
        return providerTypesRepo.getBinderProviderTypes(bincode,pageable);
    }

    @Override
    @Transactional(readOnly=true)
    public Page<ServiceProviderContracts> findBinderProvidersByType(Long bincode, Long typeCode, Pageable pageable)
    {
        Iterable<ServiceProviderContracts> serviceProviders = providerContractRepo.findAll(QServiceProviderContracts.serviceProviderContracts.binder.binId.eq(bincode)
        .and(QServiceProviderContracts.serviceProviderContracts.serviceProviders.serviceProviderTypes.id.eq(typeCode)));
        List<ServiceProviderContracts> providers= new ArrayList<>();
        for (ServiceProviderContracts serviceProvider:serviceProviders){
            serviceProvider.setProviderName(serviceProvider.getServiceProviders().getName());
            providers.add(serviceProvider);
        }
        Page<ServiceProviderContracts> providersPage = new PageImpl<ServiceProviderContracts>(providers);
        return providersPage;

    }


    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getUnassignedProviders(Long bindCode) throws IllegalAccessException {
        return binderProvidersRepo.getBinderProviders(bindCode);
    }

    @Override
    @Transactional(readOnly = false)
    public void createProviders(ProviderBean providerBean) {
        List<BinderProviders> providers = new ArrayList<>();
        for(Long providerId:providerBean.getProviders()){
            MedServiceProviders provider = providersRepo.findOne(providerId);
            BinderProviders prd = new BinderProviders();
            prd.setProvider(provider);
            prd.setBinder(bindersRepo.findOne(providerBean.getBindCode()));
            providers.add(prd);
        }
        binderProvidersRepo.save(providers);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<CoverLimits> findCoverLimits(DataTablesRequest request, Long medId) throws IllegalAccessException {
        BooleanExpression pred = QCoverLimits.coverLimits.covers.id.eq(medId);
        Page<CoverLimits> page = coverLimitsRepo.findAll(pred.and(request.searchPredicate(QCoverLimits.coverLimits)), request);
        System.out.println(page.getContent());
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = false)
    public void defineCoverLimits(CoverLimits coverLimit) throws BadRequestException {
        Long count = coverLimitsRepo.count(QCoverLimits.coverLimits.covers.id.eq(coverLimit.getCovers().getId()).and(QCoverLimits.coverLimits.limitAmount.eq(coverLimit.getLimitAmount())));
        if(count > 0) throw new BadRequestException("Limit Amount Exists...");
        coverLimitsRepo.save(coverLimit);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteCoverLimits(Long limitId) {
        System.out.println("limitId == "+limitId);
        coverLimitsRepo.delete(limitId);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteProvideContract(Long spcId) {
        providerContractRepo.delete(spcId);
    }
    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<SubLimits> findCoverSubLimits(DataTablesRequest request, Long limitId) throws IllegalAccessException {
        BooleanExpression pred = QSubLimits.subLimits.covLimit.id.eq(limitId);
        Page<SubLimits> page = subLimitsRepo.findAll(pred.and(request.searchPredicate(QSubLimits.subLimits)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class,IOException.class })
    public void importSubLimits(SubLimitsImport subLimitsImport) throws BadRequestException,IOException {
       if(subLimitsImport.getFile()==null) throw  new BadRequestException("No File to import...");
        if(subLimitsImport.getCoverLimit()==null) throw new BadRequestException("Select Cover Limit to Upload Sub Limits");
        if(!subLimitsImport.getFile().getOriginalFilename().endsWith(".csv")) throw new BadRequestException("Can only Upload CSV File");
        try(CsvParser csvParser = csvReader.parse( multipartToFile(subLimitsImport.getFile()), StandardCharsets.UTF_8)){
            CsvRow row;
            List<SubLimits> subLimits = new ArrayList<>();
            while ((row = csvParser.nextRow()) != null) {
                SubLimits subLimit = new SubLimits();
                subLimit.setShtDesc(row.getField(0));
                subLimit.setDesc(row.getField(1));
                if(row.getField(2)==null || StringUtils.isBlank(row.getField(2))){

                }
                else{
                    try{
                        Long val = Long.parseLong(row.getField(2));
                        subLimit.setWaitingPeriod(val);
                    }
                    catch(NumberFormatException ex){
                        throw new BadRequestException(ex.getMessage());
                    }
                }
                subLimit.setLimit(row.getField(3));
                subLimit.setCovLimit(coverLimitsRepo.findOne(subLimitsImport.getCoverLimit()));
                subLimits.add(subLimit);
            }
            subLimitsRepo.save(subLimits);
        }
    }

    private File multipartToFile(MultipartFile multipart) throws IllegalStateException, IOException
    {
        File convFile = new File( multipart.getOriginalFilename());
        if(!convFile.exists()) convFile.delete();
        multipart.transferTo(convFile);
        return convFile;
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<MedicalEvents> findAllEvents(DataTablesRequest request) throws IllegalAccessException {
        Page<MedicalEvents> page = medEventsRepo.findAll(request.searchPredicate(QMedicalEvents.medicalEvents), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = false)
    public void defineEvents(MedicalEvents events) throws BadRequestException {
        if(events.getType()==null) throw new BadRequestException("Select Event Type");
          medEventsRepo.save(events);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteEvents(Long eventId) {
       medEventsRepo.delete(eventId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MedServiceProviders> findProviderForSelect(String term, Pageable pageable) {
        Predicate pred = null;
        if (term == null || StringUtils.isBlank(term)) {
            pred = QMedServiceProviders.medServiceProviders.isNotNull();
        } else {
            pred = QMedServiceProviders.medServiceProviders.name.containsIgnoreCase(term);
        }
        return providersRepo.findAll(pred, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<ServiceProviderContracts> findAllProviderContracts(DataTablesRequest request, Long bindCode) throws IllegalAccessException {
        BooleanExpression pred = QServiceProviderContracts.serviceProviderContracts.binder.binId.eq(bindCode);
        Page<ServiceProviderContracts> page = providerContractRepo.findAll(pred.and(request.searchPredicate(QServiceProviderContracts.serviceProviderContracts)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public void defineProviderContract(ServiceProviderContracts providerContract) throws BadRequestException {
        if(providerContract.getSpcId()==null){
            String contractNo = generateProviderContractNo(providerContract.getServiceProviders().getName(),providerContract.getWefDate());
            if(providerContractRepo.count(QServiceProviderContracts.serviceProviderContracts.contractNo.eq(contractNo))> 0)
                throw new BadRequestException("The Contract Already exists...");
            providerContract.setContractNo(contractNo);
            providerContract.setIssueDate(new Date());
            System.out.println("debug="+providerContract.getBindCode());
            providerContract.setBinder(bindersRepo.findOne(providerContract.getBindCode()));
        }else {
            if (providerContract.getSpcId() == null)
                throw new BadRequestException("Cannot Update non-existed Contract....");
            ServiceProviderContracts contracts = providerContractRepo.findOne(providerContract.getSpcId());
            providerContract.setApproved(false);
            providerContract.setIssueDate(contracts.getIssueDate());
            providerContract.setBinder(bindersRepo.findOne(providerContract.getBindCode()));
        }
        providerContractRepo.save(providerContract);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteProviderContract(Long contractId) {
        providerContractRepo.delete(contractId);
    }

    private String generateProviderContractNo(String providerName, Date wef){
        StringTokenizer tok = new StringTokenizer(providerName, " ");
        StringBuffer shtDesc = new StringBuffer();
        while(tok.hasMoreTokens()){
            shtDesc.append(tok.nextToken().substring(0,2).toUpperCase());
        }
       return shtDesc.append(new SimpleDateFormat("ddMMyyyy").format(wef.getTime())).toString();
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<Ailments> findAilmentDiagnosis(DataTablesRequest request, Long ailId) throws IllegalAccessException {
        BooleanExpression pred = QAilments.ailments.parentAilment.baId.eq(ailId);
        Page<Ailments> page = ailmentsRepo.findAll(pred.and(request.searchPredicate(QAilments.ailments)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MedicalNetworks> findNetworksForSelect(String term, Pageable pageable) {
        Predicate pred = null;
        if (term == null || StringUtils.isBlank(term)) {
            pred = QMedicalNetworks.medicalNetworks.isNotNull();
        } else {
            pred = QMedicalNetworks.medicalNetworks.benDesc.containsIgnoreCase(term)
                    .or(QMedicalNetworks.medicalNetworks.benShtDesc.containsIgnoreCase(term));
        }
        return  medicalNetworksRepo.findAll(pred, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Ailments> findAilmentsForSelect(String term, Pageable pageable) {
        Predicate pred = null;
        if (term == null || StringUtils.isBlank(term)) {
            pred = QAilments.ailments.parentAilment.isNotNull();
        } else {
            pred = QAilments.ailments.parentAilment.isNotNull().and(QAilments.ailments.baDesc.containsIgnoreCase(term)
                    .or(QAilments.ailments.baShtDesc.containsIgnoreCase(term)))
                    .and(QAilments.ailments.parentAilment.isNotNull());
        }
        return  ailmentsRepo.findAll(pred, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MedActivities> findActivitiesForSelect(String term, Pageable pageable,long labid) {
        Predicate pred = null;
        if (term == null || StringUtils.isBlank(term)) {
            pred = QMedActivities.medActivities.services.labId.eq(labid).and(QMedActivities.medActivities.isNotNull());
        } else {
            pred = QMedActivities.medActivities.services.labId.eq(labid).and(QMedActivities.medActivities.benDesc.containsIgnoreCase(term)
                    .or(QMedActivities.medActivities.benShtDesc.containsIgnoreCase(term)));
        }
        return  medActivitiesRepo.findAll(pred, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LabTest> findServicesForSelect( String term,Pageable pageable) {
        Predicate pred = null;
        if (term == null || StringUtils.isBlank(term)){
            pred =QLabTest.labTest.isNotNull();
        }else {
            pred = (QLabTest.labTest.labDesc.containsIgnoreCase(term)
            .or(QLabTest.labTest.labShtDesc.containsIgnoreCase(term)));
        }
        return  labTestsRepo.findAll(pred,pageable);


    }

    @Override
    @Transactional(readOnly = true)
    public Page<SectionsDef> findMedicalSections(String paramString, Pageable paramPageable) {
        return  medicalCoversRepo.getMedicalSections(paramString,paramPageable);
    }

    @Override
    public void deleteSubLimits(Long subLimitId) {
        subLimitsRepo.delete(subLimitId);
    }
}
