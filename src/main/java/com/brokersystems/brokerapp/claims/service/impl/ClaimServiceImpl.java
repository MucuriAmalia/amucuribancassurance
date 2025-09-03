package com.brokersystems.brokerapp.claims.service.impl;

import com.brokersystems.brokerapp.accounts.repository.BankBranchRepo;
import com.brokersystems.brokerapp.accounts.repository.PayeeAccountsRepo;
import com.brokersystems.brokerapp.accounts.repository.PayeesRepo;
import com.brokersystems.brokerapp.claims.dtos.*;
import com.brokersystems.brokerapp.claims.exception.ClaimException;
import com.brokersystems.brokerapp.claims.model.*;
import com.brokersystems.brokerapp.claims.repository.*;
import com.brokersystems.brokerapp.claims.service.ClaimService;
import com.brokersystems.brokerapp.enums.RevenueItems;
import com.brokersystems.brokerapp.medical.repository.ServiceProviderContractRepo;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.*;
import com.brokersystems.brokerapp.setup.dto.PaymentModesDTO;
import com.brokersystems.brokerapp.setup.dto.UserDTO;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.*;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.brokersystems.brokerapp.trans.dtos.ChequeTransDTO;
import com.brokersystems.brokerapp.trans.dtos.ChequeTransDtlsDTO;
import com.brokersystems.brokerapp.trans.repository.GlTransRepo;
import com.brokersystems.brokerapp.trans.repository.SystemTransRepo;
import com.brokersystems.brokerapp.trans.service.AccountsUtilities;
import com.brokersystems.brokerapp.trans.service.PaymentService;
import com.brokersystems.brokerapp.users.dto.MakerCheckDTO;
import com.brokersystems.brokerapp.users.model.MakerChecker;
import com.brokersystems.brokerapp.users.model.QMakerChecker;
import com.brokersystems.brokerapp.users.repository.MakerCheckerRepo;
import com.brokersystems.brokerapp.users.service.MakerCheckerService;
import com.brokersystems.brokerapp.uw.model.*;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import com.brokersystems.brokerapp.uw.repository.RiskTransRepo;
import com.brokersystems.brokerapp.uw.repository.SectionTransRepo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.BooleanExpression;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by peter on 3/5/2017.
 */
@Service
@Slf4j
public class ClaimServiceImpl implements ClaimService {

    @Autowired
    private RiskTransRepo riskTransRepo;

    @Autowired
    private PaymentModeRepo paymentModeRepo;

    @Autowired
    private PolicyTransRepo policyRepo;

    @Autowired
    private ClmStatusRepo clmStatusRepo;

    @Autowired
    private ClaimantDefRepo claimantDefRepo;

    @Autowired
    private OccupationRepo occupRepo;

    @Autowired
    private ValidatorUtils validator;

    @Autowired
    private SectionTransRepo sectionTransRepo;

    @Autowired
    private BinderSectPerilsRepo binderSectPerilsRepo;

    @Autowired
    private DateUtilities dateUtilities;

    @Autowired
    private SequenceRepository sequenceRepo;

    @Autowired
    private ClaimsBookingRepo claimsBookingRepo;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private ClaimClaimantsRepo claimClaimantsRepo;

    @Autowired
    private SubclassReqDocRepo requiredDocsRepo;

    @Autowired
    private ClaimRequiredDocsRepo claimRequiredDocsRepo;

    @Autowired
    private MakerCheckerRepo makerCheckerRepo;

    @Autowired
    private PayeesRepo payeesRepo;
    @Autowired
    private PayeeAccountsRepo payeeAccountsRepo;
    @Autowired
    private BankBranchRepo bankBranchRepo;

    @Autowired
    private BankAccountsRepo bankAccountsRepo;
    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private ClaimPerilsRepo claimPerilsRepo;

    @Autowired
    private ClaimRevisionsRepo claimRevisionsRepo;

    @Autowired
    private ClaimRevisionTransRepo claimRevisionTransRepo;

    @Autowired
    private ClaimUploadRepo uploadRepo;

    @Autowired
    private ClmActivitiesRepo activitiesRepo;

    @Autowired
    private PolicyTransRepo transRepo;

    @Autowired
    private SystemTransRepo systemTransRepo;

    @Autowired
    private ParamService paramService;

    @Autowired
    private TemplateMerger templateMerger;

    @Autowired
    private ServiceProviderContractRepo serviceProviderContractRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    ClaimPerilPaymentsRepo perilPaymentsRepo;

    @Autowired
    GIBServiceProviderTypesRepo serviceProviderTypesRepo;

    @Autowired
    ServiceProviderRepo serviceProviderRepo;

    @Autowired
    ClaimStatusesRepo claimStatusesRepo;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private AccountsUtilities accountsUtilities;

    @Autowired
    private GlTransRepo glTransRepo;
    @Autowired
    private DateUtilities dateUtils;
    @Autowired
    private ClaimPaymentsRepo claimPaymentsRepo;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private ClaimPaymentDetailsRepo paymentDetailsRepo;
    @Autowired
    private ClaimServiceProviderRepo claimServiceProviderRepo;
    @Autowired
    private MakerCheckerService makerCheckerService;

