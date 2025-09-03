package com.brokersystems.brokerapp.users.service.impl;

import com.brokersystems.brokerapp.accounts.model.QRefunds;
import com.brokersystems.brokerapp.accounts.model.Refunds;
import com.brokersystems.brokerapp.accounts.repository.RefundRepo;
import com.brokersystems.brokerapp.accounts.service.AccountsService;
import com.brokersystems.brokerapp.auditlogs.model.RtsAudit;
import com.brokersystems.brokerapp.auditlogs.repositories.RtsAuditRepository;
import com.brokersystems.brokerapp.claims.dtos.ClaimDetailsDTO;
import com.brokersystems.brokerapp.claims.model.ClaimBookings;
import com.brokersystems.brokerapp.claims.model.ClaimForm;
import com.brokersystems.brokerapp.claims.repository.ClaimRequiredDocsRepo;
import com.brokersystems.brokerapp.claims.repository.ClaimsBookingRepo;
import com.brokersystems.brokerapp.integrations.apa.APAIntergrationService;
import com.brokersystems.brokerapp.mail.model.MailMessageBean;
import com.brokersystems.brokerapp.mail.model.SendEmailPublisherBean;
import com.brokersystems.brokerapp.mail.service.Mailer;
import com.brokersystems.brokerapp.quotes.dto.CreateQuoteDTO;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.DateUtilities;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.setup.dto.ClientDTO;
import com.brokersystems.brokerapp.setup.dto.UserDTO;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.EscalationActivityLoggerRepository;
import com.brokersystems.brokerapp.setup.repository.EscalationRecordRepository;
import com.brokersystems.brokerapp.setup.repository.UserRepository;
import com.brokersystems.brokerapp.setup.service.SetupsService;
import com.brokersystems.brokerapp.setup.service.impl.EscalationRecordService;
import com.brokersystems.brokerapp.trans.model.*;
import com.brokersystems.brokerapp.trans.repository.*;
import com.brokersystems.brokerapp.trans.service.AllocationService;
import com.brokersystems.brokerapp.trans.service.PolicyAuthorization;
import com.brokersystems.brokerapp.trans.service.ReceiptService;
import com.brokersystems.brokerapp.trans.utils.HibernateProxyTypeAdapter;
import com.brokersystems.brokerapp.users.dto.MakerCheckDTO;
import com.brokersystems.brokerapp.users.model.MakerChecker;
import com.brokersystems.brokerapp.users.model.QMakerChecker;
import com.brokersystems.brokerapp.users.model.UserRole;
import com.brokersystems.brokerapp.users.repository.MakerCheckerRepo;
import com.brokersystems.brokerapp.users.repository.UserRolesRepo;
import com.brokersystems.brokerapp.users.service.MakerCheckerService;
import com.brokersystems.brokerapp.uw.dtos.PolicyCreateDTO;
import com.brokersystems.brokerapp.uw.dtos.ReceiptsDTO;
import com.brokersystems.brokerapp.uw.model.*;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import com.brokersystems.brokerapp.uw.repository.RiskDocsRepo;
import com.brokersystems.brokerapp.uw.repository.RiskTransRepo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import groovy.inspect.swingui.ScriptToTreeNodeAdapter;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import java.text.NumberFormat;

import static com.brokersystems.brokerapp.users.model.QMakerChecker.makerChecker;
import java.text.NumberFormat;
import java.util.Locale;

@Service
public class MakerCheckerServiceImpl implements MakerCheckerService {
    private static final Logger log = LoggerFactory.getLogger(MakerCheckerServiceImpl.class);
    @Autowired
    private AllocationService allocService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private SystemTransRepo systransRepo;

    @Autowired
    private ClaimRequiredDocsRepo claimRequiredDocsRepo;

    @Autowired
    private SettlementRepo settlementRepo;

    @Autowired
    private RefundRepo refundRepo;

    @Autowired
    private AccountsService accountsService;

    @Autowired
    private PolicyTransRepo policyRepo;

    @Autowired
    private ClaimsBookingRepo claimsBookingRepo;

    @Autowired
    private RiskDocsRepo riskDocsRepo;
    @Autowired
    private RiskTransRepo riskRepo;

    @Autowired
    private MakerCheckerRepo makerCheckerRepo;
    @Autowired
    private UserUtils userUtils;

    @Autowired
    private DateUtilities dateUtilities;
    @Autowired
    private SetupsService setupsService;
    @Autowired
    private ReceiptService receiptService;
    @Autowired
    private ReceiptRepository receiptRepository;
    @Autowired
    private PolicyTransRepo policyTransRepo;
    @Autowired
    private SystemTransactionsRepo systemTransactionsRepo;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EscalationRecordService escalationRecordService;
    @Autowired
    private EscalationRecordRepository escalationRecordRepository;
    @Autowired
    private EscalationActivityLoggerRepository activityLoggerRepository;
    @Autowired
    private SystemTransactionsTempRepo systemTransactionsTempRepo;
    @Autowired
    private RiskTransRepo riskTransRepo;
    @Autowired
    private SendEmailPublisherBean sendEmailPublisherBean;
    @Autowired
    private PolicyAuthorization policyAuthorization;
    @Autowired
    private Mailer mailer;
    @Autowired
    private UserRolesRepo userRolesRepo;
    @Autowired
    private GlTransRepo glTransRepo;

    @Autowired
    private RtsAuditRepository rtsAuditRepository;

    @Override
    @Transactional(rollbackFor = {BadRequestException.class})
    public void createBulkMakerChecker(MakerCheckDTO makerCheckDTO) throws BadRequestException {
        // dont change meant for bulk uploads only
        if (makerCheckDTO.getTaskName() == null) {
            throw new BadRequestException("Unable to validate the Task Name...Cannot continue..");
        }
        if (makerCheckDTO.getStatus() == null) {
            throw new BadRequestException("Unable to validate the Task Status...Cannot continue..");
        }
        if (makerCheckDTO.getJson() == null) {
            throw new BadRequestException("Unable to validate the Task Description...Contact Administrator...Cannot continue..");
        }
        final MakerChecker makerChecker = new MakerChecker();
        //User currentUser = userUtils.getCurrentUser(); // always the initiator
        User currentUser = userRepository.findOne(makerCheckDTO.getMakerId());
        User assignedMaker = makerCheckDTO.getMakerId() != null
                ? userRepository.findOne(makerCheckDTO.getMakerId())
                : currentUser; // fallback if none assigned

        makerChecker.setMakerId(assignedMaker);      // Assigned maker
        makerChecker.setInitiatorId(currentUser);    // Actual policy creator (logged-in user)
//        makerChecker.setMakerId(userUtils.getCurrentUser());
        makerChecker.setMadeOnDate(new Date());
        makerChecker.setTaskJson(makerCheckDTO.getJson());
        makerChecker.setStatus(makerCheckDTO.getStatus());
        makerChecker.setTaskName(makerCheckDTO.getTaskName());
        makerChecker.setTaskCode(makerCheckDTO.getTaskCode());
        makerChecker.setTaskType(makerCheckDTO.getTaskType());
        makerChecker.setPolicyId(makerCheckDTO.getPolicyId());
        makerChecker.setResubmissionComment(makerCheckDTO.getResubmissionComment());
        makerChecker.setAcctId(makerCheckDTO.getAcctId());
        makerChecker.setAssignedCheckers(makerCheckDTO.getAssignedCheckers());
        makerCheckerRepo.save(makerChecker);
        EscalationRecord makerRecord = new EscalationRecord();
        makerRecord.setTask(makerChecker);
        Date madeOnDate = makerChecker.getMadeOnDate();
        makerRecord.setMadeTime(madeOnDate);
        makerRecord.setTaskInitiationTime(new Date());
        makerRecord.setCheckTime(null);
//        User maker = makerChecker.getMakerId();
        makerRecord.setOwner(currentUser);
        makerRecord.setCurrentEscalationLevel(currentUser.getEscalationLevel());
        escalationRecordService.saveOrUpdateEscalationRecord(makerRecord);

        EscalationActivityLogger escalationActivityLogger = new EscalationActivityLogger();
        escalationActivityLogger.setEscalationLevel(currentUser.getEscalationLevel());
        escalationActivityLogger.setTask(makerChecker);
        escalationActivityLogger.setUser(currentUser);
        escalationActivityLogger.setActivityTime(makerChecker.getMadeOnDate());
        escalationActivityLogger.setSystemAction("Record creation");
        activityLoggerRepository.save(escalationActivityLogger);

//        if (makerChecker.getPolicyId() != null) {
//            PolicyTrans policy = policyTransRepo.findOne(QPolicyTrans.policyTrans.policyId.eq(makerChecker.getPolicyId()));
//            String insuranceType = policy.getBinder().getBinName();
//            String clientPhone = (policy.getClient().getPhonePrefix() != null ? policy.getClient().getPhonePrefix().getPrefixName() : "254") + policy.getClient().getPhoneNo();
//            try {
//
//                notifyMaker(makerChecker);
//                notifyClient(insuranceType, clientPhone, "", "Policy Submission", null);
//            }
//            catch (Exception ex){
//                System.out.println("Unable to send notification..."+ex.getMessage());
//            }
//            try {
//                notifyMaker(makerChecker);
//            } catch (Exception e) {
//                System.out.println("Failed to notify maker: " + e.getMessage());
//            }
//
//            try {
//                notifyClient(insuranceType, clientPhone, "", "Policy Submission", null);
//            } catch (Exception e) {
//                System.out.println("Failed to notify client: " + e.getMessage());
//            }

//        }
    }


    @Override
    @Transactional(rollbackFor = {BadRequestException.class})
    public void createMakerChecker(MakerCheckDTO makerCheckDTO) throws BadRequestException {
        if (makerCheckDTO.getTaskName() == null) {
            throw new BadRequestException("Unable to validate the Task Name...Cannot continue..");
        }
        if (makerCheckDTO.getStatus() == null) {
            throw new BadRequestException("Unable to validate the Task Status...Cannot continue..");
        }
        if (makerCheckDTO.getJson() == null) {
            throw new BadRequestException("Unable to validate the Task Description...Contact Administrator...Cannot continue..");
        }
        final MakerChecker makerChecker = new MakerChecker();
        User currentUser = userUtils.getCurrentUser(); // always the initiator
        User assignedMaker = makerCheckDTO.getMakerId() != null
                ? userRepository.findOne(makerCheckDTO.getMakerId())
                : currentUser; // fallback if none assigned

        makerChecker.setMakerId(assignedMaker);      // Assigned maker
        makerChecker.setInitiatorId(currentUser);    // Actual policy creator (logged-in user)
//        makerChecker.setMakerId(userUtils.getCurrentUser());
        makerChecker.setMadeOnDate(new Date());
        makerChecker.setTaskJson(makerCheckDTO.getJson());
        makerChecker.setStatus(makerCheckDTO.getStatus());
        makerChecker.setTaskName(makerCheckDTO.getTaskName());
        makerChecker.setTaskCode(makerCheckDTO.getTaskCode());
        makerChecker.setTaskType(makerCheckDTO.getTaskType());
        makerChecker.setPolicyId(makerCheckDTO.getPolicyId());
        makerChecker.setResubmissionComment(makerCheckDTO.getResubmissionComment());
        makerChecker.setAcctId(makerCheckDTO.getAcctId());
        makerChecker.setAssignedCheckers(makerCheckDTO.getAssignedCheckers());
        makerCheckerRepo.save(makerChecker);
        EscalationRecord makerRecord = new EscalationRecord();
        makerRecord.setTask(makerChecker);
        Date madeOnDate = makerChecker.getMadeOnDate();
        makerRecord.setMadeTime(madeOnDate);
        makerRecord.setTaskInitiationTime(new Date());
        makerRecord.setCheckTime(null);
//        User maker = makerChecker.getMakerId();
        makerRecord.setOwner(currentUser);
        makerRecord.setCurrentEscalationLevel(currentUser.getEscalationLevel());
        escalationRecordService.saveOrUpdateEscalationRecord(makerRecord);

        EscalationActivityLogger escalationActivityLogger = new EscalationActivityLogger();
        escalationActivityLogger.setEscalationLevel(currentUser.getEscalationLevel());
        escalationActivityLogger.setTask(makerChecker);
        escalationActivityLogger.setUser(currentUser);
        escalationActivityLogger.setActivityTime(makerChecker.getMadeOnDate());
        escalationActivityLogger.setSystemAction("Record creation");
        activityLoggerRepository.save(escalationActivityLogger);

        if (makerChecker.getPolicyId() != null) {
            PolicyTrans policy = policyTransRepo.findOne(QPolicyTrans.policyTrans.policyId.eq(makerChecker.getPolicyId()));
            String insuranceType = policy.getBinder().getBinName();
            String clientPhone = (policy.getClient().getPhonePrefix() != null ? policy.getClient().getPhonePrefix().getPrefixName() : "254") + policy.getClient().getPhoneNo();
//            try {
//
//                notifyMaker(makerChecker);
//                notifyClient(insuranceType, clientPhone, "", "Policy Submission", null);
//            }
//            catch (Exception ex){
//                System.out.println("Unable to send notification..."+ex.getMessage());
//            }
            try {
                notifyMaker(makerChecker);
            } catch (Exception e) {
                System.out.println("Failed to notify maker: " + e.getMessage());
            }

            try {
                notifyClient(policy, insuranceType, clientPhone, "", "Policy Submission", null);
            } catch (Exception e) {
                System.out.println("Failed to notify client: " + e.getMessage());
            }

        }
    }

