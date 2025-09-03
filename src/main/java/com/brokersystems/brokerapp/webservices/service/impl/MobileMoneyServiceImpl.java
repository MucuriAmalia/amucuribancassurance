package com.brokersystems.brokerapp.webservices.service.impl;

import com.brokersystems.brokerapp.accounts.model.CollectionAccounts;
import com.brokersystems.brokerapp.accounts.model.QCollectionAccounts;
import com.brokersystems.brokerapp.accounts.repository.CollectionAcctsRepo;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.NumberToWordsUtils;
import com.brokersystems.brokerapp.setup.dto.UserDTO;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.OrgBranchRepository;
import com.brokersystems.brokerapp.setup.repository.SequenceRepository;
import com.brokersystems.brokerapp.setup.service.UserService;
import com.brokersystems.brokerapp.trans.model.*;
import com.brokersystems.brokerapp.trans.repository.*;
import com.brokersystems.brokerapp.trans.service.AllocationService;
import com.brokersystems.brokerapp.trans.service.ReceiptService;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.webservices.model.MobMoneydto;
import com.brokersystems.brokerapp.webservices.service.MobileMoneyService;
import com.mysema.query.types.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.web.PortResolverImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by HP on 5/27/2018.
 */
@Service
public class MobileMoneyServiceImpl implements MobileMoneyService {

    @Autowired
    private IntegrationDtlsRepo integrationDtlsRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private ReceiptRepository receiptRepo;

    @Autowired
    private ReceiptDetailsRepository rctDetailsRepo;

    @Autowired
    private SequenceRepository sequenceRepo;

    @Autowired
    private SystemTransactionsRepo transRepo;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private OrgBranchRepository branchRepository;

    @Autowired
    private ReceiptService receiptService;

    @Autowired
    private SystemTransRepo systemTransRepo;

    @Autowired
    private CollectionAcctsRepo collectionAcctsRepo;

    @Autowired
    private SystemTransRepo systransRepo;

    @Autowired
    private AllocationService allocationService;

    @Autowired
    private SettlementRepo settlementRepo;


