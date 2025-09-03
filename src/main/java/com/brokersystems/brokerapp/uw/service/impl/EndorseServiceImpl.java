
package com.brokersystems.brokerapp.uw.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.brokersystems.brokerapp.bulktransactions.models.ClientMigration;
import com.brokersystems.brokerapp.bulktransactions.repositories.ClientMigrationRepository;
import com.brokersystems.brokerapp.certs.model.PolicyCerts;
import com.brokersystems.brokerapp.certs.model.PrintCertificateQueue;
import com.brokersystems.brokerapp.certs.model.QPolicyCerts;
import com.brokersystems.brokerapp.certs.model.QPrintCertificateQueue;
import com.brokersystems.brokerapp.certs.repository.PolicyCertsRepo;
import com.brokersystems.brokerapp.certs.repository.PrintQueueRepo;
import com.brokersystems.brokerapp.dms.service.UploadService;
import com.brokersystems.brokerapp.life.model.*;
import com.brokersystems.brokerapp.life.model.QPolicyAcruals;
import com.brokersystems.brokerapp.life.repository.*;
import com.brokersystems.brokerapp.medical.model.*;
import com.brokersystems.brokerapp.medical.repository.*;
import com.brokersystems.brokerapp.quotes.model.QQuoteProTrans;
import com.brokersystems.brokerapp.quotes.model.QuoteProTrans;
import com.brokersystems.brokerapp.quotes.repository.QuotProductsRepo;
import com.brokersystems.brokerapp.schedules.model.QScheduleTrans;
import com.brokersystems.brokerapp.schedules.model.ScheduleTrans;
import com.brokersystems.brokerapp.schedules.repository.ScheduleTransRepo;
import com.brokersystems.brokerapp.server.utils.*;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.*;
import com.brokersystems.brokerapp.trans.model.*;
import com.brokersystems.brokerapp.trans.repository.SystemTransactionsTempRepo;
import com.brokersystems.brokerapp.trans.repository.TransChecksRepo;
import com.brokersystems.brokerapp.users.model.MakerChecker;
import com.brokersystems.brokerapp.users.model.QMakerChecker;
import com.brokersystems.brokerapp.users.repository.MakerCheckerRepo;
import com.brokersystems.brokerapp.uw.dtos.*;
import com.brokersystems.brokerapp.uw.mappers.EndorsementsDtoMapper;
import com.brokersystems.brokerapp.uw.model.*;
import com.brokersystems.brokerapp.uw.repository.*;
import com.brokersystems.brokerapp.workflow.docs.DocType;
import com.brokersystems.brokerapp.workflow.docs.QSysWfDocs;
import com.brokersystems.brokerapp.workflow.docs.SysWfDocs;
import com.brokersystems.brokerapp.workflow.repository.SysWfDocsRepo;
import com.brokersystems.brokerapp.workflow.utils.WorkflowService;
import com.google.common.collect.Maps;
import groovy.util.logging.Slf4j;
import org.activiti.engine.RuntimeService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.easybatch.core.job.Job;
import org.easybatch.core.job.JobBuilder;
import org.easybatch.core.job.JobExecutor;
import org.easybatch.core.job.JobReport;
import org.easybatch.core.processor.RecordProcessor;
import org.easybatch.core.reader.IterableRecordReader;
import org.easybatch.core.record.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.brokersystems.brokerapp.enums.RevenueItems;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.exception.EndorsementsException;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.brokersystems.brokerapp.trans.repository.SystemTransRepo;
import com.brokersystems.brokerapp.trans.service.PolicyAuthorization;
import com.brokersystems.brokerapp.uw.service.EndorseService;
import com.brokersystems.brokerapp.uw.service.PolicyTransService;
import com.brokersystems.brokerapp.uw.service.PremComputeService;
import com.brokersystems.brokerapp.uw.validators.RenewalJobListener;
import com.mysema.query.types.Predicate;

import javax.sql.DataSource;

@Service
public class EndorseServiceImpl implements EndorseService {
    private static final Logger log = LoggerFactory.getLogger(EndorseServiceImpl.class);

    @Autowired
    private PolicyTransRepo policyRepo;

    @Autowired
    private PolTaxesRepo polTaxesRepo;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private PolClausesRepo polClausesRepo;

    @Autowired
    private PolicyInstallmentsRepo policyInstallmentsRepo;


    @Autowired
    private UserUtils userUtils;


    @Autowired
    private PolActiveRisksRepo activeRisksRepo;

    @Autowired
    private RiskTransRepo riskTransRepo;

    @Autowired
    private SectionTransRepo sectionTransRepo;

    @Autowired
    private SystemTransRepo transRepo;

    @Autowired
    private ParamService paramService;

    @Autowired
    private DateUtilities dateUtils;

    @Autowired
    private PolicyAccrualPayRepo policyAccrualPayRepo;

    @Autowired
    private PremComputeService premComputeService;

    @Autowired
    private PolicyAuthorization polAuthService;

    @Autowired
    private PolicyTransService polTransService;

    @Autowired
    private PolicyRemarksRepo policyRemarksRepo;

    @Autowired
    private PrintQueueRepo queueRepo;
    @Autowired
    private PolicyCertsRepo certsRepo;

    @Autowired
    private ProductsRepo productRepo;

    @Autowired
    private MedicalCategoryRepo categoryRepo;

    @Autowired
    private CategoryMembersRepo memberRepo;

    @Autowired
    private SelfFundParamsRepo selfFundParamsRepo;

    @Autowired
    private CategoryRulesRepo rulesRepo;
    @Autowired
    private CategoryBenefitRepo benefitRepo;

    @Autowired
    private CatExclusionsRepo exclusionsRepo;

    @Autowired
    private CatLoadingRepo loadingRepo;

    @Autowired
    private CatProvidersRepo providersRepo;

    @Autowired
    private MedicalCardsCrudRepo cardsRepo;

    @Autowired
    private CategoryClausesRepo categoryClausesRepo;

    @Autowired
    private ScheduleTransRepo scheduleTransRepo;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private SysWfDocsRepo sysWfDocsRepo;

    @Autowired
    private RiskDocsRepo riskDocsRepo;

    @Autowired
    private TransChecksRepo transChecksRepo;

    @Autowired
    private BinderReqrdDocsRepo reqrdDocsRepo;

    @Autowired
    private SubclassReqDocRepo subclassReqDocRepo;

    @Autowired
    private RiskIntPartiesRepo intPartiesRepo;

    @Autowired
    private PolicyBeneficiariesRepo beneficiariesRepo;

    @Autowired
    private MakerCheckerRepo makerCheckerRepo;

    @Autowired
    private QuotProductsRepo quotProductsRepo;

    @Autowired
    private CategoryMemberBenefitsRepo memberBenefitsRepo;

    @Autowired
    private PremComputeService premiumService;

    @Autowired
    private PolicyBindersRepo policyBindersRepo;

    @Autowired
    private DataSource dataSource;
    @Autowired
    private PolicyBenefitsDistributionRepo policyBenefitsDistributionRepo;

    @Autowired
    private EscalationRecordRepository escalationRecordRepository;

    @Autowired
    private EscalationActivityLoggerRepository escalationActivityLoggerRepository;

    @Autowired
    private SystemTransactionsTempRepo systemTransactionsTempRepo;

    @Autowired
    private RiskImportLogRepo riskImportLogRepo;

    @Autowired
    private UploadService uploadService;
    @Autowired
    private RiskTransRepo riskRepo;
    @Autowired
    private LifeReceiptsRepo lifeReceiptsRepo;


//	@Override
//	@Transactional(readOnly = true)
//	public DataTablesResult<PolicyTrans> findActivePolicyTrans(DataTablesRequest request, String drNumber,
//			String clientName, String polNo, String endorseNumber,String agentName, String endorseType) throws IllegalAccessException {
//		QClientDef client = QPolicyTrans.policyTrans.client;
//		QAccountDef account = (QPolicyTrans.policyTrans.agent ==null)? QPolicyTrans.policyTrans.binder.account:QPolicyTrans.policyTrans.agent;
//		Predicate pred = null;
//
//		if (clientName == null || StringUtils.isBlank(clientName)) {
//			clientName = "";
//		}
//		if (drNumber == null || StringUtils.isBlank(drNumber)) {
//			drNumber = "";
//		}
//
//		if (polNo == null || StringUtils.isBlank(polNo)) {
//			polNo = "";
//		}
//
//		if (endorseNumber == null || StringUtils.isBlank(endorseNumber)) {
//			endorseNumber = "";
//		}
//		pred = QPolicyTrans.policyTrans.currentStatus.eq("A")
//				.and(QPolicyTrans.policyTrans.polNo.containsIgnoreCase(polNo))
//				.and(QPolicyTrans.policyTrans.riskTrans.any().riskShtDesc.containsIgnoreCase(endorseNumber))
//				.and(QPolicyTrans.policyTrans.refNo.containsIgnoreCase(drNumber))
//				.and(QPolicyTrans.policyTrans.product.proGroup.prgType.notIn("MD","L"))
//				.and(account.name.containsIgnoreCase(agentName))
//				.and(client.fname.containsIgnoreCase(clientName).or(client.otherNames.containsIgnoreCase(clientName)));
//		if(endorseType!=null && "EX".equalsIgnoreCase(endorseType)){
//			pred = QPolicyTrans.policyTrans.currentStatus.eq("A")
//					.and(QPolicyTrans.policyTrans.polNo.containsIgnoreCase(polNo))
//					.and(QPolicyTrans.policyTrans.riskTrans.any().riskShtDesc.containsIgnoreCase(endorseNumber))
//					.and(QPolicyTrans.policyTrans.refNo.containsIgnoreCase(drNumber))
//					.and(QPolicyTrans.policyTrans.renewable.eq(false))
//					.and(QPolicyTrans.policyTrans.product.proGroup.prgType.notIn("MD","L"))
//					.and(account.name.containsIgnoreCase(agentName))
//					.and(client.fname.containsIgnoreCase(clientName).or(client.otherNames.containsIgnoreCase(clientName)));
//		}
//		Page<PolicyTrans> page = policyRepo.findAll(pred, request);
//		return new DataTablesResult(request, page);
//	}

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<EndorsementsDTO> findActivePolicyTrans(DataTablesRequest request, String drNumber,
                                                                   Long clientCode, String polNo, String riskId, Long agentCode, String endorseType)
            throws IllegalAccessException {
        Boolean renewable = null;
        if ("EX".equalsIgnoreCase(endorseType)) {
            renewable = false;
        }
        if ("RN".equalsIgnoreCase(endorseType)) {
            renewable = true;
        }
        if (drNumber == null || StringUtils.isBlank(drNumber)) {
            drNumber = "%";
        }

        if (polNo == null || StringUtils.isBlank(polNo)) {
            polNo = "%";
        }

        if (riskId == null || StringUtils.isBlank(riskId)) {
            riskId = "%";
        }
        if(agentCode==null){
            agentCode = -2000L;
        }
        if(clientCode==null){
            clientCode = -2000L;
        }
        List<Object[]> activePolicyList = policyRepo.getActivePolicies(polNo,riskId,drNumber,agentCode,clientCode, request.getPageNumber(), request.getPageSize());
        long countPolicies = 0L;

        if(activePolicyList != null && !activePolicyList.isEmpty() && activePolicyList.get(0).length > 16){
            countPolicies = ((BigInteger)activePolicyList.get(0)[16]).longValue();
        } else {
            System.err.println("Error: Unable to retrieve countPolicies. Array is empty or too short.");
        }
        List<EndorsementsDTO> endorsementsDTOList = new ArrayList<>();
        for(Object[] activePolicy : activePolicyList){
            final Long policyId = ((BigInteger)activePolicy[3]).longValue();
            final Boolean renewableState = ((Boolean)activePolicy[8]);
            final Long uwYear = ((Integer)activePolicy[6]).longValue();
            String authComments = "";
            EndorsementsDTO endorsementsDTO =  EndorsementsDTO.instance (policyId,(String)activePolicy[0],(String)activePolicy[1],(String)activePolicy[2],
                    (String)activePolicy[4],(String)activePolicy[5],(String)activePolicy[7],uwYear, renewableState,(String)activePolicy[9], (Date)activePolicy[10],(Date)activePolicy[11],
                    (String)activePolicy[12],(String)activePolicy[13],countPolicies, (String) activePolicy[15], authComments, activePolicy[15] != null ? (String) activePolicy[15] : "N/A");
            endorsementsDTOList.add(endorsementsDTO);
        }
        Page<EndorsementsDTO> page = new PageImpl<>(endorsementsDTOList, request, countPolicies);
        return new DataTablesResult(request, page);
    }

    @Override
    public DataTablesResult<EndorsementsDTO> findCancelledActivePolicyTrans(DataTablesRequest request, String drNumber, Long clientCode, String polNo, String riskId, Long agentCode, String endorseType) throws IllegalAccessException {

        if (drNumber == null || StringUtils.isBlank(drNumber)) {
            drNumber = "%";
        }

        if (polNo == null || StringUtils.isBlank(polNo)) {
            polNo = "%";
        }

        if (riskId == null || StringUtils.isBlank(riskId)) {
            riskId = "%";
        }
        if(agentCode==null){
            agentCode = -2000L;
        }
        if(clientCode==null){
            clientCode = -2000L;
        }
        List<Object[]> activePolicyList = policyRepo.getCancelledPolicies(polNo,riskId,drNumber,agentCode,clientCode, request.getPageNumber(), request.getPageSize());
        long countPolicies = 0L;

        if(activePolicyList != null && !activePolicyList.isEmpty() && activePolicyList.get(0).length > 16){
            countPolicies = ((BigInteger)activePolicyList.get(0)[16]).longValue();
        } else {
            System.err.println("Error: Unable to retrieve countPolicies. Array is empty or too short.");
        }
        List<EndorsementsDTO> endorsementsDTOList = new ArrayList<>();
        for(Object[] activePolicy : activePolicyList){
            final Long policyId = ((BigInteger)activePolicy[3]).longValue();
            final Boolean renewableState = ((Boolean)activePolicy[8]);
            final Long uwYear = ((Integer)activePolicy[6]).longValue();
            String authComments = "";
            EndorsementsDTO endorsementsDTO =  EndorsementsDTO.instance (policyId,(String)activePolicy[0],(String)activePolicy[1],(String)activePolicy[2],
                    (String)activePolicy[4],(String)activePolicy[5],(String)activePolicy[7],uwYear, renewableState,(String)activePolicy[9], (Date)activePolicy[10],(Date)activePolicy[11],
                    (String)activePolicy[12],(String)activePolicy[13],countPolicies, (String) activePolicy[15], authComments, activePolicy[15] != null ? (String) activePolicy[15] : "N/A");
            endorsementsDTOList.add(endorsementsDTO);
        }
        Page<EndorsementsDTO> page = new PageImpl<>(endorsementsDTOList, request, countPolicies);
        return new DataTablesResult(request, page);
    }

