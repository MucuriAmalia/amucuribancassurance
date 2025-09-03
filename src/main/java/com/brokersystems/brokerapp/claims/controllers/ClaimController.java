package com.brokersystems.brokerapp.claims.controllers;

import com.brokersystems.brokerapp.claims.dtos.*;
import com.brokersystems.brokerapp.claims.repository.*;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.setup.dto.PaymentModesDTO;
import com.brokersystems.brokerapp.setup.repository.ClmStatusRepo;
import com.brokersystems.brokerapp.setup.repository.PaymentModeRepo;
import com.google.gson.Gson;
import com.brokersystems.brokerapp.claims.exception.ClaimException;
import com.brokersystems.brokerapp.claims.model.*;
import com.brokersystems.brokerapp.claims.service.ClaimService;
import com.brokersystems.brokerapp.dms.service.UploadService;
import com.brokersystems.brokerapp.mail.model.MailMessageBean;
import com.brokersystems.brokerapp.mail.model.MailTemplates;
import com.brokersystems.brokerapp.mail.service.MailTemplateService;
import com.brokersystems.brokerapp.mail.service.Mailer;
import com.brokersystems.brokerapp.quotes.services.QuotationService;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.setup.dto.OrganizationDTO;
import com.brokersystems.brokerapp.setup.dto.SubClassReqdDocsDTO;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.service.OrganizationService;
import com.brokersystems.brokerapp.users.service.MakerCheckerService;
import com.brokersystems.brokerapp.uw.repository.SectionTransRepo;
import com.mysema.query.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by peter on 3/5/2017.
 */
@Controller
@RequestMapping({ "/protected/claims" })
@Slf4j
public class ClaimController {

    @Autowired
    private ClaimService claimService;

    @Autowired
    private MakerCheckerService makerCheckerService;

    @Autowired
    private PaymentModeRepo paymentModeRepo;

    @Autowired
    private ClmActivitiesRepo clmActivitiesRepo;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private ClmStatusRepo clmStatusRepo;

    @Autowired
    private ClaimPerilsRepo claimPerilsRepo;

    @Autowired
    private ClaimsBookingRepo claimsBookingRepo;

    @Autowired
    private OrganizationService orgService;

    @Autowired
    private DataSource datasource;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private AuditTrailLogger auditTrailLogger;

    @Autowired
    private Mailer mailer;

    @Autowired
    private MailTemplateService templateService;

    @Autowired
    private QuotationService quotationService;

    @Autowired
    private SectionTransRepo sectionTransRepo;
    @Autowired
    private ClaimRequiredDocsRepo claimRequiredDocsRepo;
    @Autowired
    private ClaimUploadRepo claimUploadRepo;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @ModelAttribute
    public ClaimForm getClaimForm(){
        return new ClaimForm();
    }

    @RequestMapping(value = "newclaim",method={RequestMethod.GET})
    public String newClaim(Model model, HttpServletRequest request)
    {
        auditTrailLogger.log("Accessed New Claim Screen ",request, "New Claim");
        return "newclaim";
    }

    @RequestMapping(value = "newclaim2",method={RequestMethod.GET})
    public String newclaim2(Model model, HttpServletRequest request)
    {
        auditTrailLogger.log("Accessed New Claim Screen ",request, "New Claim");
        return "newclaim2";
    }

    @RequestMapping(value = "enquireclaims",method={RequestMethod.GET})
    public String enquireClaims(Model model, HttpServletRequest request)
    {
        auditTrailLogger.log("Accessed Claim Transactions Screen ",request, "Claim Transactions");
        return "clmEnquiry";
    }

    @RequestMapping(value = "claimants",method={RequestMethod.GET})
    public String newClaimants(Model model, HttpServletRequest request)
    {
        auditTrailLogger.log("Accessed Claimants Screen ",request, "Claimants");
        return "claimants";
    }

    @RequestMapping(value = "serviceproviders",method={RequestMethod.GET})
    public String newserviceProviders(Model model, HttpServletRequest request)
    {
        auditTrailLogger.log("Accessed Service Providers Screen ",request, "Service Providers");
        return "serviceproviderform";
    }

    @RequestMapping(value = { "selprovidertypes" }, method = { RequestMethod.GET })
    @ResponseBody
    public Page<ServiceProviderTypesDTO> selectProviderTypes(@RequestParam(value = "term", required = false) String term, Pageable pageable) {
        return claimService.findServiceProviderTypes(term, pageable);
    }

    @RequestMapping(value = { "selproviders" }, method = { RequestMethod.GET })
    @ResponseBody
    public Page<ServiceProviderDTO> selectProviders(@RequestParam(value = "term", required = false) String term, Pageable pageable) {
        return claimService.findServiceProviders(term, pageable);
    }

    @RequestMapping(value = { "selLossRisks" }, method = { RequestMethod.GET })
    @ResponseBody
    public Page<ClaimRisksDTO> selRisks(@RequestParam(value = "term", required = false) String term, Pageable pageable, @RequestParam("lossDate")Date lossDate)
            throws IllegalAccessException {
        return claimService.findRisksToClaim(term,lossDate,pageable);
    }

    @RequestMapping(value = { "selclmActivity" }, method = { RequestMethod.GET })
    @ResponseBody
    public Page<ClmCausations> selclmActivity(@RequestParam(value = "term", required = false) String term, Pageable pageable)
            throws IllegalAccessException {
        return claimService.findClaimStatuses(term,pageable);
    }

