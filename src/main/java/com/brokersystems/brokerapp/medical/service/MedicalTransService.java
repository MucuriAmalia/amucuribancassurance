package com.brokersystems.brokerapp.medical.service;

import com.brokersystems.brokerapp.medical.model.*;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.trans.model.SystemTransactions;
import com.brokersystems.brokerapp.uw.dtos.BinderDTO;
import com.brokersystems.brokerapp.uw.dtos.PolicyCreateDTO;
import com.brokersystems.brokerapp.uw.dtos.TaxesDTO;
import com.brokersystems.brokerapp.uw.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by peter on 4/26/2017.
 */
public interface MedicalTransService {

    @Transactional(readOnly = true)
    Page<BinderDTO> findMedicalInsuranceBinder(String paramString, Pageable pageable, String binType);

    public Page<BinderCardTypes> getBinderCardTypes(String paramString, Pageable paramPageable, Long binCode, Date wefDate) throws IllegalAccessException;

    public DataTablesResult<MedicalCategory> findPolicyCategories(DataTablesRequest request, Long polCode) throws IllegalAccessException;

    public DataTablesResult<MedicalCategory> findCategoryBedType(DataTablesRequest request, Long catId) throws IllegalAccessException;

    public void defineMedicalCategories(MedicalCategory medicalCategory) throws BadRequestException;
    public void defineMedicalCategoryBedType(MedicalCategory medicalCategory) throws BadRequestException;
    public void deleteMedicalCategory(Long catId);

    public DataTablesResult<CategoryFamilySize> getCategoryFamilySize(DataTablesRequest request, Long catId) throws IllegalAccessException;

    public Page<DependentTypes> findDependentTypes(String paramString, Pageable paramPageable);

    public void defineCategoryFamilySize(CategoryFamilySize familySize) throws BadRequestException;

    public void deleteFamSize(Long sizeId);

    public Page<BinderDetails> findBinderDetails(String paramString, Pageable paramPageable, Long bindCode);

    public DataTablesResult<MedCategoryBenefits> getCategoryBenefits(DataTablesRequest request, Long catId) throws IllegalAccessException;

    public void deleteCatBenefits(Long benId);

    public DataTablesResult<CategoryMembers> getCategoryMembers(DataTablesRequest request, Long catId) throws IllegalAccessException;

    public List<SubClassReqdDocs> findUnassignedMemberDocs(Long memberId, String docName) throws IllegalAccessException;

    public void defineMember(CategoryMembers member) throws BadRequestException;

    public void deleteMember(Long memberId) throws BadRequestException;

    public void undoDeleteMember(Long memberId) throws BadRequestException;

    public void createMemberRequiredDocs(RequiredDocBean requiredDocBean);

    public DataTablesResult<CategoryRules> getCategoryRules(DataTablesRequest request, Long catId) throws IllegalAccessException;

    public DataTablesResult<MedicalCategory> getCategoryBedTypes(DataTablesRequest request, Long catId) throws IllegalAccessException;

    public void defineCategoryRules(CategoryRules categoryRule) throws BadRequestException;

    public void deleteCategoryRules(Long ruleId);

    public void deletePolCategory(Long categoryId) throws BadRequestException;

    public void deletePolTaxes(Long polTaxId) throws BadRequestException;

    public void savePolTaxes(PolicyTaxes taxes) throws BadRequestException;

    public Page<ClientDef> findCategoryPrincipals(String paramString, Pageable paramPageable,Long catId);

    public DataTablesResult<CategoryClauses> getCategoryClauses(DataTablesRequest request, Long catId) throws IllegalAccessException;

    public void populateCategoryClauses(PolicyTrans policy) throws BadRequestException;

    public void populateCategoryTaxes(PolicyTrans policy) throws BadRequestException;

    public DataTablesResult<CategoryLoadings> getCategoryLoadings(DataTablesRequest request, Long catId) throws IllegalAccessException;

    public DataTablesResult<CategoryExclusions> getCategoryExclusions(DataTablesRequest request, Long catId) throws IllegalAccessException;

    public void populateCategoryLoadings(PolicyTrans policy) throws BadRequestException;

    public void populateCategoryExclusions(PolicyTrans policy) throws BadRequestException;

    public void populateCategoryProviders(PolicyTrans policy) throws BadRequestException;

    public void saveCategoryLoadings(CategoryLoadings loadings) throws BadRequestException;

    public void savePolicyDetails(PolicyCreateDTO policy) throws BadRequestException;

    public DataTablesResult<CatalogueProviders> getCategoryProviders(DataTablesRequest request, Long catId) throws IllegalAccessException;

    public Page<CoverLimits> findCoverLimits(String paramString, Pageable paramPageable, Long covId) throws BadRequestException;

    public void updateCategoryBenefits(MedCategoryBenefits benefits) throws BadRequestException;

    public void updateFundBenefits(MedCategoryBenefits benefits ) throws BadRequestException;

    public Page<ServiceProviderContracts> findProviderContractsForSelect(String term, Pageable pageable);

    public DataTablesResult<SelfFundParams> findSelfFundParams(DataTablesRequest request, Long polCode) throws IllegalAccessException;

    public void defineSelfFundParams(SelfFundParams  fundParams) throws BadRequestException;

    public void deleteSelfFundParams(Long fundParamId) throws BadRequestException;
    public List<Object[]> getUnassignPolTaxes(Long polId) throws IllegalAccessException;

    public List<Object[]> getUnassignedBinderRules(Long catId) throws IllegalAccessException;

    public void createBinderRules(RulesBean rulesBean);

    public DataTablesResult<CategoryMembers> getSchemeMembers(DataTablesRequest request,Long polCode) throws IllegalAccessException;

    public DataTablesResult<CategoryMembers> getPrincipalInfo(DataTablesRequest request,Long memberId) throws IllegalAccessException;

    public DataTablesResult<CategoryMembers> getDependantsInfo(DataTablesRequest request,Long memberId) throws IllegalAccessException;

    String uploadMembers(MemberUploadForm uploadForm, Long polCode, HttpServletRequest request) throws BadRequestException;

    public List<Object[]> getUnassignedCovers(Long catId, Long bindCode) throws IllegalAccessException;

    public void createCategoryBenefits(MedicalCoverDTO coverDTO) throws BadRequestException;
    public void createNewPolTax(TaxesDTO taxesDTO) throws BadRequestException;

    public void deleteCategoryBenefit(Long benefitId,Long polCode) throws BadRequestException;

    public List<Object[]> getUnassignedBinderProviders(Long catId, Long bindCode) throws IllegalAccessException;

    public void saveCategoryProviders(ProviderBean providerBean) throws BadRequestException;

    public void deleteCategoryProviders(Long providerId) throws BadRequestException;

    public Set<PolicyClauses> getNewClauses(Long polId) throws BadRequestException;

    public void createClause(PolicyClausesBean clause) throws BadRequestException;

    public Page<BedTypes> findBedTypes(String paramString, Pageable paramPageable);

    public DataTablesResult<SystemTransactions> getFundReceipts(DataTablesRequest request, Long polCode) throws IllegalAccessException;

    public String getFamilyDetails(Long memberId);

    public CategoryMembers getCategoryMemberDetails(Long catId);

    public BigDecimal getTotalReceiptedAmt(Long polCode);

    void processClaimBatchTrans(MedicalTransDTO transDTO) throws BadRequestException;

    public DataTablesResult<RiskDocs> findMemberDocs(DataTablesRequest request, Long memberId)
            throws IllegalAccessException;


}
