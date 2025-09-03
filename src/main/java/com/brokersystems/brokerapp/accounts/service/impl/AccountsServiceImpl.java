package com.brokersystems.brokerapp.accounts.service.impl;

import com.brokersystems.brokerapp.accounts.dtos.*;
import com.brokersystems.brokerapp.accounts.dtos.SettlementDTO;
import com.brokersystems.brokerapp.accounts.model.*;
import com.brokersystems.brokerapp.accounts.model.QRefunds;
import com.brokersystems.brokerapp.accounts.repository.*;
import com.brokersystems.brokerapp.accounts.service.AccountsService;
import com.brokersystems.brokerapp.dms.model.QSybrinCases;
import com.brokersystems.brokerapp.dms.model.SybrinCases;
import com.brokersystems.brokerapp.dms.repo.SybrinCasesRepo;
import com.brokersystems.brokerapp.enums.AccountTypeEnum;
import com.brokersystems.brokerapp.enums.RevenueItems;
import com.brokersystems.brokerapp.security.CheckAuthLimits;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.AdminFeeException;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.DateUtilities;
import com.brokersystems.brokerapp.server.utils.HolidayUtils;
import com.brokersystems.brokerapp.server.utils.Streamable;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.setup.dto.CoaSubAccountsDTO;
import com.brokersystems.brokerapp.setup.dto.UserDTO;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.*;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.brokersystems.brokerapp.trans.model.*;
import com.brokersystems.brokerapp.trans.repository.*;
import com.brokersystems.brokerapp.trans.service.AccountsUtilities;
import com.brokersystems.brokerapp.trans.service.AllocationService;
import com.brokersystems.brokerapp.trans.service.CommissionsPayinsService;
import com.brokersystems.brokerapp.trans.service.ReceiptService;
import com.brokersystems.brokerapp.users.dto.MakerCheckDTO;
import com.brokersystems.brokerapp.users.model.QMakerChecker;
import com.brokersystems.brokerapp.users.repository.MakerCheckerRepo;
import com.brokersystems.brokerapp.users.service.MakerCheckerService;
import com.brokersystems.brokerapp.uw.dtos.ReceiptsDTO;
import com.brokersystems.brokerapp.uw.dtos.RefundClientDTO;
import com.brokersystems.brokerapp.uw.dtos.RefundDetailsDTO;
import com.brokersystems.brokerapp.uw.model.*;
import com.brokersystems.brokerapp.uw.repository.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.JPASubQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.Expression;
import com.mysema.query.types.ExpressionUtils;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.Projections;
import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.expr.NumberExpression;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by peter on 3/25/2017.
 */
@Service
public class AccountsServiceImpl implements AccountsService {


    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private SettlementRepo settlementRepo;
    @Autowired
    private PaymentAuditRepo auditRepo;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PolActiveRisksRepo activeRisksRepo;

    @Autowired
    private SubClassRepo subclassRepo;

    @Autowired
    private SubclassReqDocRepo subclassReqDocRepo;

    @Autowired
    private PolicyBindersRepo policyBindersRepo;

    @Autowired
    private RiskTransRepo riskRepo;

    @Autowired
    private RiskDocsRepo riskDocsRepo;

    @Autowired
    private SybrinCasesRepo sybrinCasesRepo;

    @Autowired
    private CollectionAcctsRepo collectionAcctsRepo;

    @Autowired
    private OrgBranchRepository branchRepository;

    @Autowired
    private CheckAuthLimits authLimits;
    @Autowired
    private CurrencyRepository currencyRepo;
    @Autowired
    private UserUtils userUtils;

    @Autowired
    private DateUtilities dateUtils;
    @Autowired
    private SystemTransRepo systransRepo;
    @Autowired
    private SystemTransactionsRepo transRepo;
    @Autowired
    private DateUtilities dateUtilities;
    @Autowired
    private CoaMainAccountsRepo mainAccountsRepo;
    @Autowired
    private CoaSubAccountsRepo subAccountsRepo;
    @Autowired
    private  RefundRepo refundRepo;
    @Autowired
    private BanksRepo banksRepo;
    @Autowired
    private BankBranchRepo bankBranchRepo;
    @Autowired
    private CollectionAcctsRepo acctsRepo;
    @Autowired
    private PaymentModeRepo paymentModeRepo;
    @Autowired
    private AllocationService allocationService;
    @Autowired
    private Environment env;
    @Autowired
    private SequenceRepository sequenceRepo;
    @Autowired
    private ReceiptDetailsRepository receiptDetailsRepository;
    @Autowired
    private TransMappingRepo mappingRepo;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ParamService paramService;
    @Autowired
    private PolicyTransRepo policyRepo;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private CommissionsPayinsService commissionsPayinsService;
    @Autowired
    private SystemTransactionsRepo systemTransactionsRepo;
    @Autowired
    private PayeesRepo payeesRepo;
    @Autowired
    private PayeeAccountsRepo payeeAccountsRepo;
    @Autowired
    private OpeningBalancesRepo balancesRepo;
    @Autowired
    private FinalReportFormatsRepo finalReportFormatsRepo;
    @Autowired
    private FinalReportFormatTotalsRepo reportFormatTotalsRepo;
    @Autowired
    private FinalReportFormatAccountsRepo finalReportFormatAccountsRepo;
    @Autowired
    private FinalReportFormatGroupAccountsRepo finalReportFormatGroupAccountsRepo;
    @Autowired
    private AccountYearsRepo yearsRepo;
    @Autowired
    private AccountYearPeriodsRepo periodsRepo;
    @Autowired
    private MakerCheckerService makerCheckerService;
    @Autowired
    private MakerCheckerRepo makerCheckerRepo;

    @Autowired
    private AccountYearPeriodsRepo accountYearPeriodsRepo;
    @Autowired
    private OrgBranchRepository orgBranchRepository;
    @Autowired
    private ReceiptService receiptService;

    @Autowired
    private AccountsUtilities accountsUtilities;

    @Autowired
    private GlTransRepo glTransRepo;
    @Autowired
    private CommissionPaymentsRepo commissionPaymentsRepo;


