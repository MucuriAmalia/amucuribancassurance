package com.brokersystems.brokerapp.medical.service.impl;

import com.brokersystems.brokerapp.medical.model.*;
import com.brokersystems.brokerapp.medical.repository.*;
import com.brokersystems.brokerapp.medical.service.MedicalClmService;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.DateUtilities;
import com.brokersystems.brokerapp.server.utils.Streamable;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.SequenceRepository;
import com.brokersystems.brokerapp.setup.repository.SubclassReqDocRepo;
import com.brokersystems.brokerapp.trans.model.SystemTransactions;
import com.brokersystems.brokerapp.trans.repository.SystemTransactionsRepo;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.model.QPolicyTrans;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.BooleanExpression;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by peter on 5/12/2017.
 */
@Service
public class MedicalClmServiceImpl implements MedicalClmService {

    @Autowired
    private CategoryMembersRepo membersRepo;

    @Autowired
    private MedEventsRepo eventsRepo;

    @Autowired
    private SequenceRepository sequenceRepo;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private MedicalParRepo medicalParRepo;

    @Autowired
    private ParRequestRepo requestRepo;

    @Autowired
    private ParRequestServicesRepo requestServicesRepo;

    @Autowired
    private AilmentsRepo ailmentsRepo;

    @Autowired
    private MedActivitiesRepo activitiesRepo;

    @Autowired
    private CategoryBenefitRepo benefitRepo;

    @Autowired
    private PolicyTransRepo policyTransRepo;

    @Autowired
    private SelfFundParamsRepo selfFundParamsRepo;

    @Autowired
    private SystemTransactionsRepo systemTransactionsRepo;

    @Autowired
    private getMedicalMembersRepo medicalMembersRepo;

    @Autowired
    private DateUtilities dateUtilities;

    @Autowired
    private RequestServicesLogRepo servicesLogRepo;

    @Autowired
    private SubclassReqDocRepo reqDocRepo;

    @Autowired
    private MedParDocsRepo parDocsRepo;

