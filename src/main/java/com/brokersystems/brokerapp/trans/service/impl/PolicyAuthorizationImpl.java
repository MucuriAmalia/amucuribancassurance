package com.brokersystems.brokerapp.trans.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import com.brokersystems.brokerapp.accounts.model.QPaymentAudit;
import com.brokersystems.brokerapp.accounts.model.Refunds;
import com.brokersystems.brokerapp.accounts.repository.CoaSubAccountsRepo;
import com.brokersystems.brokerapp.accounts.repository.PaymentAuditRepo;
import com.brokersystems.brokerapp.accounts.service.AccountsService;
import com.brokersystems.brokerapp.accounts.utils.AccountsPostingUtilities;
import com.brokersystems.brokerapp.aki.dto.TypeCertificateIssueDTO;
import com.brokersystems.brokerapp.aki.service.AkiAuthenticationService;
import com.brokersystems.brokerapp.bulktransactions.repositories.CreditLifeRepository;
import com.brokersystems.brokerapp.bulktransactions.service.impl.UploadValidatorsUtils;
import com.brokersystems.brokerapp.certs.model.PrintCertificateQueue;
import com.brokersystems.brokerapp.certs.repository.PrintQueueRepo;
import com.brokersystems.brokerapp.dms.model.IntegrationUw;
import com.brokersystems.brokerapp.dms.repo.IntegrationUwRepo;
import com.brokersystems.brokerapp.enums.AccountTypeEnum;
import com.brokersystems.brokerapp.enums.RevenueItems;
import com.brokersystems.brokerapp.integrations.apa.APAIntergrationService;
import com.brokersystems.brokerapp.life.model.*;
import com.brokersystems.brokerapp.life.repository.*;
import com.brokersystems.brokerapp.life.service.LifeService;
import com.brokersystems.brokerapp.mail.model.SendEmailPublisherBean;
import com.brokersystems.brokerapp.medical.model.*;
import com.brokersystems.brokerapp.medical.repository.CategoryMembersRepo;
import com.brokersystems.brokerapp.medical.repository.MedicalCategoryRepo;
import com.brokersystems.brokerapp.medical.repository.SelfFundParamsRepo;
import com.brokersystems.brokerapp.medical.service.MedicalCardsService;
import com.brokersystems.brokerapp.security.CheckAuthLimits;
import com.brokersystems.brokerapp.server.utils.*;
import com.brokersystems.brokerapp.setup.dto.UserDTO;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.*;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.brokersystems.brokerapp.trans.dtos.AllCommissionsDTO;
import com.brokersystems.brokerapp.trans.model.*;
import com.brokersystems.brokerapp.trans.repository.*;
import com.brokersystems.brokerapp.trans.service.AccountsUtilities;
import com.brokersystems.brokerapp.trans.service.AllocationService;
import com.brokersystems.brokerapp.trans.service.ReceiptService;
import com.brokersystems.brokerapp.users.dto.MakerCheckDTO;
import com.brokersystems.brokerapp.users.model.MakerChecker;
import com.brokersystems.brokerapp.users.model.QMakerChecker;
import com.brokersystems.brokerapp.users.repository.MakerCheckerRepo;
import com.brokersystems.brokerapp.users.service.MakerCheckerService;
import com.brokersystems.brokerapp.uw.dtos.PolicyCancellationDTO;
import com.brokersystems.brokerapp.uw.dtos.alak.*;
import com.brokersystems.brokerapp.uw.model.*;
import com.brokersystems.brokerapp.uw.repository.*;
import com.brokersystems.brokerapp.uw.service.PolicyTransService;
import com.brokersystems.brokerapp.uw.service.PremComputeService;
import com.brokersystems.brokerapp.webservices.model.VehicleDetails;
import com.brokersystems.brokerapp.workflow.docs.DocType;
import com.brokersystems.brokerapp.workflow.utils.WorkflowService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.trans.service.PolicyAuthorization;
import com.mysema.query.types.Predicate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

@Service
@Slf4j
public class PolicyAuthorizationImpl implements PolicyAuthorization {

    @Autowired
    private PolicyTransRepo policyRepo;

    @Autowired
    private GlTransRepo glTransRepo;

    @Autowired
    private CommissionPaymentsRepo commissionPaymentsRepo;

    @Autowired
    private SystemTransactionsRepo sysTransRepo;

    @Autowired
    private SettlementRepo settlementRepo;

    @Autowired
    private SystemTransRepo transRepo;

    @Autowired
    private PaymentAuditRepo auditRepo;

    @Autowired
    private UserUtils userUtils;


    @Autowired
    private DataSource dataSource;

    @Autowired
    private SequenceRepository sequenceRepo;

    @Autowired
    private RiskTransRepo riskRepo;

    @Autowired
    private SectionTransRepo sectionRepo;
    @Autowired
    private BinderDetRepo binderDetRepo;

    @Autowired
    private PrintQueueRepo printQueueRepo;

    @Autowired
    private DateUtilities dateUtils;

    @Autowired
    private AllocationService allocationService;

    @Autowired
    private CheckAuthLimits authLimits;

    @Autowired
    private TransMappingRepo mappingRepo;

    @Autowired
    private MotorVehicleDetailsRepo motorVehicleDetailsRepo;

    @Autowired
    private MedicalCategoryRepo categoryRepo;

    @Autowired
    private MedicalCardsService cardsService;

    @Autowired
    private SelfFundParamsRepo selfFundParamsRepo;

    @Autowired
    private CategoryMembersRepo membersRepo;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private AccountsUtilities accountsUtilities;

    @Autowired
    private AccountsPostingUtilities accountsPostingUtilities;

    @Autowired
    private RiskDocsRepo riskDocsRepo;

    @Autowired
    private TransChecksRepo transChecksRepo;

    @Autowired
    private PolicyRemarksRepo policyRemarksRepo;

    @Autowired
    private AccountsService accountsService;

    @Autowired
    private LifeService lifeService;

    @Autowired
    private ReceiptService receiptService;

    @Autowired
    private PolicyBindersRepo policyBindersRepo;

    @Autowired
    private PremComputeService premComputeService;

    @Autowired
    private SystemTransactionsTempRepo systemTransactionsTempRepo;
    @Autowired
    private CoaSubAccountsRepo accountsRepo;
    @Autowired
    private AkiAuthenticationService authenticationService;
    @Autowired
    private BindersRepo bindersRepo;
    @Autowired
    private MakerCheckerRepo makerCheckerRepo;
    private SendEmailPublisherBean sendEmailPublisherBean;
    @Autowired
    private VelocityEngine velocityEngine;
    @Autowired
    private MakerCheckerService makerCheckerService;
    @Autowired
    private CreditLifeRepository creditLifeRepository;
    @Autowired
    private LifeReceiptsRepo lifeReceiptsRepo;
    @Autowired
    private ReceiptRepository receiptRepository;
    @Autowired
    private ReceiptDetailsRepository receiptDetailsRepository;
    @Autowired
    private PolicyBeneficiariesRepo policyBeneficiariesRepo;
    @Autowired
    private ParamService paramService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PolicyMiscInfoRepo policyMiscInfoRepo;
    @Autowired
    private PolicyTransService policyTransService;

    @Autowired
    private UploadValidatorsUtils uploadValidatorsUtils;
    @Autowired
    private APAIntergrationService apaIntergrationService;
    @Autowired
    private IntegrationUwRepo integrationUwRepo;
    @Autowired
    private PolicyInstallmentsRepo policyInstallmentsRepo;
    @Autowired
    private TransMappingRepo transMappingRepo;
    @Autowired
    private LifeSubAgentCommissionRatesRepo lifeSubAgentCommissionRatesRepo;
    @Autowired
    private LifeCommissionRatesRepo lifeCommissionRatesRepo;
    @Autowired
    private AdminFeeSetUpRepo adminFeeSetUpRepo;
    @Autowired
    private LifeReceiptAllocationsRepo lifeReceiptAllocationsRepo;