    @Override
    @Transactional(readOnly = true)
    public List<SettlementDTO> findAllocationDetails(Long agentCode, Long currencyCode, Date wefDate, Date wetDate, String pstatus) throws IllegalAccessException {
        List<Object[]> pages = null;
        Currencies currencies = currencyRepo.findOne(currencyCode);
        String databaseType = env.getProperty("database_type");
        if(org.apache.commons.lang.StringUtils.equalsIgnoreCase("postgres",databaseType)) {
            if (wefDate == null && wetDate == null)
                pages = settlementRepo.getPostgresSettlementDetails(agentCode, new Date(), new Date(), currencies.getRoundOff(),pstatus);
            else {

                pages = settlementRepo.getPostgresSettlementDetails(agentCode, wefDate, DateUtils.addDays(wetDate, 1), currencies.getRoundOff(),pstatus);
            }
        }
        else if(org.apache.commons.lang.StringUtils.equalsIgnoreCase("mssql",databaseType)){
            if (wefDate == null && wetDate == null)
                pages = settlementRepo.getSqlServerSettlementDetails(agentCode, new Date(), new Date(), currencies.getRoundOff(),pstatus);
            else {

                pages = settlementRepo.getSqlServerSettlementDetails(agentCode, wefDate, DateUtils.addDays(wetDate, 1), currencies.getRoundOff(),pstatus);
            }
        }

        List<SettlementDTO> settlementDTOs = new ArrayList<>();
        for(Object[] page:pages){
            SettlementDTO settlementDTO = new SettlementDTO();
            settlementDTO.setPolNo((String)page[0]);
            settlementDTO.setClientPolNo((String)page[1]);
            settlementDTO.setClientName((String)page[2]+" "+(String)page[3]);
            settlementDTO.setDrNo((String)page[4]);
            settlementDTO.setCrNo((String)page[5]);
            settlementDTO.setPayStatus((String)page[7]);
            settlementDTO.setBasicPrem((BigDecimal) page[8]);
            settlementDTO.setCommAmt((BigDecimal) page[9]);
            settlementDTO.setWhtx((BigDecimal) page[10]);
            settlementDTO.setSettleAmt((BigDecimal) page[13]);
            settlementDTO.setDebitBal((BigDecimal) page[12]);
            settlementDTO.setAllocAmt((BigDecimal) page[11]);
            settlementDTO.setTransType((String)page[15]);
            settlementDTOs.add(settlementDTO);
        }
        return settlementDTOs;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SettlementDTO> findSubAgentCommDetails(Long subAgentCode, Long currencyCode, Date wefDate, Date wetDate) throws IllegalAccessException {
        List<Object[]> pages = null;



        if (wefDate == null && wetDate == null) {
            LocalDate localDate = new Date().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            // Step 2: Convert back to java.util.Date at 00:00:00
            ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
            Date dateOnly = Date.from(zonedDateTime.toInstant());
            pages = settlementRepo.getSubAgentCommTrans(subAgentCode, dateOnly, dateOnly, currencyCode);
        }
        else {
            LocalDateTime wefLocal = wetDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
                    .plusDays(1)
                    .minusSeconds(1);
            Date wet = Date.from(wefLocal.atZone(ZoneId.systemDefault()).toInstant());


            LocalDateTime wetLocal = wefDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
                    .minusDays(1)
                    .minusSeconds(1);
            Date wef = Date.from(wetLocal.atZone(ZoneId.systemDefault()).toInstant());
            System.out.println("Params...agent code "+subAgentCode+" wef date "+wef+" wet date "+wet+" Round off "+currencyCode);
            pages = settlementRepo.getSubAgentCommTrans(subAgentCode, wef, wet, currencyCode);
//            System.out.println("Return data ...."+pages.size());
        }

        List<SettlementDTO> settlementDTOs = new ArrayList<>();

        // Iterate over each settlement and filter based on audit information
        for (Object[] page : pages) {
            String debitRef = (String) page[3]; // debit reference
            String receiptRef = (String) page[4]; // receipt reference

            boolean alreadyProcessed = false;

            // Check if this transaction exists in the PaymentAudit table (processed)
            if (auditRepo.count(QPaymentAudit.paymentAudit.receiptTransNo.eq(receiptRef)
                    .and(QPaymentAudit.paymentAudit.debitTransNo.eq(debitRef)).and(QPaymentAudit.paymentAudit.transType.eq("SAG"))) > 0) {
                alreadyProcessed = true;
            }

            // If the transaction hasn't been processed, continue
            if (!alreadyProcessed) {
                SettlementDTO settlementDTO = new SettlementDTO();
                settlementDTO.setClientPolNo((String) page[0]);
                settlementDTO.setClientName((String) page[1] + " " + (String) page[2]);
                settlementDTO.setDrNo((String) page[3]);
                settlementDTO.setCrNo((String) page[4]);
                settlementDTO.setProduct((String) page[5]);
                settlementDTO.setInsurer((String) page[6]);
                settlementDTO.setCommAmt((BigDecimal) page[7]);
                settlementDTO.setAllocAmt((BigDecimal) page[8]);
                settlementDTO.setBasicPrem((BigDecimal) page[9]);
                settlementDTO.setWhtx((BigDecimal) page[10]);
                settlementDTO.setPayStatus((String) page[11]);
                settlementDTO.setAgentCode(((BigInteger) page[14]).longValue());
                settlementDTO.setTransId(((BigInteger) page[15]).longValue());
                settlementDTO.setCommNetAmt((BigDecimal) page[16]);
                settlementDTOs.add(settlementDTO);
            }
        }

        return settlementDTOs;
    }


    @Override
    @Transactional(readOnly = true)
    public List<SettlementDTO> findAllocationCommDetails(Long agentCode, Long currencyCode, Date wefDate, Date wetDate, String commtype) throws IllegalAccessException {
        List<Object[]> pages = null;

        LocalDateTime wefLocal = wetDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .plusDays(1)
                .minusSeconds(1);

        Date wet = Date.from(wefLocal.atZone(ZoneId.systemDefault()).toInstant());
        System.out.println("wef " +wet);

        System.out.println("Params...agent code "+agentCode+" wef date "+wefDate+" wet date "+wet+" Round off "+currencyCode);
        pages = settlementRepo.getPostgresUnsettledCommDetails(agentCode, wefDate, wet, currencyCode, commtype);

//        if (org.apache.commons.lang.StringUtils.equalsIgnoreCase("postgres", databaseType)) {
//            if (wefDate == null && wetDate == null) {
//                pages = settlementRepo.getPostgresSettlementCommDetails(agentCode, new Date(), new Date(), currencies.getRoundOff());
//            } else {
//                System.out.println("Params...agent code "+agentCode+" wef date "+wefDate+" wet date "+wetDate+" Round off "+currencies.getRoundOff());
//                pages = settlementRepo.getPostgresSettlementCommDetails(agentCode, wefDate, wetDate, currencies.getRoundOff());
//            }
//        } else if (org.apache.commons.lang.StringUtils.equalsIgnoreCase("mssql", databaseType)) {
//            if (wefDate == null && wetDate == null) {
//                pages = settlementRepo.getSqlServerSettlementCommDetails(agentCode, new Date(), new Date(), currencies.getRoundOff());
//            } else {
//                pages = settlementRepo.getSqlServerSettlementCommDetails(agentCode, wefDate, DateUtils.addDays(wetDate, 1), currencies.getRoundOff());
//            }
//        }

        List<SettlementDTO> settlementDTOs = new ArrayList<>();

        // Iterate over each settlement and filter based on audit information
        for (Object[] page : pages) {
            String debitRef = (String) page[5]; // debit reference
            String receiptRef = (String) page[6]; // receipt reference

            boolean alreadyProcessed = false;

            // Check if this transaction exists in the PaymentAudit table (processed)
            if (auditRepo.count(QPaymentAudit.paymentAudit.receiptTransNo.eq(receiptRef)
                    .and(QPaymentAudit.paymentAudit.debitTransNo.eq(debitRef))) > 0) {
                alreadyProcessed = true;
            }

            // If the transaction hasn't been processed, continue
            if (!alreadyProcessed) {
                SettlementDTO settlementDTO = new SettlementDTO();
                settlementDTO.setPolNo((String) page[1]);
                settlementDTO.setTransType((String) page[2]);
                settlementDTO.setAllocAmt((BigDecimal) page[3]);
                settlementDTO.setDebitBal((BigDecimal) page[4]);
                settlementDTO.setDrNo((String) page[5]);
                settlementDTO.setCrNo((String) page[6]);
                settlementDTO.setBasicPrem((BigDecimal) page[7]);
                settlementDTO.setCommAmt((BigDecimal) page[8]);
                settlementDTO.setWhtx((BigDecimal) page[9]);
                settlementDTO.setTransId(((BigInteger) page[15]).longValue());
                settlementDTO.setClientName((String) page[13] + " " + (String) page[14]);
                settlementDTO.setCommNetAmt((BigDecimal) page[16]);
//                settlementDTO.setClientPolNo((String) page[1]);
//                settlementDTO.setPayStatus((String) page[7]);
//                settlementDTO.setSettleAmt((BigDecimal) page[3]);

                settlementDTOs.add(settlementDTO);
            }
        }

        return settlementDTOs;
    }

    @Override
    public List<SettlementDTO> findSubAgentTransactions(Long agentCode, Date wefDate, Date wetDate, Long subAcctId) throws IllegalAccessException {
        List<Object[]> pages = null;
        String databaseType = env.getProperty("database_type");
        if(org.apache.commons.lang.StringUtils.equalsIgnoreCase("postgres",databaseType)) {

            pages = transRepo.getSubAgentTrans(agentCode, wefDate, wetDate, subAcctId);
        }
        else{
            if(agentCode!=null && agentCode==-2000){
                agentCode = null;
            }
            pages = transRepo.getSqlServerSubAgentTrans(agentCode,wefDate,wetDate,subAcctId);
        }
        List<SettlementDTO> settlementDTOs = new ArrayList<>();
        for(Object[] page:pages){
            SettlementDTO settlementDTO = new SettlementDTO();
            settlementDTO.setPolNo((String)page[0]);
            settlementDTO.setClientName((String)page[2]+" "+(String)page[3]);
            settlementDTO.setDrNo((String)page[1]);
            settlementDTO.setCommAmt((BigDecimal) page[4]);
            settlementDTO.setSettleAmt((BigDecimal) page[5]);
            settlementDTO.setDebitBal((BigDecimal) page[7]);
            settlementDTO.setAllocAmt((BigDecimal) page[6]);
            if(page[8] instanceof Long)
                settlementDTO.setAcctCode((Long) page[8]);
            else if(page[8]!=null && page[8] instanceof BigInteger){
                settlementDTO.setAcctCode(((BigInteger) page[8]).longValue());
            }
            else if(page[8]!=null && page[8] instanceof BigDecimal){
                settlementDTO.setAcctCode(((BigDecimal) page[8]).longValue());
            }
            settlementDTOs.add(settlementDTO);
        }
        return settlementDTOs;
    }

    private static NumberExpression emptyIfNull(NumberExpression expression) {
        return expression.coalesce(BigDecimal.ZERO).asNumber();
    }


    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<SystemTransactions> findAgentsTransactions(DataTablesRequest request, Long agentCode, Long currencyCode) throws IllegalAccessException {
        AccountDef accountDef = null;
        String accShtDesc = "";
        if(agentCode!=null){
            accountDef = accountRepo.findOne(agentCode);
            accShtDesc = accountDef.getShtDesc();
        }
        if(agentCode==null) agentCode=-2000l;
        if(currencyCode==null) currencyCode=-2000l;

        Predicate pred = QSystemTransactions.systemTransactions.agent.acctId.eq(agentCode)
                .and(QSystemTransactions.systemTransactions.currency.curCode.eq(currencyCode))
                .and(QSystemTransactions.systemTransactions.clientType.eq("I"))
                .and(QSystemTransactions.systemTransactions.transType.eq("PM"))
                .and(QSystemTransactions.systemTransactions.controlAcc.eq(accShtDesc))
                .and(QSystemTransactions.systemTransactions.authorised.eq("N").or(QSystemTransactions.systemTransactions.authorised.isNull()))
                .and(QSystemTransactions.systemTransactions.narrations.eq("Creditor Payment"));
        Page<SystemTransactions> page = transRepo.findAll(pred, request);
        return new DataTablesResult(request, page);
    }

    @Override
    public DataTablesResult<SystemTransactions> findBulkCreditorCommTransactions(DataTablesRequest request, Long agentCode, Long currencyCode) throws IllegalAccessException {
        AccountDef accountDef = null;
        String accShtDesc = "";
        if(agentCode!=null){
            accountDef = accountRepo.findOne(agentCode);
            accShtDesc = accountDef.getShtDesc();
        }
        if(agentCode==null) agentCode=-2000l;
        if(currencyCode==null) currencyCode=-2000l;

        Predicate pred = QSystemTransactions.systemTransactions.agent.acctId.eq(agentCode)
                .and(QSystemTransactions.systemTransactions.currency.curCode.eq(currencyCode))
                .and(QSystemTransactions.systemTransactions.clientType.eq("I"))
                .and(QSystemTransactions.systemTransactions.transType.eq("COM"))
                .and(QSystemTransactions.systemTransactions.controlAcc.eq(accShtDesc))
                .and(QSystemTransactions.systemTransactions.authorised.eq("P").or(QSystemTransactions.systemTransactions.authorised.isNull()))
                .and(QSystemTransactions.systemTransactions.narrations.in("Bulk Commissions","Bulk Admin Fee"));
        Page<SystemTransactions> page = transRepo.findAll(pred, request);
        System.out.println("Total pages."+page.getTotalPages()+" Total Eleemnts "+page.getTotalElements());
        return new DataTablesResult(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<SystemTransactions> findCreditorCommTransactions(DataTablesRequest request, Long agentCode, Long currencyCode, String transType) throws IllegalAccessException {
        AccountDef accountDef = null;
        String accShtDesc = "";
        if(agentCode!=null){
            accountDef = accountRepo.findOne(agentCode);
            accShtDesc = accountDef.getShtDesc();
        }
        if(agentCode==null) agentCode=-2000l;
        if(currencyCode==null) currencyCode=-2000l;
        String type = null;
        String narration = null;
        if(transType.equalsIgnoreCase("C")){
            type = "COM";
            narration = "Commissions";
        }
        else   if(transType.equalsIgnoreCase("A")){
            type = "ADM";
            narration = "Admin Fees";
        }

        System.out.println("Type "+type);

        Predicate pred = QSystemTransactions.systemTransactions.agent.acctId.eq(agentCode)
                .and(QSystemTransactions.systemTransactions.currency.curCode.eq(currencyCode))
                .and(QSystemTransactions.systemTransactions.clientType.eq("I"))
                .and(QSystemTransactions.systemTransactions.transType.eq(type))
                .and(QSystemTransactions.systemTransactions.controlAcc.eq(accShtDesc))
                .and(QSystemTransactions.systemTransactions.authorised.eq("P").or(QSystemTransactions.systemTransactions.authorised.isNull()))
                .and(QSystemTransactions.systemTransactions.narrations.eq(narration));
        Page<SystemTransactions> page = transRepo.findAll(pred, request);
        return new DataTablesResult(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<SystemTransactions> findSubAgentCommTransactions(DataTablesRequest request, List<Long> agentCodes, Long currencyCode) throws IllegalAccessException {


        BooleanBuilder builder = new BooleanBuilder();

        System.out.println("Passed here..."+agentCodes);

        if (agentCodes != null && !agentCodes.isEmpty()) {
            builder.and(QSystemTransactions.systemTransactions.agent.acctId.in(agentCodes));
        }
        builder.and(QSystemTransactions.systemTransactions.currency.curCode.eq(currencyCode))
                .and(QSystemTransactions.systemTransactions.clientType.eq("SA"))
                .and(QSystemTransactions.systemTransactions.transType.eq("SAG"))
                .and(QSystemTransactions.systemTransactions.authorised.eq("P")
                        .or(QSystemTransactions.systemTransactions.authorised.isNull()))
                .and(QSystemTransactions.systemTransactions.narrations.eq("Sub Agent Payment Trans"));
        Page<SystemTransactions> page = transRepo.findAll(builder, request);
        return new DataTablesResult(request, page);
    }

    @Override
    public DataTablesResult<SystemTransactions> findIAAgentsTransactions(DataTablesRequest request, Long agentCode, Long currencyCode) throws IllegalAccessException {
        AccountDef accountDef = null;
        String accShtDesc = "";
        if(agentCode!=null){
            accountDef = accountRepo.findOne(agentCode);
            accShtDesc = accountDef.getShtDesc();
        }
        if(agentCode==null) agentCode=-2000l;
        if(currencyCode==null) currencyCode=-2000l;
        Predicate pred =  QSystemTransactions.systemTransactions.currency.curCode.eq(currencyCode)
                .and(QSystemTransactions.systemTransactions.clientType.eq("I"))
                .and(QSystemTransactions.systemTransactions.transType.eq("PM"))
                .and(QSystemTransactions.systemTransactions.authorised.eq("N").or(QSystemTransactions.systemTransactions.authorised.isNull()))
                .and(QSystemTransactions.systemTransactions.narrations.eq("Sub Agent Payment"));
        if(agentCode!=-2000l){
            pred = QSystemTransactions.systemTransactions.agent.acctId.eq(agentCode)
                    .and(QSystemTransactions.systemTransactions.currency.curCode.eq(currencyCode))
                    .and(QSystemTransactions.systemTransactions.clientType.eq("I"))
                    .and(QSystemTransactions.systemTransactions.transType.eq("PM"))
                    .and(QSystemTransactions.systemTransactions.controlAcc.eq(accShtDesc))
                    .and(QSystemTransactions.systemTransactions.authorised.eq("N").or(QSystemTransactions.systemTransactions.authorised.isNull()))
                    .and(QSystemTransactions.systemTransactions.narrations.eq("Sub Agent Payment"));
        }

        Page<SystemTransactions> page = transRepo.findAll(pred, request);
        return new DataTablesResult(request, page);
    }


    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<PaymentAudit> findTransPaymentAudits(DataTablesRequest request, Long transId) throws IllegalAccessException {
        if(transId==null) transId=-2000l;
        BooleanExpression pred = QPaymentAudit.paymentAudit.otherTransNo.transno.eq(transId);
        Page<PaymentAudit> page = auditRepo.findAll(pred.and(request.searchPredicate(QPaymentAudit.paymentAudit)), request);
        return new DataTablesResult(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String,Object> findAllocationCommDetails(Long transNo) throws IllegalAccessException {


       // String databaseType = env.getProperty("database_type");


        List<Object[]> pages = auditRepo.getCreditorCommtAudits(transNo);

        System.out.println("Total Count..."+pages.size());


        List<PaymentAuditDTO> paymentAuditDTOs = new ArrayList<>();

        for(Object[] page:pages){
            PaymentAuditDTO paymentAuditDTO = new PaymentAuditDTO();
            paymentAuditDTO.setRefNo((String) page[0]);
            paymentAuditDTO.setClientPolNo((String) page[1]);
            paymentAuditDTO.setFname((String) page[2]);
            paymentAuditDTO.setOtherNames((String) page[3]);
            paymentAuditDTO.setControlAcc((String) page[4]);
            paymentAuditDTO.setProDesc((String) page[5]);
            paymentAuditDTO.setPaymentAmount((BigDecimal) page[6]);
            paymentAuditDTO.setCommAmount((BigDecimal) page[7]);
            paymentAuditDTO.setWhtxAmount((BigDecimal) page[8]);
            paymentAuditDTOs.add(paymentAuditDTO);
        }
        Map<String,Object> result = new HashMap<>();
        result.put("data",paymentAuditDTOs);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String,Object> findAllocationSubAgentCommDetails(Long transNo) throws IllegalAccessException {
        System.out.println("Trans no..."+transNo);

        // String databaseType = env.getProperty("database_type");


        List<Object[]> pages = auditRepo.geSubAgentCommtAudits(transNo);


        List<PaymentAuditDTO> paymentAuditDTOs = new ArrayList<>();

        for(Object[] page:pages){
            PaymentAuditDTO paymentAuditDTO = new PaymentAuditDTO();
            paymentAuditDTO.setRefNo((String) page[0]);
            paymentAuditDTO.setClientPolNo((String) page[1]);
            paymentAuditDTO.setFname((String) page[2]);
            paymentAuditDTO.setOtherNames((String) page[3]);
            paymentAuditDTO.setControlAcc((String) page[4]);
            paymentAuditDTO.setProDesc((String) page[5]);
            paymentAuditDTO.setPaymentAmount((BigDecimal) page[6]);
            paymentAuditDTO.setCommAmount((BigDecimal) page[7]);
            paymentAuditDTO.setWhtxAmount((BigDecimal) page[8]);
            paymentAuditDTOs.add(paymentAuditDTO);
        }
        Map<String,Object> result = new HashMap<>();
        result.put("data",paymentAuditDTOs);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<CoaMainAccounts> findChartOfAccounts(DataTablesRequest request) throws IllegalAccessException {
        Page<CoaMainAccounts> page = mainAccountsRepo.findAll(request.searchPredicate(QCoaMainAccounts.coaMainAccounts), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<CoaSubAccountsDTO> findChartSubAccounts(DataTablesRequest request, Long mainAccId) throws IllegalAccessException {
        if(mainAccId==null) mainAccId=-2000l;
        final String search = ( request.getSearch()!=null && request.getSearch().getValue()!=null)?"%"+request.getSearch().getValue().toLowerCase()+"%":"%%";
        List<Object[]> accounts = subAccountsRepo.findMainSubAccounts(mainAccId, search.toLowerCase(),request.getPageNumber(), request.getPageSize());
        final List<CoaSubAccountsDTO> accountsDTOList = new ArrayList<>();
        long rowCount = 0L;
        if(!accounts.isEmpty()) rowCount = ((BigInteger)accounts.get(0)[15]).intValue();

        for(Object[] account:accounts){
            CoaSubAccountsDTO accountsDTO = new CoaSubAccountsDTO();
            accountsDTO.setCoId(((BigInteger)account[0]).longValue());
            accountsDTO.setAccountsOrder((String)account[1]);
            accountsDTO.setCode((String)account[2]);
            accountsDTO.setName((String)account[3]);
            accountsDTO.setIntegration((String)account[4]);
            accountsDTO.setControlAccount((String)account[5]);
            if(account[6]!=null){
                accountsDTO.setAcctTypeId(((BigInteger)account[6]).longValue());
            }
            accountsDTO.setAccTypeName((String)account[7]);
            accountsDTO.setMainAcctId(((BigInteger)account[8]).longValue());
            accountsDTO.setApplicableToScl((String)account[9]);
            accountsDTO.setSublass((String)account[10]);
            if(account[11]!=null){
                accountsDTO.setScId(((BigInteger)account[11]).longValue());
            }

            if(account[12]!=null){
                accountsDTO.setSapGlAccount((String)account[12]);
            }

            if(account[13]!=null){
                accountsDTO.setSapGlAccountBusiness((String)account[13]);
            }

            if(account[14]!=null){
                accountsDTO.setSapGlAccountCorporate((String)account[14]);
            }


            accountsDTOList.add(accountsDTO);
        }
        Page<CoaSubAccountsDTO>  page = new PageImpl<>(accountsDTOList,request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Transactional(readOnly = true)
    public BigDecimal getPayableAmount(String receiptRef,String debitRef, BigDecimal allocAmt) throws BadRequestException{
        if(receiptRef==null) throw new BadRequestException("Trans Alloc Id Cannot be null");
        if(debitRef==null) throw new BadRequestException("Debit Transaction cannot be null");
        if(allocAmt==null) throw new BadRequestException("Allocation Amount cannot be null......");
//        SystemTransactions debitTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(debitRef)
//                .and(QSystemTransactions.systemTransactions.clientType.eq("C").and(QSystemTransactions.systemTransactions.transType.in("NBD","APD","RND","RFC")))
//                .and(QSystemTransactions.systemTransactions.transdc.eq("D")));
        SystemTransactions debitTrans =null;
        if (transRepo.count(QSystemTransactions.systemTransactions.refNo.eq(debitRef)
                .and(QSystemTransactions.systemTransactions.transType.in("APC")))==0) {
            debitTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(debitRef)
                    .and(QSystemTransactions.systemTransactions.clientType.eq("C").and(QSystemTransactions.systemTransactions.transType.in("NBD", "APD", "RND")))
                    .and(QSystemTransactions.systemTransactions.transdc.eq("D")));
        }else
        {
            debitTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(debitRef)
                    .and(QSystemTransactions.systemTransactions.clientType.eq("A").and(QSystemTransactions.systemTransactions.transType.in("APC")))
                    .and(QSystemTransactions.systemTransactions.transdc.eq("D")));
            debitTrans.setNetAmount(debitTrans.getAmount());
        }
        SystemTransactions creditTrans = null;
        if(transRepo.count(QSystemTransactions.systemTransactions.refNo.eq(receiptRef).and(QSystemTransactions.systemTransactions.transType.in("RC","APC"))
                .and(QSystemTransactions.systemTransactions.transdc.eq("C")))==1)
            creditTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(receiptRef).and(QSystemTransactions.systemTransactions.transType.in("RC","APC"))
                    .and(QSystemTransactions.systemTransactions.transdc.eq("C")));
        else
            creditTrans = settlementRepo.findOne(QReceiptSettlementDetails.receiptSettlementDetails.debit.refNo.eq(debitRef).and(QReceiptSettlementDetails.receiptSettlementDetails.credit.refNo.eq(receiptRef)
                    .and(QReceiptSettlementDetails.receiptSettlementDetails.credit.transType.in("RC","APC")))).getCredit();

        if(creditTrans==null)
            throw new BadRequestException("Unable to get Receipt Transaction to allocate...");
        BigDecimal receiptSettlement = BigDecimal.ZERO;
        if(auditRepo.count(QPaymentAudit.paymentAudit.receiptTransNo.eq(receiptRef).and(QPaymentAudit.paymentAudit.debitTransNo.eq(debitRef)))>0){
            Iterable<PaymentAudit> paymentAudit = auditRepo.findAll(QPaymentAudit.paymentAudit.receiptTransNo.eq(receiptRef).and(QPaymentAudit.paymentAudit.debitTransNo.eq(debitRef)));
            for (PaymentAudit paymentAudit1:paymentAudit){
                receiptSettlement = receiptSettlement.add(paymentAudit1.getPaymentAmount());
            }
        }
        SystemTransactions agentTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(debitRef)
                .and(QSystemTransactions.systemTransactions.clientType.eq("A").and(QSystemTransactions.systemTransactions.transType.in("NBD","APD","RND")))
                .and(QSystemTransactions.systemTransactions.transdc.eq("C")));
        //double prorationRate = allocAmt.doubleValue()/(debitTrans.getNetAmount().doubleValue());
        double prorationRate = allocAmt.doubleValue()/(debitTrans.getNetAmount().doubleValue());


        Currencies currencies = debitTrans.getCurrency();

        BigDecimal prorata = new BigDecimal(prorationRate).abs();
        System.out.println("prorata="+prorata);
        System.out.println("comm="+debitTrans.getPolicy().getCommAmt());
        System.out.println("whtx="+debitTrans.getPolicy().getWhtx());
        System.out.println("temp="+creditTrans.getTempSettleAmt());
        System.out.println("SettleAmt="+receiptSettlement);

        BigDecimal locAmt = allocAmt.add(debitTrans.getPolicy().getCommAmt().multiply(prorata)).
                add(debitTrans.getPolicy().getWhtx().multiply(prorata))
                .add(creditTrans.getTempSettleAmt()==null?BigDecimal.ZERO:creditTrans.getTempSettleAmt())
                .add(receiptSettlement)
                .abs().setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN);
        System.out.println(locAmt);
//        return allocAmt.add(debitTrans.getPolicy().getCommAmt().multiply(prorata)).
//                            add(debitTrans.getPolicy().getWhtx().multiply(prorata))
//                                .add(creditTrans.getTempSettleAmt()==null?BigDecimal.ZERO:creditTrans.getTempSettleAmt())
//                                .add(agentTrans.getSettleAmt()==null?BigDecimal.ZERO:agentTrans.getSettleAmt())
//                                .abs().setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN);
        return allocAmt.add(debitTrans.getPolicy().getCommAmt().multiply(prorata)).
                add(debitTrans.getPolicy().getWhtx().multiply(prorata))
                .add(creditTrans.getTempSettleAmt()==null?BigDecimal.ZERO:creditTrans.getTempSettleAmt())
                .add(receiptSettlement)
                .setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN);

    }

    @Transactional(readOnly = true)
    public BigDecimal getPayableCommAmount(String receiptRef,String debitRef, BigDecimal locAmt, BigDecimal allocAmt) throws BadRequestException{
        System.out.println("allocAmt >> " +allocAmt);
        if(receiptRef==null) throw new BadRequestException("Trans Alloc Id Cannot be null");
        if(debitRef==null) throw new BadRequestException("Debit Transaction cannot be null");
        if(allocAmt==null) throw new BadRequestException("Allocation Amount cannot be null......");
        SystemTransactions debitTrans =null;
        System.out.println("Debit Ref "+debitRef);
        if (transRepo.count(QSystemTransactions.systemTransactions.refNo.eq(debitRef)
                .and(QSystemTransactions.systemTransactions.transType.in("APC")))==0) {
            debitTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(debitRef)
                    .and(QSystemTransactions.systemTransactions.clientType.eq("C").and(QSystemTransactions.systemTransactions.transType.in("NBD", "APD", "RND")))
                    .and(QSystemTransactions.systemTransactions.transdc.eq("D")));

            if(debitTrans==null){
                debitTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(debitRef)
                        .and(QSystemTransactions.systemTransactions.clientType.eq("A").and(QSystemTransactions.systemTransactions.transType.in("NBD", "APD", "RND")))
                        .and(QSystemTransactions.systemTransactions.transdc.eq("D")));
            }
        }else
        {
            debitTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(debitRef)
                    .and(QSystemTransactions.systemTransactions.clientType.eq("A").and(QSystemTransactions.systemTransactions.transType.in("APC")))
                    .and(QSystemTransactions.systemTransactions.transdc.eq("D")));
            debitTrans.setNetAmount(debitTrans.getAmount());
        }
        SystemTransactions creditTrans = null;
        if(transRepo.count(QSystemTransactions.systemTransactions.refNo.eq(receiptRef).and(QSystemTransactions.systemTransactions.transType.in("RC","APC"))
                .and(QSystemTransactions.systemTransactions.transdc.eq("C")))==1)
            creditTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(receiptRef).and(QSystemTransactions.systemTransactions.transType.in("RC","APC"))
                    .and(QSystemTransactions.systemTransactions.transdc.eq("C")));
        else
            creditTrans = settlementRepo.findOne(QReceiptSettlementDetails.receiptSettlementDetails.debit.refNo.eq(debitRef).and(QReceiptSettlementDetails.receiptSettlementDetails.credit.refNo.eq(receiptRef)
                    .and(QReceiptSettlementDetails.receiptSettlementDetails.credit.transType.in("RC","APC")))).getCredit();

        if(creditTrans==null)
            throw new BadRequestException("Unable to get Receipt Transaction to allocate...");
        BigDecimal receiptSettlement = BigDecimal.ZERO;
        if(auditRepo.count(QPaymentAudit.paymentAudit.receiptTransNo.eq(receiptRef).and(QPaymentAudit.paymentAudit.debitTransNo.eq(debitRef)))>0){
            Iterable<PaymentAudit> paymentAudit = auditRepo.findAll(QPaymentAudit.paymentAudit.receiptTransNo.eq(receiptRef).and(QPaymentAudit.paymentAudit.debitTransNo.eq(debitRef)));
            for (PaymentAudit paymentAudit1:paymentAudit){
                receiptSettlement = receiptSettlement.add(paymentAudit1.getPaymentAmount());
            }
        }
        SystemTransactions agentTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(debitRef)
                .and(QSystemTransactions.systemTransactions.clientType.eq("A").and(QSystemTransactions.systemTransactions.transType.in("NBD","APD","RND")))
                .and(QSystemTransactions.systemTransactions.transdc.eq("C")));
        //double prorationRate = allocAmt.doubleValue()/(debitTrans.getNetAmount().doubleValue());
        double prorationRate = allocAmt.doubleValue()/(debitTrans.getNetAmount().doubleValue());
        Currencies currencies = debitTrans.getCurrency();
        BigDecimal prorata = new BigDecimal(prorationRate).abs();

        boolean lifePolicy = "L".equalsIgnoreCase(debitTrans.getPolicy().getProduct().getProGroup().getPrgType());

        if(lifePolicy){
            System.out.println(debitTrans.getTransno());
            locAmt = (debitTrans.getCommission().multiply(prorata).abs())
                    .subtract((debitTrans.getWhtx().multiply(prorata).abs()))
                    .setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN);
        }
        else if(debitTrans.getPolicy().getCommAmt()!=null) {
            locAmt = (debitTrans.getPolicy().getCommAmt().multiply(prorata).abs())
                    .subtract((debitTrans.getPolicy().getWhtx().multiply(prorata).abs()))
                    .setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN);
        }
        else{
          return BigDecimal.ZERO;
        }
        return  locAmt;

    }
    @Transactional(readOnly = true)
    public BigDecimal getPayableSubAgentCommAmount(String receiptRef,String debitRef, BigDecimal locAmt, BigDecimal allocAmt) throws BadRequestException{
        System.out.println("allocAmt >> " +allocAmt);
        if(receiptRef==null) throw new BadRequestException("Trans Alloc Id Cannot be null");
        if(debitRef==null) throw new BadRequestException("Debit Transaction cannot be null");
        if(allocAmt==null) throw new BadRequestException("Allocation Amount cannot be null......");
         BigDecimal netAmount = BigDecimal.ZERO;
        List<Object[]> debits = new ArrayList<>();
        if (transRepo.count(QSystemTransactions.systemTransactions.refNo.eq(debitRef)
                .and(QSystemTransactions.systemTransactions.transType.in("APC")))==0) {
           debits = transRepo.getAllocationTrans(debitRef,"SAG","C","C");
            if(debits.size()!=1){
                throw new BadRequestException("Unable to get Debit Transaction. Cannot continue.....");
            }
            netAmount = (BigDecimal) debits.get(0)[0];

        }else
        {
             debits = transRepo.getAllocationTrans(debitRef,"SAG","C","D");
            if(debits.size()!=1){
                throw new BadRequestException("Unable to get Debit Transaction. Cannot continue.....");
            }
            netAmount = (BigDecimal) debits.get(0)[0];
        }
//        SystemTransactions creditTrans = null;
//        if(transRepo.count(QSystemTransactions.systemTransactions.refNo.eq(receiptRef).and(QSystemTransactions.systemTransactions.transType.in("RC"))
//                .and(QSystemTransactions.systemTransactions.transdc.eq("C")))==1)
//            creditTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(receiptRef).and(QSystemTransactions.systemTransactions.transType.in("RC"))
//                    .and(QSystemTransactions.systemTransactions.transdc.eq("C")));
//        else
//            creditTrans = settlementRepo.findOne(QReceiptSettlementDetails.receiptSettlementDetails.debit.refNo.eq(debitRef).and(QReceiptSettlementDetails.receiptSettlementDetails.credit.refNo.eq(receiptRef)
//                    .and(QReceiptSettlementDetails.receiptSettlementDetails.credit.transType.in("RC")))).getCredit();

//        if(creditTrans==null)
//            throw new BadRequestException("Unable to get Receipt Transaction to allocate...");
        BigDecimal receiptSettlement = BigDecimal.ZERO;
        if(auditRepo.count(QPaymentAudit.paymentAudit.receiptTransNo.eq(receiptRef).and(QPaymentAudit.paymentAudit.debitTransNo.eq(debitRef)))>0){
            List<BigDecimal> audits = auditRepo.getSubAgentAmts(receiptRef,debitRef);
//            Iterable<PaymentAudit> paymentAudit = auditRepo.findAll(QPaymentAudit.paymentAudit.receiptTransNo.eq(receiptRef).and(QPaymentAudit.paymentAudit.debitTransNo.eq(debitRef)));
            for (BigDecimal amount:audits){
                receiptSettlement = receiptSettlement.add(amount);
            }
        }
        SystemTransactions agentTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(debitRef)
                .and(QSystemTransactions.systemTransactions.clientType.eq("A").and(QSystemTransactions.systemTransactions.transType.in("NBD","APD","RND")))
                .and(QSystemTransactions.systemTransactions.transdc.eq("C")));
        //double prorationRate = allocAmt.doubleValue()/(debitTrans.getNetAmount().doubleValue());
        System.out.println("Get Net Amount" +netAmount);
        double prorationRate = allocAmt.doubleValue()/(netAmount.abs().doubleValue());
        if (prorationRate > 1) {
            prorationRate = 1.0;
        }
       BigDecimal subAgentAmt = (BigDecimal) debits.get(0)[1];
       Long currencyId = ((BigInteger) debits.get(0)[2]).longValue();
        Currencies currencies = currencyRepo.findOne(currencyId);
        BigDecimal prorata = new BigDecimal(prorationRate).abs();
        locAmt = (subAgentAmt.multiply(prorata).abs())
                .setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN);
        System.out.println("Sub Agnt com >> "+locAmt);
        return  locAmt;

    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getSumAuditAmt(Long allocId) throws BadRequestException {
        if(allocId==null) throw new BadRequestException("Trans Alloc Id Cannot be null");
        ReceiptSettlementDetails settlementDetails = settlementRepo.findOne(allocId);
        BigDecimal sumAudit = BigDecimal.ZERO;
        Iterable<PaymentAudit> audits = auditRepo.findAll(QPaymentAudit.paymentAudit.settlements.settlementId.eq(allocId));
        for(PaymentAudit audit:audits){
            sumAudit = sumAudit.add(audit.getPaymentAmount());
        }
        return sumAudit;
    }

    @Override
    public void consolidateCommissionPayments(ProcessingBean processingBean) throws BadRequestException {
        if(processingBean.getCredits().size()==0) throw new BadRequestException("No Credits to Process....");
        if(processingBean.getSubaccountType()==null) throw new BadRequestException("Select a Sub Account Type to Process ...");
        BigDecimal totalCredit = BigDecimal.ZERO;
        for(InsPaymentBean insPaymentBean:processingBean.getCredits()){
            totalCredit = totalCredit.add(insPaymentBean.getAmount());
        }
//        if(totalCredit.compareTo(BigDecimal.ZERO)==-1 || totalCredit.compareTo(BigDecimal.ZERO)==0)
//            throw new BadRequestException("Total Credits cannot be negative or zero");
        Iterable<SystemTransactions> existingTrans = null;
        if(processingBean.getAccountCode()!=null){
            AccountDef accountDef = accountRepo.findOne(processingBean.getAccountCode());
            existingTrans = transRepo.findAll(QSystemTransactions.systemTransactions.clientType.eq("C")
                    .and(QSystemTransactions.systemTransactions.transType.eq("SAG"))
                    .and(QSystemTransactions.systemTransactions.controlAcc.eq(accountDef.getShtDesc()))
                    .and(QSystemTransactions.systemTransactions.agent.acctId.eq(accountDef.getAcctId()))
                    .and(QSystemTransactions.systemTransactions.narrations.eq("Sub Agent Commission Trans"))
                    .and((QSystemTransactions.systemTransactions.authorised.eq("N")).or(QSystemTransactions.systemTransactions.authorised.isNull()))
                    .and(QSystemTransactions.systemTransactions.currency.curCode.eq(processingBean.getCurCode())));

            processCommissionTransactions(existingTrans,totalCredit,accountDef,currencyRepo.findOne(processingBean.getCurCode()),processingBean.getCredits(),"Sub Agent Payment");
        }
//        else {
//            Iterable<AccountDef> accountDefs = accountRepo.findAll(QAccountDef.accountDef.subAccountTypes.subAcctId.eq(processingBean.getSubaccountType()));
//            for(AccountDef accountDef:accountDefs){
//                List<InsPaymentBean> credits = processingBean.getCredits().stream().filter(a -> a.getAcctCode().intValue()==accountDef.getAcctId().intValue()).collect(Collectors.toList());
//                existingTrans = transRepo.findAll(QSystemTransactions.systemTransactions.clientType.eq("I")
//                        .and(QSystemTransactions.systemTransactions.transType.eq("PM"))
//                        .and(QSystemTransactions.systemTransactions.controlAcc.eq(accountDef.getShtDesc()))
//                        .and(QSystemTransactions.systemTransactions.agent.acctId.eq(accountDef.getAcctId()))
//                        .and(QSystemTransactions.systemTransactions.narrations.eq("Sub Agent Payment"))
//                        .and((QSystemTransactions.systemTransactions.authorised.eq("N")).or(QSystemTransactions.systemTransactions.authorised.isNull()))
//                        .and(QSystemTransactions.systemTransactions.currency.curCode.eq(processingBean.getCurCode())));
//                BigDecimal totCredit = BigDecimal.ZERO;
//                for(InsPaymentBean insPaymentBean:credits){
//                    totCredit = totCredit.add(insPaymentBean.getAmount());
//                }
//                if(totCredit.compareTo(BigDecimal.ZERO)!=0)
//                    processCommissionTransactions(existingTrans,totCredit,accountDef,currencyRepo.findOne(processingBean.getCurCode()),credits,"Sub Agent Payment");
//            }
//        }

    }

    @Override
    public void consolidateSubAgentCommissionPayments(ProcessingBean processingBean) throws BadRequestException {
        if(processingBean.getCredits().isEmpty()) {
            throw new BadRequestException("No Credits to Process....");

        }
        System.out.println("Total Credits..."+processingBean.getCredits().size());
        System.out.println(processingBean);

//        if(processingBean.getSubaccountType()==null) throw new BadRequestException("Select a Sub Account Type to Process ...");

//        if(totalCredit.compareTo(BigDecimal.ZERO)==-1 || totalCredit.compareTo(BigDecimal.ZERO)==0)
//            throw new BadRequestException("Total Credits cannot be negative or zero");
        Map<Long, List<InsPaymentBean>> groupedByAccountCode = processingBean.getCredits().stream()
                .collect(Collectors.groupingBy(InsPaymentBean::getAccountCode));
        System.out.println(groupedByAccountCode);

        groupedByAccountCode.forEach((accountCode, beans) -> {
            //try {
                Iterable<SystemTransactions> existingTrans = null;
                System.out.println("Account Code: " + accountCode);
                AccountDef accountDef = accountRepo.findOne(accountCode);
                System.out.println("Account ID.." + accountDef.getAcctId());
                System.out.println("Cur ID.." + processingBean.getCurCode());
                BigDecimal totalCredit = beans.stream()
                        .map(InsPaymentBean::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                existingTrans = transRepo.findAll(QSystemTransactions.systemTransactions.clientType.eq("C")
                        .and(QSystemTransactions.systemTransactions.transType.eq("SAG"))
                        .and(QSystemTransactions.systemTransactions.agent.acctId.eq(accountDef.getAcctId()))
                        .and(QSystemTransactions.systemTransactions.narrations.eq("Sub Agent Payment Trans"))
                        .and((QSystemTransactions.systemTransactions.authorised.eq("N")).or(QSystemTransactions.systemTransactions.authorised.isNull()))
                        .and(QSystemTransactions.systemTransactions.currency.curCode.eq(processingBean.getCurCode())));

            try {
                processSubAgentCommTrans(existingTrans, totalCredit, accountDef, currencyRepo.findOne(processingBean.getCurCode()), beans, "Sub Agent Payment Trans");
            } catch (BadRequestException e) {
                throw new RuntimeException(e);
            }
//            }
//            catch (Exception ex){
//                throw new RuntimeException(ex.getMessage());
//            }
        });

    }


    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void consolidatePayments(ProcessingBean processingBean) throws BadRequestException{
        if(processingBean.getCredits().size()==0) throw new BadRequestException("No Credits to Process....");
        if(processingBean.getAccountCode()==null) throw new BadRequestException("Select an Agent to Process ...");
        if(processingBean.getCurCode()==null)throw new BadRequestException("Select Currency to continue ...");
        BigDecimal totalCredit = BigDecimal.ZERO;
        for(InsPaymentBean insPaymentBean:processingBean.getCredits()){
            totalCredit = totalCredit.add(insPaymentBean.getAmount());
        }
        if(totalCredit.compareTo(BigDecimal.ZERO)==-1 || totalCredit.compareTo(BigDecimal.ZERO)==0)
            throw new BadRequestException("Total Credits cannot be negative or zero");
        AccountDef accountDef = accountRepo.findOne(processingBean.getAccountCode());
        Iterable<SystemTransactions> existingTrans = transRepo.findAll(QSystemTransactions.systemTransactions.clientType.eq("I")
                .and(QSystemTransactions.systemTransactions.transType.eq("PM"))
                .and(QSystemTransactions.systemTransactions.controlAcc.eq(accountDef.getShtDesc()))
                .and(QSystemTransactions.systemTransactions.agent.acctId.eq(accountDef.getAcctId()))
                .and(QSystemTransactions.systemTransactions.narrations.eq("Creditor Payment"))
                .and((QSystemTransactions.systemTransactions.authorised.eq("N")).or(QSystemTransactions.systemTransactions.authorised.isNull()))
                .and(QSystemTransactions.systemTransactions.currency.curCode.eq(processingBean.getCurCode())));
        processTransactions(existingTrans,totalCredit,accountDef,currencyRepo.findOne(processingBean.getCurCode()),processingBean.getCredits(),"Creditor Payment");


    }

    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void consolidateCreditorCommissionsPayments(ProcessingBean processingBean) throws BadRequestException{
        if(processingBean.getCredits().isEmpty()) throw new BadRequestException("No Credits to Process....");
        if(processingBean.getAccountCode()==null) throw new BadRequestException("Select an Agent to Process ...");
        if(processingBean.getCurCode()==null)throw new BadRequestException("Select Currency to continue ...");
        BigDecimal totalCredit = BigDecimal.ZERO;
        for(InsPaymentBean insPaymentBean:processingBean.getCredits()){
            totalCredit = totalCredit.add(insPaymentBean.getAmount());
        }
        if(totalCredit.compareTo(BigDecimal.ZERO)==-1 || totalCredit.compareTo(BigDecimal.ZERO)==0)
            throw new BadRequestException("Total Credits cannot be negative or zero");
        AccountDef accountDef = accountRepo.findOne(processingBean.getAccountCode());
        String transType = null;
        String narration = null;
        if(processingBean.getSelreceiptType().equalsIgnoreCase("C")){
            transType = "COM";
            narration = "Commissions";
        }
        else   if(processingBean.getSelreceiptType().equalsIgnoreCase("A")){
            transType = "ADM";
            narration = "Admin Fees";
        }
        Iterable<SystemTransactions> existingTrans = transRepo.findAll(QSystemTransactions.systemTransactions.clientType.eq("I")
                .and(QSystemTransactions.systemTransactions.transType.eq(transType))
                .and(QSystemTransactions.systemTransactions.controlAcc.eq(accountDef.getShtDesc()))
                .and(QSystemTransactions.systemTransactions.agent.acctId.eq(accountDef.getAcctId()))
                .and(QSystemTransactions.systemTransactions.narrations.eq(narration))
                .and((QSystemTransactions.systemTransactions.authorised.eq("P")).or(QSystemTransactions.systemTransactions.authorised.isNull()))
                .and(QSystemTransactions.systemTransactions.currency.curCode.eq(processingBean.getCurCode())));
        if(accountDef.getAccountType().getAccountType()==AccountTypeEnum.INS){
            processCreditorCommissions(existingTrans,totalCredit,accountDef,currencyRepo.findOne(processingBean.getCurCode()),processingBean.getCredits(),narration,transType);
        }
        else{
            processCommissionTransactions(existingTrans,totalCredit,accountDef,currencyRepo.findOne(processingBean.getCurCode()),processingBean.getCredits(),narration);
        }


    }

    private void processCommissionTransactions(Iterable<SystemTransactions> existingTrans, BigDecimal totalCredit, AccountDef accountDef, Currencies currency, final List<InsPaymentBean> paymentBeanList, final String type) throws BadRequestException {
        if(existingTrans.spliterator().getExactSizeIfKnown() > 1) throw  new BadRequestException("There can be only one unauthorised transactions to be processed. Consult the admin..");
        if(existingTrans.spliterator().getExactSizeIfKnown()==0){
            Predicate adminPredicate = QSystemSequence.systemSequence.transType.eq("RM");
            if (sequenceRepo.count(adminPredicate) == 0)
                throw new AdminFeeException("Sequence for Remittance Transactions has not been defined");
            SystemSequence sequence = sequenceRepo.findOne(adminPredicate);
            Long seqNumber = sequence.getNextNumber();
            sequence.setLastNumber(seqNumber);
            sequence.setNextNumber(seqNumber + 1);
            sequenceRepo.save(sequence);
            SystemTrans transaction = new SystemTrans();
            transaction.setDoneDate(new Date());
            transaction.setDoneBy(userUtils.getCurrentUser());
            transaction.setTransLevel("U");
            transaction.setTransCode("PM"); //A way to setup and look up for transaction transcode
            transaction.setTransAuthorised("N");
            SystemTrans createdTrans = systransRepo.save(transaction);
            SystemTransactions trans = new SystemTransactions();
            trans.setAmount(totalCredit);
            trans.setBalance(totalCredit);
            trans.setClientType("I");
            trans.setAgent(accountDef);
            trans.setTransDate(new Date());
            trans.setRefNo(accountDef.getShtDesc()+"/"+String.format("%04d",seqNumber));
            trans.setTransType("PM");
            trans.setTransdc("D");
            trans.setControlAcc(accountDef.getShtDesc());
            trans.setCurrency(currencyRepo.findOne(currency.getCurCode()));
            trans.setCurrRate(BigDecimal.ONE);
            trans.setNarrations(type);
            trans.setAuthorised("N");
            trans.setPostedDate(new Date());
            trans.setPostedUser(userUtils.getCurrentUser());
            trans.setNetAmount(totalCredit);
            trans.setTransaction(createdTrans);
            trans.setPayeeName(accountDef.getName());
            trans.setOrigin("U");
            SystemTransactions savedTrans= transRepo.save(trans);
            Currencies currencies = savedTrans.getCurrency();
            List<PaymentAudit> audits = new ArrayList<>();
            List<SystemTransactions> receipts = new ArrayList<>();
            System.out.println(paymentBeanList);
            for(InsPaymentBean insPaymentBean:paymentBeanList){
                Iterable<PaymentAudit> paymentAudits =  auditRepo.findAll(QPaymentAudit.paymentAudit.settlements.debitRefNo.eq(insPaymentBean.getDebiTrans()).and(QPaymentAudit.paymentAudit.transType.eq("SAG")));
                if(paymentAudits.spliterator().getExactSizeIfKnown() > 1){
                    throw new BadRequestException("Unable to process. More than one settlement transaction generated... Contact Admin......");
                }
                PaymentAudit audit = null;

                if(paymentAudits.spliterator().getExactSizeIfKnown() ==0){
                    audit = new PaymentAudit();
                }
                else{
                    for(PaymentAudit paymentAudit:paymentAudits){
                        audit = paymentAudit;
                        break;
                    }
                }

                audit.setTransType("SAG");
                audit.setSettlements(null);
                audit.setDebitTransNo(insPaymentBean.getDebiTrans());
                audit.setReceiptTransNo(insPaymentBean.getCreditTrans());
//                audit.setCommAmount( (audit.getCommAmount()!=null)? audit.getCommAmount().add(sysTrans.getCommission().multiply(prorata).negate().setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN)):sysTrans.getCommission().multiply(prorata).negate().setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN));
                audit.setPaymentAmount((audit.getPaymentAmount()!=null)?audit.getPaymentAmount().add(insPaymentBean.getAmount().setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN)):insPaymentBean.getAmount().setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN));
                audit.setOtherTransNo(savedTrans);
//                audit.setTransNo(sysTrans);
                audit.setWhtxAmount( (BigDecimal.ZERO));
                audits.add(audit);
            }
            auditRepo.save(audits);
            transRepo.save(receipts);
        }
        else{
            SystemTransactions transactions = null;
            for (SystemTransactions trans:existingTrans){
                transactions = trans;
            }
            if(transactions==null) throw  new BadRequestException("Error getting commissions transaction to process. Processing consult System Admin");
            Currencies currencies = transactions.getCurrency();
            transactions.setAmount(transactions.getAmount().add(totalCredit));
            transactions.setBalance(transactions.getBalance().add(totalCredit));
            transactions.setNetAmount(transactions.getNetAmount().add(totalCredit));
            transRepo.save(transactions);
            List<PaymentAudit> audits = new ArrayList<>();
            List<SystemTransactions> receipts = new ArrayList<>();
            for(InsPaymentBean insPaymentBean:paymentBeanList){

                Iterable<PaymentAudit> paymentAudits =  auditRepo.findAll(QPaymentAudit.paymentAudit.settlements.debitRefNo.eq(insPaymentBean.getDebiTrans()).and(QPaymentAudit.paymentAudit.transType.eq("SAG")));
                if(paymentAudits.spliterator().getExactSizeIfKnown() > 1){
                    throw new BadRequestException("Unable to process. More than one settlement transaction generated... Contact Admin......");
                }
                PaymentAudit audit = null;

                if(paymentAudits.spliterator().getExactSizeIfKnown() ==0){
                    audit = new PaymentAudit();
                }
                else{
                    for(PaymentAudit paymentAudit:paymentAudits){
                        audit = paymentAudit;
                        break;
                    }
                }

                audit.setTransType("SAG");
                audit.setDebitTransNo(insPaymentBean.getDebiTrans());
                audit.setReceiptTransNo(insPaymentBean.getCreditTrans());
                audit.setPaymentAmount((audit.getPaymentAmount()!=null)?audit.getPaymentAmount().add(insPaymentBean.getAmount().setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN)):insPaymentBean.getAmount().setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN));
                audit.setOtherTransNo(transactions);