    @Override
    @Modifying
    @Transactional(readOnly = false, propagation = Propagation.NEVER)
    public String createMobileReceipt(MobMoneydto mobMoneydto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime dateTime = LocalDateTime.from(formatter.parse(mobMoneydto.getTransTime()));
        IntegrationDtls integrationDtls=new IntegrationDtls();
        integrationDtls.setTransTime(fromLdt(dateTime));
        integrationDtls.setBillRfNumber(mobMoneydto.getBillRfNumber());
        integrationDtls.setClientFname(mobMoneydto.getClientFname());
        integrationDtls.setLname(mobMoneydto.getLname());
        integrationDtls.setMiddleName(mobMoneydto.getMiddleName());
        integrationDtls.setPhoneNumber(mobMoneydto.getPhoneNumber());
        integrationDtls.setReceipted("N");
        integrationDtls.setTransAmount(new BigDecimal(mobMoneydto.getAmount()));
        integrationDtls.setTransId(mobMoneydto.getTransId());
        IntegrationDtls saved = integrationDtlsRepo.save(integrationDtls);
        return  saved.getTransId();
    }

    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class }, propagation = Propagation.REQUIRES_NEW)
    public void autoReceipt(String refNo) throws BadRequestException {
        IntegrationDtls dtls = integrationDtlsRepo.findOne(QIntegrationDtls.integrationDtls.transId.endsWith(refNo)
         .and(QIntegrationDtls.integrationDtls.receipted.eq("N")));
        if(dtls == null)
            throw new BadRequestException("No Record found to receipt....");
        String accountNo = "";
        if(!StringUtils.isBlank(dtls.getBillNewRfNumber())){
            accountNo=  dtls.getBillNewRfNumber().replaceAll("\\s", "");
        }
        else
          accountNo =  dtls.getBillRfNumber().replaceAll("\\s", "");
        long count = transRepo.count(QSystemTransactions.systemTransactions.refNo.eq(accountNo).and(QSystemTransactions.systemTransactions.clientType.eq("C"))
                                           .and(QSystemTransactions.systemTransactions.transdc.eq("D")));
        if(count ==0 || count > 1) throw new BadRequestException("Transaction not found");
        SystemTransactions transaction  = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(accountNo).and(QSystemTransactions.systemTransactions.clientType.eq("C"))
                .and(QSystemTransactions.systemTransactions.transdc.eq("D")));
        UserDTO user = userService.findByUserName("Mpesa");
        CollectionAccounts collectionAccounts = collectionAcctsRepo.findOne(QCollectionAccounts.collectionAccounts.name.eq("Mpesa"));
        ReceiptTrans receiptTrans = new ReceiptTrans();
        receiptTrans.setPayId(collectionAccounts.getCaId());
        OrgBranch branch = branchRepository.findOne(QOrgBranch.orgBranch.obShtDesc.eq("HO"));
        receiptTrans.setBrnCode(branch.getObId());
        receiptTrans.setPaymentRef(refNo);
        receiptTrans.setPaidBy(dtls.getClientFname()+" "+dtls.getMiddleName()+" "+dtls.getLname());
        receiptTrans.setReceiptAmount(dtls.getTransAmount());
        receiptTrans.setReceiptDate(dtls.getTransTime());
        receiptTrans.setReceiptDesc("Premium Payment via Mpesa");
        List<ReceiptTransDtls> receiptTransDtlses = new ArrayList<>();
        ReceiptTransDtls transDtls = new ReceiptTransDtls();
        transDtls.setTransNo(transaction.getTransno());
        transDtls.setRctAmount(dtls.getTransAmount());
        transDtls.setEndorsementNumber(transaction.getPolicy().getPolRevNo());
        receiptTransDtlses.add(transDtls);
        receiptTrans.setDetails(receiptTransDtlses);
        createReceipt(receiptTrans,userService.findById(user.getId()));
        dtls.setReceipted("Y");
        dtls.setReceiptedDate(new Date());
        dtls.setReceiptedUser(user.getUsername());
        integrationDtlsRepo.save(dtls);
    }

     private Date fromLdt(LocalDateTime ldt) {
        ZonedDateTime zdt = ZonedDateTime.of(ldt, ZoneId.systemDefault());
        GregorianCalendar cal = GregorianCalendar.from(zdt);
        return cal.getTime();
    }

    private Long createReceipt(ReceiptTrans receipt,User user) throws BadRequestException {

        if (receipt.getPayId() == null) {
            throw new BadRequestException("Collection Account is mandatory");
        }

        if(receipt.getBrnCode()==null){
            throw new BadRequestException("Receipt Branch is mandatory...");
        }

        if(!StringUtils.isBlank(receipt.getPaymentRef())){
            if(receiptRepo.count(QReceiptTrans.receiptTrans.paymentRef.eq(receipt.getPaymentRef())) > 0){
                throw new BadRequestException("Receipt Reference Exists......");
            }
        }
        Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("R");
        if (sequenceRepo.count(seqPredicate) == 0)
            throw new BadRequestException("Sequence for Receipt Transactions has not been setup");
        SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
        Long seqNumber = sequence.getNextNumber();
        final String receiptNo = sequence.getSeqPrefix() + String.format("%05d", seqNumber);
        receipt.setReceiptNo(receiptNo);
        receipt.setCounter(BigInteger.valueOf(seqNumber));
        List<ReceiptTransDtls> transDtls = new ArrayList<>();
        BigDecimal totalAllocAmount = BigDecimal.ZERO;
        SystemTrans transact = new SystemTrans();
        transact.setDoneDate(new Date());
        transact.setDoneBy(user);
        transact.setTransLevel("U");
        transact.setTransCode("RCT"); //A way to setup and look up for transaction transcode
        transact.setTransAuthorised("N");
        SystemTrans systemTrans = systemTransRepo.save(transact);
        for (ReceiptTransDtls tran : receipt.getDetails()) {
            if(tran.getTransNo()==null)
                throw new BadRequestException("Select Transaction to continue...");
            if(tran.getRctAmount().compareTo(BigDecimal.ZERO) <= 0)
                throw new BadRequestException("Receipt Amount cannot be zero or less than Zero...");
            SystemTransactions transaction = transRepo.findOne(tran.getTransNo());
            tran.setReceipt(receipt);
            tran.setTransaction(transaction);
            tran.setEndorsementNumber(transaction.getPolicy().getPolRevNo());
            tran.setRctType("INV");
            tran.setRctDC("C");
            transDtls.add(tran);
            totalAllocAmount = totalAllocAmount.add(tran.getRctAmount());
            ProductsDef product =tran.getPolicy().getProduct();
            if(product==null) throw new BadRequestException("Error getting policy product");
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            List<Long> subcodes = jdbcTemplate.query("SELECT ps_sub_code  FROM sys_brk_prod_subcls sbps  WHERE ps_pr_code  = ?", new Object[]{product.getProCode()}, new RowMapper<Long>() {
                @Override
                public Long mapRow(ResultSet resultSet, int i) throws SQLException {
                    return resultSet.getLong(1);
                }
            });
            if(subcodes.size()!=1){
                throw new BadRequestException("Error getting sub classes");
            }
            long subcode = subcodes.get(0);
           // receiptService.postReceiptAccount(receipt,systemTrans, subcode,tran.getRctAmount());
        }
        if (totalAllocAmount.compareTo(receipt.getReceiptAmount()) != 0) {
            throw new BadRequestException("Total Allocation Amount " + totalAllocAmount + " and Receipt amount "
                    + receipt.getReceiptAmount() + " doesn't tally....");
        }
        rctDetailsRepo.save(transDtls);
        receipt.setBranch(branchRepository.findOne(receipt.getBrnCode()));
        receipt.setCollectionAccount(collectionAcctsRepo.findOne(receipt.getPayId()));
        receipt.setReceiptUser(user);
        receipt.setReceiptTransDate(new Date());
        sequence.setLastNumber(seqNumber);
        sequence.setNextNumber(seqNumber + 1);
        if(receipt.getDirectReceipt()==null || "off".equalsIgnoreCase(receipt.getDirectReceipt()))
            receipt.setDirectReceipt("N");
        else if("on".equalsIgnoreCase(receipt.getDirectReceipt()))
            receipt.setDirectReceipt("Y");
        else
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
        receipt.setPrinted("Y");
        ReceiptTrans trans = receiptRepo.save(receipt);

        allocateReceipt(receipt,user);
        return trans.getReceiptId();
    }

    @Modifying
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class }, propagation = Propagation.REQUIRES_NEW)
    public void allocateReceipt( ReceiptTrans receipt, User user) throws BadRequestException {
        List<SystemTransactions> transactions = new ArrayList<>();
        SystemTrans transaction = new SystemTrans();
        transaction.setDoneDate(new Date());
        transaction.setDoneBy(user);
        transaction.setTransLevel("U");
        transaction.setTransCode("RC"); //A way to setup and look up for transaction transcode
        transaction.setTransAuthorised("N");
        systransRepo.save(transaction);
        Iterable<ReceiptTransDtls> rctDetails =receipt.getDetails();
        boolean directReceipt = receipt.getDirectReceipt()!=null && "Y".equalsIgnoreCase(receipt.getDirectReceipt());
        for (ReceiptTransDtls tran : rctDetails) {
            SystemTransactions debitTrans = tran.getTransaction();
            BigDecimal debitBalance = debitTrans.getBalance();
            SystemTransactions agentTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(debitTrans.getRefNo())
                    .and(QSystemTransactions.systemTransactions.clientType.eq("A").and(QSystemTransactions.systemTransactions.transType.eq("NBD")))
                    .and(QSystemTransactions.systemTransactions.transdc.eq("C")));
            BigDecimal receiptBalance = tran.getRctAmount();
//            Iterable<ReceiptSettlementDetails> settlementDetails = settlementRepo.findAll(QReceiptSettlementDetails.receiptSettlementDetails.debit.refNo.eq(debitTrans.getRefNo()));
            if(debitBalance.compareTo(BigDecimal.ZERO)==1){
                if(!debitTrans.getTransdc().equalsIgnoreCase("D")) throw new BadRequestException("Can only allocate against a debit transaction. Contact Admin on Trans "+debitTrans.getRefNo());
                BigDecimal receiptAmt = tran.getRctAmount().abs();
                BigDecimal settleAmt = BigDecimal.ZERO;

                double prorataAgBalance = receiptAmt.doubleValue()/debitTrans.getNetAmount().doubleValue();

                if(receiptAmt.compareTo(BigDecimal.ZERO)!=1) throw new BadRequestException("Receipt Allocation Amount cannot be zero or less than zero "+receiptAmt);
                if (receiptAmt.compareTo(debitBalance) == 1 || receiptAmt.compareTo(debitBalance) == 0) {
                    debitTrans.setBalance(BigDecimal.ZERO);
                    debitTrans.setSettleAmt( (debitTrans.getSettleAmt()!=null)? debitTrans.getSettleAmt().add(debitBalance.abs()):debitBalance.abs() );
                    receiptBalance = receiptAmt.subtract(debitBalance);
                    settleAmt = debitBalance.negate();
                    //prorataAgBalance = 1;
                }
                else {
                    if(debitTrans.getSettleAmt()==null){
                        debitTrans.setSettleAmt(receiptAmt.abs());
                    }else
                        debitTrans.setSettleAmt(debitTrans.getSettleAmt().add(receiptAmt.abs()));
                    debitTrans.setBalance(debitTrans.getBalance().subtract(receiptAmt.abs()));
                    receiptBalance = BigDecimal.ZERO;
                    settleAmt = receiptAmt;
                    //prorataAgBalance = receiptAmt.doubleValue()/debitTrans.getNetAmount().doubleValue();
                }

                //postReceiptAccount(tran,transaction);
//				System.out.println("Prorata..."+prorataAgBalance);
                SystemTransactions transact =  createReceiptTransaction(receipt.getReceiptNo(), tran.getRctAmount(), receiptBalance, receipt.getCollectionAccount().getCurrencies(), debitTrans.getClient(), debitTrans.getAgent(), debitTrans, transaction, tran.getNarration(),settleAmt,user);
                if(directReceipt) {
                    Currencies currencies = debitTrans.getCurrency();
                    BigDecimal agencyAllocAmt = agentTrans.getNetAmount().multiply(BigDecimal.valueOf(prorataAgBalance)).setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN);
                    BigDecimal commAmt = (agentTrans.getCommission().abs().subtract(agentTrans.getWhtx().abs())).multiply(BigDecimal.valueOf(prorataAgBalance)).setScale(currencies.getRoundOff(), BigDecimal.ROUND_HALF_EVEN);
                    agentTrans.setBalance(agentTrans.getBalance().add(agencyAllocAmt.abs()));
                    agentTrans.setSettleAmt((agentTrans.getSettleAmt()==null)?agencyAllocAmt.abs(): agentTrans.getSettleAmt().add(agencyAllocAmt.abs()));
                    transactions.add(agentTrans);
                    SystemTransactions commTrans =createCommissionTrans(commAmt,currencies,agentTrans.getAgent(),agentTrans,transaction,"Agent Commission Transaction",transact.getRefNo(),user);
                    createSettlements( commTrans, transact, agencyAllocAmt,user);
                }
                createSettlements( debitTrans, transact, settleAmt,user);
                if(debitTrans.getBalance().compareTo(BigDecimal.ZERO)==0)
                    if(tran.getTransaction().getPolicy().getSubAgentComm()!=null && tran.getTransaction().getPolicy().getSubAgentComm().compareTo(BigDecimal.ZERO)!=0){
                        PolicyTrans policy = tran.getTransaction().getPolicy();
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
                        trans.setUserAuth(user.getUsername());
                        trans.setWhtx(BigDecimal.ZERO);
                        trans.setTransaction(transaction);
                        trans.setPostedDate(new Date());
                        trans.setPostedUser(user);
                        trans.setAgent(policy.getSubAgent());
                        transactions.add(trans);


                    }
                transactions.add(debitTrans);
            }
            transRepo.save(transactions);
        }

    }


    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public SystemTransactions createReceiptTransaction(String refNo, BigDecimal receiptAmt, BigDecimal receiptbalance,
                                                       Currencies currency,ClientDef client,AccountDef accnt, SystemTransactions prevTrans,SystemTrans transaction,
                                                       String narration,BigDecimal settleAmt, User user){
        SystemTransactions trans = new SystemTransactions();
        trans.setAmount(receiptAmt.abs().multiply(sign("C")));
        trans.setAuthDate(new Date());
        trans.setAuthorised("Y");
        trans.setBalance(receiptbalance.abs().multiply(sign("C")));
        trans.setBranch(prevTrans.getBranch());
        trans.setClientType("C");
        trans.setControlAcc(prevTrans.getControlAcc());
        trans.setClient(client);
        trans.setCurrRate(new BigDecimal(1));
        trans.setCurrency(currency);
        trans.setNarrations(narration);
        trans.setNetAmount(receiptAmt.abs().multiply(sign("C")));
        trans.setSettleAmt(settleAmt.abs().multiply(sign("C")));
        trans.setOrigin("U");
        trans.setPolicy(prevTrans.getPolicy());
        trans.setRefNo(refNo);
        trans.setTransDate(new Date());
        trans.setTransdc("C");
        trans.setTransType("RC"); //Should not be hardcorded
        trans.setUserAuth(user.getUsername());
        trans.setWhtx(BigDecimal.ZERO);
        trans.setTransaction(transaction);
        trans.setPostedDate(new Date());
        trans.setPostedUser(user);
        SystemTransactions createdTrans =  transRepo.save(trans);
        return createdTrans;
    }


    @Modifying
    @Transactional(readOnly = false, rollbackFor = {
            BadRequestException.class }, propagation = Propagation.REQUIRED)
    public void createSettlements(SystemTransactions debit, SystemTransactions credit, BigDecimal amount,User user)
            throws BadRequestException {
        String refno = debit.getRefNo();
        SystemTransactions creditTrans = transRepo.findOne(QSystemTransactions.systemTransactions.refNo.eq(refno)
                .and(QSystemTransactions.systemTransactions.clientType.in("A"))
                .and(QSystemTransactions.systemTransactions.transType.in("NBD","APD")));
        if(creditTrans==null){
            throw new BadRequestException("Error getting insurance transactions for allocation");
        }
        ReceiptSettlementDetails settlement = new ReceiptSettlementDetails();
        settlement.setCredit(credit);
        settlement.setCreditRefNo(credit.getRefNo());
        settlement.setDebit(debit);
        settlement.setDebitRefNo(debit.getRefNo());
        settlement.setAllocatedAmt(amount.abs());
        settlement.setDrCr("C");
        settlement.setAllocDate(new Date());
        settlement.setClientCredit(creditTrans);
        settlement.setAlloUser(user);
        settlementRepo.save(settlement);
    }

    @Transactional(readOnly = false)
     SystemTransactions createCommissionTrans(BigDecimal commAmt,
                                                     Currencies currency,AccountDef accnt, SystemTransactions prevTrans,SystemTrans transaction,
                                                     String narration,String receipt,User user){
        SystemTransactions trans = new SystemTransactions();
        trans.setAmount(commAmt.abs());
        trans.setAuthDate(new Date());
        trans.setAuthorised("Y");
        trans.setBalance(commAmt.abs().multiply(sign("D")));
        trans.setBranch(prevTrans.getBranch());
        trans.setClientType("A");
        trans.setControlAcc(prevTrans.getControlAcc());
        trans.setOtherRef(receipt);
        trans.setAgent(accnt);
        trans.setCurrRate(new BigDecimal(1));
        trans.setCurrency(currency);
        trans.setNarrations(narration);
        trans.setNetAmount(commAmt.abs().multiply(sign("D")));
        trans.setSettleAmt(BigDecimal.ZERO);
        trans.setOrigin("U");
        trans.setPolicy(prevTrans.getPolicy());
        trans.setRefNo(prevTrans.getRefNo());
        trans.setTransDate(new Date());
        trans.setTransdc("D");
        trans.setTransType("COMM"); //Should not be hardcorded
        trans.setUserAuth(user.getUsername());
        trans.setWhtx(BigDecimal.ZERO);
        trans.setTransaction(transaction);
        trans.setPostedDate(new Date());
        trans.setPostedUser(user);
        SystemTransactions createdTrans =  transRepo.save(trans);
        return createdTrans;
    }

    private BigDecimal sign(String type){
        return ("C".equalsIgnoreCase(type)?BigDecimal.ONE.multiply(BigDecimal.valueOf(-1)):BigDecimal.ONE.multiply(BigDecimal.valueOf(1)));
    }

}