    @Override
    public void checkExists(MakerCheckDTO makerCheckDTO) throws BadRequestException {
        if (makerCheckerRepo.countPendingRequest(makerCheckDTO.getTaskCode(), makerCheckDTO.getTaskType()) > 0) {
            makerCheckDTO.setTaskId(makerCheckDTO.getTaskId());
//            throw new BadRequestException("The Task with this ID has already been generated..Cannot continue..");
        }
    }
    public DataTablesResult<MakerCheckDTO> findPendingReceipts(DataTablesRequest request) {
        final String search = (request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%%";
        Long checkerId = userUtils.getCurrentUser().getId();
        List<Object[]> receiptsList = makerCheckerRepo.findAllReceiptTasks(checkerId.toString(), search.toLowerCase(), request.getPageNumber(), request.getPageSize());
        long rowCount = 0L;
        if (!receiptsList.isEmpty()) {
            rowCount = ((BigInteger) receiptsList.get(0)[11]).longValue(); // total_rows
        }
        final List<MakerCheckDTO> workFlowDTOList = new ArrayList<>();

        for (Object[] receipt : receiptsList) {
            MakerCheckDTO workFlowDTO = new MakerCheckDTO();
            workFlowDTO.setTaskId(((BigInteger) receipt[0]).longValue());
            workFlowDTO.setTaskName((String) receipt[1]);
            workFlowDTO.setMadeOnDate((java.sql.Timestamp) receipt[2]);
            workFlowDTO.setMadeBy((String) receipt[3]);
            workFlowDTO.setStatus((String) receipt[4]);
            workFlowDTO.setTaskType((String) receipt[5]);
            if (receipt[6] != null) {
                workFlowDTO.setPolicyId(((BigInteger) receipt[6]).longValue());
            }
            if (receipt[7] != null) {
                workFlowDTO.setAcctId(((BigInteger) receipt[7]).longValue());
            }
            workFlowDTO.setInitiatorName((String) receipt[9]);
            workFlowDTO.setPolicyNumber((String) receipt[10]);
            workFlowDTOList.add(workFlowDTO);
        }
        Page<MakerCheckDTO> page = new PageImpl<>(workFlowDTOList, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    public DataTablesResult<MakerCheckDTO> findPendingClaims(DataTablesRequest request) {
        final String search = (request.getSearch() != null && request.getSearch().getValue() != null) ?
                "%" + request.getSearch().getValue().toLowerCase() + "%" : "%%";
        Long checkerId = userUtils.getCurrentUser().getId();

        List<Object[]> claimsList = makerCheckerRepo.findAllClaimTasks(
                checkerId.toString(),
                search,
                request.getPageNumber(),
                request.getPageSize()
        );

        long rowCount = 0L;
        if (!claimsList.isEmpty()) {
            rowCount = ((BigInteger) claimsList.get(0)[12]).longValue(); // total_rows
        }

        final List<MakerCheckDTO> workFlowDTOList = new ArrayList<>();

        for (Object[] claim : claimsList) {
            // Log claim data for debugging



            MakerCheckDTO workFlowDTO = new MakerCheckDTO();
            workFlowDTO.setTaskId(((BigInteger) claim[0]).longValue());
            workFlowDTO.setTaskName((String) claim[1]);
            workFlowDTO.setMadeOnDate((java.sql.Timestamp) claim[2]);
            workFlowDTO.setMadeBy((String) claim[3]);
            workFlowDTO.setStatus((String) claim[4]);
            workFlowDTO.setTaskType((String) claim[5]);

            if (claim[6] != null) {
                workFlowDTO.setClaimId(((BigInteger) claim[6]).longValue());
            }

            if (claim[7] != null) {
                workFlowDTO.setRiskId(((BigInteger) claim[7]).longValue());
            }

            workFlowDTO.setInitiatorName((String) claim[9]);
            workFlowDTO.setClaimNumber((String) claim[10]);
            workFlowDTO.setNextReviewDate(claim[11] != null ? (Date) claim[11] : null);
//            String rawClaimStatus = (String) claim[12];
//            if ("P".equalsIgnoreCase(rawClaimStatus)) {
//                workFlowDTO.setClaimStatus("Pending");
//            } else if ("R".equalsIgnoreCase(rawClaimStatus)) {
//                workFlowDTO.setClaimStatus("Ready");
//            } else if ("B".equalsIgnoreCase(rawClaimStatus)) {
//                workFlowDTO.setClaimStatus("Booked");
//            } else {
//                workFlowDTO.setClaimStatus("Booked");
//            }


            workFlowDTOList.add(workFlowDTO);
        }

        Page<MakerCheckDTO> page = new PageImpl<>(workFlowDTOList, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<MakerCheckDTO> findPendingTasks(DataTablesRequest request) {
        final String search = (request.getSearch() != null && request.getSearch().getValue() != null)
                ? "%" + request.getSearch().getValue() + "%"
                : "%%";
        Long checkerId = userUtils.getCurrentUser().getId();

        List<Object[]> ticketsList = makerCheckerRepo.findAllMakerCheckerDetails(
                checkerId.toString(),
                search.toLowerCase(),
                request.getPageNumber(),
                request.getPageSize()
        );

        long rowCount = 0L;
        if (!ticketsList.isEmpty()) rowCount = ((BigInteger) ticketsList.get(0)[13]).longValue();

        final List<MakerCheckDTO> workFlowDTOList = new ArrayList<>();
        for (Object[] ticket : ticketsList) {
            MakerCheckDTO dto = new MakerCheckDTO();
            dto.setTaskId(((BigInteger) ticket[0]).longValue());
            dto.setTaskName((String) ticket[1]);
            dto.setMadeOnDate((Date) ticket[2]);
            dto.setMadeBy((String) ticket[3]);
            dto.setStatus((String) ticket[4]);

            String status = (String) ticket[4];
            if ("N".equalsIgnoreCase(status)) {
                dto.setStatus("Pending Approval");
                dto.setRejectedReason("Pending Approval");
            } else if ("R".equalsIgnoreCase(status)) {
                dto.setStatus("Rejected");
            }

            dto.setTaskType((String) ticket[5]);
            if (ticket[6] != null) dto.setPolicyId(((BigInteger) ticket[6]).longValue());
            if (ticket[7] != null) dto.setAcctId(((BigInteger) ticket[7]).longValue());
            dto.setInitiatorName((String) ticket[9]);

            // New fields
            dto.setClientName((String) ticket[10]);
            dto.setTransType((String) ticket[11]);
            dto.setResubmissionComment((String) ticket[12]);

            workFlowDTOList.add(dto);
        }

        Page<MakerCheckDTO> page = new PageImpl<>(workFlowDTOList, request, rowCount);
        return new DataTablesResult<>(request, page);
    }
//    public DataTablesResult<MakerCheckDTO> findPendingTasks(DataTablesRequest request) {
//        final String search = (request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%%";
//        Long checkerId = userUtils.getCurrentUser().getId();
//        List<Object[]> ticketsList = makerCheckerRepo.findAllMakerCheckerDetails(checkerId.toString(), search.toLowerCase(), request.getPageNumber(), request.getPageSize());
//        long rowCount = 0L;
//        if (!ticketsList.isEmpty()) rowCount = ((BigInteger) ticketsList.get(0)[10]).intValue();
//        final List<MakerCheckDTO> workFlowDTOList = new ArrayList<>();
//        for (Object[] ticket : ticketsList) {
//            MakerCheckDTO workFlowDTO = new MakerCheckDTO();
//            workFlowDTO.setTaskId(((BigInteger) ticket[0]).longValue());
//            workFlowDTO.setTaskName((String) ticket[1]);
//            workFlowDTO.setMadeOnDate((Date) ticket[2]);
//            workFlowDTO.setMadeBy((String) ticket[3]);
//            workFlowDTO.setStatus((String) ticket[4]);
//
//
//            String status = (String) ticket[4];
//            if (status.equalsIgnoreCase("N")) {
//                workFlowDTO.setStatus("Pending Approval");
//                workFlowDTO.setRejectedReason("Pending Approval");
//            } else if (status.equalsIgnoreCase("R")) {
//                workFlowDTO.setStatus("Rejected");
//            }
//
//            workFlowDTO.setTaskType((String) ticket[5]);
//            if (ticket[6] != null) {
//                workFlowDTO.setPolicyId(((BigInteger) ticket[6]).longValue());
//            }
//            if (ticket[7] != null) {
//                workFlowDTO.setAcctId(((BigInteger) ticket[7]).longValue());
//            }
//            workFlowDTO.setInitiatorName((String) ticket[9]);
//            workFlowDTOList.add(workFlowDTO);
//
//        }
//        Page<MakerCheckDTO> page = new PageImpl<>(workFlowDTOList, request, rowCount);
//        return new DataTablesResult<>(request, page);
//    }

    @Override
    @Modifying
    @Transactional(rollbackFor = BadRequestException.class)
    public void approveTask(Long taskId) throws BadRequestException {
        log.info("task id from maker checker {}", taskId);
        if (!makerCheckerRepo.exists(taskId)) {
            throw new BadRequestException("The Task with this ID does not exist..Cannot continue..");
        }
        MakerChecker makerChecker = makerCheckerRepo.findOne(taskId);
        User checkerId = userUtils.getCurrentUser();
        if (Objects.equals(makerChecker.getStatus(), "N")) {
            makerChecker.setStatus("A");
            if (makerChecker.getMakerId().equals(checkerId)) {
              throw new BadRequestException("You can't approve a task you've initiated");
            }
            makerChecker.setCheckerId(checkerId);
            makerChecker.setCheckDate(new Date());
            switch (makerChecker.getTaskType()) {
                case "ANP":
                case "ALP":
                    PolicyTrans policy = policyTransRepo.findOne(makerChecker.getPolicyId());

                    boolean cashBasis = policy.getInterfaceType() != null && "C".equalsIgnoreCase(policy.getInterfaceType());
                    Iterable<RiskTrans> riskTrans = riskTransRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(policy.getPolicyId()));
                    if((policy != null) && (!policy.getTransType().equalsIgnoreCase("BU"))) {
                        notifyInsurer(riskTrans, policy, cashBasis, makerChecker.getMakerId(), checkerId);
                    }
                    break;

                case "RC":
                    // Retrieve policy using makerChecker's policyId
                    // Deserialize the task JSON
                    Gson gson = new GsonBuilder()
                            .setDateFormat("dd/MM/yyyy")
                            .create();

                    // Check if this is a cancellation task
                    if (makerChecker.getTaskName() != null && makerChecker.getTaskName().contains("Cancel Receipt")) {
                        ReceiptsDTO receiptDTO = gson.fromJson(makerChecker.getTaskJson(), ReceiptsDTO.class);

                        ReceiptTrans receipt = receiptRepository.findOne(
                                QReceiptTrans.receiptTrans.receiptNo.eq(receiptDTO.getReceiptNo())
                        );

                        if (receipt == null) {
                            throw new BadRequestException("Receipt not found with number: " + receiptDTO.getReceiptNo());
                        }

                        if ("Y".equals(receipt.getCancelApproved())) {
                            throw new BadRequestException("Receipt cancellation has already been approved.");
                        }

                        // Create CancelData object
                        CancelData cancelData = new CancelData();
                        cancelData.setReceiptId(receipt.getReceiptId());
                        cancelData.setCommentl(receiptDTO.getCancelComment());

                        List<CancelData> cancelDataList = Collections.singletonList(cancelData);

                        // Call cancellation with approval flag
                        receiptService.cancelReceipts(cancelDataList, true);

                        // Update final approval metadata
                        User currentUser = userUtils.getCurrentUser();
                        Date now = new Date();


                        receipt.setCancelApproved("Y");
                        receipt.setCancelled("Y");
                        receipt.setCancelledBy(currentUser);
                        receipt.setCancelledDate(now);
                        receipt.setCancelApprovedBy(currentUser);
                        receipt.setCancelApprovedDate(now);

                        receiptRepository.save(receipt);

                    } else {
                        PolicyTrans policy1 = policyTransRepo.findOne(makerChecker.getPolicyId());
                        if (policy1 == null) {
                            throw new BadRequestException("Policy not found for task ID: " + taskId);
                        }
                        Iterable<RiskTrans> riskTrans1 = riskTransRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(policy1.getPolicyId()));
                        for (RiskTrans risk : riskTrans1) {
                            Iterable<RiskDocs> riskDocs = riskDocsRepo.findAll(QRiskDocs.riskDocs.risk.riskId.eq(risk.getRiskId()));
                            if (policy1.getTransType() != null && !"CN".equalsIgnoreCase(policy1.getTransType())) {
                                for (RiskDocs riskDoc : riskDocs) {
                                    if (riskDoc.getVerifiedBy() == null || riskDoc.getVerifiedDate() == null) {
                                        throw new BadRequestException(String.format("Cannot approve task: Document for risk ID %d is not approved", risk.getRiskId()));
                                    }
                                }
                            }
                        }
                        // This is a regular receipt creation approval
                        ReceiptTrans receiptTrans = gson.fromJson(makerChecker.getTaskJson(), ReceiptTrans.class);
                        receiptService.createReceipt(receiptTrans, true);
                        ReceiptTrans created = receiptRepository.findOne(receiptTrans.getReceiptId());
                        receiptService.markReceiptPrinted(created.getReceiptId(), checkerId);

                    }
                    break;

                case "IM":
                    setupsService.approveAccount(makerChecker.getAcctId());
                    break;
                case "CL":
                    Gson gsonCL = new GsonBuilder()
                            .setDateFormat("MMM dd, yyyy HH:mm:ss z")
                            .create();
                    ClaimDetailsDTO claimDetailsDTO = gsonCL.fromJson(makerChecker.getTaskJson(), ClaimDetailsDTO.class);
                    if (claimDetailsDTO == null || claimDetailsDTO.getClmId() == null) {
                        throw new BadRequestException("Failed to deserialize task JSON or missing clmId");
                    }
                    ClaimBookings claimBookings = claimsBookingRepo.findOne(claimDetailsDTO.getClmId());
                    if (claimBookings == null) {
                        throw new BadRequestException("Claim not found with ID: " + claimDetailsDTO.getClmId());
                    }
                    claimBookings.setApprovalStatus("A");
                    claimBookings.setClaimApprovalDate(new Date());
                    claimBookings.setClaimApprovedBy(userUtils.getCurrentUser());
                    claimsBookingRepo.save(claimBookings);
                    PolicyTrans policy1 = claimBookings.getRisk().getPolicy();

                    if((policy1 != null) && (!policy1.getTransType().equalsIgnoreCase("BU"))) {
                        notifyInsurerClaim(claimBookings, policy1, makerChecker.getMakerId(), checkerId);
                        System.out.println(policy1.getTransType() + "am at claims approval");
                    }

                    break;
                case "RF":
//                    try {
                        PolicyTrans policy2 = policyTransRepo.findOne(makerChecker.getPolicyId());
                        User currentUser = userUtils.getCurrentUser();
                        Date now = new Date();
                        Long refundTransactionNumber = makerChecker.getTaskCode();
                        policy2.setAuthStatus("A");

                        log.info("=== Starting refund approval for transaction: {} ===", refundTransactionNumber);

                        // Step 1: Find the refund transaction using native SQL to avoid column limit
                        SystemTransactions refundTransaction = null;
                        try {
                            // Use EntityManager with native SQL to get only essential fields
                            String sql = "SELECT trans_no, trans_type, trans_authorised, trans_amount, trans_balance, trans_settle_amt, " +
                                    "trans_ref_trans_no FROM sys_brk_main_transactions WHERE trans_no = ?";

                            Query query = entityManager.createNativeQuery(sql);
                            query.setParameter(1, refundTransactionNumber);

                            Object[] result = (Object[]) query.getSingleResult();

                            if (result != null) {
                                // Create a minimal SystemTransactions object with only the data we need
                                refundTransaction = new SystemTransactions();
                                refundTransaction.setTransno(((Number) result[0]).longValue());
                                refundTransaction.setTransType((String) result[1]);
                                refundTransaction.setAuthorised((String) result[2]);
                                refundTransaction.setAmount((BigDecimal) result[3]);
                                refundTransaction.setBalance((BigDecimal) result[4]);
                                refundTransaction.setSettleAmt((BigDecimal) result[5]);

                                // Get the original transaction reference if it exists
                                if (result[6] != null) {
                                    Long originalTransNo = ((Number) result[6]).longValue();

                                    // Get original transaction with minimal fields
                                    String originalSql = "SELECT trans_no, trans_type, trans_authorised, trans_amount, trans_balance, trans_settle_amt " +
                                            "FROM sys_brk_main_transactions WHERE trans_no = ?";
                                    Query originalQuery = entityManager.createNativeQuery(originalSql);
                                    originalQuery.setParameter(1, originalTransNo);

                                    Object[] originalResult = (Object[]) originalQuery.getSingleResult();
                                    if (originalResult != null) {
                                        SystemTransactions originalTrans = new SystemTransactions();
                                        originalTrans.setTransno(((Number) originalResult[0]).longValue());
                                        originalTrans.setTransType((String) originalResult[1]);
                                        originalTrans.setAuthorised((String) originalResult[2]);
                                        originalTrans.setAmount((BigDecimal) originalResult[3]);
                                        originalTrans.setBalance((BigDecimal) originalResult[4]);
                                        originalTrans.setSettleAmt((BigDecimal) originalResult[5]);

                                        refundTransaction.setRefundTransaction(originalTrans);
                                    }
                                }
                            }

                            log.info("Step 1 - Found refund transaction using native SQL: {}", refundTransaction != null ? "YES" : "NO");
                        } catch (Exception e) {
                            log.error("Step 1 - Error finding refund transaction with native SQL: {}", e.getMessage());
                            throw new BadRequestException("Database error finding refund transaction: " + e.getMessage());
                        }

                        if (refundTransaction == null) {
                            throw new BadRequestException("Refund transaction not found: " + refundTransactionNumber);
                        }

                        // Step 2: Verify transaction type
                        if (!"RF".equals(refundTransaction.getTransType())) {
                            throw new BadRequestException("Transaction " + refundTransaction.getTransno() + " is not a refund transaction");
                        }
                        log.info("Step 2 - Transaction type verified: RF");

                        // Step 3: Get original transaction
                        SystemTransactions originalTransaction = refundTransaction.getRefundTransaction();
                        if (originalTransaction == null) {
                            throw new BadRequestException("No original transaction found for refund: " + refundTransaction.getTransno());
                        }
                        log.info("Step 3 - Found original transaction: {}", originalTransaction.getTransno());

                        // Step 4: Validate original transaction
                        if (!"Y".equals(originalTransaction.getAuthorised())) {
                            throw new BadRequestException("Cannot refund unauthorized transaction: " + originalTransaction.getTransno());
                        }

                        if (originalTransaction.getBalance() == null || originalTransaction.getBalance().compareTo(BigDecimal.ZERO) == 0) {
                            throw new BadRequestException("Original transaction has no available balance for refund: " + originalTransaction.getTransno());
                        }
                        log.info("Step 4 - Original transaction validated");

                        // Step 5: Calculate and validate refund amount
                        BigDecimal refundAmount = refundTransaction.getAmount().abs();
                        BigDecimal availableBalance = originalTransaction.getBalance().abs();

                        SystemTransactions transactions = systemTransactionsRepo.findByTransno(originalTransaction.getTransno());

                        System.out.println("Original Ref No..."+transactions.getRefNo());
                        BigInteger agentTransNo = systemTransactionsRepo.getAgentTransaction(transactions.getRefNo());

                        //for life cancelation it uses policyinstallements
                        if(policy2.getBusinessType().equalsIgnoreCase("L") && agentTransNo == null){
                            agentTransNo = systemTransactionsRepo.getAgentLTransaction(transactions.getRefNo());
                        }

                        if(agentTransNo == null) {
                            throw new BadRequestException("Unable to get Intermediary Transaction. Please contact Admin...");
                        }

                        final SystemTransactions agentTrans = systemTransactionsRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(agentTransNo.longValue()));

                        BigDecimal refAmount = BigDecimal.valueOf(-1 * refundAmount.doubleValue());
                        postRefundTransaction(agentTrans.getAgent(),refAmount,agentTrans,new Date(),agentTrans.getPolicy(),agentTrans.getTransaction());

                        if (refundAmount.compareTo(availableBalance) > 0) {
                            throw new BadRequestException("Refund amount (" + refundAmount + ") exceeds available balance (" + availableBalance + ")");
                        }
                        log.info("Step 5 - Refund amount validated: {}", refundAmount);

                        // Step 6: Update transactions using native SQL to match allocateCreditTransRfnd logic
                        try {
                            // Get current values before update for debugging
                            String checkSql = "SELECT trans_balance, trans_settle_amt FROM sys_brk_main_transactions WHERE trans_no = ?";

                            Query checkOriginal = entityManager.createNativeQuery(checkSql);
                            checkOriginal.setParameter(1, originalTransaction.getTransno());
                            Object[] currentOriginal = (Object[]) checkOriginal.getSingleResult();

                            Query checkRefund = entityManager.createNativeQuery(checkSql);
                            checkRefund.setParameter(1, refundTransaction.getTransno());
                            Object[] currentRefund = (Object[]) checkRefund.getSingleResult();

                            log.info("CURRENT DATABASE VALUES:");
                            log.info("Original trans {}: balance={}, settlement={}",
                                    originalTransaction.getTransno(), currentOriginal[0], currentOriginal[1]);
                            log.info("Refund trans {}: balance={}, settlement={}",
                                    refundTransaction.getTransno(), currentRefund[0], currentRefund[1]);
                            log.info("Refund amount to process: {}", refundAmount);

                            // Calculate expected results
                            BigDecimal currentOriginalBalance = (BigDecimal) currentOriginal[0];
                            BigDecimal currentOriginalSettlement = currentOriginal[1] != null ? (BigDecimal) currentOriginal[1] : BigDecimal.ZERO;
                            BigDecimal currentRefundBalance = (BigDecimal) currentRefund[0];
                            BigDecimal currentRefundSettlement = currentRefund[1] != null ? (BigDecimal) currentRefund[1] : BigDecimal.ZERO;

                            log.info("EXPECTED RESULTS (CORRECTED):");
                            log.info("Original balance: {} + {} = {}", currentOriginalBalance, refundAmount, currentOriginalBalance.add(refundAmount));
                            log.info("Original settlement: {} + {} = {}", currentOriginalSettlement, refundAmount, currentOriginalSettlement.add(refundAmount));
                            log.info("Refund balance: {} - {} = {}", currentRefundBalance, refundAmount, currentRefundBalance.subtract(refundAmount));
                            log.info("Refund settlement: {} + {} = {}", currentRefundSettlement, refundAmount, currentRefundSettlement.add(refundAmount));

                            // For DEBIT transaction (original transaction):
                            // Customer owes money (negative balance), refund adds money back
                            // Example: -1,004,500 + 1,004,500 = 0
                            String updateOriginalSql = "UPDATE sys_brk_main_transactions SET " +
                                    "trans_balance = trans_balance + ?, " +
                                    "trans_settle_amt = COALESCE(trans_settle_amt, 0) + ? " +
                                    "WHERE trans_no = ?";
                            Query updateOriginal = entityManager.createNativeQuery(updateOriginalSql);
                            updateOriginal.setParameter(1, refundAmount);  // ADD refund to balance (reduces debt)
                            updateOriginal.setParameter(2, refundAmount);  // Add to settlement (positive)
                            updateOriginal.setParameter(3, originalTransaction.getTransno());
                            int originalUpdated = updateOriginal.executeUpdate();
                            log.info("Original (debit) transaction updated: {} rows affected", originalUpdated);

                            // For CREDIT transaction (refund transaction):
                            // Refund money is going out, so reduce the refund transaction balance
                            String updateRefundSql = "UPDATE sys_brk_main_transactions SET " +
                                    "trans_authorised = 'Y', " +
                                    "trans_user_auth = ?, " +
                                    "trans_auth_date = ?, " +
                                    "trans_posted_dt = ?, " +
                                    "trans_balance = trans_balance - ?, " +
                                    "trans_settle_amt = COALESCE(trans_settle_amt, 0) + ? " +
                                    "WHERE trans_no = ?";
                            Query updateRefund = entityManager.createNativeQuery(updateRefundSql);
                            updateRefund.setParameter(1, currentUser.getUsername());
                            updateRefund.setParameter(2, now);
                            updateRefund.setParameter(3, now);
                            updateRefund.setParameter(4, refundAmount);  // Subtract from refund balance (money going out)
                            updateRefund.setParameter(5, refundAmount);  // Add to settlement (positive)
                            updateRefund.setParameter(6, refundTransaction.getTransno());
                            int refundUpdated = updateRefund.executeUpdate();
                            log.info("Refund (credit) transaction updated: {} rows affected", refundUpdated);

                            // Check final values
                            Query finalCheckOriginal = entityManager.createNativeQuery(checkSql);
                            finalCheckOriginal.setParameter(1, originalTransaction.getTransno());
                            Object[] finalOriginal = (Object[]) finalCheckOriginal.getSingleResult();

                            Query finalCheckRefund = entityManager.createNativeQuery(checkSql);
                            finalCheckRefund.setParameter(1, refundTransaction.getTransno());
                            Object[] finalRefund = (Object[]) finalCheckRefund.getSingleResult();

                            log.info("FINAL DATABASE VALUES:");
                            log.info("Original trans {}: balance={}, settlement={}",
                                    originalTransaction.getTransno(), finalOriginal[0], finalOriginal[1]);
                            log.info("Refund trans {}: balance={}, settlement={}",
                                    refundTransaction.getTransno(), finalRefund[0], finalRefund[1]);

                            log.info("Step 6 - Transactions updated using native SQL");
                        } catch (Exception e) {
                            log.error("Step 6 - Error updating transactions: {}", e.getMessage());
                            throw new BadRequestException("Error updating transactions: " + e.getMessage());
                        }

                        // Step 7: Update maker-checker status
                        try {

                            makerChecker.setStatus("A");
                            makerChecker.setCheckerId(currentUser);
                            makerChecker.setCheckDate(now);
                            log.info("Step 7 - Maker-checker status updated");
                        } catch (Exception e) {
                            log.error("Step 7 - Error updating maker-checker: {}", e.getMessage());
                            throw new BadRequestException("Error updating maker-checker: " + e.getMessage());
                        }

                        log.info("=== Refund approval completed successfully for transaction: {} ===", refundTransaction.getTransno());

//                    } catch (BadRequestException e) {
//                        // Re-throw BadRequestException as-is
//                        throw e;
//                    } catch (Exception e) {
//                        log.error("=== Unexpected error in refund approval: {} ===", e.getMessage(), e);
//                        throw new BadRequestException("Unexpected error in refund approval: " + e.getMessage());
//                    }
                    break;



                default:
                    throw new BadRequestException("Invalid task type.");
            }
            makerCheckerRepo.save(makerChecker);

            // Rest of your escalation and activity logging code...
            List<EscalationRecord> checkerRecords = escalationRecordRepository.findByTask(makerChecker);

            if (!checkerRecords.isEmpty()) {
                EscalationRecord existingRecord = checkerRecords.get(0);
                BeanUtils.copyProperties(makerChecker, existingRecord, "escId", "task");
                existingRecord.setCheckTime(makerChecker.getCheckDate());
                existingRecord.setVerifier(makerChecker.getCheckerId());
                escalationRecordRepository.save(existingRecord);
            } else {
                EscalationRecord newRecord = new EscalationRecord();
                newRecord.setCheckTime(makerChecker.getCheckDate());
                newRecord.setVerifier(makerChecker.getCheckerId());
                newRecord.setCurrentEscalationLevel(makerChecker.getCheckerId().getEscalationLevel());
                newRecord.setOwner(null);
                newRecord.setTask(makerChecker);
                escalationRecordRepository.save(newRecord);
            }

            User checker = makerChecker.getCheckerId();
            if (checker != null) {
                EscalationActivityLogger escalationActivityLogger = new EscalationActivityLogger();
                escalationActivityLogger.setEscalationLevel(checker.getEscalationLevel());
                escalationActivityLogger.setTask(makerChecker);
                escalationActivityLogger.setUser(checker);
                escalationActivityLogger.setActivityTime(makerChecker.getCheckDate());
                escalationActivityLogger.setSystemAction("Record Approval");
                activityLoggerRepository.save(escalationActivityLogger);
            }
        }

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void postRefundTransaction(AccountDef accountDef, BigDecimal polPremium, SystemTransactions transactions, Date postDate, PolicyTrans policy, SystemTrans transaction) throws BadRequestException {
        List<GlTransactions> glTransactions = new ArrayList<>();
        if(accountDef.getReceivableAccount()==null){
            throw new BadRequestException("Unable to get Debit Account for Premium Account for .."+accountDef.getName());
        }
        if(accountDef.getPayableAccount()==null){
            throw new BadRequestException("Unable to get Credit Account for Premium Account for .."+accountDef.getName());
        }
        if (accountDef.getPayableAccount() != null && accountDef.getReceivableAccount() != null) {
            System.out.println("refund here signum "+polPremium.signum() +" pol prem"+ polPremium +" abs polprem "+ polPremium.abs());

            GlTransactions debit = new GlTransactions();
            debit.setAmount(polPremium.abs());
            debit.setSystemTransactions(transactions);
            debit.setAuthDate(postDate);
            debit.setbCuramount(polPremium.abs());
            debit.setBranch(policy.getBranch());
            debit.setCurrency(policy.getTransCurrency());
            debit.setGlAcc((polPremium.signum() == 1) ? accountDef.getPayableAccount() : accountDef.getReceivableAccount());
            debit.setGldc("D");
            debit.setTransaction(transaction);
            debit.setTransLevel("U");
            debit.setTrntCode("RF");
            debit.setGlYear(dateUtilities.getUwYear(postDate));
            debit.setGlMonth(dateUtilities.getMonth(postDate));
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
            credit.setGlAcc((polPremium.signum() == 1) ? accountDef.getReceivableAccount() : accountDef.getPayableAccount());
            credit.setGldc("C");
            credit.setTransaction(transaction);
            credit.setTransLevel("U");
            credit.setTrntCode("RF");
            credit.setGlYear(dateUtilities.getUwYear(postDate));
            credit.setGlMonth(dateUtilities.getMonth(postDate));
            credit.setNarration(String.format("Posting premium for Policy No: %s, Ref No: %s", policy.getPolNo(), policy.getPolRevNo()));
            credit.setPolicyTrans(policy); // Setting the PolicyTrans reference
            credit.setTransType("PREM");
            glTransactions.add(credit);
        }
        glTransRepo.save(glTransactions);
    }

//    public void approveTask(Long taskId) throws BadRequestException {
//        Gson gson = new GsonBuilder()
//                .setDateFormat("dd/MM/yyyy")
//                .create();
//        Logger log = LoggerFactory.getLogger(getClass());
//        if (!makerCheckerRepo.exists(taskId)) {
//            throw new BadRequestException("The Task with this ID does not exist..Cannot continue..");
//        }
//        MakerChecker makerChecker = makerCheckerRepo.findOne(taskId);
//        User checkerId = userUtils.getCurrentUser();
//        if (Objects.equals(makerChecker.getStatus(), "N")) {
//            makerChecker.setStatus("A");
//            if (makerChecker.getMakerId().equals(checkerId)) {
//                throw new BadRequestException("You can't approve a task you've initiated");
//            }
//            makerChecker.setCheckerId(checkerId);
//            makerChecker.setCheckDate(new Date());
//            switch (makerChecker.getTaskType()) {
////                case "CL":
////                    ClientDTO clientDTO = new Gson().fromJson(makerChecker.getTaskJson(), ClientDTO.class);
////                    System.out.println(clientDTO);
////                    if (clientDTO.getTenId() == null) {
////                        setupsService.defineClient(clientDTO, true);
////                    }else {
////                        setupsService.updateClient(clientDTO, true);
////                    }
////                    break;
//
//                case "ANP":
//                case "ALP":
//                    PolicyTrans policy = policyTransRepo.findOne(makerChecker.getPolicyId());
//                    boolean cashBasis = policy.getInterfaceType() != null && "C".equalsIgnoreCase(policy.getInterfaceType());
//                    Iterable<RiskTrans> riskTrans = riskTransRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(policy.getPolicyId()));
//                    notifyInsurer(riskTrans, policy, cashBasis, makerChecker.getMakerId(), checkerId);
//                    break;
//
////                case "QT":
////                    CreateQuoteDTO createQuoteDTO = new Gson().fromJson(makerChecker.getTaskJson(), CreateQuoteDTO.class);
////                    QuoteTrans quoteTrans = quotationService.createQuotation(createQuoteDTO);
////                    try {
////                        quotationService.computeQuotPrem(quoteTrans.getQuoteId());
////                    } catch (IOException e) {
////                        throw new BadRequestException(e.getMessage());
////                    }
////                    break;
//
//
//                case "RC":
////                    Gson gson = new GsonBuilder()
////                            .setDateFormat("dd/MM/yyyy")
////                            .create();
//                    ReceiptTrans receiptTrans = gson.fromJson(makerChecker.getTaskJson(), ReceiptTrans.class);
//                    receiptService.createReceipt(receiptTrans, true);
//                    ReceiptTrans created = receiptRepository.findOne(receiptTrans.getReceiptId());
//                    receiptService.markReceiptPrinted(created.getReceiptId(), checkerId);
//                    break;
//                case "CANCEL_RECEIPT":
//                    log.info("Processing CANCEL_RECEIPT task, taskJson: {}", makerChecker.getTaskJson());
//                    ReceiptsDTO cancelDTO = gson.fromJson(makerChecker.getTaskJson(), ReceiptsDTO.class);
//                    if (cancelDTO.getReceiptNo() == null) {
//                        log.error("Receipt number missing in task {}", taskId);
//                        throw new BadRequestException("Receipt number is missing in task data");
//                    }
//                    ReceiptTrans receipt = receiptRepository.findByReceiptNo(cancelDTO.getReceiptNo());
//                    if (receipt == null) {
//                        log.error("Receipt not found for receiptNo: {} in task {}", cancelDTO.getReceiptNo(), taskId);
//                        throw new BadRequestException("Receipt not found for receipt number: " + cancelDTO.getReceiptNo());
//                    }
//                    if ("Y".equalsIgnoreCase(receipt.getCancelled())) {
//                        log.warn("Receipt {} already cancelled for task {}", cancelDTO.getReceiptNo(), taskId);
//                        throw new BadRequestException("Receipt is already cancelled");
//                    }
//                    log.info("Cancelling receipt: receiptNo={}", cancelDTO.getReceiptNo());
//                    receipt.setCancelled("Y");
//                    receipt.setCancelApprovedDate(new Date());
//                    receiptRepository.save(receipt);
//                    allocService.deallocateReceipt(receipt.getReceiptId(), "Approved cancellation via Maker-Checker");
//                    break;
//
//                case "IM":
//                    setupsService.approveAccount(makerChecker.getAcctId());
//                    break;
//
//                default:
//                    throw new BadRequestException("Invalid task type.");
//
//            }
//            makerCheckerRepo.save(makerChecker);
//            List<EscalationRecord> checkerRecords = escalationRecordRepository.findByTask(makerChecker);
//
//            // Check if any records were found
//            if (!checkerRecords.isEmpty()) {
//                EscalationRecord existingRecord = checkerRecords.get(0); // Use the first matching record
//                BeanUtils.copyProperties(makerChecker, existingRecord, "escId", "task"); // Preserve ID and relations
//                existingRecord.setCheckTime(makerChecker.getCheckDate());
//                existingRecord.setVerifier(makerChecker.getCheckerId());
//                escalationRecordRepository.save(existingRecord);
//            } else {
//                // Create a new EscalationRecord if none exist
//                EscalationRecord newRecord = new EscalationRecord();
//                newRecord.setCheckTime(makerChecker.getCheckDate());
//                newRecord.setVerifier(makerChecker.getCheckerId());
//                newRecord.setCurrentEscalationLevel(makerChecker.getCheckerId().getEscalationLevel());
//                newRecord.setOwner(null);
//                newRecord.setTask(makerChecker);
//                escalationRecordRepository.save(newRecord);
////                throw new BadRequestException("Error in task submission");
//            }
//            User checker = makerChecker.getCheckerId();
//            if (checker != null) {
//                EscalationActivityLogger escalationActivityLogger = new EscalationActivityLogger();
//                escalationActivityLogger.setEscalationLevel(checker.getEscalationLevel());
//                escalationActivityLogger.setTask(makerChecker);
//                escalationActivityLogger.setUser(checker);
//                escalationActivityLogger.setActivityTime(makerChecker.getCheckDate());
//                escalationActivityLogger.setSystemAction("Record Approval");
//                activityLoggerRepository.save(escalationActivityLogger);
//            }
//
//        }
//
//    }
private void notifyInsurerClaim(ClaimBookings claimBookings, PolicyTrans policy, User maker, User checker) {
    System.out.println("Notifying insurer about approved claim");

    RiskTrans riskTran = claimBookings.getRisk();
    MailMessageBean messageBean = new MailMessageBean();
    String coverType = riskTran.getCovertype().getCovShtDesc();
    String productName = policy.getProduct().getProShtDesc();

    String insuredName = String.format("%s %s", riskTran.getInsured().getFname(), riskTran.getInsured().getOtherNames());
    messageBean.setSubject(String.format("CLAIM APPROVAL - %s %s %s %s",
            insuredName, policy.getPolNo(), claimBookings.getClaimNo(), coverType));

    // Hard code underwriter email for test purposes
    messageBean.setSendTo("Joram.Gatimu@absa.africa");

    if (checker != null) {
        messageBean.setSendCC(String.format("%s,%s,bancassuranceclaims.ke@absa.africa", maker.getEmail(), checker.getEmail()));
    } else {
        messageBean.setSendCC(String.format("%s,bancassuranceclaims.ke@absa.africa", maker.getEmail()));
    }

    // Claims-specific message
    String message = String.format(
            "Dear Team,<br><br>" +
                    "Please note that the following claim has been approved:<br><br>" +
                    "<b>Claim Details:</b><br>" +
                    "Claim Number: %s<br>" +
                    "Policy Number: %s<br>" +
                    "Insured: %s<br>" +
                    "Cover Type: %s<br>" +
                    "All supporting documents are attached for your review.",
            claimBookings.getClaimNo(),
            policy.getPolNo(),
            insuredName,
            coverType
    );

    String signature = signatureTemplate(maker, policy);
    String finalMessage = message + "<br><br>" + signature;

    List<String> attachments = new ArrayList<>();

// Add claim-specific documents that the system knows how to generate
    attachments.add("SY");
//    attachments.add("ID");


    log.info("Claim email attachments for {}: {}", claimBookings.getClaimNo(), attachments);

    messageBean.setMessage(finalMessage);
    messageBean.setTransType("C");
    messageBean.setTransCode(claimBookings.getClmId());
    messageBean.setReports(attachments);
    sendEmailPublisherBean.sendEmail(messageBean);
}

    private void notifyInsurer(Iterable<RiskTrans> riskTrans, PolicyTrans policy, boolean cashBasis, User maker, User checker) {
        System.out.println("Passed here ");
        for (RiskTrans riskTran : riskTrans) {
            MailMessageBean messageBean = new MailMessageBean();
            String coverType = riskTran.getCovertype().getCovShtDesc();
            String productName = policy.getProduct().getProShtDesc();
            String transType = policy.getTransType();

            String insuredName = String.format("%s %s", riskTran.getInsured().getFname(), riskTran.getInsured().getOtherNames());
            messageBean.setSubject(String.format("%s %s %s %s %s", insuredName, policy.getPolNo(), policy.getRefNo(), productName, transType));
            //hard code underwriter email for test purposes
            messageBean.setSendTo("Joram.Gatimu@absa.africa");//policy.getAgent().getEmail());
            if (checker != null) {
                messageBean.setSendCC(String.format("%s,%s,bancassuranceops@absa.africa", maker.getEmail(), checker.getEmail()));
            } else {
                messageBean.setSendCC(String.format("%s,bancassuranceops@absa.africa", maker.getEmail()));
            }
            String message = "";
            BigDecimal balance;
            BigDecimal settlement;
            BigDecimal totalPremium;
            List<Object[]> balanceAndSettlementList = receiptRepository.findPremiumSettlementAndBalance(policy.getPolicyId());
            settlement = (BigDecimal) balanceAndSettlementList.get(0)[1];
            if (policy.getBusinessType().equalsIgnoreCase("N")) {
                totalPremium = (BigDecimal) balanceAndSettlementList.get(0)[0];
            } else {
                totalPremium = (BigDecimal) balanceAndSettlementList.get(0)[2];
            }
            balance = totalPremium.subtract(settlement);
            boolean fullPayment = balance.compareTo(BigDecimal.ZERO) == 0;
            boolean overPayement = balance.compareTo(BigDecimal.ZERO) > 0;
            boolean initialPartialPayment = false;
            boolean futurePayment = false;
            if (settlement != null) {
                initialPartialPayment = settlement.compareTo(BigDecimal.ZERO) > 0 && balance.compareTo(BigDecimal.ZERO) > 0;
                futurePayment = settlement.compareTo(BigDecimal.ZERO) == 0 && balance.compareTo(BigDecimal.ZERO) > 0;
            }
            if (policy.getProduct().getProDesc().contains("MOTOR") || policy.getProduct().getProShtDesc().contains("MOTOR")) {
                if (fullPayment) {
                    message = String.format("Dear Team, <br> Please place cover as attached and share cover notes. Issue Annual Motor certificate.<br>Payment is as attached. <br> Kindly note the attached Risk Note Number %s: which you should quote in your cover notes / production reports", policy.getRefNo());
                } else if (initialPartialPayment) {
                    message = String.format("Dear Team,<br> Please place cover as attached and share cover notes. Issue One Month Motor certificate.<br>Payment is as attached.<br> Kindly note the attached Risk Note Number %s: which you should quote in your cover notes / production reports", policy.getRefNo());
                }
            } else {
                if (fullPayment) {
                    message = String.format("Dear Team,<br>Please place cover as attached and share cover notes.<br>Payment is as attached.<br> Kindly note the attached Risk Note Number %s: which you should quote in your cover notes / production reports", policy.getRefNo());
                } else if (initialPartialPayment) {
                    message = String.format("Dear Team,<br>Please place cover as attached and share cover notes.<br>Payment is as attached.<br> Kindly note the attached Risk Note Number %s: which you should quote in your cover notes / production reports", policy.getRefNo());
                }
            }
            if (futurePayment) {
                message = String.format("Dear Team,<br>Please place cover as attached and share cover notes.<br>Payment is as attached.<br>Kindly note the attached Risk Note Number %s: which you should quote in your cover notes / production reports", policy.getRefNo());
            } else if (cashBasis) {
                message = String.format("Dear Team,<br> Kindly find attached cover request for customer as per subject. <br> Note policy number %s and Risk Note %s: which you should quote in your cover notes / production reports We shall forward hard copy once received.", policy.getPolNo(), policy.getRefNo());
            }

            if(overPayement){
                MailMessageBean uMessageBean = new MailMessageBean();
                String uMessage = String.format("Dear Underwriter,<br> Please issue a Credit Note for overpayment. <br> Payment is as attached.<br>Kindly note the attached Risk Note Number %s: which you should quote in your cover notes / production reports", policy.getRefNo());
                uMessageBean.setSubject(String.format("%s %s %s %s", insuredName, policy.getPolNo(), policy.getRefNo(), coverType));
                //hard code underwriter email for test purposes
                uMessageBean.setSendTo("Joram.Gatimu@absa.africa");//policy.getAgent().getEmail());
                //uMessageBean.setSendTo(policy.getAgent().getEmail());
                uMessageBean.setMessage(uMessage);
                sendEmailPublisherBean.sendEmail(uMessageBean);
            }

            String signature = signatureTemplate(maker, policy);
            String finalMessage = message + " <br>" + signature;

            List<String> attachments = new ArrayList<>();
            attachments.add("RN");
            attachments.add("ID");
            attachments.add("KRA");
            attachments.add("RCT");

            //attachments.add("RD");
            List<Object[]> risks = riskRepo.findLifePolicyRisks("", policy.getPolicyId(), -2000L, 0, 1000000);
            int docNumber = 1;
            for (Object[] risk : risks) {
                Long riskId = ((BigInteger) risk[0]).longValue();
                List<Object[]> riskDocs = riskDocsRepo.getAllRiskDocs(riskId, 0, 1000000);
                for (Object[] doc : riskDocs) {
                    attachments.add("RD" + docNumber);
                    docNumber++;
                    if (docNumber > 20) break; // Set reasonable limit
                }
            }

            messageBean.setMessage(finalMessage);
            messageBean.setTransType("P");
            messageBean.setTransCode(policy.getPolicyId());
            messageBean.setReports(attachments);
            sendEmailPublisherBean.sendEmail(messageBean);

        }

    }
    private String clientEmailPolicyInfo(PolicyTrans policyTrans) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        return String.format("Cover From: %s, Cover To: %s, Premium Amount: %s %s",
                policyTrans.getCoverFrom(),
                policyTrans.getCoverTo(),
                policyTrans.getTransCurrency().getCurIsoCode(),
                formatter.format(policyTrans.getBasicPrem()));
    }

    @Override
    public void notifyClient(PolicyTrans policyTrans, String insuranceType, String clientPhone, String clientEmail, String eventType, String days) throws BadRequestException {
        MailMessageBean smsMessageBean = new MailMessageBean();
        MailMessageBean mailMessageBean = new MailMessageBean();
        String policyInfo = clientEmailPolicyInfo(policyTrans);
        String smsMessage = "";
        String emailMessage = "";
        String emailSubject = "";

        if (eventType.equalsIgnoreCase("Policy Submission")) {
            smsMessageBean.setMessage(String.format("Dear Customer, your application for %s has been successfully submitted for processing. %s", insuranceType, policyInfo));
        } else if (eventType.equalsIgnoreCase("Policy Installment Due")) {
            smsMessage = String.format("Dear Customer, your monthly instalment for %s is due in %s days. Please plan for payment. %s", insuranceType, days, policyInfo);
            smsMessageBean.setMessage(smsMessage);

            emailMessage = String.format("Dear Customer, <br> your monthly instalment for %s is due in %s days. Please plan for payment. <br> %s", insuranceType, days, policyInfo);
            mailMessageBean.setMessage(emailMessage);
            mailMessageBean.setSubject("Policy Installment Reminder");
        } else if (eventType.equalsIgnoreCase("Policy Renewal Due")) {
            smsMessage =String.format("Dear Customer, your %s policy is due for renewal in %s days. Kindly review and proceed with payment.%s", insuranceType,days, policyInfo);
            smsMessageBean.setMessage(smsMessage);

            emailMessage = String.format("Dear Customer, <br> your %s policy is due for renewal in %s days. Kindly review and proceed with payment.<br> %s", insuranceType, days, policyInfo);
            mailMessageBean.setMessage(emailMessage);
            mailMessageBean.setSubject("Policy Renewal Reminder");
        } else if (eventType.equalsIgnoreCase("Policy Accrual Due")) {
            smsMessageBean.setMessage(String.format("Dear Customer, your %s policy is due for accrual payments in %s days. Kindly review and proceed with payment.%s", insuranceType,days, policyInfo));
            emailMessage = String.format("Dear Customer, <br> your %s policy is due for accrual payments in %s days. Kindly review and proceed with payment.%s", insuranceType,days, policyInfo);
            mailMessageBean.setMessage(emailMessage);
            mailMessageBean.setSubject("Policy Accrual Reminder");
        }

        if (clientEmail.equalsIgnoreCase("")) {
            smsMessageBean.setSendTo(clientPhone);
            mailer.sendSmsAttachmentsANE(smsMessageBean, 0L, eventType, null);
        } else {
            mailMessageBean.setSendTo(clientEmail);
            sendEmailPublisherBean.sendEmail(mailMessageBean);
        }
    }
    @Override
    public void  notifyPolicyMaker(PolicyTrans policyTrans, String eventType, String Day, BigDecimal bal, BigDecimal paidAmt){
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);

        String insuranceType = policyTrans.getBinder().getBinName();
        String clientName = String.format("%s %s", policyTrans.getClient().getFname(), policyTrans.getClient().getOtherNames());
        String staffName = policyTrans.getCreatedUser().getName();
        String polNo = policyTrans.getPolNo();
        Date coverFrom = policyTrans.getCoverFrom();
        Date coverTo = policyTrans.getCoverTo();
        String riskNote = policyTrans.getRefNo();
        String polRefNo = policyTrans.getRefNo();
        String currencyCode = policyTrans.getTransCurrency().getCurIsoCode();
        String staffEmail = policyTrans.getCreatedUser().getEmail();
        String grossPrem = formatter.format(policyTrans.getBasicPrem());
        String paidAmount = formatter.format(paidAmt);
        String balance = formatter.format(bal);
        String subagentEmail = "";
        String subagentName = "";

        if(policyTrans.getSubAgent() != null) {
            subagentEmail = policyTrans.getSubAgent().getEmail();
            subagentName = policyTrans.getSubAgent().getName();
        }

        //MailMessageBean smsMessageBean = new MailMessageBean();
        MailMessageBean mailMessageBean = new MailMessageBean();

        String notificationMessage = "";
        if (eventType.equalsIgnoreCase("Policy Overdue Payments")) {
            //smsMessageBean.setMessage(String.format("Dear %s, the %s policy is due for accrual payments in %s days. Kindly review and advice the client %s to proceed with payment.",staffName ,polNo, Days,clientName));
            //            notificationMessage = String.format(
            //                    "Dear %s, <br> the %s policy is due for accrual payments on %s. " +
            //                            "Kindly review and advice the client %s to proceed with payment."
            //                    ,staffName ,polNo, Day,clientName);

            notificationMessage = String.format("Dear %s, <br> <br> " +
                    "You are receiving this email as the contact person of the customer as per email subject, " +
                    "and who has outstanding premium balance as per below details. " +
                    "Kindly note that as per existing process, notice will be issued to the insurer to cancel cover at 30 days overdue " +
                    "and effect cancellation at 90 days overdue, if premium payment or proof thereof, is not received within the said period. <br>" +
                    "<br> <br> We seek your support in reaching out to the client.  " +
                    "Please submit the payment details by attaching the proof of payment to the specific policy ticket on " +
                    "BancaAssurance System and reassigning to the  BANCASSURANCEOPS queue.",staffName);

            notificationMessage += String.format(
                    "<br> <br> Inured Client %s " +
                    "<br> Policy No %s " +
                    "<br> Insurance Type %s" +
                    "<br> Cover from %s to %s " +
                    "<br> RiskNote no %s " +
                    "<br> Gross Premium %s %s " +
                    "<br> Premium Paid %s %s " +
                    "<br> Balance %s %s " +
                    "<br> AgentName %s",
                    clientName, polNo, insuranceType,
                    coverFrom, coverTo, riskNote,
                    currencyCode, grossPrem,
                    currencyCode, paidAmount,
                    currencyCode, balance,
                    subagentName);

            mailMessageBean.setMessage(notificationMessage);
            mailMessageBean.setSubject(String.format("Outstanding premium Balance Notification for: %s %s %s %s %s", clientName, polNo, polRefNo, currencyCode, balance));
        }

        mailMessageBean.setSendTo(staffEmail);
        mailMessageBean.setSendCC(String.format("%s bancassuranceops@absa.africa", subagentEmail));

        sendEmailPublisherBean.sendEmail(mailMessageBean);
    }

    @Override
    public void notifyMaker(MakerChecker makerChecker) {
        PolicyTrans policyTrans = policyTransRepo.findOne(QPolicyTrans.policyTrans.policyId.eq(makerChecker.getPolicyId()));
        String insuranceType = policyTrans.getBinder().getBinName();
        String productName = policyTrans.getProduct().getProDesc();
        String clientName = String.format("%s %s", policyTrans.getClient().getFname(), policyTrans.getClient().getOtherNames());
        String staffName = makerChecker.getMakerId().getUsername();
        String staffEmail = makerChecker.getMakerId().getEmail();
        String taskStatus = makerChecker.getStatus();
        log.info("maker policy submission {}",makerChecker.getPolicyId());
        final MailMessageBean messageBean = new MailMessageBean();
        String notificationMessage = "";
        if (taskStatus.equalsIgnoreCase("R")) {
            String remarks = makerChecker.getRejectedReason();
            notificationMessage = String.format(
                    "Dear %s,<br> please note that the %s application for customer %s policy number %s has been returned for rework by %s. Kindly action as per the comments. <br> <b> %s </b> <br>",
                    staffName,
                    productName,
                    clientName,
                    policyTrans.getPolNo(),
                    userUtils.getCurrentUser().getEmail(),
                    //makerChecker.getRejectionReason().getReasonDesc(),
                    remarks
            );

            messageBean.setSubject("Task Rework Notification – ("+policyTrans.getPolNo()+") | ("+policyTrans.getTransType()+")");
        } else if (taskStatus.equalsIgnoreCase("N")) {
            notificationMessage = String.format(
                    "Dear %s, <br> <br> please note that the (%s) application for customer %s has been successfully submitted for processing. With the Below Information: <br><br>",
                    staffName,
                    productName,
                    clientName
                    //insuranceType,
            );
            String subject = "Task Submission Notification – ("+policyTrans.getPolNo()+") | ("+policyTrans.getTransType()+")";
            messageBean.setSubject(subject);
            notificationMessage += transTypeBasedMessage(policyTrans);
        }

        String finalNotification  = notificationMessage;
        messageBean.setMessage(finalNotification);
        messageBean.setSendTo(staffEmail);
//        if (checker != null) {
//            messageBean.setSendCC(String.format("%s,%s,bancassuranceops@absa.africa", maker.getEmail(), checker.getEmail()));
//        } else {
            messageBean.setSendCC("bancassuranceops@absa.africa");
//        }
        sendEmailPublisherBean.sendEmail(messageBean);
    }

    private String getFrequency(String frequency){
        if (frequency.equalsIgnoreCase("D")) {
            return  "Daily";
        } else if (frequency.equalsIgnoreCase("W")) {
            return  "Weekly";
        } else if (frequency.equalsIgnoreCase("M")) {
            return  "Monthly";
        } else if (frequency.equalsIgnoreCase("Q")) {
            return  "Quarterly";
        } else if (frequency.equalsIgnoreCase("S")) {
            return  "Semi-Annually";
        } else if (frequency.equalsIgnoreCase("A")) {
            return  "Annually";
        } else if (frequency.equalsIgnoreCase("SG")) {
            return  "Single";
        }
        return "";
    }



    private String policyEmailDetails(PolicyTrans policyTrans) {
        if (policyTrans == null) {
            return "No policy details available.";
        }

        String coverBusinessType = "General";
        if ("L".equalsIgnoreCase(policyTrans.getBusinessType())) {
            coverBusinessType = "Life";
        }

        String branchName = policyTrans.getBranch() != null ? policyTrans.getBranch().getObName() : "Unknown";
        String frequency = getFrequency(policyTrans.getFrequency());
        String currencyCode = policyTrans.getTransCurrency() != null ? policyTrans.getTransCurrency().getCurIsoCode() : "";

        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US); // You can customize the locale
        String sumInsured = policyTrans.getSumInsured() != null
                ? numberFormat.format(policyTrans.getSumInsured())
                : "0";
        String premium = policyTrans.getPremium() != null
                ? numberFormat.format(policyTrans.getPremium())
                : "0";

        return String.format(
                "Branch: %s <br>" +
                        "Cover From: %s <br>" +
                        "Cover To: %s <br>" +
                        "Cover Business Type: %s <br>" +
                        "Frequency: %s <br>" +
                        "Sum Insured: %s %s <br>" +
                        "Premium: %s %s",
                branchName,
                policyTrans.getCoverFrom(),
                policyTrans.getCoverTo(),
                coverBusinessType,
                frequency,
                currencyCode,
                sumInsured,
                currencyCode,
                premium
        );
    }