//                audit.setTransNo(sysTrans);
                audits.add(audit);
            }
            auditRepo.save(audits);
            transRepo.save(receipts);
        }

    }

    private void processSubAgentCommTrans(Iterable<SystemTransactions> existingTrans, BigDecimal totalCredit, AccountDef accountDef, Currencies currency, final List<InsPaymentBean> paymentBeanList, final String type) throws BadRequestException {
        if(existingTrans.spliterator().getExactSizeIfKnown() > 1) throw  new BadRequestException("There can be only one unauthorised transactions to be processed. Consult the admin..");
        System.out.println("Total Transactions..."+existingTrans.spliterator().getExactSizeIfKnown());
        if(existingTrans.spliterator().getExactSizeIfKnown()==0){
            Predicate adminPredicate = QSystemSequence.systemSequence.transType.eq("SACP");
            if (sequenceRepo.count(adminPredicate) == 0)
                throw new AdminFeeException("Sequence for Sub-Agent Commission has not been defined");
            SystemSequence sequence = sequenceRepo.findOne(adminPredicate);
            Long seqNumber = sequence.getNextNumber();
            sequence.setLastNumber(seqNumber);
            sequence.setNextNumber(seqNumber + 1);
            sequenceRepo.save(sequence);
            SystemTrans transaction = new SystemTrans();
            transaction.setDoneDate(new Date());
            transaction.setDoneBy(userUtils.getCurrentUser());
            transaction.setTransLevel("U");
            transaction.setTransCode("SAG"); //A way to setup and look up for transaction transcode
            transaction.setTransAuthorised("N");
            SystemTrans createdTrans = systransRepo.save(transaction);
            SystemTransactions trans = new SystemTransactions();
            trans.setAmount(totalCredit);
            trans.setBalance(totalCredit);
            trans.setClientType("SA");
            trans.setAgent(accountDef);
            trans.setTransDate(new Date());
            trans.setRefNo(accountDef.getShtDesc()+"/"+String.format("%04d",seqNumber));
            trans.setTransType("SAG");
            trans.setTransdc("D");
            trans.setControlAcc(accountDef.getShtDesc());
            trans.setCurrency(currencyRepo.findOne(currency.getCurCode()));
            trans.setCurrRate(BigDecimal.ONE);
            trans.setNarrations(type);
            trans.setAuthorised("P");
            trans.setPostedDate(new Date());
            trans.setPostedUser(userUtils.getCurrentUser());
            trans.setNetAmount(totalCredit);
            trans.setTransaction(createdTrans);
            trans.setPayeeName(accountDef.getName());
            trans.setOrigin("U");
            SystemTransactions savedTrans= transRepo.save(trans);
            Currencies currencies = savedTrans.getCurrency();
            List<PaymentAudit> audits = new ArrayList<>();
            List<SystemTransactions> receipts = new ArrayList<>();
            System.out.println(paymentBeanList);
            for(InsPaymentBean insPaymentBean:paymentBeanList){
                final CommissionPayments commissionPayments = commissionPaymentsRepo.findOne(insPaymentBean.getTransId());

                if(commissionPayments==null){
                    throw new BadRequestException("Unable to get Commission Transaction to process....");
                }

                commissionPayments.setAuthorised("P");
                commissionPaymentsRepo.save(commissionPayments);
                Iterable<PaymentAudit> paymentAudits =  auditRepo.findAll(QPaymentAudit.paymentAudit.settlements.debitRefNo.eq(insPaymentBean.getDebiTrans()).and(QPaymentAudit.paymentAudit.transType.eq("SAG")));
                if(paymentAudits.spliterator().getExactSizeIfKnown() > 1){
                    throw new BadRequestException("Unable to process. More than one settlement transaction generated... Contact Admin......");
                }
                PaymentAudit audit = new PaymentAudit();

                if(paymentAudits.spliterator().getExactSizeIfKnown() ==0){
                    audit = new PaymentAudit();
                }
                else{
                    for(PaymentAudit paymentAudit:paymentAudits){
                        audit = paymentAudit;
                        break;
                    }
                }
//                if(audit!=null) {
                   SystemTransactions debitTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getDebiTrans())
                            .and(QSystemTransactions.systemTransactions.clientType.eq("C").and(QSystemTransactions.systemTransactions.transType.in("SAG")))
                            .and(QSystemTransactions.systemTransactions.transdc.eq("C")));
                   if(debitTrans==null){
                       throw new BadRequestException("Unable to get Sub Agent transaction to process. Contact Administrator "+insPaymentBean.getDebiTrans());
                   }
                    audit.setTransType("SAG");
                    audit.setSettlements(null);
                    audit.setTransNo(debitTrans);
                    audit.setDebitTransNo(insPaymentBean.getDebiTrans());
                    audit.setReceiptTransNo(insPaymentBean.getCreditTrans());
                    audit.setCommissionPayments(commissionPayments);
                    audit.setCommAmount(insPaymentBean.getAmount());
//                audit.setCommAmount( (audit.getCommAmount()!=null)? audit.getCommAmount().add(sysTrans.getCommission().multiply(prorata).negate().setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN)):sysTrans.getCommission().multiply(prorata).negate().setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN));
                    audit.setPaymentAmount((audit.getPaymentAmount() != null) ? audit.getPaymentAmount().add(insPaymentBean.getAmount().setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN)) : insPaymentBean.getAmount().setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    audit.setOtherTransNo(savedTrans);
//                audit.setTransNo(sysTrans);
                    audit.setWhtxAmount((BigDecimal.ZERO));
                    audits.add(audit);
//                }
            }
            auditRepo.save(audits);
            transRepo.save(receipts);
        }
        else{
            SystemTransactions transactions = null;
            for (SystemTransactions trans:existingTrans){
                transactions = trans;
            }
            if(transactions==null) throw  new BadRequestException("Error getting commissions transaction to process. Processing consult System Admin");
            Currencies currencies = transactions.getCurrency();
            transactions.setAmount(transactions.getAmount().add(totalCredit));
            transactions.setBalance(transactions.getBalance().add(totalCredit));
            transactions.setNetAmount(transactions.getNetAmount().add(totalCredit));
            transRepo.save(transactions);
            List<PaymentAudit> audits = new ArrayList<>();
            List<SystemTransactions> receipts = new ArrayList<>();
            for(InsPaymentBean insPaymentBean:paymentBeanList){

                final CommissionPayments commissionPayments = commissionPaymentsRepo.findOne(insPaymentBean.getTransId());

                if(commissionPayments==null){
                    throw new BadRequestException("Unable to get Commission Transaction to process....");
                }

                commissionPayments.setAuthorised("P");
                commissionPaymentsRepo.save(commissionPayments);

                Iterable<PaymentAudit> paymentAudits =  auditRepo.findAll(QPaymentAudit.paymentAudit.settlements.debitRefNo.eq(insPaymentBean.getDebiTrans()).and(QPaymentAudit.paymentAudit.transType.eq("SAG")));
                if(paymentAudits.spliterator().getExactSizeIfKnown() > 1){
                    throw new BadRequestException("Unable to process. More than one settlement transaction generated... Contact Admin......");
                }
                PaymentAudit audit = new PaymentAudit();

                if(paymentAudits.spliterator().getExactSizeIfKnown() ==0){
                    audit = new PaymentAudit();
                }
                else{
                    for(PaymentAudit paymentAudit:paymentAudits){
                        audit = paymentAudit;
                        break;
                    }
                }

                audit.setTransType("SAG");
                audit.setDebitTransNo(insPaymentBean.getDebiTrans());
                audit.setReceiptTransNo(insPaymentBean.getCreditTrans());
                audit.setPaymentAmount((audit.getPaymentAmount()!=null)?audit.getPaymentAmount().add(insPaymentBean.getAmount().setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN)):insPaymentBean.getAmount().setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN));
                audit.setOtherTransNo(transactions);
                audit.setCommissionPayments(commissionPayments);
                audit.setCommAmount(insPaymentBean.getAmount());
//                audit.setTransNo(sysTrans);
                audits.add(audit);
            }
            auditRepo.save(audits);
            transRepo.save(receipts);
        }

    }



    private void processTransactions(Iterable<SystemTransactions> existingTrans, BigDecimal totalCredit, AccountDef accountDef, Currencies currency, final List<InsPaymentBean> paymentBeanList, final String type) throws BadRequestException{
        if(existingTrans.spliterator().getExactSizeIfKnown() > 1) throw  new BadRequestException("There can be only one unauthorised transactions to be processed. Consult the admin..");
        if(existingTrans.spliterator().getExactSizeIfKnown()==0){
            Predicate adminPredicate = QSystemSequence.systemSequence.transType.eq("RM");
            if (sequenceRepo.count(adminPredicate) == 0)
                throw new AdminFeeException("Sequence for Remittance Transactions has not been defined");
            SystemSequence sequence = sequenceRepo.findOne(adminPredicate);
            Long seqNumber = sequence.getNextNumber();
            sequence.setLastNumber(seqNumber);
            sequence.setNextNumber(seqNumber + 1);
            sequenceRepo.save(sequence);
            SystemTrans transaction = new SystemTrans();
            transaction.setDoneDate(new Date());
            transaction.setDoneBy(userUtils.getCurrentUser());
            transaction.setTransLevel("U");
            transaction.setTransCode("PM"); //A way to setup and look up for transaction transcode
            transaction.setTransAuthorised("N");
            SystemTrans createdTrans = systransRepo.save(transaction);
            SystemTransactions trans = new SystemTransactions();
            trans.setAmount(totalCredit.abs());
            trans.setBalance(totalCredit.abs());
            trans.setClientType("I");
            trans.setAgent(accountDef);
            trans.setTransDate(new Date());
            trans.setRefNo(accountDef.getShtDesc()+"/"+String.format("%04d",seqNumber));
            trans.setTransType("PM");
            trans.setTransdc("D");
            trans.setControlAcc(accountDef.getShtDesc());
            trans.setCurrency(currencyRepo.findOne(currency.getCurCode()));
            trans.setCurrRate(BigDecimal.ONE);
            trans.setNarrations(type);
            trans.setAuthorised("N");
            trans.setPostedDate(new Date());
            trans.setPostedUser(userUtils.getCurrentUser());
            trans.setNetAmount(totalCredit.abs());
            trans.setTransaction(createdTrans);
            trans.setPayeeName(accountDef.getName());
            trans.setOrigin("U");
            SystemTransactions savedTrans= transRepo.save(trans);
            Currencies currencies = savedTrans.getCurrency();
            List<PaymentAudit> audits = new ArrayList<>();
            List<SystemTransactions> receipts = new ArrayList<>();
            for(InsPaymentBean insPaymentBean:paymentBeanList){
//                ReceiptSettlementDetails settlementDetails = settlementRepo.findOne(insPaymentBean.getSettlementId());
//                if(insPaymentBean.getAmount().setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN).compareTo(settlementDetails.getAllocatedAmt())==1){
//                    throw  new BadRequestException("Entered Allocation Amount: "+insPaymentBean.getAmount()+" ;cannot be greater than expected allocated amount: "+settlementDetails.getAllocatedAmt());
//                }
                Iterable<PaymentAudit> prevAudits = auditRepo.findAll(QPaymentAudit.paymentAudit.settlements.debitRefNo.eq(insPaymentBean.getDebiTrans()).and(QPaymentAudit.paymentAudit.debitTransNo.eq(insPaymentBean.getCreditTrans())));
                BigDecimal prevSettledAmt = BigDecimal.ZERO;
                for(PaymentAudit paymentAudit:prevAudits){
                    prevSettledAmt = prevSettledAmt.add(paymentAudit.getPaymentAmount());
                }
//                SystemTransactions agentTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getDebiTrans())
//                        .and(QSystemTransactions.systemTransactions.clientType.eq("A").and(QSystemTransactions.systemTransactions.transType.in("NBD","APD","RND")))
//                        .and(QSystemTransactions.systemTransactions.transdc.eq("C")));
                SystemTransactions agentTrans=null;
                if (transRepo.count(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getDebiTrans())
                        .and(QSystemTransactions.systemTransactions.transType.in("APC")))==0)
                {
                    agentTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getDebiTrans())
                            .and(QSystemTransactions.systemTransactions.clientType.eq("A").and(QSystemTransactions.systemTransactions.transType.in("NBD", "APD", "RND", "APC")))
                            .and(QSystemTransactions.systemTransactions.transdc.eq("C"))
                    );
                }else {
                    agentTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getDebiTrans())
                            .and(QSystemTransactions.systemTransactions.clientType.eq("A").and(QSystemTransactions.systemTransactions.transType.in("APC")))
                            .and(QSystemTransactions.systemTransactions.transdc.eq("D"))
                    );
                }
                SystemTransactions creditTrans =null;
                ReceiptSettlementDetails settlements = null;
                long counts = settlementRepo.count(QReceiptSettlementDetails.receiptSettlementDetails.debit.refNo.eq(insPaymentBean.getDebiTrans()).and(QReceiptSettlementDetails.receiptSettlementDetails.credit.refNo.eq(insPaymentBean.getCreditTrans())
                        .and(QReceiptSettlementDetails.receiptSettlementDetails.credit.transType.eq("RC"))));
                if(counts==1) {
                    settlements = settlementRepo.findOne(QReceiptSettlementDetails.receiptSettlementDetails.debit.refNo.eq(insPaymentBean.getDebiTrans()).and(QReceiptSettlementDetails.receiptSettlementDetails.credit.refNo.eq(insPaymentBean.getCreditTrans())
                            .and(QReceiptSettlementDetails.receiptSettlementDetails.credit.transType.eq("RC"))));
                }
                if(transRepo.count(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getCreditTrans()).and(QSystemTransactions.systemTransactions.transType.eq("RC"))
                        .and(QSystemTransactions.systemTransactions.transdc.eq("C")))==1)
                    creditTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getCreditTrans()).and(QSystemTransactions.systemTransactions.transType.eq("RC"))
                            .and(QSystemTransactions.systemTransactions.transdc.eq("C")));
                else {
                    long count = settlementRepo.count(QReceiptSettlementDetails.receiptSettlementDetails.debit.refNo.eq(insPaymentBean.getDebiTrans()).and(QReceiptSettlementDetails.receiptSettlementDetails.credit.refNo.eq(insPaymentBean.getCreditTrans())
                            .and(QReceiptSettlementDetails.receiptSettlementDetails.credit.transType.eq("RC"))));
                    if(count==1) {
                        creditTrans = settlementRepo.findOne(QReceiptSettlementDetails.receiptSettlementDetails.debit.refNo.eq(insPaymentBean.getDebiTrans()).and(QReceiptSettlementDetails.receiptSettlementDetails.credit.refNo.eq(insPaymentBean.getCreditTrans())
                                .and(QReceiptSettlementDetails.receiptSettlementDetails.credit.transType.eq("RC")))).getCredit();

                    }
                }
                if(!"CLB".equalsIgnoreCase(insPaymentBean.getTransType())) {
                    if (creditTrans == null)
                        throw new BadRequestException("Unable to get Receipt Transaction to allocate...");
                }
                double prorationRate = insPaymentBean.getAmount().doubleValue()/(agentTrans.getNetAmount().abs().doubleValue());
                SystemTransactions sysTrans = transRepo.findOne(agentTrans.getTransno());
                BigDecimal prorata = new BigDecimal(prorationRate);
                Iterable<PaymentAudit> paymentAudits =  auditRepo.findAll(QPaymentAudit.paymentAudit.settlements.debitRefNo.eq(insPaymentBean.getDebiTrans()).and(QPaymentAudit.paymentAudit.debitTransNo.eq(insPaymentBean.getCreditTrans())));
                if(paymentAudits.spliterator().getExactSizeIfKnown() > 1){
                    throw new BadRequestException("Unable to process. More than one settlement transaction generated... Contact Admin......");
                }
                PaymentAudit audit = null;

                if(paymentAudits.spliterator().getExactSizeIfKnown() ==0){
                    audit = new PaymentAudit();
                }
                else{
                    for(PaymentAudit paymentAudit:paymentAudits){
                        audit = paymentAudit;
                        break;
                    }
                }
                if("CLB".equalsIgnoreCase(insPaymentBean.getTransType())) {
                    audit.setTransType("CLB");
                }
                else{
                    audit.setTransType("NML");
                }
                audit.setSettlements(settlements);
                audit.setDebitTransNo(insPaymentBean.getDebiTrans());
                audit.setReceiptTransNo(insPaymentBean.getCreditTrans());
                audit.setCommAmount( (audit.getCommAmount()!=null)? audit.getCommAmount().add(sysTrans.getCommission().multiply(prorata).negate().setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN)):sysTrans.getCommission().multiply(prorata).negate().setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN));
                audit.setPaymentAmount((audit.getPaymentAmount()!=null)?audit.getPaymentAmount().add(insPaymentBean.getAmount().setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN)):insPaymentBean.getAmount().setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN));
                audit.setOtherTransNo(savedTrans);
                audit.setTransNo(sysTrans);
                audit.setWhtxAmount( (audit.getWhtxAmount()!=null)?audit.getWhtxAmount().add(sysTrans.getWhtx().multiply(prorata).negate().setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN)):sysTrans.getWhtx().multiply(prorata).negate().setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN));
                audits.add(audit);
