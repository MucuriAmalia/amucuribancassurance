package com.brokersystems.brokerapp.quotes.services.impl;

import com.brokersystems.brokerapp.audit.DeletedTransAudit;
import com.brokersystems.brokerapp.audit.repository.DeletedTransAuditRepo;
import com.brokersystems.brokerapp.enums.RevenueItems;
import com.brokersystems.brokerapp.enums.SectionTypes;
import com.brokersystems.brokerapp.kie.rules.ClientRulesExecutor;
import com.brokersystems.brokerapp.medical.model.*;
import com.brokersystems.brokerapp.medical.repository.*;
import com.brokersystems.brokerapp.quotes.dto.*;
import com.brokersystems.brokerapp.quotes.model.FamilySizes;
import com.brokersystems.brokerapp.quotes.model.*;
import com.brokersystems.brokerapp.quotes.repository.*;
import com.brokersystems.brokerapp.quotes.services.MedComputeQuotPrem;
import com.brokersystems.brokerapp.quotes.services.QuotationService;
import com.brokersystems.brokerapp.security.CheckAuthLimits;
import com.brokersystems.brokerapp.security.DefaultPlatformObjectEncoder;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.*;
import com.brokersystems.brokerapp.setup.dto.ProspectsDTO;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.*;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.brokersystems.brokerapp.trans.model.SystemTrans;
import com.brokersystems.brokerapp.trans.repository.SystemTransRepo;
import com.brokersystems.brokerapp.trans.repository.TransChecksRepo;
import com.brokersystems.brokerapp.users.repository.MakerCheckerRepo;
import com.brokersystems.brokerapp.users.service.MakerCheckerService;
import com.brokersystems.brokerapp.uw.dtos.TaxesDTO;
import com.brokersystems.brokerapp.uw.model.*;
import com.brokersystems.brokerapp.uw.repository.*;
import com.brokersystems.brokerapp.uw.service.PremComputeService;
import com.brokersystems.brokerapp.workflow.docs.DocType;
import com.brokersystems.brokerapp.workflow.repository.SysWfDocsRepo;
import com.brokersystems.brokerapp.workflow.utils.WorkflowService;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.BooleanExpression;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by peter on 3/12/2017.
 */
@Service
public class QuotationServiceImpl implements QuotationService {

    @Autowired
    private QuotTransRepo quotTransRepo;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private SequenceRepository sequenceRepo;

    @Autowired
    private DefaultPlatformObjectEncoder encoder;

    @Autowired
    private DateUtilities dateUtils;

    @Autowired
    private ClientTitleRepo clientTitleRepo;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private MedQuoteTransRepo medQuoteTransRepo;

    @Autowired
    private BindersRepo binderRepo;

    @Autowired
    private ProductsRepo productRepo;

    @Autowired
    private CoverTypesRepo coverRepo;

    @Autowired
    private SubClassRepo subclassRepo;

    @Autowired
    private ClientRepository clientRepo;

    @Autowired
    private OrgBranchRepository branchRepository;

    @Autowired
    private BinderDetRepo binderDetRepo;

    @Autowired
    private SectionRepo setupSectionRepo;

    @Autowired
    private PremRatesRepo premRatesRepo;

    @Autowired
    private QuotProductsRepo quotProductsRepo;

    @Autowired
    private DeletedTransAuditRepo deletedTransAuditRepo;

    @Autowired
    private SysWfDocsRepo sysWfDocsRepo;

    @Autowired
    private QuotRiskRepo quotRiskRepo;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ProspectsRepo prospectsRepo;

    @Autowired
    private MedQuotCategoryBenefitsRepo benefitRepo;

    @Autowired
    private CategoryBenefitRepo polbenefitRepo;


    @Autowired
    private QuotRiskLimitsRepo quotRiskLimitsRepo;

    @Autowired
    private SectionTransRepo sectionRepo;

    @Autowired
    private RiskTransRepo riskRepo;

    @Autowired
    private PolicyTransRepo policyRepo;

    @Autowired
    private PolActiveRisksRepo activeRisksRepo;

    @Autowired
    private SystemTransRepo transRepo;

    @Autowired
    private CurrencyRepository currencyRepo;

    @Autowired
    private PaymentModeRepo paymentModeRepo;

    @Autowired
    private OrgBranchRepository branchRepo;

    @Autowired
    private QuotClausesRepo quotClausesRepo;

    @Autowired
    private BinderClauseRepo binderClauseRepo;

    @Autowired
    private SubClausesRepo clausesRepo;

    @Autowired
    private QuotTaxesRepo quotTaxesRepo;

    @Autowired
    private TaxRatesRepo taxRatesRepo;

    @Autowired
    private QuoteCategoryRulesRepo categoryRulesRepo;

    @Autowired
    private CategoryRulesRepo polcategoryRulesRepo;

    @Autowired
    private BusinessSourceGroupsRepo sourceGroupsRepo;

    @Autowired
    private BusinessSourcesRepo sourcesRepo;

//    @Autowired
//    private MedQuotClausesRepo clausesRepo;

    @Autowired
    private MedicalQuoteTaxesRepo polTaxesRepo;

    @Autowired
    private PremComputeService premComputeService;

    @Autowired
    private CheckAuthLimits authLimits;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private TemplateMerger templateMerger;

    @Autowired
    private MedicalRulesRepo medicalRulesRepo;

    @Autowired
    private ParamService paramService;

    @Autowired
    private MedicalQuoteCategoryRepo medicalQuoteCategoryRepo;

    @Autowired
    private MedicalRulesRepo rulesRepo;

    @Autowired
    private MedicalQuotCoversRepo medicalCoversRepo;

    @Autowired
    private CoverLimitsRepo coverLimitsRepo;

    @Autowired
    private MedQuotClausesRepo medQuotClausesRepo;

    @Autowired
    private MedComputeQuotPrem medComputeQuotPrem;

    @Autowired
    private RiskDocsRepo riskDocsRepo;

    @Autowired
    private SubclassReqDocRepo reqDocRepo;

    @Autowired
    private BinderRatesTblRepo ratesTblRepo;

    @Autowired
    private ClientRulesExecutor clientRulesExecutor;

    @Autowired
    private MedQuotCatFamilyDetailsRepo medQuotCatFamilyDetailsRepo;

    @Autowired
    private MedicalExcelUtils excelUtils;

    @Autowired
    private PremExcelUtils premExcelUtils;

    @Autowired
    private PremRatesTableRepo premRatesTableRepo;

    @Autowired
    private MedicalCategoryRepo medicalCategoryRepo;

    @Autowired
    private MedicalQuoteTaxesRepo medicalQuoteTaxesRepo;

    @Autowired
    private PolUnassignedTaxesRepo unassignedTaxesRepo;

    @Autowired
    private TransChecksRepo transChecksRepo;

    @Autowired
    private PolClausesRepo polClausesRepo;

    @Autowired
    private BinderReqrdDocsRepo reqrdDocsRepo;

    @Autowired
    private ClientDocsRepo clientDocsRepo;