    @Override
    @Transactional(readOnly = true)
    public Page<ClaimRisksDTO> findRisksToClaim(String searchValue, Date lossDate, Pageable pageable) {
        if(searchValue==null) searchValue="%%";
        else searchValue = "%"+searchValue+"%";
        //List<Object[]> risksTrans = riskTransRepo.findClaimRisks(lossDate,searchValue, pageable.getPageNumber(), pageable.getPageSize());
        List<Object[]> risksTrans = riskTransRepo.findClaimRisks(searchValue, pageable.getPageNumber(), pageable.getPageSize());
        long rowCount = 0L;
        if(!risksTrans.isEmpty()) rowCount = ((BigInteger)risksTrans.get(0)[7]).intValue();
        List<ClaimRisksDTO> dtoList = new ArrayList<>();
        for(Object[] risk:risksTrans){
            ClaimRisksDTO risksDTO = ClaimRisksDTO.instance(((BigInteger)risk[0]).longValue(),(String)risk[2],(String) risk[1],(String) risk[3],
                    ((BigInteger)risk[5]).longValue(),((BigInteger)risk[4]).longValue(),((BigInteger)risk[6]).longValue());
            dtoList.add(risksDTO);
        }
        return new PageImpl<>(dtoList,pageable, rowCount);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<ClmCausations> findClaimStatuses(String searchValue, Pageable pageable) {
            if(searchValue==null) searchValue="";
            Predicate pred = QClmCausations.clmCausations.activityDesc.containsIgnoreCase(searchValue);
            return clmStatusRepo.findAll(pred,pageable);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void defineClaimant(ClaimantsDef claimantsDef) {
        claimantsDef.setCreatedDate(new Date());
        claimantsDef.setCreatedUser(userUtils.getCurrentUser());
        claimantDefRepo.save(claimantsDef);
    }



    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void capturePerilPayment(ClaimPerilPayments perilPayments) throws BadRequestException {
        if(perilPayments.getClmPymntAmount()==null || perilPayments.getClmPymntAmount().compareTo(BigDecimal.ZERO) <=0)
            throw new BadRequestException("Payment Amount cannot be zero or negative");
        ClaimPerils claimPeril = claimPerilsRepo.findOne(QClaimPerils.claimPerils.clmPerilId.eq(perilPayments.getClaimPerils().getClmPerilId()));

        ClaimClaimants claimant=claimPeril.getClmClaimant();
        claimPeril.setClaimPaidAmount((claimPeril.getClaimPaidAmount()!=null)?claimPeril.getClaimPaidAmount().add(perilPayments.getClmPymntAmount()):perilPayments.getClmPymntAmount());
        claimant.setClaimPaidAmount((claimant.getClaimPaidAmount()!=null)?claimant.getClaimPaidAmount().add(perilPayments.getClmPymntAmount()):perilPayments.getClmPymntAmount());
        claimClaimantsRepo.save(claimant);
        claimPerilsRepo.save(claimPeril);
        perilPayments.setCaptureDate(new Date());
        perilPayments.setCapturedBy(userUtils.getCurrentUser());
        perilPaymentsRepo.save(perilPayments);
        BigDecimal ost = (claimRevisionsRepo.getTotalRevisions(claimPeril.getClaimBookings().getClmId(),claimPeril.getClmPerilId())).
                subtract(claimRevisionsRepo.getTotalPayments(claimPeril.getClaimBookings().getClmId()));
        System.out.println("Outstanding reserve "+ost);
        if(ost.compareTo(BigDecimal.ZERO) <0){
            throw new BadRequestException("The payment will result in negative reserve. Cannot continue");
        }
 }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void captureClaimPerils(ClaimPerils claimPeril) throws BadRequestException {

        if (claimPeril.getClmPerilId()!=null){
            ClaimPerils prevClaimPeril = claimPerilsRepo.findOne(claimPeril.getClmPerilId());
            Iterable<ClaimRevisionTrans> revisions = claimRevisionTransRepo.findAll(QClaimRevisionTrans.claimRevisionTrans.claimPeril.clmPerilId.eq(claimPeril.getClmPerilId()));

            ClaimRevisions claimRevisions = null;
            for(ClaimRevisionTrans revisionTrans:revisions){
                ClaimRevisions revisions1 = revisionTrans.getClaimRevision();
                if(!revisions1.getAuthStatus().equalsIgnoreCase("A")){
                    claimRevisions = revisions1;
                    break;
                }
            }



            if(claimRevisions!=null && claimRevisions.getRevisionId()!=null){
                claimRevisions.setRevAmount(claimPeril.getReserve());
                claimRevisions.setCreatedUser(userUtils.getCurrentUser());
                claimRevisionsRepo.save(claimRevisions);
                revisions.forEach(a -> {
                    a.setAmount(claimPeril.getReserve());
                    claimRevisionTransRepo.save(a);
                });
            }
            else {
                claimRevisions = new ClaimRevisions();
                claimRevisions.setClaimBookings(prevClaimPeril.getClaimBookings());
                claimRevisions.setTransType("LR");
                claimRevisions.setCreatedUser(userUtils.getCurrentUser());
                claimRevisions.setRevDate(new Date());
                claimRevisions.setRevAmount(claimPeril.getReserve());
                claimRevisions.setAuthStatus("D");
                ClaimRevisions savedRevision = claimRevisionsRepo.save(claimRevisions);
                ClaimRevisionTrans revisionTrans = new ClaimRevisionTrans();
                revisionTrans.setAmount(claimPeril.getReserve());
                revisionTrans.setClaimPeril(prevClaimPeril);
                revisionTrans.setClaimRevision(savedRevision);
                revisionTrans.setType("LR");
                claimRevisionTransRepo.save(revisionTrans);
            }
            BigDecimal ost = (claimRevisionsRepo.getTotalRevisionsUnauth(prevClaimPeril.getClaimBookings().getClmId(),prevClaimPeril.getClmPerilId())).
                    subtract(claimRevisionsRepo.getTotalPayments(prevClaimPeril.getClaimBookings().getClmId()));
            System.out.println("Outstanding reserve "+ost);
            if(ost.compareTo(BigDecimal.ZERO) <0){
                throw new BadRequestException("The payment will result in negative reserve. Cannot continue");
            }

        }else {
            throw  new BadRequestException("Select a peril to edit");
        }

    }


//    @Override
//    public Page<ClaimantsDTO> findAllClaimants(String searchValue, Pageable pageable) {
//        final String search = (searchValue!=null)?"%"+searchValue+"%":"%%";
//        List<Object[]> claimants = claimantDefRepo.findClmants(search.toLowerCase(),pageable.getPageNumber(), pageable.getPageSize());
//        final List<ClaimantsDTO> claimantsDTOList = new ArrayList<>();
//        long rowCount = 0L;
//        if(!claimants.isEmpty()) rowCount = ((BigInteger)claimants.get(0)[10]).intValue();
//        for(Object[] claimant:claimants){
//            ClaimantsDTO claimantsDTO = new ClaimantsDTO();
//            claimantsDTO.setClaimantId(((BigInteger)claimant[0]).longValue());
//            claimantsDTO.setOtherNames((String)claimant[4]);
//            claimantsDTO.setSurname((String)claimant[5]);
//            claimantsDTOList.add(claimantsDTO);
//        }
//        return new PageImpl<>(claimantsDTOList,pageable,rowCount);
//    }

    @Override
    public Page<ClaimantsDTO> findAllClaimants(String searchValue, Pageable pageable) {
        final String search = (searchValue != null) ? "%" + searchValue + "%" : "%%";
        List<Object[]> claimants = claimantDefRepo.findClmants(search.toLowerCase(), pageable.getPageNumber(), pageable.getPageSize());
        final List<ClaimantsDTO> claimantsDTOList = new ArrayList<>();
        long rowCount = 0L;
        if (!claimants.isEmpty()) {
            rowCount = parseToLong(claimants.get(0)[11]); // Use index 11 for total_rows
        }
        for (Object[] claimant : claimants) {
            ClaimantsDTO claimantsDTO = new ClaimantsDTO();
            claimantsDTO.setClaimantId(parseToLong(claimant[0])); // Handle clmnt_id
            claimantsDTO.setOtherNames((String) claimant[4]); // clmnt_othernames
            claimantsDTO.setSurname((String) claimant[5]); // clmnt_surname
            claimantsDTOList.add(claimantsDTO);
        }
        return new PageImpl<>(claimantsDTOList, pageable, rowCount);
    }

    private long parseToLong(Object value) {
        if (value instanceof BigInteger) {
            return ((BigInteger) value).longValue();
        } else if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid numeric string: " + value, e);
            }
        } else if (value == null) {
            throw new IllegalArgumentException("Value is null");
        } else {
            throw new IllegalArgumentException("Unsupported type for value: " + value.getClass());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<ClaimantsDTO> findAllClaimants(DataTablesRequest request) {
        final String search = ( request.getSearch()!=null && request.getSearch().getValue()!=null)?"%"+request.getSearch().getValue().toLowerCase()+"%":"%%";
        List<Object[]> claimants = claimantDefRepo.findClmants(search.toLowerCase(),request.getPageNumber(), request.getPageSize());
        final List<ClaimantsDTO> claimantsDTOList = new ArrayList<>();
        long rowCount = 0l;
        if(!claimants.isEmpty()) {
            rowCount = ((BigInteger)claimants.get(0)[11]).intValue();
        }
        for(Object[] claimant:claimants){
            ClaimantsDTO claimantsDTO = new ClaimantsDTO();
            claimantsDTO.setClaimantId(((BigInteger)claimant[0]).longValue());
            claimantsDTO.setAddress((String)claimant[1]);
            claimantsDTO.setIdNumber((String)claimant[2]);
            claimantsDTO.setMobileNo((String)claimant[3]);
            claimantsDTO.setOtherNames((String)claimant[4]);
            claimantsDTO.setSurname((String)claimant[5]);
            claimantsDTO.setOccupation((String)claimant[6]);
            if(claimant[7]!=null) {
                claimantsDTO.setOccupId(((BigInteger) claimant[7]).longValue());
            }
            claimantsDTO.setCreatedDate((Date) claimant[8]);
            claimantsDTO.setCreatedBy((String)claimant[9]);
            claimantsDTO.setEmail((String)claimant[10]); // ✅ This is what was missing

            claimantsDTOList.add(claimantsDTO);
        }
        Page<ClaimantsDTO>  page = new PageImpl<>(claimantsDTOList,request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteClaimant(Long clmntId) {
        claimantDefRepo.delete(clmntId);
    }


    @Override
    @Transactional(readOnly = false)
    public void deleteClaimantPeril(Long clmPerilId) {

        ClaimPerils peril= claimPerilsRepo.findOne(clmPerilId);
        ClaimClaimants claimant=peril.getClmClaimant();
        BigDecimal changeAmt = BigDecimal.ZERO;
        changeAmt = changeAmt.add(peril.getChangeAmount());

        claimant.setClaimAmount((claimant.getClaimAmount()!=null)?claimant.getClaimAmount().subtract((changeAmt!=null)?changeAmt:BigDecimal.ZERO):claimant.getClaimAmount());

        ClaimRevisions claimRevisions = new ClaimRevisions();
        claimRevisions.setClaimBookings(peril.getClaimBookings());
        claimRevisions.setRevAmount(changeAmt.negate());
        claimRevisions.setRevDate(new Date());
        claimRevisions.setTransType("LO");
        claimRevisionsRepo.save(claimRevisions);
        claimClaimantsRepo.save(claimant);
        Iterable<ClaimRevisionTrans> revisions= claimRevisionTransRepo.findAll(QClaimRevisionTrans.claimRevisionTrans.claimPeril.eq(peril));
        claimRevisionTransRepo.delete(revisions);
        Iterable<ClaimPerilPayments> perilPayments= perilPaymentsRepo.findAll(QClaimPerilPayments.claimPerilPayments.claimPerils.clmPerilId.eq(clmPerilId));
        for (ClaimPerilPayments payment:perilPayments){
            deletePerilPayment(payment.getClmPymntId());
        }
        claimPerilsRepo.delete(clmPerilId);
    }

    @Override
    @Transactional(readOnly = false)
    public void deletePerilPayment(Long clmPymntId) {

        ClaimPerilPayments perilPayments= perilPaymentsRepo.findOne(clmPymntId);
        BigDecimal changeAmt = BigDecimal.ZERO;
        changeAmt = changeAmt.add(perilPayments.getClmPymntAmount());
        ClaimPerils claimPeril = perilPayments.getClaimPerils();
        ClaimClaimants claimant=claimPeril.getClmClaimant();

        claimPeril.setClaimPaidAmount((claimPeril.getClaimPaidAmount()!=null)?claimPeril.getClaimPaidAmount().subtract(perilPayments.getClmPymntAmount()):BigDecimal.ZERO);
        claimant.setClaimPaidAmount((claimant.getClaimPaidAmount()!=null)?claimant.getClaimPaidAmount().subtract(perilPayments.getClmPymntAmount()):BigDecimal.ZERO);
        claimPerilsRepo.save(claimPeril);
        claimClaimantsRepo.save(claimant);
        perilPaymentsRepo.delete(clmPymntId);

    }



    @Override
    @Transactional(readOnly = false)
    public void deleteClaimClaimant(Long clmntId) {
        Iterable<ClaimPerils> perils= claimPerilsRepo.findAll(QClaimPerils.claimPerils.clmClaimant.claimantId.eq(clmntId));


        for (ClaimPerils peril:perils){
            deleteClaimantPeril(peril.getClmPerilId());
        }

        claimClaimantsRepo.delete(clmntId);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<Occupation> findOccupations(String paramString, Pageable paramPageable) {
        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred =QOccupation.occupation.isNotNull();
        } else {
            pred = QOccupation.occupation.name.containsIgnoreCase(paramString);
        }
        return occupRepo.findAll(pred, paramPageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClaimantsDef> findClaimants(String searchValue, Pageable pageable) {
        if(searchValue==null) searchValue="";
        Predicate pred = QClaimantsDef.claimantsDef.surname.containsIgnoreCase(searchValue).or(QClaimantsDef.claimantsDef.otherNames.containsIgnoreCase(searchValue));
        return claimantDefRepo.findAll(pred,pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClaimPerilDTO> findSubclassPerils(String searchValue, Pageable pageable, Long riskId) {
        long start2 = System.currentTimeMillis();
        final String search = (searchValue!=null)?"%"+searchValue+"%":"%%";
        List<Object[]> perils = binderSectPerilsRepo.searchBinderPerils(riskId, search.toLowerCase(),pageable.getPageNumber(), pageable.getPageSize());
        long count = 0L;
        if(!perils.isEmpty()) count = ((BigInteger)perils.get(0)[2]).longValue();
        final List<ClaimPerilDTO> claimPerilDTOS = new ArrayList<>();
        for(Object[] peril:perils){
            ClaimPerilDTO perilDTO = new ClaimPerilDTO();
            perilDTO.setBindPerilCode(((BigInteger)peril[0]).longValue());
            perilDTO.setPerilDesc((String)peril[1]);
            claimPerilDTOS.add(perilDTO);
        }
        long end2 = System.currentTimeMillis();
        System.out.println("Elapsed Time in milli seconds: "+ (end2-start2));
        return new PageImpl<>(claimPerilDTOS,pageable, count);
    }

    public boolean checkPerilExists(List<PerilsDef> listPerils, PerilsDef perils){
        for(PerilsDef peril:listPerils){
            if(peril.getPerilCode() == perils.getPerilCode()) return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {ClaimException.class})
    public Long createClaim(ClaimForm claimForm, boolean isApproved,boolean skipMakerChecker,String claimStatus) throws ClaimException, BadRequestException {
        // Validations
        if (claimForm.getPerils() == null || claimForm.getPerils().isEmpty()) {
            throw new ClaimException("Cannot Create A Claim without perils...");
        }
        if (claimForm.getLossDate() == null || claimForm.getNotificationDate() == null) {
            throw new ClaimException("You cannot create A Claim without Loss Date or Notification Date");
        }
        if (claimForm.getNotificationDate().after(new Date())) {
            throw new ClaimException("The date you are advised of the claim cannot be after Today's Date");
        }
        if (claimForm.getLossDate().after(claimForm.getNotificationDate())) {
            throw new ClaimException("There is No Way you would be advised of a claim that has not happened. Please check that the date advised is not before the Loss Date");
        }



        RiskTrans riskTrans = riskTransRepo.findOne(QRiskTrans.riskTrans.riskId.eq(claimForm.getRiskId()));

        if (riskTrans == null) {
            throw new BadRequestException("Risk not found: " + claimForm.getRiskId());
        }

        PolicyTrans policy = riskTrans.getPolicy();
        if (policy == null) {
            throw new BadRequestException("Policy not found for risk: " + claimForm.getRiskId());
        }

        Date policyStartDate = policy.getWefDate();
        Date policyEndDate = policy.getWetDate();

        if (policyStartDate == null || policyEndDate == null) {
            throw new ClaimException("Policy cover period is invalid - missing start or end date");
        }

        if (claimForm.getLossDate().before(policyStartDate) || claimForm.getLossDate().after(policyEndDate)) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            throw new ClaimException("Loss date (" + sdf.format(claimForm.getLossDate()) +
                    ") must be within policy cover period (" +
                    sdf.format(policyStartDate) + " to " + sdf.format(policyEndDate) + ")");
        }


        // Check if notification date is after policy expiry
        Date policyExpiryDate = riskTrans.getPolicy().getWetDate(); // or however you access policy expiry
        if (claimForm.getNotificationDate().after(policyExpiryDate)) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            throw new ClaimException("Cannot create claim: Notification date (" +
                    sdf.format(claimForm.getNotificationDate()) + ") is after policy expiry date (" +
                    sdf.format(policyExpiryDate) + ")");
        }


        // Adjust insurer date for holidays if it exists
      if (claimForm.getInsurerDate() != null) {
                        // Convert sql.Date to util.Date if needed
                        claimForm.setInsurerDate((claimForm.getInsurerDate() instanceof java.sql.Date) ?
                                new Date(claimForm.getInsurerDate().getTime()) : claimForm.getInsurerDate());
                    }

        // Generate claim number
        int clmUwYear = dateUtilities.getUwYear(claimForm.getLossDate());
        Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("CL");
        if (sequenceRepo.count(seqPredicate) == 0) {
            throw new ClaimException("Sequence for Claims Transactions has not been defined");
        }
        SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
        Long seqNumber = sequence.getNextNumber();
        sequence.setLastNumber(seqNumber);
        sequence.setNextNumber(seqNumber + 1);
        sequenceRepo.save(sequence);
        final String claimNumber = sequence.getSeqPrefix() + String.format("%05d", seqNumber);


        // Create ClaimBookings
//        RiskTrans riskTrans = riskTransRepo.findOne(QRiskTrans.riskTrans.riskId.eq(claimForm.getRiskId()));
        if (riskTrans == null) {
            throw new BadRequestException("Risk not found: " + claimForm.getRiskId());
        }
        // Validate unique claim
        Long clmCount = claimsBookingRepo.count(QClaimBookings.claimBookings.risk.riskId.eq(claimForm.getRiskId())
                .and(QClaimBookings.claimBookings.lossDate.eq(claimForm.getLossDate())));
        if (clmCount > 0) {
            throw new ClaimException("Another claim already exists for the loss date selected");
        }
        ClaimBookings bookings = new ClaimBookings();
        bookings.setBookedBy(userUtils.getCurrentUser());
        bookings.setBookedDate(new Date());
        bookings.setClaimNo(claimNumber);
        bookings.setClaimRejected(false);
        bookings.setClmDate(claimForm.getNotificationDate());
        bookings.setClaimStatus(claimStatus);
//        bookings.setClaimStatus("B");
        bookings.setClaimTime(new SimpleDateFormat("h:m").format(new Date()));
        bookings.setLossDate(claimForm.getLossDate());
        bookings.setLossDesc(claimForm.getLossDesc());
        bookings.setRiskIdentifier(claimForm.getRiskIdentifier());
        bookings.setNextReviewDate(claimForm.getNextReviewDate());
        bookings.setRisk(riskTrans);
        bookings.setStatusDate(new Date());
        bookings.setActivity(clmStatusRepo.findOne(claimForm.getActivityId()));
        bookings.setLiabilityAdmission(claimForm.isLiabilityAdmission());
        bookings.setPartyToBlame(claimForm.getPartyToBlame());
        bookings.setInsurerDate(claimForm.getInsurerDate());
        bookings.setApprovalStatus(isApproved ? "A" : "N");
        if (claimForm.isBalanceApproved()) {
            User currentUser = userUtils.getCurrentUser();
            bookings.setBalanceApprovedBy(currentUser);
            bookings.setBalanceApprovalDate(new Date());
        }
        ClaimBookings booking = claimsBookingRepo.save(bookings);
        Long clmId = booking.getClmId();

        // Create ClaimActivities
        ClaimActivities activities = new ClaimActivities();
        activities.setActivity(clmStatusRepo.findOne(claimForm.getActivityId()));
        activities.setActivityDate(new Date());
        activities.setClaimBookings(booking);
        activities.setRemDate(claimForm.getNextReviewDate());
        activities.setUserCreated(userUtils.getCurrentUser());
        activities.setCurrentActivity("Y");
        if (claimForm.getActivityNotes() != null && !claimForm.getActivityNotes().trim().isEmpty()) {
            User currentUser = userUtils.getCurrentUser();
            activities.addActivityNote(
                    claimForm.getActivityNotes().trim(),

                    currentUser.getUsername()
            );
        }
        //activities.setActivityNotes(claimForm.getActivityNotes());


        if (claimForm.getNextReviewUser() != null) {
            activities.setReviewUser(userRepo.findOne(claimForm.getNextReviewUser()));
        }
        activitiesRepo.save(activities);

        // Create ClaimClaimants and ClaimPerils
        List<PerilBean> perils = claimForm.getPerils();
        List<ClaimClaimants> claimants = new ArrayList<>();
        List<ClaimPerils> clmPerils = new ArrayList<>();
        for (PerilBean perilBean : perils) {
            BinderSectionPerils binderSectionPeril = binderSectPerilsRepo.findOne(QBinderSectionPerils.binderSectionPerils.bspId.eq(perilBean.getPerilCode()));
            if (binderSectionPeril == null) {
                throw new BadRequestException("Peril not found: " + perilBean.getPerilCode());
            }
            ClaimClaimants claimClaimant = new ClaimClaimants();
            ClaimPerils clmPeril = new ClaimPerils();

            clmPeril.setClaimBookings(booking);
            clmPeril.setClaimant("Y");
            clmPeril.setClmClaimant(claimClaimant);
            clmPeril.setExcessAmt(binderSectionPeril.getSubclassPeril().getExcess());
            clmPeril.setType(binderSectionPeril.getSubclassPeril().getSiOrLimit());
            clmPeril.setLimitAmt(binderSectionPeril.getSubclassPeril().getClaimLimit());
            clmPeril.setReserve(perilBean.getPerilEstimate());
            clmPeril.setChangeAmount(perilBean.getPerilEstimate());
            clmPeril.setRevisionBy(userUtils.getCurrentUser());
            clmPeril.setTransType("LO");
            clmPeril.setPerilsDef(binderSectionPeril.getSubclassPeril().getPeril());
            clmPeril.setTotalReserve(perilBean.getPerilEstimate());
            clmPeril.setOriginalreserve(binderSectionPeril.getSubclassPeril().getClaimLimit());
            clmPeril.setBinderSectionPerils(binderSectionPeril);
            claimClaimant.setCreatedDate(new Date());
            claimClaimant.setCreatedUser((userUtils.getCurrentUser()));

            claimClaimant.setClaimAmount(perilBean.getPerilEstimate());
            claimClaimant.setClaimantStatus("1");
            claimClaimant.setClaimBookings(booking);
            if (perilBean.getSelfAsClaimant() == null || "off".equalsIgnoreCase(perilBean.getSelfAsClaimant())) {
                claimClaimant.setThirdParty("T");
                ClaimantsDef claimantsDef = claimantDefRepo.findOne(perilBean.getClaimantCode());
                if (claimantsDef == null) {
                    throw new BadRequestException("Claimant not found: " + perilBean.getClaimantCode());
                }
                claimClaimant.setClaimant(claimantsDef);
            } else if ("on".equalsIgnoreCase(perilBean.getSelfAsClaimant())) {
                claimClaimant.setThirdParty("S");
                claimClaimant.setClient(riskTrans.getInsured());
            }
            claimants.add(claimClaimant);
            clmPerils.add(clmPeril);
        }
        claimClaimantsRepo.save(claimants);
        Iterable<ClaimPerils> savedPerils = claimPerilsRepo.save(clmPerils);

        // Create ClaimRequiredDocs
        Iterable<SubClassReqdDocs> requiredDocs = requiredDocsRepo.findAll(QSubClassReqdDocs.subClassReqdDocs.subclass.subId.eq(riskTrans.getSubclass().getSubId())
                .and(QSubClassReqdDocs.subClassReqdDocs.requiredDoc.appliesLossOpening.eq(true))
                .and(QSubClassReqdDocs.subClassReqdDocs.requiredDoc.mandatory.eq(true)));
        List<ClaimRequiredDocs> claimRequiredDocs = new ArrayList<>();
        for (SubClassReqdDocs subClassReqdDocs : requiredDocs) {
            ClaimRequiredDocs claimRequiredDoc = new ClaimRequiredDocs();
            claimRequiredDoc.setClaimBookings(booking);
            claimRequiredDoc.setRequiredDoc(subClassReqdDocs.getRequiredDoc());
            claimRequiredDoc.setSubmitted("N");
            claimRequiredDoc.setUserReceived(userUtils.getCurrentUser());
            claimRequiredDocs.add(claimRequiredDoc);
        }
        claimRequiredDocsRepo.save(claimRequiredDocs);

        // Create ClaimRevisions
        BigDecimal changeAmt = BigDecimal.ZERO;
        for (ClaimPerils clmprl : savedPerils) {
            changeAmt = changeAmt.add(clmprl.getChangeAmount());
        }
        ClaimRevisions claimRevisions = new ClaimRevisions();
        claimRevisions.setClaimBookings(booking);
        claimRevisions.setRevAmount(changeAmt);
        claimRevisions.setRevDate(new Date());
        claimRevisions.setTransType("LO");
        ClaimRevisions savedRevision = claimRevisionsRepo.save(claimRevisions);

        // Create ClaimRevisionTrans
        List<ClaimRevisionTrans> revisionTranses = new ArrayList<>();
        for (ClaimPerils peril : savedPerils) {
            ClaimRevisionTrans revisionTrans = new ClaimRevisionTrans();
            revisionTrans.setAmount(peril.getChangeAmount());
            revisionTrans.setClaimPeril(peril);
            revisionTrans.setClaimRevision(savedRevision);
            revisionTrans.setType(peril.getType());
            revisionTranses.add(revisionTrans);
        }
        claimRevisionTransRepo.save(revisionTranses);

        // Create Maker-Checker task if not approved
        if (!skipMakerChecker && !isApproved) {
            MakerCheckDTO makerCheckDTO = new MakerCheckDTO();
            String taskName = "Claim Creation: " + claimNumber;
            if (claimForm.getClaimantCode() != null && claimForm.getActivityId() != null) {
                ClaimantsDef claimant = claimantDefRepo.findOne(claimForm.getClaimantCode());
                ClmCausations activity = clmStatusRepo.findOne(claimForm.getActivityId());
                if (claimant != null && activity != null) {
                    taskName = "Created Claim: Claimant " + claimant.getOtherNames() + ", Description: " + activity.getActivityDesc();
                }
            }
            makerCheckDTO.setTaskName(taskName);
            makerCheckDTO.setTaskType("CL");
            makerCheckDTO.setTaskCode(clmId);
            makerCheckDTO.setStatus("N");

            // Use ClaimDetailsDTO for taskJson
            ClaimDetailsDTO claimDetailsDTO = new ClaimDetailsDTO();
            claimDetailsDTO.setClmId(clmId);
            claimDetailsDTO.setClaimNo(claimNumber);
            claimDetailsDTO.setLossDesc(claimForm.getLossDesc());
            claimDetailsDTO.setRiskIdentifier(claimForm.getRiskIdentifier());
            claimDetailsDTO.setLossDate(claimForm.getLossDate());
            claimDetailsDTO.setRiskId(claimForm.getRiskId() != null ? claimForm.getRiskId().toString() : null);
            claimDetailsDTO.setNotificationDate(claimForm.getNotificationDate());
            claimDetailsDTO.setNextRvwDate(claimForm.getNextReviewDate());
            claimDetailsDTO.setLiabilityAdmission(claimForm.isLiabilityAdmission());
            claimDetailsDTO.setClaimStatus("B");
            claimDetailsDTO.setBookedDate(new Date());

            Gson gson = new GsonBuilder()
                    .setDateFormat("MMM dd, yyyy HH:mm:ss z")
                    .create();
            String jsonString = gson.toJson(claimDetailsDTO);
            if (jsonString == null || jsonString.trim().isEmpty() || "{}".equals(jsonString)) {
                log.error("Failed to create valid task JSON for claim: {}", clmId);
                throw new BadRequestException("Failed to create valid task JSON");
            }
            makerCheckDTO.setJson(jsonString);
            log.info("Maker-Checker task JSON: {}", jsonString);
            makerCheckDTO.setMadeOnDate(new Date());
            User currentUser = userUtils.getCurrentUser();
            if (currentUser == null) {
                throw new BadRequestException("No authenticated user found");
            }
            makerCheckDTO.setMakerId(currentUser.getId());
            makerCheckDTO.setInitiatorId(currentUser.getId());
            makerCheckDTO.setReferenceId(clmId);

            // Assign checkers
            List<UserDTO> eligibleCheckers = makerCheckerService.findEligibleCheckers("AUTHORIZE_POLICY", null);
            List<Long> checkerIds = new ArrayList<>();
            if (!makerCheckerRepo.exists(QMakerChecker.makerChecker.taskType.equalsIgnoreCase("CL")
                    .and(QMakerChecker.makerChecker.taskCode.eq(clmId)))) {
                for (UserDTO eligibleChecker : eligibleCheckers) {
                    checkerIds.add(eligibleChecker.getId());
                }
            }
            if (checkerIds.isEmpty()) {
                log.warn("No eligible checkers found for claim: {}", clmId);
                throw new BadRequestException("No eligible checkers found for claim");
            }
            makerCheckDTO.setAssignedCheckers(new Gson().toJson(checkerIds));
            log.info("Assigned checkers: {}", checkerIds);

            // Create Maker-Checker
            try {
                log.info("Checking for existing Maker-Checker task for claim: {}", clmId);
                makerCheckerService.checkExists(makerCheckDTO);
                log.info("Creating Maker-Checker task for claim: {}", clmId);
                makerCheckerService.createMakerChecker(makerCheckDTO);
            } catch (BadRequestException e) {
                log.error("Failed to create Maker-Checker task for claim: {}, error: {}", clmId, e.getMessage(), e);
                throw e;
            } catch (Exception e) {
                log.error("Unexpected error creating Maker-Checker task for claim: {}, error: {}", clmId, e.getMessage(), e);
                throw new BadRequestException("Unexpected error creating Maker-Checker task: " + e.getMessage());
            }
        }

        return clmId;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = {BadRequestException.class})
    public void createMakerCheckerForDraftClaim(Long claimId) throws BadRequestException {
        ClaimBookings booking = claimsBookingRepo.findOne(claimId);
        if (booking == null) {
            throw new BadRequestException("Claim not found: " + claimId);
        }

//        long uploadedDocsCount = claimRequiredDocsRepo.count(
//                QClaimRequiredDocs.claimRequiredDocs.claimBookings.clmId.eq(claimId)
//                        .and(QClaimRequiredDocs.claimRequiredDocs.submitted.eq("Y"))
//        );
//
//        if (uploadedDocsCount == 0) {
//            throw new BadRequestException("Cannot submit claim without uploading at least one document");
//        }


        MakerCheckDTO makerCheckDTO = new MakerCheckDTO();
        String taskName = "Claim Creation: " + booking.getClaimNo();


        if (booking.getActivity() != null) {
            taskName = "Created Claim: " + booking.getClaimNo() + ", Activity: " + booking.getActivity().getActivityDesc();
        }

        makerCheckDTO.setTaskName(taskName);
        makerCheckDTO.setTaskType("CL");
        makerCheckDTO.setTaskCode(claimId);
        makerCheckDTO.setStatus("N");


        ClaimDetailsDTO claimDetailsDTO = new ClaimDetailsDTO();
        claimDetailsDTO.setClmId(claimId);
        claimDetailsDTO.setClaimNo(booking.getClaimNo());
        claimDetailsDTO.setLossDesc(booking.getLossDesc());
        claimDetailsDTO.setRiskIdentifier(booking.getRiskIdentifier());
        claimDetailsDTO.setLossDate(booking.getLossDate());
        claimDetailsDTO.setRiskId(booking.getRisk().getRiskId().toString());
        claimDetailsDTO.setNotificationDate(booking.getClmDate());
        claimDetailsDTO.setNextRvwDate(booking.getNextReviewDate());
        claimDetailsDTO.setLiabilityAdmission(booking.isLiabilityAdmission());
        claimDetailsDTO.setClaimStatus("B");
        claimDetailsDTO.setBookedDate(new Date());

        Gson gson = new GsonBuilder()
                .setDateFormat("MMM dd, yyyy HH:mm:ss z")
                .create();
        String jsonString = gson.toJson(claimDetailsDTO);
        if (jsonString == null || jsonString.trim().isEmpty() || "{}".equals(jsonString)) {

            throw new BadRequestException("Failed to create valid task JSON");
        }
        makerCheckDTO.setJson(jsonString);

        makerCheckDTO.setMadeOnDate(new Date());
        User currentUser = userUtils.getCurrentUser();
        if (currentUser == null) {
            throw new BadRequestException("No authenticated user found");
        }
        makerCheckDTO.setMakerId(currentUser.getId());
        makerCheckDTO.setInitiatorId(currentUser.getId());
        makerCheckDTO.setReferenceId(claimId);

        // Assign checkers (same logic as createClaim)
        List<UserDTO> eligibleCheckers = makerCheckerService.findEligibleCheckers("AUTHORIZE_POLICY", null);
        List<Long> checkerIds = new ArrayList<>();
        if (!makerCheckerRepo.exists(QMakerChecker.makerChecker.taskType.equalsIgnoreCase("CL")
                .and(QMakerChecker.makerChecker.taskCode.eq(claimId)))) {
            for (UserDTO eligibleChecker : eligibleCheckers) {
                checkerIds.add(eligibleChecker.getId());
            }
        }
        if (checkerIds.isEmpty()) {
            throw new BadRequestException("No eligible checkers found for claim");
        }
        makerCheckDTO.setAssignedCheckers(new Gson().toJson(checkerIds));



        try {
            log.info("Checking for existing Maker-Checker task for claim: {}", claimId);
            makerCheckerService.checkExists(makerCheckDTO);
            log.info("Creating Maker-Checker task for claim: {}", claimId);
            makerCheckerService.createMakerChecker(makerCheckDTO);


            booking.setApprovalStatus("N");
            claimsBookingRepo.save(booking);

        } catch (BadRequestException e) {

            throw e;
        } catch (Exception e) {

            throw new BadRequestException("Unexpected error creating Maker-Checker task: " + e.getMessage());
        }
    }








//    @Override
//    @Transactional(readOnly = false,rollbackFor = { ClaimException.class })
//    public Long createClaim(ClaimForm claimForm) throws ClaimException {
//        if(claimForm.getPerils()==null ||claimForm.getPerils().size()==0)
//            throw  new ClaimException("Cannot Create A Claim without perils...");
//        if(claimForm.getLossDate()==null || claimForm.getNotificationDate()==null)
//            throw new ClaimException("You cannot create A Claim without Loss Date or Notification Date");
//
//        if(claimForm.getNotificationDate().after(new Date())){
//            throw new ClaimException("The date you are adviced of the claim cannot be after Today's Date");
//        }
//
//        if(claimForm.getLossDate().after(claimForm.getNotificationDate())){
//            throw new ClaimException("There is No Way you would be advised of a claim that has not happened. Please check that the date adviced is not before the Loss Date");
//        }
//        // Adjust insurer date for holidays if it exists
//        if (claimForm.getInsurerDate() != null) {
//            // Convert insurer date to LocalDate
//            Date insurerDate = (claimForm.getInsurerDate() instanceof java.sql.Date) ?
//                    new Date(claimForm.getInsurerDate().getTime()) : claimForm.getInsurerDate();
//
//            LocalDate insurerLocalDate = insurerDate.toInstant()
//                    .atZone(ZoneId.systemDefault())
//                    .toLocalDate();
//
//            // Apply holiday adjustment
//            LocalDate adjustedInsurerDate = HolidayUtils.getAdjustedTransactionDate(insurerLocalDate);
//
//            // Convert back to Date and update the claimForm
//            claimForm.setInsurerDate(Date.from(adjustedInsurerDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
//
//        }
//        final Long hashCode = Long.parseLong(String.valueOf(claimForm.hashCode()));
//       // System.out.println(new Gson().toJson(claimForm));
////        MakerCheckDTO makerCheckDTO = new MakerCheckDTO();
////        makerCheckDTO.setJson(new Gson().toJson(claimForm));
////        makerCheckDTO.setStatus("N");
////        ClaimantsDef claimantsDef1 = claimantDefRepo.findOne(claimForm.getClaimantCode());
////        ClmCausations activity = clmStatusRepo.findOne(claimForm.getActivityId());
////        makerCheckDTO.setTaskName("Created Claim:Claimant..."+ claimantsDef1.getOtherNames() + " " + " Description" + activity);
////        makerCheckDTO.setTaskType("CL");
////        makerCheckDTO.setTaskCode(hashCode);
////        try {
////            makerCheckerService.checkExists(makerCheckDTO);
////        } catch (BadRequestException e) {
////            throw new RuntimeException(e);
////        }
////        try {
////            makerCheckerService.createMakerChecker(makerCheckDTO);
////        } catch (BadRequestException e) {
////            throw new RuntimeException(e);
////        }
//        int clmUwYear = dateUtilities.getUwYear(claimForm.getLossDate());
//        Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("CL");
//        if (sequenceRepo.count(seqPredicate) == 0)
//            throw new ClaimException("Sequence for Claims Transactions has not been defined");
//        SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
//        Long seqNumber = sequence.getNextNumber();
//        sequence.setLastNumber(seqNumber);
//        sequence.setNextNumber(seqNumber + 1);
//        sequenceRepo.save(sequence);
//        final String claimNumber = sequence.getSeqPrefix() + String.format("%05d", seqNumber);
//        Long clmCount = claimsBookingRepo.count(QClaimBookings.claimBookings.risk.riskId.eq(claimForm.getRiskId()).and(QClaimBookings.claimBookings.lossDate.eq(claimForm.getLossDate())));
//        if(clmCount > 0) throw new ClaimException("Another claim already exists for the loss date selected");
//        RiskTrans riskTrans  = riskTransRepo.findOne(QRiskTrans.riskTrans.riskId.eq(claimForm.getRiskId()));
//        ClaimBookings bookings = new ClaimBookings();
//        bookings.setBookedBy(userUtils.getCurrentUser());
//        bookings.setBookedDate(new Date());
//        bookings.setClaimNo(claimNumber);
//        bookings.setClaimRejected(false);
//        bookings.setClmDate(claimForm.getNotificationDate());
//        bookings.setClaimStatus("B");
//        bookings.setClaimTime(new SimpleDateFormat("h:m").format(new Date()));
//        bookings.setLossDate(claimForm.getLossDate());
//        bookings.setLossDesc(claimForm.getLossDesc());
//        bookings.setNextReviewDate(claimForm.getNextReviewDate());
//        bookings.setRisk(riskTransRepo.findOne(QRiskTrans.riskTrans.riskId.eq(claimForm.getRiskId())));
//        bookings.setStatusDate(new Date());
//        bookings.setActivity(clmStatusRepo.findOne(claimForm.getActivityId()));
//        bookings.setLiabilityAdmission(claimForm.isLiabilityAdmission());
//        bookings.setPartyToBlame(claimForm.getPartyToBlame());
//        bookings.setInsurerDate(claimForm.getInsurerDate());
//        ClaimBookings booking = claimsBookingRepo.save(bookings);
//
//        ClaimActivities activities = new ClaimActivities();
//        activities.setActivity(clmStatusRepo.findOne(claimForm.getActivityId()));
//        activities.setActivityDate(new Date());
//        activities.setClaimBookings(booking);
//        activities.setRemDate(claimForm.getNextReviewDate());
//        activities.setUserCreated(userUtils.getCurrentUser());
//        activities.setCurrentActivity("Y");
//        activities.setActivityNotes(claimForm.getActivityNotes());
//        activitiesRepo.save(activities);
//
//        List<PerilBean> perils = claimForm.getPerils();
//        List<ClaimClaimants> claimants = new ArrayList<>();
//        List<ClaimPerils> clmPerils = new ArrayList<>();
//        for(PerilBean perilBean:perils){
//            BinderSectionPerils binderSectionPeril = binderSectPerilsRepo.findOne(QBinderSectionPerils.binderSectionPerils.bspId.eq(perilBean.getPerilCode()));
//            ClaimClaimants claimClaimant = new ClaimClaimants();
//            ClaimPerils clmPeril = new ClaimPerils();
//
//            clmPeril.setClaimBookings(booking);
//            clmPeril.setClaimant("Y");
//            clmPeril.setClmClaimant(claimClaimant);
//            clmPeril.setExcessAmt(binderSectionPeril.getSubclassPeril().getExcess());
//            clmPeril.setType(binderSectionPeril.getSubclassPeril().getSiOrLimit());
//            clmPeril.setLimitAmt(binderSectionPeril.getSubclassPeril().getClaimLimit());
//            clmPeril.setReserve(perilBean.getPerilEstimate());
//            clmPeril.setChangeAmount(perilBean.getPerilEstimate());
//            clmPeril.setRevisionBy(userUtils.getCurrentUser());
//            clmPeril.setTransType("LO");
//            clmPeril.setPerilsDef(binderSectionPeril.getSubclassPeril().getPeril());
//            clmPeril.setTotalReserve(perilBean.getPerilEstimate());
//            clmPeril.setOriginalreserve(binderSectionPeril.getSubclassPeril().getClaimLimit());
//            clmPeril.setBinderSectionPerils(binderSectionPeril);
//            claimClaimant.setClaimAmount(perilBean.getPerilEstimate());
//            claimClaimant.setClaimantStatus("1");
//            claimClaimant.setClaimBookings(booking);
//            System.out.println("Self as Claimant..."+perilBean.getSelfAsClaimant());
//            if(perilBean.getSelfAsClaimant()==null || "off".equalsIgnoreCase(perilBean.getSelfAsClaimant())){
//                claimClaimant.setThirdParty("T");
//                ClaimantsDef claimantsDef = claimantDefRepo.findOne(perilBean.getClaimantCode());
//                claimClaimant.setClaimant(claimantsDef);
//            }
//            else if("on".equalsIgnoreCase(perilBean.getSelfAsClaimant())) {
//                claimClaimant.setThirdParty("S");
//                claimClaimant.setClient(riskTrans.getInsured());
//            }
//            claimants.add(claimClaimant);
//            clmPerils.add(clmPeril);
//        }
//        claimClaimantsRepo.save(claimants);
//        Iterable<ClaimPerils> savedPerils  = claimPerilsRepo.save(clmPerils);
//        Iterable<SubClassReqdDocs> requiredDocs = requiredDocsRepo.findAll(QSubClassReqdDocs.subClassReqdDocs.subclass.subId.eq(riskTrans.getSubclass().getSubId())
//                .and(QSubClassReqdDocs.subClassReqdDocs.requiredDoc.appliesLossOpening.eq(true))
//                .and(QSubClassReqdDocs.subClassReqdDocs.requiredDoc.mandatory.eq(true)));
//        List<ClaimRequiredDocs> claimRequiredDocs = new ArrayList<>();
//        for(SubClassReqdDocs subClassReqdDocs:requiredDocs){
//            ClaimRequiredDocs claimRequiredDoc = new ClaimRequiredDocs();
//            claimRequiredDoc.setClaimBookings(booking);
//            claimRequiredDoc.setRequiredDoc(subClassReqdDocs.getRequiredDoc());
//            claimRequiredDoc.setSubmitted("N");
//            claimRequiredDoc.setUserReceived(userUtils.getCurrentUser());
//            claimRequiredDocs.add(claimRequiredDoc);
//        }
//        claimRequiredDocsRepo.save(claimRequiredDocs);
//
//        BigDecimal changeAmt = BigDecimal.ZERO;
//        for(ClaimPerils clmprl:savedPerils){
//            changeAmt = changeAmt.add(clmprl.getChangeAmount());
//        }
//
//        ClaimRevisions claimRevisions = new ClaimRevisions();
//        claimRevisions.setClaimBookings(booking);
//        claimRevisions.setRevAmount(changeAmt);
//        claimRevisions.setRevDate(new Date());
//        claimRevisions.setTransType("LO");
//        ClaimRevisions savedRevision = claimRevisionsRepo.save(claimRevisions);
//        List<ClaimRevisionTrans> revisionTranses = new ArrayList<>();
//        for(ClaimPerils peril:savedPerils){
//            ClaimRevisionTrans revisionTrans = new ClaimRevisionTrans();
//            revisionTrans.setAmount(peril.getChangeAmount());
//            revisionTrans.setClaimPeril(peril);
//            revisionTrans.setClaimRevision(savedRevision);
//            revisionTrans.setType(peril.getType());
//            revisionTranses.add(revisionTrans);
//        }
//
//        claimRevisionTransRepo.save(revisionTranses);
//        return booking.getClmId();
//    }

@Override
@Transactional(readOnly = false, rollbackFor = {ClaimException.class})
public void updateRejectedClaim(Long claimId, ClaimForm claimForm, String resubmissionComment) throws ClaimException, BadRequestException {

    try {

        ClaimBookings existingClaim = claimsBookingRepo.findOne(claimId);
        if (existingClaim == null) {

            throw new BadRequestException("Claim not found: " + claimId);
        }

        if (claimForm.getLossDate() == null || claimForm.getNotificationDate() == null) {

            throw new ClaimException("You cannot update A Claim without Loss Date or Notification Date");
        }


        existingClaim.setClmDate(claimForm.getNotificationDate());
        existingClaim.setClaimStatus("B"); // Change to B
        existingClaim.setLossDate(claimForm.getLossDate());
        existingClaim.setLossDesc(claimForm.getLossDesc());
        existingClaim.setRiskIdentifier(claimForm.getRiskIdentifier());
        existingClaim.setNextReviewDate(claimForm.getNextReviewDate());
        existingClaim.setStatusDate(new Date());

        // Activity
        if (claimForm.getActivityId() != null) {
            ClmCausations activity = clmStatusRepo.findOne(claimForm.getActivityId());
            existingClaim.setActivity(activity);
            log.info("Set activity {} for claim {}", claimForm.getActivityId(), claimId);
        }

        existingClaim.setLiabilityAdmission(claimForm.isLiabilityAdmission());
        existingClaim.setPartyToBlame(claimForm.getPartyToBlame());
        existingClaim.setInsurerDate(claimForm.getInsurerDate());
        existingClaim.setApprovalStatus("N");

        if (claimForm.isBalanceApproved()) {
            User currentUser = userUtils.getCurrentUser();
            existingClaim.setBalanceApprovedBy(currentUser);
            existingClaim.setBalanceApprovalDate(new Date());
        } else {
            existingClaim.setBalanceApprovedBy(null);
            existingClaim.setBalanceApprovalDate(null);
        }

        claimsBookingRepo.save(existingClaim);


        List<ClaimActivities> existingActivities = activitiesRepo.findByClaimBookingsOrderByActivityDateDesc(existingClaim);
        ClaimActivities latestActivity = existingActivities.isEmpty() ? null : existingActivities.get(0);

        boolean shouldAddNewActivity = false;
        String reason = "";

        if (latestActivity == null) {
            shouldAddNewActivity = true;
            reason = "No existing activities";
        } else {
            boolean activityChanged = !latestActivity.getActivity().getCaId().equals(claimForm.getActivityId());
            String newNoteFromForm = claimForm.getActivityNotes();
            boolean hasNewNotes = newNoteFromForm != null && !newNoteFromForm.trim().isEmpty();

            // Get the latest existing note for comparison
            String latestExistingNote = "";
            if (latestActivity.hasNotes()) {
                ClaimActivities.ActivityNote lastNote = latestActivity.getLatestNote();
                latestExistingNote = lastNote != null ? lastNote.getNote() : "";
            }

            // Check if this note is actually different from the last one
            boolean isNoteReallyNew = hasNewNotes &&
                    !newNoteFromForm.trim().equals(latestExistingNote.trim());

            if (activityChanged) {
                shouldAddNewActivity = true;
                reason = "Activity changed from " + latestActivity.getActivity().getCaId() + " to " + claimForm.getActivityId();
            } else if (isNoteReallyNew) {
                User currentUser = userUtils.getCurrentUser();
                latestActivity.addActivityNote(
                        newNoteFromForm.trim(),

                        currentUser.getUsername()
                );
                latestActivity.setActivityDate(new Date()); // Update timestamp
                activitiesRepo.save(latestActivity);
                reason = "Added new note to existing activity";
                log.info("Added note to existing activity {}: {}", latestActivity.getActivityId(), newNoteFromForm.trim());
            } else {
                shouldAddNewActivity = false;
                reason = hasNewNotes ? "Note unchanged - no update needed" : "Activity and notes unchanged - no update needed";
            }
        }


//            boolean hasNewNotes = claimForm.getActivityNotes() != null &&
//                    !claimForm.getActivityNotes().trim().isEmpty() &&
//                    !claimForm.getActivityNotes().trim().equals(
//                            latestActivity.getActivityNotes() != null ? latestActivity.getActivityNotes().trim() : ""
//                    );
//
//            if (activityChanged) {
//                shouldAddNewActivity = true;
//                reason = "Activity changed from " + latestActivity.getActivity().getCaId() + " to " + claimForm.getActivityId();
//            } else if (hasNewNotes) {
//                latestActivity.setActivityNotes(claimForm.getActivityNotes());
//                latestActivity.setActivityDate(new Date()); // Update timestamp
//                activitiesRepo.save(latestActivity);
//                reason = "Updated notes on existing activity";
//            } else {
//                shouldAddNewActivity = false;
//                reason = "Activity and notes unchanged - no update needed";
//            }
//        }


        if (shouldAddNewActivity) {

            ClaimActivities newActivity = new ClaimActivities();
            newActivity.setActivity(clmStatusRepo.findOne(claimForm.getActivityId()));
            newActivity.setActivityDate(new Date());
            newActivity.setClaimBookings(existingClaim);
            newActivity.setRemDate(claimForm.getNextReviewDate());
            newActivity.setUserCreated(userUtils.getCurrentUser());
            newActivity.setCurrentActivity("Y");
            if (claimForm.getActivityNotes() != null && !claimForm.getActivityNotes().trim().isEmpty()) {
                User currentUser = userUtils.getCurrentUser();
                newActivity.addActivityNote(
                        claimForm.getActivityNotes().trim(),

                        currentUser.getUsername()
                );
            }
            //newActivity.setActivityNotes(claimForm.getActivityNotes());


            // Set previous activity to not current
            if (latestActivity != null) {
                latestActivity.setCurrentActivity("N");
                activitiesRepo.save(latestActivity);
            }

            activitiesRepo.save(newActivity);
        }


        try {
            createMakerCheckerForResubmission(existingClaim,resubmissionComment);

        } catch (Exception e) {

            throw new BadRequestException("Failed to create maker-checker task: " + e.getMessage());
        }


    } catch (Exception e) {

        throw e;
    }
}



    private void createMakerCheckerForResubmission(ClaimBookings claim,String resubmissionComment) throws BadRequestException {
        MakerCheckDTO makerCheckDTO = new MakerCheckDTO();
        String taskName = "Claim Creation: " + claim.getClaimNo();

        makerCheckDTO.setTaskName(taskName);
        makerCheckDTO.setTaskType("CL");
        makerCheckDTO.setTaskCode(claim.getClmId());
        makerCheckDTO.setStatus("N");

        // Create ClaimDetailsDTO for taskJson (reuse your existing logic)
        ClaimDetailsDTO claimDetailsDTO = new ClaimDetailsDTO();
        claimDetailsDTO.setClmId(claim.getClmId());
        claimDetailsDTO.setClaimNo(claim.getClaimNo());
        claimDetailsDTO.setLossDesc(claim.getLossDesc());
        claimDetailsDTO.setRiskIdentifier(claim.getRiskIdentifier());
        claimDetailsDTO.setLossDate(claim.getLossDate());
        claimDetailsDTO.setRiskId(claim.getRisk().getRiskId().toString());
        claimDetailsDTO.setNotificationDate(claim.getClmDate());
        claimDetailsDTO.setNextRvwDate(claim.getNextReviewDate());
        claimDetailsDTO.setLiabilityAdmission(claim.isLiabilityAdmission());
        claimDetailsDTO.setClaimStatus("B");

        Gson gson = new GsonBuilder()
                .setDateFormat("MMM dd, yyyy HH:mm:ss z")
                .create();
        String jsonString = gson.toJson(claimDetailsDTO);

        if (jsonString == null || jsonString.trim().isEmpty() || "{}".equals(jsonString)) {
            log.error("Failed to create valid task JSON for claim resubmission: {}", claim.getClmId());
            throw new BadRequestException("Failed to create valid task JSON");
        }

        makerCheckDTO.setJson(jsonString);
        makerCheckDTO.setMadeOnDate(new Date());

        User currentUser = userUtils.getCurrentUser();
        if (currentUser == null) {
            throw new BadRequestException("No authenticated user found");
        }
        makerCheckDTO.setMakerId(currentUser.getId());
        makerCheckDTO.setInitiatorId(currentUser.getId());
        makerCheckDTO.setReferenceId(claim.getClmId());
        makerCheckDTO.setResubmissionComment(resubmissionComment);

        // Assign checkers (reuse your existing logic)
        List<UserDTO> eligibleCheckers = makerCheckerService.findEligibleCheckers("AUTHORIZE_POLICY", null);
        List<Long> checkerIds = new ArrayList<>();
        for (UserDTO eligibleChecker : eligibleCheckers) {
            checkerIds.add(eligibleChecker.getId());
        }

        if (checkerIds.isEmpty()) {
            throw new BadRequestException("No eligible checkers found for claim resubmission");
        }

        makerCheckDTO.setAssignedCheckers(new Gson().toJson(checkerIds));

        // Create Maker-Checker task
        try {
            makerCheckerService.checkExists(makerCheckDTO);
            makerCheckerService.createMakerChecker(makerCheckDTO);
            log.info("Created Maker-Checker task for claim resubmission: {}", claim.getClaimNo());
        } catch (Exception e) {
            log.error("Failed to create Maker-Checker task for claim resubmission: {}, error: {}", claim.getClmId(), e.getMessage(), e);
            throw new BadRequestException("Failed to create Maker-Checker task: " + e.getMessage());
        }
    }
    @Override
    @Transactional(readOnly = false,rollbackFor = { ClaimException.class })
    public void createClaimantPeril(PerilBean perilBean) throws ClaimException,BadRequestException {
        if(perilBean==null)
            throw  new BadRequestException("Provide Peril to proceed...");
        if (perilBean.getClaimId()==null)
            throw  new BadRequestException("No claim selected...");
        if (perilBean.getPerilCode()==null)
            throw  new BadRequestException("Select the peril to proceed...");

        ClaimBookings booking = claimsBookingRepo.findOne(perilBean.getClaimId());
        RiskTrans riskTrans  = booking.getRisk();
        BinderSectionPerils binderSectionPeril = binderSectPerilsRepo.findOne(perilBean.getPerilCode());

        ClaimClaimants claimClaimant = new ClaimClaimants();
        claimClaimant.setCreatedUser(userUtils.getCurrentUser());
        claimClaimant.setCreatedDate(new Date());

        ClaimPerils clmPeril = new ClaimPerils();
        clmPeril.setClaimBookings(booking);
        clmPeril.setClaimant("Y");
        clmPeril.setExcessAmt(binderSectionPeril.getSubclassPeril().getExcess());
        clmPeril.setType(binderSectionPeril.getSubclassPeril().getSiOrLimit());
        clmPeril.setLimitAmt(binderSectionPeril.getSubclassPeril().getClaimLimit());
        clmPeril.setReserve(perilBean.getPerilEstimate());
        clmPeril.setChangeAmount(perilBean.getPerilEstimate());
        clmPeril.setRevisionBy(userUtils.getCurrentUser());
        clmPeril.setTransType("LO");
        clmPeril.setPerilsDef(binderSectionPeril.getSubclassPeril().getPeril());
        clmPeril.setTotalReserve(perilBean.getPerilEstimate());
        clmPeril.setOriginalreserve(binderSectionPeril.getSubclassPeril().getClaimLimit());
        clmPeril.setBinderSectionPerils(binderSectionPeril);


        System.out.println("PerilBean selfAsClaimant value: '" + perilBean.getSelfAsClaimant() + "'");
        System.out.println("PerilBean claimantCode: " + perilBean.getClaimantCode());


        if("on".equalsIgnoreCase(perilBean.getSelfAsClaimant())) {

            System.out.println("Processing as SELF claimant");

            if (claimClaimantsRepo.count(QClaimClaimants.claimClaimants.client.tenId.eq(riskTrans.getInsured().getTenId()
            ).and(QClaimClaimants.claimClaimants.claimBookings.clmId.eq(booking.getClmId())))==0) {
                // Create new self claimant
                claimClaimant.setClaimAmount(perilBean.getPerilEstimate());
                claimClaimant.setClaimantStatus("1");
                claimClaimant.setClaimBookings(booking);
                claimClaimant.setThirdParty("S");  // SELF
                claimClaimant.setClient(riskTrans.getInsured());
            } else {

                claimClaimant = claimClaimantsRepo.findOne(QClaimClaimants.claimClaimants.client.tenId.eq(riskTrans.getInsured().getTenId()
                ).and(QClaimClaimants.claimClaimants.claimBookings.clmId.eq(booking.getClmId())));

                if (claimPerilsRepo.count(QClaimPerils.claimPerils.claimBookings.clmId.eq(booking.getClmId())
                        .and(QClaimPerils.claimPerils.binderSectionPerils.eq(binderSectionPeril))
                        .and(QClaimPerils.claimPerils.clmClaimant.eq(claimClaimant)))>0){
                    throw new BadRequestException("Peril being added already exists under this claimant");
                }
                claimClaimant.setClaimAmount((claimClaimant.getClaimAmount()!=null)?
                        claimClaimant.getClaimAmount().add((perilBean.getPerilEstimate()!=null)?perilBean.getPerilEstimate():BigDecimal.ZERO):
                        perilBean.getPerilEstimate());
            }
        } else {

            System.out.println("Processing as THIRD PARTY claimant");

            if (perilBean.getClaimantCode() == null) {
                throw new BadRequestException("Claimant must be selected for third party claims");
            }

            if (claimClaimantsRepo.count(QClaimClaimants.claimClaimants.claimant.claimantId.eq(perilBean.getClaimantCode()
            ).and(QClaimClaimants.claimClaimants.claimBookings.clmId.eq(booking.getClmId())))==0){

                claimClaimant.setClaimAmount(perilBean.getPerilEstimate());
                claimClaimant.setClaimantStatus("1");
                claimClaimant.setClaimBookings(booking);
                claimClaimant.setThirdParty("T");  // THIRD PARTY
                ClaimantsDef claimantsDef = claimantDefRepo.findOne(perilBean.getClaimantCode());
                claimClaimant.setClaimant(claimantsDef);
            } else {

                claimClaimant = claimClaimantsRepo.findOne(QClaimClaimants.claimClaimants.claimant.claimantId.eq(perilBean.getClaimantCode()
                ).and(QClaimClaimants.claimClaimants.claimBookings.clmId.eq(booking.getClmId())));

                if (claimPerilsRepo.count(QClaimPerils.claimPerils.claimBookings.clmId.eq(booking.getClmId())
                        .and(QClaimPerils.claimPerils.binderSectionPerils.eq(binderSectionPeril))
                        .and(QClaimPerils.claimPerils.clmClaimant.eq(claimClaimant)))>0){
                    throw new BadRequestException("Peril being added already exists under this claimant");
                }
                claimClaimant.setClaimAmount((claimClaimant.getClaimAmount()!=null)?
                        claimClaimant.getClaimAmount().add((perilBean.getPerilEstimate()!=null)?perilBean.getPerilEstimate():BigDecimal.ZERO):
                        perilBean.getPerilEstimate());
            }
        }


        System.out.println("Final claimClaimant.thirdParty value: '" + claimClaimant.getThirdParty() + "'");

        if (claimClaimant.getThirdParty() == null) {
            throw new BadRequestException("ThirdParty field cannot be null - check selfAsClaimant logic");
        }

        clmPeril.setClmClaimant(claimClaimant);
        claimClaimantsRepo.save(claimClaimant);
        ClaimPerils savedPerils = claimPerilsRepo.save(clmPeril);

        ClaimRevisions claimRevisions = new ClaimRevisions();
        claimRevisions.setClaimBookings(booking);
        claimRevisions.setRevAmount(perilBean.getPerilEstimate());
        claimRevisions.setRevDate(new Date());
        claimRevisions.setTransType("LR");
        ClaimRevisions savedRevision = claimRevisionsRepo.save(claimRevisions);

        ClaimRevisionTrans revisionTrans = new ClaimRevisionTrans();
        revisionTrans.setAmount(perilBean.getPerilEstimate());
        revisionTrans.setClaimPeril(savedPerils);
        revisionTrans.setClaimRevision(savedRevision);
        revisionTrans.setType(savedPerils.getType());
        claimRevisionTransRepo.save(revisionTrans);
    }


//    @Override
//    @Transactional(readOnly = false,rollbackFor = { ClaimException.class })
//    public void createClaimantPeril(PerilBean perilBean) throws ClaimException,BadRequestException {
//        if(perilBean==null)
//            throw  new BadRequestException("Provide Peril to proceed...");
//        if (perilBean.getClaimId()==null)
//            throw  new BadRequestException("No claim selected...");
//        if (perilBean.getPerilCode()==null)
//            throw  new BadRequestException("Select the peril to proceed...");
//        ClaimBookings booking = claimsBookingRepo.findOne(perilBean.getClaimId());
//        RiskTrans riskTrans  = booking.getRisk();
//        BinderSectionPerils binderSectionPeril = binderSectPerilsRepo.findOne(perilBean.getPerilCode());
//        ClaimClaimants claimClaimant = new ClaimClaimants();
//        claimClaimant.setCreatedUser(userUtils.getCurrentUser());
//        claimClaimant.setCreatedDate(new Date());
//        ClaimPerils clmPeril = new ClaimPerils();
//        clmPeril.setClaimBookings(booking);
//        clmPeril.setClaimant("Y");
//        clmPeril.setExcessAmt(binderSectionPeril.getSubclassPeril().getExcess());
//        clmPeril.setType(binderSectionPeril.getSubclassPeril().getSiOrLimit());
//        clmPeril.setLimitAmt(binderSectionPeril.getSubclassPeril().getClaimLimit());
//        clmPeril.setReserve(perilBean.getPerilEstimate());
//        clmPeril.setChangeAmount(perilBean.getPerilEstimate());
//        clmPeril.setRevisionBy(userUtils.getCurrentUser());
//        clmPeril.setTransType("LO");
//        clmPeril.setPerilsDef(binderSectionPeril.getSubclassPeril().getPeril());
//        clmPeril.setTotalReserve(perilBean.getPerilEstimate());
//        clmPeril.setOriginalreserve(binderSectionPeril.getSubclassPeril().getClaimLimit());
//        clmPeril.setBinderSectionPerils(binderSectionPeril);
////        if(perilBean.getExpireSectionId()!=null) {
////            clmPeril.setExpiringSection(sectionTransRepo.findOne(perilBean.getExpireSectionId()));
////        }
////        if(perilBean.getSelfAsClaimant()==null ||perilBean.getSelfAsClaimant()=="" || "off".equalsIgnoreCase(perilBean.getSelfAsClaimant())){
////            if (claimClaimantsRepo.count(QClaimClaimants.claimClaimants.claimant.claimantId.eq(perilBean.getClaimantCode()
////            ).and(QClaimClaimants.claimClaimants.claimBookings.clmId.eq(booking.getClmId())))==0){
////                claimClaimant.setClaimAmount(perilBean.getPerilEstimate());
////                claimClaimant.setClaimantStatus("1");
////                claimClaimant.setClaimBookings(booking);
////                claimClaimant.setThirdParty("T");
////                ClaimantsDef claimantsDef = claimantDefRepo.findOne(perilBean.getClaimantCode());
////                claimClaimant.setClaimant(claimantsDef);
////            }else {
////                claimClaimant=  claimClaimantsRepo.findOne(QClaimClaimants.claimClaimants.claimant.claimantId.eq(perilBean.getClaimantCode()
////                ).and(QClaimClaimants.claimClaimants.claimBookings.clmId.eq(booking.getClmId())));
////                if (claimPerilsRepo.count(QClaimPerils.claimPerils.claimBookings.clmId.eq(booking.getClmId())
////                .and(QClaimPerils.claimPerils.binderSectionPerils.eq(binderSectionPeril)).and(QClaimPerils.claimPerils.clmClaimant.eq(claimClaimant)))>0){
////                    throw new BadRequestException("Peril being added already exists under this claimant");
////                }
////                claimClaimant.setClaimAmount((claimClaimant.getClaimAmount()!=null)?claimClaimant.getClaimAmount().add((perilBean.getPerilEstimate()!=null)?perilBean.getPerilEstimate():BigDecimal.ZERO):perilBean.getPerilEstimate());
////            }
////
////        }
////        else if("on".equalsIgnoreCase(perilBean.getSelfAsClaimant())) {
////            if (claimClaimantsRepo.count(QClaimClaimants.claimClaimants.client.tenId.eq(riskTrans.getInsured().getTenId()
////            ).and(QClaimClaimants.claimClaimants.claimBookings.clmId.eq(booking.getClmId())))==0) {
////                claimClaimant.setClaimAmount(perilBean.getPerilEstimate());
////                claimClaimant.setClaimantStatus("1");
////                claimClaimant.setClaimBookings(booking);
////                claimClaimant.setThirdParty("S");
////                claimClaimant.setClient(riskTrans.getInsured());
////            }else {
////                claimClaimant = claimClaimantsRepo.findOne(QClaimClaimants.claimClaimants.client.tenId.eq(riskTrans.getInsured().getTenId()
////                ).and(QClaimClaimants.claimClaimants.claimBookings.clmId.eq(booking.getClmId())));
////                if (claimPerilsRepo.count(QClaimPerils.claimPerils.claimBookings.clmId.eq(booking.getClmId())
////                        .and(QClaimPerils.claimPerils.binderSectionPerils.eq(binderSectionPeril)).and(QClaimPerils.claimPerils.clmClaimant.eq(claimClaimant)))>0){
////                    throw new BadRequestException("Peril being added already exists under this claimant");
////                }
////                claimClaimant.setClaimAmount((claimClaimant.getClaimAmount()!=null)?claimClaimant.getClaimAmount().add((perilBean.getPerilEstimate()!=null)?perilBean.getPerilEstimate():BigDecimal.ZERO):perilBean.getPerilEstimate());
////            }
////        }
////        clmPeril.setClmClaimant(claimClaimant);
////        claimClaimantsRepo.save(claimClaimant);
////        ClaimPerils savedPerils=claimPerilsRepo.save(clmPeril);
////        ClaimRevisions claimRevisions = new ClaimRevisions();
////        claimRevisions.setClaimBookings(booking);
////        claimRevisions.setRevAmount(perilBean.getPerilEstimate());
////        claimRevisions.setRevDate(new Date());
////        claimRevisions.setTransType("LR");
////        ClaimRevisions savedRevision = claimRevisionsRepo.save(claimRevisions);
////
////        ClaimRevisionTrans revisionTrans = new ClaimRevisionTrans();
////        revisionTrans.setAmount(perilBean.getPerilEstimate());
////        revisionTrans.setClaimPeril(savedPerils);
////        revisionTrans.setClaimRevision(savedRevision);
////        revisionTrans.setType(savedPerils.getType());
////        claimRevisionTransRepo.save(revisionTrans);
//    }


    @Override
    public ClaimBookings getOne(Long clmId) {
        return claimsBookingRepo.findOne(clmId);
    }

//    @Override
//    @Transactional(readOnly = true)
//    public ClaimDetailsDTO getClaimInformation(Long clmId) throws BadRequestException {
//        if (clmId == null) {
//            throw new BadRequestException("Error Getting Claim Information: clmId is null");
//        }
//        ClaimDetailsDTO booking = new ClaimDetailsDTO();
//        List<Object[]> detailsList = claimsBookingRepo.getClaimDetails(clmId);
//
//        // Log each item with its index
//        for (int i = 0; i < detailsList.size(); i++) {
//            Object[] details = detailsList.get(i);
//            log.info("detailsList[{}]: {}", i, Arrays.toString(details));
//        }
//
//        if (detailsList.isEmpty()) {
//            log.warn("No claim details found for clmId: {}", clmId);
//            return null; // Return null to trigger error in /claimtrans
//        }
//
//        Object[] details = detailsList.get(0);
//        try {
//            booking.setClmId(clmId);
//            booking.setClaimNo(details[1] != null ? (String) details[1] : null);
//            booking.setInsured(details[2] != null ? (String) details[2] : null);
//            booking.setLossDesc(details[3] != null ? (String) details[3] : null);
//            booking.setCausation(details[4] != null ? (String) details[4] : null);
//            booking.setLossDate(details[5] != null ? (Date) details[5] : null);
//            booking.setNextRvwDate(details[6] != null ? (Date) details[6] : null);
//            booking.setBookedDate(details[7] != null ? (Date) details[7] : null);
//            booking.setNotificationDate(details[8] != null ? (Date) details[8] : null);
//            booking.setLiabilityAdmission(details[9] != null ? (Boolean) details[9] : null);
//            booking.setPolicyNo(details[10] != null ? (String) details[10] : null);
//            booking.setClient(details[11] != null ? (String) details[11] : null);
//            booking.setProduct(details[12] != null ? (String) details[12] : null);
//            booking.setRiskId(details[13] != null ? (String) details[13] : null);
//            booking.setRiskValue(details[14] != null ? new BigDecimal(details[14].toString()) : null);
//            booking.setRiskWef(details[15] != null ? (Date) details[15] : null);
//            booking.setRiskWet(details[16] != null ? (Date) details[16] : null);
//            booking.setRiskBindId(details[17] != null ? ((BigInteger) details[17]).longValue() : null);
//            booking.setPolicyBindId(details[18] != null ? ((BigInteger) details[18]).longValue() : null);
//            booking.setClaimStatus(details[19] != null ? (String) details[19] : null);
//            booking.setClmRiskId(details[20] != null ? ((BigInteger) details[20]).longValue() : null);
//            booking.setApprovalStatus(details[21] != null ? (String) details[21] : null);
//            booking.setRiskIdentifier(details[22] != null ? (String) details[22] : null);
//            booking.setBalanceApprovedBy(details[23] != null ? (Long) details[23] : null);
//            booking.setBalanceApprovalDate(details[22] != null ? (Date) details[24] : null);
//
//            log.info("Mapped ClaimDetailsDTO - RiskIdentifier: {}", booking.getRiskIdentifier());
//
//            log.info("Mapped ClaimDetailsDTO for clmId={}: claimNo={}, insured={}, claimStatus={},riskIdentifier={}, approvalStatus={}",
//                    clmId, booking.getClaimNo(), booking.getInsured(), booking.getClaimStatus(), booking.getRiskIdentifier(),booking.getApprovalStatus());
//        } catch (Exception e) {
//            log.error("Error mapping ClaimDetailsDTO for clmId={}: {}", clmId, e.getMessage(), e);
//            throw new BadRequestException("Error mapping claim details for clmId: " + clmId);
//        }
//
//        return booking;
//    }


    @Override
    @Transactional(readOnly = true)
    public ClaimDetailsDTO getClaimInformation(Long clmId) throws BadRequestException {
        if (clmId == null) {
            throw new BadRequestException("Error Getting Claim Information: clmId is null");
        }
        ClaimDetailsDTO booking = new ClaimDetailsDTO();
        List<Object[]> detailsList = claimsBookingRepo.getClaimDetails(clmId);


        if (detailsList.isEmpty()) {

            return null; // Return null to trigger error in /claimtrans
        }

        Object[] details = detailsList.get(0);
        try {
            booking.setClmId(clmId);
            for (int i = 0; i < details.length; i++) {
                log.info("details[{}]: {}", i, details[i]);
            }
            booking.setClaimNo(details[1] != null ? (String) details[1] : null);
            booking.setInsured(details[2] != null ? (String) details[2] : null);
            booking.setLossDesc(details[3] != null ? (String) details[3] : null);
            booking.setCausation(details[4] != null ? (String) details[4] : null);
            booking.setLossDate(details[5] != null ? (Date) details[5] : null);
            booking.setNextRvwDate(details[6] != null ? (Date) details[6] : null);
            booking.setBookedDate(details[7] != null ? (Date) details[7] : null);
            booking.setNotificationDate(details[8] != null ? (Date) details[8] : null);
            booking.setLiabilityAdmission(details[9] != null ? (Boolean) details[9] : null);
            booking.setPolicyNo(details[10] != null ? (String) details[10] : null);
            booking.setClient(details[11] != null ? (String) details[11] : null);
            booking.setProduct(details[12] != null ? (String) details[12] : null);
            booking.setRiskId(details[13] != null ? (String) details[13] : null);
            booking.setRiskValue(details[14] != null ? new BigDecimal(details[14].toString()) : null);
            booking.setRiskWef(details[15] != null ? (Date) details[15] : null);
            booking.setRiskWet(details[16] != null ? (Date) details[16] : null);
            booking.setRiskBindId(details[17] != null ? ((BigInteger) details[17]).longValue() : null);
            booking.setPolicyBindId(details[18] != null ? ((BigInteger) details[18]).longValue() : null);
            booking.setClaimStatus(details[19] != null ? (String) details[19] : null);
            booking.setClmRiskId(details[20] != null ? ((BigInteger) details[20]).longValue() : null);
            booking.setApprovalStatus(details[21] != null ? (String) details[21] : null);
            booking.setRiskIdentifier(details[22] != null ? (String) details[22] : null);
            booking.setBalanceApprovedBy(details[23] != null ? (String) details[23] : null);
            booking.setBalanceApprovalDate(details[24] != null ? (Date) details[24] : null);
            booking.setRefNo(details[25] != null ? (String) details[25] : null);
            booking.setInsuranceName(details[26] != null ? (String) details[26] : null);
            booking.setInsBalance(details[28] != null ? (BigDecimal) details[28] : null);
            booking.setClientBalance(details[27] != null ? (BigDecimal) details[27] : null);

            // Fetch the taskId from MakerChecker if it exists
            MakerChecker makerChecker = makerCheckerRepo.findOne(QMakerChecker.makerChecker.taskCode.eq(clmId)
                    .and(QMakerChecker.makerChecker.taskType.eq("CL"))
                    .and(QMakerChecker.makerChecker.status.eq("N")));
            if (makerChecker != null) {
                booking.setTaskId(makerChecker.getId()); // Adjust to the correct getter if 'id' is not the field name
            } else {
                booking.setTaskId(null); // No task exists, set to null
            }


        } catch (Exception e) {
            log.error("Error mapping ClaimDetailsDTO for clmId={}: {}", clmId, e.getMessage(), e);
            throw new BadRequestException("Error mapping claim details for clmId: " + clmId);
        }

        return booking;
    }
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getClaimPerilsForEdit(Long claimId) {
        List<Map<String, Object>> perilsList = new ArrayList<>();

        try {

            List<ClaimPerils> claimPerils = claimPerilsRepo.findByClaimIdNative(claimId);

            log.info("Found {} claim perils for edit, claimId: {}", claimPerils.size(), claimId);

            for (ClaimPerils claimPeril : claimPerils) {
                Map<String, Object> perilData = new HashMap<>();

                if (claimPeril.getBinderSectionPerils() != null) {

                    perilData.put("perilCode", claimPeril.getBinderSectionPerils().getBspId());
                } else {
                    perilData.put("perilCode", "");
                }

                if (claimPeril.getPerilsDef() != null) {
                    perilData.put("perilName", claimPeril.getPerilsDef().getPerilDesc());
                } else {
                    perilData.put("perilName", "Unknown Peril");
                }

                perilData.put("perilEstimate", claimPeril.getReserve() != null ? claimPeril.getReserve() : 0);

                try {
                    if (claimPeril.getClmClaimant() != null) {
                        ClaimClaimants claimant = claimPeril.getClmClaimant();

                        if ("S".equals(claimant.getThirdParty())) {
                            perilData.put("selfAsClaimant", "on");

                            // Get insured name from the claim's risk
                            try {
                                ClaimBookings claimBooking = claimPeril.getClaimBookings();
                                if (claimBooking != null &&
                                        claimBooking.getRisk() != null &&
                                        claimBooking.getRisk().getInsured() != null) {


                                    ClientDef insured = claimBooking.getRisk().getInsured();

                                    String insuredName = insured.getFname();
                                    if (insured.getOtherNames() != null && !insured.getOtherNames().trim().isEmpty()) {
                                        insuredName += " " + insured.getOtherNames();
                                    }

                                    perilData.put("claimantName", insuredName);
                                } else {
                                    perilData.put("claimantName", "Self");
                                }
                            } catch (Exception e) {
                                perilData.put("claimantName", "Self");
                            }

                            perilData.put("claimantCode", "");
                        } else if ("T".equals(claimant.getThirdParty()) && claimant.getClaimant() != null) {
                            perilData.put("selfAsClaimant", "off");

                            ClaimantsDef thirdParty = claimant.getClaimant(); // Now correct type
                            String name = thirdParty.getSurname();
                            if (thirdParty.getOtherNames() != null && !thirdParty.getOtherNames().trim().isEmpty()) {
                                name += " " + thirdParty.getOtherNames();
                            }
                            perilData.put("claimantName", name);
                            perilData.put("claimantCode", thirdParty.getClaimantId());
                        } else {
                            perilData.put("selfAsClaimant", "off");
                            perilData.put("claimantName", "Unknown Claimant");
                            perilData.put("claimantCode", "");
                        }
                    } else {

                        perilData.put("selfAsClaimant", "off");
                        perilData.put("claimantName", "Unknown Claimant");
                        perilData.put("claimantCode", "");
                    }
                } catch (Exception e) {
                    perilData.put("selfAsClaimant", "off");
                    perilData.put("claimantName", "Unknown Claimant");
                    perilData.put("claimantCode", "");
                }

                perilsList.add(perilData);
            }


        } catch (Exception e) {

        }

        return perilsList;
    }

//    public ClaimDetailsDTO getClaimInformation(Long clmId) throws BadRequestException {
//        if(clmId==null) throw  new BadRequestException("Error Getting Claim Information");
//        ClaimDetailsDTO booking = new ClaimDetailsDTO();
//        List<Object[]> detailsList = claimsBookingRepo.getClaimDetails(clmId);
//        // Log each item with its index
//        for (int i = 0; i < detailsList.size(); i++) {
//            Object[] details = detailsList.get(i);
//            log.info("detailsList[{}]: {}", i, Arrays.toString(details));
//        }
//        if(!detailsList.isEmpty()){
//            Object[] details = detailsList.get(0);
//            booking.setClmId(clmId);
//            booking.setClaimNo((String) details[1]);
//            booking.setInsured((String) details[2]);
//            booking.setLossDesc((String) details[3]);
//            booking.setCausation((String) details[4]);
//            booking.setLossDate((Date) details[5]);
//            booking.setNextRvwDate((Date) details[6]);
//            booking.setBookedDate((Date) details[7]);
//            booking.setNotificationDate((Date) details[8]);
//            booking.setLiabilityAdmission((Boolean) details[9]);
//            booking.setPolicyNo((String)details[10]);
//            booking.setClient((String)details[11]);
//            booking.setProduct((String)details[12]);
//            booking.setRiskId((String)details[13]);
//            booking.setRiskValue((BigDecimal) details[14]);
//            booking.setRiskWef((Date) details[15]);
//            booking.setRiskWet((Date) details[16]);
//            booking.setRiskBindId(((BigInteger)details[17]).longValue());
//            booking.setPolicyBindId(((BigInteger)details[18]).longValue());
//            booking.setClaimStatus((String)details[19]);
//            booking.setClmRiskId(((BigInteger)details[20]).longValue());
//            booking.setApprovalStatus(((String)details[21]));
////            booking.setTotalReserve(claimRevisionsRepo.getClaimTotalRevisions(clmId));s
////            booking.setTotalPayments(claimRevisionsRepo.getTotalPayments(clmId));
////            booking.setOstReserve((claimRevisionsRepo.getClaimTotalRevisions(clmId).subtract(claimRevisionsRepo.getClaimTotalPayments(clmId))));
//            return booking;
//        }
//        else return booking;
//
//    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<ClaimEnquiryDTO> enquireClaims(DataTablesRequest request,
                                                           Long clientCode, String polNo, String riskId, String claimNo) throws IllegalAccessException {
        if (polNo == null || StringUtils.isBlank(polNo)) {
            polNo = "%";
        } else {
            polNo = "%" + polNo.toLowerCase() + "%";
        }

        if (claimNo == null || StringUtils.isBlank(claimNo)) {
            claimNo = "%";
        } else {
            claimNo = "%" + claimNo.toLowerCase() + "%";
        }

        if (riskId == null || StringUtils.isBlank(riskId)) {
            riskId = "%";
        } else {
            riskId = "%" + riskId.toLowerCase() + "%";
        }

        if (clientCode == null) {
            clientCode = -2000L;
        }

        List<Object[]> claimBookingsList = claimsBookingRepo.getClaimBookings(riskId, polNo, clientCode, claimNo, request.getPageNumber(), request.getPageSize());
        final List<ClaimEnquiryDTO> claimEnquiryDTOList = new ArrayList<>();
        long rowCount = 0L;
        if (!claimBookingsList.isEmpty()) rowCount = ((BigInteger) claimBookingsList.get(0)[19]).intValue();
        for(Object[] obj:claimBookingsList){
            ClaimEnquiryDTO enquiryDTO = new ClaimEnquiryDTO();
            enquiryDTO.setClmId(obj[0] != null ? ((BigInteger) obj[0]).longValue() : null);           // clm_id
            enquiryDTO.setClaimNo((String) obj[1]);                                                   // clm_no
            enquiryDTO.setUsername((String) obj[2]);                                                  // user_username
            enquiryDTO.setLossDate((Date) obj[3]);                                                    // clm_loss_date
            enquiryDTO.setClmDate((Date) obj[4]);                                                     // clm_date

            // Status mapping
            final String status = (String) obj[5];                                                    // clm_status
            if ("O".equalsIgnoreCase(status)) {
                enquiryDTO.setClmStatus("Open");
            } else if ("C".equalsIgnoreCase(status)) {
                enquiryDTO.setClmStatus("Closed");
            } else if ("B".equalsIgnoreCase(status)) {
                enquiryDTO.setClmStatus("Booked");
            } else {
                enquiryDTO.setClmStatus(status); // Keep original if not mapped
            }

            enquiryDTO.setRiskId((String) obj[6]);                                                    // risk_sht_desc
            enquiryDTO.setNextRevDate((Date) obj[7]);                                                 // clm_next_rvw_dt
            enquiryDTO.setPolicyNo((String) obj[8]);                                                  // pol_no

            // Client name combination
            String firstName = (String) obj[9];                                                       // client_fname
            String lastName = (String) obj[10];                                                       // client_onames
            enquiryDTO.setInsuredName(String.format("%s %s",
                    firstName != null ? firstName : "",
                    lastName != null ? lastName : "").trim());

            enquiryDTO.setRiskIdentifier(obj[11] != null ? obj[11].toString() : "");                 // risk_identifier
            enquiryDTO.setBalanceApprovedBy(obj[12] != null ? ((Number) obj[12]).longValue() : null); // balance_approved_by
            enquiryDTO.setBalanceApprovalDate(obj[13] != null ? (Date) obj[13] : null);              // balance_approval_date

            // NEW FIELDS from your updated query:
            enquiryDTO.setActualRiskId(obj[14] != null ? obj[14].toString() : "");                   // risk_id (actual ID)
            enquiryDTO.setProductName((String) obj[15]);                                             // pr_sht_desc (product name)
            String productName = (String) obj[15];
            if (productName != null && productName.toLowerCase().contains("motor")) {
                enquiryDTO.setProductType("Motor");
            } else {
                enquiryDTO.setProductType("Non Motor");
            }
            enquiryDTO.setBranchCode((String) obj[16]);                                              // acct_acc_code (branch code)
            enquiryDTO.setInsurerName((String) obj[17]);
            enquiryDTO.setInsurerClaimRef((String) obj[18]);   // acct_name (insurer name)

            // obj[18] is total_rows - used for pagination, not needed in DTO

            claimEnquiryDTOList.add(enquiryDTO);
        }

        Page<ClaimEnquiryDTO> page = new PageImpl<>(claimEnquiryDTOList, request, rowCount);
        return new DataTablesResult<>(request, page);
    }
    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<ClaimPerilReserveDTO> getClaimPerils(DataTablesRequest request,Long clmId) {
        List<Object[]> claimants = claimPerilsRepo.findClmPerils(clmId,request.getPageNumber(), request.getPageSize());
        long rowCount = 0L;
        if(!claimants.isEmpty()) rowCount = ((BigInteger)claimants.get(0)[6]).intValue();
        final List<ClaimPerilReserveDTO> claimPerilReserveDTOList = new ArrayList<>();
        for(Object[] claimant:claimants){
            ClaimPerilReserveDTO claimantsDTO = new ClaimPerilReserveDTO();
            claimantsDTO.setPerilDesc((String)claimant[0]);
            claimantsDTO.setType((String)claimant[1]);
            claimantsDTO.setLimitAmt((BigDecimal) claimant[2]);
            claimantsDTO.setExcessAmt((BigDecimal)claimant[3]);
            claimantsDTO.setRemarks((String)claimant[4]);
            claimantsDTO.setClmPerilId(((BigInteger)claimant[5]).longValue());
            claimPerilReserveDTOList.add(claimantsDTO);
        }
        Page<ClaimPerilReserveDTO>  page = new PageImpl<>(claimPerilReserveDTOList,request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<ClaimPaymentsDTO> getClaimPayments(DataTablesRequest request, Long clmId, Long sprId) {
        if(sprId==null){
            sprId = -2000L;
        }
        final String search = ( request.getSearch()!=null && request.getSearch().getValue()!=null)?"%"+request.getSearch().getValue()+"%":"%%";
        List<Object[]> payments = claimPaymentsRepo.getClmPayments(search.toLowerCase(),sprId, clmId,request.getPageNumber(), request.getPageSize());
        long rowCount = 0L;
        if(!payments.isEmpty()) rowCount = ((BigInteger)payments.get(0)[12]).intValue();
        final List<ClaimPaymentsDTO> paymentsDTOList = new ArrayList<>();
        for(Object[] payment:payments){
            ClaimPaymentsDTO paymentsDTO = new ClaimPaymentsDTO();
            paymentsDTO.setClmPymntId(((BigInteger)payment[0]).longValue());
            paymentsDTO.setPayee((String)payment[1]);
            paymentsDTO.setReference((String)payment[2]);
            paymentsDTO.setPaymentMode((String)payment[3]);
            paymentsDTO.setTransType((String)payment[4]);
            paymentsDTO.setCurrency((String)payment[5]);
            paymentsDTO.setAmount((BigDecimal) payment[6]);
            paymentsDTO.setStatus((String)payment[7]);
            paymentsDTO.setRaisedBy((String)payment[8]);
            paymentsDTO.setRaisedDate((Date) payment[9]);
            paymentsDTO.setAuthDate((Date) payment[10]);
            paymentsDTO.setAuthBy((String) payment[11]);
            paymentsDTOList.add(paymentsDTO);
        }
        Page<ClaimPaymentsDTO>  page = new PageImpl<>(paymentsDTOList,request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<ClaimPerilPayments> getPerilPayments(DataTablesRequest request,Long perilId) throws IllegalAccessException {
        BooleanExpression pred =QClaimPerilPayments.claimPerilPayments.claimPerils.clmPerilId.eq(perilId);
        Page<ClaimPerilPayments> page = perilPaymentsRepo.findAll(pred.and(request.searchPredicate(QClaimPerilPayments.claimPerilPayments)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<ClaimClaimantsDTO> getClaimClaimants(DataTablesRequest request, Long clmId) {
        List<Object[]> claimantsList = claimClaimantsRepo.findClmClaimants(clmId, request.getPageNumber(), request.getPageSize());
        List<ClaimClaimantsDTO> claimClaimantsDTOS = new ArrayList<>();
        long rowCount = 0L;

        if (!claimantsList.isEmpty()) {
            Object countObj = claimantsList.get(0)[12];
            rowCount = extractBigInteger(countObj).longValue();
        }

        for (Object[] claimants : claimantsList) {
            ClaimClaimantsDTO claimantsDTO = new ClaimClaimantsDTO();

            for (int i = 0; i < claimants.length; i++) {
                log.info("claimants[{}]: {}", i, claimants[i]);
            }

            claimantsDTO.setClaimantId(extractBigInteger(claimants[0]).longValue());
            claimantsDTO.setThirdParty((String) claimants[1]);
            claimantsDTO.setSelfClaimant((String) claimants[2]);
            claimantsDTO.setTpClaimant((String) claimants[3]);
            claimantsDTO.setClaimantStatus((String) claimants[4]);
            claimantsDTO.setCreatedDate((Date) claimants[5]);
            claimantsDTO.setCreatedBy((String) claimants[6]);
            claimantsDTO.setEstimatedAmount((BigDecimal) claimants[7]);
            claimantsDTO.setPeril((String) claimants[8]);
            claimantsDTO.setPerilType((String) claimants[9]);
            claimantsDTO.setCoverTypeDesc((String) claimants[11]);
            claimClaimantsDTOS.add(claimantsDTO);
        }
        Page<ClaimClaimantsDTO>  page = new PageImpl<>(claimClaimantsDTOS,request, rowCount);
        return new DataTablesResult<>(request, page);
    }
    private BigInteger extractBigInteger(Object obj) {
        if (obj instanceof BigInteger) {
            return (BigInteger) obj;
        } else if (obj instanceof String) {
            return new BigInteger((String) obj);
        } else if (obj instanceof Number) {
            return BigInteger.valueOf(((Number) obj).longValue());
        } else {
            throw new IllegalArgumentException("Unexpected type: " + (obj != null ? obj.getClass() : "null"));
        }
    }



    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<ClaimStatuses> getClaimStatuses(DataTablesRequest request, Long clmId) throws IllegalAccessException {
        BooleanExpression pred = QClaimStatuses.claimStatuses.claimBookings.clmId.eq(clmId);
        Page<ClaimStatuses> page = claimStatusesRepo.findAll(pred.and(request.searchPredicate(QClaimStatuses.claimStatuses)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<ClaimRequiredDocsDTO> getRequiredDocs(DataTablesRequest request, Long clmId) throws IllegalAccessException {
        final String search = (request.getSearch()!=null)?"%"+request.getSearch().getValue()+"%":"%%";
        List<Object[]> claimsDocs = claimRequiredDocsRepo.findClaimDocs(clmId,search,request.getPageNumber(), request.getPageSize());
        List<ClaimRequiredDocsDTO> requiredDocsDTOS = new ArrayList<>();
        long rowCount = 0l;
        if(!claimsDocs.isEmpty()) rowCount = ((BigInteger)claimsDocs.get(0)[8]).intValue();
        for(Object[] doc:claimsDocs){
            ClaimRequiredDocsDTO requiredDoc = new ClaimRequiredDocsDTO();
            requiredDoc.setClmRequiredId(((BigInteger)doc[0]).longValue());
            requiredDoc.setDocRefNo((String) doc[1]);
            requiredDoc.setFileName((String) doc[2]);
            requiredDoc.setDateReceived((Date) doc[3]);
            requiredDoc.setUsername((String) doc[4]);
            requiredDoc.setRemarks((String) doc[5]);
            requiredDoc.setDocName((String) doc[6]);
            requiredDoc.setClaimStatus((String) doc[7]);
            requiredDocsDTOS.add(requiredDoc);
        }
        Page<ClaimRequiredDocsDTO>  page = new PageImpl<>(requiredDocsDTOS,request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public void createclaimsRequiredDocs(RequiredDocBean requiredDocBean, Long clmId) {

        ClaimBookings claim = claimsBookingRepo.findOne(clmId);

        List<ClaimRequiredDocs> claimRequiredDocs =
                requiredDocBean.getRequiredDocs().stream().map(reqId -> {
                    ClaimRequiredDocs claimRequiredDoc = new ClaimRequiredDocs();
                    claimRequiredDoc.setRequiredDoc(requiredDocsRepo.findOne(reqId).getRequiredDoc());
                    claimRequiredDoc.setClaimBookings(claim);
//                    claimRequiredDoc.setUserReceived(userUtils.getCurrentUser());
                    return claimRequiredDoc;
                }).collect(Collectors.toList());
        claimRequiredDocsRepo.save(claimRequiredDocs);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<ClaimBookings> getClaimBookings(DataTablesRequest request, Long clmId) throws IllegalAccessException {
        BooleanExpression pred = QClaimBookings.claimBookings.clmId.eq(clmId);
        Page<ClaimBookings> page = claimsBookingRepo.findAll(pred.and(request.searchPredicate(QClaimBookings.claimBookings)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<ClaimUploadsDTO> getClaimUploads(DataTablesRequest request, Long clmId) throws IllegalAccessException {
        final String search = ( request.getSearch()!=null && request.getSearch().getValue()!=null)?"%"+request.getSearch().getValue()+"%":"%%";
        final List<ClaimUploadsDTO> claimUploadsDTOList = new ArrayList<>();
        List<Object[]> trans = uploadRepo.findSearchClaimUploads(clmId,search,request.getPageNumber(), request.getPageSize());
        long rowCount = 0L;
        if(!trans.isEmpty()) rowCount = ((BigInteger)trans.get(0)[7]).intValue();
        for(Object[] tran:trans){
            ClaimUploadsDTO claimUploadsDTO = new ClaimUploadsDTO();
            claimUploadsDTO.setUploadId(((BigInteger)tran[0]).longValue());
            claimUploadsDTO.setFileId((String) tran[1]);
            claimUploadsDTO.setFileName((String) tran[2]);
            claimUploadsDTO.setDateUploaded((Date) tran[3]);
            claimUploadsDTO.setUploadedComment((String) tran[4]);
            claimUploadsDTO.setUploadedBy((String) tran[5]);
            claimUploadsDTO.setClaimStatus((String) tran[6]);
            claimUploadsDTOList.add(claimUploadsDTO);
        }
        Page<ClaimUploadsDTO>  page = new PageImpl<>(claimUploadsDTOList,request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<ClaimActivityDTO> getClaimAcitivities(DataTablesRequest request, Long clmId) throws IllegalAccessException {
        final String search = ( request.getSearch()!=null && request.getSearch().getValue()!=null)?"%"+request.getSearch().getValue()+"%":"%%";
        final List<ClaimActivityDTO> activityDTOList = new ArrayList<>();
        List<Object[]> trans = activitiesRepo.getClmActivities(clmId,request.getPageNumber(), search,request.getPageSize());
        long rowCount = 0L;
        if(!trans.isEmpty()) rowCount = ((BigInteger)trans.get(0)[7]).intValue();
        for(Object[] tran:trans){
            ClaimActivityDTO activityDTO = new ClaimActivityDTO();
            activityDTO.setActivityId(((BigInteger)tran[0]).longValue());
            activityDTO.setActivityDesc((String) tran[1]);
            activityDTO.setUsername((String) tran[2]);
            activityDTO.setActivityDate((Date) tran[3]);
            activityDTO.setCurrentActivity((String) tran[4]);
            activityDTO.setRemDate((Date) tran[5]);
//            activityDTO.setActivityNotes((String) tran[6]);
            String rawNotesJson = (String) tran[6];
            String formattedNotes = formatNotesFromJson(rawNotesJson);
            activityDTO.setActivityNotes(formattedNotes);
            activityDTOList.add(activityDTO);
        }
        Page<ClaimActivityDTO>  page = new PageImpl<>(activityDTOList,request, rowCount);
        return new DataTablesResult<>(request, page);

    }
    private String formatNotesFromJson(String notesJson) {
        if (notesJson == null || notesJson.trim().isEmpty()) {
            return "";
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<ClaimActivities.ActivityNote> notes = objectMapper.readValue(notesJson,
                    new TypeReference<List<ClaimActivities.ActivityNote>>() {});

            if (notes.isEmpty()) {
                return "";
            }
        StringBuilder formatted = new StringBuilder();
        for (ClaimActivities.ActivityNote note : notes) {
            formatted.append(String.format("[%tF %<tT by %s] %s%n",
                note.getTimestamp(), note.getUserName(), note.getNote()));
        }
        return formatted.toString();
        } catch (Exception e) {
            return notesJson;
        }
    }
    @Override
    public DataTablesResult<ClaimAuditLogDTO> getClaimAuditLogs(DataTablesRequest request, Long clmId) throws IllegalAccessException {
        final String search = (request.getSearch() != null && request.getSearch().getValue() != null) ?
                "%" + request.getSearch().getValue() + "%" : "%%";
        final List<ClaimAuditLogDTO> auditLogDTOList = new ArrayList<>();
        List<Object[]> logs = claimsBookingRepo.getClaimAuditLogs(clmId, request.getPageNumber(), search, request.getPageSize());
        long rowCount = 0L;
        if (!logs.isEmpty()) rowCount = ((BigInteger) logs.get(0)[12]).longValue(); // total_rows is now at index 11

        for (Object[] log : logs) {
            ClaimAuditLogDTO auditLogDTO = new ClaimAuditLogDTO();
            auditLogDTO.setClaimId(((BigInteger) log[0]).longValue());
            auditLogDTO.setClaimNo((String) log[1]);
            auditLogDTO.setEventType((String) log[2]);
            auditLogDTO.setEventDate((Date) log[3]);

            // Handle event_user_id
            if (log[4] != null) {
                if (log[4] instanceof String) {
                    try {
                        auditLogDTO.setEventUserId(Long.parseLong((String) log[4]));
                    } catch (NumberFormatException e) {
                        auditLogDTO.setEventUserId(null);
                    }
                } else if (log[4] instanceof BigInteger) {
                    auditLogDTO.setEventUserId(((BigInteger) log[4]).longValue());
                } else if (log[4] instanceof Long) {
                    auditLogDTO.setEventUserId((Long) log[4]);
                }
            } else {
                auditLogDTO.setEventUserId(null);
            }
            auditLogDTO.setUsername((String) log[5]);
            auditLogDTO.setEventDescription((String) log[6]);
            auditLogDTO.setMakerMadeOn((Date) log[7]);
            auditLogDTO.setCheckerCheckedOn((Date) log[8]);
            auditLogDTO.setAction((String) log[9]);
            auditLogDTO.setRejectedReason((String) log[10]);
            auditLogDTO.setResubmissionComment((String) log[11]);
            auditLogDTOList.add(auditLogDTO);
        }

        Page<ClaimAuditLogDTO> page = new PageImpl<>(auditLogDTOList, request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void addActivity(ClaimActivities activity) throws BadRequestException {

        if (activity.getActivityId()==null){
            Iterable<ClaimActivities> currentActivities = activitiesRepo.findAll(QClaimActivities.claimActivities.claimBookings.clmId.eq(activity.getClaimBookings().getClmId()));
            for(ClaimActivities curactivity:currentActivities){
                if(curactivity.getRemDate().after(activity.getRemDate()))
                    throw new BadRequestException("Error creating activity. Check reminder date");
                if(curactivity.getActivity().getCaId()==activity.getActivity().getCaId())
                    throw new BadRequestException("Activity already exists..Select another activity");
                curactivity.setCurrentActivity("N");
            }
                activitiesRepo.save(currentActivities);
                activity.setCurrentActivity("Y");
                activity.setUserCreated(userUtils.getCurrentUser());
                activity.setActivityDate(new Date());
        }
        else {
            ClaimActivities preActivity = activitiesRepo.findOne(QClaimActivities.claimActivities.activityId.eq(activity.getActivityId()));
            activity.setCurrentActivity(preActivity.getCurrentActivity());
            activity.setUserCreated(preActivity.getUserCreated());
            activity.setActivityDate(preActivity.getActivityDate());
        }
        activitiesRepo.save(activity);
    }
    @Override
    @Transactional(rollbackFor = BadRequestException.class)
    public void saveDvProgress(ClaimStatuses claimStatuses, Long clmId) throws BadRequestException {
        ClaimBookings claimBookings = claimsBookingRepo.findOne(clmId);
        if (claimBookings == null) {
            throw new BadRequestException("Claim not found");
        }


        claimStatuses.setClaimBookings(claimBookings);
        claimStatuses.setCapturedBy(userUtils.getCurrentUser());
        claimStatuses.setDateCaptured(new Date());


        claimStatusesRepo.save(claimStatuses);


        log.info("DV progress saved for claim: {}", claimBookings.getClaimNo());
    }
    @Override
    @Transactional(rollbackFor = BadRequestException.class)
    public ClaimStatuses getLatestDvData(Long clmId) {
        log.info("Fetching latest DV data for claim ID: {}", clmId);

        // Look for any DV-related data, not just closed claims
        Iterator<ClaimStatuses> result = claimStatusesRepo.findAll(
                        QClaimStatuses.claimStatuses.claimBookings.clmId.eq(clmId)
                                .and(QClaimStatuses.claimStatuses.closeReason.eq("DV")
                                        .or(QClaimStatuses.claimStatuses.dvIssuanceDate.isNotNull())
                                        .or(QClaimStatuses.claimStatuses.dvOfferAmount.isNotNull())
                                        .or(QClaimStatuses.claimStatuses.clientDecision.isNotNull())),
                        QClaimStatuses.claimStatuses.dateCaptured.desc())
                .iterator();

        ClaimStatuses latestStatus = result.hasNext() ? result.next() : null;

        if (latestStatus != null) {
            log.info("Found latest DV status for claim {}: DV issuance date: {}, offer amount: {}, client decision: {}",
                    clmId,
                    latestStatus.getDvIssuanceDate(),
                    latestStatus.getDvOfferAmount(),
                    latestStatus.getClientDecision());
        } else {
            log.info("No DV status found for claim: {}", clmId);
        }

        return latestStatus;
    }

    @Override
    @Transactional(rollbackFor = BadRequestException.class)
    public void addClaimStatus(ClaimStatuses claim, Long clmId) throws BadRequestException {
        // Find all current statuses for the claim
        Iterable<ClaimStatuses> currentStatuses = claimStatusesRepo.findAll(
                QClaimStatuses.claimStatuses.claimBookings.clmId.eq(clmId)
        );

      // Find the claim booking
                ClaimBookings bookings = claimsBookingRepo.findOne(clmId);
                if (bookings == null) {
                    throw new BadRequestException("Claim booking not found for ID: " + clmId);
                }
        if (!"A".equals(bookings.getApprovalStatus())) {
            throw new BadRequestException("Claim must be approved before updating status");
        }

        // Mark existing statuses as inactive
        for (ClaimStatuses curStatus : currentStatuses) {
            curStatus.setCurrentActivity("N");
        }
        claimStatusesRepo.save(currentStatuses);

        // Validate mandatory fields
        if (claim.getRemarks() == null || claim.getRemarks().isEmpty()) {
            throw new BadRequestException("Remarks are required");
        }
        if (claim.getNewStatus() == null) {
            throw new BadRequestException("New status must be provided");
        }
        if (claim.getNewStatus().equalsIgnoreCase("C") && claim.getCloseReason() == null) {
            throw new BadRequestException("Reason for closure must be provided");
        }

        // Handle payment mode and settlement type for settled claims
//        if (claim.getNewStatus().equalsIgnoreCase("C") && "ST".equalsIgnoreCase(claim.getCloseReason())) {
//            if (claim.getPaymentId() == null) {
//                throw new BadRequestException("Payment mode must be selected for settled claims");
//            }
//
//          PaymentModes paymentMode = paymentModeRepo.findOne(claim.getPaymentId());
//            if (paymentMode == null) {
//                throw new BadRequestException("Invalid payment mode ID: " + claim.getPaymentId());
//            }
//            claim.setPaymentMode(paymentMode);
//
//            if (claim.getSettlementType() == null || claim.getSettlementType().isEmpty()) {
//                throw new BadRequestException("Type of settlement must be provided for settled claims");
//            }
//        }

        // Handle claim closure or reopening
        if (claim.getNewStatus().equalsIgnoreCase("C") || claim.getNewStatus().equalsIgnoreCase("R")) {
            Iterable<ClaimPerils> perils = claimPerilsRepo.findAll(
                    QClaimPerils.claimPerils.claimBookings.eq(bookings)
                            .and(QClaimPerils.claimPerils.expiringSection.isNotNull())
            );
            boolean isSettled = claim.getCloseReason() != null && "ST".equalsIgnoreCase(claim.getCloseReason());
            for (ClaimPerils peril : perils) {
                SectionTrans sectionTrans = peril.getExpiringSection();
                sectionTrans.setExpired(isSettled ? "Y" : "N");
                sectionTransRepo.save(sectionTrans);
            }
        }

        // Set properties for the new claim status
        claim.setCurrentActivity("Y");
        claim.setCurrentStatus(claim.getNewStatus());
        claim.setOldStatus(bookings.getClaimStatus());
        claim.setCloseReason(getFullClosureReason(claim.getCloseReason())); // Convert to full description
        claim.setClaimBookings(bookings);
        claim.setCapturedBy(userUtils.getCurrentUser());
        claim.setDateCaptured(new Date());

        // Save the new claim status
        claimStatusesRepo.save(claim);

        // Update the claim bookings status
        bookings.setClaimStatus(claim.getNewStatus());
        claimsBookingRepo.save(bookings);
    }

    private String getFullClosureReason(String closeReason) {
        if (closeReason == null) return null;

        switch (closeReason.toUpperCase()) {
            case "ST": return "Closed As Settled";
            case "RJ": return "Closed As Rejected";
            case "NC": return "Closed As No Claim";
            case "RC": return "Repairs completed/Release letter issued";
            case "DV": return "CIL DV Issued";
            case "CD": return "Claim repudiated/Declined";
            default: return closeReason; // Return as-is if unknown
        }
    }


//    @Override
//    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
//    public void addClaimStatus(ClaimStatuses claimStatuses, Long clmId) throws BadRequestException {
//        Iterable<ClaimStatuses> currentStatuses = claimStatusesRepo.findAll(QClaimStatuses.claimStatuses.claimBookings.clmId.eq(clmId));
//        ClaimBookings bookings = claimsBookingRepo.findOne(clmId);
//        for (ClaimStatuses curStatus : currentStatuses) {
//            curStatus.setCurrentActivity("N");
//        }
//        if (claimStatuses.getRemarks() == null) {
//            throw new BadRequestException("Remarks must be provided");
//        }
//        if (claimStatuses.getNewStatus() == "C" && claimStatuses.getCloseReason() == null) {
//            throw new BadRequestException("Reason for Closure must be provided");
//        }
//
//        if (claimStatuses.getNewStatus().equalsIgnoreCase("C") && "ST".equalsIgnoreCase(claimStatuses.getCloseReason())) {
//            if (claimStatuses.getPaymentMode() == null) {
//                throw new BadRequestException("Payment mode must be selected for settled claims");
//            }
//            if (claimStatuses.getSettlementType() == null || claimStatuses.getSettlementType().isEmpty()) {
//                throw new BadRequestException("Type of settlement must be provided for settled claims");
//            }
//        }
//        if (claimStatuses.getNewStatus() == null) {
//            throw new BadRequestException("Error changing claims status");
//        }
//
//        if (claimStatuses.getNewStatus().equalsIgnoreCase("C")|| claimStatuses.getNewStatus().equalsIgnoreCase("R")) {
//            Iterable<ClaimPerils> perils = claimPerilsRepo.findAll(QClaimPerils.claimPerils.claimBookings.eq(bookings)
//                    .and(QClaimPerils.claimPerils.expiringSection.isNotNull()));
//            if (claimStatuses.getCloseReason()!=null && claimStatuses.getCloseReason().equalsIgnoreCase("ST")) {
//                for (ClaimPerils perils1 : perils) {
//                    SectionTrans sectionTrans = perils1.getExpiringSection();
//                    sectionTrans.setExpired("Y");
//                    sectionTransRepo.save(sectionTrans);
//                }
//            } else {
//                for (ClaimPerils perils1 : perils) {
//                    SectionTrans sectionTrans = perils1.getExpiringSection();
//                    sectionTrans.setExpired("N");
//                    sectionTransRepo.save(sectionTrans);
//                }
//            }
//        }
//        claimStatusesRepo.save(currentStatuses);
//        claimStatuses.setCurrentActivity("Y");
//        claimStatuses.setCurrentStatus(claimStatuses.getNewStatus());
//        claimStatuses.setCloseReason(claimStatuses.getCloseReason());
//        claimStatuses.setOldStatus(bookings.getClaimStatus());
//        claimStatuses.setClaimBookings(bookings);
//        claimStatuses.setCapturedBy(userUtils.getCurrentUser());
//        claimStatuses.setDateCaptured(new Date());
//        claimStatusesRepo.save(claimStatuses);
//        bookings.setClaimStatus(claimStatuses.getNewStatus());
//        claimsBookingRepo.save(bookings);
//
//
//    }

//    @Override
//    public ClaimBalanceBean getBalance(Long clmId) {
//        List<Object[]> result = Collections.singletonList(transRepo.getClaimDetails(clmId));
//        ClaimBalanceBean balanceBean = new ClaimBalanceBean();
//        for (Object[] object : result) {
//                if(object[1]!=null){
//                    balanceBean.setInsBalance((BigDecimal)object[1]);
//                }
//            if(object[2]!=null){
//                balanceBean.setClientBalance((BigDecimal)object[2]);
//            }
//        }
//
//        return balanceBean;
//    }

@Override
public ClaimBalanceBean getBalance(String polNo) {
    List<Object[]> results = transRepo.getClaimDetails(polNo);

    ClaimBalanceBean balanceBean = new ClaimBalanceBean();

    if (results != null && !results.isEmpty()) {
        Object[] row = results.get(0);  // Only expecting one row per polNo
        String polNoResult = (String) row[0];
        BigDecimal totalBalance = (BigDecimal) row[1];

        // Set balance in bean
        balanceBean.setClientBalance(totalBalance);


        // Print the total balance
        System.out.println("Total Balance for " + polNoResult + ": " + totalBalance);
    } else {
        System.out.println("No results found for polNo: " + polNo);
    }

    return balanceBean;
}



    @Override
    @Transactional(readOnly = true)
    public void validateCoverPeriod(Long policyId, Date lossDate) throws BadRequestException {

        BooleanExpression pred = QPolicyTrans.policyTrans.policyId.eq(policyId);
        PolicyTrans policy = policyRepo.findOne(pred);

        if (policy == null) {
            throw new BadRequestException("Policy not found");
        }

        Date startDate = policy.getWefDate();
        Date endDate = policy.getWetDate();

        if (startDate == null || endDate == null) {
            throw new BadRequestException("Policy cover period is invalid");
        }

        if (lossDate.before(startDate) || lossDate.after(endDate)) {
            throw new BadRequestException("Loss date must be within policy cover period");
        }
    }

    @Override
    public Set<SectionTransBean> getExpireSections(Long perilId, Long riskId) {
        Set<SectionTransBean> toExpireSection = new HashSet<>();
        List<Object[]> sections = sectionTransRepo.findExpireSection(riskId, perilId);
        List<SectionTransBean> sectionTrans = new ArrayList<>();
        for (Object[] sectran:sections){
            SectionTransBean  sect = new SectionTransBean();
            if(sectran[0] instanceof  BigInteger){
                sect.setSectId(((BigInteger)sectran[0]).longValue());
            }
            else  if(sectran[0] instanceof  BigDecimal){
                sect.setSectId(((BigDecimal)sectran[0]).longValue());
            }
            sect.setSection(sectran[1].toString());
            sectionTrans.add(sect);
        }
        for (SectionTransBean  sect : sectionTrans) {
            SectionTrans sectionTrans1 = sectionTransRepo.findOne(sect.getSectId());
            if (sectionTrans1.getSection().getType().getCode().equalsIgnoreCase("SI")) {
                SectionTrans excess = sectionTransRepo.findOne(QSectionTrans.sectionTrans.risk.riskId.eq(riskId)
                        .and(QSectionTrans.sectionTrans.section.shtDesc.containsIgnoreCase("EX")));
                if(excess!=null){
                    sect.setSectId(excess.getSectId());
                    sect.setSection(excess.getSection().getDesc());
                    toExpireSection.add(sect);
                }
            } else toExpireSection.add(sect);
        }
        return toExpireSection;
    }

    @Override
    public DataTablesResult<ClaimsTransDto> getClaimTransactions(DataTablesRequest request, Long clmId)  {
        final List<ClaimsTransDto> claimTransList = new ArrayList<>();
        List<Object[]> trans = claimRevisionsRepo.getClaimTransactions(clmId,request.getPageNumber(), request.getPageSize());
        long rowCount = 0L;
        if(!trans.isEmpty()) rowCount = ((BigInteger)trans.get(0)[8]).intValue();
        for(Object[] tran:trans){
            ClaimsTransDto claimsTransDto = new ClaimsTransDto();
            claimsTransDto.setTransId(((BigInteger)tran[0]).longValue());
            claimsTransDto.setTransAmount((BigDecimal) tran[1]);
            claimsTransDto.setTransDate((Date) tran[2]);
            final String type = (String) tran[3];;
            if(type.equalsIgnoreCase("CP"))
                claimsTransDto.setTransType("Claimant Payment");
            else if(type.equalsIgnoreCase("SP"))
                claimsTransDto.setTransType("Service Provider Payment");
            claimsTransDto.setAuthDate((Date) tran[4]);
            claimsTransDto.setCreatedBy((String) tran[5]);
            claimsTransDto.setAuthBy((String) tran[6]);
            final String status=(String) tran[7];
            if(status==null || status.equalsIgnoreCase("N"))
            claimsTransDto.setTransStatus("Draft");
            else if(status.equalsIgnoreCase("R")){
                claimsTransDto.setTransStatus("Ready");
            }
            else if(status.equalsIgnoreCase("P")){
                claimsTransDto.setTransStatus("Pending Finance");
            }
            else if(status.equalsIgnoreCase("A")){
                claimsTransDto.setTransStatus("Authorised");
            }
            claimTransList.add(claimsTransDto);
        }
        Page<ClaimsTransDto>  page = new PageImpl<>(claimTransList,request,rowCount);
        return new DataTablesResult<>(request, page);
    }

    public void makeReady(Long transId) {
        ClaimPayments revisionTrans = claimPaymentsRepo.findOne(transId);
        revisionTrans.setAuthorised("R");
        claimPaymentsRepo.save(revisionTrans);
    }

    @Override
    public void makeUndo(Long transId) {
        ClaimPayments revisionTrans = claimPaymentsRepo.findOne(transId);
        revisionTrans.setAuthorised(null);
        claimPaymentsRepo.save(revisionTrans);
    }

    @Override
    @Transactional(rollbackFor = BadRequestException.class)
    public void authoriseTransaction(Long transId) throws BadRequestException {

        ClaimPayments revisionTrans = claimPaymentsRepo.findOne(transId);
        revisionTrans.setAuthorised("P");
        revisionTrans.setAuthDate(new Date());
        revisionTrans.setAuthBy(userUtils.getCurrentUser());
        claimPaymentsRepo.save(revisionTrans);


        final ChequeTransDTO chequeTransDTO = new ChequeTransDTO();
        chequeTransDTO.setInvoiceNo(revisionTrans.getInvoiceNo());
        chequeTransDTO.setSource("CLM");
        chequeTransDTO.setPaymentType("GL");
        chequeTransDTO.setSourcePostedUser(userUtils.getCurrentUser().getId());
        chequeTransDTO.setSourcePostedDate(new Date());
        chequeTransDTO.setAmount(revisionTrans.getClmPymntAmount());
        chequeTransDTO.setOriginType("PYMT");
        chequeTransDTO.setRefNo(""+transId);
        List<Object[]> pymtDetails = claimPaymentsRepo.getPymentDetails(transId);
        Long subclassId = null;
        for(Object[] detail:pymtDetails){
            final Long payeeId = ((BigDecimal) detail[0]).longValue();
            final Long curId = ((BigDecimal) detail[1]).longValue();
            final Long pmId = ((BigDecimal) detail[2]).longValue();
            final Long branchId = ((BigDecimal) detail[3]).longValue();
            final Long acctId = ((BigDecimal) detail[4]).longValue();
           subclassId = ((BigDecimal) detail[5]).longValue();
            chequeTransDTO.setPayee(payeeId);
            chequeTransDTO.setCurId(curId);
            chequeTransDTO.setPaymentModeId(pmId);
            chequeTransDTO.setBranchCode(branchId);
            chequeTransDTO.setBankActCode(acctId);
            chequeTransDTO.setNarration("Claim Payment for "+detail[6]);
        }
        chequeTransDTO.setRequistionDate(new Date());
        chequeTransDTO.setInvoiceDate(revisionTrans.getInvoiceDate());

        final List<ChequeTransDtlsDTO> chequeTransList = new ArrayList<>();
        List<BigDecimal> amounts = claimPaymentsRepo.findPymentDetailsAmounts(transId);
        for(BigDecimal amount: amounts){
            ChequeTransDtlsDTO dtlsDTO = new ChequeTransDtlsDTO();
            dtlsDTO.setDrcr("D");
            dtlsDTO.setTransAmount(amount);
            dtlsDTO.setBranchCode(chequeTransDTO.getBranchCode());
            //dtlsDTO.setGlId(accountsUtilities.getGlDebitAccount(RevenueItems.CPL,subclassId).getCoId());
            dtlsDTO.setNarrative(chequeTransDTO.getNarration()+" of Payment of "+amount);
            chequeTransList.add(dtlsDTO);
        }
        chequeTransDTO.setGlTrans(chequeTransList);
        paymentService.createRequistion(chequeTransDTO);
    }

    @Override
    public Page<ServiceProviderTypesDTO> findServiceProviderTypes(String searchValue, Pageable pageable) {
        final List<ServiceProviderTypesDTO> serviceProviderTypes = new ArrayList<>();
        searchValue  = (searchValue!=null)?"%"+searchValue.toLowerCase()+"%":"%%";
        List<Object[]> types = serviceProviderTypesRepo.findServProviderTypes(searchValue,pageable.getPageNumber(), pageable.getPageSize());
        long rowCount = 0L;
        if(!types.isEmpty()) rowCount = ((BigInteger)types.get(0)[2]).intValue();
        for(Object[] tran:types){
            ServiceProviderTypesDTO typesDTO = new ServiceProviderTypesDTO();
            typesDTO.setTypeId(((BigInteger) tran[0]).longValue());
            typesDTO.setProviderType((String) tran[1]);
            serviceProviderTypes.add(typesDTO);
        }
        return new PageImpl<>(serviceProviderTypes,pageable, rowCount);
    }

    @Override
    public Page<ServiceProviderDTO> findServiceProviders(String searchValue, Pageable pageable) {
        final List<ServiceProviderDTO> serviceProviderTypes = new ArrayList<>();
        searchValue  = (searchValue!=null)?"%"+searchValue.toLowerCase()+"%":"%%";
        List<Object[]> types = serviceProviderRepo.findServProvidersLov(searchValue,pageable.getPageNumber(), pageable.getPageSize());
        long rowCount = 0L;
        if(!types.isEmpty()) rowCount = ((BigInteger)types.get(0)[2]).intValue();
        for(Object[] tran:types){
            ServiceProviderDTO typesDTO = new ServiceProviderDTO();
            typesDTO.setProviderId(((BigInteger) tran[0]).longValue());
            typesDTO.setName((String) tran[1]);
            serviceProviderTypes.add(typesDTO);
        }
        return new PageImpl<>(serviceProviderTypes,pageable, rowCount);
    }

    @Override
    public void createServiceProviderTypes(ServiceProviderTypesDTO serviceProviderTypes) throws BadRequestException {
        if(serviceProviderTypes.getProviderType()==null && serviceProviderTypes.getProviderType().length() < 5){
            throw new BadRequestException("Enter Valid Service Provider Type");
        }
        ServiceProviderTypes providerTypes;
        if(serviceProviderTypes.getTypeId()!=null){
            providerTypes = serviceProviderTypesRepo.findOne(serviceProviderTypes.getTypeId());
            if(providerTypes==null) {
                throw new BadRequestException("Invalid Service Provider to update...");
            }
        }
        else{
            long count = serviceProviderTypesRepo.countExactServProviderTypes(serviceProviderTypes.getProviderType());
            if(count==1){
                throw new BadRequestException("Service Provider Exists....");
            }
            providerTypes = new ServiceProviderTypes();
        }
        providerTypes.setProviderType(serviceProviderTypes.getProviderType());
        serviceProviderTypesRepo.save(providerTypes);
    }

    @Override
    public void createServiceProviders(ServiceProviderDTO serviceProviderDTO) throws BadRequestException {
        if(serviceProviderDTO.getProviderTypeId()==null){
            throw new BadRequestException("Invalid Service Provider Type...");
        }
        ServiceProviderTypes providerTypes = serviceProviderTypesRepo.findOne(serviceProviderDTO.getProviderTypeId());
        ServiceProviderDef serviceProviderDef = null;
        if(serviceProviderDTO.getProviderId()!=null){
            serviceProviderDef = serviceProviderRepo.findOne(serviceProviderDTO.getProviderId());
            if(serviceProviderDef==null){
                throw new BadRequestException("Invalid Service Provider to update...");
            }
        }
        else{
            serviceProviderDef = new ServiceProviderDef();
        }
        serviceProviderDef.setCreatedDate(new Date());
        serviceProviderDef.setEmail(serviceProviderDTO.getEmail());
        serviceProviderDef.setName(serviceProviderDTO.getName());
        serviceProviderDef.setPhoneNumber(serviceProviderDTO.getPhoneNumber());
        serviceProviderDef.setCreatedUser(userUtils.getCurrentUser());
        serviceProviderDef.setProviderTypes(providerTypes);
        serviceProviderRepo.save(serviceProviderDef);
    }

    @Override
    public DataTablesResult<ServiceProviderDTO> getServiceProviders(Long id,DataTablesRequest request) {
        final String search = ( request.getSearch()!=null && request.getSearch().getValue()!=null)?"%"+request.getSearch().getValue()+"%":"%%";
        final List<ServiceProviderDTO> serviceProviders = new ArrayList<>();
        List<Object[]> trans = serviceProviderRepo.findServProviders(search,id,request.getPageNumber(), request.getPageSize());
        long rowCount = 0L;
        if(!trans.isEmpty()) rowCount = ((BigInteger)trans.get(0)[7]).intValue();
        for(Object[] tran:trans){
            ServiceProviderDTO serviceProviderDTO = new ServiceProviderDTO();
            serviceProviderDTO.setProviderId(((BigInteger)tran[0]).longValue());
            serviceProviderDTO.setEmail((String) tran[1]);
            serviceProviderDTO.setName((String) tran[2]);
            serviceProviderDTO.setPhoneNumber((String) tran[3]);
            serviceProviderDTO.setCreatedDate((Date) tran[4]);
            serviceProviderDTO.setProviderTypeId(((BigInteger)tran[5]).longValue());
            serviceProviderDTO.setCreatedBy((String) tran[6]);
            serviceProviders.add(serviceProviderDTO);
        }
        Page<ServiceProviderDTO>  page = new PageImpl<>(serviceProviders,request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public void deleteServiceProvider(Long providerId) throws BadRequestException {
        ServiceProviderDef serviceProviderDef = serviceProviderRepo.findOne(providerId);
        if(serviceProviderDef==null){
            throw new BadRequestException("Invalid Service Provider to delete...");
        }
        serviceProviderRepo.delete(providerId);
    }

    @Override
    public void deleteServiceProviderType(Long providerId) throws BadRequestException {
        ServiceProviderTypes serviceProviderDef = serviceProviderTypesRepo.findOne(providerId);
        if(serviceProviderDef==null){
            throw new BadRequestException("Invalid Service Provider Type to delete...");
        }
        serviceProviderTypesRepo.delete(providerId);
    }

    @Override
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void cleanupOldDrafts() {
        try {
            // Delete drafts older than 7 days
            int daysOld = 7;
            Date cutoffDate = new Date(System.currentTimeMillis() - (daysOld * 24L * 60 * 60 * 1000));

            log.info("Starting cleanup of draft claims older than {} days (before {})", daysOld, cutoffDate);

            // Find old draft claims
            Predicate draftPredicate = QClaimBookings.claimBookings.claimStatus.eq("D")
                    .and(QClaimBookings.claimBookings.bookedDate.before(cutoffDate));

            Iterable<ClaimBookings> oldDrafts = claimsBookingRepo.findAll(draftPredicate);

            int deletedCount = 0;
            for (ClaimBookings draft : oldDrafts) {
                try {
                    deleteDraftClaimSimple(draft.getClmId());
                    deletedCount++;
                    log.debug("Deleted draft claim: {}", draft.getClmId());
                } catch (Exception e) {
                    log.error("Error deleting draft claim {}: {}", draft.getClmId(), e.getMessage());
                }
            }

            log.info("Cleanup completed. Deleted {} old draft claims", deletedCount);

        } catch (Exception e) {
            log.error("Error during draft cleanup: {}", e.getMessage(), e);
        }
    }
    private void deleteDraftClaimSimple(Long claimId) throws Exception {
        try {
            ClaimBookings claim = claimsBookingRepo.findOne(claimId);
            if (claim == null || !"D".equals(claim.getClaimStatus())) {
                log.warn("Claim {} is not a draft or doesn't exist, skipping deletion", claimId);
                return;
            }

            log.debug("Deleting draft claim {} and related records", claimId);

            // Delete related records one by one

            // 1. Delete ClaimRevisionTrans
            for (ClaimRevisionTrans revTrans : claimRevisionTransRepo.findAll(
                    QClaimRevisionTrans.claimRevisionTrans.claimRevision.claimBookings.clmId.eq(claimId))) {
                claimRevisionTransRepo.delete(revTrans);
            }

            // 2. Delete ClaimRevisions
            for (ClaimRevisions revision : claimRevisionsRepo.findAll(
                    QClaimRevisions.claimRevisions.claimBookings.clmId.eq(claimId))) {
                claimRevisionsRepo.delete(revision);
            }

            // 3. Delete ClaimPerils
            for (ClaimPerils peril : claimPerilsRepo.findAll(
                    QClaimPerils.claimPerils.claimBookings.clmId.eq(claimId))) {
                claimPerilsRepo.delete(peril);
            }

            // 4. Delete ClaimClaimants
            for (ClaimClaimants claimant : claimClaimantsRepo.findAll(
                    QClaimClaimants.claimClaimants.claimBookings.clmId.eq(claimId))) {
                claimClaimantsRepo.delete(claimant);
            }

            // 5. Delete ClaimRequiredDocs
            for (ClaimRequiredDocs reqDoc : claimRequiredDocsRepo.findAll(
                    QClaimRequiredDocs.claimRequiredDocs.claimBookings.clmId.eq(claimId))) {
                claimRequiredDocsRepo.delete(reqDoc);
            }

            // 6. Delete ClaimActivities
            for (ClaimActivities activity : activitiesRepo.findAll(
                    QClaimActivities.claimActivities.claimBookings.clmId.eq(claimId))) {
                activitiesRepo.delete(activity);
            }

            // 7. Delete MakerChecker tasks if any exist
            for (MakerChecker makerChecker : makerCheckerRepo.findAll(
                    QMakerChecker.makerChecker.taskType.eq("CL")
                            .and(QMakerChecker.makerChecker.taskCode.eq(claimId)))) {
                makerCheckerRepo.delete(makerChecker);
            }

            // 8. Finally, delete the claim itself
            claimsBookingRepo.delete(claim);

        } catch (Exception e) {
            log.error("Error deleting draft claim {}: {}", claimId, e.getMessage());
            throw e;
        }
    }
}