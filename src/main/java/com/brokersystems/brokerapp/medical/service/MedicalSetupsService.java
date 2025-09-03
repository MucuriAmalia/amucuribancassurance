package com.brokersystems.brokerapp.medical.service;

import com.brokersystems.brokerapp.accounts.model.BankBranches;
import com.brokersystems.brokerapp.medical.model.*;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.model.SectionsDef;
import org.hibernate.id.insert.Binder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

/**
 * Created by peter on 4/21/2017.
 */
public interface MedicalSetupsService {

    DataTablesResult<BedTypes> findAllBedTypes(DataTablesRequest request) throws IllegalAccessException;

    public DataTablesResult<CardTypes> findAllCardTypes(DataTablesRequest request) throws IllegalAccessException;

    void defineBedTypes(BedTypes bedTypes) throws BadRequestException;

    public void defineCardTypes(CardTypes cardTypes) throws BadRequestException;

    void deleteBedTypes(Long bedId);

    void deleteCardTypes(Long cardId);

    public DataTablesResult<LabTest> findAllLabtests(DataTablesRequest request) throws IllegalAccessException;

    public void defineLabTests(LabTest bedTypes) throws BadRequestException;

    public void deleteLabTests(Long labId);

    public DataTablesResult<DependentTypes> findAllDependentTypes(DataTablesRequest request) throws IllegalAccessException;

    public void defineDependentTypes(DependentTypes dependentTypes) throws BadRequestException;

    public void deleteDependentTypes(Long dependentId);

    public DataTablesResult<MedicalNetworks> findAllBenefitSections(DataTablesRequest request) throws IllegalAccessException;

    public void defineBenefitSections(MedicalNetworks medicalNetworks) throws BadRequestException;

    public void deleteBenefitSections(Long sectionId);

    public DataTablesResult<SpecialistFees> findAllSpecialistFees(DataTablesRequest request) throws IllegalAccessException;

    public void defineSpecialistFees(SpecialistFees specialistFees) throws BadRequestException;

    public void deleteSpecialistFees(Long specId);

    public DataTablesResult<MedActivities> findAllBenefits(DataTablesRequest request,Long serviceCode) throws IllegalAccessException;

    public void defineBenefits(MedActivities benefits) throws BadRequestException;

    public void deleteBenefits(Long benId);

    public DataTablesResult<FamilySizes> findFamilySize(DataTablesRequest request) throws IllegalAccessException;

    public void defineFamilySize(FamilySizes familySize) throws BadRequestException;

    public void deleteFamilySize(Long famId);

    public DataTablesResult<MedicalBinderRules> findMedBinderRules(DataTablesRequest request, Long bindCode) throws IllegalAccessException;

    public void defineBinderRules(MedicalBinderRules binderRules) throws BadRequestException;

    public void deleteBinderRules(Long ruleId);

    public DataTablesResult<Ailments> findAilments(DataTablesRequest request) throws IllegalAccessException;

    public void defineAilments(Ailments ailments) throws BadRequestException;

    public void deleteAilments(Long ailId);

    public DataTablesResult<BinderExclusions> findBinderExclusions(DataTablesRequest request, Long bindCode) throws IllegalAccessException;

    public void deleteBinderExclusions(Long beId);

    public void defineBinderExclusion(BinderExclusions binderExclusion) throws BadRequestException;

    public DataTablesResult<BinderLoadings> findBinderLoadings(DataTablesRequest request, Long bindCode) throws IllegalAccessException;

    public void deleteBinderLoadings(Long blId);

    public void defineBinderLoadings(BinderLoadings binderLoading) throws BadRequestException;

    public List<Object[]> getUnassignedLoadings(Long bindCode)
            throws IllegalAccessException;

    public List<Object[]> getUnassignedExclusions(Long bindCode, String subName)
            throws IllegalAccessException;
    public List<Object[]> getUnassignedNetworks(Long bindCode, String subName)
            throws IllegalAccessException;

    public List<Object[]> getUnassignedClauseExclusions(Long bindCode, String subName)
            throws IllegalAccessException;

    public void createLoadings(AilmentsBean ailmentsBean);