//                audits.add(audit);
                if(!"CLB".equalsIgnoreCase(insPaymentBean.getTransType())) {
                    creditTrans.setTempSettleAmt((creditTrans.getTempSettleAmt() == null) ? insPaymentBean.getAmount() : creditTrans.getTempSettleAmt().add(insPaymentBean.getAmount()).setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    receipts.add(creditTrans);
                }
            }
            auditRepo.save(audits);
            transRepo.save(receipts);
        }
        else{
            SystemTransactions transactions = null;
            for (SystemTransactions trans:existingTrans){
                transactions = trans;
            }
            if(transactions==null) throw  new BadRequestException("Error getting insurance transaction to process. Processing consult System Admin");
            Currencies currencies = transactions.getCurrency();
            transactions.setAmount(transactions.getAmount().add(totalCredit.abs()));
            transactions.setBalance(transactions.getBalance().add(totalCredit.abs()));
            transactions.setNetAmount(transactions.getNetAmount().add(totalCredit.abs()));
            transRepo.save(transactions);
            List<PaymentAudit> audits = new ArrayList<>();
            List<SystemTransactions> receipts = new ArrayList<>();
            for(InsPaymentBean insPaymentBean:paymentBeanList){
                Iterable<PaymentAudit> prevAudits = auditRepo.findAll(QPaymentAudit.paymentAudit.settlements.debitRefNo.eq(insPaymentBean.getDebiTrans()).and(QPaymentAudit.paymentAudit.debitTransNo.eq(insPaymentBean.getCreditTrans())));
                BigDecimal prevSettledAmt = BigDecimal.ZERO;
                for(PaymentAudit paymentAudit:prevAudits){
                    prevSettledAmt = prevSettledAmt.add(paymentAudit.getPaymentAmount());
                }
                SystemTransactions sysTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getDebiTrans())
                        .and(QSystemTransactions.systemTransactions.clientType.eq("A").and(QSystemTransactions.systemTransactions.transType.in("NBD","APD")))
                        .and(QSystemTransactions.systemTransactions.transdc.eq("C")));
                double prorationRate = insPaymentBean.getAmount().doubleValue()/(sysTrans.getNetAmount().abs().doubleValue());
                BigDecimal prorata = new BigDecimal(prorationRate);
                SystemTransactions creditTrans =null;
                ReceiptSettlementDetails settlements = null;
                long counts = settlementRepo.count(QReceiptSettlementDetails.receiptSettlementDetails.debit.refNo.eq(insPaymentBean.getDebiTrans()).and(QReceiptSettlementDetails.receiptSettlementDetails.credit.refNo.eq(insPaymentBean.getCreditTrans())
                        .and(QReceiptSettlementDetails.receiptSettlementDetails.credit.transType.eq("RC"))));
                if(counts==1) {
                    settlements = settlementRepo.findOne(QReceiptSettlementDetails.receiptSettlementDetails.debit.refNo.eq(insPaymentBean.getDebiTrans()).and(QReceiptSettlementDetails.receiptSettlementDetails.credit.refNo.eq(insPaymentBean.getCreditTrans())
                            .and(QReceiptSettlementDetails.receiptSettlementDetails.credit.transType.eq("RC"))));
                    System.out.println("Settlement found...." + settlements);
                }
                if(transRepo.count(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getCreditTrans()).and(QSystemTransactions.systemTransactions.transType.eq("RC"))
                        .and(QSystemTransactions.systemTransactions.transdc.eq("C")))==1)
                    creditTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getCreditTrans()).and(QSystemTransactions.systemTransactions.transType.eq("RC"))
                            .and(QSystemTransactions.systemTransactions.transdc.eq("C")));
                else
                    creditTrans = settlementRepo.findOne(QReceiptSettlementDetails.receiptSettlementDetails.debit.refNo.eq(insPaymentBean.getDebiTrans()).and(QReceiptSettlementDetails.receiptSettlementDetails.credit.refNo.eq(insPaymentBean.getCreditTrans())
                            .and(QReceiptSettlementDetails.receiptSettlementDetails.credit.transType.eq("RC")))).getCredit();

                if(creditTrans==null)
                    throw new BadRequestException("Unable to get Receipt Transaction to allocate...");

                Iterable<PaymentAudit> paymentAudits =auditRepo.findAll(QPaymentAudit.paymentAudit.settlements.debitRefNo.eq(insPaymentBean.getDebiTrans()).and(QPaymentAudit.paymentAudit.debitTransNo.eq(insPaymentBean.getCreditTrans())));
                if(paymentAudits.spliterator().getExactSizeIfKnown() > 1){
                    throw new BadRequestException("Unable to process. More than one settlement transaction generated... Contact Admin......");
                }
                PaymentAudit audit = null;

                if(paymentAudits.spliterator().getExactSizeIfKnown() ==0){
                    audit = new PaymentAudit();
                }
                else{
                    for(PaymentAudit paymentAudit:paymentAudits){
                        audit = paymentAudit;
                        break;
                    }
                }
                audit.setDebitTransNo(insPaymentBean.getDebiTrans());
                audit.setReceiptTransNo(insPaymentBean.getCreditTrans());
                audit.setCommAmount( (audit.getCommAmount()!=null)? audit.getCommAmount().add(sysTrans.getCommission().multiply(prorata).negate().setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN)):sysTrans.getCommission().multiply(prorata).negate().setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN));
                audit.setPaymentAmount((audit.getPaymentAmount()!=null)?audit.getPaymentAmount().add(insPaymentBean.getAmount().setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN)):insPaymentBean.getAmount().setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN));
                audit.setOtherTransNo(transactions);
                audit.setTransNo(sysTrans);
                audit.setWhtxAmount( (audit.getWhtxAmount()!=null)?audit.getWhtxAmount().add(sysTrans.getWhtx().multiply(prorata).negate().setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN)):sysTrans.getWhtx().multiply(prorata).negate().setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN));
                audit.setSettlements(settlements);
                audits.add(audit);
                creditTrans.setTempSettleAmt((creditTrans.getTempSettleAmt()==null)?insPaymentBean.getAmount(): creditTrans.getTempSettleAmt().add(insPaymentBean.getAmount()).setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN));
                receipts.add(creditTrans);
            }
            auditRepo.save(audits);
            transRepo.save(receipts);
        }
    }

    private void processCreditorCommissions(Iterable<SystemTransactions> existingTrans, BigDecimal totalCredit, AccountDef accountDef, Currencies currency, final List<InsPaymentBean> paymentBeanList, final String type, String transType) throws BadRequestException{
        JdbcTemplate jdb = new JdbcTemplate(this.dataSource);
        if(existingTrans.spliterator().getExactSizeIfKnown() > 1) throw  new BadRequestException("There can be only one unauthorised transactions to be processed. Consult the admin..");
            if (existingTrans.spliterator().getExactSizeIfKnown() == 0) {
                Predicate adminPredicate = QSystemSequence.systemSequence.transType.eq("CRCP");
                if (sequenceRepo.count(adminPredicate) == 0)
                    throw new AdminFeeException("Sequence for Creditor Commissions Payments has not been defined");
                SystemSequence sequence = sequenceRepo.findOne(adminPredicate);
                Long seqNumber = sequence.getNextNumber();
                sequence.setLastNumber(seqNumber);
                sequence.setNextNumber(seqNumber + 1);
                sequenceRepo.save(sequence);
                SystemTrans transaction = new SystemTrans();
                transaction.setDoneDate(new Date());
                transaction.setDoneBy(userUtils.getCurrentUser());
                transaction.setTransLevel("U");
                transaction.setTransCode(transType); //A way to setup and look up for transaction transcode
                transaction.setTransAuthorised("N");
                SystemTrans createdTrans = systransRepo.save(transaction);
                SystemTransactions trans = new SystemTransactions();
                trans.setAmount(totalCredit.abs());
                trans.setBalance(totalCredit.abs());
                trans.setClientType("I");
                trans.setAgent(accountDef);
                trans.setTransDate(new Date());
                trans.setRefNo(accountDef.getShtDesc() + "/" + String.format("%04d", seqNumber));
                trans.setTransType(transType);
                trans.setTransdc("D");
                trans.setControlAcc(accountDef.getShtDesc());
                trans.setCurrency(currencyRepo.findOne(currency.getCurCode()));
                trans.setCurrRate(BigDecimal.ONE);
                trans.setNarrations(type);
                trans.setAuthorised("P");
                trans.setPostedDate(new Date());
                trans.setPostedUser(userUtils.getCurrentUser());
                trans.setNetAmount(totalCredit.abs());
                trans.setTransaction(createdTrans);
                trans.setPayeeName(accountDef.getName());
                trans.setOrigin("U");
                SystemTransactions savedTrans = transRepo.save(trans);
                Currencies currencies = savedTrans.getCurrency();
                List<PaymentAudit> audits = new ArrayList<>();
                List<SystemTransactions> receipts = new ArrayList<>();
                for (InsPaymentBean insPaymentBean : paymentBeanList) {
//                ReceiptSettlementDetails settlementDetails = settlementRepo.findOne(insPaymentBean.getSettlementId());
//                if(insPaymentBean.getAmount().setScale(currencies.getRoundOff(),BigDecimal.ROUND_HALF_EVEN).compareTo(settlementDetails.getAllocatedAmt())==1){
//                    throw  new BadRequestException("Entered Allocation Amount: "+insPaymentBean.getAmount()+" ;cannot be greater than expected allocated amount: "+settlementDetails.getAllocatedAmt());
//                }
                    final CommissionPayments commissionPayments = commissionPaymentsRepo.findOne(insPaymentBean.getTransId());

                    if(commissionPayments==null){
                        throw new BadRequestException("Unable to get Commission Transaction to process....");
                    }

                    commissionPayments.setAuthorised("P");
                    commissionPaymentsRepo.save(commissionPayments);

//                    Iterable<PaymentAudit> prevAudits = auditRepo.findAll(QPaymentAudit.paymentAudit.settlements.debitRefNo.eq(insPaymentBean.getDebiTrans()).and((QPaymentAudit.paymentAudit.paidStatus.isNotNull().and(QPaymentAudit.paymentAudit.paidStatus.eq("Y")))));
//                    BigDecimal prevSettledAmt = BigDecimal.ZERO;
//                    for (PaymentAudit paymentAudit : prevAudits) {
//                        prevSettledAmt = prevSettledAmt.add(paymentAudit.getPaymentAmount());
//                    }
//                SystemTransactions agentTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getDebiTrans())
//                        .and(QSystemTransactions.systemTransactions.clientType.eq("A").and(QSystemTransactions.systemTransactions.transType.in("NBD","APD","RND")))
//                        .and(QSystemTransactions.systemTransactions.transdc.eq("C")));
                    SystemTransactions agentTrans = null;
                    if (transRepo.count(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getDebiTrans())
                            .and(QSystemTransactions.systemTransactions.transType.in("APC"))) == 0) {
                        agentTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getDebiTrans())
                                .and(QSystemTransactions.systemTransactions.clientType.eq("A").and(QSystemTransactions.systemTransactions.transType.in("NBD", "APD", "RND", "APC")))
                                .and(QSystemTransactions.systemTransactions.transdc.eq("C"))
                        );
                    } else {
                        agentTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getDebiTrans())
                                .and(QSystemTransactions.systemTransactions.clientType.eq("A").and(QSystemTransactions.systemTransactions.transType.in("APC","BUD")))
                                .and(QSystemTransactions.systemTransactions.transdc.eq("D"))
                        );
                    }

                    //To take care of life policy transactions...
                    if(agentTrans==null){
                        agentTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getDebiTrans())
                                .and(QSystemTransactions.systemTransactions.clientType.eq("A").and(QSystemTransactions.systemTransactions.transType.in("NBD","APD","BUD")))
                                .and(QSystemTransactions.systemTransactions.transdc.eq("D"))
                        );
                    }
//                    SystemTransactions creditTrans = null;
                    ReceiptSettlementDetails settlements = null;
                    List<BigInteger> settlement = settlementRepo.getCreditSettlements(insPaymentBean.getDebiTrans(),insPaymentBean.getCreditTrans());
                    if(settlement.size()==1){
                        settlements = settlementRepo.findOne(settlement.get(0).longValue());
                    }
//                    if (transRepo.count(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getCreditTrans()).and(QSystemTransactions.systemTransactions.transType.eq("RC"))
//                            .and(QSystemTransactions.systemTransactions.transdc.eq("C"))) == 1)
//                        creditTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getCreditTrans()).and(QSystemTransactions.systemTransactions.transType.eq("RC"))
//                                .and(QSystemTransactions.systemTransactions.transdc.eq("C")));
//                    else {
//                        long count = settlementRepo.count(QReceiptSettlementDetails.receiptSettlementDetails.debit.refNo.eq(insPaymentBean.getDebiTrans()).and(QReceiptSettlementDetails.receiptSettlementDetails.credit.refNo.eq(insPaymentBean.getCreditTrans())
//                                .and(QReceiptSettlementDetails.receiptSettlementDetails.credit.transType.eq("RC"))));
//                        if (count == 1) {
//                            creditTrans = settlementRepo.findOne(QReceiptSettlementDetails.receiptSettlementDetails.debit.refNo.eq(insPaymentBean.getDebiTrans()).and(QReceiptSettlementDetails.receiptSettlementDetails.credit.refNo.eq(insPaymentBean.getCreditTrans())
//                                    .and(QReceiptSettlementDetails.receiptSettlementDetails.credit.transType.eq("RC")))).getCredit();
//
//                        }
//                    }
//                    if (!"CLB".equalsIgnoreCase(insPaymentBean.getTransType())) {
//                        if (creditTrans == null)
//                            throw new BadRequestException("Unable to get Receipt Transaction to allocate...");
//                    }
                    //double prorationRate = insPaymentBean.getAmount().doubleValue() / (agentTrans.getNetAmount().abs().doubleValue());
                    SystemTransactions sysTrans = transRepo.findOne(agentTrans.getTransno());
                    //BigDecimal prorata = new BigDecimal(prorationRate);
                    Iterable<PaymentAudit> paymentAudits = auditRepo.findAll(QPaymentAudit.paymentAudit.settlements.debitRefNo.eq(insPaymentBean.getDebiTrans()).and(QPaymentAudit.paymentAudit.debitTransNo.eq(insPaymentBean.getCreditTrans())));
                    if (paymentAudits.spliterator().getExactSizeIfKnown() > 1) {
                        throw new BadRequestException("Unable to process. More than one settlement transaction generated... Contact Admin......");
                    }
                    PaymentAudit audit = new PaymentAudit();

                    if (paymentAudits.spliterator().getExactSizeIfKnown() == 0) {
                        audit = new PaymentAudit();
                    } else {
                        for (PaymentAudit paymentAudit : paymentAudits) {
                            audit = paymentAudit;
                            break;
                        }
                    }
                    if(transType!=null && transType.equalsIgnoreCase("COM")) {
                        if ("CLB".equalsIgnoreCase(insPaymentBean.getTransType())) {
                            audit.setTransType("CLB");
                        } else {
                            audit.setTransType("NML");
                        }
                    }
                    else if(transType.equalsIgnoreCase("ADM")) {
                        audit.setTransType("ADM");
                    }
                    audit.setSettlements(settlements);
                    audit.setDebitTransNo(insPaymentBean.getDebiTrans());
                    audit.setReceiptTransNo(insPaymentBean.getCreditTrans());
                    audit.setCommAmount(commissionPayments.getAmount());
                    audit.setPaymentAmount(commissionPayments.getNetAmount());
                    audit.setCommissionPayments(commissionPayments);
                    audit.setOtherTransNo(savedTrans);
                    audit.setTransNo(sysTrans);
                    audit.setWhtxAmount(commissionPayments.getWhtx());
                    audits.add(audit);
//                audits.add(audit);
//                    if (!"CLB".equalsIgnoreCase(insPaymentBean.getTransType())) {
//                        creditTrans.setTempSettleAmt((creditTrans.getTempSettleAmt() == null) ? insPaymentBean.getAmount() : creditTrans.getTempSettleAmt().add(insPaymentBean.getAmount()).setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
//                        receipts.add(creditTrans);
//                    }
                }
                auditRepo.save(audits);
//                transRepo.save(receipts);
            } else {
                SystemTransactions transactions = null;
                for (SystemTransactions trans : existingTrans) {
                    transactions = trans;
                }
                if (transactions == null)
                    throw new BadRequestException("Error getting insurance transaction to process. Processing consult System Admin");
                Currencies currencies = transactions.getCurrency();
                transactions.setAmount(transactions.getAmount().add(totalCredit.abs()));
                transactions.setBalance(transactions.getBalance().add(totalCredit.abs()));
                transactions.setNetAmount(transactions.getNetAmount().add(totalCredit.abs()));
                transRepo.save(transactions);
                List<PaymentAudit> audits = new ArrayList<>();
                List<SystemTransactions> receipts = new ArrayList<>();
                for (InsPaymentBean insPaymentBean : paymentBeanList) {
//                    Iterable<PaymentAudit> prevAudits = auditRepo.findAll(QPaymentAudit.paymentAudit.settlements.debitRefNo.eq(insPaymentBean.getDebiTrans()).and(QPaymentAudit.paymentAudit.debitTransNo.eq(insPaymentBean.getCreditTrans())));
//                    BigDecimal prevSettledAmt = BigDecimal.ZERO;
//                    for (PaymentAudit paymentAudit : prevAudits) {
//                        prevSettledAmt = prevSettledAmt.add(paymentAudit.getPaymentAmount());
//                    }
                    SystemTransactions sysTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getDebiTrans())
                            .and(QSystemTransactions.systemTransactions.clientType.eq("A").and(QSystemTransactions.systemTransactions.transType.in("NBD", "APD")))
                            .and(QSystemTransactions.systemTransactions.transdc.eq("C")));
                    double prorationRate = insPaymentBean.getAmount().doubleValue() / (sysTrans.getNetAmount().abs().doubleValue());
                    BigDecimal prorata = new BigDecimal(prorationRate);
                    //SystemTransactions creditTrans = null;
                    ReceiptSettlementDetails settlements = null;
                    long counts = settlementRepo.count(QReceiptSettlementDetails.receiptSettlementDetails.debit.refNo.eq(insPaymentBean.getDebiTrans()).and(QReceiptSettlementDetails.receiptSettlementDetails.credit.refNo.eq(insPaymentBean.getCreditTrans())
                            .and(QReceiptSettlementDetails.receiptSettlementDetails.credit.transType.eq("RC"))));
                    if (counts == 1) {
                        settlements = settlementRepo.findOne(QReceiptSettlementDetails.receiptSettlementDetails.debit.refNo.eq(insPaymentBean.getDebiTrans()).and(QReceiptSettlementDetails.receiptSettlementDetails.credit.refNo.eq(insPaymentBean.getCreditTrans())
                                .and(QReceiptSettlementDetails.receiptSettlementDetails.credit.transType.eq("RC"))));
                        System.out.println("Settlement found...." + settlements);
                    }
                    final CommissionPayments commissionPayments = commissionPaymentsRepo.findOne(insPaymentBean.getTransId());

                    if(commissionPayments==null){
                        throw new BadRequestException("Unable to get Commission Transaction to process....");
                    }

                    commissionPayments.setAuthorised("P");
                    commissionPaymentsRepo.save(commissionPayments);
//                    if (transRepo.count(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getCreditTrans()).and(QSystemTransactions.systemTransactions.transType.eq("RC"))
//                            .and(QSystemTransactions.systemTransactions.transdc.eq("C"))) == 1)
//                        creditTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(insPaymentBean.getCreditTrans()).and(QSystemTransactions.systemTransactions.transType.eq("RC"))
//                                .and(QSystemTransactions.systemTransactions.transdc.eq("C")));
//                    else
//                        creditTrans = settlementRepo.findOne(QReceiptSettlementDetails.receiptSettlementDetails.debit.refNo.eq(insPaymentBean.getDebiTrans()).and(QReceiptSettlementDetails.receiptSettlementDetails.credit.refNo.eq(insPaymentBean.getCreditTrans())
//                                .and(QReceiptSettlementDetails.receiptSettlementDetails.credit.transType.eq("RC")))).getCredit();
//
//                    if (creditTrans == null)
//                        throw new BadRequestException("Unable to get Receipt Transaction to allocate...");
                    Iterable<PaymentAudit> paymentAudits = auditRepo.findAll(QPaymentAudit.paymentAudit.settlements.debitRefNo.eq(insPaymentBean.getDebiTrans()).and(QPaymentAudit.paymentAudit.debitTransNo.eq(insPaymentBean.getCreditTrans())));

                    if (paymentAudits.spliterator().getExactSizeIfKnown() > 1) {
                        throw new BadRequestException("Unable to process. More than one settlement transaction generated... Contact Admin......");
                    }
                    PaymentAudit audit = new PaymentAudit();

                    if (paymentAudits.spliterator().getExactSizeIfKnown() == 0) {
                        audit = new PaymentAudit();
                    } else {
                        for (PaymentAudit paymentAudit : paymentAudits) {
                            audit = paymentAudit;
                            break;
                        }
                    }
                    audit.setDebitTransNo(insPaymentBean.getDebiTrans());
                    audit.setReceiptTransNo(insPaymentBean.getCreditTrans());
                    audit.setCommAmount((audit.getCommAmount() != null) ? audit.getCommAmount().add(sysTrans.getCommission().multiply(prorata).negate().setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN)) : sysTrans.getCommission().multiply(prorata).negate().setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    audit.setPaymentAmount((audit.getPaymentAmount() != null) ? audit.getPaymentAmount().add(insPaymentBean.getAmount().setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN)) : insPaymentBean.getAmount().setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    audit.setOtherTransNo(transactions);
                    audit.setTransNo(sysTrans);
                    audit.setWhtxAmount((audit.getWhtxAmount() != null) ? audit.getWhtxAmount().add(sysTrans.getWhtx().multiply(prorata).negate().setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN)) : sysTrans.getWhtx().multiply(prorata).negate().setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
                    audit.setSettlements(settlements);
                    audit.setCommissionPayments(commissionPayments);
                    audits.add(audit);
//                    creditTrans.setTempSettleAmt((creditTrans.getTempSettleAmt() == null) ? insPaymentBean.getAmount() : creditTrans.getTempSettleAmt().add(insPaymentBean.getAmount()).setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN));
//                    receipts.add(creditTrans);
                }
                auditRepo.save(audits);
//                transRepo.save(receipts);
            }
    }
    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void deleteAudit(Long auditId) throws BadRequestException {
        if(auditId==null) throw new BadRequestException("Transaction to delete not found.....");
        PaymentAudit audit = auditRepo.findOne(auditId);

        SystemTransactions creditTrans =null;
        if("NML".equalsIgnoreCase(audit.getTransType())) {
            if (transRepo.count(QSystemTransactions.systemTransactions.refNo.eq(audit.getReceiptTransNo()).and(QSystemTransactions.systemTransactions.transType.in("RC","APC"))
                    .and(QSystemTransactions.systemTransactions.transdc.eq("C"))) == 1)
                creditTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(audit.getReceiptTransNo()).and(QSystemTransactions.systemTransactions.transType.in("RC","APC"))
                        .and(QSystemTransactions.systemTransactions.transdc.eq("C")));
            else
                creditTrans = settlementRepo.findOne(QReceiptSettlementDetails.receiptSettlementDetails.debit.refNo.eq(audit.getDebitTransNo()).and(QReceiptSettlementDetails.receiptSettlementDetails.credit.refNo.eq(audit.getReceiptTransNo())
                        .and(QReceiptSettlementDetails.receiptSettlementDetails.credit.transType.in("RC","APC")))).getCredit();

            if (creditTrans == null)
                throw new BadRequestException("Unable to get Receipt Transaction to allocate...");
        }
        SystemTransactions transactions = audit.getOtherTransNo();
        if(transactions.getAuthorised()!=null){
            if("Y".equalsIgnoreCase(transactions.getAuthorised())){
                throw new BadRequestException("Cannot Undo..The Transaction is already authorized...");
            }
        }

        transactions.setBalance(transactions.getBalance().subtract(audit.getPaymentAmount()));
        transactions.setAmount(transactions.getAmount().subtract(audit.getPaymentAmount()));
        transactions.setNetAmount(transactions.getNetAmount().subtract(audit.getPaymentAmount()));
        if("NML".equalsIgnoreCase(audit.getTransType())) {
            creditTrans.setTempSettleAmt(creditTrans.getTempSettleAmt().subtract(audit.getPaymentAmount()));
            transRepo.save(creditTrans);
        }
        SystemTransactions trans = transRepo.save(transactions);
        auditRepo.delete(auditId);

        if(trans.getBalance().compareTo(BigDecimal.ZERO)==0){
            transRepo.delete(trans.getTransno());
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void cancelPaymentTrans(Long transNo) throws BadRequestException {
        JdbcTemplate template = new JdbcTemplate(this.dataSource);
        if(transNo==null) throw new BadRequestException("Transaction to Cancel not found.....");
        SystemTransactions trans = transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(transNo));
        if(trans.getAuthorised()!=null){
            if("Y".equalsIgnoreCase(trans.getAuthorised())){
                throw new BadRequestException("Cannot Cancel..The Transaction is already authorized...");
            }
        }
        List<Object[]> audits = auditRepo.getOtherTransAudits(transNo);
//        List<SystemTransactions> creditTransactions = new ArrayList<>();

        for(Object[] paymentAudit:audits){
            if("NML".equalsIgnoreCase((String) paymentAudit[0])||"ADM".equalsIgnoreCase((String) paymentAudit[0])) {
//                SystemTransactions creditTrans = null;
                final String receiptNo = ((String)paymentAudit[1]);
                final String debitReceiptNo = ((String)paymentAudit[2]);
                final Long id = ((BigInteger)paymentAudit[3]).longValue();
                final Long commId =(paymentAudit[6]!=null)? ((BigInteger)paymentAudit[6]).longValue():null;
                if(commId==null){
                    throw new BadRequestException("An Error Occured when Deleting Commission Record...");
                }
                CommissionPayments commissionPayments = commissionPaymentsRepo.findOne(commId);
                if(commissionPayments.getLoaded()!=null &&"Y".equalsIgnoreCase(commissionPayments.getLoaded())){
                    commissionPaymentsRepo.delete(commId);
                }
                else {
                    commissionPayments.setAuthorised("N");
                    commissionPaymentsRepo.save(commissionPayments);
                }
//                if (transRepo.count(QSystemTransactions.systemTransactions.refNo.eq(receiptNo).and(QSystemTransactions.systemTransactions.transType.in("RC","APC"))
//                        .and(QSystemTransactions.systemTransactions.transdc.eq("C"))) == 1)
//                    creditTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(receiptNo).and(QSystemTransactions.systemTransactions.transType.in("RC","APC"))
//                            .and(QSystemTransactions.systemTransactions.transdc.eq("C")));
//                else
//                    creditTrans = settlementRepo.findOne(QReceiptSettlementDetails.receiptSettlementDetails.debit.refNo.eq(debitReceiptNo).and(QReceiptSettlementDetails.receiptSettlementDetails.credit.refNo.eq(receiptNo)
//                            .and(QReceiptSettlementDetails.receiptSettlementDetails.credit.transType.in("RC","APC")))).getCredit();
//
//                if (creditTrans == null)
//                    throw new BadRequestException("Unable to get Receipt Transaction to allocate...");
//                creditTrans.setTempSettleAmt(BigDecimal.ZERO);
//                creditTransactions.add(creditTrans);
                template.update("delete from sys_brk_payment_audit where pa_other_trans=?", new Object[]{id});
            }
            else  if("SAG".equalsIgnoreCase((String) paymentAudit[0])){
                final Long id = ((BigInteger)paymentAudit[3]).longValue();
                template.update("delete from sys_brk_payment_audit where pa_other_trans=?", new Object[]{id});
            }

        }

//        if(!creditTransactions.isEmpty())
//            transRepo.save(creditTransactions);
        transRepo.delete(trans);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void rejectAuthTrans(Long transNo) throws BadRequestException {
        if(transNo==null) throw new BadRequestException("Transaction to Reject not found.....");
        SystemTransactions trans = transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(transNo));
        if(trans.getAuthorised()!=null){
            if("Y".equalsIgnoreCase(trans.getAuthorised())){
                throw new BadRequestException("Cannot Reject..The Commission is already authorized and receipted...");
            }
        }
        trans.setAuthorised("P");
        transRepo.save(trans);
    }