    private String transTypeBasedMessage(PolicyTrans policyTrans){
        String message = "";

        String curentMessage  =  policyEmailDetails(policyTrans);

        if(policyTrans.getTransType().equalsIgnoreCase("NB")){
            message = curentMessage;
        }else if(policyTrans.getTransType().equalsIgnoreCase("CO")){
            message = curentMessage;

        }else if(policyTrans.getTransType().equalsIgnoreCase("EN")){

            message = "<table> <tr> <th> To </th>  <th> From </th> </tr> <tr> <td>"+curentMessage;
            message += "</td> <td>"+ policyEmailDetails(policyTrans.getPreviousTrans())+"</td> </tr> </table>";

        } else if(policyTrans.getTransType().equalsIgnoreCase("CN")){
            message = curentMessage;

        } else  if(policyTrans.getTransType().equalsIgnoreCase("EX")){
            message = curentMessage;

        }
        return message;
    }

    @Override
    @Transactional
    public void rejectTask(Long taskId, Long reasonId, String reason) throws BadRequestException {
        if (!makerCheckerRepo.exists(taskId)) {
            throw new BadRequestException("The Task with this ID does not exist..Cannot continue..");
        }
        User checkerId = userUtils.getCurrentUser();
        MakerChecker rejectedTask = makerCheckerRepo.findOne(taskId);
        if (rejectedTask.getMakerId() == checkerId) {
            throw new BadRequestException("You can't reject a task you've initiated");
        }
        String status = "R";

        if((reasonId != null) && (reason != null)){
            status = "I";
        }else if(reason == null){
            throw new BadRequestException("Provide a comment to guide on the action to be done.");
        }
        if (rejectedTask.getTaskType().equals("CL")) {
            Gson gson = new GsonBuilder()
                    .setDateFormat("MMM dd, yyyy HH:mm:ss z")
                    .create();
            ClaimBookings claimBookings = claimsBookingRepo.findOne(
                    gson.fromJson(rejectedTask.getTaskJson(), ClaimDetailsDTO.class).getClmId()
            );
            if (claimBookings != null) {
                claimBookings.setApprovalStatus("R");
                claimBookings.setClaimStatus("R");
                claimsBookingRepo.save(claimBookings);
            }
        }

        makerCheckerRepo.updateRejectedTask(taskId, reasonId, reason, checkerId,status);
        EscalationActivityLogger rejectedTaskActivity = new EscalationActivityLogger();
        rejectedTaskActivity.setTask(rejectedTask);
        rejectedTaskActivity.setUser(checkerId);
        rejectedTaskActivity.setActivityTime(new Date());
        rejectedTaskActivity.setEscalationLevel(checkerId.getEscalationLevel());
        rejectedTaskActivity.setComments(rejectedTask.getRejectedReason());
        rejectedTaskActivity.setSystemAction("Record rejected by checker");
        activityLoggerRepository.save(rejectedTaskActivity);
    }
    @Override
    public Object findMakerCheckerTask(Long taskId) {
        MakerChecker makerChecker = makerCheckerRepo.findOne(taskId);
        String taskJson = makerChecker.getTaskJson();
        if (makerChecker.getTaskType().equalsIgnoreCase("CL")) {
            Gson gson = new GsonBuilder()
                    .setDateFormat("MMM dd, yyyy HH:mm:ss z") // Match createClaim format
                    .create();
            try {
                ClaimDetailsDTO claimDetailsDTO = gson.fromJson(taskJson, ClaimDetailsDTO.class);
                return new Gson().toJson(claimDetailsDTO);
            } catch (Exception e) {
                e.printStackTrace();
                return taskJson; // Fallback to raw taskJson instead of null
            }

        } else if (makerChecker.getTaskType().equalsIgnoreCase("QT")) {

            CreateQuoteDTO createQuoteDTO = new Gson().fromJson(taskJson, CreateQuoteDTO.class);
            return new Gson().toJson(createQuoteDTO);

        } else if (makerChecker.getTaskType().equalsIgnoreCase("RC")) {
            Gson gson = new GsonBuilder()
                    .setDateFormat("dd/MM/yyyy") // Match the format in the JSON object
                    .create();

            try {
                System.out.println("json output: " + taskJson);
                ReceiptsDTO receiptsDTO = gson.fromJson(taskJson, ReceiptsDTO.class);
                return new Gson().toJson(receiptsDTO);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else if (makerChecker.getTaskType().equalsIgnoreCase("LP")) {
            PolicyCreateDTO policyCreateDTO = new Gson().fromJson(taskJson, PolicyCreateDTO.class);
            return new Gson().toJson(policyCreateDTO);

        }

        return taskJson;
    }

    @Override
    public Object findMakerTaskDetail(Long taskId) {
        MakerChecker makerChecker = makerCheckerRepo.findOne(taskId);
        String taskJson = makerChecker.getTaskJson();
        String rejectedReason = makerChecker.getRejectedReason();
        if (makerChecker.getTaskType().equalsIgnoreCase("CL")) {
            Gson gson = new GsonBuilder()
                    .setDateFormat("MMM dd, yyyy, hh:mm:ss a")
                    .create();
            ClientDTO clientDTO = gson.fromJson(taskJson, ClientDTO.class);
            clientDTO.setComment(rejectedReason);
            return new Gson().toJson(clientDTO);
        } else if (makerChecker.getTaskType().equalsIgnoreCase("RC")) {
            Gson gson = new GsonBuilder()
                    .setDateFormat("dd/MM/yyyy")
                    .create();
            ReceiptTrans receiptTrans = gson.fromJson(taskJson, ReceiptTrans.class);

            ArrayList<SystemTransactions> transactionsList = new ArrayList<>();
            ArrayList<SystemTransactionsTemp> transactionsTempList = new ArrayList<>();
            ArrayList<PolicyTrans> policyTransList = new ArrayList<>();
            for (int i = 0; i < receiptTrans.getDetails().size(); i++) {
                ReceiptTransDtls details = receiptTrans.getDetails().get(i);
                if (details.getTransNo() != null) {
                    long transNo = details.getTransNo();
                    if (receiptTrans.getReceiptType().equalsIgnoreCase("L")) {
                        PolicyTrans policyTrans = policyTransRepo.findOne(transNo);
                        policyTransList.add(policyTrans);
                        receiptTrans.setPolicyTransList(policyTransList);
                    } else if (receiptTrans.getReceiptType().equalsIgnoreCase("N") || receiptTrans.getReceiptType().equalsIgnoreCase("COM")) {
                        SystemTransactions systemTransactions = systemTransactionsRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(transNo));
                        transactionsList.add(systemTransactions);
                        receiptTrans.setTransactions(transactionsList);
                    }
                } else if (details.getTransTempNo() != null) {
                    long transTempNo = details.getTransTempNo();
                    if (receiptTrans.getReceiptType().equalsIgnoreCase("L")) {
                        PolicyTrans policyTrans = policyTransRepo.findOne(transTempNo);
                        policyTransList.add(policyTrans);
                        receiptTrans.setPolicyTransList(policyTransList);
                    } else if (receiptTrans.getReceiptType().equalsIgnoreCase("N") || receiptTrans.getReceiptType().equalsIgnoreCase("COM")) {
                        SystemTransactionsTemp systemTransactionsTemp = systemTransactionsTempRepo.findOne(QSystemTransactionsTemp.systemTransactionsTemp.tempTransno.eq(transTempNo));
                        transactionsTempList.add(systemTransactionsTemp);
                        receiptTrans.setTransactionsTemp(transactionsTempList);
                    }
                }
            }
            receiptTrans.setUserId(receiptTrans.getReceiptUser().getId());
            receiptTrans.setBranchName(receiptTrans.getBranch().getObName());
            receiptTrans.setInsuranceName(receiptTrans.getInsurance().getName());
            if (receiptTrans.getCollectionAccount() != null) {
                receiptTrans.setPayId(receiptTrans.getCollectionAccount().getCaId());
            }

//            receiptsDTO.setComment(rejectedReason);


            return receiptTrans;
        }
        return null;
    }

    @Override
    public List<UserDTO> findEligibleCheckers(String permissionName, String searchParam) {
        List<Object[]> users = userRepository.findByRoleAndPermission(permissionName);
        List<UserDTO> userDTOs = new ArrayList<>();
        for (Object[] user : users) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(((BigInteger) user[0]).longValue());
            userDTO.setUsername((String) user[1]);
            userDTO.setName((String) user[2]);
            if (user[3] != null) {
                userDTO.setAbsaNo((String) user[3]);
            }
            userDTOs.add(userDTO);
        }
        if (searchParam != null && !searchParam.isEmpty()) {
            return userDTOs.stream()
                    .filter(userDTO -> userDTO.getUsername().toLowerCase().contains(searchParam.toLowerCase()) ||
                            userDTO.getName().toLowerCase().contains(searchParam.toLowerCase()) ||
                            (userDTO.getAbsaNo() != null && userDTO.getAbsaNo().toLowerCase().contains(searchParam.toLowerCase()))
                    )
                    .collect(Collectors.toList());
        }
        return userDTOs;
    }

