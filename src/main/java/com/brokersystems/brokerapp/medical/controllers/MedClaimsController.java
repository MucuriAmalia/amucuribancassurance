package com.brokersystems.brokerapp.medical.controllers;

import com.brokersystems.brokerapp.dms.service.UploadService;
import com.brokersystems.brokerapp.mail.model.MailMessageBean;
import com.brokersystems.brokerapp.mail.model.MailTemplates;
import com.brokersystems.brokerapp.mail.service.MailTemplateService;
import com.brokersystems.brokerapp.mail.service.Mailer;
import com.brokersystems.brokerapp.medical.model.*;
import com.brokersystems.brokerapp.medical.service.MedicalCardsService;
import com.brokersystems.brokerapp.medical.service.MedicalClmService;
import com.brokersystems.brokerapp.medical.service.MedicalSetupsService;
import com.brokersystems.brokerapp.medical.service.MedicalTransService;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.exception.EndorsementsException;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.setup.dto.OrganizationDTO;
import com.brokersystems.brokerapp.setup.model.ModelHelperForm;
import com.brokersystems.brokerapp.setup.service.OrganizationService;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by peter on 5/11/2017.
 */
@Controller
@RequestMapping({ "/protected/medical/claims" })
public class MedClaimsController {

    @Autowired
    private MedicalClmService clmService;

    @Autowired
    private MedicalTransService medicalTransService;

    @Autowired
    private MedicalSetupsService medicalSetupsService;

    @Autowired
    private MedicalCardsService medicalCardsService;

    @Autowired
    private OrganizationService orgService;

    @Autowired
    private DataSource datasource;

    @Autowired
    private Mailer mailer;

    @Autowired
    private MailTemplateService templateService;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private AuditTrailLogger auditTrailLogger;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @ModelAttribute
    private StartClmMedical getStartClmMedical(){
        return new StartClmMedical();
    }

    @ModelAttribute
    private ModelHelperForm getModelHelperForm(){
        return new ModelHelperForm();
    }

    @RequestMapping(value = "clmprocess", method = RequestMethod.GET)
    public String clmprocess(Model model, HttpServletRequest request) {
        auditTrailLogger.log("Accessed Medical Claims Screen ",request, "Medical Claims");
        return "medclaims";
    }

    @RequestMapping(value = "medcards", method = RequestMethod.GET)
    public String medcards(Model model) {
        return "medcards";
    }

    @RequestMapping(value = "smartcards", method = RequestMethod.GET)
    public String smartcards(Model model, HttpServletRequest request) {
        auditTrailLogger.log("Accessed Smart Claims Screen ",request, "Smart Claims");
        return "smartcards";
    }

    @RequestMapping(value = "preauth", method = RequestMethod.GET)
    public String preauthTrans(Model model) {
        model.addAttribute("parCode", -2000);
        return "preauth";
    }


    @RequestMapping(value = "enquirepartrans", method = RequestMethod.GET)
    public String enquireclms(Model model) {
        return "preauthenquiry";
    }


    @RequestMapping(value = "batchpartrans", method = RequestMethod.GET)
    public String batchtrans(Model model,HttpServletRequest request) {
        String message="Accessed Batch Claims Transactions Screen";
        String resource = "Batch Claims";
        auditTrailLogger.log(message,request, resource);
        return "batchparTrans";
    }