    @Override
    @Transactional(readOnly = true)
    public Page<MedicalEvents> findEvents(String paramString, Pageable paramPageable) {
        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred = QMedicalEvents.medicalEvents.isNotNull();
        } else {
            pred = QMedicalEvents.medicalEvents.eventDesc.containsIgnoreCase(paramString)
                    .or(QMedicalEvents.medicalEvents.eventShtDesc.containsIgnoreCase(paramString));
        }
        return eventsRepo.findAll(pred, paramPageable);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<CategoryMembers> findClaimMembers(DataTablesRequest request, Long policyId, String cardNo,
                                                              String memberName, String gender,
                                                              Long clientId) throws IllegalAccessException {
        Page<CategoryMembers> page = null;
        if(cardNo==null) cardNo="";
        if(memberName==null) memberName="";

        if(gender==null) gender="";
        if(policyId==null && clientId==null){
            page = membersRepo.searchCategoryMembers(memberName,gender,cardNo,request);
        }
        else if(policyId==null && clientId!=null){
            page = membersRepo.searchCategoryMembers(clientId,memberName,gender,cardNo,"",request);
        }
        else if(policyId!=null && clientId==null){
            page = membersRepo.searchCategoryMembers(policyId,memberName,gender,cardNo,request);
        }
        else if(policyId!=null && clientId!=null){
            page = membersRepo.searchCategoryMembers(policyId,clientId,memberName,gender,cardNo,request);
        }
        return new DataTablesResult(request, page);
        //   return medicalMembersRepo.findClaimMembers(memberNo);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<CategoryMembers> findClmMembers(DataTablesRequest request, String policyNo, String memberNo,
                                                            String memberName, Long age, String gender, Date dob,
                                                            String clientName) throws IllegalAccessException {
        if(policyNo==null)
            policyNo="";
        if(memberNo==null)
            memberNo="";
        if(memberName==null)
            memberName="";
        if(clientName==null)
            clientName="";
        if(gender==null)
            gender = "";
        if(age==null)
            age = -200l;
        return medicalMembersRepo.findClaimMembers(memberNo);
    }
    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public Long createPreauthTrans(MedicalParTrans parTrans) throws BadRequestException {
        boolean manualCtrl = (parTrans.getManualControl()!=null && "on".equalsIgnoreCase(parTrans.getManualControl()));
        boolean exgratia = (parTrans.getExgratiaService()!=null && "on".equalsIgnoreCase(parTrans.getExgratiaService()));
        List<RequestLogBean> requestLogs = new ArrayList<>();
        if(!manualCtrl)
            exgratia = false;
        if(parTrans.getEvents()==null) throw new BadRequestException("Select Event");
        if(parTrans.getClientDef()==null) throw new BadRequestException("Select Member");
        if(parTrans.getMedicalNetworks()==null) throw new BadRequestException("Select Claim Network");
        if(parTrans.getProviderContracts()==null) throw new BadRequestException("Select Provider Contract");
        if(parTrans.getEventDate()==null) throw new BadRequestException("Event Date is Mandatory");
        if(parTrans.getNotDate()==null) throw new BadRequestException("Notification Date is Mandatory");
        if(parTrans.getCountry()==null) throw new BadRequestException("Select Event Country");
        if(exgratia)
        {
            if(parTrans.getExgratiaAmount()==null)
                throw new BadRequestException("Exgratia Amount is Mandatory....");
        }
        if(parTrans.getServicePlace()==null)
            throw new BadRequestException("Select Service Place to continue...");
        if(!("OT".equalsIgnoreCase(parTrans.getServicePlace()))){
            if(parTrans.getAdmissionDate()==null)
                throw new BadRequestException("Select Admission Date...");
            if(parTrans.getExpectedAdmDate()==null)
                throw new BadRequestException("Select Expected Discharge Date...");
        }
        if(parTrans.getNotDate().after(new Date())){
            throw new BadRequestException("Notification Date cannot be greater than the current date");
        }
        if(parTrans.getNotDate().before(parTrans.getEventDate())){
            throw new BadRequestException("Notification Date cannot be less than event date...");
        }
        PolicyTrans policyTrans = parTrans.getPolicyTrans();
        if(parTrans.getEventDate().after(policyTrans.getWetDate()))
            throw new BadRequestException("Event Date Cannot be outside policy period");
        if(parTrans.getEventDate().before(policyTrans.getWefDate()))
            throw new BadRequestException("Event Date Cannot be outside policy period");


        if(parTrans.getParId()!=null){
            MedicalParTrans trans = medicalParRepo.findOne(parTrans.getParId());
            parTrans.setTransDate(trans.getTransDate());
            parTrans.setTransUser(trans.getTransUser());
            parTrans.setParNo(trans.getParNo());
            parTrans.setParRevisionNo(trans.getParRevisionNo());
            parTrans.setTotalCalcAmount(trans.getTotalCalcAmount());
            parTrans.setTotalRequestAmount(trans.getTotalRequestAmount());
            parTrans.setParStatus(trans.getParStatus());
            parTrans.setTransType(trans.getTransType());
            parTrans.setTotalRequestAmount(trans.getTotalRequestAmount());
            MedicalParTrans medicalParTrans =  medicalParRepo.save(parTrans);
            Iterable<SubClassReqdDocs> reqdDocs = reqDocRepo.findAll(QSubClassReqdDocs.subClassReqdDocs.subclass.subId.eq(parTrans.getCategoryMember().getCategory()
                    .getBinderDetails().getSubCoverTypes().getSubclass().getSubId())
                    .and(QSubClassReqdDocs.subClassReqdDocs.mandatory.eq(true))
                    .and(QSubClassReqdDocs.subClassReqdDocs.requiredDoc.appliesLossOpening.eq(true)));
            Iterable<MedParReqDocs> medParReqDocs = parDocsRepo.findAll(QMedParReqDocs.medParReqDocs.parTrans.parId.eq(medicalParTrans.getParId()));
            List<MedParReqDocs> parReqDocs = new ArrayList<>();
            for(SubClassReqdDocs reqdDoc:reqdDocs){
                if(!containsSclDocs(medParReqDocs,reqdDoc)) {
                    MedParReqDocs parReqDoc = new MedParReqDocs();
                    parReqDoc.setReqdDocs(reqdDoc);
                    parReqDoc.setParTrans(medicalParTrans);
                    parReqDocs.add(parReqDoc);
                }
            }

            parDocsRepo.save(parReqDocs);
            return medicalParTrans.getParId();
        }
        if(parTrans.getActivityId()==null) throw new BadRequestException("Select Activity");
        if(parTrans.getDiagnosisId()==null) throw new BadRequestException("Select Diagnosis");
        if(parTrans.getQty()==null) throw new BadRequestException("Enter Requested Quantity");
        if(parTrans.getPrice()==null) throw new BadRequestException("Enter Requested Price");
        if(manualCtrl) {
            if (parTrans.getApprovedQty() == null) throw new BadRequestException("Enter Approved Quantity");
            if (parTrans.getApprovedPrice() == null) throw new BadRequestException("Enter Approved Price");
        }
        if(parTrans.getServiceDate()==null) throw new BadRequestException("Select Service Date");
        if(parTrans.getEventDate()==null) throw new BadRequestException("Event Date is Mandatory");
        if(parTrans.getRequestDate()==null) throw new BadRequestException("Select Request Date");
        if(parTrans.getRequestDate().before(parTrans.getEventDate()))
            throw new BadRequestException("Request Date cannot be before Event Date...");
        if(parTrans.getRequestDate().after(parTrans.getPolicyTrans().getWetDate()))
            throw new BadRequestException("Request Date cannot be outside the policy date...");
        if(parTrans.getServiceDate().before(parTrans.getRequestDate()))
            throw new BadRequestException("Service Date Cannot be Before Request Date...");
        if(parTrans.getServiceDate().after(parTrans.getPolicyTrans().getWetDate()))
            throw new BadRequestException("Service Date Cannot be outside policy...");
        if(parTrans.getRequestDate().before(parTrans.getEventDate()))
            throw new BadRequestException("Request Date cannot be before Event Date...");
        MedActivities activities = activitiesRepo.findOne(parTrans.getActivityId());
        if(manualCtrl) {
            if ((parTrans.getApprovedQty().multiply(parTrans.getApprovedPrice())).compareTo(parTrans.getQty().multiply(parTrans.getPrice())) == 1)
                throw new BadRequestException("Total Approved Amount Cannot be greater than Total Requested Amount");
        }
        boolean rejectAmt = false;
        String rejectShtDesc = "";
        String rejectReason = "";
        if(activities.getSection()==null)
            throw new BadRequestException("Activity Selected does not have a benefit attached...");
        SectionsDef sectionsDef = activities.getSection();
        Iterable<MedCategoryBenefits> catBenefits = benefitRepo.findAll(QMedCategoryBenefits.medCategoryBenefits.cover.section.id.eq(sectionsDef.getId())
                                                                        .and(QMedCategoryBenefits.medCategoryBenefits.category.eq(parTrans.getCategory())));

        Iterable<SubClassReqdDocs> reqdDocs = reqDocRepo.findAll(QSubClassReqdDocs.subClassReqdDocs.subclass.subId.eq(parTrans.getCategoryMember().getCategory()
                                        .getBinderDetails().getSubCoverTypes().getSubclass().getSubId())
                .and(QSubClassReqdDocs.subClassReqdDocs.mandatory.eq(true))
                .and(QSubClassReqdDocs.subClassReqdDocs.requiredDoc.appliesLossOpening.eq(true)));
         if(catBenefits.spliterator().getExactSizeIfKnown() > 1)
            throw new BadRequestException("Error getting benefits for this Policy . Consult the System Administrator..");
        if(catBenefits.spliterator().getExactSizeIfKnown() ==0){
            rejectAmt  = true;
            rejectShtDesc = "BENREJ";
            rejectReason = "Unable to get Benefit Details for the Activity";
            requestLogs.add(new RequestLogBean(rejectShtDesc,rejectReason));
        }
        MedCategoryBenefits categoryBenefits = null;
        for(MedCategoryBenefits benefits:catBenefits){
            categoryBenefits = benefits;
            break;
        }
        if(categoryBenefits.getWaitPeriod()!=null){
            int waitPeriod = categoryBenefits.getWaitPeriod();
            int timeElapsed = dateUtilities.getNumberOfMonths(policyTrans.getAuthDate() , new Date());
            if(waitPeriod > timeElapsed){
                rejectAmt = true;
                rejectShtDesc = "WAITPRD";
                rejectReason = "The Benefit Selected has a waiting period";
                requestLogs.add(new RequestLogBean(rejectShtDesc,rejectReason));
            }

        }

        boolean principalOrSpouse ="P".equalsIgnoreCase(parTrans.getCategoryMember().getDependentTypes()) ||"S".equalsIgnoreCase(parTrans.getCategoryMember().getDependentTypes());
        if(categoryBenefits.getCover().isDependsOnGender() && principalOrSpouse){
            if(parTrans.getClientDef().getGender()!=null){
                rejectAmt = true;
            } else throw new BadRequestException("Member Gender Cannot be null...");
        }

        BigDecimal remLimit = calculateLimit(categoryBenefits.getSectId()).subtract(parTrans.getQty().multiply(parTrans.getPrice()));

        if(remLimit.compareTo(BigDecimal.ZERO)<=0){
            rejectAmt=true;
            rejectShtDesc = "BENLIMIT";
            rejectReason = "The Remaining limit for the benefit is not enough to take care of the claim";
            requestLogs.add(new RequestLogBean(rejectShtDesc,rejectReason));
        }


        boolean fund = (parTrans.getPolicyTrans().getBinder().getFundBinder()!=null && "Y".equalsIgnoreCase(parTrans.getPolicyTrans().getBinder().getFundBinder()));

        if(fund){
            Iterable<SelfFundParams> selfFundParams = selfFundParamsRepo.findAll(QSelfFundParams.selfFundParams.policyTrans.policyId.eq(parTrans.getPolicyTrans().getPolicyId()));
            if(selfFundParams.spliterator().getExactSizeIfKnown()==0)
                throw new BadRequestException("No Fund Parameters for Policy...Check the policy to continue..");

            if(selfFundParams.spliterator().getExactSizeIfKnown() > 1)
                throw new BadRequestException("Error on Fund Parameters for Policy...Consult the System Administrator.");

            SelfFundParams fundParams = null;

            for(SelfFundParams selfFundParam:selfFundParams)
            {
                fundParams = selfFundParam;
                break;
            }
            //TO Minimal Fund for Self Fund
        }
        if(manualCtrl) {
            if (parTrans.getApprovedQty().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Approved Quantity cannot be zero or less than zero");
            }
            if (parTrans.getApprovedQty().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Approved Quantity cannot be zero or less than zero");
            }
        }
        if(parTrans.getQty().compareTo(BigDecimal.ZERO)<=0){
            throw new BadRequestException("Requested Quantity cannot be zero or less than zero");
        }
        if(parTrans.getPrice().compareTo(BigDecimal.ZERO)<=0){
            throw new BadRequestException("Requested Price cannot be zero or less than zero");
        }
        if(parTrans.getRequestDate().before(parTrans.getEventDate()))
            throw new BadRequestException("Request Date cannot be before Event Date...");
        if(parTrans.getRequestDate().after(parTrans.getPolicyTrans().getWetDate()))
            throw new BadRequestException("Request Date cannot be outside the policy date...");
        if(parTrans.getServiceDate().before(parTrans.getRequestDate()))
            throw new BadRequestException("Service Date Cannot be Before Request Date...");
        if(parTrans.getServiceDate().after(parTrans.getPolicyTrans().getWetDate()))
            throw new BadRequestException("Service Date Cannot be outside policy...");
        BigDecimal diff = BigDecimal.ZERO;
        BigDecimal exgratiaAmt = (exgratia)?parTrans.getExgratiaAmount():BigDecimal.ZERO;

        if(manualCtrl)
         diff = (parTrans.getQty().multiply(parTrans.getPrice())).subtract(parTrans.getApprovedQty().multiply(parTrans.getApprovedPrice()).add(exgratiaAmt));
        Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("CL");
        if (sequenceRepo.count(seqPredicate) == 0)
            throw new BadRequestException("Sequence for Claims Transactions has not been defined");

        SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
        Long seqNumber = sequence.getNextNumber();
        sequence.setLastNumber(seqNumber);
        sequence.setNextNumber(seqNumber + 1);
        sequenceRepo.save(sequence);
        final String claimNumber = sequence.getSeqPrefix() + String.format("%05d", seqNumber);
        parTrans.setTransDate(new Date());
        parTrans.setTransUser(userUtils.getCurrentUser());
        parTrans.setParNo(claimNumber);
        parTrans.setParRevisionNo(claimNumber+"/1");
        parTrans.setTotalCalcAmount((manualCtrl)?parTrans.getApprovedQty().multiply(parTrans.getApprovedPrice()):(parTrans.getQty().multiply(parTrans.getPrice())));
        parTrans.setTotalRequestAmount(parTrans.getQty().multiply(parTrans.getPrice()));
        parTrans.setParStatus("Open");
        parTrans.setTransType("P");
        if(exgratia){
            parTrans.setTotalexgratiaAmount(parTrans.getExgratiaAmount());
        }
        if(rejectAmt)
            parTrans.setTotalRejectedAmount((parTrans.getQty().multiply(parTrans.getPrice())));
        else
            parTrans.setTotalRejectedAmount(diff);
        parTrans.setCurrentStatus("D");
        parTrans.setTotalRequestAmount(parTrans.getQty().multiply(parTrans.getPrice()));
        parTrans.setTotalApprAmount(parTrans.getQty().multiply(parTrans.getPrice()).subtract(parTrans.getTotalRejectedAmount()));
        MedicalParTrans savedTrans = medicalParRepo.save(parTrans);
        Set<MedParReqDocs> parReqDocs = new HashSet<>();
        for(SubClassReqdDocs reqdDoc:reqdDocs){
            MedParReqDocs parReqDoc = new MedParReqDocs();
            parReqDoc.setReqdDocs(reqdDoc);
            parReqDoc.setParTrans(savedTrans);
            parReqDocs.add(parReqDoc);
        }
        parDocsRepo.save(parReqDocs);
        MedicalParRequest parRequest = new MedicalParRequest();
        parRequest.setAilments(ailmentsRepo.findOne(parTrans.getDiagnosisId()));
        if(rejectAmt)
            parRequest.setRejectAmount((parTrans.getQty().multiply(parTrans.getPrice())));
        else
            parRequest.setRejectAmount(diff);
        parRequest.setRequestAmount(parTrans.getQty().multiply(parTrans.getPrice()));
        parRequest.setDescription(parTrans.getRequestDescription());
        parRequest.setCalcAmount((manualCtrl)?parTrans.getApprovedQty().multiply(parTrans.getApprovedPrice()):parTrans.getQty().multiply(parTrans.getPrice()));
        parRequest.setParTrans(savedTrans);
        if(exgratia){
            parRequest.setExgratiaAmount(parTrans.getExgratiaAmount());
        }
        parRequest.setServicePlace(parTrans.getServicePlace());
        parRequest.setRequestDate(parTrans.getRequestDate());
        parRequest.setRequestType(parTrans.getRequestType());
        parRequest.setApprovedAmount(parTrans.getQty().multiply(parTrans.getPrice()).subtract(parRequest.getRejectAmount()));
        MedicalParRequest savedRequest = requestRepo.save(parRequest);
        MedicalRequestServices requestServices = new MedicalRequestServices();
        requestServices.setMedActivities(activities);
        if(rejectAmt) {
            requestServices.setRejAmount((parTrans.getQty().multiply(parTrans.getPrice())));
        }
        else
            requestServices.setRejAmount(diff);
        requestServices.setCategoryBenefits(categoryBenefits);
        requestServices.setReqAmount(parTrans.getQty().multiply(parTrans.getPrice()));
        requestServices.setReqPrice(parTrans.getPrice());
        requestServices.setReqQuantity(parTrans.getQty());
        requestServices.setCalcAmount((manualCtrl)?parTrans.getApprovedQty().multiply(parTrans.getApprovedPrice()):parTrans.getQty().multiply(parTrans.getPrice()));
        if(exgratia){
            requestServices.setExgratiaAmount(parTrans.getExgratiaAmount());
            requestServices.setExgratiaService("Y");
        }
        else{
            requestServices.setExgratiaService("N");
        }
        if(manualCtrl) {
            requestServices.setAppPrice(parTrans.getApprovedPrice());
            requestServices.setAppQuantity(parTrans.getApprovedQty());
            requestServices.setManualControl("Y");
        }
        else{
            requestServices.setAppPrice(BigDecimal.ZERO);
            requestServices.setAppQuantity(BigDecimal.ZERO);
            requestServices.setManualControl("N");
        }
        requestServices.setAppAmount(parTrans.getQty().multiply(parTrans.getPrice()).subtract(requestServices.getRejAmount()));
        requestServices.setRequest(savedRequest);
        requestServices.setServiceDate(parTrans.getServiceDate());
        MedicalRequestServices savedService = requestServicesRepo.save(requestServices);
        if(rejectShtDesc!=null) {
            createRequestServiceLog( savedService,requestLogs);
        }
        return savedTrans.getParId();
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<MedicalParTrans> enquirePreuathTrans(DataTablesRequest request) throws IllegalAccessException {
        BooleanExpression pred = QMedicalParTrans.medicalParTrans.parStatus.eq("Open");
        Page<MedicalParTrans> page = medicalParRepo.findAll(pred.and(request.searchPredicate(QMedicalParTrans.medicalParTrans)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<MedicalParTrans> batchTrans(DataTablesRequest request, Long contractId) throws IllegalAccessException {
        BooleanExpression pred = QMedicalParTrans.medicalParTrans.parStatus.eq("Authorised")
                                .and(QMedicalParTrans.medicalParTrans.transType.eq("C"))
                                .and(QMedicalParTrans.medicalParTrans.providerContracts.spcId.eq(contractId))
                                .and(QMedicalParTrans.medicalParTrans.batched.isNull().or(QMedicalParTrans.medicalParTrans.batched.endsWith("N")))
                                .and(QMedicalParTrans.medicalParTrans.totalApprAmount.gt(BigDecimal.ZERO));
        Page<MedicalParTrans> page = medicalParRepo.findAll(pred.and(request.searchPredicate(QMedicalParTrans.medicalParTrans)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<MedicalParRequest> enquireParRequests(DataTablesRequest request, Long parId) throws IllegalAccessException {
        BooleanExpression pred = QMedicalParRequest.medicalParRequest.parTrans.parId.eq(parId);
        Page<MedicalParRequest> page = requestRepo.findAll(pred.and(request.searchPredicate(QMedicalParRequest.medicalParRequest)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<MedicalRequestServices> getRequestServices(DataTablesRequest request, Long reqId) throws IllegalAccessException {
        BooleanExpression pred = QMedicalRequestServices.medicalRequestServices.request.reqId.eq(reqId);
        Page<MedicalRequestServices> page = requestServicesRepo.findAll(pred.and(request.searchPredicate(QMedicalRequestServices.medicalRequestServices)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = true)
    public MedicalParTrans getMedicalClaimDetails(Long parId) throws BadRequestException {
        //if(parId==null) throw new BadRequestException("Select Transaction to continue..");
        return medicalParRepo.findOne(parId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MedCategoryBenefits> findBenefits(String paramString, Pageable paramPageable, Long catId) {
        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred =QMedCategoryBenefits.medCategoryBenefits.category.id.eq(catId).and(QMedCategoryBenefits.medCategoryBenefits.isNotNull());
        } else {
            pred =QMedCategoryBenefits.medCategoryBenefits.category.id.eq(catId).and(QMedCategoryBenefits.medCategoryBenefits.cover.section.desc.containsIgnoreCase(paramString)
                    .or(QMedCategoryBenefits.medCategoryBenefits.cover.section.shtDesc.containsIgnoreCase(paramString)));
        }
        return benefitRepo.findAll(pred, paramPageable);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void authPreauth(Long parId) throws BadRequestException {
        if(parId==null) throw new BadRequestException("Select Transaction to continue..");
        MedicalParTrans parTrans = getMedicalClaimDetails(parId);
        Iterable<MedicalRequestServices> requests = requestServicesRepo.findAll(QMedicalRequestServices.medicalRequestServices.request.parTrans.parId.eq(parId));
        for(MedicalRequestServices request:requests){
            if(request.getAppAmount().compareTo(BigDecimal.ZERO)==0)
                throw new BadRequestException("Approved Amount cannot be zero");
            if(calculateLimit(request.getCategoryBenefits().getSectId()).compareTo(request.getAppAmount())==-1)
                throw new BadRequestException("Limit Amount is less than invoice amount requested");
        }
        parTrans.setParStatus("Authorised");
        parTrans.setCurrentStatus("A");
        parTrans.setAuthDate(new Date());
        parTrans.setAuthUser(userUtils.getCurrentUser());
        medicalParRepo.save(parTrans);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public Long createClmTrans(MedicalParTrans parTrans) throws BadRequestException {
        boolean manualCtrl = (parTrans.getManualControl()!=null && "on".equalsIgnoreCase(parTrans.getManualControl()));
        boolean exgratia = (parTrans.getExgratiaService()!=null && "on".equalsIgnoreCase(parTrans.getExgratiaService()));
        List<RequestLogBean> requestLogs = new ArrayList<>();
        if(!manualCtrl)
            exgratia = false;
        if(parTrans.getEvents()==null) throw new BadRequestException("Select Event");
        if(parTrans.getClientDef()==null) throw new BadRequestException("Select Member");
        if(parTrans.getMedicalNetworks()==null) throw new BadRequestException("Select Claim Network");
        if(parTrans.getProviderContracts()==null) throw new BadRequestException("Select Provider Contract");
        if(parTrans.getEventDate()==null) throw new BadRequestException("Event Date is Mandatory");
        if(parTrans.getNotDate()==null) throw new BadRequestException("Notification Date is Mandatory");
        if(parTrans.getCountry()==null) throw new BadRequestException("Select Event Country");
        if(exgratia)
        {
            if(parTrans.getExgratiaAmount()==null)
                throw new BadRequestException("Exgratia Amount is Mandatory....");
        }
        if(parTrans.getServicePlace()==null)
            throw new BadRequestException("Select Service Place to continue...");
        if(!("OT".equalsIgnoreCase(parTrans.getServicePlace()))){
            if(parTrans.getAdmissionDate()==null)
                throw new BadRequestException("Select Admission Date...");
            if(parTrans.getExpectedAdmDate()==null)
                throw new BadRequestException("Select Expected Discharge Date...");
        }
        if(parTrans.getAdmissionDate()!=null && parTrans.getExpectedAdmDate()!=null){
            if(parTrans.getAdmissionDate().after(parTrans.getExpectedAdmDate()))
                throw new BadRequestException("Admission Date cannot be greater than discharge Date");
            if(parTrans.getAdmissionDate().before(parTrans.getEventDate())){
                throw new BadRequestException("Admission Date Cannot be before Event Date");
            }
        }
        if(parTrans.getNotDate().after(new Date())){
            throw new BadRequestException("Notification Date cannot be greater than the current date");
        }
        if(parTrans.getNotDate().before(parTrans.getEventDate())){
            throw new BadRequestException("Notification Date cannot be less than event date...");
        }
        PolicyTrans policyTrans = parTrans.getPolicyTrans();
        if(parTrans.getEventDate().after(new Date()))
            throw new BadRequestException("Event Date Cannot be after the Current Date");
        if(parTrans.getEventDate().after(policyTrans.getWetDate()))
            throw new BadRequestException("Event Date Cannot be outside policy period");
        if(parTrans.getEventDate().before(policyTrans.getWefDate()))
            throw new BadRequestException("Event Date Cannot be outside policy period");
        if(parTrans.getParId()!=null){
            MedicalParTrans trans = medicalParRepo.findOne(parTrans.getParId());
            parTrans.setTransDate(trans.getTransDate());
            parTrans.setTransUser(trans.getTransUser());
            parTrans.setParNo(trans.getParNo());
            parTrans.setParRevisionNo(trans.getParRevisionNo());
            parTrans.setTotalCalcAmount(trans.getTotalCalcAmount());
            parTrans.setTotalRequestAmount(trans.getTotalRequestAmount());
            parTrans.setParStatus(trans.getParStatus());
            parTrans.setTransType(trans.getTransType());
            parTrans.setTotalRequestAmount(trans.getTotalRequestAmount());
            parTrans.setTotalInvoiceAmount(trans.getTotalInvoiceAmount());
            MedicalParTrans medicalParTrans =  medicalParRepo.save(parTrans);
            Iterable<SubClassReqdDocs> reqdDocs = reqDocRepo.findAll(QSubClassReqdDocs.subClassReqdDocs.subclass.subId.eq(parTrans.getCategoryMember().getCategory()
                    .getBinderDetails().getSubCoverTypes().getSubclass().getSubId())
                    .and(QSubClassReqdDocs.subClassReqdDocs.mandatory.eq(true))
                    .and(QSubClassReqdDocs.subClassReqdDocs.requiredDoc.appliesLossOpening.eq(true)));
            Iterable<MedParReqDocs> medParReqDocs = parDocsRepo.findAll(QMedParReqDocs.medParReqDocs.parTrans.parId.eq(medicalParTrans.getParId()));
            Set<MedParReqDocs> parReqDocs = new HashSet<>();
            for(SubClassReqdDocs reqdDoc:reqdDocs){
                if(!containsSclDocs(medParReqDocs,reqdDoc)) {
                    MedParReqDocs parReqDoc = new MedParReqDocs();
                    parReqDoc.setReqdDocs(reqdDoc);
                    parReqDoc.setParTrans(medicalParTrans);
                    parReqDocs.add(parReqDoc);
                }
            }
            parDocsRepo.save(parReqDocs);
            return medicalParTrans.getParId();
        }
        if(parTrans.getActivityId()==null) throw new BadRequestException("Select Activity");
        if(parTrans.getDiagnosisId()==null) throw new BadRequestException("Select Diagnosis");
        if(parTrans.getQty()==null) throw new BadRequestException("Enter Requested Quantity");
        if(parTrans.getPrice()==null) throw new BadRequestException("Enter Requested Price");
        if(manualCtrl) {
            if (parTrans.getApprovedPrice() == null) throw new BadRequestException("Enter Approved Price");
            if (parTrans.getApprovedQty() == null) throw new BadRequestException("Enter Approved Quantity");
        }
        if(parTrans.getServiceDate()==null) throw new BadRequestException("Select Service Date");
        if(parTrans.getRequestDate()==null) throw new BadRequestException("Select Request Date");
        if(parTrans.getRequestDate().before(parTrans.getEventDate()))
            throw new BadRequestException("Request Date cannot be before Event Date...");
        if(parTrans.getRequestDate().after(parTrans.getPolicyTrans().getWetDate()))
            throw new BadRequestException("Request Date cannot be outside the policy date...");
        if(parTrans.getServiceDate().before(parTrans.getRequestDate()))
            throw new BadRequestException("Service Date Cannot be Before Request Date...");
        if(parTrans.getServiceDate().after(parTrans.getPolicyTrans().getWetDate()))
            throw new BadRequestException("Service Date Cannot be outside policy...");
        if(parTrans.getInvoiceAmount()==null || parTrans.getInvoiceAmount().compareTo(BigDecimal.ZERO) <=0){
            throw new BadRequestException("Invoice Amount is required when creating claim");
        }
        if(parTrans.getInvoiceNumber()==null){
            throw new BadRequestException("Invoice Number is Required");
        }

        if(parTrans.getInvoiceDate()==null){
            throw new BadRequestException("Invoice Date is Required");
        }

        if(parTrans.getAdmissionDate()!=null && parTrans.getExpectedAdmDate()!=null){
            if(parTrans.getAdmissionDate().after(parTrans.getExpectedAdmDate()))
                throw new BadRequestException("Admission Date cannot be greater than discharge Date");
            if(parTrans.getAdmissionDate().before(parTrans.getEventDate())){
                throw new BadRequestException("Admission Date Cannot be before Event Date");
            }
        }
        if(parTrans.getRequestDate()==null) throw new BadRequestException("Request Date cannot be null");
        if(parTrans.getRequestDate().before(parTrans.getEventDate()))
            throw new BadRequestException("Request Date cannot be before Event Date...");
        if(manualCtrl) {
            if ((parTrans.getApprovedQty().multiply(parTrans.getApprovedPrice())).compareTo(parTrans.getQty().multiply(parTrans.getPrice())) == 1)
                throw new BadRequestException("Total Approved Amount Cannot be greater than Total Requested Amount");
        }
        MedActivities activities = activitiesRepo.findOne(parTrans.getActivityId());
        boolean rejectAmt = false;
        String rejectShtDesc = "";
        String rejectReason = "";
        if(activities.getSection()==null)
            throw new BadRequestException("Activity Selected does not have a benefit attached...");
        SectionsDef sectionsDef = activities.getSection();
        Iterable<MedCategoryBenefits> catBenefits = benefitRepo.findAll(QMedCategoryBenefits.medCategoryBenefits.cover.section.id.eq(sectionsDef.getId())
                .and(QMedCategoryBenefits.medCategoryBenefits.category.eq(parTrans.getCategory())));
        Iterable<SubClassReqdDocs> reqdDocs = reqDocRepo.findAll(QSubClassReqdDocs.subClassReqdDocs.subclass.subId.eq(parTrans.getCategoryMember().getCategory()
                .getBinderDetails().getSubCoverTypes().getSubclass().getSubId())
                .and(QSubClassReqdDocs.subClassReqdDocs.mandatory.eq(true))
                .and(QSubClassReqdDocs.subClassReqdDocs.requiredDoc.appliesLossOpening.eq(true)));
        if(catBenefits.spliterator().getExactSizeIfKnown() > 1)
            throw new BadRequestException("Error getting benefits for this Policy . Consult the System Administrator..");
        if(catBenefits.spliterator().getExactSizeIfKnown() ==0){
            rejectAmt  = true;
            rejectShtDesc = "BENREJ";
            rejectReason = "Unable to get Benefit Details for the Activity";
            requestLogs.add(new RequestLogBean(rejectShtDesc,rejectReason));
        }
        MedCategoryBenefits categoryBenefits = null;
        for(MedCategoryBenefits benefits:catBenefits){
            categoryBenefits = benefits;
            break;
        }

        if(categoryBenefits.getWaitPeriod()!=null){
            int waitPeriod = categoryBenefits.getWaitPeriod();
            int timeElapsed = dateUtilities.getNumberOfMonths(policyTrans.getAuthDate() , new Date());
            if(waitPeriod > timeElapsed) {
                rejectAmt = true;
                rejectShtDesc = "WAITPRD";
                rejectReason = "The Benefit Selected has a waiting period ";
                requestLogs.add(new RequestLogBean(rejectShtDesc,rejectReason));
            }
        }

        boolean principalOrSpouse ="P".equalsIgnoreCase(parTrans.getCategoryMember().getDependentTypes()) ||"S".equalsIgnoreCase(parTrans.getCategoryMember().getDependentTypes());

        BigDecimal remLimit = calculateLimit(categoryBenefits.getSectId()).subtract(parTrans.getQty().multiply(parTrans.getPrice()));

        if(remLimit.compareTo(BigDecimal.ZERO)<=0) {
            rejectAmt = true;
            rejectShtDesc = "BENLIMIT";
            rejectReason = "The Remaining limit for the benefit is not enough to take care of the claim";
            requestLogs.add(new RequestLogBean(rejectShtDesc,rejectReason));
        }

       boolean fund = (parTrans.getPolicyTrans().getBinder().getFundBinder()!=null && "Y".equalsIgnoreCase(parTrans.getPolicyTrans().getBinder().getFundBinder()));

        if(fund){
            Iterable<SelfFundParams> selfFundParams = selfFundParamsRepo.findAll(QSelfFundParams.selfFundParams.policyTrans.policyId.eq(parTrans.getPolicyTrans().getPolicyId()));
            if(selfFundParams.spliterator().getExactSizeIfKnown()==0)
                throw new BadRequestException("No Fund Parameters for Policy...Check the policy to continue..");

            if(selfFundParams.spliterator().getExactSizeIfKnown() > 1)
                throw new BadRequestException("Error on Fund Parameters for Policy...Consult the System Administrator.");

            SelfFundParams fundParams = null;

            for(SelfFundParams selfFundParam:selfFundParams)
            {
                fundParams = selfFundParam;
                break;
            }
            //TO Minimal Fund for Self Fund
        }

        if(categoryBenefits.getCover().isDependsOnGender() && principalOrSpouse){
            String gender = categoryBenefits.getCover().getGender();
            if(parTrans.getClientDef().getGender()!=null){
                if(!StringUtils.equals(gender,parTrans.getClientDef().getGender())){
                    rejectAmt = true;
                    rejectShtDesc = "BENGENDER";
                    rejectReason = "The Selected Benefit does not support the the gender of claimant...";
                    requestLogs.add(new RequestLogBean(rejectShtDesc,rejectReason));
                }
            }
            else throw new BadRequestException("Member Gender Cannot be null...");
        }
        if(manualCtrl) {
            if (parTrans.getApprovedQty().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Approved Quantity cannot be zero or less than zero");
            }
            if (parTrans.getApprovedQty().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Approved Quantity cannot be zero or less than zero");
            }
        }
        if(parTrans.getQty().compareTo(BigDecimal.ZERO)<=0){
            throw new BadRequestException("Requested Quantity cannot be zero or less than zero");
        }
        if(parTrans.getPrice().compareTo(BigDecimal.ZERO)<=0){
            throw new BadRequestException("Requested Price cannot be zero or less than zero");
        }

        BigDecimal diff = BigDecimal.ZERO;
        BigDecimal exgratiaAmt = (exgratia)?parTrans.getExgratiaAmount():BigDecimal.ZERO;
        if(manualCtrl)
            diff = (parTrans.getQty().multiply(parTrans.getPrice())).subtract(parTrans.getApprovedQty().multiply(parTrans.getApprovedPrice()).add(exgratiaAmt));
        Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("CL");
        if (sequenceRepo.count(seqPredicate) == 0)
            throw new BadRequestException("Sequence for Claims Transactions has not been defined");
        SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
        Long seqNumber = sequence.getNextNumber();
        sequence.setLastNumber(seqNumber);
        sequence.setNextNumber(seqNumber + 1);
        sequenceRepo.save(sequence);
        final String claimNumber = sequence.getSeqPrefix() + String.format("%05d", seqNumber);
        parTrans.setTransDate(new Date());
        parTrans.setTransUser(userUtils.getCurrentUser());
        parTrans.setParNo(claimNumber);
        parTrans.setParRevisionNo(claimNumber+"/1");
        parTrans.setParStatus("Open");
        parTrans.setTotalCalcAmount(parTrans.getApprovedQty().multiply(parTrans.getApprovedPrice()));
        parTrans.setTotalRequestAmount(parTrans.getQty().multiply(parTrans.getPrice()));
        parTrans.setTotalInvoiceAmount(parTrans.getInvoiceAmount());
        if(exgratia){
            parTrans.setTotalexgratiaAmount(parTrans.getExgratiaAmount());
        }
        if(rejectAmt)
            parTrans.setTotalRejectedAmount((parTrans.getQty().multiply(parTrans.getPrice())));
        else
            parTrans.setTotalRejectedAmount(diff);
        parTrans.setTransType("C");
        parTrans.setTotalApprAmount(parTrans.getQty().multiply(parTrans.getPrice()).subtract(parTrans.getTotalRejectedAmount()));
        parTrans.setCurrentStatus("D");
        MedicalParTrans savedTrans = medicalParRepo.save(parTrans);
        List<MedParReqDocs> parReqDocs = new ArrayList<>();
        for(SubClassReqdDocs reqdDoc:reqdDocs){
            MedParReqDocs parReqDoc = new MedParReqDocs();
            parReqDoc.setReqdDocs(reqdDoc);
            parReqDoc.setParTrans(savedTrans);
            parReqDocs.add(parReqDoc);
        }
        parDocsRepo.save(parReqDocs);
        MedicalParRequest parRequest = new MedicalParRequest();
        parRequest.setAilments(ailmentsRepo.findOne(parTrans.getDiagnosisId()));
        parRequest.setDescription(parTrans.getRequestDescription());
        parRequest.setParTrans(savedTrans);
        if(exgratia){
            parRequest.setExgratiaAmount(parTrans.getExgratiaAmount());
        }
        if(rejectAmt)
            parRequest.setRejectAmount((parTrans.getQty().multiply(parTrans.getPrice())));
        else
            parRequest.setRejectAmount(diff);
        parRequest.setInvoiceAmount(parTrans.getInvoiceAmount());
        parRequest.setServicePlace(parTrans.getServicePlace());
        parRequest.setDescription(parTrans.getInvoiceDescription());
        parRequest.setInvoiceDate(parTrans.getInvoiceDate());
        parRequest.setInvoiceNo(parTrans.getInvoiceNumber());
        parRequest.setRequestDate(parTrans.getRequestDate());
        parRequest.setApprovedAmount(parTrans.getQty().multiply(parTrans.getPrice()).subtract(parRequest.getRejectAmount()));
        parRequest.setRequestType(parTrans.getRequestType());
        parRequest.setRequestAmount(parTrans.getQty().multiply(parTrans.getPrice()));
        parRequest.setCalcAmount(parTrans.getApprovedQty().multiply(parTrans.getApprovedPrice()));
        MedicalParRequest savedRequest = requestRepo.save(parRequest);
        MedicalRequestServices requestServices = new MedicalRequestServices();
        requestServices.setMedActivities(activitiesRepo.findOne(parTrans.getActivityId()));
        if(rejectAmt) {
            requestServices.setRejAmount((parTrans.getQty().multiply(parTrans.getPrice())));
        }
        else
            requestServices.setRejAmount(diff);

        if(exgratia){
            requestServices.setExgratiaAmount(parTrans.getExgratiaAmount());
            requestServices.setExgratiaService("Y");
        }
        else{
            requestServices.setExgratiaService("N");
        }
        if(manualCtrl) {
            requestServices.setAppPrice(parTrans.getApprovedPrice());
            requestServices.setAppQuantity(parTrans.getApprovedQty());
            requestServices.setManualControl("Y");
        }
        else{
            requestServices.setAppPrice(BigDecimal.ZERO);
            requestServices.setAppQuantity(BigDecimal.ZERO);
            requestServices.setManualControl("N");
        }
        requestServices.setReqAmount(parTrans.getQty().multiply(parTrans.getPrice()));
        requestServices.setReqPrice(parTrans.getPrice());
        requestServices.setReqQuantity(parTrans.getQty());
        requestServices.setCategoryBenefits(categoryBenefits);
        requestServices.setCalcAmount(parTrans.getApprovedQty().multiply(parTrans.getApprovedPrice()));
        requestServices.setAppAmount(parTrans.getQty().multiply(parTrans.getPrice()).subtract(requestServices.getRejAmount()));
        requestServices.setAppPrice(parTrans.getApprovedPrice());
        requestServices.setAppQuantity(parTrans.getApprovedQty());
        requestServices.setRequest(savedRequest);
        requestServices.setServiceDate(parTrans.getServiceDate());
        MedicalRequestServices savedService = requestServicesRepo.save(requestServices);
        createRequestServiceLog(savedService,requestLogs);
        return savedTrans.getParId();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateLimit(Long benefitId) {
        boolean fund = false;
        BigDecimal amount = BigDecimal.ZERO;
        MedCategoryBenefits benefits = benefitRepo.findOne(benefitId);
        Iterable<MedicalRequestServices> authorisedRequests = requestServicesRepo.findAll(QMedicalRequestServices.medicalRequestServices.categoryBenefits.sectId.eq(benefitId)
                                                          .and(QMedicalRequestServices.medicalRequestServices.request.parTrans.transType.eq("C"))
                                                          .and(QMedicalRequestServices.medicalRequestServices.request.parTrans.parStatus.eq("Authorised")));
        for(MedicalRequestServices request:authorisedRequests){
            amount = amount.add(request.getAppAmount());
           }

        fund = (benefits.getCategory().getPolicy().getBinder().getFundBinder()!=null && "Y".equalsIgnoreCase(benefits.getCategory().getPolicy().getBinder().getFundBinder()));
        if(fund){
                return benefits.getFundLimit().subtract(amount);
            }
            else{
                if(benefits.getLimit()!=null)
                    return benefits.getLimit().getLimitAmount().subtract(amount);
            }
         return BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void authMedClaims(Long parId) throws BadRequestException {
        if(parId==null) throw new BadRequestException("Select Transaction to continue..");
        MedicalParTrans parTrans = getMedicalClaimDetails(parId);
        Iterable<MedicalParRequest> rqsts = requestRepo.findAll(QMedicalParRequest.medicalParRequest.parTrans.parId.eq(parId));
        for(MedicalParRequest request:rqsts){
            if(request.getInvoiceNo()==null)
                throw new BadRequestException("Invoice Number cannot be null...Ensure that all Requests have Invoice Number");
            if(request.getInvoiceDate()==null)
                throw new BadRequestException("Invoice Date is Mandatory..");
            if(request.getInvoiceAmount()==null || request.getInvoiceAmount().compareTo(BigDecimal.ZERO)<=0)
                throw new BadRequestException("Invoice Amount cannot be zero or null...");

        }
        Iterable<MedicalRequestServices> requests = requestServicesRepo.findAll(QMedicalRequestServices.medicalRequestServices.request.parTrans.parId.eq(parId));
        if(parTrans.getTotalInvoiceAmount().compareTo(parTrans.getTotalRequestAmount())!=0)
            throw new BadRequestException("Invoice Amount must be equal to Request Amount.....");
        for(MedicalRequestServices request:requests){
            if(request.getAppAmount().compareTo(BigDecimal.ZERO)==0)
                throw new BadRequestException("Approved Amount cannot be zero");
            if(calculateLimit(request.getCategoryBenefits().getSectId()).compareTo(request.getAppAmount())==-1)
                throw new BadRequestException("Limit Amount is less than invoice amount requested");
        }
        boolean selffundPolicy = (parTrans.getCategory().getPolicy().getBinder().getFundBinder()!=null && "Y".equalsIgnoreCase(parTrans.getCategory().getPolicy().getBinder().getFundBinder()));
        Iterable<SelfFundParams> selfFundParams = selfFundParamsRepo.findAll(QSelfFundParams.selfFundParams.policyTrans.policyId.eq(parTrans.getCategory().getPolicy().getPolicyId()));
        if(selffundPolicy){
            if(selfFundParams.spliterator().getExactSizeIfKnown()==0){
                throw new BadRequestException("No Fund Parameters Allocated to this policy...");
            }
        }
        parTrans.setParStatus("Authorised");
        parTrans.setCurrentStatus("A");
        parTrans.setBatched("N");
        parTrans.setAuthDate(new Date());
        parTrans.setAuthUser(userUtils.getCurrentUser());
        medicalParRepo.save(parTrans);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void makeReadyPreauth(Long parId) throws BadRequestException {
        if(parId==null) throw new BadRequestException("Select Transaction to continue..");
        MedicalParTrans parTrans = getMedicalClaimDetails(parId);
        if("Ready".equalsIgnoreCase(parTrans.getParStatus())){
            throw new BadRequestException("Transaction already made Ready....Cannot continue");
        }
        if(parTrans.getTotalCalcAmount().compareTo(parTrans.getTotalRequestAmount())==1)
            throw new BadRequestException("Total Approved Amount Cannot be greater than Total Requested Amount");
        Iterable<MedicalRequestServices> requests = requestServicesRepo.findAll(QMedicalRequestServices.medicalRequestServices.request.parTrans.parId.eq(parId));
        Iterable<MedParReqDocs> parReqDocs = parDocsRepo.findAll(QMedParReqDocs.medParReqDocs.parTrans.parId.eq(parId));
        for(MedParReqDocs a:parReqDocs){
            if(a.getCheckSum()==null || StringUtils.isBlank(a.getCheckSum()))
                throw new BadRequestException("Cannot authorize transaction without uploading documents for the claim");
        }
        for(MedicalRequestServices request:requests){
            if(request.getReqAmount().compareTo(BigDecimal.ZERO)==0)
                throw new BadRequestException("Amount cannot be zero");
            if(calculateLimit(request.getCategoryBenefits().getSectId()).compareTo(request.getAppAmount())==-1)
                throw new BadRequestException("Limit Amount is less than invoice amount requested");
        }
        parTrans.setParStatus("Ready");
        medicalParRepo.save(parTrans);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void makeReadyMedClaims(Long parId) throws BadRequestException {
        if(parId==null) throw new BadRequestException("Select Transaction to continue..");
        MedicalParTrans parTrans = getMedicalClaimDetails(parId);
        if("Ready".equalsIgnoreCase(parTrans.getParStatus())){
            throw new BadRequestException("Transaction already made Ready....Cannot continue");
        }
        if(parTrans.getEventDate().after(new Date()))
            throw new BadRequestException("Event Date Cannot be after the Current Date");
        Iterable<MedParReqDocs> parReqDocs = parDocsRepo.findAll(QMedParReqDocs.medParReqDocs.parTrans.parId.eq(parId));
        for(MedParReqDocs a:parReqDocs){
            if(a.getCheckSum()==null || StringUtils.isBlank(a.getCheckSum()))
                throw new BadRequestException("Cannot authorize transaction without uploading documents for the claim");
        }
        Iterable<MedicalParRequest> rqsts = requestRepo.findAll(QMedicalParRequest.medicalParRequest.parTrans.parId.eq(parId));
        for(MedicalParRequest request:rqsts){
            if(request.getInvoiceNo()==null)
                throw new BadRequestException("Invoice Number cannot be null...Ensure that all Requests have Invoice Number");
            if(request.getInvoiceDate()==null)
                throw new BadRequestException("Invoice Date is Mandatory..");
            if(request.getInvoiceAmount()==null || request.getInvoiceAmount().compareTo(BigDecimal.ZERO)<=0)
                throw new BadRequestException("Invoice Amount cannot be zero or null...");

        }
        if(parTrans.getTotalInvoiceAmount().compareTo(parTrans.getTotalRequestAmount())!=0)
            throw new BadRequestException("Invoice Amount must be equal to Request Amount.....");
        if(parTrans.getTotalCalcAmount().compareTo(parTrans.getTotalRequestAmount())==1)
            throw new BadRequestException("Total Approved Amount Cannot be greater than Total Requested Amount");
        Iterable<MedicalRequestServices> requests = requestServicesRepo.findAll(QMedicalRequestServices.medicalRequestServices.request.parTrans.parId.eq(parId));
        for(MedicalRequestServices request:requests){
            if(request.getAppAmount().compareTo(BigDecimal.ZERO)==0)
                throw new BadRequestException("Approved Amount cannot be zero");
            if(calculateLimit(request.getCategoryBenefits().getSectId()).compareTo(request.getAppAmount())==-1)
                throw new BadRequestException("Limit Amount is less than invoice amount requested");
        }
        boolean selffundPolicy = (parTrans.getCategory().getPolicy().getBinder().getFundBinder()!=null && "Y".equalsIgnoreCase(parTrans.getCategory().getPolicy().getBinder().getFundBinder()));
        Iterable<SelfFundParams> selfFundParams = selfFundParamsRepo.findAll(QSelfFundParams.selfFundParams.policyTrans.policyId.eq(parTrans.getCategory().getPolicy().getPolicyId()));
        if(selffundPolicy){
            if(selfFundParams.spliterator().getExactSizeIfKnown()==0){
                throw new BadRequestException("No Fund Parameters Allocated to this policy...");
            }
        }
        parTrans.setParStatus("Ready");
        medicalParRepo.save(parTrans);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<MedicalParTrans> smartClaims(DataTablesRequest request) throws IllegalAccessException {
        BooleanExpression pred = QMedicalParTrans.medicalParTrans.transType.eq("S");
        Page<MedicalParTrans> page = medicalParRepo.findAll(pred.and(request.searchPredicate(QMedicalParTrans.medicalParTrans)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public Long convertPreauthTrans(Long parId) throws BadRequestException {
        if(parId==null) throw new BadRequestException("Select Transaction to continue..");
        MedicalParTrans parTrans = getMedicalClaimDetails(parId);
        if((parTrans.getParStatus()!=null) && (!"Authorised".equalsIgnoreCase(parTrans.getParStatus()))){
            throw new BadRequestException("Cannot convert unauthorised Transaction.....");
        }
        if(parTrans.getConverted()!=null && "Y".equalsIgnoreCase(parTrans.getConverted())){
            throw new BadRequestException("The Transaction has already been converted...");
        }
        Iterable<MedicalParRequest> parRequests = requestRepo.findAll(QMedicalParRequest.medicalParRequest.parTrans.parId.eq(parId));
        MedicalParTrans claimTrans = new MedicalParTrans();
        Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("CL");
        if (sequenceRepo.count(seqPredicate) == 0)
            throw new BadRequestException("Sequence for Claims Transactions has not been defined");
        SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
        Long seqNumber = sequence.getNextNumber();
        sequence.setLastNumber(seqNumber);
        sequence.setNextNumber(seqNumber + 1);
        sequenceRepo.save(sequence);
        final String claimNumber = sequence.getSeqPrefix() + String.format("%05d", seqNumber);
        claimTrans.setTransDate((parTrans.getConvertDate()!=null)?parTrans.getConvertDate():new Date());
        claimTrans.setTransUser(parTrans.getTransUser());
        claimTrans.setCategoryMember(parTrans.getCategoryMember());
        claimTrans.setParNo(claimNumber);
        claimTrans.setParRevisionNo(claimNumber+"/1");
        claimTrans.setParStatus("Open");
        claimTrans.setTransType("C");
        claimTrans.setApprovalType(parTrans.getApprovalType());
        claimTrans.setClientDef(parTrans.getClientDef());
        claimTrans.setCountry(parTrans.getCountry());
        claimTrans.setEventDate(parTrans.getEventDate());
        claimTrans.setEvents(parTrans.getEvents());
        claimTrans.setEventTime(parTrans.getEventTime());
        claimTrans.setMedicalNetworks(parTrans.getMedicalNetworks());
        claimTrans.setNotDate(parTrans.getNotDate());
        claimTrans.setProviderContracts(parTrans.getProviderContracts());
        claimTrans.setPolicyTrans(parTrans.getPolicyTrans());
        claimTrans.setCategory(parTrans.getCategory());
        claimTrans.setTotalRequestAmount(parTrans.getTotalRequestAmount());
        claimTrans.setTotalCalcAmount(parTrans.getTotalCalcAmount());
        claimTrans.setTotalexgratiaAmount(parTrans.getTotalexgratiaAmount());
        claimTrans.setTotalRejectedAmount(parTrans.getTotalRejectedAmount());
        claimTrans.setTotalApprAmount(parTrans.getTotalApprAmount());
        claimTrans.setCurrentStatus("D");
        claimTrans.setConverted("Y");
        MedicalParTrans savedTrans = medicalParRepo.save(claimTrans);
        parTrans.setConvertReference(savedTrans);
        parTrans.setConverted("Y");
        medicalParRepo.save(parTrans);
        for(MedicalParRequest parRequest:parRequests){
            Iterable<MedicalRequestServices> services = requestServicesRepo.findAll(QMedicalRequestServices.medicalRequestServices.request.reqId.eq(parRequest.getReqId()));
            MedicalParRequest request = new MedicalParRequest();
            request.setAilments(parRequest.getAilments());
            request.setDescription(parRequest.getDescription());
            request.setServicePlace(parRequest.getServicePlace());
            request.setParTrans(savedTrans);
            request.setRequestDate(parRequest.getRequestDate());
            request.setRequestType(parRequest.getRequestType());
            request.setRequestAmount(parRequest.getRequestAmount());
            request.setCalcAmount(parRequest.getCalcAmount());
            request.setExgratiaAmount(parRequest.getExgratiaAmount());
            request.setRejectAmount(parRequest.getRejectAmount());
            MedicalParRequest savedRequest = requestRepo.save(request);
            for(MedicalRequestServices requestService:services){
                MedicalRequestServices requestServices = new MedicalRequestServices();
                requestServices.setMedActivities(requestService.getMedActivities());
                requestServices.setReqAmount(requestService.getReqAmount());
                requestServices.setReqPrice(requestService.getReqPrice());
                requestServices.setReqQuantity(requestService.getReqQuantity());
                requestServices.setRequest(savedRequest);
                requestServices.setServiceDate(requestService.getServiceDate());
                requestServices.setCategoryBenefits(requestService.getCategoryBenefits());
                requestServices.setAppAmount(requestService.getAppAmount());
                requestServices.setAppPrice(requestService.getAppPrice());
                requestServices.setAppQuantity(requestService.getAppQuantity());
                requestServices.setExgratiaAmount(requestService.getExgratiaAmount());
                requestServices.setExgratiaService(requestService.getExgratiaService());
                requestServices.setManualControl(requestService.getManualControl());
                requestServices.setRejAmount(requestService.getRejAmount());
                requestServicesRepo.save(requestServices);
            }
        }
        return savedTrans.getParId();
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void createRequests(MedicalParRequest parRequest) throws BadRequestException {
        if(parRequest.getReqId()==null){
            requestRepo.save(parRequest);
        }
        MedicalParTrans parTrans = parRequest.getParTrans();
        if(parRequest.getRequestDate().before(parTrans.getEventDate()))
            throw new BadRequestException("Request Date cannot be before Event Date...");
        if(parRequest.getRequestDate().after(parTrans.getPolicyTrans().getWetDate()))
            throw new BadRequestException("Request Date cannot be outside the policy date...");
        Iterable<MedicalRequestServices> services = requestServicesRepo.findAll(QMedicalRequestServices.medicalRequestServices.request.reqId.eq(parRequest.getReqId()));
        BigDecimal totalInvoiceAmt = BigDecimal.ZERO;
        BigDecimal totalCalcAmt = BigDecimal.ZERO;
        BigDecimal totalRejAmt = BigDecimal.ZERO;
        for(MedicalRequestServices service:services){
            totalInvoiceAmt = totalInvoiceAmt.add(service.getReqAmount());
            totalCalcAmt = totalCalcAmt.add(service.getAppAmount());
            totalRejAmt = totalRejAmt.add(service.getRejAmount());
        }
        parRequest.setRequestAmount(totalInvoiceAmt);
        parRequest.setCalcAmount(totalCalcAmt);
        parRequest.setRejectAmount(totalRejAmt);
        requestRepo.save(parRequest);
    }


    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void createClmRequests(MedicalParRequest parRequest) throws BadRequestException {

        String converted = parRequest.getParTrans().getConverted();
        boolean convertedFromPreauth = (converted!=null && "Y".equalsIgnoreCase(converted));
        if(convertedFromPreauth){
            if(parRequest.getReqId()==null){
                throw new BadRequestException("Cannot create a new Request for a converted Preauth....");
            }
            MedicalParRequest medicalParRequest = requestRepo.findOne(parRequest.getReqId());
            parRequest.setCalcAmount(medicalParRequest.getCalcAmount());
            parRequest.setAilments(medicalParRequest.getAilments());
            parRequest.setRequestAmount(medicalParRequest.getRequestAmount());
            parRequest.setDescription(medicalParRequest.getDescription());
            parRequest.setServicePlace(medicalParRequest.getServicePlace());
            parRequest.setRequestDate(medicalParRequest.getRequestDate());
            parRequest.setRequestType(medicalParRequest.getRequestType());
            requestRepo.save(parRequest);
            BigDecimal actualInvoice = BigDecimal.ZERO;
            Iterable<MedicalParRequest> requests = requestRepo.findAll(QMedicalParRequest.medicalParRequest.parTrans.parId.eq(parRequest.getParTrans().getParId()));
            for(MedicalParRequest request:requests){
                if(request.getInvoiceAmount()!=null)
                    actualInvoice = actualInvoice.add(request.getInvoiceAmount());
            }
            MedicalParTrans parTrans = parRequest.getParTrans();
            parTrans.setTotalInvoiceAmount(actualInvoice);
            medicalParRepo.save(parTrans);
            return;
        }
        if(parRequest.getRequestDate().before(parRequest.getParTrans().getEventDate()))
            throw new BadRequestException("Request Date cannot be before Event Date...");
        if(parRequest.getRequestDate().after(parRequest.getParTrans().getPolicyTrans().getWetDate()))
            throw new BadRequestException("Request Date cannot be outside the policy date...");

        MedicalParRequest savedRequest =  requestRepo.save(parRequest);
        Iterable<MedicalRequestServices> services = requestServicesRepo.findAll(QMedicalRequestServices.medicalRequestServices.request.reqId.eq(savedRequest.getReqId()));
        BigDecimal totalRequest = BigDecimal.ZERO;
        BigDecimal totalInvoiceAmt = BigDecimal.ZERO;
        BigDecimal actualInvoice = BigDecimal.ZERO;
        BigDecimal totalRejAmt = BigDecimal.ZERO;
        Iterable<MedicalParRequest> requests = requestRepo.findAll(QMedicalParRequest.medicalParRequest.parTrans.parId.eq(parRequest.getParTrans().getParId()));
        for(MedicalParRequest request:requests){
            if(request.getInvoiceAmount()!=null)
                actualInvoice = actualInvoice.add(request.getInvoiceAmount());
        }
        for(MedicalRequestServices service:services){
            totalRequest = totalRequest.add(service.getReqAmount());
            totalInvoiceAmt = totalInvoiceAmt.add(service.getAppAmount());
            totalRejAmt = totalRejAmt.add((service.getRejAmount()!=null)?service.getRejAmount():BigDecimal.ZERO);
        }
        parRequest.setRequestAmount(totalRequest);
        parRequest.setCalcAmount(totalInvoiceAmt);
        parRequest.setRejectAmount(totalRejAmt);
        requestRepo.save(parRequest);
        MedicalParTrans parTrans = parRequest.getParTrans();
        parTrans.setTotalInvoiceAmount(actualInvoice);
        medicalParRepo.save(parTrans);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void createServices(MedicalRequestServices requestServices) throws BadRequestException {
        boolean manualCtrl = (requestServices.getManualControl()!=null && "on".equalsIgnoreCase(requestServices.getManualControl()));
        boolean exgratia = (requestServices.getExgratiaService()!=null && "on".equalsIgnoreCase(requestServices.getExgratiaService()));
        if(!manualCtrl)
            exgratia = false;
        if(requestServices.getReqQuantity()==null) throw new BadRequestException("Service Quantity is Mandatory");
        if(requestServices.getReqPrice()==null) throw new BadRequestException("Service Price is Mandatory");
        if(manualCtrl) {
            if (requestServices.getAppQuantity() == null)
                throw new BadRequestException("Approved Quantity is Mandatory");
            if (requestServices.getAppPrice() == null)
                throw new BadRequestException("Approved Price is Mandatory");
        }
        if(exgratia)
        {
            if(requestServices.getExgratiaAmount()==null)
                throw new BadRequestException("Exgratia Amount is Mandatory....");
        }
        if(requestServices.getMedActivities()==null) throw new BadRequestException("Activity is Mandatory");
        if(requestServices.getServiceDate()==null) throw new BadRequestException("Service Date is mandatory");
        if(requestServices.getServiceDate().before(requestServices.getRequest().getRequestDate()))
            throw new BadRequestException("Service Date Cannot be Before Request Date...");
        if(requestServices.getServiceDate().after(requestServices.getRequest().getParTrans().getPolicyTrans().getWetDate()))
            throw new BadRequestException("Service Date Cannot be outside policy...");

        if(requestServices.getReqServiceId()==null) {
            long count = requestServicesRepo.count(QMedicalRequestServices.medicalRequestServices.medActivities.benId.eq(requestServices.getMedActivities().getBenId())
                    .and(QMedicalRequestServices.medicalRequestServices.request.reqId.eq(requestServices.getRequest().getReqId())));
            if (count > 0)
                throw new BadRequestException("Activity Selected has already been created for the Request...Select Another Activity");
        }
        if(manualCtrl) {
            if ((requestServices.getAppQuantity().multiply(requestServices.getAppPrice())).compareTo(requestServices.getReqQuantity().multiply(requestServices.getReqPrice())) == 1)
                throw new BadRequestException("Approved Amount Cannot be greater than Requested Amount");
        }

        MedActivities activities = requestServices.getMedActivities();
        if(activities.getSection()==null)
            throw new BadRequestException("Activity Selected does not have a benefit attached...");
        SectionsDef sectionsDef = activities.getSection();
        Iterable<MedCategoryBenefits> catBenefits = benefitRepo.findAll(QMedCategoryBenefits.medCategoryBenefits.cover.section.id.eq(sectionsDef.getId())
                .and(QMedCategoryBenefits.medCategoryBenefits.category.eq(requestServices.getRequest().getParTrans().getCategory())));
        boolean rejectAmt = false;
        if(catBenefits.spliterator().getExactSizeIfKnown() > 1)
            throw new BadRequestException("More than Benefit attached to the activity...");
        if(catBenefits.spliterator().getExactSizeIfKnown() ==0)
            rejectAmt = true;
        MedCategoryBenefits categoryBenefits = null;
        for(MedCategoryBenefits benefits:catBenefits){
            categoryBenefits = benefits;
            break;
        }
        if(categoryBenefits.getWaitPeriod()!=null){
            int waitPeriod = categoryBenefits.getWaitPeriod();
            int timeElapsed = dateUtilities.getNumberOfMonths(requestServices.getRequest().getParTrans().getPolicyTrans().getAuthDate() , new Date());
            if(waitPeriod > timeElapsed)
                rejectAmt = true;
        }

        boolean principalOrSpouse ="P".equalsIgnoreCase(requestServices.getRequest().getParTrans().getCategoryMember().getDependentTypes()) ||"S".equalsIgnoreCase(requestServices.getRequest().getParTrans().getCategoryMember().getDependentTypes());

        BigDecimal remLimit = calculateLimit(categoryBenefits.getSectId()).subtract(requestServices.getReqQuantity().multiply(requestServices.getReqPrice()));

        if(remLimit.compareTo(BigDecimal.ZERO)<=0)
            rejectAmt=true;

        boolean fund = (requestServices.getRequest().getParTrans().getPolicyTrans().getBinder().getFundBinder()!=null && "Y".equalsIgnoreCase(requestServices.getRequest().getParTrans().getPolicyTrans().getBinder().getFundBinder()));

        if(fund){
            Iterable<SelfFundParams> selfFundParams = selfFundParamsRepo.findAll(QSelfFundParams.selfFundParams.policyTrans.policyId.eq(requestServices.getRequest().getParTrans().getPolicyTrans().getPolicyId()));
            if(selfFundParams.spliterator().getExactSizeIfKnown()==0)
                throw new BadRequestException("No Fund Parameters for Policy...Check the policy to continue..");

            if(selfFundParams.spliterator().getExactSizeIfKnown() > 1)
                throw new BadRequestException("Error on Fund Parameters for Policy...Consult the System Administrator.");

            SelfFundParams fundParams = null;

            for(SelfFundParams selfFundParam:selfFundParams)
            {
                fundParams = selfFundParam;
                break;
            }
            //TO Minimal Fund for Self Fund
        }

        if(categoryBenefits.getCover().isDependsOnGender() && principalOrSpouse){
            String gender = categoryBenefits.getCover().getGender();
            if(requestServices.getRequest().getParTrans().getClientDef().getGender()!=null){
                if(!StringUtils.equals(gender,requestServices.getRequest().getParTrans().getClientDef().getGender())){
                    rejectAmt = true;
                }
            }
            else throw new BadRequestException("Member Gender Cannot be null...");
        }

        BigDecimal diff = BigDecimal.ZERO;
        BigDecimal exgratiaAmt = (exgratia)?requestServices.getExgratiaAmount():BigDecimal.ZERO;
        if(manualCtrl){
             diff = (requestServices.getReqQuantity().multiply(requestServices.getReqPrice())).subtract(requestServices.getAppQuantity().multiply(requestServices.getAppPrice()).add(exgratiaAmt));
             if(diff.compareTo(BigDecimal.ZERO)==-1)
                 diff = BigDecimal.ZERO;
        }

        if(rejectAmt)
            requestServices.setRejAmount(requestServices.getReqQuantity().multiply(requestServices.getReqPrice()));
        else
            requestServices.setRejAmount(diff);
        requestServices.setCalcAmount((manualCtrl)?(requestServices.getAppQuantity().multiply(requestServices.getAppPrice())):requestServices.getReqQuantity().multiply(requestServices.getReqPrice()));
        requestServices.setCategoryBenefits(categoryBenefits);
        if(exgratia){
            requestServices.setExgratiaService("Y");
        }
        else{
            requestServices.setExgratiaAmount(BigDecimal.ZERO);
            requestServices.setExgratiaService("N");
        }
        if(manualCtrl)
            requestServices.setManualControl("Y");
        else requestServices.setManualControl("N");
        if(!manualCtrl){
            requestServices.setAppPrice(requestServices.getReqPrice());
            requestServices.setAppQuantity(requestServices.getReqQuantity());
        }
        if(rejectAmt){
            requestServices.setAppPrice(BigDecimal.ZERO);
            requestServices.setAppQuantity(BigDecimal.ZERO);
        }
        requestServices.setReqAmount(requestServices.getReqQuantity().multiply(requestServices.getReqPrice()));
        requestServices.setAppAmount((requestServices.getReqQuantity().multiply(requestServices.getReqPrice())).subtract(requestServices.getRejAmount()));
        requestServicesRepo.save(requestServices);
        Iterable<MedicalRequestServices> services = requestServicesRepo.findAll(QMedicalRequestServices.medicalRequestServices.request.reqId.eq(requestServices.getRequest().getReqId()));
        BigDecimal totalInvoiceAmt = Streamable.streamOf(services).map(a -> a.getReqAmount()).reduce(BigDecimal::add).get();
        BigDecimal totalApprAmt = Streamable.streamOf(services).map(a -> (a.getAppAmount()!=null)?a.getAppAmount():BigDecimal.ZERO).reduce(BigDecimal::add).get();
        BigDecimal totalRejectAmt = Streamable.streamOf(services).map(a -> a.getRejAmount()).reduce(BigDecimal::add).get();
        BigDecimal totalExgratia = Streamable.streamOf(services).map(a -> (a.getExgratiaAmount()!=null)?a.getExgratiaAmount():BigDecimal.ZERO).reduce(BigDecimal::add).get();
        MedicalParRequest request = requestServices.getRequest();
        request.setRequestAmount(totalInvoiceAmt);
        request.setCalcAmount(totalApprAmt);
        request.setRejectAmount(totalRejectAmt);
        request.setExgratiaAmount(totalExgratia);
        request.setApprovedAmount(totalInvoiceAmt.subtract(totalRejectAmt));
        requestRepo.save(request);
        Iterable<MedicalRequestServices> totalServices = requestServicesRepo.findAll(QMedicalRequestServices.medicalRequestServices.request.parTrans.parId.eq(requestServices.getRequest().getParTrans().getParId()));
        totalInvoiceAmt = Streamable.streamOf(totalServices).map(a -> a.getReqAmount()).reduce(BigDecimal::add).get();
        totalApprAmt = Streamable.streamOf(totalServices).map(a -> (a.getAppAmount()!=null)?a.getAppAmount():BigDecimal.ZERO).reduce(BigDecimal::add).get();
        totalRejectAmt = Streamable.streamOf(totalServices).map(a -> a.getRejAmount()).reduce(BigDecimal::add).get();
        totalExgratia = Streamable.streamOf(services).map(a -> (a.getExgratiaAmount()!=null)?a.getExgratiaAmount():BigDecimal.ZERO).reduce(BigDecimal::add).get();
        MedicalParTrans parTrans = requestServices.getRequest().getParTrans();
        parTrans.setTotalRequestAmount(totalInvoiceAmt);
        parTrans.setTotalCalcAmount(totalApprAmt);
        parTrans.setTotalRejectedAmount(totalRejectAmt);
        parTrans.setTotalApprAmount(totalInvoiceAmt.subtract(totalRejectAmt));
        parTrans.setTotalexgratiaAmount(totalExgratia);
        medicalParRepo.save(parTrans);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void createClmServices(MedicalRequestServices requestServices) throws BadRequestException {
        boolean manualCtrl = (requestServices.getManualControl()!=null && "on".equalsIgnoreCase(requestServices.getManualControl()));
        boolean exgratia = (requestServices.getExgratiaService()!=null && "on".equalsIgnoreCase(requestServices.getExgratiaService()));
        if(!manualCtrl)
            exgratia = false;
        String converted = requestServices.getRequest().getParTrans().getConverted();
        boolean convertedFromPreauth = (converted!=null && "Y".equalsIgnoreCase(converted));
        if(convertedFromPreauth){
            return;
        }
        if(requestServices.getReqQuantity()==null) throw new BadRequestException("Request Quantity is Mandatory");
        if(requestServices.getReqPrice()==null) throw new BadRequestException("Request Price is Mandatory");
        if(manualCtrl) {
            if (requestServices.getAppQuantity() == null)
                throw new BadRequestException("Approved Quantity is Mandatory");
            if (requestServices.getAppPrice() == null)
                throw new BadRequestException("Approved Price is Mandatory");
        }
        if(exgratia)
        {
            if(requestServices.getExgratiaAmount()==null)
                throw new BadRequestException("Exgratia Amount is Mandatory....");
        }
        if(requestServices.getMedActivities()==null) throw new BadRequestException("Select Activity to continue...");
        if(requestServices.getServiceDate()==null) throw new BadRequestException("Service Date is mandatory");
        if(requestServices.getServiceDate().before(requestServices.getRequest().getRequestDate()))
            throw new BadRequestException("Service Date Cannot be Before Request Date...");
        if(requestServices.getServiceDate().after(requestServices.getRequest().getParTrans().getPolicyTrans().getWetDate()))
            throw new BadRequestException("Service Date Cannot be outside policy...");
        if(requestServices.getReqServiceId()==null) {
            long count = requestServicesRepo.count(QMedicalRequestServices.medicalRequestServices.medActivities.benId.eq(requestServices.getMedActivities().getBenId())
                    .and(QMedicalRequestServices.medicalRequestServices.request.reqId.eq(requestServices.getRequest().getReqId())));
            if (count > 0)
                throw new BadRequestException("Activity Selected has already been created for the Request...Select Another Activity");
        }
        if(manualCtrl) {
            if ((requestServices.getAppQuantity().multiply(requestServices.getAppPrice())).compareTo(requestServices.getReqQuantity().multiply(requestServices.getReqPrice())) == 1)
                throw new BadRequestException("Approved Amount Cannot be greater than Requested Amount");
        }
        SectionsDef sectionsDef = requestServices.getMedActivities().getSection();
        boolean rejectAmt = false;
        Iterable<MedCategoryBenefits> catBenefits = benefitRepo.findAll(QMedCategoryBenefits.medCategoryBenefits.cover.section.id.eq(sectionsDef.getId())
                .and(QMedCategoryBenefits.medCategoryBenefits.category.eq(requestServices.getRequest().getParTrans().getCategory())));
        if(catBenefits.spliterator().getExactSizeIfKnown() > 1)
            throw new BadRequestException("More than Benefit attached to the activity...");
        if(catBenefits.spliterator().getExactSizeIfKnown() ==0)
            rejectAmt = true;
        MedCategoryBenefits categoryBenefits = null;
        for(MedCategoryBenefits benefits:catBenefits){
            categoryBenefits = benefits;
            break;
        }

        if(categoryBenefits.getWaitPeriod()!=null){
            int waitPeriod = categoryBenefits.getWaitPeriod();
            int timeElapsed = dateUtilities.getNumberOfMonths(requestServices.getRequest().getParTrans().getPolicyTrans().getAuthDate() , new Date());
            if(waitPeriod > timeElapsed)
                rejectAmt = true;
        }
        boolean principalOrSpouse =false;
        if( requestServices.getRequest().getParTrans().getCategoryMember()!=null) {
             principalOrSpouse = "P".equalsIgnoreCase(requestServices.getRequest().getParTrans().getCategoryMember().getDependentTypes()) || "S".equalsIgnoreCase(requestServices.getRequest().getParTrans().getCategoryMember().getDependentTypes());
        }
        BigDecimal remLimit = calculateLimit(categoryBenefits.getSectId()).subtract(requestServices.getReqQuantity().multiply(requestServices.getReqPrice()));

        if(remLimit.compareTo(BigDecimal.ZERO)<=0)
            rejectAmt=true;

        boolean fund = (requestServices.getRequest().getParTrans().getPolicyTrans().getBinder().getFundBinder()!=null && "Y".equalsIgnoreCase(requestServices.getRequest().getParTrans().getPolicyTrans().getBinder().getFundBinder()));

        if(fund){
            Iterable<SelfFundParams> selfFundParams = selfFundParamsRepo.findAll(QSelfFundParams.selfFundParams.policyTrans.policyId.eq(requestServices.getRequest().getParTrans().getPolicyTrans().getPolicyId()));
            if(selfFundParams.spliterator().getExactSizeIfKnown()==0)
                throw new BadRequestException("No Fund Parameters for Policy...Check the policy to continue..");

            if(selfFundParams.spliterator().getExactSizeIfKnown() > 1)
                throw new BadRequestException("Error on Fund Parameters for Policy...Consult the System Administrator.");

            SelfFundParams fundParams = null;

            for(SelfFundParams selfFundParam:selfFundParams)
            {
                fundParams = selfFundParam;
                break;
            }
            //TO Minimal Fund for Self Fund
        }

        if(categoryBenefits.getCover().isDependsOnGender() && principalOrSpouse){
            String gender = categoryBenefits.getCover().getGender();
            if(requestServices.getRequest().getParTrans().getClientDef().getGender()!=null){
                if(!StringUtils.equals(gender,requestServices.getRequest().getParTrans().getClientDef().getGender())){
                    rejectAmt = true;
                }
            }
            else throw new BadRequestException("Member Gender Cannot be null...");
        }
        BigDecimal exgratiaAmt = (exgratia)?requestServices.getExgratiaAmount():BigDecimal.ZERO;
        BigDecimal diff = (requestServices.getReqQuantity().multiply(requestServices.getReqPrice())).subtract(requestServices.getAppQuantity().multiply(requestServices.getAppPrice()).add(exgratiaAmt));
        if(diff.compareTo(BigDecimal.ZERO)==-1)
            diff = BigDecimal.ZERO;
        if(rejectAmt)
            requestServices.setRejAmount(requestServices.getReqQuantity().multiply(requestServices.getReqPrice()));
        else
            requestServices.setRejAmount(diff);
        if(exgratia){
            requestServices.setExgratiaService("Y");
        }
        else{
            requestServices.setExgratiaAmount(BigDecimal.ZERO);
            requestServices.setExgratiaService("N");
        }
        if(manualCtrl)
            requestServices.setManualControl("Y");
        else requestServices.setManualControl("N");
        if(!manualCtrl){
            requestServices.setAppPrice(requestServices.getReqPrice());
            requestServices.setAppQuantity(requestServices.getReqQuantity());
        }
        if(rejectAmt){
            requestServices.setAppPrice(BigDecimal.ZERO);
            requestServices.setAppQuantity(BigDecimal.ZERO);
        }
        requestServices.setCalcAmount((requestServices.getAppQuantity().multiply(requestServices.getAppPrice())));
        requestServices.setReqAmount(requestServices.getReqQuantity().multiply(requestServices.getReqPrice()));
        requestServices.setAppAmount((requestServices.getReqQuantity().multiply(requestServices.getReqPrice())).subtract(requestServices.getRejAmount()));
        requestServices.setCategoryBenefits(categoryBenefits);
        requestServicesRepo.save(requestServices);
        Iterable<MedicalRequestServices> services = requestServicesRepo.findAll(QMedicalRequestServices.medicalRequestServices.request.reqId.eq(requestServices.getRequest().getReqId()));
        BigDecimal totalRequest = BigDecimal.ZERO;
        BigDecimal totalInvoiceAmt = BigDecimal.ZERO;
        BigDecimal totalReject = BigDecimal.ZERO;
        BigDecimal totalExgratia = BigDecimal.ZERO;
        if(services.spliterator().getExactSizeIfKnown() > 0) {
             totalRequest = Streamable.streamOf(services).map(a -> a.getReqAmount()).reduce(BigDecimal::add).get();
             totalInvoiceAmt = Streamable.streamOf(services).map(a -> (a.getAppAmount() != null) ? a.getAppAmount() : BigDecimal.ZERO).reduce(BigDecimal::add).get();
             totalReject = Streamable.streamOf(services).map(a -> (a.getRejAmount() != null) ? a.getRejAmount() : BigDecimal.ZERO).reduce(BigDecimal::add).get();
             totalExgratia = Streamable.streamOf(services).map(a -> (a.getExgratiaAmount() != null) ? a.getExgratiaAmount() : BigDecimal.ZERO).reduce(BigDecimal::add).get();
        }
        MedicalParRequest parRequest = requestServices.getRequest();
        parRequest.setRequestAmount(totalRequest);
        parRequest.setCalcAmount(totalInvoiceAmt);
        parRequest.setRejectAmount(totalReject);
        parRequest.setExgratiaAmount(totalExgratia);
        parRequest.setApprovedAmount(totalRequest.subtract(totalReject));
        requestRepo.save(parRequest);
        Iterable<MedicalRequestServices> totalServices = requestServicesRepo.findAll(QMedicalRequestServices.medicalRequestServices.request.parTrans.parId.eq(requestServices.getRequest().getParTrans().getParId()));
         if(totalServices.spliterator().getExactSizeIfKnown() > 0) {
             totalRequest = Streamable.streamOf(totalServices).map(a -> a.getReqAmount()).reduce(BigDecimal::add).get();
             totalInvoiceAmt = Streamable.streamOf(totalServices).map(a -> (a.getAppAmount() != null) ? a.getAppAmount() : BigDecimal.ZERO).reduce(BigDecimal::add).get();
             totalReject = Streamable.streamOf(totalServices).map(a -> (a.getRejAmount() != null) ? a.getRejAmount() : BigDecimal.ZERO).reduce(BigDecimal::add).get();
             totalExgratia = Streamable.streamOf(totalServices).map(a -> (a.getExgratiaAmount() != null) ? a.getExgratiaAmount() : BigDecimal.ZERO).reduce(BigDecimal::add).get();
         }
        MedicalParTrans parTrans = requestServices.getRequest().getParTrans();
        parTrans.setTotalRequestAmount(totalRequest);
        parTrans.setTotalCalcAmount(totalInvoiceAmt);
        parTrans.setTotalRejectedAmount(totalReject);
        parTrans.setTotalApprAmount(totalInvoiceAmt.subtract(totalReject));
        parTrans.setTotalexgratiaAmount(totalExgratia);
        medicalParRepo.save(parTrans);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void deleteRequests(Long requestId) throws BadRequestException {
        Long parId = requestRepo.findOne(requestId).getParTrans().getParId();
        MedicalParTrans parTrans = requestRepo.findOne(requestId).getParTrans();
        Iterable<MedicalRequestServices> services = requestServicesRepo.findAll(QMedicalRequestServices.medicalRequestServices.request.reqId.eq(requestId));
        requestServicesRepo.delete(services);
        requestRepo.delete(requestId);
        Iterable<MedicalRequestServices> totalServices = requestServicesRepo.findAll(QMedicalRequestServices.medicalRequestServices.request.parTrans.parId.eq(parId));
        BigDecimal totalRequest =Streamable.streamOf(totalServices).map(a -> a.getReqAmount()).reduce(BigDecimal::add).get();
        BigDecimal totalInvoiceAmt = Streamable.streamOf(totalServices).map(a -> (a.getAppAmount()!=null)?a.getAppAmount():BigDecimal.ZERO).reduce(BigDecimal::add).get();
        BigDecimal totalReject = Streamable.streamOf(totalServices).map(a -> (a.getRejAmount()!=null)?a.getRejAmount():BigDecimal.ZERO).reduce(BigDecimal::add).get();
        BigDecimal totalExgratia = Streamable.streamOf(totalServices).map(a -> (a.getExgratiaAmount()!=null)?a.getExgratiaAmount():BigDecimal.ZERO).reduce(BigDecimal::add).get();
        parTrans.setTotalexgratiaAmount(totalExgratia);
        parTrans.setTotalRequestAmount(totalRequest);
        parTrans.setTotalRejectedAmount(totalReject);
        parTrans.setTotalCalcAmount(totalInvoiceAmt);
        medicalParRepo.save(parTrans);


    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void deleteClmRequests(Long requestId) throws BadRequestException {
        Long parId = requestRepo.findOne(requestId).getParTrans().getParId();
        MedicalParTrans parTrans = requestRepo.findOne(requestId).getParTrans();
        if(parTrans.getConverted()!=null && "Y".equalsIgnoreCase(parTrans.getConverted())){
            throw new BadRequestException("Cannot delete A record from converted Transaction");
        }
        Iterable<MedicalRequestServices> services = requestServicesRepo.findAll(QMedicalRequestServices.medicalRequestServices.request.reqId.eq(requestId));
        requestServicesRepo.delete(services);
        requestRepo.delete(requestId);
        Iterable<MedicalRequestServices> totalServices = requestServicesRepo.findAll(QMedicalRequestServices.medicalRequestServices.request.parTrans.parId.eq(parId));
        Iterable<MedicalParRequest> requests = requestRepo.findAll(QMedicalParRequest.medicalParRequest.parTrans.parId.eq(parId));
        BigDecimal totalRequest =BigDecimal.ZERO;
        BigDecimal totalInvoiceAmt =BigDecimal.ZERO;
        BigDecimal totalReject = BigDecimal.ZERO;
        BigDecimal totalExgratia = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        if(totalServices.spliterator().getExactSizeIfKnown() > 0) {
             totalRequest = Streamable.streamOf(totalServices).map(a -> a.getReqAmount()).reduce(BigDecimal::add).get();
             totalInvoiceAmt = Streamable.streamOf(totalServices).map(a -> (a.getAppAmount() != null) ? a.getAppAmount() : BigDecimal.ZERO).reduce(BigDecimal::add).get();
             totalReject = Streamable.streamOf(totalServices).map(a -> (a.getRejAmount() != null) ? a.getRejAmount() : BigDecimal.ZERO).reduce(BigDecimal::add).get();
             totalExgratia = Streamable.streamOf(totalServices).map(a -> (a.getExgratiaAmount() != null) ? a.getExgratiaAmount() : BigDecimal.ZERO).reduce(BigDecimal::add).get();
        }
        if(requests.spliterator().getExactSizeIfKnown() > 0)
         totalAmount = Streamable.streamOf(requests).map(a -> (a.getInvoiceAmount()!=null)?a.getInvoiceAmount():BigDecimal.ZERO).reduce(BigDecimal::add).get();
        parTrans.setTotalexgratiaAmount(totalExgratia);
        parTrans.setTotalRequestAmount(totalRequest);
        parTrans.setTotalRejectedAmount(totalReject);
        parTrans.setTotalInvoiceAmount(totalAmount);
        parTrans.setTotalCalcAmount(totalInvoiceAmt);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void deleteServices(Long serviceId) throws BadRequestException {
        MedicalRequestServices requestServices = requestServicesRepo.findOne(serviceId);
        requestServicesRepo.delete(serviceId);
        Iterable<MedicalRequestServices> services = requestServicesRepo.findAll(QMedicalRequestServices.medicalRequestServices.request.reqId.eq(requestServices.getRequest().getReqId()));
        BigDecimal totalInvoiceAmt =BigDecimal.ZERO;
        BigDecimal totalReject = BigDecimal.ZERO;
        BigDecimal totalExgratia = BigDecimal.ZERO;
        BigDecimal totalAmt = BigDecimal.ZERO;

        if(services.spliterator().getExactSizeIfKnown() > 0){
            totalInvoiceAmt =Streamable.streamOf(services).map(a -> a.getReqAmount()).reduce(BigDecimal::add).get();
            totalReject = Streamable.streamOf(services).map(a -> a.getRejAmount()).reduce(BigDecimal::add).get();
            totalExgratia = Streamable.streamOf(services).map(a -> a.getExgratiaAmount()).reduce(BigDecimal::add).get();
            totalAmt = Streamable.streamOf(services).map(a -> (a.getAppAmount()!=null)?a.getAppAmount():BigDecimal.ZERO).reduce(BigDecimal::add).get();

       }
        MedicalParRequest request = requestServices.getRequest();
        request.setRequestAmount(totalInvoiceAmt);
        request.setRejectAmount(totalReject);
        request.setExgratiaAmount(totalExgratia);
        request.setCalcAmount(totalAmt);
        requestRepo.save(request);
        Iterable<MedicalRequestServices> totalServices = requestServicesRepo.findAll(QMedicalRequestServices.medicalRequestServices.request.parTrans.parId.eq(requestServices.getRequest().getParTrans().getParId()));
         if(totalServices.spliterator().getExactSizeIfKnown() > 0) {
             totalInvoiceAmt = Streamable.streamOf(totalServices).map(a -> a.getReqAmount()).reduce(BigDecimal::add).get();
             totalReject = Streamable.streamOf(totalServices).map(a -> a.getRejAmount()).reduce(BigDecimal::add).get();
             totalExgratia = Streamable.streamOf(totalServices).map(a -> a.getExgratiaAmount()).reduce(BigDecimal::add).get();
             totalAmt = Streamable.streamOf(totalServices).map(a -> (a.getAppAmount() != null) ? a.getAppAmount() : BigDecimal.ZERO).reduce(BigDecimal::add).get();
         }
        MedicalParTrans parTrans = requestServices.getRequest().getParTrans();
        parTrans.setTotalRequestAmount(totalInvoiceAmt);
        parTrans.setTotalRejectedAmount(totalReject);
        parTrans.setTotalexgratiaAmount(totalExgratia);
        parTrans.setTotalCalcAmount(totalAmt);
        medicalParRepo.save(parTrans);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void deleteClmServices(Long serviceId) throws BadRequestException {
        MedicalRequestServices requestServices = requestServicesRepo.findOne(serviceId);
        if(requestServices.getRequest().getParTrans().getConverted()!=null && "Y".equalsIgnoreCase(requestServices.getRequest().getParTrans().getConverted())){
            throw new BadRequestException("Cannot delete A record from converted Transaction");
        }
        requestServicesRepo.delete(serviceId);
        Iterable<MedicalRequestServices> services = requestServicesRepo.findAll(QMedicalRequestServices.medicalRequestServices.request.reqId.eq(requestServices.getRequest().getReqId()));
        Iterable<MedicalParRequest> requests = requestRepo.findAll(QMedicalParRequest.medicalParRequest.parTrans.parId.eq(requestServices.getRequest().getParTrans().getParId()));
        BigDecimal totalRequest =BigDecimal.ZERO;
        BigDecimal totalInvoiceAmt = BigDecimal.ZERO;
        BigDecimal totalReject = BigDecimal.ZERO;
        BigDecimal totalExgratia = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        if(services.spliterator().getExactSizeIfKnown() > 0) {
             totalRequest = Streamable.streamOf(services).map(a -> a.getReqAmount()).reduce(BigDecimal::add).get();
             totalInvoiceAmt = Streamable.streamOf(services).map(a -> (a.getAppAmount() != null) ? a.getAppAmount() : BigDecimal.ZERO).reduce(BigDecimal::add).get();
             totalReject = Streamable.streamOf(services).map(a -> (a.getRejAmount() != null) ? a.getRejAmount() : BigDecimal.ZERO).reduce(BigDecimal::add).get();
             totalExgratia = Streamable.streamOf(services).map(a -> (a.getExgratiaAmount() != null) ? a.getExgratiaAmount() : BigDecimal.ZERO).reduce(BigDecimal::add).get();
        }
        if(requests.spliterator().getExactSizeIfKnown() > 0)
        totalAmount = Streamable.streamOf(requests).map(a -> (a.getInvoiceAmount() != null) ? a.getInvoiceAmount() : BigDecimal.ZERO).reduce(BigDecimal::add).get();
        MedicalParRequest request = requestServices.getRequest();
        request.setRequestAmount(totalRequest);
        request.setCalcAmount(totalInvoiceAmt);
        request.setRejectAmount(totalReject);
        request.setExgratiaAmount(totalExgratia);
        request.setInvoiceAmount(totalAmount);
        requestRepo.save(request);
        Iterable<MedicalRequestServices> totalServices = requestServicesRepo.findAll(QMedicalRequestServices.medicalRequestServices.request.parTrans.parId.eq(requestServices.getRequest().getParTrans().getParId()));
        if(totalServices.spliterator().getExactSizeIfKnown() > 0) {
            totalRequest = Streamable.streamOf(totalServices).map(a -> a.getReqAmount()).reduce(BigDecimal::add).get();
            totalInvoiceAmt = Streamable.streamOf(totalServices).map(a -> (a.getAppAmount() != null) ? a.getAppAmount() : BigDecimal.ZERO).reduce(BigDecimal::add).get();
            totalReject = Streamable.streamOf(totalServices).map(a -> (a.getRejAmount() != null) ? a.getRejAmount() : BigDecimal.ZERO).reduce(BigDecimal::add).get();
            totalExgratia = Streamable.streamOf(totalServices).map(a -> (a.getExgratiaAmount() != null) ? a.getExgratiaAmount() : BigDecimal.ZERO).reduce(BigDecimal::add).get();
        }
        if(requests.spliterator().getExactSizeIfKnown() > 0)
        totalAmount = Streamable.streamOf(requests).map(a -> (a.getInvoiceAmount() != null) ? a.getInvoiceAmount() : BigDecimal.ZERO).reduce(BigDecimal::add).get();
        MedicalParTrans parTrans = requestServices.getRequest().getParTrans();
        parTrans.setTotalRequestAmount(totalRequest);
        parTrans.setTotalCalcAmount(totalInvoiceAmt);
        parTrans.setTotalRejectedAmount(totalReject);
        parTrans.setTotalexgratiaAmount(totalExgratia);
        parTrans.setTotalInvoiceAmount(totalAmount);
        medicalParRepo.save(parTrans);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<PolicyTrans> findMedicalPolicies(String paramString, Pageable paramPageable) {
        Predicate pred = null;
        if (paramString == null || StringUtils.isBlank(paramString)) {
            pred = QPolicyTrans.policyTrans.product.proGroup.prgType.eq("MD");
        } else {
            pred =QPolicyTrans.policyTrans.polNo.containsIgnoreCase(paramString)
                  .and(QPolicyTrans.policyTrans.product.proGroup.prgType.eq("MD"));
        }
        return policyTransRepo.findAll(pred, paramPageable);
    }

    private void createRequestServiceLog(MedicalRequestServices requestService,List<RequestLogBean> logs){
        List<RequestServiceLog> serviceLogs = logs.stream().map(a -> {
            RequestServiceLog serviceLog = new RequestServiceLog();
            serviceLog.setLogDesc(a.getRejectReason());
            serviceLog.setLogShtDesc(a.getRejectShtDesc());
            serviceLog.setRequestServices(requestService);
            return  serviceLog;
        }).collect(Collectors.toList());
        servicesLogRepo.save(serviceLogs);
    }

    private boolean containsSclDocs(Iterable<MedParReqDocs> parReqDocs, SubClassReqdDocs reqdDocs){
        for(MedParReqDocs parReqDoc:parReqDocs){
            if(parReqDoc.getReqdDocs().getSclReqrdId()==reqdDocs.getSclReqrdId())
                return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesResult<RequestServiceLog> getRequestServiceLog(DataTablesRequest request, Long serviceId) throws IllegalAccessException {
        BooleanExpression pred = QRequestServiceLog.requestServiceLog.requestServices.reqServiceId.eq(serviceId);
        Page<RequestServiceLog> page = servicesLogRepo.findAll(pred.and(request.searchPredicate(QRequestServiceLog.requestServiceLog)), request);
        return new DataTablesResult<>(request, page);
    }

    @Override
    public DataTablesResult<MedParReqDocs> getParDocs(DataTablesRequest request, Long parId) throws IllegalAccessException {
        BooleanExpression pred = QMedParReqDocs.medParReqDocs.parTrans.parId.eq(parId);
        Page<MedParReqDocs> page = parDocsRepo.findAll(pred.and(request.searchPredicate(QMedParReqDocs.medParReqDocs)), request);
        return new DataTablesResult<>(request, page);
    }
}
