package com.brokersystems.brokerapp.life.controllers;

import com.brokersystems.brokerapp.auditlogs.DTO.AuditTrailDTO;
import com.brokersystems.brokerapp.auditlogs.model.RtsAudit;
import com.brokersystems.brokerapp.auditlogs.repositories.RtsAuditRepository;
import com.brokersystems.brokerapp.dms.service.UploadService;
import com.brokersystems.brokerapp.dms.model.UploadBean;
import com.brokersystems.brokerapp.life.dto.ReceiptAllocationCommissionsDTO;
import com.brokersystems.brokerapp.life.model.*;
import com.brokersystems.brokerapp.life.service.LifeService;
import com.brokersystems.brokerapp.mail.model.MailMessageBean;
import com.brokersystems.brokerapp.mail.model.MailTemplates;
import com.brokersystems.brokerapp.mail.service.MailTemplateService;
import com.brokersystems.brokerapp.mail.service.Mailer;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.DateUtilities;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.setup.dto.OrganizationDTO;
import com.brokersystems.brokerapp.setup.dto.ReqDocsDTO;
import com.brokersystems.brokerapp.setup.dto.UserDTO;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.BindersRepo;
import com.brokersystems.brokerapp.setup.repository.ClientRepository;
import com.brokersystems.brokerapp.setup.service.OrganizationService;
import com.brokersystems.brokerapp.trans.dtos.LifeReceiptsDTO;
import com.brokersystems.brokerapp.trans.model.TransChecks;
import com.brokersystems.brokerapp.trans.service.PolicyAuthorization;
import com.brokersystems.brokerapp.trans.service.ReceiptService;
import com.brokersystems.brokerapp.trans.utils.HibernateProxyTypeAdapter;
import com.brokersystems.brokerapp.users.dto.MakerCheckDTO;
import com.brokersystems.brokerapp.users.model.MakerChecker;
import com.brokersystems.brokerapp.users.model.QMakerChecker;
import com.brokersystems.brokerapp.users.repository.MakerCheckerRepo;
import com.brokersystems.brokerapp.users.service.MakerCheckerService;
import com.brokersystems.brokerapp.uw.dtos.CreateRiskDTO;
import com.brokersystems.brokerapp.uw.dtos.PolicyCreateDTO;
import com.brokersystems.brokerapp.uw.dtos.RiskDocsDTO;
import com.brokersystems.brokerapp.uw.dtos.RiskTransDTO;
import com.brokersystems.brokerapp.uw.model.*;
import com.brokersystems.brokerapp.uw.repository.PolicyRemarksRepo;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import com.brokersystems.brokerapp.uw.repository.RiskDocsRepo;
import com.brokersystems.brokerapp.uw.service.PolicyTransService;
import com.brokersystems.brokerapp.uw.service.PremComputeService;
import com.brokersystems.brokerapp.webservices.model.PolicyModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.brokersystems.brokerapp.users.model.QMakerChecker.makerChecker;
import static groovy.util.ObservableSet.ChangeType.newValue;
import static groovy.util.ObservableSet.ChangeType.oldValue;

/**
 * Created by waititu on 24/11/2017.
 */
@Controller
@RequestMapping({ "/protected/life/policies" })
public class LifeController {
    private static final Logger log = LoggerFactory.getLogger(LifeController.class);

    @Autowired
    private LifeService lifeService;

    @Autowired
    private PolicyRemarksRepo policyRemarksRepo;

    @Autowired
    private PolicyTransService policyService;

    @Autowired
    private ReceiptService receiptService;


    @Autowired
	private PolicyAuthorization authService;

    @Autowired
    private DateUtilities dateUtils;

    @Autowired
    private DataSource datasource;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PremComputeService premiumService;

    @Autowired
    private BindersRepo bindersRepo;

    @Autowired
    private OrganizationService orgService;

    @Autowired
    private UploadService uploadService;
    @Autowired
    private MakerCheckerRepo makerCheckerRepo;
    @Autowired
    private MakerCheckerService makerCheckerService;
    @Autowired
    private PolicyTransRepo policyTransRepo;
    @Autowired
    private Mailer mailer;
    @Autowired
    private MailTemplateService mailTemplateService;

    @Autowired
    RtsAuditRepository rtsAuditRepository;

    @Autowired
    private  UserUtils userUtils;

    @Autowired
    private RiskDocsRepo riskDocsRepo;


   @InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
    @RequestMapping(value = "lifeuwform", method = RequestMethod.GET)
    public String lifeForm(Model model, HttpServletRequest request) {
        model.addAttribute("policyId", -2000);
        model.addAttribute("policyStatus", 0);
        return "lifeuwform";
    }

    @RequestMapping(value = "investmentuwform", method = RequestMethod.GET)
    public String investmentForm(Model model, HttpServletRequest request) {
        model.addAttribute("policyId", -2000);
        model.addAttribute("policyStatus", 0);
        return "investmentuwform";
    }



