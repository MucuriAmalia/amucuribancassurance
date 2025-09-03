package com.brokersystems.brokerapp.trans.service.impl;

import com.brokersystems.brokerapp.accounts.dtos.SystemTransDTO;
import com.brokersystems.brokerapp.accounts.model.CollectionAccounts;
import com.brokersystems.brokerapp.accounts.model.PaymentAudit;
import com.brokersystems.brokerapp.accounts.model.QCollectionAccounts;
import com.brokersystems.brokerapp.accounts.model.QPaymentAudit;
import com.brokersystems.brokerapp.accounts.repository.CollectionAcctsRepo;
import com.brokersystems.brokerapp.accounts.repository.PaymentAuditRepo;
import com.brokersystems.brokerapp.accounts.repository.RefundRepo;
import com.brokersystems.brokerapp.bulktransactions.models.BulkReceipt;
import com.brokersystems.brokerapp.bulktransactions.models.QBulkReceipt;
import com.brokersystems.brokerapp.bulktransactions.repositories.BulkReceiptRepository;
import com.brokersystems.brokerapp.enums.RevenueItems;
import com.brokersystems.brokerapp.mail.model.MailMessageBean;
import com.brokersystems.brokerapp.mail.model.SendEmailPublisherBean;
import com.brokersystems.brokerapp.medical.model.QSelfFundParams;
import com.brokersystems.brokerapp.medical.model.SelfFundParams;
import com.brokersystems.brokerapp.medical.repository.SelfFundParamsRepo;
import com.brokersystems.brokerapp.security.CheckAuthLimits;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.DateUtilities;
import com.brokersystems.brokerapp.server.utils.NumberToWordsUtils;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.setup.dto.UserDTO;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.*;
import com.brokersystems.brokerapp.setup.service.OrganizationService;
import com.brokersystems.brokerapp.trans.dtos.LifeReceiptsDTO;
import com.brokersystems.brokerapp.trans.model.*;
import com.brokersystems.brokerapp.trans.repository.*;
import com.brokersystems.brokerapp.trans.service.AccountsUtilities;
import com.brokersystems.brokerapp.trans.service.AllocationService;
import com.brokersystems.brokerapp.trans.service.ReceiptService;
import com.brokersystems.brokerapp.trans.utils.HibernateProxyTypeAdapter;
import com.brokersystems.brokerapp.users.dto.MakerCheckDTO;
import com.brokersystems.brokerapp.users.repository.MakerCheckerRepo;
import com.brokersystems.brokerapp.users.service.MakerCheckerService;
import com.brokersystems.brokerapp.uw.controller.PolicyController;
import com.brokersystems.brokerapp.uw.dtos.ReceiptsDTO;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.model.QPolicyTrans;
import com.brokersystems.brokerapp.uw.model.QRiskTrans;
import com.brokersystems.brokerapp.uw.model.RiskTrans;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import com.brokersystems.brokerapp.uw.repository.RiskTransRepo;
import com.brokersystems.brokerapp.webservices.service.MobileMoneyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.BooleanExpression;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.velocity.app.VelocityEngine;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class ReceiptServiceImpl implements ReceiptService {
    private static final Logger log = LoggerFactory.getLogger(PolicyController.class);

    @Autowired
    private SystemTransactionsRepo systemTransactionsRepo;

    @Autowired
    private ReceiptRepository receiptRepo;

    @Autowired
    private SystemTransactionsRepo transRepo;

    @Autowired
    private ReceiptDetailsRepository rctDetailsRepo;

    @Autowired
    private CurrencyRepository currencyRepo;

    @Autowired
    private OrgBranchRepository branchRepository;

    @Autowired
    private SequenceRepository sequenceRepo;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private CheckAuthLimits authLimits;

    @Autowired
    private AllocationService allocService;

    @Autowired
    private OrganizationService orgService;

    @Autowired
    private PaymentModeRepo paymentRepo;

    @Autowired
    private SelfFundParamsRepo fundParamsRepo;

    @Autowired
    private CollectionAcctsRepo collectionAcctsRepo;

    @Autowired
    private DateUtilities dateUtils;

    @Autowired
    private SystemTransRepo systemTransRepo;

    @Autowired
    private GlTransRepo glTransRepo;

    @Autowired
    private AccountsUtilities accountsUtilities;

    @Autowired
    private ReceiptPrintRepo printRepo;

    @Autowired
    private IntegrationDtlsRepo integrationDtlsRepo;

    @Autowired
    private MobileMoneyService mobileMoneyService;

    @Autowired
    private RefundRepo refundRepo;

    @Autowired
    private SettlementRepo settlementRepo;

    @Autowired
    private PolicyTransRepo policyTransRepo;

    @Autowired
    private PaymentAuditRepo auditRepo;

    @Autowired
    private DateUtilities dateUtilities;

    @Autowired
    private SystemTransactionsTempRepo systemTransactionsTempRepo;

    @Autowired
    private AccountRepo accountRepo;


    @Autowired
    private DataSource dataSource;
    @Autowired
    private MakerCheckerService makerCheckerService;
    @Autowired
    private ClientRepository clientRepo;
    @Autowired
    private MakerCheckerRepo makerCheckerRepo;
    @Autowired
    private RiskTransRepo riskTransRepo;
    @Autowired
    private BinderDetRepo binderDetRepo;
    @Autowired
    private SendEmailPublisherBean sendEmailPublisherBean;
    @Autowired
    private VelocityEngine velocityEngine;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BulkReceiptRepository bulkReceiptRepo;
    @Autowired
    private CommissionTransRepo commissionTransRepo;
    @Autowired
    private CommissionPaymentsRepo commissionPaymentsRepo;

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<ReceiptTrans> findAllReceipts(DataTablesRequest request) throws IllegalAccessException {
        BooleanExpression pred = QReceiptTrans.receiptTrans.printed.isNull();
        Page<ReceiptTrans> page = receiptRepo.findAll(pred.and(request.searchPredicate(QReceiptTrans.receiptTrans)), request);
        return new DataTablesResult(request, page);
    }

    @Override
    public Page<SystemTransactions> findAgentCommisionTrans(String paramString, Pageable paramPageable, Long insuranceId) throws IllegalAccessException {
        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred = QSystemTransactions.systemTransactions.transdc.eq("D")
                    .and(QSystemTransactions.systemTransactions.isNotNull())
                    .and(QSystemTransactions.systemTransactions.transType.in("COMM"))
                    .and(QSystemTransactions.systemTransactions.clientType.eq("A"))
                    .and(QSystemTransactions.systemTransactions.agent.acctId.eq(insuranceId))
                    .and(QSystemTransactions.systemTransactions.balance.gt(BigDecimal.ZERO));
        } else {
            pred = QSystemTransactions.systemTransactions.transdc.eq("D")
                    .and(QSystemTransactions.systemTransactions.isNotNull())
                    .and(QSystemTransactions.systemTransactions.transType.in("COMM"))
                    .and(QSystemTransactions.systemTransactions.refNo.containsIgnoreCase(paramString))
                    .and(QSystemTransactions.systemTransactions.clientType.eq("A"))
                    .and(QSystemTransactions.systemTransactions.agent.acctId.eq(insuranceId))
                    .and(QSystemTransactions.systemTransactions.balance.gt(BigDecimal.ZERO));
        }
        return transRepo.findAll(pred, paramPageable);
    }

    @Override
    public Page<SystemTransactions> findCreditorCommisionTrans(String paramString, Pageable paramPageable) throws IllegalAccessException {
        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred = QSystemTransactions.systemTransactions.transdc.eq("D")
                    .and(QSystemTransactions.systemTransactions.isNotNull())
                    .and(QSystemTransactions.systemTransactions.transType.in("COM"))
                    .and(QSystemTransactions.systemTransactions.clientType.eq("I"))
                    .and(QSystemTransactions.systemTransactions.authorised.eq("Y"))
                    .and(QSystemTransactions.systemTransactions.balance.gt(BigDecimal.ZERO));
        } else {
            pred = QSystemTransactions.systemTransactions.transdc.eq("C")
                    .and(QSystemTransactions.systemTransactions.isNotNull())
                    .and(QSystemTransactions.systemTransactions.transType.in("COM"))
                    .and(QSystemTransactions.systemTransactions.refNo.containsIgnoreCase(paramString))
                    .and(QSystemTransactions.systemTransactions.clientType.eq("I"))
                    .and(QSystemTransactions.systemTransactions.authorised.eq("Y"))
                    .and(QSystemTransactions.systemTransactions.balance.gt(BigDecimal.ZERO));
        }
        return transRepo.findAll(pred, paramPageable);
    }

