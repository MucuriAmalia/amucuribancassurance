package com.brokersystems.brokerapp.life.service.impl;

import com.brokersystems.brokerapp.accounts.service.AccountsService;
import com.brokersystems.brokerapp.enums.AccountTypeEnum;
import com.brokersystems.brokerapp.life.dto.ReceiptAllocationCommissionsDTO;
import com.brokersystems.brokerapp.life.model.QLifeCommissionRates;
import com.brokersystems.brokerapp.life.repository.*;
import com.brokersystems.brokerapp.life.model.PolicyBeneficiaries;
import com.brokersystems.brokerapp.life.model.PolicyBenefitsDistribution;
import com.brokersystems.brokerapp.life.model.QPolicyBeneficiaries;
import com.brokersystems.brokerapp.life.model.*;
import com.brokersystems.brokerapp.life.service.LifeService;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.DateUtilities;
import com.brokersystems.brokerapp.server.utils.HolidayUtils;
import com.brokersystems.brokerapp.server.utils.Streamable;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.*;
import com.brokersystems.brokerapp.trans.dtos.AllCommissionsDTO;
import com.brokersystems.brokerapp.trans.model.*;
import com.brokersystems.brokerapp.trans.repository.ReceiptRepository;
import com.brokersystems.brokerapp.trans.repository.SystemTransRepo;
import com.brokersystems.brokerapp.trans.repository.SystemTransactionsRepo;
import com.brokersystems.brokerapp.trans.repository.TransMappingRepo;
import com.brokersystems.brokerapp.trans.service.AllocationService;
import com.brokersystems.brokerapp.trans.service.ReceiptService;
import com.brokersystems.brokerapp.uw.dtos.PolicyDependentsInfoDTO;
import com.brokersystems.brokerapp.uw.model.*;
import com.brokersystems.brokerapp.uw.repository.*;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;


/**
 * Created by waititu on 25/11/2017.
 */
@Service
public class LifeServiceImpl implements LifeService {

    @Autowired
    private BindersRepo bindersRepo;

    @Autowired
    private ReceiptAllocationCommissionsRepo lifeReceiptCommRepo;

    @Autowired
    private PolicyTransRepo policyRepo;

    @Autowired
    private ReceiptRepository receiptRepo;


    @Autowired
    private PolicyBeneficiariesRepo beneficiariesRepo;

    @Autowired
    private PolicyBenefitsDistributionRepo maturityRepo;

    @Autowired
    private LifeCommissionRatesRepo lifeCommissionRatesRepo;

    @Autowired
    private LifeReceiptAllocationsRepo lifeallocationsRepo;

    @Autowired
    private PremRatesRepo premRatesRepo;

    @Autowired
    private  ReceiptAllocationCommissionsRepo allocationCommissionsRepo;

    @Autowired
    private SystemTransRepo transRepo;

    @Autowired
    private AccountsService accountsService;

    @Autowired
    private  PolicyQuestionnaireRepo policyQuestionnaireRepo;

    @Autowired
    private PolicyDependentsRepo policyDependentsRepo;

    @Autowired
    private SequenceRepository sequenceRepository;

    @Autowired
    private TransMappingRepo transMappingRepo;

    @Autowired
    private ReceiptService receiptService;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private DateUtilities dateUtilities;

    @Autowired
    private SectionTransRepo sectionRepo;

    @Autowired
    private LifeReceiptsRepo lifeReceiptsRepo;

    @Autowired
    private AllocationService allocationService;
    @Autowired
    private PolicyInstallmentsRepo policyInstallmentsRepo;
    @Autowired
    private LifeSubAgentCommissionRatesRepo lifeSubAgentCommissionRatesRepo;

    @Autowired
    private AdminFeeSetUpRepo adminFeeSetUpRepo;

    @Autowired
    private RiskTransRepo riskTransRepo;

    @Autowired
    private SystemTransactionsRepo systemTransactionsRepo;

    @Override
    @Transactional(readOnly = true)
    public List<BinderPolTerms> getPolTerms(Long binCode) throws BadRequestException {
        BindersDef binder=bindersRepo.findOne(binCode);
        Integer minTerm = binder.getMinTerm();
        Integer maxTerm =binder.getMaxTerm();
        List<BinderPolTerms> polTerms = new ArrayList<>();
        while (minTerm<=maxTerm){
            polTerms.add(new BinderPolTerms(minTerm,minTerm));
            minTerm++;
        }
        return polTerms;
    }

    //    @Override
//    public DataTablesResult<LifeReceiptCommissions> findAllReceipts(DataTablesRequest request, Long polCode) throws IllegalAccessException {
//        BooleanExpression pred = QLifeReceiptCommissions.lifeReceiptCommissions.policyTrans.policyId.eq(polCode);
//        Page<LifeReceiptCommissions> page = lifeReceiptCommRepo.findAll(pred.and(request.searchPredicate(QLifeReceiptCommissions.lifeReceiptCommissions)), request);
//        return new DataTablesResult<>(request, page);
//    }
    public void deletePolBeneficiary(Long benCode) {
        beneficiariesRepo.delete(benCode);
    }

    @Override
    public void deleteCommissionRates(Long commId) {
        lifeCommissionRatesRepo.delete(commId);
    }

