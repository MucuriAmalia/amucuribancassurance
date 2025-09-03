package com.brokersystems.brokerapp.life.service.impl;

import com.brokersystems.brokerapp.enums.RevenueItems;
import com.brokersystems.brokerapp.life.model.*;
import com.brokersystems.brokerapp.life.repository.*;
import com.brokersystems.brokerapp.life.service.LifeEndorseService;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.exception.EndorsementsException;
import com.brokersystems.brokerapp.server.utils.DateUtilities;
import com.brokersystems.brokerapp.server.utils.Streamable;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.BinderReqrdDocsRepo;
import com.brokersystems.brokerapp.setup.repository.SubclassReqDocRepo;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.brokersystems.brokerapp.trans.model.QReceiptTrans;
import com.brokersystems.brokerapp.trans.model.QSystemTransactions;
import com.brokersystems.brokerapp.trans.model.SystemTrans;
import com.brokersystems.brokerapp.trans.repository.ReceiptRepository;
import com.brokersystems.brokerapp.trans.repository.SystemTransactionsRepo;
import com.brokersystems.brokerapp.uw.model.*;
import com.brokersystems.brokerapp.uw.repository.*;
import com.brokersystems.brokerapp.uw.service.EndorseService;
import com.brokersystems.brokerapp.uw.service.PolicyTransService;
import com.brokersystems.brokerapp.workflow.docs.DocType;
import com.brokersystems.brokerapp.workflow.utils.WorkflowService;
import com.google.common.collect.Maps;
import com.mysema.query.types.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by waititu on 22/06/2019.
 */
@Service
public class LifeEndorseServiceImpl implements LifeEndorseService {
    @Autowired
    private PolicyTransRepo policyRepo;

    @Autowired
    private ParamService paramService;

    @Autowired
    private DateUtilities dateUtils;

    @Autowired
    private PolicyTransService polTransService;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private PolTaxesRepo polTaxesRepo;

    @Autowired
    private PolClausesRepo polClausesRepo;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private RiskTransRepo riskTransRepo;

    @Autowired
    private SectionTransRepo sectionTransRepo;

    @Autowired
    private RiskDocsRepo riskDocsRepo;

    @Autowired
    private BinderReqrdDocsRepo reqrdDocsRepo;

    @Autowired
    private SubclassReqDocRepo subclassReqDocRepo;

    @Autowired
    private PolicyBeneficiariesRepo beneficiariesRepo;

    @Autowired
    private PolicyBenefitsDistributionRepo maturityRepo;
    @Autowired
    private PolicyRemarksRepo policyRemarksRepo;
    @Autowired
    private PolicyInstallmentsRepo policyInstallmentsRepo;

    @Autowired
    private EndorseService endorseService;
    @Autowired
    private LifeReceiptsRepo lifeReceiptsRepo;
    @Autowired
    private LifeReceiptAllocationsRepo lifeReceiptAllocationsRepo;
    @Autowired
    private SystemTransactionsRepo systemTransactionsRepo;
    @Autowired
    private ReceiptRepository receiptRepository;