//	@Override
//	@Transactional(readOnly = true)
//	public DataTablesResult<SystemTransDTO> findReceiptTransactions(String paramString, DataTablesRequest request) throws IllegalAccessException {
//		Predicate pred = null;
//		Predicate pred2 = null;
//		if (paramString == null || StringUtils.isBlank(paramString)) {
//			 pred = QSystemTransactions.systemTransactions.transdc.eq("D")
//					.and(QSystemTransactions.systemTransactions.isNotNull())
//					 .and(QSystemTransactions.systemTransactions.transType.notIn("SAG"))
//					 .and(QSystemTransactions.systemTransactions.clientType.eq("C"))
//					.and(QSystemTransactions.systemTransactions.balance.gt(BigDecimal.ZERO));
//
//			pred2 = QSystemTransactionsTemp.systemTransactionsTemp.transdc.eq("D")
//					.and(QSystemTransactionsTemp.systemTransactionsTemp.isNotNull())
//					.and(QSystemTransactionsTemp.systemTransactionsTemp.transType.notIn("SAG"))
//					.and(QSystemTransactionsTemp.systemTransactionsTemp.clientType.eq("C"))
//					.and(QSystemTransactionsTemp.systemTransactionsTemp.balance.gt(BigDecimal.ZERO))
//					.and(QSystemTransactionsTemp.systemTransactionsTemp.authorised.eq("Y"));
//		}
//		else{
//			pred = (QSystemTransactions.systemTransactions.transdc.eq("D")
//					.and(QSystemTransactions.systemTransactions.refNo.containsIgnoreCase(paramString))
//							.and(QSystemTransactions.systemTransactions.clientType.eq("C"))
//					.and(QSystemTransactions.systemTransactions.balance.gt(BigDecimal.ZERO))
//					.and(QSystemTransactions.systemTransactions.transType.notIn("SAG")))
//			        .or(QSystemTransactions.systemTransactions.transdc.eq("D")
//							.and(QSystemTransactions.systemTransactions.policy.polNo.containsIgnoreCase(paramString))
//							.and(QSystemTransactions.systemTransactions.clientType.eq("C"))
//							.and(QSystemTransactions.systemTransactions.balance.gt(BigDecimal.ZERO))
//							.and(QSystemTransactions.systemTransactions.transType.notIn("SAG")));
//
//			pred2 = (QSystemTransactionsTemp.systemTransactionsTemp.transdc.eq("D")
//					.and(QSystemTransactionsTemp.systemTransactionsTemp.refNo.containsIgnoreCase(paramString))
//					.and(QSystemTransactionsTemp.systemTransactionsTemp.clientType.eq("C"))
//					.and(QSystemTransactionsTemp.systemTransactionsTemp.balance.gt(BigDecimal.ZERO))
//					.and(QSystemTransactionsTemp.systemTransactionsTemp.transType.notIn("SAG")))
//					.and(QSystemTransactionsTemp.systemTransactionsTemp.authorised.eq("Y"))
//					.or(QSystemTransactionsTemp.systemTransactionsTemp.transdc.eq("D")
//							.and(QSystemTransactionsTemp.systemTransactionsTemp.policy.polNo.containsIgnoreCase(paramString))
//							.and(QSystemTransactionsTemp.systemTransactionsTemp.clientType.eq("C"))
//							.and(QSystemTransactionsTemp.systemTransactionsTemp.balance.gt(BigDecimal.ZERO))
//							.and(QSystemTransactionsTemp.systemTransactionsTemp.transType.notIn("SAG"))
//							.and(QSystemTransactionsTemp.systemTransactionsTemp.authorised.eq("Y")));
//		}
//		List<SystemTransactionsTemp> transactionsListTemp = systemTransactionsTempRepo.findAll(pred2, paramPageable).getContent();
//		List<SystemTransactions> transactionsList = transRepo.findAll(pred, paramPageable).getContent();
//		List<SystemTransactions> systemTransactionsList = new ArrayList<>();
//		transactionsListTemp.forEach(a -> {
//			SystemTransactions transactions = new SystemTransactions();
//			try {
//				BeanUtils.copyProperties(transactions,a);
//				systemTransactionsList.add(transactions);
//			} catch (IllegalAccessException e) {
//				e.printStackTrace();
//			} catch (InvocationTargetException e) {
//				e.printStackTrace();
//			}
//		});
//		systemTransactionsList.addAll(transactionsList);
//		Page<SystemTransactions> systemTransactionsPage = new PageImpl<>(systemTransactionsList);
//		return systemTransactionsPage;
//	}


    @Override
    @Transactional(readOnly = true)
    public Page<SystemTransDTO> findReceiptTransactions(String param, Long agentId, Pageable request) {
        System.out.println("Agent Id is null: " + (agentId == null));
        if (agentId == null) {
            agentId = -2000L;
        }

              String paramString;
        if (StringUtils.isBlank(param)) {
            paramString = "%%";  // Return all records
        } else {
            paramString = "%" + param.trim() + "%";  // Partial matching
        }

        System.out.println("Search param: " + paramString);
        System.out.println("Agent ID: " + agentId);

        List<Object[]> receiptList = receiptRepo.findDebitTransactions(
                paramString, request.getPageNumber(), agentId, request.getPageSize());

        System.out.println("Results count: " + receiptList.size());

            List<SystemTransDTO> debitTransList = new ArrayList<>();
        long rowCount = 0L;
        if (!receiptList.isEmpty()) rowCount = ((BigInteger) receiptList.get(0)[9]).intValue();

        for (Object[] trans : receiptList) {
            SystemTransDTO transDTO = SystemTransDTO.instance(
                    (trans[0] != null) ? ((BigInteger) trans[0]).longValue() : null,
                    (trans[1] != null) ? ((BigInteger) trans[1]).longValue() : null,
                    (Date) trans[2],
                    null, (String) trans[3], null, (String) trans[4], (String) trans[5],
                    null, (String) trans[6], null, null, null,
                    (BigDecimal) trans[7], (String) trans[8], null);
            debitTransList.add(transDTO);
        }

        return new PageImpl<>(debitTransList, request, rowCount);
    }


    @PreAuthorize("hasAnyAuthority('CREATE_RECEIPT')")
    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public Long createFundReceipt(ReceiptTrans receipt) throws BadRequestException {
        if (receipt.getBrnCode() == null) {
            throw new BadRequestException("Receipt Branch is mandatory");
        }
        if (receipt.getPayId() == null) {
            throw new BadRequestException("Payment Mode is mandatory");
        }

        if (!authLimits.checkAuthorizationLimits("CREATE_RECEIPT", receipt.getReceiptAmount())) {
            throw new BadRequestException("You have no rights to create the transaction...Check your  limits..");
        }
        User user = userUtils.getCurrentUser();
        Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("R");
        if (sequenceRepo.count(seqPredicate) == 0)
            throw new BadRequestException("Sequence for Receipt Transactions has not been setup");
        SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
        Long seqNumber = sequence.getNextNumber();
        final String receiptNo = sequence.getSeqPrefix() + String.format("%05d", seqNumber);
        receipt.setReceiptNo(receiptNo);
        receipt.setCounter(BigInteger.valueOf(seqNumber));

        SystemTrans transaction = new SystemTrans();
        transaction.setDoneDate(new Date());
        transaction.setDoneBy(userUtils.getCurrentUser());
        transaction.setTransLevel("U");
        transaction.setTransCode("RCT");
        transaction.setTransAuthorised("N");
        SystemTrans systemTrans = systemTransRepo.save(transaction);
        List<ReceiptTransDtls> transDtls = new ArrayList<>();
        BigDecimal totalAllocAmount = BigDecimal.ZERO;
        for (ReceiptTransDtls tran : receipt.getDetails()) {
            SelfFundParams selfFundParams = fundParamsRepo.findOne(tran.getTransNo());
            tran.setFundParams(selfFundParams);
            tran.setReceipt(receipt);
            tran.setEndorsementNumber(selfFundParams.getPolicyTrans().getPolRevNo());
            tran.setRctType("INV");
            tran.setRctDC("C");
            transDtls.add(tran);
            totalAllocAmount = totalAllocAmount.add(tran.getRctAmount());
            ProductsDef product = selfFundParams.getPolicyTrans().getProduct();
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
            long subcode = subcodes.get(0);
            //postReceiptAccount(receipt,systemTrans, subcode,tran.getRctAmount());
        }
        if (totalAllocAmount.compareTo(receipt.getReceiptAmount()) != 0) {
            throw new BadRequestException("Total Allocation Amount " + totalAllocAmount + " and Receipt amount "
                    + receipt.getReceiptAmount() + " doesnt tally....");
        }
        rctDetailsRepo.save(transDtls);
        receipt.setBranch(branchRepository.findOne(receipt.getBrnCode()));
        receipt.setCollectionAccount(collectionAcctsRepo.findOne(receipt.getPayId()));
        receipt.setReceiptUser(user);
        receipt.setReceiptTransDate(new Date());
        sequence.setLastNumber(seqNumber);
        sequence.setNextNumber(seqNumber + 1);
        receipt.setDirectReceipt("N");
        sequenceRepo.save(sequence);
        Float amount = receipt.getReceiptAmount().floatValue();
        int figure = (int) Math.floor(amount);
        int cent = (int) Math.floor((amount - figure) * 100.0f);
        String words = "";
        if (cent > 0) {
            words = NumberToWordsUtils.convert(figure) + " and " + NumberToWordsUtils.convert(cent) + " cents";
        } else {
            words = NumberToWordsUtils.convert(figure);
        }
        receipt.setAmountWords(words);

        ReceiptTrans trans = receiptRepo.save(receipt);
        return trans.getReceiptId();
    }

    @PreAuthorize("hasAnyAuthority('CREATE_RECEIPT')")
    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public Long createReceipt(ReceiptTrans receipt, boolean isApproved) throws BadRequestException {
        final Long hashCode = Long.parseLong(String.valueOf(receipt.hashCode()));
        log.info("the receipt {} with hash code{} is: ",hashCode, receipt.getReceiptId());
        if (receipt.getReceiptType() != null && receipt.getReceiptType().equalsIgnoreCase("COM")) {
            if (receipt.getPayId() == null) {
                throw new BadRequestException("Collection Account is mandatory");
            }
        }

        if (receipt.getBrnCode() == null) {
            throw new BadRequestException("Receipt Branch is mandatory...");
        }

        if (!StringUtils.isBlank(receipt.getPaymentRef())) {
            if (receiptRepo.count(QReceiptTrans.receiptTrans.paymentRef.eq(receipt.getPaymentRef()).and(QReceiptTrans.receiptTrans.cancelled.ne("Y").or(QReceiptTrans.receiptTrans.cancelled.isNull()))) > 0) {
                throw new BadRequestException("Receipt Reference Exists......");
            }
        }

        if (receipt.getReceiptType() != null && !receipt.getReceiptType().equalsIgnoreCase("COM")) {
            if (receipt.getInsuranceId() == null)
                throw new BadRequestException("Invalid Insurance Company...");
            receipt.setInsurance(accountRepo.findOne(receipt.getInsuranceId()));
        }
        PolicyTrans policy = null;
        BigDecimal totalAllocAmount = BigDecimal.ZERO;
        for (ReceiptTransDtls tran : receipt.getDetails()) {
            if (tran.getTransNo() == null && tran.getTransTempNo() == null) {
                throw new BadRequestException("Select Transaction to continue...");
            }
            if (!receipt.getReceiptType().equalsIgnoreCase("COM") && tran.getRctAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Receipt Amount cannot be zero or less than Zero...");
            }
            if (tran.getTransNo() != null && receipt.getReceiptType().equalsIgnoreCase("N")) {
                SystemTransactions transaction = transRepo.findByTransno(tran.getTransNo());
                policy = transaction.getPolicy();
            }
            totalAllocAmount = totalAllocAmount.add(tran.getRctAmount());
            System.out.println("Amount..."+tran.getRctAmount());
        }


        if (totalAllocAmount.compareTo(receipt.getReceiptAmount()) != 0) {
            throw new BadRequestException("Total Allocation Amount " + totalAllocAmount + " and Receipt amount "
                    + receipt.getReceiptAmount() + " doesn't tally....");
        }
        if (receipt.getFromFCR() == null) {
            receipt.setFromFCR("N");
        }
        if (!receipt.getFromFCR().equalsIgnoreCase("Y")) {
            if (!authLimits.checkAuthorizationLimits("CREATE_RECEIPT", receipt.getReceiptAmount())) {
                throw new BadRequestException("You have no rights to create the transaction...Check your  limits..");
            }
        }

//		final Long hashCode = Long.parseLong(String.valueOf(receipt.hashCode()));
//		System.out.println(new Gson().toJson(receipt));
//		MakerCheckDTO makerCheckDTO = new MakerCheckDTO();
//		makerCheckDTO.setJson(new Gson().toJson(receipt));
//		makerCheckDTO.setStatus("N");
//		//ClientDef clientDef = clientRepo.findOne(receipt.getClient());
//		AccountDef accountDef = accountRepo.findOne(receipt.getInsuranceId());
//		makerCheckDTO.setTaskName("Created Receipt:Client..."+ receipt.getClient() + " Receipt No " + receipt.getReceiptNo());
//		makerCheckDTO.setTaskType("CL");
//		makerCheckDTO.setTaskCode(hashCode);
//		makerCheckerService.checkExists(makerCheckDTO);
//		makerCheckerService.createMakerChecker(makerCheckDTO);

        User user = userUtils.getCurrentUser() != null ? userUtils.getCurrentUser() : receipt.getReceiptUser();
        Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("R");
        if (sequenceRepo.count(seqPredicate) == 0)
            throw new BadRequestException("Sequence for Receipt Transactions has not been setup");
        SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
        Long seqNumber = sequence.getNextNumber();
        final String receiptNo = sequence.getSeqPrefix() + String.format("%05d", seqNumber);
        receipt.setReceiptNo(receiptNo);
        receipt.setCounter(BigInteger.valueOf(seqNumber));
        receipt.setBranch(branchRepository.findOne(receipt.getBrnCode()));
        if (receipt.getPayId() != null) {
            receipt.setCollectionAccount(collectionAcctsRepo.findOne(receipt.getPayId()));
        }
        receipt.setReceiptUser(user);
        receipt.setReceiptTransDate(new Date());
        sequence.setLastNumber(seqNumber);
        sequence.setNextNumber(seqNumber + 1);
        if (receipt.getDirectReceipt() == null || "off".equalsIgnoreCase(receipt.getDirectReceipt()))
            receipt.setDirectReceipt("N");
        else if ("on".equalsIgnoreCase(receipt.getDirectReceipt()))
            receipt.setDirectReceipt("Y");
        else
            receipt.setDirectReceipt("N");
        List<UserDTO> eligibleCheckers = makerCheckerService.findEligibleCheckers("CREATE_RECEIPT", null);
        List<Long> checkerIds = new ArrayList<Long>();
        for (UserDTO eligibleChecker : eligibleCheckers) {
            checkerIds.add(eligibleChecker.getId());
        }
        if (!isApproved && !makerCheckerRepo.exists(hashCode)) {
            MakerCheckDTO makerCheckDTO = new MakerCheckDTO();

//            Gson gson = new GsonBuilder()
//                    .setDateFormat("dd/MM/yyyy")
//                    .create();
            //System.out.println(new Gson().toJson(receipt));

            //optimize with the dto for better experience in production
            Gson gson = new GsonBuilder()
                    .setDateFormat("dd/MM/yyyy")
                    .registerTypeHierarchyAdapter(HibernateProxy.class, new HibernateProxyTypeAdapter())
                    .create();
            System.out.println(gson.toJson(receipt));
            makerCheckDTO.setJson(gson.toJson(receipt));

            makerCheckDTO.setStatus("N");
            String receiptType = receipt.getReceiptType();
            makerCheckDTO.setTaskName(String.format("%s receipt no %s pending authorization", receiptType.equalsIgnoreCase("L") ? "Life" : receiptType.equalsIgnoreCase("N") ? "General" : receiptType.equalsIgnoreCase("COM") ? "Commission" : "Unknown", receipt.getReceiptNo()));
            makerCheckDTO.setTaskType("RC");
            if (receipt.getReceiptType().equalsIgnoreCase("N") || receipt.getReceiptType().equalsIgnoreCase("L")) {
                long policyId = 0;
                if (receipt.getReceiptType().equalsIgnoreCase("N")) {
                    PolicyTrans policyTrans = null;
                    for (ReceiptTransDtls tran : receipt.getDetails()) {
                        if (tran.getTransNo() != null) {
                            policyTrans = transRepo.findOne(tran.getTransNo()).getPolicy();
                        } else if (tran.getTransTempNo() != null) {
                            policyTrans = systemTransactionsTempRepo.findPolicyByTransTempNo(tran.getTransTempNo());
                        }
                        if (policyTrans != null) {
                            policyId = policyTrans.getPolicyId();
                        }
                    }
                }
                if (receipt.getReceiptType().equalsIgnoreCase("L")) {
                    policyId = receipt.getPolicyId();
                }
                makerCheckDTO.setPolicyId(policyId);
            }
            makerCheckDTO.setAssignedCheckers(checkerIds.toString());
            makerCheckDTO.setTaskCode(hashCode);
            makerCheckerService.checkExists(makerCheckDTO);
            makerCheckerService.createMakerChecker(makerCheckDTO);
            return hashCode;
        }
        List<ReceiptTransDtls> transDtls = new ArrayList<>();
        SystemTrans trans = new SystemTrans();
        trans.setDoneDate(new Date());
        trans.setDoneBy(user);
        trans.setTransLevel("U");
        trans.setTransCode("RCT"); //A way to setup and look up for transaction transcode
        trans.setTransAuthorised("N");
        SystemTrans systemTrans = systemTransRepo.save(trans);
        if (receipt.getReceiptType() != null && !receipt.getReceiptType().equalsIgnoreCase("C")) {
             PolicyTrans policyTrans = null;
            for (ReceiptTransDtls tran : receipt.getDetails()) {
                if (receipt.getReceiptType() != null && "L".equalsIgnoreCase(receipt.getReceiptType())) {
                    PolicyTrans transaction = policyTransRepo.findOne(tran.getTransNo());
                    if (transaction != null) {
                        tran.setReceipt(receipt);
                        tran.setPolicy(transaction);
                        tran.setEndorsementNumber(transaction.getPolRevNo());
                        tran.setRctType("INV");
                        tran.setRctDC("C");
                        transDtls.add(tran);

                        if(transaction.getAuthStatus()!=null && transaction.getAuthStatus().equalsIgnoreCase("A")) {
                            postReceiptAccount(systemTrans, transaction, tran.getRctAmount());
                        }

                    }


                } else if (receipt.getReceiptType() != null && "COM".equalsIgnoreCase(receipt.getReceiptType())) {
                    SystemTransactions transaction = null;
                    SystemTransactionsTemp transactionsTemp = null;
                    if (tran.getTransNo() != null)
                        transaction = transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(tran.getTransNo()));
                    else if (tran.getTransTempNo() != null)
                        transactionsTemp = systemTransactionsTempRepo.findOne(QSystemTransactionsTemp.systemTransactionsTemp.tempTransno.eq(tran.getTransTempNo()));
                    if (transaction != null || transactionsTemp != null) {
                        tran.setReceipt(receipt);
                        tran.setTransaction(transaction);
                        tran.setTransactionsTemp(transactionsTemp);
                        tran.setRctType("INV");
                        tran.setRctDC("C");
                        transDtls.add(tran);
                    }

                } else {

                    SystemTransactions transaction = null;
                    SystemTransactionsTemp transactionsTemp = null;
                    if (tran.getTransNo() != null)
                        transaction = transRepo.findByTransno(tran.getTransNo());
                    else if (tran.getTransTempNo() != null)
                        transactionsTemp = systemTransactionsTempRepo.findOne(QSystemTransactionsTemp.systemTransactionsTemp.tempTransno.eq(tran.getTransTempNo()));
                    if (transaction != null || transactionsTemp != null) {
                        tran.setReceipt(receipt);
                        tran.setTransaction(transaction);
                        tran.setTransactionsTemp(transactionsTemp);
                        if (transaction != null)
                            tran.setEndorsementNumber(transaction.getPolicy().getPolRevNo());
                        else tran.setEndorsementNumber(transactionsTemp.getPolicy().getPolRevNo());
                        tran.setRctType("INV");
                        tran.setRctDC("C");
                        if (transaction != null) {
                            tran.setPolicy(transaction.getPolicy());
                            final PolicyTrans policyTranss = transaction.getPolicy();
                            ProductsDef product = policyTranss.getProduct();
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
                            long subcode = subcodes.get(0);
                            postReceiptAccount(systemTrans,policyTranss,tran.getRctAmount());

                        }
                        else {
                            tran.setPolicy(transactionsTemp.getPolicy());
                            final PolicyTrans policyTranss = transactionsTemp.getPolicy();
                            ProductsDef product = policyTranss.getProduct();
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
                            long subcode = subcodes.get(0);
                            postReceiptAccount(systemTrans,policyTranss,tran.getRctAmount());


                        }
                        transDtls.add(tran);
                    }


                }
                if ("COM".equalsIgnoreCase(receipt.getReceiptType())) {
                    System.out.println("Trans No.." + tran.getTransNo());
                    System.out.println("Trans No.." + tran.getTransaction());
                    SystemTransactions transactions = tran.getTransaction();
                    PaymentAudit paymentAudit = tran.getPaymentAudit();
                    BigDecimal balance = transactions.getBalance();

                    final List<BigInteger> commTrans = auditRepo.findCommissionTrans(paymentAudit.getPaId());

                    if(commTrans.size()!=1){
                        throw new BadRequestException("Unable To get Commission Transactions...");
                    }
                    final List<Object[]> commissionPayments  = commissionPaymentsRepo.getCommissionDebits(commTrans.get(0).longValue());

                    if(commissionPayments.isEmpty()){
                        throw new BadRequestException("Unable To get Commission Transactions...");
                    }

                    System.out.println("Comm Id "+commTrans.get(0).longValue());

                    final Long creditId =(commissionPayments.get(0)[0]!=null)? ((BigInteger)commissionPayments.get(0)[0]).longValue():null;
                    final Long debitId = (commissionPayments.get(0)[1]!=null)?((BigInteger)commissionPayments.get(0)[1]).longValue():null;

                    if ("NML".equalsIgnoreCase(paymentAudit.getTransType())) {
                        policyTrans = paymentAudit.getTransNo().getPolicy();
                    }
                    BigDecimal allocAmount = tran.getRctAmount();
                    System.out.println("Alloc Amout.."+allocAmount);

                    int count = 0;
                        if ("NML".equalsIgnoreCase(paymentAudit.getTransType()) || paymentAudit.getTransType().equalsIgnoreCase("ADM")) {
                            BigDecimal payableComm = paymentAudit.getCommAmount();
                            BigDecimal payableWhtx = paymentAudit.getWhtxAmount();
                            final SystemTransactions agentTrans = paymentAudit.getTransNo();
                            receipt.setInsurance(agentTrans.getAgent());
                            final BigDecimal commPayable = agentTrans.getCommission().abs().subtract(agentTrans.getWhtx().abs());
                            System.out.println("Alloc Amount "+allocAmount+" Payable Amt "+payableComm+" Comm Payable.."+commPayable);
//                            if (allocAmount.compareTo(commPayable) >= 0) {
                                final PolicyTrans policyTranss = paymentAudit.getTransNo().getPolicy();
                                ProductsDef product = policyTranss.getProduct();
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

                            if(payableWhtx==null){
                                throw new BadRequestException("Whtx Amount is mandatory before posting to account...");
                            }
                            if(payableComm==null){
                                throw new BadRequestException("Commission Amount is mandatory before posting to account...");
                            }
                            if(agentTrans.getAgent().getWhtxReceivableAccount()==null){
                                throw new BadRequestException("Whtx Receivable account is not set up for "+agentTrans.getAgent().getName());
                            }
                            if(agentTrans.getAgent().getCommReceivableAccount()==null){
                                throw new BadRequestException("Commission Receivable account is not set up for "+agentTrans.getAgent().getName());
                            }

                            paymentAudit.setPaidStatus("Y");
                            auditRepo.save(paymentAudit);


                                long subcode = subcodes.get(0);

                            GlTransactions debit2 = new GlTransactions();
                            debit2.setAmount(payableWhtx.abs());
                            debit2.setAuthDate(new Date());
                            debit2.setbCuramount(payableWhtx.abs());
                            debit2.setBranch(receipt.getBranch());
                            debit2.setCurrency(receipt.getCollectionAccount().getCurrencies());
                            debit2.setGlAcc(agentTrans.getAgent().getWhtxReceivableAccount());
                            if(payableComm.compareTo(BigDecimal.ZERO) > 0) {
                                if(transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(debitId))!=null) {
                                    debit2.setPolicyTrans(transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(debitId)).getPolicy());
                                    debit2.setSystemTransactions(transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(debitId)));
                                }
                                debit2.setGldc("D");
                            }
                            else{
                                debit2.setGldc("C");
                                if(transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(creditId))!=null) {
                                    debit2.setPolicyTrans(transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(creditId)).getPolicy());
                                    debit2.setSystemTransactions(transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(creditId)));
                                }
                            }
                            debit2.setTransaction(systemTrans);
                            debit2.setTransLevel("U");
                            debit2.setNarration("COMMPOSTING");
                            debit2.setTrntCode("RCT");
                            debit2.setGlYear(dateUtils.getUwYear(new Date()));
                            debit2.setGlMonth(dateUtils.getMonth(new Date()));
                            if(paymentAudit.getTransType().equalsIgnoreCase("NML")) {
                                debit2.setNarration(String.format("Posting Receipt Transaction for Commission Paid  %s, Ref No: %s", receipt.getPaidBy(), receipt.getReceiptNo()));
                            }
                            else if(paymentAudit.getTransType().equalsIgnoreCase("ADM")){
                                debit2.setNarration(String.format("Posting Receipt Transaction for Admin Fee Paid  %s, Ref No: %s", receipt.getPaidBy(), receipt.getReceiptNo()));

                            }
                            glTransRepo.save(debit2);
                            GlTransactions credit = new GlTransactions();
                            credit.setAmount(payableComm.abs());
                            credit.setAuthDate(new Date());
                            credit.setbCuramount(payableComm.abs());
                            credit.setBranch(receipt.getBranch());
                            credit.setCurrency(receipt.getCollectionAccount().getCurrencies());
                            credit.setGlAcc(agentTrans.getAgent().getCommReceivableAccount());
                            if(payableComm.compareTo(BigDecimal.ZERO) > 0) {
                                credit.setGldc("C");
                                if(transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(debitId))!=null) {
                                    credit.setPolicyTrans(transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(debitId)).getPolicy());
                                    credit.setSystemTransactions(transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(debitId)));
                                }
                            }
                            else{
                                credit.setGldc("D");
                                if(transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(creditId))!=null) {
                                    credit.setPolicyTrans(transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(creditId)).getPolicy());
                                    if(debitId!=null)
                                    credit.setSystemTransactions(transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(debitId)));
                                    else if(creditId!=null){
                                        credit.setSystemTransactions(transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(creditId)));
                                    }
                                }
                            }
                            if(paymentAudit.getTransType().equalsIgnoreCase("NML")) {
                                credit.setGlAcc(agentTrans.getAgent().getCommReceivableAccount());
                            }
                            else if(paymentAudit.getTransType().equalsIgnoreCase("ADM")){
                                credit.setGlAcc(agentTrans.getAgent().getAdminReceivableAccount());
                            }
                            credit.setNarration("COMMPOSTING");
                            credit.setPolicyTrans(policyTranss);
                            credit.setTransaction(systemTrans);
                            credit.setTransLevel("U");
                            credit.setTrntCode("RCT");
                            credit.setGlYear(dateUtils.getUwYear(new Date()));
                            credit.setGlMonth(dateUtils.getMonth(new Date()));
                            credit.setNarration(String.format("Posting Receipt Transaction for  %s, Ref No: %s", receipt.getPaidBy(), receipt.getReceiptNo()));
                            glTransRepo.save(credit);
                            balance = balance.subtract(payableComm).add(payableWhtx);
                             count++;