    public void generateCert(Long polCode) throws BadRequestException {
        PolicyTrans policy = policyRepo.findOne(polCode);
        Iterable<RiskTrans> risks = riskRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(polCode));
        for (RiskTrans riskBean : risks) {
            final List<PrintCertificateQueue> printCertificateQueue = printQueueRepo.findAllByRisk(riskBean);
//            try {

            if (printCertificateQueue.size() == 1) {
                generateCert(riskBean, policy.getPolNo());
            } else {
                throw new BadRequestException("No Certificate Available. Add A Certificate to continue....");
            }
//            }
//            catch (BadRequestException ex){
//                final PrintCertificateQueue printCertificateQueue1 = printCertificateQueue.get(0);
//                printCertificateQueue1.setErrorMessage(ex.getMessage());
//                printCertificateQueue1.setIssued("N");
//                printQueueRepo.save(printCertificateQueue1);
//            }
        }
    }

    @PreAuthorize("hasAnyAuthority('AUTHORIZE_POLICY')")
    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    //public void authorizePolicy(Long polCode, BigDecimal refundAmount) throws BadRequestException {
    public void authorizePolicy(Long polCode, BigDecimal refundAmount, boolean isUpload) throws BadRequestException {
        PolicyTrans policyTrans = policyRepo.findOne(polCode);

        PolicyTrans policy = policyRepo.findOne(polCode);
        boolean cashBasis = policy.getInterfaceType() != null && "C".equalsIgnoreCase(policy.getInterfaceType());

        if (!"R".equalsIgnoreCase(policy.getAuthStatus())) {
            throw new BadRequestException("Can only authorize ready policies");
        }
        if (!authLimits.checkAuthorizationLimits("AUTHORIZE_POLICY", policy.getBasicPrem())) {
            throw new BadRequestException("You have no rights to authorize the transaction...Check your authorization limits..");
        }
        long riskcount = riskRepo.count(QRiskTrans.riskTrans.policy.policyId.eq(polCode));

        if (riskcount == 0) throw new BadRequestException("Cannot Authorize Transaction Without Risk Details");

        List<Object[]> risks = riskRepo.findPolicyRiskTrans(polCode);

        long checkCount = transChecksRepo.count(QTransChecks.transChecks.policyTrans.policyId.eq(polCode).and(QTransChecks.transChecks.authorised.eq("N")));

        if (checkCount > 0) {
            throw new BadRequestException("Cannot Authorize when there is unauthorized checks....");
        }
        BigDecimal prems = (policy.getPremium() == null) ? BigDecimal.ZERO : policy.getPremium();
        if (cashBasis && prems.compareTo(BigDecimal.ZERO) > 0) {
            boolean receiptExists = makerCheckerRepo.exists(QMakerChecker.makerChecker.policyId.eq(polCode).and(QMakerChecker.makerChecker.taskType.equalsIgnoreCase("RC")).and(QMakerChecker.makerChecker.status.equalsIgnoreCase("N")));
            if (cashBasisBalance(polCode).compareTo(BigDecimal.ZERO) > 0 && receiptExists) {
                throw new BadRequestException("A receipt for this policy is pending approval");
            } else {
                throw new BadRequestException("Cannot authorize the Transaction..This is a cash basis transaction with balance of " + policy.getBasicPrem() + ". Please receipt the transaction first");
            }
        }

        Date polWef = dateUtils.removeTime(policy.getWefDate());
        Date polWet = dateUtils.removeTime(policy.getWetDate());
        for (Object[] riskBean : risks) {

            final Date wefDate = (Date) riskBean[7];
            final Date wetDate = (Date) riskBean[8];
            final Long riskId = ((BigInteger) riskBean[1]).longValue();
            final Long binderDetId = ((BigInteger) riskBean[0]).longValue();
            final Long riskBindId = ((BigInteger) riskBean[16]).longValue();
            final String riskShtDesc = (String) riskBean[15];

            final BinderDetails binderDetails = binderDetRepo.findOne(binderDetId);

            if (binderDetails == null) {
                throw new BadRequestException("Invalid Risk Contract Details..Cannot continue...");
            }

            final BindersDef bindersDef = bindersRepo.findOne(riskBindId);
            if (bindersDef == null) {
                throw new BadRequestException("Invalid Risk Contract..Cannot continue...");
            }


            long sectCount = sectionRepo.count(QSectionTrans.sectionTrans.risk.riskId.eq(riskId));
            if (sectCount == 0)
                throw new BadRequestException("Risk " + riskShtDesc + " has no sections..Cannot Authorize the transaction");

            Iterable<RiskDocs> riskDocs = riskDocsRepo.findAll(QRiskDocs.riskDocs.risk.riskId.eq(riskId));
            if (policy.getTransType() != null && !"CN".equalsIgnoreCase(policy.getTransType())) {
                for (RiskDocs riskDoc : riskDocs) {
//                    if (riskDoc.getCheckSum() == null || StringUtils.isBlank(riskDoc.getCheckSum())) {
//                        throw new BadRequestException(String.format("Cannot authorize policy without uploading documents for Risk %s", riskShtDesc));
//                    }
                    if (riskDoc.getVerifiedBy() == null || riskDoc.getVerifiedDate() == null) {

                        throw new BadRequestException(String.format("Cannot authorize policy without verifying documents for Risk %s", riskShtDesc));
                    }
                }
            }

//            Iterable<RiskDocs> riskDocs = riskDocsRepo.findAll(QRiskDocs.riskDocs.risk.riskId.eq(riskBean.getRiskId()));
//            if(policy.getTransType()!=null && !"CN".equalsIgnoreCase(policy.getTransType())) {
//                for (RiskDocs riskDoc : riskDocs) {
//                    if (riskDoc.getCheckSum() == null || StringUtils.isBlank(riskDoc.getCheckSum())) {
//                        throw new BadRequestException(String.format("Cannot authorize policy without uploading documents for Risk %s", riskBean.getRiskShtDesc()));
//                    }
//                }
//            }

            Date riskWef = dateUtils.removeTime(wefDate);
            Date riskWet = dateUtils.removeTime(wetDate);
            if (riskWef.before(polWef) || riskWef.after(polWet)
                    || riskWet.before(polWef) || riskWet.after(polWet)) {
                throw new BadRequestException("Risk Cover Dates outside Policy Cover Periods ");
            }
            if (binderDetails.getBinder().getBinId() != bindersDef.getBinId()) {
                throw new BadRequestException("Cannot Make Ready...Sub Class and Cover Type Details do not match the binder selected");
            }
        }


//        if(policy.getFuturePrem()!=null)
//            if(policy.getFuturePrem().compareTo(BigDecimal.ZERO)==-1) throw new BadRequestException("Policy Future Annual Premium Cannot be negative...");

        String refNo = null;
        String debitCode = null;
        if (cashBasis && prems.compareTo(BigDecimal.ZERO) == 1) {
            SystemTransactionsTemp systemTransactionsTemp = systemTransactionsTempRepo.findOne(QSystemTransactionsTemp.systemTransactionsTemp.policy.policyId.eq(polCode));
            if (systemTransactionsTemp == null)
                throw new BadRequestException("Unable to authorize without payment of the installment premium");
            refNo = systemTransactionsTemp.getRefNo();
            debitCode = systemTransactionsTemp.getTransType();
        } else {
            Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("D");
            if (sequenceRepo.count(seqPredicate) == 0)
                throw new BadRequestException("Sequence for Debit Notes has not been defined");
            SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
            Long seqNumber = sequence.getNextNumber();
            String transType = policy.getTransType();
            if ("CO".equalsIgnoreCase(policy.getTransType())) {
                transType = policy.getPreviousTrans().getTransType();
            }
            if ("NB".equalsIgnoreCase(transType)) {
                transType = "NB";
            } else if ("RN".equalsIgnoreCase(transType)) {
                transType = "RN";
            } else if ("CN".equalsIgnoreCase(transType)) {
                transType = "CN";
            } else if ("RE".equalsIgnoreCase(transType)) {
                transType = "RE";
            } else if ("RF".equalsIgnoreCase(transType)) {
                transType = "RF";
            } else if ("BU".equalsIgnoreCase(transType)) {
                transType = "BU";
            } else {
                transType = "EN";
            }
            if (mappingRepo.count(QTransactionMapping.transactionMapping.transType.eq(transType)) == 0)
                throw new BadRequestException("Error getting Transaction Mapping Setups..Contact System Administrator");
            TransactionMapping mapping = mappingRepo.findOne(QTransactionMapping.transactionMapping.transType.eq(transType));
            refNo = ((prems.compareTo(BigDecimal.ZERO) >= 0) ? mapping.getDebitCode() : mapping.getCreditCode()) + String.format("%05d", seqNumber);
            debitCode = ((prems.compareTo(BigDecimal.ZERO) >= 0) ? mapping.getDebitCode() : mapping.getCreditCode());
            sequence.setLastNumber(seqNumber);
            sequence.setNextNumber(seqNumber + 1);
            sequenceRepo.save(sequence);
        }

        policy.setAuthBy(userUtils.getCurrentUser());
        policy.setAuthStatus("A");
        policy.setAuthDate(new Date());
        if ("CO".equalsIgnoreCase(policy.getTransType()) || "CN".equalsIgnoreCase(policy.getTransType()) || "RS".equalsIgnoreCase(policy.getTransType())) {
            if (!isUpload) {
                Iterable<PolicyRemarks> policyRemarks = policyRemarksRepo.findAll(QPolicyRemarks.policyRemarks.policy.policyId.eq(policy.getPolicyId()));
                if (policyRemarks.spliterator().getExactSizeIfKnown() == 0) {
                    throw new BadRequestException("Input Policy Remarks first....");
                }
            }

            Long makerId = policyTrans.getCreatedUser().getId();
            Long checkerId = userUtils.getCurrentUser().getId();

            if ("CO".equalsIgnoreCase(policy.getTransType())) {
                policy.setCurrentStatus("CO");
            } else {
                policy.setCurrentStatus("A");
            }

            if ("CN".equalsIgnoreCase(policy.getTransType())) {
//                if (makerId.equals(checkerId)) {
//                    log.warn("Maker and checker are the same user: {}", makerId);
//                    throw new BadRequestException("You can't approve a task you've initiated");
//                }
                policy.setCurrentStatus("CN");
                log.info("Fetching MakerChecker for policyId={}, taskType='CN', status='N'", polCode);
                Iterable<MakerChecker> makerCheckers = makerCheckerRepo.findAll(
                        QMakerChecker.makerChecker.policyId.eq(polCode)
                                .and(QMakerChecker.makerChecker.taskType.equalsIgnoreCase("CN"))
                                .and(QMakerChecker.makerChecker.status.equalsIgnoreCase("N"))
                );
                for (MakerChecker makerChecker : makerCheckers) {
                    if (makerChecker != null) {
                        log.info("MakerChecker found: id={}, current status={}", makerChecker.getId(), makerChecker.getStatus());
                        makerChecker.setStatus("A"); // or whatever status is appropriate
                        makerChecker.setCheckerId(userUtils.getCurrentUser());
                        makerChecker.setCheckDate(new Date());
                        makerCheckerRepo.save(makerChecker);
                        policy.setCurrentStatus("CN");
                        log.info("MakerChecker status updated to 'A' and saved for id={}", makerChecker.getId());
                        log.info("MakerChecker status updated to 'A' and saved.");
                    } else {
                        log.warn("No MakerChecker found for policyId={}, taskType='CN', status='N'", polCode);
                    }
                }

            }

        } else
            policy.setCurrentStatus("A");
        policy.setRefNo(refNo);
        policyRepo.save(policy);

        if (!("NB".equalsIgnoreCase(policy.getTransType()) || "SP".equalsIgnoreCase(policy.getTransType()))) {
            PolicyTrans prevPolicy = policy.getPreviousTrans();
            if ("CO".equalsIgnoreCase(policy.getTransType()) && prevPolicy.getPreviousTrans().getPolicyId() != prevPolicy.getPolicyId()) {
                prevPolicy.setCurrentStatus(policy.getTransType());
                PolicyTrans prevPolicy1 = prevPolicy.getPreviousTrans();
                if (!prevPolicy1.getTransType().equalsIgnoreCase("CO")) {
                    prevPolicy1.setCurrentStatus("A");
                    policyRepo.save(prevPolicy1);
                }
            } else if ("CN".equalsIgnoreCase(policy.getTransType())) {
                prevPolicy.setCurrentStatus(prevPolicy.getTransType());
            } else {
                prevPolicy.setCurrentStatus(policy.getTransType());
            }
            policyRepo.save(prevPolicy);
        }

        if ("RE".equalsIgnoreCase(policy.getPolRevStatus())) {
            PolicyTrans prevPolicy = policy.getPreviousTrans();
            prevPolicy.setCurrentStatus(policy.getTransType());
            policyRepo.save(prevPolicy);
        }


        if (transRepo.count(QSystemTrans.systemTrans.policy.policyId.eq(polCode).and(QSystemTrans.systemTrans.transAuthorised.eq("N"))) > 1) {
            throw new BadRequestException("More than one Unauthorized Transactions for the Policy..Contact System Admin");
        }

        if (transRepo.count(QSystemTrans.systemTrans.policy.policyId.eq(polCode).and(QSystemTrans.systemTrans.transAuthorised.eq("N"))) == 0) {
            throw new BadRequestException("No Transaction to Authorize..Restart the Endorsement");
        }

        //accountsUtilities.validatePolicyAccounts(policy);

        SystemTrans transaction = transRepo.findOne(QSystemTrans.systemTrans.policy.policyId.eq(polCode).and(QSystemTrans.systemTrans.transAuthorised.eq("N")));
        transaction.setAuthBy(userUtils.getCurrentUser());
        transaction.setAuthDate(new Date());
        transaction.setTransAuthorised("Y");
        transRepo.save(transaction);
        BigDecimal basicPrem = (policy.getPremium() == null) ? BigDecimal.ZERO : policy.getPremium();
        BigDecimal extras = (policy.getExtras() == null) ? BigDecimal.ZERO : policy.getExtras();
        BigDecimal phcf = (policy.getPhcf() == null) ? BigDecimal.ZERO : policy.getPhcf();
        BigDecimal tl = (policy.getTrainingLevy() == null) ? BigDecimal.ZERO : policy.getTrainingLevy();
        BigDecimal sd = (policy.getStampDuty() == null) ? BigDecimal.ZERO : policy.getStampDuty();
        BigDecimal subAgentComm = (policy.getSubAgentComm() == null) ? BigDecimal.ZERO : policy.getSubAgentComm();
        BigDecimal amountWithTaxes = basicPrem.add(extras).add(phcf).add(tl).add(sd);
        BigDecimal adminFeeAmt = BigDecimal.ZERO;
        if (policy.getAdminFeeApplicable() != null && "Y".equalsIgnoreCase(policy.getAdminFeeApplicable())) {
            if (policy.getAdminFeeAmt() != null && policy.getAdminFeeVatAmt() != null) {
                adminFeeAmt = policy.getAdminFeeAmt().subtract(policy.getAdminFeeVatAmt());
            } else if (policy.getAdminFeeAmt() != null && policy.getAdminFeeVatAmt() == null) {
                adminFeeAmt = policy.getAdminFeeAmt();
            }
        }


        SystemTransactions savedAgentTrans = null;
//       if (policy.getPolicyId()!=null) {
//            throw new BadRequestException("Refund amount="+refundAmount );
//        }
        //Client Transaction
//        if(sysTransRepo.getTotalSubAgentFee(policy.getPolNo()).compareTo(BigDecimal.ZERO) < 0){
//            SystemTransactions trans = new SystemTransactions();
//            trans.setAmount(policy.getSubAgentComm().abs().multiply(sign("D")));
//            trans.setAuthDate(new Date());
//            trans.setAuthorised("Y");
//            trans.setBalance(policy.getSubAgentComm().abs().multiply(sign("D")));
//            trans.setBranch(policy.getBranch());
//            trans.setClientType("C");
//            trans.setControlAcc(policy.getSubAgent().getShtDesc());
//            trans.setClient(policy.getClient());
//            trans.setCurrRate(new BigDecimal(1));
//            trans.setCurrency(policy.getTransCurrency());
//            trans.setNarrations("Sub Agent Commission Trans");
//            trans.setNetAmount(policy.getSubAgentComm().abs().multiply(sign("D")));
//            trans.setOrigin("U");
//            trans.setPolicy(policy);
//            trans.setRefNo(policy.getRefNo());
//            trans.setTransDate(new Date());
//            trans.setTransdc("D");
//            trans.setTransType("SAG"); //Should not be hardcorded
//            trans.setUserAuth(userUtils.getCurrentUser().getUsername());
//            trans.setWhtx(BigDecimal.ZERO);
//            trans.setTransaction(transaction);
//            trans.setPostedDate(new Date());
//            trans.setPostedUser(userUtils.getCurrentUser());
//            trans.setAgent(policy.getSubAgent());
//            sysTransRepo.save(trans);
//        }
        if (basicPrem.compareTo(BigDecimal.ZERO) != 0) {
            String type = (amountWithTaxes.compareTo(BigDecimal.ZERO) == 1) ? "D" : "C";
            SystemTransactions trans = new SystemTransactions();
            trans.setAmount(basicPrem.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            trans.setAuthDate(new Date());
            trans.setAuthorised("Y");
            trans.setAdminFeeNet(adminFeeAmt.multiply(sign(type)));
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
            trans.setRefNo(refNo);
            trans.setSd(sd.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            trans.setTl(tl.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            trans.setTransDate(new Date());
            trans.setValueDate(java.sql.Date.valueOf(HolidayUtils.getAdjustedTransactionDate(LocalDate.now())));

            trans.setTransdc((amountWithTaxes.compareTo(BigDecimal.ZERO) == 1) ? "D" : "C");
            trans.setTransType(debitCode); //Should not be hardcorded
            trans.setUserAuth(userUtils.getCurrentUser().getUsername());
            trans.setWhtx(BigDecimal.ZERO);
            trans.setExtras(extras.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            trans.setPostedDate(new Date());

            trans.setPostedUser(userUtils.getCurrentUser());
            trans.setTransaction(transaction);
            SystemTransactions savedClientTrans = sysTransRepo.save(trans);

            if (type.equalsIgnoreCase("C")) {
/*             Iterable<SystemTransactions> trs=   sysTransRepo.findAll(QSystemTransactions.systemTransactions.policy.polNo.eq(policy.getPolNo())
                                .and(QSystemTransactions.systemTransactions.transdc.eq("D")));

             for(SystemTransactions rt:trs){
                 System.out.println(rt.getTransType()+"=="+rt.getClientType());
             }*/
                Optional<SystemTransactions> clientTrans = Streamable.streamOf(sysTransRepo.findAll(QSystemTransactions.systemTransactions.policy.polNo.eq(policy.getPolNo())
                        .and(QSystemTransactions.systemTransactions.transdc.eq("D"))
                        .and(QSystemTransactions.systemTransactions.transType.in("NBD", "APD", "RND","LD","BUD"))
                        .and(QSystemTransactions.systemTransactions.clientType.eq("C")))).findFirst();
                if (!clientTrans.isPresent()) {
                    throw new BadRequestException("Unable to get any debit transaction for this transaction...");
                }
//                if (!commissionPaymentsRepo.findUnauthCommissions(policy.getPolNo()).isEmpty()) {
//                    List<BigInteger> transs = commissionPaymentsRepo.findUnauthCommissions(policy.getPolNo());
//                    List<CommissionPayments> commissionPaymentsList = new ArrayList<>();
//                    for (BigInteger tran : transs) {
//                        CommissionPayments commissionPayments = commissionPaymentsRepo.findOne(tran.longValue());
//                        commissionPayments.setWithDrawn("Y");
//                        commissionPayments.setWithDrawnDate(new Date());
//                        commissionPayments.setWithDrawnBy(userUtils.getCurrentUser());
//                        commissionPaymentsList.add(commissionPayments);
//                    }
//                    commissionPaymentsRepo.save(commissionPaymentsList);
//                } else
                if (!commissionPaymentsRepo.findauthCommissions(policy.getPolNo()).isEmpty()) {
                    if(policy.getCommAmt() .compareTo(BigDecimal.ZERO) > 0)
                    allocationService.saveSubMarketerWithoutGl(policy.getPolicyId(),policy.getMarketerAgentComm(),policy.getSubAgentComm(),transaction,clientTrans.get(),savedClientTrans);
                    final BigDecimal commAmt = (policy.getCommAmt() != null) ? policy.getCommAmt() : BigDecimal.ZERO;
                    final BigDecimal whtx = (policy.getWhtx() != null) ? policy.getWhtx() : BigDecimal.ZERO;
                    final CommissionPayments commissionPayments = new CommissionPayments();
                    commissionPayments.setProcessed("N");
                    commissionPayments.setDate(new Date());
                    commissionPayments.setCurrencies(policy.getTransCurrency());
                    commissionPayments.setWhtx(BigDecimal.valueOf(whtx.abs().doubleValue() * 1));
                    commissionPayments.setAmount(BigDecimal.valueOf(commAmt.abs().doubleValue() * -1));
                    commissionPayments.setNetAmount(BigDecimal.valueOf((commAmt.abs().subtract(whtx.abs())).doubleValue() * -1));
                    commissionPayments.setDebitTransaction(clientTrans.get());
                    commissionPayments.setCreditTransaction(savedClientTrans);
                    commissionPayments.setTransType("Commission");
                    commissionPayments.setProcessedBy(userUtils.getCurrentUser());
                    commissionPayments.setAuthorised("N");
                    commissionPaymentsRepo.save(commissionPayments);


                    if (policy.getAdminFeeAmt() != null && policy.getAdminFeeAmt().compareTo(BigDecimal.ZERO)!=0) {
                        final BigDecimal adminWhtx = (policy.getAdminFeeAmt() != null) ? policy.getAdminFeeAmt() : BigDecimal.ZERO;
                        final CommissionPayments adminCommissionPayments = new CommissionPayments();
                        adminCommissionPayments.setProcessed("N");
                        adminCommissionPayments.setDate(new Date());
                        adminCommissionPayments.setCurrencies(policy.getTransCurrency());
                        adminCommissionPayments.setWhtx(BigDecimal.valueOf(adminWhtx.abs().doubleValue() * 1));
                        adminCommissionPayments.setAmount(BigDecimal.valueOf(policy.getAdminFeeAmt().abs().doubleValue() * -1));
                        adminCommissionPayments.setNetAmount(BigDecimal.valueOf((policy.getAdminFeeAmt().abs().subtract(adminWhtx.abs())).doubleValue() * -1));
                        adminCommissionPayments.setDebitTransaction(clientTrans.get());
                        adminCommissionPayments.setCreditTransaction(savedClientTrans);
                        adminCommissionPayments.setTransType("Admin Fee");
                        adminCommissionPayments.setProcessedBy(userUtils.getCurrentUser());
                        adminCommissionPayments.setAuthorised("N");
                        commissionPaymentsRepo.save(commissionPayments);
                    }

                    if (policy.getSubAgentComm() != null  && policy.getSubAgentComm().compareTo(BigDecimal.ZERO)!=0) {
                        final CommissionPayments commissionPaymentsSub = new CommissionPayments();
                        commissionPaymentsSub.setProcessed("N");
                        commissionPaymentsSub.setDate(new Date());
                        commissionPaymentsSub.setCurrencies(policy.getTransCurrency());
                        final BigDecimal whtmount = BigDecimal.ZERO;
                        final BigDecimal comAmt = policy.getSubAgentComm();
                        commissionPaymentsSub.setWhtx(BigDecimal.valueOf(whtmount.abs().doubleValue() * 1));
                        commissionPaymentsSub.setAmount(BigDecimal.valueOf(comAmt.abs().doubleValue() * -1));
                        commissionPaymentsSub.setNetAmount(BigDecimal.valueOf((comAmt.abs().subtract(whtmount.abs())).doubleValue() * -1));
                        commissionPaymentsSub.setDebitTransaction(clientTrans.get());
                        commissionPaymentsSub.setCreditTransaction(savedClientTrans);
                        commissionPaymentsSub.setTransType("Sub Agent Commission");
                        commissionPaymentsSub.setProcessedBy(userUtils.getCurrentUser());
                        commissionPaymentsSub.setAuthorised("N");
                        commissionPaymentsRepo.save(commissionPaymentsSub);
                    }

                    if (policy.getMarketerAgentComm() != null && policy.getMarketerAgentComm().compareTo(BigDecimal.ZERO)!=0) {
                        final CommissionPayments commissionPaymentsSub = new CommissionPayments();
                        commissionPaymentsSub.setProcessed("N");
                        commissionPaymentsSub.setDate(new Date());
                        commissionPaymentsSub.setCurrencies(policy.getTransCurrency());
                        final BigDecimal whtmount = BigDecimal.ZERO;
                        final BigDecimal comAmt = policy.getMarketerAgentComm();
                        commissionPaymentsSub.setWhtx(BigDecimal.valueOf(whtmount.abs().doubleValue() * 1));
                        commissionPaymentsSub.setAmount(BigDecimal.valueOf(comAmt.abs().doubleValue() * -1));
                        commissionPaymentsSub.setNetAmount(BigDecimal.valueOf((comAmt.abs().subtract(whtmount.abs())).doubleValue() * -1));
                        commissionPaymentsSub.setDebitTransaction(clientTrans.get());
                        commissionPaymentsSub.setCreditTransaction(savedClientTrans);
                        commissionPaymentsSub.setTransType("Sub Agent Commission");
                        commissionPaymentsSub.setProcessedBy(userUtils.getCurrentUser());
                        commissionPaymentsSub.setAuthorised("N");
                        commissionPaymentsRepo.save(commissionPaymentsSub);
                    }
                }
            }
            if ("CO".equalsIgnoreCase(policy.getTransType())) {
                String refno = policy.getPreviousTrans().getRefNo();
                System.out.println("Ref no " + refno);
                SystemTransactions clientTrans = sysTransRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(refno)
                        .and(QSystemTransactions.systemTransactions.transdc.eq("D"))
                        .and(QSystemTransactions.systemTransactions.transType.in("NBD", "APD", "RND"))
                        .and(QSystemTransactions.systemTransactions.clientType.eq("C")));
                allocationService.autoAllocateContra(savedClientTrans.getTransno(), clientTrans.getTransno());
            }

            final boolean multiproduct = policyBindersRepo.count(QPolicyBinders.policyBinders.policyTrans.policyId.eq(policy.getPolicyId())) > 0;
            final String agntSign = (basicPrem.compareTo(BigDecimal.ZERO) == -1) ? "D" : "C";
            if (multiproduct) {
                Iterable<PolicyBinders> policyBinders = policyBindersRepo.findAll(QPolicyBinders.policyBinders.policyTrans.policyId.eq(polCode));
                int counter = 0;
                for (PolicyBinders binders : policyBinders) {
                    counter++;
                    BigDecimal commamt = (binders.getCommission() == null) ? BigDecimal.ZERO : binders.getCommission();
                    BigDecimal whtx = (binders.getWhtx() == null) ? BigDecimal.ZERO : binders.getWhtx();
                    phcf = (binders.getPhcf() == null) ? BigDecimal.ZERO : binders.getPhcf();
                    tl = (binders.getTl() == null) ? BigDecimal.ZERO : binders.getTl();
                    sd = BigDecimal.ZERO;
                    BigDecimal agentAmt = (binders.getBasicPrem().abs().multiply(sign(agntSign)).add(phcf.abs().multiply(sign(agntSign))).
                            add(tl.abs().multiply(sign(agntSign))).subtract(commamt.abs().multiply(sign(agntSign))).add(whtx.abs().multiply(sign(agntSign))));
                    //Agent Transaction
                    SystemTransactions atrans = new SystemTransactions();
                    atrans.setAmount(binders.getBasicPrem().abs().multiply(sign(agntSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    atrans.setAuthDate(new Date());
                    atrans.setAuthorised("Y");
                    atrans.setBalance(agentAmt.setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    atrans.setBranch(policy.getBranch());
                    atrans.setClientType("A");
                    atrans.setControlAcc(binders.getBinder().getAccount().getShtDesc());
                    atrans.setAgent(binders.getBinder().getAccount());
                    atrans.setAdminFeeNet(adminFeeAmt.abs().multiply(sign(agntSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    atrans.setCommission(commamt.abs().multiply(sign(agntSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    atrans.setCurrRate(new BigDecimal(1));
                    atrans.setCurrency(policy.getTransCurrency());
                    atrans.setNarrations("Posting Agent Credit Note");
                    atrans.setNetAmount(agentAmt.setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    atrans.setOrigin("U");
                    atrans.setPhfund(phcf.abs().multiply(sign(agntSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    atrans.setPolicy(policy);
                    atrans.setRefNo(refNo + "/" + counter);
                    atrans.setSd(sd.abs().multiply(sign(agntSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    atrans.setTl(tl.abs().multiply(sign(agntSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    atrans.setTransDate(new Date());
                    atrans.setValueDate(java.sql.Date.valueOf(HolidayUtils.getAdjustedTransactionDate(LocalDate.now())));
                    atrans.setTransdc(agntSign);
                    atrans.setTransType(debitCode); //Should not be hardcorded
                    atrans.setUserAuth(userUtils.getCurrentUser().getUsername());
                    atrans.setWhtx(whtx.abs().multiply(sign(agntSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    atrans.setExtras(extras.abs().multiply(sign(agntSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    atrans.setPostedDate(new Date());
                    atrans.setPostedUser(userUtils.getCurrentUser());
                    atrans.setTransaction(transaction);
                    savedAgentTrans = sysTransRepo.save(atrans);
                }
                postUwTransactions(policy, transaction, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, savedAgentTrans);
            } else {
                BigDecimal commamt = (policy.getCommAmt() == null) ? BigDecimal.ZERO : policy.getCommAmt();
                BigDecimal whtx = (policy.getWhtx() == null) ? BigDecimal.ZERO : policy.getWhtx();
                BigDecimal agentAmt = (basicPrem.abs().multiply(sign(agntSign)).add(extras.abs().multiply(sign(agntSign))).add(phcf.abs().multiply(sign(agntSign))).
                        add(tl.abs().multiply(sign(agntSign))).add(sd.abs().multiply(sign(agntSign))).subtract(commamt.abs().multiply(sign(agntSign))).add(whtx.abs().multiply(sign(agntSign))));
                //Agent Transaction
                SystemTransactions atrans = new SystemTransactions();
                atrans.setAmount(basicPrem.abs().multiply(sign(agntSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                atrans.setAuthDate(new Date());
                atrans.setAuthorised("Y");
                atrans.setAdminFeeNet(adminFeeAmt.multiply(sign(agntSign)));
                atrans.setBalance(agentAmt.setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                atrans.setBranch(policy.getBranch());
                atrans.setClientType("A");
                atrans.setControlAcc(policy.getAgent().getShtDesc());
                atrans.setAgent(policy.getAgent());
                atrans.setCommission(commamt.abs().multiply(sign(agntSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                atrans.setCurrRate(new BigDecimal(1));
                atrans.setCurrency(policy.getTransCurrency());
                atrans.setNarrations("Posting Agent Credit Note");
                atrans.setNetAmount(agentAmt.setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                atrans.setOrigin("U");
                atrans.setPhfund(phcf.abs().multiply(sign(agntSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                atrans.setPolicy(policy);
                atrans.setRefNo(refNo);
                atrans.setSd(sd.abs().multiply(sign(agntSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                atrans.setTl(tl.abs().multiply(sign(agntSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                atrans.setTransDate(new Date());
                atrans.setValueDate(java.sql.Date.valueOf(HolidayUtils.getAdjustedTransactionDate(LocalDate.now())));

                atrans.setTransdc(agntSign);
                atrans.setTransType(debitCode); //Should not be hardcorded
                atrans.setUserAuth(userUtils.getCurrentUser().getUsername());
                atrans.setWhtx(whtx.abs().multiply(sign(agntSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                atrans.setExtras(extras.abs().multiply(sign(agntSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                atrans.setPostedDate(new Date());
                atrans.setPostedUser(userUtils.getCurrentUser());
                atrans.setTransaction(transaction);
                savedAgentTrans = sysTransRepo.save(atrans);
                postUwTransactions(policy, transaction, commamt, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, savedAgentTrans);
            }
            if (!multiproduct && "CO".equalsIgnoreCase(policy.getTransType())) {
                String refno = policy.getPreviousTrans().getRefNo();
                SystemTransactions agentTrans = sysTransRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(refno)
                        .and(QSystemTransactions.systemTransactions.transdc.eq("C")));
                long authcount = auditRepo.count(QPaymentAudit.paymentAudit.transNo.transno.eq(agentTrans.getTransno()).and(QPaymentAudit.paymentAudit.posted.eq("Y")));
                if (authcount == 0)
                    allocationService.autoAllocateContra(agentTrans.getTransno(), savedAgentTrans.getTransno());

                else {
                    SystemTransactions clientTrans = sysTransRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(refno)
                            .and(QSystemTransactions.systemTransactions.transdc.eq("D")));
                    Iterable<ReceiptSettlementDetails> settlements = settlementRepo.findAll(QReceiptSettlementDetails.receiptSettlementDetails.drCr.eq("C")
                            .and(QReceiptSettlementDetails.receiptSettlementDetails.debit.transno.eq(clientTrans.getTransno())));

                    BigDecimal totalReceipted = BigDecimal.ZERO;
                    for (ReceiptSettlementDetails details : settlements) {
                        totalReceipted = totalReceipted.add(details.getAllocatedAmt());
                    }
                    double prorationRate = totalReceipted.doubleValue() / (agentTrans.getNetAmount().doubleValue());
                    BigDecimal prorata = new BigDecimal(prorationRate);
                    if (prorata.compareTo(BigDecimal.ONE) == 1)
                        throw new BadRequestException("Error Doing contra...Contact Admin to check the settlement details");
                    SystemTransactions revTrans = new SystemTransactions();
                    revTrans.setAmount(agentTrans.getAmount().abs().multiply(sign("D")).multiply(prorata).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    revTrans.setAuthDate(new Date());
                    revTrans.setAuthorised("Y");
                    revTrans.setBalance(agentTrans.getBalance().abs().multiply(sign("D")).multiply(prorata).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    revTrans.setBranch(policy.getBranch());
                    revTrans.setClientType("A");
                    revTrans.setControlAcc(agentTrans.getControlAcc());
                    revTrans.setAgent(agentTrans.getAgent());
                    revTrans.setCommission(agentTrans.getCommission().abs().multiply(sign("D")).multiply(prorata).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    if (agentTrans.getAdminFeeNet() != null) {
                        revTrans.setAdminFeeNet(agentTrans.getAdminFeeNet().abs().multiply(sign("D")).multiply(prorata).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    }
                    revTrans.setCurrRate(new BigDecimal(1));
                    revTrans.setCurrency(policy.getTransCurrency());
                    revTrans.setNarrations("Posting Agent Debit Note");
                    revTrans.setNetAmount(agentTrans.getNetAmount().abs().multiply(sign("D")).multiply(prorata).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    revTrans.setOrigin("U");
                    revTrans.setPhfund(agentTrans.getPhfund().abs().multiply(sign("D")).multiply(prorata).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    revTrans.setPolicy(policy);
                    revTrans.setRefNo(refNo);
                    revTrans.setSd(agentTrans.getSd().abs().multiply(sign("D")).multiply(prorata).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    revTrans.setTl(agentTrans.getTl().abs().multiply(sign("D")).multiply(prorata).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    revTrans.setTransDate(new Date());
                    revTrans.setValueDate(java.sql.Date.valueOf(HolidayUtils.getAdjustedTransactionDate(LocalDate.now())));

                    revTrans.setTransdc("D");
                    revTrans.setTransType(debitCode); //Should not be hardcorded
                    revTrans.setUserAuth(userUtils.getCurrentUser().getUsername());
                    revTrans.setWhtx(agentTrans.getWhtx().abs().multiply(sign("D")).multiply(prorata).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    revTrans.setExtras(agentTrans.getExtras().abs().multiply(sign("D")).multiply(prorata).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    revTrans.setTransaction(transaction);
                    sysTransRepo.save(revTrans);
                }
            }

            // add refund here
            if (policy.getRefundablePremium() != null && policy.getRefundablePremium().compareTo(BigDecimal.ZERO) != 0) {
                if (mappingRepo.count(QTransactionMapping.transactionMapping.transType.eq("RF")) == 0)
                    throw new BadRequestException("Error getting Transaction Mapping Setups For Refund..Contact System Administrator");
                TransactionMapping mapping = mappingRepo.findOne(QTransactionMapping.transactionMapping.transType.eq("RF"));
                Predicate refSeqPredicate = QSystemSequence.systemSequence.transType.eq("RFD");
                if (sequenceRepo.count(refSeqPredicate) == 0)
                    throw new BadRequestException("Sequence for refund has not been defined");
                SystemSequence refundSequence = sequenceRepo.findOne(refSeqPredicate);
                Long refSeqNumber = refundSequence.getNextNumber();
//                final String refundCode = ((prems.compareTo(BigDecimal.ZERO) >= 0) ? mapping.getDebitCode() : mapping.getCreditCode());
//                final String refundRefNo = ((prems.compareTo(BigDecimal.ZERO) >= 0) ? mapping.getDebitCode() : mapping.getCreditCode()) + String.format("%05d", refSeqNumber);

                refundSequence.setLastNumber(refSeqNumber);
                refundSequence.setNextNumber(refSeqNumber + 1);
                sequenceRepo.save(refundSequence);

                //type = (amountWithTaxes.compareTo(BigDecimal.ZERO) == 1) ? "C" : "D";
                // client
//                SystemTransactions trans1 = new SystemTransactions();
//                trans1.setAmount(refundAmount.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
//                trans1.setAuthDate(new Date());
//                trans1.setAuthorised("N");
//                trans1.setBalance(refundAmount.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
//                trans1.setBranch(policy.getBranch());
//                trans1.setClientType("C");
//                trans1.setControlAcc(policy.getClient().getTenantNumber());
//                trans1.setClient(policy.getClient());
//                trans1.setCurrRate(new BigDecimal(1));
//                trans1.setCurrency(policy.getTransCurrency());
//                trans1.setNarrations("Posting client underwriting refund");
//                trans1.setNetAmount(refundAmount.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
//                trans1.setOrigin("U");
//                trans1.setPolicy(policy);
//                trans1.setRefNo(refundRefNo);
//                trans1.setTransDate(new Date());
//                trans1.setTransdc((amountWithTaxes.compareTo(BigDecimal.ZERO) == 1) ? "C" : "D");
//                trans1.setTransType(refundCode); //Should not be hardcorded
//                //trans.setUserAuth(userUtils.getCurrentUser().getUsername());
//                trans1.setWhtx(BigDecimal.ZERO);
//                //trans.setExtras(extras.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(),BigDecimal.ROUND_HALF_EVEN));
//                // trans.setPostedDate(new Date());
//                //trans.setPostedUser(userUtils.getCurrentUser());
//                trans1.setTransaction(transaction);
//                trans1.setRefundTransaction(savedClientTrans);
//                SystemTransactions savedRefundTrans = sysTransRepo.save(trans1);

//                // agent
//                agntSign =(basicPrem.compareTo(BigDecimal.ZERO)==-1)?"D":"C";
//                SystemTransactions atrans1 = new SystemTransactions();
//                atrans1.setAmount(refundAmount.abs().multiply(sign(agntSign)).setScale(policy.getTransCurrency().getRoundOff(),BigDecimal.ROUND_HALF_EVEN));
//                atrans1.setAuthDate(new Date());
//                atrans1.setAuthorised("N");
//                atrans1.setBalance(refundAmount.abs().multiply(sign(agntSign)).setScale(policy.getTransCurrency().getRoundOff(),BigDecimal.ROUND_HALF_EVEN));
//                atrans1.setBranch(policy.getBranch());
//                atrans1.setClientType("A");
//                atrans1.setControlAcc(policy.getAgent().getShtDesc());
//                atrans1.setAgent(policy.getAgent());
//                atrans1.setCurrRate(new BigDecimal(1));
//                atrans1.setCurrency(policy.getTransCurrency());
//                atrans1.setNarrations("Posting agent underwriting refund");
//                atrans1.setNetAmount(refundAmount.abs().multiply(sign(agntSign)).setScale(policy.getTransCurrency().getRoundOff(),BigDecimal.ROUND_HALF_EVEN));
//                atrans1.setOrigin("U");
//                atrans1.setPolicy(policy);
//                atrans1.setRefNo(refundRefNo);
//                atrans1.setTransDate(new Date());
//                atrans1.setTransdc((amountWithTaxes.compareTo(BigDecimal.ZERO)==1)?"C":"D");
//                atrans1.setTransType(refundCode); //Should not be hardcorded
//                //trans.setUserAuth(userUtils.getCurrentUser().getUsername());
//                atrans1.setWhtx(BigDecimal.ZERO);
//                //trans.setExtras(extras.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(),BigDecimal.ROUND_HALF_EVEN));
//                // trans.setPostedDate(new Date());
//                //trans.setPostedUser(userUtils.getCurrentUser());
//                atrans1.setTransaction(transaction);
//                SystemTransactions savedAgentRefundTrans =  sysTransRepo.save(atrans1);


                //-----refunds ----------
                Refunds refund = new Refunds();
                refund.setRefundCaptureDate(new Date());
                refund.setCreatedUser(userUtils.getCurrentUser());
                refund.setMadeReadyBy(userUtils.getCurrentUser());
                refund.setMakeReadyDate(new Date());
                refund.setClient(policy.getClient());
                refund.setRefundStatus("R");
                refund.setAmount(policy.getRefundablePremium());
                refund.setNarrations("Posting client underwriting refund");
                refund.setPayee(policy.getClient().getFname().concat(policy.getClient().getOtherNames()));
                refund.setPolicy(policy);
                //  refund.setTransactions(savedRefundTrans);
//                accountsService.createRefund(refund, "R");
            }
        }

        if (cashBasis && prems.compareTo(BigDecimal.ZERO) == 1) {
            allocationService.allocateCashBasisTrans(polCode, userUtils.getCurrentUser());
        }
        boolean medicalProduct = false;
        if (policy.getProduct().getProGroup().getPrgType() == null || !policy.getProduct().getProGroup().getPrgType().equalsIgnoreCase("MD")) {
            medicalProduct = false;
        } else if (policy.getProduct().getProGroup().getPrgType().equalsIgnoreCase("MD")) {
            medicalProduct = true;
        }
        try {
            Map<String, Object> processVariables = Maps.newHashMap();
            processVariables.put("confirmAuth", true);
            workflowService.completeTask(String.valueOf(polCode), processVariables, policy, DocType.GEN_UW_DOCUMENT, (medicalProduct) ? "Y" : "N", null, null, null, null);
        } catch (Exception e) {
            throw new BadRequestException("Authorize Checks first....");
        }
        MakerChecker makerChecker = makerCheckerRepo.findOne(QMakerChecker.makerChecker.policyId.eq(polCode).and(QMakerChecker.makerChecker.taskType.equalsIgnoreCase("ANP")));
        Iterable<RiskTrans> riskTrans = riskRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(polCode));
        for (RiskTrans risk : riskTrans) {
            Iterable<RiskDocs> riskDocs = riskDocsRepo.findAll(QRiskDocs.riskDocs.risk.riskId.eq(risk.getRiskId()));
            if (riskDocs != null) {
                for (RiskDocs riskDoc : riskDocs) {
                    riskDoc.setApprover(userUtils.getCurrentUser().getUsername());
                    riskDoc.setApprovalDate(new Date());
                    riskDocsRepo.save(riskDoc);
                }
            }

        }
        if (makerChecker != null) {
            makerCheckerService.approveTask(makerChecker.getId());
        }


    }


    private VehicleDetails findSingleVehicleDetails(Long ipuCode) {
        List<Object[]> motorDetails = motorVehicleDetailsRepo.getVehicleDetails(ipuCode);
        for (Object[] motorDetail : motorDetails) {
            VehicleDetails vehicleDetails = new VehicleDetails();
            vehicleDetails.setBodyColor((String) motorDetail[0]);
            vehicleDetails.setBodyType((String) motorDetail[1]);
            vehicleDetails.setCarModel((String) motorDetail[3]);
            vehicleDetails.setCarryCapacity(((BigDecimal) motorDetail[4]));
            vehicleDetails.setChassisNo((String) motorDetail[5]);
            vehicleDetails.setEngineCapacity(((BigDecimal) motorDetail[6]));
            vehicleDetails.setEngineNumber((String) motorDetail[7]);
            vehicleDetails.setLogbookNumber((String) motorDetail[8]);
            vehicleDetails.setYearOfManufacture((motorDetail[9] != null) ? motorDetail[9].toString() : null);
            return vehicleDetails;
        }
        return null;
    }

    private void generateCert(final RiskTrans riskTrans, final String policyNumber) throws BadRequestException {
        final VehicleDetails details = findSingleVehicleDetails(riskTrans.getRiskId());
        if (details == null) {
            throw new BadRequestException("Unable to get Vehicle details for this Risk with Registration number " + riskTrans.getRiskShtDesc());
        }
        final TypeCertificateIssueDTO typeCIssueDTO = new TypeCertificateIssueDTO();
        typeCIssueDTO.setBodytype((details.getBodyType() != null) ? details.getBodyType() : "saloon");
        typeCIssueDTO.setChassisnumber(details.getChassisNo());
        if (riskTrans.getWefDate().compareTo(dateUtils.removeTime(new Date())) < 0) {
            typeCIssueDTO.setCommencingdate(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        } else {
            typeCIssueDTO.setCommencingdate(new SimpleDateFormat("dd/MM/yyyy").format(riskTrans.getWefDate()));
        }

        typeCIssueDTO.setExpiringdate(new SimpleDateFormat("dd/MM/yyyy").format(riskTrans.getWetDate()));
        typeCIssueDTO.setEnginenumber(details.getEngineNumber());
        typeCIssueDTO.setEmail(riskTrans.getInsured().getEmailAddress());
        typeCIssueDTO.setInsuredPIN(riskTrans.getInsured().getPinNo());
        typeCIssueDTO.setPolicyholder(riskTrans.getInsured().getFname() + " " + riskTrans.getInsured().getOtherNames());
        typeCIssueDTO.setPolicynumber(policyNumber);
        final String phone = riskTrans.getInsured().getPhoneNo();
        final String clientPhone = (phone != null && phone.length() > 9) ? org.apache.commons.lang.StringUtils.substring(phone, phone.length() - 9) : phone;
        typeCIssueDTO.setPhonenumber(clientPhone);
        typeCIssueDTO.setRegistrationnumber(riskTrans.getRiskShtDesc());
        typeCIssueDTO.setSumInsured(riskTrans.getSumInsured());
        typeCIssueDTO.setYearofmanufacture(Integer.parseInt(details.getYearOfManufacture()));
        typeCIssueDTO.setTypeOfCertificate(3);
        typeCIssueDTO.setVehiclemake(details.getCarMake());
        typeCIssueDTO.setVehiclemodel(details.getCarModel());
        if (riskTrans.getCovertype().getCovShtDesc().equalsIgnoreCase("COMP")) {
            typeCIssueDTO.setTypeofcover(Integer.valueOf(100));
        } else if (riskTrans.getCovertype().getCovShtDesc().equalsIgnoreCase("TPO")) {
            typeCIssueDTO.setTypeofcover(Integer.valueOf(200));
        }
        // try {
        this.authenticationService.issueTypeCerts(typeCIssueDTO, riskTrans.getRiskId());
        // }
//            catch (BadRequestException ex){
//                throw new BadRequestException(ex.getMessage());
//            }
    }

    @PreAuthorize("hasAnyAuthority('AUTHORIZE_POLICY')")
    @Modifying
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void saveAuthorizationComment(Long polCode, String authComments) throws BadRequestException {
        PolicyTrans policy = policyRepo.findOne(polCode);
        if (policy == null) throw new BadRequestException("Invalid Policy to authorize");

        // Save comment regardless of authorization
        if (authComments != null && !authComments.trim().isEmpty()) {
            // Just save comment without authorization
            policy.setAuthComments(authComments.trim());
            policyRepo.save(policy);
        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateRefundAmount(PolicyCancellationDTO dto) throws BadRequestException {
        final PolicyTrans policyTrans = policyRepo.findOne(dto.getPolicyId());
        String polBusinessType = policyTrans.getProduct().getProGroup().getPrgType();
        if(polBusinessType.equalsIgnoreCase("L")){
            policyTrans.setNegotiatedPremium(BigDecimal.valueOf(dto.getRefundAmount().doubleValue()*-1));
            policyTrans.setBasicPrem(BigDecimal.valueOf(dto.getRefundAmount().doubleValue()*-1));
            policyTrans.setPremium(BigDecimal.valueOf(dto.getRefundAmount().doubleValue()*-1));
            policyRepo.save(policyTrans);
            Iterable<PolicyInstallments> installments = policyInstallmentsRepo
                    .findAll(QPolicyInstallments.policyInstallments.policyTrans.policyId.eq(dto.getPolicyId()));
            if(installments.spliterator().getExactSizeIfKnown()!=1){
                throw new BadRequestException("Unable to submit. Check your installments");
            }
            PolicyInstallments installment = installments.iterator().next();
            installment.setInstallPaid("N");
            installment.setInstallPrem(BigDecimal.valueOf(dto.getRefundAmount().abs().doubleValue()*-1));
            policyInstallmentsRepo.save(installment);
        }
        else {
            List<Object[]> riskTrans = riskRepo.findPolicyRiskTrans(dto.getPolicyId());
            int count = 0;
            for (Object[] riskTran : riskTrans) {
                final Long riskId = ((BigInteger) riskTran[1]).longValue();
                if (count == 0) {
                    BigDecimal refundAmount = dto.getRefundAmount() != null ? dto.getRefundAmount().abs().multiply(BigDecimal.valueOf(-1)) : BigDecimal.ZERO;
                    BigDecimal futurePremWithoutTaxes = BigDecimal.valueOf((refundAmount.doubleValue() * 100 / 100.45));
                    futurePremWithoutTaxes = futurePremWithoutTaxes.setScale(2, RoundingMode.HALF_EVEN);
//System.out.println("Refund Amount..."+new BigDecimal(dto.getRefundAmount().abs().doubleValue()*-1d)+" Risk Id "+riskId);
                    riskRepo.updateRiskOverridePrem(futurePremWithoutTaxes, riskId);
                    //riskRepo.updateRiskOverridePrem(new BigDecimal(dto.getRefundAmount().abs().doubleValue()*-1d),riskId);
                } else {
                    riskRepo.updateRiskOverridePrem(BigDecimal.ZERO, riskId);
                }
                count++;
            }
            premComputeService.computeCancelPrem(dto.getPolicyId());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void submitPolicyCancellation(PolicyCancellationDTO dto, boolean isApproved) throws BadRequestException {
        try {
            Long policyId = dto.getPolicyId();
            log.info("submitPolicyCancellation called with policyId={}, isApproved={}", dto.getPolicyId(), isApproved);
            if (policyId == null) {
                throw new BadRequestException("Policy ID is required for cancellation.");
            }
            if (dto.getRemarks() == null || dto.getRemarks().trim().isEmpty()) {
                throw new BadRequestException("Remarks are required for policy cancellation.");
            }
            PolicyTrans policy = policyRepo.findOne(policyId);
            if (policy == null) {
                log.warn("Policy not found for ID: {}", policyId);
                throw new BadRequestException("Policy not found with ID: " + policyId);
            }
            log.info("Policy found: {}", policy.getPolNo());
            final Long hashCode = Long.parseLong(String.valueOf(policy.hashCode()));
            log.debug("Generated hashCode for policy: {}", hashCode);
            if (!isApproved) {
                log.info("Cancellation is not approved, checking for existing MakerChecker task...");
                if (!makerCheckerRepo.exists(hashCode)) {
                    log.info("No existing MakerChecker task, creating new task for cancellation.");
                    Hibernate.initialize(policy);
                    PolicyCancellationDTO cancellationDTO = new PolicyCancellationDTO();
                    cancellationDTO.setPolicyId(policyId);
                    cancellationDTO.setRemarks(dto.getRemarks());
                    cancellationDTO.setCancelReasonId(dto.getCancelReasonId());
                    cancellationDTO.setRefundAmount(dto.getRefundAmount());
                    System.out.println(new Gson().toJson(cancellationDTO));
                    updateRefundAmount(cancellationDTO);
//                User currentUser = userUtils.getCurrentUser();
                    MakerCheckDTO makerCheckDTO = new MakerCheckDTO();
                    Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
                    makerCheckDTO.setJson(gson.toJson(cancellationDTO));
                    makerCheckDTO.setPolicyId(policyId);
                    makerCheckDTO.setStatus("N");
//              makerCheckDTO.setTaskName(String.format("Cancel Policy No %s ", policy.getPolNo()));
                    makerCheckDTO.setTaskName(String.format(" %s ", policy.getPolNo()));
                    makerCheckDTO.setTaskType("CN");
                    makerCheckDTO.setTaskCode(hashCode);
                    makerCheckDTO.setPolicyId(policyId);
                    // Set the maker name to the current user's username
//                makerCheckDTO.setmakerId(currentUser.getId());

                    List<UserDTO> eligibleCheckers = makerCheckerService.findEligibleCheckers("AUTHORIZE_POLICY", null);
                    List<Long> checkerIds = eligibleCheckers.stream()
                            .map(UserDTO::getId)
                            .collect(Collectors.toList());

                    if (checkerIds.isEmpty()) {

                        throw new BadRequestException("No eligible checkers found for policy authorization.");
                    }

                    makerCheckDTO.setAssignedCheckers(checkerIds.toString());
                    makerCheckerService.checkExists(makerCheckDTO);
                    makerCheckerService.createMakerChecker(makerCheckDTO);
                } else {
                    log.info("MakerChecker task already exists for hashCode: {}", hashCode);
                }
            } else {
                log.info("Cancellation is approved, attempting to update MakerChecker status to 'A'");
                User currentUser = userUtils.getCurrentUser();
                Date now = new Date();
                authorizeLifePolicy(policyId);

                policyRepo.save(policy);

            }
        }
        catch (BadRequestException exception){
            log.error("Application-level exception occurred", exception);
            throw exception;
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void submitBulkPolicyCancellation(PolicyCancellationDTO dto, boolean isApproved) throws BadRequestException {
        try {
            Long policyId = dto.getPolicyId();
            log.info("submitPolicyCancellation called with policyId={}, isApproved={}", dto.getPolicyId(), isApproved);
            if (policyId == null) {
                throw new BadRequestException("Policy ID is required for cancellation.");
            }
            if (dto.getRemarks() == null || dto.getRemarks().trim().isEmpty()) {
                throw new BadRequestException("Remarks are required for policy cancellation.");
            }
            PolicyTrans policy = policyRepo.findOne(policyId);
            if (policy == null) {
                log.warn("Policy not found for ID: {}", policyId);
                throw new BadRequestException("Policy not found with ID: " + policyId);
            }
            log.info("Policy found: {}", policy.getPolNo());
            final Long hashCode = Long.parseLong(String.valueOf(policy.hashCode()));
            log.debug("Generated hashCode for policy: {}", hashCode);
            if (!isApproved) {
                log.info("Cancellation is not approved, checking for existing MakerChecker task...");
                if (!makerCheckerRepo.exists(hashCode)) {
                    log.info("No existing MakerChecker task, creating new task for cancellation.");
                    Hibernate.initialize(policy);
                    PolicyCancellationDTO cancellationDTO = new PolicyCancellationDTO();
                    cancellationDTO.setPolicyId(policyId);
                    cancellationDTO.setRemarks(dto.getRemarks());
                    cancellationDTO.setCancelReasonId(dto.getCancelReasonId());
                    cancellationDTO.setRefundAmount(dto.getRefundAmount());
                    System.out.println(new Gson().toJson(cancellationDTO));
                    updateRefundAmount(cancellationDTO);
//                User currentUser = userUtils.getCurrentUser();
                    MakerCheckDTO makerCheckDTO = new MakerCheckDTO();
                    Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
                    makerCheckDTO.setJson(gson.toJson(cancellationDTO));
                    makerCheckDTO.setPolicyId(policyId);
                    makerCheckDTO.setStatus("N");
//              makerCheckDTO.setTaskName(String.format("Cancel Policy No %s ", policy.getPolNo()));
                    makerCheckDTO.setTaskName(String.format(" %s ", policy.getPolNo()));
                    makerCheckDTO.setTaskType("CN");
                    makerCheckDTO.setTaskCode(hashCode);
                    makerCheckDTO.setPolicyId(policyId);
                    // Set the maker name to the current user's username
//                makerCheckDTO.setmakerId(currentUser.getId());

                    List<UserDTO> eligibleCheckers = makerCheckerService.findEligibleCheckers("AUTHORIZE_POLICY", null);
                    List<Long> checkerIds = eligibleCheckers.stream()
                            .map(UserDTO::getId)
                            .collect(Collectors.toList());

                    if (checkerIds.isEmpty()) {

                        throw new BadRequestException("No eligible checkers found for policy authorization.");
                    }

                    makerCheckDTO.setAssignedCheckers(checkerIds.toString());
                    makerCheckerService.checkExists(makerCheckDTO);
                    //makerCheckerService.createMakerChecker(makerCheckDTO);
                    makerCheckerService.createBulkMakerChecker(makerCheckDTO);
                } else {
                    log.info("MakerChecker task already exists for hashCode: {}", hashCode);
                }
            } else {
                log.info("Cancellation is approved, attempting to update MakerChecker status to 'A'");
                User currentUser = userUtils.getCurrentUser();
                Date now = new Date();
                authorizeLifePolicy(policyId);

                policyRepo.save(policy);

            }
        }
        catch (BadRequestException exception){
            log.error("Application-level exception occurred", exception);
            throw exception;
        }
    }


    @PreAuthorize("hasAnyAuthority('AUTHORIZE_POLICY')")
    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void authorizeLifePolicy(Long polCode) throws BadRequestException {

        PolicyTrans policy = policyRepo.findOne(polCode);
        if (policy == null) throw new BadRequestException("Invalid Policy to authorize");


//        if(!"R".equalsIgnoreCase(policy.getAuthStatus())&& !"CN".equalsIgnoreCase(policy.getTransType())){
//            throw new BadRequestException("Can only authorize ready policy");
//        }
//        if(!"CV".equalsIgnoreCase(policy.getAuthStatus())&& "NB".equalsIgnoreCase(policy.getTransType())){
//            throw new BadRequestException("Can only authorize converted policy");
//        }
        Iterable<LifeReceipts> fcrReceipts = lifeReceiptsRepo.findAll(QLifeReceipts.lifeReceipts.policyTrans.policyId.eq(polCode).and(QLifeReceipts.lifeReceipts.receiptTrans.fromFCR.equalsIgnoreCase("Y")));
        if (fcrReceipts == null) {
            if (!authLimits.checkAuthorizationLimits("AUTHORIZE_POLICY", policy.getBasicPrem())) {
                throw new BadRequestException("You have no rights to authorize the transaction...Check your authorization limits..");
            }
        }
        long riskcount = riskRepo.count(QRiskTrans.riskTrans.policy.policyId.eq(polCode));

        if (riskcount == 0) throw new BadRequestException("Cannot Authorize Transaction Without Risk Details");
        List<Object[]> risks1 = riskRepo.findPolicyRiskTrans(polCode);

        for (Object[] riskBean : risks1) {
            final Long riskId = ((BigInteger) riskBean[1]).longValue();
            Iterable<RiskDocs> riskDocs = riskDocsRepo.findAll(QRiskDocs.riskDocs.risk.riskId.eq(riskId));
            if (policy.getTransType() != null && !"CN".equalsIgnoreCase(policy.getTransType())) {
                for (RiskDocs riskDoc : riskDocs) {
                    if (riskDoc.getVerifiedBy() == null || riskDoc.getVerifiedDate() == null) {
                        throw new BadRequestException(String.format("Cannot authorize policy without verifying documents for Risk %s", riskId));
                    }
                }
            }
        }
        List<Object[]> lifeReceipts = lifeReceiptsRepo.findLifeReceipts(polCode);

        BigDecimal totRcptAmount = BigDecimal.ZERO;
        SystemTrans transaction = transRepo.findOne(QSystemTrans.systemTrans.policy.policyId.eq(polCode).and(QSystemTrans.systemTrans.transAuthorised.eq("N")));
        if (transaction == null) {
            transaction = new SystemTrans();
            transaction.setPolicy(policy);
            transaction.setDoneDate(new Date());
            transaction.setDoneBy(userUtils.getCurrentUser() != null ? userUtils.getCurrentUser() : policy.getCreatedUser());
            transaction.setTransLevel("U");
            transaction.setTransCode("LBD");
        }
        transaction.setAuthBy(userUtils.getCurrentUser() != null ? userUtils.getCurrentUser() : policy.getCreatedUser());
        transaction.setAuthDate(new Date());
        transaction.setTransAuthorised("Y");
        transRepo.save(transaction);

        SystemTransactions systemTransaction = null;

        if(!policy.getTransType().equalsIgnoreCase("CO")) {
            for (Object[] rct : lifeReceipts) {
                if ("D".equalsIgnoreCase((String) rct[1]))
                    totRcptAmount = totRcptAmount.subtract((BigDecimal) rct[0]);
                else totRcptAmount = totRcptAmount.add((BigDecimal) rct[0]);
                Long rctId = ((BigInteger) rct[2]).longValue();
                Long transNo = ((BigInteger) rct[3]).longValue();
                AllCommissionsDTO commissionsDTO = lifeService.allocateLifeRcptBalance(polCode);
                if (commissionsDTO == null) {
                    commissionsDTO = lifeService.getCommissionEarned(polCode, rctId);
                }
                if (commissionsDTO != null) {
                if(commissionsDTO.getTransactionId()==null){
                    throw new BadRequestException("Cannot Authorise Transactions...Error generating transaction to allocate");
                }
                    systemTransaction = sysTransRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(commissionsDTO.getTransactionId()));
                if(systemTransaction==null){
                    throw new BadRequestException("Cannot Authorise Transactions.....Error generating transaction to allocate");
                }
                    SystemTransactions receiptTrans = sysTransRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(transNo));
                    if(receiptTrans==null){
                        throw new BadRequestException("Cannot Authorise Transactions.....Error getting Receipt transaction to allocate");
                    }
                    allocationService.saveSubMarketer(policy.getPolicyId(),commissionsDTO.getMarketerComm(),commissionsDTO.getSubAgentComm(),transaction,systemTransaction,receiptTrans);

                    postCommissions(policy, transaction, commissionsDTO.getCommission(), commissionsDTO.getSubAgentComm(), commissionsDTO.getMarketerComm(), commissionsDTO.getAdminFeeTotal(),systemTransaction);
                    final CommissionPayments commissionPayments = new CommissionPayments();
                    commissionPayments.setProcessed("N");
                    commissionPayments.setDate(new Date());
                    commissionPayments.setCurrencies(policy.getTransCurrency());
                    commissionPayments.setWhtx(BigDecimal.valueOf(commissionsDTO.getWhtx().abs().doubleValue()*-1));
                    commissionPayments.setAmount(commissionsDTO.getCommission().abs());
                    commissionPayments.setNetAmount((commissionsDTO.getCommission().abs().subtract(commissionsDTO.getWhtx().abs())));
                    commissionPayments.setDebitTransaction(systemTransaction);
                    commissionPayments.setCreditTransaction(receiptTrans);
                    commissionPayments.setTransType("Commission");
                    commissionPayments.setProcessedBy(userUtils.getCurrentUser());
                    commissionPayments.setAuthorised("N");
                    commissionPaymentsRepo.save(commissionPayments);

                    if(commissionsDTO.getAdminFeeTotal()!=null && commissionsDTO.getAdminFeeWhtx().compareTo(BigDecimal.ZERO)!=0){
                        final BigDecimal adminWhtx = (commissionsDTO.getAdminFeeWhtx()!=null)?commissionsDTO.getAdminFeeWhtx():BigDecimal.ZERO;
                        final CommissionPayments subcommissionPayments = new CommissionPayments();
                        subcommissionPayments.setProcessed("N");
                        subcommissionPayments.setDate(new Date());
                        subcommissionPayments.setCurrencies(policy.getTransCurrency());
                        subcommissionPayments.setWhtx(BigDecimal.valueOf(adminWhtx.abs().doubleValue()*-1));
                        subcommissionPayments.setAmount(commissionsDTO.getAdminFeeTotal().abs());
                        subcommissionPayments.setNetAmount((commissionsDTO.getAdminFeeTotal().abs().subtract(adminWhtx.abs())));
                        subcommissionPayments.setDebitTransaction(systemTransaction);
                        subcommissionPayments.setCreditTransaction(receiptTrans);
                        subcommissionPayments.setTransType("Admin Fee");
                        subcommissionPayments.setProcessedBy(userUtils.getCurrentUser());
                        subcommissionPayments.setAuthorised("N");
                        commissionPaymentsRepo.save(subcommissionPayments);
                    }

                }

            }
        }
        if (!"EN".equalsIgnoreCase(policy.getTransType()) && policy.getNetPrem() == null) {
            throw new BadRequestException("Policy Premium cannot be null");
        }
        Iterable<TransChecks> transChecks = transChecksRepo.findAll(QTransChecks.transChecks.policyTrans.policyId.eq(policy.getPolicyId()));
        boolean payLaterApproved = false;
        boolean receiptExists = makerCheckerRepo.exists(QMakerChecker.makerChecker.policyId.eq(polCode).and(QMakerChecker.makerChecker.taskType.equalsIgnoreCase("RC")).and(QMakerChecker.makerChecker.status.equalsIgnoreCase("N")));
        if (transChecks != null) {
            for (TransChecks tc : transChecks) {
                if (tc.getAuthorised() != null) {
                    payLaterApproved = tc.getChecks().getCheckName().equalsIgnoreCase("Pay Later") && tc.getAuthorised().equalsIgnoreCase("Y");
                    break;
                }
            }
        }
        if (!"CN".equalsIgnoreCase(policy.getTransType())) { //A CN IS NOT RECEIPTED
            if (!"EN".equalsIgnoreCase(policy.getTransType()) && totRcptAmount.compareTo(policy.getNetPrem()) < 0 && receiptExists) {
                throw new BadRequestException("A receipt for this policy is pending Authorization");
            }
            System.out.println("total receipt amount: " + totRcptAmount);
            System.out.println("Policy net premium: " + policy.getNetPrem());
            if (!"EN".equalsIgnoreCase(policy.getTransType()) && totRcptAmount.compareTo(policy.getNetPrem()) < 0 && !payLaterApproved && !receiptExists) {
                throw new BadRequestException("Receipt amount must be atleast equal to one instalment premium..");
            }
        }
        Iterable<RiskTrans> risks = riskRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(polCode));

        long checkCount = transChecksRepo.count(QTransChecks.transChecks.policyTrans.policyId.eq(polCode).and(QTransChecks.transChecks.authorised.eq("N")));

        if (checkCount > 0) {
            throw new BadRequestException("Cannot Authorize when there is unauthorized checks....");
        }


//        BigDecimal prems = (policy.getPremium() == null) ? BigDecimal.ZERO : policy.getPremium();
//        Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("D");
//        if (sequenceRepo.count(seqPredicate) == 0)
//            throw new BadRequestException("Sequence for Debit Notes has not been defined");
//        SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
//        Long seqNumber = sequence.getNextNumber();
//        String transType = policy.getTransType();
//        if ("CO".equalsIgnoreCase(policy.getTransType())) {
//            transType = policy.getPreviousTrans().getTransType();
//        }
//        if ("NB".equalsIgnoreCase(transType)) {
//            transType = "NB";
//        } else if ("RN".equalsIgnoreCase(transType)) {
//            transType = "RN";
//        } else {
//            transType = "EN";
//        }
//        if (mappingRepo.count(QTransactionMapping.transactionMapping.transType.eq(transType)) == 0)
//            throw new BadRequestException("Error getting Transaction Mapping Setups..Contact System Administrator");
//        TransactionMapping mapping = mappingRepo.findOne(QTransactionMapping.transactionMapping.transType.eq(transType));
//        String refNo = ((prems.compareTo(BigDecimal.ZERO) >= 0) ? mapping.getDebitCode() : mapping.getCreditCode()) + String.format("%05d", seqNumber);
//        String debitCode = ((prems.compareTo(BigDecimal.ZERO) >= 0) ? mapping.getDebitCode() : mapping.getCreditCode());
//        sequence.setLastNumber(seqNumber);
//        sequence.setNextNumber(seqNumber + 1);
//        sequenceRepo.save(sequence);
//        policy.setRefNo(refNo);

        policy.setAuthBy(userUtils.getCurrentUser());
        policy.setAuthStatus("A");
        policy.setAuthDate(new Date());

        Long makerId = policy.getCreatedUser().getId();
        Long checkerId = userUtils.getCurrentUser().getId();

        if ("CN".equalsIgnoreCase(policy.getTransType())) {
            if (makerId.equals(checkerId)) {
                log.warn("Maker and checker are the same user: {}", makerId);
                //throw new BadRequestException("You can't approve a task you've initiated");
            }
            policy.setCurrentStatus("CN");
            log.info("Fetching MakerChecker for policyId={}, taskType='CN', status='N'", polCode);
            Iterable<MakerChecker> makerCheckers = makerCheckerRepo.findAll(
                    QMakerChecker.makerChecker.policyId.eq(polCode)
                            .and(QMakerChecker.makerChecker.taskType.equalsIgnoreCase("CN"))
                            .and(QMakerChecker.makerChecker.status.equalsIgnoreCase("N"))
            );
            for (MakerChecker makerChecker:makerCheckers) {
                log.info("MakerChecker found: id={}, current status={}", makerChecker.getId(), makerChecker.getStatus());
                makerChecker.setStatus("A");
                makerChecker.setCheckerId(userUtils.getCurrentUser());
                makerChecker.setCheckDate(new Date());
                makerCheckerRepo.save(makerChecker);
                policy.setCurrentStatus("CN");
                log.info("MakerChecker status updated to 'A' and saved for id={}", makerChecker.getId());
                log.info("MakerChecker status updated to 'A' and saved.");
            }
        }
        if ("CO".equalsIgnoreCase(policy.getTransType())) {
            Iterable<PolicyRemarks> policyRemarks = policyRemarksRepo.findAll(QPolicyRemarks.policyRemarks.policy.policyId.eq(policy.getPolicyId()));
            if (policyRemarks.spliterator().getExactSizeIfKnown() == 0) {
                throw new BadRequestException("Input Remarks first....");
            }
            policy.setCurrentStatus("CO");
        } else
            policy.setCurrentStatus("A");

        if (policy.getBinder() != null && policy.getBinder().getCalculatorType() != null &&
                policy.getBinder().getCalculatorType().equalsIgnoreCase("I")
                && policy.getTransType().equalsIgnoreCase("NB")
        ) {

            String clientPolNO;
            String binName = policy.getBinder().getBinName().toUpperCase();
            System.out.println("Bin name"+ binName);
            if (binName.contains("ALAK FAMILY PROTECTION PLAN")) {
                clientPolNO = createFPPPolicyAlak(policy, risks);
            } else if (binName.contains("ALAK ULTIMATE PROTECTOR")) {
                clientPolNO = createUPPolicyAlak(policy, risks);
            } else if (binName.contains("ALAK PERSONAL ACCIDENT")) {
                clientPolNO = createPAPolicyAlak(policy, risks);
            } else if (binName.contains("ALAK EDUCATION")) {
                clientPolNO = createEducationAlak(policy,risks);
            }else{
                //endowment
                clientPolNO = createEndowmentAlak(policy, risks);

            }
            //uploadAlakDocuments(policy, clientPolNO);

            policy.setClientPolNo(clientPolNO);

            IntegrationUw integrationUw = new IntegrationUw();
            integrationUw.setClientPolNo(clientPolNO);
            integrationUw.setPolId(policy.getPolicyId());
            integrationUwRepo.save(integrationUw);
        }

        String refNo = null;
        String debitCode = null;

            Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("D");
            if (sequenceRepo.count(seqPredicate) == 0)
                throw new BadRequestException("Sequence for Debit Notes has not been defined");
            SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
            Long seqNumber = sequence.getNextNumber();
            String transType = policy.getTransType();
            if ("CO".equalsIgnoreCase(policy.getTransType())) {
                transType = policy.getPreviousTrans().getTransType();
            }
            if ("NB".equalsIgnoreCase(transType)) {
                transType = "NB";
            } else if ("RN".equalsIgnoreCase(transType)) {
                transType = "RN";
            } else if ("BU".equalsIgnoreCase(transType)) {
                transType = "BU";
            } else if ("CN".equalsIgnoreCase(transType)) {
                transType = "CN";
            } else {
                transType = "EN";

            }
            if (mappingRepo.count(QTransactionMapping.transactionMapping.transType.eq(transType)) == 0)
                throw new BadRequestException("Error getting Transaction Mapping Setups..Contact System Administrator");
            TransactionMapping mapping = mappingRepo.findOne(QTransactionMapping.transactionMapping.transType.eq(transType));
            System.out.println(policy.getBasicPrem()+"debit code"+
                     mapping.getDebitCode() +"credit code"+ mapping.getCreditCode()+" Type "+transType);
             refNo = ((policy.getBasicPrem()!=null && policy.getBasicPrem().compareTo(BigDecimal.ZERO) >= 0) ? mapping.getDebitCode() : mapping.getCreditCode()) + String.format("%05d", seqNumber);
            sequence.setLastNumber(seqNumber);
            sequence.setNextNumber(seqNumber + 1);
            sequenceRepo.save(sequence);
            policy.setRefNo(refNo);
        policyRepo.save(policy);

        if (!("NB".equalsIgnoreCase(policy.getTransType()) || "SP".equalsIgnoreCase(policy.getTransType()))) {
            PolicyTrans prevPolicy = policy.getPreviousTrans();
            if ("CO".equalsIgnoreCase(policy.getTransType()) && prevPolicy.getPreviousTrans().getPolicyId() != prevPolicy.getPolicyId()) {
                prevPolicy.setCurrentStatus(policy.getTransType());
                PolicyTrans prevPolicy1 = prevPolicy.getPreviousTrans();
                if (!prevPolicy1.getTransType().equalsIgnoreCase("CO")) {
                    prevPolicy1.setCurrentStatus("A");
                    policyRepo.save(prevPolicy1);
                }
            } else {
                prevPolicy.setCurrentStatus(policy.getTransType());
            }
            policyRepo.save(prevPolicy);
        }

        if ("RE".equalsIgnoreCase(policy.getPolRevStatus())) {
            PolicyTrans prevPolicy = policy.getPreviousTrans();
            prevPolicy.setCurrentStatus(policy.getTransType());
            policyRepo.save(prevPolicy);
        }


        accountsUtilities.validatePolicyAccounts(policy);
        AllCommissionsDTO commissionsDTO =null;


        if ("CO".equalsIgnoreCase(policy.getTransType())) {
            commissionsDTO = lifeService.allocateContraLifeRcptBalance(policy.getPolicyId());
           if(commissionsDTO.getTransactionId()==null){
               throw new BadRequestException("Cannot Authorise Transactions...Error generating transaction to allocate");
           }
           final SystemTransactions systemTransactions = sysTransRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(commissionsDTO.getTransactionId()));
           if(systemTransactions==null){
               throw new BadRequestException("Cannot Authorise Transactions.....Error generating transaction to allocate");
           }
           postCommissions(policy,transaction,commissionsDTO.getCommission(),commissionsDTO.getSubAgentComm(),commissionsDTO.getMarketerComm(),commissionsDTO.getAdminFeeTotal(),systemTransactions);

           final SystemTransactions originalTransaction = sysTransRepo.findOne(commissionsDTO.getPrevTransactionId());
           if(originalTransaction==null){
               throw new BadRequestException("Unable to get Original Transaction...");
           }
//            if(!commissionPaymentsRepo.findUnauthCommissions(policy.getPolNo()).isEmpty()){
//                List<BigInteger> trans  = commissionPaymentsRepo.findUnauthCommissions(policy.getPolNo());
//                List<CommissionPayments> commissionPaymentsList = new ArrayList<>();
//                for(BigInteger tran : trans){
//                    CommissionPayments commissionPayments = commissionPaymentsRepo.findOne(tran.longValue());
//                    commissionPayments.setWithDrawn("Y");
//                    commissionPayments.setWithDrawnDate(new Date());
//                    commissionPayments.setWithDrawnBy(userUtils.getCurrentUser());
//                    commissionPaymentsList.add(commissionPayments);
//                }
//                commissionPaymentsRepo.save(commissionPaymentsList);
//            }
//            else
                if(!commissionPaymentsRepo.findauthCommissions(policy.getPolNo()).isEmpty())  {
                allocationService.saveSubMarketerWithoutGl(policy.getPolicyId(),commissionsDTO.getMarketerComm(),commissionsDTO.getSubAgentComm(),transaction,originalTransaction,systemTransactions);
                final CommissionPayments commissionPayments = new CommissionPayments();
                commissionPayments.setProcessed("N");
                commissionPayments.setDate(new Date());
                commissionPayments.setCurrencies(policy.getTransCurrency());
                commissionPayments.setWhtx(BigDecimal.valueOf(commissionsDTO.getWhtx().abs().doubleValue() * 1));
                commissionPayments.setAmount(BigDecimal.valueOf(commissionsDTO.getCommission().abs().doubleValue() * -1));
                commissionPayments.setNetAmount(BigDecimal.valueOf((commissionsDTO.getCommission().abs().subtract(commissionsDTO.getWhtx().abs())).doubleValue() * -1));
                commissionPayments.setDebitTransaction(originalTransaction);
                commissionPayments.setCreditTransaction(systemTransactions);
                commissionPayments.setTransType("Commission");
                commissionPayments.setProcessedBy(userUtils.getCurrentUser());
                commissionPayments.setAuthorised("N");
                commissionPaymentsRepo.save(commissionPayments);


                if (commissionsDTO.getAdminFeeTotal() != null  && commissionsDTO.getAdminFeeTotal().compareTo(BigDecimal.ZERO)!=0) {
                    final BigDecimal adminWhtx = (commissionsDTO.getAdminFeeWhtx() != null) ? commissionsDTO.getAdminFeeWhtx() : BigDecimal.ZERO;
                    final CommissionPayments adminCommissionPayments = new CommissionPayments();
                    adminCommissionPayments.setProcessed("N");
                    adminCommissionPayments.setDate(new Date());
                    adminCommissionPayments.setCurrencies(policy.getTransCurrency());
                    adminCommissionPayments.setWhtx(BigDecimal.valueOf(adminWhtx.abs().doubleValue() * 1));
                    adminCommissionPayments.setAmount(BigDecimal.valueOf(commissionsDTO.getAdminFeeTotal().abs().doubleValue() * -1));
                    adminCommissionPayments.setNetAmount(BigDecimal.valueOf((commissionsDTO.getAdminFeeTotal().abs().subtract(adminWhtx.abs())).doubleValue() * -1));
                    adminCommissionPayments.setDebitTransaction(originalTransaction);
                    adminCommissionPayments.setCreditTransaction(systemTransactions);
                    adminCommissionPayments.setTransType("Admin Fee");
                    adminCommissionPayments.setProcessedBy(userUtils.getCurrentUser());
                    adminCommissionPayments.setAuthorised("N");
                    commissionPaymentsRepo.save(adminCommissionPayments);
                }

                if(commissionsDTO.getSubAgentComm()!=null && commissionsDTO.getSubAgentComm().compareTo(BigDecimal.ZERO)!=0){
                    final CommissionPayments commissionPaymentsSub = new CommissionPayments();
                    commissionPaymentsSub.setProcessed("N");
                    commissionPaymentsSub.setDate(new Date());
                    commissionPaymentsSub.setCurrencies(policy.getTransCurrency());
                    final BigDecimal whtmount =  BigDecimal.ZERO;
                    final BigDecimal comAmt = commissionsDTO.getSubAgentComm();
                    commissionPaymentsSub.setWhtx(BigDecimal.valueOf(whtmount.abs().doubleValue()*1));
                    commissionPaymentsSub.setAmount(BigDecimal.valueOf(comAmt.abs().doubleValue() * -1));
                    commissionPaymentsSub.setNetAmount(BigDecimal.valueOf((comAmt.abs().subtract(whtmount.abs())).doubleValue() * -1));
                    commissionPaymentsSub.setDebitTransaction(originalTransaction);
                    commissionPaymentsSub.setCreditTransaction(systemTransactions);
                    commissionPaymentsSub.setTransType("Sub Agent Commission");
                    commissionPaymentsSub.setProcessedBy(userUtils.getCurrentUser());
                    commissionPaymentsSub.setAuthorised("N");
                    commissionPaymentsRepo.save(commissionPaymentsSub);
                }

                if(commissionsDTO.getMarketerComm()!=null && commissionsDTO.getMarketerComm().compareTo(BigDecimal.ZERO)!=0){
                    final CommissionPayments commissionPaymentsSub = new CommissionPayments();
                    commissionPaymentsSub.setProcessed("N");
                    commissionPaymentsSub.setDate(new Date());
                    commissionPaymentsSub.setCurrencies(policy.getTransCurrency());
                    final BigDecimal whtmount =  BigDecimal.ZERO;
                    final BigDecimal comAmt = commissionsDTO.getMarketerComm();
                    commissionPaymentsSub.setWhtx(BigDecimal.valueOf(whtmount.abs().doubleValue()*1));
                    commissionPaymentsSub.setAmount(BigDecimal.valueOf(comAmt.abs().doubleValue() * -1));
                    commissionPaymentsSub.setNetAmount(BigDecimal.valueOf((comAmt.abs().subtract(whtmount.abs())).doubleValue() * -1));
                    commissionPaymentsSub.setDebitTransaction(originalTransaction);
                    commissionPaymentsSub.setCreditTransaction(systemTransactions);
                    commissionPaymentsSub.setTransType("Sub Agent Commission");
                    commissionPaymentsSub.setProcessedBy(userUtils.getCurrentUser());
                    commissionPaymentsSub.setAuthorised("N");
                    commissionPaymentsRepo.save(commissionPaymentsSub);
                }
            }
        }
        else{
            lifeService.allocateLifeRcptBalance(policy.getPolicyId());
        }
//        if(true){
//            throw new BadRequestException("Transaction type..."+policy.getTransType());
//        }
        if ("CO".equalsIgnoreCase(policy.getTransType())) {
            if(commissionsDTO==null || commissionsDTO.getTransactionId()==null){
                throw new BadRequestException("Cannot Authorise Transactions...Error generating transaction to allocate");
            }
            final SystemTransactions systemTransactions = sysTransRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(commissionsDTO.getTransactionId()));
            postUwTransactions(policy, transaction, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, systemTransactions);

        }
        else if("CN".equalsIgnoreCase(policy.getTransType())){
            String transTypes = policy.getTransType();
             seqPredicate = QSystemSequence.systemSequence.transType.eq("C");
            if (sequenceRepo.count(seqPredicate) == 0)
                throw new BadRequestException("Sequence for Debit Notes has not been defined");
             sequence = sequenceRepo.findOne(seqPredicate);
             seqNumber = sequence.getNextNumber();
            if (transMappingRepo.count(QTransactionMapping.transactionMapping.transType.eq(transTypes)) == 0)
                throw new BadRequestException("Error getting Transaction Mapping Setups..Contact System Administrator");
             mapping = transMappingRepo.findOne(QTransactionMapping.transactionMapping.transType.eq(transTypes));
            refNo = mapping.getCreditCode() + String.format("%05d", seqNumber);
            sequence.setLastNumber(seqNumber);
            sequence.setNextNumber(seqNumber + 1);
            sequenceRepo.save(sequence);
           SystemTransactions  trans = allocationService.createInstalmentTransaction(refNo,policy.getPremium(),
                    transaction, "POLICY INSTALMENT", policy,true,1L);
            postUwTransactions(policy, transaction, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, trans);



            List<BigInteger> comms = commissionPaymentsRepo.findauthCommissions(policy.getPolNo());
            for(BigInteger comm: comms){
                final List<Object[]> commissionPayment  = commissionPaymentsRepo.getCommissionDebits(comm.longValue());

                if(commissionPayment.isEmpty()){
                    throw new BadRequestException("Unable To get Commission Transactions...");
                }



                final Long creditId = ((BigInteger)commissionPayment.get(0)[0]).longValue();
                final Long debitId = ((BigInteger)commissionPayment.get(0)[1]).longValue();
                final CommissionPayments commissionPayments = commissionPaymentsRepo.findOne(comm.longValue());
                if(commissionPayments.getTransType().equalsIgnoreCase("Commission")){
                    int paidInsts = policy.getPaidInsts();
                    Map<String, Integer> frequencyMap = new HashMap<>();
                    frequencyMap.put("D", 365);
                    frequencyMap.put("W", 52);
                    frequencyMap.put("M", 12);
                    frequencyMap.put("Q", 4);
                    frequencyMap.put("S", 2);
                    frequencyMap.put("A", 1);
                    int paymentsPerYear = frequencyMap.getOrDefault(policy.getFrequency(), 12);  // Default to Monthly if not found

                    double paidYears = Math.ceil(((double) paidInsts / paymentsPerYear));
                    Iterable<LifeCommissionRates>    commissionRates = lifeCommissionRatesRepo.findAll(QLifeCommissionRates.lifeCommissionRates.binderDef.eq(policy.getBinder())
                            .and(QLifeCommissionRates.lifeCommissionRates.commTermFrom.loe(policy.getPolTerm()))
                            .and(QLifeCommissionRates.lifeCommissionRates.commTermTo.goe(policy.getPolTerm()))
                            .and(QLifeCommissionRates.lifeCommissionRates.wefDate.loe(policy.getCoverFrom()))
                            .and(QLifeCommissionRates.lifeCommissionRates.wetDate.goe(policy.getCoverFrom()).or(QLifeCommissionRates.lifeCommissionRates.wetDate.isNull()))
                            .and(QLifeCommissionRates.lifeCommissionRates.commYearFrom.loe(paidYears))
                            .and(QLifeCommissionRates.lifeCommissionRates.frequency.equalsIgnoreCase(policy.getFrequency()))
                            .and(QLifeCommissionRates.lifeCommissionRates.commYearTo.goe(paidYears)));
                    if(commissionRates.spliterator().getExactSizeIfKnown()!=1){
                        throw new BadRequestException("commission rates are not setup..");
                    }
                    LifeCommissionRates lifeCommissionRates = commissionRates.iterator().next();


                    if (lifeCommissionRates != null) {
                        BigDecimal commRate = lifeCommissionRates.getCommRate();
                        BigDecimal commss = BigDecimal.ZERO;
                        commss = (commRate.multiply(policy.getPremium()).divide(lifeCommissionRates.getCommDivFactor())).setScale(2, BigDecimal.ROUND_HALF_EVEN);
                        AccountTypes accType = policy.getAgent() != null ? policy.getAgent().getAccountType() : null;
                        BigDecimal whtxAmt = BigDecimal.ZERO;
                        BigDecimal lifewhtxAmt = BigDecimal.ZERO;
                        if (accType != null && accType.isWhtxAppl()) {
                            if (accType.getWhtaxVal().compareTo(BigDecimal.ZERO) == 1) {
                                whtxAmt = accType.getWhtaxVal().divide(new BigDecimal(100)).multiply(commss);
                                lifewhtxAmt = whtxAmt.abs();
                            }
                        }

                        final CommissionPayments commissionPaymentss = new CommissionPayments();
                        commissionPaymentss.setProcessed("N");
                        commissionPaymentss.setDate(new Date());
                        commissionPaymentss.setCurrencies(policy.getTransCurrency());
                        commissionPaymentss.setWhtx(BigDecimal.valueOf(lifewhtxAmt.abs().doubleValue() * 1));
                        commissionPaymentss.setAmount(BigDecimal.valueOf(commss.abs().doubleValue() *-1));
                        commissionPaymentss.setNetAmount(BigDecimal.valueOf( (commss.abs().doubleValue() - lifewhtxAmt.abs().doubleValue())*-1));
                        commissionPaymentss.setDebitTransaction(sysTransRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(debitId)));
                        commissionPaymentss.setCreditTransaction(trans);
                        commissionPaymentss.setTransType("Commission");
                        commissionPaymentss.setProcessedBy(userUtils.getCurrentUser());
                        commissionPaymentss.setAuthorised("N");
                        commissionPaymentsRepo.save(commissionPaymentss);
                        if (policy.getSubAgent() != null) {
                          Iterable<LifeSubAgentCommissionRates>  subAgentCommissionRates = lifeSubAgentCommissionRatesRepo.findAll(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.binderDef.eq(policy.getBinder())
                                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.accountTypes.accountType.eq(AccountTypeEnum.SUB))
                                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermFrom.loe(policy.getPolTerm()))
                                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermTo.goe(policy.getPolTerm()))
                                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wefDate.loe(policy.getCoverFrom()))
                                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.goe(policy.getCoverFrom()).or(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.isNull()))
                                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.frequency.equalsIgnoreCase(policy.getFrequency())));
                          if(subAgentCommissionRates.spliterator().getExactSizeIfKnown()!=1){
                              throw new BadRequestException("Sub Agent commission rates are not setup..");
                          }
                            if (paidYears <= 1) {
                                LifeSubAgentCommissionRates lifeSubAgentCommissionRates = subAgentCommissionRates.iterator().next();
                                if (lifeSubAgentCommissionRates != null) {
                                    BigDecimal subAgentComm = (lifeSubAgentCommissionRates.getCommRate().multiply(commss).divide(lifeSubAgentCommissionRates.getCommDivFactor())).setScale(2, BigDecimal.ROUND_HALF_EVEN);
                                    final CommissionPayments commissions = new CommissionPayments();
                                    commissions.setProcessed("N");
                                    commissions.setDate(new Date());
                                    commissions.setCurrencies(policy.getTransCurrency());
                                    commissions.setWhtx(BigDecimal.ZERO);
                                    commissions.setAmount(BigDecimal.valueOf(subAgentComm.abs().doubleValue()* -1));
                                    commissions.setNetAmount(BigDecimal.valueOf(subAgentComm.abs().doubleValue()* -1));
                                    commissions.setDebitTransaction(sysTransRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(debitId)));
                                    commissions.setCreditTransaction(trans);
                                    commissions.setTransType("Sub Agent Commission");
                                    commissions.setProcessedBy(userUtils.getCurrentUser());
                                    commissions.setAuthorised("N");
                                    commissionPaymentsRepo.save(commissions);
                                }
                            }
                        }
                        if (policy.getMarketerAgent() != null) {
                            Iterable<LifeSubAgentCommissionRates>  subAgentCommissionRates = lifeSubAgentCommissionRatesRepo.findAll(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.binderDef.eq(policy.getBinder())
                                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.accountTypes.accountType.eq(AccountTypeEnum.MRK))
                                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermFrom.loe(policy.getPolTerm()))
                                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermTo.goe(policy.getPolTerm()))
                                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wefDate.loe(policy.getCoverFrom()))
                                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.goe(policy.getCoverFrom()).or(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.isNull()))
                                    .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.frequency.equalsIgnoreCase(policy.getFrequency())));

                            if(subAgentCommissionRates.spliterator().getExactSizeIfKnown()!=1){
                                throw new BadRequestException("Markerter commission rates are not setup..");
                            }

                            if (paidYears <= 1) {
                                LifeSubAgentCommissionRates lifeSubAgentCommissionRates = subAgentCommissionRates.iterator().next();
                                if (lifeSubAgentCommissionRates != null) {
                                    BigDecimal subAgentComm = (lifeSubAgentCommissionRates.getCommRate().multiply(commss).divide(lifeSubAgentCommissionRates.getCommDivFactor())).setScale(2, BigDecimal.ROUND_HALF_EVEN);
                                    final CommissionPayments commissions = new CommissionPayments();
                                    commissions.setProcessed("N");
                                    commissions.setDate(new Date());
                                    commissions.setCurrencies(policy.getTransCurrency());
                                    commissions.setWhtx(BigDecimal.ZERO);
                                    commissions.setAmount(BigDecimal.valueOf(subAgentComm.abs().doubleValue()* -1));
                                    commissions.setNetAmount(BigDecimal.valueOf(subAgentComm.abs().doubleValue()* -1));
                                    commissions.setDebitTransaction(sysTransRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(debitId)));
                                    commissions.setCreditTransaction(trans);
                                    commissions.setTransType("Sub Agent Commission");
                                    commissions.setProcessedBy(userUtils.getCurrentUser());
                                    commissions.setAuthorised("N");
                                    commissionPaymentsRepo.save(commissions);
                                }
                            }
                        }


                        long count = adminFeeSetUpRepo.count(QAdminFeeSetUp.adminFeeSetUp.binder.binId.eq(policy.getBinder().getBinId()).and(QAdminFeeSetUp.adminFeeSetUp.status.eq("Active")));

                        if (count != 1) {
                            throw new BadRequestException("Please configure set up for admin fee to continue...");
                        }

                        AdminFeeSetUp adminFeeSetUp = adminFeeSetUpRepo.findOne(QAdminFeeSetUp.adminFeeSetUp.binder.binId.eq(policy.getBinder().getBinId()).and(QAdminFeeSetUp.adminFeeSetUp.status.eq("Active")));

                        //admin fee check
                        if(adminFeeSetUp != null && adminFeeSetUp.getAdminFeeRateType() != null) {
                            final double rate = (adminFeeSetUp.getAdminFeeRateType().equals("Percent")) ? 100 : 1;
                            BigDecimal adminFeeTotal = BigDecimal.ZERO;
                            if (adminFeeSetUp.getAdminFeeRateType().equals("Percent")) {
                                adminFeeTotal = BigDecimal.valueOf((adminFeeSetUp.getAdminFeeRate().doubleValue() / rate) * policy.getBasicPrem().doubleValue());
                            } else {
                                adminFeeTotal = adminFeeSetUp.getAdminFeeRate();
                            }
                            final double vatrate = (adminFeeSetUp.getVateRateType().equals("Percent")) ? 100 : 1;
                            BigDecimal vatTotal = BigDecimal.valueOf((adminFeeSetUp.getVatRate().doubleValue() / vatrate) * adminFeeTotal.doubleValue());
                            if ((!adminFeeSetUp.getVateRateType().equals("Percent"))) {
                                vatTotal = adminFeeSetUp.getVatRate();
                            }

                            final CommissionPayments adminCommissionPayments = new CommissionPayments();
                            adminCommissionPayments.setProcessed("N");
                            adminCommissionPayments.setDate(new Date());
                            adminCommissionPayments.setCurrencies(policy.getTransCurrency());
                            adminCommissionPayments.setWhtx(BigDecimal.valueOf(vatTotal.abs().doubleValue() * 1));
                            adminCommissionPayments.setAmount(BigDecimal.valueOf(adminFeeTotal.abs().doubleValue() * -1));
                            adminCommissionPayments.setNetAmount(BigDecimal.valueOf((adminFeeTotal.abs().subtract(vatTotal.abs())).doubleValue() * -1));
                            adminCommissionPayments.setDebitTransaction(sysTransRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(debitId)));
                            adminCommissionPayments.setCreditTransaction(trans);
                            adminCommissionPayments.setTransType("Admin Fee");
                            adminCommissionPayments.setProcessedBy(userUtils.getCurrentUser());
                            adminCommissionPayments.setAuthorised("N");
                            commissionPaymentsRepo.save(adminCommissionPayments);

                        }


                    }
                }

            }

            final BigDecimal commFee = commissionPaymentsRepo.getCreditTotalCommission(trans.getTransno(),"Commission");
            final BigDecimal subAgent = commissionPaymentsRepo.getCreditTotalCommission(trans.getTransno(),"Sub Agent Commission");
            final BigDecimal adminFee = commissionPaymentsRepo.getCreditTotalCommission(trans.getTransno(),"Admin Fee");


            final BigDecimal commWhtx = commissionPaymentsRepo.getCreditTotalWhtx(trans.getTransno(),"Commission");
            final BigDecimal adminFeeWhtx = commissionPaymentsRepo.getCreditTotalWhtx(trans.getTransno(),"Admin Fee");
            Iterable<LifeReceipts> existingLifeRcts = lifeReceiptsRepo.findAll(QLifeReceipts.lifeReceipts.policyTrans.policyId.eq(policy.getPolicyId()));
            for (LifeReceipts lifeReceiptss : existingLifeRcts) {
                Iterable<LifeReceiptAllocations> lifeReceiptAllocations = lifeReceiptAllocationsRepo.findAll(QLifeReceiptAllocations.lifeReceiptAllocations.lifeReceipts.receiptId.eq(lifeReceiptss.getReceiptId()));
                for(LifeReceiptAllocations lifeReceiptAllocation : lifeReceiptAllocations){
                    lifeReceiptAllocation.setTransaction(transaction);
                    lifeReceiptAllocation.setAdminFeeAmt(adminFee);
                    lifeReceiptAllocation.setSubAgentCommAmount(subAgent);
                    lifeReceiptAllocation.setCommAmount(commFee);
                    lifeReceiptAllocation.setLifeWhtx(commWhtx);
                    lifeReceiptAllocation.setAdminFeeAmtWhtx(adminFeeWhtx);
                    lifeReceiptAllocation.setCommAmount(commFee);
                    lifeReceiptAllocationsRepo.save(lifeReceiptAllocation);
                }
            }
        }
        else {
            if (systemTransaction == null) {
                throw new BadRequestException("Cannot Authorise Transactions.....Error generating transaction to allocate"+policy.getTransType());
            }

            postUwTransactions(policy, transaction, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, systemTransaction);
        }

        // CO PROCESSING
        if ("CO".equalsIgnoreCase(policy.getTransType())) {
            log.info("=== CO PROCESSING STARTED for Policy: {} ===", polCode);

            String refno = policy.getPreviousTrans().getRefNo();
            Long prevPolicyId = policy.getPreviousTrans().getPolicyId();

            // STEP 1: CREATE CLIENT CREDIT TRANSACTION
            log.info("CO Step 1: Creating CLIENT CREDIT transaction for CO");
            log.info("CO Step 1a: Previous policy refNo: {}, PolicyId: {}", refno, prevPolicyId);
            log.info("CO Step 1b: Current policy refNo: {}", policy.getRefNo());

            BigDecimal basicPrem = (policy.getBasicPrem() == null) ? BigDecimal.ZERO : policy.getBasicPrem();
            BigDecimal extras = (policy.getExtras() == null) ? BigDecimal.ZERO : policy.getExtras();
            BigDecimal phcf = (policy.getPhcf() == null) ? BigDecimal.ZERO : policy.getPhcf();
            BigDecimal tl = (policy.getTrainingLevy() == null) ? BigDecimal.ZERO : policy.getTrainingLevy();
            BigDecimal sd = (policy.getStampDuty() == null) ? BigDecimal.ZERO : policy.getStampDuty();
            BigDecimal amountWithTaxes = basicPrem.add(extras).add(phcf).add(tl).add(sd);

            BigDecimal adminFeeAmt = BigDecimal.ZERO;
            if(policy.getAdminFeeApplicable()!=null && "Y".equalsIgnoreCase(policy.getAdminFeeApplicable())){
                if(policy.getAdminFeeAmt()!=null && policy.getAdminFeeVatAmt()!=null){
                    adminFeeAmt = policy.getAdminFeeAmt().subtract(policy.getAdminFeeVatAmt());
                }
                else if(policy.getAdminFeeAmt()!=null && policy.getAdminFeeVatAmt()==null){
                    adminFeeAmt = policy.getAdminFeeAmt();
                }
            }

            log.info("CO Step 2: CO amounts - Basic: {}, Total: {}", basicPrem, amountWithTaxes);

            SystemTransactions savedClientTrans = null;
//            if (basicPrem.compareTo(BigDecimal.ZERO) != 0) {
//                log.info("CO Step 3: Creating CLIENT CREDIT transaction - Amount: {}", amountWithTaxes);
//
//                SystemTransactions clientTrans = new SystemTransactions();
//                clientTrans.setAmount(basicPrem.abs().multiply(BigDecimal.ONE.negate()).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
//                clientTrans.setAuthDate(new Date());
//                clientTrans.setAuthorised("Y");
//                clientTrans.setAdminFeeNet(adminFeeAmt.abs().multiply(BigDecimal.ONE.negate()).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
//                clientTrans.setBalance(amountWithTaxes.abs().multiply(BigDecimal.ONE.negate()).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
//                clientTrans.setBranch(policy.getBranch());
//                clientTrans.setClientType("C");
//                clientTrans.setControlAcc(policy.getClient().getTenantNumber());
//                clientTrans.setClient(policy.getClient());
//                clientTrans.setCurrRate(new BigDecimal(1));
//                clientTrans.setCurrency(policy.getTransCurrency());
//                clientTrans.setNarrations("Posting client Credit Note for Cancellation");
//                clientTrans.setNetAmount(amountWithTaxes.abs().multiply(BigDecimal.ONE.negate()).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
//                clientTrans.setOrigin("U");
//                clientTrans.setPhfund(phcf.setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
//                clientTrans.setPolicy(policy);
//                clientTrans.setRefNo(policy.getRefNo());
//                clientTrans.setSd(sd.abs().multiply(BigDecimal.ONE.negate()).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
//                clientTrans.setTl(tl.abs().multiply(BigDecimal.ONE.negate()).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
//                clientTrans.setTransDate(new Date());
//                clientTrans.setValueDate(java.sql.Date.valueOf(HolidayUtils.getAdjustedTransactionDate(LocalDate.now())));
//                clientTrans.setTransdc("C");
//                clientTrans.setTransType(debitCode);
//                clientTrans.setUserAuth(userUtils.getCurrentUser().getUsername());
//                clientTrans.setWhtx(BigDecimal.ZERO);
//                clientTrans.setExtras(extras.abs().multiply(BigDecimal.ONE.negate()).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
//                clientTrans.setPostedDate(new Date());
//                clientTrans.setPostedUser(userUtils.getCurrentUser());
//                clientTrans.setTransaction(transaction);
//
//                savedClientTrans = sysTransRepo.save(clientTrans);
//                log.info("CO Step 4: CLIENT CREDIT transaction SAVED - TransNo: {}, TransDC: {}, Amount: {}",
//                        savedClientTrans.getTransno(), savedClientTrans.getTransdc(), savedClientTrans.getNetAmount());
//            }

            // STEP 2: FIND ORIGINAL TRANSACTIONS AND ALLOCATE
            log.info("CO Step 5: Starting search for original transactions");
            log.info("CO Step 6: Current policy refNo: {}", policy.getRefNo());

            // Find original CLIENT CREDIT transaction (search by PolicyId, not RefNo)
//            log.info("CO Step 7: Searching for original CLIENT CREDIT transaction for policyId: {}", prevPolicyId);
//            SystemTransactions originalClientTrans = sysTransRepo.findOne(QSystemTransactions.systemTransactions.policy.policyId.eq(prevPolicyId)
//                    .and(QSystemTransactions.systemTransactions.transdc.eq("C"))  // Look for CREDIT
//                    .and(QSystemTransactions.systemTransactions.clientType.eq("C")));
            // Find the original CLIENT CREDIT transaction that matches the amount
            log.info("CO Step 7: Searching for original CLIENT CREDIT transaction for policyId: {}", prevPolicyId);
            List<SystemTransactions> clientCreditTransactions = (List<SystemTransactions>) sysTransRepo.findAll(
                    QSystemTransactions.systemTransactions.policy.policyId.eq(prevPolicyId)
                            .and(QSystemTransactions.systemTransactions.transdc.eq("C"))
                            .and(QSystemTransactions.systemTransactions.clientType.eq("C"))
                            .and(QSystemTransactions.systemTransactions.transType.eq("RC"))  // Look for RC type specifically
            );

            SystemTransactions originalClientTrans = null;
            if (!clientCreditTransactions.isEmpty()) {
                // Find the one that matches the CO amount
                BigDecimal coAmount = basicPrem.abs();
                originalClientTrans = clientCreditTransactions.stream()
                        .filter(t -> t.getAmount() != null && t.getAmount().abs().compareTo(coAmount) == 0)
                        .findFirst()
                        .orElse(clientCreditTransactions.get(0));

                log.info("CO Step 8: Using matching credit transaction - TransNo: {}, Amount: {}",
                        originalClientTrans.getTransno(), originalClientTrans.getAmount());
            }

            if (originalClientTrans != null) {
                log.info("CO Step 8: FOUND original client credit - TransNo: {}, Amount: {}, NetAmount: {}, RefNo: {}, TransType: {}",
                        originalClientTrans.getTransno(), originalClientTrans.getAmount(), originalClientTrans.getNetAmount(),
                        originalClientTrans.getRefNo(), originalClientTrans.getTransType());
            } else {

                log.info("CO Step 8a: Debugging - Searching for ANY client transactions for policyId: {}", prevPolicyId);
                Iterable<SystemTransactions> allClientTrans = sysTransRepo.findAll(
                        QSystemTransactions.systemTransactions.policy.policyId.eq(prevPolicyId)
                                .and(QSystemTransactions.systemTransactions.clientType.eq("C")));
                int clientCount = 0;
                for (SystemTransactions trans : allClientTrans) {
                    clientCount++;
                    log.info("CO Debug CLIENT: Found transaction - TransNo: {}, TransDC: {}, TransType: {}, Amount: {}, RefNo: {}",
                            trans.getTransno(), trans.getTransdc(), trans.getTransType(), trans.getAmount(), trans.getRefNo());
                }

                if (clientCount == 0) {
                    log.error("CO Step 8b: NO CLIENT transactions found for policyId: {}", prevPolicyId);
                }
            }

            // CLIENT ALLOCATION - CREDIT vs CREDIT (deallocation pattern)
            if (originalClientTrans != null && savedClientTrans != null) {
                log.info("CO Step 9: Performing client contra allocation - NEW Credit TransNo: {} vs ORIGINAL Credit TransNo: {}",
                        savedClientTrans.getTransno(), originalClientTrans.getTransno());
                try {
                    allocationService.autoAllocateContra(savedClientTrans.getTransno(), originalClientTrans.getTransno());
                    log.info("CO Step 10: Client contra allocation SUCCESSFUL");
                } catch (Exception e) {
                    log.error("CO Step 10: Client contra allocation FAILED: {}", e.getMessage(), e);
                }
            } else {
                log.error("CO Step 9-10: SKIPPING client contra allocation - originalClientTrans: {}, savedClientTrans: {}",
                        (originalClientTrans != null ? "FOUND" : "NULL"), (savedClientTrans != null ? "FOUND" : "NULL"));
            }

            log.info("CO PROCESSING COMPLETED for Policy: {} ===", polCode);
        }
        List<BigInteger> receipts = lifeReceiptsRepo.findAllPolicyLifeReceipts(policy.getPolicyId());

        for(BigInteger receipt:receipts){
            Iterable<ReceiptTransDtls> rctDetails = receiptDetailsRepository.findAllReceipts(receipt.longValue());
            for (ReceiptTransDtls tran : rctDetails) {
                receiptService.postReceiptAccount(transaction, policy, tran.getRctAmount());
            }

        }
        boolean medicalProduct = false;
        if (policy.getProduct().getProGroup().getPrgType() == null || !policy.getProduct().getProGroup().getPrgType().equalsIgnoreCase("MD")) {
            medicalProduct = false;
        } else if (policy.getProduct().getProGroup().getPrgType().equalsIgnoreCase("MD")) {
            medicalProduct = true;
        }
        try {
            Map<String, Object> processVariables = Maps.newHashMap();
            processVariables.put("confirmAuth", true);
            workflowService.completeTask(String.valueOf(polCode), processVariables, policy, DocType.GEN_UW_DOCUMENT, (medicalProduct) ? "Y" : "N", null, null, null, null);
        } catch (Exception e) {
            throw new BadRequestException("Authorize Checks first....");
        }
        // CO PROCESSING
        if ("CO".equalsIgnoreCase(policy.getTransType())) {
            MakerChecker makerChecker = makerCheckerRepo.findOne(QMakerChecker.makerChecker.policyId.eq(polCode).and(QMakerChecker.makerChecker.taskType.equalsIgnoreCase("ANP")));
            if (makerChecker != null) {
                makerCheckerService.approveTask(makerChecker.getId());
            }
        } else {
            MakerChecker makerChecker = null;

            if (policy.getBusinessType().equals("F")) {
                makerChecker = makerCheckerRepo.findOne(QMakerChecker.makerChecker.policyId.eq(polCode).and(QMakerChecker.makerChecker.taskType.equalsIgnoreCase("ANP")).and(QMakerChecker.makerChecker.status.equalsIgnoreCase("N")));
            } else {
                makerChecker = makerCheckerRepo.findOne(QMakerChecker.makerChecker.policyId.eq(polCode).and(QMakerChecker.makerChecker.taskType.equalsIgnoreCase("ALP")).and(QMakerChecker.makerChecker.status.equalsIgnoreCase("N")));
            }
            if (makerChecker != null) {
                makerCheckerService.approveTask(makerChecker.getId());
            }
        }

    }

    public String createEndowmentAlak(PolicyTrans policy, Iterable<RiskTrans> risks) throws BadRequestException {
        PolicyCreationRequest request = new PolicyCreationRequest();
        PolicyBasicDetails policyBasicDetails = new PolicyBasicDetails();
        ClientDetails clientDetails = new ClientDetails();
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

        int frequency = 0;
        String polFrequency = policy.getFrequency();
        if (polFrequency != null) {
            switch (polFrequency.toUpperCase()) {
                case "M":
                    frequency = 1;
                    break;
                case "Q":
                    frequency = 3;
                    break;
                case "S":
                    frequency = 6;
                    break;
                case "A":
                    frequency = 12;
                    break;
                case "SG":
                    frequency = 1;
                    break;
            }
        }

        policyBasicDetails.setPolicyTypeID(1);
        policyBasicDetails.setPolicyClassID(1);
        policyBasicDetails.setInceptionDate(policy.getCoverFrom() != null ? outputFormat.format(policy.getCoverFrom()) : "");
        policyBasicDetails.setFrequency(frequency);

        Integer branchId = getSafeParam("ALAK_BRANCH_ID", 1);
        Integer brokerId = getSafeParam("ALAK_BROKER_ID", 48);
        Integer brokerRefId = getSafeParam("ALAK_BROKER_REF_ID", 483);
        Integer salesChannel = getSafeParam("ALAK_SALES_CHANNEL", 417);
        policyBasicDetails.setBranchID(branchId);
        policyBasicDetails.setBrokerID(brokerId);
        policyBasicDetails.setBrokerRefID(brokerRefId);
        policyBasicDetails.setSalesChannel(salesChannel);

        request.setPolicyBasicDetails(policyBasicDetails);
        request.setStaffId("");
        boolean staffFlag = !policy.getClient().getTenantType().getClientType().equalsIgnoreCase("I");
        request.setStaffDiscount(staffFlag);
        request.setInflationPercentage(0);

        Iterable<PolicyBeneficiaries> policyBeneficiaries = policyBeneficiariesRepo.findAll(
                QPolicyBeneficiaries.policyBeneficiaries.policy.policyId.eq(policy.getPolicyId())
        );

        List<PolicyBeneficiaries> policyBeneficiariesList = new ArrayList<>();
        for (PolicyBeneficiaries beneficiary : policyBeneficiaries) {
            policyBeneficiariesList.add(beneficiary);
        }

        if (!policyBeneficiariesList.isEmpty()) {
            List<BeneficiaryDetails> beneficiaryDetailsList = new ArrayList<>();
            List<String> maleRelationships = Arrays.asList("Father", "Son", "Uncle", "Nephew", "Brother", "Grandson");

            for (PolicyBeneficiaries beneficiary : policyBeneficiariesList) {
                BeneficiaryDetails beneficiaryDetails = new BeneficiaryDetails();
                beneficiaryDetails.setBeneficiaryPortion(beneficiary.getBenAllocation().intValue());
                beneficiaryDetails.setCitizenCountryID(7);
                beneficiaryDetails.setFirstName(beneficiary.getBeneficiaryName() != null && beneficiary.getBeneficiaryName().contains(" ") ? beneficiary.getBeneficiaryName().split(" ")[0] : "");
                beneficiaryDetails.setRelationID(12);

                String title = "Miss";
                String gender = "F";
                if (maleRelationships.contains(beneficiary.getBeneficiaryRelationship())) {
                    title = "Mr";
                    gender = "M";
                }

                beneficiaryDetails.setTitle(title);
                beneficiaryDetails.setGender(gender);
                beneficiaryDetails.setInitials(beneficiary.getBeneficiaryName() != null && !beneficiary.getBeneficiaryName().isEmpty() ? String.valueOf(beneficiary.getBeneficiaryName().charAt(0)) : "");
                beneficiaryDetails.setSurName(beneficiary.getBeneficiaryName() != null && beneficiary.getBeneficiaryName().contains(" ") ? beneficiary.getBeneficiaryName().split(" ")[1] : beneficiary.getBeneficiaryName());
                beneficiaryDetails.setBirthDate(beneficiary.getDateRegistered() != null ? outputFormat.format(beneficiary.getDateRegistered()) : "");
                beneficiaryDetails.setIDNumber(beneficiary.getBeneficiaryregNo());
                beneficiaryDetailsList.add(beneficiaryDetails);
            }

            request.setBeneficiary1(beneficiaryDetailsList.get(0));
            if (beneficiaryDetailsList.size() > 1) request.setBeneficiary2(beneficiaryDetailsList.get(1));
            if (beneficiaryDetailsList.size() > 2) request.setBeneficiary3(beneficiaryDetailsList.get(2));
            if (beneficiaryDetailsList.size() > 3) request.setBeneficiary4(beneficiaryDetailsList.get(3));
            if (beneficiaryDetailsList.size() > 4) request.setBeneficiary5(beneficiaryDetailsList.get(4));
        }

        ClientDef clientDef = policy.getClient();

        if (clientDef != null) {
            if(clientDef.getPinNo() == null){ throw new RuntimeException("Client's pin no. is missing."); }
            if(clientDef.getTown() == null || clientDef.getTown().getCtName() == null){ throw new RuntimeException("Client's town is required."); }
            if(clientDef.getDob() == null ) { throw new RuntimeException("Client's DOB is required."); }
            if(clientDef.getOtherNames() == null){ throw new RuntimeException("Client's Surname is required."); }
            if(clientDef.getClientTitle() == null || clientDef.getClientTitle().getTitleName()  == null){ throw new RuntimeException("Client's title is required."); }
            if(clientDef.getFname() ==  null){ throw new RuntimeException("Client's First name is required."); }
            if(clientDef.getPhonePrefix() == null || clientDef.getPhonePrefix().getPrefixName() == null || clientDef.getPhoneNo() == null){throw new RuntimeException("Client's Tel/cell is missing."); }
            if(clientDef.getIdNo() == null){ throw new RuntimeException("Client's Identification Number is not defined."); }
            if(clientDef.getGender() == null){ throw new RuntimeException("Client's  gender is not defined."); }
            if(clientDef.getPostalCodesDef() == null || clientDef.getPostalCodesDef().getZipCode() == null || clientDef.getTown() == null || clientDef.getTown().getCtName() == null ){ throw new RuntimeException("Client's postal/zip code is missing."); }

            clientDetails.setResidenceCountryID(116);
            clientDetails.setEmailID(clientDef.getEmailAddress());
            clientDetails.setPreferredMethodSMS(true);
            clientDetails.setPreferredMethodEmail(true);
            clientDetails.setPreferredMethodPost(false);
            clientDetails.setPreferredMethodCollect(false);
            clientDetails.setTelHome(null);
            clientDetails.setTelWork(null);
            clientDetails.setTelCell(clientDef.getPhonePrefix().getPrefixName() + clientDef.getPhoneNo());
            clientDetails.setAltTelCel("");
            clientDetails.setGrossSalary(3112);
            clientDetails.setMaritalStatusID(2);
            clientDetails.setClientGroupID(1);
            clientDetails.setPostalCode(clientDef.getPostalCodesDef().getZipCode());
            clientDetails.setGender(clientDef.getGender());
            clientDetails.setCompanyName(1);
            clientDetails.setIDNumber(clientDef.getIdNo());
            clientDetails.setIndustryCode("83110");
            clientDetails.setCitizenCountryID(116);
            clientDetails.setFirstName(clientDef.getFname());
            clientDetails.setEmployer("21321");
            clientDetails.setIdentificationTypeID(2);
            clientDetails.setTitle(clientDef.getClientTitle().getTitleName() );
            clientDetails.setInitials(String.valueOf(clientDef.getFname().charAt(0)));
            String[] otherNamesParts = clientDef.getOtherNames().split(" ");
            String surName = (otherNamesParts.length > 1) ? otherNamesParts[1] : otherNamesParts[0];
            clientDetails.setSurname(surName); //clientDef.getOtherNames().split(" ")[1]);
            clientDetails.setEmploymentStatusCode("I_11");
            clientDetails.setOccupation("16-0016");
            clientDetails.setSourceOfFundsID(30);
            clientDetails.setSourceOfIncomeID("27");
            clientDetails.setResidentialAddress4("dcf");
            if (clientDef.getDob() != null) {
                Date date = new Date(clientDef.getDob().getTime());
                LocalDate localDob = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate employmentDate = localDob.plusYears(18);
                Date dateEmployed = Date.from(employmentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                clientDetails.setEmploymentDate(outputFormat.format(dateEmployed));
                clientDetails.setBirthDate(outputFormat.format(clientDef.getDob()));
            }
            clientDetails.setPostalAddress4("");
            clientDetails.setResidentialAddress1(clientDef.getTown().getCtName());
            clientDetails.setPostalAddress1(clientDef.getTown().getCtName());
            clientDetails.setResidentialAddress2("");
            clientDetails.setPostalAddress2("cdce");
            clientDetails.setPostalAddress3("");
            clientDetails.setResidentialAddress3("12");

            request.setTaxPinNo(clientDef.getPinNo());
        }

        request.setClientDetails(clientDetails);
        boolean ahb = false;
        for (RiskTrans risk : risks) {
            Iterable<SectionTrans> sections = sectionRepo.findAll(QSectionTrans.sectionTrans.risk.riskId.eq(risk.getRiskId()));
            List<PremiumItemsBean> itemsBeen = new ArrayList<>();
            for (SectionTrans section : sections) {
                List<String> items = new ArrayList<>();
                double minPrem = 0;
                if (section.getPremRates() != null) {
                    PremRatesDef premRatesDef = section.getPremRates();
                    if (premRatesDef.getMinPremium() != null && premRatesDef.getMinPremium().compareTo(BigDecimal.ZERO) == 1) {
                        minPrem = premRatesDef.getMinPremium().doubleValue();
                    }
                }

                PremiumItemsBean premiumItemsBean = new PremiumItemsBean(section.getSection().getShtDesc(), section.getRate().doubleValue(),
                        section.getFreeLimit().doubleValue(), minPrem,
                        section.getAmount().doubleValue(), section.getSectId(), section.getDivFactor().doubleValue(),
                        section.getSection().getType().getCode(), section.getSection().getType().getOrder(), items, policy.getPolicyId());
                itemsBeen.add(premiumItemsBean);
            }
            for (PremiumItemsBean premiumItemsBean : itemsBeen) {
                if (premiumItemsBean.getPremiumId().equalsIgnoreCase("Accidental Hospitalization Benefit")) {
                    ahb = true;
                }
            }
        }
        request.setAccidentalHospitalisationBenefit(ahb);
        request.setMedicalUWQ1(false);
        request.setMedicalUWQ2(false);
        request.setMedicalUWQ3(false);
        request.setMedicalUWQ4(false);
        request.setMedicalUWQ5(false);
        request.setSumAssuredAtEntry(policy.getSumInsured().intValue());
        request.setMaturityPeriod(1);
        request.setPremiumPaymentTerms(policy.getPolTerm());

        //request.setApplicationSignDate(policy.getCoverFrom() != null ? outputFormat.format(policy.getCoverFrom()) : outputFormat.format(policy.getWefDate()));
        //request.setApplicationReceivedDate(policy.getCoverFrom() != null ? outputFormat.format(policy.getCoverFrom()) : outputFormat.format(policy.getWefDate()));
        request.setApplicationSignDate(outputFormat.format(new Date()));
        request.setApplicationReceivedDate(outputFormat.format(new Date()));
        System.out.println("CREATE POLICY PAYLOAD: " + new Gson().toJson(request));
        try {
            String paramValue = paramService.getParameterString("API_INTEGRATION_URL") + "/alak/createPolicy";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<?> requestEntity = new HttpEntity<>(request, headers);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(paramValue, requestEntity, String.class);
            JsonNode rootNode = objectMapper.readTree(responseEntity.getBody());
            if ("200".equals(rootNode.get("Code").asText())) {
                //policy.setClientPolNo(rootNode.get("Value").asText());
                return rootNode.get("Value").asText();
            } else {
                throw new RuntimeException(rootNode.get("Message").asText());
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
        catch (HttpServerErrorException ex) {
            throw new BadRequestException("ELAK Integration service has issues...Please contact Support for assistance");

        } catch (RestClientException ex) {
            throw new BadRequestException("ELAK Integration service has issues..."+ex.getMessage());
        }
    }

    public String createPAPolicyAlak(PolicyTrans policy, Iterable<RiskTrans> risks) throws BadRequestException {
        PersonalAccidentPolicyCreationRequest request = new PersonalAccidentPolicyCreationRequest();
        PersonalAccidentPolicyCreationRequest.PolicyBasicDetailsDto policyBasicDetails = new PersonalAccidentPolicyCreationRequest.PolicyBasicDetailsDto();
        PersonalAccidentPolicyCreationRequest.ClientDetailsDto clientDetails = new PersonalAccidentPolicyCreationRequest.ClientDetailsDto();
        PersonalAccidentPolicyCreationRequest.PolicyBankingDataDto policyBankingDetails = new PersonalAccidentPolicyCreationRequest.PolicyBankingDataDto();

        PolicyMiscInfo policyMiscInfo = policyMiscInfoRepo.findByPolicyId(policy.getPolicyId());

        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

        int frequency = 0;
        String polFrequency = policy.getFrequency();
        if (polFrequency != null) {
            switch (polFrequency.toUpperCase()) {
                case "M":
                    frequency = 1;
                    break;
                case "Q":
                    frequency = 3;
                    break;
                case "S":
                    frequency = 6;
                    break;
                case "A":
                    frequency = 12;
                    break;
                case "SG":
                    frequency = 1;
                    break;
            }
        }

        policyBasicDetails.setPolicyTypeID(1);
        policyBasicDetails.setPolicyClassID(1);
        policyBasicDetails.setInceptionDate(policy.getCoverFrom() != null ? outputFormat.format(policy.getCoverFrom()) : "");
        policyBasicDetails.setFrequency(frequency);

        Integer branchId = getSafeParam("ALAK_BRANCH_ID", 1);
        Integer brokerId = getSafeParam("ALAK_BROKER_ID", 48);
        Integer brokerRefId = getSafeParam("ALAK_BROKER_REF_ID", 483);
        Integer salesChannel = getSafeParam("ALAK_SALES_CHANNEL", 417);
        policyBasicDetails.setBranchID(branchId);
        policyBasicDetails.setBrokerID(brokerId);
        policyBasicDetails.setBrokerRefID(brokerRefId);
        policyBasicDetails.setSalesChannel(salesChannel);

        request.setPolicyBasicDetails(policyBasicDetails);

        //request.setStaffId("");

        boolean staffFlag = !policy.getClient().getTenantType().getClientType().equalsIgnoreCase("I");
        request.setStaffDiscount(staffFlag);
//        if (policyMiscInfo == null || policyMiscInfo.getInflationPercent() == null) {
//            throw new RuntimeException("The policy is missing inflation percentage(%) or invalid.");
//        }
//        request.setInflationPercentage(policyMiscInfo.getInflationPercent());
        request.setInflationPercentage(0);


        Iterable<PolicyBeneficiaries> policyBeneficiaries = policyBeneficiariesRepo.findAll(
                QPolicyBeneficiaries.policyBeneficiaries.policy.policyId.eq(policy.getPolicyId())
        );

        List<PolicyBeneficiaries> policyBeneficiariesList = new ArrayList<>();
        for (PolicyBeneficiaries beneficiary : policyBeneficiaries) {
            policyBeneficiariesList.add(beneficiary);
        }

        if (!policyBeneficiariesList.isEmpty()) {
            List<PersonalAccidentPolicyCreationRequest.BeneficiaryDto> beneficiaryDetailsList = new ArrayList<>();
            List<String> maleRelationships = Arrays.asList("Father", "Son", "Uncle", "Nephew", "Brother", "Grandson");

            for (PolicyBeneficiaries beneficiary : policyBeneficiariesList) {
                PersonalAccidentPolicyCreationRequest.BeneficiaryDto beneficiaryDetails = new PersonalAccidentPolicyCreationRequest.BeneficiaryDto();
                beneficiaryDetails.setBeneficiaryPortion(beneficiary.getBenAllocation().intValue());
                beneficiaryDetails.setCitizenCountryID(7);
                beneficiaryDetails.setFirstName(beneficiary.getBeneficiaryName() != null && beneficiary.getBeneficiaryName().contains(" ") ? beneficiary.getBeneficiaryName().split(" ")[0] : "");
                beneficiaryDetails.setRelationID(12);

                String title = "Miss";
                String gender = "F";
                if (maleRelationships.contains(beneficiary.getBeneficiaryRelationship())) {
                    title = "Mr";
                    gender = "M";
                }

                beneficiaryDetails.setTitle(title);
                beneficiaryDetails.setGender(gender);
                beneficiaryDetails.setInitials(beneficiary.getBeneficiaryName() != null && !beneficiary.getBeneficiaryName().isEmpty() ? String.valueOf(beneficiary.getBeneficiaryName().charAt(0)) : "");
                beneficiaryDetails.setSurName(beneficiary.getBeneficiaryName() != null && beneficiary.getBeneficiaryName().contains(" ") ? beneficiary.getBeneficiaryName().split(" ")[1] : beneficiary.getBeneficiaryName());
                beneficiaryDetails.setBirthDate(beneficiary.getDateRegistered() != null ? outputFormat.format(beneficiary.getDateRegistered()) : "");
                beneficiaryDetails.setIDNumber(beneficiary.getBeneficiaryregNo());
                beneficiaryDetails.setIDType(1);
                beneficiaryDetailsList.add(beneficiaryDetails);
            }

            request.setBeneficiary(beneficiaryDetailsList.get(0));
            if (beneficiaryDetailsList.size() > 1) request.setBeneficiary(beneficiaryDetailsList.get(1));
            if (beneficiaryDetailsList.size() > 2) request.setBeneficiary(beneficiaryDetailsList.get(2));
            if (beneficiaryDetailsList.size() > 3) request.setBeneficiary(beneficiaryDetailsList.get(3));
            if (beneficiaryDetailsList.size() > 4) request.setBeneficiary(beneficiaryDetailsList.get(4));
        }

        ClientDef clientDef = policy.getClient();
        if (clientDef != null) {
            if(clientDef.getPinNo() == null){ throw new RuntimeException("Client's pin no. is missing."); }
            if(clientDef.getTown() == null || clientDef.getTown().getCtName() == null){ throw new RuntimeException("Client's town is required."); }
            if(clientDef.getDob() == null ) { throw new RuntimeException("Client's DOB is required."); }
            if(clientDef.getOtherNames() == null){ throw new RuntimeException("Client's Surname is required."); }
            if(clientDef.getClientTitle() == null || clientDef.getClientTitle().getTitleName()  == null){ throw new RuntimeException("Client's title is required."); }
            if(clientDef.getFname() ==  null){ throw new RuntimeException("Client's First name is required."); }
            if(clientDef.getPhonePrefix() == null || clientDef.getPhonePrefix().getPrefixName() == null || clientDef.getPhoneNo() == null){throw new RuntimeException("Client's Tel/cell is missing."); }
            if(clientDef.getIdNo() == null){ throw new RuntimeException("Client's Identification Number is not defined."); }
            if(clientDef.getGender() == null){ throw new RuntimeException("Client's  gender is not defined."); }
            if(clientDef.getPostalCodesDef() == null || clientDef.getPostalCodesDef().getZipCode() == null || clientDef.getTown() == null || clientDef.getTown().getCtName() == null ){ throw new RuntimeException("Client's postal/zip code is missing."); }

            clientDetails.setTitle(clientDef.getClientTitle().getTitleName());
            clientDetails.setResidenceCountryID(116);
            clientDetails.setEmailID(clientDef.getEmailAddress());
            clientDetails.setPreferredMethodSMS(true);
            clientDetails.setPreferredMethodEmail(true);
            clientDetails.setPreferredMethodPost(false);
            clientDetails.setPreferredMethodCollect(false);
            clientDetails.setTelHome(null);
            clientDetails.setTelWork(null);
            clientDetails.setTelCell(clientDef.getPhonePrefix().getPrefixName() + clientDef.getPhoneNo());
            clientDetails.setAltTelCel("");
            clientDetails.setGrossSalary("3112");
            clientDetails.setMaritalStatusID(2);
            clientDetails.setClientGroupID(1);
            clientDetails.setPostalCode(clientDef.getPostalCodesDef().getZipCode());
            clientDetails.setGender(clientDef.getGender());
            clientDetails.setCompanyName("1");
            clientDetails.setIDNumber(clientDef.getIdNo());
            clientDetails.setIndustryCode("83110");
            clientDetails.setCitizenCountryID(116);
            clientDetails.setFirstName(clientDef.getFname());
            clientDetails.setEmployer("21321");
            clientDetails.setIdentificationTypeID(2);
            clientDetails.setInitials(String.valueOf(clientDef.getFname().charAt(0)));
            String[] otherNamesParts = clientDef.getOtherNames().split(" ");
            String surName = (otherNamesParts.length > 1) ? otherNamesParts[1] : otherNamesParts[0];
            clientDetails.setSurname(surName); //clientDef.getOtherNames().split(" ")[1]);
            clientDetails.setEmploymentStatusCode("I_11");
            clientDetails.setOccupation("16-0016");
            clientDetails.setSourceOfFundsID(30);
            clientDetails.setSourceOfIncomeID("27");
            clientDetails.setSourceOfIncomeOthers("");
            clientDetails.setResidentialAddress1(clientDef.getTown().getCtName());
            clientDetails.setResidentialAddress2(null);
            clientDetails.setResidentialAddress3("12");
            clientDetails.setResidentialAddress4("dcf");
            clientDetails.setResidentialCode("411033");
            if (clientDef.getDob() != null) {
                Date date = new Date(clientDef.getDob().getTime());
                LocalDate localDob = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate employmentDate = localDob.plusYears(18);
                Date dateEmployed = Date.from(employmentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                clientDetails.setEmploymentDate(outputFormat.format(dateEmployed));
                clientDetails.setBirthDate(outputFormat.format(clientDef.getDob()));
            }

            clientDetails.setPostalAddress1(clientDef.getTown().getCtName());
            clientDetails.setPostalAddress2("cdce");
            clientDetails.setPostalAddress3("");
            clientDetails.setPostalAddress4("");
            clientDetails.setCoReg("");
            clientDetails.setFax("");
            clientDetails.setURL("");
            clientDetails.setEmployeeNumber("");
            clientDetails.setSourceOfFundsOthers("");
            clientDetails.setScreeningStatusID(2);
            clientDetails.setScreeningDate("2025-03-01T05:43:51.747Z");
            clientDetails.setScreeningExpiryDate("2025-03-27T05:43:51.747Z");
            clientDetails.setScreeningComment("okay");
            request.setTaxPinNo(clientDef.getPinNo());
        }

        request.setClientDetails(clientDetails);

        //banking details
//        if (policyMiscInfo.getAccountNumber() == null) { throw new RuntimeException("Policy holder’s account number, is required."); }
//        if (policyMiscInfo.getAccountName() == null) { throw new RuntimeException("Policy holder’s account Name, is required."); }
//        if (policyMiscInfo.getBankId() == null) { throw new RuntimeException("Policy holder’s bank ID, is required."); }
//        if (policyMiscInfo.getBranchId() == null) { throw new RuntimeException("Policy holder’s bank branch ID, is required."); }
//        if (policyMiscInfo.getAccountType() == null) { throw new RuntimeException("Policy holder's account type ID, is required."); }
//        if (policyMiscInfo.getStrikeDay() == null) { throw new RuntimeException("Policy Strike day, is required."); }
        
        //SystemTransactions transactionsInfo = systemTransactionsRepo.
        policyBankingDetails.setCurrencyID(1); //SET TO 1
        policyBankingDetails.setAccountNumber("123456789"); //policyMiscInfo.getAccountNumber());
        String fullname = "";
        if(clientDef.getFname() != null){
            fullname = clientDef.getFname();
            if(clientDef.getOtherNames() != null){
                fullname = fullname +" "+ clientDef.getOtherNames();
            }
        }
        policyBankingDetails.setAccountName(fullname);//policyMiscInfo.getAccountName());
        policyBankingDetails.setDescription("");
        policyBankingDetails.setAccountMapRef("");
        policyBankingDetails.setBankID(2); //Integer.parseInt(policyMiscInfo.getBankId()));
        policyBankingDetails.setBankBranchID(191); //Integer.parseInt(policyMiscInfo.getBranchId()));
        policyBankingDetails.setAccountTypeID(2); //Integer.parseInt(policyMiscInfo.getAccountType()));
        policyBankingDetails.setSwiftCode("");
        policyBankingDetails.setStrikeDay(1); //Integer.parseInt(policyMiscInfo.getStrikeDay()));
        policyBankingDetails.setAdditionalRef("");
        policyBankingDetails.setPaymentMethodID(1);

        request.setPolicyBankingData(policyBankingDetails);
        boolean ahb = false;
        for (RiskTrans risk : risks) {
            Iterable<SectionTrans> sections = sectionRepo.findAll(QSectionTrans.sectionTrans.risk.riskId.eq(risk.getRiskId()));
            List<PremiumItemsBean> itemsBeen = new ArrayList<>();
            for (SectionTrans section : sections) {
                List<String> items = new ArrayList<>();
                double minPrem = 0;
                if (section.getPremRates() != null) {
                    PremRatesDef premRatesDef = section.getPremRates();
                    if (premRatesDef.getMinPremium() != null && premRatesDef.getMinPremium().compareTo(BigDecimal.ZERO) == 1) {
                        minPrem = premRatesDef.getMinPremium().doubleValue();
                    }
                }

                PremiumItemsBean premiumItemsBean = new PremiumItemsBean(section.getSection().getShtDesc(), section.getRate().doubleValue(),
                        section.getFreeLimit().doubleValue(), minPrem,
                        section.getAmount().doubleValue(), section.getSectId(), section.getDivFactor().doubleValue(),
                        section.getSection().getType().getCode(), section.getSection().getType().getOrder(), items, policy.getPolicyId());
                itemsBeen.add(premiumItemsBean);
            }
            for (PremiumItemsBean premiumItemsBean : itemsBeen) {
                if (premiumItemsBean.getPremiumId().equalsIgnoreCase("Accidental Hospitalization Benefit")) {
                    ahb = true;
                }
            }
        }
        request.setAccidentalHospitalisationBenefit(ahb);
        request.setSumInsuredSelection(policy.getSumInsured().intValue());
        request.setManualSumInsured(0);
        //request.setCoverOption(Integer.parseInt(policyMiscInfo.getCoverOption()));
        request.setCoverOption(1);

//        request.setApplicationSignDate(policy.getCoverFrom() != null ? outputFormat.format(policy.getCoverFrom()) : outputFormat.format(policy.getWefDate()));
//        request.setApplicationReceivedDate(policy.getCoverFrom() != null ? outputFormat.format(policy.getCoverFrom()) : outputFormat.format(policy.getWefDate()));
        request.setApplicationSignDate(outputFormat.format(new Date()));
        request.setApplicationReceivedDate(outputFormat.format(new Date()));
        System.out.println("CREATE PA POLICY PAYLOAD: " + new Gson().toJson(request));
        try {
            String paramValue = paramService.getParameterString("API_INTEGRATION_URL") + "/alak/createPersonalAccidentPolicy";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<?> requestEntity = new HttpEntity<>(request, headers);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(paramValue, requestEntity, String.class);
            JsonNode rootNode = objectMapper.readTree(responseEntity.getBody());
            if ("200".equals(rootNode.get("Code").asText())) {
                return rootNode.get("Value").asText();
            } else {
                throw new RuntimeException(rootNode.get("Message").asText());
            }
        } catch (JsonProcessingException  e) {
            throw new RuntimeException(e.getMessage());
        }
        catch (HttpServerErrorException ex) {
            throw new BadRequestException("ELAK Integration service has issues...Please contact Support for assistance");

        } catch (RestClientException ex) {
            throw new BadRequestException("ELAK Integration service has issues..."+ex.getMessage());
        }
    }

    public String createUPPolicyAlak(PolicyTrans policy, Iterable<RiskTrans> risks) throws BadRequestException{
        UltimateProtectorPolicyCreationRequest request = new UltimateProtectorPolicyCreationRequest();
        UltimateProtectorPolicyCreationRequest.PolicyBasicDetailsDto policyBasicDetails = new UltimateProtectorPolicyCreationRequest.PolicyBasicDetailsDto();
        UltimateProtectorPolicyCreationRequest.ClientDetailsDto clientDetails = new UltimateProtectorPolicyCreationRequest.ClientDetailsDto();
        UltimateProtectorPolicyCreationRequest.PolicyBankingDataDto policyBankingDetails = new UltimateProtectorPolicyCreationRequest.PolicyBankingDataDto();
        PolicyMiscInfo policyMiscInfo = policyMiscInfoRepo.findByPolicyId(policy.getPolicyId());

        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

        int frequency = 0;
        String polFrequency = policy.getFrequency();
        if (polFrequency != null) {
            switch (polFrequency.toUpperCase()) {
                case "M":
                    frequency = 1;
                    break;
                case "Q":
                    frequency = 3;
                    break;
                case "S":
                    frequency = 6;
                    break;
                case "A":
                    frequency = 12;
                    break;
                case "SG":
                    frequency = 1;
                    break;
            }
        }

        policyBasicDetails.setPolicyTypeID(1);
        policyBasicDetails.setPolicyClassID(1);
        policyBasicDetails.setInceptionDate(policy.getCoverFrom() != null ? outputFormat.format(policy.getCoverFrom()) : "");
        policyBasicDetails.setFrequency(frequency);

        Integer branchId = getSafeParam("ALAK_BRANCH_ID", 1);
        Integer brokerId = getSafeParam("ALAK_BROKER_ID", 48);
        Integer brokerRefId = getSafeParam("ALAK_BROKER_REF_ID", 483);
        Integer salesChannel = getSafeParam("ALAK_SALES_CHANNEL", 417);
        policyBasicDetails.setBranchID(branchId);
        policyBasicDetails.setBrokerID(brokerId);
        policyBasicDetails.setBrokerRefID(brokerRefId);
        policyBasicDetails.setSalesChannel(salesChannel);

        request.setPolicyBasicDetails(policyBasicDetails);

        //request.setStaffId("");

        boolean staffFlag = !policy.getClient().getTenantType().getClientType().equalsIgnoreCase("I");
        request.setStaffDiscount(staffFlag);

//        if (policyMiscInfo == null || policyMiscInfo.getInflationPercent() == null) {
//            throw new RuntimeException("The policy is missing inflation percentage(%) or invalid.");
//        }
//        request.setInflationPercentage(policyMiscInfo.getInflationPercent());
        request.setInflationPercentage(0);


        Iterable<PolicyBeneficiaries> policyBeneficiaries = policyBeneficiariesRepo.findAll(
                QPolicyBeneficiaries.policyBeneficiaries.policy.policyId.eq(policy.getPolicyId())
        );

        List<PolicyBeneficiaries> policyBeneficiariesList = new ArrayList<>();
        for (PolicyBeneficiaries beneficiary : policyBeneficiaries) {
            policyBeneficiariesList.add(beneficiary);
        }

        if (!policyBeneficiariesList.isEmpty()) {
            List<UltimateProtectorPolicyCreationRequest.BeneficiaryDto> beneficiaryDetailsList = new ArrayList<>();
            List<String> maleRelationships = Arrays.asList("Father", "Son", "Uncle", "Nephew", "Brother", "Grandson");

            for (PolicyBeneficiaries beneficiary : policyBeneficiariesList) {
                UltimateProtectorPolicyCreationRequest.BeneficiaryDto beneficiaryDetails = new UltimateProtectorPolicyCreationRequest.BeneficiaryDto();
                beneficiaryDetails.setBeneficiaryPortion(100); //beneficiary.getBenAllocation().intValue());
                beneficiaryDetails.setCitizenCountryID(7);
                beneficiaryDetails.setFirstName(beneficiary.getBeneficiaryName() != null && beneficiary.getBeneficiaryName().contains(" ") ? beneficiary.getBeneficiaryName().split(" ")[0] : "");
                beneficiaryDetails.setRelationID(12);

                String title = "Miss";
                String gender = "F";
                if (maleRelationships.contains(beneficiary.getBeneficiaryRelationship())) {
                    title = "Mr";
                    gender = "M";
                }

                beneficiaryDetails.setTitle(title);
                beneficiaryDetails.setGender(gender);
                beneficiaryDetails.setInitials(beneficiary.getBeneficiaryName() != null && !beneficiary.getBeneficiaryName().isEmpty() ? String.valueOf(beneficiary.getBeneficiaryName().charAt(0)) : "");
                beneficiaryDetails.setSurname(beneficiary.getBeneficiaryName() != null && beneficiary.getBeneficiaryName().contains(" ") ? beneficiary.getBeneficiaryName().split(" ")[1] : beneficiary.getBeneficiaryName());
                beneficiaryDetails.setBirthDate(beneficiary.getDateRegistered() != null ? outputFormat.format(beneficiary.getDateRegistered()) : "");
                beneficiaryDetails.setIDNumber(beneficiary.getBeneficiaryregNo());
                beneficiaryDetails.setIDType(1);
                beneficiaryDetailsList.add(beneficiaryDetails);
            }

            request.setBeneficiary1(beneficiaryDetailsList.get(0));
            if (beneficiaryDetailsList.size() > 1) request.setBeneficiary1(beneficiaryDetailsList.get(1));
            if (beneficiaryDetailsList.size() > 2) request.setBeneficiary1(beneficiaryDetailsList.get(2));
            if (beneficiaryDetailsList.size() > 3) request.setBeneficiary1(beneficiaryDetailsList.get(3));
            if (beneficiaryDetailsList.size() > 4) request.setBeneficiary1(beneficiaryDetailsList.get(4));
        }

        ClientDef clientDef = policy.getClient();
        if (clientDef != null) {
            if(clientDef.getPinNo() == null){ throw new RuntimeException("Client's pin no. is missing."); }
            if(clientDef.getTown() == null || clientDef.getTown().getCtName() == null){ throw new RuntimeException("Client's town is required."); }
            if(clientDef.getDob() == null ) { throw new RuntimeException("Client's DOB is required."); }
            if(clientDef.getOtherNames() == null){ throw new RuntimeException("Client's Surname is required."); }
            if(clientDef.getClientTitle() == null || clientDef.getClientTitle().getTitleName()  == null){ throw new RuntimeException("Client's title is required."); }
            if(clientDef.getFname() ==  null){ throw new RuntimeException("Client's First name is required."); }
            if(clientDef.getPhonePrefix() == null || clientDef.getPhonePrefix().getPrefixName() == null || clientDef.getPhoneNo() == null){throw new RuntimeException("Client's Tel/cell is missing."); }
            if(clientDef.getIdNo() == null){ throw new RuntimeException("Client's Identification Number is not defined."); }
            if(clientDef.getGender() == null){ throw new RuntimeException("Client's  gender is not defined."); }
            if(clientDef.getPostalCodesDef() == null || clientDef.getPostalCodesDef().getZipCode() == null || clientDef.getTown() == null || clientDef.getTown().getCtName() == null ){ throw new RuntimeException("Client's postal/zip code is missing."); }


            clientDetails.setResidenceCountryID(116);
            clientDetails.setEmailID(clientDef.getEmailAddress());
            clientDetails.setPreferredMethodSMS(true);
            clientDetails.setPreferredMethodEmail(true);
            clientDetails.setPreferredMethodPost(false);
            clientDetails.setPreferredMethodCollect(false);
            clientDetails.setTelHome(null);
            clientDetails.setTelWork(null);
            clientDetails.setTelCell(clientDef.getPhonePrefix().getPrefixName() + clientDef.getPhoneNo());
            clientDetails.setAltTelCel("");
            clientDetails.setGrossSalary("3112");
            clientDetails.setMaritalStatusID(2);
            clientDetails.setClientGroupID(1);
            clientDetails.setPostalCode(clientDef.getPostalCodesDef().getZipCode());
            clientDetails.setGender(clientDef.getGender());
            clientDetails.setCompanyName("1");
            clientDetails.setIDNumber(clientDef.getIdNo());
            clientDetails.setIndustryCode("83110");
            clientDetails.setCitizenCountryID(116);
            clientDetails.setFirstName(clientDef.getFname());
            clientDetails.setEmployer("21321");
            clientDetails.setIdentificationTypeID(2);
            clientDetails.setTitle(clientDef.getClientTitle().getTitleName() );
            clientDetails.setInitials(String.valueOf(clientDef.getFname().charAt(0)));
            String[] otherNamesParts = clientDef.getOtherNames().split(" ");
            String surName = (otherNamesParts.length > 1) ? otherNamesParts[1] : otherNamesParts[0];
            clientDetails.setSurname(surName); //clientDef.getOtherNames().split(" ")[1]);
            clientDetails.setEmploymentStatusCode("I_11");
            clientDetails.setOccupation("16-0016");
            clientDetails.setSourceOfFundsID(30);
            clientDetails.setSourceOfIncomeID(27);
            clientDetails.setSourceOfIncomeOthers("");
            clientDetails.setResidentialAddress1(clientDef.getTown().getCtName());
            clientDetails.setResidentialAddress2(null);
            clientDetails.setResidentialAddress3("12");
            clientDetails.setResidentialAddress4("dcf");
            clientDetails.setResidentialCode("411033");
            if (clientDef.getDob() != null) {
                Date date = new Date(clientDef.getDob().getTime());
                LocalDate localDob = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate employmentDate = localDob.plusYears(18);
                Date dateEmployed = Date.from(employmentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                clientDetails.setEmploymentDate(outputFormat.format(dateEmployed));
                clientDetails.setBirthDate(outputFormat.format(clientDef.getDob()));
            }

            clientDetails.setPostalAddress1(clientDef.getTown().getCtName());
            clientDetails.setPostalAddress2("cdce");
            clientDetails.setPostalAddress3("");
            clientDetails.setPostalAddress4("");
            clientDetails.setCoReg("");
            clientDetails.setFax("");
            clientDetails.setURL("");
            clientDetails.setEmployeeNumber("");
            clientDetails.setSourceOfFundsOthers("");
            clientDetails.setScreeningStatusID(2);
            clientDetails.setScreeningDate("2025-03-01T05:43:51.747Z");
            clientDetails.setScreeningExpiryDate("2025-03-27T05:43:51.747Z");
            clientDetails.setScreeningComment("okay");
            request.setTaxPinNo(clientDef.getPinNo());
        } else{
            throw new RuntimeException("Client details not found.");
        }

        request.setClientDetails(clientDetails);

        //banking details
//        if (policyMiscInfo.getAccountNumber() == null) { throw new RuntimeException("Policy holder’s account number, is required."); }
//        if (policyMiscInfo.getAccountName() == null) { throw new RuntimeException("Policy holder’s account Name, is required."); }
//        if (policyMiscInfo.getBankId() == null) { throw new RuntimeException("Policy holder’s bank ID, is required."); }
//        if (policyMiscInfo.getBranchId() == null) { throw new RuntimeException("Policy holder’s bank branch ID, is required."); }
//        if (policyMiscInfo.getAccountType() == null) { throw new RuntimeException("Policy holder's account type ID, is required."); }
//        if (policyMiscInfo.getStrikeDay() == null) { throw new RuntimeException("Policy Strike day, is required."); }

        //SystemTransactions transactionsInfo = systemTransactionsRepo.
        policyBankingDetails.setCurrencyID(1); //SET TO 1
        policyBankingDetails.setAccountNumber("123456789"); //policyMiscInfo.getAccountNumber());
        String fullname = "";
        if(clientDef.getFname() != null){
            fullname = clientDef.getFname();
            if(clientDef.getOtherNames() != null){
                fullname = fullname +" "+ clientDef.getOtherNames();
            }
        }
        policyBankingDetails.setAccountName(fullname);//policyMiscInfo.getAccountName());
        policyBankingDetails.setDescription("");
        policyBankingDetails.setAccountMapRef("");
        policyBankingDetails.setBankID(2); //Integer.parseInt(policyMiscInfo.getBankId()));
        policyBankingDetails.setBankBranchID(191); //Integer.parseInt(policyMiscInfo.getBranchId()));
        policyBankingDetails.setAccountTypeID(2); //Integer.parseInt(policyMiscInfo.getAccountType()));
        policyBankingDetails.setSwiftCode("");
        policyBankingDetails.setStrikeDay(1); //Integer.parseInt(policyMiscInfo.getStrikeDay()));
        policyBankingDetails.setAdditionalRef("");
        policyBankingDetails.setPaymentMethodID(1);

        request.setPolicyBankingData(policyBankingDetails);
        boolean ahb = false;
        for (RiskTrans risk : risks) {
            Iterable<SectionTrans> sections = sectionRepo.findAll(QSectionTrans.sectionTrans.risk.riskId.eq(risk.getRiskId()));
            List<PremiumItemsBean> itemsBeen = new ArrayList<>();
            for (SectionTrans section : sections) {
                List<String> items = new ArrayList<>();
                double minPrem = 0;
                if (section.getPremRates() != null) {
                    PremRatesDef premRatesDef = section.getPremRates();
                    if (premRatesDef.getMinPremium() != null && premRatesDef.getMinPremium().compareTo(BigDecimal.ZERO) == 1) {
                        minPrem = premRatesDef.getMinPremium().doubleValue();
                    }
                }

                PremiumItemsBean premiumItemsBean = new PremiumItemsBean(section.getSection().getShtDesc(), section.getRate().doubleValue(),
                        section.getFreeLimit().doubleValue(), minPrem,
                        section.getAmount().doubleValue(), section.getSectId(), section.getDivFactor().doubleValue(),
                        section.getSection().getType().getCode(), section.getSection().getType().getOrder(), items, policy.getPolicyId());
                itemsBeen.add(premiumItemsBean);
            }
            for (PremiumItemsBean premiumItemsBean : itemsBeen) {
                if (premiumItemsBean.getPremiumId().equalsIgnoreCase("Accidental Hospitalization Benefit")) {
                    ahb = true;
                }
            }
        }
        request.setAccidentalHospitalisationBenefit(ahb);
        //request.setCoverOption(Integer.parseInt(policyMiscInfo.getCoverOption()));
        request.setCoverOption(1);
        request.setCoverPlan(policy.getSumInsured().intValue());
        request.setManualSumInsured(0);
        request.setTaxPinNo(policy.getClient().getPinNo());

        request.setAccidentalOnlyPlan(false);
        request.setMedicalUWQ1(1);
        request.setMedicalUWQ2(1);
        request.setMedicalUWQ3(1);

//        request.setApplicationSignDate(policy.getCoverFrom() != null ? outputFormat.format(policy.getCoverFrom()) : outputFormat.format(policy.getWefDate()));
//        request.setApplicationReceivedDate(policy.getCoverFrom() != null ? outputFormat.format(policy.getCoverFrom()) : outputFormat.format(policy.getWefDate()));
        request.setApplicationSignDate(outputFormat.format(new Date()));
        request.setApplicationReceivedDate(outputFormat.format(new Date()));
        System.out.println("CREATE UP POLICY PAYLOAD: " + new Gson().toJson(request));
        try {
            String paramValue = paramService.getParameterString("API_INTEGRATION_URL") + "/alak/createUltimateProtectorPolicy";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<?> requestEntity = new HttpEntity<>(request, headers);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(paramValue, requestEntity, String.class);
            JsonNode rootNode = objectMapper.readTree(responseEntity.getBody());

            log.info("Received UP creation response: {}", responseEntity.getBody());
            // Handle response based on 'Code'
            String responseCode = rootNode.get("Code").asText();

            if ("200".equals(responseCode)) {
                // Successful response,
                return rootNode.get("Value").asText();
            } else if ("001".equals(responseCode)) {
                // Handle missing fields error
                throw new RuntimeException("Required fields are missing: " + rootNode.get("Message").asText());
            }  else if ("007".equals(responseCode)) {
                // Handle session error
                throw new RuntimeException("Session creation failed: " + rootNode.get("Message").asText());
            }else {
                // Generic error handling for other codes
                throw new RuntimeException("API Error: " + rootNode.get("Message").asText());
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // Handle API errors related to client/server response issues
            throw new BadRequestException("ELAK Integration service has issues: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }  catch (JsonProcessingException  e) {
            throw new BadRequestException(e.getMessage());
        }
         catch (RestClientException ex) {
            throw new BadRequestException("ELAK Integration service has issues..."+ex.getMessage());
        }
    }

    public String createFPPPolicyAlak(PolicyTrans policy, Iterable<RiskTrans> risks) {
        FamilyProtectionPolicyCreationRequest request = new FamilyProtectionPolicyCreationRequest();
        FamilyProtectionPolicyCreationRequest.PolicyBasicDetailsDto policyBasicDetails = new FamilyProtectionPolicyCreationRequest.PolicyBasicDetailsDto();
        FamilyProtectionPolicyCreationRequest.ClientDetailsDto clientDetails = new FamilyProtectionPolicyCreationRequest.ClientDetailsDto();
        FamilyProtectionPolicyCreationRequest.PolicyBankingDataDto policyBankingDetails = new FamilyProtectionPolicyCreationRequest.PolicyBankingDataDto();

        PolicyMiscInfo policyMiscInfo = policyMiscInfoRepo.findByPolicyId(policy.getPolicyId());

        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

        int frequency = 0;
        String polFrequency = policy.getFrequency();
        if (polFrequency != null) {
            switch (polFrequency.toUpperCase()) {
                case "M":
                    frequency = 1;
                    break;
                case "Q":
                    frequency = 3;
                    break;
                case "S":
                    frequency = 6;
                    break;
                case "A":
                    frequency = 12;
                    break;
                case "SG":
                    frequency = 1;
                    break;
            }
        }

        policyBasicDetails.setPolicyTypeID(1);
        policyBasicDetails.setPolicyClassID(1);
        policyBasicDetails.setInceptionDate(policy.getCoverFrom() != null ? outputFormat.format(policy.getCoverFrom()) : "");
        policyBasicDetails.setFrequency(frequency);

        Integer branchId = getSafeParam("ALAK_BRANCH_ID", 1);
        Integer brokerId = getSafeParam("ALAK_BROKER_ID", 48);
        Integer brokerRefId = getSafeParam("ALAK_BROKER_REF_ID", 483);
        Integer salesChannel = getSafeParam("ALAK_SALES_CHANNEL", 417);
        policyBasicDetails.setBranchID(branchId);
        policyBasicDetails.setBrokerID(brokerId);
        policyBasicDetails.setBrokerRefID(brokerRefId);
        policyBasicDetails.setSalesChannel(salesChannel);

        request.setPolicyBasicDetails(policyBasicDetails);

        //request.setStaffId("");

        boolean staffFlag = !policy.getClient().getTenantType().getClientType().equalsIgnoreCase("I");
        request.setStaffDiscount(staffFlag);
        /*if (policyMiscInfo == null || policyMiscInfo.getInflationPercent() == null) {
            throw new RuntimeException("The policy is missing inflation percentage(%) or invalid.");
        }*/
        //request.setInflationPercentage(policyMiscInfo.getInflationPercent()); //(0%,5%, 10%)
        request.setInflationPercentage(0);

        Iterable<PolicyBeneficiaries> policyBeneficiaries = policyBeneficiariesRepo.findAll(
                QPolicyBeneficiaries.policyBeneficiaries.policy.policyId.eq(policy.getPolicyId())
        );

        List<PolicyBeneficiaries> policyBeneficiariesList = new ArrayList<>();
        for (PolicyBeneficiaries beneficiary : policyBeneficiaries) {
            policyBeneficiariesList.add(beneficiary);
        }

        if (!policyBeneficiariesList.isEmpty()) {
            List<FamilyProtectionPolicyCreationRequest.BeneficiaryDto> beneficiaryDetailsList = new ArrayList<>();
            List<String> maleRelationships = Arrays.asList("Father", "Son", "Uncle", "Nephew", "Brother", "Grandson");

            for (PolicyBeneficiaries beneficiary : policyBeneficiariesList) {
                FamilyProtectionPolicyCreationRequest.BeneficiaryDto beneficiaryDetails = new FamilyProtectionPolicyCreationRequest.BeneficiaryDto();
                beneficiaryDetails.setBeneficiaryPortion(beneficiary.getBenAllocation().intValue());
                beneficiaryDetails.setCitizenCountryID(7);
                beneficiaryDetails.setFirstName(beneficiary.getBeneficiaryName() != null && beneficiary.getBeneficiaryName().contains(" ") ? beneficiary.getBeneficiaryName().split(" ")[0] : "");
                beneficiaryDetails.setRelationID(12);

                String title = "Miss";
                String gender = "F";
                if (maleRelationships.contains(beneficiary.getBeneficiaryRelationship())) {
                    title = "Mr";
                    gender = "M";
                }

                beneficiaryDetails.setTitle(title);
                beneficiaryDetails.setGender(gender);
                beneficiaryDetails.setInitials(beneficiary.getBeneficiaryName() != null && !beneficiary.getBeneficiaryName().isEmpty() ? String.valueOf(beneficiary.getBeneficiaryName().charAt(0)) : "");
                beneficiaryDetails.setSurname(beneficiary.getBeneficiaryName() != null && beneficiary.getBeneficiaryName().contains(" ") ? beneficiary.getBeneficiaryName().split(" ")[1] : beneficiary.getBeneficiaryName());
                beneficiaryDetails.setBirthDate(beneficiary.getDateRegistered() != null ? outputFormat.format(beneficiary.getDateRegistered()) : "");
                beneficiaryDetails.setIDNumber(beneficiary.getBeneficiaryregNo());
                beneficiaryDetails.setIDType(1);
                beneficiaryDetailsList.add(beneficiaryDetails);
            }

            request.setBeneficiary(beneficiaryDetailsList.get(0));
            if (beneficiaryDetailsList.size() > 1) request.setBeneficiary(beneficiaryDetailsList.get(1));
            if (beneficiaryDetailsList.size() > 2) request.setBeneficiary(beneficiaryDetailsList.get(2));
            if (beneficiaryDetailsList.size() > 3) request.setBeneficiary(beneficiaryDetailsList.get(3));
            if (beneficiaryDetailsList.size() > 4) request.setBeneficiary(beneficiaryDetailsList.get(4));
        }

        ClientDef clientDef = policy.getClient();
        if (clientDef != null) {
            if(clientDef.getPinNo() == null){ throw new RuntimeException("Client's pin no. is missing."); }
            if(clientDef.getTown() == null || clientDef.getTown().getCtName() == null){ throw new RuntimeException("Client's town is required."); }
            if(clientDef.getDob() == null ) { throw new RuntimeException("Client's DOB is required."); }
            if(clientDef.getOtherNames() == null){ throw new RuntimeException("Client's Surname is required."); }
            if(clientDef.getClientTitle() == null || clientDef.getClientTitle().getTitleName()  == null){ throw new RuntimeException("Client's title is required."); }
            if(clientDef.getFname() ==  null){ throw new RuntimeException("Client's First name is required."); }
            if(clientDef.getPhonePrefix() == null || clientDef.getPhonePrefix().getPrefixName() == null || clientDef.getPhoneNo() == null){throw new RuntimeException("Client's Tel/cell is missing."); }
            if(clientDef.getIdNo() == null){ throw new RuntimeException("Client's Identification Number is not defined."); }
            if(clientDef.getGender() == null){ throw new RuntimeException("Client's  gender is not defined."); }
            if(clientDef.getPostalCodesDef() == null || clientDef.getPostalCodesDef().getZipCode() == null || clientDef.getTown() == null || clientDef.getTown().getCtName() == null ){ throw new RuntimeException("Client's postal/zip code is missing."); }

            clientDetails.setResidenceCountryID(116);
            clientDetails.setEmailID(clientDef.getEmailAddress());
            clientDetails.setPreferredMethodSMS(true);
            clientDetails.setPreferredMethodEmail(true);
            clientDetails.setPreferredMethodPost(false);
            clientDetails.setPreferredMethodCollect(false);
            clientDetails.setTelHome("null");
            clientDetails.setTelWork("null");
            clientDetails.setTelCell(clientDef.getPhonePrefix().getPrefixName() + clientDef.getPhoneNo());
            clientDetails.setAltTelCel("");
            clientDetails.setGrossSalary("3112");
            clientDetails.setMaritalStatusID(2); // Marital status id (1-Singe, 2- Married, 3-Divorced, 4- Widow)
            clientDetails.setClientGroupID(1);
            clientDetails.setPostalCode(clientDef.getPostalCodesDef().getZipCode());
            clientDetails.setGender(clientDef.getGender()); // Gender (M-Male, F-Female)
            clientDetails.setCompanyName("1");
            clientDetails.setIDNumber(clientDef.getIdNo());
            clientDetails.setIndustryCode("83110");
            clientDetails.setCitizenCountryID(116);
            clientDetails.setFirstName(clientDef.getFname());
            clientDetails.setEmployer("21321");
            clientDetails.setIdentificationTypeID(2); //  (1- identity number, 2 –National id number, 3- passport number)
            clientDetails.setTitle(clientDef.getClientTitle().getTitleName());
            clientDetails.setInitials(String.valueOf(clientDef.getFname().charAt(0)));
            String[] otherNamesParts = clientDef.getOtherNames().split(" ");
            String surName = (otherNamesParts.length > 1) ? otherNamesParts[1] : otherNamesParts[0];
            clientDetails.setSurname(surName); //clientDef.getOtherNames().split(" ")[1]);
            clientDetails.setEmploymentStatusCode("I_11");
            clientDetails.setOccupation("16-0016");
            clientDetails.setSourceOfFundsID(30);
            clientDetails.setSourceOfIncomeID(27);
            clientDetails.setSourceOfIncomeOthers("");
            clientDetails.setResidentialAddress1(clientDef.getTown().getCtName());
            clientDetails.setResidentialAddress2("null");
            clientDetails.setResidentialAddress3("12");
            clientDetails.setResidentialAddress4("dcf");
            clientDetails.setResidentialCode("411033");
            if (clientDef.getDob() != null) {
                Date date = new Date(clientDef.getDob().getTime());
                LocalDate localDob = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate employmentDate = localDob.plusYears(18);
                Date dateEmployed = Date.from(employmentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                clientDetails.setEmploymentDate(outputFormat.format(dateEmployed));
                clientDetails.setBirthDate(outputFormat.format(clientDef.getDob()));
            }

            clientDetails.setPostalAddress1(clientDef.getTown() == null ? "Nairobi" : clientDef.getTown().getCtName());
            clientDetails.setPostalAddress2("cdce");
            clientDetails.setPostalAddress3("");
            clientDetails.setPostalAddress4("");
            clientDetails.setCoReg("");
            clientDetails.setFax("");
            clientDetails.setURL("");
            clientDetails.setEmployeeNumber("");
            clientDetails.setSourceOfFundsOthers("");
            clientDetails.setScreeningStatusID(2);
            clientDetails.setScreeningDate("2025-03-01T05:43:51.747Z");
            clientDetails.setScreeningExpiryDate("2025-03-27T05:43:51.747Z");
            clientDetails.setScreeningComment("okay");
            request.setTaxPinNo(clientDef.getPinNo());
        }

        request.setClientDetails(clientDetails);

        //banking details
//        if (policyMiscInfo.getAccountNumber() == null) { throw new RuntimeException("Policy holder’s account number, is required."); }
//        if (policyMiscInfo.getAccountName() == null) { throw new RuntimeException("Policy holder’s account Name, is required."); }
//        if (policyMiscInfo.getBankId() == null) { throw new RuntimeException("Policy holder’s bank ID, is required."); }
//        if (policyMiscInfo.getBranchId() == null) { throw new RuntimeException("Policy holder’s bank branch ID, is required."); }
//        if (policyMiscInfo.getAccountType() == null) { throw new RuntimeException("Policy holder's account type ID, is required."); }
//        if (policyMiscInfo.getStrikeDay() == null) { throw new RuntimeException("Policy Strike day, is required."); }

        //SystemTransactions transactionsInfo = systemTransactionsRepo.
        policyBankingDetails.setCurrencyID(1); //SET TO 1
        policyBankingDetails.setAccountNumber("123456789"); //policyMiscInfo.getAccountNumber());
        String fullname = "";
        if(clientDef.getFname() != null){
            fullname = clientDef.getFname();
            if(clientDef.getOtherNames() != null){
                fullname = fullname +" "+ clientDef.getOtherNames();
            }
        }
        policyBankingDetails.setAccountName(fullname);//policyMiscInfo.getAccountName());
        policyBankingDetails.setDescription("");
        policyBankingDetails.setAccountMapRef("");
        policyBankingDetails.setBankID(2); //Integer.parseInt(policyMiscInfo.getBankId()));
        policyBankingDetails.setBankBranchID(191); //Integer.parseInt(policyMiscInfo.getBranchId()));
        policyBankingDetails.setAccountTypeID(2); //Integer.parseInt(policyMiscInfo.getAccountType()));
        policyBankingDetails.setSwiftCode("");
        policyBankingDetails.setStrikeDay(3); //Integer.parseInt(policyMiscInfo.getStrikeDay()));
        policyBankingDetails.setAdditionalRef("");
        policyBankingDetails.setPaymentMethodID(1);//1 – Direct Debit 3- EFT

        request.setPolicyBankingData(policyBankingDetails);
        boolean ahb = false;
        for (RiskTrans risk : risks) {
            Iterable<SectionTrans> sections = sectionRepo.findAll(QSectionTrans.sectionTrans.risk.riskId.eq(risk.getRiskId()));
            List<PremiumItemsBean> itemsBeen = new ArrayList<>();
            for (SectionTrans section : sections) {
                List<String> items = new ArrayList<>();
                double minPrem = 0;
                if (section.getPremRates() != null) {
                    PremRatesDef premRatesDef = section.getPremRates();
                    if (premRatesDef.getMinPremium() != null && premRatesDef.getMinPremium().compareTo(BigDecimal.ZERO) == 1) {
                        minPrem = premRatesDef.getMinPremium().doubleValue();
                    }
                }

                PremiumItemsBean premiumItemsBean = new PremiumItemsBean(section.getSection().getShtDesc(), section.getRate().doubleValue(),
                        section.getFreeLimit().doubleValue(), minPrem,
                        section.getAmount().doubleValue(), section.getSectId(), section.getDivFactor().doubleValue(),
                        section.getSection().getType().getCode(), section.getSection().getType().getOrder(), items, policy.getPolicyId());
                itemsBeen.add(premiumItemsBean);
            }
        }
        //request.setCoverOption(Integer.parseInt(policyMiscInfo.getCoverOption()));
        request.setCoverOption(1);
        request.setSumInsuredSelection(policy.getSumInsured().intValue());
        request.setManualSumInsured(0);
        request.setOptionalIncomeBenefit(false);

//        request.setApplicationSignDate(policy.getCoverFrom() != null ? outputFormat.format(policy.getCoverFrom()) : outputFormat.format(policy.getWefDate()));
//        request.setApplicationReceivedDate(policy.getCoverFrom() != null ? outputFormat.format(policy.getCoverFrom()) : outputFormat.format(policy.getWefDate()));
        request.setApplicationSignDate(outputFormat.format(new Date()));
        request.setApplicationReceivedDate(outputFormat.format(new Date()));
        System.out.println("CREATE FPP POLICY PAYLOAD: " + new Gson().toJson(request));
        try {
            String paramValue = paramService.getParameterString("API_INTEGRATION_URL") + "/alak/createFamilyProtectionPolicy";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<?> requestEntity = new HttpEntity<>(request, headers);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(paramValue, requestEntity, String.class);
            JsonNode rootNode = objectMapper.readTree(responseEntity.getBody());

            log.info("Received FPP creation response: {}", responseEntity.getBody());
            // Handle response based on 'Code'
            String responseCode = rootNode.get("Code").asText();

            if ("200".equals(responseCode)) {
                // Successful response,
                return rootNode.get("Value").asText();
            } else if ("001".equals(responseCode)) {
                // Handle missing fields error
                throw new RuntimeException("Required fields are missing: " + rootNode.get("Message").asText());
            }  else if ("007".equals(responseCode)) {
                // Handle session error
                throw new RuntimeException("Session creation failed: " + rootNode.get("Message").asText());
            }else {
                // Generic error handling for other codes
                throw new RuntimeException("API Error: " + rootNode.get("Message").asText());
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // Handle API errors related to client/server response issues
            throw new RuntimeException("API responded with error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (JsonProcessingException e) {
            // Handle JSON parsing errors
            throw new RuntimeException("JSON parsing error: " + e.getMessage(), e);
        } catch (BadRequestException e) {
            // Handle bad requests
            throw new RuntimeException("Bad request: " + e.getMessage(), e);
        } catch (RestClientException e) {
            // Handle other REST client errors
            throw new RuntimeException("REST client error: " + e.getMessage(), e);
        }

    }


    public String createEducationAlak(PolicyTrans policy, Iterable<RiskTrans> risks) throws BadRequestException {
        EducationPolicyCreationRequest request = new EducationPolicyCreationRequest();
        PolicyBasicDetails policyBasicDetails = new PolicyBasicDetails();
        ClientDetails clientDetails = new ClientDetails();
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

        int frequency = 0;
//        String polFrequency = policy.getFrequency();
//        if (polFrequency != null) {
//            switch (polFrequency.toUpperCase()) {
//                case "M":
//                    frequency = 1;
//                    break;
//                case "Q":
//                    frequency = 4;
//                    break;
//                case "S":
//                    frequency = 6;
//                    break;
//                case "A":
//                    frequency = 12;
//                    break;
//            }
//        }
//
        String polFrequency =policy.getFrequency();
        if (polFrequency.equalsIgnoreCase("M")) {
            frequency = 1;
        } else if (polFrequency.equalsIgnoreCase("Q")) {
            frequency = 3;
        } else if (polFrequency.equalsIgnoreCase("S")) {
            frequency = 6;
        } else if (polFrequency.equalsIgnoreCase("A")) {
            frequency = 12;
        } else if (polFrequency.equalsIgnoreCase("SG")) {
            frequency = 1;
        }

        policyBasicDetails.setPolicyTypeID(1);
        policyBasicDetails.setPolicyClassID(1);
        policyBasicDetails.setInceptionDate(policy.getCoverFrom() != null ? outputFormat.format(policy.getCoverFrom()) : "");
        policyBasicDetails.setFrequency(frequency);

        Integer branchId = getSafeParam("ALAK_BRANCH_ID", 1);
        Integer brokerId = getSafeParam("ALAK_BROKER_ID", 48);
        Integer brokerRefId = getSafeParam("ALAK_BROKER_REF_ID", 483);
        Integer salesChannel = getSafeParam("ALAK_SALES_CHANNEL", 417);
        policyBasicDetails.setBranchID(branchId);
        policyBasicDetails.setBrokerID(brokerId);
        policyBasicDetails.setBrokerRefID(brokerRefId);
        policyBasicDetails.setSalesChannel(salesChannel);

        request.setPolicyBasicDetails(policyBasicDetails);
        request.setStaffId("");
        boolean staffFlag = !policy.getClient().getTenantType().getClientType().equalsIgnoreCase("I");
        request.setStaffDiscount(staffFlag);
        request.setInflationPercentage(0);

        Iterable<PolicyBeneficiaries> policyBeneficiaries = policyBeneficiariesRepo.findAll(
                QPolicyBeneficiaries.policyBeneficiaries.policy.policyId.eq(policy.getPolicyId())
        );

        List<PolicyBeneficiaries> policyBeneficiariesList = new ArrayList<>();
        for (PolicyBeneficiaries beneficiary : policyBeneficiaries) {
            policyBeneficiariesList.add(beneficiary);
        }

        if (!policyBeneficiariesList.isEmpty()) {
            List<BeneficiaryDetails> beneficiaryDetailsList = new ArrayList<>();
            List<String> maleRelationships = Arrays.asList("Father", "Son", "Uncle", "Nephew", "Brother", "Grandson");

            for (PolicyBeneficiaries beneficiary : policyBeneficiariesList) {
                BeneficiaryDetails beneficiaryDetails = new BeneficiaryDetails();
                beneficiaryDetails.setBeneficiaryPortion(beneficiary.getBenAllocation().intValue());
                beneficiaryDetails.setCitizenCountryID(7);
                beneficiaryDetails.setFirstName(beneficiary.getBeneficiaryName() != null && beneficiary.getBeneficiaryName().contains(" ") ? beneficiary.getBeneficiaryName().split(" ")[0] : "");
                beneficiaryDetails.setRelationID(12);

                String title = "Miss";
                String gender = "F";
                if (maleRelationships.contains(beneficiary.getBeneficiaryRelationship())) {
                    title = "Mr";
                    gender = "M";
                }

                beneficiaryDetails.setTitle(title);
                beneficiaryDetails.setGender(gender);
                beneficiaryDetails.setInitials(beneficiary.getBeneficiaryName() != null && !beneficiary.getBeneficiaryName().isEmpty() ? String.valueOf(beneficiary.getBeneficiaryName().charAt(0)) : "");
                beneficiaryDetails.setSurName(beneficiary.getBeneficiaryName() != null && beneficiary.getBeneficiaryName().contains(" ") ? beneficiary.getBeneficiaryName().split(" ")[1] : beneficiary.getBeneficiaryName());
                beneficiaryDetails.setBirthDate(beneficiary.getDateRegistered() != null ? outputFormat.format(beneficiary.getDateRegistered()) : "");
                beneficiaryDetails.setIDNumber(beneficiary.getBeneficiaryregNo());
                beneficiaryDetailsList.add(beneficiaryDetails);
            }

            request.setBeneficiary1(beneficiaryDetailsList.get(0));
            if (beneficiaryDetailsList.size() > 1) request.setBeneficiary2(beneficiaryDetailsList.get(1));
            if (beneficiaryDetailsList.size() > 2) request.setBeneficiary3(beneficiaryDetailsList.get(2));
            if (beneficiaryDetailsList.size() > 3) request.setBeneficiary4(beneficiaryDetailsList.get(3));
            if (beneficiaryDetailsList.size() > 4) request.setBeneficiary5(beneficiaryDetailsList.get(4));
        }

        ClientDef clientDef = policy.getClient();

        if (clientDef != null) {
            if(clientDef.getPinNo() == null){ throw new RuntimeException("Client's pin no. is missing."); }
            if(clientDef.getTown() == null || clientDef.getTown().getCtName() == null){ throw new RuntimeException("Client's town is required."); }
            if(clientDef.getDob() == null ) { throw new RuntimeException("Client's DOB is required."); }
            if(clientDef.getOtherNames() == null){ throw new RuntimeException("Client's Surname is required."); }
            if(clientDef.getClientTitle() == null || clientDef.getClientTitle().getTitleName()  == null){ throw new RuntimeException("Client's title is required."); }
            if(clientDef.getFname() ==  null){ throw new RuntimeException("Client's First name is required."); }
            if(clientDef.getPhonePrefix() == null || clientDef.getPhonePrefix().getPrefixName() == null || clientDef.getPhoneNo() == null){throw new RuntimeException("Client's Tel/cell is missing."); }
            if(clientDef.getIdNo() == null){ throw new RuntimeException("Client's Identification Number is not defined."); }
            if(clientDef.getGender() == null){ throw new RuntimeException("Client's  gender is not defined."); }
            if(clientDef.getPostalCodesDef() == null || clientDef.getPostalCodesDef().getZipCode() == null || clientDef.getTown() == null || clientDef.getTown().getCtName() == null ){ throw new RuntimeException("Client's postal/zip code is missing."); }

            clientDetails.setResidenceCountryID(116);
            clientDetails.setEmailID(clientDef.getEmailAddress());
            clientDetails.setPreferredMethodSMS(true);
            clientDetails.setPreferredMethodEmail(true);
            clientDetails.setPreferredMethodPost(false);
            clientDetails.setPreferredMethodCollect(false);
            clientDetails.setTelHome(null);
            clientDetails.setTelWork(null);
            clientDetails.setTelCell(clientDef.getPhonePrefix().getPrefixName() + clientDef.getPhoneNo());
            clientDetails.setAltTelCel("");
            clientDetails.setGrossSalary(3112);
            clientDetails.setMaritalStatusID(2);
            clientDetails.setClientGroupID(1);
            clientDetails.setPostalCode(clientDef.getPostalCodesDef().getZipCode());
            clientDetails.setGender(clientDef.getGender());
            clientDetails.setCompanyName(1);
            clientDetails.setIDNumber(clientDef.getIdNo());
            clientDetails.setIndustryCode("83110");
            clientDetails.setCitizenCountryID(116);
            clientDetails.setFirstName(clientDef.getFname());
            clientDetails.setEmployer("21321");
            clientDetails.setIdentificationTypeID(2);
            clientDetails.setTitle(clientDef.getClientTitle().getTitleName() );
            clientDetails.setInitials(String.valueOf(clientDef.getFname().charAt(0)));
            String[] otherNamesParts = clientDef.getOtherNames().split(" ");
            String surName = (otherNamesParts.length > 1) ? otherNamesParts[1] : otherNamesParts[0];
            clientDetails.setSurname(surName); //clientDef.getOtherNames().split(" ")[1]);
            clientDetails.setEmploymentStatusCode("I_11");
            clientDetails.setOccupation("16-0016");
            clientDetails.setSourceOfFundsID(30);
            clientDetails.setSourceOfIncomeID("27");
            clientDetails.setResidentialAddress4("dcf");
            if (clientDef.getDob() != null) {
                Date date = new Date(clientDef.getDob().getTime());
                LocalDate localDob = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate employmentDate = localDob.plusYears(18);
                Date dateEmployed = Date.from(employmentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                clientDetails.setEmploymentDate(outputFormat.format(dateEmployed));
                clientDetails.setBirthDate(outputFormat.format(clientDef.getDob()));
            }
            clientDetails.setPostalAddress4("");
            clientDetails.setResidentialAddress1(clientDef.getTown().getCtName());
            clientDetails.setPostalAddress1(clientDef.getTown().getCtName());
            clientDetails.setResidentialAddress2("");
            clientDetails.setPostalAddress2("cdce");
            clientDetails.setResidentialAddress3("12");

            request.setTaxPinNo(clientDef.getPinNo());
        }

        request.setClientDetails(clientDetails);
        boolean ahb = false;
        for (RiskTrans risk : risks) {
            Iterable<SectionTrans> sections = sectionRepo.findAll(QSectionTrans.sectionTrans.risk.riskId.eq(risk.getRiskId()));
            List<PremiumItemsBean> itemsBeen = new ArrayList<>();
            for (SectionTrans section : sections) {
                List<String> items = new ArrayList<>();
                double minPrem = 0;
                if (section.getPremRates() != null) {
                    PremRatesDef premRatesDef = section.getPremRates();
                    if (premRatesDef.getMinPremium() != null && premRatesDef.getMinPremium().compareTo(BigDecimal.ZERO) == 1) {
                        minPrem = premRatesDef.getMinPremium().doubleValue();
                    }
                }

                PremiumItemsBean premiumItemsBean = new PremiumItemsBean(section.getSection().getShtDesc(), section.getRate().doubleValue(),
                        section.getFreeLimit().doubleValue(), minPrem,
                        section.getAmount().doubleValue(), section.getSectId(), section.getDivFactor().doubleValue(),
                        section.getSection().getType().getCode(), section.getSection().getType().getOrder(), items, policy.getPolicyId());
                itemsBeen.add(premiumItemsBean);
            }
            for (PremiumItemsBean premiumItemsBean : itemsBeen) {
                if (premiumItemsBean.getPremiumId().equalsIgnoreCase("Accidental Hospitalization Benefit")) {
                    ahb = true;
                }
            }
        }
        request.setAccidentalHospitalisationBenefit(ahb);
        request.setMedicalUWQ1(false);
        request.setMedicalUWQ2(false);
        //request.setMedicalUWQ3(false);
        request.setMedicalUWQ3(0);
        request.setMedicalUWQ4(false);
        request.setMedicalUWQ5(false);
        request.setSumAssuredAtEntry(policy.getSumInsured().intValue());
        request.setMaturityPeriod(4);// 4or 8
        request.setPremiumPaymentTerms(policy.getPolTerm());

//        request.setApplicationSignDate(policy.getCoverFrom() != null ? outputFormat.format(policy.getCoverFrom()) : outputFormat.format(policy.getWefDate()));
//        request.setApplicationReceivedDate(policy.getCoverFrom() != null ? outputFormat.format(policy.getCoverFrom()) : outputFormat.format(policy.getWefDate()));
        request.setApplicationSignDate(outputFormat.format(new Date()));
        request.setApplicationReceivedDate(outputFormat.format(new Date()));
        System.out.println("CREATE EDUCATION POLICY PAYLOAD: " + new Gson().toJson(request));
        try {
            String paramValue = paramService.getParameterString("API_INTEGRATION_URL") + "/alak/createEducationPolicy";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<?> requestEntity = new HttpEntity<>(request, headers);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(paramValue, requestEntity, String.class);
            JsonNode rootNode = objectMapper.readTree(responseEntity.getBody());
            if ("200".equals(rootNode.get("Code").asText())) {
                //policy.setClientPolNo(rootNode.get("Value").asText());
                return rootNode.get("Value").asText();
            } else {
                throw new RuntimeException(rootNode.get("Message").asText());
            }
        } catch (JsonProcessingException e) {
            throw new BadRequestException(e.getMessage());
        }
        catch (HttpServerErrorException ex) {
             throw new BadRequestException("ELAK Integration service has issues...Please contact Support for assistance");

        } catch (RestClientException ex) {
            // Catch all other errors (connection timeout, etc.)
            throw new BadRequestException("ELAK Integration service has issues..."+ex.getMessage());
        }
    }

    private BigDecimal sign(String type) {
        return ("C".equalsIgnoreCase(type) ? BigDecimal.ONE.multiply(BigDecimal.valueOf(-1)) : BigDecimal.ONE.multiply(BigDecimal.valueOf(1)));
    }

    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void authorizePolicy(Long polCode, User user) throws BadRequestException {
        PolicyTrans policy = policyRepo.findOne(polCode);
        if (policy == null) throw new BadRequestException("Invalid Policy to authorize");
        boolean cashBasis = policy.getInterfaceType() != null && "C".equalsIgnoreCase(policy.getInterfaceType());

        if (!"R".equalsIgnoreCase(policy.getAuthStatus())) {
            throw new BadRequestException("Can only authorize ready policies");
        }
        long riskcount = riskRepo.count(QRiskTrans.riskTrans.policy.policyId.eq(polCode));

        if (riskcount == 0) throw new BadRequestException("Cannot Authorize Transaction Without Risk Details");

        Iterable<RiskTrans> risks = riskRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(polCode));

        long checkCount = transChecksRepo.count(QTransChecks.transChecks.policyTrans.policyId.eq(polCode).and(QTransChecks.transChecks.authorised.eq("N")));

        if (checkCount > 0) {
            throw new BadRequestException("Cannot Authorize when there is unauthorized checks....");
        }
        BigDecimal prems = (policy.getPremium() == null) ? BigDecimal.ZERO : policy.getPremium();
        if (cashBasis && prems.compareTo(BigDecimal.ZERO) == 1) {
            if (cashBasisBalance(polCode).compareTo(BigDecimal.ZERO) > 0) {
                throw new BadRequestException("Cannot authorize the Transaction..This is a cash basis transaction with balance of " + cashBasisBalance(polCode) + ". Please receipt the transaction first");
            }
        }

        Date polWef = dateUtils.removeTime(policy.getWefDate());
        Date polWet = dateUtils.removeTime(policy.getWetDate());
        for (RiskTrans riskBean : risks) {
            long sectCount = sectionRepo.count(QSectionTrans.sectionTrans.risk.riskId.eq(riskBean.getRiskId()));
            if (sectCount == 0)
                throw new BadRequestException("Risk " + riskBean.getRiskShtDesc() + " has no sections..Cannot Authorize the transaction");

//            Iterable<RiskDocs> riskDocs = riskDocsRepo.findAll(QRiskDocs.riskDocs.risk.riskId.eq(riskBean.getRiskId()));
//            if(policy.getTransType()!=null && !"CN".equalsIgnoreCase(policy.getTransType())) {
//                for (RiskDocs riskDoc : riskDocs) {
//                    if (riskDoc.getCheckSum() == null || StringUtils.isBlank(riskDoc.getCheckSum())) {
//                        throw new BadRequestException(String.format("Cannot authorize policy without uploading documents for Risk %s", riskBean.getRiskShtDesc()));
//                    }
//                }
//            }

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

        if (policy.getFuturePrem() != null)
            if (policy.getFuturePrem().compareTo(BigDecimal.ZERO) == -1)
                throw new BadRequestException("Policy Future Annual Premium Cannot be negative...");

        String refNo = null;
        String debitCode = null;
        if (cashBasis && prems.compareTo(BigDecimal.ZERO) > 0) {
            SystemTransactionsTemp systemTransactionsTemp = systemTransactionsTempRepo.findOne(QSystemTransactionsTemp.systemTransactionsTemp.policy.policyId.eq(polCode));
            if (systemTransactionsTemp == null)
                throw new BadRequestException("Unable to authorize without payment of the installment premium");
            refNo = systemTransactionsTemp.getRefNo();
            debitCode = systemTransactionsTemp.getTransType();
        } else {
            Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("D");
            if (sequenceRepo.count(seqPredicate) == 0)
                throw new BadRequestException("Sequence for Debit Notes has not been defined");
            SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
            Long seqNumber = sequence.getNextNumber();
            String transType = policy.getTransType();
            if ("CO".equalsIgnoreCase(policy.getTransType())) {
                transType = policy.getPreviousTrans().getTransType();
            }
            if ("NB".equalsIgnoreCase(transType)) {
                transType = "NB";
            } else if ("RN".equalsIgnoreCase(transType)) {
                transType = "RN";
            }else if ("BU".equalsIgnoreCase(transType)) {
                transType = "BU";
            }
            else if ("CN".equalsIgnoreCase(transType)) {
                transType = "CN";
            }else {
                transType = "EN";
            }
            if (mappingRepo.count(QTransactionMapping.transactionMapping.transType.eq(transType)) == 0)
                throw new BadRequestException("Error getting Transaction Mapping Setups..Contact System Administrator");
            TransactionMapping mapping = mappingRepo.findOne(QTransactionMapping.transactionMapping.transType.eq(transType));
            refNo = ((prems.compareTo(BigDecimal.ZERO) >= 0) ? mapping.getDebitCode() : mapping.getCreditCode()) + String.format("%05d", seqNumber);
            debitCode = ((prems.compareTo(BigDecimal.ZERO) >= 0) ? mapping.getDebitCode() : mapping.getCreditCode());
            sequence.setLastNumber(seqNumber);
            sequence.setNextNumber(seqNumber + 1);
            sequenceRepo.save(sequence);
        }

        policy.setAuthBy(user);
        policy.setAuthStatus("A");
        policy.setAuthDate(new Date());
        if ("CN".equalsIgnoreCase(policy.getTransType())) {
            policy.setCurrentStatus("CN");
        }
        if ("CO".equalsIgnoreCase(policy.getTransType())) {
            Iterable<PolicyRemarks> policyRemarks = policyRemarksRepo.findAll(QPolicyRemarks.policyRemarks.policy.policyId.eq(policy.getPolicyId()));
            if (policyRemarks.spliterator().getExactSizeIfKnown() == 0) {
                throw new BadRequestException("Input Remarks first....");
            }
            policy.setCurrentStatus("CO");
        } else
            policy.setCurrentStatus("A");
        if ("CN".equalsIgnoreCase(policy.getTransType())) {
            policy.setCurrentStatus("CN");
        }
        policy.setRefNo(refNo);
        policyRepo.save(policy);

        if (!("NB".equalsIgnoreCase(policy.getTransType()) || "SP".equalsIgnoreCase(policy.getTransType()))) {
            PolicyTrans prevPolicy = policy.getPreviousTrans();
            if ("CO".equalsIgnoreCase(policy.getTransType()) && prevPolicy.getPreviousTrans().getPolicyId() != prevPolicy.getPolicyId()) {
                prevPolicy.setCurrentStatus(policy.getTransType());
                PolicyTrans prevPolicy1 = prevPolicy.getPreviousTrans();
                if (!prevPolicy1.getTransType().equalsIgnoreCase("CO")) {
                    prevPolicy1.setCurrentStatus("A");
                    policyRepo.save(prevPolicy1);
                }
            }
            else if ("CN".equalsIgnoreCase(policy.getTransType())){
                prevPolicy.setCurrentStatus(prevPolicy.getTransType());
            }
            else {
                prevPolicy.setCurrentStatus(policy.getTransType());
            }
            policyRepo.save(prevPolicy);
        }


        if ("RE".equalsIgnoreCase(policy.getPolRevStatus())) {
            PolicyTrans prevPolicy = policy.getPreviousTrans();
            prevPolicy.setCurrentStatus(policy.getTransType());
            policyRepo.save(prevPolicy);
        }


        if (transRepo.count(QSystemTrans.systemTrans.policy.policyId.eq(polCode).and(QSystemTrans.systemTrans.transAuthorised.eq("N"))) > 1) {
            throw new BadRequestException("More than Unauthorized Transactions for the Policy..Contact System Admin");
        }

        if (transRepo.count(QSystemTrans.systemTrans.policy.policyId.eq(polCode).and(QSystemTrans.systemTrans.transAuthorised.eq("N"))) == 0) {
            throw new BadRequestException("Not Transaction to Authorize..Restart the Endorsement");
        }

        accountsUtilities.validatePolicyAccounts(policy);

        SystemTrans transaction = transRepo.findOne(QSystemTrans.systemTrans.policy.policyId.eq(polCode).and(QSystemTrans.systemTrans.transAuthorised.eq("N")));
        transaction.setAuthBy(userUtils.getCurrentUser());
        transaction.setAuthDate(new Date());
        transaction.setTransAuthorised("Y");
        transRepo.save(transaction);
        BigDecimal basicPrem = (policy.getPremium() == null) ? BigDecimal.ZERO : policy.getPremium();
        BigDecimal extras = (policy.getExtras() == null) ? BigDecimal.ZERO : policy.getExtras();
        BigDecimal phcf = (policy.getPhcf() == null) ? BigDecimal.ZERO : policy.getPhcf();
        BigDecimal tl = (policy.getTrainingLevy() == null) ? BigDecimal.ZERO : policy.getTrainingLevy();
        BigDecimal sd = (policy.getStampDuty() == null) ? BigDecimal.ZERO : policy.getStampDuty();
        BigDecimal amountWithTaxes = basicPrem.add(extras).add(phcf).add(tl).add(sd);

        BigDecimal adminFeeAmt = BigDecimal.ZERO;
        if(policy.getAdminFeeApplicable()!=null && "Y".equalsIgnoreCase(policy.getAdminFeeApplicable())){
            if(policy.getAdminFeeAmt()!=null && policy.getAdminFeeVatAmt()!=null){
                adminFeeAmt = policy.getAdminFeeAmt().subtract(policy.getAdminFeeVatAmt());
            }
            else if(policy.getAdminFeeAmt()!=null && policy.getAdminFeeVatAmt()==null){
                adminFeeAmt = policy.getAdminFeeAmt();
            }
        }

        SystemTransactions savedAgentTrans = null;
//       if (policy.getPolicyId()!=null) {
//            throw new BadRequestException("Refund amount="+refundAmount );
//        }
        //Client Transaction
        if (basicPrem.compareTo(BigDecimal.ZERO) != 0) {
            String type = (amountWithTaxes.compareTo(BigDecimal.ZERO) == 1) ? "D" : "C";
            SystemTransactions trans = new SystemTransactions();
            trans.setAmount(basicPrem.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            trans.setAuthDate(new Date());
            trans.setAuthorised("Y");
            trans.setAdminFeeNet(adminFeeAmt.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
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
            trans.setRefNo(refNo);
            trans.setSd(sd.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            trans.setTl(tl.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            trans.setTransDate(new Date());
            trans.setValueDate(java.sql.Date.valueOf(HolidayUtils.getAdjustedTransactionDate(LocalDate.now())));
            trans.setTransdc((amountWithTaxes.compareTo(BigDecimal.ZERO) == 1) ? "D" : "C");
            trans.setTransType(debitCode); //Should not be hardcorded
            trans.setUserAuth(user.getUsername());
            trans.setWhtx(BigDecimal.ZERO);
            trans.setExtras(extras.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            trans.setPostedDate(new Date());
            trans.setPostedUser(user);
            trans.setTransaction(transaction);
            SystemTransactions savedClientTrans = sysTransRepo.save(trans);

            if(type.equalsIgnoreCase("C")) {
                Optional<SystemTransactions> clientTrans = Streamable.streamOf(sysTransRepo.findAll(QSystemTransactions.systemTransactions.policy.polNo.eq(policy.getPolNo())
                        .and(QSystemTransactions.systemTransactions.transdc.eq("D"))
                        .and(QSystemTransactions.systemTransactions.transType.in("NBD", "APD", "RND"))
                        .and(QSystemTransactions.systemTransactions.clientType.eq("C")))).findFirst();
                if(!clientTrans.isPresent()) {
                    throw new BadRequestException("Unable to get any debit transaction for this transaction...");
                }
//                if (!commissionPaymentsRepo.findUnauthCommissions(policy.getPolNo()).isEmpty()) {
//                    List<BigInteger> transs = commissionPaymentsRepo.findUnauthCommissions(policy.getPolNo());
//                    List<CommissionPayments> commissionPaymentsList = new ArrayList<>();
//                    for (BigInteger tran : transs) {
//                        CommissionPayments commissionPayments = commissionPaymentsRepo.findOne(tran.longValue());
//                        commissionPayments.setWithDrawn("Y");
//                        commissionPayments.setWithDrawnDate(new Date());
//                        commissionPayments.setWithDrawnBy(userUtils.getCurrentUser());
//                        commissionPaymentsList.add(commissionPayments);
//                    }
//                    commissionPaymentsRepo.save(commissionPaymentsList);
//                } else
                    if(!commissionPaymentsRepo.findauthCommissions(policy.getPolNo()).isEmpty()) {

                        if(policy.getCommAmt() .compareTo(BigDecimal.ZERO) > 0)
                     allocationService.saveSubMarketerWithoutGl(policy.getPolicyId(),policy.getMarketerAgentComm(),policy.getSubAgentComm(),transaction,clientTrans.get(),savedClientTrans);

                    final BigDecimal commAmt = (policy.getCommAmt() != null) ? policy.getCommAmt() : BigDecimal.ZERO;
                    final BigDecimal whtx = (policy.getWhtx() != null) ? policy.getWhtx() : BigDecimal.ZERO;
                    final CommissionPayments commissionPayments = new CommissionPayments();
                    commissionPayments.setProcessed("N");
                    commissionPayments.setDate(new Date());
                    commissionPayments.setCurrencies(policy.getTransCurrency());
                    commissionPayments.setWhtx(BigDecimal.valueOf(whtx.abs().doubleValue() * 1));
                    commissionPayments.setAmount(BigDecimal.valueOf(commAmt.abs().doubleValue() * -1));
                    commissionPayments.setNetAmount(BigDecimal.valueOf((commAmt.abs().subtract(whtx.abs())).doubleValue() * -1));
                    commissionPayments.setDebitTransaction(clientTrans.get());
                    commissionPayments.setCreditTransaction(savedClientTrans);
                    commissionPayments.setTransType("Commission");
                    commissionPayments.setProcessedBy(userUtils.getCurrentUser());
                    commissionPayments.setAuthorised("N");
                    commissionPaymentsRepo.save(commissionPayments);


                    if (policy.getAdminFeeAmt() != null && policy.getAdminFeeAmt().compareTo(BigDecimal.ZERO)!=0) {
                        final BigDecimal adminWhtx = (policy.getAdminFeeAmt() != null) ? policy.getAdminFeeAmt() : BigDecimal.ZERO;
                        final CommissionPayments adminCommissionPayments = new CommissionPayments();
                        adminCommissionPayments.setProcessed("N");
                        adminCommissionPayments.setDate(new Date());
                        adminCommissionPayments.setCurrencies(policy.getTransCurrency());
                        adminCommissionPayments.setWhtx(BigDecimal.valueOf(adminWhtx.abs().doubleValue() * 1));
                        adminCommissionPayments.setAmount(BigDecimal.valueOf(policy.getAdminFeeAmt().abs().doubleValue() * -1));
                        adminCommissionPayments.setNetAmount(BigDecimal.valueOf((policy.getAdminFeeAmt().abs().subtract(adminWhtx.abs())).doubleValue() * -1));
                        adminCommissionPayments.setDebitTransaction(clientTrans.get());
                        adminCommissionPayments.setCreditTransaction(savedClientTrans);
                        adminCommissionPayments.setTransType("Admin Fee");
                        adminCommissionPayments.setProcessedBy(userUtils.getCurrentUser());
                        adminCommissionPayments.setAuthorised("N");
                        commissionPaymentsRepo.save(commissionPayments);
                    }

                    if (policy.getSubAgentComm() != null && policy.getSubAgentComm().compareTo(BigDecimal.ZERO)!=0) {
                        final CommissionPayments commissionPaymentsSub = new CommissionPayments();
                        commissionPaymentsSub.setProcessed("N");
                        commissionPaymentsSub.setDate(new Date());
                        commissionPaymentsSub.setCurrencies(policy.getTransCurrency());
                        final BigDecimal whtmount = BigDecimal.ZERO;
                        final BigDecimal comAmt = policy.getSubAgentComm();
                        commissionPaymentsSub.setWhtx(BigDecimal.valueOf(whtmount.abs().doubleValue() * 1));
                        commissionPaymentsSub.setAmount(BigDecimal.valueOf(comAmt.abs().doubleValue() * -1));
                        commissionPaymentsSub.setNetAmount(BigDecimal.valueOf((comAmt.abs().subtract(whtmount.abs())).doubleValue() * -1));
                        commissionPaymentsSub.setDebitTransaction(clientTrans.get());
                        commissionPaymentsSub.setCreditTransaction(savedClientTrans);
                        commissionPaymentsSub.setTransType("Sub Agent Commission");
                        commissionPaymentsSub.setProcessedBy(userUtils.getCurrentUser());
                        commissionPaymentsSub.setAuthorised("N");
                        commissionPaymentsRepo.save(commissionPaymentsSub);
                    }

                    if (policy.getMarketerAgentComm() != null && policy.getMarketerAgentComm().compareTo(BigDecimal.ZERO)!=0) {
                        final CommissionPayments commissionPaymentsSub = new CommissionPayments();
                        commissionPaymentsSub.setProcessed("N");
                        commissionPaymentsSub.setDate(new Date());
                        commissionPaymentsSub.setCurrencies(policy.getTransCurrency());
                        final BigDecimal whtmount = BigDecimal.ZERO;
                        final BigDecimal comAmt = policy.getMarketerAgentComm();
                        commissionPaymentsSub.setWhtx(BigDecimal.valueOf(whtmount.abs().doubleValue() * 1));
                        commissionPaymentsSub.setAmount(BigDecimal.valueOf(comAmt.abs().doubleValue() * -1));
                        commissionPaymentsSub.setNetAmount(BigDecimal.valueOf((comAmt.abs().subtract(whtmount.abs())).doubleValue() * -1));
                        commissionPaymentsSub.setDebitTransaction(clientTrans.get());
                        commissionPaymentsSub.setCreditTransaction(savedClientTrans);
                        commissionPaymentsSub.setTransType("Sub Agent Commission");
                        commissionPaymentsSub.setProcessedBy(userUtils.getCurrentUser());
                        commissionPaymentsSub.setAuthorised("N");
                        commissionPaymentsRepo.save(commissionPaymentsSub);
                    }
                }
            }

            if ("CO".equalsIgnoreCase(policy.getTransType())) {
                String refno = policy.getPreviousTrans().getRefNo();
                System.out.println("Ref no " + refno);
                SystemTransactions clientTrans = sysTransRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(refno)
                        .and(QSystemTransactions.systemTransactions.transdc.eq("D"))
                        .and(QSystemTransactions.systemTransactions.transType.in("NBD", "APD", "RND"))
                        .and(QSystemTransactions.systemTransactions.clientType.eq("C")));
                allocationService.autoAllocateContra(savedClientTrans.getTransno(), clientTrans.getTransno());
            }

            boolean multiproduct = policyBindersRepo.count(QPolicyBinders.policyBinders.policyTrans.policyId.eq(policy.getPolicyId())) > 0;
            String agntSign = (basicPrem.compareTo(BigDecimal.ZERO) == -1) ? "D" : "C";
            if (multiproduct) {
                Iterable<PolicyBinders> policyBinders = policyBindersRepo.findAll(QPolicyBinders.policyBinders.policyTrans.policyId.eq(polCode));
                log.debug("finfding "+policyBinders);
                int counter = 0;
                for (PolicyBinders binders : policyBinders) {
                    log.info("binders "+binders);
                    counter++;
                    BigDecimal commamt = (binders.getCommission() == null) ? BigDecimal.ZERO : binders.getCommission();
                    BigDecimal whtx = (binders.getWhtx() == null) ? BigDecimal.ZERO : binders.getWhtx();
                    phcf = (binders.getPhcf() == null) ? BigDecimal.ZERO : binders.getPhcf();
                    tl = (binders.getTl() == null) ? BigDecimal.ZERO : binders.getTl();
                    sd = BigDecimal.ZERO;
                    BigDecimal agentAmt = (binders.getBasicPrem().abs().multiply(sign(agntSign)).add(phcf.abs().multiply(sign(agntSign))).
                            add(tl.abs().multiply(sign(agntSign))).subtract(commamt.abs().multiply(sign(agntSign))).add(whtx.abs().multiply(sign(agntSign))));
                    //Agent Transaction
                    SystemTransactions atrans = new SystemTransactions();
                    atrans.setAmount(binders.getBasicPrem().abs().multiply(sign(agntSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    atrans.setAuthDate(new Date());
                    atrans.setAuthorised("Y");
                    atrans.setBalance(agentAmt.setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    atrans.setBranch(policy.getBranch());
                    atrans.setClientType("A");
                    atrans.setControlAcc(binders.getBinder().getAccount().getShtDesc());
                    atrans.setAgent(binders.getBinder().getAccount());
                    atrans.setAdminFeeNet(adminFeeAmt.abs().multiply(sign(agntSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    atrans.setCommission(commamt.abs().multiply(sign(agntSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    atrans.setCurrRate(new BigDecimal(1));
                    atrans.setCurrency(policy.getTransCurrency());
                    atrans.setNarrations("Posting Agent Credit Note");
                    atrans.setNetAmount(agentAmt.setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    atrans.setOrigin("U");
                    atrans.setPhfund(phcf.abs().multiply(sign(agntSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    atrans.setPolicy(policy);
                    atrans.setRefNo(refNo + "/" + counter);
                    atrans.setSd(sd.abs().multiply(sign(agntSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    atrans.setTl(tl.abs().multiply(sign(agntSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    atrans.setTransDate(new Date());
                    atrans.setValueDate(java.sql.Date.valueOf(HolidayUtils.getAdjustedTransactionDate(LocalDate.now())));
                    atrans.setTransdc(agntSign);
                    atrans.setTransType(debitCode); //Should not be hardcorded
                    atrans.setUserAuth(user.getUsername());
                    atrans.setWhtx(whtx.abs().multiply(sign(agntSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    atrans.setExtras(extras.abs().multiply(sign(agntSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    atrans.setPostedDate(new Date());
                    atrans.setPostedUser(user);
                    atrans.setTransaction(transaction);
                    savedAgentTrans = sysTransRepo.save(atrans);
                }
                postUwTransactions(policy, transaction, BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,savedAgentTrans);
            } else {


                BigDecimal commamt = (policy.getCommAmt() == null) ? BigDecimal.ZERO : policy.getCommAmt();
                BigDecimal whtx = (policy.getWhtx() == null) ? BigDecimal.ZERO : policy.getWhtx();
                BigDecimal agentAmt = (basicPrem.abs().multiply(sign(agntSign)).add(extras.abs().multiply(sign(agntSign))).add(phcf.abs().multiply(sign(agntSign))).
                        add(tl.abs().multiply(sign(agntSign))).add(sd.abs().multiply(sign(agntSign))).subtract(commamt.abs().multiply(sign(agntSign))).add(whtx.abs().multiply(sign(agntSign))));
                //Agent Transaction
                SystemTransactions atrans = new SystemTransactions();
                atrans.setAmount(basicPrem.abs().multiply(sign(agntSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                atrans.setAuthDate(new Date());
                atrans.setAuthorised("Y");
                atrans.setBalance(agentAmt.setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                atrans.setBranch(policy.getBranch());
                atrans.setClientType("A");
                atrans.setControlAcc(policy.getAgent().getShtDesc());
                atrans.setAgent(policy.getAgent());
                atrans.setCommission(commamt.abs().multiply(sign(agntSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                atrans.setCurrRate(new BigDecimal(1));
                atrans.setCurrency(policy.getTransCurrency());
                atrans.setNarrations("Posting Agent Credit Note");
                atrans.setNetAmount(agentAmt.setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                atrans.setAdminFeeNet(adminFeeAmt.setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                atrans.setOrigin("U");
                atrans.setPhfund(phcf.abs().multiply(sign(agntSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                atrans.setPolicy(policy);
                atrans.setRefNo(refNo);
                atrans.setSd(sd.abs().multiply(sign(agntSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                atrans.setTl(tl.abs().multiply(sign(agntSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                atrans.setTransDate(new Date());
                atrans.setValueDate(java.sql.Date.valueOf(HolidayUtils.getAdjustedTransactionDate(LocalDate.now())));
                atrans.setTransdc(agntSign);
                atrans.setTransType(debitCode); //Should not be hardcorded
                atrans.setUserAuth(user.getUsername());
                atrans.setWhtx(whtx.abs().multiply(sign(agntSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                atrans.setExtras(extras.abs().multiply(sign(agntSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                atrans.setPostedDate(new Date());
                atrans.setPostedUser(user);
                atrans.setTransaction(transaction);
                savedAgentTrans = sysTransRepo.save(atrans);
                postUwTransactions(policy, transaction, commamt,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,savedAgentTrans);

            }
            if (!multiproduct && "CO".equalsIgnoreCase(policy.getTransType())) {
                String refno = policy.getPreviousTrans().getRefNo();
                SystemTransactions agentTrans = sysTransRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(refno)
                        .and(QSystemTransactions.systemTransactions.transdc.eq("C")));
                long authcount = auditRepo.count(QPaymentAudit.paymentAudit.transNo.transno.eq(agentTrans.getTransno()).and(QPaymentAudit.paymentAudit.posted.eq("Y")));
                if (authcount == 0)
                    allocationService.autoAllocateContra(agentTrans.getTransno(), savedAgentTrans.getTransno());

                else {
                    SystemTransactions clientTrans = sysTransRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(refno)
                            .and(QSystemTransactions.systemTransactions.transdc.eq("D")));
                    Iterable<ReceiptSettlementDetails> settlements = settlementRepo.findAll(QReceiptSettlementDetails.receiptSettlementDetails.drCr.eq("C")
                            .and(QReceiptSettlementDetails.receiptSettlementDetails.debit.transno.eq(clientTrans.getTransno())));

                    BigDecimal totalReceipted = BigDecimal.ZERO;
                    for (ReceiptSettlementDetails details : settlements) {
                        totalReceipted = totalReceipted.add(details.getAllocatedAmt());
                    }
                    double prorationRate = totalReceipted.doubleValue() / (agentTrans.getNetAmount().doubleValue());
                    BigDecimal prorata = new BigDecimal(prorationRate);
                    if (prorata.compareTo(BigDecimal.ONE) == 1)
                        throw new BadRequestException("Error Doing contra...Contact Admin to check the settlement details");
                    SystemTransactions revTrans = new SystemTransactions();
                    revTrans.setAmount(agentTrans.getAmount().abs().multiply(sign("D")).multiply(prorata).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    revTrans.setAuthDate(new Date());
                    revTrans.setAuthorised("Y");
                    revTrans.setBalance(agentTrans.getBalance().abs().multiply(sign("D")).multiply(prorata).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    revTrans.setBranch(policy.getBranch());
                    revTrans.setClientType("A");
                    revTrans.setControlAcc(agentTrans.getControlAcc());
                    revTrans.setAgent(agentTrans.getAgent());
                    revTrans.setCommission(agentTrans.getCommission().abs().multiply(sign("D")).multiply(prorata).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    revTrans.setSubAgentFee(agentTrans.getSubAgentFee().abs().multiply(sign("D")).multiply(prorata).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    if(agentTrans.getAdminFeeNet()!=null) {
                        revTrans.setAdminFeeNet(agentTrans.getAdminFeeNet().abs().multiply(sign("D")).multiply(prorata).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    }
                    revTrans.setCurrRate(new BigDecimal(1));
                    revTrans.setCurrency(policy.getTransCurrency());
                    revTrans.setNarrations("Posting Agent Debit Note");
                    revTrans.setNetAmount(agentTrans.getNetAmount().abs().multiply(sign("D")).multiply(prorata).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    revTrans.setOrigin("U");
                    revTrans.setPhfund(agentTrans.getPhfund().abs().multiply(sign("D")).multiply(prorata).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    revTrans.setPolicy(policy);
                    revTrans.setRefNo(refNo);
                    revTrans.setSd(agentTrans.getSd().abs().multiply(sign("D")).multiply(prorata).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    revTrans.setTl(agentTrans.getTl().abs().multiply(sign("D")).multiply(prorata).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    revTrans.setTransDate(new Date());
                    revTrans.setValueDate(java.sql.Date.valueOf(HolidayUtils.getAdjustedTransactionDate(LocalDate.now())));
                    revTrans.setTransdc("D");
                    revTrans.setTransType(debitCode); //Should not be hardcorded
                    revTrans.setUserAuth(user.getUsername());
                    revTrans.setWhtx(agentTrans.getWhtx().abs().multiply(sign("D")).multiply(prorata).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    revTrans.setExtras(agentTrans.getExtras().abs().multiply(sign("D")).multiply(prorata).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    revTrans.setTransaction(transaction);
                    sysTransRepo.save(revTrans);
                }
            }

        }

        if (cashBasis && prems.compareTo(BigDecimal.ZERO) == 1) {
            allocationService.allocateCashBasisTrans(polCode, user);
        }
        boolean medicalProduct = false;
        if (policy.getProduct().getProGroup().getPrgType() == null || !policy.getProduct().getProGroup().getPrgType().equalsIgnoreCase("MD")) {
            medicalProduct = false;
        } else if (policy.getProduct().getProGroup().getPrgType().equalsIgnoreCase("MD")) {
            medicalProduct = true;
        }
        try {
            Map<String, Object> processVariables = Maps.newHashMap();
            processVariables.put("confirmAuth", true);
            workflowService.completeTask(String.valueOf(polCode), processVariables, policy, DocType.GEN_UW_DOCUMENT, (medicalProduct) ? "Y" : "N", null, null, null, null);
        } catch (Exception e) {
            throw new BadRequestException("Authorize Checks first....");
        }
        if(!policy.getTransType().equalsIgnoreCase("BU")) {
            MakerChecker makerChecker = makerCheckerRepo.findOne(QMakerChecker.makerChecker.policyId.eq(polCode).and(QMakerChecker.makerChecker.taskType.equalsIgnoreCase("ANP")).and(QMakerChecker.makerChecker.status.equalsIgnoreCase("N")));
            makerCheckerService.approveTask(makerChecker.getId());
        }
    }

    @PreAuthorize("hasAnyAuthority('AUTHORIZE_POLICY')")
    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void authorizeBulkUploadPolicies(Long polCode) throws BadRequestException {
        PolicyTrans policyTrans = policyRepo.findOne(polCode);
        if (policyTrans.getCurrentStatus().equalsIgnoreCase("A")) {
            throw new BadRequestException("This transaction is already approved");
        }
        Long makerId = policyTrans.getCreatedUser().getId();
        Long checkerId = userUtils.getCurrentUser().getId();

        if (makerId.equals(checkerId)) {
            throw new BadRequestException("You can't approve a task you've initiated");
        }

        if(!policyTrans.getBusinessType().equalsIgnoreCase("N")) { // for life checker
            boolean cashBasis = policyTrans.getInterfaceType() != null && "C".equalsIgnoreCase(policyTrans.getInterfaceType());
            BigDecimal prems = (policyTrans.getPremium() == null) ? BigDecimal.ZERO : policyTrans.getPremium();
            if (cashBasis && prems.compareTo(BigDecimal.ZERO) == 1) {
                if (cashBasisBalance(polCode).compareTo(BigDecimal.ZERO) > 0) {
                    throw new BadRequestException("Cannot authorize the Transaction..This is a cash basis transaction with balance of " + cashBasisBalance(polCode) + ". Please receipt the transaction first");
                }
            }
        }

        //if(policyTrans.getProduct().getProGroup().getPrgType().equalsIgnoreCase("L")) {

            //handle life policies
            //            String refNo = null;
            //            BigDecimal prems = (policyTrans.getPremium() == null) ? BigDecimal.ZERO : policyTrans.getPremium();
            //            Predicate pedSystem = QSystemSequence.systemSequence.transType.eq("D");
            //            if (sequenceRepo.count(pedSystem) == 0)
            //                throw new BadRequestException("Sequence for Debit Notes has not been defined");
            //            SystemSequence sequenceSystem = sequenceRepo.findOne(pedSystem);
            //            Long sequenceNumber = sequenceSystem.getNextNumber();
            //            String transType = policyTrans.getTransType();
            //            if ("CO".equalsIgnoreCase(policyTrans.getTransType())) {
            //                transType = policyTrans.getPreviousTrans().getTransType();
            //            }
            //            if ("NB".equalsIgnoreCase(transType)) {
            //                transType = "NB";
            //            } else if ("RN".equalsIgnoreCase(transType)) {
            //                transType = "RN";
            //            } else if ("LD".equalsIgnoreCase(transType)) {
            //                transType = "LD";
            //            } else if ("BU".equalsIgnoreCase(transType)) {
            //                transType = "BU";
            //            } else {
            //                transType = "EN";
            //            }
            //            if (mappingRepo.count(QTransactionMapping.transactionMapping.transType.eq(transType)) == 0)
            //                throw new BadRequestException("Error getting Transaction Mapping Setups..Contact System Administrator");
            //
            //            TransactionMapping mapping = mappingRepo.findOne(QTransactionMapping.transactionMapping.transType.eq(transType));
            //            refNo = ((prems.compareTo(BigDecimal.ZERO) >= 0) ? mapping.getDebitCode() : mapping.getCreditCode()) + String.format("%05d", sequenceNumber);
            //            sequenceSystem.setLastNumber(sequenceNumber);
            //            sequenceSystem.setNextNumber(sequenceNumber + 1);
            //            sequenceRepo.save(sequenceSystem);

            //policyTrans.setAuthStatus("A");
            //policyTrans.setCurrentStatus("A");
            policyTrans.setAuthDate(new Date());
            policyTrans.setAuthBy(userUtils.getCurrentUser());
            //policyTrans.setRefNo(refNo);
            policyRepo.save(policyTrans);
            //System.out.println("changes the template to "+ refNo);
        //}else {
            // to handle general policies
            // authorizePolicy(polCode, userUtils.getCurrentUser());
            /*to handle bu for
            String refNo = null;
            String debitCode = null;
            BigDecimal prems = (policyTrans.getPremium() == null) ? BigDecimal.ZERO : policyTrans.getPremium();

            Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("D");
            if (sequenceRepo.count(seqPredicate) == 0)
                throw new BadRequestException("Sequence for Debit Notes has not been defined");
            SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
            Long seqNumber = sequence.getNextNumber();
            String transType = policyTrans.getTransType();
            if ("CO".equalsIgnoreCase(policyTrans.getTransType())) {
                transType = policyTrans.getPreviousTrans().getTransType();
            }
            if ("NB".equalsIgnoreCase(transType)) {
                transType = "NB";
            } else if ("RN".equalsIgnoreCase(transType)) {
                transType = "RN";
            } else if ("LD".equalsIgnoreCase(transType)) {
                transType = "LD";
            } else if ("BU".equalsIgnoreCase(transType)) {
                transType = "BU";
            } else {
                transType = "EN";
            }

            if (mappingRepo.count(QTransactionMapping.transactionMapping.transType.eq(transType)) == 0)
                throw new BadRequestException("Error getting Transaction Mapping Setups..Contact System Administrator");

            TransactionMapping mapping = mappingRepo.findOne(QTransactionMapping.transactionMapping.transType.eq(transType));
            refNo = ((prems.compareTo(BigDecimal.ZERO) >= 0) ? mapping.getDebitCode() : mapping.getCreditCode()) + String.format("%05d", seqNumber);
            //debitCode = ((prems.compareTo(BigDecimal.ZERO) >= 0) ? mapping.getDebitCode() : mapping.getCreditCode());
            sequence.setLastNumber(seqNumber);
            sequence.setNextNumber(seqNumber + 1);
            sequenceRepo.save(sequence);
            policyTrans.setRefNo(refNo);
            policyRepo.save(policyTrans);

            boolean newTrans = true;
            //save the transaction
            SystemTrans transaction = new SystemTrans();
            transaction.setDoneDate(new Date());
            transaction.setDoneBy(userUtils.getCurrentUser());
            transaction.setPolicy(policyTrans);
            transaction.setTransLevel("U");
            transaction.setTransCode("BUD"); //A way to setup and look up for transaction transcode
            transaction.setTransAuthorised("N");
            transRepo.save(transaction);
            */
        if(!policyTrans.getProduct().getProGroup().getPrgType().equalsIgnoreCase("L")) {
            uploadValidatorsUtils.makeGeneralUploadReady(polCode);
            this.authorizePolicy(polCode,userUtils.getCurrentUser());
        }


    }


    @PreAuthorize("hasAnyAuthority('AUTHORIZE_POLICY')")
    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void authorizeMedicalPolicy(Long polCode) throws BadRequestException {
        PolicyTrans policy = policyRepo.findOne(polCode);
        if (policy == null) throw new BadRequestException("Invalid Policy to authorize");

        if (!"R".equalsIgnoreCase(policy.getAuthStatus())) {
            throw new BadRequestException("Can only authorize ready policies");
        }
        if (!authLimits.checkAuthorizationLimits("AUTHORIZE_POLICY", policy.getBasicPrem())) {
            throw new BadRequestException("You have no rights to authorize the transaction...Check your authorization limits..");
        }

        if ("CR".equalsIgnoreCase(policy.getTransType())) {
            if (policy.getReissueCardFee().compareTo(BigDecimal.ZERO) == 0) {
                throw new BadRequestException("Cannot authorise...Reissue Charge fee is zero");
            }
        }

        long riskcount = categoryRepo.count(QMedicalCategory.medicalCategory.policy.policyId.eq(polCode));

        if (riskcount == 0) throw new BadRequestException("Cannot Authorize Transaction Without Category Details");

        if (policy.getFuturePrem() != null)
            if (policy.getFuturePrem().compareTo(BigDecimal.ZERO) == -1)
                throw new BadRequestException("Policy Future Annual Premium Cannot be negative...");

        long selfParamsCount = selfFundParamsRepo.count(QSelfFundParams.selfFundParams.policyTrans.policyId.eq(polCode));

        boolean selffundPolicy = (policy.getBinder().getFundBinder() != null && "Y".equalsIgnoreCase(policy.getBinder().getFundBinder()));

        if (selffundPolicy) {
            if (selfParamsCount == 0)
                throw new BadRequestException("Fund Parameters Record is Mandatory for Fund Transactions");
        }

        Iterable<MedicalCategory> risks = categoryRepo.findAll(QMedicalCategory.medicalCategory.policy.policyId.eq(polCode));
        for (MedicalCategory category : risks) {
            if (membersRepo.count(QCategoryMembers.categoryMembers.category.id.eq(category.getId())) == 0) {
                throw new BadRequestException("Cannot Authorize the policy...No Category Members Specified for " + category.getDesc());
            }
            Iterable<CategoryMembers> categoryMembers = membersRepo.findAll(QCategoryMembers.categoryMembers.category.id.eq(category.getId()));
            for (CategoryMembers categoryMember : categoryMembers) {
                Iterable<RiskDocs> riskDocs = riskDocsRepo.findAll(QRiskDocs.riskDocs.member.sectId.eq(categoryMember.getSectId()));
                for (RiskDocs riskDoc : riskDocs) {
                    if (riskDoc.getCheckSum() == null || StringUtils.isBlank(riskDoc.getCheckSum())) {
                        throw new BadRequestException(String.format("Cannot authorize policy without uploading documents for Member No %s", categoryMember.getMemberShipNo()));
                    }
                }
                if (!(categoryMember.getMemberStatus().equalsIgnoreCase("D"))) {
                    categoryMember.setMemberStatus("A");
                    membersRepo.save(categoryMember);
                }

            }

        }
        BigDecimal prems = (policy.getPremium() == null) ? BigDecimal.ZERO : policy.getPremium();
        BigDecimal reIssueCardFee = BigDecimal.ZERO;
        if ("CR".equalsIgnoreCase(policy.getTransType())) {
            prems = policy.getReissueCardFee();
            reIssueCardFee = (policy.getReissueCardFee() == null) ? BigDecimal.ZERO : policy.getReissueCardFee();
        }
        Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("D");
        if (sequenceRepo.count(seqPredicate) == 0)
            throw new BadRequestException("Sequence for Debit Notes has not been defined");
        SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
        Long seqNumber = sequence.getNextNumber();
        String transType = policy.getTransType();
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
        if (mappingRepo.count(QTransactionMapping.transactionMapping.transType.eq(transType)) == 0)
            throw new BadRequestException("Error getting Transaction Mapping Setups..Contact System Administrator");
        TransactionMapping mapping = mappingRepo.findOne(QTransactionMapping.transactionMapping.transType.eq(transType));
        final String refNo = ((prems.compareTo(BigDecimal.ZERO) >= 0) ? mapping.getDebitCode() : mapping.getCreditCode()) + String.format("%05d", seqNumber);
        final String debitCode = ((prems.compareTo(BigDecimal.ZERO) >= 0) ? mapping.getDebitCode() : mapping.getCreditCode());
        sequence.setLastNumber(seqNumber);
        sequence.setNextNumber(seqNumber + 1);
        sequenceRepo.save(sequence);
        if (!"CN".equalsIgnoreCase(policy.getTransType())) {
            cardsService.generateMedicalCards(polCode);
        }


        policy.setAuthBy(userUtils.getCurrentUser());
        policy.setAuthStatus("A");
        policy.setAuthDate(new Date());
        if ("CN".equalsIgnoreCase(policy.getTransType())) {
            policy.setCurrentStatus("CN");
        }
        if ("CO".equalsIgnoreCase(policy.getTransType())) {
            policy.setCurrentStatus("CO");
        } else
            policy.setCurrentStatus("A");
        policy.setRefNo(refNo);

        policyRepo.save(policy);

        if (!("NB".equalsIgnoreCase(policy.getTransType()) || "SP".equalsIgnoreCase(policy.getTransType()))) {
            PolicyTrans prevPolicy = policy.getPreviousTrans();
            prevPolicy.setCurrentStatus(policy.getTransType());
            policyRepo.save(prevPolicy);
        }

        if ("RE".equalsIgnoreCase(policy.getPolRevStatus())) {
            PolicyTrans prevPolicy = policy.getPreviousTrans();
            prevPolicy.setCurrentStatus(policy.getTransType());
            policyRepo.save(prevPolicy);
        }


        if (transRepo.count(QSystemTrans.systemTrans.policy.policyId.eq(polCode).and(QSystemTrans.systemTrans.transAuthorised.eq("N"))) > 1) {
            throw new BadRequestException("More than Unauthorized Transactions for the Policy..Contact System Admin");
        }

        if (transRepo.count(QSystemTrans.systemTrans.policy.policyId.eq(polCode).and(QSystemTrans.systemTrans.transAuthorised.eq("N"))) == 0) {
            throw new BadRequestException("Not Transaction to Authorize..Restart the Endorsement");
        }

        SystemTrans transaction = transRepo.findOne(QSystemTrans.systemTrans.policy.policyId.eq(polCode).and(QSystemTrans.systemTrans.transAuthorised.eq("N")));
        transaction.setAuthBy(userUtils.getCurrentUser());
        transaction.setAuthDate(new Date());
        transaction.setTransAuthorised("Y");
        transRepo.save(transaction);

        accountsUtilities.validatePolicyAccounts(policy);


        BigDecimal basicPrem = (policy.getPremium() == null) ? BigDecimal.ZERO : policy.getPremium();
        BigDecimal extras = (policy.getExtras() == null) ? BigDecimal.ZERO : policy.getExtras();
        BigDecimal phcf = (policy.getPhcf() == null) ? BigDecimal.ZERO : policy.getPhcf();
        BigDecimal tl = (policy.getTrainingLevy() == null) ? BigDecimal.ZERO : policy.getTrainingLevy();
        BigDecimal sd = (policy.getStampDuty() == null) ? BigDecimal.ZERO : policy.getStampDuty();
        BigDecimal serviceCharge = (policy.getServiceCharge() == null) ? BigDecimal.ZERO : policy.getServiceCharge();
        BigDecimal issueFee = (policy.getIssueCardFee() == null) ? BigDecimal.ZERO : policy.getIssueCardFee();
        BigDecimal reissueFee = (policy.getReissueCardFee() == null) ? BigDecimal.ZERO : policy.getReissueCardFee();
        BigDecimal vat = (policy.getVatAmount() == null) ? BigDecimal.ZERO : policy.getVatAmount();
        BigDecimal amountWithTaxes = basicPrem.add(extras).add(phcf).add(tl).add(sd).add(vat).add(serviceCharge).add(issueFee).add(reissueFee);
        String sign = (amountWithTaxes.compareTo(BigDecimal.ZERO) == 1) ? "D" : "C";
        //Client Transaction
        if (basicPrem.compareTo(BigDecimal.ZERO) != 0 || reIssueCardFee.compareTo(BigDecimal.ZERO) != 0) {
            SystemTransactions trans = new SystemTransactions();
            trans.setAmount(basicPrem.abs().multiply(sign(sign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            trans.setAuthDate(new Date());
            trans.setAuthorised("Y");
            trans.setBalance(amountWithTaxes.abs().multiply(sign(sign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            trans.setBranch(policy.getBranch());
            trans.setClientType("C");
            trans.setControlAcc(policy.getClient().getTenantNumber());
            trans.setClient(policy.getClient());
            trans.setCurrRate(new BigDecimal(1));
            trans.setCurrency(policy.getTransCurrency());
            trans.setNarrations("Posting client Debit Note");
            trans.setNetAmount(amountWithTaxes.abs().multiply(sign(sign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            trans.setOrigin("U");
            trans.setPhfund(phcf.abs().multiply(sign(sign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            trans.setPolicy(policy);
            trans.setRefNo(refNo);
            trans.setSd(sd.abs().multiply(sign(sign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            trans.setTl(tl.abs().multiply(sign(sign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            trans.setServiceCharge(serviceCharge.abs().multiply(sign(sign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            trans.setVatAmount(vat.abs().multiply(sign(sign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            trans.setIssueCardFee(issueFee.abs().multiply(sign(sign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            trans.setReIssueCardFee(reissueFee.abs().multiply(sign(sign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            trans.setTransDate(new Date());
            trans.setValueDate(java.sql.Date.valueOf(HolidayUtils.getAdjustedTransactionDate(LocalDate.now())));
            trans.setTransdc((amountWithTaxes.compareTo(BigDecimal.ZERO) == 1) ? "D" : "C");
            trans.setTransType(debitCode); //Should not be hardcorded
            trans.setUserAuth(userUtils.getCurrentUser().getUsername());
            trans.setWhtx(BigDecimal.ZERO);
            trans.setExtras(extras.abs().multiply(sign(sign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            trans.setPostedDate(new Date());
            trans.setPostedUser(userUtils.getCurrentUser());
            trans.setTransaction(transaction);
            SystemTransactions savedClientTrans = sysTransRepo.save(trans);
            if ("CO".equalsIgnoreCase(policy.getTransType())) {
                String refno = policy.getPreviousTrans().getRefNo();
                SystemTransactions clientTrans = sysTransRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(refno)
                        .and(QSystemTransactions.systemTransactions.transdc.eq("D")));
                allocationService.autoAllocateContra(savedClientTrans.getTransno(), clientTrans.getTransno());
            }

            String agSign = (basicPrem.compareTo(BigDecimal.ZERO) == -1) ? "D" : "C";
            BigDecimal commamt = (policy.getCommAmt() == null) ? BigDecimal.ZERO : policy.getCommAmt().abs().multiply(sign(agSign));
            BigDecimal whtx = (policy.getWhtx() == null) ? BigDecimal.ZERO : policy.getWhtx().abs().multiply(sign(agSign));
            BigDecimal agentAmt = (basicPrem.abs().multiply(sign(agSign)).add(extras.abs().multiply(sign(agSign))).add(phcf.abs().multiply(sign(agSign))).
                    add(tl.abs().multiply(sign(agSign))).add(sd.abs().multiply(sign(agSign))).subtract(commamt.abs().multiply(sign(agSign))).add(whtx.abs().multiply(sign(agSign)))
                    .add(vat.abs().multiply(sign(agSign))).add(serviceCharge.abs().multiply(sign(agSign))).add(reissueFee.abs().multiply(sign(agSign))).add(issueFee.abs().multiply(sign(agSign))));

            //Agent Transaction
            SystemTransactions atrans = new SystemTransactions();
            atrans.setAmount(basicPrem.abs().multiply(sign(agSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            atrans.setAuthDate(new Date());
            atrans.setAuthorised("Y");
            atrans.setBalance(agentAmt.abs().multiply(sign(agSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            atrans.setBranch(policy.getBranch());
            atrans.setClientType("A");
            atrans.setControlAcc(policy.getAgent().getShtDesc());
            atrans.setAgent(policy.getAgent());
            atrans.setCommission(commamt.abs().multiply(sign(agSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            atrans.setCurrRate(new BigDecimal(1));
            atrans.setCurrency(policy.getTransCurrency());
            atrans.setNarrations("Posting Agent Credit Note");
            atrans.setNetAmount(agentAmt.negate().setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            atrans.setOrigin("U");
            atrans.setPhfund(phcf.abs().multiply(sign(agSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            atrans.setPolicy(policy);
            atrans.setRefNo(refNo);
            atrans.setSd(sd.abs().multiply(sign(agSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            atrans.setTl(tl.abs().multiply(sign(agSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            trans.setServiceCharge(serviceCharge.abs().multiply(sign(agSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            trans.setVatAmount(vat.abs().multiply(sign(agSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            trans.setIssueCardFee(issueFee.abs().multiply(sign(agSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            trans.setReIssueCardFee(reissueFee.abs().multiply(sign(agSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            atrans.setTransDate(new Date());
            atrans.setValueDate(java.sql.Date.valueOf(HolidayUtils.getAdjustedTransactionDate(LocalDate.now())));
            atrans.setTransdc(agSign);
            atrans.setTransType(debitCode); //Should not be hardcorded
            atrans.setUserAuth(userUtils.getCurrentUser().getUsername());
            atrans.setWhtx(whtx.abs().multiply(sign(agSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            atrans.setExtras(extras.abs().multiply(sign(agSign)).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
            atrans.setPostedDate(new Date());
            atrans.setPostedUser(userUtils.getCurrentUser());
            atrans.setTransaction(transaction);
            SystemTransactions savedAgentTrans = sysTransRepo.save(atrans);

            postUwTransactions(policy, transaction, commamt,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,savedAgentTrans);

            if ("CO".equalsIgnoreCase(policy.getTransType())) {
                String refno = policy.getPreviousTrans().getRefNo();
                SystemTransactions agentTrans = sysTransRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(refno)
                        .and(QSystemTransactions.systemTransactions.transdc.eq("C")));
                long authcount = auditRepo.count(QPaymentAudit.paymentAudit.transNo.transno.eq(agentTrans.getTransno()).and(QPaymentAudit.paymentAudit.posted.eq("Y")));
                if (authcount == 0)
                    allocationService.autoAllocateContra(agentTrans.getTransno(), savedAgentTrans.getTransno());
                else {
                    SystemTransactions clientTrans = sysTransRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(refno)
                            .and(QSystemTransactions.systemTransactions.transdc.eq("D")));
                    Iterable<ReceiptSettlementDetails> settlements = settlementRepo.findAll(QReceiptSettlementDetails.receiptSettlementDetails.drCr.eq("C")
                            .and(QReceiptSettlementDetails.receiptSettlementDetails.debit.transno.eq(clientTrans.getTransno())));

                    BigDecimal totalReceipted = BigDecimal.ZERO;
                    for (ReceiptSettlementDetails details : settlements) {
                        totalReceipted = totalReceipted.add(details.getAllocatedAmt());
                    }
                    double prorationRate = totalReceipted.doubleValue() / (agentTrans.getNetAmount().doubleValue());
                    BigDecimal prorata = new BigDecimal(prorationRate);
                    if (prorata.compareTo(BigDecimal.ONE) == 1)
                        throw new BadRequestException("Error Doing contra...Contact Admin to check the settlement details");
                    SystemTransactions revTrans = new SystemTransactions();
                    revTrans.setAmount(agentTrans.getAmount().abs().multiply(sign("D")).multiply(prorata).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    revTrans.setAuthDate(new Date());
                    revTrans.setAuthorised("Y");
                    revTrans.setBalance(agentTrans.getBalance().abs().multiply(sign("D")).multiply(prorata).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    revTrans.setBranch(policy.getBranch());
                    revTrans.setClientType("A");
                    revTrans.setControlAcc(agentTrans.getControlAcc());
                    revTrans.setAgent(agentTrans.getAgent());
                    revTrans.setCommission(agentTrans.getCommission().abs().multiply(sign("D")).multiply(prorata).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    revTrans.setCurrRate(new BigDecimal(1));
                    revTrans.setCurrency(policy.getTransCurrency());
                    revTrans.setNarrations("Posting Agent Debit Note");
                    revTrans.setNetAmount(agentTrans.getNetAmount().abs().multiply(sign("D")).multiply(prorata).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    revTrans.setOrigin("U");
                    revTrans.setPhfund(agentTrans.getPhfund().abs().multiply(sign("D")).multiply(prorata).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    revTrans.setPolicy(policy);
                    revTrans.setRefNo(refNo);
                    revTrans.setSd(agentTrans.getSd().abs().multiply(sign("D")).multiply(prorata).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    revTrans.setTl(agentTrans.getTl().abs().multiply(sign("D")).multiply(prorata).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    revTrans.setTransDate(new Date());
                    revTrans.setValueDate(java.sql.Date.valueOf(HolidayUtils.getAdjustedTransactionDate(LocalDate.now())));
                    revTrans.setTransdc("D");
                    revTrans.setTransType(debitCode); //Should not be hardcorded
                    revTrans.setUserAuth(userUtils.getCurrentUser().getUsername());
                    revTrans.setWhtx(agentTrans.getWhtx().abs().multiply(sign("D")).multiply(prorata).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    revTrans.setExtras(agentTrans.getExtras().abs().multiply(sign("D")).multiply(prorata).setScale(policy.getTransCurrency().getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    revTrans.setTransaction(transaction);
                    sysTransRepo.save(revTrans);
                }
            }
        }
        boolean medicalProduct = false;
        if (policy.getProduct().getProGroup().getPrgType() == null || !policy.getProduct().getProGroup().getPrgType().equalsIgnoreCase("MD")) {
            medicalProduct = false;
        } else if (policy.getProduct().getProGroup().getPrgType().equalsIgnoreCase("MD")) {
            medicalProduct = true;
        }
        Map<String, Object> processVariables = Maps.newHashMap();
        processVariables.put("confirmAuth", true);
        workflowService.completeTask(String.valueOf(polCode), processVariables, policy, DocType.GEN_UW_DOCUMENT, (medicalProduct) ? "Y" : "N", null, null, null, null);
    }

    public  void postCommissions(PolicyTrans policy, SystemTrans transaction,BigDecimal polComm, BigDecimal polSubAgentComm, BigDecimal polMarketerAgentComm,
                                 BigDecimal adminFee,SystemTransactions transactions) throws BadRequestException {
        List<Object[]> polTransList = riskRepo.findPolicyTrans(policy.getPolicyId());
        ProductsDef product = policy.getProduct();
        if (product == null) throw new BadRequestException("Error getting policy product");
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<Long> subcodes = jdbcTemplate.query("SELECT ps_sub_code  FROM sys_brk_prod_subcls sbps  WHERE ps_pr_code  = ?", new Object[]{product.getProCode()}, new RowMapper<Long>() {
            @Override
            public Long mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getLong(1);
            }
        });
        if (subcodes.size() != 1) {
            throw new BadRequestException("Error getting sub classes");
        }
        if(polTransList.isEmpty()){
            throw new BadRequestException("Unable to get Sub class for this life policy. Please complete the mapping");
        }
        final Object[] pols = polTransList.get(0);
        //to be checked again later.....
       // Date postDate = accountsPostingUtilities.getDefaultDate(new Date(), policy.getBranch().getObId(), Long.parseLong(new SimpleDateFormat("yyyy").format(new Date())));
        Date postDate = new Date();
        List<GlTransactions> glTransactions = new ArrayList<>();
        final AccountDef accountDef = policy.getAgent();
                final Long subclassCode = ((BigInteger) pols[2]).longValue();

                if (adminFee != null && adminFee.compareTo(BigDecimal.ZERO) != 0) {
                    if (accountDef.getAdminReceivableAccount() == null) {
                        throw new BadRequestException("Unable to get Debit Account for Admin Fee..");

                    }
                    if (accountsUtilities.getGlCreditAccount(RevenueItems.AF, subclassCode) == null) {
                        throw new BadRequestException("Unable to get Credit Account for Admin Fee..");
                    }
                    GlTransactions debit = new GlTransactions();
                    debit.setAmount(adminFee.abs());
                    debit.setAuthDate(postDate);
                    debit.setbCuramount(adminFee.abs());
                    debit.setSystemTransactions(transactions);
                    debit.setBranch(policy.getBranch());
                    debit.setCurrency(policy.getTransCurrency());
                    debit.setGlAcc((adminFee.signum() == 1) ? accountDef.getAdminReceivableAccount() : accountsUtilities.getGlCreditAccount(RevenueItems.AF, subclassCode));
                    debit.setGldc("D");
                    debit.setTransaction(transaction);
                    debit.setTransLevel("U");
                    debit.setTrntCode(policy.getTransType());
                    debit.setGlYear(dateUtils.getUwYear(postDate));
                    debit.setGlMonth(dateUtils.getMonth(postDate));
                    debit.setNarration(String.format("Posting Admin Fee for Policy No: %s, Ref No: %s", policy.getPolNo(), policy.getPolRevNo()));
                    debit.setPolicyTrans(policy);
                    debit.setTransType("ADMNFEE");
                    glTransactions.add(debit);
                    GlTransactions credit = new GlTransactions();
                    credit.setAmount(adminFee.abs());
                    credit.setAuthDate(postDate);
                    credit.setbCuramount(adminFee.abs());
                    credit.setSystemTransactions(transactions);
                    credit.setBranch(policy.getBranch());
                    credit.setCurrency(policy.getTransCurrency());
                    credit.setGlAcc((adminFee.signum() == 1) ? accountsUtilities.getGlCreditAccount(RevenueItems.AF, subclassCode) : accountDef.getAdminReceivableAccount());
                    credit.setGldc("C");
                    credit.setTransaction(transaction);
                    credit.setTransLevel("U");
                    credit.setTrntCode(policy.getTransType());
                    credit.setGlYear(dateUtils.getUwYear(postDate));
                    credit.setGlMonth(dateUtils.getMonth(postDate));
                    credit.setNarration(String.format("Posting Admin Fee for Policy No: %s, Ref No: %s", policy.getPolNo(), policy.getPolRevNo()));
                    credit.setPolicyTrans(policy);
                    credit.setTransType("ADMNFEE");
                    glTransactions.add(credit);
                }

                if (polSubAgentComm != null && polSubAgentComm.compareTo(BigDecimal.ZERO) != 0) {
                    if (accountsUtilities.getGlDebitAccount(RevenueItems.SAC, subclassCode) == null) {
                        throw new BadRequestException("Unable to get Credit Account for Sub Agent Commission..");
                    }
                    if (accountsUtilities.getGlCreditAccount(RevenueItems.SAC, subclassCode) == null) {
                        throw new BadRequestException("Unable to get Credit Account for Sub Agent Commission..");
                    }
                    GlTransactions debit = new GlTransactions();
                    debit.setAmount(polSubAgentComm.abs());
                    debit.setAuthDate(postDate);
                    debit.setbCuramount(polSubAgentComm.abs());
                    debit.setSystemTransactions(transactions);
                    debit.setBranch(policy.getBranch());
                    debit.setCurrency(policy.getTransCurrency());
                    debit.setGlAcc((polSubAgentComm.signum() == 1) ? accountsUtilities.getGlDebitAccount(RevenueItems.SAC, subclassCode) : accountsUtilities.getGlCreditAccount(RevenueItems.SAC, subclassCode));
                    debit.setGldc("D");
                    debit.setTransaction(transaction);
                    debit.setTransLevel("U");
                    debit.setTrntCode(policy.getTransType());
                    debit.setGlYear(dateUtils.getUwYear(postDate));
                    debit.setGlMonth(dateUtils.getMonth(postDate));
                    debit.setNarration(String.format("Posting Sub Agent Comm for Policy No: %s, Ref No: %s", policy.getPolNo(), policy.getPolRevNo()));
                    debit.setPolicyTrans(policy);
                    debit.setTransType("SAGCOMM");
                    glTransactions.add(debit);
                    GlTransactions credit = new GlTransactions();
                    credit.setAmount(polSubAgentComm.abs());
                    credit.setAuthDate(postDate);
                    credit.setbCuramount(polSubAgentComm.abs());
                    credit.setBranch(policy.getBranch());
                    credit.setCurrency(policy.getTransCurrency());
                    credit.setGlAcc((polSubAgentComm.signum() == 1) ? accountsUtilities.getGlCreditAccount(RevenueItems.SAC, subclassCode) : accountsUtilities.getGlDebitAccount(RevenueItems.SAC, subclassCode));
                    credit.setGldc("C");
                    credit.setSystemTransactions(transactions);
                    credit.setTransaction(transaction);
                    credit.setTransLevel("U");
                    credit.setTrntCode(policy.getTransType());
                    credit.setGlYear(dateUtils.getUwYear(postDate));
                    credit.setGlMonth(dateUtils.getMonth(postDate));
                    credit.setNarration(String.format("Posting  Sub Agent Comm for Policy No: %s, Ref No: %s", policy.getPolNo(), policy.getPolRevNo()));
                    credit.setPolicyTrans(policy);
                    credit.setTransType("SAGCOMM");
                    glTransactions.add(credit);
                }
                if (polMarketerAgentComm != null && polMarketerAgentComm.compareTo(BigDecimal.ZERO) != 0) {
                    if (accountsUtilities.getGlCreditAccount(RevenueItems.SAC, subclassCode) == null) {
                        throw new BadRequestException("Unable to get Credit Account for Marketer Agent Commission..");
                    }
                    if (accountsUtilities.getGlCreditAccount(RevenueItems.SAC, subclassCode) == null) {
                        throw new BadRequestException("Unable to get Credit Account for Marketer Agent Commission..");
                    }
                    GlTransactions debit = new GlTransactions();
                    debit.setAmount(polMarketerAgentComm.abs());
                    debit.setAuthDate(postDate);
                    debit.setbCuramount(polMarketerAgentComm.abs());
                    debit.setBranch(policy.getBranch());
                    debit.setCurrency(policy.getTransCurrency());
                    debit.setGlAcc((polMarketerAgentComm.signum() == 1) ? accountsUtilities.getGlDebitAccount(RevenueItems.SAC, subclassCode) : accountsUtilities.getGlCreditAccount(RevenueItems.SAC, subclassCode));
                    debit.setGldc("D");
                    debit.setTransaction(transaction);
                    debit.setSystemTransactions(transactions);
                    debit.setTransLevel("U");
                    debit.setTrntCode(policy.getTransType());
                    debit.setGlYear(dateUtils.getUwYear(postDate));
                    debit.setGlMonth(dateUtils.getMonth(postDate));
                    debit.setNarration(String.format("Posting Marketer Agent Comm for Policy No: %s, Ref No: %s", policy.getPolNo(), policy.getPolRevNo()));
                    debit.setPolicyTrans(policy);
                    debit.setTransType("MRKCOMM");
                    glTransactions.add(debit);
                    GlTransactions credit = new GlTransactions();
                    credit.setAmount(polMarketerAgentComm.abs());
                    credit.setAuthDate(postDate);
                    credit.setSystemTransactions(transactions);
                    credit.setbCuramount(polMarketerAgentComm.abs());
                    credit.setBranch(policy.getBranch());
                    credit.setCurrency(policy.getTransCurrency());
                    credit.setGlAcc((polMarketerAgentComm.signum() == 1) ? accountsUtilities.getGlCreditAccount(RevenueItems.SAC, subclassCode) : accountsUtilities.getGlDebitAccount(RevenueItems.SAC, subclassCode));
                    credit.setGldc("C");
                    credit.setTransaction(transaction);
                    credit.setTransLevel("U");
                    credit.setTrntCode(policy.getTransType());
                    credit.setGlYear(dateUtils.getUwYear(postDate));
                    credit.setGlMonth(dateUtils.getMonth(postDate));
                    credit.setNarration(String.format("Posting  Marketer Agent Comm for Policy No: %s, Ref No: %s", policy.getPolNo(), policy.getPolRevNo()));
                    credit.setPolicyTrans(policy);
                    credit.setTransType("MRKCOMM");
                    glTransactions.add(credit);
                }
                if (polComm != null && polComm.compareTo(BigDecimal.ZERO) != 0) {
                    if (accountDef.getCommReceivableAccount() == null) {
                        throw new BadRequestException("Unable to get Credit Account for Commission..");
                    }
                    if(accountsUtilities.getGlDebitAccount(RevenueItems.UC, subclassCode) ==null){
                        throw new BadRequestException("Unable to get Debit Account for commission..Check Revenue Item Configuration..");
                    }
                    GlTransactions debit = new GlTransactions();
                    debit.setAmount(polComm.abs());
                    debit.setAuthDate(postDate);
                    debit.setbCuramount(polComm.abs());
                    debit.setSystemTransactions(transactions);
                    debit.setBranch(policy.getBranch());
                    debit.setCurrency(policy.getTransCurrency());
                    debit.setGlAcc((polComm.signum() == -1) ?  accountsUtilities.getGlDebitAccount(RevenueItems.UC, subclassCode):accountDef.getCommReceivableAccount() );
                    debit.setGldc("D");
                    debit.setTransaction(transaction);
                    debit.setTransLevel("U");
                    debit.setTrntCode(policy.getTransType());
                    debit.setGlYear(dateUtils.getUwYear(postDate));
                    debit.setGlMonth(dateUtils.getMonth(postDate));
                    debit.setNarration(String.format("Posting Commission for Policy No: %s, Ref No: %s", policy.getPolNo(), policy.getPolRevNo()));
                    debit.setPolicyTrans(policy);
                    debit.setTransType("COMM");
                    glTransactions.add(debit);
                    GlTransactions credit = new GlTransactions();
                    credit.setAmount(polComm.abs());
                    credit.setAuthDate(postDate);
                    credit.setSystemTransactions(transactions);
                    credit.setbCuramount(polComm.abs());
                    credit.setBranch(policy.getBranch());
                    credit.setCurrency(policy.getTransCurrency());
                    credit.setGlAcc((polComm.signum() == -1) ? accountDef.getCommReceivableAccount():accountsUtilities.getGlDebitAccount(RevenueItems.UC, subclassCode) );
                    credit.setGldc("C");
                    credit.setTransaction(transaction);
                    credit.setTransLevel("U");
                    credit.setTrntCode(policy.getTransType());
                    credit.setGlYear(dateUtils.getUwYear(postDate));
                    credit.setGlMonth(dateUtils.getMonth(postDate));
                    credit.setNarration(String.format("Posting Commission for Policy No: %s, Ref No: %s", policy.getPolNo(), policy.getPolRevNo()));
                    credit.setPolicyTrans(policy);
                    credit.setTransType("COMM");
                    glTransactions.add(credit);


                }
        glTransRepo.save(glTransactions);
    }

    @Modifying
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = {BadRequestException.class})
    public void postUwTransactions(PolicyTrans policy, SystemTrans transaction, BigDecimal commamt,
                                   BigDecimal subagentComm, BigDecimal marketComm, BigDecimal adminFee,
                                   SystemTransactions transactions) throws BadRequestException {
        List<Object[]> polTransList = riskRepo.findPolicyTrans(policy.getPolicyId());
        ProductsDef product = policy.getProduct();
        if (product == null) throw new BadRequestException("Error getting policy product");
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<Long> subcodes = jdbcTemplate.query("SELECT ps_sub_code  FROM sys_brk_prod_subcls sbps  WHERE ps_pr_code  = ?", new Object[]{product.getProCode()}, new RowMapper<Long>() {
            @Override
            public Long mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getLong(1);
            }
        });
        if (subcodes.size() != 1) {
            throw new BadRequestException("Error getting sub classes");
        }
        //To be checked later
        //Date postDate = accountsPostingUtilities.getDefaultDate(new Date(), policy.getBranch().getObId(), Long.parseLong(new SimpleDateFormat("yyyy").format(new Date())));
        Date postDate = new Date();
        List<GlTransactions> glTransactions = new ArrayList<>();
        final AccountDef accountDef = policy.getAgent();

        final BigDecimal polPremium = policy.getBasicPrem();



        if (polPremium != null && polPremium.compareTo(BigDecimal.ZERO) != 0) {

            if(accountDef.getReceivableAccount()==null){
                    throw new BadRequestException("Unable to get Debit Account for Premium Account for .."+accountDef.getName());
            }
            if(accountDef.getPayableAccount()==null){
                throw new BadRequestException("Unable to get Credit Account for Premium Account for .."+accountDef.getName());
            }
            if (accountDef.getPayableAccount() != null && accountDef.getReceivableAccount() != null) {
                GlTransactions debit = new GlTransactions();
                debit.setAmount(polPremium.abs());
                debit.setSystemTransactions(transactions);
                debit.setAuthDate(postDate);
                debit.setbCuramount(polPremium.abs());
                debit.setBranch(policy.getBranch());
                debit.setCurrency(policy.getTransCurrency());
                debit.setGlAcc((polPremium.signum() == 1) ? accountDef.getReceivableAccount() : accountDef.getPayableAccount());
                debit.setGldc("D");
                debit.setTransaction(transaction);
                debit.setTransLevel("U");
                debit.setTrntCode(policy.getTransType());
                debit.setGlYear(dateUtils.getUwYear(postDate));
                debit.setGlMonth(dateUtils.getMonth(postDate));
                debit.setNarration(String.format("Posting premium for Policy No: %s, Ref No: %s", policy.getPolNo(), policy.getPolRevNo()));
                debit.setPolicyTrans(policy); // Setting the PolicyTrans reference
                debit.setTransType("PREM");
                glTransactions.add(debit);
                GlTransactions credit = new GlTransactions();
                credit.setAmount(polPremium.abs());
                credit.setAuthDate(postDate);
                credit.setbCuramount(polPremium.abs());
                credit.setSystemTransactions(transactions);
                credit.setBranch(policy.getBranch());
                credit.setCurrency(policy.getTransCurrency());
                credit.setGlAcc((polPremium.signum() == 1) ? accountDef.getPayableAccount() : accountDef.getReceivableAccount());
                credit.setGldc("C");
                credit.setTransaction(transaction);
                credit.setTransLevel("U");
                credit.setTrntCode(policy.getTransType());
                credit.setGlYear(dateUtils.getUwYear(postDate));
                credit.setGlMonth(dateUtils.getMonth(postDate));
                credit.setNarration(String.format("Posting premium for Policy No: %s, Ref No: %s", policy.getPolNo(), policy.getPolRevNo()));
                credit.setPolicyTrans(policy); // Setting the PolicyTrans reference
                credit.setTransType("PREM");
                glTransactions.add(credit);
            }

        }

        for (Object[] polTrans : polTransList) {
            final BigDecimal polSubAgentComm = (BigDecimal) polTrans[12];
            final BigDecimal polMarketerAgentComm = (BigDecimal) polTrans[14];
            final BigDecimal adminFees = (BigDecimal) polTrans[15];
            final BigDecimal adminFeeWhtx = (BigDecimal) polTrans[16];
            final BigDecimal polComm = (BigDecimal) polTrans[13];
            final BigDecimal polWhtx = (BigDecimal) polTrans[7];
            final BigDecimal phcf = (BigDecimal) polTrans[8];
            final BigDecimal tl = (BigDecimal) polTrans[9];
            final boolean lifePolicy = ("L".equalsIgnoreCase(policy.getProduct().getProGroup().getPrgType()));

            //LIFE PRODUCTS DONT HAVE TAXES
            if (!lifePolicy) {
                if (polPremium != null && polPremium.compareTo(BigDecimal.ZERO) != 0) {
                    if (phcf == null || phcf.compareTo(BigDecimal.ZERO) == 0) {
                        throw new BadRequestException("Please populate taxes to continue");
                    }
                    if (tl == null || tl.compareTo(BigDecimal.ZERO) == 0) {
                        throw new BadRequestException("Please populate taxes to continue");
                    }
                }
            }

            if (!lifePolicy) {
                final Long subclassCode = ((BigInteger) polTrans[2]).longValue();

                if (policy.getAdminFeeAmt() != null && policy.getAdminFeeAmt().compareTo(BigDecimal.ZERO) != 0) {
                    if (accountDef.getAdminReceivableAccount() == null) {
                        throw new BadRequestException("Unable to get Debit Account for Admin Fee..");

                    }
                    if (accountsUtilities.getGlCreditAccount(RevenueItems.AF, subclassCode) == null) {
                        throw new BadRequestException("Unable to get Credit Account for Admin Fee..");
                    }
                    GlTransactions debit = new GlTransactions();
                    debit.setAmount(policy.getAdminFeeAmt().abs());
                    debit.setAuthDate(postDate);
                    debit.setbCuramount(policy.getAdminFeeAmt().abs());
                    debit.setBranch(policy.getBranch());
                    debit.setCurrency(policy.getTransCurrency());
                    debit.setGlAcc((policy.getAdminFeeAmt().signum() == 1) ? accountDef.getAdminReceivableAccount() : accountsUtilities.getGlCreditAccount(RevenueItems.AF, subclassCode));
                    debit.setGldc("D");
                    debit.setTransaction(transaction);
                    debit.setTransLevel("U");
                    debit.setTrntCode(policy.getTransType());
                    debit.setGlYear(dateUtils.getUwYear(postDate));
                    debit.setSystemTransactions(transactions);
                    debit.setGlMonth(dateUtils.getMonth(postDate));
                    debit.setNarration(String.format("Posting Admin Fee for Policy No: %s, Ref No: %s", policy.getPolNo(), policy.getPolRevNo()));
                    debit.setPolicyTrans(policy);
                    debit.setTransType("ADMNFEE");
                    glTransactions.add(debit);
                    GlTransactions credit = new GlTransactions();
                    credit.setAmount(policy.getAdminFeeAmt().abs());
                    credit.setAuthDate(postDate);
                    credit.setbCuramount(policy.getAdminFeeAmt().abs());
                    credit.setBranch(policy.getBranch());
                    credit.setCurrency(policy.getTransCurrency());
                    credit.setGlAcc((policy.getAdminFeeAmt().signum() == 1) ? accountsUtilities.getGlCreditAccount(RevenueItems.AF, subclassCode) : accountDef.getAdminReceivableAccount());
                    credit.setGldc("C");
                    credit.setTransaction(transaction);
                    credit.setTransLevel("U");
                    credit.setSystemTransactions(transactions);
                    credit.setTrntCode(policy.getTransType());
                    credit.setGlYear(dateUtils.getUwYear(postDate));
                    credit.setGlMonth(dateUtils.getMonth(postDate));
                    credit.setNarration(String.format("Posting Admin Fee for Policy No: %s, Ref No: %s", policy.getPolNo(), policy.getPolRevNo()));
                    credit.setPolicyTrans(policy);
                    credit.setTransType("ADMNFEE");
                    glTransactions.add(credit);
                }

                if (polSubAgentComm != null && polSubAgentComm.compareTo(BigDecimal.ZERO) != 0) {
                    if (accountsUtilities.getGlDebitAccount(RevenueItems.SAC, subclassCode) == null) {
                        throw new BadRequestException("Unable to get Credit Account for Sub Agent Commission..");
                    }
                    if (accountsUtilities.getGlCreditAccount(RevenueItems.SAC, subclassCode) == null) {
                        throw new BadRequestException("Unable to get Credit Account for Sub Agent Commission..");
                    }
                    GlTransactions debit = new GlTransactions();
                    debit.setAmount(polSubAgentComm.abs());
                    debit.setAuthDate(postDate);
                    debit.setbCuramount(polSubAgentComm.abs());
                    debit.setBranch(policy.getBranch());
                    debit.setCurrency(policy.getTransCurrency());
                    debit.setGlAcc((polSubAgentComm.signum() == 1) ? accountsUtilities.getGlDebitAccount(RevenueItems.SAC, subclassCode) : accountsUtilities.getGlCreditAccount(RevenueItems.SAC, subclassCode));
                    debit.setGldc("D");
                    debit.setSystemTransactions(transactions);
                    debit.setTransaction(transaction);
                    debit.setTransLevel("U");
                    debit.setTrntCode(policy.getTransType());
                    debit.setGlYear(dateUtils.getUwYear(postDate));
                    debit.setGlMonth(dateUtils.getMonth(postDate));
                    debit.setNarration(String.format("Posting Sub Agent Comm for Policy No: %s, Ref No: %s", policy.getPolNo(), policy.getPolRevNo()));
                    debit.setPolicyTrans(policy);
                    debit.setTransType("SAGCOMM");
                    glTransactions.add(debit);
                    GlTransactions credit = new GlTransactions();
                    credit.setAmount(polSubAgentComm.abs());
                    credit.setAuthDate(postDate);
                    credit.setbCuramount(polSubAgentComm.abs());
                    credit.setBranch(policy.getBranch());
                    credit.setCurrency(policy.getTransCurrency());
                    credit.setGlAcc((polSubAgentComm.signum() == 1) ? accountsUtilities.getGlCreditAccount(RevenueItems.SAC, subclassCode) : accountsUtilities.getGlDebitAccount(RevenueItems.SAC, subclassCode));
                    credit.setGldc("C");
                    credit.setTransaction(transaction);
                    credit.setSystemTransactions(transactions);
                    credit.setTransLevel("U");
                    credit.setTrntCode(policy.getTransType());
                    credit.setGlYear(dateUtils.getUwYear(postDate));
                    credit.setGlMonth(dateUtils.getMonth(postDate));
                    credit.setNarration(String.format("Posting  Sub Agent Comm for Policy No: %s, Ref No: %s", policy.getPolNo(), policy.getPolRevNo()));
                    credit.setPolicyTrans(policy);
                    credit.setTransType("SAGCOMM");
                    glTransactions.add(credit);
                }
                if (polMarketerAgentComm != null && polMarketerAgentComm.compareTo(BigDecimal.ZERO) != 0) {
                    if (accountsUtilities.getGlCreditAccount(RevenueItems.SAC, subclassCode) == null) {
                        throw new BadRequestException("Unable to get Credit Account for Marketer Agent Commission..");
                    }
                    if (accountsUtilities.getGlCreditAccount(RevenueItems.SAC, subclassCode) == null) {
                        throw new BadRequestException("Unable to get Credit Account for Marketer Agent Commission..");
                    }
                    GlTransactions debit = new GlTransactions();
                    debit.setAmount(polMarketerAgentComm.abs());
                    debit.setAuthDate(postDate);
                    debit.setbCuramount(polMarketerAgentComm.abs());
                    debit.setBranch(policy.getBranch());
                    debit.setCurrency(policy.getTransCurrency());
                    debit.setGlAcc((polMarketerAgentComm.signum() == 1) ? accountsUtilities.getGlDebitAccount(RevenueItems.SAC, subclassCode) : accountsUtilities.getGlCreditAccount(RevenueItems.SAC, subclassCode));
                    debit.setGldc("D");
                    debit.setTransaction(transaction);
                    debit.setSystemTransactions(transactions);
                    debit.setTransLevel("U");
                    debit.setTrntCode(policy.getTransType());
                    debit.setGlYear(dateUtils.getUwYear(postDate));
                    debit.setGlMonth(dateUtils.getMonth(postDate));
                    debit.setNarration(String.format("Posting Marketer Agent Comm for Policy No: %s, Ref No: %s", policy.getPolNo(), policy.getPolRevNo()));
                    debit.setPolicyTrans(policy);
                    debit.setTransType("MRKCOMM");
                    glTransactions.add(debit);
                    GlTransactions credit = new GlTransactions();
                    credit.setAmount(polMarketerAgentComm.abs());
                    credit.setAuthDate(postDate);
                    credit.setbCuramount(polMarketerAgentComm.abs());
                    credit.setBranch(policy.getBranch());
                    credit.setCurrency(policy.getTransCurrency());
                    credit.setGlAcc((polMarketerAgentComm.signum() == 1) ? accountsUtilities.getGlCreditAccount(RevenueItems.SAC, subclassCode) : accountsUtilities.getGlDebitAccount(RevenueItems.SAC, subclassCode));
                    credit.setGldc("C");
                    credit.setTransaction(transaction);
                    credit.setTransLevel("U");
                    credit.setSystemTransactions(transactions);
                    credit.setTrntCode(policy.getTransType());
                    credit.setGlYear(dateUtils.getUwYear(postDate));
                    credit.setGlMonth(dateUtils.getMonth(postDate));
                    credit.setNarration(String.format("Posting  Marketer Agent Comm for Policy No: %s, Ref No: %s", policy.getPolNo(), policy.getPolRevNo()));
                    credit.setPolicyTrans(policy);
                    credit.setTransType("MRKCOMM");
                    glTransactions.add(credit);
                }
                if (commamt != null && commamt.compareTo(BigDecimal.ZERO) != 0) {
                    if (accountDef.getCommReceivableAccount() == null) {
                        throw new BadRequestException("Unable to get Credit Account for Commission..");
                    }
                    if(accountsUtilities.getGlDebitAccount(RevenueItems.UC, subclassCode) == null){
                        throw new BadRequestException("Unable to get Debit Account for Commission..");
                    }
                    GlTransactions debit = new GlTransactions();
                    debit.setAmount(polComm.abs());
                    debit.setAuthDate(postDate);
                    debit.setbCuramount(polComm.abs());
                    debit.setBranch(policy.getBranch());
                    debit.setCurrency(policy.getTransCurrency());
                    debit.setGlAcc((polComm.signum() == 1) ? accountsUtilities.getGlDebitAccount(RevenueItems.UC, subclassCode) : accountDef.getCommReceivableAccount());
                    debit.setGldc("D");
                    debit.setTransaction(transaction);
                    debit.setSystemTransactions(transactions);
                    debit.setTransLevel("U");
                    debit.setTrntCode(policy.getTransType());
                    debit.setGlYear(dateUtils.getUwYear(postDate));
                    debit.setGlMonth(dateUtils.getMonth(postDate));
                    debit.setNarration(String.format("Posting Commission for Policy No: %s, Ref No: %s", policy.getPolNo(), policy.getPolRevNo()));
                    debit.setPolicyTrans(policy);
                    debit.setTransType("COMM");
                    glTransactions.add(debit);
                    GlTransactions credit = new GlTransactions();
                    credit.setAmount(polComm.abs());
                    credit.setAuthDate(postDate);
                    credit.setbCuramount(polComm.abs());
                    credit.setBranch(policy.getBranch());
                    credit.setCurrency(policy.getTransCurrency());
                    credit.setGlAcc((polComm.signum() == 1) ? accountDef.getCommReceivableAccount() : accountsUtilities.getGlDebitAccount(RevenueItems.UC, subclassCode));
                    credit.setGldc("C");
                    credit.setTransaction(transaction);
                    credit.setSystemTransactions(transactions);
                    credit.setTransLevel("U");
                    credit.setTrntCode(policy.getTransType());
                    credit.setGlYear(dateUtils.getUwYear(postDate));
                    credit.setGlMonth(dateUtils.getMonth(postDate));
                    credit.setNarration(String.format("Posting Commission for Policy No: %s, Ref No: %s", policy.getPolNo(), policy.getPolRevNo()));
                    credit.setPolicyTrans(policy);
                    credit.setTransType("COMM");
                    glTransactions.add(credit);
                }
            }
        }
        glTransRepo.save(glTransactions);
    }

    @Override
    public BigDecimal cashBasisBalance(Long polCode) {
        SystemTransactionsTemp systemTransactionsTemp = systemTransactionsTempRepo.findOne(QSystemTransactionsTemp.systemTransactionsTemp.policy.policyId.eq(polCode));
        if (systemTransactionsTemp != null && systemTransactionsTemp.getBalance().compareTo(BigDecimal.ZERO) == 1) {
            return systemTransactionsTemp.getBalance();
        }
        return BigDecimal.ZERO;
    }
    @PreAuthorize("hasAnyAuthority('VERIFY_RISK_DOCS')")
    @Modifying
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void saveRiskDocComment(Long rdId, String comments) throws BadRequestException {
        RiskDocs riskDoc = riskDocsRepo.getRiskDocsVal(rdId);
        if (riskDoc == null) {
            throw new BadRequestException("Invalid Document to comment");
        }

        if (comments != null && !comments.trim().isEmpty()) {
            riskDoc.setComments(comments.trim());
            riskDocsRepo.save(riskDoc);
        }
    }
    @PreAuthorize("hasAnyAuthority('VERIFY_RISK_DOCS')")
    @Modifying
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    @Override
    public void verifyRiskDoc(Long rdId) throws BadRequestException {
        RiskDocs riskDoc = riskDocsRepo.getRiskDocsVal(rdId);
        if (riskDoc == null) {
            throw new BadRequestException("Invalid Document to verify");
        }

        //  Check if a document was uploaded
        if (riskDoc.getUploadedFileName() == null || riskDoc.getUploadedFileName().isEmpty()) {
            throw new BadRequestException ("No document has been uploaded for verification.");
        }

        User currentUser = userUtils.getCurrentUser();

        if (currentUser.getUsername().equalsIgnoreCase(riskDoc.getInitiator())) {
            throw new BadRequestException("You cannot verify a document you uploaded.");
        }

        riskDoc.setVerifiedBy(currentUser.getUsername());
        riskDoc.setVerifiedDate(new Date());

        riskDocsRepo.save(riskDoc);
    }


    private Integer getSafeParam(String paramName, int defaultValue) {
        try {
            return paramService.getParamInt(paramName);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
