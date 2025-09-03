package com.brokersystems.brokerapp.trans.service.impl;

import com.brokersystems.brokerapp.accounts.dtos.PayeesDTO;
import com.brokersystems.brokerapp.accounts.model.CoaSubAccounts;
import com.brokersystems.brokerapp.accounts.model.Payees;
import com.brokersystems.brokerapp.accounts.repository.CoaSubAccountsRepo;
import com.brokersystems.brokerapp.accounts.repository.PayeeAccountsRepo;
import com.brokersystems.brokerapp.accounts.repository.PayeesRepo;
import com.brokersystems.brokerapp.claims.model.ClaimPerilPayments;
import com.brokersystems.brokerapp.claims.repository.ClaimPerilPaymentsRepo;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.DateUtilities;
import com.brokersystems.brokerapp.server.utils.NumberToWordsUtils;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.setup.dto.BankAccountDTO;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.*;
import com.brokersystems.brokerapp.trans.dtos.ChequeTransDTO;
import com.brokersystems.brokerapp.trans.dtos.ChequeTransDtlsDTO;
import com.brokersystems.brokerapp.trans.dtos.PettyCashDtlsDTO;
import com.brokersystems.brokerapp.trans.dtos.RejectRequistionForm;
import com.brokersystems.brokerapp.trans.model.*;
import com.brokersystems.brokerapp.trans.repository.*;
import com.brokersystems.brokerapp.trans.service.PaymentService;
import com.mysema.query.types.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentModeRepo paymentModeRepo;
    @Autowired
    private SequenceRepository sequenceRepo;
    @Autowired
    private BankAccountsRepo bankAccountsRepo;
    @Autowired
    private UserUtils userUtils;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private ChequeTransRepo chequeTransRepo;
    @Autowired
    private CoaSubAccountsRepo subAccountsRepo;
    @Autowired
    private SystemTransRepo systemTransRepo;
    @Autowired
    private DateUtilities dateUtils;
    @Autowired
    private OrgBranchRepository branchRepository;
    @Autowired
    private ChequeTransDtlsRepo chequeTransDtlsRepo;
    @Autowired
    private PettyCashTransDtlsRepo pettyCashTransDtlsRepo;
    @Autowired
    private PayeesRepo payeesRepo;
    @Autowired
    private PayeeAccountsRepo payeeAccountsRepo;
    @Autowired
    private GlTransRepo glTransRepo;
    @Autowired
    private BankAccountsRepo accountsRepo;

    @Autowired
    private ClaimPerilPaymentsRepo perilPaymentsRepo;

    @Override
    @Transactional
    public void createRequistion(ChequeTransDTO chequeTransDTO) throws BadRequestException {
        if(chequeTransDTO.getPaymentModeId()==null){
            throw new BadRequestException("Please Select Payment Method for the Requistion");
        }
        if(chequeTransDTO.getInvoiceNo()==null){
            throw new BadRequestException("Invoice Number is mandatory...");
        }
        if(chequeTransDTO.getInvoiceDate()==null){
            throw new BadRequestException("Invoice Date is Mandatory...");
        }
        if(chequeTransDTO.getInvoiceDate().after(new Date())){
            throw new BadRequestException("Invoice Date cannot be in future...");
        }
        if(chequeTransDTO.getBranchCode()==null){
            throw new BadRequestException("Please Select Transaction Branch...");
        }
        final PaymentModes paymentModes = paymentModeRepo.findOne(chequeTransDTO.getPaymentModeId());
        if(paymentModes==null){
            throw new BadRequestException("Please Select a Valid Payment Method for the Requisition");
        }
        if(chequeTransDTO.getAmount()==null || chequeTransDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0){
            throw new BadRequestException("Please Enter a valid requisition amount..");
        }



        if(paymentModes.getPmMinValue()!=null){
            if(chequeTransDTO.getAmount().compareTo(paymentModes.getPmMinValue()) < 0){
                throw new BadRequestException("The amount is too low to support this payment method");
            }
        }

        if(paymentModes.getPmMaxValue()!=null){
            if(chequeTransDTO.getAmount().compareTo(paymentModes.getPmMaxValue()) > 0){
                throw new BadRequestException("The amount is too high to support this payment method");
            }
        }

        if(chequeTransDTO.getPayee()==null){
            throw new BadRequestException("Please Select a Valid Payee...");
        }

        if(chequeTransDTO.getBankActCode()==null){
            throw new BadRequestException("Select Bank Account to continue..");
        }
        final BankAccounts bankAccounts = bankAccountsRepo.findOne(chequeTransDTO.getBankActCode());

        if(bankAccounts==null){
            throw new BadRequestException("Select a Valid Bank Account to continue..");
        }

        final String acctStatus = bankAccounts.getStatus();
        if(acctStatus==null || !acctStatus.equalsIgnoreCase("A")){
            throw new BadRequestException("Select a Valid Bank Account to continue..");
        }

        if(chequeTransDTO.getPaymentType()==null){
            throw new BadRequestException("Please select a Payment Type to continue...");
        }
        User postedUser = null;
        if(chequeTransDTO.getSourcePostedUser()!=null){
            postedUser = userRepository.findOne(chequeTransDTO.getSourcePostedUser());
        }

        if(chequeTransDTO.getCurId()==null){
            throw new BadRequestException("Select A Valid Requistion Currency");
        }

        if(chequeTransRepo.countInvoiceNoExists(chequeTransDTO.getPayee(),StringUtils.trim(chequeTransDTO.getInvoiceNo())) >0){
            throw new BadRequestException("Requisition of Payee with Similar Invoice number has already been posted in the system...");
        }

        Currencies currencies = currencyRepository.findOne(chequeTransDTO.getCurId());

        if(currencies==null){
            throw new BadRequestException("Select A Valid Requistion Currency");
        }

        if(chequeTransDTO.getPaymentType().equalsIgnoreCase("GL")){
            if(chequeTransDTO.getGlTrans()==null || chequeTransDTO.getGlTrans().isEmpty()){
                throw new BadRequestException("GL Requistion must have GL Transactions");
            }
        }
        else  if(chequeTransDTO.getPaymentType().equalsIgnoreCase("PC")){
            if(chequeTransDTO.getPettyCashTrans()==null || chequeTransDTO.getPettyCashTrans().isEmpty()){
                throw new BadRequestException("Petty Cash Requisition must have Petty Cash Transactions");
            }
        }
        for(ChequeTransDtlsDTO chequeTransDtlsDTO: chequeTransDTO.getGlTrans()) {
            if (chequeTransDtlsDTO.getNarrative() == null) {
                throw new BadRequestException("Please provide Narration for all GL Transactions...");
            }
            if (chequeTransDtlsDTO.getTransAmount() == null) {
                throw new BadRequestException("Provide amount for transaction..."+chequeTransDtlsDTO.getNarrative());
            }
            if(chequeTransDtlsDTO.getGlId()==null){
                throw new BadRequestException("Select A Valid GL Account for transaction "+chequeTransDtlsDTO.getNarrative());
            }
        }

        if(chequeTransDTO.getPettyCashTrans()!=null) {
            for (PettyCashDtlsDTO chequeTransDtlsDTO : chequeTransDTO.getPettyCashTrans()) {
                if (chequeTransDtlsDTO.getNarrative() == null) {
                    throw new BadRequestException("Please provide Narration for all GL Transactions...");
                }
                if (chequeTransDtlsDTO.getTransAmount() == null) {
                    throw new BadRequestException("Provide amount for transaction..." + chequeTransDtlsDTO.getNarrative());
                }
                if (chequeTransDtlsDTO.getBankAcctId() == null) {
                    throw new BadRequestException("Select A Valid Bank Account for transaction " + chequeTransDtlsDTO.getNarrative());
                }
            }
        }

        if(!chequeTransDTO.getGlTrans().isEmpty()) {
             BigDecimal totalDebit = chequeTransDTO.getGlTrans().stream().
                    filter(a -> a.getDrcr().equalsIgnoreCase("D")).
                    map(ChequeTransDtlsDTO::getTransAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

            final BigDecimal totalCredit = chequeTransDTO.getGlTrans().stream().
                    filter(a -> a.getDrcr().equalsIgnoreCase("C")).
                    map(ChequeTransDtlsDTO::getTransAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

            totalDebit = totalDebit.subtract(totalCredit);

            if (totalDebit.compareTo(chequeTransDTO.getAmount()) != 0) {
                throw new BadRequestException("Requistion Amount and Debit amounts are not equal "+totalDebit+" cheq amount "+chequeTransDTO.getAmount())   ;
            }
        }
        if(chequeTransDTO.getPettyCashTrans()!=null) {
            if (!chequeTransDTO.getPettyCashTrans().isEmpty()) {
                BigDecimal totalDebit = chequeTransDTO.getPettyCashTrans().stream().
                        filter(a -> a.getDrcr().equalsIgnoreCase("D")).
                        map(PettyCashDtlsDTO::getTransAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

                final BigDecimal totalCredit = chequeTransDTO.getPettyCashTrans().stream().
                        filter(a -> a.getDrcr().equalsIgnoreCase("C")).
                        map(PettyCashDtlsDTO::getTransAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

                totalDebit = totalDebit.subtract(totalCredit);

                if (totalDebit.compareTo(chequeTransDTO.getAmount()) != 0) {
                    throw new BadRequestException("Credits and Debits must match");
                }
            }
        }
        if(payeeAccountsRepo.countActiveAccount(chequeTransDTO.getPayee()) !=1){
            throw new BadRequestException("There is no active or more than one active account for the selected payee...");
        }

        final Payees payees = payeesRepo.findOne(chequeTransDTO.getPayee());
        final Long acctId = payeeAccountsRepo.findActiveAccounts(chequeTransDTO.getPayee());
        if(acctId==null){
            throw new BadRequestException("Unable to get Payee Active account...");
        }
        final ChequeTrans chequeTrans = new ChequeTrans();
        if(chequeTransDTO.getOriginType()==null){
            Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("RQ");
            if (sequenceRepo.count(seqPredicate) == 0)
                throw new BadRequestException("Sequence for Payment Requisitions has not been setup");
            SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
            Long seqNumber = sequence.getNextNumber();
            final String refNo = sequence.getSeqPrefix() + String.format("%05d", seqNumber);
            sequence.setLastNumber(seqNumber);
            sequence.setNextNumber(seqNumber + 1);
            sequenceRepo.save(sequence);
            chequeTrans.setSource("FMS");
            chequeTrans.setSourceType("FMS");
            chequeTrans.setTransRef(refNo);
        }
        else{
            chequeTrans.setSource(chequeTransDTO.getOrigin());
            chequeTrans.setSourceType(chequeTransDTO.getOriginType());
            chequeTrans.setTransRef(chequeTransDTO.getRefNo());
        }
        chequeTrans.setTransRefDate(new Date());
        chequeTrans.setBankAccounts(bankAccounts);
        chequeTrans.setPaymentModes(paymentModes);
        chequeTrans.setPayee(payees);
        chequeTrans.setPayeeAccounts(payeeAccountsRepo.findOne(acctId));
        chequeTrans.setTransAmount(chequeTransDTO.getAmount());
        chequeTrans.setPaymentType(chequeTransDTO.getPaymentType());
        chequeTrans.setNarrative(chequeTransDTO.getNarration());
        chequeTrans.setChqStatus("AS");
        chequeTrans.setBranch(branchRepository.findOne(chequeTransDTO.getBranchCode()));
        chequeTrans.setInvoiceNo(chequeTransDTO.getInvoiceNo());
        chequeTrans.setRaisedBy(userUtils.getCurrentUser());
        chequeTrans.setRaisedDate(new Date());
        chequeTrans.setPostedUser(postedUser);
        chequeTrans.setPostedDate(chequeTransDTO.getSourcePostedDate());
        chequeTrans.setCurrency(currencies);
        chequeTrans.setSource(chequeTransDTO.getSource());
        ChequeTrans savedCheques = chequeTransRepo.save(chequeTrans);
        final List<ChequeTransDtls> chequeTransDtlsList = new ArrayList<>();
        for(ChequeTransDtlsDTO chequeTransDtlsDTO: chequeTransDTO.getGlTrans()){
            final ChequeTransDtls chequeTransDtls = new ChequeTransDtls();
            chequeTransDtls.setTransAmount(chequeTransDtlsDTO.getTransAmount());
            chequeTransDtls.setNarrative(chequeTransDtlsDTO.getNarrative());
            chequeTransDtls.setDrcr(chequeTransDtlsDTO.getDrcr());
            chequeTransDtls.setChequeTrans(savedCheques);
            chequeTransDtls.setGlAcc(subAccountsRepo.findOne(chequeTransDtlsDTO.getGlId()));
            if(chequeTransDtlsDTO.getBranchCode()!=null){
                chequeTransDtls.setBranch(branchRepository.findOne(chequeTransDtlsDTO.getBranchCode()));
            }
            chequeTransDtlsList.add(chequeTransDtls);
        }
        chequeTransDtlsRepo.save(chequeTransDtlsList);
        if(chequeTransDTO.getPettyCashTrans()!=null) {
            final List<PettyCashDtls> pettyCashDtlsList = new ArrayList<>();
            for (PettyCashDtlsDTO pettyCashDtlsDTO : chequeTransDTO.getPettyCashTrans()) {
                final PettyCashDtls pettyCashDtls = new PettyCashDtls();
                pettyCashDtls.setDrcr(pettyCashDtlsDTO.getDrcr());
                pettyCashDtls.setNarrative(pettyCashDtlsDTO.getNarrative());
                pettyCashDtls.setTransAmount(pettyCashDtlsDTO.getTransAmount());
                pettyCashDtls.setChequeTrans(chequeTrans);
                pettyCashDtls.setBankAccounts(bankAccountsRepo.findOne(pettyCashDtlsDTO.getBankAcctId()));
                pettyCashDtlsList.add(pettyCashDtls);
            }
            pettyCashTransDtlsRepo.save(pettyCashDtlsList);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PayeesDTO> findPayeeAccounts(String searchValue, Pageable pageable) {
        if(searchValue==null) searchValue="%%";
        else searchValue = "%"+searchValue+"%";
        List<Object[]> payees = payeesRepo.searchAllPayesLov(searchValue, pageable.getPageNumber(), pageable.getPageSize());
        long rowCount = 0L;
        if(!payees.isEmpty()) rowCount = ((BigInteger)payees.get(0)[2]).intValue();
        List<PayeesDTO> dtoList = new ArrayList<>();
        for(Object[] payee:payees){
            PayeesDTO payeesDTO = new PayeesDTO();
            payeesDTO.setPayId(((BigInteger)payee[0]).longValue());
            payeesDTO.setFullName((String) payee[1]);
            dtoList.add(payeesDTO);
        }
        return new PageImpl<>(dtoList,pageable, rowCount);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BankAccountDTO> findAllBankAccountsLov(String searchValue, Pageable pageable) {
        if(searchValue==null) searchValue="%%";
        else searchValue = "%"+searchValue+"%";
        List<Object[]> accounts = accountsRepo.findBankAccntsLov(searchValue, pageable.getPageNumber(), pageable.getPageSize());
        long rowCount = 0L;
        if(!accounts.isEmpty()) rowCount = ((BigInteger)accounts.get(0)[2]).intValue();
        List<BankAccountDTO> bankAccountDTOList = new ArrayList<>();
        for(Object[] acct:accounts){
            BankAccountDTO accountDTO = new BankAccountDTO();
            accountDTO.setBaId(((BigInteger)acct[0]).longValue());
            accountDTO.setBankAcctName((String) acct[1]);
            bankAccountDTOList.add(accountDTO);
        }
        return new PageImpl<>(bankAccountDTOList,pageable, rowCount);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<ChequeTransDTO> findRequistions(DataTablesRequest request, String type) {
        final String search = ( request.getSearch()!=null && request.getSearch().getValue()!=null)?"%"+request.getSearch().getValue().toLowerCase()+"%":"%%";
        List<Object[]> requistions = chequeTransRepo.findRequstionTransactions(search.toLowerCase(),type, request.getPageNumber(), request.getPageSize());
        final List<ChequeTransDTO> transDTOList = new ArrayList<>();
        long rowCount = 0l;
        if(!requistions.isEmpty()) rowCount = ((BigInteger)requistions.get(0)[15]).intValue();
        for(Object[] req:requistions){
            ChequeTransDTO chequeTransDTO = new ChequeTransDTO();
            chequeTransDTO.setCtNo(((BigInteger)req[0]).longValue());
            chequeTransDTO.setInvoiceNo((String) req[1]);
            chequeTransDTO.setNarration((String) req[2]);
            chequeTransDTO.setPaymentType((String) req[3]);
            chequeTransDTO.setRequistionDate((Date) req[4]);
            chequeTransDTO.setRefNo((String) req[5]);
            chequeTransDTO.setAmount((BigDecimal) req[7]);
            chequeTransDTO.setBankAcctName((String) req[8]);
            chequeTransDTO.setCurrency((String) req[9]);
            chequeTransDTO.setBranch((String) req[10]);
            chequeTransDTO.setRaisedUser((String) req[11]);
            chequeTransDTO.setPaymentMethod((String) req[12]);
            chequeTransDTO.setPayeeName((String) req[13]);
            chequeTransDTO.setAccountNo((String) req[14]);
            transDTOList.add(chequeTransDTO);
        }
        Page<ChequeTransDTO>  page = new PageImpl<>(transDTOList,request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<ChequeTransDtlsDTO> findRequistionDetails(DataTablesRequest request, Long reqNo) {
        final String search = ( request.getSearch()!=null && request.getSearch().getValue()!=null)?"%"+request.getSearch().getValue().toLowerCase()+"%":"%%";
        List<Object[]> requistionDetails = chequeTransDtlsRepo.searchAllRequistionDetails(reqNo, search.toLowerCase(), request.getPageNumber(), request.getPageSize());
        final List<ChequeTransDtlsDTO> transDTOList = new ArrayList<>();
        long rowCount = 0l;
        if(!requistionDetails.isEmpty()) rowCount = ((BigInteger)requistionDetails.get(0)[7]).intValue();
        for(Object[] req:requistionDetails) {
            ChequeTransDtlsDTO chequeTransDtlsDTO = new ChequeTransDtlsDTO();
            chequeTransDtlsDTO.setCtdNo(((BigInteger) req[0]).longValue());
            chequeTransDtlsDTO.setDrcr((String) req[1]);
            chequeTransDtlsDTO.setNarrative((String) req[2]);
            chequeTransDtlsDTO.setTransAmount((BigDecimal) req[3]);
            chequeTransDtlsDTO.setGlAcctNo((String) req[4]);
            chequeTransDtlsDTO.setGlAcctName((String) req[5]);
            chequeTransDtlsDTO.setBranchName((String) req[6]);
            transDTOList.add(chequeTransDtlsDTO);
        }
        Page<ChequeTransDtlsDTO>  page = new PageImpl<>(transDTOList,request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional
    public void rejectRequistion(RejectRequistionForm rejectRequistionForm) throws BadRequestException {
        if(rejectRequistionForm.getReason()==null || rejectRequistionForm.getReason().length() < 10){
            throw new BadRequestException("Please provide reason to continue...");
        }
        final ChequeTrans chequeTrans = chequeTransRepo.findOne(rejectRequistionForm.getReqId());
        if(chequeTrans==null){
            throw new BadRequestException("Select A Valid Requistion to Reject...");
        }
        chequeTrans.setRemarks(rejectRequistionForm.getReason());
        chequeTrans.setChqStatus("WS");
        chequeTransRepo.save(chequeTrans);
    }

    @Override
    @Transactional
    public void rejectApprovedRequistion(RejectRequistionForm rejectRequistionForm) throws BadRequestException {
        if(rejectRequistionForm.getReason()==null || rejectRequistionForm.getReason().length() < 10){
            throw new BadRequestException("Please provide reason to continue...");
        }
        final ChequeTrans chequeTrans = chequeTransRepo.findOne(rejectRequistionForm.getReqId());
        if(chequeTrans==null){
            throw new BadRequestException("Select A Valid Requistion to Reject...");
        }
        chequeTrans.setRemarks(rejectRequistionForm.getReason());
        chequeTrans.setChqStatus("AS");
        chequeTransRepo.save(chequeTrans);
    }

    @Override
    @Transactional
    public void authRequistion(Long reqId) throws BadRequestException {
        final ChequeTrans chequeTrans = chequeTransRepo.findOne(reqId);
        final Long acctId = chequeTransRepo.getBankAcctDetails(reqId);
        final BankAccounts bankAccounts = bankAccountsRepo.findOne(acctId);
        final Long paymentMode = chequeTransRepo.getPayMethod(reqId);
        final PaymentModes paymentModes = paymentModeRepo.findOne(paymentMode);
        if(chequeTrans==null){
            throw new BadRequestException("Select A Valid Requisition to Authorise...");
        }
        if(bankAccounts==null){
            throw new BadRequestException("The requistion does not have a valid bank account");
        }
        try {
            if(paymentModes.getSupportsCheque()==null || !paymentModes.getSupportsCheque().equalsIgnoreCase("Y")){
                final Long chequeNo = Long.parseLong(bankAccounts.getCurrentCheckNo());
                chequeTrans.setChequeNumber(String.format("%05d", chequeNo));
            }
        }
        catch (Exception ex){
            throw new BadRequestException("Check your set up. The bank account does not have a valid cheque number");
        }
            chequeTrans.setChqStatus("TBP");
            chequeTrans.setChequeDate(new Date());
            float amount = chequeTrans.getTransAmount().floatValue();
            int figure = (int) Math.floor(amount);
            int cent = (int) Math.floor((amount - figure) * 100.0f);
            String words = "";
            if (cent > 0) {
                words = NumberToWordsUtils.convert(figure) + " and " + NumberToWordsUtils.convert(cent) + " cents";
            } else {
                words = NumberToWordsUtils.convert(figure);
            }
            chequeTrans.setChequeWords(words);
            chequeTransRepo.save(chequeTrans);
    }

    @Override
    @Transactional
    public void updateRequistion(Long reqId) throws BadRequestException {
        final Long bankId = chequeTransRepo.getBankAcctDetails(reqId);
        final ChequeTrans chequeTrans = chequeTransRepo.findOne(reqId);
        if(bankId==null) {
            throw new BadRequestException("Unable to get Bank Account Details for the requisition");
        }
        final Long glId = bankAccountsRepo.getGlAcct(bankId);
        if(glId==null){
            throw new BadRequestException("unable to get gl Account for the requisition");
        }
        List<Object[]> transDetails = chequeTransRepo.getTransactionDetails(reqId);
        Currencies currencies = null;
        OrgBranch branch = null;
        for(Object[] req:transDetails) {
            final Long branchid = ((BigDecimal) req[0]).longValue();
            branch = branchRepository.findOne(branchid);
            final Long currencyId = ((BigDecimal) req[1]).longValue();
            currencies = currencyRepository.findOne(currencyId);
        }
        SystemTrans transaction = new SystemTrans();
        transaction.setDoneDate(new Date());
        transaction.setDoneBy(userUtils.getCurrentUser());
        transaction.setTransLevel("U");
        transaction.setTransCode("RCT"); //A way to setup and look up for transaction transcode
        transaction.setTransAuthorised("N");
        systemTransRepo.save(transaction);
        List<GlTransactions> glTransactions = new ArrayList<>();
        final CoaSubAccounts debitAccount = subAccountsRepo.findOne(glId);
        GlTransactions debit = new GlTransactions();
        debit.setAmount(chequeTrans.getTransAmount().abs());
        debit.setAuthDate(new Date());
        debit.setbCuramount(chequeTrans.getTransAmount().abs());
        debit.setBranch(branch);
        debit.setCurrency(currencies);
        debit.setGlAcc(debitAccount);
        debit.setGldc("C");
        debit.setTransaction(transaction);
        debit.setTransLevel("P");
        debit.setTrntCode("PYMT");
        debit.setGlYear(dateUtils.getUwYear(new Date()));
        debit.setGlMonth(dateUtils.getMonth(new Date()));
        debit.setNarration(String.format("Posting Payment for Requisition No: %s", chequeTrans.getTransRef()));
        glTransactions.add(debit);

        List<Object[]> requistionDetails = chequeTransDtlsRepo.searchRequistionDetails(reqId);
        for(Object[] req:requistionDetails) {
            final BigDecimal transAmount = (BigDecimal) req[3];
            final Long branchID = ((BigDecimal) req[6]).longValue();
            final Long acctId = ((BigDecimal) req[4]).longValue();
            final CoaSubAccounts creditAccount = subAccountsRepo.findOne(acctId);
            final OrgBranch orgBranch = branchRepository.findOne(branchID);
            GlTransactions credit = new GlTransactions();
            credit.setAmount(transAmount.abs());
            credit.setAuthDate(new Date());
            credit.setbCuramount(transAmount.abs());
            credit.setBranch(orgBranch);
            credit.setCurrency(currencies);
            credit.setGlAcc(creditAccount);
            credit.setGldc((String) req[1]);
            credit.setTransaction(transaction);
            credit.setTransLevel("P");
            credit.setTrntCode("PYMTS");
            credit.setGlYear(dateUtils.getUwYear(new Date()));
            credit.setGlMonth(dateUtils.getMonth(new Date()));
            credit.setNarration(String.format("Posting Payment for Requisition No: %s", chequeTrans.getTransRef()));
            glTransactions.add(credit);
        }
        glTransRepo.save(glTransactions);
        if(chequeTrans.getSourceType()!=null && chequeTrans.getSourceType().equalsIgnoreCase("C")){
            ClaimPerilPayments perilPayments = perilPaymentsRepo.findOne(Long.parseLong(chequeTrans.getTransRef()));
            if(perilPayments==null){
                throw new BadRequestException("Error getting claim payment to update...");
            }
            perilPayments.setAuthStatus("Y");
            perilPaymentsRepo.save(perilPayments);
        }

        chequeTrans.setChqStatus("UPS");
        chequeTransRepo.save(chequeTrans);

    }
}