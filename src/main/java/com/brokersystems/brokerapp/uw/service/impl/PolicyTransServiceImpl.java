
package com.brokersystems.brokerapp.uw.service.impl;
import com.brokersystems.brokerapp.auditlogs.model.RtsAudit;
import com.brokersystems.brokerapp.auditlogs.repositories.RtsAuditRepository;
import com.brokersystems.brokerapp.bulktransactions.models.BulkPolicyCreation;
import com.brokersystems.brokerapp.bulktransactions.repositories.BulkPolicyCreationRepo;
import com.brokersystems.brokerapp.certs.model.*;
import com.brokersystems.brokerapp.certs.repository.PolicyCertsRepo;
import com.brokersystems.brokerapp.certs.repository.PrintQueueRepo;
import com.brokersystems.brokerapp.certs.repository.SubclassCertTypesRepo;
import com.brokersystems.brokerapp.claims.model.*;
import com.brokersystems.brokerapp.claims.repository.ClaimPerilsRepo;
import com.brokersystems.brokerapp.claims.repository.ClaimsBookingRepo;
import com.brokersystems.brokerapp.customscreens.model.ColumnForm;
import com.brokersystems.brokerapp.customscreens.model.RegisteredTableModel;
import com.brokersystems.brokerapp.customscreens.model.TableForm;
import com.brokersystems.brokerapp.customscreens.repository.RegisteredTableModelRepo;
import com.brokersystems.brokerapp.dms.model.QSybrinCases;
import com.brokersystems.brokerapp.dms.model.SybrinCases;
import com.brokersystems.brokerapp.dms.repo.SybrinCasesRepo;
import com.brokersystems.brokerapp.enums.AccountTypeEnum;
import com.brokersystems.brokerapp.enums.RevenueItems;
import com.brokersystems.brokerapp.enums.SectionTypes;
import com.brokersystems.brokerapp.kie.rules.GeneralTransRulesExecutor;
import com.brokersystems.brokerapp.life.model.PolicyAcruals;
import com.brokersystems.brokerapp.life.model.PolicyInstallments;
import com.brokersystems.brokerapp.life.model.QPolicyInstallments;
import com.brokersystems.brokerapp.life.repository.LifeReceiptsRepo;
import com.brokersystems.brokerapp.life.repository.PolicyAccrualPayRepo;
import com.brokersystems.brokerapp.life.repository.PolicyBeneficiariesRepo;
import com.brokersystems.brokerapp.life.repository.PolicyInstallmentsRepo;
import com.brokersystems.brokerapp.life.service.LifeService;
import com.brokersystems.brokerapp.medical.model.*;
import com.brokersystems.brokerapp.medical.repository.*;
import com.brokersystems.brokerapp.schedules.model.*;
import com.brokersystems.brokerapp.schedules.repository.ScheduleMappingRepo;
import com.brokersystems.brokerapp.schedules.repository.ScheduleTransRepo;
import com.brokersystems.brokerapp.schedules.service.ScheduleService;
import com.brokersystems.brokerapp.security.CheckAuthLimits;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.AdminFeeException;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.*;
import com.brokersystems.brokerapp.setup.dto.*;
import com.brokersystems.brokerapp.setup.model.*;

import com.brokersystems.brokerapp.setup.repository.*;
import com.brokersystems.brokerapp.setup.service.OrganizationService;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.brokersystems.brokerapp.trans.model.*;
import com.brokersystems.brokerapp.trans.repository.*;
import com.brokersystems.brokerapp.trans.utils.HibernateProxyTypeAdapter;
import com.brokersystems.brokerapp.users.dto.MakerCheckDTO;
import com.brokersystems.brokerapp.users.model.MakerChecker;
import com.brokersystems.brokerapp.users.model.QMakerChecker;
import com.brokersystems.brokerapp.users.repository.MakerCheckerRepo;
import com.brokersystems.brokerapp.users.service.MakerCheckerService;
import com.brokersystems.brokerapp.uw.dtos.*;
import com.brokersystems.brokerapp.uw.mappers.EndorsementsDtoMapper;
import com.brokersystems.brokerapp.uw.model.*;
import com.brokersystems.brokerapp.uw.repository.*;
import com.brokersystems.brokerapp.uw.service.PolicyTransService;
import com.brokersystems.brokerapp.uw.service.PremComputeService;
import com.brokersystems.brokerapp.webservices.model.VehicleDetails;
import com.brokersystems.brokerapp.workflow.docs.DocType;
import com.brokersystems.brokerapp.workflow.docs.QSysWfDocs;
import com.brokersystems.brokerapp.workflow.docs.SysWfDocs;
import com.brokersystems.brokerapp.workflow.dto.WorkFlowDTO;
import com.brokersystems.brokerapp.workflow.repository.SysWfDocsRepo;
import com.brokersystems.brokerapp.workflow.utils.WorkflowService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.BooleanExpression;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.proxy.HibernateProxy;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




@Service
public class PolicyTransServiceImpl implements PolicyTransService {
    private static final Logger log = LoggerFactory.getLogger(PolicyTransServiceImpl.class);



    @Autowired
    private ClaimPerilsRepo claimPerilsRepo;

    @Autowired
    private PolicyTransRepo policyRepo;

    @Autowired
    private ClientRepository clientRepo;

    @Autowired
    private BindersRepo binderRepo;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private OrganizationService orgRepo;

    @Autowired
    private RegionRepository regionRepo;

    @Autowired
    private CurrencyRepository currencyRepo;

    @Autowired
    private PaymentModeRepo paymentModeRepo;

    @Autowired
    private OrgBranchRepository branchRepo;

    @Autowired
    private BinderDetRepo binderDetRepo;

    @Autowired
    private BinderClauseRepo binderClauseRepo;

    @Autowired
    private PremComputeService premComputeService;

    @Autowired
    private SystemTransRepo transRepo;


    @Autowired
    private PremRatesRepo premRatesRepo;

    @Autowired
    private RiskTransRepo riskRepo;

    @Autowired
    private SubclassCertTypesRepo subclassCertTypesRepo;

    @Autowired
    private SectionTransRepo sectionRepo;

    @Autowired
    private SectionRepo setupSectionRepo;


    @Autowired
    private ScheduleMappingRepo mappingRepo;

    @Autowired
    private TransMappingRepo transMappingRepo;

    @Autowired
    private SequenceRepository sequenceRepo;

    @Autowired
    private CoverTypesRepo coverRepo;

    @Autowired
    private SubClassRepo subclassRepo;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private ProductsRepo productRepo;

    @Autowired
    private PolTaxesRepo polTaxesRepo;

    @Autowired
    private PolClausesRepo polClausesRepo;

    @Autowired
    private TaxRatesRepo taxRatesRepo;

    @Autowired
    private SubClausesRepo subclauseRepo;
    @Autowired
    private DateUtilities dateUtils;

    @Autowired
    private LifeService lifeService;

    @Autowired
    private PolActiveRisksRepo activeRisksRepo;

    @Autowired
    private TemplateMerger templateMerger;

    @Autowired
    private PolicyRemarksRepo policyRemarksRepo;

    @Autowired
    private PolicyInstallmentsRepo policyInstallmentsRepo;

    @Autowired
    private com.brokersystems.brokerapp.certs.service.CertService certService;

    @Autowired
    private ScheduleTransRepo scheduleTransRepo;

    @Autowired
    private CheckAuthLimits authLimits;

    @Autowired
    private CommRatesRepo commRatesRepo;

    @Autowired
    private ParamService paramService;

    @Autowired
    private AdminFeeRepo adminFeeRepo;

    @Autowired
    private AdminFeePolRepo adminFeePolRepo;

    @Autowired
    private AdminFeeSetUpRepo adminFeeSetUpRepo;

    @Autowired
    private SelfFundParamsRepo selfFundParamsRepo;

    @Autowired
    private CategoryMembersRepo membersRepo;

    @Autowired
    private MedicalCategoryRepo categoryRepo;

    @Autowired
    private CategoryBenefitRepo categoryBenefitRepo;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private SysWfDocsRepo sysWfDocsRepo;


    @Autowired
    private RiskDocsRepo riskDocsRepo;

    @Autowired
    private ValidatorUtils validatorUtils;

    @Autowired
    private RiskIntPartiesRepo riskIntPartiesRepo;

    @Autowired
    private InterestedPartiesRepo interestedPartiesRepo;

    @Autowired
    private BinderReqrdDocsRepo reqrdDocsRepo;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private RiskImportExcelUtils importExcelUtils;

    @Autowired
    private ClientTypeRepo clientTypeRepo;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private GeneralTransRulesExecutor rulesExecutor;

    @Autowired
    private TransChecksRepo transChecksRepo;

    @Autowired
    private PrintQueueRepo queueRepo;

    @Autowired
    private PolicyCertsRepo certsRepo;

    @Autowired
    private SubclassReqDocRepo subclassReqDocRepo;

    @Autowired
    private RiskImportLogRepo importLogRepo;

    @Autowired
    private BinderMedicalCardsRepo binderMedicalCardsRepo;

    @Autowired
    private PolicyBindersRepo policyBindersRepo;

    @Autowired
    private LifeReceiptsRepo lifeReceiptsRepo;

    @Autowired
    private BinderQuestionnaireRepo questionnaireRepo;

    @Autowired
    private ReceiptDetailsRepository receiptDetailsRepository;

    @Autowired
    private PolicyQuestionnaireRepo policyQuestionnaireRepo;

    @Autowired
    private SystemTransactionsTempRepo systemTransactionsTempRepo;

    @Autowired
    private BinderQuestionnaireRepo binderQuestionnaireRepo;

    @Autowired
    private PremComputeService premiumService;

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private ClaimsBookingRepo claimsBookingRepo;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private MotorVehicleDetailsRepo motorVehicleDetailsRepo;
    @Autowired
    private BindersRepo bindersRepo;
    @Autowired
    private MakerCheckerService makerCheckerService;
    @Autowired
    private PolicyBeneficiariesRepo policyBeneficiariesRepo;
    @Autowired
    private BusinessSourcesRepo businessSourcesRepo;

    @Autowired
    private MakerCheckerRepo makerCheckerRepo;

    @Autowired
    private RegisteredTableModelRepo registeredTableModelRepo;

    @Autowired
    private SubAgentCommRepo subAgentCommRepo;
    @Autowired
    private AccountTypeRepo accountTypeRepo;

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private ProspectsRepo prospectsRepo;
    @Autowired
    private GeneralTransRulesExecutor generalTransRulesExecutor;
    @Autowired
    private SybrinCasesRepo sybrinCasesRepo;

    @Autowired
    private  BulkPolicyCreationRepo bulkPolicyCreationRepo;

    @Autowired
    private PolicyMiscInfoRepo policyMiscInfoRepo;

    @Autowired
    private PolicyAccrualPayRepo policyAccrualPayRepo;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private RtsAuditRepository rtsAuditRepository;
    @Autowired
    private RejectedReasonsRepo rejectedReasonsRepo;

    @Override
    public MileageDTO findMileageDetails(String riskId) {
        try {
            String url = "https://api.uat.aicare.co.ke/api/v1/products/switch/{id}/mileage";
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-email", "speedflakes@gmail.com");
            headers.set("x-api-key", "API_KEY_p7xdXO68V79lqz4jWZHrnDfuruZPTUlLFDhP8Yh6kWCmtkbtm5");
            HttpEntity requestEntity = new HttpEntity<>(headers);
            ResponseEntity<String> response = new RestTemplate().exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    String.class,
                    riskId
            );
            String responseBody = response.getBody();
            JSONObject jsonObject = new JSONObject(responseBody);
            JSONObject resultOb = jsonObject.getJSONObject("switch");
            final MileageDTO mileageDTO = new MileageDTO();
            if (resultOb.has("mileage")) {
                double mileage = resultOb.getDouble("mileage");
                mileageDTO.setMileage(BigDecimal.valueOf(mileage));
            }
            if (resultOb.has("start_mileage")) {
                double mileage = resultOb.getDouble("start_mileage");
                mileageDTO.setStart_mileage(BigDecimal.valueOf(mileage));
            }
            if (resultOb.has("used_mileage")) {
                double mileage = resultOb.getDouble("used_mileage");
                mileageDTO.setUsed_mileage(BigDecimal.valueOf(mileage));
            }
            System.out.println(responseBody);
            return mileageDTO;
        } catch (Exception ex) {

        }
        return new MileageDTO();
    }
    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<WorkFlowDTO> findUserPolicies(DataTablesRequest request) {
        final String search = (request.getSearch() != null && request.getSearch().getValue() != null)
                ? "%" + request.getSearch().getValue() + "%" : "%%";
        List<Object[]> ticketsList = sysWfDocsRepo.getDashBoardTickets(
                search.toLowerCase(), userUtils.getCurrentUser().getId(), request.getPageNumber(), request.getPageSize());
        long rowCount = 0L;

        if (!ticketsList.isEmpty()) {
            rowCount = ((BigInteger) ticketsList.get(0)[10]).longValue();
        }

        final List<WorkFlowDTO> workFlowDTOList = new ArrayList<>();
        for (Object[] ticket : ticketsList) {
            WorkFlowDTO workFlowDTO = new WorkFlowDTO();
            workFlowDTO.setTaskId(((BigInteger) ticket[0]).longValue());
            workFlowDTO.setActiveProcess((String) ticket[1]);
            workFlowDTO.setRefNo((String) ticket[2]);
            workFlowDTO.setClientName(ticket[3] + " " + ticket[4]);
            workFlowDTO.setUsername((String) ticket[5]);
            workFlowDTO.setCreatedDate((Date) ticket[6]);
            Long transactionId = ((BigInteger) ticket[7]).longValue();
            workFlowDTO.setTransactionId(transactionId);

            // Use findAll to get a list of MakerChecker entities
            Iterable<MakerChecker> makerCheckers = makerCheckerRepo.findAll(
                    QMakerChecker.makerChecker.policyId.eq(transactionId)
                            .and(QMakerChecker.makerChecker.taskType.in("ANP", "ALP")));

            // Process the Iterable to get a single MakerChecker (e.g., the first one)
            MakerChecker makerChecker = null;
            for (MakerChecker mc : makerCheckers) {
                makerChecker = mc; // Take the first one
                break; // Exit after getting the first result
            }

            if (makerChecker != null) {
                workFlowDTO.setStatus(makerChecker.getStatus());
            }

            workFlowDTO.setTransType((String) ticket[8]);
            if (ticket.length > 9) {
                workFlowDTO.setAuthComments((String) ticket[9]);
            }

            workFlowDTOList.add(workFlowDTO);
        }

        Page<WorkFlowDTO> page = new PageImpl<>(workFlowDTOList, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<SysWfDocs> findSearchTickets(DataTablesRequest request, String policyNo, String quoteNo, String preparedBy) throws IllegalAccessException {
        if (policyNo == null) policyNo = "";
        if (quoteNo == null) quoteNo = "";
        if (preparedBy == null) preparedBy = "";
        BooleanExpression pred = null;
        if (policyNo.length() > 0)
            pred = QSysWfDocs.sysWfDocs.policyTrans.polNo.contains(policyNo).and(QSysWfDocs.sysWfDocs.active.eq(true));
        else if (quoteNo.length() > 0)
            pred = QSysWfDocs.sysWfDocs.quoteTrans.quotNo.contains(quoteNo).and(QSysWfDocs.sysWfDocs.active.eq(true));
        else if (preparedBy.length() > 0)
            pred = QSysWfDocs.sysWfDocs.userId.username.contains(preparedBy).and(QSysWfDocs.sysWfDocs.active.eq(true));

        else pred = QSysWfDocs.sysWfDocs.active.eq(true);

        Page<SysWfDocs> page = sysWfDocsRepo.findAll(pred.and(request.searchPredicate(QSysWfDocs.sysWfDocs)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<PolicyTrans> findUserMedicalTrans(DataTablesRequest request) throws IllegalAccessException {
        BooleanExpression pred = QPolicyTrans.policyTrans.createdUser.eq(userUtils.getCurrentUser()).and(QPolicyTrans.policyTrans.authStatus.eq("D").or(QPolicyTrans.policyTrans.authStatus.eq("R"))).and(QPolicyTrans.policyTrans.product.proGroup.prgType.equalsIgnoreCase("MD"));
        Page<PolicyTrans> page = policyRepo.findAll(pred.and(request.searchPredicate(QPolicyTrans.policyTrans)),
                request);
        return new DataTablesResult<>(request, page);
    }

//    @Override
//    @Transactional(readOnly = true)
//    public Page<ClientDef> findActiveClients(String paramString, Pageable paramPageable) {
//        Predicate pred = null;
//        if (paramString == null || StringUtils.isBlank(paramString)) {
//            pred = QClientDef.clientDef.isNotNull().and(QClientDef.clientDef.authStatus.eq("Y"));
//        } else {
//            pred = (QClientDef.clientDef.authStatus.eq("Y") .and(QClientDef.clientDef.status.notIn("T")))
//                    .and(QClientDef.clientDef.fname.containsIgnoreCase(paramString)
//                            .or(QClientDef.clientDef.otherNames.concat(QClientDef.clientDef.fname).containsIgnoreCase(paramString))
//                            .or(QClientDef.clientDef.idNo.contains(paramString))
//                            .or(QClientDef.clientDef.passportNo.contains(paramString))
//                            .or(QClientDef.clientDef.tenantNumber.containsIgnoreCase(paramString)));
//        }
//        return clientRepo.findAll(pred, paramPageable);
//    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientsDto> findActiveClients(String paramString, Pageable pageable) {
        StringBuilder searchValue = new StringBuilder();
        if (paramString == null) searchValue = new StringBuilder("%%");
        else {
            String[] splitStr = paramString.trim().split("\\s+");
            for (String search : splitStr) {
                searchValue.append("%").append(search).append("%");
            }
        }
        final List<ClientsDto> clientDTOList = new ArrayList<>();
        List<Object[]> clientsList = clientRepo.searchClientsLists(searchValue.toString(), pageable.getPageNumber(), pageable.getPageSize());
        long rowCount = 0L;
        if (!clientsList.isEmpty()) rowCount = ((BigInteger) clientsList.get(0)[6]).intValue();
        for (Object[] client : clientsList) {
            ClientsDto clientDTO = new ClientsDto();
            clientDTO.setTenId(((BigInteger) client[0]).longValue());
            clientDTO.setFname((String) client[2]);
            clientDTO.setOtherNames((String) client[3]);
            clientDTO.setTenantNumber((String) client[1]);
            clientDTO.setIdNo((String) client[4]);
            clientDTO.setPinNo((String) client[5]);
            clientDTOList.add(clientDTO);
        }
        return new PageImpl<>(clientDTOList, pageable, rowCount);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProspectsDTO> findActiveProspect(String paramString, Pageable pageable) {
        StringBuilder searchValue = new StringBuilder();
        if (paramString == null) searchValue = new StringBuilder("%%");
        else {
            String[] splitStr = paramString.trim().split("\\s+");
            for (String search : splitStr) {
                searchValue.append("%").append(search).append("%");
            }
        }
        final List<ProspectsDTO> prospectsDTOList = new ArrayList<>();
        List<Object[]> prospectList = prospectsRepo.searchProspectLists(searchValue.toString(), pageable.getPageNumber(), pageable.getPageSize());
        long rowCount = 0L;
        if (!prospectList.isEmpty()) rowCount = ((BigInteger) prospectList.get(0)[5]).intValue();
        for (Object[] prospect : prospectList) {
            ProspectsDTO prospectDTO = new ProspectsDTO();
            prospectDTO.setTenId(((BigInteger) prospect[0]).longValue());
            prospectDTO.setFname((String) prospect[2]);
            prospectDTO.setOtherNames((String) prospect[3]);
            prospectDTO.setProspShtDesc((String) prospect[1]);
            prospectDTO.setIdNo((String) prospect[4]);
            prospectsDTOList.add(prospectDTO);
        }
        return new PageImpl<>(prospectsDTOList, pageable, rowCount);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BinderDTO> findLifeInsuranceBinder(String paramString, Pageable pageable, String bindType, Long productId) {
        StringBuilder searchValue = new StringBuilder();
        if (paramString == null) searchValue = new StringBuilder("%%");
        else {
            String[] splitStr = paramString.trim().split("\\s+");
            for (String search : splitStr) {
                searchValue.append("%").append(search).append("%");
            }
        }
        final List<BinderDTO> binders = new ArrayList<>();
        List<Object[]> binderList = binderRepo.searchLifeProductBinders(searchValue.toString(), pageable.getPageNumber(), pageable.getPageSize());
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
    public Page<BinderDTO> findInsuranceBinder(String paramString, Pageable pageable, String bindType, Long productId) {
        StringBuilder searchValue = new StringBuilder();
        if (paramString == null) searchValue = new StringBuilder("%%");
        else {
            String[] splitStr = paramString.trim().split("\\s+");
            for (String search : splitStr) {
                searchValue.append("%").append(search).append("%");
            }
        }
        final List<BinderDTO> binders = new ArrayList<>();
        List<Object[]> binderList = binderRepo.searchProductBinders(searchValue.toString(), pageable.getPageNumber(), pageable.getPageSize(), productId);
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
    public Page<BinderDTO> findCompInsuranceBinder(String paramString, Pageable pageable, String bindType, Long qouteId) {
        StringBuilder searchValue = new StringBuilder();
        if (paramString == null) searchValue = new StringBuilder("%%");
        else {
            String[] splitStr = paramString.trim().split("\\s+");
            for (String search : splitStr) {
                searchValue.append("%").append(search).append("%");
            }
        }
        final List<BinderDTO> binders = new ArrayList<>();
        List<Object[]> binderList = binderRepo.searchCompBinders(searchValue.toString(), pageable.getPageNumber(), pageable.getPageSize(), qouteId);
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
    public Page<ProductsDef> findMultiProducts(String paramString, Pageable paramPageable) {
        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred = QProductsDef.productsDef.isNotNull()
                    .and(QProductsDef.productsDef.proGroup.prgType.equalsIgnoreCase("MP"))
                    .and(QProductsDef.productsDef.active.eq(Boolean.TRUE));

        } else {
            pred = QProductsDef.productsDef.isNotNull().and(QProductsDef.productsDef.proShtDesc.equalsIgnoreCase(paramString).or(QProductsDef.productsDef.proDesc.equalsIgnoreCase(paramString)))
                    .and(QProductsDef.productsDef.proGroup.prgType.equalsIgnoreCase("MP"))
                    .and(QProductsDef.productsDef.active.eq(Boolean.TRUE));
        }
        return productRepo.findAll(pred, paramPageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PolicyTrans> findClientPolicies(String paramString, Pageable paramPageable, Long clientId) {
        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred = QPolicyTrans.policyTrans.client.tenId.eq(clientId).and(QPolicyTrans.policyTrans.isNotNull()).and(QPolicyTrans.policyTrans.currentStatus.equalsIgnoreCase("A"));
        } else {
            pred = QPolicyTrans.policyTrans.client.tenId.eq(clientId).and(QPolicyTrans.policyTrans.polNo.containsIgnoreCase(paramString)
                            .or(QPolicyTrans.policyTrans.clientPolNo.containsIgnoreCase(paramString)))
                    .and(QPolicyTrans.policyTrans.currentStatus.equalsIgnoreCase("A"));
        }
        return policyRepo.findAll(pred, paramPageable);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<BindersDef> findLifeBinder(String paramString, Pageable paramPageable, String bindType) {
        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred = QBindersDef.bindersDef.binType.eq(bindType).and(QBindersDef.bindersDef.isNotNull()).and(QBindersDef.bindersDef.product.proGroup.prgType.equalsIgnoreCase("L").
                    and(QBindersDef.bindersDef.binStatus.equalsIgnoreCase("Authorised"))
                    .and(QBindersDef.bindersDef.active.eq(Boolean.TRUE)));
        } else {
            pred = QBindersDef.bindersDef.binType.eq(bindType).and(QBindersDef.bindersDef.binName.containsIgnoreCase(paramString)
                            .or(QBindersDef.bindersDef.binShtDesc.containsIgnoreCase(paramString)))
                    .and(QBindersDef.bindersDef.product.proGroup.prgType.equalsIgnoreCase("L")
                            .and(QBindersDef.bindersDef.binStatus.equalsIgnoreCase("Authorised"))
                            .and(QBindersDef.bindersDef.active.eq(Boolean.TRUE)));
        }
        return binderRepo.findAll(pred, paramPageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BindersDef> findAllBinders(String paramString, Pageable paramPageable) {
        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred = QBindersDef.bindersDef.binStatus.equalsIgnoreCase("Authorised")
                    .and(QBindersDef.bindersDef.active.eq(Boolean.TRUE));
        } else {
            pred = QBindersDef.bindersDef.binName.containsIgnoreCase(paramString)
                    .or(QBindersDef.bindersDef.binShtDesc.containsIgnoreCase(paramString))
                    .and(QBindersDef.bindersDef.binStatus.equalsIgnoreCase("Authorised"))
                    .and(QBindersDef.bindersDef.active.eq(Boolean.TRUE));
        }
        return binderRepo.findAll(pred, paramPageable);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<CurrencyDTO> findCurrencies(String paramString, Pageable pageable) {
        final String search = (paramString != null) ? "%" + paramString + "%" : "%%";
        List<Object[]> currencies = currencyRepo.findSystemCurrencies(search.toLowerCase(), pageable.getPageNumber(), pageable.getPageSize());
        final List<CurrencyDTO> currencyDTOList = new ArrayList<>();
        long rowCount = 0L;
        if (!currencies.isEmpty()) rowCount = ((BigInteger) currencies.get(0)[2]).intValue();
        for (Object[] currency : currencies) {
            CurrencyDTO currencyDTO = new CurrencyDTO();
            currencyDTO.setCurCode(((BigInteger) currency[0]).longValue());
            currencyDTO.setCurName((String) currency[1]);
            currencyDTOList.add(currencyDTO);
        }
        return new PageImpl<>(currencyDTOList, pageable, rowCount);
    }

    @Override
    public Page<CurrencyDTO> findOtherCurrencies(String paramString, Pageable pageable) {
        final String search = (paramString != null) ? "%" + paramString + "%" : "%%";
        List<Object[]> currencies = currencyRepo.findOtherSystemCurrencies(search.toLowerCase(), pageable.getPageNumber(), pageable.getPageSize());
        final List<CurrencyDTO> currencyDTOList = new ArrayList<>();
        long rowCount = 0L;
        if (!currencies.isEmpty()) rowCount = ((BigInteger) currencies.get(0)[2]).intValue();
        for (Object[] currency : currencies) {
            CurrencyDTO currencyDTO = new CurrencyDTO();
            currencyDTO.setCurCode(((BigInteger) currency[0]).longValue());
            currencyDTO.setCurName((String) currency[1]);
            currencyDTOList.add(currencyDTO);
        }
        return new PageImpl<>(currencyDTOList, pageable, rowCount);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentModesDTO> findPaymentModes(String paramString, Pageable pageable) {
        final String search = (paramString != null) ? "%" + paramString + "%" : "%%";
        List<Object[]> paymentmodes = paymentModeRepo.findPymentModes(search.toLowerCase(), pageable.getPageNumber(), pageable.getPageSize());
        final List<PaymentModesDTO> paymentModesDTOList = new ArrayList<>();
        long rowCount = 0L;
        if (!paymentmodes.isEmpty()) rowCount = ((BigInteger) paymentmodes.get(0)[2]).intValue();
        for (Object[] paymentmode : paymentmodes) {
            PaymentModesDTO paymentModesDTO = new PaymentModesDTO();
            paymentModesDTO.setPmId(((BigInteger) paymentmode[0]).longValue());
            paymentModesDTO.setPmDesc((String) paymentmode[1]);
            paymentModesDTOList.add(paymentModesDTO);
        }
        return new PageImpl<>(paymentModesDTOList, pageable, rowCount);
    }


    @Override
    public Page<AccountDef> findInhouseAgents(String paramString, Pageable paramPageable) {
        Predicate pred = null;
//        if (paramString == null || StringUtils.isBlank(paramString)) {
//            pred = QAccountDef.accountDef.isNotNull().and(QAccountDef.accountDef.accountType.accShtDesc.eq("SUB AGENT")
//                    .and(QAccountDef.accountDef.wef.loe(new Date())).and(QAccountDef.accountDef.wet.coalesce(DateUtils.addDays(new Date(), 1)).asDate().after(new Date()));
//        } else {
//            pred = QAccountDef.accountDef.name.containsIgnoreCase(paramString).and(QAccountDef.accountDef.accountType.accountType.eq(AccountTypeEnum.IA))
//                    .and(QAccountDef.accountDef.wef.loe(new Date())).and(QAccountDef.accountDef.wet.coalesce(DateUtils.addDays(new Date(), 1)).asDate().after(new Date()));
//        }
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred = QAccountDef.accountDef.accountType.accountType.eq(AccountTypeEnum.SUB);

        } else {
            pred = QAccountDef.accountDef.accountType.accountType.eq(AccountTypeEnum.SUB)
                    .and((QAccountDef.accountDef.absaNo.containsIgnoreCase(paramString))
                            .or(QAccountDef.accountDef.name.containsIgnoreCase(paramString)));
        }
        return accountRepo.findAll(pred, paramPageable);
    }

    @Override
    public Page<AccountDef> findIntroducerAgents(String paramString, Pageable paramPageable) {
        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred = QAccountDef.accountDef.accountType.accountType.eq(AccountTypeEnum.INT);
        } else {
            pred = QAccountDef.accountDef.accountType.accountType.eq(AccountTypeEnum.INT)
                    .and((QAccountDef.accountDef.absaNo.containsIgnoreCase(paramString))
                            .or(QAccountDef.accountDef.name.containsIgnoreCase(paramString)));
        }
        return accountRepo.findAll(pred, paramPageable);
    }

    @Override
    public Page<AccountsDTO> findInhouseAgentsDto(String paramString, Pageable paramPageable) {
        final String search = (paramString != null) ? "%" + paramString + "%" : "%%";
        List<Object[]> agents = accountRepo.findAllInhouseAgents(search.toLowerCase(),
                paramPageable.getPageNumber(),
                paramPageable.getPageSize());
        final List<AccountsDTO> agentDTOList = new ArrayList<>();
        long rowCount = 0l;

        if (!agents.isEmpty()) rowCount = ((BigInteger) agents.get(0)[10]).longValue(); // total_rows is at index 9

        for (Object[] agent : agents) {
            AccountsDTO accountsDTO = new AccountsDTO();

            // Map the fields based on the SELECT order in your query
            accountsDTO.setAcctId(((BigInteger) agent[0]).longValue());           // acct_id
            accountsDTO.setName((String) agent[1]);                              // acct_name
            accountsDTO.setShtDesc((String) agent[2]);                         // acct_sht_desc
            accountsDTO.setAbsaNo((String) agent[7]);                            // acct_absa_no
            accountsDTO.setAcctTypeId(((BigInteger) agent[9]).longValue()); // acc_id (account type ID)

            agentDTOList.add(accountsDTO);
        }

        return new PageImpl<>(agentDTOList, paramPageable, rowCount);
    }

    @Override
    public Page<AccountsDTO> findIntroducerAgentsDto(String paramString, Pageable paramPageable) {
        final String search = (paramString != null) ? "%" + paramString + "%" : "%%";
        List<Object[]> agents = accountRepo.findAllIntroducerAgents(search.toLowerCase(),
                paramPageable.getPageNumber(),
                paramPageable.getPageSize());
        final List<AccountsDTO> agentDTOList = new ArrayList<>();
        long rowCount = 0l;

        if (!agents.isEmpty()) rowCount = ((BigInteger) agents.get(0)[9]).longValue(); // total_rows is at index 9

        for (Object[] agent : agents) {
            AccountsDTO accountsDTO = new AccountsDTO();

            // Map the fields based on the SELECT order in your query
            accountsDTO.setAcctId(((BigInteger) agent[0]).longValue());           // acct_id
            accountsDTO.setName((String) agent[1]);                              // acct_name
            accountsDTO.setShtDesc((String) agent[2]);                           // acct_sht_desc
            accountsDTO.setAbsaNo((String) agent[7]);                            // acct_absa_no
            // agent[9] is total_rows, we don't set it in DTO

            agentDTOList.add(accountsDTO);
        }

        return new PageImpl<>(agentDTOList, paramPageable, rowCount);
    }

    @Override
    public Page<AccountDef> findMarketerAgents(String paramString, Pageable paramPageable) {
        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred = QAccountDef.accountDef.accountType.accountType.eq(AccountTypeEnum.MRK);
        } else {
            pred = QAccountDef.accountDef.accountType.accountType.eq(AccountTypeEnum.MRK)
                    .and((QAccountDef.accountDef.absaNo.containsIgnoreCase(paramString))
                            .or(QAccountDef.accountDef.name.containsIgnoreCase(paramString)));
        }
        return accountRepo.findAll(pred, paramPageable);
    }

    @Override
    public Page<OrgRegionsDTO> findOrgRegions(String paramString, Pageable paramPageable) {
        final String search = (paramString != null) ? "%" + paramString + "%" : "%%";
        List<Object[]> regions = regionRepo.findAllRegions(search.toLowerCase(), paramPageable.getPageNumber(), paramPageable.getPageSize());
        final List<OrgRegionsDTO> regionDTOList = new ArrayList<>();
        long rowCount = 0l;
        if (!regions.isEmpty()) rowCount = ((BigInteger) regions.get(0)[2]).intValue();
        for (Object[] region : regions) {
            OrgRegionsDTO orgRegionsDTO = new OrgRegionsDTO();
            orgRegionsDTO.setRegCode(((BigInteger) region[0]).longValue());
            orgRegionsDTO.setRegDesc((String) region[1]);
            regionDTOList.add(orgRegionsDTO);
        }
        return new PageImpl<>(regionDTOList, paramPageable, rowCount);
    }

    @Override
    public Page<MakerCheckDTO> findAllCheckers(String paramString, Pageable paramPageable) {
        final String search = (paramString != null) ? "%" + paramString + "%" : "%%";
        List<Object[]> checkers = makerCheckerRepo.findAllCheckers(search.toLowerCase(), paramPageable.getPageNumber(), paramPageable.getPageSize());
        final List<MakerCheckDTO> taskDTOList = new ArrayList<>();
        long rowCount = 0l;
        if (!checkers.isEmpty()) rowCount = ((BigInteger) checkers.get(0)[5]).intValue();
        for (Object[] checker : checkers) {
            MakerCheckDTO makerCheckDTO = new MakerCheckDTO();
            makerCheckDTO.setCheckerId(((BigInteger) checker[0]).longValue());
            makerCheckDTO.setAbNo((String) checker[1]);
            makerCheckDTO.setCheckedBy((String) checker[2]);
            makerCheckDTO.setAssignedCheckers((String) checker[3]);
            makerCheckDTO.setUserEmail((String) checker[4]);
            taskDTOList.add(makerCheckDTO);
        }
        return new PageImpl<>(taskDTOList, paramPageable, rowCount);
    }

    @Override
    public Page<MakerCheckDTO> findTaskTypes(String paramString, Pageable paramPageable) {
        final String search = (paramString != null) ? "%" + paramString + "%" : "%%";
        List<Object[]> types = makerCheckerRepo.findAllTasks(search.toLowerCase(), paramPageable.getPageNumber(), paramPageable.getPageSize());
        final List<MakerCheckDTO> taskDTOList = new ArrayList<>();
        long rowCount = 0l;
        if (!types.isEmpty()) rowCount = ((BigInteger) types.get(0)[2]).intValue();
        for (Object[] type : types) {
            MakerCheckDTO makerCheckDTO = new MakerCheckDTO();
            makerCheckDTO.setTaskName((String) type[0]);
            makerCheckDTO.setTaskType((String) type[1]);
            taskDTOList.add(makerCheckDTO);
        }
        return new PageImpl<>(taskDTOList, paramPageable, rowCount);
    }

    @Override
    public Page<User> findleadsMan(String paramString, Pageable pageable) {
        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred = QUser.user.enabled.eq("1");
        } else {
            pred = QUser.user.enabled.eq("1")
                    .and((QUser.user.name.containsIgnoreCase(paramString))
                            .or(QUser.user.username.containsIgnoreCase(paramString))
                            .or(QUser.user.absaNo.containsIgnoreCase(paramString)));
        }
        return userRepo.findAll(pred, pageable);
    }

    @Override
    public Page<LeadmanDto> findleadsManDto(String paramString, Pageable pageable) {
        final String search = (paramString != null) ? "%" + paramString + "%" : "%%";
        List<Object[]> users = userRepo.findAllLeadsMan(search.toLowerCase(),
                pageable.getPageNumber(),
                pageable.getPageSize());
        final List<LeadmanDto> leadmanDTOList = new ArrayList<>();
        long rowCount = 0l;

        if (!users.isEmpty()) rowCount = ((BigInteger) users.get(0)[4]).longValue();

        for (Object[] user : users) {
            LeadmanDto leadmanDto = new LeadmanDto();

            // Map user fields directly to LeadmanDto fields
            leadmanDto.setId(((BigInteger) user[0]).longValue());         // user_id
            leadmanDto.setName((String) user[1]);                        // user_name
            leadmanDto.setUsername((String) user[2]);                    // user_username
            leadmanDto.setAbsaNo((String) user[3]);                      // user_absa_no
            // user[4] is total_rows

            leadmanDTOList.add(leadmanDto);
        }

        return new PageImpl<>(leadmanDTOList, pageable, rowCount);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BranchDTO> findUserBranches(String paramString, Pageable paramPageable) {
        final Long userLogged = userUtils.getCurrentUser().getId();
        final String search = (paramString != null) ? "%" + paramString + "%" : "%%";
        List<Object[]> branches = branchRepo.findBranches(search.toLowerCase(), userLogged, paramPageable.getPageNumber(), paramPageable.getPageSize());
        final List<BranchDTO> branchDTOList = new ArrayList<>();
        long rowCount = 0l;
        if (!branches.isEmpty()) rowCount = ((BigInteger) branches.get(0)[2]).intValue();
        for (Object[] branch : branches) {
            BranchDTO branchDTO = new BranchDTO();
            branchDTO.setObId(((BigInteger) branch[0]).longValue());
            branchDTO.setObName((String) branch[1]);
            branchDTOList.add(branchDTO);
        }
        return new PageImpl<>(branchDTOList, paramPageable, rowCount);
    }

    @Override
    public Page<BranchDTO> findAllBranches(String paramString, Pageable paramPageable) {
        final String search = (paramString != null) ? "%" + paramString + "%" : "%%";
        List<Object[]> branches = branchRepo.findAllBranches(search.toLowerCase(), paramPageable.getPageNumber(), paramPageable.getPageSize());
        final List<BranchDTO> branchDTOList = new ArrayList<>();
        long rowCount = 0l;
        if (!branches.isEmpty()) rowCount = ((BigInteger) branches.get(0)[2]).intValue();
        for (Object[] branch : branches) {
            BranchDTO branchDTO = new BranchDTO();
            branchDTO.setObId(((BigInteger) branch[0]).longValue());
            branchDTO.setObName((String) branch[1]);
            branchDTOList.add(branchDTO);
        }
        return new PageImpl<>(branchDTOList, paramPageable, rowCount);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubClassDef> findBinderSubclasses(String paramString, Pageable paramPageable, Long bindCode) {
        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred = QBinderDetails.binderDetails.binder.binId.eq(bindCode).and(QBinderDetails.binderDetails.isNotNull());
        } else {
            pred = QBinderDetails.binderDetails.binder.binId.eq(bindCode).and(QBinderDetails.binderDetails.subCoverTypes.subclass.subDesc.containsIgnoreCase(paramString)
                    .or(QBinderDetails.binderDetails.subCoverTypes.subclass.subShtDesc.containsIgnoreCase(paramString)));
        }
        List<BinderDetails> bindeDetails = binderDetRepo.findAll(pred, paramPageable).getContent();
        List<SubClassDef> subclassList =
                bindeDetails.stream().map(det -> {
                    SubClassDef subclass = det.getSubCoverTypes().getSubclass();
                    return subclass;
                }).distinct().collect(Collectors.toList());
        Page<SubClassDef> subclasses = new PageImpl<>(subclassList);
        return subclasses;
    }


    @Override
    @Transactional(readOnly = true)
    public Page<CoverTypesDef> findBinderCoverTypes(String paramString, Pageable paramPageable, Long bindCode, Long subCode) {
        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred = QBinderDetails.binderDetails.subCoverTypes.subclass.subId.eq(subCode).and(QBinderDetails.binderDetails.binder.binId.eq(bindCode))
                    .and(QBinderDetails.binderDetails.isNotNull());
        } else {
            pred = QBinderDetails.binderDetails.subCoverTypes.subclass.subId.eq(subCode).and(QBinderDetails.binderDetails.binder.binId.eq(bindCode))
                    .and(QBinderDetails.binderDetails.subCoverTypes.coverTypes.covName.containsIgnoreCase(paramString)
                            .or(QBinderDetails.binderDetails.subCoverTypes.coverTypes.covShtDesc.containsIgnoreCase(paramString)));
        }
        List<BinderDetails> bindeDetails = binderDetRepo.findAll(pred, paramPageable).getContent();
        List<CoverTypesDef> coverTypesList = bindeDetails.stream().map(det -> {
            List<BigDecimal> commissRates = binderDetRepo.getDefaultComm(det.getBinder().getBinId());

            CoverTypesDef coverType = det.getSubCoverTypes().getCoverTypes();
            coverType.setDetId(det.getDetId());
            coverType.setBinId(det.getBinder().getBinId());
            final String distribution = det.getDistribution();
            String firstInstallment = null;
            if (distribution != null && distribution.contains(":")) {
                firstInstallment = distribution.split(":")[0];
            } else firstInstallment = distribution;
            coverType.setDistribution(firstInstallment);
            coverType.setInstallmentsNo(det.getInstallmentsNo());
            if (commissRates.size() == 1) {
                coverType.setCommRate(commissRates.get(0));
            }
            return coverType;
        }).collect(Collectors.toList());
        Page<CoverTypesDef> covertypes = new PageImpl<>(coverTypesList);
        return covertypes;
    }

    @Override
    public Page<CoverTypesDef> findBinderSubCoverTypes(String paramString, Pageable paramPageable, Long bindCode) {
        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred = (QBinderDetails.binderDetails.binder.binId.eq(bindCode))
                    .and(QBinderDetails.binderDetails.isNotNull());
        } else {
            pred = (QBinderDetails.binderDetails.binder.binId.eq(bindCode))
                    .and(QBinderDetails.binderDetails.subCoverTypes.coverTypes.covName.containsIgnoreCase(paramString)
                            .or(QBinderDetails.binderDetails.subCoverTypes.coverTypes.covShtDesc.containsIgnoreCase(paramString)));
        }
        List<BinderDetails> bindeDetails = binderDetRepo.findAll(pred, paramPageable).getContent();
        List<CoverTypesDef> coverTypesList = bindeDetails.stream().map(det -> {
            CoverTypesDef coverType = det.getSubCoverTypes().getCoverTypes();
            coverType.setDetId(det.getDetId());
            coverType.setSubclassName(det.getSubCoverTypes().getSubclass().getSubDesc());
            return coverType;
        }).collect(Collectors.toList());
        Page<CoverTypesDef> covertypes = new PageImpl<>(coverTypesList);
        return covertypes;
    }

    @Override
    @Transactional(readOnly = true)
    public Set<RiskSectionBean> getBinderPremRates(Long detId) {
        Iterable<PremRatesDef> premiumRates = premRatesRepo.findAll(QPremRatesDef.premRatesDef.binderDet.detId.eq(detId).and(QPremRatesDef.premRatesDef.active.eq(true)));
        Set<RiskSectionBean> premRatesDefs = Streamable.streamOf(premiumRates).map(a -> {
            RiskSectionBean sectionBean = new RiskSectionBean();
            sectionBean.setPk(a.getId());
            sectionBean.setPremId(a.getSection().getId().longValue());
            sectionBean.setDivFactor(a.getDivFactor());
            sectionBean.setSectionDesc(a.getSection().getDesc());
            sectionBean.setRate(a.getRate());
            sectionBean.setLimitsAllowed(a.getBinderDet().getLimitsAllowed());
            sectionBean.setFreeLimit(a.getFreeLimit());
            sectionBean.setRatesApplicable(a.getRangeApplicable());
            if (a.getSection().getType() == SectionTypes.RD) {
                sectionBean.setRider("Y");
            } else sectionBean.setRider("N");
            sectionBean.setMandatory((a.getMandatory() != null && "Y".equalsIgnoreCase(a.getMandatory())) ? "Y" : "N");
            return sectionBean;
        }).collect(Collectors.toSet());
        return premRatesDefs;
    }

    @Override
    @Transactional(readOnly = true)
    public Set<RiskSectionBean> getBinderClientPremRates(Long detId, Long insuredAge) {
        Iterable<PremRatesDef> premiumRates = premRatesRepo.getPremiumRates(detId, BigDecimal.valueOf(insuredAge));
        Set<RiskSectionBean> premRatesDefs = Streamable.streamOf(premiumRates).map(a -> {
            RiskSectionBean sectionBean = new RiskSectionBean();
            sectionBean.setPk(a.getId());
            sectionBean.setPremId(a.getSection().getId().longValue());
            sectionBean.setDivFactor(a.getDivFactor());
            sectionBean.setSectionDesc(a.getSection().getDesc());
            sectionBean.setRate(a.getRate());
            sectionBean.setLimitsAllowed(a.getBinderDet().getLimitsAllowed());
            sectionBean.setFreeLimit(a.getFreeLimit());
            sectionBean.setRatesApplicable(a.getRangeApplicable());
            sectionBean.setRangeType(a.getRangeType());
            if (a.getSection().getType() == SectionTypes.RD) {
                sectionBean.setRider("Y");
            } else sectionBean.setRider("N");
            sectionBean.setMandatory((a.getMandatory() != null && "Y".equalsIgnoreCase(a.getMandatory())) ? "Y" : "N");
            return sectionBean;
        }).collect(Collectors.toSet());
        return premRatesDefs;
    }


    @Override
    public PolicyTrans createLifePolicy(PolicyCreateDTO policydto, boolean isApproved) throws BadRequestException {
        System.out.println(policydto);
//        // First get the transaction type from the DTO
        String transType = policydto.getTransType();
        boolean iSBulkUpload = false;
        if(policydto.getBulkUpload() != null && !policydto.getBulkUpload().isEmpty() && "true".equalsIgnoreCase(policydto.getBulkUpload())){
            iSBulkUpload =true;
            //transType = "BU";
        }
//
//        // Then call adjustPolicyDates with both parameters
//        adjustPolicyDates(policydto, transType);
//        if ("EN".equalsIgnoreCase(transType) || "CN".equalsIgnoreCase(transType)) {
//            // Adjust risk dates proportionally for endorsements
//            adjustRiskDatesForEndorsement(policydto.getRiskBean(),
//                    policydto.getWefDate(),
//                    policydto.getWetDate());
//        }
        final Long hashCode = Long.parseLong(String.valueOf(policydto.hashCode()));
        final PolicyTrans policy = new PolicyTrans();
        org.springframework.beans.BeanUtils.copyProperties(policydto, policy);
        System.out.println(policy);

        if (policydto.getBindCode() == null) {
            throw new BadRequestException("Policy Product is mandatory...");
        }
        final BindersDef bindersDef = bindersRepo.findOne(policydto.getBindCode());
        if ("S".equalsIgnoreCase(bindersDef.getBinType())) {
            if (binderRepo.count(QBindersDef.bindersDef.fundBinder.equalsIgnoreCase("Y")) != 1) {
                throw new BadRequestException("A Fund Binder has not been set up ..Check your configuration");
            }
//            BindersDef fundBinder = binderRepo.findOne(QBindersDef.bindersDef.fundBinder.equalsIgnoreCase("Y"));
//            policy.setBindCode(fundBinder.getBinId());
//            policy.setProdId(fundBinder.getProduct().getProCode());
//            policy.setAgentId(fundBinder.getAccount().getAcctId());
        }
        PolicyTrans editPolicy = new PolicyTrans();
        if (policy.getPolicyId() != null) {
            editPolicy = policyRepo.findOne(policy.getPolicyId());
        }
        if (policydto.getClientId() == null) throw new BadRequestException("Client is Mandatory");
        if (policydto.getBindCode() == null) throw new BadRequestException("Binder is Mandatory ");
        if (policydto.getAgentId() == null) throw new BadRequestException("Intermediary is Mandatory");
        if (policydto.getProdId() == null) throw new BadRequestException("Product is Mandatory");
        if (policydto.getPaymentId() == null) throw new BadRequestException("Payment Mode is Mandatory");
        if (policydto.getBranchId() == null) throw new BadRequestException("Branch is Mandatory");
        if (policydto.getCurrencyId() == null) throw new BadRequestException("Currency is Mandatory");
        if (policy.getWefDate() == null) throw new BadRequestException("Policy Wef Date From is Mandatory");
        if (policy.getWetDate() == null) throw new BadRequestException("Policy Wet Date From is Mandatory");
        if (policy.getWefDate().after(policy.getWetDate()))
            throw new BadRequestException("Wef Date cannot be greater than Wet Date");
        System.out.println(policy.getPolTerm());
        if (policydto.getNegotiatedPremium() != null) {
            policy.setNegotiatedPremium(policydto.getNegotiatedPremium());
        }
//        Date polWetDate = dateUtils.removeTime(policy.getWetDate());
        User user = userUtils.getCurrentUser();
        if (policy.getBusinessType() == null) throw new BadRequestException("Select Business Type");
        if ("N".equalsIgnoreCase(policy.getBusinessType())) {
            if (policy.getTransType() == null || "NB".equalsIgnoreCase(policy.getTransType())) {
//                if (policy.getWetDate().after(polWetDate) || policy.getWetDate().before(polWetDate))
//                    throw new BadRequestException("The Policy Cover Period Cannot be more than a year");
            } else if ("EN".equalsIgnoreCase(policy.getTransType()) || "CN".equalsIgnoreCase(policy.getTransType())) {
                PolicyTrans currentTrans = policyRepo.findOne(policy.getPolicyId());
                PolicyTrans prevTrans = currentTrans.getPreviousTrans();
                Date currentWet = dateUtils.removeTime(policy.getWetDate());
                Date prevWet = dateUtils.removeTime(prevTrans.getWetDate());
                if (currentWet.after(prevWet) || currentWet.before(prevWet))
                    throw new BadRequestException("Policy WET Date Cannot change for endorsements...");
            }

        }

//        if ("S".equalsIgnoreCase(policy.getBusinessType()))
//            if (policy.getWetDate().after(polWetDate))
//                throw new BadRequestException("The Short Period Policy Cover Period Cannot be more than a year");

        boolean newTrans = false;
        Iterable<BinderReqrdDocs> reqdDocs = new ArrayList<>();

        //DONT CHECK FOR BULK UPLOAD REQ DOCUMENTS
        if (policydto.getRiskBean() != null && !iSBulkUpload)
            if (policydto.getRiskBean().getSclCode() != null) {
//                String transType = "";
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
                if ("NB".equalsIgnoreCase(transType)) {
                    reqdDocs = reqrdDocsRepo.findAll(QBinderReqrdDocs.binderReqrdDocs.binderDetail.detId.eq(policydto.getRiskBean().getBinderDet())
                            .and(QBinderReqrdDocs.binderReqrdDocs.mandatory.eq(true))
                            .and(QBinderReqrdDocs.binderReqrdDocs.requiredDocs.requiredDoc.appliesNewBusiness.eq(true)));
                } else if ("EN".equalsIgnoreCase(transType)) {
                    reqdDocs = reqrdDocsRepo.findAll(QBinderReqrdDocs.binderReqrdDocs.binderDetail.detId.eq(policydto.getRiskBean().getBinderDet())
                            .and(QBinderReqrdDocs.binderReqrdDocs.mandatory.eq(true))
                            .and(QBinderReqrdDocs.binderReqrdDocs.requiredDocs.requiredDoc.appliesEndorsement.eq(true)));
                } else if ("RN".equalsIgnoreCase(transType)) {
                    reqdDocs = reqrdDocsRepo.findAll(QBinderReqrdDocs.binderReqrdDocs.binderDetail.detId.eq(policydto.getRiskBean().getBinderDet())
                            .and(QBinderReqrdDocs.binderReqrdDocs.mandatory.eq(true))
                            .and(QBinderReqrdDocs.binderReqrdDocs.requiredDocs.requiredDoc.appliesRenewal.eq(true)));
                }

            }
        ProductsDef policyProduct = productRepo.findOne(policydto.getProdId());
        policy.setCurrentStatus("D");
        policy.setAuthStatus("D");

        if((bindersDef.getAdminFeeActiveStatus() != null) && (bindersDef.getAdminFeeActiveStatus().equalsIgnoreCase("Y"))) {
            policy.setAdminFeeApplicable("Y");
        } else {
            policy.setAdminFeeApplicable("N");
        }

        //if(policydto.getAdminFeePolicy()!=null && "true".equalsIgnoreCase(policydto.getAdminFeePolicy())){
        //  policy.setAdminFeeApplicable("Y");
        //}
        //else policy.setAdminFeeApplicable("N");
//        if(!isApproved && !makerCheckerRepo.exists(hashCode)) {
//            System.out.println(new Gson().toJson(policydto));
//            MakerCheckDTO makerCheckDTO = new MakerCheckDTO();
//            makerCheckDTO.setJson(new Gson().toJson(policydto));
//            makerCheckDTO.setStatus("N");
//            ClientDef clientDef = clientRepo.findOne(policydto.getClientId());
//            makerCheckDTO.setTaskName("Created Policy:Client..." + clientDef.getFname() + " " + clientDef.getOtherNames() + " Policy No " + policy.getPolNo());
//            makerCheckDTO.setTaskType("LP");
//            makerCheckDTO.setTaskCode(hashCode);
//            makerCheckerService.checkExists(makerCheckDTO);
//            makerCheckerService.createMakerChecker(makerCheckDTO);
//            return policy;
//        }
//        if(!isApproved ) {
//            System.out.println(new Gson().toJson(policydto));
//            MakerCheckDTO makerCheckDTO = new MakerCheckDTO();
//            makerCheckDTO.setJson(new Gson().toJson(policydto));
//            makerCheckDTO.setStatus("N");
//            ClientDef clientDef = clientRepo.findOne(policydto.getClientId());
//            makerCheckDTO.setTaskName("Created Policy:Client..." + clientDef.getFname() + " " + clientDef.getOtherNames() + " Policy No " + policy.getPolNo());
//            makerCheckDTO.setTaskType("LP");
//            makerCheckDTO.setTaskCode(hashCode);
//            makerCheckerService.checkExists(makerCheckDTO);
//            makerCheckerService.createMakerChecker(makerCheckDTO);
//            return policy;
//        }
        // SystemTrans transaction = null;
        boolean medicalProduct = false;
        boolean lifeProduct = false;
        boolean investmentProduct = false;
        boolean motorProduct = policyProduct.isMotorProduct();
        if (policyProduct.getProGroup().getPrgType() == null || !policyProduct.getProGroup().getPrgType().equalsIgnoreCase("N")) {
            medicalProduct = false;
            lifeProduct = false;
            investmentProduct = false;
        } else if (policyProduct.getProGroup().getPrgType().equalsIgnoreCase("MD")) {
            medicalProduct = true;
            lifeProduct = false;
            investmentProduct = false;

        } else if (policyProduct.getProGroup().getPrgType().equalsIgnoreCase("L")) {
            medicalProduct = false;
            lifeProduct = true;
            investmentProduct = false;
        } else if (policyProduct.getProGroup().getPrgType().equalsIgnoreCase("IN")) {
            medicalProduct = false;
            lifeProduct = false;
            investmentProduct = true;

        }
        if (policy.getPolicyId() == null) {

            newTrans = true;
            String policyNumberFormat = paramService.getParameterString("POLICY_NO_FORMAT");
            String endorsementFormat = paramService.getParameterString("ENDORSE_NO_FORMAT");
            String proposalFormat = paramService.getParameterString("PROPOSAL_NO_FORMAT");
            //Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("P");
            Predicate propPredicate = QSystemSequence.systemSequence.transType.eq("PR");
//            if (sequenceRepo.count(seqPredicate) == 0)
//                throw new BadRequestException("Sequence for New Business Transactions has not been defined");
            if (sequenceRepo.count(propPredicate) == 0)
                throw new BadRequestException("Sequence for Proposal Transactions has not been defined");
//            SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
//            Long seqNumber = sequence.getNextNumber();
//            final String policyNumber = templateMerger.generateFormat(policyNumberFormat, policy.getBranchId(), policy.getProdId(), policy.getWefDate(), sequence.getSeqPrefix() + String.format("%05d", seqNumber));
//            policy.setPolNo(policyNumber);
//            sequence.setLastNumber(seqNumber);
//            sequence.setNextNumber(seqNumber + 1);
//            sequenceRepo.save(sequence);
            SystemSequence lifeSequence = sequenceRepo.findOne(propPredicate);
            Long lifeSeqNumber = lifeSequence.getNextNumber();
            final String proposalNo = templateMerger.generateFormat(proposalFormat, policydto.getBranchId(), policydto.getProdId(), policy.getWefDate(), lifeSequence.getSeqPrefix() + String.format("%05d", lifeSeqNumber), null);
            if (proposalNo == null) {
                throw new BadRequestException("Proposal no has not been generated");
            }
            System.out.println("Proposal No " + proposalNo);
            policy.setProposalNo(proposalNo);
            lifeSequence.setLastNumber(lifeSeqNumber);
            lifeSequence.setNextNumber(lifeSeqNumber + 1);
            sequenceRepo.save(lifeSequence);
            Predicate endorsePredicate = QSystemSequence.systemSequence.transType.eq("E");
            if (sequenceRepo.count(endorsePredicate) == 0)
                throw new BadRequestException("Sequence for Endorsement Transactions has not been defined");
            SystemSequence endorseSequence = sequenceRepo.findOne(endorsePredicate);
            Long endosseqNumber = endorseSequence.getNextNumber();
            final String revNumber = endorseSequence.getSeqPrefix() + String.format("%05d", endosseqNumber);
            final String endorseNumber = templateMerger.generateFormat(endorsementFormat, policydto.getBranchId(), policydto.getProdId(), policy.getWefDate(), revNumber, null);
            policy.setPolRevNo(endorseNumber + "/1");
            policy.setRevisionFormat(endorseNumber);
            endorseSequence.setLastNumber(endosseqNumber);
            endorseSequence.setNextNumber(endosseqNumber + 1);
            sequenceRepo.save(endorseSequence);
//            transaction = new SystemTrans();
//            transaction.setDoneDate(new Date());
//            transaction.setDoneBy(userUtils.getCurrentUser());
//            transaction.setPolicy(policy);
//            transaction.setTransLevel("U");
//            transaction.setTransCode("NBD"); //A way to setup and look up for transaction transcode
//            transaction.setTransAuthorised("N");


            if ("S".equalsIgnoreCase(policy.getBusinessType())) {

                policy.setTransType("SP");
                policy.setPolRevStatus("SP");
            } else {
                if(iSBulkUpload){
                    policy.setTransType("BU");
                }else {
                    policy.setTransType("NB");
                }
                policy.setPolRevStatus("NB");
            }
            policy.setCurrentStatus("D");
            policy.setAuthStatus("D");
        } else {
            final PolicyTrans existing = policyRepo.findOne(policy.getPolicyId());
            policy.setPolTerm(existing.getPolTerm());
            policy.setProposalNo(existing.getProposalNo());
        }


        if (policydto.getPrevPolicy() == null) {
            policy.setPreviousTrans(policy);
        } else {
            policy.setPreviousTrans(policyRepo.findOne(policydto.getPrevPolicy()));
        }
        policy.setPolTerm(policy.getPolTerm());
        policy.setInvestmentTerm(policy.getInvestmentTerm());

        if (policy.getPolicyId() != null) {
            if (editPolicy == null)
                throw new BadRequestException("The Policy does not exist. Cannot Authorize");
            if ("A".equalsIgnoreCase(editPolicy.getAuthStatus())) {
                throw new BadRequestException("Cannot Save..Policy already authorized");
            }

            if (policy.getPolRevStatus() == null || StringUtils.isBlank(policy.getPolRevStatus())) {
                policy.setPolRevStatus(editPolicy.getPolRevStatus());
            }

            BindersDef binder = editPolicy.getBinder();
            BindersDef newBinder = binderRepo.findOne(policydto.getBindCode());
            if (binder.getBinId() != newBinder.getBinId()) {
                Iterable<RiskTrans> binderRisks = riskRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(policy.getPolicyId()));
                List<RiskTrans> newRisks = new ArrayList<>();
                for (RiskTrans risk : binderRisks) {
                    risk.setBinder(newBinder);
                    newRisks.add(risk);
                    Iterable<SectionTrans> sections = sectionRepo.findAll(QSectionTrans.sectionTrans.risk.riskId.eq(risk.getRiskId()));
                    sectionRepo.delete(sections);
                }
                riskRepo.save(newRisks);
            }


        }

        RiskTransBean riskBean = policydto.getRiskBean();
        System.out.println("INVESTMENT: " + policydto.getInvestment());
        if (policy.getPolicyId() == null && !medicalProduct && !policydto.isImportRisks()) {
//
//            // Convert policy dates to LocalDate for adjustment
//            LocalDate policyWef = policydto.getWefDate().toInstant()
//                    .atZone(ZoneId.systemDefault())
//                    .toLocalDate();
//            LocalDate policyWet = policydto.getWetDate().toInstant()
//                    .atZone(ZoneId.systemDefault())
//                    .toLocalDate();
//            adjustLifeRiskDates(policydto.getRiskBean(), policyWef, policyWet);
            Date polWef = dateUtils.removeTime(policy.getWefDate());
            Date polWet = dateUtils.removeTime(policy.getWetDate());
            Date riskWef = dateUtils.removeTime(riskBean.getWefDate());
            Date riskWet = dateUtils.removeTime(riskBean.getWetDate());
            if (riskWef.before(polWef) || riskWef.after(polWet)
                    || riskWet.before(polWef) || riskWet.after(polWet)) {
                throw new BadRequestException("Risk Cover Dates outside Policy Cover Periods ");
            }
            RiskTrans risk = new RiskTrans();
            if (riskBean.getBindCode() == null) throw new BadRequestException("Binder is Mandatory");
            if (riskBean.getSclCode() == null) throw new BadRequestException("Sub Class is Mandatory");
            if (riskBean.getCoverCode() == null) throw new BadRequestException("Cover Type is Mandatory");
            if (riskBean.getInsuredCode() == null) throw new BadRequestException("Life assured is Mandatory");
            if (riskBean.getWorkingAge() == null) throw new BadRequestException("Life assured age is Mandatory");
            SubClassDef subClassDef = subclassRepo.findOne(riskBean.getSclCode());
            risk.setBinder(binderRepo.findOne(riskBean.getBindCode()));
            risk.setCovertype(coverRepo.findOne(riskBean.getCoverCode()));
            risk.setSubclass(subClassDef);
            risk.setInsured(clientRepo.findOne(riskBean.getInsuredCode()));
            risk.setWorkingAge(riskBean.getWorkingAge());
            risk.setBinderDetails(binderDetRepo.findOne(riskBean.getBinderDet()));
            risk.setCommRate(riskBean.getCommRate());
            risk.setAutogenCert("N");
            risk.setPolicy(policy);
            risk.setComputePremium(riskBean.getPremium());
            risk.setProrata(riskBean.getProrata());
            risk.setRiskDesc(riskBean.getRiskDesc());
            risk.setRiskShtDesc(riskBean.getRiskShtDesc());
            risk.setWefDate(riskBean.getWefDate());
            risk.setWetDate(riskBean.getWetDate());
            risk.setPremium(riskBean.getPremium());
            risk.setSumInsured(riskBean.getSumInsured());
            risk.setInvestment(policydto.getInvestment());
            risk.setTopUp(policydto.getTopUp());
            risk.setComputeType(riskBean.getComputeType());
            if(iSBulkUpload){
                risk.setTransType("BU");
            }else {
                risk.setTransType("NB");
            }
            BigDecimal sumInsured;
            if(riskBean.getSumInsured()!=null && riskBean.getSumInsured().compareTo(BigDecimal.ZERO)>0){
                sumInsured = riskBean.getSumInsured();
            } else {
                sumInsured = BigDecimal.ZERO;
            }

            AtomicInteger index = new AtomicInteger(0);

            List<SectionTrans> sectionTransactions = policydto.getSections().stream().map(sectionbean -> {
                int i = index.getAndIncrement();
                SectionsDef sectiondef = setupSectionRepo.findOne(sectionbean.getSection());
                SectionTrans section = new SectionTrans();
                if(sectiondef.getType()==SectionTypes.SI){
                    section.setAmount(sumInsured);
                }
                else
                    section.setAmount((sectionbean.getAmount() == null) ? BigDecimal.ZERO : sectionbean.getAmount());
                section.setCompute(sectionbean.isCompute());
                section.setDivFactor(sectionbean.getDivFactor());
                section.setFreeLimit(sectionbean.getFreeLimit());
                if (sectionbean.getRatesApplicable() != null && "Y".equalsIgnoreCase(sectionbean.getRatesApplicable())) {
                    List<PremRatesDef> premRates = premRatesRepo.getSectPremiumRates(riskBean.getBinderDet(), sectionbean.getAmount(), sectiondef.getId());
                    if (premRates.size() == 1) {
                        section.setPremRates(premRates.get(0));
                        section.setRate(premRates.get(0).getRate());
                    }
                } else {
                    List<PremRatesDef> premRates = premRatesRepo.getSectPremiumRates(riskBean.getBinderDet(), sectiondef.getId());
                    if (premRates.size() == 1) {
                        section.setPremRates(premRates.get(0));
                        section.setRate(sectionbean.getRate());
                    }
                }
                section.setCompute(true);
                section.setSection(sectiondef);
                section.setRisk(risk);
                return section;
            }).collect(Collectors.toList());
            sectionRepo.save(sectionTransactions);
            RiskTrans savedRisk = riskRepo.save(risk);
            long riskIdentifier = Long.valueOf(String.valueOf(dateUtils.getUwYear(policy.getWefDate())) + String.valueOf(savedRisk.getRiskId()));
            PolicyActiveRisks activeRisk = new PolicyActiveRisks();
            activeRisk.setPolicy(policy);
            activeRisk.setRisk(risk);
            activeRisk.setRiskIdentifier(riskIdentifier);
            activeRisksRepo.save(activeRisk);
            savedRisk.setRiskIdentifier(riskIdentifier);
            riskRepo.save(savedRisk);
        }
        if (policy.getPolicyId() != null) {
            policy.setRevisionFormat(editPolicy.getRevisionFormat());
        }
        policy.setCommAllowed(true);
        if (policydto.getSubAgentId() != null) {
            policy.setSubAgent(accountRepo.findOne(policydto.getSubAgentId()));
        }
        if (policydto.getMarketerAgentId() != null) {
            policy.setMarketerAgent(accountRepo.findOne(policydto.getMarketerAgentId()));
        }
        if (policydto.getIntroducerAgentId() != null) {
            policy.setIntroducerAgent(accountRepo.findOne(policydto.getIntroducerAgentId()));
        }
        if (policydto.getLeadsManId() != null) {
            policy.setLeadsMan(userRepo.findOne(policydto.getLeadsManId()));
        }
        policy.setAbsaNoLeadsMan(policydto.getAbsaNoLeadsMan());
        policy.setAbsaNoMarketer(policydto.getAbsaNoMarketer());
        policy.setAbsaNoIntroducer(policydto.getAbsaNoIntroducer());
        policy.setAbsaNoSubAgent(policydto.getAbsaNoSubAgent());
        policy.setAgent(accountRepo.findOne(policydto.getAgentId()));
        policy.setBinder(binderRepo.findOne(policydto.getBindCode()));
        policy.setBranch(branchRepo.findOne(policydto.getBranchId()));
        policy.setClient(clientRepo.findOne(policydto.getClientId()));
        if (policydto.getNegotiatedPremium() != null) {
            policy.setNegotiatedPremium(policydto.getNegotiatedPremium());
        }
        System.out.println(policy);
        policy.setPaymentMode(paymentModeRepo.findOne(policydto.getPaymentId()));
        policy.setProduct(policyProduct);
        policy.setCreatedUser(user);
        policy.setTransCurrency(currencyRepo.findOne(policydto.getCurrencyId()));
        policy.setPolCreateddt(new Date());
        //MAKE LIFE POLICY RENEWABLE
        //if ("N".equalsIgnoreCase(policy.getBusinessType()))
        if ("L".equalsIgnoreCase(policy.getBusinessType()))
            policy.setRenewable(policyProduct.isRenewable());
        else
            policy.setRenewable(false);
        policy.setUwYear(dateUtils.getUwYear(policy.getWefDate()));
        //if (policyProduct.isRenewable() && "N".equalsIgnoreCase(policy.getBusinessType())) {
        if (policyProduct.isRenewable() && "L".equalsIgnoreCase(policy.getBusinessType())) {
            policy.setRenewalDate(DateUtils.addDays(policy.getWetDate(), 1));
        } else
            policy.setRenewalDate(null);
        if (policy.getTransType().equalsIgnoreCase("NB") || policy.getTransType().equalsIgnoreCase("RN") || policy.getTransType().equalsIgnoreCase("BU")) {
            policy.setCoverFrom(policy.getWefDate());
            policy.setCoverTo(policy.getWetDate());
        }
        System.out.println("frequency=" + policy.getFrequency() + ";term=" + policy.getPolTerm());
        if ("M".equalsIgnoreCase(policy.getFrequency()))
            policy.setTotalInstalments(12 * policy.getPolTerm());
        if ("A".equalsIgnoreCase(policy.getFrequency()))
            policy.setTotalInstalments(policy.getPolTerm());
        if ("S".equalsIgnoreCase(policy.getFrequency()))
            policy.setTotalInstalments(6 * policy.getPolTerm());
        if ("Q".equalsIgnoreCase(policy.getFrequency()))
            policy.setTotalInstalments(4 * policy.getPolTerm());
//        if (newTrans)
//            transRepo.save(transaction); edituwpolicy
        PolicyTrans savedTrans = policyRepo.save(policy);
        Set<TransChecks> transChecks = generalTransRulesExecutor.createLifeChecks(savedTrans);
        transChecksRepo.save(transChecks);
        System.out.println(savedTrans);
        if (newTrans) {
            workflowService.startNewWorkFlow(DocType.GEN_UW_DOCUMENT, String.valueOf(savedTrans.getPolicyId()), policy, (medicalProduct) ? "Y" : "N", null, null, null, null);
        }
        Iterable<RiskTrans> risks = riskRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(savedTrans.getPolicyId()));
        if (!medicalProduct && !lifeProduct && !investmentProduct) {
            for (RiskTrans risk : risks) {
                if (motorProduct) {
                    final List<BigInteger> codes = subclassCertTypesRepo.getCertificateCode(risk.getSubclass().getSubId());
                    if (codes.size() == 1) {
                        final RiskCertForm riskCertForm = new RiskCertForm();
                        riskCertForm.setSubclasscertId(((BigInteger) codes.get(0)).longValue());
                        riskCertForm.setWefDate(risk.getWefDate());
                        riskCertForm.setWetDate(risk.getWetDate());
                        riskCertForm.setRiskId(risk.getRiskId());
                        try {
                            certService.createRiskCert(riskCertForm);
                        } catch (BadRequestException ex) {
                            System.out.println("Error creating cert....");
                        }
                    }
                }
                List<RiskDocs> riskDocs = Streamable.streamOf(reqdDocs)
                        .filter(reqdDoc -> riskDocsRepo.count(QRiskDocs.riskDocs.risk.riskIdentifier.eq(risk.getRiskIdentifier()).and(QRiskDocs.riskDocs.reqdDocs.sclReqrdId.eq(reqdDoc.getRequiredDocs().getSclReqrdId()))) == 0)
                        .map(reqdDoc -> {
                            RiskDocs riskDoc = new RiskDocs();
                            riskDoc.setReqdDocs(reqdDoc.getRequiredDocs());
                            riskDoc.setRisk(risk);
                            return riskDoc;
                        }).collect(Collectors.toList());

                riskDocsRepo.save(riskDocs);
            }
        }
        return savedTrans;
    }
    private void adjustRiskDatesForEndorsement(RiskTransBean riskBean, Date policyWef, Date policyWet) throws BadRequestException {
        if (riskBean == null) return;

        ZoneId zone = ZoneId.systemDefault();

        // Convert dates to LocalDate for easier manipulation
        LocalDate policyStart = policyWef.toInstant().atZone(zone).toLocalDate();
        LocalDate policyEnd = policyWet.toInstant().atZone(zone).toLocalDate();

        LocalDate riskStart = riskBean.getWefDate().toInstant().atZone(zone).toLocalDate();
        LocalDate riskEnd = riskBean.getWetDate().toInstant().atZone(zone).toLocalDate();

        // 1. First adjust WEF for holidays (if needed)
        LocalDate adjustedRiskStart = HolidayUtils.getAdjustedTransactionDate(riskStart);

        // 2. Maintain original risk duration
        long riskDurationDays = ChronoUnit.DAYS.between(riskStart, riskEnd);
        LocalDate adjustedRiskEnd = adjustedRiskStart.plusDays(riskDurationDays);

        // 3. Ensure risk period doesn't exceed policy period
        if (adjustedRiskEnd.isAfter(policyEnd)) {
            // Option 1: Truncate at policy end (recommended)
            adjustedRiskEnd = policyEnd;

        }

        // Final validation
        if (adjustedRiskStart.isBefore(policyStart) || adjustedRiskEnd.isAfter(policyEnd)) {
            throw new BadRequestException("Risk period must be within policy period");
        }

        // Update the risk bean
        riskBean.setWefDate(Date.from(adjustedRiskStart.atStartOfDay(zone).toInstant()));
        riskBean.setWetDate(Date.from(adjustedRiskEnd.atStartOfDay(zone).toInstant()));
    }

    @PreAuthorize("hasAnyAuthority('SAVE_POLICY')")
    @Override
    @Modifying
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public PolicyTrans createPolicy(PolicyCreateDTO policydto, boolean isApproved) throws BadRequestException {
        log.info("Received PolicyCreateDTO: {}", new Gson().toJson(policydto));
        log.info("Received policy with dates - WEF: {}, WET: {}", policydto.getWefDate(), policydto.getWetDate());

        boolean iSBulkUpload = false;
        if(policydto.getBulkUpload() != null && !policydto.getBulkUpload().isEmpty() && "true".equalsIgnoreCase(policydto.getBulkUpload())){
            iSBulkUpload =true;
            //transType = "BU";
        }

        // Fetch PolicyTrans if policyId is present and dates are null
        if (policydto.getPolicyId() != null && (policydto.getWefDate() == null || policydto.getWetDate() == null)) {
            PolicyTrans policyTrans = policyRepo.findOne(policydto.getPolicyId());
            if (policyTrans == null) {
                log.error("No PolicyTrans found for policyId: {}", policydto.getPolicyId());
                throw new BadRequestException("Policy not found for ID: " + policydto.getPolicyId());
            }
            if (policydto.getWefDate() == null) {
                if (policyTrans.getWefDate() == null) {
                    log.error("PolicyTrans WEF date is null for policyId: {}", policydto.getPolicyId());
                    throw new BadRequestException("Policy WEF date is mandatory");
                }
                policydto.setWefDate(policyTrans.getWefDate());
                log.info("Set WEF date from PolicyTrans: {}", policydto.getWefDate());
            }
            if (policydto.getWetDate() == null) {
                if (policyTrans.getWetDate() == null) {
                    log.error("PolicyTrans WET date is null for policyId: {}", policydto.getPolicyId());
                    throw new BadRequestException("Policy WET date is mandatory");
                }
                policydto.setWetDate(policyTrans.getWetDate());
                log.info("Set WET date from PolicyTrans: {}", policydto.getWetDate());
            }
        }

        // Check for holiday or weekend before adjusting dates
        String transType = policydto.getTransType();
//        if (!"EN".equalsIgnoreCase(transType) && !"CN".equalsIgnoreCase(transType) &&
//                policydto.getWefDate() != null && policydto.getWetDate() != null) {
//            // Directly convert java.sql.Date to LocalDate
////            LocalDate wefLocalDate = policydto.getWefDate().toLocalDate();
//            LocalDate wefLocalDate = new java.sql.Date(policydto.getWefDate().getTime())
//                    .toLocalDate();  // Convert the Date to LocalDate manually
//
//            // Check if WEF date falls on holiday or weekend
//            if (HolidayUtils.isHolidayOrWeekend(wefLocalDate)) {
//                adjustPolicyDates(policydto, transType);
//            }
//        }
        final Long hashCode = Long.parseLong(String.valueOf(policydto.hashCode()));
        final PolicyTrans policy = new PolicyTrans();
        org.springframework.beans.BeanUtils.copyProperties(policydto, policy);
        if (policydto.getBindCode() == null) {
            throw new BadRequestException("Policy Product is mandatory...");
        }


        final BindersDef bindersDef = bindersRepo.findOne(policydto.getBindCode());
        if ("S".equalsIgnoreCase(bindersDef.getBinType())) {

            if (binderRepo.count(QBindersDef.bindersDef.fundBinder.equalsIgnoreCase("Y")) != 1) {
                throw new BadRequestException("A Fund Binder has not been set up ..Check your configuration");
            }
//            BindersDef fundBinder = binderRepo.findOne(QBindersDef.bindersDef.fundBinder.equalsIgnoreCase("Y"));
//            policy.setBindCode(fundBinder.getBinId());
//            policy.setProdId(fundBinder.getProduct().getProCode());
//            policy.setAgentId(fundBinder.getAccount().getAcctId());
        }
        if (policy.getSourceId() != null) {
            policy.setSource(businessSourcesRepo.findOne(policy.getSourceId()));
        }

        List<BigDecimal> rate = binderDetRepo.getDefaultComm(bindersDef.getBinId());

        if(rate.isEmpty() || rate.get(0)==null || rate.get(0).compareTo(BigDecimal.ZERO) <=0){
            throw new BadRequestException("Commission Rate is not set up for the contract. Please set up to continue...");
        }

        boolean multiproduct = false;
        if (policydto.getBindCodes() != null && policydto.getBindCodes().length > 0) {
            multiproduct = true;
        }

        if (policydto.getPolicyId() != null)
            multiproduct = policyBindersRepo.count(QPolicyBinders.policyBinders.policyTrans.policyId.eq(policy.getPolicyId())) > 0;
        if (policydto.getClientId() == null) throw new BadRequestException("Client is Mandatory");
        if (policydto.getBindCode() == null && !multiproduct) throw new BadRequestException("Binder is Mandatory ");
        if (policydto.getAgentId() == null && !multiproduct)
            throw new BadRequestException("Intermediary is Mandatory");
        if (policydto.getProdId() == null) throw new BadRequestException("Product is Mandatory");

//        if (policy.getPaymentId() == null) throw new BadRequestException("Payment Mode is Mandatory");

        if (policydto.getBranchId() == null) throw new BadRequestException("Branch is Mandatory");
        if (policydto.getCurrencyId() == null) throw new BadRequestException("Currency is Mandatory");
        if (policydto.getWefDate() == null) throw new BadRequestException("Policy Wef Date From is Mandatory");
        if (policydto.getWetDate() == null) throw new BadRequestException("Policy Wet Date From is Mandatory");

        if (policy.getCoinsuranceBusiness() != null && policy.getCoinsuranceBusiness().equalsIgnoreCase("on")) {
            policy.setCoinsuranceBusiness("Y");
        } else {
            policy.setCoinsuranceBusiness("N");
        }



        List<PolicyBinders> policyBindersList = new ArrayList<>();

        if (policy.getWefDate().after(policy.getWetDate()))
            throw new BadRequestException("Wef Date cannot be greater than Wet Date");
        Date polWetDate = dateUtils.getWetDate(policy.getWefDate());
        User user = userUtils.getCurrentUser();
        if (policydto.getBusinessType() == null) {
            if ("NB".equalsIgnoreCase(policydto.getTransType())) {
                policydto.setBusinessType("NB"); // Example default value
            } else if ("RN".equalsIgnoreCase(policydto.getTransType())) {
                policydto.setBusinessType("R");
            }
        }
        if ("N".equalsIgnoreCase(policy.getBusinessType())) {
            if (policy.getTransType() == null || "NB".equalsIgnoreCase(policy.getTransType())) {
                if (policy.getWetDate().after(polWetDate) || policy.getWetDate().before(polWetDate))
                    throw new BadRequestException("The Policy Cover Period Cannot be more than a year");
            } else if ("EN".equalsIgnoreCase(policy.getTransType()) || "CN".equalsIgnoreCase(policy.getTransType())) {
                PolicyTrans currentTrans = policyRepo.findOne(policy.getPolicyId());
                PolicyTrans prevTrans = currentTrans.getPreviousTrans();
                Date currentWet = dateUtils.removeTime(policy.getWetDate());
                Date prevWet = dateUtils.removeTime(prevTrans.getWetDate());
                if (currentWet.after(prevWet) || currentWet.before(prevWet))
                    throw new BadRequestException("Policy WET Date Cannot change for endorsements...");
            }

        }

        if ("S".equalsIgnoreCase(policy.getBusinessType()))
            if (policy.getWetDate().after(polWetDate))
                throw new BadRequestException("The Short Period Policy Cover Period Cannot be more than a year");

        boolean newTrans = false;
        Iterable<BinderReqrdDocs> reqdDocs = new ArrayList<>();
        if (policydto.getRiskBean() != null)
            if (policydto.getRiskBean().getSclCode() != null) {
//                String transType = "";
                if (policydto.getTransType() == null || StringUtils.isBlank(policydto.getTransType())) {
                    if(iSBulkUpload){
                        transType = "BU";
                    }else {
                        transType = "NB";
                    }
                } else
                    transType = policydto.getTransType();
                if ("CO".equalsIgnoreCase(policydto.getTransType())) {
                    transType = policy.getPreviousTrans().getTransType();
                }
                if ("NB".equalsIgnoreCase(transType)) {
                    transType = "NB";
                } else if ("RN".equalsIgnoreCase(transType)) {
                    transType = "RN";
                } else if ("BU".equalsIgnoreCase(transType)) {
                    transType = "BU";
                }else {
                    transType = "EN";
                }
                if ("NB".equalsIgnoreCase(transType)) {
                    reqdDocs = reqrdDocsRepo.findAll(QBinderReqrdDocs.binderReqrdDocs.binderDetail.detId.eq(policydto.getRiskBean().getBinderDet())
                            .and(QBinderReqrdDocs.binderReqrdDocs.mandatory.eq(true))
                            .and(QBinderReqrdDocs.binderReqrdDocs.requiredDocs.requiredDoc.appliesNewBusiness.eq(true)));
                } else if ("EN".equalsIgnoreCase(transType)) {
                    reqdDocs = reqrdDocsRepo.findAll(QBinderReqrdDocs.binderReqrdDocs.binderDetail.detId.eq(policydto.getRiskBean().getBinderDet())
                            .and(QBinderReqrdDocs.binderReqrdDocs.mandatory.eq(true))
                            .and(QBinderReqrdDocs.binderReqrdDocs.requiredDocs.requiredDoc.appliesEndorsement.eq(true)));
                } else if ("RN".equalsIgnoreCase(transType)) {
                    reqdDocs = reqrdDocsRepo.findAll(QBinderReqrdDocs.binderReqrdDocs.binderDetail.detId.eq(policydto.getRiskBean().getBinderDet())
                            .and(QBinderReqrdDocs.binderReqrdDocs.mandatory.eq(true))
                            .and(QBinderReqrdDocs.binderReqrdDocs.requiredDocs.requiredDoc.appliesRenewal.eq(true)));
                }

            }

        SystemTrans transaction = null;
        if (policydto.getPolicyId() == null) {

            newTrans = true;
            String policyNumberFormat = paramService.getParameterString("POLICY_NO_FORMAT");
            String endorsementFormat = paramService.getParameterString("ENDORSE_NO_FORMAT");
            Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("P");
            if (sequenceRepo.count(seqPredicate) == 0)
                throw new BadRequestException("Sequence for New Business Transactions has not been defined");
            SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
            Long seqNumber = sequence.getNextNumber();
            final String policyNumber = templateMerger.generateFormat(policyNumberFormat, policydto.getBranchId(), policydto.getProdId(), policy.getWefDate(), sequence.getSeqPrefix() + String.format("%05d", seqNumber), null);
            policy.setPolNo(policyNumber);

            sequence.setLastNumber(seqNumber);
            sequence.setNextNumber(seqNumber + 1);
            sequenceRepo.save(sequence);
            Predicate endorsePredicate = QSystemSequence.systemSequence.transType.eq("E");
            if (sequenceRepo.count(endorsePredicate) == 0)
                throw new BadRequestException("Sequence for Endorsement Transactions has not been defined");
            SystemSequence endorseSequence = sequenceRepo.findOne(endorsePredicate);
            Long endosseqNumber = endorseSequence.getNextNumber();
            final String revNumber = endorseSequence.getSeqPrefix() + String.format("%05d", endosseqNumber);
            final String endorseNumber = templateMerger.generateFormat(endorsementFormat, policydto.getBranchId(), policydto.getProdId(), policy.getWefDate(), revNumber, null);
            policy.setPolRevNo(endorseNumber + "/1");
            policy.setRevisionFormat(endorseNumber);
            endorseSequence.setLastNumber(endosseqNumber);
            endorseSequence.setNextNumber(endosseqNumber + 1);
            sequenceRepo.save(endorseSequence);
            transaction = new SystemTrans();
            transaction.setDoneDate(new Date());
            transaction.setDoneBy(userUtils.getCurrentUser());
            transaction.setPolicy(policy);
            transaction.setTransLevel("U");
            if(iSBulkUpload){
                transaction.setTransCode("BUD");
            }else {
                transaction.setTransCode("NBD"); //A way to setup and look up for transaction transcode
            }
            transaction.setTransAuthorised("N");

            if ("S".equalsIgnoreCase(policy.getBusinessType())) {
                policy.setTransType("SP");
                policy.setPolRevStatus("SP");
            } else {
                if(iSBulkUpload) {
                    policy.setTransType("BU");
                }else {
                    policy.setTransType("NB");
                    policy.setPolRevStatus("NB");
                }
            }
        }


        if ("A".equalsIgnoreCase(policydto.getInterfaceType())) {
            if (policydto.getAccrualInstDate() == null) {
                throw new BadRequestException("Accrual Installment Date is required.");
            }

            if (policydto.getAccrualPaymentType() == null || policydto.getAccrualPaymentType().trim().isEmpty()) {
                throw new BadRequestException("Accrual Premium Payment Type is required");
            }

            LocalDate today = LocalDate.now();
            LocalDate accrualDate = policydto.getAccrualInstDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            if (accrualDate.isBefore(today)) {
                throw new BadRequestException("Please Enter an Installment Date that is Today or Beyond");
            }
            policy.setAccrualInstDate(policydto.getAccrualInstDate());

        }

        policy.setCurrentStatus("D");
        policy.setAuthStatus("D");

        if (policydto.getPrevPolicy() == null) {
            policy.setPreviousTrans(policy);
        } else {
            policy.setPreviousTrans(policyRepo.findOne(policydto.getPrevPolicy()));
        }

        //if(policydto.getAdminFeePolicy()!=null && "on".equalsIgnoreCase(policydto.getAdminFeePolicy())){
        //    policy.setAdminFeeApplicable("Y");
        // }
        // else policy.setAdminFeeApplicable("N");
        if((bindersDef.getAdminFeeActiveStatus() != null) && (bindersDef.getAdminFeeActiveStatus().equalsIgnoreCase("Y"))) {
            policy.setAdminFeeApplicable("Y");
        } else {
            policy.setAdminFeeApplicable("N");
        }

        if (policy.getPolicyId() != null) {
            PolicyTrans editPolicy = policyRepo.findOne(policy.getPolicyId());
            if (editPolicy == null)
                throw new BadRequestException("The Policy does not exist. Cannot Authorize");
            if ("A".equalsIgnoreCase(editPolicy.getAuthStatus())) {
                throw new BadRequestException("Cannot Save..Policy already authorized");
            }


            if (policy.getPolRevStatus() == null || StringUtils.isBlank(policy.getPolRevStatus())) {
                policy.setPolRevStatus(editPolicy.getPolRevStatus());
            }
            if (policy.getPolRevStatus() != null && "EN".equalsIgnoreCase(policy.getPolRevStatus())) {
                policy.setInstallmentNo(editPolicy.getInstallmentNo());
                policy.setTotalInstalments(editPolicy.getTotalInstalments());
            }

            if (policydto.getBindCode() != null && !multiproduct) {
                BindersDef binder = editPolicy.getBinder();
                BindersDef newBinder = binderRepo.findOne(policydto.getBindCode());
                if (binder.getBinId() != newBinder.getBinId()) {
                    Iterable<RiskTrans> binderRisks = riskRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(policy.getPolicyId()));
                    List<RiskTrans> newRisks = new ArrayList<>();
                    for (RiskTrans risk : binderRisks) {
                        risk.setBinder(newBinder);
                        newRisks.add(risk);
                        Iterable<SectionTrans> sections = sectionRepo.findAll(QSectionTrans.sectionTrans.risk.riskId.eq(risk.getRiskId()));
                        sectionRepo.delete(sections);
                    }
                    riskRepo.save(newRisks);
                }
            } else if (policydto.getBindCodes() != null) {
                //to do
            }


        }
//        if(!isApproved && !makerCheckerRepo.exists(hashCode)) {
//            System.out.println(new Gson().toJson(policydto));
//            MakerCheckDTO makerCheckDTO = new MakerCheckDTO();
//            makerCheckDTO.setJson(new Gson().toJson(policydto));
//            makerCheckDTO.setStatus("N");
//            ClientDef clientDef = clientRepo.findOne(policydto.getClientId());
//            makerCheckDTO.setTaskName("Created Policy:Client..." + clientDef.getFname() + " " + clientDef.getOtherNames() + " Policy No " + policy.getPolNo());
//            makerCheckDTO.setTaskType("GP");
//            makerCheckDTO.setTaskCode(hashCode);
//            makerCheckerService.checkExists(makerCheckDTO);
//            makerCheckerService.createMakerChecker(makerCheckDTO);
//            return policy;
//        }

        boolean medicalProduct = false;
        boolean lifeProduct = false;
        boolean renewable = false;
        ProductsDef policyProduct = productRepo.findOne(policydto.getProdId());
        boolean cashBasis = policy.getInterfaceType() != null && "C".equalsIgnoreCase(policy.getInterfaceType());
        renewable = policyProduct.isRenewable();
        final boolean motorProduct = policyProduct.isMotorProduct();
        if (policyProduct.getProGroup().getPrgType() == null || !policyProduct.getProGroup().getPrgType().equalsIgnoreCase("MD")) {
            medicalProduct = false;
            lifeProduct = false;
        } else if (policyProduct.getProGroup().getPrgType().equalsIgnoreCase("MD")) {
            medicalProduct = true;
            lifeProduct = false;
            if (policy.getMedicalCoverType() == null || policy.getMedicalCoverType().equalsIgnoreCase(""))
                throw new BadRequestException("Type is Mandatory");

        } else if (policyProduct.getProGroup().getPrgType().equalsIgnoreCase("L")) {
            medicalProduct = false;
            lifeProduct = true;
        }
        policy.setProduct(policyProduct);

        if (medicalProduct) {
            if (policy.getMedicalCoverType() == null || StringUtils.isBlank(policy.getMedicalCoverType())) {
                throw new BadRequestException("Select Medical Cover Type....");
            }
            Predicate cardPred = QBinderMedicalCards.binderMedicalCards.binder.binId.eq(policydto.getBindCode());
            Long cardCount = binderMedicalCardsRepo.count(cardPred);
            if (cardCount != 0 && policydto.getCardId() == null) {
                throw new BadRequestException("Card type is Mandatory");
            } else {
                if (cardCount == 0) policy.setBinCardType(null);
            }
            if (!(policydto.getCardId() == null)) {
                policy.setBinCardType(binderMedicalCardsRepo.findOne(policydto.getCardId()));
            }
        }

        RiskTransBean riskBean = policydto.getRiskBean();
        if (policy.getPolicyId() == null && !medicalProduct && !policydto.isImportRisks() && !multiproduct) {
            // Convert policy dates to LocalDate for adjustment
//            LocalDate policyWef = policydto.getWefDate().toInstant()
//                    .atZone(ZoneId.systemDefault())
//                    .toLocalDate();
//            LocalDate policyWet = policydto.getWetDate().toInstant()
//                    .atZone(ZoneId.systemDefault())
//                    .toLocalDate();
//            adjustLifeRiskDates(policydto.getRiskBean(), policyWef, policyWet);
            Date polWef = dateUtils.removeTime(policy.getWefDate());
            Date polWet = dateUtils.removeTime(policy.getWetDate());
            Date riskWef = dateUtils.removeTime(riskBean.getWefDate());
            Date riskWet = dateUtils.removeTime(riskBean.getWetDate());
            if (riskWef.before(polWef) || riskWef.after(polWet)
                    || riskWet.before(polWef) || riskWet.after(polWet)) {
                throw new BadRequestException("Risk Cover Dates outside Policy Cover Periods ");
            }
            RiskTrans risk = new RiskTrans();
            if (riskBean.getBindCode() == null) throw new BadRequestException("Binder is Mandatory");
            if (riskBean.getSclCode() == null) throw new BadRequestException("Sub Class is Mandatory");
            if (riskBean.getCoverCode() == null) throw new BadRequestException("Cover Type is Mandatory");
            if (riskBean.getInsuredCode() == null) throw new BadRequestException("Insured is Mandatory");
            if (riskRepo.count(QRiskTrans.riskTrans.riskShtDesc.eq(riskBean.getRiskShtDesc())
                    .and(QRiskTrans.riskTrans.subclass.riskUnique.isTrue())
                    .and(QRiskTrans.riskTrans.policy.currentStatus.eq("A"))
                    .and(QRiskTrans.riskTrans.wefDate.between(riskBean.getWefDate(), riskBean.getWetDate()).or(QRiskTrans.riskTrans.wetDate.between(riskBean.getWefDate(), riskBean.getWetDate())))
                    .and(QRiskTrans.riskTrans.policy.transType.in("NB", "RN"))) > 0) {
                throw new BadRequestException("Risk Already Exists in the system...." +
                        riskRepo.count(QRiskTrans.riskTrans.riskShtDesc.eq(riskBean.getRiskShtDesc())
                                .and(QRiskTrans.riskTrans.subclass.riskUnique.isTrue())
                                .and(QRiskTrans.riskTrans.policy.currentStatus.eq("A"))
                                .and(QRiskTrans.riskTrans.wefDate.between(riskBean.getWefDate(), riskBean.getWetDate())).or(QRiskTrans.riskTrans.wetDate.between(riskBean.getWefDate(), riskBean.getWetDate()))
                                .and(QRiskTrans.riskTrans.policy.transType.in("NB", "RN")))
                );
            }

            SubClassDef subClassDef = subclassRepo.findOne(riskBean.getSclCode());
            risk.setBinder(binderRepo.findOne(riskBean.getBindCode()));
            risk.setCovertype(coverRepo.findOne(riskBean.getCoverCode()));
            risk.setSubclass(subClassDef);
            risk.setInsured(clientRepo.findOne(riskBean.getInsuredCode()));
            risk.setBinderDetails(binderDetRepo.findOne(riskBean.getBinderDet()));
            risk.setCommRate(riskBean.getCommRate());
            Boolean autogen = Boolean.parseBoolean(riskBean.getAutogenCert());
            if (autogen) {
                risk.setAutogenCert("Y");
            } else risk.setAutogenCert("N");
            risk.setPolicy(policy);
            risk.setProrata(riskBean.getProrata());
            risk.setRiskDesc(riskBean.getRiskDesc());
            risk.setRiskShtDesc(riskBean.getRiskShtDesc());
            risk.setWefDate(riskBean.getWefDate());
            risk.setWetDate(riskBean.getWetDate());
            risk.setButchargePrem(riskBean.getButchargePrem());
            risk.setInstallAmount(riskBean.getInstallAmount());
            policy.setCashInstallmentPremium(riskBean.getInstallAmount());
            risk.setInstallmentNo(riskBean.getInstallmentNo());
            risk.setInstallmentPerc(riskBean.getInstallmentPerc());
            risk.setSubAgentCommRate(riskBean.getSubAgentCommRate());
            risk.setMarketerCommRate(riskBean.getMarketerCommRate());
            if(iSBulkUpload) {
                risk.setTransType("BU");
            }else{
                risk.setTransType("NB");
            }
            if (binderDetRepo.findOne(riskBean.getBinderDet()).getSingleSectionCover() != null && binderDetRepo.findOne(riskBean.getBinderDet()).getSingleSectionCover().equalsIgnoreCase("Y")) {
                if (policydto.getSections().size() > 1) {
                    throw new BadRequestException("Only one Option is allowed for this contract");
                }
            }
            List<SectionTrans> sectionTransactions = policydto.getSections().stream().map(sectionbean -> {
                SectionsDef sectiondef = setupSectionRepo.findOne(sectionbean.getSection());
                SectionTrans section = new SectionTrans();
                section.setAmount((sectionbean.getAmount() == null) ? BigDecimal.ZERO : sectionbean.getAmount());
                section.setCompute(sectionbean.isCompute());
                section.setDivFactor(sectionbean.getDivFactor());
                section.setFreeLimit(sectionbean.getFreeLimit());
                List<PremRatesDef> premRates = new ArrayList<>();
                if (sectionbean.getRatesApplicable() != null && "Y".equalsIgnoreCase(sectionbean.getRatesApplicable())) {
                    premRates = premRatesRepo.getSectPremiumRates(riskBean.getBinderDet(), sectionbean.getAmount(), sectiondef.getId());
                    if (premRates.size() == 1) {
                        section.setPremRates(premRates.get(0));
                        section.setRate(premRates.get(0).getRate());
                    }
                } else {
                    premRates = premRatesRepo.getSectPremiumRates(riskBean.getBinderDet(), sectiondef.getId());
                    if (premRates.size() == 1) {
                        section.setPremRates(premRates.get(0));
                        section.setRate(sectionbean.getRate());
                    }
                }
                if (!premRates.isEmpty() && premRates.get(0).getRangeApplicable().equals("Y")) {
                    BigDecimal rangeFrom = premRates.get(0).getRangeFrom();
                    BigDecimal rangeTo = premRates.get(0).getRangeTo();
                    if (premRates.get(0).getRangeType().equalsIgnoreCase("AM")) {
                        if (sectionbean.getAmount().compareTo(rangeFrom) < 0 || sectionbean.getAmount().compareTo(rangeTo) > 0) {
                            try {
                                throw new BadRequestException(String.format("%s is not within the specified range of %s-%s", sectionbean.getAmount(), rangeFrom, rangeTo));
                            } catch (BadRequestException e) {
                                throw new RuntimeException(e.getMessage());
                            }

                        }
                    }
                }
                section.setCompute(true);
                section.setSection(sectiondef);
                section.setRisk(risk);
                return section;
            }).collect(Collectors.toList());
            sectionRepo.save(sectionTransactions);

            RiskTrans savedRisk = riskRepo.save(risk);
            long riskIdentifier = Long.valueOf(String.valueOf(dateUtils.getUwYear(policy.getWefDate())) + String.valueOf(savedRisk.getRiskId()));
            PolicyActiveRisks activeRisk = new PolicyActiveRisks();
            activeRisk.setPolicy(policy);
            activeRisk.setRisk(risk);
            activeRisk.setRiskIdentifier(riskIdentifier);
            activeRisksRepo.save(activeRisk);
            savedRisk.setRiskIdentifier(riskIdentifier);
            riskRepo.save(savedRisk);


        }
        if (policy.getPolicyId() != null) {
            policy.setRevisionFormat(policy.getRevisionFormat());
        }
        policy.setCommAllowed(true);
        if (policydto.getSubAgentId() != null) {
            policy.setSubAgent(accountRepo.findOne(policydto.getSubAgentId()));
        }
        if (policydto.getMarketerAgentId() != null) {
            policy.setMarketerAgent(accountRepo.findOne(policydto.getMarketerAgentId()));
        }
        if (policydto.getIntroducerAgentId() != null) {
            policy.setIntroducerAgent(accountRepo.findOne(policydto.getIntroducerAgentId()));
        }
        if (policydto.getLeadsManId() != null) {
            policy.setLeadsMan(userRepo.findOne(policydto.getLeadsManId()));
        }
        if (!multiproduct)
            policy.setAgent(accountRepo.findOne(policydto.getAgentId()));
        if (policydto.getBindCode() != null) {
            policy.setBinder(binderRepo.findOne(policydto.getBindCode()));

        } else if (policydto.getBindCodes() != null) {
            for (Long bindCode : policydto.getBindCodes()) {
                PolicyBinders policyBinders = new PolicyBinders();
                policyBinders.setBinder(binderRepo.findOne(bindCode));
                policyBinders.setPolicyTrans(policy);
                policyBindersList.add(policyBinders);
            }
            renewable = true;
        }
        policy.setAbsaNoIntroducer(policydto.getAbsaNoIntroducer());
        policy.setAbsaNoMarketer(policydto.getAbsaNoMarketer());
        policy.setAbsaNoLeadsMan(policydto.getAbsaNoLeadsMan());
        policy.setAbsaNoSubAgent(policydto.getAbsaNoSubAgent());
        policy.setBranch(branchRepo.findOne(policydto.getBranchId()));
        policy.setClient(clientRepo.findOne(policydto.getClientId()));
        //   policy.setPaymentMode(paymentModeRepo.findOne(policy.getPaymentId()));


        policy.setCreatedUser(user);
        policy.setTransCurrency(currencyRepo.findOne(policydto.getCurrencyId()));
        policy.setPolCreateddt(new Date());
        if ("N".equalsIgnoreCase(policy.getBusinessType()))
            policy.setRenewable(renewable);
        else
            policy.setRenewable(false);
        policy.setUwYear(dateUtils.getUwYear(policy.getWefDate()));
        if (renewable && "N".equalsIgnoreCase(policy.getBusinessType())) {
            policy.setRenewalDate(DateUtils.addDays(policy.getWetDate(), 1));
            policy.setNotificationSent(false);
        } else
            policy.setRenewalDate(null);
        if (policy.getTransType().equalsIgnoreCase("NB") || policy.getTransType().equalsIgnoreCase("RN") || policy.getTransType().equalsIgnoreCase("SP") || policy.getTransType().equalsIgnoreCase("BU")) {
            policy.setCoverFrom(policy.getWefDate());
            policy.setCoverTo(policy.getWetDate());
        } else {
            PolicyTrans currentTrans = policyRepo.findOne(policy.getPolicyId());
            policy.setCoverFrom(currentTrans.getCoverFrom());
            policy.setCoverTo(currentTrans.getCoverTo());
        }


        if (newTrans)
            transRepo.save(transaction);

        PolicyTrans savedTrans = policyRepo.save(policy);

        this.populateClauses(savedTrans);
        this.populateTaxes(savedTrans);
        if (policyBindersList.size() > 0) {
            policyBindersRepo.save(policyBindersList);
        }

        if (newTrans) {
            workflowService.startNewWorkFlow(DocType.GEN_UW_DOCUMENT, String.valueOf(savedTrans.getPolicyId()), policy, (medicalProduct) ? "Y" : "N", null, null, null, null);
        }
        List<Object[]> risks = riskRepo.findPolicyRiskTrans(savedTrans.getPolicyId());
        if (!medicalProduct && !lifeProduct) {
            for (Object[] risk : risks) {
                final Long riskId = ((BigInteger) risk[1]).longValue();
                final Date riskWefDate = (Date) risk[7];
                final Date riskWetDate = (Date) risk[8];
                final Long riskCode = ((BigInteger) risk[2]).longValue();
                final Long subclassCode = ((BigInteger) risk[17]).longValue();
                List<RiskDocsDTO> riskDocs = Streamable.streamOf(reqdDocs)
                        .filter(reqdDoc -> riskDocsRepo.count(QRiskDocs.riskDocs.risk.riskIdentifier.eq(riskCode).and(QRiskDocs.riskDocs.reqdDocs.sclReqrdId.eq(reqdDoc.getRequiredDocs().getSclReqrdId()))) == 0)
                        .map(reqdDoc -> {
                            RiskDocsDTO riskDoc = new RiskDocsDTO();
                            riskDoc.setDocId(reqdDoc.getRequiredDocs().getSclReqrdId());
                            riskDoc.setRiskId(riskId);
                            return riskDoc;
                        }).collect(Collectors.toList());

                final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource);
                jdbcTemplate.batchUpdate("insert  into sys_brk_rsk_docs(rd_id,rd_risk_id,rd_req_id)" +
                                "values(NEXTVAL('risk_docs_seq'),?,?)",
                        new BatchPreparedStatementSetter() {

                            @Override
                            public void setValues(PreparedStatement ps, int i) throws SQLException {
                                ps.setLong(1, riskDocs.get(i).getRiskId());
                                ps.setLong(2, riskDocs.get(i).getDocId());
                            }

                            @Override
                            public int getBatchSize() {
                                return riskDocs.size();
                            }
                        });
                if (motorProduct) {
                    final List<BigInteger> codes = subclassCertTypesRepo.getCertificateCode(subclassCode);
                    if (codes.size() == 1) {
                        final RiskCertForm riskCertForm = new RiskCertForm();
                        riskCertForm.setSubclasscertId(((BigInteger) codes.get(0)).longValue());
                        riskCertForm.setWefDate(riskWefDate);
                        riskCertForm.setWetDate(riskWetDate);
                        riskCertForm.setRiskId(riskId);
                        try {
                            certService.createRiskCert(riskCertForm);
                        } catch (Exception ex) {
                            System.out.println("Error creating cert....");
                        }
                    }
                }
            }
        }
        return savedTrans;

    }
    private void adjustPolicyDates(PolicyCreateDTO policydto, String transType) throws BadRequestException {
        ZoneId zone = ZoneId.systemDefault();

        // Convert to LocalDate
        LocalDate originalWef = policydto.getWefDate().toInstant().atZone(zone).toLocalDate();
        LocalDate originalWet = policydto.getWetDate().toInstant().atZone(zone).toLocalDate();

        // Adjust WEF for holidays
        LocalDate adjustedWef = HolidayUtils.getAdjustedTransactionDate(originalWef);

        // Calculate original duration (using original WEF before holiday adjustment)
        long originalDuration = ChronoUnit.DAYS.between(originalWef, originalWet);

        // Calculate new WET by maintaining original duration
        LocalDate adjustedWet = adjustedWef.plusDays(originalDuration);

        // For new business/renewals, update both dates
        if (!"EN".equalsIgnoreCase(transType) && !"CN".equalsIgnoreCase(transType)) {
            policydto.setWefDate(Date.from(adjustedWef.atStartOfDay(zone).toInstant()));
            policydto.setWetDate(Date.from(adjustedWet.atStartOfDay(zone).toInstant()));
        } else {
            // For endorsements, only adjust WEF (keep original WET)
            policydto.setWefDate(Date.from(adjustedWef.atStartOfDay(zone).toInstant()));
        }

        // Final validation
        if (policydto.getWefDate().after(policydto.getWetDate())) {
            throw new BadRequestException("WEF date cannot be after WET date");
        }
    }

    private void adjustLifeRiskDates(RiskTransBean riskBean, LocalDate policyWef, LocalDate policyWet)
            throws BadRequestException {

        if (riskBean == null) return;

        ZoneId zone = ZoneId.systemDefault();

        // Convert dates
        LocalDate originalRiskWef = riskBean.getWefDate().toInstant().atZone(zone).toLocalDate();
        LocalDate originalRiskWet = riskBean.getWetDate().toInstant().atZone(zone).toLocalDate();

        // Adjust WEF for holidays
        LocalDate adjustedRiskWef = HolidayUtils.getAdjustedTransactionDate(originalRiskWef);

        // Maintain original duration
        long originalDuration = ChronoUnit.DAYS.between(originalRiskWef, originalRiskWet);
        LocalDate adjustedRiskWet = adjustedRiskWef.plusDays(originalDuration);

        // Ensure within policy period (strict for new business)
        if (adjustedRiskWef.isBefore(policyWef) || adjustedRiskWet.isAfter(policyWet)) {
            throw new BadRequestException("Risk period must be within policy period");
        }

        // Update risk bean
        riskBean.setWefDate(Date.from(adjustedRiskWef.atStartOfDay(zone).toInstant()));
        riskBean.setWetDate(Date.from(adjustedRiskWet.atStartOfDay(zone).toInstant()));
    }




    @Override
    public DataTablesResult<RiskTransDTO> findActiveRiskTransactions(DataTablesRequest request, Long polCode, Long insuredId) throws IllegalAccessException {
        final String search = (request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue().toLowerCase() + "%" : "%%";
        if (insuredId == null) {
            insuredId = -2000L;
        }
        List<Object[]> risks = riskRepo.findPolicyActiveRisks(search.toLowerCase(), polCode, insuredId, request.getPageNumber(), request.getPageSize());
        final List<RiskTransDTO> riskList = new ArrayList<>();
        long rowCount = 0l;
        if (!risks.isEmpty()) rowCount = ((BigInteger) risks.get(0)[12]).intValue();
        for (Object[] risk : risks) {
            final RiskTransDTO riskTransDTO = new RiskTransDTO();
            riskTransDTO.setRiskId(((BigInteger) risk[0]).longValue());
            riskTransDTO.setRiskShtDesc((String) risk[1]);
            riskTransDTO.setRiskDesc((String) risk[2]);
            riskTransDTO.setWefDate((Date) risk[3]);
            riskTransDTO.setWetDate((Date) risk[4]);
            riskTransDTO.setSubId(((BigInteger) risk[5]).longValue());
            riskTransDTO.setSubDesc((String) risk[6]);
            riskTransDTO.setCovId(((BigInteger) risk[7]).longValue());
            riskTransDTO.setCovName((String) risk[8]);
            riskTransDTO.setSumInsured((BigDecimal) risk[9]);
            riskTransDTO.setPremium((BigDecimal) risk[10]);
            riskTransDTO.setAuthStatus((String) risk[11]);
            riskList.add(riskTransDTO);
        }
        Page<RiskTransDTO> page = new PageImpl<>(riskList, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<RiskTransDTO> findRiskTransactions(DataTablesRequest request, Long polCode, Long bindCode) {
        final String search = (request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue().toLowerCase() + "%" : "%%";
        if (bindCode == null) {
            bindCode = -2000L;
        }
        List<Object[]> risks = riskRepo.findLifePolicyRisks(search.toLowerCase(), polCode, bindCode, request.getPageNumber(), request.getPageSize());
        final List<RiskTransDTO> riskList = new ArrayList<>();
        long rowCount = 0l;
//        if(!risks.isEmpty()) rowCount = ((BigInteger)risks.get(0)[31]).intValue();
        for (Object[] risk : risks) {
            final RiskTransDTO riskTransDTO = new RiskTransDTO();
            riskTransDTO.setRiskId(((BigInteger) risk[0]).longValue());
            riskTransDTO.setRiskShtDesc((String) risk[1]);
            riskTransDTO.setRiskDesc((String) risk[2]);
            riskTransDTO.setWefDate((Date) risk[3]);
            riskTransDTO.setWetDate((Date) risk[4]);
            riskTransDTO.setSubId(((BigInteger) risk[5]).longValue());
            riskTransDTO.setSubDesc((String) risk[6]);
            riskTransDTO.setCovId(((BigInteger) risk[7]).longValue());
            riskTransDTO.setCovName((String) risk[8]);
            riskTransDTO.setSumInsured((BigDecimal) risk[9]);
            riskTransDTO.setPremium((BigDecimal) risk[10]);
            riskTransDTO.setAuthStatus((String) risk[11]);
            riskTransDTO.setInsuredId(((BigInteger) risk[12]).longValue());
            riskTransDTO.setBinderDetId(((BigInteger) risk[13]).longValue());
            riskTransDTO.setFname((String) risk[14]);
            riskTransDTO.setOthernames((String) risk[15]);
            riskTransDTO.setTenId(((BigInteger) risk[16]).longValue());
            riskTransDTO.setTransType((String) risk[17]);
            riskTransDTO.setCommRate((BigDecimal) risk[18]);
            riskTransDTO.setButchargePrem((BigDecimal) risk[19]);
            riskTransDTO.setInstallAmount((BigDecimal) risk[20]);
            if (risk[21] != null) {
                riskTransDTO.setInstallmentNo(((BigInteger) risk[21]).longValue());
            }
            riskTransDTO.setInstallmentPerc((String) risk[22]);
            riskTransDTO.setRiskIdentifier(((BigInteger) risk[23]).longValue());
            riskTransDTO.setAutogenCert((String) risk[24]);
            riskTransDTO.setProrata((String) risk[25]);
            if (risk[26] != null) {
                riskTransDTO.setPolBindId(((BigInteger) risk[26]).longValue());
            }
            riskTransDTO.setStartDate((String) risk[27]);
            riskTransDTO.setEndDate((String) risk[28]);
            final Date date = (Date) risk[29];
            riskTransDTO.setAge(dateUtils.getAge(date));
            riskTransDTO.setComputeType((String) risk[30]);
            riskTransDTO.setIdNo((String) risk[31]);
            riskList.add(riskTransDTO);
        }
        Page<RiskTransDTO> page = new PageImpl<>(riskList, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<PolicyBinders> findPolicyBinders(DataTablesRequest request, Long polCode) throws IllegalAccessException {
        BooleanExpression pred = QPolicyBinders.policyBinders.policyTrans.policyId.eq(polCode);
        Page<PolicyBinders> page = policyBindersRepo.findAll(pred.and(request.searchPredicate(QPolicyBinders.policyBinders)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<SectionTrans> findRiskSections(DataTablesRequest request, Long riskId) {

        List<Object[]> pages = sectionRepo.findRiskSectionTrans(riskId);
        List<SectionTrans> sectionTrans = new ArrayList<>();
        for (Object[] sectran : pages) {
            SectionTrans sec = new SectionTrans();
            if (sectran[0] instanceof BigInteger) {
                sec.setSectId(((BigInteger) sectran[0]).longValue());
            } else if (sectran[0] instanceof BigDecimal) {
                sec.setSectId(((BigInteger) sectran[0]).longValue());
            }

            sec.setAmount((BigDecimal) sectran[1]);
            sec.setCalcprem((BigDecimal) sectran[2]);
            sec.setDivFactor((BigDecimal) sectran[3]);
            sec.setFreeLimit((BigDecimal) sectran[4]);
            sec.setMultiRate((BigDecimal) sectran[5]);
            sec.setPrem((BigDecimal) sectran[6]);
            sec.setRate((BigDecimal) sectran[7]);
            if (sectran[8] instanceof BigInteger)
                sec.setSection(setupSectionRepo.findOne(((BigInteger) sectran[8]).longValue()));
            else if (sectran[8] instanceof BigDecimal)
                sec.setSection(setupSectionRepo.findOne(((BigDecimal) sectran[8]).longValue()));
            if (sectran[9] instanceof BigInteger)
                sec.setPremRates(premRatesRepo.findOne(((BigInteger) sectran[9]).longValue()));
            else if (sectran[9] instanceof BigDecimal)
                sec.setPremRates(premRatesRepo.findOne(((BigDecimal) sectran[9]).longValue()));
            if (sectran[10] instanceof BigInteger)
                sec.setRiskId(((BigInteger) sectran[10]).longValue());
            else if (sectran[10] instanceof BigDecimal)
                sec.setRiskId(((BigDecimal) sectran[10]).longValue());
            sec.setDesc((String) sectran[11]);
            sectionTrans.add(sec);

        }
        Page<SectionTrans> page = new PageImpl<SectionTrans>(sectionTrans);
        return new DataTablesResult<>(request, page);
    }


    @Override
    public DataTablesResult<InterestedPartiesDTO> findRiskInterestedParties(DataTablesRequest request, Long riskId) throws IllegalAccessException {
        final String search = (request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue().toLowerCase() + "%" : "%%";
        if (riskId == null) {
            riskId = -2000L;
        }
        List<Object[]> interestedPartiesList = riskIntPartiesRepo.findRiskAllInterestedParties(search.toLowerCase(), riskId, request.getPageNumber(), request.getPageSize());
        final List<InterestedPartiesDTO> interestedList = new ArrayList<>();
        long rowCount = 0l;
        if (!interestedPartiesList.isEmpty()) rowCount = ((BigInteger) interestedPartiesList.get(0)[7]).intValue();
        for (Object[] parties : interestedPartiesList) {
            final InterestedPartiesDTO interestedPartiesDTO = new InterestedPartiesDTO();
            interestedPartiesDTO.setIpId(((BigInteger) parties[0]).longValue());
            interestedPartiesDTO.setIpName((String) parties[1]);
            interestedPartiesDTO.setIpType((String) parties[2]);
            interestedPartiesDTO.setIpPin((String) parties[3]);
            interestedPartiesDTO.setIpRegNo((String) parties[4]);
            interestedPartiesDTO.setPolStatus((String) parties[5]);
            interestedPartiesDTO.setIpEmailAddress((String) parties[6]);
            interestedList.add(interestedPartiesDTO);
        }
        Page<InterestedPartiesDTO> page = new PageImpl<>(interestedList, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public PolicyTrans getPolicyDetails(Long polCode) throws BadRequestException {
        System.out.println("Policy Code " + polCode);
        BooleanExpression pred = QPolicyTrans.policyTrans.policyId.eq(polCode);
        PolicyTrans policy = policyRepo.findOne(pred);
        if (policy.getPreviousTrans() != null) {
            policy.setPrevPolicy(policy.getPreviousTrans().getPolicyId());
        }
        if (policy.getAccrualPaymentType() == null && policy.getPreviousTrans() != null) {
            PolicyTrans originalPolicy = policy.getPreviousTrans();
            policy.setAccrualPaymentType(originalPolicy.getAccrualPaymentType());
        }

        Iterable<PolicyBinders> policyBinders = policyBindersRepo.findAll(QPolicyBinders.policyBinders.policyTrans.policyId.eq(polCode));
        String binderName = "";
        for (PolicyBinders binders : policyBinders) {
            binderName = binderName + binders.getBinder().getBinName() + ",";
        }
        if (binderName.length() > 1) {

        }

        if (policyQuestionnaireRepo.count(QPolicyQuestionnaire.policyQuestionnaire.policy.policyId.eq(polCode)) > 0) {
            policy.setQuizTaken("Y");
        } else {
            policy.setQuizTaken("N");
        }
        BigDecimal paidPremium = policyRepo.paidPremium(polCode);
        if (policy.getBasicPrem() != null && policy.getStampDuty() != null) {
            policy.setFuturePrem(policy.getBasicPrem().subtract(policy.getStampDuty()));
        }
        if (paidPremium != null) {
            policy.setPaidPremium(paidPremium);
            policy.setOutstandingPremium(policy.getBasicPrem().subtract(policy.getPaidPremium()));
            policy.setPaidPremPct(policy.getPaidPremium().divide(policy.getBasicPrem(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP));
        }
        return policy;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SectionBean> findPremSections(String paramString, Pageable paramPageable, Long detId) {
        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred = QPremRatesDef.premRatesDef.binderDet.detId.eq(detId)
                    .and(QPremRatesDef.premRatesDef.isNotNull());
        } else {
            pred = QPremRatesDef.premRatesDef.binderDet.detId.eq(detId)
                    .and(QPremRatesDef.premRatesDef.section.desc.containsIgnoreCase(paramString)
                            .or(QPremRatesDef.premRatesDef.section.shtDesc.containsIgnoreCase(paramString)));
        }
        List<PremRatesDef> premrates = premRatesRepo.findAll(pred, paramPageable).getContent();
        List<SectionBean> sections = premrates.stream().map(det -> {
            SectionBean section = new SectionBean();
            section.setDesc(det.getSection().getDesc());
            section.setDivFactor(det.getDivFactor());
            section.setFreeLimit(det.getFreeLimit());
            section.setId(det.getSection().getId());
            section.setPremId(det.getId());
            section.setRate(det.getRate());
            section.setRateType(det.getRateType());
            section.setSectType(det.getSection().getType().getCode());
            section.setShtDesc(det.getSection().getShtDesc());
            return section;
        }).collect(Collectors.toList());

        Page<SectionBean> retSections = new PageImpl<>(sections);
        return retSections;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void createRiskSection(SectionTransDTO sectionTransDTO) throws BadRequestException {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource);
        System.out.println("Passed here");
        final Long binderDetId = jdbcTemplate.queryForObject("select risk_binder_det_id  from sys_brk_risks where risk_id=?", new Object[]{sectionTransDTO.getRiskId()}, Long.class);
        if (binderDetId == null) {
            throw new BadRequestException("Select A Valid Binder details...");
        }
        final BinderDetails binderDetails = binderDetRepo.findOne(binderDetId);
        final PremRatesDef premRatesDef = premRatesRepo.findOne(sectionTransDTO.getPremRatesId());
        if (binderDetails.getBinder().getProduct().getAgeApplicable() != null && "Y".equalsIgnoreCase(binderDetails.getBinder().getProduct().getAgeApplicable())) {

        } else {
            if (!StringUtils.isBlank(premRatesDef.getRangeApplicable()) && "Y".equalsIgnoreCase(premRatesDef.getRangeApplicable())) {

                Iterable<PremRatesDef> premRatesDefs = premRatesRepo.findAll(QPremRatesDef.premRatesDef.rangeFrom.loe(sectionTransDTO.getAmount()).and(QPremRatesDef.premRatesDef.rangeTo.goe(sectionTransDTO.getAmount()))
                        .and(QPremRatesDef.premRatesDef.binderDet.detId.eq(binderDetails.getDetId())));
                if (premRatesDefs.spliterator().getExactSizeIfKnown() > 1) {
                    throw new BadRequestException("More than one premium Rates setup for the binder...");
                }
                if (premRatesDefs.spliterator().getExactSizeIfKnown() == 0) {
                    throw new BadRequestException("No premium Rates setup for the binder...");
                }


            }
        }
        System.out.println("Amount>>>>>>>>>: " + sectionTransDTO.getAmount());
        System.out.println("Div Factor>>>>>>>>>: " + sectionTransDTO.getDivFactor());
        System.out.println("Free Limit>>>>>>>>>: " + sectionTransDTO.getFreeLimit());
        System.out.println("Prem Rate Id>>>>>>>>>: " + sectionTransDTO.getPremRatesId());
        System.out.println("Section sect Id>>>>>>>>>: " + sectionTransDTO.getSectionSectId());
        System.out.println("Risk Id>>>>>>>>>: " + sectionTransDTO.getRiskId());
        System.out.println("Section Id>>>>>>>>>: " + sectionTransDTO.getSectId());
        if (sectionTransDTO.getSectId() == null) {
            jdbcTemplate.update("insert into sys_brk_rsk_limits(sect_id,sect_amount,sect_div_fact,sect_free_limit,sect_rate,sect_prem_id,sect_sec_id,sect_risk_id)" +
                    "values(NEXTVAL('risk_limits_seq'),?,?,?,?,?,?,?)", new Object[]{sectionTransDTO.getAmount(), sectionTransDTO.getDivFactor(), sectionTransDTO.getFreeLimit(),
                    sectionTransDTO.getRate(), sectionTransDTO.getPremRatesId(), sectionTransDTO.getSectionSectId(), sectionTransDTO.getRiskId()});
        } else {
            jdbcTemplate.update("update sys_brk_rsk_limits set sect_amount =?,sect_div_fact = ?,sect_free_limit = ?," +
                            "sect_rate = ?,sect_prem_id = ?,sect_sec_id = ? ,sect_risk_id = ? where sect_id = ?",
                    new Object[]{sectionTransDTO.getAmount(), sectionTransDTO.getDivFactor(), sectionTransDTO.getFreeLimit(),
                            sectionTransDTO.getRate(), sectionTransDTO.getPremRatesId(), sectionTransDTO.getSectionSectId(), sectionTransDTO.getRiskId(),
                            sectionTransDTO.getSectId()});

        }
        if (binderDetails.getLimitsAllowed() != null && binderDetails.getLimitsAllowed().equalsIgnoreCase("Y")) {
            if (sectionRepo.findAll(QSectionTrans.sectionTrans.risk.riskId.eq(sectionTransDTO.getRiskId())).spliterator().getExactSizeIfKnown() > 1) {
                throw new BadRequestException("Only one Option is allowed for this contract");
            }
        }
    }


    @PreAuthorize("hasAnyAuthority('SAVE_POLICY')")
    @Modifying
    @Transactional(readOnly = false)
    public void deleteRiskSection(Long sectid, HttpServletRequest request) throws BadRequestException {
        SectionTrans sectionTrans = this.sectionRepo.findSectionTransInfo(sectid);
        if (sectionTrans.getPremRates() != null) {
            PremRatesDef premRatesDef = sectionTrans.getPremRates();
            if (premRatesDef.getMandatory() != null && "Y".equalsIgnoreCase(premRatesDef.getMandatory())) {
                throw new BadRequestException("Cannot delete A Mandatory Premium Item...");
            }
        }
        sectionTrans.setRisk(null);
        this.sectionRepo.delete(sectid);
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        PolicyTrans policy = this.getPolicyDetails(polCode);
        String polBusinessType = policy.getProduct().getProGroup().getPrgType();
        if (!polBusinessType.equalsIgnoreCase("L")) {
            if ("NB".equalsIgnoreCase(policy.getTransType()) || "SP".equalsIgnoreCase(policy.getTransType()) || "EX".equalsIgnoreCase(policy.getTransType()) || "RN".equalsIgnoreCase(policy.getTransType()))
                try {
                    premiumService.computePrem(polCode);
                } catch (IOException e) {
                    throw new BadRequestException(e.getMessage());
                }
            else if ("EN".equalsIgnoreCase(policy.getTransType())) {
                try {
                    premiumService.computeEndorsePremium(polCode);
                } catch (IOException e) {
                    throw new BadRequestException(e.getMessage());
                }
            }
        }
        else{
            try {
                premiumService.computeLifePrem(polCode);
            } catch (IOException e) {
                throw new BadRequestException(e.getMessage());
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<PolicyTaxes> findPolicyTaxes(DataTablesRequest request, Long polCode)
            throws IllegalAccessException {
        if (polCode == null) {
            polCode = -2000l;
        }
        BooleanExpression pred = QPolicyTaxes.policyTaxes.policy.policyId.eq(polCode);
        Page<PolicyTaxes> page = polTaxesRepo.findAll(pred.and(request.searchPredicate(QPolicyTaxes.policyTaxes)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<PolicyClauses> findPolicyClauses(DataTablesRequest request, Long polCode)
            throws IllegalAccessException {
        BooleanExpression pred = QPolicyClauses.policyClauses.policy.policyId.eq(polCode);
        Page<PolicyClauses> page = polClausesRepo.findAll(pred.and(request.searchPredicate(QPolicyClauses.policyClauses)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = false,propagation = Propagation.REQUIRED, rollbackFor = {BadRequestException.class})
    public void populateTaxes(PolicyTrans policy) throws BadRequestException {
        System.out.println("Call Populate Taxes...");
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource);
        Set<PolicyTaxes> policyTaxes = new HashSet<>();
        if (policy == null) throw new BadRequestException("Policy Cannot be null");
        int count = polTaxesRepo.countTotalTaxes(policy.getPolicyId());
        if(count > 0){
            polTaxesRepo.deletePolTaxes(policy.getPolicyId());
        }
        Iterable<RiskTrans> risks = riskRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(policy.getPolicyId()));
        for (RiskTrans risk : risks) {
            final Long prodId = policy.getProduct().getProCode();
            final Long subclassId = risk.getSubclass().getSubId();
            Iterable<TaxRates> taxRates = taxRatesRepo.findAll((QTaxRates.taxRates.active.eq(true).and(QTaxRates.taxRates.mandatory.eq(Boolean.TRUE))).and(QTaxRates.taxRates.subclass.subId.eq(subclassId))
                    .and(QTaxRates.taxRates.productsDef.proCode.eq(prodId)));
            for (TaxRates tax : taxRates) {
                if ( ("EN".equalsIgnoreCase(policy.getTransType()))
                        || ("RN".equalsIgnoreCase(policy.getTransType()))
                        || ("BU".equalsIgnoreCase(policy.getTransType()))
                        || ("EX".equalsIgnoreCase(policy.getTransType()))
                        || ("RE".equalsIgnoreCase(policy.getTransType()))){
                    if (tax.getRevenueItems().getItem() == RevenueItems.SD)
                        continue;
                }
                boolean taxExist = polTaxesRepo.countIfPolicyTaxExist(policy.getPolicyId(), tax.getSubclass().getSubId(),tax.getRevenueItems().getRevenueId()) > 0;
                if (!taxExist) {
                    PolicyTaxes policyTax = new PolicyTaxes();
                    policyTax.setPolicy(policy);
                    policyTax.setRateType(tax.getRateType());
                    policyTax.setRevenueItems(tax.getRevenueItems());
                    policyTax.setSubclass(tax.getSubclass());
                    policyTax.setTaxLevel(tax.getTaxLevel());
                    policyTax.setTaxRate(tax.getTaxRate());
                    policyTax.setDivFactor(tax.getDivFactor());
                    policyTax.setTaxAmount(premComputeService.calculateTax(policy.getPremium(), tax.getTaxRate(), tax.getDivFactor(), tax.getRateType()));
                    policyTaxes.add(policyTax);
                }
            }
        }

        polTaxesRepo.save(policyTaxes);
        Iterable<PolicyTaxes> allpolTaxes = polTaxesRepo.findAll(QPolicyTaxes.policyTaxes.policy.policyId.eq(policy.getPolicyId()));
        for (PolicyTaxes policyTax : allpolTaxes) {
            policyTax.setTaxAmount(premComputeService.calculateTax(policy.getPremium(), policyTax.getTaxRate(), policyTax.getDivFactor(), policyTax.getRateType()));
        }
        polTaxesRepo.save(allpolTaxes);


    }

    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void populateClauses(PolicyTrans policy) throws BadRequestException {
        Set<PolicyClauses> policyClauses = new HashSet<>();
        Iterable<PolicyClauses> polClauses = polClausesRepo.findAll(QPolicyClauses.policyClauses.policy.policyId.eq(policy.getPolicyId()));
        polClauses.forEach(policyClauses::add);
        List<Object[]> risks = riskRepo.findPolicyRiskTrans(policy.getPolicyId());
        for (Object[] risk : risks) {
            final Long binderDetId = ((BigInteger) risk[0]).longValue();
            final BinderDetails binderDetails = binderDetRepo.findOne(binderDetId);
            final Long subclassCode = ((BigInteger) risk[17]).longValue();
            Iterable<BinderClauses> binderClauses = binderClauseRepo.findAll(QBinderClauses.binderClauses.binderDet.detId.eq(binderDetails.getDetId()).and(QBinderClauses.binderClauses.mandatory.eq("Y")));
            for (BinderClauses clause : binderClauses) {
                PolicyClauses polClause = new PolicyClauses();
                polClause.setClauHeading((clause.getClauHeading() != null) ? clause.getClauHeading() : clause.getClause().getClause().getClauHeading());
                polClause.setClause(clause.getClause());
                polClause.setClauWording((clause.getClauWording() != null) ? clause.getClauWording() : clause.getClause().getClause().getClauWording());
                if (clause.getEditable() != null && "Y".equalsIgnoreCase(clause.getEditable())) {
                    polClause.setEditable(true);
                }
                polClause.setNewClause("Y");
                polClause.setPolicy(policy);
                policyClauses.add(polClause);
            }
            Iterable<SubclassClauses> subClauses = subclauseRepo.findAll(QSubclassClauses.subclassClauses.subclass.subId.eq(subclassCode).and(QSubclassClauses.subclassClauses.mandatory.eq(true)));
            for (SubclassClauses clause : subClauses) {
                PolicyClauses polClause = new PolicyClauses();
                polClause.setClauHeading(clause.getClause().getClauHeading());
                polClause.setClause(clause);
                polClause.setClauWording(clause.getClause().getClauWording());
                polClause.setEditable(clause.getClause().isEditable());
                polClause.setNewClause("Y");
                polClause.setPolicy(policy);
                policyClauses.add(polClause);
            }
        }

        polClausesRepo.save(policyClauses);

    }
    private void adjustRiskDates(CreateRiskDTO newrisk, LocalDate policyWef, LocalDate policyWet) throws BadRequestException {
        ZoneId zone = ZoneId.systemDefault();

        // 1. Convert risk dates from the bean (these may differ from policy dates)
        LocalDate originalRiskWef = newrisk.getWefDate().toInstant().atZone(zone).toLocalDate();
        LocalDate originalRiskWet = newrisk.getWetDate().toInstant().atZone(zone).toLocalDate();

        // 2. Calculate the exact term duration between the original risk dates
        long termDays = ChronoUnit.DAYS.between(originalRiskWef, originalRiskWet);

        // 3. Adjust only the start date for holidays if needed
        LocalDate adjustedRiskWef = HolidayUtils.getAdjustedTransactionDate(originalRiskWef);

        // 4. Calculate the maturity date by adding the exact term duration
        LocalDate adjustedRiskWet = adjustedRiskWef.plusDays(termDays);

        // 5. Validate the adjusted dates are within policy period
        if (adjustedRiskWef.isBefore(policyWef) || adjustedRiskWet.isAfter(policyWet)) {
            throw new BadRequestException(
                    String.format("Adjusted risk period (%s to %s) must be within policy period (%s to %s)",
                            adjustedRiskWef, adjustedRiskWet, policyWef, policyWet)
            );
        }

        // 6. Set the adjusted dates back to the risk bean
        newrisk.setWefDate(Date.from(adjustedRiskWef.atStartOfDay(zone).toInstant()));
        newrisk.setWetDate(Date.from(adjustedRiskWet.atStartOfDay(zone).toInstant()));
    }

    @PreAuthorize("hasAnyAuthority('SAVE_POLICY')")
    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void createRisk(CreateRiskDTO newrisk, HttpServletRequest request) throws BadRequestException {
        if (newrisk.getBindCode() == null) throw new BadRequestException("Binder is Mandatory");
        if (newrisk.getSclCode() == null) throw new BadRequestException("Sub Class is Mandatory");
        if (newrisk.getCoverCode() == null) throw new BadRequestException("Cover Type is Mandatory");
        if (newrisk.getInsuredCode() == null) throw new BadRequestException("Insured is Mandatory");
        boolean newRisk = newrisk.getRiskId() == null;
        final RiskTrans risk = new RiskTrans();
        org.springframework.beans.BeanUtils.copyProperties(newrisk, risk);
        if (risk.getRiskShtDesc() != null) {
            if (newRisk) {
                if (riskRepo.count(QRiskTrans.riskTrans.riskShtDesc.eq(risk.getRiskShtDesc())
                        .and(QRiskTrans.riskTrans.subclass.riskUnique.isTrue())
                        .and(QRiskTrans.riskTrans.policy.currentStatus.eq("A"))
                        .and((QRiskTrans.riskTrans.wefDate.between(risk.getWefDate(), risk.getWetDate())).or(QRiskTrans.riskTrans.wetDate.between(risk.getWefDate(), risk.getWetDate())))
                        .and(QRiskTrans.riskTrans.policy.transType.in("NB", "RN"))) > 0) {
                    throw new BadRequestException("Risk Already Exists in the system...." +
                            riskRepo.count(QRiskTrans.riskTrans.riskShtDesc.eq(risk.getRiskShtDesc())
                                    .and(QRiskTrans.riskTrans.subclass.riskUnique.isTrue())
                                    .and(QRiskTrans.riskTrans.policy.currentStatus.eq("A"))
                                    .and((QRiskTrans.riskTrans.wefDate.between(risk.getWefDate(), risk.getWetDate())).or(QRiskTrans.riskTrans.wetDate.between(risk.getWefDate(), risk.getWetDate())))
                                    .and(QRiskTrans.riskTrans.policy.transType.in("NB", "RN"))));
                }
            } else {
                if (riskRepo.count(QRiskTrans.riskTrans.riskShtDesc.eq(risk.getRiskShtDesc())
                        .and(QRiskTrans.riskTrans.subclass.riskUnique.isTrue())
                        .and(QRiskTrans.riskTrans.policy.currentStatus.eq("A"))
                        .and((QRiskTrans.riskTrans.wefDate.between(risk.getWefDate(), risk.getWetDate())).or(QRiskTrans.riskTrans.wetDate.between(risk.getWefDate(), risk.getWetDate())))) > 1) {
                    throw new BadRequestException("Risk Already Exists in the system...." +
                            riskRepo.count(QRiskTrans.riskTrans.riskShtDesc.eq(risk.getRiskShtDesc())
                                    .and(QRiskTrans.riskTrans.subclass.riskUnique.isTrue())
                                    .and(QRiskTrans.riskTrans.policy.currentStatus.eq("A"))
                                    .and((QRiskTrans.riskTrans.wefDate.between(risk.getWefDate(), risk.getWetDate())).or(QRiskTrans.riskTrans.wetDate.between(risk.getWefDate(), risk.getWetDate())))));
                }
            }
        }
        if (risk.getWefDate().after(risk.getWetDate())) {
            throw new BadRequestException("Risk Wef Date Cannot be greater than Risk Wet");
        }
        Long riskId = null;
        if (!newRisk) {
            riskId = riskRepo.findOne(newrisk.getRiskId()).getRiskIdentifier();
        }
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        PolicyTrans policy = policyRepo.findOne(polCode);
        Date polWef = dateUtils.removeTime(policy.getWefDate());
        Date polWet = dateUtils.removeTime(policy.getWetDate());
        Date riskWef = dateUtils.removeTime(newrisk.getWefDate());
        Date riskWet = dateUtils.removeTime(newrisk.getWetDate());
        if (riskWef.before(polWef) || riskWef.after(polWet)
                || riskWet.before(polWef) || riskWet.after(polWet)) {
            throw new BadRequestException("Risk Cover Dates outside Policy Cover Periods " + " pol wef " + polWef + " pol wet " + polWet + " Risk wef " + riskWef + " Risk Wet " + riskWet);
        }
        risk.setPolicy(policy);
        risk.setBinder(binderRepo.findOne(newrisk.getBindCode()));
        risk.setCovertype(coverRepo.findOne(newrisk.getCoverCode()));
        risk.setSubclass(subclassRepo.findOne(newrisk.getSclCode()));
        risk.setInsured(clientRepo.findOne(newrisk.getInsuredCode()));
        risk.setBinderDetails(binderDetRepo.findOne(newrisk.getBinderDet()));

        Integer installmentNo = risk.getBinderDetails().getInstallmentsNo();
        if (installmentNo == null) installmentNo = 1;
        policy.setTotalInstalments(installmentNo);
        Long installNo = risk.getInstallmentNo();
        String distribution = risk.getBinderDetails().getDistribution();
        String firstInstallment = "";
        String secondInstallment = "";
        String thirdInstallment = "";
        String fourthInstallment = "";
        if (distribution != null && distribution.contains(":")) {
            int distributionLength = distribution.split(":").length;
            firstInstallment = distribution.split(":")[0];
            if(distributionLength > 1){
                secondInstallment  = distribution.split(":")[1];
            }
            if(distributionLength > 2){
                thirdInstallment  = distribution.split(":")[2];
            }
            if(distributionLength > 3){
                fourthInstallment  = distribution.split(":")[3];
            }

        } else {
            distribution = "100";
            firstInstallment = distribution;
        }
        System.out.println("Passed through here..."+installNo+" risk install percent..."+risk.getInstallmentPerc());
        if(installNo != null && installNo == 1) {
            String installPercentage = risk.getInstallmentPerc();
            boolean isCashPayment = installPercentage == null ||
                    installPercentage.trim().isEmpty() ||
                    "100".equals(installPercentage.trim()) ||
                    "100.0".equals(installPercentage.trim());

            if (!isCashPayment) {
                // Only process installments for non-cash payments
                BigDecimal percentage = BigDecimal.ZERO;

                try{
                    percentage = new BigDecimal(installPercentage);
                }
                catch (Exception ex){
                    throw new BadRequestException("Invalid Installment percentage amount...");
                }

                if(percentage.compareTo(BigDecimal.valueOf(100)) > 0){
                    throw new BadRequestException("Installment Percentage exceeds 100%.. Please make changes to continue...");
                }
                if(percentage.compareTo(new BigDecimal(firstInstallment)) < 0){
                    throw new BadRequestException("The Minimum Installment Percentage must be .."+firstInstallment+" or higher");
                }

                risk.setInstallmentPerc(installPercentage);
            }
            // For cash payments, skip all installment processing
        }

        if (firstInstallment != null) {
            BigDecimal installment = new BigDecimal(firstInstallment);
            if (installment.compareTo(BigDecimal.valueOf(100l)) < 0) {
                risk.setWetDate(DateUtils.addMonths(risk.getWefDate(), 1));
            }
        }
        risk.setInstallmentNo(policy.getInstallmentNo());

        if (policy.getTransType() == null)

            if (newrisk.getPolBindCode() != null) {
                risk.setPolicyBinders(policyBindersRepo.findOne(newrisk.getPolBindCode()));
            }
        risk.setRiskIdentifier(riskId);
        if (risk.getAutogenCert() == null)
            risk.setAutogenCert("N");
        else if ("on".equalsIgnoreCase(risk.getAutogenCert())) {
            risk.setAutogenCert("Y");
        } else risk.setAutogenCert("N");
        if (newRisk) {
            List<SectionTrans> sectionTransactions = newrisk.getSections().stream().map(sectionbean -> {
                SectionsDef sectiondef = setupSectionRepo.findOne(sectionbean.getSection());
                SectionTrans section = new SectionTrans();
                section.setAmount((sectionbean.getAmount() != null) ? sectionbean.getAmount() : BigDecimal.ZERO);
                section.setCompute(sectionbean.isCompute());
                section.setDivFactor(sectionbean.getDivFactor());
                section.setFreeLimit(sectionbean.getFreeLimit());
                List<PremRatesDef> premRates;
                if (sectionbean.getRatesApplicable() != null && "Y".equalsIgnoreCase(sectionbean.getRatesApplicable())) {
                    premRates = premRatesRepo.getSectPremiumRates(newrisk.getBinderDet(), sectionbean.getAmount(), sectiondef.getId());
                    if (premRates.size() == 1) {
                        section.setPremRates(premRates.get(0));
                        section.setRate(premRates.get(0).getRate());
                    }
                } else {
                    premRates = premRatesRepo.getSectPremiumRates(newrisk.getBinderDet(), sectiondef.getId());
                    if (premRates.size() == 1) {
                        section.setPremRates(premRates.get(0));
                        section.setRate(sectionbean.getRate());
                    }
                }
                if (premRates.get(0).getRangeApplicable().equals("Y")) {
                    BigDecimal rangeFrom = premRates.get(0).getRangeFrom();
                    BigDecimal rangeTo = premRates.get(0).getRangeTo();
                    if (premRates.get(0).getRangeType().equalsIgnoreCase("AM")) {
                        if (sectionbean.getAmount().compareTo(rangeFrom) < 0 || sectionbean.getAmount().compareTo(rangeTo) > 0) {
                            try {
                                throw new BadRequestException(String.format("%s is not within the specified range of %s-%s", sectionbean.getAmount(), rangeFrom, rangeTo));
                            } catch (BadRequestException e) {
                                throw new RuntimeException(e.getMessage());
                            }

                        }
                    }
                }
                section.setMultiRate(sectionbean.getMultiplierRate());
                section.setCompute(true);
                section.setRate(sectionbean.getRate());
                section.setSection(sectiondef);
                section.setRisk(risk);
                return section;
            }).filter(a -> a.getPremRates() != null).collect(Collectors.toList());
            sectionRepo.save(sectionTransactions);
        }
        else{
            RiskTrans existingRisk = riskRepo.findFirstByRiskId(newrisk.getRiskId());
            risk.setTransType(existingRisk.getTransType());
            risk.setTotalPercentage(existingRisk.getTotalPercentage());
        }

        RiskTrans savedRisk = riskRepo.save(risk);

        Iterable<BinderReqrdDocs> reqdDocs = reqrdDocsRepo.findAll(QBinderReqrdDocs.binderReqrdDocs.binderDetail.detId.eq(savedRisk.getBinderDetails().getDetId())
                .and(QBinderReqrdDocs.binderReqrdDocs.mandatory.eq(true))
                .and(QBinderReqrdDocs.binderReqrdDocs.requiredDocs.requiredDoc.appliesNewBusiness.eq(true)));


        long riskIdentifier = Long.valueOf(String.valueOf(dateUtils.getUwYear(policy.getWefDate())) + String.valueOf(savedRisk.getRiskId()));
        if (newRisk) {
            PolicyActiveRisks activeRisk = new PolicyActiveRisks();
            activeRisk.setPolicy(policy);
            activeRisk.setRisk(risk);
            activeRisk.setRiskIdentifier(riskIdentifier);
            activeRisk.setStatus("NB");
            activeRisksRepo.save(activeRisk);
            savedRisk.setRiskIdentifier(riskIdentifier);
            riskRepo.save(savedRisk);
        }
        List<RiskDocs> riskDocs = new ArrayList<>();
        for (BinderReqrdDocs reqdDoc : reqdDocs) {
            if (riskDocsRepo.count(QRiskDocs.riskDocs.risk.riskIdentifier.eq(savedRisk.getRiskIdentifier()).and(QRiskDocs.riskDocs.reqdDocs.sclReqrdId.eq(reqdDoc.getRequiredDocs().getSclReqrdId()))) == 0) {
                RiskDocs riskDoc = new RiskDocs();
                riskDoc.setReqdDocs(reqdDoc.getRequiredDocs());
                riskDoc.setRisk(savedRisk);
                riskDocs.add(riskDoc);
            }
        }
        Iterable<SubClassReqdDocs> subClassReqDocs = subclassReqDocRepo.findAll(QSubClassReqdDocs.subClassReqdDocs.subclass.subId.eq(savedRisk.getBinderDetails().getSubCoverTypes().getSubclass().getSubId())
                .and(QSubClassReqdDocs.subClassReqdDocs.mandatory.eq(true))
                .and(QSubClassReqdDocs.subClassReqdDocs.requiredDoc.appliesEndorsement.eq(true)));
        for (SubClassReqdDocs reqdDoc : subClassReqDocs) {
            if (riskDocsRepo.count(QRiskDocs.riskDocs.risk.riskIdentifier.eq(savedRisk.getRiskIdentifier()).and(QRiskDocs.riskDocs.reqdDocs.sclReqrdId.eq(reqdDoc.getSclReqrdId()))) == 0
            ) {
                if (riskDocs.stream().filter(a -> a.getReqdDocs().getSclReqrdId() == reqdDoc.getSclReqrdId()).count() == 0) {
                    RiskDocs riskDoc = new RiskDocs();
                    riskDoc.setReqdDocs(reqdDoc);
                    riskDoc.setRisk(savedRisk);
                    riskDocs.add(riskDoc);
                }
            }
        }
        riskDocsRepo.save(riskDocs);
//        if(policy.getProduct().isMotorProduct()){
//            final List<BigInteger> codes = subclassCertTypesRepo.getCertificateCode(risk.getSubclass().getSubId());
//            if(codes.size()==1) {
//                final RiskCertForm riskCertForm = new RiskCertForm();
//                riskCertForm.setSubclasscertId( ((BigInteger)codes.get(0)).longValue());
//                riskCertForm.setWefDate(risk.getWefDate());
//                riskCertForm.setWetDate(risk.getWetDate());
//                riskCertForm.setRiskId(risk.getRiskId());
//                try {
//                    certService.createRiskCert(riskCertForm);
//                } catch (BadRequestException ex) {
//                    System.out.println("Error creating cert....");
//                }
//            }
//        }
        System.out.println("Policy Transa Type..." + policy.getTransType());
        if ("L".equalsIgnoreCase(policy.getProduct().getProGroup().getPrgType())) {
            try {
                premiumService.computeLifePrem(polCode);
            } catch (IOException e) {
                throw new BadRequestException(e.getMessage());
            }
        } else {
            if ("NB".equalsIgnoreCase(policy.getTransType()) || "SP".equalsIgnoreCase(policy.getTransType()) || "EX".equalsIgnoreCase(policy.getTransType()) || "RN".equalsIgnoreCase(policy.getTransType()))
                try {
                    System.out.println("Passed here to compute prem...");
                    premiumService.computePrem(polCode);
                } catch (IOException e) {
                    throw new BadRequestException(e.getMessage());
                }
            else if ("EN".equalsIgnoreCase(policy.getTransType()) || "CN".equalsIgnoreCase(policy.getTransType())) {
                try {
                    premiumService.computeEndorsePremium(polCode);
                } catch (IOException e) {
                    throw new BadRequestException(e.getMessage());
                }
            }
        }


    }


    @PreAuthorize("hasAnyAuthority('SAVE_POLICY')")
    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void createLifeRisk(CreateRiskDTO newrisk, HttpServletRequest request) throws BadRequestException {
        System.out.println(newrisk.getPremium() + " type.." + newrisk.getComputeType());
        if (newrisk.getBindCode() == null) throw new BadRequestException("Binder is Mandatory");
        if (newrisk.getSclCode() == null) throw new BadRequestException("Sub Class is Mandatory");
        if (newrisk.getCoverCode() == null) throw new BadRequestException("Cover Type is Mandatory");
        if (newrisk.getInsuredCode() == null) throw new BadRequestException("Insured is Mandatory");
        boolean newRisk = newrisk.getRiskId() == null;
        if (newrisk.getWefDate().after(newrisk.getWetDate())) {
            throw new BadRequestException("Risk Wef Date Cannot be greater than Risk Wet");
        }
        Long riskId = null;
        final RiskTrans risk = new RiskTrans();
        org.springframework.beans.BeanUtils.copyProperties(newrisk, risk);
        if (!newRisk) {
            riskId = riskRepo.QueryRiskTrans(risk.getRiskId()).getRiskIdentifier();
        }
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        PolicyTrans policy = policyRepo.findOne(polCode);
        Date polWef = dateUtils.removeTime(policy.getWefDate());
        Date polWet = dateUtils.removeTime(policy.getWetDate());
        Date riskWef = dateUtils.removeTime(newrisk.getWefDate());
        Date riskWet = dateUtils.removeTime(newrisk.getWetDate());
        if (riskWef.before(polWef) || riskWef.after(polWet)
                || riskWet.before(polWef) || riskWet.after(polWet)) {
            throw new BadRequestException("Risk Cover Dates outside Policy Cover Periods Risk Wef " + riskWef + " Risk Wet " + riskWet + " Policy Wef " + polWef + " Policy Wet " + polWet);
        }
        risk.setPolicy(policy);
        risk.setBinder(binderRepo.findOne(newrisk.getBindCode()));
        risk.setCovertype(coverRepo.findOne(newrisk.getCoverCode()));
        risk.setSubclass(subclassRepo.findOne(newrisk.getSclCode()));
        risk.setInsured(clientRepo.findOne(newrisk.getInsuredCode()));
        risk.setBinderDetails(binderDetRepo.findOne(newrisk.getBinderDet()));
        if (newrisk.getPolBindCode() != null) {
            risk.setPolicyBinders(policyBindersRepo.findOne(newrisk.getPolBindCode()));
        }
        risk.setRiskIdentifier(riskId);
        risk.setComputePremium(newrisk.getPremium());
        BigDecimal sumInsured;
        if(newrisk.getSumInsured()!=null && newrisk.getSumInsured().compareTo(BigDecimal.ZERO)>0){
            sumInsured = newrisk.getSumInsured();
        } else {
            sumInsured = BigDecimal.ZERO;
        }
        System.out.println("Sum Insured...."+sumInsured);
        if (newRisk) {
            AtomicInteger index = new AtomicInteger(0);
            List<SectionTrans> sectionTransactions = newrisk.getSections().stream().map(sectionbean -> {
                int i = index.getAndIncrement();
                SectionsDef sectiondef = setupSectionRepo.findOne(sectionbean.getSection());
                SectionTrans section = new SectionTrans();
                if(i==0 && sumInsured.compareTo(BigDecimal.ZERO) > 0){
                    section.setAmount(sumInsured);
                }
                else
                    section.setAmount((sectionbean.getAmount() != null) ? sectionbean.getAmount() : BigDecimal.ZERO);
                section.setCompute(sectionbean.isCompute());
                section.setDivFactor(sectionbean.getDivFactor());
                section.setFreeLimit(sectionbean.getFreeLimit());
                if (sectionbean.getRatesApplicable() != null && "Y".equalsIgnoreCase(sectionbean.getRatesApplicable())) {
                    List<PremRatesDef> premRates = premRatesRepo.getSectPremiumRates(newrisk.getBinderDet(), sectionbean.getAmount(), sectiondef.getId());
                    if (premRates.size() == 1) {
                        section.setPremRates(premRates.get(0));
                        section.setRate(premRates.get(0).getRate());
                    }
                } else {
                    List<PremRatesDef> premRates = premRatesRepo.getSectPremiumRates(newrisk.getBinderDet(), sectiondef.getId());
                    if (premRates.size() == 1) {
                        section.setPremRates(premRates.get(0));
                        section.setRate(sectionbean.getRate());
                    }
                }
                section.setMultiRate(sectionbean.getMultiplierRate());
                section.setCompute(true);
                section.setRate(sectionbean.getRate());
                section.setSection(sectiondef);
                section.setRisk(risk);
                return section;
            }).filter(a -> a.getPremRates() != null).collect(Collectors.toList());
            sectionRepo.save(sectionTransactions);
        }
        else{
            Iterable<SectionTrans> sectionTransactions = sectionRepo.findAll(QSectionTrans.sectionTrans.risk.riskId.eq(newrisk.getRiskId()));
            Optional<SectionTrans> sectionTransOptional =  Streamable.streamOf(sectionTransactions).filter(a -> a.getSection().getType()==SectionTypes.SI).findFirst();
            System.out.println("Sectional...test.."+sectionTransOptional.isPresent());
            if (sectionTransOptional.isPresent()){
                SectionTrans sectionTrans = sectionTransOptional.get();
                System.out.println("Section: "+sectionTrans.getSection().getDesc()+" id "+sectionTrans.getSectId());
                sectionTrans.setAmount(sumInsured);
                sectionRepo.save(sectionTrans);
            }
        }

        RiskTrans savedRisk = riskRepo.save(risk);

        Iterable<BinderReqrdDocs> reqdDocs = reqrdDocsRepo.findAll(QBinderReqrdDocs.binderReqrdDocs.binderDetail.detId.eq(savedRisk.getBinderDetails().getDetId())
                .and(QBinderReqrdDocs.binderReqrdDocs.mandatory.eq(true))
                .and(QBinderReqrdDocs.binderReqrdDocs.requiredDocs.requiredDoc.appliesNewBusiness.eq(true)));


        long riskIdentifier = Long.valueOf(String.valueOf(dateUtils.getUwYear(policy.getWefDate())) + String.valueOf(savedRisk.getRiskId()));
        if (newRisk) {
            PolicyActiveRisks activeRisk = new PolicyActiveRisks();
            activeRisk.setPolicy(policy);
            activeRisk.setRisk(risk);
            activeRisk.setRiskIdentifier(riskIdentifier);
            activeRisk.setStatus("NB");
            activeRisksRepo.save(activeRisk);
            savedRisk.setRiskIdentifier(riskIdentifier);
            riskRepo.save(savedRisk);
        }
        List<RiskDocs> riskDocs = new ArrayList<>();
        for (BinderReqrdDocs reqdDoc : reqdDocs) {
            if (riskDocsRepo.count(QRiskDocs.riskDocs.risk.riskIdentifier.eq(savedRisk.getRiskIdentifier()).and(QRiskDocs.riskDocs.reqdDocs.sclReqrdId.eq(reqdDoc.getRequiredDocs().getSclReqrdId()))) == 0) {
                RiskDocs riskDoc = new RiskDocs();
                riskDoc.setReqdDocs(reqdDoc.getRequiredDocs());
                riskDoc.setRisk(savedRisk);
                riskDocs.add(riskDoc);
            }
        }
        Iterable<SubClassReqdDocs> subClassReqDocs = subclassReqDocRepo.findAll(QSubClassReqdDocs.subClassReqdDocs.subclass.subId.eq(savedRisk.getBinderDetails().getSubCoverTypes().getSubclass().getSubId())
                .and(QSubClassReqdDocs.subClassReqdDocs.mandatory.eq(true))
                .and(QSubClassReqdDocs.subClassReqdDocs.requiredDoc.appliesEndorsement.eq(true)));
        for (SubClassReqdDocs reqdDoc : subClassReqDocs) {
            if (riskDocsRepo.count(QRiskDocs.riskDocs.risk.riskIdentifier.eq(savedRisk.getRiskIdentifier()).and(QRiskDocs.riskDocs.reqdDocs.sclReqrdId.eq(reqdDoc.getSclReqrdId()))) == 0
            ) {
                if (riskDocs.stream().filter(a -> a.getReqdDocs().getSclReqrdId() == reqdDoc.getSclReqrdId()).count() == 0) {
                    RiskDocs riskDoc = new RiskDocs();
                    riskDoc.setReqdDocs(reqdDoc);
                    riskDoc.setRisk(savedRisk);
                    riskDocs.add(riskDoc);
                }
            }
        }
        riskDocsRepo.save(riskDocs);

    }


    @PreAuthorize("hasAnyAuthority('SAVE_POLICY')")
    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void deleteRisk(Long riskId, HttpServletRequest request) throws BadRequestException {
        RiskTrans risk = riskRepo.findOne(riskId);
        if (risk == null) throw new BadRequestException("No Risk to delete");
        List<SectionTrans> sections = risk.getSectionTrans();
        Iterable<ScheduleTrans> scheduleTrans = scheduleTransRepo.findAll(QScheduleTrans.scheduleTrans.risk.riskId.eq(riskId));
        scheduleTransRepo.delete(scheduleTrans);
        Iterable<PrintCertificateQueue> certificateQueues = queueRepo.findAll(QPrintCertificateQueue.printCertificateQueue.risk.riskId.eq(riskId));
        queueRepo.delete(certificateQueues);
        Iterable<PolicyCerts> policyCerts = certsRepo.findAll(QPolicyCerts.policyCerts.risk.riskId.eq(riskId));
        certsRepo.delete(policyCerts);
        Iterable<RiskDocs> riskDocs = riskDocsRepo.findAll(QRiskDocs.riskDocs.risk.riskId.eq(riskId));
        riskDocsRepo.delete(riskDocs);
        Iterable<RiskInterestedParties> riskInterestedParties = riskIntPartiesRepo.findAll(QRiskInterestedParties.riskInterestedParties.risk.riskId.eq(riskId));
        riskIntPartiesRepo.delete(riskInterestedParties);
        sectionRepo.delete(sections);
        if ("NB".equalsIgnoreCase(risk.getTransType())) {
            PolicyActiveRisks activeRisk = activeRisksRepo.getActiveRisks(riskId);
            if (activeRisk != null)
                activeRisksRepo.delete(activeRisk);
        } else {
            PolicyActiveRisks activeRisk = activeRisksRepo.getActiveRisks(riskId);
            System.out.println("actice risks"+activeRisk+" "+riskId);

            if (activeRisk.getPrevRisk() != null) {
                activeRisk.setRisk(activeRisk.getPrevRisk());
                activeRisk.setPrevRisk(null);
                activeRisksRepo.save(activeRisk);
            } else {
                activeRisksRepo.delete(activeRisk);
            }
        }
        riskRepo.delete(riskId);
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        PolicyTrans policy = this.getPolicyDetails(polCode);
        if ("NB".equalsIgnoreCase(policy.getTransType()) || "SP".equalsIgnoreCase(policy.getTransType()) || "EX".equalsIgnoreCase(policy.getTransType()) || "RN".equalsIgnoreCase(policy.getTransType()))
            try {
                premiumService.computePrem(polCode);
            } catch (IOException e) {
                throw new BadRequestException(e.getMessage());
            }
        else if ("EN".equalsIgnoreCase(policy.getTransType())) {
            try {
                premiumService.computeEndorsePremium(polCode);
            } catch (IOException e) {
                throw new BadRequestException(e.getMessage());
            }
        }

    }

    @PreAuthorize("hasAnyAuthority('MAKE_POLICY_READY')")
    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void makeMedicalReady(Long polCode) throws BadRequestException {
        PolicyTrans policy = policyRepo.findOne(polCode);
        if (policy == null) throw new BadRequestException("No Policy Transaction to make Ready");
        if ("CR".equalsIgnoreCase(policy.getTransType())) {
            if (policy.getReissueCardFee() == null || policy.getReissueCardFee().compareTo(BigDecimal.ZERO) == 0) {
                throw new BadRequestException("Cannot authorise...Reissue Charge fee is zero");
            }
        }
        if (!authLimits.checkAuthorizationLimits("MAKE_POLICY_READY", policy.getBasicPrem())) {
            throw new BadRequestException("You have no rights to make ready the transaction...Check your authorization limits..");
        }
        long selfParamsCount = selfFundParamsRepo.count(QSelfFundParams.selfFundParams.policyTrans.policyId.eq(polCode));

        boolean selffundPolicy = (policy.getBinder().getFundBinder() != null && "Y".equalsIgnoreCase(policy.getBinder().getFundBinder()));

        if (selffundPolicy) {
            if (selfParamsCount == 0)
                throw new BadRequestException("Fund Parameters Record is Mandatory for Fund Transactions");
        }

        Iterable<MedicalCategory> risks = categoryRepo.findAll(QMedicalCategory.medicalCategory.policy.policyId.eq(polCode));
        for (MedicalCategory category : risks) {
            if (membersRepo.count(QCategoryMembers.categoryMembers.category.id.eq(category.getId())) == 0) {
                throw new BadRequestException("Cannot Make Ready the policy...No Category Members Specified for " + category.getDesc());
            }
            Iterable<CategoryMembers> categoryMembers = membersRepo.findAll(QCategoryMembers.categoryMembers.category.id.eq(category.getId()));
            for (CategoryMembers categoryMember : categoryMembers) {
                Iterable<RiskDocs> riskDocs = riskDocsRepo.findAll(QRiskDocs.riskDocs.member.sectId.eq(categoryMember.getSectId()));
                for (RiskDocs riskDoc : riskDocs) {
                    if (riskDoc.getCheckSum() == null || org.apache.commons.lang.StringUtils.isBlank(riskDoc.getCheckSum())) {
                        throw new BadRequestException(String.format("Cannot authorize policy without uploading documents for Member No %s", categoryMember.getMemberShipNo()));
                    }
                }
            }
        }
        Date polWef = dateUtils.removeTime(policy.getWefDate());
        Date polWet = dateUtils.removeTime(policy.getWetDate());
        Iterable<MedicalCategory> categories = categoryRepo.findAll(QMedicalCategory.medicalCategory.policy.policyId.eq(polCode));
        Iterable<SelfFundParams> selfFundParams = selfFundParamsRepo.findAll(QSelfFundParams.selfFundParams.policyTrans.policyId.eq(polCode));
        if (selffundPolicy) {
            for (SelfFundParams selfFundParam : selfFundParams) {
                if ((selfFundParam.getFundDepositAmount() == null || selfFundParam.getFundDepositAmount().compareTo(BigDecimal.ZERO) <= 0) && (policy.getTransType() == "NB" || policy.getTransType() == "RN"))
                    throw new BadRequestException("The self fund deposit amount cannot be Zero for this transaction");
            }
        } else {
            if ((policy.getBasicPrem() == null || policy.getBasicPrem().compareTo(BigDecimal.ZERO) <= 0) && (policy.getTransType() == "NB" || policy.getTransType() == "RN"))
                throw new BadRequestException("Premium amount cannot be Zero for this transaction");
        }
        for (MedicalCategory category : categories) {
            Iterable<MedCategoryBenefits> categoryBenefits = categoryBenefitRepo.findAll(QMedCategoryBenefits.medCategoryBenefits.category.id.eq(category.getId()));
            if (selffundPolicy) {
                for (MedCategoryBenefits benefit : categoryBenefits) {
                    if (benefit.getFundLimit() == null || benefit.getFundLimit().compareTo(BigDecimal.ZERO) <= 0)
                        throw new BadRequestException("Limit amount is mandatory for all benefits in this policy");
                }
            } else {
                for (MedCategoryBenefits benefit : categoryBenefits) {
                    if (benefit.getLimit() == null)
                        throw new BadRequestException("Limit amount is mandatory for all benefits in this policy");
                    if ((benefit.getPremium() == null || benefit.getPremium().compareTo(BigDecimal.ZERO) <= 0) && (policy.getTransType() == "NB" || policy.getTransType() == "RN")) {
                        throw new BadRequestException("Premium for benefit " + benefit.getCover().getSection().getDesc() + " cannot be zero or negative");
                    }
                }

            }
            Iterable<CategoryMembers> categoryMembers = membersRepo.findAll(QCategoryMembers.categoryMembers.category.id.eq(category.getId()));
            for (CategoryMembers member : categoryMembers) {
                Date riskWef = dateUtils.removeTime(member.getWefDate());
                Date riskWet = dateUtils.removeTime(member.getWetDate());
                if ((riskWef.before(polWef) || riskWef.after(polWet)
                        || riskWet.before(polWef) || riskWet.after(polWet)) && (!(member.getMemberStatus().equalsIgnoreCase("A")) && !(member.getMemberStatus().equalsIgnoreCase("E")))) {
                    throw new BadRequestException("Member Cover Dates outside Policy Cover Periods ");
                }
            }

        }
        boolean medicalProduct = false;
        if (policy.getProduct().getProGroup().getPrgType() == null || !policy.getProduct().getProGroup().getPrgType().equalsIgnoreCase("MD")) {
            medicalProduct = false;
        } else if (policy.getProduct().getProGroup().getPrgType().equalsIgnoreCase("MD")) {
            medicalProduct = true;
        }
        rulesExecutor.handlePolicyChecks(policy);
        if ("D".equalsIgnoreCase(policy.getAuthStatus())) {
            policy.setAuthStatus("R");
            policyRepo.save(policy);
            Map<String, Object> processVariables = Maps.newHashMap();
            processVariables.put("canAuthorize", true);
            workflowService.completeTask(String.valueOf(polCode), processVariables, policy, DocType.GEN_UW_DOCUMENT, (medicalProduct) ? "Y" : "N", null, null, null, null);
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void makeRenewalReady(Long polCode) throws BadRequestException {
        PolicyTrans policy = policyRepo.findOne(polCode);
        if (policy == null) throw new BadRequestException("No Policy Transaction to make Ready");
        Date polWef = dateUtils.removeTime(policy.getWefDate());
        Date polWet = dateUtils.removeTime(policy.getWetDate());
        Iterable<RiskTrans> risks = riskRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(polCode));
        for (RiskTrans riskBean : risks) {
            Date riskWef = dateUtils.removeTime(riskBean.getWefDate());
            Date riskWet = dateUtils.removeTime(riskBean.getWetDate());
            if (riskWef.before(polWef) || riskWef.after(polWet)
                    || riskWet.before(polWef) || riskWet.after(polWet)) {
                throw new BadRequestException("Risk Cover Dates outside Policy Cover Periods ");
            }

            if (riskBean.getBinderDetails().getBinder().getBinId() != riskBean.getBinder().getBinId()) {
                throw new BadRequestException("Cannot Make Ready...Sub Class and Cover Type Details do not match the binder selected");
            }
        }

        if ("EN".equalsIgnoreCase(policy.getTransType()) || "RS".equalsIgnoreCase(policy.getTransType()) || "CO".equalsIgnoreCase(policy.getTransType())) {
            Long remarkCount = policyRemarksRepo.count(QPolicyRemarks.policyRemarks.policy.policyId.eq(polCode));
            if (remarkCount == 0)
                throw new BadRequestException("Endorsement Remarks are mandatory");
            PolicyRemarks remarks = policyRemarksRepo.findOne(QPolicyRemarks.policyRemarks.policy.policyId.eq(polCode));
            if (remarks.getPolRemarks() == null || StringUtils.isBlank(remarks.getPolRemarks()))
                throw new BadRequestException("Endorsement Remarks are mandatory");
        }
        if ("D".equalsIgnoreCase(policy.getAuthStatus())) {
            policy.setAuthStatus("R");
            policyRepo.save(policy);
        }
    }

    private BigDecimal sign(String type) {
        return ("C".equalsIgnoreCase(type) ? BigDecimal.ONE.multiply(BigDecimal.valueOf(-1)) : BigDecimal.ONE.multiply(BigDecimal.valueOf(1)));
    }

    @PreAuthorize("hasAnyAuthority('MAKE_POLICY_READY')")
    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class}, propagation = Propagation.REQUIRED)
    public String makeLifeReady(Long polCode) throws BadRequestException {
        PolicyTrans policy = policyRepo.findOne(polCode);

        boolean isBulkUpload = "BU".equalsIgnoreCase(policy.getTransType());

        if (policy == null) throw new BadRequestException("No Policy Transaction to make Ready");
        boolean isEndorsement = "EN".equalsIgnoreCase(policy.getTransType()) ||
                "CN".equalsIgnoreCase(policy.getTransType());
        if(userUtils.getCurrentUser()!=null) {
            if (!authLimits.checkAuthorizationLimits("MAKE_POLICY_READY", policy.getBasicPrem())) {
                throw new BadRequestException("You have no rights to make ready the transaction...Check your authorization limits..");
            }
        }

        boolean checks = false;// rulesExecutor.handlePolicyChecks(policy);
        long sectCount = sectionRepo.count(QSectionTrans.sectionTrans.risk.policy.policyId.eq(polCode));
        if (sectCount == 0)
            throw new BadRequestException("Cannot Make Ready the Policy without Premium Items..");
        Date polWef = dateUtils.removeTime(policy.getWefDate());
        Date polWet = dateUtils.removeTime(policy.getWetDate());

        //SKIP DOCUMENTS CHECK FOR BULK UPLOADS
        if(!isBulkUpload) {
            Iterable<RiskTrans> risks = riskRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(polCode));
            for (RiskTrans riskBean : risks) {
                Date riskWef = dateUtils.removeTime(riskBean.getWefDate());
                Date riskWet = dateUtils.removeTime(riskBean.getWetDate());
                if (riskWef.before(polWef) || riskWef.after(polWet)
                        || riskWet.before(polWef) || riskWet.after(polWet)) {
                    throw new BadRequestException("Risk Cover Dates outside Policy Cover Periods");
                }

//            Iterable<RiskDocs> riskDocs = riskDocsRepo.findAll(QRiskDocs.riskDocs.risk.riskId.eq(riskBean.getRiskId()));
//            for (RiskDocs riskDoc : riskDocs) {
//                if (riskDoc.getCheckSum() == null || org.apache.commons.lang.StringUtils.isBlank(riskDoc.getCheckSum())) {
//                    throw new BadRequestException(String.format("Cannot authorize policy without uploading documents for Risk %s", riskBean.getRiskShtDesc()));
//                }
//            }
                Iterable<SubClassReqdDocs> subClassReqdDocs = subclassReqDocRepo.findAll(QSubClassReqdDocs.subClassReqdDocs.subclass.subId.eq(riskBean.getSubclass().getSubId()));
                Iterable<RiskDocs> riskDocs = riskDocsRepo.findAll(QRiskDocs.riskDocs.risk.riskId.eq(riskBean.getRiskId()));
                if (subClassReqdDocs == null) {
                    throw new BadRequestException("Subclass Required Documents are not set up");
                }
                if (riskDocs == null) {
                    throw new BadRequestException("Please upload required documents");
                }
                for (SubClassReqdDocs subClassReqdDoc : subClassReqdDocs) {
                    SybrinCases caseExists = sybrinCasesRepo.findOne(QSybrinCases.sybrinCases.caseRiskId.eq(riskBean.getRiskId()).and(QSybrinCases.sybrinCases.caseRequiredDocId.eq(subClassReqdDoc.getRequiredDoc().getReqId())));
                    String requiredDocDesc = subClassReqdDoc.getRequiredDoc().getReqDesc();
                    boolean isContraLife = "CO".equalsIgnoreCase(policy.getTransType())
                            && "L".equalsIgnoreCase(policy.getProduct().getProGroup().getPrgType());

                    if (subClassReqdDoc.isMandatory() && !isContraLife) {
                        if (caseExists == null) {
//                            throw new BadRequestException(String.format("%s document is Mandatory", subClassReqdDoc.getRequiredDoc().getReqDesc()));
                        }
                    }

                    for (RiskDocs riskDoc : riskDocs) {
                        if (requiredDocDesc.equals(riskDoc.getReqdDocs().getRequiredDoc().getReqDesc()) &&
                                caseExists == null) {
                            throw new BadRequestException(String.format("%s document upload  is initiated but not completed. Upload document to proceed.", requiredDocDesc));
                        }
                    }

                }

                Set<String> attachedDocDescriptions = new HashSet<>();
                if (riskDocs != null) {
                    for (RiskDocs riskDoc : riskDocs) {
                        attachedDocDescriptions.add(riskDoc.getReqdDocs().getRequiredDoc().getReqDesc());
                    }
                }

                if (policy.getNegotiatedPremium() != null) {

                    // Flag to track if the Negotiated Premium Authorization document is found
                    boolean isNegotiatedPremiumDocFound = false;


                    if (!policy.getProduct().getProGroup().getPrgType().equalsIgnoreCase("L") && (riskDocs == null || !riskDocs.iterator().hasNext())) {
                        throw new BadRequestException("No supporting documents found. Please upload the required documents.");
                    }


                    // Validate that the required "Negotiated Premium Authorization" document exists and is uploaded
                    for (SubClassReqdDocs reqdDoc : subClassReqdDocs) {
                        String requiredDocDesc = reqdDoc.getRequiredDoc().getReqDesc();
                        System.out.println("Required document: " + requiredDocDesc);

                        if ("Negotiated Premium Authorization".equals(requiredDocDesc)) {
                            isNegotiatedPremiumDocFound = true;

                            // Check if the required document is attached
                            if (!attachedDocDescriptions.contains(requiredDocDesc)) {
                                throw new BadRequestException("Negotiated Premium Authorization document is missing. Please upload it.");
                            }

                        }
                    }

                    // Ensure that the Negotiated Premium Authorization is part of the required documents
                    if (!isNegotiatedPremiumDocFound) {
                        throw new BadRequestException("Negotiated Premium Authorization is a required document but is not listed in the system.");
                    }

                    System.out.println("All required documents validated successfully.");
                }

                if (!Objects.equals(riskBean.getBinderDetails().getBinder().getBinId(), riskBean.getBinder().getBinId())) {
                    throw new BadRequestException("Cannot Make Ready...Sub Class and Cover Type Details do not match the binder selected");
                }
            }

            List<Object[]> beneficiaryData = policyBeneficiariesRepo.countBeneficiaries(polCode);
            if (beneficiaryData.size() != 1) {
                throw new BadRequestException("Beneficiary Data is not available...");
            }
            Object[] data = beneficiaryData.get(0);
            Long count = ((BigInteger) data[0]).longValue();
            BigDecimal totalPercentage = (BigDecimal) data[1];
            if (count == 0) {
                throw new BadRequestException("No Beneficiary data available...");
            }

            if (totalPercentage.compareTo(BigDecimal.valueOf(100)) != 0) {
                throw new BadRequestException("Beneficiary distribution must add up to 100%");
            }
        }
        policy.setAuthStatus("R");
        policyRepo.save(policy);
        if(userUtils.getCurrentUser()!=null) {
            Map<String, Object> processVariables = Maps.newHashMap();
            processVariables.put("canAuthorize", !checks);
            processVariables.put("confirmAuth", true);
            workflowService.completeTask(String.valueOf(polCode), processVariables, policy, DocType.GEN_UW_DOCUMENT, "N", null, null, null, null);
        }
        return (!checks) ? "Y" : "N";
    }

    @PreAuthorize("hasAnyAuthority('MAKE_POLICY_READY')")
    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class}, propagation = Propagation.REQUIRED)
    public String makeBulkLifeReady(Long polCode, Long userId) throws BadRequestException {
        PolicyTrans policy = policyRepo.findOne(polCode);
        User user = userRepo.findOne(userId);
        boolean isBulkUpload = "BU".equalsIgnoreCase(policy.getTransType());

        if (policy == null) throw new BadRequestException("No Policy Transaction to make Ready");
        boolean isEndorsement = "EN".equalsIgnoreCase(policy.getTransType()) ||
                "CN".equalsIgnoreCase(policy.getTransType());
//        if(user!=null) {
//            if (!authLimits.checkAuthorizationLimits("MAKE_POLICY_READY", policy.getBasicPrem())) {
//                throw new BadRequestException("You have no rights to make ready the transaction...Check your authorization limits..");
//            }
//        }

        boolean checks = false;// rulesExecutor.handlePolicyChecks(policy);
        long sectCount = sectionRepo.count(QSectionTrans.sectionTrans.risk.policy.policyId.eq(polCode));
        if (sectCount == 0)
            throw new BadRequestException("Cannot Make Ready the Policy without Premium Items..");
        Date polWef = dateUtils.removeTime(policy.getWefDate());
        Date polWet = dateUtils.removeTime(policy.getWetDate());

        //SKIP DOCUMENTS CHECK FOR BULK UPLOADS
        if(!isBulkUpload) {
            Iterable<RiskTrans> risks = riskRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(polCode));
            for (RiskTrans riskBean : risks) {
                Date riskWef = dateUtils.removeTime(riskBean.getWefDate());
                Date riskWet = dateUtils.removeTime(riskBean.getWetDate());
                if (riskWef.before(polWef) || riskWef.after(polWet)
                        || riskWet.before(polWef) || riskWet.after(polWet)) {
                    throw new BadRequestException("Risk Cover Dates outside Policy Cover Periods");
                }

//            Iterable<RiskDocs> riskDocs = riskDocsRepo.findAll(QRiskDocs.riskDocs.risk.riskId.eq(riskBean.getRiskId()));
//            for (RiskDocs riskDoc : riskDocs) {
//                if (riskDoc.getCheckSum() == null || org.apache.commons.lang.StringUtils.isBlank(riskDoc.getCheckSum())) {
//                    throw new BadRequestException(String.format("Cannot authorize policy without uploading documents for Risk %s", riskBean.getRiskShtDesc()));
//                }
//            }
                Iterable<SubClassReqdDocs> subClassReqdDocs = subclassReqDocRepo.findAll(QSubClassReqdDocs.subClassReqdDocs.subclass.subId.eq(riskBean.getSubclass().getSubId()));
                Iterable<RiskDocs> riskDocs = riskDocsRepo.findAll(QRiskDocs.riskDocs.risk.riskId.eq(riskBean.getRiskId()));
                if (subClassReqdDocs == null) {
                    throw new BadRequestException("Subclass Required Documents are not set up");
                }
                if (riskDocs == null) {
                    throw new BadRequestException("Please upload required documents");
                }
                for (SubClassReqdDocs subClassReqdDoc : subClassReqdDocs) {
                    SybrinCases caseExists = sybrinCasesRepo.findOne(QSybrinCases.sybrinCases.caseRiskId.eq(riskBean.getRiskId()).and(QSybrinCases.sybrinCases.caseRequiredDocId.eq(subClassReqdDoc.getRequiredDoc().getReqId())));
                    String requiredDocDesc = subClassReqdDoc.getRequiredDoc().getReqDesc();
                    boolean isContraLife = "CO".equalsIgnoreCase(policy.getTransType())
                            && "L".equalsIgnoreCase(policy.getProduct().getProGroup().getPrgType());

                    if (subClassReqdDoc.isMandatory() && !isContraLife) {
                        if (caseExists == null) {
//                            throw new BadRequestException(String.format("%s document is Mandatory", subClassReqdDoc.getRequiredDoc().getReqDesc()));
                        }
                    }

                    for (RiskDocs riskDoc : riskDocs) {
                        if (requiredDocDesc.equals(riskDoc.getReqdDocs().getRequiredDoc().getReqDesc()) &&
                                caseExists == null) {
                            throw new BadRequestException(String.format("%s document upload  is initiated but not completed. Upload document to proceed.", requiredDocDesc));
                        }
                    }

                }

                Set<String> attachedDocDescriptions = new HashSet<>();
                if (riskDocs != null) {
                    for (RiskDocs riskDoc : riskDocs) {
                        attachedDocDescriptions.add(riskDoc.getReqdDocs().getRequiredDoc().getReqDesc());
                    }
                }

                if (policy.getNegotiatedPremium() != null) {

                    // Flag to track if the Negotiated Premium Authorization document is found
                    boolean isNegotiatedPremiumDocFound = false;


                    if (!policy.getProduct().getProGroup().getPrgType().equalsIgnoreCase("L") && (riskDocs == null || !riskDocs.iterator().hasNext())) {
                        throw new BadRequestException("No supporting documents found. Please upload the required documents.");
                    }


                    // Validate that the required "Negotiated Premium Authorization" document exists and is uploaded
                    for (SubClassReqdDocs reqdDoc : subClassReqdDocs) {
                        String requiredDocDesc = reqdDoc.getRequiredDoc().getReqDesc();
                        System.out.println("Required document: " + requiredDocDesc);

                        if ("Negotiated Premium Authorization".equals(requiredDocDesc)) {
                            isNegotiatedPremiumDocFound = true;

                            // Check if the required document is attached
                            if (!attachedDocDescriptions.contains(requiredDocDesc)) {
                                throw new BadRequestException("Negotiated Premium Authorization document is missing. Please upload it.");
                            }

                        }
                    }

                    // Ensure that the Negotiated Premium Authorization is part of the required documents
                    if (!isNegotiatedPremiumDocFound) {
                        throw new BadRequestException("Negotiated Premium Authorization is a required document but is not listed in the system.");
                    }

                    System.out.println("All required documents validated successfully.");
                }

                if (!Objects.equals(riskBean.getBinderDetails().getBinder().getBinId(), riskBean.getBinder().getBinId())) {
                    throw new BadRequestException("Cannot Make Ready...Sub Class and Cover Type Details do not match the binder selected");
                }
            }

            List<Object[]> beneficiaryData = policyBeneficiariesRepo.countBeneficiaries(polCode);
            if (beneficiaryData.size() != 1) {
                throw new BadRequestException("Beneficiary Data is not available...");
            }
            Object[] data = beneficiaryData.get(0);
            Long count = ((BigInteger) data[0]).longValue();
            BigDecimal totalPercentage = (BigDecimal) data[1];
            if (count == 0) {
                throw new BadRequestException("No Beneficiary data available...");
            }

            if (totalPercentage.compareTo(BigDecimal.valueOf(100)) != 0) {
                throw new BadRequestException("Beneficiary distribution must add up to 100%");
            }
        }
        policy.setAuthStatus("R");
        policyRepo.save(policy);
        if(user!=null) {
            Map<String, Object> processVariables = Maps.newHashMap();
            processVariables.put("canAuthorize", !checks);
            processVariables.put("confirmAuth", true);
            workflowService.completeTask(String.valueOf(polCode), processVariables, policy, DocType.GEN_UW_DOCUMENT, "N", null, null, null, null);
        }
        return (!checks) ? "Y" : "N";
    }

    @PreAuthorize("hasAnyAuthority('MAKE_POLICY_READY')")
    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class}, propagation = Propagation.REQUIRED)

    public String makeReady(Long polCode) throws BadRequestException {
        log.info("Making policy ready for polCode: {}", polCode);

        PolicyTrans policy = policyRepo.findOne(polCode);

        if (policy == null) {
            log.error("No PolicyTrans found for polCode: {}", polCode);
            throw new BadRequestException("No Policy Transaction to make Ready");
        }

//        if(policy.getBasicPrem() != null || policy.getBasicPrem().compareTo(BigDecimal.ZERO) != 0 ){
//            throw new BadRequestException("Basic Premium can't be null or less than 1");
//        }
        //LIFE PRODUCTS DONT HAVE TAXES
        if (!"L".equalsIgnoreCase(policy.getProduct().getProGroup().getPrgType())) {
            if (policy.getPremium() != null && policy.getPremium().compareTo(BigDecimal.ZERO) != 0) {
                if (policy.getPhcf() == null || policy.getPhcf().compareTo(BigDecimal.ZERO) == 0) {
                    throw new BadRequestException("Please populate taxes to continue");
                }
                if (policy.getTrainingLevy() == null || policy.getTrainingLevy().compareTo(BigDecimal.ZERO) == 0) {
                    throw new BadRequestException("Please populate taxes to continue");
                }
            }
        }

        if (policy.getInterfaceType() == null) {
            log.warn("InterfaceType was null for polCode: {}, setting default to 'A'", polCode);
            policy.setInterfaceType("A"); // or "C", or fetch from a default config
        }
        if (policy.getBusinessType() == null) {
            log.warn("BusinessType was null for polCode: {}, setting default to 'NEW'", polCode);
            policy.setBusinessType("NEW"); // Default business type
        }

        if (!(policy.getInterfaceType().equalsIgnoreCase("A") || policy.getInterfaceType().equalsIgnoreCase("C"))) {
            log.error("Invalid Interface Type '{}' for PolicyTrans with polCode: {}", policy.getInterfaceType(), polCode);
            throw new BadRequestException("Set the policy type to either accrual or cash.");
        }

        if (!authLimits.checkAuthorizationLimits("MAKE_POLICY_READY", policy.getBasicPrem())) {
            log.error("Authorization limits failed for user on polCode: {}", polCode);
            throw new BadRequestException("You have no rights to make ready the transaction...Check your authorization limits..");
        }


        boolean checks = false;// rulesExecutor.handlePolicyChecks(policy);
        long sectCount = sectionRepo.count(QSectionTrans.sectionTrans.risk.policy.policyId.eq(polCode));
        if (sectCount == 0)
            throw new BadRequestException("Cannot Make Ready the Policy without Premium Items..");
        // 3. Adjust policy dates based on transaction type
//        LocalDate originalPolWef = new java.sql.Date(policy.getWefDate().getTime()).toLocalDate();
//        LocalDate originalPolWet = new java.sql.Date(policy.getWetDate().getTime()).toLocalDate();
//
//        LocalDate adjustedPolWef = HolidayUtils.getAdjustedTransactionDate(originalPolWef);
//        LocalDate adjustedPolWet;
//        if ("EN".equalsIgnoreCase(policy.getTransType()) || "CN".equalsIgnoreCase(policy.getTransType())) {
//            // For endorsements - keep original end date
//            adjustedPolWet = originalPolWet;
//        } else {
//            // For new business/renewals - maintain exact duration
//            long policyDurationDays = ChronoUnit.DAYS.between(originalPolWef, originalPolWet);
//            adjustedPolWet = adjustedPolWef.plusDays(policyDurationDays);
//
//        }

        // Update policy dates
//        policy.setWefDate(java.sql.Date.valueOf(adjustedPolWef));
//        policy.setWetDate(java.sql.Date.valueOf(adjustedPolWet));

        // 4. Process and validate risks
        List<Object[]> risks = riskRepo.findPolicyRiskTrans(polCode);
        List<RiskTrans> updatedRisks = new ArrayList<>();

        if ("EN".equalsIgnoreCase(policy.getTransType()) || "CO".equalsIgnoreCase(policy.getTransType())) {
            Long remarkCount = policyRemarksRepo.count(QPolicyRemarks.policyRemarks.policy.policyId.eq(polCode));
            if (remarkCount == 0)
                throw new BadRequestException("Cannot Make Ready Endorsement Without Endorsement Remarks");
            PolicyRemarks remarks = policyRemarksRepo.findOne(QPolicyRemarks.policyRemarks.policy.policyId.eq(polCode));
            if (remarks.getPolRemarks() == null || StringUtils.isBlank(remarks.getPolRemarks()))
                throw new BadRequestException("Cannot Make Ready Endorsement Without Endorsement Remarks");
        }

        for (Object[] risk : risks) {
            final Long riskId = ((BigInteger) risk[1]).longValue();
            final Date wef = (Date) risk[7];
            final Date wet = (Date) risk[8];
            final Long binderDetId = ((BigInteger) risk[0]).longValue();

            // Convert and adjust risk dates
            LocalDate originalRiskWef = new java.sql.Date(wef.getTime()).toLocalDate();
            LocalDate originalRiskWet = new java.sql.Date(wet.getTime()).toLocalDate();

//            LocalDate adjustedRiskWef = HolidayUtils.getAdjustedTransactionDate(originalRiskWef);
//            LocalDate adjustedRiskWet;

//            if ("EN".equalsIgnoreCase(policy.getTransType()) || "CN".equalsIgnoreCase(policy.getTransType())) {
//                // For endorsements - keep original risk end date
//                adjustedRiskWet = originalRiskWet;
//            } else {
//                // For new business - maintain exact duration
//                long riskDurationDays = ChronoUnit.DAYS.between(originalRiskWef, originalRiskWet);
//                adjustedRiskWet = adjustedRiskWef.plusDays(riskDurationDays);
//            }
//
//            // Update risk with adjusted dates
//            RiskTrans riskBean = riskRepo.findOne(riskId);
//            riskBean.setWefDate(java.sql.Date.valueOf(adjustedRiskWef));
//            riskBean.setWetDate(java.sql.Date.valueOf(adjustedRiskWet));
//            updatedRisks.add(riskBean);
//            // Validate within policy period
//            if (adjustedRiskWef.isBefore(adjustedPolWef) || adjustedRiskWet.isAfter(adjustedPolWet)) {
//                throw new BadRequestException("Risk Cover Dates outside Policy Cover Periods after adjustments");
//            }

//            if (riskWef.before(polWef) || riskWef.after(polWet)
//                    || riskWet.before(polWef) || riskWet.after(polWet)) {
//                throw new BadRequestException("Risk Cover Dates outside Policy Cover Periods ");
//            }
            final BinderDetails binderDetails = binderDetRepo.findOne(binderDetId);
//            final Long binderId = ((BigInteger)risk[14]).longValue();
            final BindersDef bindersDef = policy.getBinder();

//            Iterable<RiskDocs> riskDocs = riskDocsRepo.findAll(QRiskDocs.riskDocs.risk.riskId.eq(riskBean.getRiskId()));
//            for (RiskDocs riskDoc : riskDocs) {
//                if (riskDoc.getCheckSum() == null || org.apache.commons.lang.StringUtils.isBlank(riskDoc.getCheckSum())) {
//                    throw new BadRequestException(String.format("Cannot authorize policy without uploading documents for Risk %s", riskBean.getRiskShtDesc()));
//                }
//            }

            Iterable<SubClassReqdDocs> subClassReqdDocs = subclassReqDocRepo.findAll(QSubClassReqdDocs.subClassReqdDocs.subclass.subId.eq(((BigInteger) risk[4]).longValue()));
            Iterable<RiskDocs> riskDocs = riskDocsRepo.findRiskDocsById(((BigInteger) risk[1]).longValue());
            if (subClassReqdDocs == null) {
                throw new BadRequestException("Subclass Required Documents are not set up");
            }
            if (riskDocs == null) {
                throw new BadRequestException("Please upload required documents");
            }
            boolean proofOfPayment = false;
            boolean isAccrualDocFound = false;
            for (RiskDocs riskDoc : riskDocs) {
//                if (riskDoc.getReqdDocs().getRequiredDoc().getReqDesc().equalsIgnoreCase("PROOF OF PAYMENT")) {
//                    proofOfPayment = true;
//                }
//                if (policy.getInterfaceType().equalsIgnoreCase("A") && riskDoc.getReqdDocs().getRequiredDoc().getAccrualDoc() != null && riskDoc.getReqdDocs().getRequiredDoc().getAccrualDoc().equalsIgnoreCase("Y")) {
//                    isAccrualDocFound = true;
//                }
            }
            if(!policy.getTransType().equalsIgnoreCase("CO")){ //co
                if(!policy.getTransType().equalsIgnoreCase("BU")) { //skip bu for uploads
                    for (SubClassReqdDocs subClassReqdDoc : subClassReqdDocs) {
                        SybrinCases caseExists = sybrinCasesRepo.findOne(QSybrinCases.sybrinCases.caseRiskId.eq(((BigInteger) risk[1]).longValue()).and(QSybrinCases.sybrinCases.caseRequiredDocId.eq(subClassReqdDoc.getRequiredDoc().getReqId())));
                        String requiredDocDesc = subClassReqdDoc.getRequiredDoc().getReqDesc();
                        if (subClassReqdDoc.isMandatory() && caseExists == null) {
                            throw new BadRequestException(String.format("%s document is Mandatory", subClassReqdDoc.getRequiredDoc().getReqDesc()));
                        }
                        for (RiskDocs riskDoc : riskDocs) {
                            if (requiredDocDesc.equals(riskDoc.getReqdDocs().getRequiredDoc().getReqDesc()) && caseExists == null) {
                                throw new BadRequestException(String.format("%s document upload  is initiated but not completed. Upload document to proceed.", requiredDocDesc));
                            }
                        }
                    }
                }
            }

            //FOR DOWNWARDS EN DONT ASK FOR PROOF OF PAYMENT
            boolean downEn = false;
            List<Object[]> results = policyRemarksRepo.findRemarksByPolicyId(policy.getPolicyId());
            for (Object[] row : results) {
                String remarks = (String) row[0];
                String shortDesc = (String) row[1];

                // Check for "down" (case-insensitive)
                if ((remarks != null && remarks.toLowerCase().contains("down")) || (shortDesc != null && shortDesc.toLowerCase().contains("down"))) {
                    System.out.println("down");
                    downEn = true;
                    break;
                }
            }

            final boolean contraCancellation = policy.getTransType()!=null && (policy.getTransType().equalsIgnoreCase("CO") || policy.getTransType().equalsIgnoreCase("CN"));
            ///----//ask for kyc if the the policy is not from bulk upload
            //  Optional<BulkPolicyCreation> existsFromBulk = bulkPolicyCreationRepo.policyFromUploadCheck(policy.getPolicyId());
            System.out.println(policy.getTransType());
            if(!policy.getTransType().equalsIgnoreCase("BU")) {
                if (!contraCancellation) {
//                dont ask for proff of payment for downwards endorsement
                    if (!(policy.getTransType().equalsIgnoreCase("EN") && downEn) &&
                            !policy.getTransType().equalsIgnoreCase("CO")) {
                        for (SubClassReqdDocs reqdDoc : subClassReqdDocs) {
                            String accrualDocRequired = reqdDoc.getRequiredDoc().getAccrualDoc();
//                            if (policy.getInterfaceType().equalsIgnoreCase("C") && !proofOfPayment) {
//                                throw new BadRequestException("Kindly Attach Proof of Payment");
//                            }


                            if (policy.getInterfaceType().equalsIgnoreCase("A") && !isAccrualDocFound) {
                                log.info(" policy is {}, with transtype {}", policy, policy.getTransType());
                                if (!policy.getTransType().equalsIgnoreCase("RN")) {
                                    throw new BadRequestException("Kindly Attach Required Accrual Documents");
                                }
                            }
                            for (RiskDocs riskDoc : riskDocs) {
                                SybrinCases caseExists = sybrinCasesRepo.findOne(QSybrinCases.sybrinCases.caseRiskId.eq(((BigInteger) risk[1]).longValue()).and(QSybrinCases.sybrinCases.caseRequiredDocId.eq(riskDoc.getReqdDocs().getRequiredDoc().getReqId())));
                                if ((policy.getInterfaceType().equalsIgnoreCase("A") && accrualDocRequired != null && accrualDocRequired.equals(riskDoc.getReqdDocs().getRequiredDoc().getAccrualDoc()) || policy.getInterfaceType().equalsIgnoreCase("C")) &&
                                        caseExists == null) {
                                    throw new BadRequestException(String.format("%s document upload is initiated but not completed", riskDoc.getReqdDocs().getRequiredDoc().getReqDesc()));
                                }
                            }
                        }
                    }
                }
            }

            if (binderDetails.getBinder().getBinId() != bindersDef.getBinId()) {
                throw new BadRequestException("Cannot Make Ready...Sub Class and Cover Type Details do not match the binder selected");
            }
        }

        policy.setAuthStatus("R");
        policyRepo.save(policy);
        boolean cashBasis = policy.getInterfaceType() != null && "C".equalsIgnoreCase(policy.getInterfaceType());
        SystemTransactionsTemp trans = systemTransactionsTempRepo.findOne(QSystemTransactionsTemp.systemTransactionsTemp.policy.policyId.eq(policy.getPolicyId()));

        if (!cashBasis && trans != null) {
            systemTransactionsTempRepo.delete(trans);
        }
        if (!checks) {
            if (cashBasis) {
                BigDecimal basicPrem = (policy.getPremium() == null) ? BigDecimal.ZERO : policy.getPremium();
                if (trans == null) {
                    trans = new SystemTransactionsTemp();
                    Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("D");
                    if (sequenceRepo.count(seqPredicate) == 0)
                        throw new BadRequestException("Sequence for Debit Notes has not been defined");
                    SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
                    Long seqNumber = sequence.getNextNumber();
                    String transType = policy.getTransType();
                    if ("NB".equalsIgnoreCase(transType)) {
                        transType = "NB";
                    } else if ("RN".equalsIgnoreCase(transType)) {
                        transType = "RN";
                    } else if ("BU".equalsIgnoreCase(transType)) {
                        transType = "BU";
                    } else if ("LD".equalsIgnoreCase(transType)) {
                        transType = "LD";
                    } else if ("RE".equalsIgnoreCase(transType)) {
                        transType = "RE";
                    } else {
                        transType = "EN";
                    }
                    TransactionMapping mapping = transMappingRepo.findOne(QTransactionMapping.transactionMapping.transType.eq(transType));
                    final String refNo = ((basicPrem.compareTo(BigDecimal.ZERO) >= 0) ? mapping.getDebitCode() : mapping.getCreditCode()) + String.format("%05d", seqNumber);
                    final String debitCode = ((basicPrem.compareTo(BigDecimal.ZERO) >= 0) ? mapping.getDebitCode() : mapping.getCreditCode());
                    sequence.setLastNumber(seqNumber);
                    sequence.setNextNumber(seqNumber + 1);
                    sequenceRepo.save(sequence);
                    trans.setRefNo(refNo);
                    trans.setTransType(debitCode);

                    //PROVIDE THE REF NO TO BE USED FOR BULK RECEIPTING
                    if(transType.equalsIgnoreCase("BU")) {
                        policy.setRefNo(refNo);
                        policyRepo.save(policy);
                    }
                }


                BigDecimal extras = (policy.getExtras() == null) ? BigDecimal.ZERO : policy.getExtras();
                BigDecimal phcf = (policy.getPhcf() == null) ? BigDecimal.ZERO : policy.getPhcf();
                BigDecimal tl = (policy.getTrainingLevy() == null) ? BigDecimal.ZERO : policy.getTrainingLevy();
                BigDecimal sd = (policy.getStampDuty() == null) ? BigDecimal.ZERO : policy.getStampDuty();
                BigDecimal amountWithTaxes = basicPrem.add(extras).add(phcf).add(tl).add(sd);

                if (basicPrem.compareTo(BigDecimal.ZERO) == 1) {
                    String type = (amountWithTaxes.compareTo(BigDecimal.ZERO) == 1) ? "D" : "C";

                    trans.setAmount(basicPrem.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    trans.setAuthDate(new Date());
                    trans.setAuthorised("Y");
                    trans.setBalance(amountWithTaxes.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    trans.setBranch(policy.getBranch());
                    trans.setClientType("C");
                    trans.setControlAcc(policy.getClient().getTenantNumber());
                    trans.setClient(policy.getClient());
                    trans.setCurrRate(new BigDecimal(1));
                    trans.setCurrency(policy.getTransCurrency());
                    trans.setNarrations("Posting client Debit Note");
                    trans.setNetAmount(amountWithTaxes.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    trans.setOrigin("U");
                    trans.setPhfund(phcf.setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    trans.setPolicy(policy);
                    trans.setSd(sd.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    trans.setTl(tl.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
//                    trans.setTransDate(new Date());
                    trans.setTransDate(java.sql.Date.valueOf(HolidayUtils.getAdjustedTransactionDate(LocalDate.now())));
                    trans.setTransdc((amountWithTaxes.compareTo(BigDecimal.ZERO) == 1) ? "D" : "C");
                    trans.setUserAuth(userUtils.getCurrentUser().getUsername());
                    trans.setWhtx(BigDecimal.ZERO);
                    trans.setExtras(extras.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    trans.setPostedDate(new Date());
                    trans.setPostedUser(userUtils.getCurrentUser());
                    systemTransactionsTempRepo.save(trans);
                }
            }
        }

        //  if ("D".equalsIgnoreCase(policy.getAuthStatus())) {
        boolean medicalProduct = false;
        if (policy.getProduct().getProGroup().getPrgType() == null || !policy.getProduct().getProGroup().getPrgType().equalsIgnoreCase("MD")) {
            medicalProduct = false;
        } else if (policy.getProduct().getProGroup().getPrgType().equalsIgnoreCase("MD")) {
            medicalProduct = true;
        }

        Map<String, Object> processVariables = Maps.newHashMap();
        processVariables.put("canAuthorize", !checks);
        processVariables.put("confirmAuth", true);
        workflowService.completeTask(String.valueOf(polCode), processVariables, policy, DocType.GEN_UW_DOCUMENT, (medicalProduct) ? "Y" : "N", null, null, null, null);
        return (!checks) ? "Y" : "N";
        //}
    }

    @PreAuthorize("hasAnyAuthority('PROPOSAL_CONVERSION')")
    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void convertBulkPropToPolicy(Long polCode) throws BadRequestException {
        PolicyTrans policy = policyRepo.findOne(polCode);
        if (policy == null) throw new BadRequestException("No Proposal Transaction to convert");
        if(userUtils.getCurrentUser()!=null) {
            if (!authLimits.checkAuthorizationLimits("PROPOSAL_CONVERSION", policy.getBasicPrem())) {
                throw new BadRequestException("You have no rights to convert the transaction...Check your authorization limits..");
            }
        }
        if (!("R".equalsIgnoreCase(policy.getAuthStatus()) || "RCV".equalsIgnoreCase(policy.getAuthStatus()))) {
            throw new BadRequestException("Can only convert proposal after Submit Process..");
        }


        if ("RCV".equalsIgnoreCase(policy.getAuthStatus())) {
            policy.setAuthStatus("CV");
        }

        boolean checks = false; // rulesExecutor.handlePolicyChecks(policy);
        long sectCount = sectionRepo.count(QSectionTrans.sectionTrans.risk.policy.policyId.eq(polCode));
        if (sectCount == 0)
            throw new BadRequestException("Cannot convert proposal without Premium Items..");

//        // Convert and adjust policy dates with instanceof check
//        Date policyWefDate = (policy.getWefDate() instanceof java.sql.Date) ?
//                new Date(policy.getWefDate().getTime()) : policy.getWefDate();
//        Date policyWetDate = (policy.getWetDate() instanceof java.sql.Date) ?
//                new Date(policy.getWetDate().getTime()) : policy.getWetDate();
//
//        // Convert to LocalDate
//        LocalDate originalPolWef = policyWefDate.toInstant()
//                .atZone(ZoneId.systemDefault())
//                .toLocalDate();
//        LocalDate originalPolWet = policyWetDate.toInstant()
//                .atZone(ZoneId.systemDefault())
//                .toLocalDate();
//
//        // Adjust dates (keeping your existing holiday adjustment logic)
//        LocalDate adjustedPolWef = HolidayUtils.getAdjustedTransactionDate(originalPolWef);
//        LocalDate adjustedPolWet = originalPolWet;
//
//
//        // Convert back to java.util.Date then to java.sql.Date if needed
//        Date adjustedPolWefDate = Date.from(adjustedPolWef.atStartOfDay(ZoneId.systemDefault()).toInstant());
//        Date adjustedPolWetDate = Date.from(adjustedPolWet.atStartOfDay(ZoneId.systemDefault()).toInstant());
//
//        // Set back to policy (assuming your entity expects java.sql.Date)
//        policy.setWefDate(new java.sql.Date(adjustedPolWefDate.getTime()));
//        policy.setWetDate(new java.sql.Date(adjustedPolWetDate.getTime()));
        // No date adjustment - just get original dates
        Date policyWefDate = policy.getWefDate();
        Date policyWetDate = policy.getWetDate();

        // Set dates as is (assuming entity expects java.sql.Date)
        policy.setWefDate(new java.sql.Date(policyWefDate.getTime()));
        policy.setWetDate(new java.sql.Date(policyWetDate.getTime()));

        Iterable<RiskTrans> risks = riskRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(polCode));
        List<RiskTrans> updatedRisks = new ArrayList<>();

        for (RiskTrans riskBean : risks) {
            // Convert risk dates with instanceof check
//            Date riskWefDate = (riskBean.getWefDate() instanceof java.sql.Date) ?
//                    new Date(riskBean.getWefDate().getTime()) : riskBean.getWefDate();
//            Date riskWetDate = (riskBean.getWetDate() instanceof java.sql.Date) ?
//                    new Date(riskBean.getWetDate().getTime()) : riskBean.getWetDate();
//
//            // Convert to LocalDate
//            LocalDate originalRiskWef = riskWefDate.toInstant()
//                    .atZone(ZoneId.systemDefault())
//                    .toLocalDate();
//            LocalDate originalRiskWet = riskWetDate.toInstant()
//                    .atZone(ZoneId.systemDefault())
//                    .toLocalDate();
//
//            // Adjust risk dates
//            LocalDate adjustedRiskWef = HolidayUtils.getAdjustedTransactionDate(originalRiskWef);
//            LocalDate adjustedRiskWet = originalRiskWet;
//
//            // Validate within policy period
//            if (adjustedRiskWef.isBefore(adjustedPolWef) || adjustedRiskWet.isAfter(adjustedPolWet)) {
//                throw new BadRequestException("Risk Cover Dates outside Policy Cover Periods after adjustments");
//            }
//
//            // Convert back to java.util.Date then to java.sql.Date
//            Date adjustedRiskWefDate = Date.from(adjustedRiskWef.atStartOfDay(ZoneId.systemDefault()).toInstant());
//            Date adjustedRiskWetDate = Date.from(adjustedRiskWet.atStartOfDay(ZoneId.systemDefault()).toInstant());
//
//            riskBean.setWefDate(new java.sql.Date(adjustedRiskWefDate.getTime()));
//            riskBean.setWetDate(new java.sql.Date(adjustedRiskWetDate.getTime()));
//            updatedRisks.add(riskBean);
            Date riskWefDate = riskBean.getWefDate();
            Date riskWetDate = riskBean.getWetDate();

            // Check that risk dates are within policy dates
            if (riskWefDate.before(policyWefDate) || riskWetDate.after(policyWetDate)) {
                throw new BadRequestException("Risk Cover Dates outside Policy Cover Periods");
            }

            riskBean.setWefDate(new java.sql.Date(riskWefDate.getTime()));
            riskBean.setWetDate(new java.sql.Date(riskWetDate.getTime()));
            updatedRisks.add(riskBean);


            Iterable<RiskDocs> riskDocs = riskDocsRepo.findAll(QRiskDocs.riskDocs.risk.riskId.eq(riskBean.getRiskId()));
//            for (RiskDocs riskDoc : riskDocs) {
//                SybrinCases caseExists = sybrinCasesRepo.findOne(QSybrinCases.sybrinCases.caseRiskId.eq(riskBean.getRiskId()).and(QSybrinCases.sybrinCases.caseRequiredDocId.eq(riskDoc.getReqdDocs().getRequiredDoc().getReqId())));
//                String requiredDocDesc = riskDoc.getReqdDocs().getRequiredDoc().getReqDesc();
//                if (requiredDocDesc.equals(riskDoc.getReqdDocs().getRequiredDoc().getReqDesc()) &&
//                        caseExists == null) {
//                    throw new BadRequestException(String.format("%s document upload  is initiated but not completed. Upload document to proceed.", requiredDocDesc));
//                }
//            }
//
//            if (riskBean.getBinderDetails().getBinder().getBinId() != riskBean.getBinder().getBinId()) {
//                throw new BadRequestException("Cannot convert...Sub Class and Cover Type Details do not match the binder selected");
//            }
        }
        // Save all adjusted risks
        riskRepo.save(updatedRisks);

        if ("R".equalsIgnoreCase(policy.getAuthStatus())) {
            boolean medicalProduct = false;
            if (policy.getProduct().getProGroup().getPrgType() == null || !policy.getProduct().getProGroup().getPrgType().equalsIgnoreCase("MD")) {
                medicalProduct = false;
            } else if (policy.getProduct().getProGroup().getPrgType().equalsIgnoreCase("MD")) {
                medicalProduct = true;
            }
//            if(!policy.getTransType().equalsIgnoreCase("NB")){
//                policy.setAuthStatus("CV");
//                policyRepo.save(policy);
//            }
//            else {
                String policyNumberFormat = paramService.getParameterString("POLICY_NO_FORMAT");
                Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("P");
                if (sequenceRepo.count(seqPredicate) == 0)
                    throw new BadRequestException("Sequence for New Business Transactions has not been defined");
                SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
                Long seqNumber = sequence.getNextNumber();
                final String policyNumber = templateMerger.generateFormat(policyNumberFormat, policy.getBranch().getObId(), policy.getProduct().getProCode(), policy.getWefDate(), sequence.getSeqPrefix() + String.format("%05d", seqNumber), null);
                policy.setPolNo(policyNumber);
                sequence.setLastNumber(seqNumber);
                sequence.setNextNumber(seqNumber + 1);
                sequenceRepo.save(sequence);
                policy.setAuthStatus("CV");
                policyRepo.save(policy);
            //}
//            Map<String, Object> processVariables = Maps.newHashMap();
//            processVariables.put("canAuthorize", (checks) ? false : true);
//            workflowService.completeTask(String.valueOf(polCode), processVariables, policy, DocType.GEN_UW_DOCUMENT, (medicalProduct) ? "Y" : "N", null, null, null);
        }
    }


    @PreAuthorize("hasAnyAuthority('PROPOSAL_CONVERSION')")
    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void convertPropToPolicy(Long polCode) throws BadRequestException {
        PolicyTrans policy = policyRepo.findOne(polCode);
        if (policy == null) throw new BadRequestException("No Proposal Transaction to convert");
        if (!authLimits.checkAuthorizationLimits("PROPOSAL_CONVERSION", policy.getBasicPrem())) {
            throw new BadRequestException("You have no rights to convert the transaction...Check your authorization limits..");
        }
        if (!("R".equalsIgnoreCase(policy.getAuthStatus()) || "RCV".equalsIgnoreCase(policy.getAuthStatus()))) {
            throw new BadRequestException("Can only convert proposal after Submit Process..");
        }


        if ("RCV".equalsIgnoreCase(policy.getAuthStatus())) {
            policy.setAuthStatus("CV");
        }

        boolean checks = false; // rulesExecutor.handlePolicyChecks(policy);
        long sectCount = sectionRepo.count(QSectionTrans.sectionTrans.risk.policy.policyId.eq(polCode));
        if (sectCount == 0)
            throw new BadRequestException("Cannot convert proposal without Premium Items..");

//        // Convert and adjust policy dates with instanceof check
//        Date policyWefDate = (policy.getWefDate() instanceof java.sql.Date) ?
//                new Date(policy.getWefDate().getTime()) : policy.getWefDate();
//        Date policyWetDate = (policy.getWetDate() instanceof java.sql.Date) ?
//                new Date(policy.getWetDate().getTime()) : policy.getWetDate();
//
//        // Convert to LocalDate
//        LocalDate originalPolWef = policyWefDate.toInstant()
//                .atZone(ZoneId.systemDefault())
//                .toLocalDate();
//        LocalDate originalPolWet = policyWetDate.toInstant()
//                .atZone(ZoneId.systemDefault())
//                .toLocalDate();
//
//        // Adjust dates (keeping your existing holiday adjustment logic)
//        LocalDate adjustedPolWef = HolidayUtils.getAdjustedTransactionDate(originalPolWef);
//        LocalDate adjustedPolWet = originalPolWet;
//
//
//        // Convert back to java.util.Date then to java.sql.Date if needed
//        Date adjustedPolWefDate = Date.from(adjustedPolWef.atStartOfDay(ZoneId.systemDefault()).toInstant());
//        Date adjustedPolWetDate = Date.from(adjustedPolWet.atStartOfDay(ZoneId.systemDefault()).toInstant());
//
//        // Set back to policy (assuming your entity expects java.sql.Date)
//        policy.setWefDate(new java.sql.Date(adjustedPolWefDate.getTime()));
//        policy.setWetDate(new java.sql.Date(adjustedPolWetDate.getTime()));
        // No date adjustment - just get original dates
        Date policyWefDate = policy.getWefDate();
        Date policyWetDate = policy.getWetDate();

        // Set dates as is (assuming entity expects java.sql.Date)
        policy.setWefDate(new java.sql.Date(policyWefDate.getTime()));
        policy.setWetDate(new java.sql.Date(policyWetDate.getTime()));

        Iterable<RiskTrans> risks = riskRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(polCode));
        List<RiskTrans> updatedRisks = new ArrayList<>();

        for (RiskTrans riskBean : risks) {
            // Convert risk dates with instanceof check
//            Date riskWefDate = (riskBean.getWefDate() instanceof java.sql.Date) ?
//                    new Date(riskBean.getWefDate().getTime()) : riskBean.getWefDate();
//            Date riskWetDate = (riskBean.getWetDate() instanceof java.sql.Date) ?
//                    new Date(riskBean.getWetDate().getTime()) : riskBean.getWetDate();
//
//            // Convert to LocalDate
//            LocalDate originalRiskWef = riskWefDate.toInstant()
//                    .atZone(ZoneId.systemDefault())
//                    .toLocalDate();
//            LocalDate originalRiskWet = riskWetDate.toInstant()
//                    .atZone(ZoneId.systemDefault())
//                    .toLocalDate();
//
//            // Adjust risk dates
//            LocalDate adjustedRiskWef = HolidayUtils.getAdjustedTransactionDate(originalRiskWef);
//            LocalDate adjustedRiskWet = originalRiskWet;
//
//            // Validate within policy period
//            if (adjustedRiskWef.isBefore(adjustedPolWef) || adjustedRiskWet.isAfter(adjustedPolWet)) {
//                throw new BadRequestException("Risk Cover Dates outside Policy Cover Periods after adjustments");
//            }
//
//            // Convert back to java.util.Date then to java.sql.Date
//            Date adjustedRiskWefDate = Date.from(adjustedRiskWef.atStartOfDay(ZoneId.systemDefault()).toInstant());
//            Date adjustedRiskWetDate = Date.from(adjustedRiskWet.atStartOfDay(ZoneId.systemDefault()).toInstant());
//
//            riskBean.setWefDate(new java.sql.Date(adjustedRiskWefDate.getTime()));
//            riskBean.setWetDate(new java.sql.Date(adjustedRiskWetDate.getTime()));
//            updatedRisks.add(riskBean);
            Date riskWefDate = riskBean.getWefDate();
            Date riskWetDate = riskBean.getWetDate();

            // Check that risk dates are within policy dates
            if (riskWefDate.before(policyWefDate) || riskWetDate.after(policyWetDate)) {
                throw new BadRequestException("Risk Cover Dates outside Policy Cover Periods");
            }

            riskBean.setWefDate(new java.sql.Date(riskWefDate.getTime()));
            riskBean.setWetDate(new java.sql.Date(riskWetDate.getTime()));
            updatedRisks.add(riskBean);


            Iterable<RiskDocs> riskDocs = riskDocsRepo.findAll(QRiskDocs.riskDocs.risk.riskId.eq(riskBean.getRiskId()));
            for (RiskDocs riskDoc : riskDocs) {
                SybrinCases caseExists = sybrinCasesRepo.findOne(QSybrinCases.sybrinCases.caseRiskId.eq(riskBean.getRiskId()).and(QSybrinCases.sybrinCases.caseRequiredDocId.eq(riskDoc.getReqdDocs().getRequiredDoc().getReqId())));
                String requiredDocDesc = riskDoc.getReqdDocs().getRequiredDoc().getReqDesc();
                if (requiredDocDesc.equals(riskDoc.getReqdDocs().getRequiredDoc().getReqDesc()) &&
                        caseExists == null) {
                    throw new BadRequestException(String.format("%s document upload  is initiated but not completed. Upload document to proceed.", requiredDocDesc));
                }
            }

            if (riskBean.getBinderDetails().getBinder().getBinId() != riskBean.getBinder().getBinId()) {
                throw new BadRequestException("Cannot convert...Sub Class and Cover Type Details do not match the binder selected");
            }
        }
        // Save all adjusted risks
        riskRepo.save(updatedRisks);

        if ("R".equalsIgnoreCase(policy.getAuthStatus())) {
            boolean medicalProduct = false;
            if (policy.getProduct().getProGroup().getPrgType() == null || !policy.getProduct().getProGroup().getPrgType().equalsIgnoreCase("MD")) {
                medicalProduct = false;
            } else if (policy.getProduct().getProGroup().getPrgType().equalsIgnoreCase("MD")) {
                medicalProduct = true;
            }
            if(!policy.getTransType().equalsIgnoreCase("NB")){
                policy.setAuthStatus("CV");
                policyRepo.save(policy);
            }
            else {
                String policyNumberFormat = paramService.getParameterString("POLICY_NO_FORMAT");
                Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("P");
                if (sequenceRepo.count(seqPredicate) == 0)
                    throw new BadRequestException("Sequence for New Business Transactions has not been defined");
                SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
                Long seqNumber = sequence.getNextNumber();
                final String policyNumber = templateMerger.generateFormat(policyNumberFormat, policy.getBranch().getObId(), policy.getProduct().getProCode(), policy.getWefDate(), sequence.getSeqPrefix() + String.format("%05d", seqNumber), null);
                policy.setPolNo(policyNumber);
                sequence.setLastNumber(seqNumber);
                sequence.setNextNumber(seqNumber + 1);
                sequenceRepo.save(sequence);
                policy.setAuthStatus("CV");
                policyRepo.save(policy);
            }
//            Map<String, Object> processVariables = Maps.newHashMap();
//            processVariables.put("canAuthorize", (checks) ? false : true);
//            workflowService.completeTask(String.valueOf(polCode), processVariables, policy, DocType.GEN_UW_DOCUMENT, (medicalProduct) ? "Y" : "N", null, null, null);
        }
    }



    @PreAuthorize("hasAnyAuthority('PROPOSAL_CONVERSION')")
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    @Override
    public void undoProposalConversion(Long polCode, Long reasonId, String reason) throws BadRequestException {
        PolicyTrans policy = policyRepo.findOne(polCode);
        if (policy == null) throw new BadRequestException("No Policy Transaction to Undo Conversion");
        if ("CN".equalsIgnoreCase(policy.getTransType())) {
            throw new BadRequestException("Cannot Undo Conversion A Cancellation Transaction");
        }
        if ("RS".equalsIgnoreCase(policy.getTransType())) {
            throw new BadRequestException("Cannot Undo Conversion A Section Reinstatement Transaction");
        }

// Build a QueryDSL query to find the most recent MakerChecker record for the policy
        JPAQuery query = new JPAQuery(entityManager);
        QMakerChecker qMakerChecker = QMakerChecker.makerChecker;
        MakerChecker makerChecker = query.from(qMakerChecker)
                .where(qMakerChecker.policyId.eq(polCode))
                .orderBy(new OrderSpecifier<Date>(com.mysema.query.types.Order.DESC, qMakerChecker.checkDate),
                        new OrderSpecifier<Long>(com.mysema.query.types.Order.DESC, qMakerChecker.id))
                .limit(1)
                .list(qMakerChecker)
                .stream()
                .findFirst()
                .orElse(null);

        // Initialize Gson for policy serialization

        Gson gson = new GsonBuilder()
                .setDateFormat("dd/MM/yyyy")
                .registerTypeHierarchyAdapter(HibernateProxy.class, new HibernateProxyTypeAdapter())
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getAnnotation(JsonIgnore.class) != null;
                    }
                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();

        if (makerChecker != null) {
            // Set rejection status and reason
            // Fetch the RejectedReasons entity
            RejectedReasons rejectionReason = rejectedReasonsRepo.findOne(reasonId);
            if (rejectionReason == null) {
                throw new BadRequestException("Invalid rejection reason ID: " + reasonId);
            }
            makerChecker.setStatus("R");
            makerChecker.setRejectedReason(reason); // Set plain-text reason
            makerChecker.setCheckerId(userUtils.getCurrentUser()); // Set current user as checker
            makerChecker.setCheckDate(new Date()); // Set the check date
            makerCheckerRepo.save(makerChecker);


            RtsAudit rtsAudit = new RtsAudit();
            rtsAudit.setCheckerId(userUtils.getCurrentUser());
//            rtsAudit.setRejectionReason(rejectionReason);
//            rtsAudit.setMakerId(userUtils.getCurrentUser());
            rtsAudit.setRejectionReason(null); // Clear the RejectedReasons object
            rtsAudit.setUserRejectedReason(makerChecker.getRejectedReason() != null ? makerChecker.getRejectedReason() : (rejectionReason != null ? rejectionReason.getReasonDesc() : "N/A"));
            rtsAudit.setPolicyId(policy.getPolicyId());
            rtsAudit.setRrtsPolId(polCode);
            rtsAudit.setAuditTime(new Date());
            rtsAudit.setPolicyInfo(gson.toJson(policy));
            rtsAuditRepository.save(rtsAudit);


            // Notify maker (if logic exists)
            makerCheckerService.notifyMaker(makerChecker);
            policy.setAuthStatus("RCV");
        }
        policyRepo.save(policy);
    }

    @PreAuthorize("hasAnyAuthority('MAKE_POLICY_READY')")
    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void undoMakeReady(Long polCode, Long reasonId, String reason) throws BadRequestException {
        PolicyTrans policy = policyRepo.findOne(polCode);
        if (policy == null) throw new BadRequestException("No Policy Transaction to Undo make Ready");
        if ("CN".equalsIgnoreCase(policy.getTransType())) {
            throw new BadRequestException("Cannot Undo Make Ready A Cancellation Transaction");
        }
        if ("RS".equalsIgnoreCase(policy.getTransType())) {
            throw new BadRequestException("Cannot Undo Make Ready A Section Reinstatement Transaction");
        }
        boolean cashBasis = policy.getInterfaceType() != null && "C".equalsIgnoreCase(policy.getInterfaceType());
        if (cashBasis) {
            SystemTransactionsTemp systemTransactionsTemp = systemTransactionsTempRepo.findOne(QSystemTransactionsTemp.systemTransactionsTemp.policy.policyId.eq(polCode));
            if (systemTransactionsTemp != null && systemTransactionsTemp.getBalance().compareTo(BigDecimal.ZERO) == 1) {
                if (systemTransactionsTemp.getSettleAmt() != null && systemTransactionsTemp.getSettleAmt().compareTo(BigDecimal.ZERO) == 1) {
                    throw new BadRequestException("Cannot undo make ready to already paid transaction....");
                }

                systemTransactionsTemp.setAuthorised("N");
                systemTransactionsTempRepo.save(systemTransactionsTemp);
            }


        }
        log.info("checking {}",polCode);
        //MakerChecker makerChecker = makerCheckerRepo.findOne(QMakerChecker.makerChecker.policyId.eq(polCode).and(QMakerChecker.makerChecker.policyId.eq(polCode)));
        MakerChecker makerChecker = makerCheckerRepo.findOne(QMakerChecker.makerChecker.policyId.eq(polCode));

        // Initialize Gson for policy serialization

        Gson gson = new GsonBuilder()
                .setDateFormat("dd/MM/yyyy")
                .registerTypeHierarchyAdapter(HibernateProxy.class, new HibernateProxyTypeAdapter())
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getAnnotation(JsonIgnore.class) != null;
                    }
                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();

        if (makerChecker != null) {
            User currentUser = userUtils.getCurrentUser();
            if (!makerChecker.getMakerId().equals(currentUser)) {
                log.info("Rejecting task for maker: {}, reason: {}", makerChecker.getId(), reason);
                makerCheckerService.rejectTask(makerChecker.getId(), reasonId, reason);
            }else if(makerChecker.getMakerId()!=currentUser){
                makerCheckerService.rejectTask(makerChecker.getId(), reasonId, reason);}

            if (policy.getCreatedUser() != null) {
                makerChecker.setMakerId(policy.getCreatedUser());
            }
            // Fetch the RejectedReasons entity
            RejectedReasons rejectionReason = rejectedReasonsRepo.findOne(reasonId);
            if (rejectionReason == null) {
                throw new BadRequestException("Invalid rejection reason ID: " + reasonId);
            }
            makerChecker.setStatus("R");
            makerChecker.setRejectedReason(reason);
            makerChecker.setModifiedDate(new Date());
            makerChecker.setCheckerId(userUtils.getCurrentUser());
            makerChecker.setCheckDate(new Date());
            makerCheckerRepo.save(makerChecker);

            RtsAudit rtsAudit = new RtsAudit();
            rtsAudit.setCheckerId(userUtils.getCurrentUser());
//            rtsAudit.setRejectionReason(rejectionReason);
            rtsAudit.setRejectionReason(null); // Clear the RejectedReasons object
            rtsAudit.setUserRejectedReason(makerChecker.getRejectedReason() != null ? makerChecker.getRejectedReason() : (rejectionReason != null ? rejectionReason.getReasonDesc() : "N/A"));
//            rtsAudit.setMakerId(userUtils.getCurrentUser());
            rtsAudit.setPolicyId(policy.getPolicyId());
            rtsAudit.setRrtsPolId(polCode);
            rtsAudit.setAuditTime(new Date());
            rtsAudit.setPolicyInfo(gson.toJson(policy));
            rtsAuditRepository.save(rtsAudit);

            makerCheckerService.notifyMaker(makerChecker);
            policy.setAuthStatus("RD");
        } else {
            policy.setAuthStatus("D");
            policy.setCurrentStatus("D");
        }
        boolean medicalProduct = false;
        if (policy.getProduct().getProGroup().getPrgType() == null || !policy.getProduct().getProGroup().getPrgType().equalsIgnoreCase("MD")) {
            medicalProduct = false;
        } else if (policy.getProduct().getProGroup().getPrgType().equalsIgnoreCase("MD")) {
            medicalProduct = true;
        }
        policyRepo.save(policy);
        transChecksRepo.delete(transChecksRepo.findAll(QTransChecks.transChecks.policyTrans.policyId.eq(polCode)));
        Map<String, Object> processVariables = Maps.newHashMap();
        processVariables.put("confirmAuth", false);
        processVariables.put("rejectTrans", true);
        processVariables.put("hasAuthority", false);
        workflowService.completeTask(String.valueOf(polCode), processVariables, policy, DocType.GEN_UW_DOCUMENT, (medicalProduct) ? "Y" : "N", null, null, null, null);

    }

    @Override
    @Transactional(readOnly = true)
    public List<PremRatesDef> getNewPremiumItems(Long detId, Long riskId, String searchVal) {
        if (riskId == null || detId == null)
            return new ArrayList<>();
        return sectionRepo.getUnassignedPremItems(detId, riskId, searchVal);

    }

    @Override
    public List<PremRatesDef> getNewSectPremiumItems(Long detId, Long riskId, String searchVal, Long insuredAge) {
        if (riskId == null || detId == null || insuredAge == null)
            return new ArrayList<>();
        return sectionRepo.getRangePremRates(detId, riskId, searchVal, BigDecimal.valueOf(insuredAge));
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void createRiskSections(RiskBean sections, HttpServletRequest request) throws BadRequestException {
        if (sections.getRiskId() == null)
            throw new BadRequestException("Cannot save a section without Risk Transaction");
        RiskTrans risk = riskRepo.findOne(sections.getRiskId());
        if (risk == null) throw new BadRequestException("Cannot save a section without Risk Transaction");
        List<SectionTrans> sectionTransactions = new ArrayList<>();
        for (RiskSectionBean sectionbean : sections.getSections()) {
            SectionsDef sectiondef = setupSectionRepo.findOne(sectionbean.getSection());
            SectionTrans section = new SectionTrans();
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
            section.setMultiRate(sectionbean.getMultiplierRate());
            section.setCompute(true);
            section.setSection(sectiondef);
            section.setRisk(risk);
            sectionTransactions.add(section);
        }
        sectionRepo.save(sectionTransactions);
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        PolicyTrans policy = this.getPolicyDetails(polCode);
        if(policy.getBusinessType().equalsIgnoreCase("L")){
            try {
                premiumService.computeLifePrem(polCode);
            } catch (IOException e) {
                throw new BadRequestException(e.getMessage());
            }
        }
        else {
            if ("NB".equalsIgnoreCase(policy.getTransType()) || "SP".equalsIgnoreCase(policy.getTransType()) || "EX".equalsIgnoreCase(policy.getTransType()) || "RN".equalsIgnoreCase(policy.getTransType()))
                try {
                    premiumService.computePrem(polCode);
                } catch (IOException e) {
                    throw new BadRequestException(e.getMessage());
                }
            else if ("EN".equalsIgnoreCase(policy.getTransType())) {
                try {
                    premiumService.computeEndorsePremium(polCode);
                } catch (IOException e) {
                    throw new BadRequestException(e.getMessage());
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Set<PolicyTaxes> getNewTaxes(Long polId) throws BadRequestException {
        PolicyTrans policy = policyRepo.findOne(polId);
        if (policy == null) throw new BadRequestException("No Policy Transaction");
        Set<PolicyTaxes> policyTaxes = new HashSet<>();
        Iterable<PolicyTaxes> polTaxes = polTaxesRepo.findAll(QPolicyTaxes.policyTaxes.policy.policyId.eq(polId));
        polTaxes.forEach(policyTaxes::add);
        Set<PolicyTaxes> newTaxes = new HashSet<>();
        Iterable<RiskTrans> risks = riskRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(policy.getPolicyId()));
        for (RiskTrans risk : risks) {
            Iterable<TaxRates> taxes = taxRatesRepo.findAll(QTaxRates.taxRates.subclass.subId.eq(risk.getSubclass().getSubId()).and(QTaxRates.taxRates.active.eq(true)));
            for (TaxRates tax : taxes) {
                PolicyTaxes polTax = new PolicyTaxes();
                polTax.setTaxLevel(tax.getTaxLevel());
                polTax.setDivFactor(tax.getDivFactor());
                polTax.setRateType(tax.getRateType());
                polTax.setRevenueItems(tax.getRevenueItems());
                polTax.setPolicy(policy);
                polTax.setTaxRate(tax.getTaxRate());
                polTax.setPolTaxId(tax.getTaxId());
                newTaxes.add(polTax);
            }
        }
        newTaxes.removeAll(policyTaxes);
        return newTaxes;
    }

    @Override
    public List<InterestedParties> getNewInterestedParties(Long riskId) throws BadRequestException {
        RiskTrans riskTrans = riskRepo.findOne(riskId);
        if (riskTrans == null) throw new BadRequestException("No Risk Transaction");
        return interestedPartiesRepo.searchRiskInterestedParties(riskId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClauseDTO> getNewClauses(Long polId) throws BadRequestException {
        List<Object[]> clausesList = polClausesRepo.getSubClauses(polId);
        final List<ClauseDTO> newClauses = new ArrayList<>();
        for (Object[] clause : clausesList) {
            ClauseDTO clauseDTO = new ClauseDTO();
            clauseDTO.setClauseId(((BigInteger) clause[2]).longValue());
            clauseDTO.setClauseHeading((String) clause[1]);
            newClauses.add(clauseDTO);
        }
        return newClauses;
    }

    @PreAuthorize("hasAnyAuthority('SAVE_POLICY')")
    @Override
    @Transactional(readOnly = false)
    public void deletePolicyClause(Long clauseId) throws BadRequestException {
        PolicyClauses clause = polClausesRepo.findOne(clauseId);
        if (clause.getClause().isMandatory()) {
            throw new BadRequestException("Cannot delete Mandatory Clause");
        }
        List<BinderClauses> binderClauses = clause.getClause().getBinderClauses();
        for (BinderClauses binClauses : binderClauses) {
            if (binClauses.getMandatory() != null && "Y".equalsIgnoreCase(binClauses.getMandatory())) {
                throw new BadRequestException("Cannot delete Mandatory Clause");
            }

        }
        polClausesRepo.delete(clauseId);

    }

    @PreAuthorize("hasAnyAuthority('SAVE_POLICY')")
    @Override
    @Transactional(readOnly = false)
    public void deletePolicyTax(Long taxId) {
        polTaxesRepo.delete(taxId);

    }

    @PreAuthorize("hasAnyAuthority('SAVE_POLICY')")
    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void createClause(PolicyClausesBean clause) throws BadRequestException {
        PolicyTrans policy = policyRepo.findOne(clause.getPolId());
        List<PolicyClauses> createdClauses = new ArrayList<>();
        if (policy == null) throw new BadRequestException("No Policy Transaction");
        for (Long clauseId : clause.getClauses()) {
            for (Iterator<ClauseDTO> it = getNewClauses(clause.getPolId()).iterator(); it.hasNext(); ) {
                final SubclassClauses subclassClauses = subclauseRepo.findOne(it.next().getClauseId());
                PolicyClauses cl = new PolicyClauses();
                cl.setClause(subclassClauses);
                cl.setNewClause("N");
                cl.setClauHeading(subclassClauses.getClause().getClauHeading());
                cl.setEditable(subclassClauses.getClause().isEditable());
                cl.setClauWording(subclassClauses.getClause().getClauWording());
                cl.setPolicy(policy);
                if (cl.getClause().getClauId().longValue() == clauseId.longValue()) {
                    createdClauses.add(cl);
                }

            }
        }
        polClausesRepo.save(createdClauses);

    }

    @PreAuthorize("hasAnyAuthority('SAVE_POLICY')")
    @Override
    @Transactional(readOnly = false)
    public void createPolicyClause(PolicyClauses clause) throws BadRequestException {
        polClausesRepo.save(clause);

    }

    @PreAuthorize("hasAnyAuthority('SAVE_POLICY')")
    @Override
    @Transactional(readOnly = false)
    public void createPolicyTaxes(PolicyTaxes policyTax) throws BadRequestException {
        polTaxesRepo.save(policyTax);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<EndorsementRemarks> findEndorsementRemarks(DataTablesRequest request, Long polCode)
            throws IllegalAccessException {
        Page<EndorsementRemarks> page = policyRemarksRepo.getEndorsementRemarks(polCode, "", request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<PolicyInstallments> findPolicyInstallments(DataTablesRequest request, Long polCode) throws IllegalAccessException {
        Page<PolicyInstallments> page = policyInstallmentsRepo.findAll(QPolicyInstallments.policyInstallments.policyTrans.policyId.eq(polCode), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public PolicyRemarks getPolicyRemarks(Long polCode) {
        Iterable<PolicyRemarks> policyRemarks = policyRemarksRepo.findAll(QPolicyRemarks.policyRemarks.policy.policyId.eq(polCode));
        PolicyRemarks remark = null;
        for (PolicyRemarks remarks : policyRemarks) {
            remark = remarks;
            break;
        }
        return remark;
    }
    @Override
    @Transactional(readOnly = true)
    public String getRefundComments(Long polCode) {
        String comments = policyRepo.getRefundCommentsByPolicyId(polCode);
        System.out.println("Remarks for policy " + polCode + ": " + comments);
        return comments;
    }

    @PreAuthorize("hasAnyAuthority('SAVE_POLICY')")
    @Override
    @Transactional(readOnly = false)
    public void saveEndorsementRemarks(PolicyRemarks remarks) throws BadRequestException {
        if (remarks.getRemarks() == null || StringUtils.isBlank(remarks.getRemarks()))
            throw new BadRequestException("Please Provide Endorsement Remarks to continue");
        if (remarks.getEndRemarks().getRemarkId() == null)
            throw new BadRequestException("Select Existing Endorsement Remarks to Modify and Save");
        PolicyTrans policy = policyRepo.findOne(remarks.getPolicy().getPolicyId());
        String remark = templateMerger.mergePolicyDetails(policy, remarks.getRemarks());
        remarks.setPolRemarks(remark);
        policyRemarksRepo.save(remarks);

    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public DataTablesResult<PolicyTrans> findEnquiryPolicies(DataTablesRequest request, String drNumber,
//                                                             Long clientCode, String polNo, String endorseNumber, Long agentCode,
//                                                             Long prodCode) throws IllegalAccessException {
//        QClientDef client = QPolicyTrans.policyTrans.client;
//        QAccountDef account = QPolicyTrans.policyTrans.agent;
//        QProductsDef product = QPolicyTrans.policyTrans.product;
//        Predicate pred = null;
//
//        if (drNumber == null || StringUtils.isBlank(drNumber)) {
//            drNumber = null;
//        } else {
//            drNumber = drNumber.toLowerCase();
//        }
//
//        if (polNo == null || StringUtils.isBlank(polNo)) {
//            polNo = "";
//        } else {
//            polNo = polNo.toLowerCase();
//        }
//
//        if (endorseNumber == null || StringUtils.isBlank(endorseNumber)) {
//            endorseNumber = "";
//        } else {
//            endorseNumber = endorseNumber.toLowerCase();
//        }
//
//        //System.out.println("polNo "+polNo);
//        pred = QPolicyTrans.policyTrans.polNo.lower().containsIgnoreCase(StringUtils.lowerCase(polNo))
//                .and(QPolicyTrans.policyTrans.riskTrans.any().riskShtDesc.containsIgnoreCase(endorseNumber))
//                .and(QPolicyTrans.policyTrans.product.proGroup.prgType.upper().notEqualsIgnoreCase("MD"))

    /// /                .and(QPolicyTrans.policyTrans.authStatus.notEqualsIgnoreCase("D"))
//                .and((drNumber == null) ? QPolicyTrans.policyTrans.isNotNull() : QPolicyTrans.policyTrans.refNo.lower().containsIgnoreCase(StringUtils.lowerCase(drNumber)))
//                .and((agentCode == null) ? account.isNotNull() : account.acctId.eq(agentCode))
//                .and((prodCode == null) ? product.isNotNull() : product.proCode.eq(prodCode))
//                .and((clientCode == null) ? client.isNotNull() : client.tenId.eq(clientCode));
//        Page<PolicyTrans> page = policyRepo.findAll(pred, request);
//        return new DataTablesResult(request, page);
//    }
    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<EndorsementsDTO> findEnquiryPolicies(DataTablesRequest request, String searchTerm) {
        String lowerCaseSearchTerm = StringUtils.isBlank(searchTerm) ? null : searchTerm.toLowerCase();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        EndorsementsDtoMapper mapper = new EndorsementsDtoMapper();
        long countPolicies = jdbcTemplate.queryForObject(EndorsementPolicyQueries.countPolicyQuery, Long.class,
                lowerCaseSearchTerm, lowerCaseSearchTerm, lowerCaseSearchTerm, lowerCaseSearchTerm, lowerCaseSearchTerm, lowerCaseSearchTerm, lowerCaseSearchTerm, lowerCaseSearchTerm, lowerCaseSearchTerm, lowerCaseSearchTerm);
        List<EndorsementsDTO> endorsementsDTOList = jdbcTemplate.query(EndorsementPolicyQueries.getPolicyEnquiryQuery, mapper,
                lowerCaseSearchTerm, lowerCaseSearchTerm, lowerCaseSearchTerm, lowerCaseSearchTerm, lowerCaseSearchTerm, lowerCaseSearchTerm, lowerCaseSearchTerm, lowerCaseSearchTerm, lowerCaseSearchTerm, lowerCaseSearchTerm,
                request.getPageNumber(), request.getPageSize(), request.getPageNumber(), request.getPageSize());
        Page<EndorsementsDTO> page = new PageImpl<>(endorsementsDTOList, request, countPolicies);
        return new DataTablesResult(request, page);
    }

    @Override
    public DataTablesResult<PolicyTrans> findActiveEnquiryPolicies(DataTablesRequest request, String drNumber, Long clientCode, String polNo, String endorseNumber, Long agentCode, Long prodCode) throws IllegalAccessException {
        QClientDef client = QPolicyTrans.policyTrans.client;
        QAccountDef account = QPolicyTrans.policyTrans.agent;
        QProductsDef product = QPolicyTrans.policyTrans.product;
        Predicate pred = null;

        if (drNumber == null || StringUtils.isBlank(drNumber)) {
            drNumber = null;
        } else {
            drNumber = drNumber.toLowerCase();
        }

        if (polNo == null || StringUtils.isBlank(polNo)) {
            polNo = "";
        } else {
            polNo = polNo.toLowerCase();
        }

        if (endorseNumber == null || StringUtils.isBlank(endorseNumber)) {
            endorseNumber = "";
        } else {
            endorseNumber = endorseNumber.toLowerCase();
        }

        //System.out.println("polNo "+polNo);
        pred = QPolicyTrans.policyTrans.polNo.lower().containsIgnoreCase(StringUtils.lowerCase(polNo))
                .and(QPolicyTrans.policyTrans.riskTrans.any().riskShtDesc.containsIgnoreCase(endorseNumber))
                .and(QPolicyTrans.policyTrans.product.proGroup.prgType.upper().notEqualsIgnoreCase("MD"))
                .and(QPolicyTrans.policyTrans.currentStatus.in("A", "L"))
                .and((drNumber == null) ? QPolicyTrans.policyTrans.isNotNull() : QPolicyTrans.policyTrans.refNo.lower().containsIgnoreCase(StringUtils.lowerCase(drNumber)))
                .and((agentCode == null) ? account.isNotNull() : account.acctId.eq(agentCode))
                .and((prodCode == null) ? product.isNotNull() : product.proCode.eq(prodCode))
                .and((clientCode == null) ? client.isNotNull() : client.tenId.eq(clientCode));
        Page<PolicyTrans> page = policyRepo.findAll(pred, request);
        return new DataTablesResult(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<PolicyTrans> findEnquiryMedPolicies(DataTablesRequest request, String drNumber,
                                                                Long clientCode, String polNo, String endorseNumber, Long agentCode,
                                                                Long prodCode) throws IllegalAccessException {
        QClientDef client = QPolicyTrans.policyTrans.client;
        QAccountDef account = QPolicyTrans.policyTrans.agent;
        QProductsDef product = QPolicyTrans.policyTrans.product;
        Predicate pred = null;

        if (drNumber == null || StringUtils.isBlank(drNumber)) {
            drNumber = null;
        } else {
            drNumber = drNumber.toLowerCase();
        }

        if (polNo == null || StringUtils.isBlank(polNo)) {
            polNo = "";
        } else {
            polNo = polNo.toLowerCase();
        }

        if (endorseNumber == null || StringUtils.isBlank(endorseNumber)) {
            endorseNumber = "";
        } else {
            endorseNumber = endorseNumber.toLowerCase();
        }
        pred = QPolicyTrans.policyTrans.polNo.lower().containsIgnoreCase(StringUtils.lowerCase(polNo))
                // .and(QPolicyTrans.policyTrans.riskTrans.any().riskShtDesc.containsIgnoreCase(endorseNumber))
                .and(QPolicyTrans.policyTrans.product.proGroup.prgType.equalsIgnoreCase("MD"))
//                .and(QPolicyTrans.policyTrans.authStatus.notEqualsIgnoreCase("D"))
                .and((drNumber == null) ? QPolicyTrans.policyTrans.isNotNull() : QPolicyTrans.policyTrans.refNo.lower().containsIgnoreCase(StringUtils.lowerCase(drNumber)))
                .and((agentCode == null) ? account.isNotNull() : account.acctId.eq(agentCode))
                .and((prodCode == null) ? product.isNotNull() : product.proCode.eq(prodCode))
                .and((clientCode == null) ? client.isNotNull() : client.tenId.eq(clientCode));
        Page<PolicyTrans> page = policyRepo.findAll(pred, request);
        return new DataTablesResult(request, page);
    }

    @Override
    public DataTablesResult<LapsePoliciesDTO> findEnquiryActiveorLapsedMedPolicies(DataTablesRequest request, Long clientCode, String polNo, Long agentCode, Long prodCode, String riskShtDesc, String drNumber) throws IllegalAccessException {

        if (drNumber == null || StringUtils.isBlank(drNumber)) {
            drNumber = "%%";
        } else {
            drNumber = "%" + drNumber + "%";
        }

        if (polNo == null || StringUtils.isBlank(polNo)) {
            polNo = "%%";
        } else {
            polNo = "%" + polNo + "%";
        }

        if (riskShtDesc == null || StringUtils.isBlank(riskShtDesc)) {
            riskShtDesc = "%%";
        } else {
            riskShtDesc = "%" + riskShtDesc + "%";
        }

        if (clientCode == null) {
            clientCode = -2000L;
        }

        if (agentCode == null) {
            agentCode = -2000L;
        }

        if (prodCode == null) {
            prodCode = -2000L;
        }
        List<Object[]> activePolicies = policyRepo.findEnquiryActiveorLapsedMedPolicies(polNo, riskShtDesc, agentCode, prodCode, clientCode, drNumber, request.getPageNumber(), request.getPageSize());
        long rowCount = 0L;


        if (!activePolicies.isEmpty()) {
            rowCount = activePolicies.size();
        }
        final List<LapsePoliciesDTO> lapsePoliciesDTOList = new ArrayList<>();
        for (Object[] activePolicy : activePolicies) {
            LapsePoliciesDTO lapsePoliciesDTO = new LapsePoliciesDTO();
            lapsePoliciesDTO.setPolicyId(((BigInteger) activePolicy[2]).longValue());
            lapsePoliciesDTO.setPolicyNo((String) activePolicy[0]);
            lapsePoliciesDTO.setClientName((String) activePolicy[3]);
            lapsePoliciesDTO.setPolRevNo((String) activePolicy[13]);
            lapsePoliciesDTO.setProduct((String) activePolicy[8]);
            lapsePoliciesDTO.setWefDate((Date) activePolicy[9]);
            lapsePoliciesDTO.setWetDate((Date) activePolicy[10]);
            lapsePoliciesDTO.setAgentName((String) activePolicy[4]);
            lapsePoliciesDTO.setCurrency((String) activePolicy[11]);
            lapsePoliciesDTO.setUsername((String) activePolicy[12]);
            String currentStatus;
            if (Objects.equals(activePolicy[14], "A")) {
                currentStatus = "Active";
                lapsePoliciesDTO.setCurrentStatus(currentStatus);
            } else if (Objects.equals(activePolicy[14], "L")) {
                currentStatus = "Lapsed";
                lapsePoliciesDTO.setCurrentStatus(currentStatus);
            }
            lapsePoliciesDTOList.add(lapsePoliciesDTO);

        }

        Page<LapsePoliciesDTO> page = new PageImpl<>(lapsePoliciesDTOList, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<PolicyTrans> findPendingPolicies(DataTablesRequest request, String drNumber, Long clientCode, String polNo, String endorseNumber, Long agentCode, Long prodCode) throws IllegalAccessException {
        QClientDef client = QPolicyTrans.policyTrans.client;
        QAccountDef account = QPolicyTrans.policyTrans.agent;
        QProductsDef product = QPolicyTrans.policyTrans.product;
        Predicate pred = null;

        if (drNumber == null || StringUtils.isBlank(drNumber)) {
            drNumber = "";
        }

        if (polNo == null || StringUtils.isBlank(polNo)) {
            polNo = "";
        }

        if (endorseNumber == null || StringUtils.isBlank(endorseNumber)) {
            endorseNumber = "";
            pred = QPolicyTrans.policyTrans.polNo.containsIgnoreCase(polNo)
                    .and(QPolicyTrans.policyTrans.authStatus.ne("A"))
                    .and((agentCode == null) ? account.isNotNull() : account.acctId.eq(agentCode))
                    .and((prodCode == null) ? product.isNotNull() : product.proCode.eq(prodCode))
                    .and((clientCode == null) ? client.isNotNull() : client.tenId.eq(clientCode));
        } else {
            pred = QPolicyTrans.policyTrans.polNo.containsIgnoreCase(polNo)
                    .and(QPolicyTrans.policyTrans.authStatus.ne("A"))
                    .and(QPolicyTrans.policyTrans.riskTrans.any().riskShtDesc.containsIgnoreCase(endorseNumber))
                    .and(QPolicyTrans.policyTrans.refNo.containsIgnoreCase(drNumber)
                            .or(QPolicyTrans.policyTrans.refNo.isNull()))
                    .and((agentCode == null) ? account.isNotNull() : account.acctId.eq(agentCode))
                    .and((prodCode == null) ? product.isNotNull() : product.proCode.eq(prodCode))
                    .and((clientCode == null) ? client.isNotNull() : client.tenId.eq(clientCode));
        }
        Page<PolicyTrans> page = policyRepo.findAll(pred, request);
        return new DataTablesResult(request, page);
    }

    @PreAuthorize("hasAnyAuthority('SAVE_POLICY')")
    @Override
    @Transactional(readOnly = false)
    public void createTaxes(PolicyTaxBean taxBean) throws BadRequestException {
        PolicyTrans policy = policyRepo.findOne(taxBean.getPolId());
        List<PolicyTaxes> createdTaxes = new ArrayList<>();
        if (policy == null) throw new BadRequestException("No Policy Transaction");
        for (Long taxId : taxBean.getTaxes()) {
            TaxRates tax = taxRatesRepo.findOne(taxId);
            PolicyTaxes polTax = new PolicyTaxes();
            polTax.setTaxLevel(tax.getTaxLevel());
            polTax.setDivFactor(tax.getDivFactor());
            polTax.setRateType(tax.getRateType());
            polTax.setRevenueItems(tax.getRevenueItems());
            polTax.setTaxRate(tax.getTaxRate());
            polTax.setSubclass(tax.getSubclass());
            polTax.setPolicy(policy);
            createdTaxes.add(polTax);
        }
        polTaxesRepo.save(createdTaxes);
    }

    @Override
    @Transactional(readOnly = false)
    public void createIntParties(RiskIntPartiesBean partiesBean) throws BadRequestException {
        RiskTrans riskTrans = riskRepo.findOne(partiesBean.getRiskId());
        List<RiskInterestedParties> interestedParties = new ArrayList<>();
        if (riskTrans == null) throw new BadRequestException("No Risk Transaction");
        for (Long partId : partiesBean.getParties()) {
            InterestedParties parties = interestedPartiesRepo.findOne(partId);
            RiskInterestedParties riskInterestedParties = new RiskInterestedParties();
            riskInterestedParties.setInterestedParties(parties);
            riskInterestedParties.setRisk(riskTrans);
            interestedParties.add(riskInterestedParties);
        }

        riskIntPartiesRepo.save(interestedParties);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteRiskIntParty(Long partId) {
        riskIntPartiesRepo.delete(partId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PolicyTrans> findAllPolicies(String paramString, Pageable paramPageable) {
        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred = QPolicyTrans.policyTrans.isNotNull().and(QPolicyTrans.policyTrans.authStatus.eq("A"));
        } else {
            pred = (QPolicyTrans.policyTrans.authStatus.eq("A")).and(QPolicyTrans.policyTrans.polNo.containsIgnoreCase(paramString)
                    .or(QPolicyTrans.policyTrans.polRevNo.containsIgnoreCase(paramString)));
        }
        return policyRepo.findAll(pred, paramPageable);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<ScheduleTrans> findRiskSchedules(DataTablesRequest request, Long riskId) throws IllegalAccessException {
        BooleanExpression pred = QScheduleTrans.scheduleTrans.risk.riskId.eq(riskId);
        Page<ScheduleTrans> page = scheduleTransRepo.findAll(pred.and(request.searchPredicate(QScheduleTrans.scheduleTrans)), request);
        return new DataTablesResult<>(request, page);
    }

    @PreAuthorize("hasAnyAuthority('SAVE_POLICY')")
    @Override
    @Transactional(readOnly = false)
    public void createRiskSchedules(VehicleDetails scheduleTrans) throws BadRequestException {
        if (scheduleTrans.getRiskId() == null) {
            throw new BadRequestException("Please Select Risk ID to continue....");
        }
        final RiskTrans riskTrans = riskRepo.findOne(scheduleTrans.getRiskId());
        if (riskTrans == null) {
            throw new BadRequestException("Please Select Risk ID to continue....");
        }
        MotorVehicleDetails vehicleDetails = new MotorVehicleDetails();
        vehicleDetails.setRisk(riskTrans);
        vehicleDetails.setVdId(scheduleTrans.getVdId());
        vehicleDetails.setBodyColor(scheduleTrans.getBodyColor());
        vehicleDetails.setBodyType(scheduleTrans.getBodyType());
        vehicleDetails.setCarMake(scheduleTrans.getCarMake());
        vehicleDetails.setCarModel(scheduleTrans.getCarModel());
        vehicleDetails.setCarryCapacity(scheduleTrans.getCarryCapacity());
        vehicleDetails.setEngineNumber(scheduleTrans.getEngineNumber());
        vehicleDetails.setChassisNumber(scheduleTrans.getChassisNo());
        vehicleDetails.setYearOfManufacture(Long.valueOf(scheduleTrans.getYearOfManufacture()));
        vehicleDetails.setEngineCapacity(scheduleTrans.getEngineCapacity());
        motorVehicleDetailsRepo.save(vehicleDetails);
    }

    @Override
    public Map<String, Object> getRiskSchedules(Long riskId) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String scheduleTable = riskRepo.getScheduleTable(riskId);
        if (scheduleTable == null)
            return new HashMap<>();
        List<Object[]> rawColumns = riskRepo.getTableModel(scheduleTable);

        RegisteredTableModel registeredTableModel = registeredTableModelRepo.findByTableName(scheduleTable);
        TableForm tableForm = new TableForm();
        tableForm.setDatatableName(scheduleTable);
        tableForm.setApptableName(registeredTableModel.getAppTableName());
        tableForm.setAppTableNameKey(registeredTableModel.getKeyValue());

        for (Object[] columnDetails : rawColumns) {
            String columnName = (String) columnDetails[0];
            String dataType = (String) columnDetails[1];
            Integer columnLength = (columnDetails[2] != null) ? (Integer) columnDetails[2] : null;
            String isNullable = (String) columnDetails[3];
            ColumnForm columnForm = new ColumnForm();
            if (Objects.equals(isNullable, "NO")) {
                columnForm.setMandatory("YES");
            } else {
                columnForm.setMandatory("NO");
            }
            columnForm.setName(columnName);
            columnForm.setType(dataType);
            columnForm.setLength(columnLength);
            tableForm.getColumnFormList().add(columnForm);
        }
        Map<String, Object> schedules = new HashMap<>();
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("select ");
        for (ColumnForm cf : tableForm.getColumnFormList()) {
            stringBuilder.append("\"").append(cf.getName()).append("\",");
        }

        stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
        stringBuilder.append(" from " + "\"").append(scheduleTable).append("\"");
        stringBuilder.append(" where s_brk_risks_id = ?");
        System.out.println("QUERY" + stringBuilder + " RISK ID " + riskId);
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(stringBuilder.toString(), riskId);
        System.out.println(Collections.singletonList(rows));
        schedules.put("tableForm", tableForm);
        schedules.put("data", rows);
        return schedules;
    }

    @Override
    @Transactional
    public void insertOrUpdateDataIntoCustomTable(String tableName, Map<String, Object> data) throws BadRequestException {
        if (data == null || data.isEmpty()) {
            throw new BadRequestException("Data to insert cannot be empty.");
        }
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String sequenceName = "pri_code_seq";
        createSequenceIfNotExists(jdbcTemplate, sequenceName);

        Object priCode = data.get("pri_code");
        boolean isUpdate = false;
        if (priCode != null) {
            String checkRecordExistsQuery = "SELECT COUNT(*) FROM \"" + tableName + "\" WHERE \"pri_code\" = ?";
            int count = jdbcTemplate.queryForObject(checkRecordExistsQuery, new Object[]{priCode}, Integer.class);
            isUpdate = count > 0;
        }

        data.remove("pri_code");

        List<String> columns = new ArrayList<>(data.keySet());
        List<Object> values = new ArrayList<>(data.values());

        List<String> quotedColumns = new ArrayList<>();
        for (String column : columns) {
            quotedColumns.add("\"" + column + "\"");
        }

        if (isUpdate) {
            StringBuilder updateSqlBuilder = new StringBuilder();
            updateSqlBuilder.append("UPDATE \"").append(tableName).append("\" SET ");

            List<String> setClauses = new ArrayList<>();
            for (String quotedColumn : quotedColumns) {
                setClauses.add(quotedColumn + " = ?");
            }
            updateSqlBuilder.append(String.join(", ", setClauses));
            updateSqlBuilder.append(" WHERE \"pri_code\" = ?");

            values.add(priCode);

            jdbcTemplate.update(updateSqlBuilder.toString(), values.toArray());
            System.out.println("UPDATE QUERY: " + updateSqlBuilder);
        } else {

            quotedColumns.add("\"pri_code\"");

            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("INSERT INTO \"" + tableName + "\" (");
            sqlBuilder.append(String.join(", ", quotedColumns));
            sqlBuilder.append(") VALUES (");
            sqlBuilder.append(String.join(", ", Collections.nCopies(values.size(), "?")));
            sqlBuilder.append(", nextval('").append(sequenceName).append("'))");
            jdbcTemplate.update(sqlBuilder.toString(), values.toArray());
        }
    }

    private void createSequenceIfNotExists(JdbcTemplate jdbcTemplate, String sequenceName) {
        String checkSequenceQuery = "SELECT count(*) FROM information_schema.sequences WHERE sequence_name = ?";
        Integer count = jdbcTemplate.queryForObject(checkSequenceQuery, new Object[]{sequenceName}, Integer.class);

        if (count == null || count == 0) {
            String createSequenceSQL = "CREATE SEQUENCE " + sequenceName + " START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1";
            jdbcTemplate.execute(createSequenceSQL);
        }
    }

    @Override
    @Transactional
    public void deleteScheduleData(Integer priCode, String tableName) throws BadRequestException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String deleteSql = String.format("DELETE FROM \"%s\" WHERE pri_code = ?", tableName);
        System.out.println("DELETE QUERY: " + deleteSql);
        jdbcTemplate.update(deleteSql, priCode);

    }


    @PreAuthorize("hasAnyAuthority('SAVE_POLICY')")
    @Override
    @Transactional(readOnly = false)
    public void deleteRiskSchedule(Long scheduleId) {
        scheduleTransRepo.delete(scheduleId);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void populateRiskScheduleDetails(Long riskId) throws BadRequestException {
        RiskTrans riskTrans = riskRepo.findOne(riskId);
        Long subId = riskTrans.getSubclass().getSubId();
        Long colCount = mappingRepo.count(QScheduleMapping.scheduleMapping.subclass.subId.eq(subId));
        if (colCount == 0) return;
        Iterable<ScheduleMapping> mappings = mappingRepo.findAll(QScheduleMapping.scheduleMapping.subclass.subId.eq(subId));
        Long count = scheduleTransRepo.count(QScheduleTrans.scheduleTrans.risk.riskId.eq(riskId));
        ScheduleTrans scheduleTrans = null;
        if (count > 1) return;
        else if (count == 1) {
            scheduleTrans = scheduleTransRepo.findOne(QScheduleTrans.scheduleTrans.risk.riskId.eq(riskId));
        } else if (count == 0)
            scheduleTrans = new ScheduleTrans();
        scheduleTrans.setRisk(riskTrans);

        boolean createRecord = false;
        for (ScheduleMapping mapping : mappings) {
            if (mapping.getColumnIndex().equalsIgnoreCase("1")) {
                if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskId")) {
                    createRecord = true;
                    scheduleTrans.setColumn1(riskTrans.getRiskShtDesc());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskDesc")) {
                    createRecord = true;
                    scheduleTrans.setColumn1(riskTrans.getRiskDesc());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskDesc")) {
                    createRecord = true;
                    scheduleTrans.setColumn1(riskTrans.getRiskDesc());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskPrem")) {
                    createRecord = true;
                    scheduleTrans.setColumn1(riskTrans.getPremium() == null ? "0" : riskTrans.getPremium().toString());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskValue")) {
                    createRecord = true;
                    scheduleTrans.setColumn1(riskTrans.getSumInsured() == null ? "0" : riskTrans.getSumInsured().toString());
                }
                if (mapping.getMappedSections() != null) {
                    createRecord = true;
                    Stream<SectionTrans> sections = Streamable.streamOf(sectionRepo.findAll(QSectionTrans.sectionTrans.risk.riskId.eq(riskId)));
                    Optional<SectionTrans> sectionTrans = sections.filter(a -> a.getSection().getId() == mapping.getMappedSections().getId()).findFirst();
                    if (sectionTrans.isPresent())
                        scheduleTrans.setColumn1(String.valueOf(sectionTrans.get().getAmount()));
                }
            } else if (mapping.getColumnIndex().equalsIgnoreCase("2")) {
                if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskId")) {
                    createRecord = true;
                    scheduleTrans.setColumn2(riskTrans.getRiskShtDesc());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskDesc")) {
                    createRecord = true;
                    scheduleTrans.setColumn2(riskTrans.getRiskDesc());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskDesc")) {
                    createRecord = true;
                    scheduleTrans.setColumn2(riskTrans.getRiskDesc());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskPrem")) {
                    createRecord = true;
                    scheduleTrans.setColumn2(riskTrans.getPremium() == null ? "0" : riskTrans.getPremium().toString());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskValue")) {
                    createRecord = true;
                    scheduleTrans.setColumn2(riskTrans.getSumInsured() == null ? "0" : riskTrans.getSumInsured().toString());
                }
                if (mapping.getMappedSections() != null) {
                    createRecord = true;
                    Stream<SectionTrans> sections = Streamable.streamOf(sectionRepo.findAll(QSectionTrans.sectionTrans.risk.riskId.eq(riskId)));
                    Optional<SectionTrans> sectionTrans = sections.filter(a -> a.getSection().getId() == mapping.getMappedSections().getId()).findFirst();
                    if (sectionTrans.isPresent())
                        scheduleTrans.setColumn2(String.valueOf(sectionTrans.get().getAmount()));
                }
            } else if (mapping.getColumnIndex().equalsIgnoreCase("3")) {
                if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskId")) {
                    createRecord = true;
                    scheduleTrans.setColumn3(riskTrans.getRiskShtDesc());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskDesc")) {
                    createRecord = true;
                    scheduleTrans.setColumn3(riskTrans.getRiskDesc());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskDesc")) {
                    createRecord = true;
                    scheduleTrans.setColumn3(riskTrans.getRiskDesc());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskPrem")) {
                    createRecord = true;
                    scheduleTrans.setColumn3(riskTrans.getPremium() == null ? "0" : riskTrans.getPremium().toString());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskValue")) {
                    createRecord = true;
                    scheduleTrans.setColumn3(riskTrans.getSumInsured() == null ? "0" : riskTrans.getSumInsured().toString());
                }
                if (mapping.getMappedSections() != null) {
                    createRecord = true;
                    Stream<SectionTrans> sections = Streamable.streamOf(sectionRepo.findAll(QSectionTrans.sectionTrans.risk.riskId.eq(riskId)));
                    Optional<SectionTrans> sectionTrans = sections.filter(a -> a.getSection().getId() == mapping.getMappedSections().getId()).findFirst();
                    if (sectionTrans.isPresent())
                        scheduleTrans.setColumn3(String.valueOf(sectionTrans.get().getAmount()));
                }
            } else if (mapping.getColumnIndex().equalsIgnoreCase("4")) {
                if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskId")) {
                    createRecord = true;
                    scheduleTrans.setColumn4(riskTrans.getRiskShtDesc());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskDesc")) {
                    createRecord = true;
                    scheduleTrans.setColumn4(riskTrans.getRiskDesc());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskDesc")) {
                    createRecord = true;
                    scheduleTrans.setColumn4(riskTrans.getRiskDesc());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskPrem")) {
                    createRecord = true;
                    scheduleTrans.setColumn4(riskTrans.getPremium() == null ? "0" : riskTrans.getPremium().toString());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskValue")) {
                    createRecord = true;
                    scheduleTrans.setColumn4(riskTrans.getSumInsured() == null ? "0" : riskTrans.getSumInsured().toString());
                }
                if (mapping.getMappedSections() != null) {
                    createRecord = true;
                    Stream<SectionTrans> sections = Streamable.streamOf(sectionRepo.findAll(QSectionTrans.sectionTrans.risk.riskId.eq(riskId)));
                    Optional<SectionTrans> sectionTrans = sections.filter(a -> a.getSection().getId() == mapping.getMappedSections().getId()).findFirst();
                    if (sectionTrans.isPresent())
                        scheduleTrans.setColumn4(String.valueOf(sectionTrans.get().getAmount()));
                }
            } else if (mapping.getColumnIndex().equalsIgnoreCase("5")) {
                if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskId")) {
                    createRecord = true;
                    scheduleTrans.setColumn5(riskTrans.getRiskShtDesc());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskDesc")) {
                    createRecord = true;
                    scheduleTrans.setColumn5(riskTrans.getRiskDesc());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskDesc")) {
                    createRecord = true;
                    scheduleTrans.setColumn5(riskTrans.getRiskDesc());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskPrem")) {
                    createRecord = true;
                    scheduleTrans.setColumn5(riskTrans.getPremium() == null ? "0" : riskTrans.getPremium().toString());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskValue")) {
                    createRecord = true;
                    scheduleTrans.setColumn5(riskTrans.getSumInsured() == null ? "0" : riskTrans.getSumInsured().toString());
                }
                if (mapping.getMappedSections() != null) {
                    createRecord = true;
                    Stream<SectionTrans> sections = Streamable.streamOf(sectionRepo.findAll(QSectionTrans.sectionTrans.risk.riskId.eq(riskId)));
                    Optional<SectionTrans> sectionTrans = sections.filter(a -> a.getSection().getId() == mapping.getMappedSections().getId()).findFirst();
                    if (sectionTrans.isPresent())
                        scheduleTrans.setColumn5(String.valueOf(sectionTrans.get().getAmount()));
                }
            } else if (mapping.getColumnIndex().equalsIgnoreCase("6")) {
                if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskId")) {
                    createRecord = true;
                    scheduleTrans.setColumn6(riskTrans.getRiskShtDesc());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskDesc")) {
                    createRecord = true;
                    scheduleTrans.setColumn6(riskTrans.getRiskDesc());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskDesc")) {
                    createRecord = true;
                    scheduleTrans.setColumn6(riskTrans.getRiskDesc());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskPrem")) {
                    createRecord = true;
                    scheduleTrans.setColumn6(riskTrans.getPremium() == null ? "0" : riskTrans.getPremium().toString());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskValue")) {
                    createRecord = true;
                    scheduleTrans.setColumn6(riskTrans.getSumInsured() == null ? "0" : riskTrans.getSumInsured().toString());
                }
                if (mapping.getMappedSections() != null) {
                    createRecord = true;
                    Stream<SectionTrans> sections = Streamable.streamOf(sectionRepo.findAll(QSectionTrans.sectionTrans.risk.riskId.eq(riskId)));
                    Optional<SectionTrans> sectionTrans = sections.filter(a -> a.getSection().getId() == mapping.getMappedSections().getId()).findFirst();
                    if (sectionTrans.isPresent())
                        scheduleTrans.setColumn6(String.valueOf(sectionTrans.get().getAmount()));
                }
            } else if (mapping.getColumnIndex().equalsIgnoreCase("7")) {
                if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskId")) {
                    createRecord = true;
                    scheduleTrans.setColumn7(riskTrans.getRiskShtDesc());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskDesc")) {
                    createRecord = true;
                    scheduleTrans.setColumn7(riskTrans.getRiskDesc());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskDesc")) {
                    createRecord = true;
                    scheduleTrans.setColumn7(riskTrans.getRiskDesc());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskPrem")) {
                    createRecord = true;
                    scheduleTrans.setColumn7(riskTrans.getPremium() == null ? "0" : riskTrans.getPremium().toString());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskValue")) {
                    createRecord = true;
                    scheduleTrans.setColumn7(riskTrans.getSumInsured() == null ? "0" : riskTrans.getSumInsured().toString());
                }
                if (mapping.getMappedSections() != null) {
                    createRecord = true;
                    Stream<SectionTrans> sections = Streamable.streamOf(sectionRepo.findAll(QSectionTrans.sectionTrans.risk.riskId.eq(riskId)));
                    Optional<SectionTrans> sectionTrans = sections.filter(a -> a.getSection().getId() == mapping.getMappedSections().getId()).findFirst();
                    if (sectionTrans.isPresent())
                        scheduleTrans.setColumn7(String.valueOf(sectionTrans.get().getAmount()));
                }
            } else if (mapping.getColumnIndex().equalsIgnoreCase("8")) {
                if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskId")) {
                    createRecord = true;
                    scheduleTrans.setColumn8(riskTrans.getRiskShtDesc());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskDesc")) {
                    createRecord = true;
                    scheduleTrans.setColumn8(riskTrans.getRiskDesc());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskDesc")) {
                    createRecord = true;
                    scheduleTrans.setColumn8(riskTrans.getRiskDesc());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskPrem")) {
                    createRecord = true;
                    scheduleTrans.setColumn8(riskTrans.getPremium() == null ? "0" : riskTrans.getPremium().toString());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskValue")) {
                    createRecord = true;
                    scheduleTrans.setColumn8(riskTrans.getSumInsured() == null ? "0" : riskTrans.getSumInsured().toString());
                }
                if (mapping.getMappedSections() != null) {
                    createRecord = true;
                    Stream<SectionTrans> sections = Streamable.streamOf(sectionRepo.findAll(QSectionTrans.sectionTrans.risk.riskId.eq(riskId)));
                    Optional<SectionTrans> sectionTrans = sections.filter(a -> a.getSection().getId() == mapping.getMappedSections().getId()).findFirst();
                    if (sectionTrans.isPresent())
                        scheduleTrans.setColumn8(String.valueOf(sectionTrans.get().getAmount()));
                }
            } else if (mapping.getColumnIndex().equalsIgnoreCase("9")) {
                if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskId")) {
                    createRecord = true;
                    scheduleTrans.setColumn9(riskTrans.getRiskShtDesc());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskDesc")) {
                    createRecord = true;
                    scheduleTrans.setColumn9(riskTrans.getRiskDesc());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskDesc")) {
                    createRecord = true;
                    scheduleTrans.setColumn9(riskTrans.getRiskDesc());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskPrem")) {
                    createRecord = true;
                    scheduleTrans.setColumn9(riskTrans.getPremium() == null ? "0" : riskTrans.getPremium().toString());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskValue")) {
                    createRecord = true;
                    scheduleTrans.setColumn9(riskTrans.getSumInsured() == null ? "0" : riskTrans.getSumInsured().toString());
                }
                if (mapping.getMappedSections() != null) {
                    createRecord = true;
                    Stream<SectionTrans> sections = Streamable.streamOf(sectionRepo.findAll(QSectionTrans.sectionTrans.risk.riskId.eq(riskId)));
                    Optional<SectionTrans> sectionTrans = sections.filter(a -> a.getSection().getId() == mapping.getMappedSections().getId()).findFirst();
                    if (sectionTrans.isPresent())
                        scheduleTrans.setColumn9(String.valueOf(sectionTrans.get().getAmount()));
                }
            } else if (mapping.getColumnIndex().equalsIgnoreCase("10")) {
                if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskId")) {
                    createRecord = true;
                    scheduleTrans.setColumn10(riskTrans.getRiskShtDesc());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskDesc")) {
                    createRecord = true;
                    scheduleTrans.setColumn10(riskTrans.getRiskDesc());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskDesc")) {
                    createRecord = true;
                    scheduleTrans.setColumn10(riskTrans.getRiskDesc());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskPrem")) {
                    createRecord = true;
                    scheduleTrans.setColumn10(riskTrans.getPremium() == null ? "0" : riskTrans.getPremium().toString());
                } else if (mapping.getMappedRiskColumn() != null && mapping.getMappedRiskColumn().equalsIgnoreCase("riskValue")) {
                    createRecord = true;
                    scheduleTrans.setColumn10(riskTrans.getSumInsured() == null ? "0" : riskTrans.getSumInsured().toString());
                }
                if (mapping.getMappedSections() != null) {
                    createRecord = true;
                    Stream<SectionTrans> sections = Streamable.streamOf(sectionRepo.findAll(QSectionTrans.sectionTrans.risk.riskId.eq(riskId)));
                    Optional<SectionTrans> sectionTrans = sections.filter(a -> a.getSection().getId() == mapping.getMappedSections().getId()).findFirst();
                    if (sectionTrans.isPresent())
                        scheduleTrans.setColumn10(String.valueOf(sectionTrans.get().getAmount()));
                }
            }
        }
        if (createRecord)
            scheduleTransRepo.save(scheduleTrans);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getCommissionRate(Long binId) throws BadRequestException {
        Iterable<CommissionRates> commissionRates = commRatesRepo.findAll(QCommissionRates.commissionRates.bindersDef.binId.eq(binId)
                .and(QCommissionRates.commissionRates.applicableAt.isNull().or(QCommissionRates.commissionRates.applicableAt.eq("NB"))));
        BigDecimal commRate = BigDecimal.ZERO;
        for (CommissionRates commissionRate : commissionRates) {
            commRate = commissionRate.getCommRate();
            break;
        }
        if (commRate.compareTo(BigDecimal.ZERO) == 0) {
            BindersDef details = binderRepo.findOne(binId);
            commRate = details.getAccount().getAccountType().getCommRate();
        }
        return commRate;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getSubAgentCommissionRate(Long bindId, Long accId) throws BadRequestException {
        BigDecimal commSubAgentRate = BigDecimal.ZERO;
        Iterable<SubAgentCommissionRates> subAgentCommissionRates = subAgentCommRepo.findAll(QSubAgentCommissionRates.subAgentCommissionRates.bindersDef.binId.eq(bindId)
                .and(QSubAgentCommissionRates.subAgentCommissionRates.accountTypes.accountType.eq(AccountTypeEnum.SUB)));
        for (SubAgentCommissionRates subAgentCommissionRate : subAgentCommissionRates) {
            commSubAgentRate = subAgentCommissionRate.getCommRate();
            break;
        }
        if (commSubAgentRate.compareTo(BigDecimal.ZERO) == 0) {
            if (accId != null) {
                AccountTypes accountTypes = accountTypeRepo.findOne(QAccountTypes.accountTypes.accId.eq(accId));
                commSubAgentRate = accountTypes.getCommRate();
            }
        }
        return commSubAgentRate;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getMarketerCommissionRate(Long bindId, Long accId) throws BadRequestException {
        BigDecimal commSubAgentRate = BigDecimal.ZERO;
        Iterable<SubAgentCommissionRates> subAgentCommissionRates = subAgentCommRepo.findAll(QSubAgentCommissionRates.subAgentCommissionRates.bindersDef.binId.eq(bindId)
                .and(QSubAgentCommissionRates.subAgentCommissionRates.accountTypes.accountType.eq(AccountTypeEnum.MRK)));
        for (SubAgentCommissionRates subAgentCommissionRate : subAgentCommissionRates) {
            commSubAgentRate = subAgentCommissionRate.getCommRate();
            break;
        }
        if (commSubAgentRate.compareTo(BigDecimal.ZERO) == 0) {
            if (accId != null) {
                AccountTypes accountTypes = accountTypeRepo.findOne(QAccountTypes.accountTypes.accId.eq(accId));
                commSubAgentRate = accountTypes.getCommRate();
            }
        }
        return commSubAgentRate;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {AdminFeeException.class})
    public Long createAdminFeeTrans(AdminFeeForm adminFeeForm) throws AdminFeeException {
        AdminFee adminFee = new AdminFee();
        adminFee.setApplicableAt("P");
        adminFee.setPreparedBy(userUtils.getCurrentUser());
        adminFee.setClientDef(clientRepo.findOne(adminFeeForm.getClientId()));
        adminFee.setCurrencies(currencyRepo.findOne(adminFeeForm.getCurrencyId()));
        adminFee.setBranch(branchRepo.findOne(adminFeeForm.getBrnCode()));
        adminFee.setRemarks(adminFeeForm.getRemarks());
        adminFee.setAsAtDate(adminFeeForm.getProcessDate());
        Predicate adminPredicate = QSystemSequence.systemSequence.transType.eq("AD");
        if (sequenceRepo.count(adminPredicate) == 0)
            throw new AdminFeeException("Sequence for Admin Fee Transactions has not been defined");
        SystemSequence adminSequence = sequenceRepo.findOne(adminPredicate);
        Long adminSequenceNextNumber = adminSequence.getNextNumber();
        final String refNo = adminSequence.getSeqPrefix() + String.format("%05d", adminSequenceNextNumber);
        adminFee.setRefNo(refNo);
        adminSequence.setLastNumber(adminSequenceNextNumber);
        adminSequence.setNextNumber(adminSequenceNextNumber + 1);
        adminFee.setAuthorised("N");
        adminFee.setPreparedDate(new Date());
        sequenceRepo.save(adminSequence);
        AdminFee savedAdminFee = adminFeeRepo.save(adminFee);
        return savedAdminFee.getAdminFeeId();
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<AdminFee> findUnauthTrans(DataTablesRequest request) throws IllegalAccessException {
        BooleanExpression pred = (QAdminFee.adminFee.authorised.eq("N"));
        Page<AdminFee> page = adminFeeRepo.findAll(pred.and(request.searchPredicate(QAdminFee.adminFee)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<AdminFee> findAuthorisedTrans(DataTablesRequest request) throws IllegalAccessException {
        BooleanExpression pred = (QAdminFee.adminFee.authorised.eq("Y"));
        Page<AdminFee> page = adminFeeRepo.findAll(pred.and(request.searchPredicate(QAdminFee.adminFee)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminFee getAdminFeeDetails(Long adminFeeId) {
        return adminFeeRepo.findOne(adminFeeId);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<AdminFeePolicies> findAdminFeePolicies(DataTablesRequest request, Long adminFeeId) throws IllegalAccessException {
        BooleanExpression pred = QAdminFeePolicies.adminFeePolicies.adminFee.adminFeeId.eq(adminFeeId);
        Page<AdminFeePolicies> page = adminFeePolRepo.findAll(pred.and(request.searchPredicate(QAdminFeePolicies.adminFeePolicies)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void addAdminFeePolicies(AdminFeePolicyBean feePolicyBean) throws BadRequestException {
        if (feePolicyBean.getAdminFeeId() == null)
            throw new BadRequestException("Admin Fee Transaction required");

        if (feePolicyBean.getPolicies().size() == 0)
            throw new BadRequestException("No Policies to Add");

        BigDecimal vatOnExciseDutyRate = paramService.getParamValue("VAT_ON_EXCISE_DUTY");
        BigDecimal exciseDutyRate = paramService.getParamValue("EXCISE_DUTY_RATE");
        AdminFee adminFee = adminFeeRepo.findOne(feePolicyBean.getAdminFeeId());

        BigDecimal totalAdminFee = BigDecimal.ZERO;
        BigDecimal totalVatAmt = BigDecimal.ZERO;
        BigDecimal totalExciseDuty = BigDecimal.ZERO;
        BigDecimal totalVatOnExciseDuty = BigDecimal.ZERO;
        List<AdminFeePolicies> adminFeePoliciesList = new ArrayList<>();
        Iterable<AdminFeePolicies> savedAdminFeePolicies = adminFeePolRepo.findAll(QAdminFeePolicies.adminFeePolicies.adminFee.adminFeeId.eq(feePolicyBean.getAdminFeeId()));
        for (AdminFeePolicies adminFeePolicy : savedAdminFeePolicies) {
            PolicyTrans policyTrans = adminFeePolicy.getPolicy();
            Iterable<SelfFundParams> selfFundParams = selfFundParamsRepo.findAll(QSelfFundParams.selfFundParams.policyTrans.policyId.eq(policyTrans.getPolicyId()));
            if (selfFundParams.spliterator().getExactSizeIfKnown() == 1) {
                BigDecimal vatTotal = BigDecimal.ZERO;
                SelfFundParams selfFundParam = null;
                for (SelfFundParams fundParams : selfFundParams) {
                    selfFundParam = fundParams;
                    break;
                }
                if ("FFS".equalsIgnoreCase(selfFundParam.getApplicableLevel())) {
                    totalAdminFee = totalAdminFee.add(selfFundParam.getApplicableValue());
                    vatTotal = selfFundParam.getApplicableValue();
                } else if ("FFP".equalsIgnoreCase(selfFundParam.getApplicableLevel())) {
                    Long memberCount = membersRepo.count(QCategoryMembers.categoryMembers.category.policy.policyId.eq(policyTrans.getPolicyId()));
                    totalAdminFee = totalAdminFee.add(selfFundParam.getApplicableValue().multiply(BigDecimal.valueOf(memberCount)));
                    vatTotal = selfFundParam.getApplicableValue().multiply(BigDecimal.valueOf(memberCount));
                } else if ("FFF".equalsIgnoreCase(selfFundParam.getApplicableLevel())) {
                    Long familyCount = membersRepo.count(QCategoryMembers.categoryMembers.category.policy.policyId.eq(policyTrans.getPolicyId())
                            .and(QCategoryMembers.categoryMembers.dependentTypes.eq("P")));
                    totalAdminFee = totalAdminFee.add(selfFundParam.getApplicableValue().multiply(BigDecimal.valueOf(familyCount)));
                    vatTotal = selfFundParam.getApplicableValue().multiply(BigDecimal.valueOf(familyCount));
                }

                totalVatAmt = totalVatAmt.add(policyTrans.getAgent().getAccountType().getVatRate().multiply(vatTotal).divide(BigDecimal.valueOf(100)));
                totalExciseDuty = totalExciseDuty.add(exciseDutyRate.multiply(vatTotal).divide(BigDecimal.valueOf(100)));
            }
        }
        for (long policyId : feePolicyBean.getPolicies()) {
            PolicyTrans policyTrans = policyRepo.findOne(policyId);
            Calendar wetDate = Calendar.getInstance();
            wetDate.setTime(policyTrans.getWetDate());

            Calendar currentDate = Calendar.getInstance();
            currentDate.setTime(new Date());
            if (policyTrans.getWetDate().before(new Date())) {
                if (wetDate.get(Calendar.MONTH) < currentDate.get(Calendar.MONTH)) {
                    continue;
                }

            }
            if(!policyTrans.getBinder().isActive()){
                throw new BadRequestException("The policy contract is not active. Please authorise the contract to continue...");
            }
            long count = adminFeeSetUpRepo.count(QAdminFeeSetUp.adminFeeSetUp.binder.binId.eq(policyTrans.getBinder().getBinId()).and(QAdminFeeSetUp.adminFeeSetUp.status.eq("Active")));

            if(count!=1){
                throw new BadRequestException("Please configure set up for admin fee to continue...");
            }

            AdminFeeSetUp adminFeeSetUp = adminFeeSetUpRepo.findOne(QAdminFeeSetUp.adminFeeSetUp.binder.binId.eq(policyTrans.getBinder().getBinId()).and(QAdminFeeSetUp.adminFeeSetUp.status.eq("Active")));

            final double rate = (adminFeeSetUp.getAdminFeeRateType().equals("Percent"))?100:1;
            BigDecimal adminFeeTotal = BigDecimal.ZERO;
            if(adminFeeSetUp.getAdminFeeRateType().equals("Percent")){
                adminFeeTotal = BigDecimal.valueOf((adminFeeSetUp.getAdminFeeRate().doubleValue()/rate)*policyTrans.getPremium().doubleValue());
                totalAdminFee = totalAdminFee.add(BigDecimal.valueOf((adminFeeSetUp.getAdminFeeRate().doubleValue()/rate)*policyTrans.getPremium().doubleValue()));
            }
            else{
                adminFeeTotal = adminFeeSetUp.getAdminFeeRate();
                totalAdminFee = totalAdminFee.add(adminFeeSetUp.getAdminFeeRate());
            }
//            if (selfFundParams.spliterator().getExactSizeIfKnown() == 1) {
            final double vatrate = (adminFeeSetUp.getVateRateType().equals("Percent"))?100:1;
            BigDecimal vatTotal =BigDecimal.valueOf((adminFeeSetUp.getVatRate().doubleValue()/vatrate)*totalAdminFee.doubleValue());
            if((!adminFeeSetUp.getVateRateType().equals("Percent"))){
                vatTotal = adminFeeSetUp.getVatRate();
            }

            totalVatAmt = totalVatAmt.add(vatTotal);


            SelfFundParams selfFundParam = null;
//                for (SelfFundParams fundParams : selfFundParams) {
//                    selfFundParam = fundParams;
//                    break;
//                }
//            if("M".equalsIgnoreCase(selfFundParam.getBillingFrequency())){
//               //to be implemented
//            }

//            adminFeeTotal = BigDecimal.valueOf(0);
//            vatTotal = BigDecimal.valueOf(0);
//                if ("FFS".equalsIgnoreCase(selfFundParam.getApplicableLevel())) {
//                    totalAdminFee = totalAdminFee.add(selfFundParam.getApplicableValue());
//                    adminFeeTotal = selfFundParam.getApplicableValue();
//                    vatTotal = selfFundParam.getApplicableValue();
//                } else if ("FFP".equalsIgnoreCase(selfFundParam.getApplicableLevel())) {
//                    Long memberCount = membersRepo.count(QCategoryMembers.categoryMembers.category.policy.policyId.eq(policyId));
//                    totalAdminFee = totalAdminFee.add(selfFundParam.getApplicableValue().multiply(BigDecimal.valueOf(memberCount)));
//                    adminFeeTotal = selfFundParam.getApplicableValue().multiply(BigDecimal.valueOf(memberCount));
//                    vatTotal = selfFundParam.getApplicableValue().multiply(BigDecimal.valueOf(memberCount));
//                } else if ("FFF".equalsIgnoreCase(selfFundParam.getApplicableLevel())) {
//                    Long familyCount = membersRepo.count(QCategoryMembers.categoryMembers.category.policy.policyId.eq(policyId)
//                            .and(QCategoryMembers.categoryMembers.dependentTypes.eq("P")));
//                    totalAdminFee = totalAdminFee.add(selfFundParam.getApplicableValue().multiply(BigDecimal.valueOf(familyCount)));
//                    adminFeeTotal = selfFundParam.getApplicableValue().multiply(BigDecimal.valueOf(familyCount));
//                    vatTotal = selfFundParam.getApplicableValue().multiply(BigDecimal.valueOf(familyCount));
//                }

//            totalVatAmt = totalVatAmt.add(adminFeeSetUp.getVatRate().multiply(vatTotal).divide(BigDecimal.valueOf(100)));
            BigDecimal exciseDuty = adminFeeSetUp.getExciseRate().multiply(vatTotal).divide(BigDecimal.valueOf(100));
            totalExciseDuty = totalExciseDuty.add(adminFeeSetUp.getExciseRate().multiply(vatTotal).divide(BigDecimal.valueOf(100)));

            if(adminFeeSetUp.getExciseRateType().equalsIgnoreCase("Percent")){
                totalExciseDuty = totalExciseDuty.add(adminFeeSetUp.getExciseRate());
                exciseDuty  = adminFeeSetUp.getExciseRate();
            }

            AdminFeePolicies adminFeePolicies = new AdminFeePolicies();
            adminFeePolicies.setPolicy(policyTrans);
            adminFeePolicies.setAdminFee(adminFeeRepo.findOne(feePolicyBean.getAdminFeeId()));
            adminFeePolicies.setProcessedDate(new Date());
            adminFeePolicies.setAdminFeeAmt(adminFeeTotal);
            adminFeePolicies.setVatAmt(vatTotal);
            adminFeePolicies.setExciseDuty(exciseDuty);
            adminFeePolicies.setVatExciseDuty(null);
            adminFeePolicies.setAdminNetAmt(adminFeeTotal.add(vatTotal).add(exciseDuty));
            if (!checkDuplicate(adminFeePoliciesList, adminFeePolicies))
                adminFeePoliciesList.add(adminFeePolicies);

        }
//        AdminFeeSetUp adminFeeSetUp = adminFeeSetUpRepo.findOne(QAdminFeeSetUp.adminFeeSetUp.binder.binId.eq(policyTrans.getBinder().getBinId()).and(QAdminFeeSetUp.adminFeeSetUp.status.eq("Active")));

//        }
        adminFeePolRepo.save(adminFeePoliciesList);
        adminFee.setAdminFeeAmt(totalAdminFee);
        adminFee.setVatAmt(totalVatAmt);
        adminFee.setExciseDuty(totalExciseDuty);
//        adminFee.setExciseDutyRate(adminFeeSetUp.getExciseRate());
//        adminFee.setVatExciseDutyRate(vatOnExciseDutyRate);
//        adminFee.setVatExciseDuty(vatOnExciseDutyRate.multiply(totalExciseDuty).divide(BigDecimal.valueOf(100)));
        adminFee.setAdminNetAmt(totalAdminFee.add(totalVatAmt).add(totalExciseDuty));
        adminFeeRepo.save(adminFee);

    }

    private boolean checkDuplicate(List<AdminFeePolicies> adminFeePolicies, AdminFeePolicies policyToCheck) {
        return adminFeePolicies.stream().map(a -> a.getPolicy().getPolicyId()).filter(a -> a == policyToCheck.getPolicy().getPolicyId()).count() > 0;
    }


    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getAdminFeePolicies(Long clientId, Long adminFeeId) throws IllegalAccessException {
        return adminFeePolRepo.getAdminFeePolicies(clientId, adminFeeId);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void authorizeAdminFee(Long adminFeeId) throws BadRequestException {
        AdminFee adminFee = adminFeeRepo.findOne(adminFeeId);
        if ("Y".equalsIgnoreCase(adminFee.getAuthorised())) {
            throw new BadRequestException("Transaction already authorised");
        }

        if (adminFeePolRepo.count(QAdminFeePolicies.adminFeePolicies.adminFee.adminFeeId.eq(adminFeeId)) == 0)
            throw new BadRequestException("Cannot authorised without admin Fee Policies");

        if (adminFee.getAdminFeeAmt().compareTo(BigDecimal.ZERO) == 0)
            throw new BadRequestException("Cannot authorise the Transaction. Admin Fee Gross Amount is Zero");

        adminFee.setAuthorised("Y");
        adminFee.setAuthorisedBy(userUtils.getCurrentUser());
        adminFee.setAuthDate(new Date());
        adminFeeRepo.save(adminFee);
    }

    @Override
    @Transactional(readOnly = false)
    public void dispatchDocuments(Long polCode) {
        PolicyTrans policyTrans = policyRepo.findOne(polCode);
        boolean medicalProduct = false;
        if (policyTrans.getProduct().getProGroup().getPrgType() == null || !policyTrans.getProduct().getProGroup().getPrgType().equalsIgnoreCase("MD")) {
            medicalProduct = false;
        } else if (policyTrans.getProduct().getProGroup().getPrgType().equalsIgnoreCase("MD")) {
            medicalProduct = true;
        }
        workflowService.completeTask(String.valueOf(polCode), policyTrans, DocType.GEN_UW_DOCUMENT, (medicalProduct) ? "Y" : "N", null, null, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<RiskDocsDTO> findRiskDocs(DataTablesRequest request, Long riskId) throws IllegalAccessException {
        List<Object[]> riskDocs = riskDocsRepo.getAllRiskDocs(riskId, request.getPageNumber(), request.getPageSize());
        long rowCount = 0L;
        if (!riskDocs.isEmpty()) rowCount = ((BigInteger) riskDocs.get(0)[18]).intValue();
        List<RiskDocsDTO> riskDocsList = new ArrayList<>();
        for (Object[] doc : riskDocs) {
            final RiskDocsDTO rdoc = new RiskDocsDTO();
            rdoc.setPolRevNo((String) doc[0]);
            rdoc.setRdId(((BigInteger) doc[1]).longValue());
            rdoc.setCheckSum((String) doc[2]);
            rdoc.setContentType((String) doc[3]);
            rdoc.setUploadedFileName((String) doc[4]);
            rdoc.setUrl((String) doc[5]);
            rdoc.setDocShtDesc((String) doc[6]);
            rdoc.setDocDesc((String) doc[7]);
            rdoc.setPolicyId(((BigInteger) doc[8]).longValue());
            rdoc.setAuthStatus((String) doc[9]);
            rdoc.setUploadedBy((String) doc[10]);
            rdoc.setUploadedDate((Date) doc[13]);
            rdoc.setVerifiedBy((String) doc[11]);
            rdoc.setVerifiedDate((Date) doc[12]);
            if (doc.length > 14) {
                rdoc.setComments((String) doc[14]);
            }
            rdoc.setVerifiedBy((String) doc[15]);     // ? new verified_by
            rdoc.setVerifiedDate((Date) doc[16]);     // ? new verified_date
            riskDocsList.add(rdoc);
        }
        Page<RiskDocsDTO> page = new PageImpl<>(riskDocsList, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<RiskDocsDTO> findRiskRefundDocs(DataTablesRequest request, Long riskId) throws IllegalAccessException {
        List<Object[]> riskDocs = riskDocsRepo.getAllRiskRefundDocs(riskId, request.getPageNumber(), request.getPageSize());
        long rowCount = 0L;
        if (!riskDocs.isEmpty()) rowCount = ((BigInteger) riskDocs.get(0)[18]).intValue();
        List<RiskDocsDTO> riskDocsList = new ArrayList<>();
        for (Object[] doc : riskDocs) {
            final RiskDocsDTO rdoc = new RiskDocsDTO();
            rdoc.setPolRevNo((String) doc[0]);
            rdoc.setRdId(((BigInteger) doc[1]).longValue());
            rdoc.setCheckSum((String) doc[2]);
            rdoc.setContentType((String) doc[3]);
            rdoc.setUploadedFileName((String) doc[4]);
            rdoc.setUrl((String) doc[5]);
            rdoc.setDocShtDesc((String) doc[6]);
            rdoc.setDocDesc((String) doc[7]);
            rdoc.setPolicyId(((BigInteger) doc[8]).longValue());
            rdoc.setAuthStatus((String) doc[9]);
            rdoc.setUploadedBy((String) doc[10]);
            rdoc.setUploadedDate((Date) doc[13]);
            rdoc.setVerifiedBy((String) doc[11]);
            rdoc.setVerifiedDate((Date) doc[12]);
            if (doc.length > 14) {
                rdoc.setComments((String) doc[14]);
            }
            rdoc.setVerifiedBy((String) doc[15]);     // ? new verified_by
            rdoc.setVerifiedDate((Date) doc[16]);     // ? new verified_date
            riskDocsList.add(rdoc);
        }
        Page<RiskDocsDTO> page = new PageImpl<>(riskDocsList, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<TransChecks> findPolicyChecks(DataTablesRequest request, Long polCode) throws IllegalAccessException {
        BooleanExpression pred = QTransChecks.transChecks.policyTrans.policyId.eq(polCode);
        Page<TransChecks> page = transChecksRepo.findAll(pred.and(request.searchPredicate(QTransChecks.transChecks)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<RiskImportationLog> findPolicyImportationLog(DataTablesRequest request, Long policyId) throws IllegalAccessException {
        BooleanExpression pred = QRiskImportationLog.riskImportationLog.policyTrans.policyId.eq(policyId);
        Page<RiskImportationLog> page = importLogRepo.findAll(pred.and(request.searchPredicate(QRiskImportationLog.riskImportationLog)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = {BadRequestException.class})
    public void validateRiskIdFormat(Long subCode, String riskId) throws BadRequestException {
        if (subCode == null)
            throw new BadRequestException("Sub Class Not Selected...");
        SubClassDef subClassDef = subclassRepo.findOne(subCode);
        if (subClassDef == null)
            throw new BadRequestException("Sub Class Not Selected...");
        if (!validatorUtils.validate(riskId, subClassDef.getRiskFormat())) {
            throw new BadRequestException("Enter Valid Plate Number...");
        }
    }


    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void importExcelRiskTemplate(RiskUploadForm uploadForm) throws BadRequestException {
        try {
            if (uploadForm.getPolCode() == null) throw new BadRequestException("Cannot Upload Risk without Policy....");
            if (uploadForm.getBinderDetails() == null)
                throw new BadRequestException("Cannot Upload....Select Sub Class And Cover to upload....");
            PolicyTrans policyTrans = policyRepo.findOne(uploadForm.getPolCode());
            RiskSections riskSections = importExcelUtils.importRisks(uploadForm.getFile());
            if (riskSections.getRisks().isEmpty())
                throw new BadRequestException("No Risks to import....");
            Set<RiskImportBean> risks = riskSections.getRisks();
            List<RiskTrans> policyRisks = new ArrayList<>();
            List<ScheduleTrans> scheduleTransactions = new ArrayList<>();
            List<SectionTrans> sectionTransactions = new ArrayList<>();
            List<RiskImportationLog> importationLogs = new ArrayList<>();
            for (RiskImportBean riskImportBean : risks) {
                ClientDef client = null;
                if (riskImportBean.getIdNo() == null || StringUtils.isBlank(riskImportBean.getIdNo())) {
                    RiskImportationLog log = new RiskImportationLog();
                    log.setErrorMessage("No Id No For Plate Number " + riskImportBean.getRiskId());
                    log.setPolicyTrans(policyTrans);
                    importationLogs.add(log);
                    continue;
                }
                long clientCount = clientRepo.count(QClientDef.clientDef.idNo.eq(riskImportBean.getIdNo()));
                if (clientCount == 0) {
                    String names = riskImportBean.getInsuredName();
                    StringTokenizer tokenizer = new StringTokenizer(names, " ");
                    Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("C");
                    if (sequenceRepo.count(seqPredicate) == 0)
                        throw new BadRequestException("Sequence for Client Definition has not been setup");
                    SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
                    Long seqNumber = sequence.getNextNumber();
                    final String clientNumber = sequence.getSeqPrefix() + String.format("%06d", seqNumber);
                    sequence.setLastNumber(seqNumber);
                    sequence.setNextNumber(seqNumber + 1);
                    sequenceRepo.save(sequence);
                    client = new ClientDef();
                    if (tokenizer.hasMoreTokens())
                        client.setFname(tokenizer.nextToken());
                    client.setGender(riskImportBean.getGender());
                    client.setIdNo(riskImportBean.getIdNo());
                    if (tokenizer.hasMoreTokens())
                        client.setOtherNames(tokenizer.nextToken() + ((tokenizer.hasMoreTokens()) ? " " + tokenizer.nextToken() : ""));
                    client.setPhoneNo(riskImportBean.getMobileNumber());
                    client.setPinNo(riskImportBean.getPinNo());
                    client.setRegisteredbrn(policyTrans.getBranch());
                    client.setDateregistered(new Date());
                    long clientTypeCount = clientTypeRepo.count(QClientTypes.clientTypes.clientType.eq("I").and(QClientTypes.clientTypes.typeDesc.containsIgnoreCase("INDIV")));
                    if (clientTypeCount == 1)
                        client.setTenantType(clientTypeRepo.findOne(QClientTypes.clientTypes.clientType.eq("I").and(QClientTypes.clientTypes.typeDesc.containsIgnoreCase("INDIV"))));
                    else {
                        Iterable<ClientTypes> clientTypes = clientTypeRepo.findAll(QClientTypes.clientTypes.clientType.eq("I"));
                        if (clientTypes.spliterator().getExactSizeIfKnown() == 0)
                            throw new BadRequestException("Client Type Not defined in the system...");
                        for (ClientTypes clientType : clientTypes) {
                            client.setTenantType(clientType);
                            break;
                        }
                    }
                    long countryCount = countryRepository.count(QCountry.country.couShtDesc.eq(riskImportBean.getCountry()));
                    if (countryCount == 1)
                        client.setCountry(countryRepository.findOne(QCountry.country.couShtDesc.eq(riskImportBean.getCountry())));
                    client.setDob(riskImportBean.getDateOfBirth());
                    client.setStatus("A");
                    client.setTenantNumber(clientNumber);
                    client = clientRepo.save(client);
                } else if (clientCount == 1) {
                    client = clientRepo.findOne(QClientDef.clientDef.idNo.eq(riskImportBean.getIdNo()));
                } else {
                    RiskImportationLog log = new RiskImportationLog();
                    log.setErrorMessage("More than one Client Exists in the System with ID " + riskImportBean.getIdNo() + " for Risk " + riskImportBean.getRiskId());
                    log.setPolicyTrans(policyTrans);
                    importationLogs.add(log);
                    continue;
                }
                Date polWef = dateUtils.removeTime(policyTrans.getWefDate());
                Date polWet = dateUtils.removeTime(policyTrans.getWetDate());
                Date riskWef = dateUtils.removeTime(riskImportBean.getEffDate());
                Date riskWet = dateUtils.removeTime(riskImportBean.getExpDate());
                if (riskWef.before(polWef) || riskWef.after(polWet)
                        || riskWet.before(polWef) || riskWet.after(polWet)) {
                    RiskImportationLog log = new RiskImportationLog();
                    log.setErrorMessage("Risk WEF and WET outside policy period for Risk " + riskImportBean.getRiskId());
                    log.setPolicyTrans(policyTrans);
                    importationLogs.add(log);
                    continue;
                }
                if (riskRepo.count(QRiskTrans.riskTrans.riskShtDesc.eq(riskImportBean.getRegno())
                        .and(QRiskTrans.riskTrans.subclass.riskUnique.isTrue())
                        .and(QRiskTrans.riskTrans.policy.currentStatus.eq("A"))
                        .and(QRiskTrans.riskTrans.wefDate.between(riskWef, riskWet).or(QRiskTrans.riskTrans.wetDate.between(riskWef, riskWet)))
                        .and(QRiskTrans.riskTrans.policy.transType.in("NB", "RN"))) > 0) {
                    throw new BadRequestException("Risk Already Exists in the system...." +
                            riskRepo.count(QRiskTrans.riskTrans.riskShtDesc.eq(riskImportBean.getRegno())
                                    .and(QRiskTrans.riskTrans.subclass.riskUnique.isTrue())
                                    .and(QRiskTrans.riskTrans.policy.currentStatus.eq("A"))
                                    .and(QRiskTrans.riskTrans.wefDate.between(riskWef, riskWet)).or(QRiskTrans.riskTrans.wetDate.between(riskWef, riskWet))
                                    .and(QRiskTrans.riskTrans.policy.transType.in("NB", "RN")))
                    );
                }
                RiskTrans risk = new RiskTrans();
                risk.setWefDate(riskWef);
                risk.setWetDate(riskWet);
                risk.setCovertype(uploadForm.getBinderDetails().getSubCoverTypes().getCoverTypes());
                risk.setBinderDetails(uploadForm.getBinderDetails());
                risk.setBinder(policyTrans.getBinder());
                risk.setSubclass(uploadForm.getBinderDetails().getSubCoverTypes().getSubclass());
                risk.setAutogenCert("N");
                risk.setCommRate(policyTrans.getAgent().getAccountType().getCommRate());
                risk.setPolicy(policyTrans);
                risk.setProrata("F");
                risk.setRiskDesc(riskImportBean.getRiskdesc());
                risk.setRiskShtDesc(riskImportBean.getRegno());
                risk.setTransType("NB");
                risk.setInsured(client);
                risk.setButchargePrem(riskImportBean.getButCharge());
                policyRisks.add(risk);
                riskSections.getSections().stream().filter(a -> a.getRiskId().equals(riskImportBean.getRiskId())).forEach(a -> {
                    long count = setupSectionRepo.count(QSectionsDef.sectionsDef.shtDesc.eq(a.getSectionId()));
                    if (count == 1) {
                        SectionsDef sectiondef = setupSectionRepo.findOne(QSectionsDef.sectionsDef.shtDesc.eq(a.getSectionId()));
                        SectionTrans section = new SectionTrans();
                        List<PremRatesDef> premRates = premRatesRepo.getSectPremiumRates(uploadForm.getBinderDetails().getDetId(), sectiondef.getId());
                        if (premRates.size() == 1) {
                            section.setPremRates(premRates.get(0));
                            section.setRate(premRates.get(0).getRate());
                            section.setAmount(a.getLimit());
                            section.setCompute(true);
                            section.setDivFactor(premRates.get(0).getDivFactor());
                            section.setFreeLimit(premRates.get(0).getFreeLimit());
                            section.setSection(sectiondef);
                            section.setRisk(risk);
                            sectionTransactions.add(section);
                        }

                    } else {
                        try {
                            throw new BadRequestException(String.format("Section %s is not defined", a.getSectionId()));
                        } catch (BadRequestException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

                Long subId = risk.getSubclass().getSubId();
                Long colCount = mappingRepo.count(QScheduleMapping.scheduleMapping.subclass.subId.eq(subId));
                if (colCount == 0) {
                    RiskImportationLog log = new RiskImportationLog();
                    log.setErrorMessage("No Schedule Mapping for Sub Class " + risk.getSubclass().getSubDesc() + " for Plate Number " + riskImportBean.getRiskId());
                    log.setPolicyTrans(policyTrans);
                    importationLogs.add(log);
                    continue;
                }
                Iterable<ScheduleMapping> mappings = mappingRepo.findAll(QScheduleMapping.scheduleMapping.subclass.subId.eq(subId));
                ScheduleTrans scheduleTrans = new ScheduleTrans();
                scheduleTrans.setRisk(risk);
                for (ScheduleMapping mapping : mappings) {
                    if (mapping.getColumnIndex().equalsIgnoreCase("1")) {
                        scheduleTrans.setColumn1(riskImportBean.getColumn1());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("2")) {
                        scheduleTrans.setColumn2(riskImportBean.getColumn2());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("3")) {
                        scheduleTrans.setColumn3(riskImportBean.getColumn3());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("4")) {
                        scheduleTrans.setColumn4(riskImportBean.getColumn4());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("5")) {
                        scheduleTrans.setColumn5(riskImportBean.getColumn5());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("6")) {
                        scheduleTrans.setColumn6(riskImportBean.getColumn6());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("7")) {
                        scheduleTrans.setColumn7(riskImportBean.getColumn7());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("8")) {
                        scheduleTrans.setColumn8(riskImportBean.getColumn8());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("9")) {
                        scheduleTrans.setColumn9(riskImportBean.getColumn9());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("10")) {
                        scheduleTrans.setColumn10(riskImportBean.getColumn10());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("11")) {
                        scheduleTrans.setColumn11(riskImportBean.getColumn11());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("12")) {
                        scheduleTrans.setColumn12(riskImportBean.getColumn12());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("13")) {
                        scheduleTrans.setColumn13(riskImportBean.getColumn13());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("14")) {
                        scheduleTrans.setColumn14(riskImportBean.getColumn14());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("15")) {
                        scheduleTrans.setColumn15(riskImportBean.getColumn15());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("16")) {
                        scheduleTrans.setColumn16(riskImportBean.getColumn16());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("17")) {
                        scheduleTrans.setColumn17(riskImportBean.getColumn17());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("18")) {
                        scheduleTrans.setColumn18(riskImportBean.getColumn18());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("19")) {
                        scheduleTrans.setColumn19(riskImportBean.getColumn19());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("20")) {
                        scheduleTrans.setColumn20(riskImportBean.getColumn20());
                    }
                }
                scheduleTransactions.add(scheduleTrans);

            }
            Iterable<RiskTrans> savedRisks = riskRepo.save(policyRisks);
            for (RiskTrans savedRisk : savedRisks) {
                long riskIdentifier = Long.valueOf(String.valueOf(dateUtils.getUwYear(savedRisk.getWefDate())) + String.valueOf(savedRisk.getRiskId()));
                PolicyActiveRisks activeRisk = new PolicyActiveRisks();
                activeRisk.setPolicy(policyTrans);
                activeRisk.setRisk(savedRisk);
                activeRisk.setRiskIdentifier(riskIdentifier);
                activeRisksRepo.save(activeRisk);
                savedRisk.setRiskIdentifier(riskIdentifier);


            }
            importLogRepo.save(importationLogs);
            sectionRepo.save(sectionTransactions);
            scheduleTransRepo.save(scheduleTransactions);
            premComputeService.computePrem(uploadForm.getPolCode());

        } catch (IOException e) {
            throw new BadRequestException("Unable to Upload Excel File...Check the Excel file and try again....");
        }
    }

    @Override
    @Transactional
    public void savePendingRisks(RiskUploadForm uploadForm) throws BadRequestException {
        try {
            if (uploadForm.getBinderDetails() == null)
                throw new BadRequestException("Cannot Upload....Select Sub Class And Cover to upload....");

            RiskSections riskSections = importExcelUtils.importRisks(uploadForm.getFile());
            if (riskSections.getRisks().isEmpty())
                throw new BadRequestException("No Risks to import....");

            Set<RiskImportBean> risks = riskSections.getRisks();
            List<RiskTrans> pendingRisks = new ArrayList<>();
            List<ScheduleTrans> scheduleTransactions = new ArrayList<>();
            List<SectionTrans> sectionTransactions = new ArrayList<>();
            List<RiskImportationLog> importationLogs = new ArrayList<>();

            for (RiskImportBean riskImportBean : risks) {
                ClientDef client = null;

                if (riskImportBean.getIdNo() == null || StringUtils.isBlank(riskImportBean.getIdNo())) {
                    RiskImportationLog log = new RiskImportationLog();
                    log.setErrorMessage("No Id No For Plate Number " + riskImportBean.getRiskId());
                    importationLogs.add(log);
                    continue;
                }

                long clientCount = clientRepo.count(QClientDef.clientDef.idNo.eq(riskImportBean.getIdNo()));
                if (clientCount == 0) {
                    String names = riskImportBean.getInsuredName();
                    StringTokenizer tokenizer = new StringTokenizer(names, " ");
                    Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("C");

                    if (sequenceRepo.count(seqPredicate) == 0)
                        throw new BadRequestException("Sequence for Client Definition has not been setup");

                    SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
                    Long seqNumber = sequence.getNextNumber();
                    final String clientNumber = sequence.getSeqPrefix() + String.format("%06d", seqNumber);
                    sequence.setLastNumber(seqNumber);
                    sequence.setNextNumber(seqNumber + 1);
                    sequenceRepo.save(sequence);

                    client = new ClientDef();
                    client.setRegisteredbrn(branchRepo.findAll().iterator().next());
                    if (tokenizer.hasMoreTokens())
                        client.setFname(tokenizer.nextToken());

                    client.setGender(riskImportBean.getGender());
                    client.setIdNo(riskImportBean.getIdNo());

                    if (tokenizer.hasMoreTokens())
                        client.setOtherNames(tokenizer.nextToken() + ((tokenizer.hasMoreTokens()) ? " " + tokenizer.nextToken() : ""));

                    client.setPhoneNo(riskImportBean.getMobileNumber());
                    client.setPinNo(riskImportBean.getPinNo());
                    client.setDateregistered(new Date());

                    long clientTypeCount = clientTypeRepo.count(QClientTypes.clientTypes.clientType.eq("I").and(QClientTypes.clientTypes.typeDesc.containsIgnoreCase("INDIV")));
                    if (clientTypeCount == 1)
                        client.setTenantType(clientTypeRepo.findOne(QClientTypes.clientTypes.clientType.eq("I").and(QClientTypes.clientTypes.typeDesc.containsIgnoreCase("INDIV"))));

                    client.setDob(riskImportBean.getDateOfBirth());
                    client.setStatus("A");
                    client.setTenantNumber(clientNumber);
                    client = clientRepo.save(client);

                } else if (clientCount == 1) {
                    client = clientRepo.findOne(QClientDef.clientDef.idNo.eq(riskImportBean.getIdNo()));
                } else {
                    RiskImportationLog log = new RiskImportationLog();
                    log.setErrorMessage("More than one Client Exists in the System with ID " + riskImportBean.getIdNo() + " for Risk " + riskImportBean.getRiskId());
                    importationLogs.add(log);
                    continue;
                }

                RiskTrans risk = new RiskTrans();
                Date riskWef = dateUtils.removeTime(riskImportBean.getEffDate());
                Date riskWet = dateUtils.removeTime(riskImportBean.getExpDate());

                if (riskRepo.count(QRiskTrans.riskTrans.riskShtDesc.eq(riskImportBean.getRegno())
                        .and(QRiskTrans.riskTrans.subclass.riskUnique.isTrue())
                        .and(QRiskTrans.riskTrans.policy.currentStatus.eq("A"))
                        .and(QRiskTrans.riskTrans.wefDate.between(riskWef, riskWet).or(QRiskTrans.riskTrans.wetDate.between(riskWef, riskWet)))
                        .and(QRiskTrans.riskTrans.policy.transType.in("NB", "RN"))) > 0) {
                    throw new BadRequestException("Risk Already Exists in the system...." +
                            riskRepo.count(QRiskTrans.riskTrans.riskShtDesc.eq(riskImportBean.getRegno())
                                    .and(QRiskTrans.riskTrans.subclass.riskUnique.isTrue())
                                    .and(QRiskTrans.riskTrans.policy.currentStatus.eq("A"))
                                    .and(QRiskTrans.riskTrans.wefDate.between(riskWef, riskWet)).or(QRiskTrans.riskTrans.wetDate.between(riskWef, riskWet))
                                    .and(QRiskTrans.riskTrans.policy.transType.in("NB", "RN")))
                    );
                }

                risk.setWefDate(riskWef);
                risk.setWetDate(riskWet);
                risk.setCovertype(uploadForm.getBinderDetails().getSubCoverTypes().getCoverTypes());
                risk.setBinderDetails(uploadForm.getBinderDetails());
                risk.setBinder(uploadForm.getBinderDetails().getBinder());
                risk.setSubclass(uploadForm.getBinderDetails().getSubCoverTypes().getSubclass());
                risk.setAutogenCert("N");
                risk.setCommRate(null);
                risk.setPolicy(null);
                risk.setProrata("F");
                risk.setRiskDesc(riskImportBean.getRiskdesc());
                risk.setRiskShtDesc(riskImportBean.getRegno());
                risk.setTransType("NB");
                risk.setInsured(client);
                risk.setButchargePrem(riskImportBean.getButCharge());
                risk.setPending(true);

                pendingRisks.add(risk);

                riskSections.getSections().stream()
                        .filter(a -> a.getRiskId().equals(riskImportBean.getRiskId()))
                        .forEach(a -> {
                            long count = setupSectionRepo.count(QSectionsDef.sectionsDef.shtDesc.eq(a.getSectionId()));
                            if (count == 1) {
                                SectionsDef sectiondef = setupSectionRepo.findOne(QSectionsDef.sectionsDef.shtDesc.eq(a.getSectionId()));
                                SectionTrans section = new SectionTrans();
                                List<PremRatesDef> premRates = premRatesRepo.getSectPremiumRates(uploadForm.getBinderDetails().getDetId(), sectiondef.getId());
                                if (premRates.size() == 1) {
                                    section.setPremRates(premRates.get(0));
                                    section.setRate(premRates.get(0).getRate());
                                    section.setAmount(a.getLimit());
                                    section.setDivFactor(premRates.get(0).getDivFactor());
                                    section.setFreeLimit(premRates.get(0).getFreeLimit());
                                    section.setCompute(true);
                                    section.setSection(sectiondef);
                                    section.setRisk(risk);
                                    sectionTransactions.add(section);
                                }
                            }
                        });

                Long subId = risk.getSubclass().getSubId();
                long colCount = mappingRepo.count(QScheduleMapping.scheduleMapping.subclass.subId.eq(subId));
                if (colCount == 0) {
                    RiskImportationLog log = new RiskImportationLog();
                    log.setErrorMessage("No Schedule Mapping for Sub Class " + risk.getSubclass().getSubDesc() + " for Plate Number " + riskImportBean.getRiskId());
                    importationLogs.add(log);
                    continue;
                }

                Iterable<ScheduleMapping> mappings = mappingRepo.findAll(QScheduleMapping.scheduleMapping.subclass.subId.eq(subId));
                ScheduleTrans scheduleTrans = new ScheduleTrans();
                scheduleTrans.setRisk(risk);

                for (ScheduleMapping mapping : mappings) {
                    if (mapping.getColumnIndex().equalsIgnoreCase("1")) {
                        scheduleTrans.setColumn1(riskImportBean.getColumn1());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("2")) {
                        scheduleTrans.setColumn2(riskImportBean.getColumn2());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("3")) {
                        scheduleTrans.setColumn3(riskImportBean.getColumn3());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("4")) {
                        scheduleTrans.setColumn4(riskImportBean.getColumn4());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("5")) {
                        scheduleTrans.setColumn5(riskImportBean.getColumn5());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("6")) {
                        scheduleTrans.setColumn6(riskImportBean.getColumn6());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("7")) {
                        scheduleTrans.setColumn7(riskImportBean.getColumn7());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("8")) {
                        scheduleTrans.setColumn8(riskImportBean.getColumn8());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("9")) {
                        scheduleTrans.setColumn9(riskImportBean.getColumn9());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("10")) {
                        scheduleTrans.setColumn10(riskImportBean.getColumn10());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("11")) {
                        scheduleTrans.setColumn11(riskImportBean.getColumn11());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("12")) {
                        scheduleTrans.setColumn12(riskImportBean.getColumn12());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("13")) {
                        scheduleTrans.setColumn13(riskImportBean.getColumn13());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("14")) {
                        scheduleTrans.setColumn14(riskImportBean.getColumn14());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("15")) {
                        scheduleTrans.setColumn15(riskImportBean.getColumn15());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("16")) {
                        scheduleTrans.setColumn16(riskImportBean.getColumn16());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("17")) {
                        scheduleTrans.setColumn17(riskImportBean.getColumn17());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("18")) {
                        scheduleTrans.setColumn18(riskImportBean.getColumn18());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("19")) {
                        scheduleTrans.setColumn19(riskImportBean.getColumn19());
                    } else if (mapping.getColumnIndex().equalsIgnoreCase("20")) {
                        scheduleTrans.setColumn20(riskImportBean.getColumn20());
                    }
                }

                scheduleTransactions.add(scheduleTrans);
            }

            riskRepo.save(pendingRisks);
            sectionRepo.save(sectionTransactions);
            scheduleTransRepo.save(scheduleTransactions);
            importLogRepo.save(importationLogs);

        } catch (IOException e) {
            throw new BadRequestException("Unable to Upload Excel File...Check the Excel file and try again....");
        }
    }


    @Override
    @Transactional
    public void linkPendingRisksToPolicy(Long polCode) {
        Iterable<RiskTrans> pendingRisks = riskRepo.findAll(QRiskTrans.riskTrans.pending.isTrue().and(QRiskTrans.riskTrans.policy.isNull()));
        if (pendingRisks != null) {
            PolicyTrans policyTrans = policyRepo.findOne(polCode);
            for (RiskTrans risk : pendingRisks) {
                long riskIdentifier = Long.valueOf(String.valueOf(dateUtils.getUwYear(risk.getWefDate())) + String.valueOf(risk.getRiskId()));
                PolicyActiveRisks activeRisk = new PolicyActiveRisks();
                risk.setPolicy(policyTrans);
                risk.setCommRate(policyTrans.getAgent().getAccountType().getCommRate());
                risk.setBinder(policyTrans.getBinder());
                risk.setPending(false);
                risk.setRiskIdentifier(riskIdentifier);
                activeRisk.setPolicy(policyTrans);
                activeRisk.setRisk(risk);
                activeRisk.setRiskIdentifier(riskIdentifier);
                riskRepo.save(risk);
                activeRisksRepo.save(activeRisk);

            }
        }
    }


    @Override
    public void approveException(Long checkId, Long policyId) throws BadRequestException {
        PolicyTrans policy = policyRepo.findOne(policyId);
        TransChecks checks = transChecksRepo.findOne(checkId);
        if (checks.getAuthorised() != null || "Y".equalsIgnoreCase(checks.getAuthorised())) {
            throw new BadRequestException("Exception already authorized...");
        }
        if (!authLimits.checkAuthorizationLimits(checks.getPermission().getPermName())) {
            throw new BadRequestException("You have no rights to Authorize the Exception....");
        }
        checks.setAuthBy(userUtils.getCurrentUser());
        checks.setAuthDate(new Date());
        checks.setAuthorised("Y");
        transChecksRepo.save(checks);
        long count = transChecksRepo.count(QTransChecks.transChecks.policyTrans.policyId.eq(policyId).and((QTransChecks.transChecks.authorised.isNull().or(QTransChecks.transChecks.authorised.eq("N")))));
        if (count == 0) {
            boolean medicalProduct = false;
            if (policy.getProduct().getProGroup().getPrgType() == null || !policy.getProduct().getProGroup().getPrgType().equalsIgnoreCase("MD")) {
                medicalProduct = false;
            } else if (policy.getProduct().getProGroup().getPrgType().equalsIgnoreCase("MD")) {
                medicalProduct = true;
            }

            boolean cashBasis = policy.getInterfaceType() != null && "C".equalsIgnoreCase(policy.getInterfaceType());

            SystemTransactionsTemp trans = systemTransactionsTempRepo.findOne(QSystemTransactionsTemp.systemTransactionsTemp.policy.policyId.eq(policyId));

            if (!cashBasis && trans != null) {
                systemTransactionsTempRepo.delete(trans);
            }

            if (cashBasis) {
                BigDecimal basicPrem = (policy.getPremium() == null) ? BigDecimal.ZERO : policy.getPremium();
                if (trans == null) {
                    trans = new SystemTransactionsTemp();
                    Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("D");
                    if (sequenceRepo.count(seqPredicate) == 0)
                        throw new BadRequestException("Sequence for Debit Notes has not been defined");
                    SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
                    Long seqNumber = sequence.getNextNumber();
                    String transType = policy.getTransType();
                    if ("NB".equalsIgnoreCase(transType)) {
                        transType = "NB";
                    } else if ("RN".equalsIgnoreCase(transType)) {
                        transType = "RN";
                    } else {
                        transType = "EN";
                    }
                    TransactionMapping mapping = transMappingRepo.findOne(QTransactionMapping.transactionMapping.transType.eq(transType));
                    final String refNo = ((basicPrem.compareTo(BigDecimal.ZERO) >= 0) ? mapping.getDebitCode() : mapping.getCreditCode()) + String.format("%05d", seqNumber);
                    final String debitCode = ((basicPrem.compareTo(BigDecimal.ZERO) >= 0) ? mapping.getDebitCode() : mapping.getCreditCode());
                    sequence.setLastNumber(seqNumber);
                    sequence.setNextNumber(seqNumber + 1);
                    sequenceRepo.save(sequence);
                    trans.setRefNo(refNo);
                    trans.setTransType(debitCode);
                }


                BigDecimal extras = (policy.getExtras() == null) ? BigDecimal.ZERO : policy.getExtras();
                BigDecimal phcf = (policy.getPhcf() == null) ? BigDecimal.ZERO : policy.getPhcf();
                BigDecimal tl = (policy.getTrainingLevy() == null) ? BigDecimal.ZERO : policy.getTrainingLevy();
                BigDecimal sd = (policy.getStampDuty() == null) ? BigDecimal.ZERO : policy.getStampDuty();
                BigDecimal amountWithTaxes = basicPrem.add(extras).add(phcf).add(tl).add(sd);

                if (basicPrem.compareTo(BigDecimal.ZERO) == 1) {
                    String type = (amountWithTaxes.compareTo(BigDecimal.ZERO) == 1) ? "D" : "C";

                    trans.setAmount(basicPrem.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    trans.setAuthDate(new Date());
                    trans.setAuthorised("Y");
                    trans.setBalance(amountWithTaxes.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    trans.setBranch(policy.getBranch());
                    trans.setClientType("C");
                    trans.setControlAcc(policy.getClient().getTenantNumber());
                    trans.setClient(policy.getClient());
                    trans.setCurrRate(new BigDecimal(1));
                    trans.setCurrency(policy.getTransCurrency());
                    trans.setNarrations("Posting client Debit Note");
                    trans.setNetAmount(amountWithTaxes.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    trans.setOrigin("U");
                    trans.setPhfund(phcf.setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    trans.setPolicy(policy);
                    trans.setSd(sd.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    trans.setTl(tl.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    trans.setTransDate(new Date());
                    trans.setTransdc((amountWithTaxes.compareTo(BigDecimal.ZERO) == 1) ? "D" : "C");
                    trans.setUserAuth(userUtils.getCurrentUser().getUsername());
                    trans.setWhtx(BigDecimal.ZERO);
                    trans.setExtras(extras.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    trans.setPostedDate(new Date());
                    trans.setPostedUser(userUtils.getCurrentUser());
                    systemTransactionsTempRepo.save(trans);
                }
            }
            Map<String, Object> processVariables = Maps.newHashMap();
            processVariables.put("confirmAuth", true);
            processVariables.put("rejectTrans", false);
            processVariables.put("hasAuthority", true);
            workflowService.completeTask(String.valueOf(policyId), processVariables, policy, DocType.GEN_UW_DOCUMENT, (medicalProduct) ? "Y" : "N", null, null, null, null);
        }

    }

    @Override
    public List<ReqDocsDTO> findUnassignedRiskDocs(Long riskId, String docName) throws IllegalAccessException {
        //RiskTrans riskTrans = riskRepo.findOne(riskId);
        List<Object[]> risk = riskRepo.findRiskTrans(riskId);
        //List<RiskTrans> riskTrans = new ArrayList<>();
        RiskTrans riskTrans = new RiskTrans();
        for (Object[] risktrn : risk) {

            if (risktrn[1] instanceof BigInteger) {
                riskTrans.setRiskIdentifier(((BigInteger) risktrn[1]).longValue());
                riskTrans.setPolicy(policyRepo.findOne(((BigInteger) risktrn[2]).longValue()));
                riskTrans.setSubclass(subclassRepo.findOne(((BigInteger) risktrn[3]).longValue()));
            } else if (risktrn[1] instanceof BigDecimal) {
                riskTrans.setRiskIdentifier(((BigDecimal) risktrn[1]).longValue());
                riskTrans.setPolicy(policyRepo.findOne(((BigDecimal) risktrn[2]).longValue()));
                riskTrans.setSubclass(subclassRepo.findOne(((BigDecimal) risktrn[3]).longValue()));

            }

            //riskTrans.add(rsktran);
        }
        PolicyTrans policy = riskTrans.getPolicy();
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
        } else if ("RF".equalsIgnoreCase(transType)) {
            transType = "RF";
        } else {
            transType = "EN";
        }
        List<Object[]> reqDocs = subclassReqDocRepo.unassignedRiskDocs(riskTrans.getSubclass().getSubId(), riskId, transType);
        List<ReqDocsDTO> reqDocsDTOs = new ArrayList<>();
        for (Object[] reqDoc : reqDocs) {
            ReqDocsDTO reqDocsDTO = new ReqDocsDTO();
            reqDocsDTO.setSclReqrdId(((BigInteger) reqDoc[0]).longValue());
            reqDocsDTO.setReqShtDesc((String) reqDoc[1]);
            reqDocsDTO.setReqDesc((String) reqDoc[2]);
            reqDocsDTOs.add(reqDocsDTO);
        }
        return reqDocsDTOs;

    }

    @Override
    public void createRiskRequiredDocs(RequiredDocBean requiredDocBean) {

        List<Object[]> risk = riskRepo.findRiskTrans(requiredDocBean.getSubCode());
        RiskTrans riskTrans = new RiskTrans();
        for (Object[] risktrn : risk) {
            if (risktrn[1] instanceof BigInteger) {
                riskTrans.setRiskIdentifier(((BigInteger) risktrn[1]).longValue());
                riskTrans.setPolicy(policyRepo.findOne(((BigInteger) risktrn[2]).longValue()));
                riskTrans.setSubclass(subclassRepo.findOne(((BigInteger) risktrn[3]).longValue()));
            } else if (risktrn[1] instanceof BigDecimal) {
                riskTrans.setRiskIdentifier(((BigDecimal) risktrn[1]).longValue());
                riskTrans.setPolicy(policyRepo.findOne(((BigDecimal) risktrn[2]).longValue()));
                riskTrans.setSubclass(subclassRepo.findOne(((BigDecimal) risktrn[3]).longValue()));
            }
        }
        List<RiskDocs> riskDocs =
                requiredDocBean.getRequiredDocs().stream().map(reqId -> {
                    RiskDocs riskDoc = new RiskDocs();
                    riskDoc.setReqdDocs(subclassReqDocRepo.findOne(reqId));
                    //riskDoc.setRisk(riskTrans);
                    riskDoc.setRisk(riskRepo.QueryRiskTrans(requiredDocBean.getSubCode()));
                    return riskDoc;
                }).collect(Collectors.toList());
        riskDocsRepo.save(riskDocs);
    }

    @Override
    public int countPolicies(Long clientCode) throws BadRequestException {
        return 0;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void questionnaireCompleted(PolicyTrans policy) throws BadRequestException {
        if ((questionnaireRepo.count(QBinderQuestionnaire.binderQuestionnaire.binder.binId.eq(policy.getBinder().getBinId())) > 0) && (policyQuestionnaireRepo.count(QPolicyQuestionnaire.policyQuestionnaire.policy.policyId.eq(policy.getPolicyId())) <= 0)) {
            throw new BadRequestException("The questionnaire for this haven't been completed");
        }
    }

    @Override
    public void lapsePolicy(Long polCode) throws BadRequestException {
        PolicyTrans policy = policyRepo.findOne(polCode);
        if (policy == null) throw new BadRequestException("No Policy Transaction");
        if (policy.getCurrentStatus() == null)
            throw new BadRequestException("Cannot Lapse a Policy which is not active");

        if (!policy.getCurrentStatus().equalsIgnoreCase("A"))
            throw new BadRequestException("Cannot Lapse a Policy which is not active");
        policy.setCurrentStatus("L");
        policyRepo.save(policy);
    }

    @Override
    public void unLapsePolicy(Long polCode) throws BadRequestException {
        PolicyTrans policy = policyRepo.findOne(polCode);
        if (policy == null) throw new BadRequestException("No Policy Transaction");
        if (policy.getCurrentStatus() == null)
            throw new BadRequestException("Cannot UnLapse a Policy which is not lapsed");

        if (!policy.getCurrentStatus().equalsIgnoreCase("L"))
            throw new BadRequestException("Cannot Lapse a Policy which is not lapsed");
        policy.setCurrentStatus("A");
        policyRepo.save(policy);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void savePolicyQuiz(QuestionnaireDTO questionnaireDTO) throws BadRequestException {
        List<PolicyQuestionnaire> polquiz = new ArrayList<>();
        Long policyCode = null;
        for (QuestionnaireBean quiz : questionnaireDTO.getQuizandAnswers()) {
            // if (quiz.getAnswer()!=null) {
            PolicyTrans policyTrans = policyRepo.findOne(questionnaireDTO.getQuizPolicyCode());
            policyCode = policyTrans.getPolicyId();
            BinderQuestionnaire question = binderQuestionnaireRepo.findOne(QBinderQuestionnaire.binderQuestionnaire.binder.binId.eq(policyTrans.getBinder().getBinId())
                    .and(QBinderQuestionnaire.binderQuestionnaire.questionname.equalsIgnoreCase(quiz.getQuestion())));
            PolicyQuestionnaire policyQuestionnaire = new PolicyQuestionnaire();
            policyQuestionnaire.setChoice(quiz.getAnswer().stream().collect(Collectors.joining(",")));
            policyQuestionnaire.setPolicy(policyRepo.findOne(questionnaireDTO.getQuizPolicyCode()));
            policyQuestionnaire.setQuestion(question);
            polquiz.add(policyQuestionnaire);
            System.out.println("quiz=" + quiz.getQuestion() + ";answer=" + quiz.getAnswer() + ";Policy=" + questionnaireDTO.getQuizPolicyCode());
            // }
        }
        deletePolicyQuiz(policyCode);
        policyQuestionnaireRepo.save(polquiz);
        PolicyTrans policyTrans = policyRepo.findOne(policyCode);
        try {
            if (policyTrans.getProduct().getProGroup().getPrgType().equalsIgnoreCase("L")) {
                premiumService.computeLifePrem(policyCode);
            } else {
                premiumService.computePrem(policyCode);
            }


        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }

    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void deletePolicyQuiz(Long polCode) {
        Iterable<PolicyQuestionnaire> policyQuiz = policyQuestionnaireRepo.findAll(QPolicyQuestionnaire.policyQuestionnaire.policy.policyId.eq(polCode));
        policyQuestionnaireRepo.delete(policyQuiz);
    }

    @Override
    public DataTablesResult<RiskTrans> findEnquiryMaster(DataTablesRequest request, Long polNo, Long riskId, Long idNo) throws IllegalAccessException {
        BooleanExpression pred = QRiskTrans.riskTrans.insured.tenId.eq(idNo).and(QRiskTrans.riskTrans
                .policy.policyId.eq(polNo).and(QRiskTrans.riskTrans.riskId.eq(riskId)));

        Page<RiskTrans> page = riskRepo.findAll(pred, request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<PolicyTrans> masterEnqPI(DataTablesRequest request, Long polNo, Long idNo) {
        BooleanExpression pred = QPolicyTrans.policyTrans.policyId.eq(polNo).and(
                QPolicyTrans.policyTrans.client.tenId.eq(idNo));
        Page<PolicyTrans> page = policyRepo.findAll(pred, request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<RiskTrans> findEnquiryPR(DataTablesRequest request, Long polNo, Long riskId) throws IllegalAccessException {
        BooleanExpression pred = QRiskTrans.riskTrans
                .policy.policyId.eq(polNo).and(QRiskTrans.riskTrans.riskId.eq(riskId));

        Page<RiskTrans> page = riskRepo.findAll(pred, request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<RiskTrans> findEnquiryRI(DataTablesRequest request, Long idNo, Long riskId) {
        BooleanExpression pred = QRiskTrans.riskTrans.insured.tenId.eq(idNo).and(QRiskTrans.riskTrans.riskId.eq(riskId));

        Page<RiskTrans> page = riskRepo.findAll(pred, request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<PolicyTrans> masterEnqPol(DataTablesRequest request, Long polNo) {
        Predicate pred = QPolicyTrans.policyTrans.policyId.eq(polNo);
        Page<PolicyTrans> page = policyRepo.findAll(pred, request);
        return new DataTablesResult<>(request, page);

    }

    @Override
    public DataTablesResult<PolicyTrans> masterEnqIdNo(DataTablesRequest request, Long idNo) {
        BooleanExpression pred = QPolicyTrans.policyTrans.client.tenId.eq(idNo);
        Page<PolicyTrans> page = policyRepo.findAll(pred, request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<RiskTrans> masterEnqRisk(DataTablesRequest request, Long policyId) {
        BooleanExpression pred = QRiskTrans.riskTrans.policy.policyId.eq(policyId);
        Page<RiskTrans> page = riskRepo.findAll(pred, request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<RiskTrans> masterEnqUniqueId(DataTablesRequest pageable, Long riskId) {
        Predicate predicate = QRiskTrans.riskTrans.riskId.eq(riskId);
        Page<RiskTrans> page = riskRepo.findAll(predicate, pageable);
        return new DataTablesResult<>(pageable, page);
    }

    @Override
    public DataTablesResult<ClaimPerils> masterEnqUniqueClaim(DataTablesRequest pageable, Long riskId) {
        BooleanExpression bool = QClaimPerils.claimPerils.claimBookings.risk.riskId.eq(riskId);
        Page<ClaimPerils> page = claimPerilsRepo.findAll(bool, pageable);
        return new DataTablesResult<>(pageable, page);
    }

    @Override
    public DataTablesResult<RiskTrans> masterEnqUniqueRisk(DataTablesRequest pageable, Long riskId) {
        Predicate pred = QRiskTrans.riskTrans.riskId.eq(riskId);
        Page<RiskTrans> page = riskRepo.findAll(pred, pageable);
        return new DataTablesResult<>(pageable, page);
    }

    @Override
    public PolicyTrans findEnquiryId(Long idNo) {

        return policyRepo.findFirstByClient_TenId(idNo);
    }

    @Override
    public PolicyTrans findEnquiryPol(Long polNo) {

        return policyRepo.findFirstByPolicyId(polNo);
    }

    @Override
    public RiskTrans findEnquiryRisk(Long riskId) {

        return riskRepo.findFirstByRiskId(riskId);
    }

    @Override
    public RiskTrans findEnquiryRiskPol(String riskId, String polNo) {

        return riskRepo.findFirstByPolicy_PolNoAndRiskShtDesc(polNo, riskId);

    }

    @Override
    public RiskTrans findEnquiryRiskId(String riskId, Long idNo) {
        return riskRepo.findFirstByInsured_TenIdAndRiskShtDesc(idNo, riskId);
    }

    @Override
    public PolicyTrans findEnquiryPolAndId(String polNo, Long idNo) {

        return policyRepo.findFirstByPolNoAndClient_TenId(polNo, idNo);
    }

    @Override
    public RiskTrans checkAllParam(String polNo, Long idNo, String riskId) {

        return riskRepo.findFirstByInsured_TenIdAndPolicy_PolNoAndRiskShtDesc(idNo, polNo, riskId);
    }

    @Override
    public ClientDef findClient(Long clId) {
        Predicate predicate = QClientDef.clientDef.tenId.eq(clId);

        return clientRepo.findOne(predicate);
    }

    @Override
    public DataTablesResult<ClientDef> masterIdNo(DataTablesRequest pageable, Long idNo) {
        Predicate predicate = QClientDef.clientDef.tenId.eq(idNo);
        Page<ClientDef> page = clientRepo.findAll(predicate, pageable);
        return new DataTablesResult<>(pageable, page);
    }

    @Override
    public Page<ClientDef> findAllClients(String paramString, Pageable pageable) {
        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred = QClientDef.clientDef.isNotNull();
        } else {
            pred = QClientDef.clientDef.fname.containsIgnoreCase(paramString).or(QClientDef.clientDef.otherNames.containsIgnoreCase(paramString));
        }
        return clientRepo.findAll(pred, pageable);
    }

    @Override
    public Page<PolicyTrans> findAllPols(String paramString, Pageable pageable) {
        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred = QPolicyTrans.policyTrans.isNotNull();
        } else {
            pred = QPolicyTrans.policyTrans.polNo.containsIgnoreCase(paramString);
        }
        return policyRepo.findAll(pred, pageable);
    }

    @Override
    public Page<RiskTrans> allRisksLov(String paramString, Pageable pageable) {
        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred = QRiskTrans.riskTrans.isNotNull();
        } else {
            pred = QRiskTrans.riskTrans.riskShtDesc.containsIgnoreCase(paramString);
        }
        return riskRepo.findAll(pred, pageable);
    }

    @Override
    public DataTablesResult<ReceiptTrans> masterReceipts(DataTablesRequest pageable, Long idNo) {
//        BooleanExpression booleanExpression = QReceiptTrans.receiptTrans.client.tenId.eq(idNo);
        BooleanExpression booleanExpression = QReceiptTrans.receiptTrans.receiptDtls.any().policy.client.tenId.eq(idNo);
        Page<ReceiptTrans> page = receiptRepository.findAll(booleanExpression, pageable);
        return new DataTablesResult<>(pageable, page);
    }

    @Override
    public ReceiptTrans getReceiptDetails(Long id) {
        Predicate predicate = QReceiptTrans.receiptTrans.receiptId.eq(id);
        return receiptRepository.findOne(predicate);
    }

    @Override
    public DataTablesResult<ReceiptTransDtls> getReceiptsDets(DataTablesRequest pageable, Long receiptId) {
        BooleanExpression bool = QReceiptTransDtls.receiptTransDtls
                .receipt.receiptId.eq(receiptId);
        Page<ReceiptTransDtls> page = receiptDetailsRepository.findAll(bool, pageable);
        return new DataTablesResult<>(pageable, page);
    }

    @Override
    public DataTablesResult<OverpaidPolicyDTO> getOverpaidPolicies(DataTablesRequest request, Date dateFrom, Date dateTo, Long accountCode) {
        final String search = (request.getSearch() != null && request.getSearch().getValue() != null)
                ? "%" + request.getSearch().getValue().toLowerCase() + "%"
                : "%%";
        List<Object[]> resultList = receiptRepository.findOverpaidPolicies(search.toLowerCase(), dateFrom, dateTo, accountCode, request.getPageNumber(), request.getPageSize());
        List<OverpaidPolicyDTO> overpaidPolicyDTOS = new ArrayList<>();
        for (Object[] obj : resultList) {
            OverpaidPolicyDTO overpaidPolicyDTO = new OverpaidPolicyDTO();
            BigDecimal refundPremium;
            overpaidPolicyDTO.setPolicyNo((String) obj[0]);
            overpaidPolicyDTO.setMaturityDate((Date) obj[1]);
            overpaidPolicyDTO.setClient(obj[2] + " " + obj[3]);
            overpaidPolicyDTO.setPaidPremium((BigDecimal) obj[6]);
            if (Objects.equals(obj[5], "L")) {
                overpaidPolicyDTO.setPolicyType("Life");
                overpaidPolicyDTO.setTotalPremium((BigDecimal) obj[7]);
                refundPremium = ((BigDecimal) obj[6]).subtract((BigDecimal) obj[7]);
            } else {
                overpaidPolicyDTO.setPolicyType("General");
                overpaidPolicyDTO.setTotalPremium((BigDecimal) obj[4]);
                refundPremium = ((BigDecimal) obj[6]).subtract((BigDecimal) obj[4]);
            }

            if (refundPremium.compareTo(BigDecimal.ONE) > 0) {
                overpaidPolicyDTO.setRefundPremium(refundPremium);
                overpaidPolicyDTOS.add(overpaidPolicyDTO);
            }
        }
        long rowCount = overpaidPolicyDTOS.size();
        Page<OverpaidPolicyDTO> page = new PageImpl<>(overpaidPolicyDTOS, request, rowCount);
        return new DataTablesResult<>(request, page);
    }


    @Override
    public ClaimBookings checkClaim(Long claim) {
        Predicate predicate = QClaimBookings.claimBookings.clmId.eq(claim);
        return claimsBookingRepo.findOne(predicate);
    }

    @Override
    public byte[] getPolicyDocument(Long prodCode) throws BadRequestException {
        byte[] arr = new byte[0];
        System.out.println("Product Code " + prodCode);
        if (prodCode == null)
            throw new BadRequestException("Policy Document does not exist...");
        ProductsDef upload = productRepo.findOne(prodCode);
        String uploadFolder = paramService.getParameterString("APP_UPLOAD_FOLDER");
        String folderName = uploadFolder + "/" + upload.getProCode();
        System.out.println("Folder name..." + folderName);
        Path path = Paths.get(folderName + "/" + upload.getProductPolicyDocument());
        if (path.toFile().exists()) {
            try {
                arr = Files.readAllBytes(path);
            } catch (IOException e) {
                e.printStackTrace();
                throw new BadRequestException(e.getMessage());
            }
        }
        return arr;
    }

    @Override
    public String getPolicyDocumentType(Long docId) throws BadRequestException {
        if (docId == null)
            throw new BadRequestException("Document does not exist...");
        ProductsDef upload = productRepo.findOne(docId);
        return upload.getContentType();
    }

    @Override
    public DataTablesResult<VehicleDetails> findVehicleDetails(DataTablesRequest request, Long ipuCode) throws IllegalAccessException {
        List<Object[]> motorDetails = motorVehicleDetailsRepo.getRiskVehicleDetails(ipuCode, request.getPageNumber(), request.getPageSize());
        final List<VehicleDetails> vehicleDetailsList = new ArrayList<>();
        long rowCount = 0l;
        if (!motorDetails.isEmpty()) rowCount = ((BigInteger) motorDetails.get(0)[12]).intValue();
        for (Object[] motorDetail : motorDetails) {
            VehicleDetails vehicleDetails = new VehicleDetails();
            vehicleDetails.setBodyColor((String) motorDetail[0]);
            vehicleDetails.setBodyType((String) motorDetail[1]);
            vehicleDetails.setCarMake((String) motorDetail[2]);
            vehicleDetails.setCarModel((String) motorDetail[3]);
            vehicleDetails.setCarryCapacity(((BigDecimal) motorDetail[4]));
            vehicleDetails.setChassisNo((String) motorDetail[5]);
            vehicleDetails.setEngineCapacity(((BigDecimal) motorDetail[6]));
            vehicleDetails.setEngineNumber((String) motorDetail[7]);
            vehicleDetails.setLogbookNumber((motorDetail[9] != null) ? motorDetail[9].toString() : null);
            vehicleDetails.setYearOfManufacture((motorDetail[8] != null) ? motorDetail[8].toString() : null);
            vehicleDetails.setRiskId(((BigInteger) motorDetail[10]).longValue());
            vehicleDetails.setVdId(((BigInteger) motorDetail[11]).longValue());
            vehicleDetailsList.add(vehicleDetails);
        }
        Page<VehicleDetails> page = new PageImpl<>(vehicleDetailsList, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public void createPolicyAddonsInfo(PolicyTrans created, PolicyCreateDTO request) {
        System.out.println("request is"+ request+ " policy id "+created.getPolicyId());

        //find if the policy exists if not create it
        PolicyMiscInfo policyMiscInfo = new PolicyMiscInfo();
        policyMiscInfo.setPolicyId(created.getPolicyId());

        if(request.getCover_option_fpp() != null ){
            policyMiscInfo.setCovername("family ");
            policyMiscInfo.setCoverOption(request.getCover_option_fpp());
        }
        if(request.getCover_option_up() != null){
            policyMiscInfo.setCovername("");
            policyMiscInfo.setCoverOption(request.getCover_option_up());
        }
        if(request.getCover_option_pa() != null){
            policyMiscInfo.setCovername("");
            policyMiscInfo.setCoverOption(request.getCover_option_pa());
        }

        if(request.getCover_option_en() != null){
            policyMiscInfo.setCovername("");
            policyMiscInfo.setCoverOption(request.getCover_option_en());
        }

        policyMiscInfoRepo.save(policyMiscInfo);

    }
}