//    @Override
//    @Transactional(readOnly = true)
//    public DataTablesResult<SystemTransactions> findAuthorizeTrans(DataTablesRequest request) throws IllegalAccessException {
//        BooleanExpression pred = (QSystemTransactions.systemTransactions.authorised.isNull().or(QSystemTransactions.systemTransactions.authorised.eq("N")))
//                .and(QSystemTransactions.systemTransactions.transType.notIn("RFC","RFD"));
//        Page<SystemTransactions> page = transRepo.findAll(pred.and(request.searchPredicate(QSystemTransactions.systemTransactions)), request);
//        return new DataTablesResult<>(request, page);
//    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<SystemTransDTO> findAuthorizeTrans(DataTablesRequest request)  {
        List<Object[]> authList = transRepo.findAuthTransactions(request.getPageNumber(), request.getPageSize());
        List<SystemTransDTO> transList = new ArrayList<>();
        long rowCount = 0L;
        if(!authList.isEmpty()) rowCount = ((BigInteger)authList.get(0)[15]).intValue();
        for(Object[] trans:authList){
            SystemTransDTO transDTO =  SystemTransDTO.instance( ((BigInteger)trans[0]).longValue(),
                    null, (Date) trans[1],
                    (String) trans[2], (String) trans[3],
                    (String) trans[4], (String) trans[5], (String) trans[6], (String) trans[7], (String) trans[8], (String) trans[9], (BigDecimal) trans[10], (BigDecimal) trans[11],
                    (BigDecimal) trans[12],(String) trans[13], (String) trans[14]);
            transList.add(transDTO);

        }
        Page<SystemTransDTO> page = new PageImpl<>(transList, request, rowCount);
        return new DataTablesResult(request, page);
    }

    @PreAuthorize("hasAnyAuthority('AUTHORIZE_ACCOUNT_TRANS')")
    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void authAccountTrans(Long transId) throws BadRequestException {
        if (transId == null) throw new BadRequestException("No Transaction to Authorize");
        List<Object[]> audits = auditRepo.getTransApprove(transId);
        List<PaymentAudit> paymentAudits = new ArrayList<>();
        List<SystemTransactions> transactions = new ArrayList<>();
        final ReceiptTrans receiptTrans =new ReceiptTrans();
        receiptTrans.setReceiptType("COM");
        receiptTrans.setPaidBy("Auto Receipted on Authorisation");
        Iterable<CollectionAccounts> collectionAccounts = collectionAcctsRepo.findAll();
        if(collectionAccounts.spliterator().getExactSizeIfKnown()!=1){
            throw new BadRequestException("Collection is not set up...Cannot continue..");
        }
        List<BigInteger> branches = branchRepository.findUserBranches(userUtils.getCurrentUser().getId());
        if(branches.isEmpty()){
            throw new BadRequestException("Unable to get branch for user logged in.. Cannot authorise");
        }
        final Long branchId = branches.get(0).longValue();
        CollectionAccounts collectionAccounts1 = Streamable.streamOf(collectionAccounts).findFirst().get();
        receiptTrans.setPayId(collectionAccounts1.getCaId());
        receiptTrans.setBrnCode(branchId);
        List<ReceiptTransDtls> transDtlsList = new ArrayList<>();
        BigDecimal amount = BigDecimal.ZERO;
        for (Object[] paymentAudit : audits) {
            final Long commId =(paymentAudit[16]!=null)? ((BigInteger) paymentAudit[16]).longValue():null;
            PaymentAudit audit = new PaymentAudit();
            audit.setPaId(((BigInteger) paymentAudit[0]).longValue());
            audit.setTransType((String) paymentAudit[15]);

            if(commId==null){
                throw new BadRequestException("Please Reject the transaction and Process Again...");
            }
            CommissionPayments commissionPayments = commissionPaymentsRepo.findOne(commId);
            commissionPayments.setAuthorised("Y");
            commissionPayments.setAuthorisedBy(userUtils.getCurrentUser());
            commissionPayments.setAuthorisedDate(new Date());
            commissionPaymentsRepo.save(commissionPayments);
            audit.setCommissionPayments(commissionPayments);
            if ( "NML".equalsIgnoreCase(audit.getTransType()) || "CLB".equalsIgnoreCase(audit.getTransType()) ||"ADM".equalsIgnoreCase(audit.getTransType())) {
                audit.setTransNo(transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(((BigInteger) paymentAudit[1]).longValue())));
                if(paymentAudit[2]!=null) {
                    audit.setSettlements(settlementRepo.findOne(QReceiptSettlementDetails.receiptSettlementDetails.settlementId.eq(((BigInteger) paymentAudit[2]).longValue())));
                }
                audit.setReceiptTransNo((String) paymentAudit[3]);
                audit.setDebitTransNo((String) paymentAudit[4]);
                audit.setOtherTransNo(transRepo.findOne(((BigInteger) paymentAudit[5]).longValue()));
                audit.setCommAmount((BigDecimal) paymentAudit[6]);
                audit.setWhtxAmount((BigDecimal) paymentAudit[7]);
                audit.setPaymentAmount((BigDecimal) paymentAudit[8]);
                System.out.println(" Amount..."+audit.getPaymentAmount()+" Whtx .."+audit.getWhtxAmount()+" Gross "+audit.getCommAmount());
            }
            audit.setPostDate(new Date());
            audit.setPosted("Y");
            audit.setPaidStatus("Y");
            audit.setPostedBy(userUtils.getCurrentUser());
            if ("NML".equalsIgnoreCase(audit.getTransType())||"ADM".equalsIgnoreCase(audit.getTransType())) {
                SystemTransactions credit = audit.getTransNo();
                if(credit.getBalance()!=null && credit.getBalance().doubleValue() != 0){
                    credit.setBalance(credit.getBalance().add(audit.getPaymentAmount().abs()));
                    if (credit.getSettleAmt() == null)
                        credit.setSettleAmt(audit.getPaymentAmount());
                    else
                        credit.setSettleAmt(credit.getSettleAmt().add(audit.getPaymentAmount()));
                }
                amount = amount.add(audit.getPaymentAmount());
                ReceiptTransDtls dtls = new ReceiptTransDtls();
                dtls.setTransNo(transId);
                dtls.setPolicy(audit.getTransNo().getPolicy());
                dtls.setPaymentAudit(audit);
                dtls.setRctAmount(audit.getPaymentAmount());
                transDtlsList.add(dtls);
                System.out.println("Receipt Amount..."+audit.getPaymentAmount());

//                receiptTrans.setDetails(transDtlsList);
//                SystemTransactions creditTrans = null;
//                if (transRepo.count(QSystemTransactions.systemTransactions.refNo.eq(audit.getReceiptTransNo()).and(QSystemTransactions.systemTransactions.transType.in("RC", "APC"))
//                        .and(QSystemTransactions.systemTransactions.transdc.eq("C"))) == 1)
//                    creditTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(audit.getReceiptTransNo()).and(QSystemTransactions.systemTransactions.transType.in("RC", "APC"))
//                            .and(QSystemTransactions.systemTransactions.transdc.eq("C")));
//                else
//                    creditTrans = settlementRepo.findOne(QReceiptSettlementDetails.receiptSettlementDetails.debit.refNo.eq(audit.getDebitTransNo()).and(QReceiptSettlementDetails.receiptSettlementDetails.credit.refNo.eq(audit.getReceiptTransNo())
//                            .and(QReceiptSettlementDetails.receiptSettlementDetails.credit.transType.in("RC", "APC")))).getCredit();
//
//                if (creditTrans == null)
//                    throw new BadRequestException("Unable to get Receipt Transaction to allocate...");
////            SystemTransactions creditTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(audit.getReceiptTransNo()).and(QSystemTransactions.systemTransactions.transType.eq("RC"))
////                    .and(QSystemTransactions.systemTransactions.transdc.eq("C")));
//                creditTrans.setTempSettleAmt(BigDecimal.ZERO);
//                transactions.add(credit);
//                transactions.add(creditTrans);
            } else if ("CLB".equalsIgnoreCase(audit.getTransType())) {
                long count = transRepo.count(QSystemTransactions.systemTransactions.refNo.eq(audit.getDebitTransNo()).and(QSystemTransactions.systemTransactions.clientType.eq("A"))
                        .and(QSystemTransactions.systemTransactions.transdc.eq("D"))
                        .and(QSystemTransactions.systemTransactions.transType.eq("AGR")));
                if (count == 1) {
                    SystemTransactions crTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(audit.getDebitTransNo()).and(QSystemTransactions.systemTransactions.clientType.eq("A"))
                            .and(QSystemTransactions.systemTransactions.transdc.eq("D"))
                            .and(QSystemTransactions.systemTransactions.transType.eq("AGR")));
                    crTrans.setBalance(crTrans.getBalance().subtract(audit.getPaymentAmount().abs()));
                    crTrans.setSettleAmt(audit.getPaymentAmount().abs());
                    transactions.add(crTrans);
                } else
                    throw new BadRequestException("Error getting Agent Transaction to allocate...Contact System Admin");
            }
            else if ("SAG".equalsIgnoreCase(audit.getTransType())) {
                SystemTransactions transactions1 = transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(((BigInteger) paymentAudit[1]).longValue()));
                transactions1.setAuthDate(new Date());
                transRepo.save(transactions1);
                audit.setTransNo(transactions1);
                SystemTransactions transNo =audit.getTransNo();
                postSubAgentTransactions(transNo,(BigDecimal) paymentAudit[8]);
                audit.setCommAmount((BigDecimal) paymentAudit[6]);
                audit.setWhtxAmount((BigDecimal) paymentAudit[7]);
                audit.setPaymentAmount((BigDecimal) paymentAudit[8]);
            }
            paymentAudits.add(audit);
        }
        if (!authLimits.checkAuthorizationLimits("AUTHORIZE_ACCOUNT_TRANS", amount)) {
            throw new BadRequestException("You have no rights to authorize the transaction...Check your authorization limits..");
        }
        auditRepo.save(paymentAudits);
        SystemTransactions trans = transRepo.findOne(transId);
        trans.setAuthorised("Y");
        trans.setAuthDate(new Date());
        trans.setUserAuth(userUtils.getCurrentUser().getName());
        transactions.add(trans);
        transRepo.save(transactions);
        if(!transDtlsList.isEmpty()) {
            receiptTrans.setReceiptAmount(amount);
            receiptTrans.setDetails(transDtlsList);
            receiptService.createReceipt(receiptTrans, true);
        }

    }

    @Transactional(rollbackFor = BadRequestException.class,propagation = Propagation.REQUIRED)
    public void postSubAgentTransactions(SystemTransactions transactions, BigDecimal paymentAmt) throws BadRequestException {

        Iterable<CollectionAccounts> collectionAccounts = collectionAcctsRepo.findAll();
        if(collectionAccounts.spliterator().getExactSizeIfKnown()!=1){
            throw new BadRequestException("Collection is not set up...Cannot continue..");
        }
        CollectionAccounts collectionAccounts1 = Streamable.streamOf(collectionAccounts).findFirst().get();
        final BigDecimal subAgentAmt = transactions.getNetAmount();

        if(subAgentAmt==null){
            throw new BadRequestException("Sub Agent Amount cannot be null");
        }
        if(transactions.getPolicy()==null){
            throw new BadRequestException("Policy cannot be found for this sub agent for this transaction. Cannot continue");
        }
        final PolicyTrans policy = transactions.getPolicy();
        SystemTrans transaction = new SystemTrans();
        transaction.setDoneDate(new Date());
        transaction.setDoneBy(userUtils.getCurrentUser());
        transaction.setPolicy(policy);
        transaction.setTransLevel("U");
        transaction.setTransCode("SAGPYMT");
        transaction.setTransAuthorised("Y");
        transaction.setAuthBy(userUtils.getCurrentUser());
        transaction.setAuthDate(new Date());
        SystemTrans createdTrans = systransRepo.save(transaction);
        final Date postDate = new Date();
        final AccountDef accountDef = transactions.getPolicy().getAgent();
        List<GlTransactions> glTransactions = new ArrayList<>();
        GlTransactions debit = new GlTransactions();
        debit.setAmount(subAgentAmt.abs());
        debit.setAuthDate(postDate);
        debit.setbCuramount(paymentAmt.abs());
        debit.setBranch(policy.getBranch());
        debit.setCurrency(policy.getTransCurrency());
        debit.setGlAcc(accountsUtilities.getGlCreditAccount(RevenueItems.SAC, null));
        if(paymentAmt.compareTo(BigDecimal.ZERO)> 0)
        debit.setGldc("D");
        else
            debit.setGldc("C");
        debit.setTransaction(createdTrans);
        debit.setTransLevel("U");
        debit.setTrntCode(policy.getTransType());
        debit.setGlYear(dateUtils.getUwYear(postDate));
        debit.setGlMonth(dateUtils.getMonth(postDate));
        debit.setNarration(String.format("Posting Sub Agent Commission Payment for Policy No: %s, Ref No: %s", policy.getPolNo(), policy.getPolRevNo()));
        debit.setPolicyTrans(policy);
        debit.setTransType("SUBAGCOMMPYMT");
        glTransactions.add(debit);
        GlTransactions credit = new GlTransactions();
        credit.setAmount(subAgentAmt.abs());
        credit.setAuthDate(postDate);
        credit.setbCuramount(paymentAmt.abs());
        credit.setBranch(policy.getBranch());
        credit.setCurrency(policy.getTransCurrency());
        credit.setGlAcc(collectionAccounts1.getAccounts());
        if(paymentAmt.compareTo(BigDecimal.ZERO)> 0)
        credit.setGldc("C");
        else
            credit.setGldc("D");
        credit.setTransaction(createdTrans);
        credit.setTransLevel("U");
        credit.setTrntCode(policy.getTransType());
        credit.setGlYear(dateUtils.getUwYear(postDate));
        credit.setGlMonth(dateUtils.getMonth(postDate));
        credit.setNarration(String.format("Posting Sub Agent Commission Payment for Policy No: %s, Ref No: %s", policy.getPolNo(), policy.getPolRevNo()));
        credit.setPolicyTrans(policy);
        credit.setTransType("SUBAGCOMMPYMT");
        glTransactions.add(credit);
        glTransRepo.save(glTransactions);

    }


    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void approveCreditorComm(Long transId) throws BadRequestException {
        System.out.println("transId "+transId);
        SystemTransactions trans = transRepo.findOne(transId);

        if (trans == null) {
            throw new BadRequestException("Transaction with ID " + transId + " not found.");
        }
        if (!"P".equals(trans.getAuthorised())) {
            throw new BadRequestException("Transaction with ID " + transId + " is not in a state to be approved.");
        }
        trans.setAuthorised("N");
        transRepo.save(trans);
    }

    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void approveSubAgentComm(Long transId) throws BadRequestException {
        SystemTransactions trans = transRepo.findOne(transId);

        if (trans == null) {
            throw new BadRequestException("Transaction with ID " + transId + " not found.");
        }
        if (!"P".equals(trans.getAuthorised())) {
            throw new BadRequestException("Transaction with ID " + transId + " is not in a state to be approved.");
        }
        trans.setAuthorised("N");
        transRepo.save(trans);
    }


    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<SystemTransactions> findCreditTrans(DataTablesRequest request, Long clientCode, Long curCode,Long brnCode,String allTrans) throws IllegalAccessException {
        if(allTrans==null)
            allTrans="A";
        if(clientCode==null ){
            clientCode = -2000l;
        }
        QClientDef clientDef = QSystemTransactions.systemTransactions.client;
        QCurrencies currencies = QSystemTransactions.systemTransactions.currency;
        QOrgBranch branch = QSystemTransactions.systemTransactions.branch;
        BooleanExpression pred = null;
        if("Y".equalsIgnoreCase(allTrans)){
            pred = QSystemTransactions.systemTransactions.transdc.eq("C")
                    .and(QSystemTransactions.systemTransactions.transType.notIn("SAG","RFC"))
                    .and(QSystemTransactions.systemTransactions.balance.ne(BigDecimal.ZERO))
                    .and((clientCode == null) ? clientDef.isNotNull() : clientDef.tenId.eq(clientCode))
                    .and((curCode == null) ? currencies.isNotNull() : currencies.curCode.eq(curCode))
                    .and((brnCode == null) ? branch.isNotNull() : branch.obId.eq(brnCode));
        }
        else {
            pred = QSystemTransactions.systemTransactions.transdc.eq("C")
                    .and(QSystemTransactions.systemTransactions.transType.notIn("SAG","RFC"))
                    .and(QSystemTransactions.systemTransactions.isNotNull())
                    .and((clientCode == null) ? clientDef.isNotNull() : clientDef.tenId.eq(clientCode))
                    .and((curCode == null) ? currencies.isNotNull() : currencies.curCode.eq(curCode))
                    .and((brnCode == null) ? branch.isNotNull() : branch.obId.eq(brnCode));
        }
        Iterable<SystemTransactions> credits = transRepo.findAll(pred.and(request.searchPredicate(QSystemTransactions.systemTransactions)), request);
        List<SystemTransactions> newCredits =new ArrayList<>();
        for (SystemTransactions cr:credits){
            if(transRepo.count(QSystemTransactions.systemTransactions.refundTransaction.transno.eq(cr.getTransno())
                    .and(QSystemTransactions.systemTransactions.refundTransaction.authorised.ne("R")))==0){
                newCredits.add(cr);
            }
        }

        Page<SystemTransactions> page = new PageImpl<SystemTransactions>(newCredits);
        return new DataTablesResult(request, page);
    }

    @Override
    public DataTablesResult<ReceiptsDTO> findPolicyCreditTrans(Long policyId, DataTablesRequest request) throws IllegalAccessException {
        Long pol = (policyId!=null)?policyId:-2000;
        final List<ReceiptsDTO> receiptsDTOList = new ArrayList<>();
        List<Object[]> receiptList = receiptDetailsRepository.findPolRcpts(pol, request.getPageNumber(), request.getPageSize());
        long rowCount = 0L;
        if (!receiptList.isEmpty()) rowCount = ((BigInteger)receiptList.get(0)[3]).intValue();
        for (Object[] receipt : receiptList) {
            ReceiptsDTO receiptsDTO =new ReceiptsDTO();
            receiptsDTO.setReceiptNo((String) receipt[0]);
            receiptsDTO.setReceiptDate((Date) receipt[1]);
            receiptsDTO.setReceiptAmount((BigDecimal) receipt[2]);
            receiptsDTOList.add(receiptsDTO);
        }
        Page<ReceiptsDTO> page = new PageImpl<>(receiptsDTOList, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<SystemTransactions> findDebitTrans(DataTablesRequest request, Long clientCode, Long curCode, Long brnCode, String allTrans) throws IllegalAccessException {
        if(allTrans==null)
            allTrans="A";
        if(clientCode==null ){
            clientCode = -2000l;
        }
        QClientDef clientDef = QSystemTransactions.systemTransactions.client;
        QCurrencies currencies = QSystemTransactions.systemTransactions.currency;
        QOrgBranch branch = QSystemTransactions.systemTransactions.branch;
        BooleanExpression pred = QSystemTransactions.systemTransactions.transdc.eq("D")
                .and(QSystemTransactions.systemTransactions.transType.notIn("SAG","RFC"))
                .and((allTrans.equalsIgnoreCase("N"))?QSystemTransactions.systemTransactions.isNotNull():QSystemTransactions.systemTransactions.balance.ne(BigDecimal.ZERO))
                .and((clientCode==null)?clientDef.isNotNull():clientDef.tenId.eq(clientCode))
                .and((curCode==null)?currencies.isNotNull():currencies.curCode.eq(curCode))
                .and((brnCode==null)?branch.isNotNull():branch.obId.eq(brnCode));
        Page<SystemTransactions> page = transRepo.findAll(pred.and(request.searchPredicate(QSystemTransactions.systemTransactions)), request);
        return new DataTablesResult(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getAllocateAmount(Long transno) throws BadRequestException {
        if(transno==null) throw new BadRequestException("Trans Alloc Id Cannot be null");
        SystemTransactions transactions = transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(transno));
        return transactions.getBalance();
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void allocateCreditTrans(Long crTransNo, Long drTransNo, BigDecimal allocAmount) throws BadRequestException {
        if(crTransNo==null) throw new BadRequestException("Credit Transaction not found...");
        if(drTransNo==null) throw new BadRequestException("Debit Transaction not found");
        if(allocAmount==null || allocAmount.compareTo(BigDecimal.ZERO)==0) throw  new BadRequestException("Allocation Amount cannot be zero");
        SystemTransactions crTrans = transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(crTransNo));
        SystemTransactions drTrans = transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(drTransNo));
        if(crTrans.getBalance()==null || crTrans.getBalance().compareTo(BigDecimal.ZERO)==0){
            throw new BadRequestException("Credit Transaction Balance is  fully depleted. Select another Credit Transaction");
        }
        if(drTrans.getBalance()==null || drTrans.getBalance().compareTo(BigDecimal.ZERO)==0){
            throw new BadRequestException("Debit Transaction Selected is fully allocated....");
        }
        if(allocAmount.compareTo(drTrans.getBalance().abs())==1){
            throw new BadRequestException("Allocation Amount cannot be greater than Debit Balance.....");
        }

        if(allocAmount.compareTo(crTrans.getBalance().abs()) ==1){
            throw new BadRequestException("Allocation Amount cannot be greater than credit amount");
        }
        drTrans.setBalance(drTrans.getBalance().subtract(allocAmount.abs()));
        if(drTrans.getSettleAmt()==null)
            drTrans.setSettleAmt(allocAmount.abs());
        else
            drTrans.setSettleAmt(drTrans.getSettleAmt().add(allocAmount.abs()));

        if(crTrans.getSettleAmt()==null){
            crTrans.setSettleAmt(allocAmount.abs().negate());
        }
        else{
            crTrans.setSettleAmt(crTrans.getSettleAmt().add(allocAmount.abs().negate()));
        }
        crTrans.setBalance(crTrans.getBalance().subtract(allocAmount.abs().negate()));
        if ("L".equalsIgnoreCase(drTrans.getPolicy().getProduct().getProGroup().getPrgType())){
            allocationService.createLifeSettlements(drTrans, crTrans, allocAmount, userUtils.getCurrentUser());
        }else {
            allocationService.createSettlements(drTrans, crTrans, allocAmount, userUtils.getCurrentUser());
        }
        transRepo.save(drTrans);
        transRepo.save(crTrans);
        if(crTrans.getTransType()!=null && "RC".equalsIgnoreCase(crTrans.getTransType())) {
            if (drTrans.getBalance().compareTo(BigDecimal.ZERO) == 0)
                if (drTrans.getPolicy().getSubAgentComm() != null && drTrans.getPolicy().getSubAgentComm().compareTo(BigDecimal.ZERO) != 0) {
                    SystemTrans transaction = new SystemTrans();
                    transaction.setDoneDate(new Date());
                    transaction.setDoneBy(userUtils.getCurrentUser());
                    transaction.setTransLevel("U");
                    transaction.setTransCode("CR"); //A way to setup and look up for transaction transcode
                    transaction.setTransAuthorised("N");
                    systransRepo.save(transaction);
                    PolicyTrans policy = drTrans.getTransaction().getPolicy();
                    SystemTransactions trans = new SystemTransactions();
                    trans.setAmount(policy.getSubAgentComm().abs().multiply(sign("C")));
                    trans.setAuthDate(new Date());
                    trans.setAuthorised("Y");
                    trans.setBalance(policy.getSubAgentComm().abs().multiply(sign("C")));
                    trans.setBranch(policy.getBranch());
                    trans.setClientType("C");
                    trans.setControlAcc(policy.getSubAgent().getShtDesc());
                    trans.setClient(policy.getClient());
                    trans.setCurrRate(new BigDecimal(1));
                    trans.setCurrency(policy.getTransCurrency());
                    trans.setNarrations("Sub Agent Commission Trans");
                    trans.setNetAmount(policy.getSubAgentComm().abs().multiply(sign("C")));
                    trans.setOrigin("U");
                    trans.setPolicy(policy);
                    trans.setRefNo(policy.getRefNo());
                    trans.setTransDate(new Date());
                    trans.setTransdc("C");
                    trans.setTransType("SAG"); //Should not be hardcorded
                    try {
                        trans.setUserAuth(userUtils.getCurrentUser().getUsername());
                    } catch (NullPointerException | IllegalStateException e) {
                        trans.setUserAuth("system");
                    }
                    trans.setWhtx(BigDecimal.ZERO);
                    trans.setTransaction(transaction);
                    trans.setPostedDate(new Date());
                    trans.setPostedUser(userUtils.getCurrentUser());
                    trans.setAgent(policy.getSubAgent());
                    transRepo.save(trans);
                }
        }
        else if(crTrans.getTransType()!=null && !"RC".equalsIgnoreCase(crTrans.getTransType())) {
            if (crTrans.getBalance().compareTo(BigDecimal.ZERO) == 0){
                if (crTrans.getPolicy()!=null && crTrans.getPolicy().getSubAgentComm() != null && crTrans.getPolicy().getSubAgentComm().compareTo(BigDecimal.ZERO) != 0) {
                    SystemTrans transaction = new SystemTrans();
                    transaction.setDoneDate(new Date());
                    transaction.setDoneBy(userUtils.getCurrentUser());
                    transaction.setTransLevel("U");
                    transaction.setTransCode("CR"); //A way to setup and look up for transaction transcode
                    transaction.setTransAuthorised("N");
                    systransRepo.save(transaction);
                    PolicyTrans policy = crTrans.getPolicy();
                    SystemTransactions trans = new SystemTransactions();
                    trans.setAmount(policy.getSubAgentComm().abs().multiply(sign("D")));
                    trans.setAuthDate(new Date());
                    trans.setAuthorised("Y");
                    trans.setBalance(policy.getSubAgentComm().abs().multiply(sign("D")));
                    trans.setBranch(policy.getBranch());
                    trans.setClientType("C");
                    trans.setControlAcc(policy.getSubAgent().getShtDesc());
                    trans.setClient(policy.getClient());
                    trans.setCurrRate(new BigDecimal(1));
                    trans.setCurrency(policy.getTransCurrency());
                    trans.setNarrations("Sub Agent Commission Trans");
                    trans.setNetAmount(policy.getSubAgentComm().abs().multiply(sign("D")));
                    trans.setOrigin("U");
                    trans.setPolicy(policy);
                    trans.setRefNo(policy.getRefNo());
                    trans.setTransDate(new Date());
                    trans.setTransdc("D");
                    trans.setTransType("SAG"); //Should not be hardcorded
                    trans.setUserAuth(userUtils.getCurrentUser().getUsername());
                    trans.setWhtx(BigDecimal.ZERO);
                    trans.setTransaction(transaction);
                    trans.setPostedDate(new Date());
                    trans.setPostedUser(userUtils.getCurrentUser());
                    trans.setAgent(policy.getSubAgent());
                    transRepo.save(trans);
                }
            }
        }
    }

    private BigDecimal sign(String type){
        return ("C".equalsIgnoreCase(type)?BigDecimal.ONE.multiply(BigDecimal.valueOf(-1)):BigDecimal.ONE.multiply(BigDecimal.valueOf(1)));
    }



    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void allocateCreditTransRfnd(Long crTransNo, Long drTransNo, BigDecimal allocAmount) throws BadRequestException {
        if(crTransNo==null) throw new BadRequestException("Credit Transaction not found...");
        if(drTransNo==null) throw new BadRequestException("Debit Transaction not found");
        if(allocAmount==null || allocAmount.compareTo(BigDecimal.ZERO)==0) throw  new BadRequestException("Allocation Amount cannot be zero");
        SystemTransactions crTrans = transRepo.findOne(crTransNo);
        SystemTransactions drTrans = transRepo.findOne(drTransNo);
        if(crTrans.getBalance()==null || crTrans.getBalance().compareTo(BigDecimal.ZERO)==0){
            throw new BadRequestException("Credit Transaction Balance is  fully depleted. Select another Credit Transaction");
        }
        if(drTrans.getBalance()==null || drTrans.getBalance().compareTo(BigDecimal.ZERO)==0){
            throw new BadRequestException("Debit Transaction Selected is fully allocated....");
        }
        if(allocAmount.compareTo(drTrans.getBalance().abs())==1){
            throw new BadRequestException("Allocation Amount cannot be greater than Debit Balance.....");
        }

        if(allocAmount.compareTo(crTrans.getBalance().abs()) ==1){
            throw new BadRequestException("Allocation Amount cannot be greater than credit amount");
        }
        drTrans.setBalance(drTrans.getBalance().subtract(allocAmount.abs()));
        if(drTrans.getSettleAmt()==null)
            drTrans.setSettleAmt(allocAmount.abs());
        else
            drTrans.setSettleAmt(drTrans.getSettleAmt().add(allocAmount.abs()));

        if(crTrans.getSettleAmt()==null){
            crTrans.setSettleAmt(allocAmount.abs().negate());
        }
        else{
            crTrans.setSettleAmt(crTrans.getSettleAmt().add(allocAmount.abs().negate()));
        }
        crTrans.setBalance(crTrans.getBalance().subtract(allocAmount.abs().negate()));
        allocationService.createClientSettlements(drTrans,crTrans,allocAmount,userUtils.getCurrentUser());
        transRepo.save(drTrans);
        transRepo.save(crTrans);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void defineCoa(CoaMainAccounts mainAccount) throws BadRequestException {
        if(!mainAccount.getAccountsOrder().matches("\\d+")){
            throw new BadRequestException("Account Order must match a number...");
        }
        if(mainAccount.getCoId()==null)
            if(mainAccountsRepo.count(QCoaMainAccounts.coaMainAccounts.code.eq(StringUtils.trim(mainAccount.getCode()))) > 0){
                throw new BadRequestException("Account Code already exist...");
            }
        mainAccountsRepo.save(mainAccount);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteCoa(Long coId) {
        mainAccountsRepo.delete(coId);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void defineSubCoa(CoaSubAccounts subAccount) throws BadRequestException {
        if(subAccount.getMainAccounts().getCoId()==null){
            throw new BadRequestException("Main Account cannot be null");
        }
        if(!subAccount.getAccountsOrder().matches("\\d+")){
            throw new BadRequestException("Account Order must match a number...");
        }
        if(subAccount.getCoId()==null)
            if(subAccountsRepo.count(QCoaSubAccounts.coaSubAccounts.code.eq(StringUtils.trim(subAccount.getCode()))) > 0){
                throw new BadRequestException("Account Code already exist...");
            }
        subAccountsRepo.save(subAccount);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteSubCoa(Long subCoaId) {
        subAccountsRepo.delete(subCoaId);
    }



    @Override
    public DataTablesResult<Banks> findBanks(DataTablesRequest request) throws IllegalAccessException {
        Page<Banks> page = banksRepo.findAll(request.searchPredicate(QBanks.banks), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<BankBranches> findBankBranches(DataTablesRequest request, Long bnId) throws IllegalAccessException {
        if(bnId==null) bnId=-2000l;
        BooleanExpression pred = QBankBranches.bankBranches.bank.bnId.eq(bnId);
        Page<BankBranches> page = bankBranchRepo.findAll(pred.and(request.searchPredicate(QBankBranches.bankBranches)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = false)
    public void defineBank(Banks bank) throws BadRequestException {
        banksRepo.save(bank);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteBank(Long bnId) {
        banksRepo.delete(bnId);
    }

    @Override
    @Transactional(readOnly = false)
    public void defineBankBranch(BankBranches bankBranch) throws BadRequestException {
        bankBranchRepo.save(bankBranch);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteBankBranch(Long bbId) {
        bankBranchRepo.delete(bbId);
    }

    @Override
    public DataTablesResult<CollectionAccounts> findCollectionAccts(DataTablesRequest request, Long pmId) throws IllegalAccessException {
        if(pmId==null) pmId=-2000l;
        BooleanExpression pred = QCollectionAccounts.collectionAccounts.paymentModes.pmId.eq(pmId);
        Page<CollectionAccounts> page = acctsRepo.findAll(pred.and(request.searchPredicate(QCollectionAccounts.collectionAccounts)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public void defineCollectAccount(CollectionAccounts collectionAccounts) throws BadRequestException {
        acctsRepo.save(collectionAccounts);
    }

    @Override
    public void deleteCollectAcct(Long caId) {
        acctsRepo.delete(caId);
    }

    @Override
    public Page<PaymentModes> findPaymentModes(String paramString, Pageable paramPageable) {
        Predicate pred=null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred = QPaymentModes.paymentModes.isNotNull();
        } else {
            pred = QPaymentModes.paymentModes.pmDesc.containsIgnoreCase(paramString).or(QPaymentModes.paymentModes.pmShtDesc.containsIgnoreCase(paramString));
        }
        return paymentModeRepo.findAll(pred, paramPageable);
    }

    @Override
    public Page<BankBranchDTO> findBankBranches(String paramString, Pageable paramPageable) {
        final String search = (paramString!=null)?"%"+paramString+"%":"%%";
        List<Object[]> branches = bankBranchRepo.findBnkBranches(search.toLowerCase(),paramPageable.getPageNumber(), paramPageable.getPageSize());
        final List<BankBranchDTO> branchDTOList = new ArrayList<>();
        long rowCount = 0l;
        if(!branches.isEmpty()) rowCount = ((BigInteger)branches.get(0)[2]).intValue();
        for(Object[] branch:branches){
            BankBranchDTO branchDTO = new BankBranchDTO();
            branchDTO.setBbId(((BigInteger)branch[0]).longValue());
            branchDTO.setBranchName((String)branch[1]);
            branchDTOList.add(branchDTO);
        }
        return new PageImpl<>(branchDTOList,paramPageable,rowCount);
    }

    @Override
    public Page<AccountingPeriodDTO> findAccountingPeriods(String paramString, Pageable paramPageable) {
        final String search = (paramString!=null)?"%"+paramString+"%":"%%";
        final Long headOfficeId = branchRepository.findHeadOffice();
        List<Object[]> periods = balancesRepo.searchAccountingPeriods(search.toLowerCase(),headOfficeId,paramPageable.getPageNumber(), paramPageable.getPageSize());
        final List<AccountingPeriodDTO> acountingPeriods = new ArrayList<>();
        long rowCount = 0l;
        if(!periods.isEmpty()) rowCount = ((BigInteger)periods.get(0)[1]).intValue();
        for(Object[] period:periods){
            final AccountingPeriodDTO accountingPeriod = new AccountingPeriodDTO();
            accountingPeriod.setPeriodName((String) period[0]);
            acountingPeriods.add(accountingPeriod);
        }
        return new PageImpl<>(acountingPeriods,paramPageable,rowCount);
    }

    @Override
    public DataTablesResult<ReceiptSettlementDetails> findCreditSettlements(DataTablesRequest request, Long crCode) throws IllegalAccessException {
        if(crCode==null) crCode=-2000l;
        BooleanExpression pred = QReceiptSettlementDetails.receiptSettlementDetails.credit.transno.eq(crCode);
        Page<ReceiptSettlementDetails> page = settlementRepo.findAll(pred.and(request.searchPredicate(QReceiptSettlementDetails.receiptSettlementDetails)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PolicyTrans> findClientPolicies(String paramString, Pageable paramPageable, Long clientId) {
        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred = QPolicyTrans.policyTrans.client.tenId.eq(clientId).and(QPolicyTrans.policyTrans.isNotNull())
                    .and(QPolicyTrans.policyTrans.authStatus.equalsIgnoreCase("A"));
        } else {
            pred =QPolicyTrans.policyTrans.client.tenId.eq(clientId).and(QPolicyTrans.policyTrans.polNo.containsIgnoreCase(paramString)
                            .or(QPolicyTrans.policyTrans.clientPolNo.containsIgnoreCase(paramString)))
                    .and(QPolicyTrans.policyTrans.authStatus.equalsIgnoreCase("A"));
        }
        return policyRepo.findAll(pred, paramPageable);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<SystemTransactions> findPolicyTrans(String paramString, Pageable paramPageable, Long policyId) {
        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred =QSystemTransactions.systemTransactions.policy.policyId.eq(policyId)
                    .and(QSystemTransactions.systemTransactions.isNotNull()
                            .and(QSystemTransactions.systemTransactions.transType.notIn("RFC","SAG"))
                            .and(QSystemTransactions.systemTransactions.clientType.equalsIgnoreCase("C"))
                            .and(QSystemTransactions.systemTransactions.transdc.equalsIgnoreCase("C"))
                            .and(QSystemTransactions.systemTransactions.balance.lt(BigDecimal.ZERO)));
        } else {
            pred =QSystemTransactions.systemTransactions.policy.policyId.eq(policyId)
                    .and(QSystemTransactions.systemTransactions.refNo.containsIgnoreCase(paramString)
                            .or(QSystemTransactions.systemTransactions.otherRef.containsIgnoreCase(paramString))
                            .and(QSystemTransactions.systemTransactions.clientType.equalsIgnoreCase("C"))
                            .and(QSystemTransactions.systemTransactions.transType.ne("RFC"))
                            .and(QSystemTransactions.systemTransactions.transdc.equalsIgnoreCase("C"))
                            .and(QSystemTransactions.systemTransactions.balance.lt(BigDecimal.ZERO)));
        }
        return transRepo.findAll(pred, paramPageable);
    }

    @PreAuthorize("hasAnyAuthority('CREATE_REFUND')")
    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class }, propagation = Propagation.REQUIRED)
    public void createRefund(Refunds refund , String refundStatus) throws BadRequestException {
        Long creditTransNo =null;
//        if (refund.getTransactions()==null && refund.getTransNoToRefund()==null){
//            throw new BadRequestException("Select credit note/receipt");
//        }else {
            if (refund.getRefId()==null) {
//                if (refund.getTransactions()!=null){
//                    creditTransNo = refund.getTransactions().getRefundTransaction().getTransno();
//                }
                if (refund.getTransNoToRefund()!=null){
                    creditTransNo = refund.getTransNoToRefund();
                }
                System.out.println("Trano = "+creditTransNo);
                if (refundRepo.count(QRefunds.refunds.transactions.refundTransaction.transno.eq(creditTransNo)
                        .and(QRefunds.refunds.refundStatus.eq("D"))) > 0) {
                    throw new BadRequestException("There is a pending refund on the same credit note/Receipt");
                }
            }
//        }
        if (refund.getAmount()==null){
            throw new BadRequestException("Amount is mandatory");
        }
        if (refund.getClient()==null){
            throw new BadRequestException("Client is mandatory");
        }
        if (refund.getNarrations()==null){
            throw new BadRequestException("Narration is mandatory");
        }
        if (refund.getPayee()==null){
            throw new BadRequestException("Payee is mandatory");
        }

        if (refund.getPolicy()==null){
            throw new BadRequestException("Select policy");
        }

        if (refund.getRefId()==null){
            refund.setRefundCaptureDate(new Date());
            refund.setRefundStatus(refundStatus);
            refund.setCreatedUser(userUtils.getCurrentUser());
            if (refund.getTransactions()==null && refund.getTransNoToRefund()!=null){

                PolicyTrans policy = refund.getPolicy();
       //         BigDecimal prems = (policy.getPremium()==null)?BigDecimal.ZERO:policy.getPremium();
                if(mappingRepo.count(QTransactionMapping.transactionMapping.transType.eq("RF")) ==0)
                    throw new BadRequestException("Error getting Transaction Mapping Setups For Refund..Contact System Administrator");
        //        TransactionMapping mapping = mappingRepo.findOne(QTransactionMapping.transactionMapping.transType.eq("RF"));
                Predicate refSeqPredicate = QSystemSequence.systemSequence.transType.eq("RFD");
                if (sequenceRepo.count(refSeqPredicate) == 0)
                    throw new BadRequestException("Sequence for refund has not been defined");
                SystemSequence refundSequence = sequenceRepo.findOne(refSeqPredicate);
                Long refSeqNumber = refundSequence.getNextNumber();
//                final String refundCode = ((prems.compareTo(BigDecimal.ZERO)>=0)?mapping.getDebitCode():mapping.getCreditCode());
//                final String refundRefNo =((prems.compareTo(BigDecimal.ZERO)>=0)?mapping.getDebitCode():mapping.getCreditCode()) + String.format("%05d", refSeqNumber);

                refundSequence.setLastNumber(refSeqNumber);
                refundSequence.setNextNumber(refSeqNumber + 1);
                sequenceRepo.save(refundSequence);
//                SystemTrans transaction = new SystemTrans();
//                transaction.setDoneDate(new Date());
//                transaction.setDoneBy(userUtils.getCurrentUser());
//                transaction.setPolicy(policy);
//                transaction.setTransLevel("U");
//                transaction.setTransCode("RFD");
//                transaction.setTransAuthorised("N");
//                SystemTrans savedTrans =systransRepo.save(transaction);
//                BigDecimal basicPrem = (policy.getPremium()==null)?BigDecimal.ZERO:policy.getPremium();
//                BigDecimal extras = (policy.getExtras()==null)?BigDecimal.ZERO:policy.getExtras();
//                BigDecimal phcf = (policy.getPhcf()==null)?BigDecimal.ZERO:policy.getPhcf();
//                BigDecimal tl = (policy.getTrainingLevy()==null)?BigDecimal.ZERO:policy.getTrainingLevy();
//                BigDecimal sd = (policy.getStampDuty()==null)?BigDecimal.ZERO:policy.getStampDuty();
//                BigDecimal amountWithTaxes =basicPrem.add(extras).add(phcf).add(tl).add(sd);
//                String type = (amountWithTaxes.compareTo(BigDecimal.ZERO)==1)?"D":"C";
//                BigDecimal refundAmount =refund.getAmount();
                // client
//                SystemTransactions trans1 = new SystemTransactions();
//                trans1.setAmount(refundAmount.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(),BigDecimal.ROUND_HALF_EVEN));
//                trans1.setAuthDate(new Date());
//                trans1.setAuthorised("N");
//                trans1.setBalance(refundAmount.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(),BigDecimal.ROUND_HALF_EVEN));
//                trans1.setBranch(policy.getBranch());
//                trans1.setClientType("C");
//                trans1.setControlAcc(policy.getClient().getTenantNumber());
//                trans1.setClient(policy.getClient());
//                trans1.setCurrRate(new BigDecimal(1));
//                trans1.setCurrency(policy.getTransCurrency());
//                trans1.setNarrations("Posting client underwriting refund");
//                trans1.setNetAmount(refundAmount.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(),BigDecimal.ROUND_HALF_EVEN));
//                trans1.setOrigin("U");
//                trans1.setPolicy(policy);
//                trans1.setRefNo(refundRefNo);
//                trans1.setTransDate(new Date());
//                trans1.setTransdc((amountWithTaxes.compareTo(BigDecimal.ZERO)==1)?"C":"D");
//                trans1.setTransType(refundCode); //Should not be hardcorded
//                //trans.setUserAuth(userUtils.getCurrentUser().getUsername());
//                trans1.setWhtx(BigDecimal.ZERO);
                //trans.setExtras(extras.abs().multiply(sign(type)).setScale(policy.getTransCurrency().getRoundOff(),BigDecimal.ROUND_HALF_EVEN));
                // trans.setPostedDate(new Date());
                //trans.setPostedUser(userUtils.getCurrentUser());
//                trans1.setTransaction(savedTrans);
//                trans1.setRefundTransaction(transRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(refund.getTransNoToRefund())));
//                SystemTransactions savedRefundTrans =  transRepo.save(trans1);
               // refund.setTransactions(savedRefundTrans);
            }
        }else {
            Refunds existingRefund = refundRepo.findOne(refund.getRefId());
            refund.setRefundCaptureDate(existingRefund.getRefundCaptureDate());
            refund.setRefundStatus(existingRefund.getRefundStatus());
            refund.setCreatedUser(existingRefund.getCreatedUser());
           // refund.setTransactions(existingRefund.getTransactions());
        }

        refundRepo.save(refund);
    }


    @PreAuthorize("hasAnyAuthority('MAKE_REFUND_READY')")
    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void makeRefundReady(Long refundId) throws BadRequestException {
        Refunds refund = refundRepo.findOne(QRefunds.refunds.refId.eq(refundId));
        if (refund.getAmount()==null){
            throw new BadRequestException("Amount is mandatory");
        }
        if (refund.getClient()==null){
            throw new BadRequestException("Client is mandatory");
        }
        if (refund.getNarrations()==null){
            throw new BadRequestException("Narration is mandatory");
        }
        if (refund.getPayee()==null){
            throw new BadRequestException("Payee is mandatory");
        }
        if (refund.getPaymentMode()==null){
            throw new BadRequestException("Pay mode is mandatory");
        }
        if (refund.getPolicy()==null){
            throw new BadRequestException("Select policy");
        }
//        if (refund.getTransactions()==null){
//            throw new BadRequestException("Select credit note/receipt");
//        }

        if (!refund.getRefundStatus().equalsIgnoreCase("D")){
            throw new BadRequestException("Can only make ready a draft transaction");
        }
        refund.setRefundStatus("R");
        refund.setMakeReadyDate(new Date());
        refund.setMadeReadyBy(userUtils.getCurrentUser());
        refundRepo.save(refund);
    }


    @PreAuthorize("hasAnyAuthority('AUTHORIZE_REFUND')")
    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void    rejectRefund(Long refundId, String remarks) throws BadRequestException {
        System.out.println("Refund Id="+refundId);
        Refunds refund = refundRepo.findOne(QRefunds.refunds.refId.eq(refundId));
        if(refund==null) throw new BadRequestException("No refund transaction to reject");
        if (remarks==null){
            throw new BadRequestException("Please provide rejection remarks");

        }
        SystemTransactions refundTran = transRepo.findOne(refund.getTransactions().getTransno()) ;
        refundTran.setAuthorised("R");
        refundTran.setUserAuth(userUtils.getCurrentUser().getUsername());
        refundTran.setPostedDate(new Date());
        refundTran.setPostedUser(userUtils.getCurrentUser());

        SystemTrans transaction = refundTran.getTransaction();
        transaction.setAuthBy(userUtils.getCurrentUser());
        transaction.setAuthDate(new Date());
        transaction.setTransAuthorised("R");
        systransRepo.save(transaction);

        transRepo.save(refundTran);
        refund.setRefundStatus("J");
        refund.setRejectionRemarks(remarks);
        refund.setRejectedDate(new Date());
        refund.setRejectedBy(userUtils.getCurrentUser());
        refundRepo.save(refund);
    }


    @PreAuthorize("hasAnyAuthority('AUTHORIZE_REFUND')")
    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void    authorizeRefund(Long refundId, String accoutno) throws BadRequestException {
        Refunds refund = refundRepo.findOne(QRefunds.refunds.refId.eq(refundId));
        if(refund==null) throw new BadRequestException("No refund transaction to Authorize");
        SystemTransactions refundTran = transRepo.findOne(refund.getTransactions().getTransno()) ;
        refundTran.setAuthorised("Y");
        refundTran.setUserAuth(userUtils.getCurrentUser().getUsername());
        refundTran.setPostedDate(new Date());
        refundTran.setPostedUser(userUtils.getCurrentUser());
        transRepo.save(refundTran);

        SystemTrans transaction = refundTran.getTransaction();
        transaction.setAuthBy(userUtils.getCurrentUser());
        transaction.setAuthDate(new Date());
        transaction.setTransAuthorised("Y");
        systransRepo.save(transaction);
        SystemTransactions creditTran = transRepo.findOne(refund.getTransactions().getRefundTransaction().getTransno());
        // creditTran.setBalance();
        allocateCreditTransRfnd(creditTran.getTransno(), refundTran.getTransno(), refundTran.getAmount());

        refund.setRefundStatus("A");
        refund.setRefundAuthDate(new Date());
        refund.setAuthBy(userUtils.getCurrentUser());
        refundRepo.save(refund);


    }



    @Override
    public DataTablesResult<Refunds> findRefundTrans(DataTablesRequest request, Date from, Date to,String refNo,String status,String policyNo,Long clientId) throws IllegalAccessException {
        QRefunds refund = QRefunds.refunds;
        to = dateUtilities.removeTime(DateUtils.addDays(to, 1));
        System.out.println("to="+to);
        BooleanExpression pred =
                QRefunds.refunds.refundCaptureDate.between(dateUtilities.removeTime(from),dateUtilities.removeTime(to))
                        .and((status==null || StringUtils.isEmpty(status))?refund.isNotNull():refund.refundStatus.eq(status))
                        .and((policyNo==null|| StringUtils.isEmpty(policyNo))?refund.isNotNull():refund.policy.polNo.eq(policyNo))
                        .and((refNo==null || StringUtils.isEmpty(refNo))?refund.isNotNull():refund.transactions.refNo.containsIgnoreCase(refNo))
                        .and((clientId==null)?refund.isNotNull():refund.client.tenId.eq(clientId));
        Page<Refunds> page = refundRepo.findAll(pred.and(request.searchPredicate(QRefunds.refunds)), request);
        return new DataTablesResult(request, page);
    }

    @Override
    public Page<AccountDef> findSubAgentAccounts(String paramString, Pageable paramPageable) {
        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred = QAccountDef.accountDef.isNotNull().and(QAccountDef.accountDef.accountType.accountType.eq(AccountTypeEnum.IA));
        } else {
            pred =  QAccountDef.accountDef.name.containsIgnoreCase(paramString).and(QAccountDef.accountDef.accountType.accountType.eq(AccountTypeEnum.IA));
        }
        return accountRepo.findAll(pred, paramPageable);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteRefund(Long refId) throws BadRequestException {
        Refunds refund = refundRepo.findOne(refId);
        if ("RFD".equalsIgnoreCase(refund.getTransactions().getTransaction().getTransCode()) && "N".equalsIgnoreCase(refund.getTransactions().getTransaction().getTransAuthorised())){
            systransRepo.delete(refund.getTransactions().getTransaction().getTransNo());
            transRepo.delete(refund.getTransactions().getTransno());
            refundRepo.delete(refId);
        }else {
            throw new BadRequestException("Cannot delete this refund, Reject it instead");
        }


    }
    @Override
    @Transactional
    public Map<String, Object> processRefunds(List<Long> transactionNumbers, String refundComments) throws IllegalAccessException {
        Map<String, Object> response = new HashMap<>();
        List<Long> processedTransactions = new ArrayList<>();
        List<Long> processedRefundPolicies = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        User currentUser = userUtils.getCurrentUser();
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "No authenticated user found");
            return response;
        }

        for (Long transNo : transactionNumbers) {
            try {

                SystemTransactions originalTrans = systemTransactionsRepo.findByTransno(transNo);
                if (originalTrans == null) {
                    errors.add("Transaction " + transNo + " not found");
                    continue;
                }

                System.out.println("Original trans "+originalTrans);

                long uploadedDocsCount = riskDocsRepo.count(
                        QRiskDocs.riskDocs.risk.policy.policyId.eq(originalTrans.getPolicy().getPolicyId())
                                .and(QRiskDocs.riskDocs.documentSource.eq("REFUND"))
                );

                if (uploadedDocsCount == 0) {
                    errors.add("Transaction " + transNo + " cannot process refund without attaching required documents");
                    continue;
                }

                // Validate transaction is eligible for refund
                if (!isEligibleForRefund(originalTrans)) {
                    errors.add("Transaction " + transNo + " is not eligible for refund");
                    continue;
                }

                System.out.println("Creating reverse trans.....");
                // Create reverse transaction
                SystemTransactions reverseTrans = createReverseTransaction(originalTrans, currentUser);
                System.out.println("Create Reverse trans "+reverseTrans);

                SystemTransactions savedRefundTrans = systemTransactionsRepo.save(reverseTrans);

                if (refundComments != null && !refundComments.trim().isEmpty()) {
                    PolicyTrans refundPolicy = savedRefundTrans.getPolicy();
                    refundPolicy.setRefundComments(refundComments);
                    policyRepo.save(refundPolicy);
                }
                createRefundRisks(originalTrans.getPolicy(), savedRefundTrans.getPolicy());

                processedTransactions.add(transNo);
                processedRefundPolicies.add(reverseTrans.getPolicy().getPolicyId());
            } catch (Exception e) {
                errors.add("Transaction " + transNo + ": " + e.getMessage());
            }
        }

        response.put("processedTransactions", processedTransactions);
        response.put("errors", errors);

        if (processedTransactions.isEmpty()) {
            response.put("success", false);
            response.put("message", "No refunds were processed successfully");
        } else {
            response.put("success", true);
            String message = "Successfully processed " + processedTransactions.size() + " refund(s)";
            if (!errors.isEmpty()) {
                message += " with " + errors.size() + " error(s)";
            }
            response.put("message", message);
            response.put("refundPolId", processedRefundPolicies);
        }

        return response;
    }

    private void createRefundRisks(PolicyTrans originalPolicy, PolicyTrans refundPolicy) {
        try {
            List<Object[]> activeRisks = activeRisksRepo.getPolicyActRisks(originalPolicy.getPolicyId());
            List<PolicyActiveRisks> newActiveRisks = new ArrayList<>();

            for (Object[] activeRisk : activeRisks) {
                PolicyActiveRisks newActiveRisk = new PolicyActiveRisks();
                newActiveRisk.setPolicy(refundPolicy);
                newActiveRisk.setRisk(riskRepo.findOne(((BigInteger)activeRisk[4]).longValue()));
                newActiveRisk.setRiskIdentifier(((BigInteger)activeRisk[0]).longValue());
                newActiveRisk.setStatus("NR");
                newActiveRisks.add(newActiveRisk);
            }

            if (!newActiveRisks.isEmpty()) {
                Iterable<PolicyActiveRisks> savedActiveRisks = activeRisksRepo.save(newActiveRisks);

                for (PolicyActiveRisks savedActiveRisk : savedActiveRisks) {
                    try {
                        RiskTrans originalRisk = savedActiveRisk.getRisk();

                        // Create new risk for refund policy
                        RiskTrans newRisk = new RiskTrans();
                        newRisk.setBinder(originalRisk.getBinder());
                        newRisk.setBinderDetails(originalRisk.getBinderDetails());
                        newRisk.setCommRate(originalRisk.getCommRate());
                        newRisk.setCovertype(originalRisk.getCovertype());
                        newRisk.setInsured(originalRisk.getInsured());
                        newRisk.setPolicy(refundPolicy);
                        newRisk.setRiskDesc(originalRisk.getRiskDesc());
                        newRisk.setRiskIdentifier(savedActiveRisk.getRiskIdentifier());
                        newRisk.setRiskShtDesc(originalRisk.getRiskShtDesc());
                        newRisk.setSubclass(originalRisk.getSubclass());
                        newRisk.setWefDate(refundPolicy.getWefDate());
                        newRisk.setWetDate(refundPolicy.getWetDate());
                        newRisk.setTransType("RF");
                        newRisk.setPending(true);
                        newRisk.setUwYear(originalRisk.getUwYear());

                        if (originalRisk.getPolicyBinders() != null) {
                            PolicyBinders polbinder = policyBindersRepo.findOne(
                                    QPolicyBinders.policyBinders.binder.binId.eq(originalRisk.getBinder().getBinId())
                                            .and(QPolicyBinders.policyBinders.policyTrans.policyId.eq(refundPolicy.getPolicyId()))
                            );
                            newRisk.setPolicyBinders(polbinder);
                        }

                        // Save the new risk
                        RiskTrans savedRisk = riskRepo.save(newRisk);

                        // Copy documents from original risk to new risk
                        copyDocuments(originalRisk.getRiskId(), savedRisk.getRiskId());

                        // Update the active risk to point to new risk
                        savedActiveRisk.setPrevRisk(originalRisk);
                        savedActiveRisk.setRisk(savedRisk);
                        savedActiveRisk.setStatus("NR");
                        activeRisksRepo.save(savedActiveRisk);

                    } catch (Exception e) {
                        System.err.println("Error creating risk transaction for refund: " + e.getMessage());
                    }
                }

                System.out.println("Created " + newActiveRisks.size() + " risk records for refund policy " + refundPolicy.getPolicyId());
            }

        } catch (Exception e) {
            System.err.println("Error creating refund risks for policy " + refundPolicy.getPolicyId() + ": " + e.getMessage());
        }
    }

    private void copyDocuments(Long originalRiskId, Long newRiskId) {
        try {
            // Copy RiskDocs
            List<RiskDocs> originalDocs = (List<RiskDocs>) riskDocsRepo.findAll(
                    QRiskDocs.riskDocs.risk.riskId.eq(originalRiskId)
            );

            RiskTrans newRisk = riskRepo.findOne(newRiskId);
            List<RiskDocs> newDocs = new ArrayList<>();

            for (RiskDocs originalDoc : originalDocs) {
                RiskDocs newDoc = new RiskDocs();
                newDoc.setRisk(newRisk);
                newDoc.setReqdDocs(originalDoc.getReqdDocs());
                newDoc.setUploadedFileName(originalDoc.getUploadedFileName());
                newDoc.setContentType(originalDoc.getContentType());
                newDoc.setInitiator(originalDoc.getInitiator());
                newDoc.setCreationDate(originalDoc.getCreationDate());
                newDoc.setVerifiedBy(originalDoc.getVerifiedBy());
                newDoc.setVerifiedDate(originalDoc.getVerifiedDate());
                newDoc.setComments(originalDoc.getComments());
                newDoc.setCreationDate(new Date());
                newDoc.setDocumentSource(originalDoc.getDocumentSource());
                newDoc.setInitiator(userUtils.getCurrentUser().getUsername());

                if (originalDoc.getMember() != null) {
                    newDoc.setMember(originalDoc.getMember());
                }

                newDocs.add(newDoc);
            }

            if (!newDocs.isEmpty()) {
                riskDocsRepo.save(newDocs);
            }

            // Copy SybrinCases
            List<SybrinCases> originalCases = (List<SybrinCases>) sybrinCasesRepo.findAll(
                    QSybrinCases.sybrinCases.caseRiskId.eq(originalRiskId)
            );

            List<SybrinCases> newCases = new ArrayList<>();
            for (SybrinCases originalCase : originalCases) {
                SybrinCases newCase = new SybrinCases();
                newCase.setCaseRiskId(newRiskId);
                newCase.setCaseRequiredDocId(originalCase.getCaseRequiredDocId());
                newCase.setCaseFileName(originalCase.getCaseFileName());
                newCase.setCaseNumber(originalCase.getCaseNumber());

                newCases.add(newCase);
            }

            if (!newCases.isEmpty()) {
                sybrinCasesRepo.save(newCases);
            }

            System.out.println("Copied " + newDocs.size() + " documents and " + newCases.size() + " cases from risk " + originalRiskId + " to risk " + newRiskId);

        } catch (Exception e) {
            System.err.println("Error copying documents from risk " + originalRiskId + " to risk " + newRiskId + ": " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void createRefundRequiredDocs(RequiredDocBean requiredDocBean) {
        // Copy the exact logic from createRiskRequiredDocs but add document_source
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
        List<RiskDocs> riskDocs = requiredDocBean.getRequiredDocs().stream().map(reqId -> {
            RiskDocs riskDoc = new RiskDocs();
            riskDoc.setReqdDocs(subclassReqDocRepo.findOne(reqId));
            riskDoc.setRisk(riskRepo.QueryRiskTrans(requiredDocBean.getSubCode()));
            riskDoc.setDocumentSource("REFUND");

            System.out.println("Saving RiskDoc with documentSource: " + riskDoc.getDocumentSource());

            return riskDoc;
        }).collect(Collectors.toList());
        riskDocsRepo.save(riskDocs);

    }

    private boolean isEligibleForRefund(SystemTransactions transaction) {
        // Check if transaction has negative balance (credit balance to refund)
        if (transaction.getBalance() == null || transaction.getBalance().compareTo(BigDecimal.ZERO) >= 0) {
            return false;
        }

        // Check if transaction is authorized
        if (!"Y".equals(transaction.getAuthorised())) {
            return false;
        }

        // Check transaction type eligibility - exclude these types
        List<String> ineligibleTypes = Arrays.asList("RC", "SAG", "RF");
        if (ineligibleTypes.contains(transaction.getTransType())) {
            return false;
        }

        // Check if client type is 'C' (client transactions)
        //if (!"C".equals(transaction.getClientType())) {
        if (!"C".equals(transaction.getClientType()) && !"A".equals(transaction.getClientType())) { //A FOR LIFE
            return false;
        }

        // Check if transaction DC is 'C' (credit)
        if (!"C".equals(transaction.getTransdc())) {
            return false;
        }

        return true;
    }

    private SystemTransactions createReverseTransaction(SystemTransactions originalTrans, User currentUser) throws BadRequestException {
        SystemTransactions reverseTrans = new SystemTransactions();
        System.out.println("original policy..."+originalTrans.getPolicy());
        if (originalTrans.getPolicy() != null) {
            System.out.println("Created reverse policy...");
            PolicyTrans originalPolicy = originalTrans.getPolicy();
            PolicyTrans refundPolicy = new PolicyTrans();


            refundPolicy.setPolNo(originalPolicy.getPolNo());
            long refundCount = policyRepo.count(QPolicyTrans.policyTrans.polNo.eq(originalPolicy.getPolNo())) + 1;
            if (originalPolicy.getPolRevNo() != null) {
                final String input = originalPolicy.getPolRevNo();
                long count = refundCount;
                if (input.matches(".*\\/\\d+$")) {
                   String extracted = input.replaceAll("^.*/", "");
                   count = Long.parseLong(extracted)+1;
                }
                refundPolicy.setPolRevNo(originalPolicy.getPolRevNo().substring(0, originalPolicy.getPolRevNo().indexOf("/")) + "/" + count);
            } else {
                refundPolicy.setPolRevNo(originalPolicy.getRevisionFormat() + "/" + refundCount);
            }


            refundPolicy.setTransType("RF");
            refundPolicy.setPolRevStatus("RF");
            refundPolicy.setCurrentStatus("D");
            refundPolicy.setAuthStatus("D");


            refundPolicy.setCreatedUser(currentUser);
            refundPolicy.setPolCreateddt(new Date());
            refundPolicy.setPreviousTrans(originalPolicy);


            refundPolicy.setAgent(originalPolicy.getAgent());
            refundPolicy.setBinder(originalPolicy.getBinder());
            refundPolicy.setBranch(originalPolicy.getBranch());
            refundPolicy.setClient(originalPolicy.getClient());
            refundPolicy.setClientPolNo(originalPolicy.getClientPolNo());
            refundPolicy.setProduct(originalPolicy.getProduct());
            refundPolicy.setTransCurrency(originalPolicy.getTransCurrency());


            refundPolicy.setWefDate(originalPolicy.getWefDate());
            refundPolicy.setWetDate(originalPolicy.getWetDate());
            refundPolicy.setCoverFrom(originalPolicy.getCoverFrom());
            refundPolicy.setCoverTo(originalPolicy.getCoverTo());
            refundPolicy.setRenewalDate(originalPolicy.getRenewalDate());
            refundPolicy.setUwYear(originalPolicy.getUwYear());


            refundPolicy.setInterfaceType(originalPolicy.getInterfaceType());
            refundPolicy.setBusinessType(originalPolicy.getBusinessType());
            refundPolicy.setPaymentMode(originalPolicy.getPaymentMode());
            refundPolicy.setFrequency(originalPolicy.getFrequency());
            refundPolicy.setRevisionFormat(originalPolicy.getRevisionFormat());


            refundPolicy.setCommAllowed(originalPolicy.isCommAllowed());
            refundPolicy.setRenewable(originalPolicy.isRenewable());


            refundPolicy.setBasicPrem(originalPolicy.getBasicPrem());
            refundPolicy.setCommAmt(originalPolicy.getCommAmt());
            refundPolicy.setExtras(originalPolicy.getExtras());
            refundPolicy.setNetPrem(originalPolicy.getNetPrem());
            refundPolicy.setPhcf(originalPolicy.getPhcf());
            refundPolicy.setPremium(originalPolicy.getPremium());
            refundPolicy.setStampDuty(originalPolicy.getStampDuty());
            refundPolicy.setSumInsured(originalPolicy.getSumInsured());
            refundPolicy.setTrainingLevy(originalPolicy.getTrainingLevy());
            refundPolicy.setWhtx(originalPolicy.getWhtx());


            refundPolicy.setOldpolNo(originalPolicy.getOldpolNo());
            refundPolicy.setAdminFeeApplicable(originalPolicy.getAdminFeeApplicable());




            PolicyTrans savedRefundPolicy = policyRepo.save(refundPolicy);

            reverseTrans.setPolicy(savedRefundPolicy);
        }
        Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("RFD");
        if (sequenceRepo.count(seqPredicate) == 0) {
            //System.out.println("Sequence refund error");
            throw new BadRequestException("Sequence for Refunds has not been defined");
        }
        SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
        Long seqNumber = sequence.getNextNumber();
        final String transType = "RF";
        if (mappingRepo.count(QTransactionMapping.transactionMapping.transType.eq(transType)) == 0) {
           // System.out.println("Sequence refund error  6");
            throw new BadRequestException("Error getting Transaction Mapping Setups..Contact System Administrator");
        }
        TransactionMapping mapping = mappingRepo.findOne(QTransactionMapping.transactionMapping.transType.eq(transType));
        String refNo = mapping.getCreditCode() + String.format("%05d", seqNumber);
        sequence.setLastNumber(seqNumber);
        sequence.setNextNumber(seqNumber + 1);
        sequenceRepo.save(sequence);
        // Copy basic fields from original transaction
        reverseTrans.setTransDate(new Date());
        reverseTrans.setValueDate(java.sql.Date.valueOf(HolidayUtils.getAdjustedTransactionDate(LocalDate.now())));
        reverseTrans.setRefNo(refNo);
        reverseTrans.setOtherRef(originalTrans.getOtherRef());
        reverseTrans.setTransType("RF"); // Refund transaction type

        // Reverse the debit/credit indicator (Credit becomes Debit for refund)
        reverseTrans.setTransdc("D"); // Debit for refund

        reverseTrans.setControlAcc(originalTrans.getControlAcc());
        reverseTrans.setClientType(originalTrans.getClientType());

        // Copy relationship fields
        reverseTrans.setClient(originalTrans.getClient());
        reverseTrans.setAgent(originalTrans.getAgent());
        //reverseTrans.setPolicy(originalTrans.getPolicy());
        reverseTrans.setCurrency(originalTrans.getCurrency());
        reverseTrans.setCurrRate(originalTrans.getCurrRate());
        reverseTrans.setBranch(originalTrans.getBranch());
        reverseTrans.setFundParams(originalTrans.getFundParams());
        reverseTrans.setTransaction(originalTrans.getTransaction());

        // Reverse all amounts - convert negative to positive and reverse sign logic
        BigDecimal refundAmount = originalTrans.getBalance().abs();
        reverseTrans.setAmount(refundAmount);
        reverseTrans.setBalance(refundAmount);
        reverseTrans.setNetAmount(originalTrans.getNetAmount() != null ? originalTrans.getNetAmount().negate() : null);
        reverseTrans.setSettleAmt(originalTrans.getSettleAmt() != null ? originalTrans.getSettleAmt().negate() : null);
        reverseTrans.setTempSettleAmt(originalTrans.getTempSettleAmt() != null ? originalTrans.getTempSettleAmt().negate() : BigDecimal.ZERO);

        // Reverse all other financial fields
        reverseTrans.setSd(originalTrans.getSd() != null ? originalTrans.getSd().negate() : null);
        reverseTrans.setTl(originalTrans.getTl() != null ? originalTrans.getTl().negate() : null);
        reverseTrans.setWhtx(originalTrans.getWhtx() != null ? originalTrans.getWhtx().negate() : null);
        reverseTrans.setCommission(originalTrans.getCommission() != null ? originalTrans.getCommission().negate() : null);
        reverseTrans.setPhfund(originalTrans.getPhfund() != null ? originalTrans.getPhfund().negate() : null);
        reverseTrans.setExtras(originalTrans.getExtras() != null ? originalTrans.getExtras().negate() : null);
        reverseTrans.setAdminFeeNet(originalTrans.getAdminFeeNet() != null ? originalTrans.getAdminFeeNet().negate() : null);
        reverseTrans.setServiceCharge(originalTrans.getServiceCharge() != null ? originalTrans.getServiceCharge().negate() : null);
        reverseTrans.setIssueCardFee(originalTrans.getIssueCardFee() != null ? originalTrans.getIssueCardFee().negate() : null);
        reverseTrans.setReIssueCardFee(originalTrans.getReIssueCardFee() != null ? originalTrans.getReIssueCardFee().negate() : null);
        reverseTrans.setVatAmount(originalTrans.getVatAmount() != null ? originalTrans.getVatAmount().negate() : null);

        // Set refund-specific fields
        reverseTrans.setNarrations("Posting Client Debit Note");
        reverseTrans.setAuthorised("N"); // Not authorized yet - awaiting approval
        reverseTrans.setAuthDate(null); // Will be set when authorized
        reverseTrans.setUserAuth(null); // Will be set when authorized
        reverseTrans.setOrigin("U");
        reverseTrans.setPostedUser(currentUser);
        reverseTrans.setPostedDate(new Date());
        reverseTrans.setPayeeName(originalTrans.getPayeeName());
        // Add before systemTransactionsRepo.save(reverseTrans);
        System.out.println("Narrations length: " + (reverseTrans.getNarrations() != null ? reverseTrans.getNarrations().length() : 0));
        System.out.println("RefNo length: " + (reverseTrans.getRefNo() != null ? reverseTrans.getRefNo().length() : 0));
        System.out.println("PayeeName length: " + (reverseTrans.getPayeeName() != null ? reverseTrans.getPayeeName().length() : 0));
        // Add before systemTransactionsRepo.save(reverseTrans);
        System.out.println("OtherRef length: " + (reverseTrans.getOtherRef() != null ? reverseTrans.getOtherRef().length() : 0));
        System.out.println("TransType length: " + (reverseTrans.getTransType() != null ? reverseTrans.getTransType().length() : 0));
        System.out.println("TransDc length: " + (reverseTrans.getTransdc() != null ? reverseTrans.getTransdc().length() : 0));
        System.out.println("ControlAcc length: " + (reverseTrans.getControlAcc() != null ? reverseTrans.getControlAcc().length() : 0));
        System.out.println("ClientType length: " + (reverseTrans.getClientType() != null ? reverseTrans.getClientType().length() : 0));
        System.out.println("Origin length: " + (reverseTrans.getOrigin() != null ? reverseTrans.getOrigin().length() : 0));
        System.out.println("Authorised length: " + (reverseTrans.getAuthorised() != null ? reverseTrans.getAuthorised().length() : 0));


        reverseTrans.setRefundTransaction(originalTrans);

        // Save the transaction first to get the ID
        systemTransactionsRepo.save(reverseTrans);

        // Create maker-checker task after saving (so we have the transaction ID)
        try {
            createRefundMakerChecker(reverseTrans, originalTrans, currentUser);
        } catch (Exception e) {
            // Log the error but don't fail the transaction creation
            System.err.println("Failed to create maker-checker task: " + e.getMessage());
            e.printStackTrace();
            // You could also delete the transaction here if maker-checker creation is mandatory
        }
        // Link to original transaction
//        reverseTrans.setRefundTransaction(originalTrans.getRefundTransaction());

        return reverseTrans;
    }
    private void createRefundMakerChecker(SystemTransactions reverseTrans, SystemTransactions originalTrans, User currentUser) throws BadRequestException {
        try {
            MakerCheckDTO makerCheckDTO = new MakerCheckDTO();

            String taskName = "Refund Processing: Transaction " + originalTrans.getTransno();
            if (originalTrans.getClient() != null) {
                String clientName = (originalTrans.getClient().getFname() != null ? originalTrans.getClient().getFname() : "") +
                        " " + (originalTrans.getClient().getOtherNames() != null ? originalTrans.getClient().getOtherNames() : "");
                String policyNo = originalTrans.getPolicy() != null ? originalTrans.getPolicy().getPolNo() : "N/A";

//                taskName = "Refund for " + clientName.trim() + " - Policy: " + policyNo;
                taskName = policyNo;
                // Truncate if too long for database
                if (taskName.length() > 100) {
                    taskName = taskName.substring(0, 97) + "...";
                }
            }
            makerCheckDTO.setTaskName(taskName);
            makerCheckDTO.setTaskType("RF");
            makerCheckDTO.setTaskCode(reverseTrans.getTransno());
            makerCheckDTO.setStatus("N");
            makerCheckDTO.setPolicyId(reverseTrans.getPolicy().getPolicyId());

            // Create refund details DTO for task JSON
            RefundDetailsDTO refundDetailsDTO = new RefundDetailsDTO();
            refundDetailsDTO.setReverseTransId(reverseTrans.getTransno());
            refundDetailsDTO.setOriginalTransId(originalTrans.getTransno());
            refundDetailsDTO.setRefundAmount(reverseTrans.getAmount());
            refundDetailsDTO.setClientName(originalTrans.getClient() != null ?
                    originalTrans.getClient().getFname() + " " + originalTrans.getClient().getOtherNames() :
                    "Unknown Client");
            refundDetailsDTO.setPolicyNo(originalTrans.getPolicy() != null ? originalTrans.getPolicy().getPolNo() : null);
            refundDetailsDTO.setOriginalTransType(originalTrans.getTransType());
            refundDetailsDTO.setRefundReason("System generated refund");
            refundDetailsDTO.setRequestedBy(currentUser.getUsername());
            refundDetailsDTO.setRequestDate(new Date());

            Gson gson = new GsonBuilder()
                    .setDateFormat("MMM dd, yyyy HH:mm:ss z")
                    .create();
            String jsonString = gson.toJson(refundDetailsDTO);

            if (jsonString == null || jsonString.trim().isEmpty() || "{}".equals(jsonString)) {
                throw new BadRequestException("Failed to create valid task JSON for refund");
            }

            makerCheckDTO.setJson(jsonString);
            makerCheckDTO.setMadeOnDate(new Date());
            makerCheckDTO.setMakerId(currentUser.getId());
            makerCheckDTO.setInitiatorId(currentUser.getId());
            makerCheckDTO.setReferenceId(reverseTrans.getTransno());

            // Assign checkers
            List<UserDTO> eligibleCheckers = makerCheckerService.findEligibleCheckers("AUTHORIZE_POLICY", null);
            List<Long> checkerIds = new ArrayList<>();

            if (!makerCheckerRepo.exists(QMakerChecker.makerChecker.taskType.equalsIgnoreCase("AUTHORIZE_POLICY")
                    .and(QMakerChecker.makerChecker.taskCode.eq(reverseTrans.getTransno())))) {
                for (UserDTO eligibleChecker : eligibleCheckers) {
                    checkerIds.add(eligibleChecker.getId());
                }
            }

            if (checkerIds.isEmpty()) {
                throw new BadRequestException("No eligible checkers found for refund");
            }

            makerCheckDTO.setAssignedCheckers(new Gson().toJson(checkerIds));

            // Create Maker-Checker
            makerCheckerService.checkExists(makerCheckDTO);
            makerCheckerService.createMakerChecker(makerCheckDTO);

        } catch (Exception e) {
            throw new BadRequestException("Failed to create Maker-Checker task for refund: " + e.getMessage());
        }
    }
    @Override
    public DataTablesResult<RefundClientDTO> findClientRefunds(DataTablesRequest request, Long clientCode, Long prodCode, String policyNo) throws IllegalAccessException {

        QSystemTransactions systemTrans = QSystemTransactions.systemTransactions;
        QSystemTransactions refundTrans = new QSystemTransactions("refundTrans");
        QClientDef client = systemTrans.client;
        QPolicyTrans policy = systemTrans.policy;
        QRiskTrans risk = new QRiskTrans("risk");

        String trimmedPolicyNo = (policyNo != null) ? policyNo.trim() : null;
        Predicate basePred = systemTrans.transType.notIn("RC", "SAG", "RF")
                .and(systemTrans.balance.lt(0))
                .and(systemTrans.clientType.eq("C"))
                .and(systemTrans.authorised.eq("Y"))
                .and(systemTrans.transdc.eq("C"))
                .and((clientCode == null) ? systemTrans.client.isNotNull() : systemTrans.client.tenId.eq(clientCode))
                .and((prodCode == null) ? systemTrans.policy.product.isNotNull() : systemTrans.policy.product.proCode.eq(prodCode))
                .and((trimmedPolicyNo == null || trimmedPolicyNo.isEmpty()) ? systemTrans.policy.polNo.isNotNull() : systemTrans.policy.polNo.eq(trimmedPolicyNo));

        Predicate baseLifePred = systemTrans.transType.notIn("RC", "SAG", "RF")
                .and(systemTrans.balance.lt(0))
                .and(systemTrans.clientType.eq("A"))
                .and(systemTrans.authorised.eq("Y"))
                .and(systemTrans.transdc.eq("C"))
                .and(policy.businessType.eq("L"))
                .and((clientCode == null) ? systemTrans.client.isNotNull() : systemTrans.client.tenId.eq(clientCode))
                .and((prodCode == null) ? systemTrans.policy.product.isNotNull() : systemTrans.policy.product.proCode.eq(prodCode))
                .and((trimmedPolicyNo == null || trimmedPolicyNo.isEmpty()) ? systemTrans.policy.polNo.isNotNull() : systemTrans.policy.polNo.eq(trimmedPolicyNo));
        BooleanBuilder builder = new BooleanBuilder();
        builder.or(basePred);
        builder.or(baseLifePred);

        // Subquery to find transactions that already have refunds (pending or approved)
        JPASubQuery refundSubquery = new JPASubQuery()
                .from(refundTrans)
                .where(refundTrans.transType.eq("RF")
                        .and(refundTrans.refundTransaction.transno.eq(systemTrans.transno)));

        // Exclude transactions that already have refunds initiated
        Predicate finalPred = ExpressionUtils.and(builder, refundSubquery.notExists());


        JPAQuery query = new JPAQuery(entityManager)
                .from(systemTrans)
                .leftJoin(systemTrans.client)
                .leftJoin(systemTrans.policy)
                .leftJoin(systemTrans.policy.riskTrans, risk)
                .where(finalPred);


        query.offset(request.getOffset())
                .limit(request.getPageSize());


        List<RefundClientDTO> dtoList = query.list(Projections.constructor(RefundClientDTO.class,
                systemTrans.transno,
                systemTrans.transDate,
                systemTrans.client.fname,
                systemTrans.client.otherNames,
                systemTrans.policy.polNo,
                systemTrans.policy.refNo,
                systemTrans.refNo,
                systemTrans.transType,
                systemTrans.policy.product.proDesc,
                systemTrans.settleAmt,
                systemTrans.balance, // Use balance directly; handle abs in DTO constructor
                risk.riskId
        ));

        // Get total count for pagination
        long totalCount = query.count();

        // Create Page object
        PageRequest pageRequest = new PageRequest(request.getPageNumber(), request.getPageSize());
        Page<RefundClientDTO> dtoPage = new PageImpl<>(dtoList, pageRequest, totalCount);

        return new DataTablesResult<>(request, dtoPage);
    }

//        // Subquery to find transactions that already have refunds (pending or approved)
//        JPASubQuery refundSubquery = new JPASubQuery()
//                .from(refundTrans)
//                .where(refundTrans.transType.eq("RF")
//                        .and(refundTrans.refundTransaction.transno.eq(systemTrans.transno)));
//
//        // Exclude transactions that already have refunds initiated
//        Predicate finalPred = ExpressionUtils.and(basePred, refundSubquery.notExists());
//
//
//        JPAQuery query = new JPAQuery(entityManager)
//                .from(systemTrans)
//                .leftJoin(systemTrans.client)
//                .leftJoin(systemTrans.policy)
//                .where(finalPred);
//
//
//        query.offset(request.getOffset())
//                .limit(request.getPageSize());
//
//
//        List<RefundClientDTO> dtoList = query.list(Projections.constructor(RefundClientDTO.class,
//                systemTrans.transno,
//                systemTrans.transDate,
//                systemTrans.client.fname,
//                systemTrans.client.otherNames,
//                systemTrans.policy.polNo,
//                systemTrans.policy.refNo,
//                systemTrans.refNo,
//                systemTrans.transType,
//                systemTrans.policy.product.proDesc,
//                systemTrans.settleAmt,
//                systemTrans.balance // Use balance directly; handle abs in DTO constructor
//        ));
//
//        // Get total count for pagination
//        long totalCount = query.count();
//
//        // Create Page object
//        PageRequest pageRequest = new PageRequest(request.getPageNumber(), request.getPageSize());
//        Page<RefundClientDTO> dtoPage = new PageImpl<>(dtoList, pageRequest, totalCount);
//
//        return new DataTablesResult<>(request, dtoPage);
//    }



    @Override
    public Page<SystemTransactions> findInsuranceReceipts(String paramString,Long accountId, Pageable paramPageable) {
        Predicate pred=null;
        AccountDef agent = accountRepo.findByAcctId(accountId);
        if(agent!=null){
            if (paramString == null || StringUtils.isBlank(paramString)) {
                pred =QSystemTransactions.systemTransactions.isNotNull().and(QSystemTransactions.systemTransactions.agent.eq(agent)).and(QSystemTransactions.systemTransactions.clientType.eq("A"))
                        .and(QSystemTransactions.systemTransactions.balance.ne(BigDecimal.ZERO).and(QSystemTransactions.systemTransactions.transType.eq("RC")).and(QSystemTransactions.systemTransactions.balance.abs().gt(BigDecimal.ZERO))
                                .and(QSystemTransactions.systemTransactions.transdc.eq("C")));
            } else {
                pred = QSystemTransactions.systemTransactions.isNotNull().and(QSystemTransactions.systemTransactions.agent.eq(agent)).and(QSystemTransactions.systemTransactions.clientType.eq("A"))
                        .and(QSystemTransactions.systemTransactions.balance.ne(BigDecimal.ZERO)).and(QSystemTransactions.systemTransactions.transType.eq("RC"))
                        .and(QSystemTransactions.systemTransactions.transdc.eq("C")).and(QSystemTransactions.systemTransactions.refNo.contains(paramString)).and(QSystemTransactions.systemTransactions.balance.abs().gt(BigDecimal.ZERO))
                        .and(QSystemTransactions.systemTransactions.agent.name.contains(paramString));
            }
        }
        return transRepo.findAll(pred, paramPageable);
    }

    @Override
    public DataTablesResult<FinalReportFormatDTO> findFinalReportFormats(String type,DataTablesRequest request) {
        List<FinalReportFormatDTO> reportFormatDTOList = new ArrayList<>();
        List<Object[]> reportFormats = finalReportFormatsRepo.searchAllReportFormats(type,(request.getSearch()!=null && request.getSearch().getValue()!=null)?"%"+request.getSearch().getValue()+"%":"%%"
                ,request.getPageNumber(), request.getPageSize());
        long rowCount = 0L;
        if(!reportFormats.isEmpty()) rowCount = ((BigInteger)reportFormats.get(0)[11]).intValue();
        for(Object[] format:reportFormats) {
            FinalReportFormatDTO reportFormatDTO = new FinalReportFormatDTO();
            reportFormatDTO.setRowCode((String) format[0]);
            reportFormatDTO.setDescription((String) format[1]);
            reportFormatDTO.setDetailFormat((String) format[2]);
            reportFormatDTO.setSummaryFormat((String) format[3]);
            reportFormatDTO.setType((String) format[4]);
            reportFormatDTO.setOrder((Integer) format[5]);
            reportFormatDTO.setPickedFrom((String) format[6]);
            reportFormatDTO.setAssetLiability((String) format[7]);
            if(format[8]!=null)
                reportFormatDTO.setAssetLiabilitySign((Integer) format[8]);
            reportFormatDTO.setAllocation(((BigDecimal) format[9]));
            reportFormatDTO.setRfId(((BigInteger) format[10]).longValue());
            reportFormatDTOList.add(reportFormatDTO);
        }
        Page<FinalReportFormatDTO>  page = new PageImpl<>(reportFormatDTOList,request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<FinalReportFormatTotalDTO> findFinalReportFormatsTotals(String type, DataTablesRequest request) {
        List<FinalReportFormatTotalDTO> reportFormatDTOList = new ArrayList<>();
        List<Object[]> reportFormats = reportFormatTotalsRepo.searchAllReportTotals(type,(request.getSearch()!=null && request.getSearch().getValue()!=null)?"%"+request.getSearch().getValue()+"%":"%%"
                ,request.getPageNumber(), request.getPageSize());
        long rowCount = 0L;
        if(!reportFormats.isEmpty()) rowCount = ((BigInteger)reportFormats.get(0)[4]).intValue();
        for(Object[] format:reportFormats) {
            final FinalReportFormatTotalDTO formatTotalDTO = new FinalReportFormatTotalDTO();
            formatTotalDTO.setRftId(((BigInteger) format[0]).longValue());
            formatTotalDTO.setSign((Boolean) format[1]);
            formatTotalDTO.setColumn((String) format[2]);
            formatTotalDTO.setTotal((String) format[3]);
            reportFormatDTOList.add(formatTotalDTO);
        }
        Page<FinalReportFormatTotalDTO>  page = new PageImpl<>(reportFormatDTOList,request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<FinalReportFormatAcctsDTO> findFinalReportFormatsGroupAccts(Long formatId, DataTablesRequest request) {
        List<FinalReportFormatAcctsDTO> reportFormatDTOList = new ArrayList<>();
        List<Object[]> reportFormatAccounts = finalReportFormatGroupAccountsRepo.searchAllReportFormatsGroupAccts(formatId,(request.getSearch()!=null && request.getSearch().getValue()!=null)?"%"+request.getSearch().getValue()+"%":"%%"
                ,request.getPageNumber(), request.getPageSize());
        long rowCount = 0L;
        if(!reportFormatAccounts.isEmpty()) rowCount = ((BigInteger)reportFormatAccounts.get(0)[3]).intValue();
        for(Object[] format:reportFormatAccounts) {
            FinalReportFormatAcctsDTO reportFormatAcctsDTO = new FinalReportFormatAcctsDTO();
            reportFormatAcctsDTO.setRfaId(((BigDecimal) format[0]).longValue());
            reportFormatAcctsDTO.setAccountNo((String) format[1]);
            reportFormatAcctsDTO.setSign((Boolean) format[2]);
            reportFormatDTOList.add(reportFormatAcctsDTO);
        }
        Page<FinalReportFormatAcctsDTO>  page = new PageImpl<>(reportFormatDTOList,request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<FinalReportFormatAcctsDTO> findFinalReportFormatsAccts(Long formatId, DataTablesRequest request) {
        List<FinalReportFormatAcctsDTO> reportFormatDTOList = new ArrayList<>();
        List<Object[]> reportFormatAccounts = finalReportFormatAccountsRepo.searchAllReportFormatsAccts(formatId,(request.getSearch()!=null && request.getSearch().getValue()!=null)?"%"+request.getSearch().getValue()+"%":"%%"
                ,request.getPageNumber(), request.getPageSize());
        long rowCount = 0L;
        if(!reportFormatAccounts.isEmpty()) rowCount = ((BigInteger)reportFormatAccounts.get(0)[4]).intValue();
        for(Object[] format:reportFormatAccounts) {
            FinalReportFormatAcctsDTO reportFormatAcctsDTO = new FinalReportFormatAcctsDTO();
            reportFormatAcctsDTO.setRfaId(((BigDecimal) format[0]).longValue());
            reportFormatAcctsDTO.setAccountNo((String) format[1]);
            reportFormatAcctsDTO.setSign((Boolean) format[2]);
            reportFormatAcctsDTO.setAccountName((String) format[3]);
            reportFormatDTOList.add(reportFormatAcctsDTO);
        }
        Page<FinalReportFormatAcctsDTO>  page = new PageImpl<>(reportFormatDTOList,request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<PayeesDTO> findAllPayees(DataTablesRequest request) {
        List<Object[]> payeesList = payeesRepo.searchAllPayes((request.getSearch()!=null && request.getSearch().getValue()!=null)?"%"+request.getSearch().getValue()+"%":"%%"
                ,request.getPageNumber(), request.getPageSize());
        List<PayeesDTO> payeesDTOList = new ArrayList<>();
        long rowCount = 0L;
        if(!payeesList.isEmpty()) rowCount = ((BigInteger)payeesList.get(0)[8]).intValue();
        for(Object[] payee:payeesList) {
            PayeesDTO payeesDTO = new PayeesDTO();
            payeesDTO.setPayId(((BigInteger) payee[0]).longValue());
            payeesDTO.setCreatedDate((Date)payee[1]);
            payeesDTO.setEmail((String) payee[2]);
            payeesDTO.setFullName((String) payee[3]);
            payeesDTO.setMobileNo((String) payee[4]);
            payeesDTO.setTelNo((String) payee[5]);
            payeesDTO.setCreatedUser((String) payee[6]);
            payeesDTO.setStatus((String) payee[7]);
            payeesDTOList.add(payeesDTO);
        }
        Page<PayeesDTO>  page = new PageImpl<>(payeesDTOList,request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<PayeeAccountsDTO> findAllPayeesAccounts(Long payeeId,DataTablesRequest request) {
        List<Object[]> payeesAccountsList = payeeAccountsRepo.searchAllPayesAccounts(payeeId,
                (request.getSearch()!=null && request.getSearch().getValue()!=null)?"%"+request.getSearch().getValue()+"%":"%%"
                ,request.getPageNumber(), request.getPageSize());
        List<PayeeAccountsDTO> payeesDTOList = new ArrayList<>();
        long rowCount = 0L;
        if(!payeesAccountsList.isEmpty()) rowCount = ((BigInteger)payeesAccountsList.get(0)[6]).intValue();
        for(Object[] payee:payeesAccountsList) {
            PayeeAccountsDTO payeesDTO = new PayeeAccountsDTO();
            payeesDTO.setPaycId(((BigInteger) payee[0]).longValue());
            payeesDTO.setAccountNo((String) payee[1]);
            payeesDTO.setBankBranch((String) payee[2]);
            payeesDTO.setBankBranchId(((BigInteger) payee[3]).longValue());
            payeesDTO.setBank((String) payee[4]);
            payeesDTO.setStatus((String) payee[5]);
            payeesDTOList.add(payeesDTO);
        }
        Page<PayeeAccountsDTO>  page = new PageImpl<>(payeesDTOList,request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    private Date constructDate(Long start, Long year, String month){
        try {
            if(start==null)
                return new SimpleDateFormat("dd/MMMM/yyyy").parse("01/"+month+"/"+year);
            else
                return new SimpleDateFormat("dd/MMMM/yyyy").parse(start+"/"+month+"/"+year);
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public DataTablesResult<OpeningBalanceDTO> findAllOpeningBalances(DataTablesRequest request) throws BadRequestException {
        Long currentYear = Long.parseLong(new SimpleDateFormat("yyyy").format(new Date()));
        final Long headOfficeId = branchRepository.findHeadOffice();
        if(headOfficeId==null){
            throw new BadRequestException("Unable to get Head Office. Cannot continue...");
        }
        String firstPeriod = accountYearPeriodsRepo.firstPeriodOfYr(headOfficeId,currentYear);
        String lastPeriod = accountYearPeriodsRepo.lastPeriodOfYr(headOfficeId,currentYear);
        Date wefDate = constructDate(null,currentYear,firstPeriod);
        if(wefDate==null){
            throw new BadRequestException("Unable to get Accounting Start Period of the current year....");
        }
        Date lastDate = constructDate(null,currentYear,lastPeriod);
        if(lastDate==null){
            throw new BadRequestException("Unable to get Accounting End Period of the current year....");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(lastDate);
        int last  = cal.getActualMaximum(Calendar.DATE);
        Date wetDate = constructDate((long) last,currentYear,lastPeriod);

        // Extract search value from DataTables request
        String searchValue = "";
        if (request.getSearch() != null && request.getSearch().getValue() != null) {
            searchValue = request.getSearch().getValue().trim();
        }

        // Call updated repository method with search parameter
        List<Object[]> openingBalancesList = balancesRepo.queryAcctYearOpeningBalances(
                currentYear, wefDate, wetDate, searchValue,
                request.getPageNumber(), request.getPageSize()
        );

        List<OpeningBalanceDTO> balanceDTOList = new ArrayList<>();
        long rowCount = 0L;
        if(!openingBalancesList.isEmpty()) rowCount = ((BigInteger)openingBalancesList.get(0)[5]).intValue();
        for(Object[] bal:openingBalancesList) {
            final OpeningBalanceDTO balanceDTO = new OpeningBalanceDTO();
            balanceDTO.setAccountName(balancesRepo.getAccountName((String)bal[4]));
            balanceDTO.setAccountNo((String)bal[4]);
            balanceDTO.setBalance((BigDecimal) bal[0]);
            balanceDTO.setCurrBalance((BigDecimal) bal[3]);
            balanceDTOList.add(balanceDTO);
        }
        Page<OpeningBalanceDTO>  page = new PageImpl<>(balanceDTOList,request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional
    public void createPayee(PayeesDTO payeesDTO) throws BadRequestException {
        if(payeesDTO.getFullName()==null){
            throw new BadRequestException("Enter Account Full Name");
        }
        if(payeesDTO.getEmail()==null){
            throw new BadRequestException("Enter Account Email Address");
        }
        if(payeesDTO.getMobileNo()==null){
            throw new BadRequestException("Enter Account Phone Number");
        }
        Payees payees = new Payees();
        if(payeesDTO.getPayId()==null){
            if(payeesRepo.countPayesByEmail(payeesDTO.getEmail()) > 0){
                throw new BadRequestException("Payee with Email Address Exists..");
            }
            payees.setStatus("A");
        }
        else{
            if(payeesRepo.countPayesByEmail(payeesDTO.getEmail()) > 1){
                throw new BadRequestException("Payee with Email Address Exists..");
            }
            payees.setStatus(payeesDTO.getStatus());
            payees.setPayId(payeesDTO.getPayId());
        }
        payees.setCreatedBy(userUtils.getCurrentUser());
        payees.setCreatedDate(new Date());
        payees.setEmail(payeesDTO.getEmail());
        payees.setMobileNo(payeesDTO.getMobileNo());
        payees.setTelNo(payeesDTO.getTelNo());
        payees.setFullName(payeesDTO.getFullName());
        Payees savedPayee = payeesRepo.save(payees);
        if(payeesDTO.getPayId()==null){
            if(payeesDTO.getAccountNo()==null){
                throw new BadRequestException("Please Provide Account No");
            }
            if(payeesDTO.getBankBranchId()==null){
                throw new BadRequestException("Please Select Bank Branch");
            }
            PayeeAccounts payeeAccounts = new PayeeAccounts();
            payeeAccounts.setAccountNo(payeesDTO.getAccountNo());
            payeeAccounts.setBankBranches(bankBranchRepo.findOne(payeesDTO.getBankBranchId()));
            payeeAccounts.setPayees(savedPayee);
            payeeAccounts.setStatus("A");
            payeeAccountsRepo.save(payeeAccounts);
        }
    }

    @Override
    @Transactional(rollbackFor = {BadRequestException.class})
    public void createPayeeAccount(PayeeAccountsDTO payeesDTO) throws BadRequestException {
        if(payeesDTO.getPayId()==null){
            throw new BadRequestException("Select Payee to Add Account");
        }
        if(payeesDTO.getAccountNo()==null){
            throw new BadRequestException("Enter Account No to continue...");
        }
        if(payeesDTO.getBankBranchId()==null){
            throw new BadRequestException("Please Select Bank Branch");
        }
        PayeeAccounts payeeAccounts = new PayeeAccounts();
        if(payeesDTO.getPaycId()==null) {
            List<PayeeAccounts> payeeAccountsList = payeeAccountsRepo.findOtherAccounts(payeesDTO.getPayId());
            for (PayeeAccounts accounts : payeeAccountsList) {
                accounts.setStatus("I");
            }
            payeeAccountsRepo.save(payeeAccountsList);
            payeeAccounts.setStatus("A");
        }
        else{
            payeeAccounts.setStatus(payeesDTO.getStatus());
            payeeAccounts.setPaycId(payeesDTO.getPaycId());

        }

        payeeAccounts.setPayees(payeesRepo.findOne(payeesDTO.getPayId()));
        payeeAccounts.setBankBranches(bankBranchRepo.findOne(payeesDTO.getBankBranchId()));
        payeeAccounts.setAccountNo(payeesDTO.getAccountNo());

        payeeAccountsRepo.save(payeeAccounts);
        if(payeeAccountsRepo.countAccountExists(payeesDTO.getPayId(),payeesDTO.getAccountNo()) > 1){
            throw new BadRequestException("Account Number is duplicated for Payee..");
        }
    }

    @Override
    public void createAccountingYear(AccountYearDTO accountYearDTO) throws BadRequestException {
        if(accountYearDTO.getYear()==null){
            throw new BadRequestException("Please enter a valid year");
        }
        if(accountYearDTO.getYear().longValue()!= yearsRepo.getCurrentAccountingYear().longValue()){
            throw new BadRequestException("Unable to generate accounting periods for the selected Period. Consult Administrator");
        }
//        if(yearsRepo.countCurrentAccountingYear() > 0){
//            throw new BadRequestException("An existing open accounting period exists..Close the period to open a new one...");
//        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, accountYearDTO.getYear().intValue());
        cal.set(Calendar.MONTH,0);
        cal.set(Calendar.DAY_OF_MONTH,1);
        Date startdate = cal.getTime();

        Calendar calendarEnd=Calendar.getInstance();
        calendarEnd.set(Calendar.YEAR,accountYearDTO.getYear().intValue());
        calendarEnd.set(Calendar.MONTH,11);
        calendarEnd.set(Calendar.DAY_OF_MONTH,31);
        Date enddate = calendarEnd.getTime();
        final Iterable<OrgBranch> branches = branchRepository.findAll();
        System.out.println("Total Branches..."+branches.spliterator().getExactSizeIfKnown());
        final List<AccountYearPeriods> periodsList = new ArrayList<>();
        for(OrgBranch branch:branches){
            final AccountYears accountYears = new AccountYears();
            accountYears.setYear(accountYearDTO.getYear());
            accountYears.setYearStart(startdate);
            accountYears.setYearEnd(enddate);
            accountYears.setState("O");
            accountYears.setTotalPeriods(12);
            accountYears.setBranch(branch);
            AccountYears savedYear = yearsRepo.save(accountYears);
            for (int currentMonth = 0; currentMonth < 12; currentMonth++) {
                Calendar cal2 = Calendar.getInstance();
                cal2.set(Calendar.MONTH, currentMonth);
                AccountYearPeriods periods = new AccountYearPeriods();
                periods.setAccountYears(savedYear);
                periods.setBranch(branch);
                cal2.set(Calendar.DAY_OF_MONTH, 1);
                periods.setMonthStart(cal2.getTime());
                cal2.set(Calendar.DAY_OF_MONTH, cal2.getActualMaximum(Calendar.DAY_OF_MONTH));
                periods.setMonthEnd(cal2.getTime());
                final String month = new SimpleDateFormat("MMMM").format(cal2.getTime());
                periods.setPeriodName(month);
                periods.setState("O");
                periodsList.add(periods);
            }
        }
        periodsRepo.save(periodsList);
    }
    @Override
    public Map<String, Long> getCurrentAccountPeriod() {
        final Long year = yearsRepo.getCurrentAccountingYear();
        Map<String,Long> map = new HashMap<>();
        map.put("currentYear",year);
        return map;
    }

    @Override
    public DataTablesResult<AccountYearDTO> findAccountingYears(Long year,Long branchCode, DataTablesRequest request) {
        List<Object[]> accountingYears = new ArrayList<>();
        if(year==null){
            accountingYears = yearsRepo.searchAccountingYearsNone(branchCode,request.getPageNumber(), request.getPageSize());
        }
        else
            accountingYears = yearsRepo.searchAccountingYears(branchCode,year,request.getPageNumber(), request.getPageSize());
        List<AccountYearDTO> accountYearDTOList = new ArrayList<>();
        long rowCount = 0L;
        if(!accountingYears.isEmpty()) rowCount = ((BigInteger)accountingYears.get(0)[6]).intValue();
        for(Object[] years:accountingYears) {
            AccountYearDTO accountYearDTO = new AccountYearDTO();
            accountYearDTO.setYearId(((BigInteger) years[0]).longValue());
            final String state = (String) years[1];
            if(state==null || state.equalsIgnoreCase("O")){
                accountYearDTO.setStatus("Open");
            }
            else accountYearDTO.setStatus("Closed");
            accountYearDTO.setYear(((BigInteger) years[2]).longValue());
            accountYearDTO.setWef((Date) years[3]);
            accountYearDTO.setWet((Date) years[4]);
            accountYearDTO.setNoofMonths(((Integer) years[5]).intValue());
            accountYearDTOList.add(accountYearDTO);
        }
        Page<AccountYearDTO>  page = new PageImpl<>(accountYearDTOList,request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<AccountingPeriodDTO> findAccountingYearsPeriods(Long yearId, DataTablesRequest request) {
        List<Object[]> accountingYearPeriods = periodsRepo.searchAccountingYearsPeriods(yearId,request.getPageNumber(), request.getPageSize());
        List<AccountingPeriodDTO> accountingPeriodDTOList = new ArrayList<>();
        long rowCount = 0L;
        if(!accountingYearPeriods.isEmpty()) rowCount = ((BigInteger)accountingYearPeriods.get(0)[9]).intValue();
        for(Object[] yearPrd:accountingYearPeriods) {
            AccountingPeriodDTO periodDTO = new AccountingPeriodDTO();
            periodDTO.setPeriodId(((BigInteger) yearPrd[0]).longValue());
            final String state = (String) yearPrd[4];
            if(state==null || state.equalsIgnoreCase("O")){
                periodDTO.setStatus("Open");
            }
            else periodDTO.setStatus("Closed");
            periodDTO.setPeriodName((String) yearPrd[1]);
            periodDTO.setWef((Date) yearPrd[2]);
            periodDTO.setWet((Date) yearPrd[3]);
            periodDTO.setClosedDate((Date) yearPrd[5]);
            periodDTO.setUserClosed((String) yearPrd[6]);
            periodDTO.setTransacted((String) yearPrd[7]);
            periodDTO.setBranch((String) yearPrd[8]);
            accountingPeriodDTOList.add(periodDTO);
        }
        Page<AccountingPeriodDTO>  page = new PageImpl<>(accountingPeriodDTOList,request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public void createReportFormatAccount(FinalReportFormatAcctsDTO acctsDTO) throws BadRequestException {
        if(acctsDTO.getAffsign()==null){
            throw new BadRequestException("Select Account Sign to continue....");
        }
        if(acctsDTO.getAccountNo()==null){
            throw new BadRequestException("Select A valid account to continue...");
        }

        if(acctsDTO.getRfId()==null){
            throw new BadRequestException("Select Report Format to Attach an account...");
        }
        if(finalReportFormatAccountsRepo.countDuplicateAccts(acctsDTO.getAccountNo(),acctsDTO.getRfId()) > 0){
            throw new BadRequestException("Account Already Mapped..Cannot remap");
        }

        FinalReportFormatAccounts accounts = new FinalReportFormatAccounts();
        accounts.setAccountNo(acctsDTO.getAccountNo());
        accounts.setAccountName(acctsDTO.getAccountName());
        accounts.setSign(acctsDTO.getAffsign().equalsIgnoreCase("P"));
        accounts.setReportFormats(finalReportFormatsRepo.findOne(acctsDTO.getRfId()));
        finalReportFormatAccountsRepo.save(accounts);
    }

    @Override
    public void createReportFormatGroupAccount(FinalReportFormatAcctsDTO acctsDTO) throws BadRequestException {
        if(acctsDTO.getAffsign()==null){
            throw new BadRequestException("Select Account Sign to continue....");
        }
        if(acctsDTO.getAccountNo()==null){
            throw new BadRequestException("Select A valid account to continue...");
        }

        if(finalReportFormatGroupAccountsRepo.countDuplicateAccts(acctsDTO.getAccountNo(),acctsDTO.getRfId())> 0){
            throw new BadRequestException("Group Account Already Mapped..Cannot remap");
        }

        if(finalReportFormatGroupAccountsRepo.validateAccount(acctsDTO.getAccountNo())!=1){
            throw new BadRequestException("Account No has been mapped incorrectly..Please check you account set ups...");
        }

        FinalReportFormatGroupAccounts groupAccounts = new FinalReportFormatGroupAccounts();
        groupAccounts.setAccountNo(acctsDTO.getAccountNo());
        groupAccounts.setSign(acctsDTO.getAffsign().equalsIgnoreCase("P"));
        groupAccounts.setReportFormats(finalReportFormatsRepo.findOne(acctsDTO.getRfId()));
        finalReportFormatGroupAccountsRepo.save(groupAccounts);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<GlTransactions> findAllGlTransactions(
            DataTablesRequest request,
            String accountNo,
            String asAtDate) throws IllegalAccessException {

        QGlTransactions glTransactions = QGlTransactions.glTransactions;
        BooleanExpression pred = glTransactions.glNo.isNotNull();

        // Add current year filter
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        pred = pred.and(glTransactions.glYear.eq(currentYear));

        // Add account filter
        if (accountNo != null && !accountNo.isEmpty()) {
            pred = pred.and(glTransactions.glAcc.code.eq(accountNo));
        }

        // Add date filter
        if (asAtDate != null && !asAtDate.isEmpty()) {
            try {
                Date filterDate = new SimpleDateFormat("yyyy-MM-dd").parse(asAtDate);
                pred = pred.and(glTransactions.authDate.lt(DateUtils.addDays(DateUtils.truncate(filterDate, Calendar.DATE), 1)));
            } catch (ParseException e) {
                throw new IllegalArgumentException("Invalid date format");
            }
        }

        // Add search filter
        if (request.getSearch() != null && !request.getSearch().getValue().isEmpty()) {
            String term = request.getSearch().getValue().trim();
            pred = pred.and(glTransactions.authDate.stringValue().containsIgnoreCase(term)
                    .or(glTransactions.transType.containsIgnoreCase(term)));
        }

        Page<GlTransactions> page = glTransRepo.findAll(pred, request);

        page.forEach(transaction -> {
            if ("C".equalsIgnoreCase(transaction.getGldc())) {
                transaction.setAmount(transaction.getAmount().negate());
            }
        });

        return new DataTablesResult<>(request, page);
    }
}