    @RequestMapping(value = { "claimantdefs" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<ClaimantsDTO> getAllClaimants(@DataTable DataTablesRequest pageable) throws IllegalAccessException {
        return claimService.findAllClaimants(pageable);
    }

    @RequestMapping(value = { "createClaimant" }, method = {
            RequestMethod.POST })
    @ResponseBody
    public void createClaimant(ClaimantsDef claimantsDef) throws IllegalAccessException, IOException, BadRequestException {
        claimService.defineClaimant(claimantsDef);
    }

    @RequestMapping(value = { "createPerilPayment" }, method = {
            RequestMethod.POST })
    @ResponseBody
    public void createClaimant(ClaimPerilPayments perilPayments) throws IllegalAccessException, IOException, BadRequestException {
        claimService.capturePerilPayment(perilPayments);
    }

    @RequestMapping(value = { "createClaimPeril" }, method = {
            RequestMethod.POST })
    @ResponseBody
    public void createClaimant(ClaimPerils claimPerils) throws IllegalAccessException, IOException, BadRequestException {
        claimService.captureClaimPerils(claimPerils);
    }

    @RequestMapping(value = { "sendEmail" }, method = {org.springframework.web.bind.annotation.RequestMethod.POST })
    public ResponseEntity<String>  sendEmail(@RequestBody MailMessageBean messageBean, HttpServletRequest request) throws BadRequestException {
        System.out.println(messageBean);
        Long clmId = (Long) request.getSession().getAttribute("claimId");
        mailer.sendEmailAttachments(messageBean,clmId,"C",request);
        return new ResponseEntity<String>("OK", HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value ="mailtemplate" )
    public String getMailTemplate(HttpServletResponse response, HttpServletRequest request) throws BadRequestException{
        return templateService.getMailTemplate(MailTemplates.CLAIMS_TEMPLATE,request);
    }


    @RequestMapping(value = { "getReceiverEmail" }, method = {
            RequestMethod.GET })
    public ResponseEntity<String> getReceiverEmail(@RequestParam(value = "receiver", required = false) String receiver, HttpServletRequest request) throws BadRequestException {
        Long clmId = (Long) request.getSession().getAttribute("claimId");
        String email = mailer.getEmailReceivers(clmId,"C",receiver);
        return new ResponseEntity<String>(email, HttpStatus.OK);
    }



    @RequestMapping(value = { "createClaimantPeril" }, method = {
            RequestMethod.POST })
    @ResponseBody
    public void createClaimantPeril(PerilBean perilBean,HttpServletRequest request) throws IllegalAccessException, IOException, BadRequestException {
        Long clmId = (Long) request.getSession().getAttribute("claimId");
        perilBean.setClaimId(clmId);
        claimService.createClaimantPeril(perilBean);
    }



    @RequestMapping(value = { "deleteClaimant/{claimantId}" }, method = {
            RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClaimant(@PathVariable Long claimantId) {
        claimService.deleteClaimant(claimantId);
    }

    @RequestMapping(value = { "deleteClaimantPeril/{clmPerilId}" }, method = {
            RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClaimantPeril(@PathVariable Long clmPerilId) {
        claimService.deleteClaimantPeril(clmPerilId);
    }

    @RequestMapping(value = { "deletePerilPayment/{clmPymntId}" }, method = {
            RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePerilPayment(@PathVariable Long clmPymntId) {
        claimService.deletePerilPayment(clmPymntId);
    }



    @RequestMapping(value = { "deleteClaimClaimant/{claimantId}" }, method = {
            RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClaimClaimant(@PathVariable Long claimantId) {
        claimService.deleteClaimClaimant(claimantId);
    }



    @RequestMapping(value = { "selOccupations" }, method = { RequestMethod.GET })
    @ResponseBody
    public Page<Occupation> selOccupations(@RequestParam(value = "term", required = false) String term, Pageable pageable)
            throws IllegalAccessException {
        return claimService.findOccupations(term,pageable);
    }

    @RequestMapping(value = "/claimtrans", method = RequestMethod.POST)
    public String editPolicyForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model, HttpServletRequest request) {
        log.info("Processing claim transaction with claim ID: {}", helperForm.getId());
        Long clmId = helperForm.getId();

        // Validate claim ID
        if (clmId == null) {
            log.error("Claim ID is null");
            model.addAttribute("error", "Claim ID cannot be null");
            return "claimdetails";
        }

        // Fetch claim details
        ClaimDetailsDTO claimDetails;
        try {
            claimDetails = claimService.getClaimInformation(clmId);
        } catch (BadRequestException e) {
            log.error("Error fetching claim details for clmId: {}", clmId, e);
            model.addAttribute("error", e.getMessage());
            return "claimdetails";
        }

        if (claimDetails == null || claimDetails.getClaimNo() == null) {
            log.error("Claim details are null or empty for clmId: {}", clmId);
            model.addAttribute("error", "Claim not found or no details available for clmId: " + clmId);
            return "claimdetails";
        }

        // Set model and session attributes
        model.addAttribute("clmId", clmId);
        request.getSession().setAttribute("claimId", clmId);

        // Handle claim status
        String claimStatus = claimDetails.getClaimStatus();
        String displayClaimStatus;
        if (claimStatus == null) {
            displayClaimStatus = "Unknown";
            log.warn("claimStatus is null for clmId: {}", clmId);
        } else if ("O".equalsIgnoreCase(claimStatus) || "B".equalsIgnoreCase(claimStatus)) {
            displayClaimStatus = "Open";
        } else if ("R".equalsIgnoreCase(claimStatus)) {
            displayClaimStatus = "Re-Open";
        } else if ("C".equalsIgnoreCase(claimStatus)) {
            displayClaimStatus = "Closed";
        } else {
            displayClaimStatus = "Unknown";
            log.warn("Unexpected claimStatus: {} for clmId: {}", claimStatus, clmId);
        }
        model.addAttribute("claimStatus", displayClaimStatus);

        // Set approval status and taskId/mckClmId based on approval status
        String approvalStatus = claimDetails.getApprovalStatus() != null ? claimDetails.getApprovalStatus() : "Unknown";
        Long taskId = claimDetails.getTaskId();
        if ("A".equals(approvalStatus) || "C".equals(claimStatus)) {
            // For approved or closed claims, use clmId as mckClmId since no new taskId is needed
            model.addAttribute("mckClmId", clmId);
            model.addAttribute("taskId", null); // No taskId needed for approved/closed claims
        } else {
            // For pending claims, use the taskId from claimDetails
            model.addAttribute("mckClmId", taskId != null ? taskId : clmId);
            model.addAttribute("taskId", taskId);
        }
        model.addAttribute("approvalStatus", approvalStatus);
        model.addAttribute("claimDetails", claimDetails);

        log.info("Claim details for clmId={}: claimNo={}, insured={}, claimStatus={}, approvalStatus={}, taskId={}",
                clmId, claimDetails.getClaimNo(), claimDetails.getInsured(), displayClaimStatus, approvalStatus, taskId);
        return "claimdetails";
    }
//    @RequestMapping(value = "/claimtrans", method = RequestMethod.POST)
//    public String editPolicyForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model, HttpServletRequest request) {
//        log.info("Processing claim transaction with claim ID: {}", helperForm.getId());
//        Long clmId = helperForm.getId();
//
//        // Validate claim ID
//        if (clmId == null) {
//            log.error("Claim ID is null");
//            model.addAttribute("error", "Claim ID cannot be null");
//            return "claimdetails";
//        }
//
//        // Fetch claim details
//        ClaimDetailsDTO claimDetails;
//        try {
//            claimDetails = claimService.getClaimInformation(clmId);
//        } catch (BadRequestException e) {
//            log.error("Error fetching claim details for clmId: {}", clmId, e);
//            model.addAttribute("error", e.getMessage());
//            return "claimdetails";
//        }
//
//        if (claimDetails == null || claimDetails.getClaimNo() == null) {
//            log.error("Claim details are null or empty for clmId: {}", clmId);
//            model.addAttribute("error", "Claim not found or no details available for clmId: " + clmId);
//            return "claimdetails";
//        }
//
//        // Set model and session attributes
//        model.addAttribute("clmId", clmId);
//        request.getSession().setAttribute("claimId", clmId);
//
//        // Handle claim status
//        String claimStatus = claimDetails.getClaimStatus();
//        String displayClaimStatus;
//        if (claimStatus == null) {
//            displayClaimStatus = "Unknown";
//            log.warn("claimStatus is null for clmId: {}", clmId);
//        } else if ("O".equalsIgnoreCase(claimStatus) || "B".equalsIgnoreCase(claimStatus)) {
//            displayClaimStatus = "Open";
//        } else if ("R".equalsIgnoreCase(claimStatus)) {
//            displayClaimStatus = "Re-Open";
//        } else if ("C".equalsIgnoreCase(claimStatus)) {
//            displayClaimStatus = "Closed";
//        } else {
//            displayClaimStatus = "Unknown";
//            log.warn("Unexpected claimStatus: {} for clmId: {}", claimStatus, clmId);
//        }
//        model.addAttribute("claimStatus", displayClaimStatus);
//        model.addAttribute("approvalStatus", claimDetails.getApprovalStatus() != null ? claimDetails.getApprovalStatus() : "Unknown");
//        model.addAttribute("claimDetails", claimDetails);
//
//        log.info("Claim details for clmId={}: claimNo={}, insured={}, claimStatus={}, approvalStatus={}",
//                clmId, claimDetails.getClaimNo(), claimDetails.getInsured(), displayClaimStatus, claimDetails.getApprovalStatus());
//        return "claimdetails";
//    }
//    @RequestMapping(value = "/claimtrans", method = RequestMethod.POST)
//    public String editPolicyForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model, HttpServletRequest request) throws BadRequestException {
//        log.info("Processing claim transaction with claim ID: {}", helperForm.getId());
//        Long clmId = helperForm.getId();
//
//        // Validate claim ID
//        if (clmId == null) {
//            log.error("Claim ID is null");
//            model.addAttribute("error", "Claim ID cannot be null");
//            return "claimdetails";
//        }
//
//        // Fetch claim details
//        ClaimDetailsDTO claimDetails = claimService.getClaimInformation(clmId);
//        if (claimDetails == null) {
//            log.error("Claim not found for clmId: {}", clmId);
//            model.addAttribute("error", "Claim not found: " + clmId);
//            return "claimdetails";
//        }
//
//        // Set model and session attributes
//        model.addAttribute("clmId", clmId);
//        request.getSession().setAttribute("claimId", clmId);
//
//        // Handle claim status
//        String claimStatus = claimDetails.getClaimStatus();
//        String displayClaimStatus;
//        if (claimStatus == null) {
//            displayClaimStatus = "Unknown";
//            log.warn("claimStatus is null for clmId: {}", clmId);
//        } else if ("O".equalsIgnoreCase(claimStatus) || "B".equalsIgnoreCase(claimStatus)) {
//            displayClaimStatus = "Open";
//        } else if ("R".equalsIgnoreCase(claimStatus)) {
//            displayClaimStatus = "Re-Open";
//        } else if ("C".equalsIgnoreCase(claimStatus)) {
//            displayClaimStatus = "Closed";
//        } else {
//            displayClaimStatus = "Unknown";
//            log.warn("Unexpected claimStatus: {} for clmId: {}", claimStatus, clmId);
//        }
//        model.addAttribute("claimStatus", displayClaimStatus);
//        model.addAttribute("approvalStatus", claimDetails.getApprovalStatus());
//        model.addAttribute("claimDetails", claimDetails);
//
//        log.info("Claim details for clmId={}: claimStatus={}, approvalStatus={}", clmId, displayClaimStatus, claimDetails.getApprovalStatus());
//        return "claimdetails";
//    }
//    @RequestMapping(value = "/claimtrans", method = RequestMethod.POST)
//    public String editPolicyForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model, HttpServletRequest request) throws BadRequestException {
//        log.info("Processing claim transaction with claim ID: {}", helperForm.getId());
//        Long clmId = helperForm.getId();
//
//        if (clmId == null) {
//            log.error("Claim ID is null");
//            throw new BadRequestException("Claim ID cannot be null");
//        }
//
//        // Fetch claim details, including unapproved claims
//        ClaimDetailsDTO claimDetails = claimService.getClaimInformation(clmId);
//        if (claimDetails == null) {
//            log.error("Claim not found for clmId: {}", clmId);
//            throw new BadRequestException("Claim not found: " + clmId);
//        }
//
//        // Set model and session attributes
//        model.addAttribute("clmId", clmId);
//        request.getSession().setAttribute("claimId", clmId);
//
//        // Handle claim status
//        String claimStatus = claimDetails.getClaimStatus();
//        if (claimStatus == null) {
//            model.addAttribute("claimStatus", "Unknown");
//        } else if ("O".equalsIgnoreCase(claimStatus) || "B".equalsIgnoreCase(claimStatus)) {
//            model.addAttribute("claimStatus", "Open");
//        } else if ("R".equalsIgnoreCase(claimStatus)) {
//            model.addAttribute("claimStatus", "Re-Open");
//        } else if ("C".equalsIgnoreCase(claimStatus)) {
//            model.addAttribute("claimStatus", "Closed");
//        } else {
//            model.addAttribute("claimStatus", "Unknown");
//        }
//
//        model.addAttribute("approvalStatus", claimDetails.getApprovalStatus());
//        model.addAttribute("claimDetails", claimDetails);
//
//        return "claimdetails";
//    }
//    @RequestMapping(value = "/claimtrans", method = RequestMethod.POST)
//    public String editPolicyForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model, HttpServletRequest request) throws BadRequestException {
//        log.info("Processing claim transaction with claim ID: {}", helperForm.getId());
//        Long clmId = helperForm.getId();
//
//        // Validate claim ID
//        if (clmId == null) {
//            log.error("Claim ID is null");
//            throw new BadRequestException("Claim ID cannot be null");
//        }
//
//        // Fetch claim details
//        ClaimDetailsDTO claimDetails = claimService.getClaimInformation(clmId);
//        if (claimDetails == null) {
//            log.error("Claim not found for clmId: {}", clmId);
//            throw new BadRequestException("Claim not found: " + clmId);
//        }
//
//        // Set model and session attributes
//        model.addAttribute("clmId", clmId);
//        request.getSession().setAttribute("claimId", clmId);
//
//        // Set claim status for UI
//
////        String claimStatus = claimDetails.getClaimStatus();
////        if (claimStatus == null) {
////            model.addAttribute("claimStatus", "Unknown");
////        } else if ("O".equalsIgnoreCase(claimStatus) || "B".equalsIgnoreCase(claimStatus)) {
////            model.addAttribute("claimStatus", "Open");
////        } else if ("R".equalsIgnoreCase(claimStatus)) {
////            model.addAttribute("claimStatus", "Re-Open");
////        } else if ("C".equalsIgnoreCase(claimStatus)) {
////            model.addAttribute("claimStatus", "Closed");
////        } else {
////            model.addAttribute("claimStatus", "Unknown");
////        }
//
//        // Set approval status
//        model.addAttribute("approvalStatus", claimDetails.getApprovalStatus());
//        model.addAttribute("claimDetails", claimDetails);
//
//        return "claimdetails";
//    }


//    @RequestMapping(value = "/claimtrans", method = RequestMethod.POST)
//    public String editPolicyForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model, HttpServletRequest request) {
//        model.addAttribute("clmId", helperForm.getId());
//        request.getSession().setAttribute("claimId", helperForm.getId());
//        return "claimdetails";
//    }

    @RequestMapping(value = { "getInhouseEmail" }, method = {
            RequestMethod.GET })
    public ResponseEntity<String> getInhouseEmail() throws BadRequestException {
        String email = quotationService.getInhouseEmail();
        return new ResponseEntity<String>(email, HttpStatus.OK);
    }

//    @RequestMapping(value = "newclaim", method = RequestMethod.POST)
//    public ModelAndView createClaim(@ModelAttribute("claimForm") ClaimForm claimForm,
//                                    BindingResult result,
//                                    RedirectAttributes redirectAttrs,
//                                    HttpServletRequest request) throws BadRequestException {
//        ModelAndView mav;
//
//        // Validate form
//        if (result.hasErrors()) {
//            redirectAttrs.addFlashAttribute("error", "Invalid form submission");
//            redirectAttrs.addFlashAttribute("claimForm", claimForm);
//            return new ModelAndView("redirect:/protected/claims/newclaim2");
//        }
//
//        try {
//            // Create claim
//            boolean isApproved = false;
//            Long claimId = claimService.createClaim(claimForm, isApproved);
//            log.info("Created claim with ID: {}", claimId);
//
//            // Set session attribute first
//            request.getSession().setAttribute("claimId", claimId);
//
//            // Fetch claim details
//            ClaimDetailsDTO claimDetails = claimService.getClaimInformation(claimId);
//            if (claimDetails == null) {
//                throw new BadRequestException("Unable to retrieve claim details");
//            }
//
//            // Initialize view
//            mav = new ModelAndView("claimdetails");
//
//            // Set essential attributes
//            mav.addObject("clmId", claimId);
//            mav.addObject("claimNo", claimDetails.getClaimNo());
//            mav.addObject("insured", claimDetails.getInsured());
//            mav.addObject("lossDesc", claimDetails.getLossDesc());
//            mav.addObject("riskIdentifier", claimDetails.getRiskIdentifier());
//            mav.addObject("notificationDate", claimDetails.getNotificationDate());
//            mav.addObject("lossDate", claimDetails.getLossDate());
//            mav.addObject("nextRvwDate", claimDetails.getNextRvwDate());
//            mav.addObject("liabilityAdmission", claimDetails.getLiabilityAdmission());
//            mav.addObject("claimDetails", claimDetails);
//
//            // Handle claim status
//            String claimStatus = claimDetails.getClaimStatus();
//            if ("B".equals(claimStatus)) {
//                mav.addObject("claimStatus", "Booked");
//            } else if ("O".equals(claimStatus)) {
//                mav.addObject("claimStatus", "Open");
//            } else if ("R".equals(claimStatus)) {
//                mav.addObject("claimStatus", "Re-Open");
//            } else if ("C".equals(claimStatus)) {
//                mav.addObject("claimStatus", "Closed");
//            } else {
//                mav.addObject("claimStatus", claimStatus);
//            }
//
//            // Set approval status
//            mav.addObject("approvalStatus", claimDetails.getApprovalStatus());
//            mav.addObject("success", "Claim created successfully");
//
//            log.info("Claim details retrieved successfully for ID: {}, Status: {}, Approval: {}",
//                    claimId, claimStatus, claimDetails.getApprovalStatus());
//
//        } catch (ClaimException | BadRequestException e) {
//            log.error("Error creating claim: {}", e.getMessage());
//            redirectAttrs.addFlashAttribute("error", e.getMessage());
//            redirectAttrs.addFlashAttribute("claimForm", claimForm);
//            return new ModelAndView("redirect:/protected/claims/newclaim2");
//        } catch (Exception e) {
//            log.error("Unexpected error creating claim", e);
//            redirectAttrs.addFlashAttribute("error", "System error occurred while creating claim");
//            redirectAttrs.addFlashAttribute("claimForm", claimForm);
//            return new ModelAndView("redirect:/protected/claims/newclaim2");
//        }
//
//        return mav;
//    }
//@RequestMapping(value = "newclaim",method = RequestMethod.POST)
//public ModelAndView createClaim(@ModelAttribute("claimForm")ClaimForm claimForm, BindingResult result, RedirectAttributes redirectAttrs,HttpServletRequest request) throws BadRequestException {
//    Long claimId = null;
//    try{
//        claimId = claimService.createClaim(claimForm);
//    }
//    catch (ClaimException ex){
//        redirectAttrs.addFlashAttribute("error", ex.getMessage());
//        redirectAttrs.addFlashAttribute("claimForm", claimForm);
//        return new ModelAndView("redirect:/protected/claims/newclaim2");
//    }
//    request.getSession().setAttribute("claimId", claimId);
//    return new ModelAndView("claimdetails","clmId",claimId);
//}
//@RequestMapping(value = "newclaim", method = RequestMethod.POST)
//public ModelAndView createClaim(@ModelAttribute("claimForm") ClaimForm claimForm,
//                                BindingResult result,
//                                RedirectAttributes redirectAttrs,
//                                HttpServletRequest request) throws BadRequestException {
//    Long claimId = null;
//    try {
//        claimId = claimService.createClaim(claimForm, false);
//
//        // Store claim ID in session
//        request.getSession().setAttribute("claimId", claimId);
//
//        // Fetch claim details
//        ClaimDetailsDTO claimDetails = claimService.getClaimInformation(claimId);
//
//        // Create ModelAndView with ALL required attributes
//        ModelAndView mav = new ModelAndView("claimdetails");
//
//        // Add all attributes seen in maker-checker flow
//        mav.addObject("clmId", claimId);
//        mav.addObject("claimDetails", claimDetails);
//
//        // These are critical for the view to work properly
//        String randomCode = UUID.randomUUID().toString();
//        mav.addObject("mckClmCheckerCode", randomCode);
//
//        // For new claims, we can use the claimId as mckClmId temporarily
//        // until the actual maker-checker task is created
//        mav.addObject("mckClmId", claimId);
//        mav.addObject("taskId", claimId);
//
//        // Set approval status
//        mav.addObject("approvalStatus", "N"); // "N" for not approved
//
//        // Log the attributes for debugging
//        log.info("Model Attributes in Post-Creation Flow:");
//        log.info("clmId: {}", claimId);
//        log.info("claimDetails: {}", claimDetails);
//        log.info("mckClmCheckerCode: {}", randomCode);
//        log.info("approvalStatus: N");
//
//        return mav;
//
//    } catch (ClaimException ex) {
//        redirectAttrs.addFlashAttribute("error", ex.getMessage());
//        redirectAttrs.addFlashAttribute("claimForm", claimForm);
//        return new ModelAndView("redirect:/protected/claims/newclaim2");
//    }
//}
// Save Draft Claim
@RequestMapping(value = "saveDraftClaim", method = RequestMethod.POST)
@ResponseBody  // Add this for AJAX response
public ResponseEntity<Map<String, Object>> saveDraftClaim(@ModelAttribute("claimForm") ClaimForm claimForm,
                                                          HttpServletRequest request) throws BadRequestException {
    try {
        // Create claim but skip maker-checker
        Long claimId = claimService.createClaim(claimForm, false, true, "D");

        // Store claim ID in session for document uploads
        request.getSession().setAttribute("claimId", claimId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("claimId", claimId);
        response.put("message", "Claim saved as draft successfully");

        log.info("Draft claim created with ID: {}", claimId);
        return ResponseEntity.ok(response);

    } catch (ClaimException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }
}

    // Submit Draft for Approval
    @RequestMapping(value = "submitClaimForApproval", method = RequestMethod.POST)
    public ModelAndView submitClaimForApproval(HttpServletRequest request, RedirectAttributes redirectAttrs) throws BadRequestException {
        Long claimId = (Long) request.getSession().getAttribute("claimId");

        if (claimId == null) {
            throw new BadRequestException("No draft claim found in session");
        }

        try {
            // Submit for approval
            claimService.createMakerCheckerForDraftClaim(claimId);

            // Fetch claim details again
            ClaimDetailsDTO claimDetails = claimService.getClaimInformation(claimId);

            // Create ModelAndView with ALL required attributes (identical to newclaim)
            ModelAndView mav = new ModelAndView("claimdetails");

            // Add all attributes seen in maker-checker flow
            mav.addObject("clmId", claimId);
            mav.addObject("claimDetails", claimDetails);

            // These are critical for the view to work properly
            String randomCode = UUID.randomUUID().toString();
            mav.addObject("mckClmCheckerCode", randomCode);

            // Set mckClmId and taskId to null, but ensure claimDetails has enough data
            mav.addObject("mckClmId", claimDetails.getTaskId() != null ? claimDetails.getTaskId() : null);
            mav.addObject("taskId", claimDetails.getTaskId() != null ? claimDetails.getTaskId() : null);

            // Set approval status
            mav.addObject("approvalStatus", claimDetails.getApprovalStatus() != null ? claimDetails.getApprovalStatus() : "N");

            // Log the attributes for debugging (same as newclaim)
            log.info("Model Attributes in Post-Approval Flow:");
            log.info("clmId: {}", claimId);
            log.info("claimDetails: {}", claimDetails);
            log.info("mckClmCheckerCode: {}", randomCode);
            log.info("mckClmId: {}", claimDetails.getTaskId());
            log.info("taskId: {}", claimDetails.getTaskId());
            log.info("approvalStatus: {}", claimDetails.getApprovalStatus() != null ? claimDetails.getApprovalStatus() : "N");

            return mav;

        } catch (Exception e) {
            log.error("Failed to submit claim for approval: {}", e.getMessage(), e);
            redirectAttrs.addFlashAttribute("error", "Failed to submit claim: " + e.getMessage());
            return new ModelAndView("redirect:/protected/claims/newclaim2");
        }
    }



    @RequestMapping(value = "newclaim", method = RequestMethod.POST)
    public ModelAndView createClaim(@ModelAttribute("claimForm") ClaimForm claimForm,
                                    BindingResult result,
                                    RedirectAttributes redirectAttrs,
                                    HttpServletRequest request) throws BadRequestException {
        Long claimId = null;
        try {
            claimId = claimService.createClaim(claimForm, false, false,"D");

            // Store claim ID in session
            request.getSession().setAttribute("claimId", claimId);

            // Fetch claim details
            ClaimDetailsDTO claimDetails = claimService.getClaimInformation(claimId);

            // Create ModelAndView with ALL required attributes
            ModelAndView mav = new ModelAndView("claimdetails");

            // Add all attributes seen in maker-checker flow
            mav.addObject("clmId", claimId);
            mav.addObject("claimDetails", claimDetails);

            // These are critical for the view to work properly
            String randomCode = UUID.randomUUID().toString();
            mav.addObject("mckClmCheckerCode", randomCode);

            // Set mckClmId and taskId to null, but ensure claimDetails has enough data
            mav.addObject("mckClmId", claimDetails.getTaskId() != null ? claimDetails.getTaskId() : null); // Use taskId from claimDetails if available
            mav.addObject("taskId", claimDetails.getTaskId() != null ? claimDetails.getTaskId() : null);   // Use taskId from claimDetails if available

            // Set approval status
            mav.addObject("approvalStatus", claimDetails.getApprovalStatus() != null ? claimDetails.getApprovalStatus() : "N");

            // Log the attributes for debugging
            log.info("Model Attributes in Post-Creation Flow:");
            log.info("clmId: {}", claimId);
            log.info("claimDetails: {}", claimDetails);
            log.info("mckClmCheckerCode: {}", randomCode);
            log.info("mckClmId: {}", claimDetails.getTaskId());
            log.info("taskId: {}", claimDetails.getTaskId());
            log.info("approvalStatus: {}", claimDetails.getApprovalStatus() != null ? claimDetails.getApprovalStatus() : "N");

            return mav;

        } catch (ClaimException ex) {
            redirectAttrs.addFlashAttribute("error", ex.getMessage());
            redirectAttrs.addFlashAttribute("claimForm", claimForm);
            return new ModelAndView("redirect:/protected/claims/newclaim2");
        }
    }





    @ExceptionHandler(ClaimException.class)
    public ModelAndView getSuperheroesUnavailable(ClaimException ex) {
        ModelAndView mv = new ModelAndView("newclaim", "error", ex.getMessage());
        mv.addObject("claimForm", new ClaimForm());
        return mv;
    }

    @RequestMapping(value = { "selClaimants" }, method = { RequestMethod.GET })
    @ResponseBody
    public Page<ClaimantsDTO> selClaimants(@RequestParam(value = "term", required = false) String term, Pageable pageable) {
        return claimService.findAllClaimants(term, pageable);
    }

    @RequestMapping(value = { "selSubclassPerils" }, method = { RequestMethod.GET })
    @ResponseBody
    public Page<ClaimPerilDTO> selSubclassPerils(@RequestParam(value = "term", required = false) String term, Pageable pageable, @RequestParam("riskId")Long riskId)
            throws IllegalAccessException {
        return claimService.findSubclassPerils(term, pageable, riskId);
    }
//    @RequestMapping(value = "/checkerViewClaim", method = RequestMethod.POST)
//    public String checkerViewClaim(@Valid @ModelAttribute ModelHelperForm helperForm, Model model, HttpServletRequest request) throws BadRequestException {
//        log.info("Viewing Maker-Checker claim task with ID: {}", helperForm.getId());
//        Long taskId = helperForm.getId();
//
//        // Retrieve task JSON
//        Object taskData = makerCheckerService.findMakerCheckerTask(helperForm.getId());
//        log.info("Raw task data: {}", taskData);
//
//        // Convert to JSON string
//        String jsonString;
//        if (taskData == null) {
//            log.error("Task data is null for task ID: {}", helperForm.getId());
//            throw new BadRequestException("Task JSON is null");
//        } else if (taskData instanceof String) {
//            jsonString = (String) taskData;
//        } else {
//            // Handle non-string data (e.g., MakerChecker or JSON object)
//            try {
//                jsonString = new Gson().toJson(taskData);
//                log.info("Converted task data to JSON: {}", jsonString);
//            } catch (Exception e) {
//                log.error("Failed to convert task data to JSON for task ID: {}", helperForm.getId(), e);
//                throw new BadRequestException("Failed to parse task data: " + e.getMessage());
//            }
//        }
//
//        // Parse JSON
//        try {
//            JSONObject jsonObject = new JSONObject(jsonString);
//            // Try clmId as string first, then as number
//            String clmIdStr = jsonObject.optString("clmId", null);
//            Long clmId = null;
//            if (clmIdStr != null && !clmIdStr.isEmpty()) {
//                try {
//                    clmId = Long.parseLong(clmIdStr);
//                } catch (NumberFormatException e) {
//                    log.error("clmId is not a valid number: {}", clmIdStr);
//                }
//            }
//            if (clmId == null) {
//                clmId = jsonObject.optLong("clmId", 0);
//            }
//            if (clmId == null || clmId == 0) {
//                log.error("Claim ID not found or invalid in task JSON: {}", jsonString);
//                throw new BadRequestException("Claim ID not found in task JSON");
//            }
//            log.info("Parsed clmId: {}", clmId);
//
//            // Fetch claim details
//            ClaimDetailsDTO claimDetails = claimService.getClaimInformation(clmId);
//
//            if (claimDetails == null) {
//                log.error("Claim not found for clmId: {}", clmId);
//                throw new BadRequestException("Claim not found: " + clmId);
//            }
//            // Set model and session attributes
//            model.addAttribute("clmId", clmId);
//            request.getSession().setAttribute("claimId", clmId);
//            // Generate a unique code for the checker
//            String randomCode = UUID.randomUUID().toString();
//            model.addAttribute("mckClmCheckerCode", randomCode);
//            model.addAttribute("mckClmId", helperForm.getId());
//            model.addAttribute("taskId", taskId);
//            request.getSession().setAttribute("taskId", taskId);
//
//            model.addAttribute("approvalStatus", claimDetails.getApprovalStatus());
//
//            return "claimdetails";
//        } catch (JSONException e) {
//            log.error("Failed to parse task JSON: {}", jsonString, e);
//            throw new BadRequestException("Invalid task JSON: " + e.getMessage());
//        }
//    }
@RequestMapping(value = "/checkerViewClaim", method = RequestMethod.POST)
public String checkerViewClaim(@Valid @ModelAttribute ModelHelperForm helperForm, Model model, HttpServletRequest request) throws BadRequestException {
    log.info("Viewing Maker-Checker claim task with ID: {}", helperForm.getId());
    Long taskId = helperForm.getId();

    // Retrieve task JSON
    Object taskData = makerCheckerService.findMakerCheckerTask(helperForm.getId());
    log.info("Raw task data: {}", taskData);

    // Convert to JSON string
    String jsonString;
    if (taskData == null) {
        log.error("Task data is null for task ID: {}", helperForm.getId());
        throw new BadRequestException("Task JSON is null");
    } else if (taskData instanceof String) {
        jsonString = (String) taskData;
    } else {
        try {
            jsonString = new Gson().toJson(taskData);
            log.info("Converted task data to JSON: {}", jsonString);
        } catch (Exception e) {
            log.error("Failed to convert task data to JSON for task ID: {}", helperForm.getId(), e);
            throw new BadRequestException("Failed to parse task data: " + e.getMessage());
        }
    }

    // Parse JSON
    try {
        JSONObject jsonObject = new JSONObject(jsonString);
        String clmIdStr = jsonObject.optString("clmId", null);
        Long clmId = null;
        if (clmIdStr != null && !clmIdStr.isEmpty()) {
            try {
                clmId = Long.parseLong(clmIdStr);
            } catch (NumberFormatException e) {
                log.error("clmId is not a valid number: {}", clmIdStr);
            }
        }
        if (clmId == null) {
            clmId = jsonObject.optLong("clmId", 0);
        }
        if (clmId == null || clmId == 0) {
            log.error("Claim ID not found or invalid in task JSON: {}", jsonString);
            throw new BadRequestException("Claim ID not found in task JSON");
        }
        log.info("Parsed clmId: {}", clmId);

        // Fetch claim details
        ClaimDetailsDTO claimDetails = claimService.getClaimInformation(clmId);

        if (claimDetails == null) {
            log.error("Claim not found for clmId: {}", clmId);
            throw new BadRequestException("Claim not found: " + clmId);
        }

        // Set model and session attributes
        model.addAttribute("clmId", clmId);
        request.getSession().setAttribute("claimId", clmId);
        String randomCode = UUID.randomUUID().toString();
        model.addAttribute("mckClmCheckerCode", randomCode);
        model.addAttribute("mckClmId", helperForm.getId());
        model.addAttribute("taskId", taskId);
        request.getSession().setAttribute("taskId", taskId);
        model.addAttribute("approvalStatus", claimDetails.getApprovalStatus());

        // Log all model attributes before returning the view
        logModelAttributes(model);

        return "claimdetails";
    } catch (JSONException e) {
        log.error("Failed to parse task JSON: {}", jsonString, e);
        throw new BadRequestException("Invalid task JSON: " + e.getMessage());
    }
}
    @RequestMapping(value = "editRejectedClaim", method = RequestMethod.GET)
    public String editRejectedClaim(@RequestParam("claimId") String claimIdStr,
                                    @RequestParam("mode") String mode,
                                    Model model,
                                    HttpServletRequest request) throws BadRequestException {
        Long claimId;
        try {
            claimId = Long.parseLong(claimIdStr);
            log.info("Edit mode - claimId: {} (from string: {})", claimId, claimIdStr);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid claim ID: " + claimIdStr);
        }

        request.getSession().setAttribute("claimId", claimId);

        try {

            ClaimBookings claim = claimsBookingRepo.findOne(claimId);
            if (claim == null) {
                throw new BadRequestException("Claim not found: " + claimId);
            }

            if (!"R".equals(claim.getClaimStatus())) {
                throw new BadRequestException("Claim cannot be edited. Current status: " + claim.getClaimStatus());
            }

            ClaimForm claimForm = convertClaimToForm(claim);

            List<PerilBean> existingPerils = convertClaimPerilsToPerilBeans(claimId);

            model.addAttribute("claimForm", claimForm);
            model.addAttribute("editMode", true);
            model.addAttribute("claimId", claimId);
            model.addAttribute("originalClaimNo", claim.getClaimNo());
            model.addAttribute("existingPerils", existingPerils);


            return "newclaim2";

        } catch (Exception e) {
            throw new BadRequestException("Failed to load claim for editing: " + e.getMessage());
        }
    }
    private ClaimForm convertClaimToForm(ClaimBookings claim) {
        ClaimForm form = new ClaimForm();

        form.setLossDate(claim.getLossDate());
        form.setNotificationDate(claim.getClmDate());
        form.setLossDesc(claim.getLossDesc());
        form.setRiskIdentifier(claim.getRiskIdentifier());
        form.setNextReviewDate(claim.getNextReviewDate());
        form.setLiabilityAdmission(claim.isLiabilityAdmission());
        form.setPartyToBlame(claim.getPartyToBlame());
        form.setInsurerDate(claim.getInsurerDate());
        try {
            ClaimActivities latestActivity = clmActivitiesRepo.findTopByClaimBookingsOrderByActivityDateDesc(claim);

            if (latestActivity != null) {
//                form.setActivityNotes(latestActivity.getActivityNotes());
                ClaimActivities.ActivityNote latestNote = latestActivity.getLatestNote();
                form.setActivityNotes(latestNote != null ? latestNote.getNote() : "");
                if (latestActivity.getReviewUser() != null) {
                    form.setNextReviewUser(latestActivity.getReviewUser().getId());
                    String reviewUserName = latestActivity.getReviewUser().getName();
                    form.setReviewUser(reviewUserName);

                    System.out.println("Next Review User ID: " + latestActivity.getReviewUser().getId());
                    System.out.println("Next Review User Name: " + reviewUserName);
                } else {
                    System.out.println("No review user set for this activity");
                }

                if (claim.getActivity() != null) {
                    form.setActivityId(claim.getActivity().getCaId());
                    form.setActivityDesc(claim.getActivity().getActivityDesc());
                }
            }
        } catch (Exception e) {
            log.error("Error processing activities for claim {}: {}", claim.getClmId(), e.getMessage());
        }


        // Risk info
        if (claim.getRisk() != null) {
            form.setRiskId(claim.getRisk().getRiskId());
            form.setRiskDesc(claim.getRisk().getRiskDesc());
            form.setRiskShtDesc(claim.getRisk().getRiskShtDesc());
        }

        // Activity info
        if (claim.getActivity() != null) {
            form.setActivityId(claim.getActivity().getCaId());
            form.setActivityDesc(claim.getActivity().getActivityDesc());

        }

        List<PerilBean> perils = convertClaimPerilsToPerilBeans(claim.getClmId());
        form.setPerils(perils);

        // Balance approval
        form.setBalanceApproved(claim.getBalanceApprovedBy() != null);

        return form;
    }

    private List<PerilBean> convertClaimPerilsToPerilBeans(Long claimId) {
        List<PerilBean> perilBeans = new ArrayList<>();

        try {

            List<ClaimPerils> claimPerils = claimPerilsRepo.findByClaimIdNative(claimId);

            log.info("Found {} claim perils for claim {}", claimPerils.size(), claimId);

            for (ClaimPerils claimPeril : claimPerils) {
                PerilBean perilBean = new PerilBean();

                if (claimPeril.getBinderSectionPerils() != null) {
                    perilBean.setPerilCode(claimPeril.getBinderSectionPerils().getBspId());
                    log.debug("Set peril code: {}", perilBean.getPerilCode());
                }

                // Amount
                if (claimPeril.getReserve() != null) {
                    perilBean.setPerilEstimate(claimPeril.getReserve());
                    log.debug("Set peril estimate: {}", perilBean.getPerilEstimate());
                }

                try {
                    if (claimPeril.getClmClaimant() != null) {
                        ClaimClaimants claimant = claimPeril.getClmClaimant();

                        if ("S".equals(claimant.getThirdParty())) {
                            perilBean.setSelfAsClaimant("on");
                            log.debug("Set self as claimant: on");
                        } else if ("T".equals(claimant.getThirdParty())) {
                            perilBean.setSelfAsClaimant("off");


                            if (claimant.getClaimant() != null) {
                                perilBean.setClaimantCode(claimant.getClaimant().getClaimantId());
                                log.debug("Set claimant code: {}", perilBean.getClaimantCode());
                            }
                        }
                    } else {

                        perilBean.setSelfAsClaimant("off");
                    }
                } catch (Exception e) {
                    log.warn("Could not load claimant for peril {}: {}", claimPeril.getClmPerilId(), e.getMessage());
                    perilBean.setSelfAsClaimant("off");
                }

                perilBeans.add(perilBean);
            }

            log.info("Successfully converted {} perils to beans for claim {}", perilBeans.size(), claimId);

        } catch (Exception e) {
            log.error("Error converting claim perils for claim {}: {}", claimId, e.getMessage(), e);
        }

        return perilBeans;
    }

    @RequestMapping(value = "updateRejectedClaim", method = RequestMethod.POST)
    public ModelAndView updateRejectedClaim(@ModelAttribute("claimForm") ClaimForm claimForm,
                                            @RequestParam("claimId") Long claimId,
                                            @RequestParam(value = "resubmissionComment", required = false) String resubmissionComment,
                                            @RequestParam("editMode") String editMode,
                                            HttpServletRequest request,
                                            RedirectAttributes redirectAttrs) throws BadRequestException {


        try {
            claimService.updateRejectedClaim(claimId, claimForm, resubmissionComment);

            request.getSession().setAttribute("claimId", claimId);

            ClaimDetailsDTO claimDetails = claimService.getClaimInformation(claimId);

            ModelAndView mav = new ModelAndView("claimdetails");

            mav.addObject("clmId", claimId);
            mav.addObject("claimDetails", claimDetails);

            String randomCode = UUID.randomUUID().toString();
            mav.addObject("mckClmCheckerCode", randomCode);
            mav.addObject("mckClmId", claimDetails.getTaskId() != null ? claimDetails.getTaskId() : null);
            mav.addObject("taskId", claimDetails.getTaskId() != null ? claimDetails.getTaskId() : null);
            mav.addObject("approvalStatus", claimDetails.getApprovalStatus() != null ? claimDetails.getApprovalStatus() : "N");

            return mav;

        } catch (ClaimException ex) {
            redirectAttrs.addFlashAttribute("error", ex.getMessage());
            redirectAttrs.addFlashAttribute("claimForm", claimForm);
            return new ModelAndView("redirect:/protected/claims/editRejectedClaim?claimId=" + claimId + "&mode=edit");

        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Failed to update claim: " + e.getMessage());
            return new ModelAndView("redirect:/protected/claims/editRejectedClaim?claimId=" + claimId + "&mode=edit");
        }
    }


    // Helper method to log all model attributes
    private void logModelAttributes(Model model) {
        Map<String, Object> modelAttributes = model.asMap();
        log.info("Model Attributes in Maker-Checker Flow:");
        for (Map.Entry<String, Object> entry : modelAttributes.entrySet()) {
            log.info("Key: {}, Value: {}", entry.getKey(), entry.getValue());
        }
    }
    @RequestMapping(value = "getClaimPerilsForEdit/{claimId}", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getClaimPerilsForEdit(@PathVariable Long claimId) {
        return claimService.getClaimPerilsForEdit(claimId);
    }



    @RequestMapping(value = { "getClaimDetails/{clmId}" }, method = {
            RequestMethod.GET })
    public ResponseEntity<ClaimDetailsDTO> getClmBookings(@PathVariable Long clmId) throws BadRequestException {
        ClaimDetailsDTO booking = claimService.getClaimInformation(clmId);
        ClaimBookings claimEntity = claimsBookingRepo.findOne(clmId);
        User currentUser = userUtils.getCurrentUser();
        booking.setCurrentUser(currentUser);
        if (claimEntity.getBookedBy() != null) {
            booking.setCreatedBy(claimEntity.getBookedBy().getUsername());
        }
        return new ResponseEntity<>(booking, HttpStatus.OK);
    }

    @RequestMapping(value = { "enquireClaims" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<ClaimEnquiryDTO> enquireClaims(@DataTable DataTablesRequest pageable,
                                                           @RequestParam(value = "policyNo", required = false) String policyNo,
                                                           @RequestParam(value = "riskId", required = false) String riskId,
                                                           @RequestParam(value = "clientCode", required = false) Long clientCode,
                                                           @RequestParam(value = "claimNo", required = false) String claimNo) throws IllegalAccessException {
        return claimService.enquireClaims(pageable,  clientCode, policyNo, riskId,claimNo);
    }

    @RequestMapping(value = { "getClmClaimants" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<ClaimClaimantsDTO> getClaimClaimants(@DataTable DataTablesRequest pageable, HttpServletRequest request) {
        Long clmId = (Long) request.getSession().getAttribute("claimId");
        return claimService.getClaimClaimants(pageable, clmId);
    }


    @RequestMapping(value = { "getClaimPerils" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<ClaimPerilReserveDTO> getClaimPerils(@DataTable DataTablesRequest pageable,HttpServletRequest request) {
        Long clmId = (Long) request.getSession().getAttribute("claimId");
        return claimService.getClaimPerils(pageable,clmId);
    }

    @RequestMapping(value = { "getClaimPayments/{sprId}" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<ClaimPaymentsDTO> getClaimPayments(@DataTable DataTablesRequest pageable, @PathVariable(value = "sprId") Long sprId,
                                                               HttpServletRequest request) {
        Long clmId = (Long) request.getSession().getAttribute("claimId");
        return claimService.getClaimPayments(pageable,clmId,sprId);
    }

    @RequestMapping(value = { "getPerilPayments" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<ClaimPerilPayments> getPerilPayments(@DataTable DataTablesRequest pageable,
                                                        @RequestParam(value = "perilId", required = false) Long perilId,HttpServletRequest request)
            throws IllegalAccessException {
        return claimService.getPerilPayments(pageable,perilId);
    }

    @RequestMapping(value = { "getClmRequiredDocs" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<ClaimRequiredDocsDTO> getClmRequiredDocs(@DataTable DataTablesRequest pageable,HttpServletRequest request)
            throws IllegalAccessException {
        Long clmId = (Long) request.getSession().getAttribute("claimId");
        return claimService.getRequiredDocs(pageable,clmId);
    }

    @RequestMapping(value = { "getClaimActivities" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<ClaimActivityDTO> getClaimActivities(@DataTable DataTablesRequest pageable,HttpServletRequest request)
            throws IllegalAccessException {
        Long clmId = (Long) request.getSession().getAttribute("claimId");
        return  claimService.getClaimAcitivities(pageable,clmId);
    }
    @RequestMapping(value = { "getClaimAuditLogs" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<ClaimAuditLogDTO> getClaimAuditLogs(@DataTable DataTablesRequest pageable, HttpServletRequest request)
            throws IllegalAccessException {
        Long clmId = (Long) request.getSession().getAttribute("claimId");
        return claimService.getClaimAuditLogs(pageable, clmId);
    }

    @RequestMapping(value = { "getClaimStatuses" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<ClaimStatuses> getClaimStatuses(@DataTable DataTablesRequest pageable,HttpServletRequest request)
            throws IllegalAccessException {
        Long clmId = (Long) request.getSession().getAttribute("claimId");
        return  claimService.getClaimStatuses(pageable,clmId);
    }

    @RequestMapping(value = { "getClaimUploads" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<ClaimUploadsDTO> getClaimUploads(@DataTable DataTablesRequest pageable,HttpServletRequest request)
            throws IllegalAccessException {
        Long clmId = (Long) request.getSession().getAttribute("claimId");
        return claimService.getClaimUploads(pageable,clmId);
    }

    @RequestMapping(value = { "getClaimTransactions" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<ClaimsTransDto> getClaimTransactions(@DataTable DataTablesRequest pageable, HttpServletRequest request)
            throws IllegalAccessException {
        Long clmId = (Long) request.getSession().getAttribute("claimId");
        return claimService.getClaimTransactions(pageable,clmId);
    }

    @RequestMapping(value = { "uploadClaimDocument" }, method = {
            RequestMethod.POST })
    @ResponseStatus(HttpStatus.CREATED)
    public void saveOrUpdateAccount(ClaimUploads upload) throws BadRequestException {
        uploadService.uploadGeneralClaimDoc(upload);
    }


    @RequestMapping(value = { "uploadClaimReqDocument" }, method = {
            RequestMethod.POST })
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadClaimReqDocument(ClaimRequiredDocs upload) throws BadRequestException {
        uploadService.uploadClaimReqDoc(upload);
    }



    @RequestMapping(value = "rpt_claim_synopsis", method = RequestMethod.GET)
    public ModelAndView renewalNoticeNonMotor(ModelMap modelMap, HttpServletRequest request, ModelAndView modelAndView)
            throws BadRequestException, IOException {
        Long clmId = (Long) request.getSession().getAttribute("claimId");
        OrganizationDTO organization = orgService.getOrganizationLogoDetails();
        InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
        BufferedImage image = ImageIO.read(in);
        modelMap.put("logo", image );
        modelMap.put("datasource", datasource);
        modelMap.put("format", "pdf");
        modelMap.put("clmid", clmId);
        modelAndView = new ModelAndView("rpt_claim_synopsis", modelMap);
        return modelAndView;
    }

    @RequestMapping(value = "rpt_claims_synopsis", method = RequestMethod.GET)
    public ModelAndView renewalNoticeNonMoto(ModelMap modelMap, HttpServletRequest request, ModelAndView modelAndView)
            throws BadRequestException, IOException {
        Long clmId = (Long) request.getSession().getAttribute("claimId");
        OrganizationDTO organization = orgService.getOrganizationLogoDetails();
        InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
        BufferedImage image = ImageIO.read(in);
        modelMap.put("logo", image );
        modelMap.put("datasource", datasource);
        modelMap.put("format", "pdf");
        modelMap.put("clmId", clmId);
        modelAndView = new ModelAndView("rpt_claims_synopsis", modelMap);
        return modelAndView;
    }

    @RequestMapping(value = { "createClmActivity" }, method = {
            RequestMethod.POST })
    @ResponseBody
    public void createClmActivity(ClaimActivities activities,HttpServletRequest request) throws IllegalAccessException, IOException, BadRequestException {
        Long clmId = (Long) request.getSession().getAttribute("claimId");
        ClaimBookings booking = claimService.getOne(clmId);
        activities.setClaimBookings(booking);
        claimService.addActivity(activities);
    }

    @RequestMapping(value = { "createClmStatus" }, method = {
            RequestMethod.POST })
    @ResponseBody
    public void createClmStatus(ClaimStatuses claimStatuses,HttpServletRequest request) throws IllegalAccessException, IOException, BadRequestException {
        Long clmId = (Long) request.getSession().getAttribute("claimId");
        claimService.addClaimStatus(claimStatuses,clmId);
    }

    @RequestMapping(value = { "saveDvProgress" }, method = {
            RequestMethod.POST })
    @ResponseBody
    public void saveDvProgress(ClaimStatuses claimStatuses,HttpServletRequest request) throws IllegalAccessException, IOException, BadRequestException {
        String paymentIdStr = request.getParameter("paymentId");
        System.out.println("paymentId specifically: " + paymentIdStr);

        // ADD THIS: Set payment mode on the entity
        if (paymentIdStr != null && !paymentIdStr.trim().isEmpty()) {
            try {
                Long paymentId = Long.valueOf(paymentIdStr);

                // Use your PaymentModeRepo to find the PaymentModes object
                PaymentModes paymentMode = paymentModeRepo.findOne(paymentId);  // Since you're using PagingAndSortingRepository

                if (paymentMode != null) {
                    claimStatuses.setPaymentMode(paymentMode);
                    System.out.println("Payment mode set: " + paymentMode.getPmDesc());
                } else {
                    System.out.println("Payment mode not found for ID: " + paymentId);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid payment ID format: " + paymentIdStr);
            }
        }
        Long clmId = (Long) request.getSession().getAttribute("claimId");
        claimService.saveDvProgress(claimStatuses, clmId);
    }

    @RequestMapping(value = {"getClaimDvData"}, method = {RequestMethod.GET})
    @ResponseBody
    public ResponseEntity<?> getClaimDvData(HttpServletRequest request) {
        try {
            Long clmId = (Long) request.getSession().getAttribute("claimId");
            ClaimStatuses dvData = claimService.getLatestDvData(clmId);

            Map<String, Object> response = new HashMap<>();
            response.put("dvData", dvData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(Collections.emptyMap()); // Return empty if no data
        }
    }



    @RequestMapping(value = "/clmdocument/{docId}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> thumbnail(@PathVariable Long docId ) throws BadRequestException {
        ClaimUploads upload = claimUploadRepo.findOne(docId);
        byte[] content = uploadService.sybrinDocumentDetails("Claim Gen Doc",null, upload.getClaimBookings().getClmId());
        if (content.length>0) {
            String contentType = uploadService.getGeneralClmContentType(docId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentLength(content.length);
            return new ResponseEntity<byte[]>(content, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<byte[]>(HttpStatus.NOT_FOUND);
        }
    }

//    @RequestMapping(value = "/clmreqdocument/{docId}", method = RequestMethod.GET)
//    public ResponseEntity<byte[]> getclmreqdocument(@PathVariable Long docId ) throws BadRequestException {
//        ClaimRequiredDocs upload = claimRequiredDocsRepo.findOne(docId);
//        byte[] content = uploadService.sybrinDocumentDetails("Claim Req Doc",docId,upload.getClaimBookings().getClmId());
//        if (content.length>0) {
//            String contentType = uploadService.getReqClmContentType(docId);
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.parseMediaType(contentType));
//            headers.setContentLength(content.length);
//            return new ResponseEntity<byte[]>(content, headers, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<byte[]>(HttpStatus.NOT_FOUND);
//        }
//    }

    @RequestMapping(value = "/clmreqdocument/{docId}", method = RequestMethod.GET)
    public void getclmreqdocument(@PathVariable Long docId, HttpServletResponse response) throws IOException, BadRequestException {
        ClaimRequiredDocs upload = claimRequiredDocsRepo.findOne(docId);
        if (upload == null) {
            response.sendError(HttpStatus.NOT_FOUND.value(), "Document not found");
            return;
        }

        byte[] content = uploadService.sybrinDocumentDetails("Claim Req Doc", docId, upload.getClaimBookings().getClmId());

        if (content != null && content.length > 0) {
            String fileName = upload.getFileName();

            // Set headers first
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");

            // Set content type and disposition based on file type
            if (fileName != null && (fileName.endsWith(".xlsx") || fileName.endsWith(".xls"))) {
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            } else if (fileName != null && (fileName.endsWith(".docx") || fileName.endsWith(".doc"))) {
                response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            } else {
                response.setContentType("application/pdf");
            }

            // Set content length after other headers
            response.setContentLength(content.length);

            // Write content using try-with-resources
            try (OutputStream out = response.getOutputStream()) {
                out.write(content);
                out.flush();
            }
        } else {
            response.sendError(HttpStatus.NOT_FOUND.value(), "Document not found");
        }
    }


    @RequestMapping(value = {"claimBalance"}, method = {RequestMethod.GET})
    public ResponseEntity<ClaimBalanceBean> getPremiumProduction(@RequestParam("polNo") String polNo) throws BadRequestException {
        ClaimBalanceBean aggregateBean = claimService.getBalance(polNo);
        return new ResponseEntity<ClaimBalanceBean>(aggregateBean, HttpStatus.OK);
    }
    @RequestMapping(value = { "validateCoverPeriod/{polId}" }, method = {
            RequestMethod.GET })
    public ResponseEntity<String> validateCoverPeriod(
            @PathVariable Long polId,
            @RequestParam("lossDate") Date lossDate) throws BadRequestException {
        claimService.validateCoverPeriod(polId, lossDate);
        return new ResponseEntity<>("Valid", HttpStatus.OK);
    }
    @RequestMapping(value = { "deleteClmReqDoc/{clmRequiredId}" }, method = {
            RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClmReqDoc(@PathVariable Long clmRequiredId) throws BadRequestException {
        uploadService.deleteClmReqDoc(clmRequiredId);
    }


    @RequestMapping(value = { "deleteClmUploadDoc/{uploadId}" }, method = {
            RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClmUploadDoc(@PathVariable Long uploadId) throws BadRequestException {
        uploadService.deleteClmUploadDoc(uploadId);
    }

    @RequestMapping(value = {"getClmReqDocs"}, method = {RequestMethod.GET})
    @ResponseBody
    public List<SubClassReqdDocsDTO> getRiskUnassignedDocs(@RequestParam(value = "docName", required = false) String docName, HttpServletRequest request)
            throws IllegalAccessException {
        Long clmId = (Long) request.getSession().getAttribute("claimId");
        return uploadService.findUnassignedRiskDocs(clmId, docName);
    }

    @RequestMapping(value = {"getExpireRiskSection/{peril}/{risk}"}, method = {RequestMethod.GET})
    public ResponseEntity<Set<SectionTransBean>>  getExpireRiskSection(@PathVariable(value = "peril") Long perilId, @PathVariable(value = "risk") Long riskId)  throws BadRequestException {
        Set<SectionTransBean> sectionToExpire = claimService.getExpireSections(perilId, riskId);
         return new ResponseEntity<>(sectionToExpire, HttpStatus.OK);
    }

    @RequestMapping(value = { "createClaimReqDocs" }, method = {
            RequestMethod.POST })
    public ResponseEntity<String> createClaimReqDocs(@RequestBody RequiredDocBean requiredDocBean ,
                                                     HttpServletRequest request) throws IllegalAccessException, IOException, BadRequestException {
        Long clmId = (Long) request.getSession().getAttribute("claimId");
        claimService.createclaimsRequiredDocs(requiredDocBean,clmId);
        return new ResponseEntity<String>("OK",HttpStatus.OK);
    }

    @RequestMapping(value ={ "makeRevTransReady/{id}" }, method = {
            RequestMethod.GET })
    @ResponseBody
    public void makeReady(@PathVariable Long id,HttpServletRequest request){
        claimService.makeReady(id);
    }

    @RequestMapping(value ={ "undoRevTransReady/{id}" }, method = {
            RequestMethod.GET })
    @ResponseBody
    public void undoMakeReady(@PathVariable Long id,HttpServletRequest request){
        claimService.makeUndo(id);
    }

    @RequestMapping(value ={ "authoriseRevision/{id}" }, method = {
            RequestMethod.GET })
    @ResponseBody
    public void authoriseRevision(@PathVariable Long id,HttpServletRequest request) throws BadRequestException {
        claimService.authoriseTransaction(id);
    }

    @RequestMapping(value = { "createServProviderTypes" }, method = {
            RequestMethod.POST })
    @ResponseBody
    public void createServProviderTypes(ServiceProviderTypesDTO providerTypesDTO) throws BadRequestException {
        claimService.createServiceProviderTypes(providerTypesDTO);
    }


    @RequestMapping(value = { "createServProviders" }, method = {
            RequestMethod.POST })
    @ResponseBody
    public void createServProviders(ServiceProviderDTO serviceProviderDTO) throws BadRequestException {
        claimService.createServiceProviders(serviceProviderDTO);
    }


    @RequestMapping(value = { "getServiceProviders/{id}" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<ServiceProviderDTO> getServiceProviders(@PathVariable Long id,@DataTable DataTablesRequest pageable) {
        return claimService.getServiceProviders(id, pageable);
    }


    @RequestMapping(value = { "deleteServiceProvider/{providerId}" }, method = {
            RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteServiceProvider(@PathVariable Long providerId) throws BadRequestException {
        claimService.deleteServiceProvider(providerId);
    }

    @RequestMapping(value = { "deleteServiceProviderType/{providerId}" }, method = {
            RequestMethod.GET })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteServiceProviderType(@PathVariable Long providerId) throws BadRequestException {
        claimService.deleteServiceProviderType(providerId);
    }

    @RequestMapping(value = "rpt_claims_req/{polId}", method = RequestMethod.GET)
    public ModelAndView rpt_claims_req(ModelMap modelMap,@PathVariable Long polId,
                                       HttpServletRequest request, ModelAndView modelAndView)
            throws BadRequestException, IOException {
        OrganizationDTO organization = orgService.getOrganizationLogoDetails();
        InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
        BufferedImage image = ImageIO.read(in);
        modelMap.put("logo", image );
        modelMap.put("datasource", datasource);
        modelMap.put("format", "pdf");
        modelMap.put("polId", polId);
        modelAndView = new ModelAndView("rpt_claims_req", modelMap);
        return modelAndView;
    }

}
