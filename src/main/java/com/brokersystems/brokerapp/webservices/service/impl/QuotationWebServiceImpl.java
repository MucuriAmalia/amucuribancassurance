package com.brokersystems.brokerapp.webservices.service.impl;

import com.brokersystems.brokerapp.claims.model.ClaimBalanceBean;
import com.brokersystems.brokerapp.claims.model.ClaimBookings;
import com.brokersystems.brokerapp.claims.model.QClaimBookings;
import com.brokersystems.brokerapp.claims.repository.ClaimsBookingRepo;
import com.brokersystems.brokerapp.claims.service.ClaimService;
import com.brokersystems.brokerapp.quotes.model.QQuoteProTrans;
import com.brokersystems.brokerapp.quotes.model.QQuoteTrans;
import com.brokersystems.brokerapp.setup.model.QSubclassClauses;
import com.brokersystems.brokerapp.quotes.model.QuoteProTrans;
import com.brokersystems.brokerapp.quotes.model.QuoteTrans;
import com.brokersystems.brokerapp.quotes.repository.QuotProductsRepo;
import com.brokersystems.brokerapp.quotes.repository.QuotTransRepo;
import com.brokersystems.brokerapp.server.utils.RiskSections;
import com.brokersystems.brokerapp.server.utils.Streamable;
import com.brokersystems.brokerapp.setup.model.ClientDef;
import com.brokersystems.brokerapp.setup.model.QClientDef;
import com.brokersystems.brokerapp.setup.model.SubclassClauses;
import com.brokersystems.brokerapp.setup.repository.ClientRepository;
import com.brokersystems.brokerapp.setup.repository.SubClausesRepo;
import com.brokersystems.brokerapp.uw.model.*;
import com.brokersystems.brokerapp.uw.repository.MotorVehicleDetailsRepo;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import com.brokersystems.brokerapp.uw.repository.RiskTransRepo;
import com.brokersystems.brokerapp.uw.repository.SectionTransRepo;
import com.brokersystems.brokerapp.webservices.config.Config;
import com.brokersystems.brokerapp.webservices.model.*;
import com.brokersystems.brokerapp.webservices.service.QuotationWebService;
import com.mysema.query.types.expr.BooleanExpression;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 3/28/2018.
 */
@Service
public class QuotationWebServiceImpl implements QuotationWebService {


    @Autowired
    private QuotTransRepo quotTransRepo;

    @Autowired
    private PolicyTransRepo policyTransRepo;

    @Autowired
    private ClaimService claimService;

    @Autowired
    private ClaimsBookingRepo claimsBookingRepo;

    @Autowired
    private RiskTransRepo riskTransRepo;

    @Autowired
    private MotorVehicleDetailsRepo motorVehicleDetailsRepo;

    @Autowired
    private SectionTransRepo sectionTransRepo;

    @Autowired
    private QuotProductsRepo productsRepo;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private SubClausesRepo subclauseRepo;

    @Override
    @Transactional(readOnly = true)
    public DataResponse findSubAgentQuotes(int page, int size,String key) {
        DataResponse dataResponse = new DataResponse();
        if(key==null || !key.equalsIgnoreCase(Config.API_KEY)){
            dataResponse.setMessage("invalid key");
            return dataResponse;
        }
        Pageable pageable = new PageRequest(page,size);
        BooleanExpression pred = QQuoteTrans.quoteTrans.client.isNotNull();
        Page<QuoteTrans> pages = quotTransRepo.findAll( pred,  pageable);
        List<QuoteTrans> data = pages.getContent();
        List<QuotModel> quotModels = new ArrayList<>();
        for(QuoteTrans trans:data){
            String quotStatus = "";
            switch (trans.getQuotStatus()){
                case "C":
                    quotStatus = "Confirmed";
                    break;
                case "CL":
                    quotStatus = "Cancelled";
                    break;
                case "D":
                    quotStatus = "Draft";
                    break;
                case "R":
                    quotStatus = "Ready";
                    break;
                case "A":
                    quotStatus = "Authorised";
                    break;
                default:
                    quotStatus = "Draft";
            }
            quotModels.add(new QuotModel(new SimpleDateFormat("dd/MM/yyyy").format(trans.getWefDate()),
                    new SimpleDateFormat("dd/MM/yyyy").format(trans.getWetDate()),quotStatus,
                    trans.getQuotNo(),trans.getClient().getFname()+" "+trans.getClient().getOtherNames(),String.valueOf(trans.getQuoteId())));
        }
        dataResponse.setMessage("success");
        return dataResponse;
    }