    @RequestMapping(value = "edituwpolicy", method = RequestMethod.GET)
    public String edituwpolicy(Model model,HttpServletRequest request) throws BadRequestException {
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        model.addAttribute("policyId", polCode);
        PolicyTrans poltrans =policyService.getPolicyDetails(polCode);
        if ( poltrans.getAuthStatus()!=null && poltrans.getAuthStatus().equalsIgnoreCase("A")){
            model.addAttribute("policyStatus", 2);
        }else if (poltrans.getAuthStatus()!=null  && poltrans.getAuthStatus().equalsIgnoreCase("R"))
            model.addAttribute("policyStatus", 1);
        else if (poltrans.getAuthStatus()!=null  && poltrans.getAuthStatus().equalsIgnoreCase("CV"))
            model.addAttribute("policyStatus", 3);
        else model.addAttribute("policyStatus", 0);
        return "lifeuwform";
    }

    @RequestMapping(value = { "binderPolTerms/{binCode}" }, method = { RequestMethod.GET })
    @ResponseBody
    public List<BinderPolTerms> selectageBracket(@PathVariable long binCode)
            throws BadRequestException {
        return  lifeService.getPolTerms(binCode);
    }


    @RequestMapping(value = "rpt_risk_note", method = RequestMethod.GET)
    public ModelAndView riskNote(ModelMap modelMap, HttpServletRequest request, ModelAndView modelAndView)
            throws BadRequestException, IOException {
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        PolicyTrans policyTrans = policyService.getPolicyDetails(polCode);

        OrganizationDTO organization = orgService.getOrganizationLogoDetails();
        InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
        BufferedImage image = ImageIO.read(in);
        modelMap.put("logo", image );
        modelMap.put("datasource", datasource);
        modelMap.put("format", "pdf");
        modelMap.put("polId", polCode);
        if(policyTrans.getProduct().getRiskNote()!=null || !StringUtils.isBlank(policyTrans.getProduct().getRiskNote())){
            modelAndView = new ModelAndView(policyTrans.getProduct().getRiskNote(), modelMap);
        }else
            modelAndView = new ModelAndView("rpt_risk_note", modelMap);
        return modelAndView;
    }
    @RequestMapping(value = { "lifeBinders" }, method = { RequestMethod.GET })
    @ResponseBody
    public Page<BindersDef> selectLifeBinders(@RequestParam(value = "term", required = false) String term, @RequestParam("bindType") String bindType, Pageable pageable)
            throws IllegalAccessException {
        return policyService.findLifeBinder(term, pageable,bindType);
    }