//                            }
                        }

                    receipt.setCommPosted("Y");
                    receiptRepo.save(receipt);
                    transactions.setBalance(balance);
                    transRepo.save(transactions);

                }
            }
            rctDetailsRepo.save(transDtls);

            if ("COM".equalsIgnoreCase(receipt.getReceiptType())) {
                GlTransactions debit = new GlTransactions();
                debit.setAmount(receipt.getReceiptAmount().abs());
                debit.setAuthDate(new Date());
                debit.setbCuramount(receipt.getReceiptAmount().abs());
                debit.setBranch(receipt.getBranch());
                debit.setCurrency(receipt.getCollectionAccount().getCurrencies());
                debit.setGlAcc(receipt.getCollectionAccount().getAccounts());
                debit.setGldc("D");
                debit.setPolicyTrans(policyTrans);
                debit.setNarration("COMMPOSTING");
                debit.setTransaction(systemTrans);
                debit.setTransLevel("U");
                debit.setTrntCode("RCT");
                debit.setGlYear(dateUtils.getUwYear(new Date()));
                debit.setGlMonth(dateUtils.getMonth(new Date()));
                debit.setNarration(String.format("Posting Receipt Transaction for Commission/Admin Fee Paid  %s, Ref No: %s", receipt.getPaidBy(), receipt.getReceiptNo()));
                glTransRepo.save(debit);
            }
        }

        sequenceRepo.save(sequence);
        Float amount = receipt.getReceiptAmount().floatValue();
        int figure = (int) Math.floor(amount);
        int cent = (int) Math.floor((amount - figure) * 100.0f);
        String words = "";
        if (cent > 0) {
            words = NumberToWordsUtils.convert(figure) + " and " + NumberToWordsUtils.convert(cent) + " cents";
        } else {
            words = NumberToWordsUtils.convert(figure);
        }
        receipt.setAmountWords(words);
        ReceiptTrans transs = receiptRepo.save(receipt);
        if (receipt.getReceiptType() != null && "L".equalsIgnoreCase(receipt.getReceiptType())) {
            allocService.allocateLifeReceipt(transs.getReceiptId(), user);
        }
        //DONT SEND EMAIL TO UNDERWRITERS FOR BULK UPLOADS
        if (policy != null && !policy.getTransType().equalsIgnoreCase("BU")) {
            Iterable<RiskTrans> riskTrans = riskTransRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(policy.getPolicyId()));
            MailMessageBean messageBean = new MailMessageBean();
            for (RiskTrans riskTran : riskTrans) {
                String coverType = riskTran.getCovertype().getCovName();
                String insuredName = String.format("%s %s", riskTran.getInsured().getFname(), riskTran.getInsured().getOtherNames());
                messageBean.setSubject(String.format("%s %s %s %s", insuredName, policy.getPolNo(), policy.getRefNo(), coverType));
                //hard code underwriter email for test purposes
                messageBean.setSendTo("Joram.Gatimu@absa.africa");//policy.getAgent().getEmail());
                String message = "";
                BigDecimal balance;
                BigDecimal settlement;
                BigDecimal totalPremium;
                List<Object[]> balanceAndSettlementList = receiptRepo.findPremiumSettlementAndBalance(policy.getPolicyId());
                settlement = (BigDecimal) balanceAndSettlementList.get(0)[1];
                if (policy.getBusinessType().equalsIgnoreCase("N")) {
                    totalPremium = (BigDecimal) balanceAndSettlementList.get(0)[0];
                } else {
                    totalPremium = (BigDecimal) balanceAndSettlementList.get(0)[2];
                }
                balance = totalPremium.subtract(settlement);
                boolean overPayment = balance.compareTo(BigDecimal.ONE) > 0;
                if (overPayment) {
                    message = String.format("Dear Team,<br>Please place cover as attached and share cover notes. Note an overpayment has been done on this Policy Number %s. <br> Kindly note the attached Risk Note Number %s: which you should quote in your cover notes / production reports", policy.getPolNo(), policy.getRefNo());
                }

                if (rctDetailsRepo.count(QReceiptTransDtls.receiptTransDtls.policy.policyId.eq(policy.getPolicyId()).and(QReceiptTransDtls.receiptTransDtls.rctDC.equalsIgnoreCase("D"))) > 0) {
                    boolean additionalFullPayment = balance.compareTo(BigDecimal.ZERO) == 0;
                    boolean additionalPartialPayment = settlement.compareTo(BigDecimal.ZERO) > 0 && balance.compareTo(BigDecimal.ZERO) > 0;
                    if (policy.getProduct().getProDesc().contains("MOTOR") || policy.getProduct().getProShtDesc().contains("MOTOR")) {
                        if (additionalPartialPayment) {
                            message = String.format("Dear Team,<br>Please place cover as attached and share cover notes. Extend Motor certificate by One Month.Payment is as attached. <br> Kindly note the attached Risk Note Number %s: which you should quote in your cover notes / production reports", policy.getRefNo());
                        } else if (additionalFullPayment) {
                            message = String.format("Dear Team,<br>Please place cover as attached and share cover notes. Issue Annual Motor certificate.Payment is as attached.<br> Kindly note the attached Risk Note Number %s: which you should quote in your cover notes / production reports", policy.getRefNo());
                        }
                    } else {
                        message = String.format("Dear Team,<br>Please receipt and allocate the attached additional premium payment.Kindly note the attached Risk Note Number %s: which you should quote in your cover notes / production reports", policy.getRefNo());
                    }
                }

                List<String> attachments = new ArrayList<>();
                attachments.add("RN");
                attachments.add("ID");
                attachments.add("KRA");
                attachments.add("RCT");
                messageBean.setMessage(message);
                messageBean.setTransType("P");
                messageBean.setTransCode(policy.getPolicyId());
                messageBean.setReports(attachments);
                sendEmailPublisherBean.sendEmail(messageBean);
            }
        }
        return transs.getReceiptId();
    }


    @Override
    public Long createBulkReceipt(ReceiptTrans receipt, boolean isApproved, BulkReceipt bulkReceipt, List<Long> checkerIds) throws BadRequestException {
        final Long hashCode = Long.parseLong(String.valueOf(receipt.hashCode()));
        log.info("the receipt {} with hash code{} is: ",hashCode, receipt.getReceiptId());
        if (receipt.getReceiptType() != null && receipt.getReceiptType().equalsIgnoreCase("COM")) {
            if (receipt.getPayId() == null) {
                throw new BadRequestException("Collection Account is mandatory");
            }
        }

        if (receipt.getBrnCode() == null) {
            throw new BadRequestException("Receipt Branch is mandatory...");
        }

        if (!StringUtils.isBlank(receipt.getPaymentRef())) {
            if (receiptRepo.count(QReceiptTrans.receiptTrans.paymentRef.eq(receipt.getPaymentRef()).and(QReceiptTrans.receiptTrans.cancelled.ne("Y").or(QReceiptTrans.receiptTrans.cancelled.isNull()))) > 0) {
                throw new BadRequestException("Receipt Reference Exists......");
            }
        }

        if (receipt.getReceiptType() != null && !receipt.getReceiptType().equalsIgnoreCase("COM")) {
            if (receipt.getInsuranceId() == null)
                throw new BadRequestException("Invalid Insurance Company...");
            receipt.setInsurance(bulkReceipt.getInsurance());//accountRepo.findOne(receipt.getInsuranceId()));
        }
        PolicyTrans policy = null;
        BigDecimal totalAllocAmount = BigDecimal.ZERO;
        for (ReceiptTransDtls tran : receipt.getDetails()) {
            if (tran.getTransNo() == null && tran.getTransTempNo() == null) {
                throw new BadRequestException("Select Transaction to continue...");
            }
            if (!receipt.getReceiptType().equalsIgnoreCase("COM") && tran.getRctAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Receipt Amount cannot be zero or less than Zero...");
            }
            if (tran.getTransNo() != null && receipt.getReceiptType().equalsIgnoreCase("N")) {
                //SystemTransactions transaction = transRepo.findByTransno(tran.getTransNo());
                policy = bulkReceipt.getPolicy(); //transaction.getPolicy();
            }
            totalAllocAmount = totalAllocAmount.add(tran.getRctAmount());
            System.out.println("Amount..."+tran.getRctAmount());
        }


        if (totalAllocAmount.compareTo(receipt.getReceiptAmount()) != 0) {
            throw new BadRequestException("Total Allocation Amount " + totalAllocAmount + " and Receipt amount "
                    + receipt.getReceiptAmount() + " doesn't tally....");
        }
        if (receipt.getFromFCR() == null) {
            receipt.setFromFCR("N");
        }
        if (!receipt.getFromFCR().equalsIgnoreCase("Y")) {
            if (!authLimits.checkAuthorizationLimits("CREATE_RECEIPT", receipt.getReceiptAmount())) {
                throw new BadRequestException("You have no rights to create the transaction...Check your  limits..");
            }
        }


        User user = receipt.getReceiptUser();
        Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("R");
        if (sequenceRepo.count(seqPredicate) == 0)
            throw new BadRequestException("Sequence for Receipt Transactions has not been setup");
        SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
        Long seqNumber = sequence.getNextNumber();
        final String receiptNo = sequence.getSeqPrefix() + String.format("%05d", seqNumber);
        receipt.setReceiptNo(receiptNo);
        receipt.setCounter(BigInteger.valueOf(seqNumber));
        receipt.setBranch(bulkReceipt.getBranch()); //branchRepository.findOne(receipt.getBrnCode()));
        if (receipt.getPayId() != null) {
            receipt.setCollectionAccount(collectionAcctsRepo.findOne(receipt.getPayId()));
        }
        receipt.setReceiptUser(user);
        receipt.setReceiptTransDate(new Date());
        sequence.setLastNumber(seqNumber);
        sequence.setNextNumber(seqNumber + 1);
        if (receipt.getDirectReceipt() == null || "off".equalsIgnoreCase(receipt.getDirectReceipt()))
            receipt.setDirectReceipt("N");
        else if ("on".equalsIgnoreCase(receipt.getDirectReceipt()))
            receipt.setDirectReceipt("Y");
        else
            receipt.setDirectReceipt("N");

        if (!isApproved && !makerCheckerRepo.exists(hashCode)) {
            MakerCheckDTO makerCheckDTO = new MakerCheckDTO();

//            Gson gson = new GsonBuilder()
//                    .setDateFormat("dd/MM/yyyy")
//                    .create();
            //System.out.println(new Gson().toJson(receipt));

            //optimize with the dto for better experience in production
            Gson gson = new GsonBuilder()
                    .setDateFormat("dd/MM/yyyy")
                    .registerTypeHierarchyAdapter(HibernateProxy.class, new HibernateProxyTypeAdapter())
                    .create();
            System.out.println(gson.toJson(receipt));
            makerCheckDTO.setJson(gson.toJson(receipt));

            makerCheckDTO.setStatus("N");
            String receiptType = receipt.getReceiptType();
            makerCheckDTO.setTaskName(String.format("%s receipt no %s pending authorization", receiptType.equalsIgnoreCase("L") ? "Life" : receiptType.equalsIgnoreCase("N") ? "General" : receiptType.equalsIgnoreCase("COM") ? "Commission" : "Unknown", receipt.getReceiptNo()));
            makerCheckDTO.setTaskType("RC");
            if (receipt.getReceiptType().equalsIgnoreCase("N") || receipt.getReceiptType().equalsIgnoreCase("L")) {
                long policyId = 0;
                if (receipt.getReceiptType().equalsIgnoreCase("N")) {
                    PolicyTrans policyTrans = null;
                    for (ReceiptTransDtls tran : receipt.getDetails()) {
                        if (tran.getTransNo() != null) {
                            policyTrans = bulkReceipt.getPolicy(); //transRepo.findOne(tran.getTransNo()).getPolicy();
                        } else if (tran.getTransTempNo() != null) {
                            policyTrans = bulkReceipt.getPolicy(); //systemTransactionsTempRepo.findPolicyByTransTempNo(tran.getTransTempNo());
                        }
                        if (policyTrans != null) {
                            policyId = policyTrans.getPolicyId();
                        }
                    }
                }
                if (receipt.getReceiptType().equalsIgnoreCase("L")) {
                    policyId = receipt.getPolicyId();
                }
                makerCheckDTO.setPolicyId(policyId);
            }
            makerCheckDTO.setAssignedCheckers(checkerIds.toString());
            makerCheckDTO.setTaskCode(hashCode);
            makerCheckerService.checkExists(makerCheckDTO);
            makerCheckerService.createBulkMakerChecker(makerCheckDTO);
            return hashCode;
        }
        List<ReceiptTransDtls> transDtls = new ArrayList<>();
        SystemTrans trans = new SystemTrans();
        trans.setDoneDate(new Date());
        trans.setDoneBy(user);
        trans.setTransLevel("U");
        trans.setTransCode("RCT"); //A way to setup and look up for transaction transcode
        trans.setTransAuthorised("N");
        SystemTrans systemTrans = systemTransRepo.save(trans);
        if (receipt.getReceiptType() != null && !receipt.getReceiptType().equalsIgnoreCase("C")) {
            PolicyTrans policyTrans = null;
            for (ReceiptTransDtls tran : receipt.getDetails()) {
                if (receipt.getReceiptType() != null && "L".equalsIgnoreCase(receipt.getReceiptType())) {
                    PolicyTrans transaction = bulkReceipt.getPolicy(); //policyTransRepo.findOne(tran.getTransNo());
                    if (transaction != null) {
                        tran.setReceipt(receipt);
                        tran.setPolicy(transaction);
                        tran.setEndorsementNumber(transaction.getPolRevNo());
                        tran.setRctType("INV");
                        tran.setRctDC("C");
                        transDtls.add(tran);

                        if(transaction.getAuthStatus()!=null && transaction.getAuthStatus().equalsIgnoreCase("A")) {
                            postReceiptAccount(systemTrans, transaction, tran.getRctAmount());
                        }

                    }


                } else if (receipt.getReceiptType() != null && "COM".equalsIgnoreCase(receipt.getReceiptType())) {
                    SystemTransactions transaction = null;
                    SystemTransactionsTemp transactionsTemp = null;
                    if (tran.getTransNo() != null)
                        transaction = transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(tran.getTransNo()));
                    else if (tran.getTransTempNo() != null)
                        transactionsTemp = systemTransactionsTempRepo.findOne(QSystemTransactionsTemp.systemTransactionsTemp.tempTransno.eq(tran.getTransTempNo()));
                    if (transaction != null || transactionsTemp != null) {
                        tran.setReceipt(receipt);
                        tran.setTransaction(transaction);
                        tran.setTransactionsTemp(transactionsTemp);
                        tran.setRctType("INV");
                        tran.setRctDC("C");
                        transDtls.add(tran);
                    }

                } else {

                    SystemTransactions transaction = null;
                    SystemTransactionsTemp transactionsTemp = null;
                    if (tran.getTransNo() != null)
                        transaction = transRepo.findByTransno(tran.getTransNo());
                    else if (tran.getTransTempNo() != null)
                        transactionsTemp = systemTransactionsTempRepo.findOne(QSystemTransactionsTemp.systemTransactionsTemp.tempTransno.eq(tran.getTransTempNo()));
                    if (transaction != null || transactionsTemp != null) {
                        tran.setReceipt(receipt);
                        tran.setTransaction(transaction);
                        tran.setTransactionsTemp(transactionsTemp);
                        if (transaction != null)
                            tran.setEndorsementNumber(transaction.getPolicy().getPolRevNo());
                        else tran.setEndorsementNumber(transactionsTemp.getPolicy().getPolRevNo());
                        tran.setRctType("INV");
                        tran.setRctDC("C");
                        if (transaction != null) {
                            tran.setPolicy(transaction.getPolicy());
                            final PolicyTrans policyTranss = transaction.getPolicy();
                            ProductsDef product = policyTranss.getProduct();
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
                            long subcode = subcodes.get(0);
                            postReceiptAccount(systemTrans,policyTranss,tran.getRctAmount());

                        }
                        else {
                            tran.setPolicy(transactionsTemp.getPolicy());
                            final PolicyTrans policyTranss = transactionsTemp.getPolicy();
                            ProductsDef product = policyTranss.getProduct();
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
                            long subcode = subcodes.get(0);
                            postReceiptAccount(systemTrans,policyTranss,tran.getRctAmount());


                        }
                        transDtls.add(tran);
                    }


                }
                if ("COM".equalsIgnoreCase(receipt.getReceiptType())) {
                    System.out.println("Trans No.." + tran.getTransNo());
                    System.out.println("Trans No.." + tran.getTransaction());
                    SystemTransactions transactions = tran.getTransaction();
                    PaymentAudit paymentAudit = tran.getPaymentAudit();
                    BigDecimal balance = transactions.getBalance();

                    final List<BigInteger> commTrans = auditRepo.findCommissionTrans(paymentAudit.getPaId());

                    if(commTrans.size()!=1){
                        throw new BadRequestException("Unable To get Commission Transactions...");
                    }
                    final List<Object[]> commissionPayments  = commissionPaymentsRepo.getCommissionDebits(commTrans.get(0).longValue());

                    if(commissionPayments.isEmpty()){
                        throw new BadRequestException("Unable To get Commission Transactions...");
                    }

                    System.out.println("Comm Id "+commTrans.get(0).longValue());

                    final Long creditId =(commissionPayments.get(0)[0]!=null)? ((BigInteger)commissionPayments.get(0)[0]).longValue():null;
                    final Long debitId = (commissionPayments.get(0)[1]!=null)?((BigInteger)commissionPayments.get(0)[1]).longValue():null;

                    if ("NML".equalsIgnoreCase(paymentAudit.getTransType())) {
                        policyTrans = paymentAudit.getTransNo().getPolicy();
                    }
                    BigDecimal allocAmount = tran.getRctAmount();
                    System.out.println("Alloc Amout.."+allocAmount);

                    int count = 0;
                    if ("NML".equalsIgnoreCase(paymentAudit.getTransType()) || paymentAudit.getTransType().equalsIgnoreCase("ADM")) {
                        BigDecimal payableComm = paymentAudit.getCommAmount();
                        BigDecimal payableWhtx = paymentAudit.getWhtxAmount();
                        final SystemTransactions agentTrans = paymentAudit.getTransNo();
                        receipt.setInsurance(agentTrans.getAgent());
                        final BigDecimal commPayable = agentTrans.getCommission().abs().subtract(agentTrans.getWhtx().abs());
                        System.out.println("Alloc Amount "+allocAmount+" Payable Amt "+payableComm+" Comm Payable.."+commPayable);
//                            if (allocAmount.compareTo(commPayable) >= 0) {
                        final PolicyTrans policyTranss = paymentAudit.getTransNo().getPolicy();
                        ProductsDef product = policyTranss.getProduct();
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

                        if(payableWhtx==null){
                            throw new BadRequestException("Whtx Amount is mandatory before posting to account...");
                        }
                        if(payableComm==null){
                            throw new BadRequestException("Commission Amount is mandatory before posting to account...");
                        }
                        if(agentTrans.getAgent().getWhtxReceivableAccount()==null){
                            throw new BadRequestException("Whtx Receivable account is not set up for "+agentTrans.getAgent().getName());
                        }
                        if(agentTrans.getAgent().getCommReceivableAccount()==null){
                            throw new BadRequestException("Commission Receivable account is not set up for "+agentTrans.getAgent().getName());
                        }

                        paymentAudit.setPaidStatus("Y");
                        auditRepo.save(paymentAudit);


                        long subcode = subcodes.get(0);

                        GlTransactions debit2 = new GlTransactions();
                        debit2.setAmount(payableWhtx.abs());
                        debit2.setAuthDate(new Date());
                        debit2.setbCuramount(payableWhtx.abs());
                        debit2.setBranch(receipt.getBranch());
                        debit2.setCurrency(receipt.getCollectionAccount().getCurrencies());
                        debit2.setGlAcc(agentTrans.getAgent().getWhtxReceivableAccount());
                        if(payableComm.compareTo(BigDecimal.ZERO) > 0) {
                            if(transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(debitId))!=null) {
                                debit2.setPolicyTrans(transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(debitId)).getPolicy());
                                debit2.setSystemTransactions(transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(debitId)));
                            }
                            debit2.setGldc("D");
                        }
                        else{
                            debit2.setGldc("C");
                            if(transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(creditId))!=null) {
                                debit2.setPolicyTrans(transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(creditId)).getPolicy());
                                debit2.setSystemTransactions(transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(creditId)));
                            }
                        }
                        debit2.setTransaction(systemTrans);
                        debit2.setTransLevel("U");
                        debit2.setNarration("COMMPOSTING");
                        debit2.setTrntCode("RCT");
                        debit2.setGlYear(dateUtils.getUwYear(new Date()));
                        debit2.setGlMonth(dateUtils.getMonth(new Date()));
                        if(paymentAudit.getTransType().equalsIgnoreCase("NML")) {
                            debit2.setNarration(String.format("Posting Receipt Transaction for Commission Paid  %s, Ref No: %s", receipt.getPaidBy(), receipt.getReceiptNo()));
                        }
                        else if(paymentAudit.getTransType().equalsIgnoreCase("ADM")){
                            debit2.setNarration(String.format("Posting Receipt Transaction for Admin Fee Paid  %s, Ref No: %s", receipt.getPaidBy(), receipt.getReceiptNo()));

                        }
                        glTransRepo.save(debit2);
                        GlTransactions credit = new GlTransactions();
                        credit.setAmount(payableComm.abs());
                        credit.setAuthDate(new Date());
                        credit.setbCuramount(payableComm.abs());
                        credit.setBranch(receipt.getBranch());
                        credit.setCurrency(receipt.getCollectionAccount().getCurrencies());
                        credit.setGlAcc(agentTrans.getAgent().getCommReceivableAccount());
                        if(payableComm.compareTo(BigDecimal.ZERO) > 0) {
                            credit.setGldc("C");
                            if(transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(debitId))!=null) {
                                credit.setPolicyTrans(transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(debitId)).getPolicy());
                                credit.setSystemTransactions(transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(debitId)));
                            }
                        }
                        else{
                            credit.setGldc("D");
                            if(transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(creditId))!=null) {
                                credit.setPolicyTrans(transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(creditId)).getPolicy());
                                if(debitId!=null)
                                    credit.setSystemTransactions(transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(debitId)));
                                else if(creditId!=null){
                                    credit.setSystemTransactions(transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(creditId)));
                                }
                            }
                        }
                        if(paymentAudit.getTransType().equalsIgnoreCase("NML")) {
                            credit.setGlAcc(agentTrans.getAgent().getCommReceivableAccount());
                        }
                        else if(paymentAudit.getTransType().equalsIgnoreCase("ADM")){
                            credit.setGlAcc(agentTrans.getAgent().getAdminReceivableAccount());
                        }
                        credit.setNarration("COMMPOSTING");
                        credit.setPolicyTrans(policyTranss);
                        credit.setTransaction(systemTrans);
                        credit.setTransLevel("U");
                        credit.setTrntCode("RCT");
                        credit.setGlYear(dateUtils.getUwYear(new Date()));
                        credit.setGlMonth(dateUtils.getMonth(new Date()));
                        credit.setNarration(String.format("Posting Receipt Transaction for  %s, Ref No: %s", receipt.getPaidBy(), receipt.getReceiptNo()));
                        glTransRepo.save(credit);
                        balance = balance.subtract(payableComm).add(payableWhtx);
                        count++;
