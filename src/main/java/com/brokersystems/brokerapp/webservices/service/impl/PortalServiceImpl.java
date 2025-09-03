package com.brokersystems.brokerapp.webservices.service.impl;

import com.brokersystems.brokerapp.aki.dto.TypeCIssueDTO;
import com.brokersystems.brokerapp.aki.service.AkiAuthenticationService;
import com.brokersystems.brokerapp.certs.model.RiskCertForm;
import com.brokersystems.brokerapp.certs.model.SubclassCertTypes;
import com.brokersystems.brokerapp.certs.repository.SubclassCertTypesRepo;
import com.brokersystems.brokerapp.certs.service.CertService;
import com.brokersystems.brokerapp.claims.exception.ClaimException;
import com.brokersystems.brokerapp.claims.model.*;
import com.brokersystems.brokerapp.claims.repository.ClaimsBookingRepo;
import com.brokersystems.brokerapp.enums.RevenueItems;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.*;
import com.brokersystems.brokerapp.setup.dto.ClientDTO;
import com.brokersystems.brokerapp.setup.dto.UserDTO;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.*;
import com.brokersystems.brokerapp.setup.service.OrganizationService;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.brokersystems.brokerapp.setup.service.UserService;
import com.brokersystems.brokerapp.trans.model.SystemTrans;
import com.brokersystems.brokerapp.trans.model.WebServiceReceipts;
import com.brokersystems.brokerapp.trans.repository.SystemTransRepo;
import com.brokersystems.brokerapp.trans.repository.SystemTransactionsRepo;
import com.brokersystems.brokerapp.trans.repository.WebServiceReceiptsRepo;
import com.brokersystems.brokerapp.uw.model.*;
import com.brokersystems.brokerapp.uw.repository.*;
import com.brokersystems.brokerapp.uw.service.PolicyTransService;
import com.brokersystems.brokerapp.uw.service.PremComputeService;
import com.brokersystems.brokerapp.webservices.model.*;
import com.brokersystems.brokerapp.webservices.portalmodel.*;
import com.brokersystems.brokerapp.webservices.portalmodel.PolicyData;
import com.brokersystems.brokerapp.webservices.service.PortalService;
import com.brokersystems.brokerapp.webservices.utils.BinderExcelUtils;
import com.brokersystems.brokerapp.workflow.docs.DocType;
import com.brokersystems.brokerapp.workflow.utils.WorkflowService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mysema.query.types.Predicate;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PortalServiceImpl implements PortalService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private OrganizationService orgService;

    @Autowired
    private DataSource datasource;


    @Autowired
    private CertService certService;
    @Autowired
    private UserService userRepository;

    @Autowired
    private PostalCodeRepo postalCodeRepo;

    @Autowired
    private ClientTypeRepo clientTypeRepo;

    @Autowired
    private SequenceRepository sequenceRepo;

    @Autowired
    private ClaimsBookingRepo claimsBookingRepo;

    @Autowired
    private SystemTransRepo systemTransRepo;


    @Autowired
    private CoverTypesRepo coverTypesRepo;

    @Autowired
    ServletContext context;

    @Autowired
    TemplateMerger templateMerger;

    @Autowired
    private ClmStatusRepo clmStatusRepo;

    @Autowired
    private ParamService paramService;

    @Autowired
    private OrgBranchRepository branchRepository;

    @Autowired
    private BindersRepo bindersRepo;

    @Autowired
    private BinderDetRepo binderDetRepo;

    @Autowired
    private RiskTransRepo riskTransRepo;

    @Autowired
    private RiskDocsRepo riskDocsRepo;

    @Autowired
    private SubclassReqDocRepo subclassReqDocRepo;

    @Autowired
    private SubclassCertTypesRepo subclassCertTypesRepo;
    @Autowired
    private SectionRepo sectionRepo;

    @Autowired
    private SectionTransRepo sectionTransRepo;
    @Autowired
    private PolActiveRisksRepo activeRisksRepo;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private WebServiceReceiptsRepo webServiceReceiptRepo;

    @Autowired
    private PremComputeService premComputeService;

    @Autowired
    private PolicyTransRepo policyTransRepo;

    @Autowired
    private PolicyTransService policyService;

    @Autowired
    private SystemTransRepo transactionsRepo;
    @Autowired
    private BinderClauseRepo clausesRepo;

     @Autowired
    private TaxRatesRepo taxRatesRepo;

     @Autowired
     private PremRatesRepo premRatesRepo;

     @Autowired
     private BinderExcelUtils excelUtils;
     @Autowired
     private CurrencyRepository currencyRepository;
     @Autowired
     private ProductsRepo productsRepo;
    @Autowired
    private DateUtilities dateUtils;
    @Autowired
    private PaymentModeRepo paymentModeRepo;

    @Autowired
    private MotorVehicleDetailsRepo motorVehicleDetailsRepo;

    @Override
    public String createClient(Agent client) throws BadRequestException {
        ClientDef clientDef = new ClientDef();
        boolean isNewClient = true;

        if(StringUtils.isNotEmpty(client.getIdNumber())){
             clientDef = clientRepository.findOne(QClientDef.clientDef.idNo.eq(client.getIdNumber()));
             if(clientDef==null){

             }
             else{
                 isNewClient=false;
             }

             if(isNewClient){
                 clientDef = clientRepository.findOne(QClientDef.clientDef.pinNo.eq(client.getPin()));
                 if(clientDef==null){

                 }
                 else{
                     isNewClient=false;
                 }

             }
        }

        clientDef.setAddress(client.getPostalAddress());
        try{
        if(StringUtils.isNotEmpty(client.getDob()))
           clientDef.setDob(new SimpleDateFormat("dd-MM-yyyy").parse(client.getDob()));
        }
        catch (ParseException ex){

        }

        clientDef.setDateCreated(new Date());
        clientDef.setEmailAddress(client.getEmail());
        clientDef.setIdNo(client.getIdNumber());
        int countSpaces =  client.getName().replaceAll("[^ ]", "").length();
        if(countSpaces==2){
            String[] nameArr = client.getName().split(" ");
            clientDef.setFname(nameArr[0]);
            clientDef.setOtherNames(nameArr[1]+" "+nameArr[2]);
        }
        else if(countSpaces==1){
            String[] nameArr = client.getName().split(" ");
            clientDef.setFname(nameArr[0]);
            clientDef.setOtherNames(nameArr[1]);
        }
        if(client.getTelNo()!=null)
        clientDef.setPhoneNo(client.getTelNo().substring(client.getTelNo().length()-7));
        clientDef.setAuthStatus("Y");
        if(client.getPostalCode()!=null){
            PostalCodesDef postalCodesDef = postalCodeRepo.findOne(QPostalCodesDef.postalCodesDef.postalName.eq(client.getPostalCode()));
            clientDef.setPostalCodesDef(postalCodesDef);
        }
        clientDef.setPinNo(client.getPin());
        clientDef.setGender(client.getGender());
        if(isNewClient) {
            clientDef.setDateregistered(new Date());
            Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("C");
            if (sequenceRepo.count(seqPredicate) == 0)
                throw new BadRequestException("Sequence for Client Definition has not been setup");
            SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
            Long seqNumber = sequence.getNextNumber();
            final String clientNumber = sequence.getSeqPrefix() + String.format("%06d", seqNumber);
            sequence.setLastNumber(seqNumber);
            sequence.setNextNumber(seqNumber + 1);
            sequenceRepo.save(sequence);
            OrgBranch branch = branchRepository.findOne(QOrgBranch.orgBranch.obShtDesc.eq("HO"));
            clientDef.setRegisteredbrn(branch);
            clientDef.setStatus("A");
            clientDef.setAuthStatus("Y");

//        clientDef.setAuthBy();
            clientDef.setTenantNumber(clientNumber);
        }

        Iterable<ClientTypes> clientTypes = clientTypeRepo.findAll(QClientTypes.clientTypes.clientType.eq("I"));
        if(clientTypes.spliterator().getExactSizeIfKnown() > 0)
        {
            clientDef.setTenantType(Streamable.streamOf(clientTypes).findFirst().get());
        }
//        HERE
//        clientDef.set
//        UserDTO user = userRepository.findUserSignatureDetails(client.)


        ClientDef savedClient =   clientRepository.save(clientDef);


        return String.valueOf(savedClient.getTenId());
    }

    @Override
    public String createAgent(Agent agent) {
        if(agent.getPin()==null){
            throw new RuntimeException("Please provide Pin No");
        }
        if(accountRepo.findAccountDefByPinNo(agent.getPin()).size() > 0){
            throw new RuntimeException("Pin No already used..");
        }
        AccountDef accountDef = new AccountDef();
        accountDef.setAddress(agent.getPostalAddress());
        try{
            if(StringUtils.isNotEmpty(agent.getDob()))
                accountDef.setDob(new SimpleDateFormat("dd-MM-yyyy").parse(agent.getDob()));
        }
        catch (ParseException ex){

        }
        accountDef.setCreatedDate(new Date());
        accountDef.setEmail(agent.getEmail());
        accountDef.setLicenseNumber(agent.getLicenseNumber());
//        accountDef.set(client.getIdNumber());
        accountDef.setName(agent.getName());
        if(agent.getTelNo()!=null)
            accountDef.setPhoneNo(agent.getTelNo());

        accountDef.setPinNo(agent.getPin());

//        clientDef.set
        AccountDef savedClient =   accountRepo.save(accountDef);

        return String.valueOf(savedClient.getAcctId());
    }

    @Override
    public DocResponseObject getPolicyDocument(Long policyId) throws BadRequestException {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        final PolicyTrans policyTrans = policyTransRepo.findOne(policyId);
        System.out.println("Policy Found...."+policyTrans.getPolicyId());
        if(policyTrans==null){
            throw new BadRequestException("Unable to get Policy Details. Contact Administrator");
        }
        try {
            byte[] output = policyService.getPolicyDocument(policyTrans.getProduct().getProCode());
            System.out.println("Byte Length..."+output);
            File folder = new File(context.getRealPath("")+"/policydocs");
            if(!folder.exists()){
                folder.mkdir();
            }
            File file  = new File(context.getRealPath("")+"/policydocs/policy_doc"+new SimpleDateFormat("ddMMyyyyHHmmss").format(new Date())+".pdf");
            OutputStream os = Files.newOutputStream(file.toPath());
            os.write(output);
            final DocResponseObject responseObject = new DocResponseObject();
            responseObject.setFileurl("https://sis.uat.tiaswitch.ke/policydocs/"+file.getName());
            responseObject.setSuccess(true);
            return responseObject;
        } catch (IOException e) {
            e.printStackTrace();
            throw new BadRequestException("Unable to get Report Details. Contact Administrator");
        }
    }

    @Override
    @Transactional(readOnly = false,rollbackFor = { BadRequestException.class })
    public ResponseObject downgradePolicy(DowngradePolicyObject policyData) throws BadRequestException{
        if(policyData==null){
            throw new BadRequestException("The Policy Downgrade object must not be null");
        }
        if(policyData.getRegistrationNumber()==null){
            throw new BadRequestException("The Registration Number cannot be null");
        }

        if(policyData.getEffectiveDate()==null){
            throw new BadRequestException("Effective Downgrade cannot be null");
        }
         Date date = null;
        try {
             date = new SimpleDateFormat("dd-MMM-yyyy").parse(policyData.getEffectiveDate());
        } catch (ParseException e) {
            throw new BadRequestException("Effective Date format must be dd-MMM-yyyy ");
        }
        List<BigDecimal> riskIds = riskTransRepo.findDuplicateRisks(policyData.getRegistrationNumber());

        PolicyTrans policyTrans = null;
        for(BigDecimal risk:riskIds){
            final RiskTrans riskTrans = riskTransRepo.findOne(((BigDecimal)risk).longValue());
            if(riskTrans.getPolicy().getCurrentStatus().equalsIgnoreCase("A")){
                policyTrans = riskTrans.getPolicy();
            }
        }
        if(policyTrans==null){
            throw new BadRequestException("Invalid Registration Number..Cannot continue..");
        }
        final ResponseObject responseObject = new ResponseObject();
        responseObject.setRefcode(policyTrans.getPolicyId());
        responseObject.setPolicyno(policyTrans.getPolNo());
        responseObject.setMessage("Policy Downgraded successfully");
        responseObject.setSuccess(true);
        return responseObject;

    }

    @Override
    @Transactional(readOnly = false,rollbackFor = { BadRequestException.class })
    public ResponseObject createPolicy(PolicyData policyData) throws BadRequestException {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        System.out.println(gson.toJson(policyData).replace("\\\\", "\\"));
        if(policyData==null){
            throw new BadRequestException("The Policy object must not be null");
        }
        if(policyData.getRisks()==null || policyData.getRisks().isEmpty()){
            throw new BadRequestException("Your policy must have at least one risk...");
        }

        if(policyData.getPaymentInfo()==null){
            throw new BadRequestException("Please Provide Payment Information to continue..");
        }

        if(policyData.getCurrency()==null){
            throw new BadRequestException("Currency is mandatory...");
        }
        final List<Currencies> currenciesList = currencyRepository.findCurrenciesByCurIsoCode(policyData.getCurrency());

        if(currenciesList.size()!=1){
            throw new BadRequestException("Invalid Currency Entered..");
        }
        if(policyData.getProduct()==null){
            throw new BadRequestException("Product is mandatory...");
        }
        final List<ProductsDef> productsList = productsRepo.findProductsDefByProShtDesc(policyData.getProduct());
        if(productsList.size()!=1){
            throw new BadRequestException("Invalid Product Entered..");
        }
        if(policyData.getClient()==null){
            throw new BadRequestException("Client is Mandatory...");
        }
        final ClientDTO clientDTO = policyData.getClient();
        final OrgBranch branch = branchRepository.findOne(QOrgBranch.orgBranch.obShtDesc.eq("HO"));
        if(branch==null){
            throw new BadRequestException("Invalid Head office...");
        }
        Date wef = null;
        try{
            wef = new SimpleDateFormat("dd/MM/yyyy").parse(policyData.getWefDate());
        }
        catch (ParseException ex){
            throw new BadRequestException("Invalid Date format for policy Start Date. The format should be dd/MM/yyyy");
        }
        if(wef.before(dateUtils.removeTime(new Date()))){
            throw new BadRequestException("Policy Start Date should be today or in the future...");
        }
        List<PaymentModes> paymentModes = paymentModeRepo.findPaymentModesByPmShtDesc(policyData.getPaymentMode());
        if(paymentModes.size()!=1){
            throw new BadRequestException("Invalid Payment Mode Entered");
        }
        final UserDTO user = userRepository.findByUserName("Admin");
        if(user==null){
            throw new BadRequestException("Unable to get Admin user to auto create policies");
        }

        for(RiskData riskData:policyData.getRisks()){
            if(riskData.getRiskShtDesc()==null){
                throw new BadRequestException("The Vehicle Registration Number cannot be null");
            }
            if(riskTransRepo.checkDuplicateRisks(riskData.getRiskShtDesc().toUpperCase()) > 0){
                 List<Object[]> riskTrans = riskTransRepo.getDuplicateRisks(riskData.getRiskShtDesc().toUpperCase());
                final ResponseObject responseObject = new ResponseObject();
                Object[] risk = riskTrans.get(0);
                responseObject.setRefcode(((BigDecimal)risk[0]).longValue());
                responseObject.setPolicyno((String)risk[1]);
                responseObject.setMessage("Policy Created successfully");
                responseObject.setSuccess(true);
                return responseObject;
            }
        }
        final ProductsDef productsDef = productsList.get(0);
        final Date wet = dateUtils.getWetDate(wef);
        final ClientDef clientDef = validateAndCreateClient(clientDTO,branch);
        final BindersDef bindersDef =bindersRepo.findOne(QBindersDef.bindersDef.product.proCode.eq(productsDef.getProCode()).and(QBindersDef.bindersDef.binStatus.eq("Authorised")));
        if(bindersDef==null){
            throw new BadRequestException("No Default Contract for the Product...");
        }
        final PolicyTrans policyTrans = new PolicyTrans();
        policyTrans.setClient(clientDef);
        policyTrans.setBranch(branch);
        policyTrans.setTransCurrency(currenciesList.get(0));
        policyTrans.setBusinessType("N");
        policyTrans.setWefDate(wef);
        policyTrans.setWetDate(wet);
        policyTrans.setProduct(productsDef);
        policyTrans.setRenewalDate(DateUtils.addDays(wet,1));
        policyTrans.setPaymentMode(paymentModes.get(0));
        policyTrans.setUwYear(dateUtils.getUwYear(wef));
        policyTrans.setCommAllowed(true);
        policyTrans.setBinder(bindersDef);
        policyTrans.setCoverFrom(wef);
        policyTrans.setCoverTo(wet);
        policyTrans.setCreatedUser(userRepository.findById(user.getId()));
        policyTrans.setCurrentStatus("D");
        policyTrans.setFrequency("A");
        policyTrans.setInterfaceType("A");
        String policyNumberFormat = paramService.getParameterString("POLICY_NO_FORMAT");
        String endorsementFormat = paramService.getParameterString("ENDORSE_NO_FORMAT");
        Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("P");
        if (sequenceRepo.count(seqPredicate) == 0)
            throw new BadRequestException("Sequence for New Business Transactions has not been defined");
        SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
        Long seqNumber = sequence.getNextNumber();
        final String policyNumber = templateMerger.generateFormat(policyNumberFormat, branch.getObId(), productsDef.getProCode(),wef, sequence.getSeqPrefix() + String.format("%05d", seqNumber),null);
        policyTrans.setPolNo(policyNumber);

        sequence.setLastNumber(seqNumber);
        sequence.setNextNumber(seqNumber + 1);
        sequenceRepo.save(sequence);
        Predicate endorsePredicate = QSystemSequence.systemSequence.transType.eq("E");
        if (sequenceRepo.count(endorsePredicate) == 0)
            throw new BadRequestException("Sequence for Endorsement Transactions has not been defined");
        SystemSequence endorseSequence = sequenceRepo.findOne(endorsePredicate);
        Long endosseqNumber = endorseSequence.getNextNumber();
        final String revNumber = endorseSequence.getSeqPrefix() + String.format("%05d", endosseqNumber);
        final String endorseNumber = templateMerger.generateFormat(endorsementFormat, branch.getObId(), productsDef.getProCode(), wef, revNumber,null);
        policyTrans.setPolRevNo(endorseNumber + "/1");
        policyTrans.setRevisionFormat(endorseNumber);
        endorseSequence.setLastNumber(endosseqNumber);
        endorseSequence.setNextNumber(endosseqNumber + 1);
        sequenceRepo.save(endorseSequence);
        policyTrans.setTransType("NB");
        policyTrans.setPolRevStatus("NB");
        policyTrans.setAuthStatus("D");
        policyTrans.setPreviousTrans(policyTrans);
        SystemTrans transaction = new SystemTrans();
        transaction.setDoneDate(new Date());
        transaction.setDoneBy(userRepository.findById(user.getId()));
        transaction.setPolicy(policyTrans);
        transaction.setTransLevel("U");
        transaction.setTransCode("NBD"); //A way to setup and look up for transaction transcode
        transaction.setTransAuthorised("N");
        policyTrans.setAgent(bindersDef.getAccount());
        policyTrans.setRenewable(productsDef.isRenewable());
        policyTrans.setPolCreateddt(new Date());
        PolicyTrans savedPolicy = policyTransRepo.save(policyTrans);
        transactionsRepo.save(transaction);
        for(RiskData riskData:policyData.getRisks()){
            final List<CoverTypesDef> coverTypes = coverTypesRepo.findCoverTypesDefByCovShtDesc(riskData.getCoverType());
            if(coverTypes.size()!=1){
                throw new BadRequestException("Invalid Cover Type "+riskData.getCoverType());
            }
           final Iterable<BinderDetails> binderDetails = binderDetRepo.findAll(QBinderDetails.binderDetails.binder.binId.eq(bindersDef.getBinId()).and(QBinderDetails.binderDetails.subCoverTypes.coverTypes.covId.eq(coverTypes.get(0).getCovId())));
            if(binderDetails.spliterator().getExactSizeIfKnown()!=1){
                throw new BadRequestException("Check on the setups. Unable to get the complete configuration to complete the vehicle underwriting");
            }
            final ClientDef insured =(riskData.getInsured()==null)?clientDef: validateAndCreateClient(riskData.getInsured(), branch);
            RiskTrans risk = new RiskTrans();
            risk.setWefDate(wef);
            risk.setWetDate(wet);
            risk.setCovertype(coverTypes.get(0));
            risk.setBinderDetails(binderDetails.iterator().next());
            risk.setBinder(bindersDef);
            risk.setSubclass(binderDetails.iterator().next().getSubCoverTypes().getSubclass());
            risk.setAutogenCert("N");
            risk.setCommRate(BigDecimal.ZERO);
            risk.setInsured(insured);
            risk.setPolicy(savedPolicy);
            risk.setProrata("P");
            if(riskData.getPremium()!=null && riskData.getPremium().compareTo(BigDecimal.ZERO) > 0){
                risk.setButchargePrem(riskData.getPremium());
            }
            Iterable<SubclassCertTypes> subclassCertTypes  = subclassCertTypesRepo.findAll();
            Optional<SubclassCertTypes> certTypes = Streamable.streamOf(subclassCertTypes).findFirst();
            risk.setRiskDesc(riskData.getRiskDesc());
            risk.setRiskShtDesc(riskData.getRiskShtDesc());
            risk.setTransType("NB");
            RiskTrans savedRisk = riskTransRepo.save(risk);
            List<RiskDocs> riskDocs = new ArrayList<>();
            List<UwDocsDTO> docsDTOS = riskData.getDocuments();
            Iterable<SubClassReqdDocs> subClassReqDocs = subclassReqDocRepo.findAll(QSubClassReqdDocs.subClassReqdDocs.subclass.subId.eq(savedRisk.getBinderDetails().getSubCoverTypes().getSubclass().getSubId())
                    .and(QSubClassReqdDocs.subClassReqdDocs.mandatory.eq(true))
                    .and(QSubClassReqdDocs.subClassReqdDocs.requiredDoc.appliesNewBusiness.eq(true)));
            for (SubClassReqdDocs reqdDoc : subClassReqDocs) {
//                if (riskDocsRepo.count(QRiskDocs.riskDocs.risk.riskIdentifier.eq(savedRisk.getRiskIdentifier()).and(QRiskDocs.riskDocs.reqdDocs.sclReqrdId.eq(reqdDoc.getSclReqrdId()))) == 0
//                ) {
                        Optional<UwDocsDTO> docsDTO = docsDTOS.stream().filter(a -> a.getDocId().equalsIgnoreCase(reqdDoc.getRequiredDoc().getReqShtDesc())).findFirst();
                        RiskDocs riskDoc = new RiskDocs();
                        riskDoc.setReqdDocs(reqdDoc);
                        riskDoc.setRisk(savedRisk);
                        docsDTO.ifPresent(uwDocsDTO -> riskDoc.setUrl(uwDocsDTO.getDocUrl()));
                        riskDocs.add(riskDoc);
//                }
            }
            riskDocsRepo.save(riskDocs);

            long riskIdentifier = Long.valueOf(String.valueOf(dateUtils.getUwYear(wef)) + String.valueOf(savedRisk.getRiskId()));
            PolicyActiveRisks activeRisk = new PolicyActiveRisks();
            activeRisk.setPolicy(savedPolicy);
            activeRisk.setRisk(savedRisk);
            activeRisk.setRiskIdentifier(riskIdentifier);
            activeRisksRepo.save(activeRisk);

            risk.setRiskIdentifier(riskIdentifier);
            riskTransRepo.save(risk);

            if(certTypes.isPresent()){
                final RiskCertForm riskCertForm =new RiskCertForm();
                riskCertForm.setSubclasscertId(certTypes.get().getSubclasscertId());
                riskCertForm.setWefDate(risk.getWefDate());
                riskCertForm.setWetDate(risk.getWetDate());
                riskCertForm.setRiskId(risk.getRiskId());
                try {
                    certService.createRiskCert(riskCertForm);
                }
                catch (BadRequestException ex){
                    ex.printStackTrace();
                    System.out.println("Error creating cert....");
                }
            }


            List<SectionTrans> sectionTransactions = new ArrayList<>();
            if(riskData.getWindscreen()!=null && riskData.getWindscreen().compareTo(BigDecimal.ZERO)>0){
                SectionsDef sectionsDef = sectionRepo.findOne(QSectionsDef.sectionsDef.shtDesc.eq("WINDSCREEN"));
                if(sectionsDef==null){
                    throw new BadRequestException("Error getting Configuration Mapping for Windscreen. Contact Administrator");
                }
                SectionTrans section = new SectionTrans();
                section.setAmount(riskData.getWindscreen());
                section.setCompute(true);
                List<PremRatesDef> premRates = premRatesRepo.getSectPremiumRates(risk.getBinderDetails().getDetId(), sectionsDef.getId());
                if (premRates.size() == 1) {
                    section.setPremRates(premRates.get(0));
                    section.setRate(premRates.get(0).getRate());
                    section.setDivFactor(premRates.get(0).getDivFactor());
                    section.setFreeLimit(premRates.get(0).getFreeLimit());
                }
                else{
                    throw new BadRequestException("Unable to get Premium rates for Windscreen..."+premRates.size());
                }
                section.setCompute(true);
                section.setSection(sectionsDef);
                section.setRisk(risk);
                sectionTransactions.add(section);
            }
            if(riskData.getRadio()!=null && riskData.getRadio().compareTo(BigDecimal.ZERO)>0){
                SectionsDef sectionsDef = sectionRepo.findOne(QSectionsDef.sectionsDef.shtDesc.eq("ENTERTAINMENT"));
                if(sectionsDef==null){
                    throw new BadRequestException("Error getting Configuration Mapping for Entertainment. Contact Administrator");
                }
                SectionTrans section = new SectionTrans();
                section.setAmount(riskData.getRadio());
                section.setCompute(true);
                List<PremRatesDef> premRates = premRatesRepo.getSectPremiumRates(risk.getBinderDetails().getDetId(), sectionsDef.getId());
                if (premRates.size() == 1) {
                    section.setPremRates(premRates.get(0));
                    section.setRate(premRates.get(0).getRate());
                    section.setDivFactor(premRates.get(0).getDivFactor());
                    section.setFreeLimit(premRates.get(0).getFreeLimit());
                }
                else{
                    throw new BadRequestException("Unable to get Premium rates for Entertainment..."+premRates.size());
                }
                section.setCompute(true);
                section.setSection(sectionsDef);
                section.setRisk(risk);
                sectionTransactions.add(section);
            }
            if(riskData.getSumInsured()!=null && riskData.getSumInsured().compareTo(BigDecimal.ZERO)>0){
                SectionsDef sectionsDef = sectionRepo.findOne(QSectionsDef.sectionsDef.shtDesc.eq("SUM INSURED"));
                if(sectionsDef==null){
                    throw new BadRequestException("Error getting Configuration Mapping for Value of Vehicle. Contact Administrator");
                }
                SectionTrans section = new SectionTrans();
                section.setAmount(riskData.getSumInsured());
                section.setCompute(true);
                List<PremRatesDef> premRates = premRatesRepo.getSectPremiumRates(risk.getBinderDetails().getDetId(), sectionsDef.getId());
                if (premRates.size() == 1) {
                    section.setPremRates(premRates.get(0));
                    section.setRate(premRates.get(0).getRate());
                    section.setDivFactor(premRates.get(0).getDivFactor());
                    section.setFreeLimit(premRates.get(0).getFreeLimit());
                }
                else{
                    throw new BadRequestException("Unable to get Premium rates for Sum Insured..."+premRates.size());
                }
                section.setCompute(true);
                section.setSection(sectionsDef);
                section.setRisk(risk);
                sectionTransactions.add(section);
            }
            sectionTransRepo.save(sectionTransactions);

            if(riskData.getVehicle()!=null) {
                VehicleDetails details = riskData.getVehicle();
                final MotorVehicleDetails motorVehicleDetails = new MotorVehicleDetails();
                motorVehicleDetails.setBodyColor(details.getBodyColor());
                motorVehicleDetails.setBodyType("Saloon");
                motorVehicleDetails.setCarModel(details.getCarModel());
                motorVehicleDetails.setCarMake(details.getCarMake());
                motorVehicleDetails.setCarryCapacity(details.getCarryCapacity());
                motorVehicleDetails.setChassisNumber(details.getChassisNo());
                motorVehicleDetails.setEngineCapacity(details.getEngineCapacity());
                if (details.getYearOfManufacture() != null)
                    motorVehicleDetails.setYearOfManufacture(Long.parseLong(details.getYearOfManufacture()));
                motorVehicleDetails.setLogbookNumber(details.getLogbookNumber());
                motorVehicleDetails.setRisk(risk);
                motorVehicleDetailsRepo.save(motorVehicleDetails);
            }
        }

        try {
            premComputeService.computePrem(savedPolicy.getPolicyId());
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }
        final PaymentInfo paymentInfo = policyData.getPaymentInfo();
        final WebServiceReceipts webServiceReceipts = new WebServiceReceipts();
        webServiceReceipts.setAmountPaid(paymentInfo.getAmountPaid());
        webServiceReceipts.setReceipted("N");
        webServiceReceipts.setPayeeName(paymentInfo.getPayeeName());
        webServiceReceipts.setPaymentRef(paymentInfo.getPaymentRef());
        webServiceReceipts.setPaymentMode(paymentInfo.getPaymentMode());
        webServiceReceiptRepo.save(webServiceReceipts);

        workflowService.startNewWorkFlowRest(DocType.GEN_UW_DOCUMENT, String.valueOf(savedPolicy.getPolicyId()), savedPolicy, "N", null, null, null,user.getUsername(),savedPolicy.getClient());

//        workflowService.startNewWorkFlowRest(DocType.GEN_UW_DOCUMENT, String.valueOf(savedPolicy.getPolicyId()), savedPolicy, "N", null, null, null,user.getUsername());


        final ResponseObject responseObject = new ResponseObject();
        responseObject.setRefcode(policyTrans.getPolicyId());
        responseObject.setPolicyno(policyNumber);
        responseObject.setMessage("Policy Created successfully");
        responseObject.setSuccess(true);
        return responseObject;
    }

    @Override
    public ClaimResponseObject createClaim(CreateClaimModel claimModel) throws BadRequestException {
        System.out.println("Claim Request Received..."+new Gson().toJson(claimModel));
        if(claimModel.getLossDate()==null || claimModel.getNotificationDate()==null)
            throw new BadRequestException("You cannot create A Claim without Loss Date or Notification Date");
          Date notDate = null;
        try {
            notDate = new SimpleDateFormat("dd/MM/yyyy").parse(claimModel.getNotificationDate());
        } catch (ParseException e) {
            throw new BadRequestException("Invalid Notification Date. The Date must be in format dd/MM/yyyy");
        }
        Date lossDate = null;
        try {
            lossDate = new SimpleDateFormat("dd/MM/yyyy").parse(claimModel.getLossDate());
        } catch (ParseException e) {
            throw new BadRequestException("Invalid Loss Date. The Date must be in format dd/MM/yyyy");
        }
        if(notDate.after(new Date())){
            throw new BadRequestException("The date you are adviced of the claim cannot be after Today's Date");
        }

        if(lossDate.after(new Date())){
            throw new BadRequestException("The loss date of the claim cannot be after Today's Date");
        }

        if(claimModel.getRegistrationNumber()==null){
            throw new BadRequestException("The Registration number is mandatory....");
        }
        final UserDTO user = userRepository.findByUserName("Admin");
        if(user==null){
            throw new BadRequestException("Unable to get Admin user to auto create claims");
        }


        List<Object[]> riskIds = riskTransRepo.getPolicyRisks(claimModel.getRegistrationNumber().toUpperCase(), lossDate);
        if(riskIds.isEmpty()){
            throw new BadRequestException("Invalid Risk Id...Please select a valid risk id for the valid loss date...");
        }
        final RiskTrans riskTrans = riskTransRepo.findOne( ((BigDecimal)riskIds.get(0)[0]).longValue());


        Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("CL");
        if (sequenceRepo.count(seqPredicate) == 0)
            throw new BadRequestException("Sequence for Claims Transactions has not been defined");
        String claimNumberFormat = paramService.getParameterString("CLAIM_NO_FORMAT");


        // final String claimNumber = sequence.getSeqPrefix() + String.format("%05d", seqNumber);
        long clmCount = claimsBookingRepo.count(QClaimBookings.claimBookings.risk.riskId.eq(riskTrans.getRiskId()).and(QClaimBookings.claimBookings.lossDate.eq(lossDate)));
        if(clmCount > 0) throw new BadRequestException("Another claim already exists for the loss date selected");
        SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
        Long seqNumber = sequence.getNextNumber();
        final String claimNumber = templateMerger.generateFormat(claimNumberFormat,
                riskTrans.getPolicy().getBranch().getObId(),
                riskTrans.getPolicy().getProduct().getProCode(),
                lossDate,
                sequence.getSeqPrefix() + String.format("%05d", seqNumber),
                riskTrans);
        sequence.setLastNumber(seqNumber);
        sequence.setNextNumber(seqNumber + 1);
        sequenceRepo.save(sequence);
        ClaimBookings bookings = new ClaimBookings();
        bookings.setBookedBy(userRepository.findById(user.getId()));
        bookings.setBookedDate(new Date());
        bookings.setClaimNo(claimNumber);
        bookings.setClaimRejected(false);
        bookings.setClmDate(notDate);
        bookings.setClaimStatus("O");
        bookings.setClaimTime(new SimpleDateFormat("h:m").format(new Date()));
        bookings.setLossDate(lossDate);
        bookings.setLossDesc(claimModel.getLossDesc());
        bookings.setNextReviewDate(notDate);
        bookings.setRisk(riskTrans);
        bookings.setStatusDate(new Date());
        bookings.setActivity(clmStatusRepo.findOne(1L));
        bookings.setLiabilityAdmission(false);
        bookings.setCurrency(riskTrans.getPolicy().getTransCurrency());
        ClaimBookings booking = claimsBookingRepo.save(bookings);

        SystemTrans trans = new SystemTrans();
        trans.setTransLevel("C");
        trans.setTransCode("CLM");
        trans.setTransAuthorised("N");
        trans.setClaim(booking);
        trans.setDoneBy(userRepository.findById(user.getId()));
        trans.setDoneDate(new Date());
        SystemTrans savedTrans =  systemTransRepo.save(trans);
        final ClaimResponseObject claimResponseObject = new ClaimResponseObject();
        claimResponseObject.setMessage("claim successful created");
        claimResponseObject.setSuccess(true);
        claimResponseObject.setClaimno(claimNumber);
        claimResponseObject.setRefcode(bookings.getClmId());
        return claimResponseObject;
    }

    private ClientDef validateAndCreateClient(ClientDTO clientDTO, OrgBranch branch) throws BadRequestException{
        if(clientDTO.getPinNo()==null){
            throw new BadRequestException("Pin Number is Mandatory...");
        }
        if(clientDTO.getClientName()==null){
            throw new BadRequestException("Client Name is Mandatory...");
        }

        if(clientDTO.getEmailAddress()==null){
            throw new BadRequestException("Email Address is Mandatory...");
        }

        if(clientDTO.getClientType()==null){
            throw new BadRequestException("Client Type is Mandatory...");
        }
        if(clientDTO.getPhoneNo()==null){
            throw new BadRequestException("Client phone Number is Mandatory...");
        }
        List<String> clientTypes = new ArrayList<>();
        clientTypes.add("I");
        clientTypes.add("C");
        final UserDTO user = userRepository.findByUserName("Admin");

        if(!clientTypes.contains(clientDTO.getClientType())){
            throw new BadRequestException("Client Type Must be either I for Individual or C for Corporate");
        }
        if(clientDTO.getClientType().equalsIgnoreCase("I")){
            if(clientDTO.getIdNo()==null){
                throw new BadRequestException("Id Number is Mandatory...");
            }
            if(clientDTO.getGender()==null){
                throw new BadRequestException("Gender is Mandatory for Individual Clients...");
            }
            List<String> genderList = new ArrayList<>();
            genderList.add("F");
            genderList.add("M");
            genderList.add("O");
            if(!genderList.contains(clientDTO.getGender()))
            {
                throw new BadRequestException("Gender must be either F for Female, M for Male or O for Others");
            }
            if(clientDTO.getDateOfBirth()==null){
                throw new BadRequestException("Date of Birth is Mandatory for individual clients");
            }
            List<ClientDef> clientDefs = clientRepository.findClientDefByIdNo(clientDTO.getIdNo());
            if(!clientDefs.isEmpty()){
                return clientDefs.get(0);
            }
             clientDefs = clientRepository.findClientDefByPinNo(clientDTO.getPinNo());
            if(!clientDefs.isEmpty()){
                return clientDefs.get(0);
            }
            clientDefs = clientRepository.findByEmailAddress(clientDTO.getEmailAddress());
            if(!clientDefs.isEmpty()){
                return clientDefs.get(0);
            }
            final ClientDef clientDef = new ClientDef();
            final String[] names = clientDTO.getClientName().split(" ");
            if(names.length==1){
                throw new BadRequestException("Individual client must have two names at least separated by space");
            }
            Date dob = null;
            try{
                dob = new SimpleDateFormat("dd/MM/yyyy").parse(clientDTO.getDateOfBirth());
            }
            catch (ParseException ex){
                throw new BadRequestException("Invalid Date format for the Date of Birth. The format should be dd/MM/yyyy");
            }
            if(dob==null || DateUtilities.computeAge(dob)> 80)  {
                throw new BadRequestException("Invalid Date format for the Date of Birth. The format should be dd/MM/yyyy");
            }
            if(DateUtilities.computeAge(dob) < 18){
                throw new BadRequestException("Individual Client must be 18 years or older");
            }
            Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("C");
            if (sequenceRepo.count(seqPredicate) == 0)
                throw new BadRequestException("Sequence for Client Definition has not been setup");
            SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
            Long seqNumber = sequence.getNextNumber();
            final String clientNumber = sequence.getSeqPrefix() + String.format("%06d", seqNumber);
            sequence.setLastNumber(seqNumber);
            sequence.setNextNumber(seqNumber + 1);
            sequenceRepo.save(sequence);
            clientDef.setTenantNumber(clientNumber);
            clientDef.setFname(names[0]);
            clientDef.setOtherNames(names[1]);
            clientDef.setEmailAddress(clientDTO.getEmailAddress());
            clientDef.setRegisteredbrn(branch);
            clientDef.setPinNo(clientDTO.getPinNo());
            clientDef.setTenantType(clientTypeRepo.findOne(QClientTypes.clientTypes.clientType.eq(clientDTO.getClientType())));
            clientDef.setGender(clientDTO.getGender());
            clientDef.setDateCreated(new Date());
            clientDef.setDateregistered(new Date());
            clientDef.setPhoneNo(clientDTO.getPhoneNo());
            clientDef.setStatus("A");
            clientDef.setCreatedBy(userRepository.findById(user.getId()));
            clientDef.setAuthStatus("Y");
            clientDef.setAuthBy(userRepository.findById(user.getId()));
            clientDef.setDob(dob);
            return clientRepository.save(clientDef);
        }
        List<ClientDef> clientDefs = clientRepository.findClientDefByPinNo(clientDTO.getPinNo());
        if(!clientDefs.isEmpty()){
            return clientDefs.get(0);
        }
        final ClientDef clientDef = new ClientDef();
        Predicate seqPredicate = QSystemSequence.systemSequence.transType.eq("C");
        if (sequenceRepo.count(seqPredicate) == 0)
            throw new BadRequestException("Sequence for Client Definition has not been setup");
        SystemSequence sequence = sequenceRepo.findOne(seqPredicate);
        Long seqNumber = sequence.getNextNumber();
        final String clientNumber = sequence.getSeqPrefix() + String.format("%06d", seqNumber);
        final String[] nameSplitr = clientDTO.getClientName().split("\\s+");

        sequence.setLastNumber(seqNumber);
        sequence.setNextNumber(seqNumber + 1);
        sequenceRepo.save(sequence);
        clientDef.setTenantNumber(clientNumber);
        if(nameSplitr.length == 2){
           clientDef.setFname(nameSplitr[0]);
           clientDef.setOtherNames(nameSplitr[1]);
        }
        else  if(nameSplitr.length == 3){
            clientDef.setFname(nameSplitr[0]);
            clientDef.setOtherNames(nameSplitr[1] +nameSplitr[2] );
        }
        else {
            clientDef.setFname(clientDTO.getClientName());
        }
        clientDef.setPinNo(clientDTO.getPinNo());
        clientDef.setPhoneNo(clientDTO.getPhoneNo());
        clientDef.setRegisteredbrn(branch);
        clientDef.setTenantType(clientTypeRepo.findOne(QClientTypes.clientTypes.clientType.eq(clientDTO.getClientType())));
        clientDef.setDateCreated(new Date());
        clientDef.setDateregistered(new Date());
        clientDef.setPhoneNo(clientDTO.getPhoneNo());
        clientDef.setCreatedBy(userRepository.findById(user.getId()));
        clientDef.setAuthStatus("Y");
        clientDef.setStatus("A");
        clientDef.setAuthBy(userRepository.findById(user.getId()));
        return clientRepository.save(clientDef);
    }


    private List<Product> findMedicalProducts(Long bindId) {
        Iterable<BindersDef>  bindersDefs = bindersRepo.findAll(QBindersDef.bindersDef.binId.eq(bindId));
        List<Product> products = new ArrayList<>();
        for(BindersDef bindersDef:bindersDefs) {
            Iterable<BinderDetails> binderDetails = binderDetRepo.findAll(QBinderDetails.binderDetails.binder.binId.eq(bindersDef.getBinId()).and(QBinderDetails.binderDetails.primary.eq("Y")));
            Optional<BinderDetails> binderDetails1 = Streamable.streamOf(binderDetails).findFirst();
            if (binderDetails1.isPresent()) {
                BinderDetails details = binderDetails1.get();
               long detId = details.getDetId();
                List<MedicalDTO> medicalBeans = excelUtils.getMedicalBinders(detId);
                List<String> covers = medicalBeans.stream().map(a -> a.getCover()).distinct().collect(Collectors.toList());
                System.out.println(covers);
                String subclass = details.getSubCoverTypes().getSubclass().getSubDesc().toLowerCase();
                for (String cover : covers) {
                    Product product = new Product();
                    product.setName(bindersDef.getBinName()+" - "+ cover);
                    product.setId(BigDecimal.valueOf(bindersDef.getBinId()));
                    Cover_Type cover_type = new Cover_Type();
                    if(cover.toLowerCase().indexOf("outpatient")!=-1){
                        cover_type.setPrimary(false);
                        cover_type.setName(cover);
                        cover_type.setBenefit(false);
                        cover_type.setOptional_benefit(true);
                    }
                    else{
                        cover_type.setPrimary(true);
                        cover_type.setName(cover);
                        cover_type.setBenefit(false);
                        cover_type.setOptional_benefit(false);
                    }
                    cover_type.setId(BigDecimal.valueOf(details.getDetId()));
                    Insurance_type insurance_type = new Insurance_type();
                    insurance_type.setName("Health Insurance");
                    insurance_type.setAlias(subclass);
                    insurance_type.setId(BigDecimal.valueOf(details.getSubCoverTypes().getScId()));
                    cover_type.setInsurance_type(insurance_type);
                    product.setCover_type(cover_type);
                    Iterable<BinderClauses> clauses = clausesRepo.findAll(QBinderClauses.binderClauses.binderDet.binder.binId.eq(bindersDef.getBinId())
                            .and(QBinderClauses.binderClauses.clause.clause.clauseType.eq("L")));
                    List<Benefits> benefits = new ArrayList<>();
                    for (BinderClauses binderClauses : clauses) {
                        Benefits benefit = new Benefits();
                        benefit.setId(BigDecimal.valueOf(binderClauses.getClauId()));
                        benefit.setName(binderClauses.getClauHeading());
                        benefit.setLimit(binderClauses.getClause().getClause().getClauWording());
                        Cover_Type covertype = new Cover_Type();
                        covertype.setBenefit(true);
                        covertype.setPrimary(false);
                        covertype.setOptional_benefit(false);
                        covertype.setId(BigDecimal.valueOf(binderClauses.getClause().getClauId()));
                        covertype.setName(binderClauses.getClauHeading());
                        benefit.setCoverType(covertype);
                        benefits.add(benefit);
                    }
                    product.setBenefits(benefits);
                   List<MedicalDTO> coverDtos =   medicalBeans.stream().filter(a -> a.getCover().equalsIgnoreCase(cover)).collect(Collectors.toList());

                    List<Binders> bindersList = new ArrayList<>();
                    for(MedicalDTO medicalDTO:coverDtos) {
                        Binders binders = new Binders();
                        binders.setName(medicalDTO.getName());
                        Underwriter underwriter = new Underwriter();
                        underwriter.setId(BigDecimal.valueOf(bindersDef.getAccount().getAcctId()));
                        underwriter.setName(bindersDef.getAccount().getName());
                        binders.setUnderwriter(underwriter);
                        binders.setCountry_id(BigDecimal.ONE);
                        binders.setCover_limit(medicalDTO.getCoverLimit());
                        binders.setFlat_rate(BigDecimal.valueOf(medicalDTO.getPremium()));
                        binders.setMaximum_age_years(BigDecimal.valueOf(medicalDTO.getMaxAgeYears()));
                        binders.setMinimum_age_years(BigDecimal.valueOf(medicalDTO.getMinAgeYears()));
                        binders.setMaximum_age_months(BigDecimal.valueOf(medicalDTO.getMaxAgeMonths()));
                        binders.setMinimum_age_months(BigDecimal.valueOf(medicalDTO.getMinAgeMonths()));
                        binders.setMaximum_joining_age_years(BigDecimal.valueOf(medicalDTO.getMaxJoinAgeYears()));
                        binders.setMaximum_joining_age_months(BigDecimal.valueOf(medicalDTO.getMaxJoinAgeYears()*12));

                        bindersList.add(binders);
                        product.setBinders(bindersList);

                    }
                    products.add(product);

                }

            }
        }
        return products;
    }

    @Override
    public IntProducts findProducts() {
        Iterable<BindersDef>  bindersDefs = bindersRepo.findAll(QBindersDef.bindersDef.binId.eq(748l));
        List<Product> products = new ArrayList<>();
        for(BindersDef bindersDef:bindersDefs) {
            boolean medical = false;
            Product product = new Product();
            product.setName(bindersDef.getBinName());
            product.setId(BigDecimal.valueOf(bindersDef.getBinId()));
            boolean motor = bindersDef.getProduct().isMotorProduct();
            boolean isMedical = "MD".equalsIgnoreCase(bindersDef.getProduct().getProGroup().getPrgType());


            if(isMedical){

                  List<Product> allMedical = findMedicalProducts(bindersDef.getBinId());
                  Product outp = new Product();
                  for(Product p: allMedical){
                      if(p.getName().toLowerCase().indexOf("outpatient")!=-1){
                          outp = p;
                      }


                  }
                allMedical.remove(outp);
                System.out.println(allMedical);
                List<Product> optionalProducts = new ArrayList<>();
                  optionalProducts.add(outp);
                  for(Product product1:optionalProducts){
                      List<Binders> bindersd = product1.getBinders();
                      List<Binders> newBinders = new ArrayList<>();
                      if(bindersd.size()>0) {
                          newBinders.add(bindersd.get(0));
                          product1.setBinders(newBinders);
                      }
                  }
                   for(Product p:allMedical){
                       List<Binders> binderss = p.getBinders();
                       for(Binders bb:binderss){
                           bb.setOptional_benefits(optionalProducts);
                       }
                   }
                  products.addAll( allMedical);

            }
            if(!isMedical) {
                Iterable<BinderDetails> binderDetails = binderDetRepo.findAll(QBinderDetails.binderDetails.binder.binId.eq(bindersDef.getBinId()).and(QBinderDetails.binderDetails.primary.eq("Y")));
                Optional<BinderDetails> binderDetails1 = Streamable.streamOf(binderDetails).findFirst();
                String insuranceType = "";
                BigDecimal insuranceTypeId = BigDecimal.ONE;
                BigDecimal taxValue = BigDecimal.ZERO;
                BigDecimal rateOfSumInsured = BigDecimal.ZERO;
                String vehicleUse = "";
                BigDecimal subClassId = BigDecimal.ZERO;
                BigDecimal epId = BigDecimal.ZERO;
                BigDecimal pll = BigDecimal.ZERO;
                BigDecimal politicalViolence = BigDecimal.ZERO;
                BigDecimal politicalViolId = BigDecimal.ZERO;
                BigDecimal building = BigDecimal.ZERO;
                BigDecimal contents = BigDecimal.ZERO;
                BigDecimal portable = BigDecimal.ZERO;
                BigDecimal employeeRate = BigDecimal.ZERO;
                Long detId = 0l;


                BigDecimal excessProtector = BigDecimal.ZERO;
                boolean domesticCover = false;

                List<Product> optionalBenefits = new ArrayList<>();
                if (binderDetails1.isPresent()) {
                    BinderDetails details = binderDetails1.get();
                    if ("MD".equalsIgnoreCase(bindersDef.getProduct().getProGroup().getPrgType())) {
                        detId = details.getDetId();
                        medical = true;
                    }


                    CoverTypesDef coverTypesDef = details.getSubCoverTypes().getCoverTypes();
                    Cover_Type cover_type = new Cover_Type();
                    cover_type.setPrimary(true);
                    cover_type.setName(coverTypesDef.getCovName());
                    cover_type.setBenefit(false);
                    cover_type.setOptional_benefit(false);
                    cover_type.setId(BigDecimal.valueOf(details.getDetId()));
                    String subclass = details.getSubCoverTypes().getSubclass().getSubDesc().toLowerCase();
//                System.out.println("Sub Class "+subclass);
                    if (subclass.indexOf("domestic") != -1) {
                        Insurance_type insurance_type = new Insurance_type();
                        insurance_type.setName("Domestic Insurance");
                        insurance_type.setAlias(insuranceType);
                        insurance_type.setId(insuranceTypeId);
                        cover_type.setInsurance_type(insurance_type);
                        domesticCover = true;
                        boolean isBuildingAvail = Streamable.streamOf(premRatesRepo.findAll(QPremRatesDef.premRatesDef.binderDet.detId.eq(details.getDetId())
                                .and(QPremRatesDef.premRatesDef.section.shtDesc.eq("Section A")))).findAny().isPresent();
                        if (isBuildingAvail) {
                            building = Streamable.streamOf(premRatesRepo.findAll(QPremRatesDef.premRatesDef.binderDet.detId.eq(details.getDetId())
                                    .and(QPremRatesDef.premRatesDef.section.shtDesc.eq("Section A")))).findAny().get().getRate();
                        }
                        boolean isContentAvail = Streamable.streamOf(premRatesRepo.findAll(QPremRatesDef.premRatesDef.binderDet.detId.eq(details.getDetId())
                                .and(QPremRatesDef.premRatesDef.section.shtDesc.eq("Section B")))).findAny().isPresent();
                        if (isContentAvail) {
                            contents = Streamable.streamOf(premRatesRepo.findAll(QPremRatesDef.premRatesDef.binderDet.detId.eq(details.getDetId())
                                    .and(QPremRatesDef.premRatesDef.section.shtDesc.eq("Section B")))).findAny().get().getRate();
                        }
                        boolean isPortable = Streamable.streamOf(premRatesRepo.findAll(QPremRatesDef.premRatesDef.binderDet.detId.eq(details.getDetId())
                                .and(QPremRatesDef.premRatesDef.section.shtDesc.eq("Section C")))).findAny().isPresent();
                        if (isPortable) {
                            portable = Streamable.streamOf(premRatesRepo.findAll(QPremRatesDef.premRatesDef.binderDet.detId.eq(details.getDetId())
                                    .and(QPremRatesDef.premRatesDef.section.shtDesc.eq("Section C")))).findAny().get().getRate();
                        }
                        boolean isEmployeeRate = Streamable.streamOf(premRatesRepo.findAll(QPremRatesDef.premRatesDef.binderDet.detId.eq(details.getDetId())
                                .and(QPremRatesDef.premRatesDef.section.shtDesc.eq("Section D")))).findAny().isPresent();
                        if (isEmployeeRate) {
                            employeeRate = Streamable.streamOf(premRatesRepo.findAll(QPremRatesDef.premRatesDef.binderDet.detId.eq(details.getDetId())
                                    .and(QPremRatesDef.premRatesDef.section.shtDesc.eq("Section D")))).findAny().get().getRate();
                        }
                    }
                    if (subclass.indexOf("medical") != -1) {
                        Insurance_type insurance_type = new Insurance_type();
                        insurance_type.setName("Health Insurance");
                        insurance_type.setAlias(subclass);
                        insurance_type.setId(insuranceTypeId);
                        cover_type.setInsurance_type(insurance_type);
                        medical = true;
                    }


                    taxValue = Streamable.streamOf(taxRatesRepo.findAll(QTaxRates.taxRates.subclass.subId.eq(details.getSubCoverTypes().getSubclass().getSubId()).
                            and(QTaxRates.taxRates.revenueItems.item.in(RevenueItems.PHCF, RevenueItems.TL))
                            .and(QTaxRates.taxRates.productsDef.proCode.eq(bindersDef.getProduct().getProCode())))).map(a -> a.getTaxRate()).reduce(BigDecimal.ZERO, BigDecimal::add);

                    boolean epAvailable = Streamable.streamOf(premRatesRepo.findAll(QPremRatesDef.premRatesDef.binderDet.detId.eq(details.getDetId())
                            .and(QPremRatesDef.premRatesDef.section.shtDesc.eq("EP")))).findAny().isPresent();
                    if (epAvailable) {
                        excessProtector = Streamable.streamOf(premRatesRepo.findAll(QPremRatesDef.premRatesDef.binderDet.detId.eq(details.getDetId())
                                .and(QPremRatesDef.premRatesDef.section.shtDesc.eq("EP")))).findAny().get().getRate();

                        epId = BigDecimal.valueOf(Streamable.streamOf(premRatesRepo.findAll(QPremRatesDef.premRatesDef.binderDet.detId.eq(details.getDetId())
                                .and(QPremRatesDef.premRatesDef.section.shtDesc.eq("EP")))).findAny().get().getId());
                    }

                    boolean politicalViolenceAvail = Streamable.streamOf(premRatesRepo.findAll(QPremRatesDef.premRatesDef.binderDet.detId.eq(details.getDetId())
                            .and(QPremRatesDef.premRatesDef.section.shtDesc.eq("Terrorism")))).findAny().isPresent();
                    if (politicalViolenceAvail) {
                        politicalViolence = Streamable.streamOf(premRatesRepo.findAll(QPremRatesDef.premRatesDef.binderDet.detId.eq(details.getDetId())
                                .and(QPremRatesDef.premRatesDef.section.shtDesc.eq("Terrorism")))).findAny().get().getRate();
                        politicalViolId = BigDecimal.valueOf(Streamable.streamOf(premRatesRepo.findAll(QPremRatesDef.premRatesDef.binderDet.detId.eq(details.getDetId())
                                .and(QPremRatesDef.premRatesDef.section.shtDesc.eq("Terrorism")))).findAny().get().getId());
                    }
                    boolean sumInsuredAvail = Streamable.streamOf(premRatesRepo.findAll(QPremRatesDef.premRatesDef.binderDet.detId.eq(details.getDetId())
                            .and(QPremRatesDef.premRatesDef.section.shtDesc.eq("Value")))).findAny().isPresent();
                    if (sumInsuredAvail)
                        rateOfSumInsured = Streamable.streamOf(premRatesRepo.findAll(QPremRatesDef.premRatesDef.binderDet.detId.eq(details.getDetId())
                                .and(QPremRatesDef.premRatesDef.section.shtDesc.eq("Value")))).findAny().get().getRate();

                    boolean pllAvailable = Streamable.streamOf(premRatesRepo.findAll(QPremRatesDef.premRatesDef.binderDet.detId.eq(details.getDetId())
                            .and(QPremRatesDef.premRatesDef.section.shtDesc.eq("PLL")))).findAny().isPresent();
                    if (pllAvailable)
                        pll = Streamable.streamOf(premRatesRepo.findAll(QPremRatesDef.premRatesDef.binderDet.detId.eq(details.getDetId())
                                .and(QPremRatesDef.premRatesDef.section.shtDesc.eq("PLL")))).findAny().get().getRate();

                    subClassId = BigDecimal.valueOf(details.getSubCoverTypes().getSubclass().getSubId());

                    if (motor) {
                        insuranceType = details.getSubCoverTypes().getSubclass().getSubDesc();
                        insuranceTypeId = BigDecimal.valueOf(details.getSubCoverTypes().getScId());
                        vehicleUse = details.getSubCoverTypes().getSubclass().getSubDesc();
                        Insurance_type insurance_type = new Insurance_type();
                        insurance_type.setName("Vehicle Insurance");
                        insurance_type.setAlias(details.getSubCoverTypes().getSubclass().getSubDesc());
                        insurance_type.setId(BigDecimal.valueOf(details.getSubCoverTypes().getScId()));
                        cover_type.setInsurance_type(insurance_type);
                    }
                    product.setCover_type(cover_type);
                }
                Iterable<BinderClauses> clauses = clausesRepo.findAll(QBinderClauses.binderClauses.binderDet.binder.binId.eq(bindersDef.getBinId())
                        .and(QBinderClauses.binderClauses.clause.clause.clauseType.eq("L")));
                List<Benefits> benefits = new ArrayList<>();
                for (BinderClauses binderClauses : clauses) {
                    Benefits benefit = new Benefits();
                    benefit.setId(BigDecimal.valueOf(binderClauses.getClauId()));
                    benefit.setName(binderClauses.getClauHeading());
                    benefit.setLimit(binderClauses.getClause().getClause().getClauWording());
                    Cover_Type cover_type = new Cover_Type();
                    cover_type.setBenefit(true);
                    cover_type.setPrimary(false);
                    cover_type.setOptional_benefit(false);
                    cover_type.setId(BigDecimal.valueOf(binderClauses.getClause().getClauId()));
                    cover_type.setName(binderClauses.getClauHeading());
                    if (motor) {
                        Insurance_type insurance_type = new Insurance_type();
                        insurance_type.setName("Vehicle Insurance");
                        insurance_type.setAlias(insuranceType);
                        insurance_type.setId(insuranceTypeId);
                        cover_type.setInsurance_type(insurance_type);
                    }
                    benefit.setCoverType(cover_type);
                    benefits.add(benefit);
                }
                product.setBenefits(benefits);
                products.add(product);
                List<Binders> bindersList = new ArrayList<>();
                if (medical) {
//                List<MedicalDTO> medicalBeans = excelUtils.getMedicalBinders(detId);
//                for(MedicalDTO medicalDTO:medicalBeans) {
//                    Binders binders = new Binders();
//                    binders.setName(medicalDTO.getName());
//                    Underwriter underwriter = new Underwriter();
//                    underwriter.setId(BigDecimal.valueOf(bindersDef.getAccount().getAcctId()));
//                    underwriter.setName(bindersDef.getAccount().getName());
//                    binders.setUnderwriter(underwriter);
//                    binders.setCountry_id(BigDecimal.ONE);
//                    binders.setCover_limit(medicalDTO.getCoverLimit());
//                    binders.setFlat_rate(BigDecimal.valueOf(medicalDTO.getPremium()));
//                    binders.setMaximum_age_years(BigDecimal.valueOf(medicalDTO.getMaxAgeYears()));
//                    binders.setMinimum_age_years(BigDecimal.valueOf(medicalDTO.getMinAgeYears()));
//                    binders.setMaximum_age_months(BigDecimal.valueOf(medicalDTO.getMaxAgeMonths()));
//                    binders.setMinimum_age_months(BigDecimal.valueOf(medicalDTO.getMinAgeMonths()));
//                    bindersList.add(binders);
//                    product.setBinders(bindersList);
//                }

                } else {
                    Binders binders = new Binders();
                    binders.setName(bindersDef.getBinName());
                    Underwriter underwriter = new Underwriter();
                    underwriter.setId(BigDecimal.valueOf(bindersDef.getAccount().getAcctId()));
                    underwriter.setName(bindersDef.getAccount().getName());
                    binders.setUnderwriter(underwriter);
                    binders.setLevies_rate_of_total_premiums(taxValue);
                    binders.setRate_of_sum_insured(rateOfSumInsured);
                    binders.setCountry_id(BigDecimal.ONE);
                    if (excessProtector.compareTo(BigDecimal.ZERO) == 1) {
                        Product ep = new Product();
                        ep.setId(epId);
                        ep.setName("Excess Protection");
                        Cover_Type cover_type = new Cover_Type();
                        cover_type.setBenefit(false);
                        cover_type.setPrimary(false);
                        cover_type.setOptional_benefit(true);
                        cover_type.setId(epId);
                        cover_type.setName("Excess Protection");
                        if (motor) {
                            Insurance_type insurance_type = new Insurance_type();
                            insurance_type.setName("Vehicle Insurance");
                            insurance_type.setAlias(insuranceType);
                            insurance_type.setId(insuranceTypeId);
                            cover_type.setInsurance_type(insurance_type);
                        }
                        ep.setCover_type(cover_type);
                        List<Binders> epBinders = new ArrayList<>();
                        Binders epBinder = new Binders();
                        epBinder.setName("Excess Protection");
                        epBinder.setUnderwriter(underwriter);
                        epBinder.setRate_of_sum_insured(excessProtector);
                        epBinder.setVehicle_uses(new ArrayList<>());
                        epBinder.setOptional_benefits(new ArrayList<>());
                        epBinders.add(epBinder);
                        ep.setBinders(epBinders);
                        ep.setBenefits(new ArrayList<>());
                        optionalBenefits.add(ep);
                    }
                    if (motor) {
                        if (politicalViolence.compareTo(BigDecimal.ZERO) == 1) {
                            Product ep = new Product();
                            ep.setId(politicalViolId);
                            ep.setName("Political violence and terrorism");
                            Cover_Type cover_type = new Cover_Type();
                            cover_type.setBenefit(false);
                            cover_type.setPrimary(false);
                            cover_type.setOptional_benefit(true);
                            cover_type.setId(politicalViolId);
                            cover_type.setName("Political violence and terrorism");
                            if (motor) {
                                Insurance_type insurance_type = new Insurance_type();
                                insurance_type.setName("Vehicle Insurance");
                                insurance_type.setAlias(insuranceType);
                                insurance_type.setId(insuranceTypeId);
                                cover_type.setInsurance_type(insurance_type);
                            }
                            ep.setCover_type(cover_type);
                            List<Binders> epBinders = new ArrayList<>();
                            Binders epBinder = new Binders();
                            epBinder.setName("Political violence and terrorism");
                            epBinder.setUnderwriter(underwriter);
                            epBinder.setRate_of_sum_insured(politicalViolence);
                            epBinder.setVehicle_uses(new ArrayList<>());
                            epBinder.setOptional_benefits(new ArrayList<>());
                            epBinders.add(epBinder);
                            ep.setBinders(epBinders);
                            ep.setBenefits(new ArrayList<>());
                            optionalBenefits.add(ep);
                        }
                    }
                    binders.setOptional_benefits(optionalBenefits);
                    if (motor) {
                        binders.setPassenger_legal_liability(pll);

                        String oldVehicleUse = vehicleUse;
                        vehicleUse = vehicleUse.toLowerCase();

                        if (vehicleUse.indexOf("private") != -1) {
                            List<Vehicle_use> vehicle_uses = new ArrayList<>();
                            Vehicle_use vehicle_use = new Vehicle_use();
                            vehicle_use.setId(subClassId);
                            vehicle_use.setAlias(oldVehicleUse);
                            vehicle_use.setName("Private");
                            vehicle_uses.add(vehicle_use);
                            binders.setVehicle_uses(vehicle_uses);
                        } else if (vehicleUse.indexOf("commercial") != -1) {
                            List<Vehicle_use> vehicle_uses = new ArrayList<>();
                            Vehicle_use vehicle_use = new Vehicle_use();
                            vehicle_use.setId(subClassId);
                            vehicle_use.setAlias(oldVehicleUse);
                            vehicle_use.setName("CV Own Goods");
                            vehicle_uses.add(vehicle_use);
                            binders.setVehicle_uses(vehicle_uses);
                        }
                    }
                    binders.setAllow_other_providers(false);
                    binders.setSpouse(false);
                    binders.setRecommended(false);
                    binders.setShared(false);
                    binders.setDeductible_min(BigDecimal.ZERO);
                    binders.setMaximum_age_years(BigDecimal.ZERO);
                    binders.setWar_risk(BigDecimal.ZERO);
                    binders.setRate_of_containerized(BigDecimal.ZERO);
                    binders.setRate_of_sum_insured_on_building(building);
                    binders.setRate_of_sum_insured_on_household_items(contents);
                    binders.setRate_of_sum_insured_on_portable_items(portable);
                    binders.setWiba_per_indoors_employee(employeeRate);
                    bindersList.add(binders);
                    product.setBinders(bindersList);
                }
            }
        }
        IntProducts intProducts = new IntProducts();
        intProducts.setProducts(products);
        return intProducts;
    }

}