    @Override
    @Transactional
    public void resubmitTask(Long taskId, String taskType, Object updatedData, List<Long> checkerIds) throws BadRequestException {
        Logger log = LoggerFactory.getLogger(getClass());
        log.info("Resubmitting task with taskId: {}, taskType: {}", taskId, taskType);
        MakerChecker makerChecker = makerCheckerRepo.findOne(taskId);

        if (taskType == null || taskType.trim().isEmpty()) {
            throw new BadRequestException("Task type is required");
        }
        if ("ALP".equalsIgnoreCase(taskType) && !(updatedData instanceof PolicyTrans)) {
            throw new BadRequestException("Invalid data type for ALP task");
        }
        if ("ANP".equalsIgnoreCase(taskType) && !(updatedData instanceof PolicyCreateDTO)) {
            throw new BadRequestException("Invalid data type for ANP task");
        }

        String updatedJson;
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
        String resubmissionComment = null;

        if (updatedData instanceof ClientDTO && "CL".equalsIgnoreCase(taskType)) {
            ClientDTO clientDTO = (ClientDTO) updatedData;
            updatedJson = gson.toJson(clientDTO);
        } else if (updatedData instanceof ReceiptTrans && "RC".equalsIgnoreCase(taskType)) {
            ReceiptTrans receiptTrans = (ReceiptTrans) updatedData;
            updatedJson = gson.toJson(receiptTrans);
        } else if (updatedData instanceof PolicyCreateDTO && "ANP".equalsIgnoreCase(taskType)) {
            PolicyCreateDTO policyCreateDTO = (PolicyCreateDTO) updatedData;
            updatedJson = gson.toJson(policyCreateDTO);
            if (policyCreateDTO.getResubmissionComment() != null && !policyCreateDTO.getResubmissionComment().trim().isEmpty()) {
                resubmissionComment = policyCreateDTO.getResubmissionComment();
            }
        } else if ("ALP".equalsIgnoreCase(taskType)) {
            updatedJson = "{}";
        } else if ("IM".equalsIgnoreCase(taskType)) {
            updatedJson = "{}";
        } else {
            throw new BadRequestException("Unsupported task type");
        }

        // MANDATORY comment validation for ANP
        if (("ANP".equalsIgnoreCase(taskType) ) &&
                (resubmissionComment == null || resubmissionComment.trim().isEmpty())) {
            throw new BadRequestException("Resubmission comment is required for Rejected General Policy ");
        }

        if ("ANP".equalsIgnoreCase(taskType) && resubmissionComment != null && !resubmissionComment.trim().isEmpty()) {
            RtsAudit rtsAudit = new RtsAudit();
            rtsAudit.setResubmissionComment(resubmissionComment);
            rtsAudit.setAuditTime(new Date());
            rtsAudit.setPolicyInfo(updatedJson);
            rtsAudit.setPolicyId(makerChecker.getPolicyId());
           // rtsAudit.setCheckerId(userUtils.getCurrentUser());
            rtsAudit.setMakerId(userUtils.getCurrentUser());
//            rtsAudit.setRejectionReason(makerChecker.getRejectionReason());
            rtsAudit.setRejectionReason(null); // Clear the RejectedReasons object
//            rtsAudit.setUserRejectedReason(makerChecker.getRejectedReason() != null ? makerChecker.getRejectedReason() : (rejectionReason != null ? rejectionReason.getReasonDesc() : "N/A"));
            rtsAudit.setUserRejectedReason(makerChecker.getRejectionReason() != null ? makerChecker.getRejectionReason().getReasonDesc() : "N/A");
            rtsAudit.setTaskCode(makerChecker.getTaskCode());
            rtsAuditRepository.save(rtsAudit);
            log.info("Saved RtsAudit for taskType: {}, policyId: {}", taskType, makerChecker.getPolicyId());
        }

        if (resubmissionComment != null && !resubmissionComment.trim().isEmpty()) {
            makerChecker.setResubmissionComment(resubmissionComment);
            log.info("Setting resubmissionComment on makerChecker: {}", resubmissionComment);
        }

        if (checkerIds != null && !checkerIds.isEmpty()) {
            makerChecker.setAssignedCheckers(checkerIds.toString());
        }

        makerChecker.setTaskJson(updatedJson);
        makerChecker.setStatus("N");
        makerChecker.setModifiedDate(new Date());
        makerCheckerRepo.save(makerChecker);
        log.info("Saved MakerChecker with resubmissionComment: {}", makerChecker.getResubmissionComment());

        List<EscalationRecord> checkerRecords = escalationRecordRepository.findByTask(makerChecker);
        if (!checkerRecords.isEmpty()) {
            EscalationRecord existingRecord = checkerRecords.get(0);
            BeanUtils.copyProperties(makerChecker, existingRecord, "escId", "task");
            existingRecord.setCheckTime(null);
            existingRecord.setVerifier(null);
            escalationRecordRepository.save(existingRecord);
        } else {
            EscalationRecord newRecord = new EscalationRecord();
            newRecord.setCheckTime(makerChecker.getCheckDate());
            newRecord.setVerifier(makerChecker.getCheckerId());
            newRecord.setCurrentEscalationLevel(makerChecker.getCheckerId() != null ? makerChecker.getCheckerId().getEscalationLevel() : null);
            newRecord.setOwner(makerChecker.getMakerId());
            newRecord.setTask(makerChecker);
            escalationRecordRepository.save(newRecord);
        }

        User resubmittingUser = userUtils.getCurrentUser();
        MakerChecker taskToResubmit = makerCheckerRepo.findOne(taskId);
        EscalationActivityLogger rejectedTaskActivity = new EscalationActivityLogger();
        rejectedTaskActivity.setTask(taskToResubmit);
        rejectedTaskActivity.setUser(resubmittingUser);
        rejectedTaskActivity.setActivityTime(new Date());
        rejectedTaskActivity.setEscalationLevel(resubmittingUser.getEscalationLevel());
        rejectedTaskActivity.setSystemAction("Record resubmitted for verification");
        activityLoggerRepository.save(rejectedTaskActivity);
    }
    @Override
    public DataTablesResult<MakerCheckDTO> getMakerTasks(DataTablesRequest request) {
        final String search = (request.getSearch() != null && request.getSearch().getValue() != null) ? "%" + request.getSearch().getValue() + "%" : "%%";
        Long makerId = userUtils.getCurrentUser().getId();
        List<Object[]> makerTasks = makerCheckerRepo.findMakerTasks(makerId, search.toLowerCase(), request.getPageNumber(), request.getPageSize());
        long rowCount = 0L;
        if (!makerTasks.isEmpty()) {
            rowCount = makerTasks.size();
        }
        final List<MakerCheckDTO> makerCheckDTOList = new ArrayList<>();
        for (Object[] makerTask : makerTasks) {
            MakerCheckDTO makerCheckDTO = new MakerCheckDTO();
            makerCheckDTO.setTaskId(((BigInteger) makerTask[0]).longValue());
            makerCheckDTO.setTaskName((String) makerTask[1]);
            makerCheckDTO.setMadeOnDate((Date) makerTask[2]);
            if (makerTask[3] != null) {
                makerCheckDTO.setCheckedBy(userRepository.findOne(QUser.user.id.eq(((BigInteger) makerTask[3]).longValue())).getName());
            }
            String status = (String) makerTask[4];
            if (status.equalsIgnoreCase("N")) {
                makerCheckDTO.setStatus("Pending Approval");
                makerCheckDTO.setRejectedReason("Pending Approval");
            } else if (status.equalsIgnoreCase("R")) {
                makerCheckDTO.setStatus("Rejected");
                makerCheckDTO.setRejectionReasonDesc((String) makerTask[7]);
            }
            makerCheckDTO.setTaskType((String) makerTask[5]);
            makerCheckDTO.setCheckedOnDate((Date) makerTask[6]);
            if (makerTask[8] != null) {
                makerCheckDTO.setPolicyId(((BigInteger) makerTask[8]).longValue());
            }
            if (makerTask[9] != null) {
                makerCheckDTO.setAcctId(((BigInteger) makerTask[9]).longValue());
            }
            if (makerTask[11] != null) {
                makerCheckDTO.setTaskCode(((BigInteger) makerTask[11]).longValue());
            }
//            if (makerTask[10] != null) {
//                makerCheckDTO.setRejectionReasonDesc((String) makerTask[10]);
//            }
            makerCheckDTOList.add(makerCheckDTO);
        }
        Page<MakerCheckDTO> page = new PageImpl<>(makerCheckDTOList, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    public String usersRole(Long userID) {
        List<UserRole> roles = userRolesRepo.findByUserId(userID);
        if (roles == null || roles.isEmpty()) {
            return "No roles assigned.";
        }

        return roles.stream()
                .map(userRole -> userRole.getRoles().getRoleName())
                .collect(Collectors.joining(", "));
    }


    //make dynamic in future
    public String signatureTemplate(User maker, PolicyTrans policyTrans){
        String email = "";
        String name = "";
        String role = "";
        log.info("policy info, {}",policyTrans.getInterfaceType());
        if ("A".equalsIgnoreCase(policyTrans != null ? policyTrans.getInterfaceType() : null)) {
            //the one who authorized the policy accrual
            if(userUtils.getCurrentUser().getEmail() != null && !userUtils.getCurrentUser().getEmail().isEmpty()) {
                email = userUtils.getCurrentUser().getEmail();
                name = userUtils.getCurrentUser().getName();
                role = usersRole(userUtils.getCurrentUser().getId());
            }
        }else{
            //the one who created the policy receipt
            MakerChecker makerChecker = makerCheckerRepo.findByPolAndReceipt(policyTrans.getPolicyId());
            if (makerChecker != null && makerChecker.getMakerId() != null && !makerChecker.getMakerId().getEmail().isEmpty()) {
                email = makerChecker.getMakerId().getEmail();
                name = makerChecker.getMakerId().getName();
                role = usersRole(makerChecker.getMakerId().getId());
            }else {
                email = "";
                name = "";
                role ="";
            }
        }

        String signature = "<br/>";
        //if maker is not founds for the receipt use the generic absa sisgnature
        if(email != null || name != null) {

            if (email.equalsIgnoreCase("Nelson.Alubia@absa.africa")) {
                signature = "Regards,<br>" +
                        " <br>" +
                        "Nelson Alubia<br>" +
                        "Reconciliation Manager<br>" +
                        "Bancassurance Operations<br>" +
                        "<br>" +
                        "<b>T</b> +254 (0)20 4926540<br>" +
                        "<b>M</b> +254 (0)721676014<br>" +
                        "<b>E</b> Nelson.Alubia@absa.africa<br>" +
                        "<br>" +
                        "Absa, 1st Floor, Bishops Gate Building,5th Ngong Avenue, Kenya<br>" +
                        "www.absabank.co.ke<br>";
            } else if (email.equalsIgnoreCase("Anthony.Obade@absa.africa")) {
                signature = "Regards,<br>" +
                        " <br>" +
                        "Anthony Obade<br>" +
                        "Claims<br>" +
                        "Bancassurance Ops<br>" +
                        " <br>" +
                        "<b>T</b> +254 20 4926 540<br>" +
                        "<b>M</b> +254 732 974 790<br>" +
                        "<b>E</b> Anthony.Obade@absa.africa<br>" +
                        " <br>" +
                        "Absa Bank Kenya PLC, 1st Floor, Bishops Gate, 5th Avenue Ngong<br>" +
                        " <br>" +
                        "www.absabank.co.ke<br>" +
                        "<br>" +
                        "Absa Bank Kenya PLC is regulated by the Central Bank of Kenya <br>";
            } else if (email.equalsIgnoreCase("Anastasia.tonui@absa.africa")) {
                signature = "Kind Regards, <br>" +
                        "Anastasia Tonui <br>" +
                        " <br>" +
                        "Operations Analyst <br>" +
                        "Bancassurance Operations <br>" +
                        " <br>" +
                        "<b>M</b> +254 (0)721 843 498 <br>" +
                        "<b>E</b> Anastasia.tonui@absa.africa <br>" +
                        " <br>" +
                        "Absa, 1st Floor, Bishops Gate Building,5th Ngong Avenue,Kenya <br>";
            } else if (email.equalsIgnoreCase("kennedy.wangui@absa.africa")) {
                signature = "Regards,<br>" +
                        "Kennedy Wangui<br>" +
                        "Operations Analyst<br>" +
                        "Bancassurance Ops – COO<br>" +
                        " <br>" +
                        "<b>M</b> +254 712 955 066<br>" +
                        "<b>E</b> kennedy.wangui@absa.africa<br>" +
                        " <br>" +
                        "2nd Floor, Bishops Gate Building, 5th Ngong Avenue, Nairobi, Kenya<br>" +
                        " <br>" +
                        "www.absabank.co.ke<br>";
            } else if (email.equalsIgnoreCase("David.Macharia@absa.africa")) {
                signature = "Regards,<br>" +
                        " <br>" +
                        "David Macharia Mwangi<br>" +
                        "Bancassurance Claims and Underwriting Manager<br>" +
                        "Operations and Technology<br>" +
                        " <br>" +
                        "<b>T</b>: +254 20 4926 540<br>" +
                        "<b>M</b>: +254 721 696 290<br>" +
                        "<b>E</b>: David.Macharia@absa.africa<br>" +
                        "Absa, 2nd Floor, Bishops Gate Building, North Wing, 5th Ngong Avenue, Nairobi, Kenya<br>" +
                        " <br>" +
                        "www.absa.africa<br>" +
                        " <br>" +
                        "Absa Bank Kenya PLC is regulated by the Central Bank of Kenya<br>";
            } else if (email.equalsIgnoreCase("hesbonochieng.ochola@absa.africa")) {
                signature = "Kind Regards, <br>" +
                        "Hesbon Ochola <br>" +
                        " <br>" +
                        "Operations Analyst <br>" +
                        "Bancassurance Operations <br>" +
                        " <br>" +
                        "<b>M</b> +254 (0)734 794 835<br>" +
                        "<b>E</b> hesbonochieng.ochola@absa.africa <br>" +
                        "Absa, 1st Floor ,Bishops Gate Building,5th Ngong Avenue,Kenya <br>";
            } else if (email.equalsIgnoreCase("Dorcas.kunusia@absa.africa")) {
                signature = "Dorcas kunusia<br>" +
                        "Operations Analyst <br>" +
                        "Bancassurance Operations <br>" +
                        "<b>M</b> +254 (0)726908618 <br>" +
                        "<b>E</b> Dorcas.kunusia@absa.africa <br>" +
                        " <br>" +
                        "Absa, 2nd Floor ,Bishops Gate Building,5th Ngong Avenue,Kenya <br>";
            } else if (email.equalsIgnoreCase("Margaret.Mwaura2@absa.africa")) {
                signature = "Best Regards,<br>" +
                        " <br>" +
                        "Margaret Mwaura<br>" +
                        "Operations Analyst-BANCAOPERATIONS<br>" +
                        " <br>" +
                        "<b>M</b> +254 791 204 335<br>" +
                        "<b>E</b>  Margaret.Mwaura2@absa.africa<br>" +
                        "<br>" +
                        "Bishops Gate, Ground Floor<br>" +
                        "5th Ngong Avenue, Nairobi<br>" +
                        " <br>" +
                        "www.absabank.co.ke<br>" +
                        " <br>" +
                        "Absa Bank Kenya PLC is regulated by the Central Bank of Kenya<br>";
            } else if (email.equalsIgnoreCase("antonia.ngaywa@absa.africa")) {
                signature = "Regards,<br>" +
                        "Antonia Ngaywa <br>" +
                        "Operations Analyst<br>" +
                        "Bancassurance Ops<br>" +
                        "<br>" +
                        "<br>" +
                        "<b>T</b> +254 743 933 122<br>" +
                        "<b>E</b> antonia.ngaywa@absa.africa<br>" +
                        "<br>" +
                        "<br>" +
                        "2nd Floor, Bishops Gate Building, 5th Ngong Avenue,<br>" +
                        "www.absabank.co.ke<br>" +
                        "<br>" +
                        "<br>" +
                        "Absa Bank Kenya PLC is regulated by the Central Bank of Kenya<br>";
            } else if (email.equalsIgnoreCase("george.maina4@absa.africa")) {
                signature = "Regards,<br>" +
                        "George Mutuma<br>" +
                        "Operations Analyst<br>" +
                        "Bancassurance Ops – COO<br>" +
                        " <br>" +
                        "<b>M</b> +254 700 289 831<br>" +
                        "<b>E</b>: george.maina4@absa.africa<br>" +
                        " <br>" +
                        "2nd Floor, Bishops Gate Building, 5th Ngong Avenue, Nairobi, Kenya<br>" +
                        "<br>" +
                        "<br>" +
                        "www.absabank.co.ke<br>";
            } else if (email.equalsIgnoreCase("Maureen.Ndinda@absa.africa")) {
                signature = "Regards,<br>" +
                        " <br>" +
                        "Maureen Ndinda<br>" +
                        "Operations Analyst<br>" +
                        "Central Disbursement Team | CROPS <br>" +
                        " <br>" +
                        "<b>T</b>: +254 (0) 20 4925 613 <br>" +
                        "<b>M</b>: +254 (0) 790085365  <br>" +
                        "<b>E</b>: Maureen.Ndinda@absa.africa <br>" +
                        " <br>" +
                        "Absa Bank Kenya PLC, 2nd Floor, Bishops Gate Building, 5th Ngong Ave. <br>" +
                        "P.O. Box 30120-00100, Nairobi, Kenya <br>" +
                        "www.absabank.co.ke<br>";
            } else {
                //    User userStaff = userRepository.findUserByEmail(email);
                // if (userStaff != null) {
                signature = "Regards, <br>" ;
                        if(!name.isEmpty()){
                           signature += name+"<br>";
                        }

                        if(!role.isEmpty()){
                            signature += role+"<br>";
                        }
                        if(!email.isEmpty()) {
                            signature += "<br> <b> E </b>: " + email;
                        }
                       signature += "<br>" +
                        "Absa Bank Kenya PLC, 2nd Floor, Bishops Gate Building, 5th Ngong Ave. <br>" +
                        "P.O. Box 30120-00100, Nairobi, Kenya <br>" +
                        "www.absabank.co.ke<br>";
                // }
            }
        } else{
            signature = "<br>" +
                    "Absa Bank Kenya PLC, 2nd Floor, Bishops Gate Building, 5th Ngong Ave. <br>" +
                    "P.O. Box 30120-00100, Nairobi, Kenya <br>" +
                    "www.absabank.co.ke<br>";
        }

        return signature;
    }
}