    @Override
    public DataTablesResult<PolicyBeneficiaries> findPolBeneficiaries(DataTablesRequest request, Long polCode) throws IllegalAccessException {
        BooleanExpression pred = QPolicyBeneficiaries.policyBeneficiaries.policy.policyId.eq(polCode);
        Page<PolicyBeneficiaries>  page = beneficiariesRepo.findAll(pred.and(request.searchPredicate(QPolicyBeneficiaries.policyBeneficiaries)),request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<LifeReceipts> findPolReceipts(DataTablesRequest request, Long polCode) throws IllegalAccessException{
        BooleanExpression pred =QLifeReceipts.lifeReceipts.policyTrans.policyId.eq(polCode);
        Page<LifeReceipts> page = lifeReceiptsRepo.findAll(pred.and(request.searchPredicate(QLifeReceipts.lifeReceipts)), request);
        return new DataTablesResult<>(request,page);
    }

    @Override
    public DataTablesResult<LifeReceiptAllocations> findReceiptsAllocations(DataTablesRequest request, Long receiptCode) throws IllegalAccessException{
//        BooleanExpression pred =QLifeReceiptAllocations.lifeReceiptAllocations.lifeReceipts.receiptTrans.receiptId.eq(receiptCode);
        Page<LifeReceiptAllocations> page = lifeallocationsRepo.findAll((request.searchPredicate(QLifeReceiptAllocations.lifeReceiptAllocations)), request);
        return new DataTablesResult<>(request,page);
    }

    @Override
    public DataTablesResult<ReceiptAllocationCommissions> findReceiptsCommissions(DataTablesRequest request, Long receiptCode) throws IllegalAccessException {
        BooleanExpression pred =QReceiptAllocationCommissions.receiptAllocationCommissions.lifeReceipts.receiptTrans.receiptId.eq(receiptCode);
        Page<ReceiptAllocationCommissions> page = allocationCommissionsRepo.findAll(pred.and(request.searchPredicate(QReceiptAllocationCommissions.receiptAllocationCommissions)),request);
        System.out.println(page.getTotalElements());
        return new DataTablesResult<>(request,page);
    }

    @Override
    public DataTablesResult<ReceiptAllocationCommissionsDTO> findAllocationCommissions(DataTablesRequest request, Long receiptId) {
        final List<ReceiptAllocationCommissionsDTO> commissionsList = new ArrayList<>();
        List<Object[]> commList = allocationCommissionsRepo.searchReceiptCommissionsAlloc(receiptId, request.getPageNumber(),request.getPageSize());
        long rowCount = 0L;
        if(!commList.isEmpty()) rowCount = ((BigInteger)commList.get(0)[8]).intValue();
        for(Object[] comm:commList ){
            ReceiptAllocationCommissionsDTO commissionsDTO = new ReceiptAllocationCommissionsDTO();
            commissionsDTO.setAllocCommId(((BigInteger) comm[0]).longValue());
            commissionsDTO.setInstallNo((Integer) comm[1]);
            commissionsDTO.setInstalmentPremium((BigDecimal) comm[2]);
            commissionsDTO.setCommissionAmt(((BigDecimal) comm[3]));
            commissionsDTO.setPaidToDate((String) comm[4]);
            commissionsDTO.setPremiumItem((String) comm[5]);
            commissionsDTO.setSubAgentCommissionAmt((BigDecimal) comm[6]);
            commissionsDTO.setMarketerCommissionAmt((BigDecimal) comm[7]);
            commissionsList.add(commissionsDTO);
        }
        Page<ReceiptAllocationCommissionsDTO>  page = new PageImpl<>(commissionsList,request, rowCount);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<ReceiptAllocationCommissions> findAllocationComm(DataTablesRequest request, Long allocId) throws IllegalAccessException{
        // BooleanExpression pred =QReceiptAllocationCommissions.receiptAllocationCommissions.allocations.allocId.eq(allocId);
        // Page<ReceiptAllocationCommissions> page = allocationCommissionsRepo.findAll(pred.and(request.searchPredicate(QReceiptAllocationCommissions.receiptAllocationCommissions)), request);
        List<Object[]> com  = allocationCommissionsRepo.findAllocCommisions(allocId);
        List<ReceiptAllocationCommissions> comms = new ArrayList<>();
        for (Object[] com1:com){
            ReceiptAllocationCommissions allocComm = new ReceiptAllocationCommissions();
            if (com1[0] instanceof BigInteger){
                allocComm.setAllocCommId(((BigInteger)com1[0]).longValue());
                allocComm.setAllocations(lifeallocationsRepo.findOne(((BigInteger)com1[1]).longValue()));
                allocComm.setPremRatesDef(premRatesRepo.findOne(((BigInteger)com1[8]).longValue()));




            }
            if (com1[0] instanceof BigDecimal){
                allocComm.setAllocCommId(((BigDecimal)com1[0]).longValue());
                allocComm.setAllocations(lifeallocationsRepo.findOne(((BigDecimal)com1[1]).longValue()));
                allocComm.setPremRatesDef(premRatesRepo.findOne(((BigDecimal)com1[8]).longValue()));
            }
            allocComm.setSectionPrem((BigDecimal)com1[2]);
            allocComm.setCommissionAmt((BigDecimal)com1[3]);
            allocComm.setSectionAlloc((BigDecimal)com1[4]);
            allocComm.setCoverPremium((BigDecimal)com1[5]);
            allocComm.setCommRate((BigDecimal)com1[6]);
            allocComm.setCommDivfact((BigDecimal)com1[6]);


            comms.add(allocComm);
        }
        Page<ReceiptAllocationCommissions> page = new PageImpl<ReceiptAllocationCommissions>(comms);
        return new DataTablesResult<>(request,page);
    }


    @Override
    public DataTablesResult<PolicyBenefitsDistribution> findPolBenefits(DataTablesRequest request, Long polCode) throws IllegalAccessException {
        BooleanExpression pred = QPolicyBenefitsDistribution.policyBenefitsDistribution.policyId.policyId.eq(polCode);
        Page<PolicyBenefitsDistribution>  page = maturityRepo.findAll(pred.and(request.searchPredicate(QPolicyBenefitsDistribution.policyBenefitsDistribution)),request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<PolicyQuestionnaire> findPolQuiz(DataTablesRequest request, Long polCode) throws IllegalAccessException {
        BooleanExpression pred = QPolicyQuestionnaire.policyQuestionnaire.policy.policyId.eq(polCode);
        Iterable<PolicyQuestionnaire> policyQuestionnaires = policyQuestionnaireRepo.findAll(pred);
        List<PolicyQuestionnaire> quiz = new ArrayList<>();
        for (PolicyQuestionnaire polquiz:policyQuestionnaires){
            PolicyQuestionnaire polquiz1 = new PolicyQuestionnaire();
            if (polquiz.getChoice()!=null){
                polquiz1 =polquiz;
                quiz.add(polquiz1);
            }

        }
        Page<PolicyQuestionnaire>  page = new PageImpl<PolicyQuestionnaire>(quiz);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public Iterable<PolicyQuestionnaire> findPolQuizList(Long polCode) throws IllegalAccessException {
        BooleanExpression pred = QPolicyQuestionnaire.policyQuestionnaire.policy.policyId.eq(polCode);
        Iterable<PolicyQuestionnaire> policyQuestionnaires = policyQuestionnaireRepo.findAll(pred);


        return policyQuestionnaires;
    }


    @Override
    @Transactional(readOnly = false)
    public void definePolicyBeneficiary(PolicyBeneficiaries beneficiaries ) throws BadRequestException {
//        if(beneficiaries.getDateRegistered().after(new Date())){
//            throw new BadRequestException("Date of Birth cannot be after today..");
//        }

//        if(beneficiaries.getBenAllocation()==null && beneficiaries.getBenAllocation().compareTo(BigDecimal.ZERO) <=0){
//            throw new BadRequestException("Beneficiary Share cannot be zero or less than zero...");
//        }

        List<Object[]> beneficiaryData = beneficiariesRepo.countBeneficiaries(beneficiaries.getPolicy().getPolicyId());
        if(beneficiaryData.size()!=1){
            throw new BadRequestException("Beneficiary Data is not available...");
        }
        Object[] data = beneficiaryData.get(0);
        BigDecimal totalPercentage = (BigDecimal)data[1];

        // Fetch existing beneficiary if this is an update
        PolicyBeneficiaries existingBeneficiary = null;
        if (beneficiaries.getBeneficiaryCode() != null) {
            existingBeneficiary = beneficiariesRepo.findOne(beneficiaries.getBeneficiaryCode());
        }

        // If updating, subtract the existing allocation from the total
        if (existingBeneficiary != null) {
            totalPercentage = totalPercentage.subtract(existingBeneficiary.getBenAllocation());
        }

        if((totalPercentage.add(beneficiaries.getBenAllocation())).compareTo(BigDecimal.valueOf(100)) > 0){
            throw new BadRequestException("Beneficiary distribution cannot be greater than 100%");
        }
        beneficiaries.setDateCreated(new Date());
        beneficiaries.setUser(userUtils.getCurrentUser());
        beneficiaries.setPolicy(policyRepo.findOne(beneficiaries.getPolicy().getPolicyId()));
        beneficiariesRepo.save(beneficiaries);


    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class }, propagation = Propagation.REQUIRED)
    public AllCommissionsDTO getCommissionEarned(Long polCode,Long  receiptId) throws BadRequestException {
                AllCommissionsDTO allCommissionsDTO = new AllCommissionsDTO();
                PolicyTrans policyTrans = policyRepo.findOne(polCode);
                Iterable<LifeReceiptAllocations> allocations = lifeallocationsRepo.findAll(QLifeReceiptAllocations.lifeReceiptAllocations.lifeReceipts.receiptId.eq(receiptId));
                for(LifeReceiptAllocations allocation:allocations) {
                int paidInsts = policyTrans.getPaidInsts();
                Map<String, Integer> frequencyMap = new HashMap<>();
                frequencyMap.put("D", 365);
                frequencyMap.put("W", 52);
                frequencyMap.put("M", 12);
                frequencyMap.put("Q", 4);
                frequencyMap.put("S", 2);
                frequencyMap.put("A", 1);
                int paymentsPerYear = frequencyMap.getOrDefault(policyTrans.getFrequency(), 12);  // Default to Monthly if not found

                double paidYears = Math.ceil(((double) paidInsts / paymentsPerYear));
                Iterable<RiskTrans> risks = riskTransRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(policyTrans.getPolicyId()));
                for (RiskTrans riskTrans : risks) {
                    if (lifeCommissionRatesRepo.count(QLifeCommissionRates.lifeCommissionRates.binderDef.eq(policyTrans.getBinder())
                            .and(QLifeCommissionRates.lifeCommissionRates.commTermFrom.loe(policyTrans.getPolTerm()))
                            .and(QLifeCommissionRates.lifeCommissionRates.commTermTo.goe(policyTrans.getPolTerm()))
                            .and(QLifeCommissionRates.lifeCommissionRates.wefDate.loe(policyTrans.getCoverFrom()))
                            .and(QLifeCommissionRates.lifeCommissionRates.wetDate.goe(policyTrans.getCoverFrom()).or(QLifeCommissionRates.lifeCommissionRates.wetDate.isNull()))
                            .and(QLifeCommissionRates.lifeCommissionRates.commYearFrom.loe(paidYears))
                            .and(QLifeCommissionRates.lifeCommissionRates.frequency.equalsIgnoreCase(policyTrans.getFrequency()))
                            .and(QLifeCommissionRates.lifeCommissionRates.commYearTo.goe(paidYears))) <= 0) {
                        throw new BadRequestException("No commission rate defined...." + policyTrans.getPolTerm() + ";" + policyTrans.getCoverFrom() + ";" + policyTrans.getCoverFrom() + ";" + paidYears + ";" + policyTrans.getFrequency());

                    }
                    if (lifeCommissionRatesRepo.count(QLifeCommissionRates.lifeCommissionRates.binderDef.eq(policyTrans.getBinder())
                            .and(QLifeCommissionRates.lifeCommissionRates.commTermFrom.loe(policyTrans.getPolTerm()))
                            .and(QLifeCommissionRates.lifeCommissionRates.commTermTo.goe(policyTrans.getPolTerm()))
                            .and(QLifeCommissionRates.lifeCommissionRates.wefDate.loe(policyTrans.getCoverFrom()))
                            .and(QLifeCommissionRates.lifeCommissionRates.wetDate.goe(policyTrans.getCoverFrom()).or(QLifeCommissionRates.lifeCommissionRates.wetDate.isNull()))
                            .and(QLifeCommissionRates.lifeCommissionRates.commYearFrom.loe(paidYears))
                            .and(QLifeCommissionRates.lifeCommissionRates.frequency.equalsIgnoreCase(policyTrans.getFrequency()))
                            .and(QLifeCommissionRates.lifeCommissionRates.commYearTo.goe(paidYears))) > 1) {
                        throw new BadRequestException("More than one commission rate exists....");
                    }

                    if (policyTrans.getSubAgent() != null) {
                        if (lifeSubAgentCommissionRatesRepo.count(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.binderDef.eq(policyTrans.getBinder())
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.accountTypes.accountType.eq(AccountTypeEnum.SUB))
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermFrom.loe(policyTrans.getPolTerm()))
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermTo.goe(policyTrans.getPolTerm()))
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wefDate.loe(policyTrans.getCoverFrom()))
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.goe(policyTrans.getCoverFrom()).or(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.isNull()))
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.frequency.equalsIgnoreCase(policyTrans.getFrequency()))
                        ) <= 0) {
                            throw new BadRequestException("No SubAgent commission rate defined...." + policyTrans.getPolTerm() + ";" + policyTrans.getCoverFrom() + ";" + policyTrans.getCoverFrom() + ";" + paidYears + ";" + policyTrans.getFrequency());

                        }
                        if (lifeSubAgentCommissionRatesRepo.count(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.binderDef.eq(policyTrans.getBinder())
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.accountTypes.accountType.eq(AccountTypeEnum.SUB))
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermFrom.loe(policyTrans.getPolTerm()))
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermTo.goe(policyTrans.getPolTerm()))
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wefDate.loe(policyTrans.getCoverFrom()))
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.goe(policyTrans.getCoverFrom()).or(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.isNull()))
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.frequency.equalsIgnoreCase(policyTrans.getFrequency()))
                        ) > 1) {
                            throw new BadRequestException("More than one subAgen commission rate exists....");
                        }
                    }

                    if (policyTrans.getMarketerAgent() != null) {
                        if (lifeSubAgentCommissionRatesRepo.count(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.binderDef.eq(policyTrans.getBinder())
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.accountTypes.accountType.eq(AccountTypeEnum.MRK))
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermFrom.loe(policyTrans.getPolTerm()))
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermTo.goe(policyTrans.getPolTerm()))
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wefDate.loe(policyTrans.getCoverFrom()))
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.goe(policyTrans.getCoverFrom()).or(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.isNull()))
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.frequency.equalsIgnoreCase(policyTrans.getFrequency()))
                        ) <= 0) {
                            throw new BadRequestException("No Marketer commission rate defined...." + policyTrans.getPolTerm() + ";" + policyTrans.getCoverFrom() + ";" + policyTrans.getCoverFrom() + ";" + paidYears + ";" + policyTrans.getFrequency());

                        }
                        if (lifeSubAgentCommissionRatesRepo.count(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.binderDef.eq(policyTrans.getBinder())
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.accountTypes.accountType.eq(AccountTypeEnum.MRK))
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermFrom.loe(policyTrans.getPolTerm()))
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermTo.goe(policyTrans.getPolTerm()))
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wefDate.loe(policyTrans.getCoverFrom()))
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.goe(policyTrans.getCoverFrom()).or(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.isNull()))
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.frequency.equalsIgnoreCase(policyTrans.getFrequency()))
                        ) > 1) {
                            throw new BadRequestException("More than one Marketer commission rate exists....");
                        }
                    }

                    LifeCommissionRates commissionRates = lifeCommissionRatesRepo.findOne(QLifeCommissionRates.lifeCommissionRates.binderDef.eq(policyTrans.getBinder())
                            .and(QLifeCommissionRates.lifeCommissionRates.commTermFrom.loe(policyTrans.getPolTerm()))
                            .and(QLifeCommissionRates.lifeCommissionRates.commTermTo.goe(policyTrans.getPolTerm()))
                            .and(QLifeCommissionRates.lifeCommissionRates.wefDate.loe(policyTrans.getCoverFrom()))
                            .and(QLifeCommissionRates.lifeCommissionRates.wetDate.goe(policyTrans.getCoverFrom()).or(QLifeCommissionRates.lifeCommissionRates.wetDate.isNull()))
                            .and(QLifeCommissionRates.lifeCommissionRates.commYearFrom.loe(paidYears))
                            .and(QLifeCommissionRates.lifeCommissionRates.frequency.equalsIgnoreCase(policyTrans.getFrequency()))
                            .and(QLifeCommissionRates.lifeCommissionRates.commYearTo.goe(paidYears)));
                    LifeSubAgentCommissionRates subAgentCommissionRates = null;
                    LifeSubAgentCommissionRates marketerCommissionRates = null;
                    if (policyTrans.getSubAgent() != null) {
                        subAgentCommissionRates = lifeSubAgentCommissionRatesRepo.findOne(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.binderDef.eq(policyTrans.getBinder())
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.accountTypes.accountType.eq(AccountTypeEnum.SUB))
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermFrom.loe(policyTrans.getPolTerm()))
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermTo.goe(policyTrans.getPolTerm()))
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wefDate.loe(policyTrans.getCoverFrom()))
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.goe(policyTrans.getCoverFrom()).or(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.isNull()))
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.frequency.equalsIgnoreCase(policyTrans.getFrequency()))
                        );
                    }
                    if (policyTrans.getMarketerAgent() != null) {
                        marketerCommissionRates = lifeSubAgentCommissionRatesRepo.findOne(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.binderDef.eq(policyTrans.getBinder())
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.accountTypes.accountType.eq(AccountTypeEnum.SUB))
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermFrom.loe(policyTrans.getPolTerm()))
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermTo.goe(policyTrans.getPolTerm()))
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wefDate.loe(policyTrans.getCoverFrom()))
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.goe(policyTrans.getCoverFrom()).or(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.isNull()))
                                .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.frequency.equalsIgnoreCase(policyTrans.getFrequency()))
                        );
                    }

                    if(policyTrans.getAdminFeeApplicable()!=null && "Y".equalsIgnoreCase(policyTrans.getAdminFeeApplicable())) {
                        long count = adminFeeSetUpRepo.count(QAdminFeeSetUp.adminFeeSetUp.binder.binId.eq(policyTrans.getBinder().getBinId()).and(QAdminFeeSetUp.adminFeeSetUp.status.eq("Active")));

                        if (count != 1) {
                            throw new BadRequestException("Please configure set up for admin fee to continue...");
                        }

                        AdminFeeSetUp adminFeeSetUp = adminFeeSetUpRepo.findOne(QAdminFeeSetUp.adminFeeSetUp.binder.binId.eq(policyTrans.getBinder().getBinId()).and(QAdminFeeSetUp.adminFeeSetUp.status.eq("Active")));

                        //admin fee check
                        if(adminFeeSetUp != null && adminFeeSetUp.getAdminFeeRateType() != null) {
                            final double rate = (adminFeeSetUp.getAdminFeeRateType().equals("Percent")) ? 100 : 1;
                            BigDecimal adminFeeTotal = BigDecimal.ZERO;
                            if (adminFeeSetUp.getAdminFeeRateType().equals("Percent")) {
                                adminFeeTotal = BigDecimal.valueOf((adminFeeSetUp.getAdminFeeRate().doubleValue() / rate) * allocation.getInstalmentPremium().doubleValue());
                            } else {
                                adminFeeTotal = adminFeeSetUp.getAdminFeeRate();
                            }
                            allCommissionsDTO.setAdminFeeTotal(adminFeeTotal);
                            final double vatrate = (adminFeeSetUp.getVateRateType().equals("Percent")) ? 100 : 1;
                            BigDecimal vatTotal = BigDecimal.valueOf((adminFeeSetUp.getVatRate().doubleValue() / vatrate) * adminFeeTotal.doubleValue());
                            if ((!adminFeeSetUp.getVateRateType().equals("Percent"))) {
                                vatTotal = adminFeeSetUp.getVatRate();
                            }
                            allCommissionsDTO.setAdminFeeWhtx(vatTotal);
                        }
                    }

                    BigDecimal sectionAlloc = BigDecimal.ZERO;
                    if (riskTrans.getPremium() != null) {
                        sectionAlloc = allocation.getAllocAmount().multiply(riskTrans.getPremium()).divide(allocation.getInstalmentPremium()).setScale(2, BigDecimal.ROUND_HALF_EVEN);
                    }

                    if (commissionRates != null) {
                        BigDecimal commRate = commissionRates.getCommRate();
                        BigDecimal comm = BigDecimal.ZERO;
                        comm = (commRate.multiply(sectionAlloc).divide(commissionRates.getCommDivFactor())).setScale(2, BigDecimal.ROUND_HALF_EVEN);

                        allCommissionsDTO.setCommission(comm);
                        if (paidYears <= 1) {
                            if (subAgentCommissionRates != null) {
                                BigDecimal subAgentComm = (subAgentCommissionRates.getCommRate().multiply(comm).divide(subAgentCommissionRates.getCommDivFactor())).setScale(2, BigDecimal.ROUND_HALF_EVEN);
                                allCommissionsDTO.setSubAgentComm(subAgentComm);
                            }
                            if (marketerCommissionRates != null) {
                                BigDecimal mkrAgentComm = (marketerCommissionRates.getCommRate().multiply(comm).divide(marketerCommissionRates.getCommDivFactor())).setScale(2, BigDecimal.ROUND_HALF_EVEN);
                                allCommissionsDTO.setMarketerComm(mkrAgentComm);
                            }
                        }
                        return allCommissionsDTO;
                    }

                }

        }
        return allCommissionsDTO;
    }

    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class }, propagation = Propagation.REQUIRED)
    public AllCommissionsDTO allocateLifeRcptComm(LifeReceiptAllocations savedAlloc, int paidInsts, boolean alreadyAllocated) throws BadRequestException {
        AllCommissionsDTO allCommissionsDTO = new AllCommissionsDTO();
        if (savedAlloc!=null){
            PolicyTrans policyTrans =  savedAlloc.getLifeReceipts().getPolicyTrans();
            Map<String, Integer> frequencyMap = new HashMap<>();
            frequencyMap.put("D", 365);
            frequencyMap.put("W", 52);
            frequencyMap.put("M", 12);
            frequencyMap.put("Q", 4);
            frequencyMap.put("S", 2);
            frequencyMap.put("A", 1);
            int paymentsPerYear = frequencyMap.getOrDefault(policyTrans.getFrequency(), 12);  // Default to Monthly if not found

            double paidYears = Math.ceil(((double) paidInsts/paymentsPerYear));
            Iterable<RiskTrans> risks = riskTransRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(policyTrans.getPolicyId()));
            List<ReceiptAllocationCommissions> comms = new ArrayList<>();
            BigDecimal totComm = new BigDecimal(BigInteger.ZERO);
            BigDecimal totSubAgentComm = new BigDecimal(BigInteger.ZERO);
            BigDecimal totAdminFee = new BigDecimal(BigInteger.ZERO);
            BigDecimal totAdminFeeWhtx = new BigDecimal(BigInteger.ZERO);
            BigDecimal totMarketerComm = new BigDecimal(BigInteger.ZERO);
            for(RiskTrans riskTrans:risks) {
                if (lifeCommissionRatesRepo.count(QLifeCommissionRates.lifeCommissionRates.binderDef.eq(policyTrans.getBinder())
                        .and(QLifeCommissionRates.lifeCommissionRates.commTermFrom.loe(policyTrans.getPolTerm()))
                        .and(QLifeCommissionRates.lifeCommissionRates.commTermTo.goe(policyTrans.getPolTerm()))
                        .and(QLifeCommissionRates.lifeCommissionRates.wefDate.loe(policyTrans.getCoverFrom()))
                        .and(QLifeCommissionRates.lifeCommissionRates.wetDate.goe(policyTrans.getCoverFrom()).or(QLifeCommissionRates.lifeCommissionRates.wetDate.isNull()))
                        .and(QLifeCommissionRates.lifeCommissionRates.commYearFrom.loe(paidYears))
                        .and(QLifeCommissionRates.lifeCommissionRates.frequency.equalsIgnoreCase(policyTrans.getFrequency()))
                        .and(QLifeCommissionRates.lifeCommissionRates.commYearTo.goe(paidYears))) <= 0) {
                    throw new BadRequestException("No commission rate defined...." + policyTrans.getPolTerm() + ";" + policyTrans.getCoverFrom() + ";" + policyTrans.getCoverFrom() + ";" + paidYears + ";" + policyTrans.getFrequency());

                }
                if (lifeCommissionRatesRepo.count(QLifeCommissionRates.lifeCommissionRates.binderDef.eq(policyTrans.getBinder())
                        .and(QLifeCommissionRates.lifeCommissionRates.commTermFrom.loe(policyTrans.getPolTerm()))
                        .and(QLifeCommissionRates.lifeCommissionRates.commTermTo.goe(policyTrans.getPolTerm()))
                        .and(QLifeCommissionRates.lifeCommissionRates.wefDate.loe(policyTrans.getCoverFrom()))
                        .and(QLifeCommissionRates.lifeCommissionRates.wetDate.goe(policyTrans.getCoverFrom()).or(QLifeCommissionRates.lifeCommissionRates.wetDate.isNull()))
                        .and(QLifeCommissionRates.lifeCommissionRates.commYearFrom.loe(paidYears))
                        .and(QLifeCommissionRates.lifeCommissionRates.frequency.equalsIgnoreCase(policyTrans.getFrequency()))
                        .and(QLifeCommissionRates.lifeCommissionRates.commYearTo.goe(paidYears))) > 1) {
                    throw new BadRequestException("More than one commission rate exists....");
                }

                if (policyTrans.getSubAgent() != null) {
                    if (lifeSubAgentCommissionRatesRepo.count(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.binderDef.eq(policyTrans.getBinder())
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.accountTypes.accountType.eq(AccountTypeEnum.SUB))
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermFrom.loe(policyTrans.getPolTerm()))
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermTo.goe(policyTrans.getPolTerm()))
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wefDate.loe(policyTrans.getCoverFrom()))
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.goe(policyTrans.getCoverFrom()).or(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.isNull()))
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.frequency.equalsIgnoreCase(policyTrans.getFrequency()))
                    ) <= 0) {
                        throw new BadRequestException("No SubAgent commission rate defined...." + policyTrans.getPolTerm() + ";" + policyTrans.getCoverFrom() + ";" + policyTrans.getCoverFrom() + ";" + paidYears + ";" + policyTrans.getFrequency());

                    }
                    if (lifeSubAgentCommissionRatesRepo.count(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.binderDef.eq(policyTrans.getBinder())
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.accountTypes.accountType.eq(AccountTypeEnum.SUB))
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermFrom.loe(policyTrans.getPolTerm()))
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermTo.goe(policyTrans.getPolTerm()))
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wefDate.loe(policyTrans.getCoverFrom()))
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.goe(policyTrans.getCoverFrom()).or(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.isNull()))
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.frequency.equalsIgnoreCase(policyTrans.getFrequency()))
                    ) > 1) {
                        throw new BadRequestException("More than one subAgen commission rate exists....");
                    }
                }

                if (policyTrans.getMarketerAgent() != null) {
                    if (lifeSubAgentCommissionRatesRepo.count(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.binderDef.eq(policyTrans.getBinder())
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.accountTypes.accountType.eq(AccountTypeEnum.MRK))
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermFrom.loe(policyTrans.getPolTerm()))
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermTo.goe(policyTrans.getPolTerm()))
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wefDate.loe(policyTrans.getCoverFrom()))
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.goe(policyTrans.getCoverFrom()).or(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.isNull()))
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.frequency.equalsIgnoreCase(policyTrans.getFrequency()))
                    ) <= 0) {
                        throw new BadRequestException("No Marketer commission rate defined...." + policyTrans.getPolTerm() + ";" + policyTrans.getCoverFrom() + ";" + policyTrans.getCoverFrom() + ";" + paidYears + ";" + policyTrans.getFrequency());

                    }
                    if (lifeSubAgentCommissionRatesRepo.count(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.binderDef.eq(policyTrans.getBinder())
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.accountTypes.accountType.eq(AccountTypeEnum.MRK))
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermFrom.loe(policyTrans.getPolTerm()))
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermTo.goe(policyTrans.getPolTerm()))
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wefDate.loe(policyTrans.getCoverFrom()))
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.goe(policyTrans.getCoverFrom()).or(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.isNull()))
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.frequency.equalsIgnoreCase(policyTrans.getFrequency()))
                    ) > 1) {
                        throw new BadRequestException("More than one Marketer commission rate exists....");
                    }
                }

                LifeCommissionRates commissionRates = lifeCommissionRatesRepo.findOne(QLifeCommissionRates.lifeCommissionRates.binderDef.eq(policyTrans.getBinder())
                        .and(QLifeCommissionRates.lifeCommissionRates.commTermFrom.loe(policyTrans.getPolTerm()))
                        .and(QLifeCommissionRates.lifeCommissionRates.commTermTo.goe(policyTrans.getPolTerm()))
                        .and(QLifeCommissionRates.lifeCommissionRates.wefDate.loe(policyTrans.getCoverFrom()))
                        .and(QLifeCommissionRates.lifeCommissionRates.wetDate.goe(policyTrans.getCoverFrom()).or(QLifeCommissionRates.lifeCommissionRates.wetDate.isNull()))
                        .and(QLifeCommissionRates.lifeCommissionRates.commYearFrom.loe(paidYears))
                        .and(QLifeCommissionRates.lifeCommissionRates.frequency.equalsIgnoreCase(policyTrans.getFrequency()))
                        .and(QLifeCommissionRates.lifeCommissionRates.commYearTo.goe(paidYears)));
                LifeSubAgentCommissionRates subAgentCommissionRates = null;
                LifeSubAgentCommissionRates marketerCommissionRates = null;
                if (policyTrans.getSubAgent() != null) {
                    subAgentCommissionRates = lifeSubAgentCommissionRatesRepo.findOne(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.binderDef.eq(policyTrans.getBinder())
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.accountTypes.accountType.eq(AccountTypeEnum.SUB))
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermFrom.loe(policyTrans.getPolTerm()))
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermTo.goe(policyTrans.getPolTerm()))
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wefDate.loe(policyTrans.getCoverFrom()))
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.goe(policyTrans.getCoverFrom()).or(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.isNull()))
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.frequency.equalsIgnoreCase(policyTrans.getFrequency()))
                    );
                }
                if (policyTrans.getMarketerAgent() != null) {
                    marketerCommissionRates = lifeSubAgentCommissionRatesRepo.findOne(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.binderDef.eq(policyTrans.getBinder())
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.accountTypes.accountType.eq(AccountTypeEnum.MRK))
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermFrom.loe(policyTrans.getPolTerm()))
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.commTermTo.goe(policyTrans.getPolTerm()))
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wefDate.loe(policyTrans.getCoverFrom()))
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.goe(policyTrans.getCoverFrom()).or(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.wetDate.isNull()))
                            .and(QLifeSubAgentCommissionRates.lifeSubAgentCommissionRates.frequency.equalsIgnoreCase(policyTrans.getFrequency()))
                    );
                }



                ReceiptAllocationCommissions com = new ReceiptAllocationCommissions();
