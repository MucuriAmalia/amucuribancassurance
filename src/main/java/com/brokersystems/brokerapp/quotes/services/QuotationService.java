package com.brokersystems.brokerapp.quotes.services;

import com.brokersystems.brokerapp.medical.model.RulesBean;
import com.brokersystems.brokerapp.quotes.dto.*;
import com.brokersystems.brokerapp.quotes.model.*;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.dto.ProspectsDTO;
import com.brokersystems.brokerapp.setup.model.BusinessSourceGroups;
import com.brokersystems.brokerapp.setup.model.BusinessSources;
import com.brokersystems.brokerapp.setup.model.PremRatesDef;
import com.brokersystems.brokerapp.setup.model.ProspectDef;
import com.brokersystems.brokerapp.uw.model.PolicyTaxBean;
import com.brokersystems.brokerapp.uw.model.RiskBean;
import com.brokersystems.brokerapp.uw.dtos.TaxesDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * Created by peter on 3/12/2017.
 */
public interface QuotationService {

    public DataTablesResult<QuoteTrans> findUserQuotes(DataTablesRequest request)
            throws IllegalAccessException;

    DataTablesResult<QuoteEnquireDTO> quoteEnquiry(DataTablesRequest request, Long clientCode, String quoteNumber, Long prsCode);

     DataTablesResult<QuoteProductDTO> quoteProductEnquiry(DataTablesRequest request, Long clientCode, String quoteNumber, Long productCode, Long agentCode, Long prsCode);
    public DataTablesResult<QuoteTrans> medQuoteEnquiry(DataTablesRequest request, Long clientCode, String quoteNumber, Long productCode, Long agentCode, Long prsCode);
    public QuoteDetailsDTO getQuotationDetails(long quoteId);

    public QuoteTrans getMedQuotationDetails(long quoteId);

    public long medCount(Long quoteId);

    public void defineCategoryRules(QuoteCategoryRules categoryRule) throws BadRequestException;

    public void createBinderRules(RulesBean rulesBean);

    public List<Object[]> getUnassignedBinderRules(Long catId) throws IllegalAccessException;

    public void deleteCategoryRules(Long ruleId);

    public void deleteQuotTax(Long quotTaxId);

    public void deleteQuotCategory(Long categoryId);

    QuoteTrans createQuotation(CreateQuoteDTO quoteTrans) throws BadRequestException;

    ComparisonQuotDTO compareQuot(ComparisonQuotDTO comparisonQuotDTO) throws BadRequestException;

    public List<QuoteProductDTO> findQuotProductsByQuoteId( Long quotCode);

    List<QuoteProTrans> findCompQuotProductsByQuoteId( Long quotCode);

    public QuoteTrans createMedQuotation(CreateQuoteDTO quoteTrans) throws BadRequestException;

    public Page<BusinessSourceGroups> findSourcesGroups(String paramString, Pageable paramPageable);

    public Page<BusinessSources> findBusinessSources(String paramString, Pageable paramPageable, Long srcGroupId);

    public void computeQuotPrem(Long quotCode) throws BadRequestException, IOException;

    public void populateTaxes(QuoteTrans quoteTrans) throws BadRequestException;

    public Set<QuotClausesDTO> getNewClauses(Long prodId) throws BadRequestException;

    void createClause(QuoteClausesBean clause) throws BadRequestException;

    public void populateClauses(QuoteTrans quoteTrans) throws BadRequestException;

    public DataTablesResult<QuoteProductDTO> findQuotProducts(DataTablesRequest request, Long quotCode);

    public void undoMakeReady(Long polCode) throws BadRequestException;

    DataTablesResult<QuoteRiskDTO> findProdRisks(DataTablesRequest request, Long qrCode) throws IllegalAccessException;

    DataTablesResult<QuotRiskPremItemsDTO> findQuotRiskLimits(DataTablesRequest request, Long riskCode) throws IllegalAccessException;

     void createQuotRiskSection(QuotRiskPremItemsDTO riskLimits) throws BadRequestException;

    DataTablesResult<QuotTaxesDTO> getProductTaxes(DataTablesRequest request, Long qrCode) throws IllegalAccessException;

    public DataTablesResult<QuoteClauseDTO> getProductClauses(DataTablesRequest request, Long qrCode);

    public void deleteQuotProducts(Long qrCode);

    public void deleteQuote(Long quoteCode) throws BadRequestException;

    public void deleteProductRisks(Long riskCode);

    public void deleteQuoteRiskSections(Long sectId);