    public void createExclusions(AilmentsBean ailmentsBean);

    public List<LabTest> findUnassignedServices(Long providerId, String subName) throws IllegalAccessException ;
    public List<MedActivities> findUnassignedServiceActivities(Long serviceId, String subName) throws IllegalAccessException;

    public void defineBinderExclusions(BinderExclusions binderExclusions) throws BadRequestException;

    public void createProviderServices(providerServicesBean servicesBean);
    public void createProviderServiceActvty(providerServicesActivitiesBean activityBean);

    public void createNetworks(AilmentsBean ailmentsBean);

    public void createClauseExclusions(AilmentsBean ailmentsBean);

    public DataTablesResult<MedServiceProviderTypes> findServiceProviderTypes(DataTablesRequest request) throws IllegalAccessException;

    public void defineProviderTypes(MedServiceProviderTypes serviceProviderTypes) throws BadRequestException;

    public void deleteProviderTypes(Long providerId);

    public DataTablesResult<MedServiceProviders> findServiceProviders(DataTablesRequest request,Long providerId) throws IllegalAccessException;

    public void defineProviders(MedServiceProviders serviceProviders) throws BadRequestException;

    public void defineProviderSerces(ServiceProviderServices serviceProviderServices) throws BadRequestException;

    public void deleteProviderService(Long providerServiceId);

    public DataTablesResult<ServiceProviderServices> findProviderServices(DataTablesRequest request , Long providerId ) throws  IllegalAccessException;

    public DataTablesResult<ServiceProviderActivities> findServiceActivities(DataTablesRequest request, Long serviceId) throws IllegalAccessException;

    public void deleteProviders(Long providerId);

    public Page<MedServiceProviderTypes> findProviderTypesForSelect(String term, Pageable pageable);

    public Page<BankBranches> findBankBranchesForSelect(String term, Pageable pageable);

    public DataTablesResult<BinderProviders> findBinderProviders(DataTablesRequest request, Long bindCode) throws IllegalAccessException;
    public Page<MedServiceProviderTypes> findBinderProviderTypes(Long bincode, Pageable pageable);
    public Page<ServiceProviderContracts> findBinderProvidersByType(Long bincode, Long typeCode, Pageable pageable);

    public List<Object[]> getUnassignedProviders(Long bindCode)
            throws IllegalAccessException;

    public void createProviders(ProviderBean providerBean);

    public DataTablesResult<CoverLimits> findCoverLimits(DataTablesRequest request,Long medId) throws IllegalAccessException;

    public void defineCoverLimits(CoverLimits coverLimit) throws BadRequestException;

    public void deleteCoverLimits(Long limitId);

    public void deleteProvideContract(Long spcId);

    public DataTablesResult<SubLimits> findCoverSubLimits(DataTablesRequest request,Long limitId) throws IllegalAccessException;

     void importSubLimits(SubLimitsImport subLimitsImport) throws BadRequestException,IOException;

    public void deleteSubLimits(Long subLimitId);

    public DataTablesResult<MedicalEvents> findAllEvents(DataTablesRequest request) throws IllegalAccessException;

    public void defineEvents(MedicalEvents events) throws BadRequestException;

    public void deleteEvents(Long eventId);

    public Page<MedServiceProviders> findProviderForSelect(String term, Pageable pageable);

    public DataTablesResult<ServiceProviderContracts> findAllProviderContracts(DataTablesRequest request, Long bindCode) throws IllegalAccessException;

    public void defineProviderContract(ServiceProviderContracts providerContract) throws BadRequestException;

    public void deleteProviderContract(Long contractId);

    public DataTablesResult<Ailments> findAilmentDiagnosis(DataTablesRequest request,Long ailId) throws IllegalAccessException;

    public Page<MedicalNetworks> findNetworksForSelect(String term, Pageable pageable);

    public Page<Ailments> findAilmentsForSelect(String term, Pageable pageable);

    public Page<MedActivities> findActivitiesForSelect(String term, Pageable pageable, long labid);

    public Page<LabTest> findServicesForSelect(String term,Pageable pageable);

    public Page<SectionsDef> findMedicalSections(String paramString, Pageable paramPageable);

}