    //	@Override
//	@Transactional(readOnly = true)
//	public DataTablesResult<PolicyTrans> findActiveMedicalPolicyTrans(DataTablesRequest request, String drNumber, String clientName, String polNo, String endorseNumber, String agentName) throws IllegalAccessException {
//		QClientDef client = QPolicyTrans.policyTrans.client;
//		QAccountDef account = QPolicyTrans.policyTrans.agent;
//		Predicate pred = null;
//
//		if (clientName == null || StringUtils.isBlank(clientName)) {
//			clientName = "";
//		}
//		if (drNumber == null || StringUtils.isBlank(drNumber)) {
//			drNumber = "";
//		}
//
//		if (polNo == null || StringUtils.isBlank(polNo)) {
//			polNo = "";
//		}
//
//		if (endorseNumber == null || StringUtils.isBlank(endorseNumber)) {
//			endorseNumber = "";
//		}
//		pred = QPolicyTrans.policyTrans.currentStatus.eq("A")
//				.and(QPolicyTrans.policyTrans.polNo.containsIgnoreCase(polNo))
//				//.and(QPolicyTrans.policyTrans.riskTrans.any().riskShtDesc.containsIgnoreCase(endorseNumber))
//				.and(QPolicyTrans.policyTrans.refNo.containsIgnoreCase(drNumber))
//				.and(QPolicyTrans.policyTrans.product.proGroup.prgType.eq("MD"))
//				.and(account.name.containsIgnoreCase(agentName))
//				.and(client.fname.containsIgnoreCase(clientName).or(client.otherNames.containsIgnoreCase(clientName)));
//		Page<PolicyTrans> page = policyRepo.findAll(pred, request);
//		return new DataTablesResult(request, page);
//	}
    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<EndorsementsDTO> findActiveMedicalPolicyTrans(DataTablesRequest request, String drNumber, Long clientCode, String polNo, String endorseNumber, Long agentCode) throws IllegalAccessException {
        if (drNumber == null || StringUtils.isBlank(drNumber)) {
            drNumber = null;
        }

        if (polNo == null || StringUtils.isBlank(polNo)) {
            polNo = null;
        }

        if (endorseNumber == null || StringUtils.isBlank(endorseNumber)) {
            endorseNumber = null;
        }
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        EndorsementsDtoMapper mapper = new EndorsementsDtoMapper();
        long countPolicies = jdbcTemplate.queryForObject(EndorsementPolicyQueries.countMedEndorsementsQuery, Long.class,
                new Object[]{polNo, drNumber, agentCode, clientCode});
        List<EndorsementsDTO> endorsementsDTOList = jdbcTemplate.query(EndorsementPolicyQueries.getMedEndorsementsQuery, mapper,
                new Object[]{polNo, drNumber, agentCode, clientCode,
                        request.getPageNumber(), request.getPageSize(), request.getPageNumber(), request.getPageSize()});
        Page<EndorsementsDTO> page = new PageImpl<>(endorsementsDTOList, request, countPolicies);
        return new DataTablesResult(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<PolicyTrans> findActiveLifePolicyTrans(DataTablesRequest request, String clientName, String polNo, String agentName) throws IllegalAccessException {
        Predicate pred = null;

        if (clientName == null || StringUtils.isBlank(clientName)) {
            clientName = "";
        }
//		if (drNumber == null || StringUtils.isBlank(drNumber)) {
//			drNumber = "";
//		}

        if (polNo == null || StringUtils.isBlank(polNo)) {
            polNo = "";
        }

//		if (endorseNumber == null || StringUtils.isBlank(endorseNumber)) {
//			endorseNumber = "";
//		}
        pred = QPolicyTrans.policyTrans.currentStatus.eq("A")
                .and(QPolicyTrans.policyTrans.polNo.containsIgnoreCase(polNo))
                //.and(QPolicyTrans.policyTrans.riskTrans.any().riskShtDesc.containsIgnoreCase(endorseNumber))
                //.and(QPolicyTrans.policyTrans.refNo.containsIgnoreCase(drNumber))
                .and(QPolicyTrans.policyTrans.product.proGroup.prgType.eq("L"))
                .and(QPolicyTrans.policyTrans.agent.name.containsIgnoreCase(agentName))
                .and(QPolicyTrans.policyTrans.client.fname.concat(QPolicyTrans.policyTrans.client.otherNames).containsIgnoreCase(clientName));
        System.out.println("client: " + clientName);
        System.out.println("pred=" + pred.toString());
        Page<PolicyTrans> page = policyRepo.findAll(pred, request);
        return new DataTablesResult(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countUnauthTransactions(String policyNumber) {
        if (policyNumber == null) return 0L;
        Predicate pred = QPolicyTrans.policyTrans.polNo.eq(policyNumber)
                .and(QPolicyTrans.policyTrans.currentStatus.in("D","R")
                        .and(QPolicyTrans.policyTrans.businessType.in("N","L")));
        long count = policyRepo.count(pred);
        System.out.println("Count..."+count);
        return count;
    }


    @PreAuthorize("hasAnyAuthority('ENDORSE_POLICY')")
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = {EndorsementsException.class})
    public Long reinstatate(ReinstateForm reinstateForm) throws EndorsementsException {
        if (reinstateForm.getPolicyId() == null)
            throw new EndorsementsException("Policy to Revise cannot be Empty....");

        PolicyTrans currentTrans = policyRepo.findOne(reinstateForm.getPolicyId());

        Predicate activePred = QPolicyTrans.policyTrans.polNo.eq(currentTrans.getPolNo())
                .and(QPolicyTrans.policyTrans.currentStatus.eq("A"));

        if (countUnauthTransactions(currentTrans.getPolNo()) > 0) {
            throw new EndorsementsException(
                    "The Policy has unfinished Transaction");
        }

        if (policyRepo.count(activePred) > 1) {
            throw new EndorsementsException("The current Policy has an active Endorsement. Only One Active Endorsement can be allowed");
        }
        long endorsementCount = policyRepo.count(QPolicyTrans.policyTrans.polNo.eq(currentTrans.getPolNo())) + 1;

        PolicyTrans destination = new PolicyTrans();
        destination.setWefDate(currentTrans.getWefDate());
        destination.setWetDate(currentTrans.getWetDate());
        destination.setCoverFrom(currentTrans.getCoverFrom());
        destination.setCoverTo(currentTrans.getCoverTo());
        destination.setRenewalDate(currentTrans.getRenewalDate());
        destination.setUwYear(currentTrans.getUwYear());
        destination.setPolRevStatus("RE");
        if (currentTrans.getRevisionFormat() == null) {
            destination.setPolRevNo(currentTrans.getPolRevNo().substring(0, currentTrans.getPolRevNo().indexOf("/")) + "/" + endorsementCount);
        } else
            destination.setPolRevNo(currentTrans.getRevisionFormat() + "/" + endorsementCount);
        destination.setPolNo(currentTrans.getPolNo());
        destination.setAgent(currentTrans.getAgent());
        destination.setAuthStatus("D");
        destination.setAccrualInstDate(currentTrans.getAccrualInstDate());
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
        destination.setAdminFeeApplicable(currentTrans.getAdminFeeApplicable());
        destination.setBusinessType(currentTrans.getBusinessType());
        destination.setRevisionFormat(currentTrans.getRevisionFormat());
        destination.setTransCurrency(currentTrans.getTransCurrency());
        destination.setTransType("RE");
        destination.setSubAgent(currentTrans.getSubAgent());
        destination.setMarketerAgent(currentTrans.getMarketerAgent());
        destination.setIntroducerAgent(currentTrans.getIntroducerAgent());
        destination.setSumInsured(BigDecimal.ZERO);
        destination.setPrevEndosbasicPremium(currentTrans.getEndosbasicPremium());
        destination.setTotalInstalments(currentTrans.getTotalInstalments());
        if (currentTrans.getTotalInstalments() != null && currentTrans.getTotalInstalments() > 1) {
            destination.setInstallmentNo(currentTrans.getInstallmentNo() + 1);
        }
        SystemTrans transaction = new SystemTrans();
        transaction.setDoneDate(new Date());
        transaction.setDoneBy(userUtils.getCurrentUser());
        transaction.setPolicy(destination);
        transaction.setTransLevel("U");
        transaction.setTransCode("APD"); //A way to setup and look up for transaction transcode
        transaction.setTransAuthorised("N");
        Iterable<PolicyTaxes> taxes = polTaxesRepo.findAll(QPolicyTaxes.policyTaxes.policy.policyId.eq(currentTrans.getPolicyId()));
        List<PolicyTaxes> newTaxes = new ArrayList<>();
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

        Iterable<PolicyClauses> clauses = polClausesRepo.findAll(QPolicyClauses.policyClauses.policy.policyId.eq(currentTrans.getPolicyId()));
        List<PolicyClauses> newClauses = new ArrayList<>();
        for (PolicyClauses clause : clauses) {
            PolicyClauses newClause = new PolicyClauses();
            newClause.setClauHeading(clause.getClauHeading());
            newClause.setClause(clause.getClause());
            newClause.setClauWording(clause.getClauWording());
            newClause.setEditable(clause.isEditable());
            newClause.setNewClause("N");
            newClause.setPolicy(destination);
            newClauses.add(newClause);
        }


        List<Object[]> activeRisks = activeRisksRepo.getPolicyActRisks(reinstateForm.getPolicyId());
        List<PolicyActiveRisks> newActiveRisks = new ArrayList<>();
        for (Object[] activeRisk : activeRisks) {
            PolicyActiveRisks newActiveRisk = new PolicyActiveRisks();
            newActiveRisk.setPolicy(destination);
            newActiveRisk.setRisk(riskTransRepo.findOne(((BigInteger)activeRisk[4]).longValue()));
            newActiveRisk.setRiskIdentifier(((BigInteger)activeRisk[0]).longValue());
            newActiveRisk.setStatus("NR");
            newActiveRisks.add(newActiveRisk);

        }
        PolicyTrans savedPolicy = policyRepo.save(destination);
        polClausesRepo.save(newClauses);
        activeRisksRepo.save(newActiveRisks);
        polTaxesRepo.save(newTaxes);
        transRepo.save(transaction);
        workflowService.startNewWorkFlow(DocType.GEN_UW_DOCUMENT, String.valueOf(savedPolicy.getPolicyId()), savedPolicy, "N", null, null, null, null);
        Iterable<PolicyActiveRisks> endorsedRisks = activeRisksRepo.findAll(QPolicyActiveRisks.policyActiveRisks.policy.policyId.eq(savedPolicy.getPolicyId()));
        for (PolicyActiveRisks activeRisk : endorsedRisks) {
            try {
                endorseRisk(activeRisk.getArId(), "R", null);
            } catch (BadRequestException e) {
                throw new EndorsementsException(e.getMessage());
            }
        }
        final Iterable<RiskTrans> riskTrans = riskTransRepo.getRiskDetails(reinstateForm.getPolicyId());
        final Iterable<RiskTrans> currentRisks = riskTransRepo.getRiskDetails(savedPolicy.getPolicyId());
        for(RiskTrans cuurRisk: currentRisks){
            Optional<RiskTrans> prev = Streamable.streamOf(riskTrans).filter(a -> a.getRiskIdentifier().longValue()==cuurRisk.getRiskIdentifier().longValue()).findFirst();
            if(prev.isPresent()){
                cuurRisk.setButchargePrem(prev.get().getPremium().abs());
            }
            riskTransRepo.save(cuurRisk);

        }

        return savedPolicy.getPolicyId();
    }

    @PreAuthorize("hasAnyAuthority('ENDORSE_POLICY')")
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = {EndorsementsException.class})
    public Long reviseTransaction(RevisionForm revisionForm) throws EndorsementsException {


        if (revisionForm.getPolicyId() == null)
            throw new EndorsementsException("Policy to Revise cannot be Empty....");

        if (revisionForm.getRevisionType() == null || StringUtils.isBlank(revisionForm.getRevisionType()))
            throw new EndorsementsException("Select Revision Type...");

        PolicyTrans currentTrans = policyRepo.findOne(revisionForm.getPolicyId());
        if ("L".equalsIgnoreCase(currentTrans.getProduct().getProGroup().getPrgType()) ||
                "L".equalsIgnoreCase(currentTrans.getBusinessType())) {
            System.out.println("DEBUG: Blocking life policy from general module - Policy: " + currentTrans.getPolNo());
            throw new EndorsementsException("Life endorsements cannot happen in the general module. Please go to the Life Endorsements Module");
        }
        if ("RS".equalsIgnoreCase(revisionForm.getRevisionType())) {
            Long expiredSections = sectionTransRepo.count(QSectionTrans.sectionTrans.risk.policy.policyId.eq(currentTrans.getPolicyId())
                    .and(QSectionTrans.sectionTrans.expired.equalsIgnoreCase("Y")));
            if (expiredSections < 1) {
                throw new EndorsementsException("The Policy cannot be Reinstated as it has no expired sections");
            }
        }


        Predicate activePred = QPolicyTrans.policyTrans.polNo.eq(currentTrans.getPolNo())
                .and(QPolicyTrans.policyTrans.currentStatus.eq("A"));

        if (countUnauthTransactions(currentTrans.getPolNo()) > 0) {
            throw new EndorsementsException(
                    "The Policy has unfinished Transaction");
        }

        if (policyRepo.count(activePred) > 1) {
            throw new EndorsementsException("The current Policy has an active Endorsement. Only One Active Endorsement can be allowed");
        }

        if (!("EX".equalsIgnoreCase(revisionForm.getRevisionType()))) {
            if ("SP".equalsIgnoreCase(currentTrans.getTransType()))
                throw new EndorsementsException("Endorsement Type not Supported on Short Period Policy Transactions");
        }

        if ("EX".equalsIgnoreCase(revisionForm.getRevisionType())) {
            long extensionCount = policyRepo.count(QPolicyTrans.policyTrans.polNo.eq(currentTrans.getPolNo()).and(QPolicyTrans.policyTrans.transType.eq("EX")));
            try {
                long maxExtensions = paramService.getParamValue("MAX_EXTENSIONS").longValue();
                if (maxExtensions == extensionCount)
                    throw new EndorsementsException("The maximum number of extensions on this policy has reached. Cannot do Extension");
            } catch (BadRequestException e) {
                throw new EndorsementsException(e.getMessage());
            }
            if (currentTrans.isRenewable())
                throw new EndorsementsException("Extension is not allowed on renewable policies");
            if (revisionForm.getEffToDate() == null) {
                throw new EndorsementsException("Extension Cover to Date cannot be null");
            }

            if (revisionForm.getEffToDate().before(revisionForm.getEffectiveDate())) {
                throw new EndorsementsException("Effective to Date Cannot be Earlier than Effective from Date");
            }

            if (revisionForm.getEffToDate().before(currentTrans.getWefDate())) {
                throw new EndorsementsException("Effective to Date Cannot be Earlier than Current Policy Wef Date (" + dateUtils.formatDate(currentTrans.getWefDate()) + ")");
            }

            if (revisionForm.getEffectiveDate().before(currentTrans.getWetDate())) {
                throw new EndorsementsException("Effective to Date Cannot less than Previous Policy Wet Date(" + dateUtils.formatDate(currentTrans.getWetDate()) + ")");
            }

            Date extendDate = DateUtils.addDays(currentTrans.getWetDate(), 1);
            if (revisionForm.getEffectiveDate().after(extendDate) || revisionForm.getEffectiveDate().before(extendDate))
                throw new EndorsementsException("Effective Date Should be Date immediately After Wet Date (" + dateUtils.formatDate(extendDate) + ")");

        }
        if (!("EX".equalsIgnoreCase(revisionForm.getRevisionType()))) {
            if (revisionForm.getEffectiveDate().after(currentTrans.getWetDate()) ||
                    revisionForm.getEffectiveDate().before(currentTrans.getWefDate())) {
                throw new EndorsementsException("Effective Date "+revisionForm.getEffectiveDate()+" must be within Policy Period for Revision of Cover Transaction(" + dateUtils.formatDate(currentTrans.getWefDate()) + " to " + dateUtils.formatDate(currentTrans.getWetDate()) + ")");
            }
        }
        if (revisionForm.getRevisionType().equalsIgnoreCase("NB")
                || revisionForm.getRevisionType().equalsIgnoreCase("SP")) {
            throw new EndorsementsException("Endorsement Type not catered For. Contact the Vendor");
        }

        long endorsementCount = policyRepo.count(QPolicyTrans.policyTrans.polNo.eq(currentTrans.getPolNo())) + 1;

        PolicyTrans destination = new PolicyTrans();
        destination.setWefDate(revisionForm.getEffectiveDate());
        destination.setWetDate(currentTrans.getWetDate());
        if ("EX".equalsIgnoreCase(revisionForm.getRevisionType())) {
            destination.setCoverFrom(currentTrans.getCoverFrom());
            destination.setCoverTo(revisionForm.getEffToDate());
            destination.setWetDate(revisionForm.getEffToDate());
            if (currentTrans.isRenewable())
                destination.setRenewalDate(DateUtils.addDays(revisionForm.getEffToDate(), 1));
        } else {
            destination.setCoverFrom(currentTrans.getCoverFrom());
            destination.setCoverTo(currentTrans.getCoverTo());
            if (currentTrans.isRenewable()) {
                if ("CN".equalsIgnoreCase(revisionForm.getRevisionType()))
                    destination.setRenewalDate(currentTrans.getRenewalDate());
                else
                    destination.setRenewalDate(DateUtils.addDays(revisionForm.getEffToDate(), 1));
            }

        }
        destination.setAccrualInstDate(currentTrans.getAccrualInstDate());
        destination.setUwYear(currentTrans.getUwYear());
        destination.setPolRevStatus(revisionForm.getRevisionType());
        if (currentTrans.getRevisionFormat() == null) {
            destination.setPolRevNo(currentTrans.getPolRevNo().substring(0, currentTrans.getPolRevNo().indexOf("/")) + "/" + endorsementCount);
        } else
            destination.setPolRevNo(currentTrans.getRevisionFormat() + "/" + endorsementCount);
        destination.setPolNo(currentTrans.getPolNo());
        destination.setAgent(currentTrans.getAgent());
        if ("CN".equalsIgnoreCase(revisionForm.getRevisionType()) || "RS".equalsIgnoreCase(revisionForm.getRevisionType())) {
            destination.setAuthStatus("R");
        } else
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
        destination.setAdminFeeApplicable(currentTrans.getAdminFeeApplicable());
        destination.setBusinessType(currentTrans.getBusinessType());
        destination.setRevisionFormat(currentTrans.getRevisionFormat());
        destination.setTransCurrency(currentTrans.getTransCurrency());
        destination.setTransType(revisionForm.getRevisionType());
        destination.setSubAgent(currentTrans.getSubAgent());
        destination.setMarketerAgent(currentTrans.getMarketerAgent());
        destination.setIntroducerAgent(currentTrans.getIntroducerAgent());
        destination.setSumInsured(BigDecimal.ZERO);
        destination.setPrevEndosbasicPremium(currentTrans.getEndosbasicPremium());
        destination.setTotalInstalments(currentTrans.getTotalInstalments());
        if (currentTrans.getTotalInstalments() != null && currentTrans.getTotalInstalments() > 1) {
            destination.setInstallmentNo(currentTrans.getInstallmentNo() + 1);
        }
        Iterable<PolicyBinders> policyBinders = policyBindersRepo.findAll(QPolicyBinders.policyBinders.policyTrans.eq(currentTrans));
        List<PolicyBinders> newPolBinders = new ArrayList<>();
        for (PolicyBinders polBind : policyBinders) {

            PolicyBinders pb = new PolicyBinders();

            pb.setPrevBasicPrem(polBind.getBasicPrem());
            pb.setBinder(polBind.getBinder());
            pb.setPolicyTrans(destination);

            newPolBinders.add(pb);
        }

        SystemTrans transaction = new SystemTrans();
        transaction.setDoneDate(new Date());
        transaction.setDoneBy(userUtils.getCurrentUser());
        transaction.setPolicy(destination);
        transaction.setTransLevel("U");
        transaction.setTransCode("APD"); //A way to setup and look up for transaction transcode
        transaction.setTransAuthorised("N");
        Iterable<PolicyTaxes> taxes = polTaxesRepo.findAll(QPolicyTaxes.policyTaxes.policy.policyId.eq(currentTrans.getPolicyId()));
        List<PolicyTaxes> newTaxes = new ArrayList<>();
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

        Iterable<PolicyClauses> clauses = polClausesRepo.findAll(QPolicyClauses.policyClauses.policy.policyId.eq(currentTrans.getPolicyId()));
        List<PolicyClauses> newClauses = new ArrayList<>();
        for (PolicyClauses clause : clauses) {
            PolicyClauses newClause = new PolicyClauses();
            newClause.setClauHeading(clause.getClauHeading());
            newClause.setClause(clause.getClause());
            newClause.setClauWording(clause.getClauWording());
            newClause.setEditable(clause.isEditable());
            newClause.setNewClause("N");
            newClause.setPolicy(destination);
            newClauses.add(newClause);
        }


        List<Object[]> activeRisks = activeRisksRepo.getPolicyActRisks(revisionForm.getPolicyId());
        List<PolicyActiveRisks> newActiveRisks = new ArrayList<>();
        for (Object[] activeRisk : activeRisks) {
            PolicyActiveRisks newActiveRisk = new PolicyActiveRisks();
            newActiveRisk.setPolicy(destination);
            newActiveRisk.setRisk(riskTransRepo.findOne(((BigInteger)activeRisk[4]).longValue()));
            newActiveRisk.setRiskIdentifier(((BigInteger)activeRisk[0]).longValue());
            newActiveRisk.setStatus("NR");
            newActiveRisks.add(newActiveRisk);

        }
        PolicyTrans savedPolicy = policyRepo.save(destination);
        polClausesRepo.save(newClauses);
        activeRisksRepo.save(newActiveRisks);
        polTaxesRepo.save(newTaxes);
        policyBindersRepo.save(newPolBinders);
        transRepo.save(transaction);

        try {
            PolicyRemarks remarks = new PolicyRemarks();
            EndorsementRemarks  endorsementRemarks = policyRemarksRepo.findremarksByshtDesc(revisionForm.getRemarks().toUpperCase());//getEndorsementRemarks(savedPolicy.getPolicyId(), "", null);
            remarks.setEndRemarks(endorsementRemarks);
            remarks.setPolicy(savedPolicy);
            remarks.setRemarks(revisionForm.getRemarks().toUpperCase());
            polTransService.saveEndorsementRemarks(remarks);
        } catch (BadRequestException e) {
            throw new RuntimeException(e);
        }

        workflowService.startNewWorkFlow(DocType.GEN_UW_DOCUMENT, String.valueOf(savedPolicy.getPolicyId()), savedPolicy, "N", null, null, null, null);
        if ("CN".equalsIgnoreCase(revisionForm.getRevisionType()) || "RS".equalsIgnoreCase(revisionForm.getRevisionType()) || "EX".equalsIgnoreCase(revisionForm.getRevisionType())) {
            Iterable<PolicyActiveRisks> endorsedRisks = activeRisksRepo.findAll(QPolicyActiveRisks.policyActiveRisks.policy.policyId.eq(savedPolicy.getPolicyId()));
            for (PolicyActiveRisks activeRisk : endorsedRisks) {
                try {
                    endorseRisk(activeRisk.getArId(), "R", revisionForm.getAmount());
                } catch (BadRequestException e) {
                    throw new EndorsementsException(e.getMessage());
                }
            }
            Map<String, Object> processVariables = Maps.newHashMap();
            processVariables.put("canAuthorize", true);
            workflowService.completeTask(String.valueOf(savedPolicy.getPolicyId()), processVariables, savedPolicy, DocType.GEN_UW_DOCUMENT, "N", null, null,null, null);
        }
        return savedPolicy.getPolicyId();
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<PolicyEnquiryDTO> findUnauthorisedPolicies(DataTablesRequest request, String policyNumber) {
        List<Object[]> policiesList = policyRepo.findPendingTransactions(policyNumber, request.getPageNumber(), request.getPageSize());
        final List<PolicyEnquiryDTO> policyList = new ArrayList<>();
        long rowCount = 0l;
        if(!policiesList.isEmpty()) rowCount = ((BigInteger)policiesList.get(0)[8]).intValue();
        for (Object[] policy : policiesList) {
            final PolicyEnquiryDTO enquiryDTO = new PolicyEnquiryDTO();
            enquiryDTO.setProposalNo((String) policy[0]);
            enquiryDTO.setPolNo((String) policy[1]);
            enquiryDTO.setPolCreateddt((Date) policy[2]);
            enquiryDTO.setWef((Date) policy[3]);
            enquiryDTO.setWet((Date) policy[4]);
            enquiryDTO.setTransactionId(((BigInteger) policy[5]).longValue());
            enquiryDTO.setCurrentStatus((String) policy[6]);
            enquiryDTO.setUser((String) policy[7]);
            policyList.add(enquiryDTO);
        }
        Page<PolicyEnquiryDTO> page = new PageImpl<>(policyList, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    private void reDeleteRiskDocs(Long polCode, Long bindCode) throws BadRequestException{
        final String search = "%";
        if (bindCode == null) {
            bindCode = -2000L;
        }
        List<Object[]> risks = riskRepo.findLifePolicyRisks(search.toLowerCase(), polCode, bindCode, 0, 1000);

        if(!risks.isEmpty()) {
            for (Object[] risk : risks) {
                Long riskId = ((BigInteger) risk[0]).longValue(); // riskid
                System.out.println("deleting risk id " + riskId);

                List<Object[]> riskDocs = riskDocsRepo.getAllRiskDocs(riskId, 0, 1000);
                if(!riskDocs.isEmpty()) {
                    for (Object[] doc : riskDocs) {
                        Long rdId = (((BigInteger) doc[1]).longValue());
                        System.out.println("deleting risk doc "+rdId+ "of riskID"+riskId);
                        uploadService.deleteRiskDoc(rdId);
                    }
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void deletePolicyRecord(Long polCode, boolean renewals) throws BadRequestException {
        if (polCode == null) throw new BadRequestException("Cannot Delete A null Transaction");
        PolicyTrans currentPolicy = policyRepo.findOne(polCode);
        quotProductsRepo.updatePolicyStatusToNull("N",polCode);
        if (renewals) {
            PolicyTrans prevTrans = currentPolicy.getPreviousTrans();
            System.out.println("Current Policy.."+currentPolicy.getPolicyId()+" Prev Policy "+prevTrans.getPolicyId());
            if (currentPolicy.getPolicyId() == prevTrans.getPolicyId())
                throw new BadRequestException("There was an Error Deleting the Renewal Transaction..Contact Administrator");
            if ("A".equalsIgnoreCase(currentPolicy.getAuthStatus()))
                throw new BadRequestException("Cannot Delete an Authorised Transaction");
            prevTrans.setRenewed(false);
            policyRepo.save(prevTrans);
        }

        if ("A".equalsIgnoreCase(currentPolicy.getAuthStatus()))
            throw new BadRequestException("Cannot Delete an Authorised Transaction");
        Iterable<PolicyTaxes> policyTaxes = polTaxesRepo.findAll(QPolicyTaxes.policyTaxes.policy.policyId.eq(polCode));
        polTaxesRepo.delete(policyTaxes);

        Iterable<PolicyInstallments> policyInstallments = policyInstallmentsRepo.findAll(QPolicyInstallments.policyInstallments.policyTrans.policyId.eq(polCode));
        policyInstallmentsRepo.delete(policyInstallments);

        Iterable<PolicyActiveRisks> activeRisks = activeRisksRepo.findAll(QPolicyActiveRisks.policyActiveRisks.policy.policyId.eq(polCode));
        activeRisksRepo.delete(activeRisks);

        Iterable<PolicyClauses> clauses = polClausesRepo.findAll(QPolicyClauses.policyClauses.policy.policyId.eq(polCode));
        polClausesRepo.delete(clauses);

        Iterable<TransChecks> transCheckses = transChecksRepo.findAll(QTransChecks.transChecks.policyTrans.policyId.eq(polCode));
        transChecksRepo.delete(transCheckses);

        Iterable<PolicyRemarks> remarks = policyRemarksRepo.findAll(QPolicyRemarks.policyRemarks.policy.policyId.eq(polCode));
        policyRemarksRepo.delete(remarks);

        Iterable<CategoryMembers> members = memberRepo.findAll(QCategoryMembers.categoryMembers.category.policy.policyId.eq(polCode));
        for (CategoryMembers member : members) {
            Iterable<MedicalCards> cards = cardsRepo.findAll(QMedicalCards.medicalCards.member.sectId.eq(member.getSectId()));
            for (MedicalCards card : cards) {
                if (card.getPrevCard() != null) {
                    MedicalCards prevCard = cardsRepo.findOne(QMedicalCards.medicalCards.cardId.eq(card.getPrevCard().getCardId()));
                    prevCard.setStatus("Dispatched");
                    cardsRepo.save(prevCard);
                }
            }
            cardsRepo.delete(cards);
            Iterable<RiskDocs> riskDocs = riskDocsRepo.findAll(QRiskDocs.riskDocs.member.sectId.eq(member.getSectId()));
            riskDocsRepo.delete(riskDocs);

            Iterable<CategoryMemberBenefits> memberBenefits = memberBenefitsRepo.findAll(QCategoryMemberBenefits.categoryMemberBenefits.member.sectId.eq(member.getSectId()));
            memberBenefitsRepo.delete(memberBenefits);
        }

        memberRepo.delete(members);

        Iterable<SelfFundParams> selfFunds = selfFundParamsRepo.findAll(QSelfFundParams.selfFundParams.policyTrans.policyId.eq(polCode));
        selfFundParamsRepo.delete(selfFunds);
        Iterable<CategoryRules> rules = rulesRepo.findAll(QCategoryRules.categoryRules.category.policy.policyId.eq(polCode));
        rulesRepo.delete(rules);
        Iterable<MedCategoryBenefits> benefits = benefitRepo.findAll(QMedCategoryBenefits.medCategoryBenefits.category.policy.policyId.eq(polCode));
        benefitRepo.delete(benefits);

        Iterable<CategoryExclusions> exclusions = exclusionsRepo.findAll(QCategoryExclusions.categoryExclusions.category.policy.policyId.eq(polCode));
        exclusionsRepo.delete(exclusions);

        Iterable<CategoryLoadings> loadings = loadingRepo.findAll(QCategoryLoadings.categoryLoadings.category.policy.policyId.eq(polCode));
        loadingRepo.delete(loadings);

        Iterable<CatalogueProviders> providers = providersRepo.findAll(QCatalogueProviders.catalogueProviders.category.policy.policyId.eq(polCode));

        providersRepo.delete(providers);

        Iterable<CategoryClauses> categoryClauses = categoryClausesRepo.findAll(QCategoryClauses.categoryClauses.category.policy.policyId.eq(polCode));
        categoryClausesRepo.delete(categoryClauses);

        Iterable<MedicalCategory> categories = categoryRepo.findAll(QMedicalCategory.medicalCategory.policy.policyId.eq(polCode));
        categoryRepo.delete(categories);

        reDeleteRiskDocs(polCode, null);
        Iterable<RiskTrans> risks = riskTransRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(polCode));
        System.out.println("Risks Size..."+risks.spliterator().getExactSizeIfKnown());
        for (RiskTrans risk : risks) {
            Iterable<PolicyActiveRisks> policyActiveRisks = activeRisksRepo.findAll(QPolicyActiveRisks.policyActiveRisks.risk.riskId.eq(risk.getRiskId()));
            activeRisksRepo.delete(policyActiveRisks);
            Iterable<SectionTrans> sections = sectionTransRepo.findAll(QSectionTrans.sectionTrans.risk.riskId.eq(risk.getRiskId()));
            sectionTransRepo.delete(sections);
            Iterable<PrintCertificateQueue> certificateQueues = queueRepo.findAll(QPrintCertificateQueue.printCertificateQueue.risk.riskId.eq(risk.getRiskId()));
            queueRepo.delete(certificateQueues);
            Iterable<PolicyCerts> policyCerts = certsRepo.findAll(QPolicyCerts.policyCerts.risk.riskId.eq(risk.getRiskId()));
            certsRepo.delete(policyCerts);
            Iterable<ScheduleTrans> schedules = scheduleTransRepo.findAll(QScheduleTrans.scheduleTrans.risk.riskId.eq(risk.getRiskId()));
            scheduleTransRepo.delete(schedules);
            Iterable<RiskDocs> riskDocs = riskDocsRepo.findAll(QRiskDocs.riskDocs.risk.riskId.eq(risk.getRiskId()));
            riskDocsRepo.delete(riskDocs);
            Iterable<RiskDocs> riskDocsByPolicy = riskDocsRepo.findAll(QRiskDocs.riskDocs.risk.policy.policyId.eq(polCode));
            riskDocsRepo.delete(riskDocsByPolicy);
            Iterable<RiskInterestedParties> interestedParties = intPartiesRepo.findAll(QRiskInterestedParties.riskInterestedParties.risk.riskId.eq(risk.getRiskId()));
            intPartiesRepo.delete(interestedParties);
            System.out.println("deleted above items ");
            riskTransRepo.delete(risk);
            System.out.println("deleting the risk now");
        }


        Iterable<PolicyBinders> policyBinders = policyBindersRepo.findAll(QPolicyBinders.policyBinders.policyTrans.policyId.eq(polCode));
        policyBindersRepo.delete(policyBinders);

        Iterable<SystemTrans> trans = transRepo.findAll(QSystemTrans.systemTrans.policy.policyId.eq(polCode));
        transRepo.delete(trans);

        Iterable<SysWfDocs> wfDocs = sysWfDocsRepo.findAll(QSysWfDocs.sysWfDocs.policyTrans.policyId.eq(polCode));
        sysWfDocsRepo.delete(wfDocs);

        Iterable<PolicyBeneficiaries> beneficiaries = beneficiariesRepo.findAll(QPolicyBeneficiaries.policyBeneficiaries.policy.policyId.eq(polCode));
        beneficiariesRepo.delete(beneficiaries);

        Iterable<PolicyBenefitsDistribution> benefitsDistributions = policyBenefitsDistributionRepo.findAll(QPolicyBenefitsDistribution.policyBenefitsDistribution.policyId.policyId.eq(polCode));
        policyBenefitsDistributionRepo.delete(benefitsDistributions);

        Iterable<MakerChecker> makerCheckers = makerCheckerRepo.findAll(QMakerChecker.makerChecker.policyId.eq(polCode));
        if(makerCheckers != null){
            System.out.println("polcode"+ polCode + " "+ makerCheckers);
            for (MakerChecker makerChecker : makerCheckers) {
                System.out.println("deleting "+ makerChecker.getId());

                escalationActivityLoggerRepository.deleteByTaskId(makerChecker.getId());
                escalationRecordRepository.deleteByTaskId(makerChecker.getId());
            }
            makerCheckerRepo.delete(makerCheckers);
        }
        // Delete PolicyAcruals records to avoid foreign key violation
        Iterable<PolicyAcruals> accruals = policyAccrualPayRepo.findAll(QPolicyAcruals.policyAcruals.policyTrans.eq(polCode));
        policyAccrualPayRepo.delete(accruals);

        // Delete temp transactions that reference this policy
        Iterable<SystemTransactionsTemp> tempTransactions = systemTransactionsTempRepo.findAll(QSystemTransactionsTemp.systemTransactionsTemp.policy.policyId.eq(polCode));
        systemTransactionsTempRepo.delete(tempTransactions);

        Iterable<RiskImportationLog> logTransactions = riskImportLogRepo.findAll(QRiskImportationLog.riskImportationLog.policyTrans.policyId.eq(polCode));
        riskImportLogRepo.delete(logTransactions);

        try {
            policyRepo.delete(polCode); //This is where FK violation occurs
        } catch (DataIntegrityViolationException ex) {
            String message = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();

            if (message != null && message.contains("sys_brk_risk_docs")) {
                throw new BadRequestException("Cannot delete this policy transaction because it has related documents attached to risks. Please delete the documents first.");
            }

            if (message != null && message.contains("sys_brk_risk")) {
                throw new BadRequestException("Cannot delete the endorsement once active");
            }

            // fallback generic FK message
            throw new BadRequestException("Delete failed due to related records. Please remove dependencies before deleting.");
        }

    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientsDto> findActiveInsureds(String searchValue, Pageable pageable, Long polCode) {
        if(searchValue==null) searchValue="%%";
        else searchValue = "%"+searchValue+"%";
        List<Object[]> insureds = activeRisksRepo.findActiveRiskInsured(polCode,searchValue, pageable.getPageNumber(), pageable.getPageSize());
        long rowCount = 0L;
        if(!insureds.isEmpty()) rowCount = ((BigInteger)insureds.get(0)[2]).intValue();
        final List<ClientsDto> pageInsureds = new ArrayList<>();
        for(Object[] insured : insureds) {
            final ClientsDto pagedInsured = new ClientsDto();
            pagedInsured.setTenId(((BigInteger)insured[0]).longValue());
            pagedInsured.setFname((String) insured[1]);
            pageInsureds.add(pagedInsured);
        }
        return new PageImpl<>(pageInsureds,pageable, rowCount);
    }

    @Override
    public Long findRiskExpiredSections(Long polCode) throws BadRequestException {

        PolicyTrans policyTrans = policyRepo.findOne(polCode);

        Long expiredSections = sectionTransRepo.count(QSectionTrans.sectionTrans.risk.policy.policyId.eq(policyTrans.getPreviousTrans().getPolicyId()).and(QSectionTrans.sectionTrans.expired.equalsIgnoreCase("Y")));

//		if (expiredSections < 1){
//			throw new BadRequestException("The Policy cannot be Reinstated as it has no expired sections");
//		}

//		PolicyActiveRisks policyActiveRisks = activeRisksRepo.findOne(QPolicyActiveRisks.policyActiveRisks.policy.policyId.eq(polCode));
//
//        endorseRisk(policyActiveRisks.getArId(),"RS",amount);

        return expiredSections;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RiskTrans> findRenewalRisks(String paramString, Pageable paramPageable) {
        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred = QRiskTrans.riskTrans.isNotNull();
        } else {
            pred = QRiskTrans.riskTrans.riskShtDesc.containsIgnoreCase(paramString);
        }
        return riskTransRepo.findAll(pred, paramPageable);
    }

    @Override
    public PolicyTrans getErrorsPol(Long policyId) {
        Predicate predicate = QPolicyTrans.policyTrans.policyId.eq(policyId);
        return policyRepo.findOne(predicate);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<PolicyActiveRisks> findActiveRisks(DataTablesRequest request, Long insured, String riskId, Long polCode)
            throws IllegalAccessException {
        Page<PolicyActiveRisks> page = null;
        if (insured == null) {
            page = activeRisksRepo.getUnendorsedRisks(polCode, riskId, request);
        } else {
            page = activeRisksRepo.getUnendorsedRisksByInsured(insured, polCode, riskId, request);
        }
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class}, propagation = Propagation.REQUIRED)
    public void endorseRisk(Long activeRiskCode, String endorseType, BigDecimal amount) throws BadRequestException {

        PolicyActiveRisks activeRisk = activeRisksRepo.getActiveRisks(activeRiskCode);
        if (activeRisk == null) {
            throw new BadRequestException("Error Endorsing the Risk..Active Risk Details not found for Policy ");
        }
        Set<RiskDocs> riskDocs = new HashSet<>();
        RiskTrans risk = activeRisk.getRisk();

        if (risk == null) {
            throw new BadRequestException("Error Endorsing the Risk..Risk Details not found for Policy " + activeRisk.getPolicy().getPolNo());
        }

        PolicyTrans newPolicy = activeRisk.getPolicy();

        boolean cashBasis = newPolicy.getInterfaceType() != null && "C".equalsIgnoreCase(newPolicy.getInterfaceType());

        if (newPolicy == null) {
            throw new BadRequestException("Error Endorsing the Risk..Policy Details not found for Policy " + activeRisk.getPolicy().getPolNo());
        }
//        if (!cashBasis && endorseType.equalsIgnoreCase("E")) {
//            throw new BadRequestException("Endorse Type not Supported");
//        }

        long count = riskTransRepo.count(QRiskTrans.riskTrans.riskIdentifier.eq(risk.getRiskIdentifier()).and(QRiskTrans.riskTrans.policy.policyId.eq(newPolicy.getPolicyId())));

        if (count > 0 || activeRisk.getPrevRisk() != null)
            throw new BadRequestException("The Risk has already been endorsed for Policy " + activeRisk.getPolicy().getPolNo());

        RiskTrans newRisk = new RiskTrans();
        newRisk.setBinder(risk.getBinder());
        newRisk.setBinderDetails(risk.getBinderDetails());
        newRisk.setCommRate(risk.getCommRate());
        newRisk.setCovertype(risk.getCovertype());
        newRisk.setInsured(risk.getInsured());
        newRisk.setPolicy(newPolicy);
        if(newPolicy.getBusinessType().equalsIgnoreCase("L")){ //to handle life mising working age
            newRisk.setWorkingAge(risk.getWorkingAge());
            newRisk.setComputeType(risk.getComputeType());
        }
        newRisk.setProrata(risk.getProrata());
        newRisk.setRiskDesc(risk.getRiskDesc());
        newRisk.setRiskIdentifier(activeRisk.getRiskIdentifier());
        newRisk.setRiskShtDesc(risk.getRiskShtDesc());
        newRisk.setSubclass(risk.getSubclass());
        newRisk.setPending(true);
        newRisk.setUwYear(risk.getUwYear());
        if (risk.getPolicyBinders() != null) {
            PolicyBinders polbinder = policyBindersRepo.findOne(QPolicyBinders.policyBinders.binder.binId.eq(risk.getBinder().getBinId())
                    .and(QPolicyBinders.policyBinders.policyTrans.policyId.eq(newPolicy.getPolicyId())));
            newRisk.setPolicyBinders(polbinder);
        }

        if (endorseType.equalsIgnoreCase("R") && amount != null) {
            newRisk.setButchargePrem(amount);
        }
        newRisk.setWefDate(newPolicy.getWefDate());
        newRisk.setWetDate(newPolicy.getWetDate());
        newRisk.setTransType("EN");
        if ( endorseType.equalsIgnoreCase("E")) {
            BinderDetails coverTypes = risk.getBinderDetails();
            String distribution = coverTypes.getDistribution();
            if(coverTypes.getDistribution()!=null){
                System.out.println("Installment No "+risk.getInstallmentNo());
                System.out.println("Total Installments "+newPolicy.getTotalInstalments());
                if (risk.getInstallmentNo().intValue() == newPolicy.getTotalInstalments()) {
                    throw new BadRequestException("Cannot Extend the Installment Risk further...The policy has reached its maximum extensions");
                }
                newRisk.setInstallmentNo(risk.getInstallmentNo() + 1);

                String installment = "";
                if (distribution != null && distribution.contains(":")) {
                    installment = distribution.split(":")[risk.getInstallmentNo().intValue()];
                } else installment = distribution;

                if(installment==null){
                    throw new BadRequestException("Installment Cannot be null...");
                }

                BigDecimal installPerc = new BigDecimal(installment);
                if ((installPerc.add(risk.getTotalPercentage())).compareTo(BigDecimal.valueOf(100l)) == 0) {
                    newRisk.setWefDate(org.apache.commons.lang.time.DateUtils.addDays(risk.getWetDate(), 1));
                    newRisk.setWetDate(newPolicy.getWetDate());
                    newPolicy.setTotalInstalments(risk.getInstallmentNo().intValue() + 1);
                    newRisk.setInstallmentPerc(installment);
                    newRisk.setTotalPercentage(BigDecimal.valueOf(100));
                    newRisk.setPrevPercentage(new BigDecimal(installment));

                } else if ((installPerc.add(risk.getTotalPercentage())).compareTo(BigDecimal.valueOf(100l)) < 0) {
                    newRisk.setWefDate(org.apache.commons.lang.time.DateUtils.addDays(risk.getWetDate(), 1));
                    newRisk.setWetDate(org.apache.commons.lang.time.DateUtils.addDays(org.apache.commons.lang.time.DateUtils.addMonths(risk.getWefDate(), 1), -1));
                    newRisk.setInstallmentPerc(installment);
                    newRisk.setTotalPercentage(installPerc.add(risk.getTotalPercentage()));
                    newRisk.setPrevPercentage(new BigDecimal(installment));
                } else if ((installPerc.add(risk.getTotalPercentage())).compareTo(BigDecimal.valueOf(100l)) > 0) {
                    BigDecimal remainder = BigDecimal.valueOf(100).subtract(risk.getTotalPercentage());
                    newRisk.setTotalPercentage(BigDecimal.valueOf(100));
                    newRisk.setWefDate(org.apache.commons.lang.time.DateUtils.addDays(risk.getWetDate(), 1));
                    newRisk.setWetDate(newPolicy.getWetDate());
                    newRisk.setInstallmentPerc(String.valueOf(remainder));
                    newRisk.setPrevPercentage(remainder);
                }
            }
            newRisk.setTransType("EX");
        }
        Iterable<BinderReqrdDocs> reqdDocs = reqrdDocsRepo.findAll(QBinderReqrdDocs.binderReqrdDocs.binderDetail.detId.eq(newRisk.getBinderDetails().getDetId())
                .and(QBinderReqrdDocs.binderReqrdDocs.mandatory.eq(true))
                .and(QBinderReqrdDocs.binderReqrdDocs.requiredDocs.requiredDoc.appliesEndorsement.eq(true)));
        riskDocs = Streamable.streamOf(reqdDocs).filter(reqdDoc -> riskDocsRepo.count(QRiskDocs.riskDocs.risk.riskIdentifier.eq(newRisk.getRiskIdentifier()).and(QRiskDocs.riskDocs.reqdDocs.sclReqrdId.eq(reqdDoc.getRequiredDocs().getSclReqrdId()))) == 0)
                .map(reqdDoc -> {
                    RiskDocs riskDoc = new RiskDocs();
                    riskDoc.setReqdDocs(reqdDoc.getRequiredDocs());
                    riskDoc.setRisk(newRisk);
                    return riskDoc;
                }).collect(Collectors.toSet());


        Iterable<SubClassReqdDocs> subClassReqDocs = subclassReqDocRepo.findAll(QSubClassReqdDocs.subClassReqdDocs.subclass.subId.eq(newRisk.getBinderDetails().getSubCoverTypes().getSubclass().getSubId())
                .and(QSubClassReqdDocs.subClassReqdDocs.mandatory.eq(true))
                .and(QSubClassReqdDocs.subClassReqdDocs.requiredDoc.appliesEndorsement.eq(true)));
        riskDocs.addAll(Streamable.streamOf(subClassReqDocs).filter(reqdDoc -> riskDocsRepo.count(QRiskDocs.riskDocs.risk.riskIdentifier.eq(newRisk.getRiskIdentifier()).and(QRiskDocs.riskDocs.reqdDocs.sclReqrdId.eq(reqdDoc.getSclReqrdId()))) == 0)
                .map(reqdDoc -> {
                    RiskDocs riskDoc = new RiskDocs();
                    riskDoc.setReqdDocs(reqdDoc);
                    riskDoc.setRisk(newRisk);
                    return riskDoc;
                }).collect(Collectors.toSet()));

        Iterable<SectionTrans> sections = sectionTransRepo.findAll(QSectionTrans.sectionTrans.risk.riskId.eq(risk.getRiskId()));
        List<SectionTrans> newSections = Streamable.streamOf(sections).map(section -> {
            if(section==null){
                try {
                    throw new BadRequestException("Error Section cannot be null...");
                } catch (BadRequestException e) {
                    throw new RuntimeException(e);
                }
            }
            SectionTrans newSection = new SectionTrans();
            newSection.setAmount(section.getAmount());
            newSection.setCompute(true);
            newSection.setDivFactor(section.getDivFactor());
            newSection.setFreeLimit(section.getFreeLimit());
            newSection.setMultiRate(section.getMultiRate());
            newSection.setPremRates(section.getPremRates());
            newSection.setRate(section.getRate());
            newSection.setRisk(newRisk);
            newSection.setSection(section.getSection());
            newSection.setRisk(newRisk);
            newSection.setExpired("N");
            newSection.setPrevSection(section);
            return newSection;
        }).filter(a -> a != null).collect(Collectors.toList());

        activeRisk.setPrevRisk(risk);
        activeRisk.setRisk(newRisk);
        activeRisk.setStatus("ER");
        RiskTrans savedRisk = riskTransRepo.save(newRisk);
        riskDocsRepo.save(riskDocs);
        sectionTransRepo.save(newSections);
        Iterable<RiskInterestedParties> interestedParties = intPartiesRepo.findAll(QRiskInterestedParties.riskInterestedParties.risk.riskId.eq(risk.getRiskId()));
        List<RiskInterestedParties> parties = Streamable.streamOf(interestedParties).map(intParty -> {
            RiskInterestedParties party = new RiskInterestedParties();
            party.setInterestedParties(intParty.getInterestedParties());
            party.setRisk(savedRisk);
            return party;
        }).filter(a -> a != null).collect(Collectors.toList());
        intPartiesRepo.save(parties);
        activeRisksRepo.save(activeRisk);
    }

//	@Override
//	@Transactional(readOnly = true)
//	public DataTablesResult<PolicyTrans> findActiveAndCancelledTrans(DataTablesRequest request, String drNumber,
//			String clientName, String polNo, String endorseNumber, String agentName) throws IllegalAccessException {
//		QClientDef client = QPolicyTrans.policyTrans.client;
//		QAccountDef account = (QPolicyTrans.policyTrans.agent ==null)? QPolicyTrans.policyTrans.binder.account:QPolicyTrans.policyTrans.agent;
//		Predicate pred = null;
//
//		if (clientName == null || StringUtils.isBlank(clientName)) {
//			clientName = "";
//		}
//		if (drNumber == null || StringUtils.isBlank(drNumber)) {
//			drNumber = "";
//		}
//
//		if (polNo == null || StringUtils.isBlank(polNo)) {
//			polNo = "";
//		}
//
//		if (endorseNumber == null || StringUtils.isBlank(endorseNumber)) {
//			endorseNumber = "";
//		}
//		pred = (QPolicyTrans.policyTrans.currentStatus.eq("A").or(QPolicyTrans.policyTrans.currentStatus.eq("CN")))
//				.and(QPolicyTrans.policyTrans.polNo.containsIgnoreCase(polNo))
//				.and(QPolicyTrans.policyTrans.riskTrans.any().riskShtDesc.containsIgnoreCase(endorseNumber))
//				.and(QPolicyTrans.policyTrans.refNo.containsIgnoreCase(drNumber))
//				.and(account.name.containsIgnoreCase(agentName))
//				.and(client.fname.containsIgnoreCase(clientName).or(client.otherNames.containsIgnoreCase(clientName)));
//		Page<PolicyTrans> page = policyRepo.findAll(pred, request);
//		return new DataTablesResult(request, page);
//	}


    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<EndorsementsDTO> findActiveAndCancelledTrans(DataTablesRequest pageable, String drNumber,
                                                                         String clientName, String polNo, String riskId, String agentName) throws IllegalAccessException {
        if (clientName == null || StringUtils.isBlank(clientName)) {
            clientName = null;
        }
        if (drNumber == null || StringUtils.isBlank(drNumber)) {
            drNumber = null;
        }

        if (polNo == null || StringUtils.isBlank(polNo)) {
            polNo = null;
        }

        if (riskId == null || StringUtils.isBlank(riskId)) {
            riskId = null;
        }
        if (agentName == null || StringUtils.isBlank(agentName)) {
            agentName = null;
        }

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        EndorsementsDtoMapper mapper = new EndorsementsDtoMapper();
        long countPolicies = jdbcTemplate.queryForObject(EndorsementPolicyQueries.countContraPoliciesQuery, Long.class,
                new Object[]{polNo, riskId, drNumber, agentName, clientName, clientName, clientName, clientName, clientName});
        List<EndorsementsDTO> endorsementsDTOList = jdbcTemplate.query(EndorsementPolicyQueries.getContraPoliciesQuery, mapper,
                new Object[]{polNo, riskId, drNumber, agentName, clientName, clientName, clientName, clientName, clientName,
                        pageable.getPageNumber(), pageable.getPageSize(), pageable.getPageNumber(), pageable.getPageSize()});
        Page<EndorsementsDTO> page = new PageImpl<>(endorsementsDTOList, pageable, countPolicies);
        return new DataTablesResult(pageable, page);
    }
    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<EndorsementsDTO> findActiveAndCancelledLifeTrans(DataTablesRequest pageable, String drNumber,
                                                                         String clientName, String polNo, String riskId, String agentName) throws IllegalAccessException {
        if (clientName == null || StringUtils.isBlank(clientName)) {
            clientName = null;
        }
        if (drNumber == null || StringUtils.isBlank(drNumber)) {
            drNumber = null;
        }

        if (polNo == null || StringUtils.isBlank(polNo)) {
            polNo = null;
        }

        if (riskId == null || StringUtils.isBlank(riskId)) {
            riskId = null;
        }
        if (agentName == null || StringUtils.isBlank(agentName)) {
            agentName = null;
        }

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        EndorsementsDtoMapper mapper = new EndorsementsDtoMapper();
        long countPolicies = jdbcTemplate.queryForObject(EndorsementPolicyQueries.countContraPoliciesQuery, Long.class,
                new Object[]{polNo, riskId, drNumber, agentName, clientName, clientName, clientName, clientName, clientName});
        List<EndorsementsDTO> endorsementsDTOList = jdbcTemplate.query(EndorsementPolicyQueries.getContraLifePoliciesQuery, mapper,
                new Object[]{polNo, riskId, drNumber, agentName, clientName, clientName, clientName, clientName, clientName,
                        pageable.getPageNumber(), pageable.getPageSize(), pageable.getPageNumber(), pageable.getPageSize()});
        Page<EndorsementsDTO> page = new PageImpl<>(endorsementsDTOList, pageable, countPolicies);
        return new DataTablesResult(pageable, page);
    }

    @PreAuthorize("hasAnyAuthority('REVERSE_POLICY')")
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = {EndorsementsException.class})
    public Long contraPolicy(RevisionForm revisionForm) throws EndorsementsException {
        if (revisionForm.getPolicyId() == null || revisionForm.getPolicyId() == null)
            throw new EndorsementsException("Policy to Contra cannot be Empty....");

        if (revisionForm.getRevisionType() == null || StringUtils.isBlank(revisionForm.getRevisionType()))
            throw new EndorsementsException("Select Revision Type...");


        PolicyTrans currentTrans = policyRepo.findOne(revisionForm.getPolicyId());



        Predicate activePred = QPolicyTrans.policyTrans.polNo.eq(currentTrans.getPolNo())
                .and(QPolicyTrans.policyTrans.currentStatus.eq("A"));

        if (countUnauthTransactions(currentTrans.getPolNo()) > 0) {
            throw new EndorsementsException(
                    "The Policy has unfinished Transaction");
        }

        if (policyRepo.count(activePred) > 1) {
            throw new EndorsementsException("The current Policy has an active Endorsement. Only One Active Endorsement can be allowed");
        }

        if (revisionForm.getRevisionType().equalsIgnoreCase("NB")
                || revisionForm.getRevisionType().equalsIgnoreCase("SP")) {
            throw new EndorsementsException("Endorsement Type not catered For. Contact the Vendor");
        }

        long endorsementCount = policyRepo.count(QPolicyTrans.policyTrans.polNo.eq(currentTrans.getPolNo())) + 1;

        PolicyTrans destination = new PolicyTrans();
        destination.setWefDate(currentTrans.getWefDate());
        destination.setWetDate(currentTrans.getWetDate());
        destination.setCoverFrom(currentTrans.getCoverFrom());
        destination.setCoverTo(currentTrans.getCoverTo());
        destination.setRenewalDate(currentTrans.getRenewalDate());
        destination.setUwYear(currentTrans.getUwYear());
        destination.setPolRevStatus(revisionForm.getRevisionType());
        if (currentTrans.getRevisionFormat() == null) {
            destination.setPolRevNo(currentTrans.getPolRevNo().substring(0, currentTrans.getPolRevNo().indexOf("/")) + "/" + endorsementCount);
        } else
            destination.setPolRevNo(currentTrans.getRevisionFormat() + "/" + endorsementCount);
        destination.setPolNo(currentTrans.getPolNo());
        destination.setAgent(currentTrans.getAgent());
        destination.setAuthStatus("R");
        destination.setAccrualInstDate(currentTrans.getAccrualInstDate());
        destination.setBinder(currentTrans.getBinder());
        destination.setBranch(currentTrans.getBranch());
        destination.setClient(currentTrans.getClient());
        destination.setClientPolNo(currentTrans.getClientPolNo());
        destination.setCommAllowed(currentTrans.isCommAllowed());
        destination.setCreatedUser(userUtils.getCurrentUser());
        destination.setCurrentStatus("D");
        destination.setRevisionFormat(currentTrans.getRevisionFormat());
        destination.setAdminFeeApplicable(currentTrans.getAdminFeeApplicable());
        destination.setFrequency(currentTrans.getFrequency());
        destination.setInterfaceType(currentTrans.getInterfaceType());
        destination.setOldpolNo(currentTrans.getOldpolNo());
        destination.setPaymentMode(currentTrans.getPaymentMode());
        destination.setPolCreateddt(new Date());
        destination.setPreviousTrans(currentTrans);
        destination.setProduct(currentTrans.getProduct());
        destination.setRenewable(currentTrans.isRenewable());
        destination.setBusinessType(currentTrans.getBusinessType());
        destination.setTransCurrency(currentTrans.getTransCurrency());
        destination.setTransType(revisionForm.getRevisionType());
        destination.setBasicPrem(currentTrans.getBasicPrem().negate());
        destination.setCommAmt(currentTrans.getCommAmt().negate());
        if (currentTrans.getEndosbasicPremium() != null)
            destination.setEndosbasicPremium(currentTrans.getEndosbasicPremium().negate());
        if (currentTrans.getEndosCommissions() != null)
            destination.setEndosCommissions(currentTrans.getEndosCommissions().negate());
        destination.setExtras(currentTrans.getExtras().negate());
        destination.setFuturePrem(currentTrans.getFuturePrem().abs());
        destination.setNetPrem(currentTrans.getNetPrem().negate());
        destination.setPhcf(currentTrans.getPhcf().negate());
        destination.setPremium(currentTrans.getPremium().negate());
        destination.setStampDuty(currentTrans.getStampDuty().negate());
        destination.setSumInsured(currentTrans.getSumInsured().negate());
        destination.setTrainingLevy(currentTrans.getTrainingLevy().negate());
        destination.setWhtx(currentTrans.getWhtx().negate());
        destination.setSubAgent(currentTrans.getSubAgent());

        Iterable<PolicyBinders> policyBinders = policyBindersRepo.findAll(QPolicyBinders.policyBinders.policyTrans.eq(currentTrans));
        List<PolicyBinders> newPolBinders = new ArrayList<>();
        for (PolicyBinders polBind : policyBinders) {
            PolicyBinders pb = new PolicyBinders();

            pb.setBasicPrem(polBind.getBasicPrem() == null ? BigDecimal.ZERO : polBind.getBasicPrem().negate());
            pb.setCommission(polBind.getCommission() == null ? BigDecimal.ZERO : polBind.getCommission().negate());
            pb.setPhcf(polBind.getPhcf() == null ? BigDecimal.ZERO : polBind.getPhcf().negate());
            pb.setTl(polBind.getTl() == null ? BigDecimal.ZERO : polBind.getTl().negate());
            pb.setWhtx(polBind.getWhtx() == null ? BigDecimal.ZERO : polBind.getWhtx().negate());
            pb.setSd(polBind.getSd() == null ? BigDecimal.ZERO : polBind.getSd().negate());
            pb.setPrevBasicPrem(polBind.getBasicPrem());
            pb.setBinder(polBind.getBinder());
            pb.setPolicyTrans(destination);

            newPolBinders.add(pb);
        }

        SystemTrans transaction = new SystemTrans();
        transaction.setDoneDate(new Date());
        transaction.setDoneBy(userUtils.getCurrentUser());
        transaction.setPolicy(destination);
        transaction.setTransLevel("U");
        transaction.setTransCode("APC"); //A way to setup and look up for transaction transcode
        transaction.setTransAuthorised("N");
        Iterable<PolicyTaxes> taxes = polTaxesRepo.findAll(QPolicyTaxes.policyTaxes.policy.policyId.eq(currentTrans.getPolicyId()));
        List<PolicyTaxes> newTaxes = new ArrayList<>();
        for (PolicyTaxes tax : taxes) {
            PolicyTaxes newTax = new PolicyTaxes();
            newTax.setDivFactor(tax.getDivFactor());
            newTax.setPolicy(destination);
            newTax.setRateType(tax.getRateType());
            newTax.setRevenueItems(tax.getRevenueItems());
            newTax.setSubclass(tax.getSubclass());
            newTax.setTaxLevel(tax.getTaxLevel());
            newTax.setTaxRate(tax.getTaxRate());
            newTax.setTaxAmount(tax.getTaxAmount().negate());
            newTaxes.add(newTax);
        }

        Iterable<PolicyClauses> clauses = polClausesRepo.findAll(QPolicyClauses.policyClauses.policy.policyId.eq(currentTrans.getPolicyId()));
        List<PolicyClauses> newClauses = new ArrayList<>();
        for (PolicyClauses clause : clauses) {
            PolicyClauses newClause = new PolicyClauses();
            newClause.setClauHeading(clause.getClauHeading());
            newClause.setClause(clause.getClause());
            newClause.setClauWording(clause.getClauWording());
            newClause.setEditable(clause.isEditable());
            newClause.setNewClause("N");
            newClause.setPolicy(destination);
            newClauses.add(newClause);
        }
        Iterable<PolicyActiveRisks> activeRisks = activeRisksRepo.findAll(QPolicyActiveRisks.policyActiveRisks.policy.policyId.eq(revisionForm.getPolicyId()));
        List<PolicyActiveRisks> newActiveRisks = new ArrayList<>();
        for (PolicyActiveRisks activeRisk : activeRisks) {
            PolicyActiveRisks newActiveRisk = new PolicyActiveRisks();
            newActiveRisk.setPolicy(destination);
            newActiveRisk.setRisk(activeRisk.getRisk());
            newActiveRisk.setRiskIdentifier(activeRisk.getRiskIdentifier());
            newActiveRisks.add(newActiveRisk);
        }
        PolicyTrans savedPolicy = policyRepo.save(destination);
        polClausesRepo.save(newClauses);
        polTaxesRepo.save(newTaxes);
        policyBindersRepo.save(newPolBinders);
        activeRisksRepo.save(newActiveRisks);
        transRepo.save(transaction);


        Iterable<PolicyActiveRisks> endorsedRisks = activeRisksRepo.findAll(QPolicyActiveRisks.policyActiveRisks.policy.policyId.eq(savedPolicy.getPolicyId()));
            for (PolicyActiveRisks activeRisk : endorsedRisks) {
                try {
                    endorseContraRisk(activeRisk.getArId());
                } catch (BadRequestException e) {
                    throw new EndorsementsException(e.getMessage());
                }
            }
            workflowService.startNewWorkFlow(DocType.GEN_UW_DOCUMENT, String.valueOf(+savedPolicy.getPolicyId()), savedPolicy, "N", null, null, null, null);
            Map<String, Object> processVariables = Maps.newHashMap();
            processVariables.put("canAuthorize", true);
            workflowService.completeTask(String.valueOf(savedPolicy.getPolicyId()), processVariables, savedPolicy, DocType.GEN_UW_DOCUMENT, "N", null, null,null, null);
            return savedPolicy.getPolicyId();
        }
    private void reverseBeneficiaries(Long originalPolicyId, PolicyTrans contraPolicy) {
        Iterable<PolicyBeneficiaries> originalBeneficiaries =
                beneficiariesRepo.findAll(QPolicyBeneficiaries.policyBeneficiaries.policy.policyId.eq(originalPolicyId));

        List<PolicyBeneficiaries> contraBeneficiaries = new ArrayList<>();

        log.info("Reversing beneficiaries for policy ID: {}", originalPolicyId);

        for (PolicyBeneficiaries originalBen : originalBeneficiaries) {
            PolicyBeneficiaries contraBen = new PolicyBeneficiaries();

            // Required fields
            contraBen.setPolicy(contraPolicy);
            contraBen.setUser(userUtils.getCurrentUser());
            contraBen.setDateCreated(new Date());

            // Copy beneficiary information
            contraBen.setBeneficiaryName(originalBen.getBeneficiaryName());
            contraBen.setBeneficiarypostalAddress(originalBen.getBeneficiarypostalAddress());
            contraBen.setBeneficiaryemailAddress(originalBen.getBeneficiaryemailAddress());
            contraBen.setBeneficiarytelNo(originalBen.getBeneficiarytelNo());
            contraBen.setBeneficiaryregNo(originalBen.getBeneficiaryregNo());
            contraBen.setDateRegistered(originalBen.getDateRegistered());

            // Copy relationship
            contraBen.setRelationshipType(originalBen.getRelationshipType());
            contraBen.setBeneficiaryRelationship(originalBen.getBeneficiaryRelationship());

            // Copy allocation (keeping positive for audit trail)
            contraBen.setBenAllocation(originalBen.getBenAllocation());

            contraBeneficiaries.add(contraBen);

            log.info("Copied beneficiary: {} with allocation: {}%",
                    originalBen.getBeneficiaryName(),
                    originalBen.getBenAllocation());
        }

        if (!contraBeneficiaries.isEmpty()) {
            beneficiariesRepo.save(contraBeneficiaries);
            log.info("Saved {} contra beneficiaries for policy {}",
                    contraBeneficiaries.size(),
                    contraPolicy.getPolNo());
        } else {
            log.info("No beneficiaries found to reverse for policy ID: {}", originalPolicyId);
        }
    }



    @PreAuthorize("hasAnyAuthority('REVERSE_POLICY')")
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = {EndorsementsException.class})
    public Long contraLifePolicy(RevisionForm revisionForm) throws EndorsementsException {
        if (revisionForm.getPolicyId() == null)
            throw new EndorsementsException("Policy to Contra cannot be Empty....");

        if (revisionForm.getRevisionType() == null || StringUtils.isBlank(revisionForm.getRevisionType()))
            throw new EndorsementsException("Select Revision Type...");

        PolicyTrans currentTrans = policyRepo.findOne(revisionForm.getPolicyId());

        if(this.countUnauthTransactions(currentTrans.getPolNo()) > 0){
            Predicate pred = QPolicyTrans.policyTrans.polNo.eq(currentTrans.getPolNo())
                    .and(QPolicyTrans.policyTrans.currentStatus.eq("D")
                            .and(QPolicyTrans.policyTrans.businessType.in("N","L")));
            Iterable<PolicyTrans> policyTransList = policyRepo.findAll(pred);
            for(PolicyTrans policyTrans : policyTransList){
                try {
                    this.deletePolicyRecord(policyTrans.getPolicyId(),false);
                } catch (BadRequestException e) {
                    throw new EndorsementsException(e.getMessage());
                }
            }
        }

        // LIFE-SPECIFIC: Check multiple installments restriction
        if (currentTrans.getPaidInsts() != null && currentTrans.getPaidInsts() > 1) {
            throw new EndorsementsException("Cannot reverse policy: Multiple installment payments (" +
                    currentTrans.getPaidInsts() + ") have been made for this life insurance policy.");
        }

        // Standard validations
        Predicate activePred = QPolicyTrans.policyTrans.polNo.eq(currentTrans.getPolNo())
                .and(QPolicyTrans.policyTrans.currentStatus.in("A"));

        if (countUnauthTransactions(currentTrans.getPolNo()) > 0) {
            throw new EndorsementsException("The Policy has unfinished Transaction");
        }

        if (policyRepo.count(activePred) > 1) {
            throw new EndorsementsException("The current Policy has an active Endorsement. Only One Active Endorsement can be allowed");
        }

        if (revisionForm.getRevisionType().equalsIgnoreCase("NB") || revisionForm.getRevisionType().equalsIgnoreCase("SP")) {
            throw new EndorsementsException("Endorsement Type not catered For. Contact the Vendor");
        }

        long endorsementCount = policyRepo.count(QPolicyTrans.policyTrans.polNo.eq(currentTrans.getPolNo())) + 1;

        // Create contra policy
        PolicyTrans destination = new PolicyTrans();

        // Copy standard fields
        destination.setWefDate(currentTrans.getWefDate());
        destination.setWetDate(currentTrans.getWetDate());
        destination.setCoverFrom(currentTrans.getCoverFrom());
        destination.setCoverTo(currentTrans.getCoverTo());
        destination.setRenewalDate(currentTrans.getRenewalDate());
        destination.setUwYear(currentTrans.getUwYear());
        destination.setPolRevStatus(revisionForm.getRevisionType());

        // Handle revision number
        if (currentTrans.getRevisionFormat() == null) {
            destination.setPolRevNo(currentTrans.getPolRevNo().substring(0, currentTrans.getPolRevNo().indexOf("/")) + "/" + endorsementCount);
        } else {
            destination.setPolRevNo(currentTrans.getRevisionFormat() + "/" + endorsementCount);
        }

        destination.setPolNo(currentTrans.getPolNo());
        destination.setAgent(currentTrans.getAgent());
        destination.setAuthStatus("R");
        destination.setBinder(currentTrans.getBinder());
        destination.setBranch(currentTrans.getBranch());
        destination.setClient(currentTrans.getClient());
        destination.setClientPolNo(currentTrans.getClientPolNo());
        destination.setCommAllowed(currentTrans.isCommAllowed());
        destination.setCreatedUser(userUtils.getCurrentUser());
        destination.setCurrentStatus("D");
        destination.setRevisionFormat(currentTrans.getRevisionFormat());
        destination.setAdminFeeApplicable(currentTrans.getAdminFeeApplicable());
        destination.setFrequency(currentTrans.getFrequency());
        destination.setInterfaceType(currentTrans.getInterfaceType());
        destination.setOldpolNo(currentTrans.getOldpolNo());
        destination.setPaymentMode(currentTrans.getPaymentMode());
        destination.setPolCreateddt(new Date());
        destination.setPreviousTrans(currentTrans);
        destination.setProduct(currentTrans.getProduct());
        destination.setRenewable(currentTrans.isRenewable());
        destination.setBusinessType(currentTrans.getBusinessType());
        destination.setTransCurrency(currentTrans.getTransCurrency());
        destination.setTransType(revisionForm.getRevisionType());

        // LIFE-SPECIFIC: Copy life policy fields
        destination.setProposalNo(currentTrans.getProposalNo());
        destination.setPolTerm(currentTrans.getPolTerm());
        destination.setInvestmentTerm(currentTrans.getInvestmentTerm());
        destination.setTotalInstalments(currentTrans.getTotalInstalments());

        // Copy agent/intermediary fields
        destination.setSubAgent(currentTrans.getSubAgent());
        destination.setMarketerAgent(currentTrans.getMarketerAgent());
        destination.setIntroducerAgent(currentTrans.getIntroducerAgent());
        destination.setLeadsMan(currentTrans.getLeadsMan());
        destination.setAbsaNoLeadsMan(currentTrans.getAbsaNoLeadsMan());
        destination.setAbsaNoMarketer(currentTrans.getAbsaNoMarketer());
        destination.setAbsaNoIntroducer(currentTrans.getAbsaNoIntroducer());
        destination.setAbsaNoSubAgent(currentTrans.getAbsaNoSubAgent());

        // Financial reversals
        destination.setBasicPrem(currentTrans.getBasicPrem().negate());
        destination.setCommAmt(currentTrans.getCommAmt().negate());
        if (currentTrans.getEndosbasicPremium() != null)
            destination.setEndosbasicPremium(currentTrans.getEndosbasicPremium().negate());
        if (currentTrans.getEndosCommissions() != null)
            destination.setEndosCommissions(currentTrans.getEndosCommissions().negate());
        destination.setExtras(currentTrans.getExtras().negate());
        destination.setFuturePrem(currentTrans.getFuturePrem().abs());
        destination.setNetPrem(currentTrans.getNetPrem().negate());
        destination.setPhcf(currentTrans.getPhcf().negate());
        destination.setPremium(currentTrans.getPremium().negate());
        destination.setStampDuty(currentTrans.getStampDuty().negate());
        destination.setSumInsured(currentTrans.getSumInsured().negate());
        destination.setTrainingLevy(currentTrans.getTrainingLevy().negate());
        destination.setWhtx(currentTrans.getWhtx().negate());

        // LIFE-SPECIFIC: Reverse negotiated premium
        if (currentTrans.getNegotiatedPremium() != null) {
            destination.setNegotiatedPremium(currentTrans.getNegotiatedPremium().negate());
        }

        // Handle policy binders (same as general contra)
        Iterable<PolicyBinders> policyBinders = policyBindersRepo.findAll(QPolicyBinders.policyBinders.policyTrans.eq(currentTrans));
        List<PolicyBinders> newPolBinders = new ArrayList<>();
        for (PolicyBinders polBind : policyBinders) {
            PolicyBinders pb = new PolicyBinders();
            pb.setBasicPrem(polBind.getBasicPrem() == null ? BigDecimal.ZERO : polBind.getBasicPrem().negate());
            pb.setCommission(polBind.getCommission() == null ? BigDecimal.ZERO : polBind.getCommission().negate());
            pb.setPhcf(polBind.getPhcf() == null ? BigDecimal.ZERO : polBind.getPhcf().negate());
            pb.setTl(polBind.getTl() == null ? BigDecimal.ZERO : polBind.getTl().negate());
            pb.setWhtx(polBind.getWhtx() == null ? BigDecimal.ZERO : polBind.getWhtx().negate());
            pb.setSd(polBind.getSd() == null ? BigDecimal.ZERO : polBind.getSd().negate());
            pb.setPrevBasicPrem(polBind.getBasicPrem());
            pb.setBinder(polBind.getBinder());
            pb.setPolicyTrans(destination);
            newPolBinders.add(pb);
        }

        // Create system transaction
        SystemTrans transaction = new SystemTrans();
        transaction.setDoneDate(new Date());
        transaction.setDoneBy(userUtils.getCurrentUser());
        transaction.setPolicy(destination);
        transaction.setTransLevel("U");
        transaction.setTransCode("APC"); // A way to setup and look up for transaction transcode
        transaction.setTransAuthorised("N");

        // Handle policy taxes (same as general contra)
        Iterable<PolicyTaxes> taxes = polTaxesRepo.findAll(QPolicyTaxes.policyTaxes.policy.policyId.eq(currentTrans.getPolicyId()));
        List<PolicyTaxes> newTaxes = new ArrayList<>();
        for (PolicyTaxes tax : taxes) {
            PolicyTaxes newTax = new PolicyTaxes();
            newTax.setDivFactor(tax.getDivFactor());
            newTax.setPolicy(destination);
            newTax.setRateType(tax.getRateType());
            newTax.setRevenueItems(tax.getRevenueItems());
            newTax.setSubclass(tax.getSubclass());
            newTax.setTaxLevel(tax.getTaxLevel());
            newTax.setTaxRate(tax.getTaxRate());
            newTax.setTaxAmount(tax.getTaxAmount().negate());
            newTaxes.add(newTax);
        }

        // Handle policy clauses (same as general contra)
        Iterable<PolicyClauses> clauses = polClausesRepo.findAll(QPolicyClauses.policyClauses.policy.policyId.eq(currentTrans.getPolicyId()));
        List<PolicyClauses> newClauses = new ArrayList<>();
        for (PolicyClauses clause : clauses) {
            PolicyClauses newClause = new PolicyClauses();
            newClause.setClauHeading(clause.getClauHeading());
            newClause.setClause(clause.getClause());
            newClause.setClauWording(clause.getClauWording());
            newClause.setEditable(clause.isEditable());
            newClause.setNewClause("N");
            newClause.setPolicy(destination);
            newClauses.add(newClause);
        }

        // Handle active risks (same as general contra)
        Iterable<PolicyActiveRisks> activeRisks = activeRisksRepo.findAll(QPolicyActiveRisks.policyActiveRisks.policy.policyId.eq(revisionForm.getPolicyId()));
        List<PolicyActiveRisks> newActiveRisks = new ArrayList<>();
        for (PolicyActiveRisks activeRisk : activeRisks) {
            PolicyActiveRisks newActiveRisk = new PolicyActiveRisks();
            newActiveRisk.setPolicy(destination);
            newActiveRisk.setRisk(activeRisk.getRisk());
            newActiveRisk.setRiskIdentifier(activeRisk.getRiskIdentifier());
            newActiveRisks.add(newActiveRisk);
        }

        // SAVE THE POLICY FIRST (important - need policy ID for beneficiaries)
        PolicyTrans savedPolicy = policyRepo.save(destination);

        // LIFE-SPECIFIC: Call reverseBeneficiaries AFTER saving policy
        try {
            reverseBeneficiaries(revisionForm.getPolicyId(), savedPolicy);
            log.info("Successfully reversed beneficiaries for life policy: {}", savedPolicy.getPolNo());
        } catch (Exception e) {
            log.error("Error reversing beneficiaries for policy {}: {}", savedPolicy.getPolNo(), e.getMessage());
            throw new EndorsementsException("Failed to reverse beneficiaries: " + e.getMessage());
        }

        Iterable<PolicyInstallments> policyInstallmentsList = policyInstallmentsRepo.findAll(QPolicyInstallments.policyInstallments.policyTrans.policyId.eq(revisionForm.getPolicyId()));

        long countPaid = Streamable.streamOf(policyInstallmentsList).filter(a -> a.getInstallPaid().equalsIgnoreCase("Y")).count();

        if(countPaid != 1){
            throw new EndorsementsException("Cannot contra for transaction with paid installments more than one...");
        }

        Optional<PolicyInstallments> policyInstallmentsOptional = Streamable.streamOf(policyInstallmentsList).filter(a -> a.getInstallmentNo()==1L).findAny();

        if(policyInstallmentsOptional.isPresent()){
            final PolicyInstallments policyInstallments = new PolicyInstallments();
            policyInstallments.setInstallPaid(policyInstallmentsOptional.get().getInstallPaid());
            policyInstallments.setPolicyTrans(savedPolicy);
            policyInstallments.setPaidDate(policyInstallmentsOptional.get().getPaidDate());
            policyInstallments.setInstallmentNo(policyInstallmentsOptional.get().getInstallmentNo());
            policyInstallments.setInstallPrem((policyInstallmentsOptional.get().getInstallPrem().multiply(BigDecimal.valueOf(-1))));
//            policyInstallments.setExpectedCommission((policyInstallmentsOptional.get().getExpectedCommission().multiply(BigDecimal.valueOf(-1))));
            if(policyInstallmentsOptional.get().getExpectedSubAgentCommission()!=null)
                policyInstallments.setExpectedCommission((policyInstallmentsOptional.get().getExpectedCommission().multiply(BigDecimal.valueOf(-1))));
            if(policyInstallmentsOptional.get().getExpectedSubAgentCommission()!=null)
            policyInstallments.setExpectedSubAgentCommission((policyInstallmentsOptional.get().getExpectedSubAgentCommission().multiply(BigDecimal.valueOf(-1))));
            if(policyInstallmentsOptional.get().getExpectedMarkerterCommission()!=null)
                policyInstallments.setExpectedMarkerterCommission((policyInstallmentsOptional.get().getExpectedMarkerterCommission().multiply(BigDecimal.valueOf(-1))));
            policyInstallments.setDueDate(policyInstallmentsOptional.get().getDueDate());
            policyInstallments.setNotificationSent(true);
            policyInstallments.setInstallmentNo(1L);
            System.out.println("Install no .."+policyInstallments.getInstallmentNo()+" id..."+policyInstallmentsOptional.get().getInstallmentNo());
            policyInstallmentsRepo.save(policyInstallments);
        }

//        try {
//            deletePaymentSchedulesExceptFirst(revisionForm.getPolicyId());
//            log.info("Successfully processed payment schedules for life policy: {}", savedPolicy.getPolNo());
//        } catch (Exception e) {
//            log.error("Error processing payment schedules for policy {}: {}", savedPolicy.getPolNo(), e.getMessage());
//            throw new EndorsementsException("Failed to process payment schedules: " + e.getMessage());
//        }

        // Save other related entitiesauthori
        polClausesRepo.save(newClauses);
        polTaxesRepo.save(newTaxes);
        policyBindersRepo.save(newPolBinders);
        activeRisksRepo.save(newActiveRisks);
        transRepo.save(transaction);
        try {
            PolicyRemarks remarks = new PolicyRemarks();
            EndorsementRemarks  endorsementRemarks = policyRemarksRepo.findremarksByshtDesc(revisionForm.getRemarks().toUpperCase());//getEndorsementRemarks(savedPolicy.getPolicyId(), "", null);
            remarks.setEndRemarks(endorsementRemarks);
            remarks.setPolicy(savedPolicy);
            remarks.setRemarks(revisionForm.getRemarks().toUpperCase());
            polTransService.saveEndorsementRemarks(remarks);
        } catch (BadRequestException e) {
            throw new RuntimeException(e);
        }
        // Handle risk reversals
        Iterable<PolicyActiveRisks> endorsedRisks = activeRisksRepo.findAll(QPolicyActiveRisks.policyActiveRisks.policy.policyId.eq(savedPolicy.getPolicyId()));
        for (PolicyActiveRisks activeRisk : endorsedRisks) {
            try {
                endorseContraRisk(activeRisk.getArId());
            } catch (BadRequestException e) {
                throw new EndorsementsException(e.getMessage());
            }
        }

        // LIFE-SPECIFIC: Start life workflow with proper variables
        workflowService.startNewWorkFlow(DocType.GEN_UW_DOCUMENT, String.valueOf(savedPolicy.getPolicyId()), savedPolicy, "N", null, null, null, null);

        Map<String, Object> processVariables = Maps.newHashMap();
        processVariables.put("canAuthorize", true);
        processVariables.put("confirmAuth", true);        // Required for workflow form property

        workflowService.completeTask(String.valueOf(savedPolicy.getPolicyId()), processVariables, savedPolicy, DocType.GEN_UW_DOCUMENT, "N", null, null, null, null);

        return savedPolicy.getPolicyId();
    }
    private void deletePaymentSchedulesExceptFirst(Long policyId) throws EndorsementsException {
        try {
            log.info("Starting deletion and negation of payment installments for policy ID: {}", policyId);

            // Find all installments ordered by installment number
        List<PolicyInstallments> allInstallments = StreamSupport.stream(policyInstallmentsRepo.findAll(
                QPolicyInstallments.policyInstallments.policyTrans.policyId.eq(policyId)
        ).spliterator(), false)
        .sorted(Comparator.comparing(
                PolicyInstallments::getInstallmentNo,
                Comparator.nullsLast(Comparator.naturalOrder())
        ))
        .collect(Collectors.toList());


            if (allInstallments.isEmpty()) {
                log.warn("No payment installments found for policy ID: {}", policyId);
                return;
            }

            if (allInstallments.size() == 1) {
                log.info("Only one payment installment exists for policy ID: {}, proceeding to negate it", policyId);
                // Still need to negate the single installment
            } else {
                // Delete all installments except the first
                List<PolicyInstallments> installmentsToDelete = allInstallments.subList(1, allInstallments.size());

                log.info("Found {} payment installments to delete for policy ID: {}", installmentsToDelete.size(), policyId);

                // Log details of each installment being deleted
                BigDecimal totalPremiumDeleted = BigDecimal.ZERO;
                BigDecimal totalCommissionDeleted = BigDecimal.ZERO;
                BigDecimal totalSubCommissionDeleted = BigDecimal.ZERO;
                BigDecimal totalMarketerCommissionDeleted = BigDecimal.ZERO;

                for (PolicyInstallments installment : installmentsToDelete) {
                    log.info("Deleting payment installment - ID: {}, Installment No: {}, Due Date: {}, Premium: {}, Status: {}, Paid Date: {}, Notification Sent: {}",
                            installment.getInstallmentsId(),
                            installment.getInstallmentNo(),
                            installment.getDueDate(),
                            installment.getInstallPrem(),
                            installment.getInstallPaid(),
                            installment.getPaidDate(),
                            installment.isNotificationSent());

                    // Log commission details
                    log.info("Commission details for installment ID {}: Expected Comm: {}, Sub Agent Comm: {}, Marketer Comm: {}",
                            installment.getInstallmentsId(),
                            installment.getExpectedCommission(),
                            installment.getExpectedSubAgentCommission(),
                            installment.getExpectedMarkerterCommission());

                    // Accumulate totals
                    if (installment.getInstallPrem() != null) {
                        totalPremiumDeleted = totalPremiumDeleted.add(installment.getInstallPrem());
                    }
                    if (installment.getExpectedCommission() != null) {
                        totalCommissionDeleted = totalCommissionDeleted.add(installment.getExpectedCommission());
                    }
                    if (installment.getExpectedSubAgentCommission() != null) {
                        totalSubCommissionDeleted = totalSubCommissionDeleted.add(installment.getExpectedSubAgentCommission());
                    }
                    if (installment.getExpectedMarkerterCommission() != null) {
                        totalMarketerCommissionDeleted = totalMarketerCommissionDeleted.add(installment.getExpectedMarkerterCommission());
                    }
                }

                // Delete the installments
                policyInstallmentsRepo.delete(installmentsToDelete);

                log.info("Successfully deleted {} payment installments for policy ID: {}", installmentsToDelete.size(), policyId);
                log.info("Total amounts deleted - Premium: {}, Commission: {}, Sub Agent Commission: {}, Marketer Commission: {}",
                        totalPremiumDeleted, totalCommissionDeleted, totalSubCommissionDeleted, totalMarketerCommissionDeleted);
            }

            // Now negate the first (remaining) installment
            PolicyInstallments firstInstallment = allInstallments.get(0);

            log.info("Original first installment - ID: {}, Installment No: {}, Due Date: {}, Premium: {}, Paid: {}",
                    firstInstallment.getInstallmentsId(),
                    firstInstallment.getInstallmentNo(),
                    firstInstallment.getDueDate(),
                    firstInstallment.getInstallPrem(),
                    firstInstallment.getInstallPaid());

            log.info("Original commission amounts - Commission: {}, Sub Agent Comm: {}, Marketer Comm: {}",
                    firstInstallment.getExpectedCommission(),
                    firstInstallment.getExpectedSubAgentCommission(),
                    firstInstallment.getExpectedMarkerterCommission());

            // Negate all the amounts
            if (firstInstallment.getInstallPrem() != null) {
                firstInstallment.setInstallPrem(firstInstallment.getInstallPrem().negate());
            }
            if (firstInstallment.getExpectedCommission() != null) {
                firstInstallment.setExpectedCommission(firstInstallment.getExpectedCommission().negate());
            }
            if (firstInstallment.getExpectedSubAgentCommission() != null) {
                firstInstallment.setExpectedSubAgentCommission(firstInstallment.getExpectedSubAgentCommission().negate());
            }
            if (firstInstallment.getExpectedMarkerterCommission() != null) {
                firstInstallment.setExpectedMarkerterCommission(firstInstallment.getExpectedMarkerterCommission().negate());
            }

//            // Reset payment status since it's now a contra entry
//            firstInstallment.setInstallPaid("N");  // Assuming "Y"/"N" for paid status
//            firstInstallment.setPaidDate(null);     // Clear paid date
//            firstInstallment.setNotificationSent(false); // Reset notification flag

            // Save the negated first installment
            policyInstallmentsRepo.save(firstInstallment);

            log.info("Negated first installment - ID: {}, Installment No: {}, Premium: {}, Paid Status: {}",
                    firstInstallment.getInstallmentsId(),
                    firstInstallment.getInstallmentNo(),
                    firstInstallment.getInstallPrem(),
                    firstInstallment.getInstallPaid());

            log.info("Negated commission amounts - Commission: {}, Sub Agent Comm: {}, Marketer Comm: {}",
                    firstInstallment.getExpectedCommission(),
                    firstInstallment.getExpectedSubAgentCommission(),
                    firstInstallment.getExpectedMarkerterCommission());

            // Update policy installment tracking
            PolicyTrans policy = policyRepo.findOne(policyId);
            if (policy != null) {
                // Reset to 1 installment since we only have the first (negated) one
                policy.setPaidInsts(1);
                policy.setTotalInstalments(1);
                policyRepo.save(policy);

                log.info("Updated policy installment tracking - Policy ID: {}, Paid Installments: 1, Total Installments: 1", policyId);
            }

            log.info("Successfully completed installment deletion and negation for policy ID: {}", policyId);

        } catch (Exception e) {
            log.error("Error processing payment installments for policy ID: {}", policyId, e);
            throw new EndorsementsException("Failed to delete and negate payment installments: " + e.getMessage());
        }
    }


    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void endorseContraRisk(Long activeRiskCode) throws BadRequestException {
        PolicyActiveRisks activeRisk = activeRisksRepo.findOne(activeRiskCode);
        if (activeRisk == null) {
            throw new BadRequestException("Error Endorsing the Risk..Active Risk Details not found");
        }
        RiskTrans risk = activeRisk.getRisk();

        if (risk == null) {
            throw new BadRequestException("Error Endorsing the Risk..Risk Details not found");
        }

        PolicyTrans newPolicy = activeRisk.getPolicy();

        if (newPolicy == null) {
            throw new BadRequestException("Error Endorsing the Risk..Policy Details not found");
        }

        long count = riskTransRepo.count(QRiskTrans.riskTrans.riskIdentifier.eq(risk.getRiskIdentifier()).and(QRiskTrans.riskTrans.policy.policyId.eq(newPolicy.getPolicyId())));

        if (count > 0 || activeRisk.getPrevRisk() != null)
            throw new BadRequestException("The Risk has already been endorsed");

        RiskTrans newRisk = new RiskTrans();
        newRisk.setBinder(risk.getBinder());
        newRisk.setBinderDetails(risk.getBinderDetails());
        newRisk.setCommRate(risk.getCommRate());
        newRisk.setCovertype(risk.getCovertype());
        newRisk.setInsured(risk.getInsured());
        newRisk.setPolicy(newPolicy);
        newRisk.setProrata(risk.getProrata());
        newRisk.setRiskDesc(risk.getRiskDesc());
        newRisk.setRiskIdentifier(activeRisk.getRiskIdentifier());
        newRisk.setRiskShtDesc(risk.getRiskShtDesc());
        newRisk.setSubclass(risk.getSubclass());
        newRisk.setUwYear(risk.getUwYear());
        newRisk.setWefDate(newPolicy.getWefDate());
        newRisk.setWetDate(newPolicy.getWetDate());
        newRisk.setTransType("CO");
        newRisk.setCalcPremium(risk.getCalcPremium().negate());
        newRisk.setCommAmt((risk.getCommAmt() != null) ? risk.getCommAmt().negate() : BigDecimal.ZERO);
        if (risk.getExtras() != null)
            newRisk.setExtras(risk.getExtras().negate());
        newRisk.setFuturePrem(risk.getFuturePrem().abs());
        newRisk.setNetpremium((risk.getNetpremium() != null) ? risk.getNetpremium().negate() : BigDecimal.ZERO);
        newRisk.setPhfFund((risk.getPhfFund() != null) ? risk.getPhfFund().negate() : BigDecimal.ZERO);
        newRisk.setPremium((risk.getPremium() != null) ? risk.getPremium().negate() : BigDecimal.ZERO);
        if (risk.getStampDuty() != null)
            newRisk.setStampDuty((risk.getStampDuty() != null) ? risk.getStampDuty().negate() : BigDecimal.ZERO);
        newRisk.setSumInsured((risk.getSumInsured() != null) ? risk.getSumInsured().negate() : BigDecimal.ZERO);
        if (risk.getTrainingLevy() != null)
            newRisk.setTrainingLevy(risk.getTrainingLevy().negate());
        if (risk.getWhtax() != null)
            newRisk.setWhtax(risk.getWhtax().negate());
        if (risk.getPolicyBinders() != null) {
            PolicyBinders polbinder = policyBindersRepo.findOne(QPolicyBinders.policyBinders.binder.binId.eq(risk.getBinder().getBinId())
                    .and(QPolicyBinders.policyBinders.policyTrans.policyId.eq(newPolicy.getPolicyId())));
            newRisk.setPolicyBinders(polbinder);

        }
        Iterable<SectionTrans> sections = sectionTransRepo.findAll(QSectionTrans.sectionTrans.risk.riskId.eq(risk.getRiskId()));
        List<SectionTrans> newSections = new ArrayList<>();
        for (SectionTrans section : sections) {
            SectionTrans newSection = new SectionTrans();
            newSection.setAmount(section.getAmount().negate());
            newSection.setPrem((section.getPrem() == null) ? BigDecimal.ZERO : section.getPrem().negate());
            newSection.setCompute(true);
            newSection.setDivFactor(section.getDivFactor());
            newSection.setFreeLimit(section.getFreeLimit().negate());
            newSection.setMultiRate(section.getMultiRate());
            newSection.setPremRates(section.getPremRates());
            newSection.setRate(section.getRate());
            newSection.setRisk(newRisk);
            newSection.setSection(section.getSection());
            newSections.add(newSection);
        }
        activeRisk.setPrevRisk(risk);
        activeRisk.setRisk(newRisk);
        riskTransRepo.save(newRisk);
        sectionTransRepo.save(newSections);
        activeRisksRepo.save(activeRisk);

    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<EndorsementsDTO> findContradTransactions(DataTablesRequest request, String drNumber,
                                                                     String clientName, String polNo, String endorseNumber, String agentName) throws IllegalAccessException {
        if (clientName == null || StringUtils.isBlank(clientName)) {
            clientName = "%";
        }
        if (drNumber == null || StringUtils.isBlank(drNumber)) {
            drNumber = "%";
        }

        if (polNo == null || StringUtils.isBlank(polNo)) {
            polNo = "%";
        }
        if (agentName == null || StringUtils.isBlank(agentName)) {
            agentName = "%";
        }

        if (endorseNumber == null || StringUtils.isBlank(endorseNumber)) {
            endorseNumber = "%";
        }
        List<Object[]> activePolicyList = policyRepo.getContraPolicies(polNo,endorseNumber,drNumber,clientName,agentName, request.getPageNumber(), request.getPageSize());
        long countPolicies = 0L;
        if(activePolicyList != null && !activePolicyList.isEmpty()){
            countPolicies = ((BigInteger)activePolicyList.get(0)[15]).longValue();
        }
        List<EndorsementsDTO> endorsementsDTOList = new ArrayList<>();
        for(Object[] activePolicy : activePolicyList){
            final Long policyId = ((BigInteger)activePolicy[3]).longValue();
            final Boolean renewableState = ((Boolean)activePolicy[8]);
            final Long uwYear = ((Integer)activePolicy[6]).longValue();
            String authComments = "";
            String transType = "";
            EndorsementsDTO endorsementsDTO =  EndorsementsDTO.instance (policyId,(String)activePolicy[0],(String)activePolicy[1],(String)activePolicy[2],
                    (String)activePolicy[4],(String)activePolicy[5],(String)activePolicy[7],uwYear, renewableState,(String)activePolicy[9], (Date)activePolicy[10],(Date)activePolicy[11],
                    (String)activePolicy[12],(String)activePolicy[13],countPolicies, (String) activePolicy[14],authComments,transType);
            endorsementsDTOList.add(endorsementsDTO);
        }
        Page<EndorsementsDTO> page = new PageImpl<>(endorsementsDTOList, request, countPolicies);
        return new DataTablesResult(request, page);
    }

    @PreAuthorize("hasAnyAuthority('REUSE_OF_CONTRA')")
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = {EndorsementsException.class})
    public PolicyTrans reuseOfContra(RevisionForm revisionForm) throws EndorsementsException {
        if (revisionForm.getPolicyId() == null || revisionForm.getPolicyId() == null)
            throw new EndorsementsException("Policy to Reuse Contra cannot be Empty....");

        if (revisionForm.getRevisionType() == null || StringUtils.isBlank(revisionForm.getRevisionType()))
            throw new EndorsementsException("Select Revision Type...");


        PolicyTrans currentTrans = policyRepo.findOne(revisionForm.getPolicyId());

        Predicate activePred = QPolicyTrans.policyTrans.polNo.eq(currentTrans.getPolNo())
                .and(QPolicyTrans.policyTrans.currentStatus.eq("A"));

        if (countUnauthTransactions(currentTrans.getPolNo()) > 0) {
            throw new EndorsementsException(
                    "The Policy has unfinished Transaction");
        }

        if (policyRepo.count(activePred) > 1) {
            throw new EndorsementsException("The current Policy has an active Endorsement. Only One Active Endorsement can be allowed");
        }

        if (revisionForm.getRevisionType().equalsIgnoreCase("NB")
                || revisionForm.getRevisionType().equalsIgnoreCase("SP")) {
            throw new EndorsementsException("Endorsement Type not catered For. Contact the Vendor");
        }

        long endorsementCount = policyRepo.count(QPolicyTrans.policyTrans.polNo.eq(currentTrans.getPolNo())) + 1;

        PolicyTrans destination = new PolicyTrans();
        destination.setWefDate(currentTrans.getWefDate());
        destination.setWetDate(currentTrans.getWetDate());
        destination.setCoverFrom(currentTrans.getCoverFrom());
        destination.setCoverTo(currentTrans.getCoverTo());
        destination.setRenewalDate(currentTrans.getRenewalDate());
        destination.setUwYear(currentTrans.getUwYear());
        destination.setPolRevStatus(revisionForm.getRevisionType());
        if (currentTrans.getRevisionFormat() == null) {
            destination.setPolRevNo(currentTrans.getPolRevNo().substring(0, currentTrans.getPolRevNo().indexOf("/")) + "/" + endorsementCount);
        } else
            destination.setPolRevNo(currentTrans.getRevisionFormat() + "/" + endorsementCount);
        destination.setPolNo(currentTrans.getPolNo());
        destination.setAgent(currentTrans.getAgent());
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
        destination.setRevisionFormat(currentTrans.getRevisionFormat());
        destination.setProduct(currentTrans.getProduct());
        destination.setRenewable(currentTrans.isRenewable());
        destination.setBusinessType(currentTrans.getBusinessType());
        destination.setTransCurrency(currentTrans.getTransCurrency());
        PolicyTrans previousTrans = currentTrans.getPreviousTrans();
        if (previousTrans.getPolicyId() == currentTrans.getPolicyId())
            throw new EndorsementsException("Can only Reuse Contra A Contra Transactions..Contact System Admin");
        destination.setTransType(previousTrans.getTransType());
        destination.setReusecontraPolicy(previousTrans);
        destination.setSubAgent(currentTrans.getSubAgent());


        SystemTrans transaction = new SystemTrans();
        transaction.setDoneDate(new Date());
        transaction.setDoneBy(userUtils.getCurrentUser());
        transaction.setPolicy(destination);
        transaction.setTransLevel("U");
        transaction.setTransCode("APC"); //A way to setup and look up for transaction transcode
        transaction.setTransAuthorised("N");
        Iterable<PolicyTaxes> taxes = polTaxesRepo.findAll(QPolicyTaxes.policyTaxes.policy.policyId.eq(currentTrans.getPolicyId()));
        List<PolicyTaxes> newTaxes = new ArrayList<>();
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

        Iterable<PolicyClauses> clauses = polClausesRepo.findAll(QPolicyClauses.policyClauses.policy.policyId.eq(currentTrans.getPolicyId()));
        List<PolicyClauses> newClauses = new ArrayList<>();
        for (PolicyClauses clause : clauses) {
            PolicyClauses newClause = new PolicyClauses();
            newClause.setClauHeading(clause.getClauHeading());
            newClause.setClause(clause.getClause());
            newClause.setClauWording(clause.getClauWording());
            newClause.setEditable(clause.isEditable());
            newClause.setNewClause("N");
            newClause.setPolicy(destination);
            newClauses.add(newClause);
        }
        Iterable<PolicyActiveRisks> activeRisks = activeRisksRepo.findAll(QPolicyActiveRisks.policyActiveRisks.policy.policyId.eq(revisionForm.getPolicyId()));
        List<PolicyActiveRisks> newActiveRisks = new ArrayList<>();
        for (PolicyActiveRisks activeRisk : activeRisks) {
            PolicyActiveRisks newActiveRisk = new PolicyActiveRisks();
            newActiveRisk.setPolicy(destination);
            newActiveRisk.setRisk(activeRisk.getRisk());
            newActiveRisk.setRiskIdentifier(activeRisk.getRiskIdentifier());
            newActiveRisks.add(newActiveRisk);
        }
        PolicyTrans savedPolicy = policyRepo.save(destination);
        polClausesRepo.save(newClauses);
        polTaxesRepo.save(newTaxes);
        activeRisksRepo.save(newActiveRisks);
        transRepo.save(transaction);

        Iterable<PolicyActiveRisks> endorsedRisks = activeRisksRepo.findAll(QPolicyActiveRisks.policyActiveRisks.policy.policyId.eq(savedPolicy.getPolicyId()));
        for (PolicyActiveRisks activeRisk : endorsedRisks) {
            try {
                endorseReuseContraRisk(activeRisk.getArId());
            } catch (BadRequestException e) {
                throw new EndorsementsException(e.getMessage());
            }
        }
        workflowService.startNewWorkFlow(DocType.GEN_UW_DOCUMENT, String.valueOf(savedPolicy.getPolicyId()), savedPolicy, "N", null, null, null, null);
        return savedPolicy;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void endorseReuseContraRisk(Long activeRiskCode) throws BadRequestException {
        PolicyActiveRisks activeRisk = activeRisksRepo.findOne(activeRiskCode);
        if (activeRisk == null) {
            throw new BadRequestException("Error Endorsing the Risk..Active Risk Details not found");
        }
        RiskTrans risk = activeRisk.getRisk();

        if (risk == null) {
            throw new BadRequestException("Error Endorsing the Risk..Risk Details not found");
        }

        PolicyTrans newPolicy = activeRisk.getPolicy();

        if (newPolicy == null) {
            throw new BadRequestException("Error Endorsing the Risk..Policy Details not found");
        }

        long count = riskTransRepo.count(QRiskTrans.riskTrans.riskIdentifier.eq(risk.getRiskIdentifier()).and(QRiskTrans.riskTrans.policy.policyId.eq(newPolicy.getPolicyId())));

        if (count > 0 || activeRisk.getPrevRisk() != null)
            throw new BadRequestException("The Risk has already been endorsed");

        RiskTrans newRisk = new RiskTrans();
        newRisk.setBinder(risk.getBinder());
        newRisk.setBinderDetails(risk.getBinderDetails());
        newRisk.setCommRate(risk.getCommRate());
        newRisk.setCovertype(risk.getCovertype());
        newRisk.setInsured(risk.getInsured());
        newRisk.setPolicy(newPolicy);
        newRisk.setProrata(risk.getProrata());
        newRisk.setRiskDesc(risk.getRiskDesc());
        newRisk.setRiskIdentifier(activeRisk.getRiskIdentifier());
        newRisk.setRiskShtDesc(risk.getRiskShtDesc());
        newRisk.setSubclass(risk.getSubclass());
        newRisk.setUwYear(risk.getUwYear());
        newRisk.setWefDate(newPolicy.getWefDate());
        newRisk.setWetDate(newPolicy.getWetDate());
        newRisk.setTransType(newPolicy.getTransType());

        Iterable<SectionTrans> sections = sectionTransRepo.findAll(QSectionTrans.sectionTrans.risk.riskId.eq(risk.getRiskId()));
        List<SectionTrans> newSections = new ArrayList<>();
        for (SectionTrans section : sections) {
            SectionTrans newSection = new SectionTrans();
            newSection.setAmount(section.getAmount().negate());
            newSection.setCompute(true);
            newSection.setDivFactor(section.getDivFactor());
            newSection.setFreeLimit(section.getFreeLimit().negate());
            newSection.setMultiRate(section.getMultiRate());
            newSection.setPremRates(section.getPremRates());
            newSection.setRate(section.getRate());
            newSection.setRisk(newRisk);
            newSection.setSection(section.getSection());
            newSections.add(newSection);
        }
        activeRisk.setPrevRisk(risk);
        activeRisk.setRisk(newRisk);
        riskTransRepo.save(newRisk);
        sectionTransRepo.save(newSections);
        activeRisksRepo.save(activeRisk);

    }

    @Transactional(readOnly = true)
    public Boolean getProdutGroup(Long polCode) {
        PolicyTrans policy = policyRepo.findOne(polCode);
        ProductsDef policyProduct = policy.getProduct();
        boolean medicalProduct = false;
        if (policyProduct.getProGroup().getPrgType() == null || !policyProduct.getProGroup().getPrgType().equalsIgnoreCase("MD")) {
            medicalProduct = false;
        } else if (policyProduct.getProGroup().getPrgType().equalsIgnoreCase("MD")) {
            medicalProduct = true;
        }
        return medicalProduct;
    }

    @PreAuthorize("hasAnyAuthority('RENEW_POLICY')")
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = {EndorsementsException.class})
    public Long renewPolicy(RenewalsForm renForm) throws EndorsementsException {
        if (renForm.getPolicyId() == null || renForm.getPolicyId() == null)
            throw new EndorsementsException("Policy to Renew cannot be Empty....");


        PolicyTrans currentTrans = policyRepo.findOne(renForm.getPolicyId());

        Predicate activePred = QPolicyTrans.policyTrans.polNo.eq(currentTrans.getPolNo())
                .and(QPolicyTrans.policyTrans.currentStatus.eq("A"));

        Predicate renewalPredicate = QPolicyTrans.policyTrans.polNo.eq(currentTrans.getPolNo())
                .and(QPolicyTrans.policyTrans.transType.eq("RN"))
                .and(QPolicyTrans.policyTrans.authStatus.eq("D").or(QPolicyTrans.policyTrans.authStatus.eq("R")));

        if (policyRepo.count(renewalPredicate) > 0) {
            PolicyTrans policy = policyRepo.findOne(renewalPredicate);
            try {
                deletePolicyRecord(policy.getPolicyId(), true);
            } catch (BadRequestException e) {
                throw new EndorsementsException(e.getMessage());
            }
        }

        if (countUnauthTransactions(currentTrans.getPolNo()) > 0) {
            throw new EndorsementsException(
                    "The Policy has unfinished Transaction");
        }

        if (policyRepo.count(activePred) > 1) {
            throw new EndorsementsException("The current Policy has an active Endorsement. Only One Active Endorsement can be allowed");
        }

        if ("SP".equalsIgnoreCase(currentTrans.getTransType()))
            throw new EndorsementsException("Renewal not Supported on Short Period Policy Transactions");

        if (currentTrans.getRenewalDate() == null)
            throw new EndorsementsException("Cannot Renew the Policy...The policy is configured to be not renewable. Contact System Administrator");

        if(this.countUnauthTransactions(currentTrans.getPolNo()) > 0){
            Predicate pred = QPolicyTrans.policyTrans.polNo.eq(currentTrans.getPolNo())
                    .and(QPolicyTrans.policyTrans.currentStatus.eq("D")
                            .and(QPolicyTrans.policyTrans.businessType.in("N","L")));
            Iterable<PolicyTrans> policyTransList = policyRepo.findAll(pred);
            for(PolicyTrans policyTrans : policyTransList){
                try {
                    this.deletePolicyRecord(policyTrans.getPolicyId(),false);
                } catch (BadRequestException e) {
                    throw new EndorsementsException(e.getMessage());
                }
            }
        }

        LocalDate d1 =(currentTrans.getCoverFrom() instanceof java.sql.Date)?((java.sql.Date) currentTrans.getCoverFrom()).toLocalDate(): currentTrans.getCoverFrom().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate d2 = (currentTrans.getCoverTo() instanceof java.sql.Date)?((java.sql.Date) currentTrans.getCoverTo()).toLocalDate():currentTrans.getCoverTo().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        final boolean isOneYearMinusOneDay = d2.equals(d1.plusYears(1).minusDays(1));

        if(!isOneYearMinusOneDay && currentTrans.getBusinessType().equalsIgnoreCase("L")){
            throw new EndorsementsException("Can only an annual policy for life transactions...");
        }

        long endorsementCount = policyRepo.count(QPolicyTrans.policyTrans.polNo.eq(currentTrans.getPolNo())) + 1;

        PolicyTrans destination = new PolicyTrans();
        destination.setPolTerm(currentTrans.getPolTerm());
        destination.setTotalInstalments(currentTrans.getTotalInstalments());
        destination.setWefDate(currentTrans.getRenewalDate());
        destination.setWetDate(dateUtils.getWetDate(currentTrans.getRenewalDate()));
        destination.setCoverFrom(currentTrans.getRenewalDate());
        destination.setCoverTo(dateUtils.getWetDate(currentTrans.getRenewalDate()));
        destination.setRenewalDate(DateUtils.addDays(dateUtils.getWetDate(currentTrans.getRenewalDate()), 1));
        destination.setUwYear(dateUtils.getUwYear(currentTrans.getRenewalDate()));
        destination.setPolRevStatus("RN");
        if (currentTrans.getRevisionFormat() == null) {
            destination.setPolRevNo(currentTrans.getPolRevNo().substring(0, currentTrans.getPolRevNo().indexOf("/")) + "/" + endorsementCount);
        } else
            destination.setPolRevNo(currentTrans.getRevisionFormat() + "/" + endorsementCount);
        destination.setPolNo(currentTrans.getPolNo());
        destination.setAgent(currentTrans.getAgent());
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
        destination.setSubAgent(currentTrans.getSubAgent());

        destination.setTransCurrency(currentTrans.getTransCurrency());
        destination.setTransType("RN");

        SystemTrans transaction = new SystemTrans();
        transaction.setDoneDate(new Date());
        transaction.setDoneBy(userUtils.getCurrentUser());
        transaction.setPolicy(destination);
        transaction.setTransLevel("U");
        transaction.setTransCode("APD"); //A way to setup and look up for transaction transcode
        transaction.setTransAuthorised("N");
        boolean medicalProduct = false;
        ProductsDef productsDef = currentTrans.getProduct();
        if (productsDef.getProGroup().getPrgType() == null || !productsDef.getProGroup().getPrgType().equalsIgnoreCase("MD")) {
            medicalProduct = false;
        } else if (productsDef.getProGroup().getPrgType().equalsIgnoreCase("MD")) {
            medicalProduct = true;
            destination.setMedicalCoverType(currentTrans.getMedicalCoverType());
            destination.setBinCardType(currentTrans.getBinCardType());

        }
        Iterable<MedicalCategory> categories = categoryRepo.findAll(QMedicalCategory.medicalCategory.policy.policyId.eq(currentTrans.getPolicyId()));
        List<MedicalCategory> newCategories = new ArrayList<>();
        List<CategoryMembers> newMembers = new ArrayList<>();
        List<CategoryMemberBenefits> memBenefits = new ArrayList<>();
        Iterable<SelfFundParams> selfFunds = selfFundParamsRepo.findAll(QSelfFundParams.selfFundParams.policyTrans.policyId.eq(currentTrans.getPolicyId()));
        List<SelfFundParams> newSelfunds = new ArrayList<>();
        Iterable<CategoryRules> rules = rulesRepo.findAll(QCategoryRules.categoryRules.category.policy.policyId.eq(currentTrans.getPolicyId()));
        List<CategoryRules> newRules = new ArrayList<>();
        Iterable<MedCategoryBenefits> benefits = benefitRepo.findAll(QMedCategoryBenefits.medCategoryBenefits.category.policy.policyId.eq(currentTrans.getPolicyId()));
        List<MedCategoryBenefits> newBenefits = new ArrayList<>();

        Iterable<CategoryExclusions> exclusions = exclusionsRepo.findAll(QCategoryExclusions.categoryExclusions.category.policy.policyId.eq(currentTrans.getPolicyId()));
        List<CategoryExclusions> newExclusions = new ArrayList<>();

        Iterable<CategoryLoadings> loadings = loadingRepo.findAll(QCategoryLoadings.categoryLoadings.category.policy.policyId.eq(currentTrans.getPolicyId()));
        List<CategoryLoadings> newLoadings = new ArrayList<>();

        Iterable<CatalogueProviders> providers = providersRepo.findAll(QCatalogueProviders.catalogueProviders.category.policy.policyId.eq(currentTrans.getPolicyId()));
        List<CatalogueProviders> newProviders = new ArrayList<>();
        if (medicalProduct) {

            for (MedicalCategory category : categories) {
                MedicalCategory newCategory = new MedicalCategory();
                newCategory.setBedCost(category.getBedCost());
                newCategory.setBedTypes(category.getBedTypes());
                newCategory.setBinderDetails(category.getBinderDetails());
                newCategory.setDesc(category.getDesc());
                newCategory.setShtDesc(category.getShtDesc());
                newCategory.setLoadingFactor(category.getLoadingFactor());
                newCategory.setLoadingPrem(null);
                newCategory.setPolicy(destination);
                newCategory.setPremium(null);
                newCategories.add(newCategory);
                Iterable<CategoryMembers> categoryMembers = memberRepo.findAll(QCategoryMembers.categoryMembers.category.id.eq(category.getId())
                        .and(QCategoryMembers.categoryMembers.memberStatus.equalsIgnoreCase("A")));
                for (MedCategoryBenefits benefit : benefits) {
                    MedCategoryBenefits newBenefit = new MedCategoryBenefits();
                    newBenefit.setApplicableAt(benefit.getApplicableAt());
                    newBenefit.setWaitPeriod(benefit.getWaitPeriod());
                    newBenefit.setCategory(newCategory);
                    newBenefit.setFundLimit(benefit.getFundLimit());
                    newBenefit.setCover(benefit.getCover());
                    newBenefit.setLimit(benefit.getLimit());
                    newBenefits.add(newBenefit);
                }
                for (CategoryMembers member : categoryMembers) {
                    CategoryMembers newMember = new CategoryMembers();
                    newMember.setCategory(newCategory);
                    newMember.setChildType(member.getChildType());
                    newMember.setClient(member.getClient());
                    newMember.setMainClient(member.getMainClient());
                    newMember.setDependentTypes(member.getDependentTypes());
                    newMember.setMemberShipNo(member.getMemberShipNo());
                    newMember.setWefDate(currentTrans.getRenewalDate());
                    newMember.setWetDate(dateUtils.getWetDate(currentTrans.getRenewalDate()));
                    newMember.setMemberStatus("R");
                    newMember.setPrevsectId(member.getSectId());
                    newMembers.add(newMember);


                }
                for (CategoryRules rule : rules) {
                    CategoryRules newRule = new CategoryRules();
                    newRule.setBinderRules(rule.getBinderRules());
                    newRule.setCategory(newCategory);
                    newRule.setDesc(rule.getDesc());
                    newRule.setShtDesc(rule.getShtDesc());
                    newRule.setValue(rule.getValue());
                    newRules.add(newRule);
                }

                for (CategoryExclusions exclusion : exclusions) {
                    CategoryExclusions newExlusion = new CategoryExclusions();
                    newExlusion.setCategory(newCategory);
                    newExlusion.setAilment(exclusion.getAilment());
                    newExclusions.add(newExlusion);
                }
                for (CategoryLoadings loading : loadings) {
                    CategoryLoadings newloading = new CategoryLoadings();
                    newloading.setAilment(loading.getAilment());
                    newloading.setCategory(newCategory);
                    newloading.setLoadingAmt(loading.getLoadingAmt());
                    newloading.setRate(loading.getRate());
                    newloading.setRateType(loading.getRateType());
                    newLoadings.add(newloading);
                }
                for (CatalogueProviders provider : providers) {
                    CatalogueProviders newProvider = new CatalogueProviders();
                    newProvider.setCategory(newCategory);
                    newProvider.setProviders(provider.getProviders());
                    newProviders.add(newProvider);
                }

            }

            for (SelfFundParams selfFundParam : selfFunds) {
                SelfFundParams newSelfFund = new SelfFundParams();
                newSelfFund.setApplicableLevel(selfFundParam.getApplicableLevel());
                newSelfFund.setApplicableValue(selfFundParam.getApplicableValue());
                newSelfFund.setBillingFrequency(selfFundParam.getBillingFrequency());
                newSelfFund.setFundResetAmount(selfFundParam.getFundResetAmount());
                newSelfFund.setFundDepositAmount(selfFundParam.getFundDepositAmount());
                newSelfFund.setMinDeposit(selfFundParam.getMinDeposit());
                newSelfFund.setPolicyTrans(destination);
                newSelfFund.setCarryForwardBalances(selfFundParam.isCarryForwardBalances());
                newSelfFund.setDeductAdminFeeFromFund(selfFundParam.isDeductAdminFeeFromFund());
                newSelfFund.setPayWhenBenefitExhausted(selfFundParam.isPayWhenBenefitExhausted());
                newSelfFund.setPayWhenFundExhausted(selfFundParam.isPayWhenFundExhausted());
                newSelfFund.setSelfFundBalance(selfFundParam.getSelfFundBalance());
                newSelfunds.add(newSelfFund);

            }


        }
        Iterable<PolicyTaxes> taxes = polTaxesRepo.findAll(QPolicyTaxes.policyTaxes.policy.policyId.eq(currentTrans.getPolicyId()));
        List<PolicyTaxes> newTaxes = new ArrayList<>();
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

        Iterable<PolicyClauses> clauses = polClausesRepo.findAll(QPolicyClauses.policyClauses.policy.policyId.eq(currentTrans.getPolicyId()));
        List<PolicyClauses> newClauses = new ArrayList<>();
        for (PolicyClauses clause : clauses) {
            PolicyClauses newClause = new PolicyClauses();
            newClause.setClauHeading(clause.getClauHeading());
            newClause.setClause(clause.getClause());
            newClause.setClauWording(clause.getClauWording());
            newClause.setEditable(clause.isEditable());
            newClause.setNewClause("N");
            newClause.setPolicy(destination);
            newClauses.add(newClause);
        }
        Iterable<PolicyActiveRisks> activeRisks = activeRisksRepo.findAll(QPolicyActiveRisks.policyActiveRisks.policy.policyId.eq(renForm.getPolicyId()));
        List<PolicyActiveRisks> newActiveRisks = new ArrayList<>();
        for (PolicyActiveRisks activeRisk : activeRisks) {
            PolicyActiveRisks newActiveRisk = new PolicyActiveRisks();
            newActiveRisk.setPolicy(destination);
            newActiveRisk.setRisk(activeRisk.getRisk());
            newActiveRisk.setRiskIdentifier(activeRisk.getRiskIdentifier());
            newActiveRisks.add(newActiveRisk);
        }
        currentTrans.setRenewed(true);
        policyRepo.save(currentTrans);
        PolicyTrans savedPolicy = policyRepo.save(destination);

        reverseBeneficiaries(renForm.getPolicyId(), savedPolicy);

        polClausesRepo.save(newClauses);
        if (!newCategories.isEmpty()) {
            categoryRepo.save(newCategories);
            Iterable<CategoryMembers> savedMems = new ArrayList<>();
            if (!newMembers.isEmpty()) {
                savedMems = memberRepo.save(newMembers);
            }
            if (!newSelfunds.isEmpty()) {
                selfFundParamsRepo.save(newSelfunds);
            }
            if (!newRules.isEmpty()) {
                rulesRepo.save(newRules);
            }
            if (!newBenefits.isEmpty()) {
                Iterable<MedCategoryBenefits> savedBens = benefitRepo.save(newBenefits);
                for (CategoryMembers newMember : savedMems) {
                    for (MedCategoryBenefits ben : savedBens) {
                        Iterable<CategoryMemberBenefits> prevMemBenefits = memberBenefitsRepo.findAll(QCategoryMemberBenefits.categoryMemberBenefits.member.sectId.eq(newMember.getPrevsectId())
                                .and(QCategoryMemberBenefits.categoryMemberBenefits.benefit.status.notEqualsIgnoreCase("D"))
                                .and(QCategoryMemberBenefits.categoryMemberBenefits.benefit.cover.id.eq(ben.getCover().getId())));
                        for (CategoryMemberBenefits benefit : prevMemBenefits) {
                            CategoryMemberBenefits memBenefit = new CategoryMemberBenefits();
                            memBenefit.setComputedPremium(BigDecimal.ZERO);
                            memBenefit.setMember(newMember);
                            memBenefit.setBenefit(ben);
                            memBenefit.setPrevPremium(BigDecimal.ZERO);
                            memBenefit.setPrevUnitPremium(BigDecimal.ZERO);
                            memBenefit.setPremium(BigDecimal.ZERO);
                            memBenefit.setWefDate(savedPolicy.getWefDate());
                            memBenefit.setWetDate(savedPolicy.getWetDate());
                            memBenefit.setPrevcmbId(benefit.getCmbId());
                            memBenefits.add(memBenefit);
                        }
                    }
                }
            }

            if (!memBenefits.isEmpty()) {
                memberBenefitsRepo.save(memBenefits);
            }
            if (!newExclusions.isEmpty()) {
                exclusionsRepo.save(newExclusions);
            }
            if (!newLoadings.isEmpty()) {
                loadingRepo.save(newLoadings);
            }
            if (!newProviders.isEmpty()) {
                providersRepo.save(newProviders);
            }
        }
        polTaxesRepo.save(newTaxes);
        activeRisksRepo.save(newActiveRisks);
        transRepo.save(transaction);
        Iterable<PolicyActiveRisks> endorsedRisks = activeRisksRepo.findAll(QPolicyActiveRisks.policyActiveRisks.policy.policyId.eq(savedPolicy.getPolicyId()));
        for (PolicyActiveRisks activeRisk : endorsedRisks) {
            try {
                endorseRisk(activeRisk.getArId(), "R", null);
            } catch (BadRequestException e) {
                throw new EndorsementsException(e.getMessage());
            }
        }
        workflowService.startNewWorkFlow(DocType.GEN_UW_DOCUMENT, String.valueOf(savedPolicy.getPolicyId()), savedPolicy, "N", null, null, null, null);
        return savedPolicy.getPolicyId();
    }

    @Override
    @Transactional(readOnly = true)

    public DataTablesResult<PolicyTrans> findRenewalPolicies(DataTablesRequest request, Date wefDate, Date wetDate,
                                                             Long productCode, Long branchId, Long agentId, Long bindCode, Long riskId, Long clientId)
            throws IllegalAccessException {

        Predicate pred = null;
        pred = QPolicyTrans.policyTrans.currentStatus.eq("A").and(QPolicyTrans.policyTrans.renewed.eq(false))
                .and((riskId != null) ? QPolicyTrans.policyTrans.riskTrans.contains(riskTransRepo.findOne(riskId)) : QPolicyTrans.policyTrans.isNotNull())
                .and(QPolicyTrans.policyTrans.renewalDate.between(wefDate, wetDate))
                .and((productCode != null) ? QPolicyTrans.policyTrans.product.proCode.eq(productCode) : QPolicyTrans.policyTrans.product.isNotNull())
                .and((branchId != null) ? QPolicyTrans.policyTrans.branch.obId.eq(branchId) : QPolicyTrans.policyTrans.branch.isNotNull())
                .and((agentId != null) ? QPolicyTrans.policyTrans.agent.acctId.eq(agentId) : QPolicyTrans.policyTrans.authBy.isNotNull())
                .and((bindCode != null) ? QPolicyTrans.policyTrans.binder.binId.eq(bindCode) : QPolicyTrans.policyTrans.binder.isNotNull())
                .and((clientId != null) ? QPolicyTrans.policyTrans.client.tenId.eq(clientId) : QPolicyTrans.policyTrans.client.isNotNull());
        Page<PolicyTrans> page = policyRepo.findAll(pred, request);
        return new DataTablesResult(request, page);
    }

    @PreAuthorize("hasAnyAuthority('RENEW_POLICY')")
    @Override
    public String processBatchRenewals(BatchRenewalForm renewalForm) throws BadRequestException, InterruptedException, ExecutionException {
        Job job = JobBuilder.aNewJob()
                .reader(new IterableRecordReader(renewalForm.getRenewals()))
                .named("renewal_processing" + new SimpleDateFormat("ddMMyyyhhmmss").format(new Date()))
                .processor(new RecordProcessor<Record, Record>() {

                    @Override
                    public Record processRecord(Record renForm) throws Exception {
                        RenewalsForm renewal = new RenewalsForm();
                        renewal.setPolicyId(((RenewalRecord) renForm.getPayload()).getPayload().getPolicyId());
                        Long policyId = renewPolicy(renewal, ((RenewalRecord) renForm.getPayload()).getPayload().getUser());
                        premComputeService.computePrem(policyId);
                        return renForm;
                    }
                })
                .pipelineListener(new RenewalJobListener())
                .build();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Future<JobReport> report = executorService.submit(job);
//		System.out.println(report);
        String error = "";//report.get().getLastError().getMessage();
        return error;
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<PolicyTrans> findRenewalProgress(DataTablesRequest request, Date wefDate, Date wetDate,
                                                             Long productCode, Long branchId, Long agentId, Long bindCode, Long riskId, Long clientId)
            throws IllegalAccessException {
        Predicate pred = null;
        pred = QPolicyTrans.policyTrans.transType.eq("RN")
                .and(QPolicyTrans.policyTrans.authStatus.notEqualsIgnoreCase("A"))
                .and((riskId != null) ? QPolicyTrans.policyTrans.riskTrans.contains(riskTransRepo.findOne(riskId)) : QPolicyTrans.policyTrans.isNotNull())
                .and(QPolicyTrans.policyTrans.wefDate.between(wefDate, wetDate))
                .and((productCode != null) ? QPolicyTrans.policyTrans.product.proCode.eq(productCode) : QPolicyTrans.policyTrans.product.isNotNull())
                .and((branchId != null) ? QPolicyTrans.policyTrans.branch.obId.eq(branchId) : QPolicyTrans.policyTrans.branch.isNotNull())
                .and((agentId != null) ? QPolicyTrans.policyTrans.agent.acctId.eq(agentId) : QPolicyTrans.policyTrans.agent.isNotNull())
                .and((bindCode != null) ? QPolicyTrans.policyTrans.binder.binId.eq(bindCode) : QPolicyTrans.policyTrans.binder.isNotNull())
                .and((clientId != null) ? QPolicyTrans.policyTrans.client.tenId.eq(clientId) : QPolicyTrans.policyTrans.client.isNotNull());
        Page<PolicyTrans> page = policyRepo.findAll(pred, request);
        return new DataTablesResult(request, page);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {EndorsementsException.class})
    public Long renewPolicy(RenewalsForm renForm, User user) throws EndorsementsException {
        if (renForm.getPolicyId() == null || renForm.getPolicyId() == null)
            throw new EndorsementsException("Policy to Renew cannot be Empty....");


        PolicyTrans currentTrans = policyRepo.findOne(renForm.getPolicyId());

        Predicate activePred = QPolicyTrans.policyTrans.polNo.eq(currentTrans.getPolNo())
                .and(QPolicyTrans.policyTrans.currentStatus.eq("A"));

        Predicate renewalPredicate = QPolicyTrans.policyTrans.polNo.eq(currentTrans.getPolNo())
                .and(QPolicyTrans.policyTrans.transType.eq("RN"))
                .and(QPolicyTrans.policyTrans.authStatus.eq("D").or(QPolicyTrans.policyTrans.authStatus.eq("R")));

        if (policyRepo.count(renewalPredicate) > 0) {
            PolicyTrans policy = policyRepo.findOne(renewalPredicate);
            try {
                deletePolicyRecord(policy.getPolicyId(), true);
            } catch (BadRequestException e) {
                throw new EndorsementsException(e.getMessage());
            }
        }

        if (countUnauthTransactions(currentTrans.getPolNo()) > 0) {
            Predicate predicate = QPolicyTrans.policyTrans.policyId.eq(currentTrans.getPolicyId());
            PolicyTrans policy = policyRepo.findOne(predicate);
            policy.setRenewalError("Policy No" + currentTrans.getPolNo() + " has unfinished Transaction");

            policyRepo.save(policy);
            throw new EndorsementsException(
                    "Policy No" + currentTrans.getPolNo() + " has unfinished Transaction");
        }

        if (policyRepo.count(activePred) > 1) {
            PolicyTrans policy = policyRepo.findOne(activePred);
            policy.setRenewalError("Policy No" + currentTrans.getPolNo() + " has an active Endorsement. Only One Active Endorsement can be allowed");
            policyRepo.save(policy);
            throw new EndorsementsException("Policy No" + currentTrans.getPolNo() + " has an active Endorsement. Only One Active Endorsement can be allowed");
        }

        if ("SP".equalsIgnoreCase(currentTrans.getTransType())) {
            Predicate predicate = QPolicyTrans.policyTrans.policyId.eq(currentTrans.getPolicyId());
            PolicyTrans policyTrans = policyRepo.findOne(predicate);
            policyTrans.setRenewalError("Renewal not Supported on Short Period Policy Transactions");
            policyRepo.save(policyTrans);
            throw new EndorsementsException("Renewal not Supported on Short Period Policy Transactions");
        }

        if (currentTrans.getRenewalDate() == null) {
            Predicate predicate = QPolicyTrans.policyTrans.policyId.eq(currentTrans.getPolicyId());
            PolicyTrans policyTrans = policyRepo.findOne(predicate);
            policyTrans.setRenewalError("Cannot Renew the Policy " + currentTrans.getPolNo() + "...Renewal Date is null. Contact System Administrator");
            throw new EndorsementsException("Cannot Renew the Policy " + currentTrans.getPolNo() + "...Renewal Date is null. Contact System Administrator");
        }
        long endorsementCount = policyRepo.count(QPolicyTrans.policyTrans.polNo.eq(currentTrans.getPolNo())) + 1;

        PolicyTrans destination = new PolicyTrans();
        destination.setWefDate(currentTrans.getRenewalDate());
        destination.setWetDate(dateUtils.getWetDate(currentTrans.getRenewalDate()));
        destination.setCoverFrom(currentTrans.getRenewalDate());
        destination.setCoverTo(dateUtils.getWetDate(currentTrans.getRenewalDate()));
        destination.setRenewalDate(DateUtils.addDays(dateUtils.getWetDate(currentTrans.getRenewalDate()), 1));
        destination.setUwYear(dateUtils.getUwYear(currentTrans.getRenewalDate()));
        destination.setPolRevStatus("RN");
        if (currentTrans.getRevisionFormat() == null) {
            destination.setPolRevNo(currentTrans.getPolRevNo().substring(0, currentTrans.getPolRevNo().indexOf("/")) + "/" + endorsementCount);
        } else
            destination.setPolRevNo(currentTrans.getRevisionFormat() + "/" + endorsementCount);
        destination.setPolNo(currentTrans.getPolNo());
        destination.setAgent(currentTrans.getAgent());
        destination.setAuthStatus("D");
        destination.setBinder(currentTrans.getBinder());
        destination.setBranch(currentTrans.getBranch());
        destination.setClient(currentTrans.getClient());
        destination.setClientPolNo(currentTrans.getClientPolNo());
        destination.setCommAllowed(currentTrans.isCommAllowed());
        destination.setCreatedUser(user);
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
        destination.setTransType("RN");
        destination.setSubAgent(currentTrans.getSubAgent());

        SystemTrans transaction = new SystemTrans();
        transaction.setDoneDate(new Date());
        transaction.setDoneBy(user);
        transaction.setPolicy(destination);
        transaction.setTransLevel("U");
        transaction.setTransCode("APD"); //A way to setup and look up for transaction transcode
        transaction.setTransAuthorised("N");
        Iterable<PolicyTaxes> taxes = polTaxesRepo.findAll(QPolicyTaxes.policyTaxes.policy.policyId.eq(currentTrans.getPolicyId()));
        List<PolicyTaxes> newTaxes = new ArrayList<>();
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

        Iterable<PolicyClauses> clauses = polClausesRepo.findAll(QPolicyClauses.policyClauses.policy.policyId.eq(currentTrans.getPolicyId()));
        List<PolicyClauses> newClauses = new ArrayList<>();
        for (PolicyClauses clause : clauses) {
            PolicyClauses newClause = new PolicyClauses();
            newClause.setClauHeading(clause.getClauHeading());
            newClause.setClause(clause.getClause());
            newClause.setClauWording(clause.getClauWording());
            newClause.setEditable(clause.isEditable());
            newClause.setNewClause("N");
            newClause.setPolicy(destination);
            newClauses.add(newClause);
        }
        Iterable<PolicyActiveRisks> activeRisks = activeRisksRepo.findAll(QPolicyActiveRisks.policyActiveRisks.policy.policyId.eq(renForm.getPolicyId()));
        List<PolicyActiveRisks> newActiveRisks = new ArrayList<>();
        for (PolicyActiveRisks activeRisk : activeRisks) {
            PolicyActiveRisks newActiveRisk = new PolicyActiveRisks();
            newActiveRisk.setPolicy(destination);
            newActiveRisk.setRisk(activeRisk.getRisk());
            newActiveRisk.setRiskIdentifier(activeRisk.getRiskIdentifier());
            newActiveRisks.add(newActiveRisk);
        }
        currentTrans.setRenewed(true);
        policyRepo.save(currentTrans);
        PolicyTrans savedPolicy = policyRepo.save(destination);

        polClausesRepo.save(newClauses);
        polTaxesRepo.save(newTaxes);
        activeRisksRepo.save(newActiveRisks);
        transRepo.save(transaction);
        Iterable<PolicyActiveRisks> endorsedRisks = activeRisksRepo.findAll(QPolicyActiveRisks.policyActiveRisks.policy.policyId.eq(savedPolicy.getPolicyId()));
        for (PolicyActiveRisks activeRisk : endorsedRisks) {
            try {
                endorseRisk(activeRisk.getArId(), "R", null);
            } catch (BadRequestException e) {
                throw new EndorsementsException(e.getMessage());
            }
        }


        return savedPolicy.getPolicyId();
    }

    @Override
    public String makeReadyBatchRenewals(BatchRenewalForm renewalForm)
            throws BadRequestException, InterruptedException, ExecutionException {
        Job job = JobBuilder.aNewJob()
                .reader(new IterableRecordReader(renewalForm.getRenewals()))
                .named("renewal_processing" + new SimpleDateFormat("ddMMyyyhhmmss").format(new Date()))
                .processor(new RecordProcessor<Record, Record>() {

                    @Override
                    public Record processRecord(Record renForm) throws Exception {
                        polTransService.makeRenewalReady(((RenewalRecord) renForm.getPayload()).getPayload().getPolicyId());

                        return renForm;
                    }
                })
                //.pipelineListener(new RenewalJobListener())
                .build();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Future<JobReport> report = executorService.submit(job);
        //System.out.println(report);
        //  String error = report.get().getLastError().getMessage();
        return "";
    }

    @Override
    public String authorizeBatchRenewals(BatchRenewalForm renewalForm)
            throws BadRequestException, InterruptedException, ExecutionException {
        Job job = JobBuilder.aNewJob()
                .reader(new IterableRecordReader(renewalForm.getRenewals()))
                .named("renewal_processing" + new SimpleDateFormat("ddMMyyyhhmmss").format(new Date()))
                .processor(new RecordProcessor<Record, Record>() {

                    @Override
                    public Record processRecord(Record renForm) throws Exception {
                        polAuthService.authorizePolicy(((RenewalRecord) renForm.getPayload()).getPayload().getPolicyId(), ((RenewalRecord) renForm.getPayload()).getPayload().getUser());
                        return renForm;
                    }
                })
                //.pipelineListener(new RenewalJobListener())
                .build();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Future<JobReport> report = executorService.submit(job);
        //System.out.println(report);
        // String error = report.get().getLastError().getMessage();
        return "";
    }


}