    @Override
    public List<WebBenefitsDTO> getSwitchBenefits() {
        BooleanExpression pred = QSubclassClauses.subclassClauses.subclass.subShtDesc.equalsIgnoreCase("100");
        Iterable<SubclassClauses> page = subclauseRepo.findAll(pred);
        final List<WebBenefitsDTO> benefitsDTOS = new ArrayList<>();
        for(SubclassClauses subclassClauses:page){
            WebBenefitsDTO webBenefitsDTO = new WebBenefitsDTO();
            if(subclassClauses.getClause().getClauseType()!=null && subclassClauses.getClause().getClauseType().equalsIgnoreCase("L")){
                webBenefitsDTO.setBenefit(subclassClauses.getClause().getClauWording());
                webBenefitsDTO.setShtDesc(subclassClauses.getClause().getClauHeading());
                benefitsDTOS.add(webBenefitsDTO);
            }
        }
        return benefitsDTOS;
    }

    @Override
    public DataResponse findPoliciesByEmail(int page, int size, String email) {
        DataResponse dataResponse = new DataResponse();
        --page;
        Pageable pageable = new PageRequest(page,size);
        if(StringUtils.isBlank(email)){
            dataResponse.setMessage("Invalid Email Address");
            dataResponse.setSuccess(false);
            return dataResponse;
        }
        final Iterable<ClientDef> clientDef = clientRepository.findAll(QClientDef.clientDef.emailAddress.equalsIgnoreCase(email));
        if(clientDef.spliterator().getExactSizeIfKnown()!=1){
            dataResponse.setMessage("No Client returned with Email Address");
            dataResponse.setSuccess(false);
            return dataResponse;
        }
        final Long tenId = Streamable.streamOf(clientDef).map(ClientDef::getTenId).findFirst().get();
        BooleanExpression pred = QPolicyTrans.policyTrans.isNotNull().and(QPolicyTrans.policyTrans.client.tenId.eq(tenId));
        Page<PolicyTrans> pages = policyTransRepo.findAll( pred,  pageable);
        List<PolicyTrans> data = pages.getContent();
        System.out.println("Total Records "+data+" Size..."+data.size()+" count "+pages.getTotalElements()+" client code "+tenId);
        List<PolicyModel> policyModels = new ArrayList<>();
        for(PolicyTrans trans:data){
            String quotStatus = "";
            switch (trans.getAuthStatus()) {
                case "D":
                    quotStatus = "Draft";
                    break;
                case "R":
                    quotStatus = "Ready";
                    break;
                case "A":
                    quotStatus = "Authorised";
                    break;
                default:
                    quotStatus = "Draft";
            }
            ClaimBalanceBean balanceBean = claimService.getBalance(trans.getPolNo());
            String balance = "0.00";
            if(balanceBean.getClientBalance()!=null){
                balance =  String.valueOf(balanceBean.getClientBalance());
            }
            Long id = trans.getPolicyId();
            Iterable<RiskTrans> riskTrans = riskTransRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(id));
            final List<VehicleDTO> vehicleDTOS = new ArrayList<>();
            for(RiskTrans riskTran:riskTrans){
                final VehicleDTO vehicleDTO = new VehicleDTO();
                vehicleDTO.setNumberPlate(riskTran.getRiskShtDesc());
                final VehicleDetails vehicleDetails = this.findSingleVehicleDetails(riskTran.getRiskId());
                if(vehicleDetails!=null) {
                    vehicleDTO.setColor(vehicleDetails.getBodyColor());
                    vehicleDTO.setChassisNumber(vehicleDetails.getChassisNo());
                    vehicleDTO.setMake(vehicleDetails.getCarMake());
                    vehicleDTO.setModel(vehicleDetails.getCarModel());
                    vehicleDTO.setEngineSize(vehicleDetails.getEngineCapacity());
                    if(vehicleDetails.getYearOfManufacture()!=null){
                        vehicleDTO.setYearOfManufacture(Long.parseLong(vehicleDetails.getYearOfManufacture()));
                    }
                    vehicleDTOS.add(vehicleDTO);
                }
            }
            policyModels.add(new PolicyModel(new SimpleDateFormat("dd/MM/yyyy").format(trans.getWefDate()),
                    new SimpleDateFormat("dd/MM/yyyy").format(trans.getWetDate()),quotStatus,balance,
                    trans.getAgent().getName(),trans.getPolNo(),trans.getProduct().getProDesc(),trans.getClient().getFname()+" "+trans.getClient().getOtherNames(),
                    String.valueOf(trans.getPolicyId()),vehicleDTOS.size() > 0?vehicleDTOS.get(0).getNumberPlate():null, vehicleDTOS, String.valueOf(trans.getNetPrem())
            ));
        }
        dataResponse.setPolicyModels(policyModels);
        dataResponse.setMessage("Policies Successful fetched");
        dataResponse.setTotalRecords(pages.getTotalElements());
        dataResponse.setSuccess(true);
        return dataResponse;
    }


    @Override
    public CountObject findClientCountClaims(String idNumber, String pinNo) {
        final CountObject balanceObject = new CountObject();
        int count = 0;
        if(StringUtils.isBlank(pinNo)){
            balanceObject.setObject(count);
            return balanceObject;
        }
        final Iterable<ClientDef> clientDef = clientRepository.findAll(QClientDef.clientDef.pinNo.equalsIgnoreCase(pinNo));
        if(clientDef.spliterator().getExactSizeIfKnown()!=1){
            balanceObject.setObject(count);
            return balanceObject;
        }
        final Long tenId = Streamable.streamOf(clientDef).map(a -> a.getTenId()).findFirst().get();
        BooleanExpression pred = QClaimBookings.claimBookings.isNotNull().and(QClaimBookings.claimBookings.risk.insured.tenId.eq(tenId));
        Long pages = claimsBookingRepo.count( pred);
        balanceObject.setObject(pages.intValue());
        return balanceObject;
    }

    @Override
    public CountObject findClientCountPolicies(String idNumber, String pinNo) {
        final CountObject balanceObject = new CountObject();
        int count = 0;
        if(StringUtils.isBlank(pinNo)){
            balanceObject.setObject(count);
            return balanceObject;
        }
        final Iterable<ClientDef> clientDef = clientRepository.findAll(QClientDef.clientDef.pinNo.equalsIgnoreCase(pinNo));
        if(clientDef.spliterator().getExactSizeIfKnown()!=1){
            balanceObject.setObject(count);
            return balanceObject;
        }
        final Long tenId = Streamable.streamOf(clientDef).map(a -> a.getTenId()).findFirst().get();
        BooleanExpression pred = QPolicyTrans.policyTrans.isNotNull().and(QPolicyTrans.policyTrans.client.tenId.eq(tenId));
        Long pages = policyTransRepo.count( pred);
        balanceObject.setObject(pages.intValue());
        return balanceObject;
    }

    @Override
    public BalanceObject findClientBalance(String idNumber, String pinNo) {
        final BalanceObject balanceObject = new BalanceObject();
         BigDecimal balance  = BigDecimal.ZERO;
        if(StringUtils.isBlank(pinNo)){
            balanceObject.setObject(balance);
            return balanceObject;
        }
        final Iterable<ClientDef> clientDef = clientRepository.findAll(QClientDef.clientDef.pinNo.equalsIgnoreCase(pinNo));
        if(clientDef.spliterator().getExactSizeIfKnown()!=1){
            balanceObject.setObject(balance);
            return balanceObject;
        }
        final Long tenId = Streamable.streamOf(clientDef).map(a -> a.getTenId()).findFirst().get();
        BooleanExpression pred = QPolicyTrans.policyTrans.isNotNull().and(QPolicyTrans.policyTrans.client.tenId.eq(tenId));
        Iterable<PolicyTrans> pages = policyTransRepo.findAll( pred);
        for(PolicyTrans trans:pages){
            ClaimBalanceBean balanceBean = claimService.getBalance(trans.getPolNo());
            if(balanceBean.getClientBalance()!=null){
                balance = balance.add(balanceBean.getClientBalance());
            }
        }
        balanceObject.setObject(balance);
        return balanceObject;
    }

    @Override
    public DataResponse findSubAgentPolicies(int page, int size, String pinNo) {
        DataResponse dataResponse = new DataResponse();
        --page;
        Pageable pageable = new PageRequest(page,size);
        if(StringUtils.isBlank(pinNo)){
            dataResponse.setMessage("Invalid Pin No");
            dataResponse.setSuccess(false);
            return dataResponse;
        }
        final Iterable<ClientDef> clientDef = clientRepository.findAll(QClientDef.clientDef.pinNo.equalsIgnoreCase(pinNo));
        if(clientDef.spliterator().getExactSizeIfKnown()!=1){
            dataResponse.setMessage("No Client returned with Pin");
            dataResponse.setSuccess(false);
            return dataResponse;
        }
        final Long tenId = Streamable.streamOf(clientDef).map(a -> a.getTenId()).findFirst().get();
        BooleanExpression pred = QPolicyTrans.policyTrans.isNotNull().and(QPolicyTrans.policyTrans.client.tenId.eq(tenId));
        Page<PolicyTrans> pages = policyTransRepo.findAll( pred,  pageable);
        List<PolicyTrans> data = pages.getContent();
        List<PolicyModel> policyModels = new ArrayList<>();
        for(PolicyTrans trans:data){
            String quotStatus = "";
            switch (trans.getAuthStatus()) {
                case "D":
                    quotStatus = "Draft";
                    break;
                case "R":
                    quotStatus = "Ready";
                    break;
                case "A":
                    quotStatus = "Active";
                    break;
                default:
                    quotStatus = "Draft";
            }
            ClaimBalanceBean balanceBean = claimService.getBalance(trans.getPolNo());
            String balance = "0.00";
            if(balanceBean.getClientBalance()!=null){
                balance =  String.valueOf(balanceBean.getClientBalance());
            }
            Long id = trans.getPolicyId();
            Iterable<RiskTrans> riskTrans = riskTransRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(id));
            final List<VehicleDTO> vehicleDTOS = new ArrayList<>();
            for(RiskTrans riskTran:riskTrans){
                final VehicleDTO vehicleDTO = new VehicleDTO();
                vehicleDTO.setNumberPlate(riskTran.getRiskShtDesc());
                final VehicleDetails vehicleDetails = this.findSingleVehicleDetails(riskTran.getRiskId());
                if(vehicleDetails!=null) {
                    vehicleDTO.setColor(vehicleDetails.getBodyColor());
                    vehicleDTO.setChassisNumber(vehicleDetails.getChassisNo());
                    vehicleDTO.setMake(vehicleDetails.getCarMake());
                    vehicleDTO.setModel(vehicleDetails.getCarModel());
                    vehicleDTO.setEngineSize(vehicleDetails.getEngineCapacity());
                    if(vehicleDetails.getYearOfManufacture()!=null){
                        vehicleDTO.setYearOfManufacture(Long.parseLong(vehicleDetails.getYearOfManufacture()));
                    }
                    vehicleDTOS.add(vehicleDTO);
                }
            }

            policyModels.add(new PolicyModel(new SimpleDateFormat("dd/MM/yyyy").format(trans.getWefDate()),
                    new SimpleDateFormat("dd/MM/yyyy").format(trans.getWetDate()),quotStatus,balance,
                    trans.getAgent().getName(),trans.getPolNo(),trans.getProduct().getProDesc(),trans.getClient().getFname()+" "+trans.getClient().getOtherNames(),
                    String.valueOf(trans.getPolicyId()),vehicleDTOS.size() > 0?vehicleDTOS.get(0).getNumberPlate():null, vehicleDTOS,String.valueOf(trans.getNetPrem())
                    ));
        }
        dataResponse.setPolicyModels(policyModels);
        dataResponse.setMessage("Policies Successful fetched");
        dataResponse.setTotalRecords(pages.getTotalElements());
        dataResponse.setSuccess(true);
        return dataResponse;
    }

    @Override
    public DataResponse findSubAgentClaims(int page, int size,String key) {
        DataResponse dataResponse = new DataResponse();
        if(key==null || !key.equalsIgnoreCase(Config.API_KEY)){
            dataResponse.setMessage("invalid key");
            return dataResponse;
        }
        Pageable pageable = new PageRequest(page,size);
//        BooleanExpression pred = QClaimBookings.claimBookings.isNotNull();
        Page<ClaimBookings> pages = claimsBookingRepo.findAll(pageable);
        List<ClaimBookings> data = pages.getContent();
        List<ClaimModel> claimModels = new ArrayList<>();
        for(ClaimBookings trans:data) {
            String quotStatus = "";
            switch (trans.getClaimStatus()) {
                case "P":
                    quotStatus = "Pending";
                    break;
                case "R":
                    quotStatus = "Ready";
                    break;
                case "B":
                    quotStatus = "Booked";
                    break;
                default:
                    quotStatus = "Booked";
            }
            claimModels.add(new ClaimModel(trans.getClaimNo(),trans.getRisk().getInsured().getFname()+" "+trans.getRisk().getInsured().getOtherNames(),
                    trans.getRisk().getRiskShtDesc(),new SimpleDateFormat("dd/MM/yyyy").format(trans.getLossDate()),quotStatus, String.valueOf(trans.getClmId()),
                    trans.getLossDesc(),trans.getRiskIdentifier(),new SimpleDateFormat("dd/MM/yyyy").format(trans.getBookedDate())));
        }
        dataResponse.setMessage("success");
        return dataResponse;
    }

    @Override
    public PolicyWebInfo getPolicyInfo(String polId) {
        PolicyWebInfo webInfo = new PolicyWebInfo();
        if(polId==null){
            webInfo.setMessage("error getting policy details");
            return webInfo;
        }
        Long id = Long.valueOf(polId);
        PolicyTrans policyTrans = policyTransRepo.findOne(id);
        if(policyTrans==null){
            webInfo.setMessage("error getting policy details");
            return webInfo;
        }
        if(policyTrans.getAuthDate()!=null)
        webInfo.setAuthDate(new SimpleDateFormat("dd/MM/yyyy").format(policyTrans.getAuthDate()));
        if("A".equalsIgnoreCase(policyTrans.getAuthStatus()))
        webInfo.setAuthStatus("Authorised");
        else if("R".equalsIgnoreCase(policyTrans.getAuthStatus()))
            webInfo.setAuthStatus("Ready");
        else if("D".equalsIgnoreCase(policyTrans.getAuthStatus()))
            webInfo.setAuthStatus("Draft");
        webInfo.setCommission(policyTrans.getSubAgentComm());
        webInfo.setCurrency(policyTrans.getTransCurrency().getCurName());
        webInfo.setInsurance(policyTrans.getAgent().getName());
        webInfo.setMessage("success");
        webInfo.setPolNo(policyTrans.getPolNo());
        webInfo.setPolRevNo(policyTrans.getPolRevNo());
        webInfo.setPremium(policyTrans.getPremium());
        webInfo.setProduct(policyTrans.getProduct().getProDesc());
        webInfo.setRefNo(policyTrans.getRefNo());
        if(policyTrans.getRenewalDate()!=null) {
            webInfo.setRenewalDate(new SimpleDateFormat("dd/MM/yyyy").format(policyTrans.getRenewalDate()));
        }
        webInfo.setSumInsured(policyTrans.getSumInsured());
        webInfo.setTaxes(( (policyTrans.getExtras()!=null)? policyTrans.getExtras(): BigDecimal.ZERO).
                add((policyTrans.getStampDuty()!=null)? policyTrans.getStampDuty():BigDecimal.ZERO).
                add( (policyTrans.getPhcf()!=null)? policyTrans.getPhcf():BigDecimal.ZERO).
                add((policyTrans.getTrainingLevy()!=null)? policyTrans.getTrainingLevy():BigDecimal.ZERO));
        if(policyTrans.getWefDate()!=null) {
            webInfo.setWefDate(new SimpleDateFormat("dd/MM/yyyy").format(policyTrans.getWefDate()));
        }
        if(policyTrans.getWetDate()!=null) {
            webInfo.setWetDate(new SimpleDateFormat("dd/MM/yyyy").format(policyTrans.getWetDate()));
        }
        Iterable<RiskTrans> riskTrans = riskTransRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(id));
        final List<VehicleDTO> vehicleDTOS = new ArrayList<>();
        for(RiskTrans riskTran:riskTrans){
            final VehicleDTO vehicleDTO = new VehicleDTO();
            vehicleDTO.setNumberPlate(riskTran.getRiskShtDesc());
            final VehicleDetails vehicleDetails = this.findSingleVehicleDetails(riskTran.getRiskId());
            if(vehicleDetails!=null) {
                vehicleDTO.setColor(vehicleDetails.getBodyColor());
                vehicleDTO.setChassisNumber(vehicleDetails.getChassisNo());
                vehicleDTO.setMake(vehicleDetails.getCarMake());
                vehicleDTO.setModel(vehicleDetails.getCarModel());
                vehicleDTO.setEngineSize(vehicleDetails.getEngineCapacity());
                if(vehicleDetails.getYearOfManufacture()!=null){
                    vehicleDTO.setYearOfManufacture(Long.parseLong(vehicleDetails.getYearOfManufacture()));
                }
                vehicleDTOS.add(vehicleDTO);
            }
        }
        webInfo.setVehicleDetails(vehicleDTOS);
        return webInfo;
    }

    private VehicleDetails findSingleVehicleDetails(Long ipuCode) {
        List<Object[]> motorDetails = motorVehicleDetailsRepo.getVehicleDetails(ipuCode);
        for(Object[] motorDetail:motorDetails){
            VehicleDetails vehicleDetails = new VehicleDetails();
            vehicleDetails.setBodyColor((String) motorDetail[0]);
            vehicleDetails.setBodyType((String) motorDetail[1]);
            vehicleDetails.setCarMake((String) motorDetail[2]);
            vehicleDetails.setCarModel((String) motorDetail[3]);
            vehicleDetails.setCarryCapacity(((BigDecimal)motorDetail[4]));
            vehicleDetails.setChassisNo((String) motorDetail[5]);
            vehicleDetails.setEngineCapacity(((BigDecimal)motorDetail[6]));
            vehicleDetails.setEngineNumber((String) motorDetail[7]);
            vehicleDetails.setLogbookNumber((String) motorDetail[8]);
            vehicleDetails.setYearOfManufacture((motorDetail[9]!=null)?motorDetail[9].toString():null);
            return vehicleDetails;
        }
        return null;
    }

    @Override
    public DataResponse findPolicyRisks(String polId, String key) {
        DataResponse dataResponse = new DataResponse();
        if(key==null || !key.equalsIgnoreCase(Config.API_KEY)){
            dataResponse.setMessage("invalid key");
            return dataResponse;
        }
        List<RisksWebInfo> risksWebInfos = new ArrayList<>();
        Long id = Long.valueOf(polId);
        Iterable<RiskTrans> riskTrans = riskTransRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(id));
        for(RiskTrans riskTran:riskTrans){
            RisksWebInfo webInfo = new RisksWebInfo();
            webInfo.setCoverType(riskTran.getCovertype().getCovName());
            webInfo.setInsured(riskTran.getInsured().getFname()+" "+riskTran.getInsured().getOtherNames());
            webInfo.setPremium(riskTran.getPremium().toString());
            webInfo.setRiskDesc(riskTran.getRiskDesc());
            webInfo.setRiskId(riskTran.getRiskShtDesc());
            webInfo.setWef(new SimpleDateFormat("dd/MM/yyyy").format(riskTran.getWefDate()));
            webInfo.setWet(new SimpleDateFormat("dd/MM/yyyy").format(riskTran.getWetDate()));
            webInfo.setType("R");
            risksWebInfos.add(webInfo);
            Iterable<SectionTrans> sectionTranses = sectionTransRepo.findAll(QSectionTrans.sectionTrans.risk.riskId.eq(riskTran.getRiskId()));
            for(SectionTrans sectionTrans:sectionTranses){
                RisksWebInfo sectionWebInfo = new RisksWebInfo();
                sectionWebInfo.setSectionName(sectionTrans.getSection().getDesc());
                sectionWebInfo.setSectionPremium((sectionTrans.getPrem()!=null)?sectionTrans.getPrem().toString():"0");
                sectionWebInfo.setSectionRate(sectionTrans.getRate().toString());
                sectionWebInfo.setSectionValue(sectionTrans.getAmount().toString());
                sectionWebInfo.setType("S");
                risksWebInfos.add(sectionWebInfo);
            }
        }