//                            }
                    }

                    receipt.setCommPosted("Y");
                    receiptRepo.save(receipt);
                    transactions.setBalance(balance);
                    transRepo.save(transactions);

                }
            }
            rctDetailsRepo.save(transDtls);

            if ("COM".equalsIgnoreCase(receipt.getReceiptType())) {
                GlTransactions debit = new GlTransactions();
                debit.setAmount(receipt.getReceiptAmount().abs());
                debit.setAuthDate(new Date());
                debit.setbCuramount(receipt.getReceiptAmount().abs());
                debit.setBranch(receipt.getBranch());
                debit.setCurrency(receipt.getCollectionAccount().getCurrencies());
                debit.setGlAcc(receipt.getCollectionAccount().getAccounts());
                debit.setGldc("D");
                debit.setPolicyTrans(policyTrans);
                debit.setNarration("COMMPOSTING");
                debit.setTransaction(systemTrans);
                debit.setTransLevel("U");
                debit.setTrntCode("RCT");
                debit.setGlYear(dateUtils.getUwYear(new Date()));
                debit.setGlMonth(dateUtils.getMonth(new Date()));
                debit.setNarration(String.format("Posting Receipt Transaction for Commission/Admin Fee Paid  %s, Ref No: %s", receipt.getPaidBy(), receipt.getReceiptNo()));
                glTransRepo.save(debit);
            }
        }

        sequenceRepo.save(sequence);
        Float amount = receipt.getReceiptAmount().floatValue();
        int figure = (int) Math.floor(amount);
        int cent = (int) Math.floor((amount - figure) * 100.0f);
        String words = "";
        if (cent > 0) {
            words = NumberToWordsUtils.convert(figure) + " and " + NumberToWordsUtils.convert(cent) + " cents";
        } else {
            words = NumberToWordsUtils.convert(figure);
        }
        receipt.setAmountWords(words);
        ReceiptTrans transs = receiptRepo.save(receipt);
        if (receipt.getReceiptType() != null && "L".equalsIgnoreCase(receipt.getReceiptType())) {
            allocService.allocateLifeReceipt(transs.getReceiptId(), user);
        }

        return transs.getReceiptId();
    }

    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void markReceiptPrinted(Long receiptId, User user) throws BadRequestException {
        ReceiptTrans receipt = receiptRepo.findOne(receiptId);
        if (receipt.getReceiptType() != null && "N".equalsIgnoreCase(receipt.getReceiptType())) {
            receipt.setPrinted("Y");
            receiptRepo.save(receipt);
            if (receipt.getReceiptType() != null && "L".equalsIgnoreCase(receipt.getReceiptType())) {
                allocService.allocateLifeReceipt(receiptId, user);
            } else {
                if (receipt.getFundReceipt() != null && "Y".equalsIgnoreCase(receipt.getFundReceipt())) {
                    allocService.createFundReceiptTransaction(receiptId);
                } else
                    allocService.allocateReceipt(receiptId, user);
            }
        }
        if (receipt.getReceiptType() != null && "COM".equalsIgnoreCase(receipt.getReceiptType())) {
            allocService.allocateCommReceipt(receiptId, user);
        }

    }

    @Override
    public DataTablesResult<LifeReceiptsDTO> findPolicyReceipts(Long polId, DataTablesRequest request) {
        List<Object[]> receiptsList = receiptRepo.findPolicyReceipts(polId, request.getPageNumber(), request.getPageSize());
        long rowCount = 0L;
        if (!receiptsList.isEmpty()) rowCount = ((BigInteger) receiptsList.get(0)[12]).intValue();
        final List<LifeReceiptsDTO> receiptsDTOList = new ArrayList<>();
        for (Object[] ticket : receiptsList) {
            LifeReceiptsDTO workFlowDTO = new LifeReceiptsDTO();
            workFlowDTO.setReceiptNo((String) ticket[0]);
            workFlowDTO.setReceiptDate((Date) ticket[1]);
            workFlowDTO.setDc((String) ticket[2]);
            workFlowDTO.setReceiptAmount((BigDecimal) ticket[3]);
            workFlowDTO.setAllocationAmount((BigDecimal) ticket[4]);
            workFlowDTO.setBalance((BigDecimal) ticket[5]);
            workFlowDTO.setReceiptId(((BigInteger) ticket[6]).longValue());
            workFlowDTO.setLifeRctId(((BigInteger) ticket[7]).longValue());
            workFlowDTO.setCommissionAmount((BigDecimal) ticket[8]);
            workFlowDTO.setSubAgentcommissionAmount((BigDecimal) ticket[9]);
            workFlowDTO.setMarketerCommissionAmount((BigDecimal) ticket[10]);
            workFlowDTO.setWhtxAmt((BigDecimal) ticket[11]);
            receiptsDTOList.add(workFlowDTO);
        }
        Page<LifeReceiptsDTO> page = new PageImpl<>(receiptsDTOList, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CollectionAccounts> findCollectionAccts(String paramString, Pageable paramPageable) {
        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred = QCollectionAccounts.collectionAccounts.isNotNull();
        } else {
            pred = QCollectionAccounts.collectionAccounts.name.containsIgnoreCase(paramString);
        }
        return collectionAcctsRepo.findAll(pred, paramPageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SelfFundParams> findSelfFundTransactions(String paramString, Pageable paramPageable) {
        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred = QSelfFundParams.selfFundParams.policyTrans.authStatus.eq("A").and(
                    QSelfFundParams.selfFundParams.selfFundBalance.isNull().or(QSelfFundParams.selfFundParams.fundDepositAmount.subtract(QSelfFundParams.selfFundParams.selfFundBalance).gt(BigDecimal.ZERO)));
        } else {
            pred = QSelfFundParams.selfFundParams.policyTrans.authStatus.eq("A").and(QSelfFundParams.selfFundParams.policyTrans.refNo.containsIgnoreCase(paramString)
                    .or(QSelfFundParams.selfFundParams.policyTrans.polNo.containsIgnoreCase(paramString))).and(
                    QSelfFundParams.selfFundParams.selfFundBalance.isNull().or(QSelfFundParams.selfFundParams.fundDepositAmount.subtract(QSelfFundParams.selfFundParams.selfFundBalance).gt(BigDecimal.ZERO)));

        }
        return fundParamsRepo.findAll(pred, paramPageable);
    }

    @Override
    public Page<PolicyTrans> findLifeTransactions(String paramString, Pageable paramPageable) throws IllegalAccessException {
        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred = QPolicyTrans.policyTrans.businessType.eq("L");
        } else {
            String searchTerm = paramString.trim();
            pred = QPolicyTrans.policyTrans.businessType.eq("L")
                    .and(
                            QPolicyTrans.policyTrans.polNo.containsIgnoreCase(searchTerm)
                                    .or(QPolicyTrans.policyTrans.proposalNo.containsIgnoreCase(searchTerm))
                                    .or(QPolicyTrans.policyTrans.refNo.containsIgnoreCase(searchTerm))
                                    .or(QPolicyTrans.policyTrans.polRevNo.containsIgnoreCase(searchTerm))
                                    .or(QPolicyTrans.policyTrans.clientPolNo.containsIgnoreCase(searchTerm))
                    );
        }

        System.out.println("Life transactions found: " + policyTransRepo.findAll(pred, paramPageable).getTotalElements());
        return policyTransRepo.findAll(pred, paramPageable);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = {BadRequestException.class})
    public void postReceiptAccount( SystemTrans systemTrans, PolicyTrans policy, final BigDecimal allocationAmt) throws BadRequestException {
        List<GlTransactions> glTransactions = new ArrayList<>();

        //To be handled on commission payments

//            if (polWhtx != null && polWhtx.compareTo(BigDecimal.ZERO) != 0) {
//                if (policy.getAgent().getWhtxReceivableAccount() == null) {
//                    throw new BadRequestException("Unable to get Debit Account for UWHTX ..");
//                }
//                if (policy.getAgent().getWhtxPayableAccount() == null) {
//                    throw new BadRequestException("Unable to get Credit Account for UWHTX ..");
//                }
//                GlTransactions debit = new GlTransactions();
//                debit.setAmount(polWhtx.abs());
//                debit.setAuthDate(new Date());
//                debit.setbCuramount(polWhtx.abs());
//                debit.setBranch(policy.getBranch());
//                debit.setCurrency(policy.getTransCurrency());
//                debit.setGlAcc((polWhtx.signum() == 1) ?  policy.getAgent().getWhtxReceivableAccount(): policy.getAgent().getWhtxPayableAccount());
//                debit.setGldc((polWhtx.signum() == 1) ? "D" : "C");
//                debit.setTransaction(systemTrans);
//                debit.setTransLevel("U");
//                debit.setTrntCode(policy.getTransType());
//                debit.setGlYear(dateUtils.getUwYear(new Date()));
//                debit.setGlMonth(dateUtils.getMonth(new Date()));
//                debit.setNarration(String.format("Posting WHTX for Policy No: %s, Ref No: %s", policy.getPolNo(), policy.getPolRevNo()));
//                debit.setPolicyTrans(policy); // Setting the PolicyTrans reference
//                debit.setTransType("WHTX");
//                glTransactions.add(debit);
//                GlTransactions credit = new GlTransactions();
//                credit.setAmount(polWhtx.abs());
//                credit.setAuthDate(new Date());
//                credit.setbCuramount(polWhtx.abs());
//                credit.setBranch(policy.getBranch());
//                credit.setCurrency(policy.getTransCurrency());
//                credit.setGlAcc((polWhtx.signum() == 1) ? policy.getAgent().getWhtxPayableAccount() : policy.getAgent().getWhtxReceivableAccount());
//                credit.setGldc((polWhtx.signum() == 1) ? "C" : "D");
//                credit.setTransaction(systemTrans);
//                credit.setTransLevel("U");
//                credit.setTrntCode(policy.getTransType());
//                credit.setGlYear(dateUtils.getUwYear(new Date()));
//                credit.setGlMonth(dateUtils.getMonth(new Date()));
//                credit.setNarration(String.format("new Date() WHTX for Policy No: %s, Ref No: %s", policy.getPolNo(), policy.getPolRevNo()));
//                credit.setPolicyTrans(policy); // Setting the PolicyTrans reference
//                credit.setTransType("WHTX");
//                glTransactions.add(credit);
//            }
//
//            if (policy.getAdminFeeVatAmt() != null && policy.getAdminFeeVatAmt().compareTo(BigDecimal.ZERO) != 0) {
//                if(policy.getAgent().getAdminWhtxReceivableAccount()==null){
//                    throw new BadRequestException("Unable to get Debit Account for Admin Fee..");
//
//                }
//                if(policy.getAgent().getAdminWhtxPayableAccount()==null){
//                    throw new BadRequestException("Unable to get Debit Account for Admin Fee..");
//                }
//
//                GlTransactions debit = new GlTransactions();
//                debit.setAmount(policy.getAdminFeeVatAmt().abs());
//                debit.setAuthDate(new Date());
//                debit.setbCuramount(policy.getAdminFeeVatAmt().abs());
//                debit.setBranch(policy.getBranch());
//                debit.setCurrency(policy.getTransCurrency());
//                debit.setGlAcc((policy.getAdminFeeVatAmt().signum() == 1) ? policy.getAgent().getAdminWhtxReceivableAccount() : policy.getAgent().getAdminWhtxPayableAccount());
//                debit.setGldc((policy.getAdminFeeVatAmt().signum() == 1) ? "D" : "C");
//                debit.setTransaction(systemTrans);
//                debit.setTransLevel("U");
//                debit.setTrntCode(policy.getTransType());
//                debit.setGlYear(dateUtils.getUwYear(new Date()));
//                debit.setGlMonth(dateUtils.getMonth(new Date()));
//                debit.setNarration(String.format("Posting Admin Fee Whtx for Policy No: %s, Ref No: %s", policy.getPolNo(), policy.getPolRevNo()));
//                debit.setPolicyTrans(policy);
//                debit.setTransType("ADMNFEEWHTX");
//                glTransactions.add(debit);
//                GlTransactions credit = new GlTransactions();
//                credit.setAmount(policy.getAdminFeeVatAmt().abs());
//                credit.setAuthDate(new Date());
//                credit.setbCuramount(policy.getAdminFeeVatAmt().abs());
//                credit.setBranch(policy.getBranch());
//                credit.setCurrency(policy.getTransCurrency());
//                credit.setGlAcc((policy.getAdminFeeVatAmt().signum() == 1) ? policy.getAgent().getAdminWhtxPayableAccount():policy.getAgent().getAdminWhtxReceivableAccount());
//                credit.setGldc((policy.getAdminFeeVatAmt().signum() == 1) ? "C" : "D");
//                credit.setTransaction(systemTrans);
//                credit.setTransLevel("U");
//                credit.setTrntCode(policy.getTransType());
//                credit.setGlYear(dateUtils.getUwYear(new Date()));
//                credit.setGlMonth(dateUtils.getMonth(new Date()));
//                credit.setNarration(String.format("Posting Admin Fee Whtx for Policy No: %s, Ref No: %s", policy.getPolNo(), policy.getPolRevNo()));
//                credit.setPolicyTrans(policy);
//                credit.setTransType("ADMNFEEWHTX");
//                glTransactions.add(credit);
//            }

        if (allocationAmt != null && allocationAmt.compareTo(BigDecimal.ZERO) != 0) {
            if(policy.getAgent().getReceivableAccount()==null){
                throw new BadRequestException("Unable to get Debit Account for Premium Account for .."+policy.getAgent().getName());
            }
            if(policy.getAgent().getPayableAccount()==null){
                throw new BadRequestException("Unable to get Credit Account for Premium Account for .."+policy.getAgent().getName());
            }
            if (policy.getAgent().getPayableAccount() != null && policy.getAgent().getReceivableAccount() != null) {
                GlTransactions debit = new GlTransactions();
                debit.setAmount(allocationAmt.abs());
                debit.setAuthDate(new Date());
                debit.setbCuramount(allocationAmt.abs());
                debit.setBranch(policy.getBranch());
                debit.setCurrency(policy.getTransCurrency());
                debit.setGlAcc((allocationAmt.signum() == 1) ?   policy.getAgent().getPayableAccount():policy.getAgent().getReceivableAccount());
                debit.setGldc("D");
                debit.setTransaction(systemTrans);
                debit.setTransLevel("U");
                debit.setTrntCode(policy.getTransType());
                debit.setGlYear(dateUtils.getUwYear(new Date()));
                debit.setGlMonth(dateUtils.getMonth(new Date()));
                debit.setNarration(String.format("Posting Receipt premium for Policy No: %s, Ref No: %s", policy.getPolNo(), policy.getPolRevNo()));
                debit.setPolicyTrans(policy); // Setting the PolicyTrans reference
                debit.setTransType("RCPREM");
                glTransactions.add(debit);
                GlTransactions credit = new GlTransactions();
                credit.setAmount(allocationAmt.abs());
                credit.setAuthDate(new Date());
                credit.setbCuramount(allocationAmt.abs());
                credit.setBranch(policy.getBranch());
                credit.setCurrency(policy.getTransCurrency());
                credit.setGlAcc((allocationAmt.signum() == 1) ?  policy.getAgent().getReceivableAccount():policy.getAgent().getPayableAccount() );
                credit.setGldc("C");
                credit.setTransaction(systemTrans);
                credit.setTransLevel("U");
                credit.setTrntCode(policy.getTransType());
                credit.setGlYear(dateUtils.getUwYear(new Date()));
                credit.setGlMonth(dateUtils.getMonth(new Date()));
                credit.setNarration(String.format("Posting Receipt premium for Policy No: %s, Ref No: %s", policy.getPolNo(), policy.getPolRevNo()));
                credit.setPolicyTrans(policy); // Setting the PolicyTrans reference
                credit.setTransType("RCPREM");
                glTransactions.add(credit);
            }

        }
        glTransRepo.save(glTransactions);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = {BadRequestException.class})
    public void postReceiptAccount(ReceiptTrans receiptTrans, SystemTrans systemTrans, long subCode, BigDecimal amount, BigDecimal whtx, BigDecimal totalComm, AccountDef accountDef, PolicyTrans policyTrans, int count) throws BadRequestException {
        List<GlTransactions> glTransactions = new ArrayList<>();



    }


    @Override
    public DataTablesResult<ReceiptTrans> findPrintedReceipts(DataTablesRequest request, Date from, Date to) throws IllegalAccessException {
        BooleanExpression pred = QReceiptTrans.receiptTrans.receiptDate.eq(new Date()).and(QReceiptTrans.receiptTrans.printed.eq("Y"))
                .and(QReceiptTrans.receiptTrans.cancelled.isNull().or(QReceiptTrans.receiptTrans.cancelled.ne("Y")));
        if (from != null && to != null) {
            pred = QReceiptTrans.receiptTrans.receiptDate.between(from, to).and(QReceiptTrans.receiptTrans.printed.eq("Y"))
                    .and(QReceiptTrans.receiptTrans.cancelled.isNull().or(QReceiptTrans.receiptTrans.cancelled.ne("Y")));

        }
        Page<ReceiptTrans> page = receiptRepo.findAll(pred.and(request.searchPredicate(QReceiptTrans.receiptTrans)), request);
        return new DataTablesResult(request, page);
    }

    @Override
    public DataTablesResult<ReceiptTrans> findReceiptsToCancel(DataTablesRequest request, Date from, Date to, String refNo, String receiptNo, String policyNo, Long clientId) throws IllegalAccessException {
        QReceiptTrans receipt = QReceiptTrans.receiptTrans;
        to = dateUtilities.removeTime(DateUtils.addDays(to, 1));
        from = dateUtilities.removeTime(from);
        BooleanExpression pred = QReceiptTrans.receiptTrans.receiptDate.between(from, to).and(QReceiptTrans.receiptTrans.cancelled.isNull()
                        .or(QReceiptTrans.receiptTrans.cancelled.eq("N")))
                .and((receiptNo == null || StringUtils.isEmpty(receiptNo)) ? receipt.isNotNull() : receipt.receiptNo.eq(receiptNo))
                .and((policyNo == null || StringUtils.isEmpty(policyNo)) ? receipt.isNotNull() : receipt.receiptDtls.any().transaction.policy.polNo.eq(policyNo))
                .and((refNo == null || StringUtils.isEmpty(refNo)) ? receipt.isNotNull() : receipt.receiptDtls.any().transaction.refNo.containsIgnoreCase(refNo));
        //.and((clientId==null)?receipt.isNotNull():receipt.receiptDtls.any().transaction.policy.client.tenId.eq(clientId));
        Page<ReceiptTrans> page = receiptRepo.findAll(pred.and(request.searchPredicate(QReceiptTrans.receiptTrans)), request);
        return new DataTablesResult(request, page);
    }

    @Override
    public DataTablesResult<ReceiptTrans> findUnPrintedReceipts(DataTablesRequest request, Date from, Date to) throws IllegalAccessException {
        BooleanExpression pred = QReceiptTrans.receiptTrans.receiptDate.eq(new Date()).and((QReceiptTrans.receiptTrans.printed.isNull().or(QReceiptTrans.receiptTrans.printed.eq("N"))))
                .and(QReceiptTrans.receiptTrans.cancelled.isNull().or(QReceiptTrans.receiptTrans.cancelled.ne("Y")));
        if (from != null && to != null) {
            pred = QReceiptTrans.receiptTrans.receiptDate.between(from, to).and((QReceiptTrans.receiptTrans.printed.isNull().or(QReceiptTrans.receiptTrans.printed.eq("N"))))
                    .and(QReceiptTrans.receiptTrans.cancelled.isNull().or(QReceiptTrans.receiptTrans.cancelled.ne("Y")));

        }
        Page<ReceiptTrans> page = receiptRepo.findAll(pred.and(request.searchPredicate(QReceiptTrans.receiptTrans)), request);
        System.out.println("Receipts: " + page);
        return new DataTablesResult(request, page);
    }

    @Transactional(readOnly = false)
    public void deleteCertTrans() {
        Iterable<ReceiptPrint> prints = printRepo.findAll(QReceiptPrint.receiptPrint.user.id.eq(userUtils.getCurrentUser().getId()));
        printRepo.delete(prints);
    }

    @Override

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void createReceipts(List<Long> receipts) {
        List<ReceiptPrint> certPrints = new ArrayList<>();
        for (Long receiptCode : receipts) {
            ReceiptTrans receiptTrans = receiptRepo.findOne(receiptCode);
            ReceiptPrint print = new ReceiptPrint();
            print.setReceiptTrans(receiptTrans);
            print.setUser(userUtils.getCurrentUser());
            certPrints.add(print);

        }
        printRepo.save(certPrints);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void markReceiptsPrinted(List<Long> receipts) throws BadRequestException {
        for (Long receiptCode : receipts) {
            ReceiptTrans receipt = receiptRepo.findOne(receiptCode);
            receipt.setPrinted("Y");
            receiptRepo.save(receipt);
            if (receipt.getReceiptType() != null && "L".equalsIgnoreCase(receipt.getReceiptType())) {
                allocService.allocateLifeReceipt(receiptCode, userUtils.getCurrentUser());
            } else {
                if (receipt.getFundReceipt() != null && "Y".equalsIgnoreCase(receipt.getFundReceipt())) {
                    allocService.createFundReceiptTransaction(receiptCode);
                } else
                    allocService.allocateReceipt(receiptCode, userUtils.getCurrentUser());
            }
        }
    }
    @PreAuthorize("hasAnyAuthority('CREATE_RECEIPT')")
    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})


    public void cancelReceipts(List<CancelData> receipts, boolean isApproved) throws BadRequestException {
        List<Long> resultIds = new ArrayList<>();

        for (CancelData receiptCode : receipts) {
            if (receiptCode.getCommentl() == null || StringUtils.isEmpty(receiptCode.getCommentl())) {
                throw new BadRequestException("Cannot cancel receipt without comment");
            }

            Long receiptId = receiptCode.getReceiptId();
            final Long hashCode = Long.parseLong(String.valueOf(receiptId.hashCode()));

            ReceiptTrans receipt = receiptRepo.findOne(receiptId);
            if (receipt == null) {
                throw new BadRequestException("Receipt not found with ID: " + receiptId);
            }

            if ("Y".equalsIgnoreCase(receipt.getCancelled())) {
                throw new BadRequestException("Receipt with ID " + receiptId + " is already cancelled");
            }

            if (!isApproved) {
                // Maker step - create approval task only if not already existing
                if (!makerCheckerRepo.exists(hashCode)) {
                    Hibernate.initialize(receipt.getDetails());
                    Long policyId = null;
                    if (receipt.getDetails() != null && !receipt.getDetails().isEmpty()) {
                        for (ReceiptTransDtls detail : receipt.getDetails()) {
                            if (detail.getPolicy() != null) {
                                policyId = detail.getPolicy().getPolicyId();
                                break;
                            } else if (detail.getTransaction() != null && detail.getTransaction().getPolicy() != null) {
                                policyId = detail.getTransaction().getPolicy().getPolicyId();
                                break;
                            } else if (detail.getTransactionsTemp() != null && detail.getTransactionsTemp().getPolicy() != null) {
                                policyId = detail.getTransactionsTemp().getPolicy().getPolicyId();
                                break;
                            }
                        }
                    }

                    ReceiptsDTO dto = new ReceiptsDTO();
                    dto.setBrnCode(receipt.getBranch().getObId());
                    dto.setReceiptNo(receipt.getReceiptNo());
                    dto.setReceiptAmount(receipt.getReceiptAmount());
                    dto.setReceiptDate(receipt.getReceiptDate());
                    dto.setReceiptTransDate(receipt.getReceiptTransDate());
                    dto.setReceiptType(receipt.getReceiptType());
                    dto.setReceiptDesc(receipt.getReceiptDesc());
                    dto.setPaidBy(receipt.getPaidBy());
                    dto.setPaymentRef(receipt.getPaymentRef());
                    dto.setManualRef(receipt.getManualRef());
                    dto.setDocumentDate(receipt.getDocumentDate());
                    dto.setBranch(receipt.getBranch());
                    dto.setCollectionAccount(receipt.getCollectionAccount());
                    dto.setDetails(receipt.getDetails() != null ? new ArrayList<>(receipt.getDetails()) : new ArrayList<>());
                    dto.setCancelComment(receiptCode.getCommentl());

                    MakerCheckDTO makerCheckDTO = new MakerCheckDTO();
                    Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
                    makerCheckDTO.setJson(gson.toJson(dto));
                    makerCheckDTO.setStatus("N");
                    makerCheckDTO.setTaskName(String.format("Cancel Receipt No %s pending authorization", receipt.getReceiptNo()));
                    makerCheckDTO.setTaskType("RC");
                    makerCheckDTO.setTaskCode(hashCode);
                    if (policyId != null) {
                        makerCheckDTO.setPolicyId(policyId);
                    }


                    List<UserDTO> eligibleCheckers = makerCheckerService.findEligibleCheckers("CREATE_RECEIPT", null);
                    List<Long> checkerIds = eligibleCheckers.stream()
                            .map(UserDTO::getId)
                            .collect(Collectors.toList());
                    makerCheckDTO.setAssignedCheckers(checkerIds.toString());

                    makerCheckerService.checkExists(makerCheckDTO);
                    makerCheckerService.createMakerChecker(makerCheckDTO);

                    resultIds.add(hashCode);
                }
            } else {
                // Checker step - approve and perform actual cancellation
                User currentUser = userUtils.getCurrentUser();
                Date now = new Date();
                receipt.setCancelledBy(currentUser);
                receipt.setCancelledDate(now);
                receipt.setCancelApproved("Y");
                receipt.setCancelApprovedBy(currentUser);
                receipt.setCancelApprovedDate(now);
                receipt.setCancelComment(receiptCode.getCommentl());

                receiptRepo.save(receipt);
                allocService.deallocateReceipt(receiptId, receiptCode.getCommentl());
                resultIds.add(receipt.getReceiptId());
            }
        }
    }