    public List<PremRatesDef> getNewPremiumItems(Long detId, Long riskId, String searchVal);

    public void createRiskSections(RiskBean sections) throws BadRequestException;

    void createProductRisk(QuoteRiskTrans risk) throws BadRequestException, IOException;

    void createQuotProduct(CreateQuoteProductDTO quoteProduct) throws BadRequestException;

    public void MakeQuotReady(Long quoteCode) throws BadRequestException;

    void MakeCompQuotReady(Long quoteCode, Long quoteProductId) throws BadRequestException;

    public void authorizeQuote(Long quoteCode) throws BadRequestException;

    public void confirmQuote(Long quoteCode) throws BadRequestException;

    public void cancelQuote(Long quoteCode, String comments) throws BadRequestException;

    QuoteProspectResult convertQuoteProduct(ConvertQuotDTO convertQuotDTO) throws BadRequestException, IOException;

    QuoteProspectResult convertMedQuote(Long quoteProductCode) throws BadRequestException;

    Page<ProspectsDTO> findActiveProspects(String paramString, Pageable paramPageable);

    public ProspectDef getProspectDetails(Long id) throws BadRequestException;

    public void deleteCategoryBenefit(Long benefitId, Long polCode) throws BadRequestException;

    public void deleteCategoryFamDetails(Long familyId, Long polCode) throws BadRequestException;

    public void confirmQuoteWF(Long quoteCode) throws BadRequestException;

    public void updateFundBenefits(MedQuotCategoryBenefits benefits) throws BadRequestException;

    public void updateCategoryBenefits(MedQuotCategoryBenefits benefits) throws BadRequestException;

    public void cancelQuoteWF(Long quoteCode, String reason) throws BadRequestException;

    public void defineMedicalCategories(MedicalQuoteCategory medicalCategory) throws BadRequestException;

    public void saveQuotTaxes(MedicalQuoteTaxes taxes) throws BadRequestException;

    public List<Object[]> getUnassignQuotTaxes(Long polId) throws IllegalAccessException;

    public void createNewQuotTax(TaxesDTO taxesDTO) throws BadRequestException;

    public List<FamilySizes> familySize(Long quotCode) throws BadRequestException, FileNotFoundException, IOException;

    public List<AgeBrackets> getAgeBrackets(Long quotCode) throws BadRequestException, FileNotFoundException, IOException;

    public void defineMedQuotFamDetails(MedQuotCatFamilyDetails medQuotCatFamilyDetails) throws BadRequestException;

    public DataTablesResult<MedQuotCatFamilyDetails> getCategoryFamDetails(DataTablesRequest request, Long catId) throws IllegalAccessException;

    public void deleteMedQuotFamDetails(Long familyId);

    public void populateCategoryClauses(QuoteTrans policy) throws BadRequestException;

    public void populateCategoryTaxes(QuoteTrans policy) throws BadRequestException;

    public DataTablesResult<MedQuotCategoryBenefits> getCategoryBenefits(DataTablesRequest request, Long catId) throws IllegalAccessException;

    public DataTablesResult<QuoteCategoryRules> getCategoryRules(DataTablesRequest request, Long catId) throws IllegalAccessException;

    public DataTablesResult<MedicalQuoteTaxes> findQuotTaxes(DataTablesRequest request, Long quotCode) throws IllegalAccessException;

    public DataTablesResult<MedicalQuoteCategory> findQuotCategories(DataTablesRequest request, Long quotCode) throws IllegalAccessException;

    public DataTablesResult<MedQuoteClauses> findQuotClauses(DataTablesRequest request, Long quotCode)
            throws IllegalAccessException;

    public Set<QuotTaxes> getNewTaxes(Long proQuoteId) throws BadRequestException;

    public void createQuoteTaxes(PolicyTaxBean taxBean) throws BadRequestException;

    public String getInhouseEmail() throws BadRequestException;

    Long reuseQuote(Long quotCode) throws BadRequestException;

    Long reviseQuote(Long quotCode) throws BadRequestException;

    public void deleteQuotGenTax(Long quotTaxId) throws BadRequestException, IOException;
    void editGenTax(Long polTaxId, BigDecimal divFactor, BigDecimal taxRate) throws BadRequestException, IOException;

    void createQuotClause(QuoteClausesDTO clause) throws BadRequestException;

    List<Object[]> compQuoteConvertSelect (Long quotId) throws IllegalAccessException;

    QuoteProductDTO getProductDetails(Long quotCode);

    List<Object[]> getCombineQuoteProduct(Long quotId) throws IllegalAccessException;
}