//                BigDecimal sectionAlloc = BigDecimal.ZERO;
//                if (riskTrans.getPremium() != null) {
//                    sectionAlloc = savedAlloc.getAllocAmount()
//                            .multiply(riskTrans.getPremium())
//                            .divide(savedAlloc.getInstalmentPremium()).setScale(2, BigDecimal.ROUND_HALF_EVEN);
//                }

                BigDecimal sectionAlloc = BigDecimal.ZERO;
                if (riskTrans.getPremium() != null && savedAlloc.getInstalmentPremium() != null && savedAlloc.getInstalmentPremium().compareTo(BigDecimal.ZERO) != 0) {
                    sectionAlloc = savedAlloc.getAllocAmount()
                            .multiply(riskTrans.getPremium())
                            .divide(savedAlloc.getInstalmentPremium(), 2, BigDecimal.ROUND_HALF_EVEN); // <-- Fix is here
                }

                if(policyTrans.getAdminFeeApplicable()!=null && "Y".equalsIgnoreCase(policyTrans.getAdminFeeApplicable())) {
                    long count = adminFeeSetUpRepo.count(QAdminFeeSetUp.adminFeeSetUp.binder.binId.eq(policyTrans.getBinder().getBinId()).and(QAdminFeeSetUp.adminFeeSetUp.status.eq("Active")));

                    if (count != 1) {
                        throw new BadRequestException("Please configure set up for admin fee to continue...");
                    }

                    AdminFeeSetUp adminFeeSetUp = adminFeeSetUpRepo.findOne(QAdminFeeSetUp.adminFeeSetUp.binder.binId.eq(policyTrans.getBinder().getBinId()).and(QAdminFeeSetUp.adminFeeSetUp.status.eq("Active")));

                    //admin fee check
                    if(adminFeeSetUp != null && adminFeeSetUp.getAdminFeeRateType() != null) {
                        final double rate = (adminFeeSetUp.getAdminFeeRateType().equals("Percent")) ? 100 : 1;
                        BigDecimal adminFeeTotal = BigDecimal.ZERO;
                        if (adminFeeSetUp.getAdminFeeRateType().equals("Percent")) {
                            adminFeeTotal = BigDecimal.valueOf((adminFeeSetUp.getAdminFeeRate().doubleValue() / rate) * sectionAlloc.doubleValue());
                        } else {
                            adminFeeTotal = adminFeeSetUp.getAdminFeeRate();
                        }
                        allCommissionsDTO.setAdminFeeTotal(adminFeeTotal);
                        final double vatrate = (adminFeeSetUp.getVateRateType().equals("Percent")) ? 100 : 1;
                        BigDecimal vatTotal = BigDecimal.valueOf((adminFeeSetUp.getVatRate().doubleValue() / vatrate) * adminFeeTotal.doubleValue());
                        if ((!adminFeeSetUp.getVateRateType().equals("Percent"))) {
                            vatTotal = adminFeeSetUp.getVatRate();
                        }
                        allCommissionsDTO.setAdminFeeWhtx(vatTotal);
                    }

                }

                if (commissionRates != null) {
                    BigDecimal commRate = commissionRates.getCommRate();
                    BigDecimal comm = BigDecimal.ZERO;
                    comm = (commRate.multiply(sectionAlloc).divide(commissionRates.getCommDivFactor())).setScale(2, BigDecimal.ROUND_HALF_EVEN);

                    if(alreadyAllocated){
                        allCommissionsDTO.setCommission(comm);
                        if(paidYears <=1) {
                            if (subAgentCommissionRates != null) {
                                BigDecimal subAgentComm = (subAgentCommissionRates.getCommRate().multiply(comm).divide(subAgentCommissionRates.getCommDivFactor())).setScale(2, BigDecimal.ROUND_HALF_EVEN);
                                com.setSubAgentcommissionAmt(subAgentComm);
                                allCommissionsDTO.setSubAgentComm(subAgentComm);
                            }
                            if (marketerCommissionRates != null) {
                                BigDecimal mkrAgentComm = (marketerCommissionRates.getCommRate().multiply(comm).divide(marketerCommissionRates.getCommDivFactor())).setScale(2, BigDecimal.ROUND_HALF_EVEN);
                                com.setMarketerCommissionAmt(mkrAgentComm);
                                allCommissionsDTO.setMarketerComm(mkrAgentComm);
                            }
                        }
                        return allCommissionsDTO;
                    }

                    BigDecimal whtxAmt = BigDecimal.ZERO;
                    BigDecimal lifewhtxAmt = BigDecimal.ZERO;
                    AccountTypes accType = policyTrans.getAgent() != null ? policyTrans.getAgent().getAccountType() : null;

                    totComm = totComm.add(comm);
                    savedAlloc.setCommAmount(totComm);
                    com.setLifeReceipts(savedAlloc.getLifeReceipts());
                    com.setAllocations(savedAlloc);
                    com.setCommDivfact(commissionRates.getCommDivFactor());
                    com.setCommissionAmt(comm);
                    com.setCommissionRates(commissionRates);
                    com.setCommRate(commissionRates.getCommRate());
                    if(allCommissionsDTO.getAdminFeeTotal()!=null){
                        com.setAdminFeeAmt(allCommissionsDTO.getAdminFeeTotal());
                        com.setAdminFeeWhtxAmt(allCommissionsDTO.getAdminFeeWhtx());
                        totAdminFee = totAdminFee.add(allCommissionsDTO.getAdminFeeTotal());
                        totAdminFeeWhtx  = totAdminFeeWhtx.add(allCommissionsDTO.getAdminFeeWhtx());
                    }
                    allCommissionsDTO.setCommission(comm);
                    if(paidYears <=1) {
                        if (subAgentCommissionRates != null) {
                            BigDecimal subAgentComm = (subAgentCommissionRates.getCommRate().multiply(comm).divide(subAgentCommissionRates.getCommDivFactor())).setScale(2, BigDecimal.ROUND_HALF_EVEN);
                            com.setSubAgentcommissionAmt(subAgentComm);
                            allCommissionsDTO.setSubAgentComm(subAgentComm);
                            totSubAgentComm = totSubAgentComm.add(subAgentComm);
                        }
                        if (marketerCommissionRates != null) {
                            BigDecimal mkrAgentComm = (marketerCommissionRates.getCommRate().multiply(comm).divide(marketerCommissionRates.getCommDivFactor())).setScale(2, BigDecimal.ROUND_HALF_EVEN);
                            com.setMarketerCommissionAmt(mkrAgentComm);
                            allCommissionsDTO.setMarketerComm(mkrAgentComm);
                            totMarketerComm = totMarketerComm.add(mkrAgentComm);
                        }
                    }
                    com.setCoverPremium(riskTrans.getPremium());
                    com.setSectionAlloc(sectionAlloc);

                    if (accType != null && accType.isWhtxAppl()) {
                        if (accType.getWhtaxVal().compareTo(BigDecimal.ZERO) == 1) {
                            whtxAmt = accType.getWhtaxVal().divide(new BigDecimal(100)).multiply(totComm);
                            lifewhtxAmt = whtxAmt.negate();
                        }
                        allCommissionsDTO.setWhtx(lifewhtxAmt);
                    }
                    savedAlloc.setLifeWhtx(lifewhtxAmt);
                    policyTrans.setWhtx(lifewhtxAmt);
                    policyTrans.setPolTotWhtx(lifewhtxAmt);
                }
                comms.add(com);
            }
            lifeReceiptCommRepo.save(comms);
            savedAlloc.setSubAgentCommAmount(totSubAgentComm);
            savedAlloc.setMarketerCommAmount(totMarketerComm);
            savedAlloc.setAdminFeeAmt(totAdminFee);
            savedAlloc.setAdminFeeAmtWhtx(totAdminFeeWhtx);
            lifeallocationsRepo.save(savedAlloc);
        }
        return allCommissionsDTO;
    }

    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class }, propagation = Propagation.REQUIRED)
    public void deallocateLifeRcpt(LifeReceipts lifeReceipts, User user) throws BadRequestException {

        if (lifeReceipts ==null) {
            throw new BadRequestException("No Receipt transaction Selected....");
        }

        LifeReceipts originalRcpt = lifeReceiptsRepo.findOne(lifeReceipts.getOriginalLifeReceipts().getReceiptId());
        System.out.println("Policy="+lifeReceipts.getPolicyTrans().getPolicyId());
        PolicyTrans policyTrans = lifeReceipts.getPolicyTrans();
        int fullInst=0;
        if(originalRcpt.getAllocatedAmt()==null || originalRcpt.getAllocatedAmt().compareTo(BigDecimal.ZERO)==0){
            return;
        }
        if (lifeReceipts.getDrCr().equalsIgnoreCase("D")){
            // int  fullInsts = (totBalance.divide(policyTrans.getNetPrem(),BigDecimal.ROUND_FLOOR)).intValue();
            fullInst = (originalRcpt.getAllocatedAmt().divide(lifeReceipts.getPolicyTrans().getNetPrem(),BigDecimal.ROUND_FLOOR)).intValue();

            if (fullInst>0) { // allocates only full instalments
                int counter=0;
                int paidInsts =policyTrans.getPaidInsts();
                int prevpaidInsts =0;

                while (counter<fullInst && policyTrans.getPaidInsts()<policyTrans.getTotalInstalments()){
                    counter++;
                    LifeReceiptAllocations alloc = new LifeReceiptAllocations();
                    alloc.setAllocDate(new Date());
                    alloc.setInstalmentPremium(policyTrans.getNetPrem());
                    alloc.setLifeReceipts(lifeReceipts);
                    alloc.setAllocAmount(policyTrans.getNetPrem());
                    alloc.setInstallNo(paidInsts);
                    alloc.setDoneDate(new Date());
                    alloc.setDoneBy(user);
                    alloc.setPaidToDate(dateUtilities.getPaidToDate(policyTrans.getCoverFrom(),(paidInsts-1)));
                    LifeReceiptAllocations savedAlloc = lifeallocationsRepo.save(alloc);
                    //lifeReceipts.setBalanceAmt(lifeReceipts.getBalanceAmt().subtract(policyTrans.getNetPrem()));
                    if (lifeReceipts.getAllocatedAmt()!=null) {
                        lifeReceipts.setAllocatedAmt(lifeReceipts.getAllocatedAmt().add(savedAlloc.getAllocAmount()));
                    }else {
                        lifeReceipts.setAllocatedAmt(savedAlloc.getAllocAmount());
                    }
                    lifeReceiptsRepo.save(lifeReceipts);
                    prevpaidInsts= paidInsts;
                    paidInsts--;
                    policyTrans.setPaidInsts(paidInsts);
                    policyTrans.setPolPaidToDate( dateUtilities.getPaidToDate(policyTrans.getCoverFrom(),paidInsts) );
                    //System.out.println("xxx="+policyTrans);
                    policyTrans=policyRepo.save(policyTrans);
                    allocateLifeRcptComm(savedAlloc,prevpaidInsts,false);

                }

            }

        }
    }

    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class }, propagation = Propagation.REQUIRED)
    public AllCommissionsDTO allocateLifeRcpt(LifeReceipts lifeReceipts) throws BadRequestException {
        AllCommissionsDTO allCommissionsDTO = null;
        if (lifeReceipts ==null) {
            throw new BadRequestException("No Receipt transaction Selected....");
        }
        System.out.println("Policy="+lifeReceipts.getPolicyTrans().getPolicyId());
        PolicyTrans policyTrans = lifeReceipts.getPolicyTrans();
        int fullInst=0;
        if(lifeReceipts.getBalanceAmt()==null || lifeReceipts.getBalanceAmt().compareTo(BigDecimal.ZERO)==0){
            throw new BadRequestException("Receipt transaction Selected is fully allocated....");
        }
        if (lifeReceipts.getDrCr().equalsIgnoreCase("C")){
            fullInst = (lifeReceipts.getBalanceAmt().divide(lifeReceipts.getPolicyTrans().getNetPrem())).intValue();

            if (fullInst>0) { // allocates only full instalments
                int counter=0;
                int paidInsts =0;
                if (policyTrans.getPaidInsts()!=null ) {
                    paidInsts =policyTrans.getPaidInsts();
                }else
                    policyTrans.setPaidInsts(0);

                while (counter<fullInst && policyTrans.getPaidInsts()<policyTrans.getTotalInstalments()){
                    counter++;
                    paidInsts++;
                    LifeReceiptAllocations alloc = new LifeReceiptAllocations();
                    alloc.setAllocDate(new Date());
                    alloc.setInstalmentPremium(policyTrans.getNetPrem());
                    alloc.setLifeReceipts(lifeReceipts);
                    alloc.setAllocAmount(policyTrans.getNetPrem());
                    alloc.setPaidToDate(lifeallocationsRepo.getPaidToDate(policyTrans.getPolicyId()));
                    alloc.setInstallNo(paidInsts);
                    alloc.setDoneDate(new Date());
                    alloc.setDoneBy(userUtils.getCurrentUser());
                    LifeReceiptAllocations savedAlloc = lifeallocationsRepo.save(alloc);
                    lifeReceipts.setBalanceAmt(lifeReceipts.getBalanceAmt().subtract(policyTrans.getNetPrem()));
                    if (lifeReceipts.getAllocatedAmt()!=null) {
                        lifeReceipts.setAllocatedAmt(lifeReceipts.getAllocatedAmt().add(savedAlloc.getAllocAmount()));
                    }else {
                        lifeReceipts.setAllocatedAmt(savedAlloc.getAllocAmount());
                    }
                    lifeReceiptsRepo.save(lifeReceipts);
                    allCommissionsDTO = allocateLifeRcptComm(savedAlloc,paidInsts,false);
                    policyTrans.setPaidInsts(paidInsts);
                    policyTrans.setPolPaidToDate( dateUtilities.getPaidToDate(policyTrans.getCoverFrom(),paidInsts) );
                    //System.out.println("xxx="+policyTrans);
                    policyTrans=policyRepo.save(policyTrans);

                }

            }

        }
        return allCommissionsDTO;
    }

    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class }, propagation = Propagation.REQUIRED)
    public AllCommissionsDTO settlePartialInstalment(LifeReceipts lifeReceipts ,
                                        BigDecimal allocAmount,
                                        int paidInsts ,
                                        String refNo,
                                        SystemTrans trans) throws BadRequestException {
         AllCommissionsDTO allCommissionsDTO = null;
        if (lifeReceipts ==null) {
            throw new BadRequestException("No Receipt transaction Selected....");
        }
        PolicyTrans policyTrans = lifeReceipts.getPolicyTrans();
        if (lifeReceipts.getDrCr().equalsIgnoreCase("C")){

            LifeReceiptAllocations alloc = new LifeReceiptAllocations();
            alloc.setAllocDate(new Date());
            alloc.setInstalmentPremium(policyTrans.getNetPrem());
            alloc.setLifeReceipts(lifeReceipts);
            alloc.setAllocAmount(allocAmount);
            alloc.setRefNo(refNo);
            alloc.setTransaction(trans);
            alloc.setPaidToDate(lifeallocationsRepo.getPaidToDate(policyTrans.getPolicyId()));
            alloc.setInstallNo(paidInsts);
            alloc.setDoneDate(new Date());
            alloc.setDoneBy(userUtils.getCurrentUser() != null ? userUtils.getCurrentUser() : policyTrans.getCreatedUser());

            LifeReceiptAllocations savedAlloc = lifeallocationsRepo.save(alloc);
            lifeReceipts.setBalanceAmt(lifeReceipts.getBalanceAmt().subtract(allocAmount));
            if (lifeReceipts.getAllocatedAmt()!=null) {
                lifeReceipts.setAllocatedAmt(lifeReceipts.getAllocatedAmt().add(savedAlloc.getAllocAmount()));
            }else {
                lifeReceipts.setAllocatedAmt(savedAlloc.getAllocAmount());
            }
            lifeReceiptsRepo.save(lifeReceipts);
            allCommissionsDTO = allocateLifeRcptComm(savedAlloc,paidInsts,false);

        }
        return allCommissionsDTO;
    }


    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class }, propagation = Propagation.REQUIRED)
    public AllCommissionsDTO allocateContraLifeRcptBalance(Long polId) throws BadRequestException {
        AllCommissionsDTO allCommissionsDTO = new AllCommissionsDTO();
        final PolicyTrans policyTrans = policyRepo.findOne(polId);
        if(policyTrans==null){
            throw new BadRequestException("Policy Not Found");
        }

        if(policyRepo.getPreviousTrans(polId)==null){
            throw new BadRequestException("Previous Policy Not Found");
        }
        final PolicyTrans prevPolicy = policyRepo.findOne(policyRepo.getPreviousTrans(polId));
        Iterable<SystemTransactions> transactions = systemTransactionsRepo.findAll(QSystemTransactions.systemTransactions.policy.isNotNull()
                .and(QSystemTransactions.systemTransactions.policy.policyId.eq(prevPolicy.getPolicyId())));
        if(transactions==null || transactions.spliterator().getExactSizeIfKnown()==0){
            throw new BadRequestException("No Receipt Available for this policy");
        }
        SystemTransactions prevDebit = null;
        for(SystemTransactions transaction:transactions){
            //transaction.setBalance(transaction.getSettleAmt());
            //transaction.setSettleAmt(BigDecimal.ZERO);
            if(transaction.getTransdc().equalsIgnoreCase("D")){
                prevDebit = transaction;
            }
          //  systemTransactionsRepo.save(transaction);
        }
        List<BigInteger> receipts = receiptRepo.getLifePolReceipts(prevPolicy.getPolicyId());
        if(receipts.isEmpty()){
            throw new BadRequestException("Unable to get previous receipt to continue with cancellation...");
        }
        List<CancelData> cancelDataList = new ArrayList<>();
        for(BigInteger receipt: receipts){
            CancelData cancelData = new CancelData();
            cancelData.setReceiptId(receipt.longValue());
            cancelData.setCommentl("Policy Contra");
            cancelDataList.add(cancelData);
        }
        final long countNB = policyRepo.countRenewals(policyTrans.getPolNo());
        final String transCode = (countNB > 0)?"RNR":"NBR";
        receiptService.cancelReceipts(cancelDataList, true);
        SystemTrans transaction = new SystemTrans();
        transaction.setDoneDate(new Date());
        transaction.setDoneBy(userUtils.getCurrentUser() != null ? userUtils.getCurrentUser() : policyTrans.getCreatedUser());
        transaction.setPolicy(policyTrans);
        transaction.setTransLevel("U");
        transaction.setTransCode(transCode); //A way to setup and look up for transaction transcode
        transaction.setAuthBy(userUtils.getCurrentUser() != null ? userUtils.getCurrentUser() : policyTrans.getCreatedUser());
        transaction.setAuthDate(new Date());
        transaction.setTransAuthorised("Y");
        SystemTrans savedTrans = transRepo.save(transaction);
        Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("D");
        if (sequenceRepository.count(seqPredicate) == 0)
            throw new BadRequestException("Sequence for Debit Notes has not been defined");
        SystemSequence sequence = sequenceRepository.findOne(seqPredicate);
        Long seqNumber = sequence.getNextNumber();
        final String transType = (countNB > 0)?"RN":"NB";
        if (transMappingRepo.count(QTransactionMapping.transactionMapping.transType.eq(transType)) == 0)
            throw new BadRequestException("Error getting Transaction Mapping Setups..Contact System Administrator");
        TransactionMapping mapping = transMappingRepo.findOne(QTransactionMapping.transactionMapping.transType.eq(transType));
        final String refNo = mapping.getCreditCode() + String.format("%05d", seqNumber);
        sequence.setLastNumber(seqNumber);
        sequence.setNextNumber(seqNumber + 1);
        sequenceRepository.save(sequence);

        receiptService.postReceiptAccount(savedTrans,policyTrans,policyTrans.getNetPrem());

        SystemTransactions trans = allocationService.createInstalmentTransaction(refNo, policyTrans.getNetPrem(),
                savedTrans, "POLICY REVERSAL INSTALMENT", policyTrans,true,1L);
        if(prevDebit!=null) {
            if(prevDebit.getWhtx()!=null)
            allCommissionsDTO.setWhtx((prevDebit.getWhtx().multiply(BigDecimal.valueOf(-1))));
            if(prevDebit.getAdminFeeNetWhtx()!=null)
            allCommissionsDTO.setAdminFeeWhtx((prevDebit.getAdminFeeNetWhtx().multiply(BigDecimal.valueOf(-1))));
            if(prevDebit.getSubAgentFee()!=null)
            allCommissionsDTO.setSubAgentComm((prevDebit.getSubAgentFee().multiply(BigDecimal.valueOf(-1))));
            if(prevDebit.getAdminFeeNet()!=null)
            allCommissionsDTO.setAdminFeeTotal((prevDebit.getAdminFeeNet().multiply(BigDecimal.valueOf(-1))));
            if(prevDebit.getCommission()!=null)
            allCommissionsDTO.setCommission((prevDebit.getCommission().multiply(BigDecimal.valueOf(-1))));
            trans.setCommission(prevDebit.getCommission());
            trans.setWhtx(prevDebit.getWhtx());
            trans.setAdminFeeNet(prevDebit.getAdminFeeNet());
            trans.setSubAgentFee(prevDebit.getSubAgentFee());
            trans.setAdminFeeNetWhtx(prevDebit.getAdminFeeNetWhtx());
           SystemTransactions savedTranss = systemTransactionsRepo.save(trans);
           allCommissionsDTO.setTransactionId(savedTranss.getTransno());
           allCommissionsDTO.setPrevTransactionId(prevDebit.getTransno());
        }
        Iterable<LifeReceipts> lifeReceiptsList = lifeReceiptsRepo.findAll(QLifeReceipts.lifeReceipts.policyTrans.policyId.eq(prevPolicy.getPolicyId()));
        for(LifeReceipts receipt:lifeReceiptsList){
            LifeReceipts newReceipt = new LifeReceipts();
            newReceipt.setPolicyTrans(policyTrans);
            newReceipt.setDoneBy(userUtils.getCurrentUser());
            newReceipt.setDoneDate(new Date());
            if(receipt.getReceiptAmt()!=null)
                newReceipt.setReceiptAmt(BigDecimal.valueOf(receipt.getReceiptAmt().doubleValue() * -1));
            if(receipt.getAllocatedAmt()!=null)
                newReceipt.setAllocatedAmt(BigDecimal.valueOf(receipt.getAllocatedAmt().doubleValue() * -1));
            if(receipt.getBalanceAmt()!=null)
                newReceipt.setBalanceAmt(BigDecimal.valueOf(receipt.getBalanceAmt().doubleValue() * -1));
            newReceipt.setDrCr("D");
            newReceipt.setOriginalLifeReceipts(receipt.getOriginalLifeReceipts());
            newReceipt.setReceiptDate(new Date());
            newReceipt.setReceiptTrans(receipt.getReceiptTrans());
            newReceipt.setSystemTransaction(trans);
            newReceipt.setTaxeAmt(receipt.getTaxeAmt());
            newReceipt.setReceiptValueDate(java.sql.Date.valueOf(HolidayUtils.getAdjustedTransactionDate(LocalDate.now())));
            LifeReceipts savedReceipt =  lifeReceiptsRepo.save(newReceipt);
            Iterable<LifeReceiptAllocations> lifeReceiptAllocations = lifeallocationsRepo.findAll(QLifeReceiptAllocations.lifeReceiptAllocations.lifeReceipts.receiptId.eq(receipt.getReceiptId()));
            for(LifeReceiptAllocations allocation:lifeReceiptAllocations){
                LifeReceiptAllocations newAllocation = new LifeReceiptAllocations();
                newAllocation.setAllocAmount((allocation.getAllocAmount()!=null)?allocation.getAllocAmount():null);
                newAllocation.setLifeWhtx((allocation.getLifeWhtx()!=null)?allocation.getLifeWhtx():null);
                newAllocation.setAdminFeeAmt((allocation.getAdminFeeAmt()!=null)?allocation.getAdminFeeAmt():null);
                newAllocation.setAdminFeeAmtWhtx((allocation.getAdminFeeAmtWhtx()!=null)?allocation.getAdminFeeAmtWhtx():null);
                newAllocation.setCommAmount((allocation.getCommAmount()!=null)?allocation.getCommAmount():null);
                newAllocation.setSubAgentCommAmount((allocation.getSubAgentCommAmount()!=null)?allocation.getSubAgentCommAmount():null);
                newAllocation.setMarketerCommAmount((allocation.getMarketerCommAmount()!=null)?allocation.getMarketerCommAmount():null);
                newAllocation.setInstalmentPremium((allocation.getInstalmentPremium()!=null)?allocation.getInstalmentPremium():null);
                newAllocation.setTransaction(savedTrans);
                newAllocation.setRefNo(refNo);
                newAllocation.setPaidToDate(allocation.getPaidToDate());
                newAllocation.setLifeReceipts(savedReceipt);
                newAllocation.setAllocDate(allocation.getAllocDate());
                newAllocation.setInstallNo(allocation.getInstallNo());
                newAllocation.setDoneBy(userUtils.getCurrentUser());
                newAllocation.setDoneDate(new Date());
                lifeallocationsRepo.save(newAllocation);
            }

        }
        System.out.println("Prev Policy id "+prevPolicy.getPolicyId()+" prev..."+prevDebit);
        Iterable<SystemTransactions> debitTransactions = systemTransactionsRepo.findAll(QSystemTransactions.systemTransactions.policy.isNotNull()
                .and(QSystemTransactions.systemTransactions.policy.policyId.eq(prevPolicy.getPolicyId()))
                .and(QSystemTransactions.systemTransactions.narrations.eq("POLICY INSTALMENT"))
                .and(QSystemTransactions.systemTransactions.transdc.eq("D")));
        if(debitTransactions.spliterator().getExactSizeIfKnown()!=1){
            throw new BadRequestException("Unable to get Debit Transaction..Cannot continue.."+debitTransactions.spliterator().getExactSizeIfKnown());
        }
        for(SystemTransactions debitTransaction:debitTransactions){
            debitTransaction.setBalance(BigDecimal.ZERO);
            debitTransaction.setSettleAmt(debitTransaction.getBalance());
            trans.setBalance(BigDecimal.ZERO);
            trans.setSettleAmt(trans.getBalance());
            systemTransactionsRepo.save(trans);
            systemTransactionsRepo.save(debitTransaction);
        }

        Iterable<SystemTransactions> subAgentTrans = systemTransactionsRepo.findAll(QSystemTransactions.systemTransactions.transType.eq("SAG")
                .and(QSystemTransactions.systemTransactions.policy.policyId.eq(prevPolicy.getPolicyId())
                        .and(QSystemTransactions.systemTransactions.transdc.eq("C"))));

        for (SystemTransactions subAgent : subAgentTrans) {
            SystemTransactions sgTrans = new SystemTransactions();
            sgTrans.setAgent(subAgent.getAgent());
            sgTrans.setAmount((subAgent.getAmount().abs().multiply(sign("D"))).setScale(2, BigDecimal.ROUND_HALF_EVEN));
            sgTrans.setAuthDate(new Date());
            sgTrans.setAuthorised("Y");
            sgTrans.setBalance((subAgent.getBalance().abs().multiply(sign("D"))).setScale(2, BigDecimal.ROUND_HALF_EVEN));
            sgTrans.setBranch(subAgent.getBranch());
            sgTrans.setClient(subAgent.getClient());
            sgTrans.setClientType(subAgent.getClientType());
            sgTrans.setCommission(((subAgent.getCommission() == null) ? BigDecimal.ZERO : subAgent.getCommission().abs().multiply(sign("D"))).setScale(2, BigDecimal.ROUND_HALF_EVEN));
            sgTrans.setControlAcc(subAgent.getControlAcc());
            sgTrans.setCurrency(subAgent.getCurrency());
            sgTrans.setCurrRate(subAgent.getCurrRate());
            sgTrans.setExtras(((subAgent.getExtras() == null) ? BigDecimal.ZERO : subAgent.getExtras().abs().multiply(sign("D"))).setScale(2, BigDecimal.ROUND_HALF_EVEN));
            sgTrans.setFundParams(subAgent.getFundParams());
            sgTrans.setIssueCardFee((subAgent.getIssueCardFee() == null) ? BigDecimal.ZERO : subAgent.getIssueCardFee().abs().multiply(sign("D")));
            sgTrans.setNarrations("Sub Agent Commission");
            sgTrans.setNetAmount(((subAgent.getNetAmount() == null) ? BigDecimal.ZERO : subAgent.getNetAmount().abs().multiply(sign("D"))).setScale(2, BigDecimal.ROUND_HALF_EVEN));
            sgTrans.setOrigin(subAgent.getOrigin());
//					sgTrans.setOtherRef(subAgent.getOrigin()+"/CN");
            sgTrans.setPayeeName(subAgent.getPayeeName());
            sgTrans.setPhfund(((subAgent.getPhfund() == null) ? BigDecimal.ZERO : subAgent.getPhfund().abs().multiply(sign("D"))).setScale(2, BigDecimal.ROUND_HALF_EVEN));
            sgTrans.setPolicy(subAgent.getPolicy());
            sgTrans.setPostedDate(new Date());
            sgTrans.setPostedUser(userUtils.getCurrentUser());
//            sgTrans.setRefNo(subAgent.getRefNo()+"/CN");
            sgTrans.setRefNo(refNo);
            sgTrans.setReIssueCardFee(((subAgent.getReIssueCardFee() == null) ? BigDecimal.ZERO : subAgent.getReIssueCardFee().abs().multiply(sign("D"))).setScale(2, BigDecimal.ROUND_HALF_EVEN));
            sgTrans.setSd(((subAgent.getSd() == null) ? BigDecimal.ZERO : subAgent.getSd().negate()).setScale(2, BigDecimal.ROUND_HALF_EVEN));
            sgTrans.setServiceCharge(((subAgent.getServiceCharge() == null) ? BigDecimal.ZERO : subAgent.getServiceCharge().abs().multiply(sign("D"))).setScale(2, BigDecimal.ROUND_HALF_EVEN));
            sgTrans.setSettleAmt(((subAgent.getSettleAmt() == null) ? BigDecimal.ZERO : subAgent.getSettleAmt().abs().multiply(sign("D")).setScale(2, BigDecimal.ROUND_HALF_EVEN)));
            sgTrans.setTl((subAgent.getTl() == null) ? BigDecimal.ZERO : subAgent.getTl().abs().multiply(sign("D")));
            sgTrans.setTransaction(savedTrans);
            sgTrans.setTransDate(new Date());
            sgTrans.setTransdc("D");
            sgTrans.setTransType(subAgent.getTransType());
            sgTrans.setWhtx(((subAgent.getWhtx() == null) ? BigDecimal.ZERO : subAgent.getWhtx().abs().multiply(sign("D"))).setScale(2, BigDecimal.ROUND_HALF_EVEN));
            systemTransactionsRepo.save(sgTrans);
        }

        return allCommissionsDTO;
    }

    private BigDecimal sign(String type) {
        return ("C".equalsIgnoreCase(type) ? BigDecimal.ONE.multiply(BigDecimal.valueOf(-1)) : BigDecimal.ONE.multiply(BigDecimal.valueOf(1)));
    }

    @Override
    @Modifying
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class }, propagation = Propagation.REQUIRED)
    public AllCommissionsDTO allocateLifeRcptBalance(Long polId) throws BadRequestException {
        List<Object[]> lifeReceipts = lifeReceiptsRepo.findPolicyLifeReceipts(polId);
        AllCommissionsDTO allCommissionsDTO = null;
        BigDecimal totBalance = BigDecimal.ZERO;
        for(Object[] row:lifeReceipts) {
            totBalance = totBalance.add((BigDecimal)row[3]);
        }

        BigDecimal balToAllocate;
        BigDecimal allocatedAmount ;
        BigDecimal instBal;
        PolicyTrans policyTrans = policyRepo.findOne(polId);
        if (policyTrans.getNetPrem() != null && totBalance.compareTo(policyTrans.getNetPrem()) >= 0) {
            int paidInst = 0;
            String revisionFormat = policyTrans.getRefNo();
            String refNo = "";
            if (policyTrans.getPaidInsts() != null) {
                paidInst = policyTrans.getPaidInsts();
            } else
                policyTrans.setPaidInsts(0);
            if (totBalance.compareTo(policyTrans.getNetPrem()) >= 0 && totBalance.compareTo(BigDecimal.ZERO) > 0) {
                SystemTrans transaction = new SystemTrans();
                transaction.setDoneDate(new Date());
                transaction.setDoneBy(userUtils.getCurrentUser() != null ? userUtils.getCurrentUser() : policyTrans.getCreatedUser());
                transaction.setPolicy(policyTrans);
                transaction.setTransLevel("U");
                transaction.setTransCode("NBD"); //A way to setup and look up for transaction transcode
                transaction.setAuthBy(userUtils.getCurrentUser() != null ? userUtils.getCurrentUser() : policyTrans.getCreatedUser());
                transaction.setAuthDate(new Date());
                transaction.setTransAuthorised("Y");
                SystemTrans savedTrans = transRepo.save(transaction);
                balToAllocate =totBalance;// policyTrans.getNetPrem().multiply(new BigDecimal(fullInsts));

                instBal = policyTrans.getNetPrem();
                SystemTransactions trans = new SystemTransactions();
                for (Object[]  rct : lifeReceipts) {
                    BigDecimal rctBalance = (BigDecimal)rct[3];
                    final LifeReceipts lifeReceipts1 = lifeReceiptsRepo.findOne(QLifeReceipts.lifeReceipts.receiptId.eq(((BigInteger)rct[0]).longValue()));
                    final Long transNo = lifeReceiptsRepo.findPolicyLifeReceiptsId(((BigInteger)rct[0]).longValue());
                    int insts;
                    //if frequency is single set installments to 1
                    if(policyTrans.getFrequency().toUpperCase().contains("SG")){
                        insts = 1;
                    }else{
                        insts = policyTrans.getTotalInstalments();
                    }

                 //   while(rctBalance.compareTo(BigDecimal.ZERO) > 0 && policyTrans.getPaidInsts() < policyTrans.getTotalInstalments()
                   //         && balToAllocate.compareTo(BigDecimal.ZERO) > 0) {
                       while(rctBalance.compareTo(BigDecimal.ZERO) > 0 && policyTrans.getPaidInsts() < insts && balToAllocate.compareTo(BigDecimal.ZERO) > 0) {
                        if (instBal.compareTo(BigDecimal.ZERO) == 0) {
                            instBal = policyTrans.getNetPrem();
                        }
                        if (instBal.compareTo(policyTrans.getNetPrem())==0) {
                            paidInst++;
                            String transType = policyTrans.getTransType();
                            Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("D");
                            if (sequenceRepository.count(seqPredicate) == 0)
                                throw new BadRequestException("Sequence for Debit Notes has not been defined");
                            SystemSequence sequence = sequenceRepository.findOne(seqPredicate);
                            if(paidInst > 1){
                                transType = "EN";
                            }
                            Long seqNumber = sequence.getNextNumber();
                            if (transMappingRepo.count(QTransactionMapping.transactionMapping.transType.eq(transType)) == 0)
                                throw new BadRequestException("Error getting Transaction Mapping Setups..Contact System Administrator");
                            TransactionMapping mapping = transMappingRepo.findOne(QTransactionMapping.transactionMapping.transType.eq(transType));
                             refNo = mapping.getDebitCode() + String.format("%05d", seqNumber);
                            sequence.setLastNumber(seqNumber);
                            sequence.setNextNumber(seqNumber + 1);
                            sequenceRepository.save(sequence);
                            trans = allocationService.createInstalmentTransaction(refNo, policyTrans.getNetPrem(),
                                    savedTrans, "POLICY INSTALMENT", policyTrans,false,(long)paidInst);

                        }
                        if (rctBalance.compareTo(instBal) > 0) {
                            allCommissionsDTO = settlePartialInstalment(lifeReceipts1, instBal, paidInst, refNo, savedTrans);
                            allocatedAmount = instBal;
                            accountsService.allocateCreditTrans(transNo, trans.getTransno(), allocatedAmount);
                        } else {
                            allCommissionsDTO = settlePartialInstalment(lifeReceipts1, rctBalance, paidInst, refNo, savedTrans);
                            allocatedAmount = rctBalance;
                            accountsService.allocateCreditTrans(transNo, trans.getTransno(), allocatedAmount);
                        }
                           allCommissionsDTO.setInstallmentNo(paidInst);
                           allCommissionsDTO.setBalance(trans.getBalance());
                           trans.setCommission(allCommissionsDTO.getCommission());
                           trans.setWhtx(allCommissionsDTO.getWhtx());
                           trans.setAdminFeeNet(allCommissionsDTO.getAdminFeeTotal());
                           trans.setSubAgentFee(allCommissionsDTO.getSubAgentComm());
                           trans.setAdminFeeNetWhtx(allCommissionsDTO.getAdminFeeWhtx());
                           if(paidInst > 1){
                               trans.setTransType("APD");
                           }
                          SystemTransactions savedTranss =  systemTransactionsRepo.save(trans);
                           allCommissionsDTO.setTransactionId(savedTranss.getTransno());
                        instBal = instBal.subtract(allocatedAmount);
                        rctBalance = rctBalance.subtract(allocatedAmount);
                        balToAllocate = balToAllocate.subtract(allocatedAmount);
                        if (instBal.compareTo(BigDecimal.ZERO) == 0) {
                             Iterable<PolicyInstallments> installments = policyInstallmentsRepo.findAll(QPolicyInstallments.policyInstallments.policyTrans.policyId.eq(polId)
                                    .and(QPolicyInstallments.policyInstallments.installmentNo.eq((long) paidInst)));
                            for(PolicyInstallments installment: installments) {
                                installment.setInstallPaid("Y");
                                installment.setPaidDate(lifeReceipts1.getReceiptDate());
                            }
                            policyInstallmentsRepo.save(installments);
                            if(paidInst > 1){
                                policyTrans.setTransType("EN");
                                policyTrans.setPolRevNo(trans.getEndorsementNo());
                            }
                            policyTrans.setPaidInsts(paidInst);
                            policyTrans.setPolPaidToDate(dateUtilities.getPaidToDate(policyTrans.getCoverFrom(), paidInst));
                            policyTrans = policyRepo.save(policyTrans);
                        }


                    }
                }
                ///// settlement concept here
            }
        }
        return allCommissionsDTO;
    }

    @Override
    public void definePolicyDependents(PolicyDependentsInfoDTO dependentsInfoDTO) throws BadRequestException {
        PolicyDependentsInfo policyDependentsInfo = policyDependentsRepo.findByPolicy(dependentsInfoDTO.getPolicyId());

        policyDependentsInfo.setFullName(dependentsInfoDTO.getFullName());

        policyDependentsRepo.save(policyDependentsInfo);
    }

    @Override
    public DataTablesResult<PolicyDependentsInfo> findPolDependents(DataTablesRequest request, Long polCode) throws IllegalAccessException {
        BooleanExpression pred = QPolicyDependentsInfo.policyDependentsInfo.policyId.eq(polCode);
        Page<PolicyDependentsInfo>  page = policyDependentsRepo.findAll(pred.and(request.searchPredicate(QPolicyDependentsInfo.policyDependentsInfo)),request);
        return new DataTablesResult<>(request, page);
    }

}