//    public void cancelReceipts(List<CancelData> receipts) throws BadRequestException {
//        for (CancelData receiptCode : receipts) {
//            if (receiptCode.getCommentl() == null || StringUtils.isEmpty(receiptCode.getCommentl())) {
//                throw new BadRequestException("Cannot cancel receipt without comment");
//            }
//        }
//        for (CancelData receiptCode : receipts) {
//            allocService.deallocateReceipt(receiptCode.getReceiptId(), receiptCode.getCommentl());
//        }
//    }


    @Override
    public DataTablesResult<IntegrationDtls> findIntegrationDtls(DataTablesRequest request, String receipted) throws IllegalAccessException {
        BooleanExpression pred = QIntegrationDtls.integrationDtls.receipted.eq(receipted);
        Page<IntegrationDtls> page = integrationDtlsRepo.findAll(pred.and(request.searchPredicate(QIntegrationDtls.integrationDtls)), request);
        return new DataTablesResult(request, page);
    }

    @Override
    public void updateIntegrationDtls(IntegrationDtls integrationDtls) {
        IntegrationDtls dtls = integrationDtlsRepo.findOne(integrationDtls.getBidId());
        dtls.setBillNewRfNumber(integrationDtls.getBillNewRfNumber());
        integrationDtlsRepo.save(dtls);
    }

    @Override
    public BigDecimal getPolicyTotalRcptAmount(String policyno) throws BadRequestException {
        BigDecimal totalReceipt = BigDecimal.ZERO;
        BooleanExpression pred = QReceiptSettlementDetails.receiptSettlementDetails.debit.policy.polNo.eq(policyno);
        Iterable<ReceiptSettlementDetails> settlementDetails = settlementRepo.findAll(pred);
        for (ReceiptSettlementDetails settlementDetail : settlementDetails) {
            long multiplier = 1;
            if (settlementDetail.getDrCr().equalsIgnoreCase("D"))
                multiplier = -1;

            totalReceipt = totalReceipt.add(settlementDetail.getAllocatedAmt().abs().multiply(BigDecimal.valueOf(multiplier)));
        }
        return totalReceipt;


    }


}