    @PreAuthorize("hasAnyAuthority('ENDORSE_POLICY')")
    @Override
    @Transactional(readOnly = false,propagation = Propagation.REQUIRES_NEW, rollbackFor = { EndorsementsException.class })
    public Long reviseTransaction(RevisionForm revisionForm) throws EndorsementsException {
        if (revisionForm.getPolicyId() == null || revisionForm.getPolicyId() == null)
            throw new EndorsementsException("Policy to Revise cannot be Empty....");

        if (revisionForm.getRevisionType() == null || StringUtils.isBlank(revisionForm.getRevisionType()))
            throw new EndorsementsException("Select Revision Type...");





        PolicyTrans currentTrans = policyRepo.findOne(revisionForm.getPolicyId());

        if("CN".equalsIgnoreCase(revisionForm.getRevisionType()) && dateUtils.getNumberOfMonths(currentTrans.getCoverFrom(),currentTrans.getCoverTo()) > 12){
            throw new EndorsementsException("Cancellation can only be done on a short term life policy...");
        }

        Long pendingTransaction = this.countUnauthTransactions(currentTrans.getPolNo());
        if(pendingTransaction != null && pendingTransaction > 0) {
            System.out.println("Deleting existing pending transactions.");
            List<Object[]> policiesList = policyRepo.findPendingTransactions(currentTrans.getPolNo(), 0, 10);
            for (Object[] policy : policiesList) {
                Long policyId = (((BigInteger) policy[5]).longValue());
                try {
                    endorseService.deletePolicyRecord(policyId,true);//
                } catch (BadRequestException e) {
                    throw new EndorsementsException(e.getMessage());
                }
            }
        }

// Prevent general policies from being processed in life endorsement module
        if (!"L".equalsIgnoreCase(currentTrans.getProduct().getProGroup().getPrgType()) &&
                !"L".equalsIgnoreCase(currentTrans.getBusinessType())) {
            throw new EndorsementsException("General endorsements cannot happen in the life module. Please go to the General Endorsements Module");
        }


        Predicate activePred = QPolicyTrans.policyTrans.polNo.eq(currentTrans.getPolNo())
                .and(QPolicyTrans.policyTrans.currentStatus.eq("A"));

        if (countUnauthTransactions(currentTrans.getPolNo()) > 0) {
            throw new EndorsementsException(
                    "The Policy has unfinished Transaction");
        }

        if(policyRepo.count(activePred)> 1){
            throw new EndorsementsException("The current Policy has an active Endorsement. Only One Active Endorsement can be allowed");
        }

        if(!("EX".equalsIgnoreCase(revisionForm.getRevisionType()))){
            if("SP".equalsIgnoreCase( currentTrans.getTransType())) throw new EndorsementsException("Endorsement Type not Supported on Short Period Policy Transactions");
        }

        if("EX".equalsIgnoreCase(revisionForm.getRevisionType())){
            long extensionCount = policyRepo.count(QPolicyTrans.policyTrans.polNo.eq(currentTrans.getPolNo()).and(QPolicyTrans.policyTrans.transType.eq("EX")));
            try {
                long maxExtensions = paramService.getParamValue("MAX_EXTENSIONS").longValue();
                if(maxExtensions == extensionCount) throw new EndorsementsException("The maximum number of extensions on this policy has reached. Cannot do Extension");
            } catch (BadRequestException e) {
                throw new EndorsementsException(e.getMessage());
            }
            if(currentTrans.isRenewable())
                throw new EndorsementsException("Extension is not allowed on renewable policies");
            if(revisionForm.getEffToDate()==null){
                throw new EndorsementsException("Extension Cover to Date cannot be null");
            }

            if(revisionForm.getEffToDate().before(revisionForm.getEffectiveDate())){
                throw new EndorsementsException("Effective to Date Cannot be Earlier than Effective from Date");
            }

            if(revisionForm.getEffToDate().before(currentTrans.getWefDate())){
                throw new EndorsementsException("Effective to Date Cannot be Earlier than Current Policy Wef Date ("+dateUtils.formatDate(currentTrans.getWefDate())+")");
            }

            if(revisionForm.getEffectiveDate().before(currentTrans.getWetDate())){
                throw new EndorsementsException("Effective to Date Cannot less than Previous Policy Wet Date("+dateUtils.formatDate(currentTrans.getWetDate())+")");
            }

            Date extendDate = DateUtils.addDays(currentTrans.getWetDate(), 1);
            if(revisionForm.getEffectiveDate().after(extendDate) || revisionForm.getEffectiveDate().before(extendDate))
                throw new EndorsementsException("Effective Date Should be Date immediately After Wet Date ("+dateUtils.formatDate(extendDate)+")");

        }
        if(!("EX".equalsIgnoreCase(revisionForm.getRevisionType()))){
            if(revisionForm.getEffectiveDate().after(currentTrans.getWetDate()) ||
                    revisionForm.getEffectiveDate().before(currentTrans.getWefDate())){
                throw new EndorsementsException("Effective Date must be within Policy Period for Revision of Cover Transaction("+ dateUtils.formatDate(currentTrans.getWefDate()) +" to "+ dateUtils.formatDate(currentTrans.getWetDate()) +")");
            }
        }
        if(revisionForm.getRevisionType().equalsIgnoreCase("NB")
                || revisionForm.getRevisionType().equalsIgnoreCase("SP")){
            throw new EndorsementsException("Endorsement Type not catered For. Contact the Vendor");
        }

        long endorsementCount = policyRepo.count(QPolicyTrans.policyTrans.polNo.eq(currentTrans.getPolNo()))+1;

        PolicyTrans destination = new PolicyTrans();
        destination.setWefDate(revisionForm.getEffectiveDate());
        destination.setWetDate(currentTrans.getWetDate());
        if("EX".equalsIgnoreCase(revisionForm.getRevisionType())){
            destination.setCoverFrom(currentTrans.getCoverFrom());
            destination.setCoverTo(revisionForm.getEffToDate());
            destination.setWetDate(revisionForm.getEffToDate());
            if(currentTrans.isRenewable())
                destination.setRenewalDate(DateUtils.addDays(revisionForm.getEffToDate(), 1));
        }
        else{
            destination.setCoverFrom(currentTrans.getCoverFrom());
            destination.setCoverTo(currentTrans.getCoverTo());
            if(currentTrans.isRenewable())
                destination.setRenewalDate(DateUtils.addDays(revisionForm.getEffToDate(), 1));
        }
        destination.setUwYear(currentTrans.getUwYear());
        destination.setPolRevStatus(revisionForm.getRevisionType());
        if(currentTrans.getRevisionFormat()==null){
            destination.setPolRevNo(currentTrans.getPolRevNo().substring(0,currentTrans.getPolRevNo().indexOf("/"))+"/"+endorsementCount);
        }else
            destination.setPolRevNo(currentTrans.getRevisionFormat()+"/"+endorsementCount);
        destination.setPolNo(currentTrans.getPolNo());
        destination.setAgent(currentTrans.getAgent());
        if("CN".equalsIgnoreCase(revisionForm.getRevisionType())){
            destination.setAuthStatus("CV");
            destination.setCurrentStatus("CV");
        }
        else {
            destination.setAuthStatus("D");
            destination.setCurrentStatus("D");
        }
        destination.setBinder(currentTrans.getBinder());
        destination.setBranch(currentTrans.getBranch());
        destination.setClient(currentTrans.getClient());
        destination.setClientPolNo(currentTrans.getClientPolNo());
        destination.setCommAllowed(currentTrans.isCommAllowed());
        destination.setCreatedUser(userUtils.getCurrentUser());

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
        destination.setTransType(revisionForm.getRevisionType());
        destination.setSubAgent(currentTrans.getSubAgent());
        destination.setSumInsured(currentTrans.getSumInsured()); //BigDecimal.ZERO);
        destination.setPrevEndosbasicPremium(currentTrans.getEndosbasicPremium());
        destination.setTotalInstalments(currentTrans.getTotalInstalments());
        destination.setPolTerm(currentTrans.getPolTerm());
        destination.setProposalNo(currentTrans.getProposalNo());
        destination.setPaidInsts(currentTrans.getPaidInsts());
        destination.setNetPrem(currentTrans.getNetPrem());
        destination.setPolPaidToDate(currentTrans.getPolPaidToDate());
//        if(currentTrans.getTotalInstalments() !=null && currentTrans.getTotalInstalments() > 1){
//            destination.setInstallmentNo(currentTrans.getInstallmentNo()+1);
//        }


        Iterable<PolicyTaxes> taxes = polTaxesRepo.findAll(QPolicyTaxes.policyTaxes.policy.policyId.eq(currentTrans.getPolicyId()));
        List<PolicyTaxes> newTaxes = new ArrayList<>();
        for(PolicyTaxes tax:taxes){
            if(tax.getRevenueItems().getItem() == RevenueItems.SD)
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
        for(PolicyClauses clause:clauses){
            PolicyClauses newClause = new PolicyClauses();
            newClause.setClauHeading(clause.getClauHeading());
            newClause.setClause(clause.getClause());
            newClause.setClauWording(clause.getClauWording());
            newClause.setEditable(clause.isEditable());
            newClause.setNewClause("N");
            newClause.setPolicy(destination);
            newClauses.add(newClause);
        }

        Iterable<PolicyBenefitsDistribution> maturities = maturityRepo.findAll(QPolicyBenefitsDistribution.policyBenefitsDistribution.policyId.policyId.eq(currentTrans.getPolicyId()));
        List<PolicyBenefitsDistribution> newMaturities = new ArrayList<>();
        for (PolicyBenefitsDistribution mat:maturities){
            PolicyBenefitsDistribution newMat = new PolicyBenefitsDistribution();
            newMat.setMaturityYear(mat.getMaturityYear());
            newMat.setPolicyId(destination);
            newMat.setMaturityExpDate(mat.getMaturityExpDate());
            newMat.setEstBenefit(mat.getEstBenefit());
            newMaturities.add(newMat);
        }

        Iterable<PolicyInstallments> policyInstallmentsList = policyInstallmentsRepo.findAll(QPolicyInstallments.policyInstallments.policyTrans.policyId.eq(revisionForm.getPolicyId()));

        Iterable<PolicyBeneficiaries> beneficiaries = beneficiariesRepo.findAll(QPolicyBeneficiaries.policyBeneficiaries.policy.policyId.eq(currentTrans.getPolicyId()));
        List<PolicyBeneficiaries> newBeneficiaries = new ArrayList<>();
        for (PolicyBeneficiaries ben:beneficiaries){
            PolicyBeneficiaries newBeneficiary = new PolicyBeneficiaries();
            newBeneficiary.setPolicy(destination);
            newBeneficiary.setBenAllocation(ben.getBenAllocation());
            newBeneficiary.setBeneficiaryemailAddress(ben.getBeneficiaryemailAddress());
            newBeneficiary.setBeneficiaryName(ben.getBeneficiaryName());
            newBeneficiary.setBeneficiarypostalAddress(ben.getBeneficiarypostalAddress());
            newBeneficiary.setBeneficiaryregNo(ben.getBeneficiaryregNo());
            newBeneficiary.setBeneficiarytelNo(ben.getBeneficiarytelNo());
            newBeneficiary.setDateCreated(ben.getDateCreated());
            newBeneficiary.setDateRegistered(ben.getDateRegistered());
            newBeneficiary.setRelationshipType(ben.getRelationshipType());
            newBeneficiary.setUser(ben.getUser());
            newBeneficiaries.add(newBeneficiary);
        }
        PolicyTrans savedPolicy = policyRepo.save(destination);
        polClausesRepo.save(newClauses);
        polTaxesRepo.save(newTaxes);
        maturityRepo.save(newMaturities);
        beneficiariesRepo.save(newBeneficiaries);

        Optional<PolicyInstallments> policyInstallmentsOptional = Streamable.streamOf(policyInstallmentsList).filter(a -> a.getInstallmentNo()==1L).findAny();

        if(policyInstallmentsOptional.isPresent()){
            final PolicyInstallments policyInstallments = new PolicyInstallments();
            policyInstallments.setInstallPaid(policyInstallmentsOptional.get().getInstallPaid());
            policyInstallments.setPolicyTrans(savedPolicy);
            policyInstallments.setPaidDate(policyInstallmentsOptional.get().getPaidDate());
            policyInstallments.setInstallmentNo(policyInstallmentsOptional.get().getInstallmentNo());
            policyInstallments.setInstallPrem((policyInstallmentsOptional.get().getInstallPrem().multiply(BigDecimal.valueOf(-1))));
            if(policyInstallmentsOptional.get().getExpectedSubAgentCommission()!=null)
                policyInstallments.setExpectedCommission((policyInstallmentsOptional.get().getExpectedCommission().multiply(BigDecimal.valueOf(-1))));
            if(policyInstallmentsOptional.get().getExpectedSubAgentCommission()!=null)
                policyInstallments.setExpectedSubAgentCommission((policyInstallmentsOptional.get().getExpectedSubAgentCommission().multiply(BigDecimal.valueOf(-1))));
            if(policyInstallmentsOptional.get().getExpectedMarkerterCommission()!=null)
                policyInstallments.setExpectedMarkerterCommission((policyInstallmentsOptional.get().getExpectedMarkerterCommission().multiply(BigDecimal.valueOf(-1))));
            policyInstallments.setDueDate(policyInstallmentsOptional.get().getDueDate());
            policyInstallments.setNotificationSent(true);
            policyInstallments.setInstallmentNo(1L);
            policyInstallmentsRepo.save(policyInstallments);
        }
        if("CN".equalsIgnoreCase(revisionForm.getRevisionType())) {
            Iterable<LifeReceipts> lifeReceipts = lifeReceiptsRepo.findAll(QLifeReceipts.lifeReceipts.policyTrans.policyId.eq(revisionForm.getPolicyId()));
            for (LifeReceipts lifeReceiptss : lifeReceipts) {
                final Long rctId = lifeReceiptsRepo.findPolicyLifeReceiptsRec(lifeReceiptss.getReceiptId());
                final Long trans = lifeReceiptsRepo.findPolicyLifeReceiptsId(lifeReceiptss.getReceiptId());
                LifeReceipts lifeReceipt = new LifeReceipts();
                lifeReceipt.setPolicyTrans(savedPolicy);
                lifeReceipt.setDrCr("D");
                lifeReceipt.setReceiptAmt(lifeReceiptss.getReceiptAmt());
                lifeReceipt.setReceiptDate(lifeReceiptss.getReceiptDate());
                lifeReceipt.setReceiptValueDate(lifeReceiptAllocationsRepo.getPaidToDate(savedPolicy.getPolicyId()));
                lifeReceipt.setReceiptTrans(receiptRepository.findOne(QReceiptTrans.receiptTrans.receiptId.eq(rctId)));
                lifeReceipt.setBalanceAmt(lifeReceipt.getBalanceAmt());
                lifeReceipt.setDoneBy(userUtils.getCurrentUser());
                lifeReceipt.setDoneDate(new Date());
                lifeReceipt.setSystemTransaction(systemTransactionsRepo.findOne(QSystemTransactions.systemTransactions.transno.eq(trans)));
                LifeReceipts savedReceipt = lifeReceiptsRepo.save(lifeReceipt);

                Iterable<LifeReceiptAllocations> allocations = lifeReceiptAllocationsRepo.findAll(QLifeReceiptAllocations.lifeReceiptAllocations.lifeReceipts.receiptId.eq(lifeReceiptss.getReceiptId()));
                for (LifeReceiptAllocations allocation : allocations) {
                    LifeReceiptAllocations lifeReceiptAllocations = new LifeReceiptAllocations();
                    lifeReceiptAllocations.setAdminFeeAmt(allocation.getAdminFeeAmt());
                    lifeReceiptAllocations.setAllocAmount(allocation.getAllocAmount());
                    lifeReceiptAllocations.setAllocDate(allocation.getAllocDate());
                    lifeReceiptAllocations.setLifeReceipts(savedReceipt);
                    lifeReceiptAllocations.setDoneBy(userUtils.getCurrentUser());
                    lifeReceiptAllocations.setCommAmount(allocation.getCommAmount());
                    lifeReceiptAllocations.setTransaction(allocation.getTransaction());
                    lifeReceiptAllocations.setInstallNo(allocation.getInstallNo());
                    lifeReceiptAllocations.setInstalmentPremium(allocation.getInstalmentPremium());
                    lifeReceiptAllocations.setSubAgentCommAmount(allocation.getSubAgentCommAmount());
                    lifeReceiptAllocations.setLifeWhtx(allocation.getLifeWhtx());
                    lifeReceiptAllocations.setRefNo(allocation.getRefNo());
                    lifeReceiptAllocations.setPaidToDate(lifeReceiptAllocationsRepo.getPaidToDate(savedPolicy.getPolicyId()));
                    lifeReceiptAllocations.setMarketerCommAmount(allocation.getMarketerCommAmount());
                    lifeReceiptAllocations.setAdminFeeAmtWhtx(allocation.getAdminFeeAmtWhtx());
                    lifeReceiptAllocations.setDoneDate(allocation.getDoneDate());
                    lifeReceiptAllocationsRepo.save(lifeReceiptAllocations);
                }
            }
        }

        try {
            PolicyRemarks remarks = new PolicyRemarks();
            EndorsementRemarks endorsementRemarks = policyRemarksRepo.findremarksByshtDesc(revisionForm.getRemarks().toUpperCase());//getEndorsementRemarks(savedPolicy.getPolicyId(), "", null);
            remarks.setEndRemarks(endorsementRemarks);
            remarks.setPolicy(savedPolicy);
            remarks.setRemarks(revisionForm.getRemarks().toUpperCase());
            polTransService.saveEndorsementRemarks(remarks);
        } catch (BadRequestException e) {
            throw new RuntimeException(e);
        }
        Iterable<RiskTrans> risks = riskTransRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(currentTrans.getPolicyId()));
        List<RiskTrans> newRisks  = new ArrayList<>();
        for(RiskTrans risk:risks){
            Set<RiskDocs> riskDocs = new HashSet<>();
            RiskTrans newRisk = new RiskTrans();
            newRisk.setSubclass(risk.getSubclass());
            newRisk.setPolicy(savedPolicy);
            if(risk.getRiskShtDesc()!= null) {
                newRisk.setRiskShtDesc(risk.getRiskShtDesc());
            }
            newRisk.setRiskIdentifier(risk.getRiskIdentifier());
            newRisk.setComputeType(risk.getComputeType());
            newRisk.setPremium(risk.getPremium());
            newRisk.setBinder(risk.getBinder());
            newRisk.setBinderDetails(risk.getBinderDetails());
            newRisk.setButchargePrem(risk.getButchargePrem());
            newRisk.setCalcPremium(risk.getCalcPremium());
            newRisk.setCovertype(risk.getCovertype());
            newRisk.setFreeLimit(risk.getFreeLimit());
            newRisk.setFuturePrem(risk.getFuturePrem());
            newRisk.setStampDuty(risk.getStampDuty());
            newRisk.setInstallAmount(risk.getInstallAmount());
            newRisk.setInsured(risk.getInsured());
            newRisk.setWefDate(destination.getWefDate());
            newRisk.setWetDate(risk.getWetDate());
            newRisk.setWorkingAge(risk.getWorkingAge());
            newRisk.setTransType(revisionForm.getRevisionType());
            newRisk.setProrata(risk.getProrata());
            newRisk.setPhfFund(risk.getPhfFund());
            newRisk.setTrainingLevy(risk.getTrainingLevy());
            newRisk.setNetpremium(risk.getNetpremium());
            RiskTrans savedRisk = riskTransRepo.save(newRisk);
            Iterable<SectionTrans> sections = sectionTransRepo.findAll(QSectionTrans.sectionTrans.risk.riskId.eq(risk.getRiskId()));
            List<SectionTrans> newSections = new ArrayList<>();
            for (SectionTrans sectionTrans:sections){
                SectionTrans newSection = new SectionTrans();
                newSection.setPremRates(sectionTrans.getPremRates());
                newSection.setRate(sectionTrans.getRate());
                newSection.setSection(sectionTrans.getSection());
                newSection.setMultiRate(sectionTrans.getMultiRate());
                newSection.setFreeLimit(sectionTrans.getFreeLimit());
                newSection.setAmount(sectionTrans.getAmount());
                newSection.setCalcprem(sectionTrans.getCalcprem());
                newSection.setCompute(sectionTrans.isCompute());
                newSection.setDivFactor(sectionTrans.getDivFactor());
                newSection.setRisk(savedRisk);
                newSections.add(newSection);
            }
            sectionTransRepo.save(newSections);
            Iterable<BinderReqrdDocs> reqdDocs = reqrdDocsRepo.findAll(QBinderReqrdDocs.binderReqrdDocs.binderDetail.detId.eq(newRisk.getBinderDetails().getDetId())
                    .and(QBinderReqrdDocs.binderReqrdDocs.mandatory.eq(true))
                    .and(QBinderReqrdDocs.binderReqrdDocs.requiredDocs.requiredDoc.appliesEndorsement.eq(true)));
            riskDocs = Streamable.streamOf(reqdDocs).filter(reqdDoc -> riskDocsRepo.count(QRiskDocs.riskDocs.risk.riskIdentifier.eq(newRisk.getRiskIdentifier()).and(QRiskDocs.riskDocs.reqdDocs.sclReqrdId.eq(reqdDoc.getRequiredDocs().getSclReqrdId())))==0)
                    .map(reqdDoc -> {
                        RiskDocs riskDoc = new RiskDocs();
                        riskDoc.setReqdDocs(reqdDoc.getRequiredDocs());
                        riskDoc.setRisk(newRisk);
                        return riskDoc;
                    }).collect(Collectors.toSet());

            Iterable<SubClassReqdDocs> subClassReqDocs = subclassReqDocRepo.findAll(QSubClassReqdDocs.subClassReqdDocs.subclass.subId.eq(newRisk.getBinderDetails().getSubCoverTypes().getSubclass().getSubId())
                    .and(QSubClassReqdDocs.subClassReqdDocs.mandatory.eq(true))
                    .and(QSubClassReqdDocs.subClassReqdDocs.requiredDoc.appliesEndorsement.eq(true)));
            riskDocs.addAll(Streamable.streamOf(subClassReqDocs).filter(reqdDoc -> riskDocsRepo.count(QRiskDocs.riskDocs.risk.riskIdentifier.eq(newRisk.getRiskIdentifier()).and(QRiskDocs.riskDocs.reqdDocs.sclReqrdId.eq(reqdDoc.getSclReqrdId())))==0)
                    .map(reqdDoc ->{
                        RiskDocs riskDoc = new RiskDocs();
                        riskDoc.setReqdDocs(reqdDoc);
                        riskDoc.setRisk(newRisk);
                        return riskDoc;
                    }).collect(Collectors.toSet()));
            riskDocsRepo.save(riskDocs);

        }
        workflowService.startNewWorkFlow(DocType.GEN_UW_DOCUMENT,String.valueOf(savedPolicy.getPolicyId()),savedPolicy,"N",null,null,null, null);
//        workflowService.startNewWorkFlow(DocType.GEN_UW_DOCUMENT,String.valueOf(savedPolicy.getPolicyId()),savedPolicy,"N",null,null,null);
        if("CN".equalsIgnoreCase(revisionForm.getRevisionType())){

            Map<String, Object> processVariables = Maps.newHashMap();
            processVariables.put("canAuthorize",true);
            workflowService.completeTask(String.valueOf(savedPolicy.getPolicyId()),processVariables,savedPolicy,DocType.GEN_UW_DOCUMENT,"N",null,null,null,null);
        }
        return savedPolicy.getPolicyId();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countUnauthTransactions(String policyNumber) {
        if(policyNumber==null) return 0L;
        Predicate pred = QPolicyTrans.policyTrans.polNo.eq(policyNumber)
                .and(QPolicyTrans.policyTrans.currentStatus.eq("D"));
        long count = policyRepo.count(pred);
        return count;
    }
}