    @RequestMapping(value = { "getpolicyReceipts/{policyCode}" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<LifeReceiptsDTO> getPolicyReceiptss(@DataTable DataTablesRequest pageable, @PathVariable Long policyCode)
    {
        return receiptService.findPolicyReceipts(policyCode, pageable);
    }

    @RequestMapping(value = { "policyRisks/{policyCode}" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<RiskTransDTO> getPolicyRisks(@DataTable DataTablesRequest pageable, @PathVariable Long policyCode)
            throws IllegalAccessException {
        DataTablesResult<RiskTransDTO> result = policyService.findRiskTransactions(pageable,policyCode,-2000l);
        return result;
    }

    @RequestMapping(value = { "createRisk" }, method = {
            RequestMethod.POST })
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public ResponseEntity<String> createRiskTrans(@RequestBody CreateRiskDTO risk,
                                                  HttpServletRequest request) throws BadRequestException {
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        System.out.println("Passed through here....");
        policyService.createLifeRisk(risk,request);
        PolicyTrans policy = policyService.getPolicyDetails(polCode);
        if("L".equalsIgnoreCase(policy.getProduct().getProGroup().getPrgType())){
            try {
                premiumService.computeLifePrem(polCode);
            } catch (IOException e) {
                throw new BadRequestException(e.getMessage());
            }
        }else {
            if ("NB".equalsIgnoreCase(policy.getTransType()) || "SP".equalsIgnoreCase(policy.getTransType()) || "EX".equalsIgnoreCase(policy.getTransType()) || "RN".equalsIgnoreCase(policy.getTransType()))
                try {
                    premiumService.computePrem(polCode);
                } catch (IOException e) {
                    throw new BadRequestException(e.getMessage());
                }
            else if ("EN".equalsIgnoreCase(policy.getTransType()) || "CN".equalsIgnoreCase(policy.getTransType())) {
                try {
                    premiumService.computeEndorsePremium(polCode);
                } catch (IOException e) {
                    throw new BadRequestException(e.getMessage());
                }
            }
        }
        return new ResponseEntity<String>("Ok", HttpStatus.OK);
    }

    @RequestMapping(value = { "deleteRiskSection/{sectCode}" }, method = {
            RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public void deleteBinder(@PathVariable Long sectCode,HttpServletRequest request) throws BadRequestException {
        policyService.deleteRiskSection(sectCode,request);
    }
    @RequestMapping(value = { "risksSections/{riskId}" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<SectionTrans> getRiskSections(@DataTable DataTablesRequest pageable, @PathVariable Long riskId)
            throws IllegalAccessException {
        return policyService.findRiskSections(pageable,riskId);
    }

    @RequestMapping(value = { "riskDocs/{riskCode}" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<RiskDocsDTO> getRiskDocs(@DataTable DataTablesRequest pageable, @PathVariable Long riskCode)
            throws IllegalAccessException {
        return policyService.findRiskDocs(pageable,riskCode);
    }

    @RequestMapping(value = { "getMaturityDate" }, method = { RequestMethod.GET })
    @ResponseBody
    public Date getPolicyWetDate(@RequestParam(value = "wefDate", required = false) Date wef,@RequestParam(value = "polTerm", required = false) Integer polTerm){
        if (polTerm!=null) {
            return dateUtils.getMaturityDate(wef, polTerm);
        } else return null;

    }
    @RequestMapping(value = { "getLifeClientAge" }, method = {
            RequestMethod.GET })
    public ResponseEntity<Integer> getClientAge(
            @RequestParam(value = "clientId", required = false) Long clientId,@RequestParam(value = "binCode", required = false) Long binCode) throws BadRequestException {
        if(clientId==null)
            throw new BadRequestException("Client cannot be null");
        ClientDef clientDef = clientRepository.findOne(clientId);
        BindersDef bindersDef =bindersRepo.findOne(binCode);
        Integer age = dateUtils.getAge(clientDef.getDob());
        if(bindersDef.getPremiumAgeType()!=null && "N".equalsIgnoreCase(bindersDef.getPremiumAgeType()))
            age=age+1;
        return new ResponseEntity<Integer>(age, HttpStatus.OK);
    }

//    @RequestMapping(value = { "createLifePolicy" }, method = {
//            org.springframework.web.bind.annotation.RequestMethod.POST })
//    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
//    public ResponseEntity<PolicyCreateDTO> createLifePolicyTrans(@RequestBody PolicyCreateDTO policy,
//                                                             HttpServletRequest request) throws BadRequestException {
//        Long hashCode = Long.valueOf(String.valueOf(policy.hashCode()));
//        PolicyTrans created;
//        if(makerCheckerRepo.exists(hashCode)) {
//            created = policyService.createLifePolicy(policy, true);
//            Long polCode = created.getPolicyId();
//            request.getSession().setAttribute("policyCode", polCode);
//            if ("NB".equalsIgnoreCase(created.getTransType()) || "SP".equalsIgnoreCase(created.getTransType()) || "EX".equalsIgnoreCase(created.getTransType()) || "RN".equalsIgnoreCase(created.getTransType()))
//                try {
//                    premiumService.computeLifePrem(polCode);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    throw new BadRequestException(e.getMessage());
//                }
//        } else {
//            created = policyService.createLifePolicy(policy, false);
//
//        }
//        return new ResponseEntity<PolicyCreateDTO>(policy, HttpStatus.OK);
//    }
    @RequestMapping(value = { "createLifePolicy" }, method = {
            RequestMethod.POST })
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public ResponseEntity<PolicyCreateDTO> createLifePolicyTrans(@RequestBody PolicyCreateDTO policy,
                                                                 HttpServletRequest request) throws BadRequestException {
        System.out.println("from life controller");
        System.out.println("DTO: " + policy);
        PolicyTrans created = policyService.createLifePolicy(policy, true);
        policyService.createPolicyAddonsInfo(created,policy);

        System.out.println("After policy creation" +policy);
        Long polCode =created.getPolicyId();
        request.getSession().setAttribute("policyCode", polCode);
        if("NB".equalsIgnoreCase( created.getTransType()) || "SP".equalsIgnoreCase( created.getTransType())|| "EX".equalsIgnoreCase( created.getTransType())|| "RN".equalsIgnoreCase( created.getTransType()))
            try {
                System.out.println(created.getTransType());
                System.out.println("Computing life policy....");
                premiumService.computeLifePrem(polCode);
            } catch (IOException e) {
                e.printStackTrace();
                throw new BadRequestException(e.getMessage());
            }
        return new ResponseEntity<PolicyCreateDTO>(policy, HttpStatus.OK);
    }

    @RequestMapping(value = { "policyChecks" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<TransChecks> getPolicyChecks(@DataTable DataTablesRequest pageable, HttpServletRequest request)
            throws IllegalAccessException {
        Long policyCode = (Long) request.getSession().getAttribute("policyCode");
        if(policyCode==null) policyCode=-2000l;
        return policyService.findPolicyChecks(pageable,policyCode);
    }

    @RequestMapping(value = { "policyClauses" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<PolicyClauses> getPolicyClauses(@DataTable DataTablesRequest pageable,HttpServletRequest request)
            throws IllegalAccessException {
        Long policyCode = (Long) request.getSession().getAttribute("policyCode");
        return policyService.findPolicyClauses(pageable,policyCode);
    }

    @RequestMapping(value = { "policyInstallments" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<PolicyInstallments> getPolicyInstallments(@DataTable DataTablesRequest pageable,HttpServletRequest request)
            throws IllegalAccessException {
        Long policyCode = (Long) request.getSession().getAttribute("policyCode");
        return policyService.findPolicyInstallments(pageable,policyCode);
    }

    @RequestMapping(value = { "createRiskDocs" }, method = {
            RequestMethod.POST })
    public ResponseEntity<String> createClientDocs(@RequestBody RequiredDocBean requiredDocBean) throws IllegalAccessException, IOException, BadRequestException {
        policyService.createRiskRequiredDocs(requiredDocBean);
        return new ResponseEntity<String>("OK",HttpStatus.OK);
    }

    @RequestMapping(value = { "uploadRequiredDocs" }, method = { RequestMethod.POST })
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> uploadRequiredDocs(UploadBean uploadBean) {
        try {
            // First validate the file type for ALAK products
            validateFileTypeForALAK(uploadBean);

            uploadService.sybrinCreateCase(uploadBean, "Risk");
            return ResponseEntity.status(HttpStatus.CREATED).body("File Uploaded successfully");
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    private void validateFileTypeForALAK(UploadBean uploadBean) throws BadRequestException {
        if (uploadBean.getDocId() == null) {
            return; // Skip validation if no docId is provided
        }

        // Get the risk document to check if it belongs to an ALAK product
        RiskDocs riskDoc = riskDocsRepo.getRiskDocsVal(uploadBean.getDocId());
        if (riskDoc == null || riskDoc.getRisk() == null || riskDoc.getRisk().getPolicy() == null) {
            return; // Skip validation if we can't trace the policy
        }

        // Get the policy to check the insurer
        PolicyTrans policy = riskDoc.getRisk().getPolicy();

        // Check if this is an ALAK product by checking the agent's ABSA number
        if (!isALAKProduct(policy)) {
            return; // Skip validation if not an ALAK product
        }

        // Validate file type for ALAK products
        MultipartFile file = uploadBean.getFile();
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BadRequestException("Invalid file name");
        }

        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!Arrays.asList("pdf", "jpg", "jpeg").contains(fileExtension)) {
            throw new BadRequestException("Absa Life Assurance Kenya Limited only supports PDF, JPG and JPEG documents");
        }
    }

    private boolean isALAKProduct(PolicyTrans policy) {
        if (policy.getAgent() == null) {
            return false;
        }

        // Check if the agent's ABSA number matches ALAK's identifier
        return "ABSA007".equals(policy.getAgent().getAbsaNo());
    }
//    public ResponseEntity<String> uploadRequiredDocs(UploadBean uploadBean) {
//        try {
//            uploadService.sybrinCreateCase(uploadBean, "Risk");
//            return ResponseEntity.status(HttpStatus.CREATED).body("File Uploaded successfully");
//        } catch (BadRequestException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
//        }
//    }


    @RequestMapping(value = { "deleteRiskDoc/{docId}" }, method = {
            RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRiskDoc(@PathVariable Long docId) throws BadRequestException {
        uploadService.deleteRiskDoc(docId);
    }
    @RequestMapping(value = { "getPolicyDetails" }, method = {
            RequestMethod.GET })
    public ResponseEntity<PolicyTrans> getPolicyDetails(HttpServletRequest request) throws BadRequestException {
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        PolicyTrans created = policyService.getPolicyDetails(polCode);
        return new ResponseEntity<PolicyTrans>(created, HttpStatus.OK);
    }

    @RequestMapping(value = { "createLifePolMakeReady" }, method = { RequestMethod.POST })
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    public ResponseEntity<PolicyTrans> createLifePolMakeReady(HttpServletRequest request, @RequestParam(value = "resubmissionComment", required = false) String resubmissionComment) throws BadRequestException {
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        log.info("Processing createLifePolMakeReady for polCode: {}, resubmissionComment: {}", polCode, resubmissionComment);
        PolicyTrans created = policyService.getPolicyDetails(polCode);
        Long hashcode = Long.parseLong(String.valueOf(created.hashCode()));

        // Check if the task is a rejected life policy (ALP) and requires a resubmission comment
        boolean isRejectedLifePolicy = makerCheckerRepo.exists(QMakerChecker.makerChecker.taskType.equalsIgnoreCase("ALP")
                .and(QMakerChecker.makerChecker.policyId.eq(polCode)));
        if (isRejectedLifePolicy && (resubmissionComment == null || resubmissionComment.trim().isEmpty())) {
            throw new BadRequestException("Resubmission comment is required for Rejected Life Policy.");
        }

        System.out.println("Make Ready...");

        System.out.println("From create life policy make ready: " + created.getClient().getOtherNames());
        if ("CO".equalsIgnoreCase(created.getTransType())) {
            log.info("PROCESSING CO (Contra) transaction for life policy: {} ===", created.getPolNo());
            Iterable<PolicyRemarks> policyRemarks = policyRemarksRepo.findAll(QPolicyRemarks.policyRemarks.policy.policyId.eq(created.getPolicyId()));
            if (!policyRemarks.iterator().hasNext()) {
                throw new BadRequestException("Input Remarks first....");
            }

            List<UserDTO> eligibleCheckers = makerCheckerService.findEligibleCheckers("AUTHORIZE_POLICY", null);
            List<Long> checkerIds = new ArrayList<>();

            boolean makerCheckerExists = makerCheckerRepo.exists(QMakerChecker.makerChecker.taskType.equalsIgnoreCase("ALP")
                    .and(QMakerChecker.makerChecker.policyId.eq(polCode)));

            if (!makerCheckerExists) {
                log.info("CREATING maker checker for contra life policy: {}, found {} eligible checkers ===",
                        created.getPolNo(), eligibleCheckers.size());

                for (UserDTO eligibleChecker : eligibleCheckers) {
                    checkerIds.add(eligibleChecker.getId());
                }

                PolicyCreateDTO policyDTO = new PolicyCreateDTO();
                BeanUtils.copyProperties(created, policyDTO);

                MakerCheckDTO makerCheckDTO = new MakerCheckDTO();
                makerCheckDTO.setStatus("N");
                makerCheckDTO.setTaskName(String.format(" %s ", created.getPolNo()));
                makerCheckDTO.setTaskType("ALP");
                makerCheckDTO.setJson(new Gson().toJson(policyDTO));
                makerCheckDTO.setTaskCode(hashcode);
                makerCheckDTO.setAssignedCheckers(checkerIds.toString());
                makerCheckDTO.setPolicyId(polCode);
                makerCheckerService.checkExists(makerCheckDTO);
                makerCheckerService.createMakerChecker(makerCheckDTO);

                return new ResponseEntity<>(created, HttpStatus.OK);
            } else {
                log.info("MAKER CHECKER already exists for contra life policy: {} - proceeding with approval flow ===",
                        created.getPolNo());
            }
        }

        request.getSession().setAttribute("policyCode", polCode);
        System.out.println("Trans Type..." + created.getTransType());
        if ("NB".equalsIgnoreCase(created.getTransType()) || "SP".equalsIgnoreCase(created.getTransType()) ||
                "EX".equalsIgnoreCase(created.getTransType()) || "RN".equalsIgnoreCase(created.getTransType())) {
            try {
                premiumService.computeLifePrem(polCode);
            } catch (IOException e) {
                throw new BadRequestException(e.getMessage());
            }
        }
        policyService.makeLifeReady(polCode);
        return new ResponseEntity<>(created, HttpStatus.OK);
    }
    @RequestMapping(value = { "proposalConversion" }, method = { RequestMethod.POST })
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void proposalConversion(HttpServletRequest request,
                                   @RequestParam(required = false) List<String> checkers,
                                   @RequestParam(value = "resubmissionComment", required = false) String resubmissionComment,
                                   @RequestParam(value = "rejectionReason", required = false) RejectedReasons rejectionReason)
            throws BadRequestException {
        Logger log = LoggerFactory.getLogger(getClass());

        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        request.getSession().setAttribute("policyCode", polCode);
        log.info("POL CODE: {} comm: {}", polCode, resubmissionComment);
        boolean manualOnly = checkers != null && !checkers.isEmpty();
        //optimize with the dto for better experience in production
        Gson gson = new GsonBuilder()
                .setDateFormat("dd/MM/yyyy")
                .registerTypeHierarchyAdapter(HibernateProxy.class, new HibernateProxyTypeAdapter())
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getAnnotation(JsonIgnore.class) != null;
                    }
                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();

        if (manualOnly) {
            List<Long> checkerIds = checkers.stream()
                    .map(Long::parseLong)
                    .collect(Collectors.toList());

            boolean taskExists = makerCheckerRepo.exists(
                    QMakerChecker.makerChecker.taskType.equalsIgnoreCase("ALP")
                            .and(QMakerChecker.makerChecker.policyId.eq(polCode)));

            PolicyTrans policy = policyTransRepo.findOne(polCode);
            Long makerId = checkerIds.get(0);
            Long originalInitiatorId = policy.getCreatedUser().getId();

            if (!taskExists) {
                MakerCheckDTO makerCheckDTO = new MakerCheckDTO();
                makerCheckDTO.setStatus("N");
                makerCheckDTO.setTaskName(String.format(" %s", polCode));
                makerCheckDTO.setTaskType("ALP");
                makerCheckDTO.setJson("{}");
                makerCheckDTO.setMakerId(makerId);
                makerCheckDTO.setInitiatorId(originalInitiatorId);
                makerCheckDTO.setTaskCode(Long.parseLong(String.valueOf(policy.hashCode())));
                makerCheckDTO.setAssignedCheckers(checkerIds.toString());
                makerCheckDTO.setPolicyId(policy.getPolicyId());
                if (resubmissionComment != null && !resubmissionComment.trim().isEmpty()) {
                    makerCheckDTO.setResubmissionComment(resubmissionComment);

                    RtsAudit rtsAudit = new RtsAudit();
                    rtsAudit.setResubmissionComment(resubmissionComment);
                    rtsAudit.setAuditTime(new Date());
                    rtsAudit.setPolicyInfo(gson.toJson(policy));
                    rtsAudit.setPolicyId(policy.getPolicyId());
                    rtsAudit.setMakerId(userUtils.getCurrentUser());
                    rtsAudit.setRejectionReason(null); // Clear the RejectedReasons object
                    rtsAudit.setUserRejectedReason("N/A");

                    rtsAuditRepository.save(rtsAudit);

                    log.info("Setting resubmissionComment on new task: {}", resubmissionComment);
                }
                makerCheckerService.checkExists(makerCheckDTO);
                makerCheckerService.createMakerChecker(makerCheckDTO);
            } else {
                MakerChecker makerChecker = makerCheckerRepo.findOne(
                        QMakerChecker.makerChecker.taskType.equalsIgnoreCase("ALP")
                                .and(QMakerChecker.makerChecker.policyId.eq(polCode)));
                if (resubmissionComment != null && !resubmissionComment.trim().isEmpty()) {
                    makerChecker.setResubmissionComment(resubmissionComment);

                    RtsAudit rtsAudit = new RtsAudit();
                    rtsAudit.setResubmissionComment(resubmissionComment);
                    rtsAudit.setAuditTime(new Date());
                    rtsAudit.setPolicyInfo(gson.toJson(policy));
                    rtsAudit.setPolicyId(policy.getPolicyId());
                    rtsAudit.setMakerId(userUtils.getCurrentUser());
                    rtsAudit.setRejectionReason(null); // Clear the RejectedReasons object
                    rtsAudit.setUserRejectedReason(makerChecker.getRejectedReason() != null ? makerChecker.getRejectedReason() : "N/A");

                    rtsAuditRepository.save(rtsAudit);

                    log.info("Setting resubmissionComment on existing task: {}", resubmissionComment);
                }
                makerCheckerService.resubmitTask(makerChecker.getId(), "ALP", policy, checkerIds);
                log.info("Task resubmitted with ID: {}", makerChecker.getId());
            }
            return;
        }
        policyService.convertPropToPolicy(polCode);
        PolicyTrans policy = policyTransRepo.findOne(polCode);
        final Long hashCode = Long.parseLong(String.valueOf(policy.hashCode()));
        List<UserDTO> eligibleCheckers = makerCheckerService.findEligibleCheckers("AUTHORIZE_POLICY", null);
        List<Long> checkerIds = new ArrayList<>();
        if (!makerCheckerRepo.exists(QMakerChecker.makerChecker.taskType.equalsIgnoreCase("ALP")
                .and(QMakerChecker.makerChecker.policyId.eq(polCode)))) {
            for (UserDTO eligibleChecker : eligibleCheckers) {
                checkerIds.add(eligibleChecker.getId());
            }
            MakerCheckDTO makerCheckDTO = new MakerCheckDTO();
            makerCheckDTO.setStatus("N");
            makerCheckDTO.setTaskName(String.format(" %s", policy.getPolNo()));
            makerCheckDTO.setTaskType("ALP");
            makerCheckDTO.setJson("{}");
            makerCheckDTO.setTaskCode(hashCode);
            makerCheckDTO.setAssignedCheckers(checkerIds.toString());
            makerCheckDTO.setPolicyId(policy.getPolicyId());
            if (resubmissionComment != null && !resubmissionComment.trim().isEmpty()) {
                makerCheckDTO.setResubmissionComment(resubmissionComment);

                RtsAudit rtsAudit = new RtsAudit();
                rtsAudit.setResubmissionComment(resubmissionComment);
                rtsAudit.setAuditTime(new Date());
                rtsAudit.setPolicyInfo(gson.toJson(policy));
                rtsAudit.setPolicyId(policy.getPolicyId());
                rtsAudit.setMakerId(userUtils.getCurrentUser());
                rtsAudit.setRejectionReason(null); // Clear the RejectedReasons object
                rtsAudit.setUserRejectedReason("N/A");

                rtsAuditRepository.save(rtsAudit);

                log.info("Setting resubmissionComment on new task (non-manual): {}", resubmissionComment);
            }
            makerCheckerService.checkExists(makerCheckDTO);
            makerCheckerService.createMakerChecker(makerCheckDTO);
        } else {
            MakerChecker makerChecker = makerCheckerRepo.findOne(
                    QMakerChecker.makerChecker.taskType.equalsIgnoreCase("ALP")
                            .and(QMakerChecker.makerChecker.policyId.eq(polCode)));
            if (resubmissionComment != null && !resubmissionComment.trim().isEmpty()) {
                makerChecker.setResubmissionComment(resubmissionComment);

                RtsAudit rtsAudit = new RtsAudit();
                rtsAudit.setResubmissionComment(resubmissionComment);
                rtsAudit.setAuditTime(new Date());
                rtsAudit.setPolicyId(policy.getPolicyId());
                rtsAudit.setPolicyInfo(gson.toJson(policy));
                rtsAudit.setMakerId(userUtils.getCurrentUser());
                rtsAudit.setRejectionReason(null); // Clear the RejectedReasons object
                rtsAudit.setUserRejectedReason(makerChecker.getRejectedReason() != null ? makerChecker.getRejectedReason() : "N/A");

                rtsAuditRepository.save(rtsAudit);

                log.info("Setting resubmissionComment on existing task (non-manual): {}", resubmissionComment);
            }
            makerCheckerService.resubmitTask(makerChecker.getId(), "ALP", policy, checkerIds);
            log.info("Task resubmitted with ID (non-manual): {}", makerChecker.getId());
        }
    }
    @RequestMapping(value = {"/audit-trails/{policyId}"}, method = { RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<List<AuditTrailDTO>> getAuditTrails(@PathVariable Long policyId) {
        List<RtsAudit> auditTrails = rtsAuditRepository.findByPolicyId(policyId);
        List<AuditTrailDTO> dtos = auditTrails.stream().map(audit -> {
            AuditTrailDTO dto = new AuditTrailDTO();
            dto.setCheckerName(audit.getCheckerId() != null ? audit.getCheckerId().getUsername() : "N/A");
            dto.setMakerName(audit.getMakerId() != null ? audit.getMakerId().getUsername() : "N/A");
            dto.setAuditTime(audit.getAuditTime());
            dto.setResubmissionComment(audit.getResubmissionComment() != null ? audit.getResubmissionComment() : "N/A");
            dto.setRejectionReasonDesc(audit.getUserRejectedReason() != null ? audit.getUserRejectedReason() : "N/A");
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @RequestMapping(value = { "undoProposalConversion" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void undoProposalConversion(
            HttpServletRequest request,
            @RequestParam Long reasonId,
            @RequestParam String reason) throws BadRequestException {
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        policyService.undoProposalConversion(polCode, reasonId, reason);

    }

    @RequestMapping(value = { "getPolicyRemarks" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    public ResponseEntity<PolicyRemarks> getPolicyRemarks(HttpServletRequest request) throws BadRequestException {
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        PolicyRemarks remarks = policyService.getPolicyRemarks(polCode);
        return new ResponseEntity<PolicyRemarks>(remarks, HttpStatus.OK);
    }

    @RequestMapping(value = { "getBinderClientPremRates" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    public ResponseEntity<Set<RiskSectionBean>> getBinderClientPremRates(
            @RequestParam(value = "detId", required = false) Long detId,@RequestParam(value = "age", required = false) Long age) throws BadRequestException {
        Set<RiskSectionBean> rates = policyService.getBinderClientPremRates(detId,age);
        return new ResponseEntity<Set<RiskSectionBean>>(rates, HttpStatus.OK);
    }

    @RequestMapping(value = { "getBinderPremRates" }, method = {
            RequestMethod.GET })
    public ResponseEntity<Set<RiskSectionBean>> getBinderPremRates(
            @RequestParam(value = "detId", required = false) Long detId) throws BadRequestException {
        Set<RiskSectionBean> rates = policyService.getBinderPremRates(detId);
        return new ResponseEntity<Set<RiskSectionBean>>(rates, HttpStatus.OK);
    }

    @RequestMapping(value = { "createBeneficiary" }, method = {
            RequestMethod.POST })
    @ResponseStatus(HttpStatus.CREATED)
    public void createBeneficiary(PolicyBeneficiaries beneficiaries) throws IllegalAccessException, BadRequestException {
        lifeService.definePolicyBeneficiary(beneficiaries);
    }

    @RequestMapping(value = { "deleteBeneficiary/{benCode}" }, method = {
            RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBeneficiary(@PathVariable Long benCode) {
        lifeService.deletePolBeneficiary(benCode);
    }


    @RequestMapping(value = { "policyBeneficiary/{policyCode}" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<PolicyBeneficiaries> getPolicyBeneficiaries(@DataTable DataTablesRequest pageable,@PathVariable Long policyCode)
            throws IllegalAccessException {
        return lifeService.findPolBeneficiaries(pageable,policyCode);
    }


    @RequestMapping(value = { "policyReceipts/{policyCode}" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<LifeReceipts> getPolicyReceipts(@DataTable DataTablesRequest pageable, @PathVariable Long policyCode)
            throws IllegalAccessException {
        return lifeService.findPolReceipts(pageable,policyCode);
    }


    @RequestMapping(value = { "receiptAllocs/{receiptCode}" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<LifeReceiptAllocations> getReceiptAllocs(@DataTable DataTablesRequest pageable, @PathVariable Long receiptCode)
            throws IllegalAccessException {
        return lifeService.findReceiptsAllocations(pageable,receiptCode);
    }


    @RequestMapping(value = { "allocationCommission/{receiptId}" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<ReceiptAllocationCommissionsDTO> getAllocCommission(@DataTable DataTablesRequest pageable, @PathVariable Long receiptId) {
        return lifeService.findAllocationCommissions(pageable,receiptId);
    }
    
    @RequestMapping(value = { "getriskreqdocs" }, method = { RequestMethod.GET })
	@ResponseBody
	public List<ReqDocsDTO> getRiskUnassignedDocs(@RequestParam(value = "riskId", required = false) Long riskId, @RequestParam(value = "docName", required = false) String docName )
			throws IllegalAccessException {
		return policyService.findUnassignedRiskDocs(riskId,docName);
	}
    
    @RequestMapping(value = { "authChecks/{checkId}" }, method = {
			RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void authChecks(@PathVariable Long checkId, HttpServletRequest request) throws BadRequestException {
		Long policyCode = (Long) request.getSession().getAttribute("policyCode");
		policyService.approveException(checkId,policyCode);
	}
    
    @RequestMapping(value = { "undoMakeReady" }, method = {
			RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void undoMakePolicyReady(HttpServletRequest request,
                                    @RequestParam Long reasonId,
                                    @RequestParam(required = false) String reason) throws BadRequestException {
	 Long polCode = (Long) request.getSession().getAttribute("policyCode");
	 policyService.undoMakeReady(polCode, reasonId, reason);
		
	}

    @RequestMapping(value = { "authorizeLifePolicy" }, method = {
            RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void authorizeLifePolicy(HttpServletRequest request,@RequestParam(value = "refundAmt", required = false) BigDecimal refundAmt) throws BadRequestException {
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        authService.authorizeLifePolicy(polCode);

    }

@RequestMapping(value = { "dispatchDocs" }, method = {
		RequestMethod.GET })
@ResponseStatus(HttpStatus.NO_CONTENT)
public void dispatchDocs(HttpServletRequest request) throws BadRequestException {
	Long polCode = (Long) request.getSession().getAttribute("policyCode");
	policyService.dispatchDocuments(polCode);

}
 


    @RequestMapping(value = { "policyBenefits/{policyCode}" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<PolicyBenefitsDistribution> getPolicyBenefits(@DataTable DataTablesRequest pageable, @PathVariable Long policyCode)
            throws IllegalAccessException {
        return lifeService.findPolBenefits(pageable,policyCode);
    }

    @RequestMapping(value = {"sendSms"}, method = {
            RequestMethod.POST})
    public ResponseEntity<String> sendSms(@RequestBody MailMessageBean messageBean, HttpServletRequest request) throws BadRequestException {
        Long policyCode = (Long) request.getSession().getAttribute("policyCode");
        mailer.sendSmsAttachmentsANE(messageBean, policyCode, "P", request);
        return new ResponseEntity<String>("OK", HttpStatus.OK);
    }

    @RequestMapping(value = { "sendEmail" }, method = {RequestMethod.POST })
    public ResponseEntity<String>  sendEmail(@RequestBody MailMessageBean messageBean,HttpServletRequest request) throws BadRequestException {
        System.out.println(messageBean);
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        mailer.sendEmailAttachments(messageBean,polCode,"P",request);
        return new ResponseEntity<String>("OK", HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value ="mailtemplate" )
    public String getMailTemplate(HttpServletResponse response, HttpServletRequest request, @RequestParam String templateType) throws BadRequestException{
        String template = "";
        switch (templateType) {
            case "non_motor_full_premium":
                template = mailTemplateService.getMailTemplate(MailTemplates.NON_MOTOR_FULL_PREMIUM,request);
                break;
            case "non_motor_initial_premium":
                template = mailTemplateService.getMailTemplate(MailTemplates.NON_MOTOR_INITIAL_PREMIUM,request);
                break;
            case "non_motor_additional_premium":
                template = mailTemplateService.getMailTemplate(MailTemplates.NON_MOTOR_ADDITIONAL_PREMIUM,request);
                break;
            case "motor_full_premium":
                template = mailTemplateService.getMailTemplate(MailTemplates.MOTOR_FULL_PREMIUM,request);
                break;
            case "motor_initial_premium":
                template = mailTemplateService.getMailTemplate(MailTemplates.MOTOR_INITIAL_PREMIUM,request);
                break;
            case "motor_additional_full_premium":
                template = mailTemplateService.getMailTemplate(MailTemplates.MOTOR_ADDITIONAL_FULL_PREMIUM,request);
                break;
            case "motor_additional_partial_premium":
                template = mailTemplateService.getMailTemplate(MailTemplates.MOTOR_ADDITIONAL_PARTIAL_PREMIUM,request);
                break;
            case "cash_based_policy":
                template = mailTemplateService.getMailTemplate(MailTemplates.CASH_BASED_POLICY_TEMPLATE,request);
                break;
            case "future_based_policy":
                template = mailTemplateService.getMailTemplate(MailTemplates.FUTURE_BASED_POLICY_TEMPLATE,request);
                break;
            case "overpaid_premium":
                template = mailTemplateService.getMailTemplate(MailTemplates.OVERPAID_PREMIUM_TEMPLATE,request);
                break;
            case "policy_template":
                template = mailTemplateService.getMailTemplate(MailTemplates.POLICY_TEMPLATE,request);
                break;
            default:
        }
        return template;
    }

    @ResponseBody
    @RequestMapping(value = "smstemplate")
    public String getSmsTemplate(HttpServletResponse response, HttpServletRequest request) throws BadRequestException {
        return mailTemplateService.getMailTemplate(MailTemplates.POLICY_TEMPLATE, request);
    }

    @RequestMapping(value = { "getReceiverEmail" }, method = {
            RequestMethod.GET })
    public ResponseEntity<String> getReceiverEmail(@RequestParam(value = "receiver", required = false) String receiver, HttpServletRequest request) throws BadRequestException {
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
        String email = mailer.getEmailReceivers(polCode,"P",receiver);
        return new ResponseEntity<String>(email, HttpStatus.OK);
    }

    @RequestMapping(value = {"getReceiverSmsNumber"}, method = {
            RequestMethod.GET})
    public ResponseEntity<String> getReceiverSmsNumber(@RequestParam(value = "receiver", required = false) String receiver, HttpServletRequest request) throws BadRequestException {
        Long policyCode = (Long) request.getSession().getAttribute("policyCode");
        String sms = mailer.getSMSReceivers(policyCode, "P", receiver);
        return new ResponseEntity<String>(sms, HttpStatus.OK);
    }

}