    @Autowired
    BinderMedicalCardsRepo binderMedicalCardsRepo;
    @Autowired
    private MakerCheckerService makerCheckerService;
    @Autowired
    private MakerCheckerRepo makerCheckerRepo;
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<QuoteTrans> findUserQuotes(DataTablesRequest request) throws IllegalAccessException {
        BooleanExpression pred = QQuoteTrans.quoteTrans.preparedBy.eq(userUtils.getCurrentUser()).and((QQuoteTrans.quoteTrans.quotStatus.in("D", "R", "A", "C"))).and(QQuoteTrans.quoteTrans.quoteProTranses.any().convertedReference.isNull());
        Page<QuoteTrans> page = quotTransRepo.findAll(pred.and(request.searchPredicate(QQuoteTrans.quoteTrans)), request);
        return new DataTablesResult<>(request, page);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<BusinessSources> findBusinessSources(String paramString, Pageable paramPageable, Long srcGroupId) {
        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred = QBusinessSources.businessSources.businessSourceGroup.srcGroupId.eq(srcGroupId).and(QBusinessSources.businessSources.isNotNull());

        } else {
            pred = QBusinessSources.businessSources.businessSourceGroup.srcGroupId.eq(srcGroupId).and(QBusinessSources.businessSources.shtDesc.containsIgnoreCase(paramString)
                    .or(QBusinessSources.businessSources.desc.containsIgnoreCase(paramString)));
        }
        return sourcesRepo.findAll(pred, paramPageable);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<QuoteEnquireDTO> quoteEnquiry(DataTablesRequest request, Long clientCode, String quoteNumber, Long prsCode) {
        if (quoteNumber == null || StringUtils.isBlank(quoteNumber)) {
            quoteNumber = "%%";
        } else {
            quoteNumber = "%" + quoteNumber + "%";
        }
        if (clientCode == null) {
            clientCode = -2000L;
        }
        if (prsCode == null) {
            prsCode = -2000L;
        }
        System.out.println("Client Code " + clientCode + " PRS Code " + prsCode + " quoteNumber " + quoteNumber);
        List<Object[]> quotes = quotTransRepo.enquireQuotes(quoteNumber, clientCode, prsCode, request.getPageNumber(), request.getPageSize());
        final List<QuoteEnquireDTO> enquireDTOList = new ArrayList<>();
        long rowCount = 0l;
        if (!quotes.isEmpty()) rowCount = ((BigInteger) quotes.get(0)[10]).intValue();
        for (Object[] enquire : quotes) {
            QuoteEnquireDTO quote = new QuoteEnquireDTO();
            quote.setQuoteId(((BigInteger) enquire[0]).longValue());
            quote.setQuotNo((String) enquire[1]);
            quote.setQuotRevNo((String) enquire[2]);
            quote.setWefDate((Date) enquire[3]);
            quote.setWetDate((Date) enquire[4]);
            quote.setClient((String) enquire[5]);
            quote.setProduct((String) enquire[6]);
            quote.setCurIsoCode((String) enquire[7]);
            quote.setQuotStatus((String) enquire[8]);
            quote.setUsername((String) enquire[9]);
            enquireDTOList.add(quote);
        }
        Page<QuoteEnquireDTO> page = new PageImpl<>(enquireDTOList, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class, IOException.class})
    public List<FamilySizes> familySize(Long quotCode) throws BadRequestException, FileNotFoundException, IOException {
        if (quotCode == null) throw new BadRequestException("No Quote Details");
        QuoteTrans quoteTrans = quotTransRepo.findOne(quotCode);
        boolean fundPolicy = quoteTrans.getBinder().getFundBinder() != null && "Y".equalsIgnoreCase(quoteTrans.getBinder().getFundBinder());
        String tableRateType = "";
        if (quoteTrans.getMedicalCoverType() != null) {
            tableRateType = quoteTrans.getMedicalCoverType();
        }

        Iterable<BinderRatesTable> ratesTables = ratesTblRepo.findAll(QBinderRatesTable.binderRatesTable.binder.binId.eq(quoteTrans.getBinder().getBinId())
                .and(QBinderRatesTable.binderRatesTable.tableType.eq(tableRateType)));
        if (!fundPolicy)
            if (ratesTables.spliterator().getExactSizeIfKnown() == 0)
                throw new BadRequestException("Rates Table for the Binder has not been setup");
        Calendar cal = Calendar.getInstance();
        cal.set(1970, 01, 01);
        Date maxDate = cal.getTime();
        for (BinderRatesTable ratesTable : ratesTables) {
            if (ratesTable.getEffDate().after(maxDate)) {
                maxDate = ratesTable.getEffDate();
            }
        }
        BinderRatesTable rateTable = ratesTblRepo.findOne(QBinderRatesTable.binderRatesTable.binder.binId.eq(quoteTrans.getBinder().getBinId())
                .and(QBinderRatesTable.binderRatesTable.effDate.eq(maxDate))
                .and(QBinderRatesTable.binderRatesTable.tableType.eq(tableRateType)));
        if (!fundPolicy) {
            if (rateTable.getRate_table().length == 0)
                throw new BadRequestException("Rates Table for the Binder has not been setup");
            try (
                    ByteArrayInputStream in = new ByteArrayInputStream(rateTable.getRate_table());
                    FileOutputStream out = new FileOutputStream(rateTable.getFileName())) {
                IOUtils.copy(in, out);
            }
        }

        if (!fundPolicy) {
            try (FileInputStream inputStream = new FileInputStream(rateTable.getFileName())) {
                if (inputStream != null) {
                    return excelUtils.getSetFamilysize("INPATIENT", inputStream);
                }
            }
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BusinessSourceGroups> findSourcesGroups(String paramString, Pageable paramPageable) {

        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred = QBusinessSourceGroups.businessSourceGroups.isNotNull();
        } else {
            pred = QBusinessSourceGroups.businessSourceGroups.desc.containsIgnoreCase(paramString)
                    .or(QBusinessSourceGroups.businessSourceGroups.shtDesc.containsIgnoreCase(paramString));
        }
        return sourceGroupsRepo.findAll(pred, paramPageable);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class, IOException.class})
    public List<AgeBrackets> getAgeBrackets(Long quotCode) throws BadRequestException, FileNotFoundException, IOException {
        if (quotCode == null) throw new BadRequestException("No Quote Details");
        QuoteTrans quoteTrans = quotTransRepo.findOne(quotCode);
        boolean fundPolicy = quoteTrans.getBinder().getFundBinder() != null && "Y".equalsIgnoreCase(quoteTrans.getBinder().getFundBinder());
        String tableRateType = "";
        if (quoteTrans.getMedicalCoverType() != null) {
            tableRateType = quoteTrans.getMedicalCoverType();
        }

        Iterable<BinderRatesTable> ratesTables = ratesTblRepo.findAll(QBinderRatesTable.binderRatesTable.binder.binId.eq(quoteTrans.getBinder().getBinId())
                .and(QBinderRatesTable.binderRatesTable.tableType.eq(tableRateType)));
        if (!fundPolicy)
            if (ratesTables.spliterator().getExactSizeIfKnown() == 0)
                throw new BadRequestException("Rates Table for the Binder has not been setup");
        Calendar cal = Calendar.getInstance();
        cal.set(1970, 01, 01);
        Date maxDate = cal.getTime();
        for (BinderRatesTable ratesTable : ratesTables) {
            if (ratesTable.getEffDate().after(maxDate)) {
                maxDate = ratesTable.getEffDate();
            }
        }
        BinderRatesTable rateTable = ratesTblRepo.findOne(QBinderRatesTable.binderRatesTable.binder.binId.eq(quoteTrans.getBinder().getBinId())
                .and(QBinderRatesTable.binderRatesTable.effDate.eq(maxDate))
                .and(QBinderRatesTable.binderRatesTable.tableType.eq(tableRateType)));
        if (!fundPolicy) {
            if (rateTable.getRate_table().length == 0)
                throw new BadRequestException("Rates Table for the Binder has not been setup");
            try (
                    ByteArrayInputStream in = new ByteArrayInputStream(rateTable.getRate_table());
                    FileOutputStream out = new FileOutputStream(rateTable.getFileName())) {
                IOUtils.copy(in, out);
            }
        }

        if (!fundPolicy) {
            try (FileInputStream inputStream = new FileInputStream(rateTable.getFileName())) {
                if (inputStream != null) {
                    return excelUtils.getSetAgeBrackets("INPATIENT", inputStream);
                }
            }
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<QuoteProductDTO> quoteProductEnquiry(DataTablesRequest request, Long clientCode, String quoteNumber, Long productCode, Long agentCode, Long prsCode) {
        if (quoteNumber == null || StringUtils.isBlank(quoteNumber)) {
            quoteNumber = "%%";
        } else {
            quoteNumber = "%" + quoteNumber + "%";
        }
        if (clientCode == null) clientCode = -2000L;
        if (productCode == null) productCode = -2000L;
        if (agentCode == null) agentCode = -2000L;
        if (prsCode == null) prsCode = -2000L;
        List<Object[]> quotesProducts = quotProductsRepo.enquireQuotes(quoteNumber, clientCode, prsCode, productCode, agentCode, request.getPageNumber(), request.getPageSize());
        final List<QuoteProductDTO> enquireDTOList = new ArrayList<>();
        long rowCount = 0l;
        if (!quotesProducts.isEmpty()) rowCount = ((BigInteger) quotesProducts.get(0)[19]).intValue();
        for (Object[] enquire : quotesProducts) {
            QuoteProductDTO quote = new QuoteProductDTO();
            quote.setQuoteProductId(((BigInteger) enquire[0]).longValue());
            quote.setQuoteId(((BigInteger) enquire[1]).longValue());
            quote.setQuoteNo(((String) enquire[2]));
            quote.setProduct(((String) enquire[3]));
            quote.setWef(((Date) enquire[4]));
            quote.setWet(((Date) enquire[5]));
            final String clientType = (String) enquire[10];
            if (clientType != null && clientType.equalsIgnoreCase("P")) {
                quote.setFname((String) enquire[6]);
                quote.setOtherNames((String) enquire[7]);
                quote.setTenId(((BigInteger) enquire[8]).longValue());
                quote.setProspShtDesc((String) enquire[9]);
            } else if (clientType != null && clientType.equalsIgnoreCase("C")) {
                quote.setFname((String) enquire[11]);
                quote.setOtherNames((String) enquire[12]);
                quote.setTenId(((BigInteger) enquire[13]).longValue());
            }
            quote.setCurrency((String) enquire[14]);
            quote.setConverted((String) enquire[15]);
            if (enquire[16] != null) {
                quote.setPolId(((BigInteger) enquire[16]).longValue());
                quote.setPolicyNo(((String) enquire[17]));
            }
            quote.setInsuranceCompany(((String) enquire[18]));
            enquireDTOList.add(quote);
        }
        Page<QuoteProductDTO> page = new PageImpl<>(enquireDTOList, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public QuoteProductDTO getProductDetails(Long quotCode) {
        QuoteTrans quoteTrans = quotTransRepo.findByQuoteId(quotCode);
        if (quoteTrans.getQuoteType().equalsIgnoreCase("combined")) {
            List<Object[]> quotesProducts = quotProductsRepo.getProductDetails(quotCode);
            for (Object[] enquire : quotesProducts) {
                QuoteProductDTO quote = new QuoteProductDTO();
                quote.setQuoteProductId(((BigInteger) enquire[0]).longValue());
                quote.setQuoteId(((BigInteger) enquire[1]).longValue());
                quote.setQuoteNo(((String) enquire[2]));
                quote.setProduct(((String) enquire[3]));
                quote.setWef(((Date) enquire[4]));
                quote.setWet(((Date) enquire[5]));
                final String clientType = (String) enquire[10];
                if (clientType != null && clientType.equalsIgnoreCase("P")) {
                    quote.setFname((String) enquire[6]);
                    quote.setOtherNames((String) enquire[7]);
                    quote.setTenId(((BigInteger) enquire[8]).longValue());
                    quote.setProspShtDesc((String) enquire[9]);
                } else if (clientType != null && clientType.equalsIgnoreCase("C")) {
                    quote.setFname((String) enquire[11]);
                    quote.setOtherNames((String) enquire[12]);
                    quote.setTenId(((BigInteger) enquire[13]).longValue());
                }
                quote.setCurrency((String) enquire[14]);
                quote.setConverted((String) enquire[15]);
                if (enquire[16] != null) {
                    quote.setPolId(((BigInteger) enquire[16]).longValue());
                    quote.setPolicyNo(((String) enquire[17]));
                }
                quote.setQuotType((String) enquire[19]);
                return quote;
            }
        } else if (quoteTrans.getQuoteType().equalsIgnoreCase("comparison")) {
            List<Object[]> quotesProducts = quotProductsRepo.getCompConvertProductDetails(quotCode);
            for (Object[] enquire : quotesProducts) {
                QuoteProductDTO quote = new QuoteProductDTO();
                quote.setQuoteProductId(((BigInteger) enquire[0]).longValue());
                quote.setQuoteId(((BigInteger) enquire[1]).longValue());
                quote.setQuoteNo(((String) enquire[2]));
                quote.setProduct(((String) enquire[3]));
                quote.setWef(((Date) enquire[4]));
                quote.setWet(((Date) enquire[5]));
                final String clientType = (String) enquire[10];
                if (clientType != null && clientType.equalsIgnoreCase("P")) {
                    quote.setFname((String) enquire[6]);
                    quote.setOtherNames((String) enquire[7]);
                    quote.setTenId(((BigInteger) enquire[8]).longValue());
                    quote.setProspShtDesc((String) enquire[9]);
                } else if (clientType != null && clientType.equalsIgnoreCase("C")) {
                    quote.setFname((String) enquire[11]);
                    quote.setOtherNames((String) enquire[12]);
                    quote.setTenId(((BigInteger) enquire[13]).longValue());
                }
                quote.setCurrency((String) enquire[14]);
                quote.setConverted((String) enquire[15]);
                if (enquire[16] != null) {
                    quote.setPolId(((BigInteger) enquire[16]).longValue());
                    quote.setPolicyNo(((String) enquire[17]));
                }
                quote.setQuotType((String) enquire[19]);
                return quote;
            }
        }
        return new QuoteProductDTO();
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<QuoteTrans> medQuoteEnquiry(DataTablesRequest request, Long clientCode, String quoteNumber,
                                                        Long productCode, Long agentCode, Long prsCode) {
        QClientDef client = QQuoteTrans.quoteTrans.client;
        QProductsDef product = QQuoteTrans.quoteTrans.product;
        QProspectDef prospect = QQuoteTrans.quoteTrans.prospect;
        if (quoteNumber == null || StringUtils.isBlank(quoteNumber)) {
            quoteNumber = "";
        }
        Predicate pred = QQuoteTrans.quoteTrans.quotNo.containsIgnoreCase(quoteNumber)
                .and(QQuoteTrans.quoteTrans.quotStatus.in("CV", "C"))
                .and((clientCode == null) ? client.isNotNull().or(client.isNull()) : client.tenId.eq(clientCode))
                .and((prsCode == null) ? prospect.isNull().or(prospect.isNotNull()) : prospect.tenId.eq(prsCode))
                .and((productCode == null) ? product.isNotNull() : product.proCode.eq(productCode));
        Page<QuoteTrans> page = quotTransRepo.findAll(pred, request);
        return new DataTablesResult(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public QuoteDetailsDTO getQuotationDetails(long quoteId) {
        List<Object[]> quotes = quotTransRepo.getQuoteDetails(quoteId);
        if (quotes.size() != 1) return new QuoteDetailsDTO();
        for (Object[] enquire : quotes) {
            QuoteDetailsDTO quote = new QuoteDetailsDTO();
            quote.setQuoteId(((BigInteger) enquire[0]).longValue());
            quote.setQuotStatus((String) enquire[1]);
            final String clientType = (String) enquire[4];
            quote.setClientType(clientType);
            if (clientType != null && clientType.equalsIgnoreCase("P")) {
                quote.setFname((String) enquire[2]);
                quote.setOtherNames((String) enquire[3]);
                quote.setTenId(((BigInteger) enquire[7]).longValue());
            } else if (clientType != null && clientType.equalsIgnoreCase("C")) {
                quote.setFname((String) enquire[5]);
                quote.setOtherNames((String) enquire[6]);
                quote.setTenId(((BigInteger) enquire[8]).longValue());
                quote.setIdNo((String) enquire[35]);
            }
            quote.setPmId(((BigInteger) enquire[9]).longValue());
            quote.setPaymentMode((String) enquire[10]);
            if (enquire[11] != null) {
                quote.setSourceId(((BigInteger) enquire[11]).longValue());
                quote.setSourceName((String) enquire[12]);
            }
            if (enquire[13] != null) {
                quote.setSourceGroupId(((BigInteger) enquire[13]).longValue());
                quote.setSourceGroupName((String) enquire[14]);
            }
            if (enquire[15] != null) {
                quote.setObId(((BigInteger) enquire[15]).longValue());
                quote.setBranch((String) enquire[16]);
            }
            if (enquire[17] != null) {
                quote.setCurCode(((BigInteger) enquire[17]).longValue());
                quote.setCurrency((String) enquire[18]);
            }
            quote.setQuotNo((String) enquire[19]);
            quote.setQuotRevNo((String) enquire[20]);
            quote.setSumInsured((BigDecimal) enquire[21]);
            quote.setPremium((BigDecimal) enquire[22]);
            quote.setBasicPrem((BigDecimal) enquire[23]);
            quote.setNetPrem((BigDecimal) enquire[24]);
            quote.setCommAmt((BigDecimal) enquire[25]);
            quote.setTrainingLevy((BigDecimal) enquire[26]);
            quote.setPhcf((BigDecimal) enquire[27]);
            quote.setStampDuty((BigDecimal) enquire[28]);
            if (enquire[29] != null) {
                quote.setWhtx((BigDecimal) enquire[29]);
            } else {
                quote.setWhtx(BigDecimal.ZERO);
            }
            quote.setExtras((BigDecimal) enquire[30]);
            quote.setQuoteWef((Date) enquire[31]);
            quote.setQuoteWet((Date) enquire[32]);
            quote.setExpiryDate((Date) enquire[33]);
            quote.setQuoteType((String) enquire[34]);

            if (enquire[36] != null) {
                quote.setSubAgentId(((BigInteger) enquire[36]).longValue());
                quote.setSubAgent((String) enquire[37]);
            }
            if (enquire[38] != null) {
                quote.setIntroducerAgentId(((BigInteger) enquire[38]).longValue());
                quote.setIntroducerAgent((String) enquire[39]);
            }
            if (enquire[40] != null) {
                quote.setMarketerAgentId(((BigInteger) enquire[40]).longValue());
                quote.setMarketerAgent((String) enquire[41]);
            }
            if (enquire[42] != null) {
                quote.setLeadsManId(((BigInteger) enquire[42]).longValue());
                quote.setLeadsMan((String) enquire[43]);
            }
            quote.setAbsaNoIntroducer((String) enquire[44]);
            quote.setAbsaNoLeadsMan((String) enquire[45]);
            quote.setAbsaNoSubAgent((String) enquire[46]);
            quote.setAbsaNoMarketer((String) enquire[47]);
            return quote;
        }
        return new QuoteDetailsDTO();
    }

    @Override
    @Transactional(readOnly = true)
    public QuoteTrans getMedQuotationDetails(long quoteId) {
        return  quotTransRepo.findByQuoteId(quoteId);
        //return medQuoteTransRepo.findOne(quoteId);
    }


    @PreAuthorize("hasAnyAuthority('SAVE_QUOTE')")
    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public QuoteTrans createMedQuotation(CreateQuoteDTO quote) throws BadRequestException {
        if (quote.getClientType() == null || "C".equalsIgnoreCase(quote.getClientType()))
            if (quote.getClientId() == null) throw new BadRequestException("Client is Mandatory");
            else if ("P".equalsIgnoreCase(quote.getClientType()))
                if (quote.getClientId() == null) throw new BadRequestException("Prospect is Mandatory");
        if (quote.getBusinessType() == null) throw new BadRequestException("Period is Mandatory");
        if (quote.getBranchId() == null) throw new BadRequestException("Branch is Mandatory");
        if (quote.getBindCode() == null) throw new BadRequestException("Binder is Mandatory");
        if (quote.getMedicalCoverType() == null) throw new BadRequestException("Type is Mandatory");
        if (quote.getCurrencyId() == null) throw new BadRequestException("Currency is Mandatory");
        if (quote.getWefDate() == null) throw new BadRequestException("Policy Wef Date From is Mandatory");
        if (quote.getWetDate() == null) throw new BadRequestException("Policy Wet Date From is Mandatory");
//        if (quote.getSourceId() == null) throw new BadRequestException("Quotation source is Mandatory");
        Predicate cardPred = QBinderMedicalCards.binderMedicalCards.binder.binId.eq(quote.getBindCode());
        Long cardCount = binderMedicalCardsRepo.count(cardPred);
        if (quote.getWefDate().after(quote.getWetDate()))
            throw new BadRequestException("Wef Date cannot be greater than Wet Date");
        QuoteTrans quoteTrans = new QuoteTrans();
        org.springframework.beans.BeanUtils.copyProperties(quote, quoteTrans);
        if (cardCount != 0 && quote.getCardId() == null) {
            throw new BadRequestException("Card type is Mandatory");
        } else {
            if (cardCount == 0) quoteTrans.setBinCardType(null);
        }


        boolean newTrans = false;


        if (quoteTrans.getQuoteId() == null) {
            newTrans = true;
            Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("Q");
            String policyNumberFormat = paramService.getParameterString("QUOTE_NO_FORMAT");
            if (sequenceRepo.count(seqPredicate) == 0)
                throw new BadRequestException("Sequence for Quotations Transactions has not been defined");
            SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
            Long seqNumber = sequence.getNextNumber();
            final String policyNumber = templateMerger.generateFormat(policyNumberFormat, quote.getBranchId(), quote.getProdId(), quoteTrans.getWefDate(), String.format("%05d", seqNumber), null);

            //final String policyNumber = sequence.getSeqPrefix() + String.format("%05d", seqNumber);
            quoteTrans.setQuotNo(policyNumber);
            quoteTrans.setQuotRevNo(policyNumber + "/1");
            sequence.setLastNumber(seqNumber);
            sequence.setNextNumber(seqNumber + 1);
            sequenceRepo.save(sequence);
        }
        quoteTrans.setQuotStatus("D");
        if (quoteTrans.getQuoteId() == null) {
            int quotExpiryVal = paramService.getParamInt("QUOT_EXPIRY_VAL");
            Date quotWef = dateUtils.removeTime(quoteTrans.getWefDate());
            Date quotWet = dateUtils.removeTime(quoteTrans.getWetDate());
            if (quotWet.before(quotWef) || quotWef.after(quotWet)) {
                throw new BadRequestException("Cover Date from cannot be greater than cover Date to");
            }
            quoteTrans.setQuotDate(new Date());
            quoteTrans.setExpiryDate(DateUtils.addDays(new Date(), quotExpiryVal));
        }
        if (quoteTrans.getQuoteId() != null) {
            QuoteTrans existingQuote = quotTransRepo.findOne(quoteTrans.getQuoteId());
            quoteTrans.setQuotDate(existingQuote.getQuotDate());
            quoteTrans.setQuotNo(existingQuote.getQuotNo());
            quoteTrans.setQuotRevNo(existingQuote.getQuotRevNo());
            quoteTrans.setQuotStatus(existingQuote.getQuotStatus());
            quoteTrans.setExpiryDate(existingQuote.getExpiryDate());

        }
        quoteTrans.setBranch(branchRepo.findOne(quote.getBranchId()));
        quoteTrans.setSource(sourcesRepo.findOne(quote.getSourceId()));
        quoteTrans.setBinder(binderRepo.findOne(quote.getBindCode()));
        quoteTrans.setProduct(productRepo.findOne(quote.getProdId()));
        if (!(quote.getCardId() == null))
            quoteTrans.setBinCardType(binderMedicalCardsRepo.findOne(quote.getCardId()));
        if (quoteTrans.getClientType() == null || "C".equalsIgnoreCase(quoteTrans.getClientType()))
            quoteTrans.setClient(clientRepo.findOne(quote.getClientId()));
        else
            quoteTrans.setProspect(prospectsRepo.findOne(quote.getClientId()));
        quoteTrans.setPreparedBy(userUtils.getCurrentUser());
        quoteTrans.setTransCurrency(currencyRepo.findOne(quote.getCurrencyId()));
        quoteTrans.setPaymentMode(paymentModeRepo.findOne(quote.getPaymentId()));
        QuoteTrans savedTrans = medQuoteTransRepo.save(quoteTrans);
        if (!newTrans) {
            try {
                medComputeQuotPrem.computePrem(quoteTrans.getQuoteId());
            } catch (IOException ex) {
                throw new BadRequestException(ex.getMessage());
            }
        }
        if (newTrans) {
            //workflowService.startNewWorkFlow(DocType.QUOTATION_DOCUMENT, String.valueOf("Q"+savedTrans.getQuoteId()), null, "N", savedTrans, null, null);
        }
        return savedTrans;
    }

    @PreAuthorize("hasAnyAuthority('SAVE_QUOTE')")
    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public QuoteTrans createQuotation(CreateQuoteDTO quote) throws BadRequestException {
        System.out.println(new Gson().toJson(quote));
        if (quote.getClientType() == null || "C".equalsIgnoreCase(quote.getClientType()))
            if (quote.getClientId() == null) throw new BadRequestException("Client is Mandatory");
            else if ("P".equalsIgnoreCase(quote.getClientType()))
                if (quote.getClientId() == null) throw new BadRequestException("Prospect is Mandatory");
        if (quote.getPaymentId() == null) throw new BadRequestException("Payment Mode is Mandatory");
        if (quote.getBranchId() == null) throw new BadRequestException("Branch is Mandatory");
        if (quote.getCurrencyId() == null) throw new BadRequestException("Currency is Mandatory");
        if (quote.getClientId() == null) {
            throw new BadRequestException("Select either Prospect or Client to continue...");
        }


        final QuoteTrans quoteTrans = new QuoteTrans();
        org.springframework.beans.BeanUtils.copyProperties(quote, quoteTrans);
        final Date startDate = new Date();
        final Date endDate = DateUtils.addDays(DateUtils.addMonths(startDate, 12), -1);
        boolean newTrans = false;
        User user = userUtils.getCurrentUser();
        if (quoteTrans.getQuoteId() == null) {
            newTrans = true;
            Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("Q");
            String policyNumberFormat = paramService.getParameterString("QUOTE_NO_FORMAT");
            if (sequenceRepo.count(seqPredicate) == 0)
                throw new BadRequestException("Sequence for Quotations Transactions has not been defined");
            SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
            Long seqNumber = sequence.getNextNumber();
            final String policyNumber = templateMerger.generateFormat(policyNumberFormat, quote.getBranchId(), quote.getProdId(), startDate, String.format("%05d", seqNumber), null);
            //final String policyNumber = sequence.getSeqPrefix() + String.format("%05d", seqNumber);
            quoteTrans.setQuotNo(policyNumber);
            quoteTrans.setQuotRevNo(policyNumber + "/1");
            sequence.setLastNumber(seqNumber);
            sequence.setNextNumber(seqNumber + 1);
            sequenceRepo.save(sequence);
        }
        quoteTrans.setWefDate(startDate);
        quoteTrans.setWetDate(endDate);
        quoteTrans.setQuotStatus("D");


//        final Long hashCode = Long.parseLong(String.valueOf(quote.hashCode()));
//        System.out.println(new Gson().toJson(quote));
//        if (!isApproved && !makerCheckerRepo.exists(hashCode)) {
//            MakerCheckDTO makerCheckDTO = new MakerCheckDTO();
//            makerCheckDTO.setJson(new Gson().toJson(quote));
//            makerCheckDTO.setStatus("N");
//            if (Objects.equals(quote.getClientType(), "C")) {
//                ClientDef clientDef = clientRepo.findOne(quote.getClientId());
//                makerCheckDTO.setTaskName("Created Quote: Quotation No " + quoteTrans.getQuotNo() + " Client " + clientDef.getFname() + " " + clientDef.getOtherNames());
//            } else if (Objects.equals(quote.getClientType(), "P")) {
//                ProspectDef prospectDef = prospectsRepo.findOne(quote.getClientId());
//                makerCheckDTO.setTaskName("Created Quote: Quotation No " + quoteTrans.getQuotNo() + " Prospect " + prospectDef.getFname() + " " + prospectDef.getOtherNames());
//            }
//            makerCheckDTO.setTaskType("QT");
//            makerCheckDTO.setTaskCode(hashCode);
//            makerCheckerService.checkExists(makerCheckDTO);
//            makerCheckerService.createMakerChecker(makerCheckDTO);
//            return quoteTrans;
//        }

        RiskTransBean riskBean = quote.getRiskBean();
        System.out.println("Risk 001: " + quote.toString());
        QuoteProductBean productBean = quote.getQuoteProductBean();
        if (quoteTrans.getQuoteId() == null) {
            int quotExpiryVal = paramService.getParamInt("QUOT_EXPIRY_VAL");
            System.out.println(quote.getBindCode());
            System.out.println(productBean.getBindCode());
            QuoteProTrans quoteProTrans = new QuoteProTrans();
            if (productBean.getBindCode() == null) throw new BadRequestException("Binder is Mandatory");
            if (productBean.getAgentId() == null) throw new BadRequestException("Intermediary is Mandatory");
            if (productBean.getProdId() == null) throw new BadRequestException("Product is Mandatory");

            quoteProTrans.setAgent(accountRepo.findOne(productBean.getAgentId()));
            quoteProTrans.setBinder(binderRepo.findOne(productBean.getBindCode()));
            quoteProTrans.setProduct(productRepo.findOne(productBean.getProdId()));

            quoteProTrans.setWefDate(startDate);
            quoteProTrans.setWetDate(endDate);
            quoteProTrans.setQuoteTrans(quoteTrans);

            QuoteRiskTrans risk = new QuoteRiskTrans();
            if (riskBean.getBindCode() == null) throw new BadRequestException("Binder is Mandatory");
            if (riskBean.getSclCode() == null) throw new BadRequestException("Sub Class is Mandatory");
            if (riskBean.getCoverCode() == null) throw new BadRequestException("Cover Type is Mandatory");
            if (riskBean.getInsuredCode() == null) throw new BadRequestException("Insured is Mandatory");
            risk.setBinder(binderRepo.findOne(riskBean.getBindCode()));
            risk.setCovertype(coverRepo.findOne(riskBean.getCoverCode()));
            risk.setSubclass(subclassRepo.findOne(riskBean.getSclCode()));
            if (quoteTrans.getClientType() == null || "C".equalsIgnoreCase(quoteTrans.getClientType()))
                risk.setInsured(clientRepo.findOne(riskBean.getInsuredCode()));
            else
                risk.setProspect(prospectsRepo.findOne(riskBean.getInsuredCode()));
            risk.setBinderDetails(binderDetRepo.findOne(riskBean.getBinderDet()));
            risk.setCommRate(riskBean.getCommRate());
            risk.setProrata(riskBean.getProrata());
            risk.setRiskDesc(riskBean.getRiskDesc());
            risk.setRiskShtDesc(riskBean.getRiskShtDesc());
            risk.setWefDate(startDate);
            risk.setWetDate(endDate);
            risk.setProductTrans(quoteProTrans);
            List<QuotRiskLimits> sectionTransactions = new ArrayList<>();
            for (RiskSectionBean sectionbean : quote.getSections()) {
                SectionsDef sectiondef = setupSectionRepo.findOne(sectionbean.getSection());
                QuotRiskLimits section = new QuotRiskLimits();
                List<PremRatesDef> premRates = new ArrayList<>();
                if (sectionbean.getRatesApplicable() != null && "Y".equalsIgnoreCase(sectionbean.getRatesApplicable())) {
                    premRates = premRatesRepo.getSectPremiumRates(risk.getBinderDetails().getDetId(), sectionbean.getAmount(), sectiondef.getId());
                    if (premRates.size() == 0)
                        throw new BadRequestException("No Premium Rates for the Value entered...");
                    if (premRates.size() > 1)
                        throw new BadRequestException("Duplicate Prem Rates set up for the Section..." + sectionbean.getSectionDesc());
                    section.setPremRates(premRates.get(0));
                    section.setRate(premRates.get(0).getRate());
                } else {
                    premRates = premRatesRepo.getSectPremiumRates(risk.getBinderDetails().getDetId(), sectiondef.getId());
                    if (premRates.isEmpty())
                        throw new BadRequestException("No Premium Rates for the Value entered...");
                    if (premRates.size() > 1)
                        throw new BadRequestException("Duplicate Prem Rates set up for the Section..." + sectionbean.getSectionDesc());
                    section.setPremRates(premRates.get(0));
                    section.setRate(sectionbean.getRate());
                }
                if (premRates.get(0).getRangeApplicable().equals("Y")) {
                    BigDecimal rangeFrom = premRates.get(0).getRangeFrom();
                    BigDecimal rangeTo = premRates.get(0).getRangeTo();
                    if (premRates.get(0).getRangeType().equalsIgnoreCase("AM")) {
                        if (sectionbean.getAmount().compareTo(rangeFrom) < 0 || sectionbean.getAmount().compareTo(rangeTo) > 0) {
                            throw new BadRequestException(String.format("%s is not within the specified range of %s-%s", sectionbean.getAmount(), rangeFrom, rangeTo));
                        }
                    }
                }
                section.setAmount((sectionbean.getAmount() == null) ? BigDecimal.ZERO : sectionbean.getAmount());
                section.setCompute(sectionbean.isCompute());
                section.setDivFactor(sectionbean.getDivFactor());
                section.setFreeLimit(sectionbean.getFreeLimit());
                section.setCompute(true);

                section.setSection(sectiondef);
                section.setRisk(risk);
                sectionTransactions.add(section);
            }
            quotRiskLimitsRepo.save(sectionTransactions);
            quotRiskRepo.save(risk);
            quotProductsRepo.save(quoteProTrans);
            quoteTrans.setExpiryDate(DateUtils.addDays(new Date(), quotExpiryVal));
            quoteTrans.setQuotDate(new Date());
        }
        if (quoteTrans.getQuoteId() != null) {
            QuoteTrans existingQuote = quotTransRepo.findOne(QQuoteTrans.quoteTrans.quoteId.eq(quoteTrans.getQuoteId()));
            quoteTrans.setExpiryDate(existingQuote.getExpiryDate());
            quoteTrans.setQuotDate(existingQuote.getQuotDate());
            quoteTrans.setQuotNo(existingQuote.getQuotNo());
            quoteTrans.setQuotRevNo(existingQuote.getQuotRevNo());
            quoteTrans.setQuotStatus(existingQuote.getQuotStatus());
            if (existingQuote.getMedicalCoverType() != quoteTrans.getMedicalCoverType()) {
                Iterable<MedicalQuoteCategory> categories = medicalQuoteCategoryRepo.findAll(QMedicalQuoteCategory.medicalQuoteCategory.quotation.quoteId.eq(quoteTrans.getQuoteId()));

                if ("G".equalsIgnoreCase(quoteTrans.getMedicalCoverType())) {
                    for (MedicalQuoteCategory category : categories) {
                        category.setChildrenCount(null);
                        category.setPrincipalAge(null);
                        category.setSpouseAge(null);
                        category.setPrincipalGender(null);
                        medicalQuoteCategoryRepo.save(category);
                    }

                } else {
                    for (MedicalQuoteCategory category : categories) {
                        Iterable<MedQuotCatFamilyDetails> famDetails = medQuotCatFamilyDetailsRepo.findAll(QMedQuotCatFamilyDetails.medQuotCatFamilyDetails.category.id.eq(category.getId()));
                        for (MedQuotCatFamilyDetails familyDetails : famDetails) {
                            medQuotCatFamilyDetailsRepo.delete(familyDetails.getFamilyId());
                        }
                    }

                }
            }
        }
        quoteTrans.setBranch(branchRepo.findOne(quote.getBranchId()));
        if (quote.getSourceId() != null) {
            quoteTrans.setSource(sourcesRepo.findOne(quote.getSourceId()));
            BusinessSources sources = sourcesRepo.findOne(quote.getSourceId());
            if (sources.getBusinessSourceGroup().getDesc().equalsIgnoreCase("ACTIVATION CAMPAIGN")) {
                quoteTrans.setActivationCode(quote.getActivationCode());
            }
        }
        quoteTrans.setAbsaNoIntroducer(quote.getAbsaNoIntroducer());
        quoteTrans.setAbsaNoLeadsMan(quote.getAbsaNoLeadsMan());
        quoteTrans.setAbsaNoSubAgent(quote.getAbsaNoSubAgent());
        quoteTrans.setAbsaNoMarketer(quote.getAbsaNoMarketer());
        System.out.println("Sub agent id>>>>: " + quote.getSubAgentId());
        if (quote.getSubAgentId() != null) {
            quoteTrans.setSubAgent(accountRepo.findOne(quote.getSubAgentId()));
        }
        if (quote.getIntroducerAgentId() != null) {
            quoteTrans.setIntroducerAgent(accountRepo.findOne(quote.getIntroducerAgentId()));
        }
        if (quote.getMarketerAgentId() != null) {
            quoteTrans.setMarketerAgent(accountRepo.findOne(quote.getMarketerAgentId()));
        }
        if (quote.getLeadsManId() != null) {
            quoteTrans.setLeadsMan(userRepository.findOne(quote.getLeadsManId()));
        }
        if (quoteTrans.getClientType() == null || "C".equalsIgnoreCase(quoteTrans.getClientType()))
            quoteTrans.setClient(clientRepo.findOne(quote.getClientId()));
        else
            quoteTrans.setProspect(prospectsRepo.findOne(quote.getClientId()));
        quoteTrans.setPaymentMode(paymentModeRepo.findOne(quote.getPaymentId()));
        quoteTrans.setPreparedBy(user
        );
        quoteTrans.setTransCurrency(currencyRepo.findOne(quote.getCurrencyId()));
        if (quoteTrans.getQuoteId() != null) {
            Iterable<QuoteProTrans> quotProducts = quotProductsRepo.findAll(QQuoteProTrans.quoteProTrans.quoteTrans.quoteId.eq(quoteTrans.getQuoteId()));
            for (QuoteProTrans proTrans : quotProducts) {
                Iterable<QuoteRiskTrans> riskTrans = quotRiskRepo.findAll(QQuoteRiskTrans.quoteRiskTrans.productTrans.quoteProductId.eq(proTrans.getQuoteProductId()));
                for (QuoteRiskTrans quoteRiskTrans : riskTrans) {
                    if (quoteTrans.getClientType() == null || "C".equalsIgnoreCase(quoteTrans.getClientType())) {
                        quoteRiskTrans.setInsured(quoteTrans.getClient());
                        quoteRiskTrans.setProspect(null);
                    } else if ("P".equalsIgnoreCase(quoteTrans.getClientType())) {
                        quoteRiskTrans.setProspect(quoteTrans.getProspect());
                        quoteRiskTrans.setInsured(null);
                    }
                }
                quotRiskRepo.save(riskTrans);
            }
        }
        quoteTrans.setQuoteType(quote.getQuoteType());
        QuoteTrans savedTrans = quotTransRepo.save(quoteTrans);
        try {
            this.computeQuotPrem(savedTrans.getQuoteId());
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }
        if (newTrans) {
            workflowService.startNewWorkFlow(DocType.QUOTATION_DOCUMENT, String.valueOf("Q" + savedTrans.getQuoteId()), null, "N", savedTrans, null, null, null);
        }
        return savedTrans;
    }

    @Override
    @Transactional
    @Modifying
    public ComparisonQuotDTO compareQuot(ComparisonQuotDTO comparisonQuotDTO) throws BadRequestException {
        if (comparisonQuotDTO.getQuotId() == null) {
            throw new BadRequestException("A Quotation should be available to do comparison");
        }
        if (comparisonQuotDTO.getContractId().isEmpty()) {
            throw new BadRequestException("Contract should not be empty");
        }
        final QuoteTrans comparisonQuote = quotTransRepo.findOne(QQuoteTrans.quoteTrans.quoteId.eq(comparisonQuotDTO.getQuotId()));
        if (comparisonQuote.getQuoteType() != null && !comparisonQuote.getQuoteType().equalsIgnoreCase("comparison")) {
            throw new BadRequestException("This is not a comparison quote");
        }
        final List<QuoteProductDTO> productTrans = this.findQuotProductsByQuoteId(comparisonQuote.getQuoteId());
        if (productTrans.size() > 5) {
            throw new BadRequestException("A maximum of 5 products can be compared");
        }
        if (productTrans.isEmpty()) {
            throw new BadRequestException("Comparison quote must have at least one product");
        }
        final QuoteProductDTO productDTO = productTrans.get(0);
        for (Long contractId : comparisonQuotDTO.getContractId()) {
            BindersDef bindersDef = binderRepo.findOne(contractId);
            if (bindersDef == null) {
                throw new BadRequestException("Product binder cannot be empty");
            }
            final QuoteProTrans proTrans = new QuoteProTrans();
            proTrans.setWefDate(productDTO.getWef());
            proTrans.setWetDate(productDTO.getWet());
            proTrans.setQuoteTrans(comparisonQuote);
            proTrans.setAgent(bindersDef.getAccount());
            proTrans.setProduct(bindersDef.getProduct());
            proTrans.setBinder(bindersDef);
            QuoteProTrans savedProTrans = quotProductsRepo.save(proTrans);
            Iterable<Object[]> riskTrans = quotRiskRepo.findQuoteRiskTrans(productDTO.getQuoteProductId());
            for (Object[] quoteRiskTrans : riskTrans) {
                List<BigInteger> binDetId = binderDetRepo.getBinderId(((BigInteger) quoteRiskTrans[22]).longValue(), ((BigInteger) quoteRiskTrans[25]).longValue(), savedProTrans.getBinder().getBinId());
                if (binDetId.size() != 1) {
                    throw new BadRequestException("Unable to get binder details...");
                }

                BigInteger arr = binDetId.get(0);
                final BinderDetails binderDetails = binderDetRepo.findOne((arr).longValue());
                QuoteRiskTrans newRisk = new QuoteRiskTrans();
                BigInteger insuredId = (BigInteger) quoteRiskTrans[23];
                if (insuredId != null) {
                    ClientDef insured = clientRepo.findOne(insuredId.longValue());
                    newRisk.setInsured(insured);
                }
                BigInteger coverTypeId = (BigInteger) quoteRiskTrans[22];
                CoverTypesDef coverType = coverRepo.findOne(coverTypeId.longValue());
                newRisk.setCovertype(coverType);
                BigInteger subClassId = (BigInteger) quoteRiskTrans[25];
                SubClassDef subClass = subclassRepo.findOne(subClassId.longValue());
                newRisk.setSubclass(subClass);
                newRisk.setCommRate((BigDecimal) quoteRiskTrans[4]);
                newRisk.setWetDate((Date) quoteRiskTrans[18]);
                newRisk.setProrata((String) quoteRiskTrans[11]);
                newRisk.setWefDate((Date) quoteRiskTrans[17]);
                newRisk.setBinderDetails(binderDetails);
                newRisk.setRiskDesc((String) quoteRiskTrans[12]);
                newRisk.setWhtax((BigDecimal) quoteRiskTrans[19]);
                newRisk.setProductTrans(savedProTrans);
                newRisk.setExtras((BigDecimal) quoteRiskTrans[5]);
                BigInteger prospectId = (BigInteger) quoteRiskTrans[24];
                if (prospectId != null) {
                    ProspectDef prospect = prospectsRepo.findOne(prospectId.longValue());
                    newRisk.setProspect(prospect);
                }
                newRisk.setRiskShtDesc((String) quoteRiskTrans[13]);
                newRisk.setCalcPremium((BigDecimal) quoteRiskTrans[2]);
                newRisk.setCommAmt((BigDecimal) quoteRiskTrans[3]);
                newRisk.setNetpremium((BigDecimal) quoteRiskTrans[8]);
                newRisk.setPhfFund((BigDecimal) quoteRiskTrans[9]);
                newRisk.setStampDuty((BigDecimal) quoteRiskTrans[14]);
                newRisk.setTrainingLevy((BigDecimal) quoteRiskTrans[16]);
                newRisk.setButchargePrem((BigDecimal) quoteRiskTrans[1]);
                newRisk.setInsuredType((String) quoteRiskTrans[7]);
                newRisk.setFreeLimit((BigDecimal) quoteRiskTrans[6]);
                newRisk.setBinder(bindersDef);
                QuoteRiskTrans savedRisk = quotRiskRepo.save(newRisk);
                Iterable<Object[]> riskSections = quotRiskLimitsRepo.findRiskSections(((BigInteger) quoteRiskTrans[0]).longValue());
                for (Object[] quotRiskLimits : riskSections) {

                    QuotRiskLimits newQuotRiskLimit = new QuotRiskLimits();
                    List<PremRatesDef> newPremRates = premRatesRepo.getSectPremiumRates(
                            savedRisk.getBinderDetails().getDetId(),
                            ((BigInteger) quotRiskLimits[11]).longValue()
                    );
                    if (newPremRates.isEmpty()) {
                        throw new BadRequestException("No premium rates found for the new binder and section");
                    }
                    newQuotRiskLimit.setPremRates(newPremRates.get(0));
                    newQuotRiskLimit.setFreeLimit((BigDecimal) quotRiskLimits[6]);
                    newQuotRiskLimit.setRate(newPremRates.get(0).getRate());
                    newQuotRiskLimit.setDivFactor(newPremRates.get(0).getDivFactor());
                    newQuotRiskLimit.setAmount((BigDecimal) quotRiskLimits[1]);
                    newQuotRiskLimit.setRisk(savedRisk);
                    BigInteger sectionId = (BigInteger) quotRiskLimits[11];
                    SectionsDef section = setupSectionRepo.findOne(sectionId.longValue());
                    System.out.println("section: " + section.getShtDesc());
                    newQuotRiskLimit.setSection(section);
                    quotRiskLimitsRepo.save(newQuotRiskLimit);
                }

            }
        }

        return comparisonQuotDTO;
    }

    @Override
    public List<QuoteProductDTO> findQuotProductsByQuoteId(Long quotCode) {
        List<Object[]> quotesProducts = quotProductsRepo.enquireQuoteProductsByQuoteId(quotCode);
        final List<QuoteProductDTO> enquireDTOList = new ArrayList<>();
        for (Object[] enquire : quotesProducts) {
            QuoteProductDTO quote = new QuoteProductDTO();
            quote.setQuoteProductId(((BigInteger) enquire[0]).longValue());
            quote.setWef((Date) enquire[1]);
            quote.setWet((Date) enquire[2]);
            quote.setBinderId(((BigInteger) enquire[3]).longValue());
            quote.setProductId(((BigInteger) enquire[4]).longValue());
            enquireDTOList.add(quote);
        }
        return enquireDTOList;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void updateCategoryBenefits(MedQuotCategoryBenefits benefits) throws BadRequestException {
        if (benefits.getSectId() == null) throw new BadRequestException("Select Benefit to update limit");
        if (benefits.getLimit() == null) throw new BadRequestException("Select Benefit to continue...");
        if (benefits.getApplicableAt() == null)
            throw new BadRequestException("Selection benefit application at to continue..");
        MedQuotCategoryBenefits benefit = benefitRepo.findOne(benefits.getSectId());
        benefit.setLimit(benefits.getLimit());
        benefit.setWaitPeriod(benefits.getWaitPeriod());
        benefit.setApplicableAt(benefits.getApplicableAt());
        benefitRepo.save(benefit);
        try {
            medComputeQuotPrem.computePrem(benefit.getCategory().getQuotation().getQuoteId());
        } catch (IOException ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void updateFundBenefits(MedQuotCategoryBenefits benefits) throws BadRequestException {
        if (benefits.getSectId() == null) throw new BadRequestException("Choose Benefit to update limit");
        if (benefits.getApplicableAt() == null)
            throw new BadRequestException("Selection benefit application at to continue..");
        MedQuotCategoryBenefits benefit = benefitRepo.findOne(benefits.getSectId());
        benefit.setFundLimit(benefits.getFundLimit());
        benefit.setWaitPeriod(benefits.getWaitPeriod());
        benefit.setApplicableAt(benefits.getApplicableAt());
        benefitRepo.save(benefit);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void computeQuotPrem(Long quotCode) throws BadRequestException, IOException {
        QuoteTrans quoteTrans = quotTransRepo.findOne(QQuoteTrans.quoteTrans.quoteId.eq(quotCode));
        populateClauses(quoteTrans);
        populateTaxes(quoteTrans);
        Iterable<QuoteProTrans> quoteProTrans = quotProductsRepo.findAll(QQuoteProTrans.quoteProTrans.quoteTrans.quoteId.eq(quoteTrans.getQuoteId()));
        BigDecimal qtpremium = BigDecimal.ZERO;
        BigDecimal qtbasicpremium = BigDecimal.ZERO;
        BigDecimal qtnetpremium = BigDecimal.ZERO;
        BigDecimal qtsumInsured = BigDecimal.ZERO;
        BigDecimal qttotalCommission = BigDecimal.ZERO;
        BigDecimal qtpolstampDuty = BigDecimal.ZERO;
        BigDecimal qtpolphfFund = BigDecimal.ZERO;
        BigDecimal qtpolwhtxAmt = BigDecimal.ZERO;
        BigDecimal qtpolextras = BigDecimal.ZERO;
        BigDecimal qtpolTl = BigDecimal.ZERO;
        for (QuoteProTrans proTrans : quoteProTrans) {
            Iterable<QuoteRiskTrans> riskTrans = quotRiskRepo.findAll(QQuoteRiskTrans.quoteRiskTrans.productTrans.quoteProductId.eq(proTrans.getQuoteProductId()));
            Iterable<QuotTaxes> quotTaxes = quotTaxesRepo.findAll(QQuotTaxes.quotTaxes.proTrans.quoteProductId.eq(proTrans.getQuoteProductId()));
            BigDecimal premium = BigDecimal.ZERO;
            BigDecimal prodPremium = BigDecimal.ZERO;
            BigDecimal sumInsured = BigDecimal.ZERO;
            BigDecimal prodInsured = BigDecimal.ZERO;
            BigDecimal totalCommission = BigDecimal.ZERO;
            BigDecimal polstampDuty = BigDecimal.ZERO;
            BigDecimal polphfFund = BigDecimal.ZERO;
            BigDecimal polwhtxAmt = BigDecimal.ZERO;
            BigDecimal polextras = BigDecimal.ZERO;
            BigDecimal polTl = BigDecimal.ZERO;
            BigDecimal sumPolicytaxAmount = BigDecimal.ZERO;
            long calDays = TimeUnit.DAYS.convert((proTrans.getWetDate().getTime() - proTrans.getWefDate().getTime()), TimeUnit.MILLISECONDS) + 1;
            for (QuoteRiskTrans risk : riskTrans) {
                Iterable<QuotRiskLimits> quotLimits = quotRiskLimitsRepo.findAll(QQuotRiskLimits.quotRiskLimits.risk.riskId.eq(risk.getRiskId()));
                BigDecimal riskPrem = BigDecimal.ZERO;
                BigDecimal riskInsured = BigDecimal.ZERO;
                List<PremiumItemsBean> itemsBeen = new ArrayList<>();
                List<PremiumItemsBean> discountItems = new ArrayList<>();
                final String ratesTable = premRatesTableRepo.getRatesLocation(risk.getBinderDetails().getDetId());
                System.out.println("RAtes Table>>>>>>" + ratesTable);
                if (ratesTable == null)
                    throw new BadRequestException("Rates Table for the Binder has not been setup");
                for (QuotRiskLimits section : quotLimits) {
                    List<String> items = new ArrayList<>();
                    double minPrem = 0;
                    if (section.getPremRates() != null) {
                        PremRatesDef premRatesDef = section.getPremRates();
                        if (premRatesDef.getMinPremium() != null && premRatesDef.getMinPremium().compareTo(BigDecimal.ZERO) == 1) {
                            minPrem = premRatesDef.getMinPremium().doubleValue();
                        }
                    }
                    if (!section.getSection().getType().equals(SectionTypes.DS)) {
                        itemsBeen.add(new PremiumItemsBean(section.getSection().getShtDesc(), section.getRate().doubleValue(),
                                section.getFreeLimit().doubleValue(), minPrem,
                                section.getAmount().doubleValue(), section.getSectId(), section.getDivFactor().doubleValue(),
                                section.getSection().getType().getCode(), section.getSection().getType().getOrder(), items, null));
                    } else if (section.getSection().getType().equals(SectionTypes.DS)) {
                        discountItems.add(new PremiumItemsBean(section.getSection().getShtDesc(), section.getRate().doubleValue(),
                                section.getFreeLimit().doubleValue(), minPrem,
                                section.getAmount().doubleValue(), section.getSectId(), section.getDivFactor().doubleValue(),
                                section.getSection().getType().getCode(), section.getSection().getType().getOrder(), items, null));
                    }
                }

                itemsBeen.stream().sorted(Comparator.comparing(PremiumItemsBean::getOrder));
                PremiumResultBean resultBean = premExcelUtils.getQuotPremium(itemsBeen, ratesTable);
                riskPrem = BigDecimal.valueOf(resultBean.getPremium());
                Optional<PremiumItemsBean> premiumItemsBean = discountItems.stream().filter(a -> a.getSectType().equalsIgnoreCase("DS")).findFirst();
                if (premiumItemsBean.isPresent()) {
                    PremiumItemsBean itemsBean = premiumItemsBean.get();
                    final QuotRiskLimits section = quotRiskLimitsRepo.findOne(itemsBean.getSectId());
                    final BigDecimal discountPrem = BigDecimal.valueOf(itemsBean.getRate()).multiply(riskPrem).divide(BigDecimal.valueOf(itemsBean.getDivFactor()), 2, RoundingMode.HALF_EVEN);
                    section.setCalcprem(discountPrem.negate());
                    section.setPrem(discountPrem.negate());
                    quotRiskLimitsRepo.save(section);
                    riskPrem = riskPrem.subtract(discountPrem);
                }
                sumInsured = BigDecimal.valueOf(resultBean.getSumInsured());
                riskInsured = sumInsured;
                premium = riskPrem;
                quotRiskLimitsRepo.save(quotLimits);
                String proratedFull = risk.getProrata();
                if ("F".equalsIgnoreCase(proratedFull)) {
                    riskPrem = riskPrem.multiply(BigDecimal.ONE);
                    premium = premium.multiply(BigDecimal.ONE);
                } else if ("P".equalsIgnoreCase(proratedFull)) {
                    long datediff = risk.getWetDate().getTime() - risk.getWefDate().getTime();
                    long daysDiff = TimeUnit.DAYS.convert(datediff, TimeUnit.MILLISECONDS) + 1;
                    double rata = (double) daysDiff / calDays;
                    BigDecimal prorata = new BigDecimal(rata);
                    riskPrem = riskPrem.multiply(prorata);
                    premium = premium.multiply(prorata);
                }


                if (risk.getButchargePrem() == null || risk.getButchargePrem().compareTo(BigDecimal.ZERO) == 0) {
                } else {
                    riskPrem = risk.getButchargePrem();
                    premium = risk.getButchargePrem();
                }

                if (risk.getBinderDetails().getMinPrem() != null && risk.getBinderDetails().getMinPrem().compareTo(riskPrem) == 1) {
                    riskPrem = risk.getBinderDetails().getMinPrem();
                    premium = risk.getBinderDetails().getMinPrem();
                }


                prodPremium = prodPremium.add(premium);
                prodInsured = prodInsured.add(sumInsured);
                risk.setPremium(riskPrem);
                risk.setCalcPremium(riskPrem);
                risk.setSumInsured(riskInsured);
                BigDecimal comm = BigDecimal.ZERO;
                if (risk.getCommRate() != null)
                    comm = riskPrem.multiply(risk.getCommRate()).divide(new BigDecimal(100));
                if (riskPrem.compareTo(BigDecimal.ZERO) == 1) comm = comm.negate();
                else if (riskPrem.compareTo(BigDecimal.ZERO) == -1) comm = comm.abs();
                risk.setCommAmt(comm);
                totalCommission = totalCommission.add(comm);

                BigDecimal sumRisktaxAmount = BigDecimal.ZERO;
                BigDecimal riskstampDuty = BigDecimal.ZERO;
                BigDecimal riskphfFund = BigDecimal.ZERO;
                BigDecimal riskwhtxAmt = BigDecimal.ZERO;
                BigDecimal riskextras = BigDecimal.ZERO;
                BigDecimal riskTl = BigDecimal.ZERO;

                for (QuotTaxes quotTax : quotTaxes) {
                    BigDecimal computedTax = premComputeService.calculateTax(premium, quotTax.getTaxRate(), quotTax.getDivFactor(), quotTax.getRateType());
                    //   System.out.println("Computed Tax "+computedTax+"  Premium "+premium+" Rate ...."+quotTax.getTaxRate());
                    // quotTax.setTaxAmount(computedTax);
                    sumPolicytaxAmount = sumPolicytaxAmount.add(computedTax);
//                    if (quotTax.getRevenueItems().getItem() == RevenueItems.EX) {
//                        if ("R".equalsIgnoreCase(quotTax.getTaxLevel())) {
//                            riskextras = riskextras.add(computedTax);
//                            sumRisktaxAmount = sumRisktaxAmount.add(computedTax);
//                        }
//                    } else
                    if (quotTax.getRevenueItems().getItem() == RevenueItems.SD) {
                        if ("R".equalsIgnoreCase(quotTax.getTaxLevel())) {
                            riskstampDuty = riskstampDuty.add(computedTax);
                            sumRisktaxAmount = sumRisktaxAmount.add(computedTax);
                        }
                    } else if (quotTax.getRevenueItems().getItem() == RevenueItems.PHCF) {
                        if ("R".equalsIgnoreCase(quotTax.getTaxLevel())) {
                            riskphfFund = riskphfFund.add(computedTax);
                            sumRisktaxAmount = sumRisktaxAmount.add(computedTax);
                        }
                    } else if (quotTax.getRevenueItems().getItem() == RevenueItems.WHTX) {
                        if ("R".equalsIgnoreCase(quotTax.getTaxLevel())) {
                            riskwhtxAmt = riskwhtxAmt.add(computedTax);
                            sumRisktaxAmount = sumRisktaxAmount.add(computedTax);
                        }
                    } else if (quotTax.getRevenueItems().getItem() == RevenueItems.TL) {
                        if ("R".equalsIgnoreCase(quotTax.getTaxLevel())) {
                            riskTl = riskTl.add(computedTax);
                            sumRisktaxAmount = sumRisktaxAmount.add(computedTax);
                        }
                    }
                }
                risk.setNetpremium(premium.add(sumRisktaxAmount).subtract(comm));
                risk.setExtras(riskextras);
                risk.setWhtax(riskwhtxAmt);
                risk.setPhfFund(riskphfFund);
                risk.setTrainingLevy(riskTl);
                risk.setStampDuty(riskstampDuty);
//                polextras = polextras.add(riskextras);
//                polphfFund = polphfFund.add(riskphfFund);
//                polstampDuty = polstampDuty.add(riskstampDuty);

            }
            BigDecimal whtxAmt = BigDecimal.ZERO;
            AccountTypes accType = proTrans.getAgent().getAccountType();
            if (accType.isWhtxAppl()) {
                if (accType.getWhtaxVal().compareTo(BigDecimal.ZERO) == 1) {
                    whtxAmt = accType.getWhtaxVal().divide(new BigDecimal(100)).multiply(totalCommission);
                    polwhtxAmt = whtxAmt.negate();
                }
            }
            sumPolicytaxAmount = sumPolicytaxAmount.add(polwhtxAmt);

//            for (QuotTaxes taxes : quotTaxes) {
//                BigDecimal computedTax = premComputeService.calculateTax(premium, taxes.getTaxRate(), taxes.getDivFactor(), taxes.getRateType());
//                if (taxes.getRevenueItems().getItem() == RevenueItems.EX) {
//
//                    polextras = polextras.add(computedTax);
//                } else if (taxes.getRevenueItems().getItem() == RevenueItems.SD) {
//
//                    polstampDuty = polstampDuty.add(computedTax);
//                } else if (taxes.getRevenueItems().getItem() == RevenueItems.PHCF) {
//
//                    polphfFund = polphfFund.add(computedTax);
//                } else if (taxes.getRevenueItems().getItem() == RevenueItems.TL) {
//
//                    polTl = polTl.add(computedTax);
//                }
//            }

            quotRiskRepo.save(riskTrans);
            BigDecimal policyLevelCommRate = BigDecimal.ZERO;
            if (prodPremium.compareTo(BigDecimal.ZERO) != 0)
                policyLevelCommRate = totalCommission.divide(prodPremium, 2, RoundingMode.HALF_EVEN);
            if (proTrans.getProduct().getMinPrem() != null) {
                if (proTrans.getProduct().getMinPrem().compareTo(prodPremium) == 1) {
                    prodPremium = proTrans.getProduct().getMinPrem();
                    totalCommission = policyLevelCommRate.multiply(prodPremium);


                }
            }
            for (QuotTaxes quotTax : quotTaxes) {
                BigDecimal computedTax = premComputeService.calculateTax(prodPremium, quotTax.getTaxRate(), quotTax.getDivFactor(), quotTax.getRateType());
                quotTax.setTaxAmount(computedTax);
                sumPolicytaxAmount = sumPolicytaxAmount.add(computedTax);
//                if (quotTax.getRevenueItems().getItem() == RevenueItems.EX) {
//                    polextras = polextras.add(computedTax);
//                } else
                if (quotTax.getRevenueItems().getItem() == RevenueItems.SD) {
                    polstampDuty = polstampDuty.add(computedTax);
                } else if (quotTax.getRevenueItems().getItem() == RevenueItems.PHCF) {
                    polphfFund = polphfFund.add(computedTax);
                }
//                else if (quotTax.getRevenueItems().getItem() == RevenueItems.WHTX) {
//                    polwhtxAmt = polwhtxAmt.add(computedTax);
//                }
                else if (quotTax.getRevenueItems().getItem() == RevenueItems.TL) {
                    polTl = polTl.add(computedTax);
                }
            }
            quotTaxesRepo.save(quotTaxes);
            proTrans.setPremium(prodPremium);
            proTrans.setBasicPrem(prodPremium.add(polextras).add(polstampDuty).add(polphfFund).add(polTl));
            proTrans.setCommAmt(totalCommission);
            proTrans.setExtras(polextras);
            proTrans.setWhtax(polwhtxAmt);
            proTrans.setPhfFund(polphfFund);
            proTrans.setTrainingLevy(polTl);
            proTrans.setStampDuty(polstampDuty);
            proTrans.setSumInsured(prodInsured);
            proTrans.setNetPrem(prodPremium.add(polextras).add(polstampDuty).add(polphfFund).add(polTl).add(polwhtxAmt).add(totalCommission));
            qtbasicpremium = qtbasicpremium.add(prodPremium.add(polextras).add(polstampDuty).add(polphfFund).add(polTl));
            qtnetpremium = qtnetpremium.add(prodPremium.add(polextras).add(polstampDuty).add(polphfFund).add(polTl).add(polwhtxAmt).add(totalCommission));
            qtpremium = qtpremium.add(prodPremium);
            qtsumInsured = qtsumInsured.add(prodInsured);
            qttotalCommission = qttotalCommission.add(totalCommission);
            qtpolstampDuty = qtpolstampDuty.add(polstampDuty);
            qtpolphfFund = qtpolphfFund.add(polphfFund);
            qtpolwhtxAmt = qtpolwhtxAmt.add(polwhtxAmt);
            qtpolextras = qtpolextras.add(polextras);
            qtpolTl = qtpolTl.add(polTl);
        }
        Currencies currencies = quoteTrans.getTransCurrency();
        qtpremium = qtpremium.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN);
        quotProductsRepo.save(quoteProTrans);
        quoteTrans.setPremium(qtpremium);
        quoteTrans.setBasicPrem(qtbasicpremium.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        quoteTrans.setExtras(qtpolextras);
        quoteTrans.setNetPrem(qtnetpremium.setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
        quoteTrans.setPhcf(qtpolphfFund);
        quoteTrans.setStampDuty(qtpolstampDuty);
        quoteTrans.setWhtx(qtpolwhtxAmt);
        quoteTrans.setCommAmt(qttotalCommission);
        quoteTrans.setSumInsured(qtsumInsured);
        quoteTrans.setTrainingLevy(qtpolTl);
        quotTransRepo.save(quoteTrans);

    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void populateTaxes(QuoteTrans quoteTrans) throws BadRequestException {
        if (quoteTrans == null) throw new BadRequestException("Quotation cannot be null");
        Set<QuotTaxes> quotTaxes = new HashSet<>();
        Iterable<QuoteProTrans> quoteProTrans = quotProductsRepo.findAll(QQuoteProTrans.quoteProTrans.quoteTrans.quoteId.eq(quoteTrans.getQuoteId()));
        for (QuoteProTrans proTrans : quoteProTrans) {
            Iterable<QuotTaxes> qtaxes = quotTaxesRepo.findAll(QQuotTaxes.quotTaxes.proTrans.quoteProductId.eq(proTrans.getQuoteProductId()));
            qtaxes.forEach(quotTaxes::add);
            Iterable<QuoteRiskTrans> riskTrans = quotRiskRepo.findAll(QQuoteRiskTrans.quoteRiskTrans.productTrans.quoteProductId.eq(proTrans.getQuoteProductId()));
            for (QuoteRiskTrans quoteRiskTrans : riskTrans) {
                Iterable<TaxRates> taxRates = taxRatesRepo.findAll((QTaxRates.taxRates.active.eq(true).and(QTaxRates.taxRates.mandatory.eq(Boolean.TRUE)))
                        .and(QTaxRates.taxRates.subclass.eq(quoteRiskTrans.getSubclass()))
                        .and(QTaxRates.taxRates.productsDef.proCode.eq(quoteRiskTrans.getProductTrans().getProduct().getProCode())));
                for (TaxRates tax : taxRates) {
                    if ("B".equalsIgnoreCase(proTrans.getBinder().getBinType())) {
                        if (tax.getRevenueItems().getItem() == RevenueItems.SD)
                            continue;
                    }
                    QuotTaxes quotTax = new QuotTaxes();
                    quotTax.setProTrans(proTrans);
                    quotTax.setRateType(tax.getRateType());
                    quotTax.setRevenueItems(tax.getRevenueItems());
                    quotTax.setSubclass(tax.getSubclass());
                    quotTax.setTaxLevel(tax.getTaxLevel());
                    quotTax.setTaxRate(tax.getTaxRate());
                    quotTax.setDivFactor(tax.getDivFactor());
                    quotTax.setTaxAmount(premComputeService.calculateTax(proTrans.getPremium(), tax.getTaxRate(), tax.getDivFactor(), tax.getRateType()));
                    quotTaxes.add(quotTax);
                }
            }
        }

        quotTaxesRepo.save(quotTaxes);
    }

    @Override
    public void createClause(QuoteClausesBean clause) throws BadRequestException {
        QuoteProTrans proTrans = quotProductsRepo.findOne(clause.getQuoteProdId());
        if (proTrans == null) throw new BadRequestException("Quotation Product cannot be null");
        List<QuotClauses> createdClauses = new ArrayList<>();
        for (Long clauseId : clause.getClauses()) {
            final SubclassClauses subclassClauses = clausesRepo.findOne(clauseId);
            final QuotClauses clauses = new QuotClauses();
            clauses.setProTrans(proTrans);
            clauses.setClause(subclassClauses);
            clauses.setClauHeading(subclassClauses.getClause().getClauHeading());
            clauses.setClauWording(subclassClauses.getClause().getClauWording());
            clauses.setEditable(subclassClauses.getClause().isEditable());
            createdClauses.add(clauses);
        }
        quotClausesRepo.save(createdClauses);
    }

    @Override
    public Set<QuotClausesDTO> getNewClauses(Long prodId) throws BadRequestException {
        QuoteProTrans proTrans = quotProductsRepo.findOne(prodId);
        if (proTrans == null) throw new BadRequestException("Quotation Product cannot be null");
        Set<QuotClausesDTO> quotClauses = new HashSet<>();
        List<Object[]> clausesList = quotClausesRepo.getQuotProdClauses(prodId);
        for (Object[] obj : clausesList) {
            final Long clauseId = ((BigInteger) obj[0]).longValue();
            final String header = (String) obj[2];
            QuotClausesDTO quotClaus = new QuotClausesDTO();
            quotClaus.setHeader(header);
            quotClaus.setSubClauseId(clauseId);
            quotClauses.add(quotClaus);
        }
        return quotClauses;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void populateClauses(QuoteTrans quoteTrans) throws BadRequestException {
        if (quoteTrans == null) throw new BadRequestException("Quotation cannot be null");
        Set<QuotClauses> quotClauses = new HashSet<>();
        Iterable<QuoteProTrans> quoteProTrans = quotProductsRepo.findAll(QQuoteProTrans.quoteProTrans.quoteTrans.quoteId.eq(quoteTrans.getQuoteId()));
        for (QuoteProTrans proTrans : quoteProTrans) {
            Set<QuotClausesDTO> clausesDTOS = this.getNewClauses(proTrans.getQuoteProductId());
            for (QuotClausesDTO clauseId : clausesDTOS) {
                final SubclassClauses subclassClauses = clausesRepo.findOne(clauseId.getSubClauseId());
                final QuotClauses clauses = new QuotClauses();
                clauses.setProTrans(proTrans);
                clauses.setClause(subclassClauses);
                clauses.setClauHeading(subclassClauses.getClause().getClauHeading());
                clauses.setClauWording(subclassClauses.getClause().getClauWording());
                clauses.setEditable(subclassClauses.getClause().isEditable());
                quotClauses.add(clauses);
            }
            Iterable<QuoteRiskTrans> riskTrans = quotRiskRepo.findAll(QQuoteRiskTrans.quoteRiskTrans.productTrans.quoteProductId.eq(proTrans.getQuoteProductId()));
            for (QuoteRiskTrans quoteRiskTrans : riskTrans) {
                Iterable<BinderClauses> binderClauses = binderClauseRepo.findAll(QBinderClauses.binderClauses.binderDet.detId.eq(quoteRiskTrans.getBinderDetails().getDetId()).and(QBinderClauses.binderClauses.mandatory.equalsIgnoreCase("Y")));
                for (BinderClauses clause : binderClauses) {
                    QuotClauses quotClaus = new QuotClauses();
                    quotClaus.setClauHeading((clause.getClauHeading() != null) ? clause.getClauHeading() : clause.getClause().getClause().getClauHeading());
                    quotClaus.setClause(clause.getClause());
                    quotClaus.setClauWording((clause.getClauWording() != null) ? clause.getClauWording() : clause.getClause().getClause().getClauWording());
                    if (clause.getEditable() != null && "Y".equalsIgnoreCase(clause.getEditable())) {
                        quotClaus.setEditable(true);
                    } else {
                        quotClaus.setEditable(false);
                    }
                    quotClaus.setProTrans(proTrans);
                    quotClauses.add(quotClaus);
                }
            }
        }
        quotClausesRepo.save(quotClauses);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<QuoteProductDTO> findQuotProducts(DataTablesRequest request, Long quotCode) {
        QuoteTrans quot = quotTransRepo.findOne(QQuoteTrans.quoteTrans.quoteId.eq(quotCode));
        if (quot.getQuoteType().equalsIgnoreCase("comparison") && !quot.getQuotStatus().equalsIgnoreCase("D")) {
            List<Object[]> quotesProducts = quotProductsRepo.enquireQuoteCompProducts((quotCode != null) ? quotCode : -2000L,
                    (request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%",
                    request.getPageNumber(), request.getPageSize());
            final List<QuoteProductDTO> enquireDTOList = new ArrayList<>();
            long rowCount = 0L;
            if (!quotesProducts.isEmpty()) rowCount = ((BigInteger) quotesProducts.get(0)[16]).intValue();
            for (Object[] enquire : quotesProducts) {
                QuoteProductDTO quote = new QuoteProductDTO();
                quote.setQuoteProductId(((BigInteger) enquire[0]).longValue());
                quote.setWef((Date) enquire[1]);
                quote.setWet((Date) enquire[2]);
                quote.setBinder((String) enquire[3]);
                quote.setBinderId(((BigInteger) enquire[4]).longValue());
                quote.setAcctId(((BigInteger) enquire[5]).longValue());
                quote.setAccount((String) enquire[6]);
                quote.setSumInsured((BigDecimal) enquire[7]);
                quote.setPremium((BigDecimal) enquire[8]);
                quote.setCommAmt((BigDecimal) enquire[9]);
                quote.setQuotStatus((String) enquire[10]);
                quote.setProductId(((BigInteger) enquire[11]).longValue());
                quote.setProduct((String) enquire[12]);
                quote.setBindType((String) enquire[13]);
                quote.setQuotConverted((String) enquire[14]);
                quote.setQuotType((String) enquire[15]);
                enquireDTOList.add(quote);
            }
            Page<QuoteProductDTO> page = new PageImpl<>(enquireDTOList, request, rowCount);
            return new DataTablesResult<>(request, page);

        } else {
            List<Object[]> quotesProducts = quotProductsRepo.enquireQuoteProducts((quotCode != null) ? quotCode : -2000L,
                    (request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%",
                    request.getPageNumber(), request.getPageSize());
            final List<QuoteProductDTO> enquireDTOList = new ArrayList<>();
            long rowCount = 0L;
            if (!quotesProducts.isEmpty()) rowCount = ((BigInteger) quotesProducts.get(0)[16]).intValue();
            for (Object[] enquire : quotesProducts) {
                QuoteProductDTO quote = new QuoteProductDTO();
                quote.setQuoteProductId(((BigInteger) enquire[0]).longValue());
                quote.setWef((Date) enquire[1]);
                quote.setWet((Date) enquire[2]);
                quote.setBinder((String) enquire[3]);
                quote.setBinderId(((BigInteger) enquire[4]).longValue());
                quote.setAcctId(((BigInteger) enquire[5]).longValue());
                quote.setAccount((String) enquire[6]);
                quote.setSumInsured((BigDecimal) enquire[7]);
                quote.setPremium((BigDecimal) enquire[8]);
                quote.setCommAmt((BigDecimal) enquire[9]);
                quote.setQuotStatus((String) enquire[10]);
                quote.setProductId(((BigInteger) enquire[11]).longValue());
                quote.setProduct((String) enquire[12]);
                quote.setBindType((String) enquire[13]);
                quote.setQuotConverted((String) enquire[14]);
                quote.setQuotType((String) enquire[15]);
                enquireDTOList.add(quote);
            }
            Page<QuoteProductDTO> page = new PageImpl<>(enquireDTOList, request, rowCount);
            return new DataTablesResult<>(request, page);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<QuoteRiskDTO> findProdRisks(DataTablesRequest request, Long qrCode) throws
            IllegalAccessException {
        List<Object[]> prodRisks = quotRiskRepo.enquireQuoteProducts((qrCode != null) ? qrCode : -2000L,
                (request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%",
                request.getPageNumber(), request.getPageSize());

        final List<QuoteRiskDTO> enquireDTOList = new ArrayList<>();
        long rowCount = 0L;
        if (!prodRisks.isEmpty()) rowCount = ((BigInteger) prodRisks.get(0)[23]).intValue();
        for (Object[] enquire : prodRisks) {
            QuoteRiskDTO quote = new QuoteRiskDTO();
            quote.setRiskId(((BigInteger) enquire[0]).longValue());
            quote.setRiskShtDesc((String) enquire[1]);
            quote.setRiskDesc((String) enquire[2]);
            quote.setWefDate((Date) enquire[3]);
            quote.setWetDate((Date) enquire[4]);
            quote.setSubId(((BigInteger) enquire[5]).longValue());
            quote.setSubDesc((String) enquire[6]);
            quote.setCovId(((BigInteger) enquire[7]).longValue());
            quote.setCovName((String) enquire[8]);
            quote.setSumInsured((BigDecimal) enquire[9]);
            quote.setPremium((BigDecimal) enquire[10]);
            quote.setQuotStatus((String) enquire[11]);
            quote.setProrata((String) enquire[12]);
            quote.setCommRate((BigDecimal) enquire[13]);
            quote.setButchargePrem((BigDecimal) enquire[14]);
            if (enquire[18] != null) {
                quote.setClientType("P");
                quote.setTenId(((BigInteger) enquire[18]).longValue());
                quote.setFname((String) enquire[19]);
                quote.setOtherNames((String) enquire[20]);
            } else {
                quote.setClientType("C");
                quote.setTenId(((BigInteger) enquire[15]).longValue());
                quote.setFname((String) enquire[16]);
                quote.setOtherNames((String) enquire[17]);
                quote.setIdNo((String) enquire[22]);
            }
            quote.setBinderId(((BigInteger) enquire[21]).longValue());
            enquireDTOList.add(quote);
        }
        Page<QuoteRiskDTO> page = new PageImpl<>(enquireDTOList, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<QuotRiskPremItemsDTO> findQuotRiskLimits(DataTablesRequest request, Long riskCode) throws
            IllegalAccessException {
        List<Object[]> prodRisksSections = quotRiskLimitsRepo.enquireQuoteRiskSections((riskCode != null) ? riskCode : -2000L,
                (request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%",
                request.getPageNumber(), request.getPageSize());
        final List<QuotRiskPremItemsDTO> enquireDTOList = new ArrayList<>();
        long rowCount = 0L;
        if (!prodRisksSections.isEmpty()) rowCount = ((BigInteger) prodRisksSections.get(0)[12]).intValue();
        for (Object[] enquire : prodRisksSections) {
            QuotRiskPremItemsDTO quote = new QuotRiskPremItemsDTO();
            quote.setSectId(((BigInteger) enquire[0]).longValue());
            quote.setSecId(((BigInteger) enquire[1]).longValue());
            quote.setSecName((String) enquire[2]);
            quote.setAmount((BigDecimal) enquire[3]);
            quote.setRate((BigDecimal) enquire[4]);
            if (enquire[5] != null)
                quote.setPrem((BigDecimal) enquire[5]);
            quote.setDivFactor((BigDecimal) enquire[6]);
            if (enquire[7] != null)
                quote.setFreeLimit((BigDecimal) enquire[7]);
            quote.setQuotStatus((String) enquire[8]);
            quote.setRateId(((BigInteger) enquire[9]).longValue());
            quote.setRiskId(((BigInteger) enquire[10]).longValue());
            quote.setAnnualEarnings(((BigDecimal) enquire[11]));
            enquireDTOList.add(quote);
        }
        Page<QuotRiskPremItemsDTO> page = new PageImpl<>(enquireDTOList, request, rowCount);
        return new DataTablesResult<>(request, page);
    }


    @Override
    @Transactional(readOnly = false)
    public void createQuotRiskSection(QuotRiskPremItemsDTO riskLimits) throws BadRequestException {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource);
        if (riskLimits.getRiskId() == null) {
            throw new BadRequestException("Select Risk to continue....");
        }
        if (riskLimits.getSectId() == null) {
            jdbcTemplate.update("insert into sys_brk_quot_limits(quot_sect_id,quot_sect_amount,quot_sect_div_fact,quot_sect_free_limit,quot_sect_rate,quot_sect_prem_id,quot_sect_sec_id,quot_sect_rsk_id)" +
                    "values(NEXTVAL('hibernate_sequence'),?,?,?,?,?,?,?)", new Object[]{riskLimits.getAmount(), riskLimits.getDivFactor(), riskLimits.getFreeLimit(),
                    riskLimits.getRate(), riskLimits.getRateId(), riskLimits.getSecId(), riskLimits.getRiskId()});
        } else {
            jdbcTemplate.update("update sys_brk_quot_limits set quot_sect_amount =?,quot_sect_div_fact = ?,quot_sect_free_limit = ?," +
                            "quot_sect_rate = ?,quot_sect_prem_id = ?,quot_sect_sec_id = ? ,quot_sect_rsk_id = ? where quot_sect_id = ?",
                    new Object[]{riskLimits.getAmount(), riskLimits.getDivFactor(), riskLimits.getFreeLimit(),
                            riskLimits.getRate(), riskLimits.getRateId(), riskLimits.getSecId(), riskLimits.getRiskId(),
                            riskLimits.getSectId()});

        }
    }


    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<QuotTaxesDTO> getProductTaxes(DataTablesRequest request, Long qrCode) throws
            IllegalAccessException {
        List<Object[]> prodTaxes = quotTaxesRepo.enquireQuoteTaxes((qrCode != null) ? qrCode : -2000L, request.getPageNumber(), request.getPageSize());
        final List<QuotTaxesDTO> enquireDTOList = new ArrayList<>();
        long rowCount = 0L;
        if (!prodTaxes.isEmpty()) rowCount = ((BigInteger) prodTaxes.get(0)[8]).intValue();
        for (Object[] enquire : prodTaxes) {
            QuotTaxesDTO quote = new QuotTaxesDTO();
            quote.setPolTaxId(((BigInteger) enquire[6]).longValue());
            quote.setTaxRate((BigDecimal) enquire[1]);
            quote.setDivFactor((BigDecimal) enquire[2]);
            quote.setRateType((String) enquire[3]);
            quote.setTaxAmount((BigDecimal) enquire[4]);
            quote.setTaxLevel((String) enquire[5]);
            quote.setRevItemCode((String) enquire[0]);
            quote.setQuotStatus((String) enquire[7]);
            enquireDTOList.add(quote);
        }
        Page<QuotTaxesDTO> page = new PageImpl<>(enquireDTOList, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<QuoteClauseDTO> getProductClauses(DataTablesRequest request, Long qrCode) {
        List<Object[]> prodClauses = quotClausesRepo.getQuoteProdClauses((qrCode != null) ? qrCode : -2000L, request.getPageNumber(), request.getPageSize());
        final List<QuoteClauseDTO> enquireDTOList = new ArrayList<>();
        long rowCount = 0L;
        if (!prodClauses.isEmpty()) rowCount = ((BigInteger) prodClauses.get(0)[8]).intValue();
        for (Object[] enquire : prodClauses) {
            QuoteClauseDTO clauseDTO = new QuoteClauseDTO();
            clauseDTO.setQpClauId(((BigInteger) enquire[0]).longValue());
            clauseDTO.setClauHeading((String) enquire[1]);
            clauseDTO.setEditable((String) enquire[2]);
            clauseDTO.setClauWording((String) enquire[3]);
            clauseDTO.setClauseType((String) enquire[4]);
            clauseDTO.setClauseId(((BigInteger) enquire[5]).longValue());
            clauseDTO.setClauShtDesc((String) enquire[6]);
            clauseDTO.setQuotStatus((String) enquire[7]);
            clauseDTO.setQuoteProductId(qrCode);
            enquireDTOList.add(clauseDTO);
        }
        Page<QuoteClauseDTO> page = new PageImpl<>(enquireDTOList, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @PreAuthorize("hasAnyAuthority('SAVE_QUOTE')")
    @Override
    @Transactional(readOnly = false)
    public void deleteProductRisks(Long riskCode) {
        quotRiskLimitsRepo.delete(quotRiskLimitsRepo.findAll(QQuotRiskLimits.quotRiskLimits.risk.riskId.eq(riskCode)));
        quotRiskRepo.delete(riskCode);
    }

    @PreAuthorize("hasAnyAuthority('SAVE_QUOTE')")
    @Override
    @Transactional(readOnly = false)
    public void deleteQuoteRiskSections(Long sectId) {
        quotRiskLimitsRepo.delete(sectId);
    }

    @PreAuthorize("hasAnyAuthority('SAVE_QUOTE')")
    @Override
    @Transactional(readOnly = false)
    public void deleteQuotProducts(Long qrCode) {
//        quotRiskLimitsRepo.delete(quotRiskLimitsRepo.findAll(QQuotRiskLimits.quotRiskLimits.risk.productTrans.quoteProductId.eq(qrCode)));
//        quotRiskRepo.delete(quotRiskRepo.findAll(QQuoteRiskTrans.quoteRiskTrans.productTrans.quoteProductId.eq(qrCode)));
//        quotClausesRepo.delete(quotClausesRepo.findAll(QQuotClauses.quotClauses.proTrans.quoteProductId.eq(qrCode)));
//        quotTaxesRepo.delete(quotTaxesRepo.findAll(QQuotTaxes.quotTaxes.proTrans.quoteProductId.eq(qrCode)));
        quotProductsRepo.deleteQuoteRiskLimits(qrCode);
        quotProductsRepo.deleteQuoteRisks(qrCode);
        quotProductsRepo.deleteQuoteClauses(qrCode);
        quotProductsRepo.deleteQuotTaxes(qrCode);
        quotProductsRepo.deleteQuoteProduct(qrCode);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void deleteQuote(Long quoteCode) throws BadRequestException {
        if (!authLimits.checkAuthorizationLimits("DELETE_QUOTE")) {
            throw new BadRequestException("You have no rights to delete the quote...Contact Administrator for assistance..");
        }
        final QuoteDetailsDTO quoteTrans = this.getQuotationDetails(quoteCode);
        DeletedTransAudit deletedTransAudit = new DeletedTransAudit("Quote", userUtils.getCurrentUser().getUsername(), quoteTrans.getQuotNo(), quoteTrans.getPremium());
        deletedTransAuditRepo.save(deletedTransAudit);
        List<Object[]> quotProducts = quotProductsRepo.getQuotProducts(quoteCode);
        for (Object[] prodId : quotProducts) {
            final Long policyId = (prodId[1] != null) ? ((BigInteger) prodId[1]).longValue() : null;
            final Long quotProdId = ((BigInteger) prodId[0]).longValue();
            if (policyId != null) {
                throw new BadRequestException("Cannot Delete the Quote. It has a converted policy. You need to delete it first " + policyRepo.findOne(QPolicyTrans.policyTrans.policyId.eq(policyId)).getPolNo());
            }
            quotProductsRepo.deleteQuoteRiskLimits(quotProdId);
            quotProductsRepo.deleteQuoteRisks(quotProdId);
            quotProductsRepo.deleteQuoteClauses(quotProdId);
            quotProductsRepo.deleteQuotTaxes(quotProdId);
            quotProductsRepo.deleteQuoteProduct(quotProdId);
        }
        quotProductsRepo.deleteQuoteWorkFlows(quoteCode);
        quotProductsRepo.deleteQuote(quoteCode);

    }

    @Override
    @Transactional(readOnly = true)
    public List<PremRatesDef> getNewPremiumItems(Long detId, Long riskId, String searchVal) {
        return quotRiskLimitsRepo.getUnassignedPremItems(detId, riskId, searchVal);
    }

    @PreAuthorize("hasAnyAuthority('SAVE_QUOTE')")
    @Override
    @Transactional(readOnly = false)
    public void deleteMedQuotFamDetails(Long familyId) {
        medQuotCatFamilyDetailsRepo.delete(familyId);
    }

    @PreAuthorize("hasAnyAuthority('SAVE_QUOTE')")
    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void createRiskSections(RiskBean sections) throws BadRequestException {
        if (sections.getRiskId() == null)
            throw new BadRequestException("Cannot save a section without Risk Transaction");
        QuoteRiskTrans risk = quotRiskRepo.findOne(sections.getRiskId());
        if (risk == null) throw new BadRequestException("Cannot save a section without Risk Transaction");
        List<QuotRiskLimits> sectionTransactions = new ArrayList<>();
        for (RiskSectionBean sectionbean : sections.getSections()) {
            SectionsDef sectiondef = setupSectionRepo.findOne(sectionbean.getSection());
            QuotRiskLimits section = new QuotRiskLimits();
            section.setPremRates(premRatesRepo.findOne(sectionbean.getPremId()));
            section.setAmount((sectionbean.getAmount() == null) ? BigDecimal.ZERO : sectionbean.getAmount());
            section.setCompute(sectionbean.isCompute());
            section.setDivFactor(sectionbean.getDivFactor());
            section.setFreeLimit(sectionbean.getFreeLimit());
            if (sectionbean.getRatesApplicable() != null && "Y".equalsIgnoreCase(sectionbean.getRatesApplicable())) {
                List<PremRatesDef> premRates = premRatesRepo.getSectPremiumRates(risk.getBinderDetails().getDetId(), sectionbean.getAmount(), sectiondef.getId());
                if (premRates.size() == 0)
                    throw new BadRequestException("No Premium Rates for the Value entered...");
                if (premRates.size() > 1)
                    throw new BadRequestException("Duplicate Prem Rates set up for the Section..." + sectionbean.getSectionDesc());
                section.setPremRates(premRates.get(0));
                section.setRate(premRates.get(0).getRate());
            } else {
                List<PremRatesDef> premRates = premRatesRepo.getSectPremiumRates(risk.getBinderDetails().getDetId(), sectiondef.getId());
                if (premRates.size() == 0)
                    throw new BadRequestException("No Premium Rates for the Value entered...");
                if (premRates.size() > 1)
                    throw new BadRequestException("Duplicate Prem Rates set up for the Section..." + sectionbean.getSectionDesc());
                section.setPremRates(premRates.get(0));
                section.setRate(sectionbean.getRate());
            }
            section.setCompute(true);
            section.setSection(sectiondef);
            section.setRisk(risk);
            sectionTransactions.add(section);
        }
        quotRiskLimitsRepo.save(sectionTransactions);
        try {
            this.computeQuotPrem(risk.getProductTrans().getQuoteTrans().getQuoteId());
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('SAVE_QUOTE')")
    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void createProductRisk(QuoteRiskTrans risk) throws BadRequestException, IOException {
        if (risk.getBindCode() == null) throw new BadRequestException("Binder is Mandatory");
        if (risk.getSclCode() == null) throw new BadRequestException("Sub Class is Mandatory");
        if (risk.getCoverCode() == null) throw new BadRequestException("Cover Type is Mandatory");
        if (risk.getInsuredCode() == null) throw new BadRequestException("Insured is Mandatory");
        if (risk.getProductCode() == null) throw new BadRequestException("Product is Mandatory");
        List<Object[]> proTransList = quotProductsRepo.getQuotProductDetails(risk.getProductCode());
        final Object[] proTrans = proTransList.get(0);
        final Long quotProdId = ((BigInteger) proTrans[0]).longValue();
        final Long quotId = ((BigInteger) proTrans[2]).longValue();
        Date polWef = ((Date) proTrans[9]);
        Date polWet = ((Date) proTrans[8]);
        risk.setBinder(binderRepo.findOne(risk.getBindCode()));
        risk.setCovertype(coverRepo.findOne(risk.getCoverCode()));
        risk.setSubclass(subclassRepo.findOne(risk.getSclCode()));
        risk.setWefDate(polWef);
        risk.setWetDate(polWet);
        if (risk.getInsuredType().equalsIgnoreCase("C")) {
            risk.setInsured(clientRepo.findOne(risk.getInsuredCode()));
        } else if (risk.getInsuredType().equalsIgnoreCase("P")) {
            risk.setProspect(prospectsRepo.findOne(risk.getInsuredCode()));
        }

        risk.setBinderDetails(binderDetRepo.findOne(risk.getBinderDet()));
        List<Object[]> quotProds = quotProductsRepo.getProductDetailsByPrdId(quotProdId);
        QuoteProTrans quoteProTrans = new QuoteProTrans();
        for (Object[] quotProd : quotProds) {
            quoteProTrans.setQuoteProductId(((BigInteger) quotProd[0]).longValue());
            quoteProTrans.setBasicPrem((BigDecimal) quotProd[1]);
            quoteProTrans.setCalcPremium((BigDecimal) quotProd[2]);
            quoteProTrans.setCommAmt((BigDecimal) quotProd[3]);
            quoteProTrans.setCommRate((BigDecimal) quotProd[4]);
            quoteProTrans.setConverted((String) quotProd[5]);
            quoteProTrans.setExtras((BigDecimal) quotProd[6]);
            quoteProTrans.setFreeLimit((BigDecimal) quotProd[7]);
            quoteProTrans.setNetPrem((BigDecimal) quotProd[8]);
            quoteProTrans.setPhfFund((BigDecimal) quotProd[9]);
            quoteProTrans.setPremium((BigDecimal) quotProd[10]);
            quoteProTrans.setStampDuty((BigDecimal) quotProd[11]);
            quoteProTrans.setSumInsured((BigDecimal) quotProd[12]);
            quoteProTrans.setTrainingLevy((BigDecimal) quotProd[13]);
            quoteProTrans.setWefDate((Date) quotProd[14]);
            quoteProTrans.setWetDate((Date) quotProd[15]);
            quoteProTrans.setWhtax((BigDecimal) quotProd[16]);
            if (quotProd[17] != null) {
                Long acctId = ((BigInteger) quotProd[17]).longValue();
                AccountDef accountDef = accountRepo.findOne(QAccountDef.accountDef.acctId.eq(acctId));
                quoteProTrans.setAgent(accountDef);
            }
            Long bindId = ((BigInteger) quotProd[18]).longValue();
            if (bindId != null) {
                BindersDef bindersDef = binderRepo.findOne(QBindersDef.bindersDef.binId.eq(bindId));
                quoteProTrans.setBinder(bindersDef);
            }
            if (quotProd[19] != null) {
                Long polId = ((BigInteger) quotProd[19]).longValue();
                PolicyTrans policyTrans = policyRepo.findOne(QPolicyTrans.policyTrans.policyId.eq(polId));
                quoteProTrans.setConvertedReference(policyTrans);
            }
            if (quotProd[20] != null) {
                Long prdId = ((BigInteger) quotProd[20]).longValue();
                ProductsDef productsDef = productRepo.findOne(QProductsDef.productsDef.proCode.eq(prdId));
                quoteProTrans.setProduct(productsDef);
            }
            if (quotProd[21] != null) {
                Long quoteId = ((BigInteger) quotProd[21]).longValue();
                QuoteTrans quoteTrans = quotTransRepo.findOne(QQuoteTrans.quoteTrans.quoteId.eq(quoteId));
                quoteProTrans.setQuoteTrans(quoteTrans);
            }
            quoteProTrans.setSelectedProduct((String) quotProd[22]);
        }
        QuoteProTrans savedTrans = quotProductsRepo.save(quoteProTrans);
        risk.setProductTrans(savedTrans);
        boolean newRisk = risk.getRiskId() == null;
        if (newRisk) {
            List<QuotRiskLimits> sectionTransactions = new ArrayList<>();
            for (RiskSectionBean sectionbean : risk.getSections()) {
                SectionsDef sectiondef = setupSectionRepo.findOne(sectionbean.getSection());
                QuotRiskLimits section = new QuotRiskLimits();
                if (sectionbean.getRatesApplicable() != null && "Y".equalsIgnoreCase(sectionbean.getRatesApplicable())) {
                    List<PremRatesDef> premRates = premRatesRepo.getSectPremiumRates(risk.getBinderDetails().getDetId(), sectionbean.getAmount(), sectiondef.getId());
                    if (premRates.size() == 0)
                        throw new BadRequestException("No Premium Rates for the Value entered...");
                    if (premRates.size() > 1)
                        throw new BadRequestException("Duplicate Prem Rates set up for the Section..." + sectionbean.getSectionDesc());
                    section.setPremRates(premRates.get(0));
                    section.setRate(premRates.get(0).getRate());
                } else {
                    List<PremRatesDef> premRates = premRatesRepo.getSectPremiumRates(risk.getBinderDetails().getDetId(), sectiondef.getId());
                    if (premRates.size() == 0)
                        throw new BadRequestException("No Premium Rates for the Value entered...");
                    if (premRates.size() > 1)
                        throw new BadRequestException("Duplicate Prem Rates set up for the Section..." + sectionbean.getSectionDesc());
                    section.setPremRates(premRates.get(0));
                    section.setRate(sectionbean.getRate());
                }
                section.setAmount((sectionbean.getAmount() == null) ? BigDecimal.ZERO : sectionbean.getAmount());
                section.setCompute(sectionbean.isCompute());
                section.setDivFactor(sectionbean.getDivFactor());
                section.setFreeLimit(sectionbean.getFreeLimit());
                section.setCompute(true);
                section.setSection(sectiondef);
                section.setRisk(risk);
                sectionTransactions.add(section);
            }
            quotRiskLimitsRepo.save(sectionTransactions);
        }
        quotRiskRepo.save(risk);
        try {
            this.computeQuotPrem(quotId);
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('SAVE_QUOTE')")
    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void createQuotProduct(CreateQuoteProductDTO createQuoteProductDTO) throws BadRequestException {
        if (createQuoteProductDTO.getBindCode() == null) {
            throw new BadRequestException("Binder is compulsory");
        }
        if (createQuoteProductDTO.getAgentId() == null) {
            throw new BadRequestException("Intermediary is compulsory");
        }
        if (createQuoteProductDTO.getProdId() == null) {
            throw new BadRequestException("Product is compulsory");
        }
        QuoteTrans quoteTrans = quotTransRepo.findOne(QQuoteTrans.quoteTrans.quoteId.eq(createQuoteProductDTO.getQuoteId()));
        if (quoteTrans == null) {
            throw new BadRequestException("Select A Valid Quotation...");
        }
        final QuoteProTrans quoteProduct = new QuoteProTrans();
        quoteProduct.setQuoteProductId(createQuoteProductDTO.getQuoteProductId());
        final Date startDate = quoteTrans.getWefDate();
        final Date endDate = quoteTrans.getWetDate();
        quoteProduct.setQuoteTrans(quoteTrans);
        quoteProduct.setBinder(binderRepo.findOne(createQuoteProductDTO.getBindCode()));
        quoteProduct.setProduct(productRepo.findOne(createQuoteProductDTO.getProdId()));
        quoteProduct.setAgent(accountRepo.findOne(createQuoteProductDTO.getAgentId()));
        quoteProduct.setWefDate(startDate);
        quoteProduct.setWetDate(endDate);

        if (createQuoteProductDTO.getRiskBean() != null) {
            RiskTransBean riskBean = createQuoteProductDTO.getRiskBean();

            QuoteRiskTrans risk = new QuoteRiskTrans();
            if (riskBean.getBindCode() == null) throw new BadRequestException("Binder is Mandatory");
            if (riskBean.getSclCode() == null) throw new BadRequestException("Sub Class is Mandatory");
            if (riskBean.getCoverCode() == null) throw new BadRequestException("Cover Type is Mandatory");
            if (riskBean.getInsuredCode() == null) throw new BadRequestException("Insured is Mandatory");
            risk.setBinder(binderRepo.findOne(riskBean.getBindCode()));
            risk.setCovertype(coverRepo.findOne(riskBean.getCoverCode()));
            risk.setSubclass(subclassRepo.findOne(riskBean.getSclCode()));
            if (quoteTrans.getClientType() == null || "C".equalsIgnoreCase(quoteTrans.getClientType()))
                risk.setInsured(clientRepo.findOne(riskBean.getInsuredCode()));
            else
                risk.setProspect(prospectsRepo.findOne(riskBean.getInsuredCode()));
            risk.setBinderDetails(binderDetRepo.findOne(riskBean.getBinderDet()));
            risk.setCommRate(riskBean.getCommRate());
            risk.setProrata(riskBean.getProrata());
            risk.setRiskDesc(riskBean.getRiskDesc());
            risk.setRiskShtDesc(riskBean.getRiskShtDesc());
            risk.setWefDate(startDate);
            risk.setWetDate(endDate);
            risk.setProductTrans(quoteProduct);
            List<QuotRiskLimits> sectionTransactions = new ArrayList<>();
            for (RiskSectionBean sectionbean : createQuoteProductDTO.getSections()) {
                SectionsDef sectiondef = setupSectionRepo.findOne(sectionbean.getSection());
                QuotRiskLimits section = new QuotRiskLimits();
                if (sectionbean.getRatesApplicable() != null && "Y".equalsIgnoreCase(sectionbean.getRatesApplicable())) {
                    List<PremRatesDef> premRates = premRatesRepo.getSectPremiumRates(risk.getBinderDetails().getDetId(), sectionbean.getAmount(), sectiondef.getId());
                    if (premRates.size() == 0)
                        throw new BadRequestException("No Premium Rates for the Value entered...");
                    if (premRates.size() > 1)
                        throw new BadRequestException("Duplicate Prem Rates set up for the Section..." + sectionbean.getSectionDesc());
                    section.setPremRates(premRates.get(0));
                    section.setRate(premRates.get(0).getRate());
                } else {
                    List<PremRatesDef> premRates = premRatesRepo.getSectPremiumRates(risk.getBinderDetails().getDetId(), sectiondef.getId());
                    if (premRates.size() == 0)
                        throw new BadRequestException("No Premium Rates for the Value entered...");
                    if (premRates.size() > 1)
                        throw new BadRequestException("Duplicate Prem Rates set up for the Section..." + sectionbean.getSectionDesc());
                    section.setPremRates(premRates.get(0));
                    section.setRate(sectionbean.getRate());
                }
                section.setAmount((sectionbean.getAmount() == null) ? BigDecimal.ZERO : sectionbean.getAmount());
                section.setCompute(sectionbean.isCompute());
                section.setDivFactor(sectionbean.getDivFactor());
                section.setFreeLimit(sectionbean.getFreeLimit());
                section.setCompute(true);

                section.setSection(sectiondef);
                section.setRisk(risk);
                sectionTransactions.add(section);
            }
            quotRiskLimitsRepo.save(sectionTransactions);
            quotRiskRepo.save(risk);
        }
        quotProductsRepo.save(quoteProduct);

    }

    @PreAuthorize("hasAnyAuthority('MAKE_READY_QUOTE')")
    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void undoMakeReady(Long quoteCode) throws BadRequestException {
        QuoteTrans quoteTrans = quotTransRepo.findOne(QQuoteTrans.quoteTrans.quoteId.eq(quoteCode));
        if (quoteTrans == null) throw new BadRequestException("No Quotation Transaction to Undo make Ready");
        if (quoteTrans.getQuoteType().equalsIgnoreCase("comparison")) {
            final List<QuoteProTrans> allProducts = this.findCompQuotProductsByQuoteId(quoteCode);
            BigDecimal totalBasicPremium = allProducts.stream()
                    .map(QuoteProTrans::getBasicPrem)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalCommAmt = allProducts.stream()
                    .map(QuoteProTrans::getCommAmt)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalExtras = allProducts.stream()
                    .map(QuoteProTrans::getExtras)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalNetPrem = allProducts.stream()
                    .map(QuoteProTrans::getNetPrem)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalPhcf = allProducts.stream()
                    .map(QuoteProTrans::getPhfFund)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalPremium = allProducts.stream()
                    .map(QuoteProTrans::getPremium)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalStampDuty = allProducts.stream()
                    .map(QuoteProTrans::getStampDuty)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalSumInsured = allProducts.stream()
                    .map(QuoteProTrans::getSumInsured)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalTrainingLevy = allProducts.stream()
                    .map(QuoteProTrans::getBasicPrem)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            quoteTrans.setBasicPrem(totalBasicPremium);
            quoteTrans.setCommAmt(totalCommAmt);
            quoteTrans.setExtras(totalExtras);
            quoteTrans.setNetPrem(totalNetPrem);
            quoteTrans.setPhcf(totalPhcf);
            quoteTrans.setPremium(totalPremium);
            quoteTrans.setStampDuty(totalStampDuty);
            quoteTrans.setSumInsured(totalSumInsured);
            quoteTrans.setTrainingLevy(totalTrainingLevy);
            quoteTrans.setQuotStatus("D");
            quotTransRepo.save(quoteTrans);

            for (QuoteProTrans product : allProducts) {
                if ("Y".equals(product.getSelectedProduct())) {
                    product.setSelectedProduct(null);
                }
                product.setWefDate(quoteTrans.getWefDate());
                product.setWetDate(quoteTrans.getWetDate());
                product.setQuoteTrans(product.getQuoteTrans());
                product.setBinder(product.getBinder());
                product.setQuoteTrans(product.getQuoteTrans());
                product.setProduct(product.getProduct());
                product.setAgent(product.getAgent());
                product.setFreeLimit(product.getFreeLimit());
                product.setCalcPremium(product.getCalcPremium());
                product.setCommRate(product.getCommRate());
                product.setConverted(product.getConverted());
                product.setWhtax(product.getWhtax());
                quotProductsRepo.save(product);
            }
        } else {
            quoteTrans.setQuotStatus("D");
            quotTransRepo.save(quoteTrans);
        }
        Map<String, Object> processVariables = Maps.newHashMap();
        processVariables.put("confirmAuth", false);
        //workflowService.completeTask(String.valueOf("Q"+quoteCode), processVariables, null, DocType.QUOTATION_DOCUMENT, (false) ? "Y" : "N", quoteTrans, null, null);

    }

    @Override
    public List<QuoteProTrans> findCompQuotProductsByQuoteId(Long quotCode) {
        List<Object[]> quoteProducts = quotProductsRepo.getUndoCompQuotProd(quotCode);
        final List<QuoteProTrans> enquireProductList = new ArrayList<>();
        for (Object[] enquire : quoteProducts) {
            QuoteProTrans quote = new QuoteProTrans();
            quote.setQuoteProductId(((BigInteger) enquire[0]).longValue());
            quote.setBasicPrem((BigDecimal) enquire[1]);
            quote.setCommAmt((BigDecimal) enquire[2]);
            quote.setExtras((BigDecimal) enquire[3]);
            quote.setNetPrem((BigDecimal) enquire[4]);
            quote.setPhfFund((BigDecimal) enquire[5]);
            quote.setPremium((BigDecimal) enquire[6]);
            quote.setStampDuty((BigDecimal) enquire[7]);
            quote.setSumInsured((BigDecimal) enquire[8]);
            quote.setTrainingLevy((BigDecimal) enquire[9]);
            BigInteger binderId = (BigInteger) enquire[10];
            BindersDef binder = binderRepo.findOne(binderId.longValue());
            quote.setBinder(binder);
            BigInteger quotId = (BigInteger) enquire[11];
            QuoteTrans quot = quotTransRepo.findOne(quotId.longValue());
            quote.setQuoteTrans(quot);
            BigInteger productId = (BigInteger) enquire[12];
            ProductsDef product = productRepo.findOne(productId.longValue());
            quote.setProduct(product);
            BigInteger agentId = (BigInteger) enquire[13];
            AccountDef account = accountRepo.findOne(agentId.longValue());
            quote.setAgent(account);
            quote.setFreeLimit((BigDecimal) enquire[14]);
            quote.setCalcPremium((BigDecimal) enquire[15]);
            quote.setCommRate((BigDecimal) enquire[16]);
            quote.setConverted((String) enquire[17]);
            quote.setWhtax((BigDecimal) enquire[18]);
            enquireProductList.add(quote);

        }
        return enquireProductList;
    }

    @PreAuthorize("hasAnyAuthority('MAKE_READY_QUOTE')")
    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void MakeQuotReady(Long quoteCode) throws BadRequestException {
        QuoteTrans quoteTrans = quotTransRepo.findOne(QQuoteTrans.quoteTrans.quoteId.eq(quoteCode));
        if (quoteTrans == null) throw new BadRequestException("Quotation cannot be null...Cannot Make Ready");
        if (!quoteTrans.getQuotStatus().equalsIgnoreCase("D")) {
            throw new BadRequestException("Cannot Make Ready.. The Quote is not Draft");
        }
        if (!authLimits.checkAuthorizationLimits("MAKE_READY_QUOTE", quoteTrans.getBasicPrem())) {
            throw new BadRequestException("You have no rights to make ready the transaction...Check your authorization limits..");
        }
        quoteTrans.setQuotStatus("R");
        Map<String, Object> processVariables = Maps.newHashMap();
        processVariables.put("canAuthorize", true);
        processVariables.put("confirmAuth", false);
        processVariables.put("clientRequest", false);
        //  workflowService.completeTask(String.valueOf("Q"+quoteCode), processVariables, null, DocType.QUOTATION_DOCUMENT, (false) ? "Y" : "N", quoteTrans, null, null);

        quotTransRepo.save(quoteTrans);
    }

    @PreAuthorize("hasAnyAuthority('MAKE_READY_QUOTE')")
    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void MakeCompQuotReady(Long quoteCode, Long quoteProductId) throws BadRequestException {
        QuoteTrans quoteTrans = quotTransRepo.findOne(QQuoteTrans.quoteTrans.quoteId.eq(quoteCode));
        QuoteProTrans quoteProTrans = quotProductsRepo.findByQuoteProductId(quoteProductId);
        if (quoteTrans == null) throw new BadRequestException("Quotation cannot be null...Cannot Make Ready");
        if (!quoteTrans.getQuotStatus().equalsIgnoreCase("D")) {
            throw new BadRequestException("Cannot Make Ready.. The Quote is not Draft");
        }
        if (!authLimits.checkAuthorizationLimits("MAKE_READY_QUOTE", quoteProTrans.getBasicPrem())) {
            throw new BadRequestException("You have no rights to make ready the transaction...Check your authorization limits..");
        }
        quoteTrans.setBasicPrem(quoteProTrans.getBasicPrem());
        quoteTrans.setCommAmt(quoteProTrans.getCommAmt());
        quoteTrans.setExtras(quoteProTrans.getExtras());
        quoteTrans.setNetPrem(quoteProTrans.getNetPrem());
        quoteTrans.setPhcf(quoteProTrans.getPhfFund());
        quoteTrans.setPremium(quoteProTrans.getPremium());
        quoteTrans.setStampDuty(quoteProTrans.getStampDuty());
        quoteTrans.setSumInsured(quoteProTrans.getSumInsured());
        quoteTrans.setTrainingLevy(quoteProTrans.getTrainingLevy());
        quoteTrans.setQuotStatus("R");
        Map<String, Object> processVariables = Maps.newHashMap();
        processVariables.put("canAuthorize", true);
        processVariables.put("confirmAuth", false);
        processVariables.put("clientRequest", false);
        //  workflowService.completeTask(String.valueOf("Q"+quoteCode), processVariables, null, DocType.QUOTATION_DOCUMENT, (false) ? "Y" : "N", quoteTrans, null, null);
        quotTransRepo.save(quoteTrans);
        quoteProTrans.setSelectedProduct("Y");
        quotProductsRepo.save(quoteProTrans);
    }


    @PreAuthorize("hasAnyAuthority('CLOSE_QUOTE')")
    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void cancelQuote(Long quoteCode, String comments) throws BadRequestException {
        if (StringUtils.isBlank(StringUtils.trim(comments))) {
            throw new BadRequestException("Provide Reason for closing the quote");
        }
        QuoteTrans quoteTrans = quotTransRepo.findOne(quoteCode);
        if (quoteTrans == null) throw new BadRequestException("Quotation cannot be null...Cannot Make Ready");
        if (!authLimits.checkAuthorizationLimits("CLOSE_QUOTE", quoteTrans.getBasicPrem())) {
            throw new BadRequestException("You have no rights to close the transaction...Check your authorization limits..");
        }

        if ((quoteTrans.getQuotStatus().equalsIgnoreCase("A")) || (quoteTrans.getQuotStatus().equalsIgnoreCase("C"))) {
            quoteTrans.setQuotStatus("CL");
            quoteTrans.setCancelReasons(comments);
        } else
            throw new BadRequestException("Cannot Close Quote.. The Quote is not Authorized " + quoteTrans.getQuotStatus());


        Iterable<QuoteProTrans> quoteProTrans = quotProductsRepo.findAll(QQuoteProTrans.quoteProTrans.quoteTrans.quoteId.eq(quoteCode));
        for (QuoteProTrans quoteProduct : quoteProTrans) {
            PolicyTrans policyTrans = quoteProduct.getConvertedReference();
            if (policyTrans != null) {
                Long activeTrans = policyRepo.count(QPolicyTrans.policyTrans.currentStatus.in("A", "D").and(QPolicyTrans.policyTrans.polNo.eq(policyTrans.getPolNo())));
                if (activeTrans > 0)
                    throw new BadRequestException("Cannot Cancel A Quote with A Pending Policy Transaction");
            }
        }


        quotTransRepo.save(quoteTrans);
    }

    @PreAuthorize("hasAnyAuthority('CONFIRM_QUOTE')")
    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void confirmQuote(Long quoteCode) throws BadRequestException {

        System.out.println("quotation to confirm=" + quoteCode);
        long count = medCount(quoteCode);
        QuoteTrans quoteTrans;
        if (count > 0) {
            quoteTrans = quotTransRepo.findOne(QQuoteTrans.quoteTrans.quoteId.eq(quoteCode));
        } else {
            quoteTrans = medQuoteTransRepo.findOne(quoteCode);
        }
        if (quoteTrans == null) throw new BadRequestException("Quotation cannot be null...Cannot Make Ready");
        if (!quoteTrans.getQuotStatus().equalsIgnoreCase("A")) {
            throw new BadRequestException("Cannot Confirm.. The Quote is not Authorized");
        }
        if (!authLimits.checkAuthorizationLimits("CONFIRM_QUOTE", quoteTrans.getBasicPrem())) {
            throw new BadRequestException("You have no rights to confirm the transaction...Check your authorization limits..");
        }
        quoteTrans.setQuotStatus("C");
        quotTransRepo.save(quoteTrans);
    }

    @PreAuthorize("hasAnyAuthority('AUTHORIZE_QUOTE')")
    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void authorizeQuote(Long quoteCode) throws BadRequestException {
        QuoteTrans quoteTrans = quotTransRepo.findOne(QQuoteTrans.quoteTrans.quoteId.eq(quoteCode));
        if (quoteTrans == null) throw new BadRequestException("Quotation cannot be null...Cannot Make Ready");
        if (!quoteTrans.getQuotStatus().equalsIgnoreCase("R")) {
            throw new BadRequestException("Cannot Authorize Quote. The Quote is not Ready");
        }
        if (!authLimits.checkAuthorizationLimits("AUTHORIZE_QUOTE", quoteTrans.getBasicPrem())) {
            throw new BadRequestException("You have no rights to authorize the transaction...Check your authorization limits..");
        }
        quoteTrans.setQuotStatus("A");
        quoteTrans.setAuthorisedBy(userUtils.getCurrentUser());
        quoteTrans.setQuotAuthDate(new Date());
        Map<String, Object> processVariables = Maps.newHashMap();
        processVariables.put("confirmAuth", true);
        //  workflowService.completeTask(String.valueOf("Q"+quoteCode), processVariables, null, DocType.QUOTATION_DOCUMENT, (false) ? "Y" : "N", quoteTrans, null, null);
        quotTransRepo.save(quoteTrans);
    }

    @PreAuthorize("hasAnyAuthority('CONVERT_QUOT_PRODUCT')")
    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public QuoteProspectResult convertQuoteProduct(ConvertQuotDTO convertQuotDTO) throws BadRequestException, IOException {
        if (convertQuotDTO.getQuotProdId() == null) throw new BadRequestException("No Quote Product to Convert");
        if (convertQuotDTO.getStartDate() == null) throw new BadRequestException("Select the Policy start Date");
        // Adjust start date for holidays
        ZoneId zone = ZoneId.systemDefault();
        LocalDate originalStartDate = convertQuotDTO.getStartDate().toInstant().atZone(zone).toLocalDate();
        LocalDate adjustedStartDate = HolidayUtils.getAdjustedTransactionDate(originalStartDate);
        Date finalStartDate = Date.from(adjustedStartDate.atStartOfDay(zone).toInstant());
//        Date endDate = DateUtils.addDays(DateUtils.addMonths(finalStartDate, 12), -1);

        System.out.println(convertQuotDTO.getQuotProdId());
        List<Object[]> proTransList = quotProductsRepo.getQuotProductDetails(convertQuotDTO.getQuotProdId());
        if (proTransList.size() != 1) {
            throw new BadRequestException("Unable to get Quotation Product Details");
        }
        final Object[] proTrans = proTransList.get(0);
        final String converted = (String) proTrans[1];
        final BigDecimal basicPrem = (BigDecimal) proTrans[3];
        final Long quotId = ((BigInteger) proTrans[2]).longValue();
        final Long binderId = ((BigInteger) proTrans[4]).longValue();
        final Long agentId = ((BigInteger) proTrans[5]).longValue();
        final Long productId = ((BigInteger) proTrans[6]).longValue();
        final Date wetDate = ((Date) proTrans[8]);

        ProductsDef productsDef = productRepo.findOne(QProductsDef.productsDef.proCode.eq(productId));

        if (converted != null && "Y".equalsIgnoreCase(converted)) {
            throw new BadRequestException("Cannot Convert an Already Converted Quote Product");
        }
        if (!authLimits.checkAuthorizationLimits("CONVERT_QUOT_PRODUCT", basicPrem)) {
            throw new BadRequestException("You have no rights to convert the transaction...Check your authorization limits..");
        }
        QuoteTrans quote = quotTransRepo.findOne(QQuoteTrans.quoteTrans.quoteId.eq(quotId));
        if (quote.getQuotStatus() != null && !quote.getQuotStatus().equalsIgnoreCase("C")) {
            throw new BadRequestException("Can only convert a confirmed quote...");
        }
        User user = userUtils.getCurrentUser();
        final QuoteProspectResult prospectResult = new QuoteProspectResult();
        ClientDef policyClient = null;
        // System.out.println("Client Type..."+quote.getClientType());
        if (quote.getClientType() != null && "P".equalsIgnoreCase(quote.getClientType())) {
            if (quote.getProspect().getConvertedReference() == null) {
                if (quote.getProspect() == null)
                    throw new BadRequestException("Cannot convert a prospect quote without prospect");
                ProspectDef prospect = quote.getProspect();
                if ("T".equalsIgnoreCase(prospect.getStatus()))
                    throw new BadRequestException("Cannot convert a quote with terminated prospect...");

                Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("C");
                if (sequenceRepo.count(seqPredicate) == 0)
                    throw new BadRequestException("Sequence for Client Definition has not been setup");
                SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
                Long seqNumber = sequence.getNextNumber();
                final String clientNumber = sequence.getSeqPrefix() + String.format("%06d", seqNumber);
                sequence.setLastNumber(seqNumber);
                sequence.setNextNumber(seqNumber + 1);
                sequenceRepo.save(sequence);
                ClientDef clientDef = new ClientDef();
                clientDef.setFname(prospect.getFname());
                clientDef.setGender(prospect.getGender());
                clientDef.setIdNo(prospect.getIdNo());
                clientDef.setOtherNames(prospect.getOtherNames());
                clientDef.setPhoneNo(prospect.getPhoneNo());
                clientDef.setPinNo(prospect.getPinNo());
                clientDef.setRegisteredbrn(prospect.getBranch());
                clientDef.setDateregistered(new Date());
                clientDef.setTenantType(prospect.getTenantType());
                clientDef.setClientTitle(prospect.getClientTitle());
                clientDef.setCountry(prospect.getCountry());
                clientDef.setSmsPrefix(prospect.getSmsPrefix());
                clientDef.setPhonePrefix(prospect.getPhonePrefix());
                clientDef.setDob(prospect.getDob());
                clientDef.setEmailAddress(prospect.getEmailAddress());
                clientDef.setGender(prospect.getGender());
                clientDef.setClientSector(prospect.getClientSector());
                clientDef.setOccupation(prospect.getOccupation());
                clientDef.setPassportNo(prospect.getPassportNo());
                clientDef.setSmsNumber(prospect.getSmsNumber());
                clientDef.setStatus("A");
                clientDef.setTenantNumber(clientNumber);
                clientDef.setAuthBy(userUtils.getCurrentUser());
                clientDef.setAuthStatus("Y");
                clientDef.setCreatedBy(userUtils.getCurrentUser());
                clientDef.setClientHash(encoder.encodeExistingClient(clientDef));
                clientDef.setDateCreated(new Date());
                clientDef.setOfficeTel(prospect.getOfficeTel());
                clientDef.setTown(prospect.getTown());
                clientDef.setAddress(prospect.getAddress());
                clientDef.setPostalCodesDef(prospect.getPostalCodesDef());
                clientDef.setClientTitle(prospect.getClientTitle());
                try {
                    clientRulesExecutor.handleClientChecks(clientDef);
                } catch (Exception ex) {
                    prospectResult.setResult(ex.toString());
                    prospectResult.setProspectId(prospect.getTenId());
                    return prospectResult;
                }


                if (prospect.getTenantType().getClientType().equalsIgnoreCase("I")) {
//                    if (prospect.getIdNo() == null || StringUtils.isBlank(prospect.getIdNo())) {
//                        if (prospect.getPassportNo() == null || StringUtils.isBlank(prospect.getPassportNo())) {
//                            return "Client Passport Number Compulsory";
//                        }
//                    }
//                    if (prospect.getPassportNo() == null || StringUtils.isBlank(prospect.getPassportNo())) {
//                        if (prospect.getIdNo() == null || StringUtils.isBlank(prospect.getIdNo())) {
//                            return "Client ID Number Compulsory";
//                        }
//                        //return "Client Passport Number Compulsory";
//                    }
//                    if (prospect.getPinNo() == null || StringUtils.isBlank(prospect.getPinNo())) {
//                        return "Client Pin No Compulsory";
//                    }
//                    if (prospect.getFname() == null || StringUtils.isBlank(StringUtils.trim(prospect.getFname()))) {
//                        return "Client First Name compulsory";
//                    }
//                    if (prospect.getOtherNames() == null || StringUtils.isBlank(StringUtils.trim(prospect.getOtherNames()))) {
//                        return "Client Other Names Compulsory";
//                    }
//                    if (prospect.getClientTitle() == null) {
//                        return "Client Title compulsory";
//                    }
//                    if (prospect.getDob() == null) {
//                        return "Date of Birth/Date of Registration compulsory";
//                    }
//                    if (prospect.getOccupation() == null) {
//                        return "Occupation Compulsory";
//                    }
//                    if (prospect.getClientSector() == null) {
//                        return "Client Sector Compulsory";
//                    }
//                    if (prospect.getGender() == null || StringUtils.isBlank(StringUtils.trim(prospect.getGender()))) {
//                        return "Gender Compulsory";
//                    }
//                    if (prospect.getPhoneNo() == null || StringUtils.isBlank(StringUtils.trim(prospect.getPhoneNo()))) {
//                        return "Phone Number Compulsory";
//                    }
//
//                    if (prospect.getIdNo() != null) {
//                        if (clientRepo.count(QClientDef.clientDef.idNo.eq(prospect.getIdNo())) > 1) {
//                            throw new BadRequestException("Client With ID Number already exists...");
//                        }
//                    }
                    if (prospect.getPinNo() != null) {
                        if (clientRepo.count(QClientDef.clientDef.pinNo.eq(prospect.getPinNo())) > 1) {
                            throw new BadRequestException("Client With Pin Number already exists...");
                        }
                    }
                    if (!StringUtils.isBlank(prospect.getPassportNo())) {
                        if (clientRepo.count(QClientDef.clientDef.passportNo.eq(prospect.getPassportNo())) > 1) {
                            throw new BadRequestException("Client With Passport Number already exists...");
                        }
                    }
                }

                policyClient = clientRepo.save(clientDef);
                prospect.setConvertedReference(policyClient);
                prospectsRepo.save(prospect);
                Iterable<ClientDocs> clientDocs = clientDocsRepo.findAll(QClientDocs.clientDocs.prospectDef.tenId.eq(prospect.getTenId()));
                List<ClientDocs> newClientDocs = new ArrayList<>();
                for (ClientDocs clntdocs : clientDocs) {
                    ClientDocs newClientDoc = new ClientDocs();
                    newClientDoc.setCdId(clntdocs.getCdId());
                    newClientDoc.setProspectDef(clntdocs.getProspectDef());
                    newClientDoc.setCheckSum(clntdocs.getCheckSum());
                    newClientDoc.setClientDef(policyClient);
                    newClientDoc.setContentType(clntdocs.getContentType());
                    newClientDoc.setRequiredDoc(clntdocs.getRequiredDoc());
                    newClientDoc.setUploadedFileName(clntdocs.getUploadedFileName());
                    newClientDocs.add(newClientDoc);

                }
                clientDocsRepo.save(newClientDocs);

            } else {
                policyClient = quote.getProspect().getConvertedReference();
            }
        }
        final Date startDate = convertQuotDTO.getStartDate();
        Date endDate = DateUtils.addDays(DateUtils.addMonths(finalStartDate, 12), -1);
        final BindersDef bindersDef = binderRepo.findOne(QBindersDef.bindersDef.binId.eq(binderId));
        Iterable<QuoteRiskTrans> risks = quotRiskRepo.findAll(QQuoteRiskTrans.quoteRiskTrans.productTrans.quoteProductId.eq(convertQuotDTO.getQuotProdId()));
        PolicyTrans policyTrans = new PolicyTrans();
        policyTrans.setBinder(bindersDef);
        policyTrans.setAgent(accountRepo.findOne(QAccountDef.accountDef.acctId.eq(agentId)));
        policyTrans.setBranch(quote.getBranch());
        policyTrans.setBusinessType("N");
        if (quote.getClientType() != null && "P".equalsIgnoreCase(quote.getClientType())) {
            policyTrans.setClient(policyClient);
        } else
            policyTrans.setClient(quote.getClient());
        policyTrans.setCommAllowed(true);
        policyTrans.setCoverFrom(finalStartDate);
        policyTrans.setCoverTo(endDate);
        policyTrans.setCreatedUser(userUtils.getCurrentUser());
        policyTrans.setCurrentStatus("D");
        policyTrans.setFrequency("A");
        policyTrans.setInterfaceType("C");
        policyTrans.setPaymentMode(quote.getPaymentMode());
        String policyNumberFormat = paramService.getParameterString("POLICY_NO_FORMAT");
        String endorsementFormat = paramService.getParameterString("ENDORSE_NO_FORMAT");
        Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("P");
        if (sequenceRepo.count(seqPredicate) == 0)
            throw new BadRequestException("Sequence for New Business Transactions has not been defined");
        SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
        Long seqNumber = sequence.getNextNumber();
        final String policyNumber = templateMerger.generateFormat(policyNumberFormat, quote.getBranch().getObId(), productsDef.getProCode(), quote.getWefDate(), sequence.getSeqPrefix() + String.format("%05d", seqNumber), null);
        policyTrans.setPolNo(policyNumber);

        sequence.setLastNumber(seqNumber);
        sequence.setNextNumber(seqNumber + 1);
        sequenceRepo.save(sequence);
        Predicate endorsePredicate = QSystemSequence.systemSequence.transType.eq("E");
        if (sequenceRepo.count(endorsePredicate) == 0)
            throw new BadRequestException("Sequence for Endorsement Transactions has not been defined");
        SystemSequence endorseSequence = sequenceRepo.findOne(endorsePredicate);
        Long endosseqNumber = endorseSequence.getNextNumber();
        final String revNumber = endorseSequence.getSeqPrefix() + String.format("%05d", endosseqNumber);
        final String endorseNumber = templateMerger.generateFormat(endorsementFormat, quote.getBranch().getObId(), productsDef.getProCode(), quote.getWefDate(), revNumber, null);
        policyTrans.setPolRevNo(endorseNumber + "/1");
        policyTrans.setRevisionFormat(endorseNumber);
        endorseSequence.setLastNumber(endosseqNumber);
        endorseSequence.setNextNumber(endosseqNumber + 1);
        sequenceRepo.save(endorseSequence);
        policyTrans.setTransType("NB");
        policyTrans.setPolRevStatus("NB");
        policyTrans.setAuthStatus("D");
        policyTrans.setPreviousTrans(policyTrans);
        policyTrans.setCommAllowed(true);
        //policyTrans.setClient(quote.getClient());
        policyTrans.setAgent(accountRepo.findOne(QAccountDef.accountDef.acctId.eq(agentId)));
        policyTrans.setBinder(bindersDef);
        policyTrans.setBranch(quote.getBranch());
        policyTrans.setSubAgent(quote.getSubAgent());
        policyTrans.setMarketerAgent(quote.getMarketerAgent());
        policyTrans.setLeadsMan(quote.getLeadsMan());
        policyTrans.setIntroducerAgent(quote.getIntroducerAgent());
        policyTrans.setAbsaNoSubAgent(quote.getAbsaNoSubAgent());
        policyTrans.setAbsaNoIntroducer(quote.getAbsaNoIntroducer());
        policyTrans.setAbsaNoMarketer(quote.getAbsaNoMarketer());
        policyTrans.setAbsaNoLeadsMan(quote.getAbsaNoLeadsMan());
        policyTrans.setPaymentMode(quote.getPaymentMode());
        policyTrans.setProduct(productsDef);
        policyTrans.setCreatedUser(user);
        policyTrans.setTransCurrency(quote.getTransCurrency());
        policyTrans.setPolCreateddt(new Date());
        policyTrans.setRenewable(productsDef.isRenewable());
        policyTrans.setUwYear(dateUtils.getUwYear(finalStartDate));
        policyTrans.setRenewalDate(org.apache.commons.lang.time.DateUtils.addDays(wetDate, 1));
        policyTrans.setCoverFrom(finalStartDate);
        policyTrans.setCoverTo(endDate);
        policyTrans.setWefDate(finalStartDate);
        policyTrans.setWetDate(endDate);
        PolicyTrans savedPolicy = policyRepo.save(policyTrans);
        quote.setConvertedReference(savedPolicy);
        quotTransRepo.save(quote);
        premComputeService.computePrem(policyTrans.getPolicyId());
        SystemTrans transaction = new SystemTrans();
        transaction.setDoneDate(new Date());
        transaction.setDoneBy(userUtils.getCurrentUser());
        transaction.setPolicy(policyTrans);
        transaction.setTransLevel("U");
        transaction.setTransCode("NBD"); //A way to setup and look up for transaction transcode
        transaction.setTransAuthorised("N");
        List<RiskTrans> policyRisks = new ArrayList<>();
        for (QuoteRiskTrans quotRiskBean : risks) {
            Date polWef = dateUtils.removeTime(finalStartDate);
            Date polWet = dateUtils.removeTime(endDate);
            Date riskWef = dateUtils.removeTime(finalStartDate);
            Date riskWet = dateUtils.removeTime(endDate);
            RiskTrans risk = new RiskTrans();
            risk.setWefDate(finalStartDate);
            risk.setWetDate(endDate);
            risk.setCovertype(quotRiskBean.getCovertype());
            risk.setQuoteRisk(quotRiskBean);
            risk.setBinderDetails(quotRiskBean.getBinderDetails());
            risk.setBinder(quotRiskBean.getBinder());
            risk.setSubclass(quotRiskBean.getSubclass());
            risk.setAutogenCert("N");
//            risk.setBinderDet(quotRiskBean.getBinderDet());
            risk.setCommRate(quotRiskBean.getCommRate());
            if (quote.getClientType() != null && "P".equalsIgnoreCase(quote.getClientType())) {
                if (quotRiskBean.getProspect().getTenId() == quote.getProspect().getTenId())
                    risk.setInsured(policyClient);
                else {
                    ProspectDef prospect = quotRiskBean.getProspect();
                    if (prospect.getConvertedReference() == null) {
                        if ("T".equalsIgnoreCase(prospect.getStatus()))
                            throw new BadRequestException("Cannot convert a quote with terminated prospect as one of the risks...");
                        if (prospect.getTenantType().getClientType().equalsIgnoreCase("I")) {
                            if (prospect.getIdNo() == null || StringUtils.isBlank(prospect.getIdNo())) {
                                throw new BadRequestException("Cannot convert the Risk Insured..Risk Prospect has no ID Details.");
                            }
                            if (prospect.getPinNo() == null || StringUtils.isBlank(prospect.getPinNo())) {
                                throw new BadRequestException("Cannot convert the Risk Insured..Risk Prospect has no Pin Details.");
                            }
                            if (prospect.getFname() == null || StringUtils.isBlank(StringUtils.trim(prospect.getFname()))) {
                                throw new BadRequestException("Cannot convert the Risk Insured..Risk Prospect has no First Name Details.");
                            }
                            if (prospect.getOtherNames() == null || StringUtils.isBlank(StringUtils.trim(prospect.getOtherNames()))) {
                                throw new BadRequestException("Cannot convert the Risk Insured..Risk Prospect has no Other Name Details.");
                            }
                        }
                        seqPredicate = QSystemSequence.systemSequence.transType.eq("C");
                        if (sequenceRepo.count(seqPredicate) == 0)
                            throw new BadRequestException("Sequence for Client Definition has not been setup");
                        sequence = sequenceRepo.findOne(seqPredicate);
                        seqNumber = sequence.getNextNumber();
                        final String clientNumber = sequence.getSeqPrefix() + String.format("%06d", seqNumber);
                        sequence.setLastNumber(seqNumber);
                        sequence.setNextNumber(seqNumber + 1);
                        sequenceRepo.save(sequence);
                        ClientDef clientDef = new ClientDef();
                        clientDef.setFname(prospect.getFname());
                        clientDef.setGender(prospect.getGender());
                        clientDef.setIdNo(prospect.getIdNo());
                        clientDef.setOtherNames(prospect.getOtherNames());
                        clientDef.setPhoneNo(prospect.getPhoneNo());
                        clientDef.setPinNo(prospect.getPinNo());
                        clientDef.setRegisteredbrn(prospect.getRegisteredbrn());
                        clientDef.setDateregistered(new Date());
                        clientDef.setTenantType(prospect.getTenantType());
                        clientDef.setClientTitle(prospect.getClientTitle());
                        clientDef.setCountry(prospect.getCountry());
                        clientDef.setSmsPrefix(prospect.getSmsPrefix());
                        clientDef.setPhonePrefix(prospect.getPhonePrefix());
                        clientDef.setDob(prospect.getDob());
                        clientDef.setEmailAddress(prospect.getEmailAddress());
                        clientDef.setGender(prospect.getGender());
                        clientDef.setClientSector(prospect.getClientSector());
                        clientDef.setOccupation(prospect.getOccupation());
                        clientDef.setPassportNo(prospect.getPassportNo());
                        clientDef.setSmsNumber(prospect.getSmsNumber());
                        clientDef.setStatus("A");
                        clientDef.setTenantNumber(clientNumber);
                        ClientDef savedClient = clientRepo.save(clientDef);
                        prospect.setConvertedReference(savedClient);
                        prospectsRepo.save(prospect);
                        risk.setInsured(savedClient);
                    } else {
                        risk.setInsured(prospect.getConvertedReference());
                    }
                }
            } else
                risk.setInsured(quotRiskBean.getInsured());
            risk.setPolicy(savedPolicy);
            risk.setProrata(quotRiskBean.getProrata());
            risk.setRiskDesc(quotRiskBean.getRiskDesc());
            risk.setRiskShtDesc(quotRiskBean.getRiskShtDesc());
            risk.setTransType("NB");
            policyRisks.add(risk);

        }
        riskRepo.save(policyRisks);
//            List<PolicyClauses> polClauses = new ArrayList<>();
//            Iterable<QuotClauses> quoteClauses = quotClausesRepo.findAll(QQuotClauses.quotClauses.proTrans.quoteProductId.eq(convertQuotDTO.getQuotProdId()));
//            for (QuotClauses clause : quoteClauses) {
//                PolicyClauses policyClauses = new PolicyClauses();
//                policyClauses.setPolicy(savedPolicy);
//                policyClauses.setClauHeading(clause.getClauHeading());
//                policyClauses.setClause(clause.getClause());
//                policyClauses.setClauWording(clause.getClauWording());
//                policyClauses.setEditable(clause.isEditable());
//                polClauses.add(policyClauses);
//            }
//            polClausesRepo.save(polClauses);
        Iterable<RiskTrans> createdRisks = riskRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(savedPolicy.getPolicyId()));
        List<SectionTrans> sectionTransactions = new ArrayList<>();
        for (QuoteRiskTrans quotRiskBean : risks) {
            Iterable<QuotRiskLimits> sections = quotRiskLimitsRepo.findAll(QQuotRiskLimits.quotRiskLimits.risk.riskId.eq(quotRiskBean.getRiskId()));
            for (QuotRiskLimits riskLimits : sections) {

                SectionsDef sectiondef = riskLimits.getSection();
                SectionTrans section = new SectionTrans();
                section.setPremRates(riskLimits.getPremRates());
                section.setAmount(riskLimits.getAmount());
                section.setCompute(riskLimits.isCompute());
                section.setDivFactor(riskLimits.getDivFactor());
                section.setFreeLimit(riskLimits.getFreeLimit());
                section.setAnnualEarnings(riskLimits.getAnnualEarnings());
                section.setMultiRate(riskLimits.getMultiRate());
                section.setCompute(true);
                section.setRate(riskLimits.getRate());
                section.setSection(sectiondef);
                createdRisks.forEach(a -> {
                    if (a.getQuoteRisk().getRiskId() == quotRiskBean.getRiskId()) {
                        section.setRisk(a);
                    }
                });
                sectionTransactions.add(section);
            }
        }
        sectionRepo.save(sectionTransactions);

        List<RiskDocs> riskDocs = new ArrayList<>();
        for (QuoteRiskTrans savedRisk : risks) {
            Iterable<BinderReqrdDocs> reqdDocs = reqrdDocsRepo.findAll(QBinderReqrdDocs.binderReqrdDocs.binderDetail.detId.eq(savedRisk.getBinderDetails().getDetId())
                    .and(QBinderReqrdDocs.binderReqrdDocs.mandatory.eq(true))
                    .and(QBinderReqrdDocs.binderReqrdDocs.requiredDocs.requiredDoc.appliesNewBusiness.eq(true)));
            for (BinderReqrdDocs reqdDoc : reqdDocs) {
                RiskDocs riskDoc = new RiskDocs();
                riskDoc.setReqdDocs(reqdDoc.getRequiredDocs());
                createdRisks.forEach(a -> {
                    if (a.getQuoteRisk().getRiskId() == savedRisk.getRiskId()) {
                        riskDoc.setRisk(a);
                    }
                });

                riskDocs.add(riskDoc);
            }
        }
        riskDocsRepo.save(riskDocs);
        for (QuoteRiskTrans savedRisk : risks) {
            long riskIdentifier = Long.valueOf(String.valueOf(dateUtils.getUwYear(quote.getWefDate())) + String.valueOf(savedRisk.getRiskId()));
            PolicyActiveRisks activeRisk = new PolicyActiveRisks();
            activeRisk.setPolicy(policyTrans);
            createdRisks.forEach(a -> {
                if (a.getQuoteRisk().getRiskId() == savedRisk.getRiskId()) {
                    activeRisk.setRisk(a);
                }
            });
            activeRisk.setRiskIdentifier(riskIdentifier);
            activeRisksRepo.save(activeRisk);
            createdRisks.forEach(a -> {
                if (a.getQuoteRisk().getRiskId() == savedRisk.getRiskId()) {
                    a.setRiskIdentifier(riskIdentifier);
                }
            });
        }
        riskRepo.save(createdRisks);
        transRepo.save(transaction);
        try {
            premComputeService.computePrem(savedPolicy.getPolicyId());
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }
        workflowService.startNewWorkFlow(DocType.GEN_UW_DOCUMENT, String.valueOf(savedPolicy.getPolicyId()), savedPolicy, "N", null, null, null, savedPolicy.getClient());
//        workflowService.startNewWorkFlow(DocType.GEN_UW_DOCUMENT, String.valueOf(savedPolicy.getPolicyId()), savedPolicy, "N", null, null, null);
        quotProductsRepo.updateQuotStatus("Y", savedPolicy.getPolicyId(), convertQuotDTO.getQuotProdId());
        prospectResult.setResult("Y");
        prospectResult.setPolId(savedPolicy.getPolicyId());
        return prospectResult;
    }

    @PreAuthorize("hasAnyAuthority('CONVERT_QUOT_PRODUCT')")
    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public QuoteProspectResult convertMedQuote(Long quotCode) throws BadRequestException {
        if (quotCode == null) throw new BadRequestException("No Quote to Convert");
        QuoteTrans quote = quotTransRepo.findOne(quotCode);
        if (quote.getQuotStatus() != null && "CV".equalsIgnoreCase(quote.getQuotStatus())) {
            throw new BadRequestException("Cannot Convert an Already Converted Quote");
        }
        if (!authLimits.checkAuthorizationLimits("CONVERT_QUOT_PRODUCT", quote.getBasicPrem())) {
            throw new BadRequestException("You have no rights to convert the transaction...Check your authorization limits..");
        }
        ClientDef policyClient = null;
        QuoteProspectResult quoteProspectResult = new QuoteProspectResult();
        if (quote.getClientType() != null && "P".equalsIgnoreCase(quote.getClientType())) {
            if (quote.getProspect().getConvertedReference() == null) {
                if (quote.getProspect() == null)
                    throw new BadRequestException("Cannot convert a prospect quote without prospect");
                ProspectDef prospect = quote.getProspect();
                if ("T".equalsIgnoreCase(prospect.getStatus()))
                    throw new BadRequestException("Cannot convert a quote with terminated prospect...");
                if (prospect.getTenantType().getClientType().equalsIgnoreCase("I")) {
                    quoteProspectResult.setProspectId(prospect.getTenId());
                    if (prospect.getIdNo() == null || StringUtils.isBlank(prospect.getIdNo())) {
                        if (prospect.getPassportNo() == null || StringUtils.isBlank(prospect.getPassportNo())) {
                            quoteProspectResult.setResult("Passport or ID is required ");
                            return quoteProspectResult;
                        }
                    }
                    if (prospect.getPassportNo() == null || StringUtils.isBlank(prospect.getPassportNo())) {
                        if (prospect.getIdNo() == null || StringUtils.isBlank(prospect.getIdNo())) {
                            quoteProspectResult.setResult("Passport or ID is required ");
                            return quoteProspectResult;
                        }
                    }
                    if (prospect.getPinNo() == null || StringUtils.isBlank(prospect.getPinNo())) {
                        quoteProspectResult.setResult("PIN is mandatory");
                        return quoteProspectResult;
                    }
                    if (prospect.getFname() == null || StringUtils.isBlank(StringUtils.trim(prospect.getFname()))) {
                        quoteProspectResult.setResult("Provide client first name");
                        return quoteProspectResult;
                    }
                    if (prospect.getOtherNames() == null || StringUtils.isBlank(StringUtils.trim(prospect.getOtherNames()))) {
                        quoteProspectResult.setResult("Provide client other names");
                        return quoteProspectResult;
                    }
                    if (prospect.getClientTitle() == null && quote.getMedicalCoverType() == "I") {
                        quoteProspectResult.setResult("Provide client title");
                        return quoteProspectResult;
                    }
                    if (prospect.getDob() == null) {
                        quoteProspectResult.setResult("Provide client date of birth");
                        return quoteProspectResult;
                    }
                    if (prospect.getOccupation() == null) {
                        quoteProspectResult.setResult("Provide client occupation");
                        return quoteProspectResult;
                    }
                    if (prospect.getClientSector() == null) {
                        quoteProspectResult.setResult("Provide client sector");
                        return quoteProspectResult;
                    }
                    if (prospect.getGender() == null || StringUtils.isBlank(StringUtils.trim(prospect.getGender()))) {
                        quoteProspectResult.setResult("Provide client gender");
                        return quoteProspectResult;
                    }
                    if (prospect.getPhoneNo() == null || StringUtils.isBlank(StringUtils.trim(prospect.getPhoneNo()))) {
                        quoteProspectResult.setResult("Provide client phone number");
                        return quoteProspectResult;
                    }
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
                ClientDef clientDef = new ClientDef();
                clientDef.setFname(prospect.getFname());
                clientDef.setGender(prospect.getGender());
                clientDef.setIdNo(prospect.getIdNo());
                clientDef.setOtherNames(prospect.getOtherNames());
                clientDef.setPhoneNo(prospect.getPhoneNo());
                clientDef.setPinNo(prospect.getPinNo());
                clientDef.setRegisteredbrn(prospect.getRegisteredbrn());
                clientDef.setDateregistered(new Date());
                clientDef.setTenantType(prospect.getTenantType());
                clientDef.setClientTitle(prospect.getClientTitle());
                clientDef.setCountry(prospect.getCountry());
                clientDef.setSmsPrefix(prospect.getSmsPrefix());
                clientDef.setPhonePrefix(prospect.getPhonePrefix());
                clientDef.setDob(prospect.getDob());
                clientDef.setEmailAddress(prospect.getEmailAddress());
                clientDef.setGender(prospect.getGender());
                clientDef.setClientSector(prospect.getClientSector());
                clientDef.setOccupation(prospect.getOccupation());
                clientDef.setPassportNo(prospect.getPassportNo());
                clientDef.setSmsNumber(prospect.getSmsNumber());
                clientDef.setStatus("A");
                clientDef.setTenantNumber(clientNumber);
                clientDef.setDateCreated(new Date());
                clientDef.setOfficeTel(prospect.getOfficeTel());
                clientDef.setTown(prospect.getTown());
                clientDef.setAddress(prospect.getAddress());
                clientDef.setPostalCodesDef(prospect.getPostalCodesDef());

                try {
                    clientRulesExecutor.handleClientChecks(clientDef);
                } catch (Exception ex) {
                    quoteProspectResult.setResult(ex.toString());
                    return quoteProspectResult;
                }
                policyClient = clientRepo.save(clientDef);

                clientRulesExecutor.handleClientChecks(clientDef);

                OrgBranch defaultBranch = null;
                for (OrgBranch branch : branchRepository.findAll()) {
                    defaultBranch = branch;
                    break;
                }
                clientDef.setRegisteredbrn(defaultBranch);
                policyClient = clientRepo.save(clientDef);

                prospect.setConvertedReference(policyClient);
                prospectsRepo.save(prospect);
                Iterable<ClientDocs> clientDocs = clientDocsRepo.findAll(QClientDocs.clientDocs.prospectDef.tenId.eq(prospect.getTenId()));
                List<ClientDocs> newClientDocs = new ArrayList<>();
                for (ClientDocs clntdocs : clientDocs) {
                    ClientDocs newClientDoc = new ClientDocs();
                    newClientDoc.setCdId(clntdocs.getCdId());
                    newClientDoc.setProspectDef(clntdocs.getProspectDef());
                    newClientDoc.setCheckSum(clntdocs.getCheckSum());
                    newClientDoc.setClientDef(policyClient);
                    newClientDoc.setContentType(clntdocs.getContentType());
                    newClientDoc.setRequiredDoc(clntdocs.getRequiredDoc());
                    newClientDoc.setUploadedFileName(clntdocs.getUploadedFileName());
                    newClientDocs.add(newClientDoc);

                }
                clientDocsRepo.save(newClientDocs);
            } else {
                policyClient = quote.getProspect().getConvertedReference();
            }
        }
        PolicyTrans policyTrans = new PolicyTrans();
        policyTrans.setBinder(quote.getBinder());
        policyTrans.setBranch(quote.getBranch());
        policyTrans.setBusinessType("N");
        if (quote.getClientType() != null && "P".equalsIgnoreCase(quote.getClientType())) {
            policyTrans.setClient(policyClient);
        } else
            policyTrans.setClient(quote.getClient());
        policyTrans.setAgent(accountRepo.findOne(quote.getBinder().getAccount().getAcctId()));
        policyTrans.setCommAllowed(true);
        policyTrans.setCoverFrom(quote.getWefDate());
        policyTrans.setCoverTo(quote.getWetDate());
        policyTrans.setMedicalCoverType(quote.getMedicalCoverType());
//        policyTrans.setIssueCard(quote.getIssueCard());
        // policyTrans.setCardType(quote.getCardType());
        policyTrans.setBinCardType(quote.getBinCardType());
        policyTrans.setCreatedUser(userUtils.getCurrentUser());
        policyTrans.setCurrentStatus("D");
        policyTrans.setFrequency("A");
        policyTrans.setInterfaceType("A");
        policyTrans.setPaymentMode(quote.getPaymentMode());
        String policyNumberFormat = paramService.getParameterString("POLICY_NO_FORMAT");
        String endorsementFormat = paramService.getParameterString("ENDORSE_NO_FORMAT");
        Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("P");
        if (sequenceRepo.count(seqPredicate) == 0)
            throw new BadRequestException("Sequence for New Business Transactions has not been defined");
        SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
        Long seqNumber = sequence.getNextNumber();
        final String policyNumber = templateMerger.generateFormat(policyNumberFormat, quote.getBranch().getObId(), quote.getProduct().getProCode(), quote.getWefDate(), sequence.getSeqPrefix() + String.format("%05d", seqNumber), null);
        policyTrans.setPolNo(policyNumber);

        sequence.setLastNumber(seqNumber);
        sequence.setNextNumber(seqNumber + 1);
        sequenceRepo.save(sequence);
        Predicate endorsePredicate = QSystemSequence.systemSequence.transType.eq("E");
        if (sequenceRepo.count(endorsePredicate) == 0)
            throw new BadRequestException("Sequence for Endorsement Transactions has not been defined");
        SystemSequence endorseSequence = sequenceRepo.findOne(endorsePredicate);
        Long endosseqNumber = endorseSequence.getNextNumber();
        final String revNumber = endorseSequence.getSeqPrefix() + String.format("%05d", endosseqNumber);
        final String endorseNumber = templateMerger.generateFormat(endorsementFormat, quote.getBranch().getObId(), quote.getProduct().getProCode(), quote.getWefDate(), revNumber, null);
        policyTrans.setPolRevNo(endorseNumber + "/1");
        policyTrans.setRevisionFormat(endorseNumber);
        endorseSequence.setLastNumber(endosseqNumber);
        endorseSequence.setNextNumber(endosseqNumber + 1);
        sequenceRepo.save(endorseSequence);
        policyTrans.setTransType("NB");
        policyTrans.setPolRevStatus("NB");
        policyTrans.setAuthStatus("D");
        policyTrans.setPreviousTrans(policyTrans);
        SystemTrans transaction = new SystemTrans();
        transaction.setDoneDate(new Date());
        transaction.setDoneBy(userUtils.getCurrentUser());
        transaction.setPolicy(policyTrans);
        transaction.setTransLevel("U");
        transaction.setTransCode("NBD"); //A way to setup and look up for transaction transcode
        transaction.setTransAuthorised("N");

        policyTrans.setCommAllowed(true);
        //policyTrans.setClient(quote.getClient());
        policyTrans.setBinder(quote.getBinder());
        policyTrans.setBranch(quote.getBranch());
        policyTrans.setPaymentMode(quote.getPaymentMode());
        policyTrans.setProduct(quote.getProduct());
        policyTrans.setCreatedUser(userUtils.getCurrentUser());
        policyTrans.setTransCurrency(quote.getTransCurrency());
        policyTrans.setPolCreateddt(new Date());
        policyTrans.setRenewable(quote.getProduct().isRenewable());
        policyTrans.setUwYear(dateUtils.getUwYear(quote.getWefDate()));
        policyTrans.setRenewalDate(org.apache.commons.lang.time.DateUtils.addDays(quote.getWetDate(), 1));
        policyTrans.setCoverFrom(quote.getWefDate());
        policyTrans.setCoverTo(quote.getWetDate());
        policyTrans.setWefDate(quote.getWefDate());
        policyTrans.setWetDate(quote.getWetDate());

        PolicyTrans savedPolicy = policyRepo.save(policyTrans);
        List<PolicyClauses> polClauses = new ArrayList<>();
        Iterable<MedQuoteClauses> quoteClauses = medQuotClausesRepo.findAll(QMedQuoteClauses.medQuoteClauses.quotation.quoteId.eq(quote.getQuoteId()));
        for (MedQuoteClauses clause : quoteClauses) {
            PolicyClauses policyClauses = new PolicyClauses();
            policyClauses.setPolicy(savedPolicy);
            policyClauses.setClauHeading(clause.getClauHeading());
            policyClauses.setClause(clause.getClause());
            policyClauses.setClauWording(clause.getClauWording());
            policyClauses.setEditable(clause.isEditable());
            polClauses.add(policyClauses);
        }
        polClausesRepo.save(polClauses);
        List<MedicalCategory> polCategories = new ArrayList<>();
        Iterable<MedicalQuoteCategory> quoteCategories = medicalQuoteCategoryRepo.findAll(QMedicalQuoteCategory.medicalQuoteCategory.quotation.quoteId.eq(quote.getQuoteId()));
        for (MedicalQuoteCategory category : quoteCategories) {
            List<CategoryRules> polCatRules = new ArrayList<>();
            List<MedCategoryBenefits> polBenefits = new ArrayList<>();
            Iterable<QuoteCategoryRules> quoteRules = categoryRulesRepo.findAll(QQuoteCategoryRules.quoteCategoryRules.category.id.eq(category.getId()));
            Iterable<MedQuotCategoryBenefits> quoteBenefits = benefitRepo.findAll(QMedQuotCategoryBenefits.medQuotCategoryBenefits.category.id.eq(category.getId()));
            BinderDetails binderDetails = category.getBinderDetails();
            MedicalCategory policyCategory = new MedicalCategory();
            policyCategory.setDesc(category.getDesc());
            policyCategory.setShtDesc(category.getShtDesc());
            policyCategory.setBinderDetails(binderDetails);
            policyCategory.setPolicy(savedPolicy);
            MedicalCategory saveCategory = medicalCategoryRepo.save(policyCategory);
            for (QuoteCategoryRules rules : quoteRules) {
                CategoryRules policyCatRules = new CategoryRules();
                policyCatRules.setDesc(rules.getDesc());
                policyCatRules.setShtDesc(rules.getShtDesc());
                policyCatRules.setBinderRules(rules.getBinderRules());
                policyCatRules.setCategory(saveCategory);
                policyCatRules.setValue(rules.getValue());
                polCatRules.add(policyCatRules);
            }
            polcategoryRulesRepo.save(polCatRules);
            for (MedQuotCategoryBenefits benefits : quoteBenefits) {
                MedCategoryBenefits policyBenefits = new MedCategoryBenefits();
                policyBenefits.setCategory(saveCategory);
                policyBenefits.setApplicableAt(benefits.getApplicableAt());
                policyBenefits.setCover(benefits.getCover());
                policyBenefits.setFundLimit(benefits.getFundLimit());
                policyBenefits.setLimit(benefits.getLimit());
                policyBenefits.setWaitPeriod(benefits.getWaitPeriod());
                polBenefits.add(policyBenefits);
            }
            polbenefitRepo.save(polBenefits);
        }
        transRepo.save(transaction);
        //workflowService.startNewWorkFlow(DocType.GEN_UW_DOCUMENT, String.valueOf(savedPolicy.getPolicyId()), savedPolicy, "N", null, null, null);
        quote.setQuotStatus("CV");
        quote.setConvertedReference(policyTrans);
        quotTransRepo.save(quote);
        quoteProspectResult.setResult("Y");
        return quoteProspectResult;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProspectsDTO> findActiveProspects(String paramString, Pageable paramPageable) {
        final String search = (paramString != null) ? "%" + paramString + "%" : "%%";
        List<Object[]> prospcts = prospectsRepo.findProspcts(search.toLowerCase(), paramPageable.getPageNumber(), paramPageable.getPageSize());
        final List<ProspectsDTO> prospectsDTOList = new ArrayList<>();
        long rowCount = 0l;
        if (!prospcts.isEmpty()) rowCount = ((BigInteger) prospcts.get(0)[4]).intValue();
        for (Object[] prospect : prospcts) {
            ProspectsDTO prospectsDTO = new ProspectsDTO();
            prospectsDTO.setTenId(((BigInteger) prospect[0]).longValue());
            prospectsDTO.setFname((String) prospect[1]);
            prospectsDTO.setOtherNames((String) prospect[2]);
            prospectsDTO.setPhoneNo((String) prospect[3]);
            prospectsDTOList.add(prospectsDTO);
        }
        return new PageImpl<>(prospectsDTOList, paramPageable, rowCount);
    }

    @Override
    @Transactional(readOnly = true)
    public ProspectDef getProspectDetails(Long id) throws BadRequestException {
        if (id == null) throw new BadRequestException("Unable to get prospect details.....");
        return prospectsRepo.findOne(id);
    }

    @Override
    public void confirmQuoteWF(Long quoteCode) throws BadRequestException {
        Map<String, Object> processVariables = Maps.newHashMap();
        processVariables.put("clientRequest", true);
//        QuoteTrans quoteTrans = quotTransRepo.findOne(quoteCode);
        this.confirmQuote(Long.valueOf(quoteCode));
        //  workflowService.completeTask(String.valueOf("Q"+quoteCode), processVariables, null, DocType.QUOTATION_DOCUMENT, (false) ? "Y" : "N", quoteTrans, null, null);
    }

    @Override
    public void cancelQuoteWF(Long quoteCode, String reason) throws BadRequestException {
//        if (StringUtils.isBlank(reason))
        if (true)
            throw new BadRequestException("Cancellation Reason cannot be blank....");
//        String activeTaskName = workflowService.getActiveTaskName(String.valueOf(quoteCode));
//        if (activeTaskName == null) {
//            cancelQuote(quoteCode, reason);
//            return;
//        }
//        Map<String, Object> processVariables = Maps.newHashMap();
//        processVariables.put("clientRequest", false);
//        processVariables.put("cancelReason", reason);
//        QuoteTrans quoteTrans = quotTransRepo.findOne(quoteCode);
        //workflowService.completeTask(String.valueOf("Q"+quoteCode), processVariables, null, DocType.QUOTATION_DOCUMENT, (false) ? "Y" : "N", quoteTrans, null, null);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void defineCategoryRules(QuoteCategoryRules categoryRule) throws BadRequestException {
        if (categoryRule.getSectId() == null) throw new BadRequestException("Provide Rule to Update");
        QuoteCategoryRules rule = categoryRulesRepo.findOne(categoryRule.getSectId());
        categoryRule.setCategory(rule.getCategory());
        categoryRule.setShtDesc(rule.getShtDesc());
        categoryRule.setDesc(rule.getDesc());
        categoryRule.setBinderRules(rule.getBinderRules());
        categoryRulesRepo.save(categoryRule);
    }

    @Override
    @Transactional(readOnly = false)
    public void createBinderRules(RulesBean rulesBean) {
        List<QuoteCategoryRules> categoryRules = new ArrayList<>();
        for (Long ruleId : rulesBean.getRules()) {
            MedicalBinderRules binderRule = medicalRulesRepo.findOne(ruleId);
            QuoteCategoryRules rule = new QuoteCategoryRules();
            rule.setBinderRules(binderRule);
            rule.setDesc(binderRule.getDesc());
            rule.setCategory(medicalQuoteCategoryRepo.findOne(rulesBean.getCatCode()));
            rule.setShtDesc(binderRule.getShtDesc());
            rule.setValue(binderRule.getValue());
            categoryRules.add(rule);
        }
        categoryRulesRepo.save(categoryRules);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getUnassignedBinderRules(Long catId) throws IllegalAccessException {
        Long bindCode = 0l;
        if (catId == null) {
            catId = 0l;
        } else {
            if (medicalQuoteCategoryRepo.count(QMedicalQuoteCategory.medicalQuoteCategory.id.eq(catId)) == 0) {
                bindCode = 0l;
            } else {
                MedicalQuoteCategory category = medicalQuoteCategoryRepo.findOne(catId);
                catId = category.getId();
                bindCode = category.getQuotation().getBinder().getBinId();
            }
        }
        return categoryRulesRepo.getBinderRules(bindCode, catId);
    }

    @Override
    public void defineMedQuotFamDetails(MedQuotCatFamilyDetails medQuotCatFamilyDetails) throws BadRequestException {
        MedicalQuoteCategory category = medQuotCatFamilyDetails.getCategory();
        if (medQuotCatFamilyDetails.getFamilySize() == null)
            throw new BadRequestException("Family Size is Mandatory");
        if (medQuotCatFamilyDetails.getFamilies() == null)
            throw new BadRequestException("Number of families is Mandatory");
        if (medQuotCatFamilyDetails.getFamilyId() == null) {
            if (category.getQuotation() != null) {
                Long count = medQuotCatFamilyDetailsRepo.count(QMedQuotCatFamilyDetails.medQuotCatFamilyDetails.category.id.eq(category.getId())
                        .and(QMedQuotCatFamilyDetails.medQuotCatFamilyDetails.familySize.equalsIgnoreCase(medQuotCatFamilyDetails.getFamilySize()))
                        .and(QMedQuotCatFamilyDetails.medQuotCatFamilyDetails.agebracket.eq(medQuotCatFamilyDetails.getAgebracket())));
                if (count > 0)
                    throw new BadRequestException("Cannot have more than one " + medQuotCatFamilyDetails.getFamilySize() + " family size for the same category and same age");
            }
        }
        medQuotCatFamilyDetailsRepo.save(medQuotCatFamilyDetails);
        try {
            medComputeQuotPrem.computePrem(medQuotCatFamilyDetails.getCategory().getQuotation().getQuoteId());
        } catch (IOException ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteCategoryRules(Long ruleId) {
        categoryRulesRepo.delete(ruleId);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteQuotTax(Long quotTaxId) {
        medicalQuoteTaxesRepo.delete(quotTaxId);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteQuotGenTax(Long quotTaxId) throws BadRequestException, IOException {
        Long quotId = quotTaxesRepo.getQuotId(quotTaxId);
        quotTaxesRepo.deleteQuotTaxes(quotTaxId);
        this.computeQuotPrem(quotId);
    }

    @Override
    public void editGenTax(Long polTaxId, BigDecimal divFactor, BigDecimal taxRate) throws
            BadRequestException, IOException {
        QuotTaxes quotTaxes = quotTaxesRepo.findOne(polTaxId);
        quotTaxes.setDivFactor(divFactor);
        quotTaxes.setTaxRate(taxRate);
        quotTaxesRepo.save(quotTaxes);
        this.computeQuotPrem(quotTaxes.getProTrans().getQuoteTrans().getQuoteId());
    }

    @Override
    public void createQuotClause(QuoteClausesDTO clause) throws BadRequestException {
        if (clause.getQpClauId() == null) {
            throw new BadRequestException("Select A valid Quote Clause to update...");
        }
        if (clause.getQuotProdCode() == null) {
            throw new BadRequestException("Select Quotation Product to Update Clause");
        }
        final QuoteProTrans proTrans = quotProductsRepo.findOne(clause.getQuotProdCode());
        if (proTrans == null) {
            throw new BadRequestException("Select A Valid Product...");
        }
        if (clause.getSubclauseId() == null) {
            throw new BadRequestException("Unable to Update Clause...Contact Administrator...");
        }
        final SubclassClauses clauses = clausesRepo.findOne(clause.getSubclauseId());
        if (clauses == null) {
            throw new BadRequestException("Unable to Update Clause...Contact Administrator...");
        }

        final QuotClauses quotClauses = quotClausesRepo.findOne(clause.getQpClauId());
        if (quotClauses == null) {
            throw new BadRequestException("Select A valid Quote Clause to update...");
        }
        quotClauses.setClause(clauses);
        quotClauses.setEditable(clause.getEditable() != null && "on".equalsIgnoreCase(clause.getEditable()));
        quotClauses.setClauHeading(clause.getClauHeading());
        quotClauses.setClauWording(clause.getClauWording());
        quotClauses.setProTrans(proTrans);
        quotClausesRepo.save(quotClauses);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteQuotCategory(Long categoryId) {

        Iterable<QuoteCategoryRules> categoryRules = categoryRulesRepo.findAll(QQuoteCategoryRules.quoteCategoryRules.category.id.eq(categoryId));
        categoryRulesRepo.delete(categoryRules);

        Iterable<MedQuotCategoryBenefits> categoryBenefits = benefitRepo.findAll(QMedQuotCategoryBenefits.medQuotCategoryBenefits.category.id.eq(categoryId));
        benefitRepo.delete(categoryBenefits);

        Iterable<MedQuotCatFamilyDetails> familyDetailses = medQuotCatFamilyDetailsRepo.findAll(QMedQuotCatFamilyDetails.medQuotCatFamilyDetails.category.id.eq(categoryId));
        medQuotCatFamilyDetailsRepo.delete(familyDetailses);
        medicalQuoteCategoryRepo.delete(categoryId);

    }

    @Override
    @Transactional(readOnly = false)
    public void deleteCategoryBenefit(Long benefitId, Long polCode) throws BadRequestException {
        if (benefitRepo.findOne(benefitId).getCover().getFundCoverMand() != null && "Y".equalsIgnoreCase(benefitRepo.findOne(benefitId).getCover().getFundCoverMand())) {
            throw new BadRequestException("Cannot delete mandatory cover...");
        }
        benefitRepo.delete(benefitId);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteCategoryFamDetails(Long familyId, Long quotCode) throws BadRequestException {

        benefitRepo.delete(familyId);
        try {
            medComputeQuotPrem.computePrem(quotCode);
        } catch (IOException ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void defineMedicalCategories(MedicalQuoteCategory medicalCategory) throws BadRequestException {
        QuoteTrans policyTrans = medicalCategory.getQuotation();

        boolean newRecord = medicalCategory.getId() == null;
        if (!newRecord) {
            MedicalQuoteCategory existingQuote = medicalQuoteCategoryRepo.findOne(medicalCategory.getId());
            if (policyTrans.getMedicalCoverType() != null && "I".equalsIgnoreCase(policyTrans.getMedicalCoverType())) {
                Long count = medicalQuoteCategoryRepo.count(QMedicalQuoteCategory.medicalQuoteCategory.quotation.quoteId.eq(policyTrans.getQuoteId())
                        .and(QMedicalQuoteCategory.medicalQuoteCategory.id.ne(medicalCategory.getId())));
                if (count > 0)
                    throw new BadRequestException("Cannot Have more than one category for an Individual Policy");
                if (medicalCategory.getPrincipalAge() == null) {
                    throw new BadRequestException("Principal Age is Mandatory");
                }
                if (medicalCategory.getPrincipalGender() == null) {
                    throw new BadRequestException("Principal gender is Mandatory");
                }
            }
            medicalCategory.setPremium(existingQuote.getPremium());
            medicalCategory.setLoadingFactor(existingQuote.getLoadingFactor());
            medicalCategory.setLoadingPrem(existingQuote.getLoadingPrem());
            medicalCategory.setBedCost(existingQuote.getBedCost());
        } else {
            if (policyTrans.getMedicalCoverType() != null && "I".equalsIgnoreCase(policyTrans.getMedicalCoverType())) {
                Long count = medicalQuoteCategoryRepo.count(QMedicalQuoteCategory.medicalQuoteCategory.quotation.quoteId.eq(policyTrans.getQuoteId()));
                if (count > 0)
                    throw new BadRequestException("Cannot Have more than one category for an Individual Policy");
                if (medicalCategory.getPrincipalAge() == null) {
                    throw new BadRequestException("Principal Age is Mandatory");
                }
                if (medicalCategory.getPrincipalGender() == null) {
                    throw new BadRequestException("Principal gender is Mandatory");
                }
            }
        }
        if (medicalCategory.getBinderDetails() == null) {
            throw new BadRequestException("Select Category");
        }
        MedicalQuoteCategory category = medicalQuoteCategoryRepo.save(medicalCategory);
        if (!newRecord) {
            return;
        }
        Iterable<MedicalCovers> medicalCovers = null;
        Iterable<MedicalBinderRules> binderRules = null;
        if (policyTrans.getBinder().getFundBinder() == null || "N".equalsIgnoreCase(policyTrans.getBinder().getFundBinder())) {
            binderRules = rulesRepo.findAll(QMedicalBinderRules.medicalBinderRules.binder.binId.eq(medicalCategory.getBinderDetails().getBinder().getBinId()));
            medicalCovers = medicalCoversRepo.findAll(QMedicalCovers.medicalCovers.binderDet.detId.eq(medicalCategory.getBinderDetails().getDetId()));
        } else if ("Y".equalsIgnoreCase(policyTrans.getBinder().getFundBinder())) {
            binderRules = rulesRepo.findAll(QMedicalBinderRules.medicalBinderRules.binder.binId.eq(medicalCategory.getBinderDetails().getBinder().getBinId())
                    .and(QMedicalBinderRules.medicalBinderRules.mandatory.equalsIgnoreCase("Y")));
            medicalCovers = medicalCoversRepo.findAll(QMedicalCovers.medicalCovers.binderDet.detId.eq(medicalCategory.getBinderDetails().getDetId())
                    .and(QMedicalCovers.medicalCovers.fundCoverMand.eq("Y")));

        }

        Set<MedQuotCategoryBenefits> categoryBenefits = StreamsHelper.stream(medicalCovers)
                .map(a -> {
                    Iterable<CoverLimits> coverLimits = coverLimitsRepo.findAll(QCoverLimits.coverLimits.covers.eq(a));
                    CoverLimits limit = null;
                    for (CoverLimits coverLimit : coverLimits) {
                        limit = coverLimit;
                        break;
                    }
                    MedQuotCategoryBenefits benefit = new MedQuotCategoryBenefits();
                    if (limit != null)
                        benefit.setLimit(limit);
                    benefit.setCover(a);
                    benefit.setCategory(category);
                    benefit.setWaitPeriod(a.getWaitPeriod());
                    return benefit;
                }).collect(Collectors.toSet());
        benefitRepo.save(categoryBenefits);
        Set<QuoteCategoryRules> categoryRules = StreamsHelper.stream(binderRules)
                .map(a -> {
                    QuoteCategoryRules rule = new QuoteCategoryRules();
                    rule.setShtDesc(a.getShtDesc());
                    rule.setDesc(a.getDesc());
                    rule.setValue(a.getValue());
                    rule.setBinderRules(a);
                    rule.setCategory(category);
                    return rule;
                }).collect(Collectors.toSet());

        categoryRulesRepo.save(categoryRules);
        try {
            medComputeQuotPrem.computePrem(policyTrans.getQuoteId());
        } catch (IOException ex) {
            throw new BadRequestException(ex.getMessage());
        }
        populateCategoryClauses(policyTrans);
        populateCategoryTaxes(policyTrans);

        //  populateCategoryExclusions(policyTrans);
        // populateCategoryLoadings(policyTrans);
        //populateCategoryProviders(policyTrans);
    }

    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void populateCategoryClauses(QuoteTrans policy) throws BadRequestException {
//        Set<MedQuoteClauses> policyClauses = new HashSet<>();
//        Iterable<MedicalQuoteCategory> risks = medicalQuoteCategoryRepo.findAll(QMedicalQuoteCategory.medicalQuoteCategory.quotation.quoteId.eq(policy.getQuoteId()));
//        for (MedicalQuoteCategory risk : risks) {
//            Iterable<MedQuoteClauses> polClauses = clausesRepo.findAll(QMedQuoteClauses.medQuoteClauses.quotation.quoteId.eq(risk.getId()));
//            polClauses.forEach(policyClauses::add);
//            Iterable<BinderClauses> binderClauses = binderClauseRepo.findAll(QBinderClauses.binderClauses.binderDet.detId.eq(risk.getBinderDetails().getDetId()));
//            for (BinderClauses clause : binderClauses) {
//                MedQuoteClauses polClause = new MedQuoteClauses();
//                polClause.setClauHeading((clause.getClauHeading()!=null)?clause.getClauHeading():clause.getClause().getClause().getClauHeading());
//                polClause.setClause(clause.getClause());
//                polClause.setClauWording((clause.getClauWording()!=null)?clause.getClauWording():clause.getClause().getClause().getClauWording());
//                if(clause.getEditable()!=null && "Y".equalsIgnoreCase(clause.getEditable())){
//                    polClause.setEditable(true);
//                }
//                polClause.setQuotation(policy);
//                policyClauses.add(polClause);
//            }
//        }
//
//        clausesRepo.save(policyClauses);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<MedQuotCategoryBenefits> getCategoryBenefits(DataTablesRequest request, Long catId) throws
            IllegalAccessException {
        BooleanExpression pred = QMedQuotCategoryBenefits.medQuotCategoryBenefits.category.id.eq(catId);
        Page<MedQuotCategoryBenefits> page = benefitRepo.findAll(pred.and(request.searchPredicate(QMedQuotCategoryBenefits.medQuotCategoryBenefits)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<QuoteCategoryRules> getCategoryRules(DataTablesRequest request, Long catId) throws
            IllegalAccessException {
        BooleanExpression pred = QQuoteCategoryRules.quoteCategoryRules.category.id.eq(catId);
        Page<QuoteCategoryRules> page = categoryRulesRepo.findAll(pred.and(request.searchPredicate(QQuoteCategoryRules.quoteCategoryRules)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<MedQuotCatFamilyDetails> getCategoryFamDetails(DataTablesRequest request, Long catId) throws
            IllegalAccessException {
        BooleanExpression pred = QMedQuotCatFamilyDetails.medQuotCatFamilyDetails.category.id.eq(catId);
        Page<MedQuotCatFamilyDetails> page = medQuotCatFamilyDetailsRepo.findAll(pred.and(request.searchPredicate(QMedQuotCatFamilyDetails.medQuotCatFamilyDetails)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<MedicalQuoteTaxes> findQuotTaxes(DataTablesRequest request, Long quotCode)
            throws IllegalAccessException {

        BooleanExpression pred = QMedicalQuoteTaxes.medicalQuoteTaxes.quotation.quoteId.eq(quotCode);
        Page<MedicalQuoteTaxes> page = polTaxesRepo.findAll(pred.and(request.searchPredicate(QMedicalQuoteTaxes.medicalQuoteTaxes)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<MedicalQuoteCategory> findQuotCategories(DataTablesRequest request, Long quotCode) throws
            IllegalAccessException {
        BooleanExpression pred = QMedicalQuoteCategory.medicalQuoteCategory.quotation.quoteId.eq(quotCode);
        Page<MedicalQuoteCategory> page = medicalQuoteCategoryRepo.findAll(pred.and(request.searchPredicate(QMedicalQuoteCategory.medicalQuoteCategory)), request);
        return new DataTablesResult<>(request, page);

    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void saveQuotTaxes(MedicalQuoteTaxes taxes) throws BadRequestException {

        QuoteTrans policyTrans = taxes.getQuotation();
        MedicalQuoteTaxes existing = medicalQuoteTaxesRepo.findOne(QMedicalQuoteTaxes.medicalQuoteTaxes.quotTaxId.eq(taxes.getQuotTaxId()));
        taxes.setRateType(existing.getRateType());
        taxes.setRevenueItems(existing.getRevenueItems());
        taxes.setSubclass(existing.getSubclass());
        taxes.setTaxLevel(existing.getTaxLevel());
        Long quotCode = policyTrans.getQuoteId();
        medicalQuoteTaxesRepo.save(taxes);
        try {
            medComputeQuotPrem.computePrem(quotCode);
        } catch (IOException ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }


    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getUnassignQuotTaxes(Long quotId) throws IllegalAccessException {
        QuoteTrans policy = quotTransRepo.findOne(quotId);
        Long proCode = policy.getProduct().getProCode();
        return unassignedTaxesRepo.getUnassignQuotTaxes(quotId, proCode);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void createNewQuotTax(TaxesDTO taxesDTO) throws BadRequestException {
        List<MedicalQuoteTaxes> policyTaxes = new ArrayList<>();
        QuoteTrans policy = quotTransRepo.findOne(taxesDTO.getTaxPolicyCode());
        for (Long taxId : taxesDTO.getTaxes()) {
            TaxRates taxRates = unassignedTaxesRepo.findOne(taxId);
            MedicalQuoteTaxes policyTax = new MedicalQuoteTaxes();
            policyTax.setQuotation(policy);
            policyTax.setRateType(taxRates.getRateType());
            policyTax.setRevenueItems(taxRates.getRevenueItems());
            policyTax.setSubclass(taxRates.getSubclass());
            policyTax.setTaxLevel(taxRates.getTaxLevel());
            policyTax.setTaxRate(taxRates.getTaxRate());
            policyTax.setDivFactor(taxRates.getDivFactor());
            policyTax.setTaxAmount(premComputeService.calculateTax(policy.getPremium(), taxRates.getTaxRate(), taxRates.getDivFactor(), taxRates.getRateType()));
            policyTaxes.add(policyTax);
        }
        polTaxesRepo.save(policyTaxes);
        try {
            medComputeQuotPrem.computePrem(policy.getQuoteId());
        } catch (IOException ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }


    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<MedQuoteClauses> findQuotClauses(DataTablesRequest request, Long quotCode)
            throws IllegalAccessException {
        BooleanExpression pred = QMedQuoteClauses.medQuoteClauses.quotation.quoteId.eq(quotCode);
        Page<MedQuoteClauses> page = medQuotClausesRepo.findAll(pred.and(request.searchPredicate(QMedQuoteClauses.medQuoteClauses)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void populateCategoryTaxes(QuoteTrans policy) throws BadRequestException {
        Set<MedicalQuoteTaxes> policyTaxes = new HashSet<>();
        if (policy == null) throw new BadRequestException("Quotation Cannot be null");
        Iterable<MedicalQuoteTaxes> polTaxes = polTaxesRepo.findAll(QMedicalQuoteTaxes.medicalQuoteTaxes.quotation.quoteId.eq(policy.getQuoteId()));
        polTaxes.forEach(policyTaxes::add);
        Iterable<MedicalQuoteCategory> risks = medicalQuoteCategoryRepo.findAll(QMedicalQuoteCategory.medicalQuoteCategory.quotation.quoteId.eq(policy.getQuoteId()));
//            for (MedicalQuoteCategory risk : risks) {
//                Iterable<TaxRates> taxRates = taxRatesRepo.findAll((QTaxRates.taxRates.active.eq(true).and(QTaxRates.taxRates.mandatory.eq(Boolean.TRUE)))
//                        .and(QTaxRates.taxRates.revenueItems.item.ne(RevenueItems.RE))
//                        .and(QTaxRates.taxRates.subclass.eq(risk.getBinderDetails().getSubCoverTypes().getSubclass()))
//                        .and(QTaxRates.taxRates.productsDef.proCode.eq(risk.getQuotation().getProduct().getProCode())));
//                for (TaxRates tax : taxRates) {
//                    if ("B".equalsIgnoreCase(policy.getBinder().getBinType())) {
//                        if (tax.getRevenueItems().getItem() == RevenueItems.SD)
//                            continue;
//                    }
//                    MedicalQuoteTaxes policyTax = new MedicalQuoteTaxes();
//                    policyTax.setQuotation(policy);
//                    policyTax.setRateType(tax.getRateType());
//                    policyTax.setRevenueItems(tax.getRevenueItems());
//                    policyTax.setSubclass(tax.getSubclass());
//                    policyTax.setTaxLevel(tax.getTaxLevel());
//                    policyTax.setTaxRate(tax.getTaxRate());
//                    policyTax.setDivFactor(tax.getDivFactor());
//                    policyTax.setTaxAmount(premComputeService.calculateTax(policy.getPremium(), tax.getTaxRate(), tax.getDivFactor(), tax.getRateType()));
//                    policyTaxes.add(policyTax);
//                }
//            }
//
//            polTaxesRepo.save(policyTaxes);
    }

    @Override
    public long medCount(Long quoteId) {
        long count = quotProductsRepo.count(QQuoteProTrans.quoteProTrans.quoteTrans.quoteId.eq(quoteId).and(QQuoteProTrans.quoteProTrans.product.isNotNull()));
        return count;
    }

    @Override
    public Set<QuotTaxes> getNewTaxes(Long proQuoteId) throws BadRequestException {
        QuoteProTrans proTrans = quotProductsRepo.findOne(proQuoteId);
        if (proTrans == null) throw new BadRequestException("No Quote Product Transaction");
        Iterable<QuotTaxes> quotTaxes = quotTaxesRepo.findAll(QQuotTaxes.quotTaxes.proTrans.quoteProductId.eq(proQuoteId));
        List<Long> existingTaxes = new ArrayList<>();
        if (quotTaxes.spliterator().getExactSizeIfKnown() > 0)
            existingTaxes = Streamable.streamOf(quotTaxes).filter(a -> a.getRevenueItems() != null).map(a -> a.getRevenueItems().getRevenueId()).collect(Collectors.toList());
        Set<QuotTaxes> newTaxes = new HashSet<>();
        Iterable<QuoteRiskTrans> risks = quotRiskRepo.findAll(QQuoteRiskTrans.quoteRiskTrans.productTrans.quoteProductId.eq(proQuoteId));
        for (QuoteRiskTrans risk : risks) {
            Iterable<TaxRates> taxes = taxRatesRepo.findAll(QTaxRates.taxRates.subclass.subId.eq(risk.getSubclass().getSubId()).and(QTaxRates.taxRates.revenueItems.revenueId.notIn(existingTaxes)).and(QTaxRates.taxRates.active.eq(true)));
            for (TaxRates tax : taxes) {
                QuotTaxes polTax = new QuotTaxes();
                polTax.setTaxLevel(tax.getTaxLevel());
                polTax.setDivFactor(tax.getDivFactor());
                polTax.setRateType(tax.getRateType());
                polTax.setRevenueItems(tax.getRevenueItems());
                polTax.setProTrans(proTrans);
                polTax.setTaxRate(tax.getTaxRate());
                polTax.setPolTaxId(tax.getTaxId());
                newTaxes.add(polTax);
            }
        }
        return newTaxes;
    }

    @Override
    public void createQuoteTaxes(PolicyTaxBean taxBean) throws BadRequestException {
        QuoteProTrans proTrans = quotProductsRepo.findOne(taxBean.getPolId());
        List<QuotTaxes> createdTaxes = new ArrayList<>();
        if (proTrans == null) throw new BadRequestException("No Quote Product Transaction");
        for (Long taxId : taxBean.getTaxes()) {
            TaxRates tax = taxRatesRepo.findOne(taxId);
            QuotTaxes polTax = new QuotTaxes();
            polTax.setTaxLevel(tax.getTaxLevel());
            polTax.setDivFactor(tax.getDivFactor());
            polTax.setRateType(tax.getRateType());
            polTax.setRevenueItems(tax.getRevenueItems());
            polTax.setTaxRate(tax.getTaxRate());
            polTax.setSubclass(tax.getSubclass());
            polTax.setProTrans(proTrans);
            createdTaxes.add(polTax);
        }
        quotTaxesRepo.save(createdTaxes);
    }

    @Override
    public String getInhouseEmail() throws BadRequestException {
        return paramService.getParameterString("INHOUSE_EMAIL");
    }

    @Override
    @Transactional(readOnly = false)
    public Long reuseQuote(Long quotCode) throws BadRequestException {
        if (quotCode == null) throw new BadRequestException("No Quotation Transaction");
        //QuoteTrans quoteTrans = quotTransRepo.findOne(quotCode);
        QuoteTrans quoteTrans = quotTransRepo.findByQuoteId(quotCode);
        QuoteTrans newQuote = new QuoteTrans();
        if (quoteTrans.getBinder() != null) {
            throw new BadRequestException("Reuse for this product not supported....");
        }
        Iterable<QuoteProTrans> quoteProTranss = quoteTrans.getQuoteProTranses();
        try {
            BeanUtils.copyProperties(newQuote, quoteTrans);
            newQuote.setQuoteId(null);
            Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("Q");
            String policyNumberFormat = paramService.getParameterString("QUOTE_NO_FORMAT");
            if (sequenceRepo.count(seqPredicate) == 0)
                throw new BadRequestException("Sequence for Quotations Transactions has not been defined");
            SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
            Long seqNumber = sequence.getNextNumber();
            final String policyNumber = templateMerger.generateFormat(policyNumberFormat, quoteTrans.getBranch().getObId(), null, quoteTrans.getWefDate(), String.format("%05d", seqNumber), null);
            //final String policyNumber = sequence.getSeqPrefix() + String.format("%05d", seqNumber);
            newQuote.setQuotNo(policyNumber);
            newQuote.setQuotRevNo(policyNumber + "/1");
            newQuote.setQuotStatus("D");
            sequence.setLastNumber(seqNumber);
            sequence.setNextNumber(seqNumber + 1);
            List<QuoteProTrans> quoteProTransList = new ArrayList<>();
            List<QuoteRiskTrans> quoteRiskTransList = new ArrayList<>();
            List<QuotRiskLimits> quotRiskLimitsList = new ArrayList<>();
            List<QuotClauses> quotClausesList = new ArrayList<>();
            List<QuotTaxes> quotTaxesList = new ArrayList<>();
            for (QuoteProTrans a : quoteProTranss) {
                QuoteProTrans proTrans = new QuoteProTrans();
                BeanUtils.copyProperties(proTrans, a);
                proTrans.setQuoteProductId(null);
                proTrans.setQuoteTrans(newQuote);
                Iterable<QuoteRiskTrans> quoteRiskTranss = quotRiskRepo.findAll(QQuoteRiskTrans.quoteRiskTrans.productTrans.quoteProductId.eq(a.getQuoteProductId()));
                for (QuoteRiskTrans b : quoteRiskTranss) {
                    QuoteRiskTrans riskTrans = new QuoteRiskTrans();
                    BeanUtils.copyProperties(riskTrans, b);
                    riskTrans.setRiskId(null);
                    riskTrans.setProductTrans(proTrans);
                    quoteRiskTransList.add(riskTrans);
                    Iterable<QuotRiskLimits> quotRiskLimitss = quotRiskLimitsRepo.findAll(QQuotRiskLimits.quotRiskLimits.risk.riskId.eq(b.getRiskId()));
                    for (QuotRiskLimits riskLimits : quotRiskLimitss) {
                        QuotRiskLimits quotRiskLimits = new QuotRiskLimits();
                        BeanUtils.copyProperties(quotRiskLimits, riskLimits);
                        quotRiskLimits.setSectId(null);
                        quotRiskLimits.setRisk(riskTrans);
                        quotRiskLimitsList.add(quotRiskLimits);
                    }
                }

                Iterable<QuotTaxes> quotTaxes = quotTaxesRepo.findAll(QQuotTaxes.quotTaxes.proTrans.quoteProductId.eq(a.getQuoteProductId()));
                for (QuotTaxes taxes : quotTaxes) {
                    QuotTaxes quotTaxes1 = new QuotTaxes();
                    BeanUtils.copyProperties(quotTaxes1, taxes);
                    quotTaxes1.setPolTaxId(null);
                    quotTaxes1.setProTrans(proTrans);
                    quotTaxesList.add(quotTaxes1);
                }
                Iterable<QuotClauses> quotClauses = quotClausesRepo.findAll(QQuotClauses.quotClauses.proTrans.quoteProductId.eq(a.getQuoteProductId()));
                for (QuotClauses clauses : quotClauses) {
                    QuotClauses quotClauses1 = new QuotClauses();
                    BeanUtils.copyProperties(quotClauses1, clauses);
                    quotClauses1.setQpClauId(null);
                    quotClauses1.setProTrans(proTrans);
                    quotClausesList.add(quotClauses1);
                }
                quoteProTransList.add(proTrans);
            }
            newQuote.setQuoteProTranses(quoteProTransList);
            sequenceRepo.save(sequence);
            QuoteTrans savedTrans = quotTransRepo.save(newQuote);
            quotProductsRepo.save(quoteProTransList);
            quotTaxesRepo.save(quotTaxesList);
            quotClausesRepo.save(quotClausesList);
            quotRiskRepo.save(quoteRiskTransList);
            quotRiskLimitsRepo.save(quotRiskLimitsList);
            //  workflowService.startNewWorkFlow(DocType.QUOTATION_DOCUMENT, String.valueOf("Q"+savedTrans.getQuoteId()), null, "N", savedTrans, null, null);

            return savedTrans.getQuoteId();
        } catch (IllegalAccessException e) {
            throw new BadRequestException("Error occured.....Please consult admin " + e.getMessage());
        } catch (InvocationTargetException e) {
            throw new BadRequestException("Error occured.....Please consult admin " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Long reviseQuote(Long quotCode) throws BadRequestException {
        if (quotCode == null) throw new BadRequestException("No Quotation Transaction");
        QuoteTrans quoteTrans = quotTransRepo.findOne(QQuoteTrans.quoteTrans.quoteId.eq(quotCode));
        long count = quotTransRepo.count(QQuoteTrans.quoteTrans.quotNo.eq(quoteTrans.getQuotNo()));
        QuoteTrans newQuote = new QuoteTrans();
        if (quoteTrans.getBinder() != null) {
            throw new BadRequestException("Revise for this product not supported....");
        }
        Iterable<QuoteProTrans> quoteProTranss = quoteTrans.getQuoteProTranses();
        try {
            BeanUtils.copyProperties(newQuote, quoteTrans);
            newQuote.setQuoteId(null);
            newQuote.setQuotNo(quoteTrans.getQuotNo());
            newQuote.setQuotStatus("D");
            newQuote.setQuotRevNo(quoteTrans.getQuotNo() + "/" + (++count));
            List<QuoteProTrans> quoteProTransList = new ArrayList<>();
            List<QuoteRiskTrans> quoteRiskTransList = new ArrayList<>();
            List<QuotRiskLimits> quotRiskLimitsList = new ArrayList<>();
            List<QuotClauses> quotClausesList = new ArrayList<>();
            List<QuotTaxes> quotTaxesList = new ArrayList<>();
            for (QuoteProTrans a : quoteProTranss) {
                QuoteProTrans proTrans = new QuoteProTrans();
                BeanUtils.copyProperties(proTrans, a);
                proTrans.setQuoteProductId(null);
                proTrans.setQuoteTrans(newQuote);
                Iterable<QuoteRiskTrans> quoteRiskTranss = quotRiskRepo.findAll(QQuoteRiskTrans.quoteRiskTrans.productTrans.quoteProductId.eq(a.getQuoteProductId()));
                for (QuoteRiskTrans b : quoteRiskTranss) {
                    QuoteRiskTrans riskTrans = new QuoteRiskTrans();
                    BeanUtils.copyProperties(riskTrans, b);
                    riskTrans.setRiskId(null);
                    riskTrans.setProductTrans(proTrans);
                    quoteRiskTransList.add(riskTrans);
                    Iterable<QuotRiskLimits> quotRiskLimitss = quotRiskLimitsRepo.findAll(QQuotRiskLimits.quotRiskLimits.risk.riskId.eq(b.getRiskId()));
                    for (QuotRiskLimits riskLimits : quotRiskLimitss) {
                        QuotRiskLimits quotRiskLimits = new QuotRiskLimits();
                        BeanUtils.copyProperties(quotRiskLimits, riskLimits);
                        quotRiskLimits.setSectId(null);
                        quotRiskLimits.setRisk(riskTrans);
                        quotRiskLimitsList.add(quotRiskLimits);
                    }
                }

                Iterable<QuotTaxes> quotTaxes = quotTaxesRepo.findAll(QQuotTaxes.quotTaxes.proTrans.quoteProductId.eq(a.getQuoteProductId()));
                for (QuotTaxes taxes : quotTaxes) {
                    QuotTaxes quotTaxes1 = new QuotTaxes();
                    BeanUtils.copyProperties(quotTaxes1, taxes);
                    quotTaxes1.setPolTaxId(null);
                    quotTaxes1.setProTrans(proTrans);
                    quotTaxesList.add(quotTaxes1);
                }
                Iterable<QuotClauses> quotClauses = quotClausesRepo.findAll(QQuotClauses.quotClauses.proTrans.quoteProductId.eq(a.getQuoteProductId()));
                for (QuotClauses clauses : quotClauses) {
                    QuotClauses quotClauses1 = new QuotClauses();
                    BeanUtils.copyProperties(quotClauses1, clauses);
                    quotClauses1.setQpClauId(null);
                    quotClauses1.setProTrans(proTrans);
                    quotClausesList.add(quotClauses1);
                }
                quoteProTransList.add(proTrans);
            }
            newQuote.setQuoteProTranses(quoteProTransList);
            QuoteTrans savedTrans = quotTransRepo.save(newQuote);
            quotProductsRepo.save(quoteProTransList);
            quotTaxesRepo.save(quotTaxesList);
            quotClausesRepo.save(quotClausesList);
            quotRiskRepo.save(quoteRiskTransList);
            quotRiskLimitsRepo.save(quotRiskLimitsList);
            //workflowService.startNewWorkFlow(DocType.QUOTATION_DOCUMENT, String.valueOf("Q"+savedTrans.getQuoteId()), null, "N", savedTrans, null, null);
            return savedTrans.getQuoteId();
        } catch (IllegalAccessException e) {
            throw new BadRequestException("Error occured.....Please consult admin " + e.getMessage());
        } catch (InvocationTargetException e) {
            throw new BadRequestException("Error occured.....Please consult admin " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = false)
    public List<Object[]> compQuoteConvertSelect(Long quotId) throws IllegalAccessException {
        return quotProductsRepo.getComparisonQuotProd(quotId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getCombineQuoteProduct(Long quotId) throws IllegalAccessException {
        return quotProductsRepo.getQuoteProductsConvert(quotId);
    }
}