    @RequestMapping(value = { "startTrans" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    public String startClmTrans(@Valid @ModelAttribute StartClmMedical startClmMedical, BindingResult result, RedirectAttributes redirectAttrs, Model model){
        if(startClmMedical.getTransType()==null || StringUtils.isBlank(startClmMedical.getTransType())){
            redirectAttrs.addFlashAttribute("error", "Select Claim Type");
            redirectAttrs.addFlashAttribute("startClmMedical", startClmMedical);
            return "redirect:/protected/medical/claims/clmprocess";
        }
          switch(startClmMedical.getTransType()){
              case "R":
                  return "preauthenquiry";
              case "C":
                  model.addAttribute("parCode", -2000);
                  return "medtrans";
              case "P":
                  model.addAttribute("parCode", -2000);
                  return "preauth";
              default:
                  return "redirect:/protected/medical/claims/clmprocess";
          }
    }

    @RequestMapping(value = { "selMembers" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<CategoryMembers> getCategoryMembers(@DataTable DataTablesRequest pageable,
                                                             @RequestParam(value = "policyId", required = false) Long policyId,
                                                             @RequestParam(value = "cardNo", required = false) String cardNo,
                                                             @RequestParam(value = "memberName", required = false) String memberName,
                                                              @RequestParam(value = "clientId", required = false) Long clientId,
                                                             @RequestParam(value = "gender", required = false) String gender) throws IllegalAccessException {
        return clmService.findClaimMembers(pageable,policyId,cardNo,memberName,gender,clientId);
    }

    @RequestMapping(value = { "selClmMembers" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<CategoryMembers> getClmCategoryMembers(@DataTable DataTablesRequest pageable,
                                                                @RequestParam(value = "policyNo", required = false) String policyNo,
                                                                @RequestParam(value = "memberNo", required = false) String memberNo,
                                                                @RequestParam(value = "memberName", required = false) String memberName,
                                                                @RequestParam(value = "clientName", required = false) String clientName,
                                                                @RequestParam(value = "age", required = false) Long age,
                                                                @RequestParam(value = "gender", required = false) String gender,
                                                                @RequestParam(value = "dob", required = false) Date dob) throws IllegalAccessException {
        return clmService.findClmMembers(pageable, policyNo, memberNo, memberName, age,gender,dob,clientName);
    }

    @RequestMapping(value = { "medicalCards" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<MedicalCards> getMemberCards(@DataTable DataTablesRequest pageable,
                                                                @RequestParam(value = "policyNo", required = false) String policyNo,
                                                                @RequestParam(value = "memberNo", required = false) String memberNo,
                                                                @RequestParam(value = "memberName", required = false) String memberName,
                                                                @RequestParam(value = "clientName", required = false) String clientName,
                                                                @RequestParam(value = "cardNo", required = false) String cardNo) throws IllegalAccessException {
        return medicalCardsService.findMedicalCards(pageable, policyNo, memberNo, memberName,clientName,cardNo);
    }

    @RequestMapping(value = { "selevents" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public Page<MedicalEvents> selectEvents(@RequestParam(value = "term", required = false) String term, Pageable pageable)
            throws IllegalAccessException {
        return clmService.findEvents(term, pageable);
    }

    @RequestMapping(value={"providercontracts"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Page<ServiceProviderContracts> providerContractssel(@RequestParam(value="term", required=false) String term, Pageable pageable)
            throws IllegalAccessException
    {
        return medicalTransService.findProviderContractsForSelect(term, pageable);
    }

    @RequestMapping(value={"ailmentssel"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Page<Ailments> ailmentssel(@RequestParam(value="term", required=false) String term, Pageable pageable)
            throws IllegalAccessException
    {
        return medicalSetupsService.findAilmentsForSelect(term, pageable);
    }

    @RequestMapping(value={"networkssel"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Page<MedicalNetworks> networkssel(@RequestParam(value="term", required=false) String term, Pageable pageable)
            throws IllegalAccessException
    {
        return medicalSetupsService.findNetworksForSelect(term, pageable);
    }

    @RequestMapping(value={"activitiessel"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Page<MedActivities> activitiessel(@RequestParam(value="term", required=false) String term, @RequestParam("labId") Long labId,Pageable pageable)
            throws IllegalAccessException
    {
        return medicalSetupsService.findActivitiesForSelect(term, pageable,labId);
    }
    @RequestMapping(value={"medicalServices"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Page<LabTest> medicalServices( @RequestParam(value="term", required=false) String term,Pageable pageable)
            throws IllegalAccessException
    {
        return medicalSetupsService.findServicesForSelect(term,pageable);
    }


    @RequestMapping(value = { "saveParRequest" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public Long saveParRequest(MedicalParTrans parTrans,HttpServletRequest request) throws IllegalAccessException, IOException, BadRequestException {
       Long parId =  clmService.createPreauthTrans(parTrans);
        request.getSession().setAttribute("parCode",parId);
        return parId;
    }

    @RequestMapping(value = { "saveMedClaims" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public Long saveMedClaims(MedicalParTrans parTrans,HttpServletRequest request,Model model) throws IllegalAccessException, IOException, BadRequestException {
        Long parId =  clmService.createClmTrans(parTrans);
        model.addAttribute("parCode",parId);
        request.getSession().setAttribute("parCode",parId);
        return parId;
    }

    @RequestMapping(value = { "partrans" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<MedicalParTrans> getParTrans(@DataTable DataTablesRequest pageable) throws IllegalAccessException {
        return clmService.enquirePreuathTrans(pageable);
    }

    @RequestMapping(value = { "allbatchTrans" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<MedicalParTrans> getBatchTrans(@DataTable DataTablesRequest pageable,@RequestParam(value = "contractId", required = false) Long contractId) throws IllegalAccessException {
        return clmService.batchTrans(pageable,contractId);
    }

    @RequestMapping(value = "/parclaimtrans", method = RequestMethod.POST)
    public String editPolicyForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model, HttpServletRequest request) throws BadRequestException {
        model.addAttribute("parCode", helperForm.getId());
        MedicalParTrans created = clmService.getMedicalClaimDetails(helperForm.getId());
        request.getSession().setAttribute("parCode", helperForm.getId());
        if("P".equalsIgnoreCase(created.getTransType()))
        return "preauth";
        else
            return "medtrans";
    }

    @RequestMapping(value = { "getClaimDetails" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    public ResponseEntity<MedicalParTrans> getClaimDetails(HttpServletRequest request) throws BadRequestException {
        Long parId =(Long) request.getSession().getAttribute("parCode");
        MedicalParTrans created = clmService.getMedicalClaimDetails(parId);
        return new ResponseEntity<MedicalParTrans>(created, HttpStatus.OK);
    }

    @RequestMapping(value = { "requests/{parId}" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<MedicalParRequest> getMedicalRequests(@DataTable DataTablesRequest pageable, @PathVariable Long parId)
            throws IllegalAccessException {
        return clmService.enquireParRequests(pageable,parId);
    }

    @RequestMapping(value = { "services/{reqId}" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<MedicalRequestServices> getMedicalServices(@DataTable DataTablesRequest pageable, @PathVariable Long reqId)
            throws IllegalAccessException {
        return clmService.getRequestServices(pageable,reqId);
    }

    @RequestMapping(value = { "selbenefits" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public Page<MedCategoryBenefits> selbenefits(@RequestParam(value = "term", required = false) String term,@RequestParam("catId") Long catId, Pageable pageable)
            throws IllegalAccessException {
        return clmService.findBenefits(term, pageable,catId);
    }

    @RequestMapping(value = { "makereadypreauth" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void makeReadyPreauth(HttpServletRequest request) throws BadRequestException {
        Long parId =(Long) request.getSession().getAttribute("parCode");
        clmService.makeReadyPreauth(parId);
    }

    @RequestMapping(value = { "authorizepreauth" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void authorizepreauth(HttpServletRequest request) throws BadRequestException {
        Long parId =(Long) request.getSession().getAttribute("parCode");
        clmService.authPreauth(parId);
    }

    @RequestMapping(value = { "remainingLimit/{reqId}" }, method = { RequestMethod.GET })
    @ResponseBody
    public BigDecimal getBenefitLimit( @PathVariable Long reqId)
            throws IllegalAccessException {
        return clmService.calculateLimit(reqId);
    }

    @RequestMapping(value = { "makereadyclaim" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void makeReadyClaim(HttpServletRequest request) throws BadRequestException {
        Long parId =(Long) request.getSession().getAttribute("parCode");
        clmService.makeReadyMedClaims(parId);
    }

    @RequestMapping(value = { "authorizeClaim" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void authorizeClaim(HttpServletRequest request) throws BadRequestException {
        Long parId =(Long) request.getSession().getAttribute("parCode");
        clmService.authMedClaims(parId);
    }

    @RequestMapping(value = { "smartclaims" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<MedicalParTrans> getSmartClaims(@DataTable DataTablesRequest pageable) throws IllegalAccessException {
        return clmService.smartClaims(pageable);
    }

    @RequestMapping(value = { "dispatchCards" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    public ResponseEntity<String> dispatchCards(@RequestBody MedicalBatchDTO medicalBatchDTO) throws IllegalAccessException, IOException, BadRequestException {
        medicalCardsService.dispatchCards(medicalBatchDTO);
        return new ResponseEntity<String>("OK",HttpStatus.OK);
    }

    @RequestMapping(value = { "receiveCards" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    public ResponseEntity<String> receiveCards(@RequestBody MedicalBatchDTO medicalBatchDTO) throws IllegalAccessException, IOException, BadRequestException {
        medicalCardsService.receiveCards(medicalBatchDTO);
        return new ResponseEntity<String>("OK",HttpStatus.OK);
    }

    @RequestMapping(value = { "processBatchClaims" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    public ResponseEntity<String> processBatchClaims(@RequestBody MedicalTransDTO transDTO) throws IllegalAccessException, IOException, BadRequestException {
        medicalTransService.processClaimBatchTrans(transDTO);
        return new ResponseEntity<String>("OK",HttpStatus.OK);
    }

    @RequestMapping(value = { "requestCards" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    public ResponseEntity<String> requestCards(@RequestBody MedicalBatchDTO medicalBatchDTO) throws IllegalAccessException, IOException, BadRequestException {
        medicalCardsService.requestCards(medicalBatchDTO);
        return new ResponseEntity<String>("OK",HttpStatus.OK);
    }

    @ExceptionHandler(EndorsementsException.class)
    public ModelAndView getSuperheroesUnavailable(EndorsementsException ex) {
        ModelAndView mv = new ModelAndView("preauth", "error", ex.getMessage());
        mv.addObject("modelHelperForm", getModelHelperForm());
        return mv;
    }

    @RequestMapping(value = "/convertpreauth", method = RequestMethod.POST)
    public String convertPreuath(@Valid @ModelAttribute ModelHelperForm helperForm, Model model,HttpServletRequest request) throws EndorsementsException {
        try {
            Long converted = clmService.convertPreauthTrans(helperForm.getId());
            request.getSession().setAttribute("parCode", converted);
            return "redirect:/protected/medical/claims/allclaimtrans";
        }
        catch (BadRequestException ex){
            throw new EndorsementsException(ex.getMessage());
        }
    }

    @RequestMapping(value = "/allclaimtrans", method = RequestMethod.GET)
    public String allclaimtrans( HttpServletRequest request) throws BadRequestException {
            return "medtrans";
    }

    @RequestMapping(value = { "createRequests" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    @ResponseBody
    public void createRequests(MedicalParRequest parRequest,
                                                  HttpServletRequest request) throws BadRequestException {
        Long parCode = (Long) request.getSession().getAttribute("parCode");
        parRequest.setParTrans(clmService.getMedicalClaimDetails(parCode));
        clmService.createRequests(parRequest);
    }

    @RequestMapping(value = { "createRequestServices" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    @ResponseBody
    public void createRequestServices( MedicalRequestServices requestServices,
                                                 HttpServletRequest request) throws BadRequestException {
        clmService.createServices(requestServices);
    }

    @RequestMapping(value = { "createClmRequests" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    @ResponseBody
    public void createClmRequests(MedicalParRequest parRequest,
                               HttpServletRequest request) throws BadRequestException {
        Long parCode = (Long) request.getSession().getAttribute("parCode");
        parRequest.setParTrans(clmService.getMedicalClaimDetails(parCode));
        clmService.createClmRequests(parRequest);
    }

    @RequestMapping(value = { "createClmRequestServices" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
    @ResponseBody
    public void createClmRequestServices( MedicalRequestServices requestServices,
                                       HttpServletRequest request) throws BadRequestException {
        clmService.createClmServices(requestServices);
    }

    @RequestMapping(value = { "deleteRequests/{requestId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRequests(@PathVariable Long requestId) throws BadRequestException {
        clmService.deleteRequests(requestId);
    }

    @RequestMapping(value = { "deleteServices/{serviceId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteServices(@PathVariable Long serviceId) throws BadRequestException {
        clmService.deleteServices(serviceId);
    }

    @RequestMapping(value = { "deleteClmRequests/{requestId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClmRequests(@PathVariable Long requestId) throws BadRequestException {
        clmService.deleteClmRequests(requestId);
    }

    @RequestMapping(value = { "deleteClmServices/{serviceId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClmServices(@PathVariable Long serviceId) throws BadRequestException {
        clmService.deleteClmServices(serviceId);
    }

    @RequestMapping(value = { "selpolicies" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseBody
    public Page<PolicyTrans> SelectMedicalPolicies(@RequestParam(value = "term", required = false) String term, Pageable pageable)
            throws IllegalAccessException {
        return clmService.findMedicalPolicies(term, pageable);
    }

    @RequestMapping(value = "rpt_inp_letter", method = RequestMethod.GET)
    public ModelAndView inpatientRpt(ModelMap modelMap, HttpServletRequest request, ModelAndView modelAndView)
            throws BadRequestException, IOException {
        Long parCode = (Long) request.getSession().getAttribute("parCode");
        OrganizationDTO organization = orgService.getOrganizationLogoDetails();
        InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
        BufferedImage image = ImageIO.read(in);
        modelMap.put("logo", image );
        modelMap.put("datasource", datasource);
        modelMap.put("format", "pdf");
        modelMap.put("parId", parCode);
        modelAndView = new ModelAndView("rpt_inp_undertaking_letter", modelMap);
        return modelAndView;
    }

    @RequestMapping(value = "rpt_outp_letter", method = RequestMethod.GET)
    public ModelAndView outpatientRpt(ModelMap modelMap, HttpServletRequest request, ModelAndView modelAndView)
            throws BadRequestException, IOException {
        Long parCode = (Long) request.getSession().getAttribute("parCode");
        OrganizationDTO organization = orgService.getOrganizationLogoDetails();
        InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
        BufferedImage image = ImageIO.read(in);
        modelMap.put("logo", image );
        modelMap.put("datasource", datasource);
        modelMap.put("format", "pdf");
        modelMap.put("parId", parCode);
        modelAndView = new ModelAndView("rpt_outp_undertaking_letter", modelMap);
        return modelAndView;
    }

    @RequestMapping(value = "rpt_decline_letter", method = RequestMethod.GET)
    public ModelAndView declineRpt(ModelMap modelMap, HttpServletRequest request, ModelAndView modelAndView)
            throws BadRequestException, IOException {
        Long parCode = (Long) request.getSession().getAttribute("parCode");
        OrganizationDTO organization = orgService.getOrganizationLogoDetails();
        InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
        BufferedImage image = ImageIO.read(in);
        modelMap.put("logo", image );
        modelMap.put("datasource", datasource);
        modelMap.put("format", "pdf");
        modelMap.put("parId", parCode);
        modelAndView = new ModelAndView("rpt_med_clm_rejection_letter", modelMap);
        return modelAndView;
    }


    @RequestMapping(value = { "getCategoryMemberInfo/{memberId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    public ResponseEntity<String> getCategoryMemberInfo(@PathVariable Long memberId) throws BadRequestException {
       String  memberInfo = medicalTransService.getFamilyDetails(memberId);
        return new ResponseEntity<String>(memberInfo, HttpStatus.OK);
    }

    @RequestMapping(value = { "getCategoryMemberDetails/{memberId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    public ResponseEntity<CategoryMembers> getCategoryMemberDetails(@PathVariable Long memberId) throws BadRequestException {
        CategoryMembers  memberInfo = medicalTransService.getCategoryMemberDetails(memberId);
        return new ResponseEntity<CategoryMembers>(memberInfo, HttpStatus.OK);
    }

    @RequestMapping(value = { "servicesLog/{serviceId}" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<RequestServiceLog> getMedicalServicesLogs(@DataTable DataTablesRequest pageable, @PathVariable Long serviceId)
            throws IllegalAccessException {
        return clmService.getRequestServiceLog(pageable,serviceId);
    }

    @RequestMapping(value = { "sendEmail" }, method = {org.springframework.web.bind.annotation.RequestMethod.POST })
    public ResponseEntity<String>  sendEmail(@RequestBody MailMessageBean messageBean, HttpServletRequest request) throws BadRequestException {
        Long parCode = (Long) request.getSession().getAttribute("parCode");
        mailer.sendEmailAttachments(messageBean,parCode,"M",request);
        return new ResponseEntity<String>("OK", HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value ="mailtemplate" )
    public String getMailTemplate(HttpServletResponse response, HttpServletRequest request) throws BadRequestException{
        return templateService.getMailTemplate(MailTemplates.MED_CLAIMS_TEMPLATE,request);
    }

    @RequestMapping(value = { "getReceiverEmail" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    public ResponseEntity<String> getReceiverEmail(@RequestParam(value = "receiver", required = false) String receiver, HttpServletRequest request) throws BadRequestException {
        Long parCode = (Long) request.getSession().getAttribute("parCode");
        String email = mailer.getEmailReceivers(parCode,"M",receiver);
        return new ResponseEntity<String>(email, HttpStatus.OK);
    }

    @RequestMapping(value = { "pardocuments" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<MedParReqDocs> getParDocuments(@DataTable DataTablesRequest pageable, HttpServletRequest request)
            throws IllegalAccessException {
        Long parCode = (Long) request.getSession().getAttribute("parCode");
        return clmService.getParDocs(pageable,parCode);
    }

    @RequestMapping(value = { "uploadRequiredDocs" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadRequiredDocs(MedParReqDocs parReqDocs, HttpServletRequest request) throws BadRequestException {
        Long parCode = (Long) request.getSession().getAttribute("parCode");
        MedicalParTrans parTrans = clmService.getMedicalClaimDetails(parCode);
        parReqDocs.setParTrans(parTrans);
        uploadService.uploadClaimDocument(parReqDocs);
    }

    @RequestMapping(value = "/claimdocument/{docId}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> thumbnail(@PathVariable Long docId ) throws BadRequestException {
        byte[] content = uploadService.getMedClmDocFileDetails(docId);
        if (content.length>0) {
            String contentType = uploadService.getClmDocContentTyoe(docId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentLength(content.length);
            return new ResponseEntity<byte[]>(content, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<byte[]>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = { "deleteclmDoc/{docId}" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRiskDoc(@PathVariable Long docId) throws BadRequestException {
        uploadService.deleteClmDoc(docId);
    }

    @RequestMapping(value = { "saveMemberCard" }, method = {
            org.springframework.web.bind.annotation.RequestMethod.POST })
    @ResponseBody
    public void saveMemberCard(MedicalCards medicalCards) throws IllegalAccessException, IOException, BadRequestException {
        medicalCardsService.saveMemberCardNo(medicalCards);
    }



}