//        dataResponse.setRisksWebInfos(risksWebInfos);
        dataResponse.setMessage("success");
        return dataResponse;
    }

    @Override
    public QuoteWebInfo getQuotInfo(String quotId, String key) {
        QuoteWebInfo webInfo = new QuoteWebInfo();
        if(key==null || !key.equalsIgnoreCase(Config.API_KEY)){
            webInfo.setMessage("invalid key");
            return webInfo;
        }
        if(quotId==null){
            webInfo.setMessage("error getting policy details");
            return webInfo;
        }
        Long id = Long.valueOf(quotId);
        QuoteTrans quoteTrans = quotTransRepo.findOne(id);
        webInfo.setClientType(("C".equalsIgnoreCase(quoteTrans.getClientType())?"Client":"Prospect"));
        if(quoteTrans.getClientType()!=null) {
            if (quoteTrans.getClientType().equalsIgnoreCase("C")) {
                webInfo.setClientName(quoteTrans.getClient().getFname() + " " + quoteTrans.getClient().getOtherNames());
            } else if(quoteTrans.getClientType().equalsIgnoreCase("P")){
                webInfo.setClientName(quoteTrans.getProspect().getFname()+" "+quoteTrans.getProspect().getOtherNames());
            }
        }
        webInfo.setCurrency(quoteTrans.getTransCurrency().getCurName());
        webInfo.setMessage("success");
        if(quoteTrans.getExpiryDate()!=null)
        webInfo.setQuotExpDate(new SimpleDateFormat("dd/MM/yyyy").format(quoteTrans.getExpiryDate()));
        webInfo.setQuotNo(quoteTrans.getQuotNo());
        webInfo.setQuotPrem((quoteTrans.getPremium()!=null)?quoteTrans.getPremium().toString():"0");
        String quotStatus = "";
        switch (quoteTrans.getQuotStatus()){
            case "C":
                quotStatus = "Confirmed";
                break;
            case "CL":
                quotStatus = "Cancelled";
                break;
            case "D":
                quotStatus = "Draft";
                break;
            case "R":
                quotStatus = "Ready";
                break;
            case "A":
                quotStatus = "Authorised";
                break;
            default:
                quotStatus = "Draft";
        }
        webInfo.setQuotStatus(quotStatus);
        webInfo.setQuotTaxes(((quoteTrans.getStampDuty()!=null)?quoteTrans.getStampDuty():BigDecimal.ZERO)
                .add((quoteTrans.getPhcf()!=null)?quoteTrans.getPhcf():BigDecimal.ZERO)
                .add((quoteTrans.getTrainingLevy()!=null)?quoteTrans.getTrainingLevy():BigDecimal.ZERO)
                .add((quoteTrans.getExtras()!=null)?quoteTrans.getExtras():BigDecimal.ZERO).toString());
        webInfo.setWef(new SimpleDateFormat("dd/MM/yyyy").format(quoteTrans.getWefDate()));
        webInfo.setWet(new SimpleDateFormat("dd/MM/yyyy").format(quoteTrans.getWetDate()));
        return webInfo;
    }

    @Override
    public DataResponse findQuotProducts(String quotId, String key) {
        DataResponse dataResponse = new DataResponse();
        if(key==null || !key.equalsIgnoreCase(Config.API_KEY)){
            dataResponse.setMessage("invalid key");
            return dataResponse;
        }
        List<QuotWebProduct> quotWebProducts = new ArrayList<>();
        Long id = Long.valueOf(quotId);
        Iterable<QuoteProTrans> proTranses = productsRepo.findAll(QQuoteProTrans.quoteProTrans.quoteTrans.quoteId.eq(id));
        for(QuoteProTrans proTrans:proTranses){
            QuotWebProduct webProduct = new QuotWebProduct();
            webProduct.setInsuranceCompany(proTrans.getAgent().getName());
            webProduct.setPremium((proTrans.getPremium()!=null)?proTrans.getPremium().toString():"0");
            webProduct.setProductName(proTrans.getProduct().getProDesc());
            webProduct.setWef(new SimpleDateFormat("dd/MM/yyyy").format(proTrans.getWefDate()));
            webProduct.setWet(new SimpleDateFormat("dd/MM/yyyy").format(proTrans.getWetDate()));
            webProduct.setConverted((proTrans.getConverted()!=null && "Y".equalsIgnoreCase(proTrans.getConverted()))?"Yes":"No");
            webProduct.setPolicyNo((proTrans.getConvertedReference()!=null)?proTrans.getConvertedReference().getPolNo():"");
            webProduct.setTaxes(((proTrans.getStampDuty()!=null)?proTrans.getStampDuty():BigDecimal.ZERO)
                    .add((proTrans.getPhfFund()!=null)?proTrans.getPhfFund():BigDecimal.ZERO)
                    .add((proTrans.getTrainingLevy()!=null)?proTrans.getTrainingLevy():BigDecimal.ZERO)
                    .add((proTrans.getExtras()!=null)?proTrans.getExtras():BigDecimal.ZERO).toString());
            quotWebProducts.add(webProduct);
        }
//        dataResponse.setQuotWebProducts(quotWebProducts);
        dataResponse.setMessage("success");
        return dataResponse;
    }

    @Override
    public DataResponse getClientDetails(Long subAgentId, int page, int size,String key,String search) {
        DataResponse dataResponse = new DataResponse();
        if(key==null || !key.equalsIgnoreCase(Config.API_KEY)){
            dataResponse.setMessage("invalid key");
            return dataResponse;
        }
        Pageable pageable = new PageRequest(page,size);
        Page<ClientDef> clients = clientRepository.findAll(pageable);
        List<WebClient> webClients = new ArrayList<>();
        for(ClientDef clientDef:clients.getContent()){
            WebClient webClient = new WebClient();
            webClient.setClientId(clientDef.getTenId());
            webClient.setClientShtDesc(clientDef.getTenantNumber());
            webClient.setClientName(clientDef.getFname()+" "+clientDef.getOtherNames());
            webClients.add(webClient);
        }
//        dataResponse.setWebClients(webClients);
        return dataResponse;
    }
}
