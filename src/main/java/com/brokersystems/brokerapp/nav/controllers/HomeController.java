package com.brokersystems.brokerapp.nav.controllers;


import com.brokersystems.brokerapp.accounts.repository.CollectionAcctsRepo;
import com.brokersystems.brokerapp.nav.checkerdatamappings.ClientCheckerMapperService;
import com.brokersystems.brokerapp.nav.checkerdatamappings.QuoteCheckerMapperService;
import com.brokersystems.brokerapp.nav.checkerdatamappings.ReceiptCheckerMapperService;
import com.brokersystems.brokerapp.quotes.dto.PendingQuotDTO;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.DateUtilities;
import com.brokersystems.brokerapp.server.utils.FormLockManager;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.setup.dto.*;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.*;
import com.brokersystems.brokerapp.setup.service.SetupsService;
import com.brokersystems.brokerapp.setup.service.UserService;
import com.brokersystems.brokerapp.trans.model.HomeAggregateBean;
import com.brokersystems.brokerapp.trans.model.HomePremiumBean;
import com.brokersystems.brokerapp.trans.model.ReceiptTrans;
import com.brokersystems.brokerapp.trans.service.HomeService;
import com.brokersystems.brokerapp.users.dto.MakerCheckDTO;
import com.brokersystems.brokerapp.users.dto.ResubmitTaskRequest;
import com.brokersystems.brokerapp.users.model.MakerChecker;
import com.brokersystems.brokerapp.users.model.PasswordResetToken;
import com.brokersystems.brokerapp.users.model.QPasswordResetToken;
import com.brokersystems.brokerapp.users.repository.MakerCheckerRepo;
import com.brokersystems.brokerapp.users.repository.PasswordResetTokenRepo;
import com.brokersystems.brokerapp.users.service.MakerCheckerService;
import com.brokersystems.brokerapp.uw.dtos.PolicyEnquiryDTO;
import com.brokersystems.brokerapp.uw.dtos.PortfolioDTO;
import com.brokersystems.brokerapp.uw.dtos.RefundDetailsDTO;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.service.PolicyTransService;
import com.brokersystems.brokerapp.workflow.dto.WorkFlowDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@Controller
@RequestMapping({"/protected/home", "/protected"})
public class HomeController {

    @Autowired
    private HomeService homeService;

    @Autowired
    private MakerCheckerRepo makerCheckerRepo;

    @Autowired
    private SetupsService setupsService;

    @Autowired
    private PolicyTransService policyService;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private DateUtilities dateUtilities;

    @Autowired
    private PasswordResetTokenRepo passwordResetTokenRepo;

    @Autowired
    private MakerCheckerService makerCheckerService;

    @Autowired
    private UserService userService;

    @Autowired
    private ClientCheckerMapperService clientCheckerMapperService;

    @Autowired
    private QuoteCheckerMapperService quoteCheckerMapperService;

    @Autowired
    private ReceiptCheckerMapperService receiptCheckerMapperService;
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private OrgBranchRepository orgBranchRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CollectionAcctsRepo collectionAcctsRepo;
    @Autowired
    private FormLockManager formLockManager;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String entry(HttpServletRequest request, Model model) {
        User user = userUtils.getCurrentUser();
        Sort sort = new Sort(Sort.Direction.ASC, "branchName");
        DataTablesRequest dataTableRequest = new DataTablesRequest(sort, 0, 10);
        DataTablesResult<UserBranchesDTO> userBranches = userService.findUserBranches(user.getId(), dataTableRequest);
        request.getSession().setAttribute("userBranches", userBranches.getData());
        if ("Y".equalsIgnoreCase(user.getResetPass())) {
            PasswordResetToken resetToken = passwordResetTokenRepo.findOne(QPasswordResetToken.passwordResetToken.user.id.eq(user.getId()));
            return "redirect:/reset-password?token=" + resetToken.getToken();
        } else {
//            Calendar calendar = Calendar.getInstance();
//            calendar.set(2018,05,31);
//             long days = dateUtilities.daysBetweenUsingJoda(new Date(),calendar.getTime());
//             System.out.println("days..."+days);
//             if(days > 0)
//            request.getSession().setAttribute("timeoutMessage","Number of Days Remaining for the Trial Period.... "+days);
//            else{
//                 request.getSession().removeAttribute("Username");
//                 return "redirect:/login";
//             }
            return "home";
        }
    }

    @RequestMapping(value = "unlockForm", method = RequestMethod.POST)
    public ResponseEntity<String> unlockForm(@RequestParam String formName, @RequestParam Long formId) {
        try {
            formLockManager.closeForm(formName, String.valueOf(formId));
            return ResponseEntity.ok("Form unlocked successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to unlock form.");
        }
    }


//    @RequestMapping(value = {"dashboardDetails"}, method = {
//            RequestMethod.GET})
//    public ResponseEntity<HomeAggregateBean> getPolicyDetails() throws BadRequestException {
//        HomeAggregateBean aggregateBean = homeService.getDashBoardDetails();
//        return new ResponseEntity<HomeAggregateBean>(aggregateBean, HttpStatus.OK);
//    }
@RequestMapping(value = {"dashboardDetails"}, method = {RequestMethod.GET})
public ResponseEntity<HomeAggregateBean> getPolicyDetails() throws BadRequestException {
    try {
        User currentUser = userUtils.getCurrentUser();
        Long userId = currentUser.getId();
        HomeAggregateBean aggregateBean;

        if (hasGlobalViewPermission(userId)) {
            aggregateBean = homeService.getDashBoardDetails();
        } else {
            // Regular users see only their own data
            aggregateBean = homeService.getDashBoardDetailsByUser(userId);
        }

        return new ResponseEntity<>(aggregateBean, HttpStatus.OK);

    } catch (Exception e) {

        throw new BadRequestException("Error retrieving dashboard details: " + e.getMessage());
    }
}
    private boolean hasGlobalViewPermission(Long userId) {
        return userId != null && (userId.equals(1L) || userId.equals(22358L));
    }

    @RequestMapping(value = {"premiumProduction"}, method = {
            RequestMethod.GET})
    public ResponseEntity<List<HomePremiumBean>> getPremiumProduction() throws BadRequestException {
        List<HomePremiumBean> aggregateBean = homeService.getPremiumProduction();
        return new ResponseEntity<List<HomePremiumBean>>(aggregateBean, HttpStatus.OK);
    }

    @RequestMapping(value = {"productPremium"}, method = {
            RequestMethod.GET})
    public ResponseEntity<List<HomePremiumBean>> getProductPremium() throws BadRequestException {
        List<HomePremiumBean> aggregateBean = homeService.getProductPremium();
        return new ResponseEntity<List<HomePremiumBean>>(aggregateBean, HttpStatus.OK);
    }

    @RequestMapping(value = {"branchPremium"}, method = {
            RequestMethod.GET})
    public ResponseEntity<List<HomePremiumBean>> getBranchPremium() throws BadRequestException {
        List<HomePremiumBean> aggregateBean = homeService.getBranchPremium();
        return new ResponseEntity<List<HomePremiumBean>>(aggregateBean, HttpStatus.OK);
    }

    @RequestMapping(value = {"userTickets"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<WorkFlowDTO> getUserPolTrans(@DataTable DataTablesRequest pageable) {
        return policyService.findUserPolicies(pageable);
    }

    @RequestMapping(value = {"pendingTickets"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<MakerCheckDTO> getMakerCheckerTickets(@DataTable DataTablesRequest pageable) throws BadRequestException {
        return makerCheckerService.findPendingTasks(pageable);
    }

    @RequestMapping(value = {"userPortfolio"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<PortfolioDTO> findUserPolicyPortfolio(
            @DataTable DataTablesRequest pageable,
            @RequestParam(required = false) String status) throws IllegalAccessException {
        if (status != null && status.isEmpty()) {
            status = null;
        }
//        System.out.println("Passed status: " + status);
        return homeService.findUserPolicyPortfolio(pageable, status);
    }

    @RequestMapping(value = "/edituwtrans", method = RequestMethod.POST)
    public String editPolicyForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model, HttpServletRequest request,RedirectAttributes redirectAttrs) throws BadRequestException {
        request.getSession().setAttribute("policyCode", helperForm.getId());
        System.out.println("ID BEING PASSED: " + helperForm.getId());
        PolicyTrans policyTrans = policyService.getPolicyDetails(helperForm.getId());
        User createdUser = policyTrans.getCreatedUser();
        User currentUser = userUtils.getCurrentUser();
        // Set session and redirect attributes
        redirectAttrs.addFlashAttribute("createdBy", createdUser);
        redirectAttrs.addFlashAttribute("currentUser", currentUser);
        if ("MD".equalsIgnoreCase(policyTrans.getProduct().getProGroup().getPrgType())) {
            formLockManager.openForm("MEDICAL", String.valueOf(helperForm.getId()));
            return "redirect:/protected/medical/policies/edituwpolicy";
        } else if ("L".equalsIgnoreCase(policyTrans.getProduct().getProGroup().getPrgType())) {
            formLockManager.openForm("LIFE", String.valueOf(helperForm.getId()));
            return "redirect:/protected/uw/policies/editlifepolicy";
        } else {
            formLockManager.openForm("GENERAL", String.valueOf(helperForm.getId()));
            return "redirect:/protected/uw/policies/edituwpolicy";
        }

    }

    @RequestMapping(value = "/editAcctForm", method = RequestMethod.POST)
    public String editRentalForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model,HttpServletRequest request) throws BadRequestException {
        model.addAttribute("accId", helperForm.getId());
        request.getSession().setAttribute("accId", helperForm.getId());
        formLockManager.openForm("INSURER/AGENT",String.valueOf(helperForm.getId()));
        return "acctsform";
    }

    @RequestMapping(value = "/editquottrans", method = RequestMethod.POST)
    public String editQuoteForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model, HttpServletRequest request) throws BadRequestException {
        request.getSession().setAttribute("quotCode", helperForm.getId());
        formLockManager.openForm("QUOTE", String.valueOf(helperForm.getId()));
        return "redirect:/protected/quotes/editquote";
    }

    @RequestMapping(value = "/checkerGetIntermediaryForm", method = RequestMethod.GET)
    public String checkerGetIntermediaryForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model, HttpServletRequest request) throws BadRequestException {

        System.out.println("ID BEING PASSED: " + helperForm.getId());
        String jsonString = (String) makerCheckerService.findMakerCheckerTask(helperForm.getId());
        System.out.println("Account as JSON string: " + jsonString);
        JSONObject jsonObject = new JSONObject(jsonString);
        AccountDef checkerAccount = accountRepo.findOne((long) jsonObject.optInt("acctId"));
        System.out.println("Account as account object: " + checkerAccount);
        String randomCode = UUID.randomUUID().toString();
        model.addAttribute("mckAccCheckerCode", randomCode);
        System.out.println("Generated mckAccCheckerCode: " + randomCode);
        model.addAttribute("mckAccId", helperForm.getId());
        System.out.println("MAKER CHECKED ID: " + helperForm.getId());
        model.addAttribute("accId", checkerAccount.getAcctId());
        System.out.println("Account ID: " + checkerAccount.getAcctId());
        formLockManager.openForm("INSURER/AGENT", String.valueOf(helperForm.getId()));
        return "accountCheckerForm";

    }

    @RequestMapping(value = "/checkerViewPolicy", method = RequestMethod.POST)
    public String checkerViewPolicy(@Valid @ModelAttribute ModelHelperForm helperForm, Model model, HttpServletRequest request) throws BadRequestException {

        System.out.println("ID BEING PASSED: " + helperForm.getId());
        String jsonString = (String) makerCheckerService.findMakerCheckerTask(helperForm.getId());
        System.out.println(jsonString);
        JSONObject jsonObject = new JSONObject(jsonString);
        PolicyTrans policyTrans = policyService.getPolicyDetails((long) jsonObject.optInt("policyId"));
        if (policyTrans.getAuthStatus() != null && policyTrans.getAuthStatus().equalsIgnoreCase("A")) {
            model.addAttribute("policyStatus", 2);
        } else if (policyTrans.getAuthStatus() != null && policyTrans.getAuthStatus().equalsIgnoreCase("R"))
            model.addAttribute("policyStatus", 1);
        else if (policyTrans.getAuthStatus() != null && policyTrans.getAuthStatus().equalsIgnoreCase("CV"))
            model.addAttribute("policyStatus", 3);
        else model.addAttribute("policyStatus", 0);
        request.getSession().setAttribute("policyCode", policyTrans.getPolicyId());
        String randomCode = UUID.randomUUID().toString();
        model.addAttribute("mckPolCheckerCode", randomCode);
        System.out.println("Generated mckPolCheckerCode: " + randomCode);
        model.addAttribute("mckPolId", helperForm.getId());
        if ("MD".equalsIgnoreCase(policyTrans.getProduct().getProGroup().getPrgType()))
            return "redirect:/protected/medical/policies/edituwpolicy";
        else if ("L".equalsIgnoreCase(policyTrans.getProduct().getProGroup().getPrgType())) {
            model.addAttribute("policyId", policyTrans.getPolicyId());
            return "checkerviewpolicy";
        } else {
            return "redirect:/protected/uw/policies/edituwpolicy";
        }

    }

    @RequestMapping(value = "/checkerGetClientForm", method = RequestMethod.GET)
    public String checkerGetClientForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model, HttpServletRequest request) {
        Long mckIdNo = helperForm.getId();
        request.getSession().setAttribute("mck_id_no", mckIdNo);
        model.addAttribute("mckIdNo", mckIdNo);
        System.out.println("Stored mck_id_no in session: " + mckIdNo);
        return "checkerClient";
    }

    @RequestMapping(value = "/checkerGetQuoteForm", method = RequestMethod.GET)
    public String checkerGetQuoteForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model, HttpServletRequest request) {
        Long mckIdQuote = helperForm.getId();
        request.getSession().setAttribute("mck_id_quote", mckIdQuote);
        model.addAttribute("mckIdQuote", mckIdQuote);
        System.out.println("Stored mck_id_quote in session: " + mckIdQuote);
        return "checkerviewquote";
    }

    @RequestMapping(value = "/checkerGetReceiptForm", method = RequestMethod.GET)
    public String checkerGetReceiptForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model, HttpServletRequest request) throws BadRequestException {
        Long mckIdReceipt = helperForm.getId();
        request.getSession().setAttribute("mck_id_receipt", mckIdReceipt);
        model.addAttribute("mckIdReceipt", mckIdReceipt);
        System.out.println("Stored mck_id_receipt in session: " + mckIdReceipt);
        formLockManager.openForm("RECEIPT", String.valueOf(helperForm.getId()));
        return "checkerReceipt";
    }

    @RequestMapping(value = "/getCheckerData", method = RequestMethod.GET)
    @ResponseBody
    public Object checkerViewClient(@RequestParam Long mckIdNo,
                                    @RequestParam String taskType,
                                    HttpServletRequest request) {
        System.out.println(mckIdNo);
        System.out.println(taskType);

        String jsonString = (String) makerCheckerService.findMakerCheckerTask(mckIdNo);
        System.out.println(jsonString);
        JSONObject jsonObject = new JSONObject(jsonString);
        if ("client".equals(taskType)) {
            return clientCheckerMapperService.mapClientData(jsonObject);
        }
        if ("quote".equals(taskType)) {
            return quoteCheckerMapperService.mapQuoteData(jsonObject);
        }
        if ("receipt".equals(taskType)) {
            return receiptCheckerMapperService.mapReceiptData(jsonObject);
        }
        return jsonObject.toString();  // Convert JSONObject to String

    }

    @RequestMapping(value = "/viewMakerTask", method = RequestMethod.GET)
    @ResponseBody
    public Object makerViewTask(@RequestParam Long taskId) {
        return makerCheckerService.findMakerTaskDetail(taskId);
    }

    @RequestMapping(value = "/makerGetClientForm", method = RequestMethod.GET)
    public String makerGetClientForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model, HttpServletRequest request) {
        Long mckIdNo = helperForm.getId();
        request.getSession().setAttribute("mck_id_no", mckIdNo);
        model.addAttribute("mckIdNo", mckIdNo);
        System.out.println("Stored mck_id_no in session: " + mckIdNo);
        return "makerClient";
    }

    @RequestMapping(value = "/makerGetReceiptForm", method = RequestMethod.GET)
    public String makerGetReceiptForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model, HttpServletRequest request) {
        Long mckIdNo = helperForm.getId();
        request.getSession().setAttribute("mck_id_no", mckIdNo);
        model.addAttribute("mckIdNo", mckIdNo);
        System.out.println("Stored mck_id_no in session: " + mckIdNo);
        return "makerReceipt";
    }

    @RequestMapping(value = "/getEligibleCheckers", method = RequestMethod.GET)
    @ResponseBody
    public List<UserDTO> eligibleCheckers(@RequestParam String permissionName, @RequestParam(required = false) String searchParam) {
        return makerCheckerService.findEligibleCheckers(permissionName, searchParam);
    }

    @RequestMapping(value = "/resubmitTask", method = RequestMethod.POST)
    public ResponseEntity<?> resubmitTask(@RequestBody ResubmitTaskRequest request) throws BadRequestException {
        Long taskId = request.getTaskId();
        String taskType = request.getTaskType();
        List<Long> checkerIds = new ArrayList<>();
        for (String checkerId : request.getCheckerIds()) {
            Long checker_id = Long.parseLong(checkerId);
            checkerIds.add(checker_id);
        }

        System.out.println("checkerIds" + checkerIds);
        Gson gson = new GsonBuilder()
                .setDateFormat("dd/MM/yyyy")
                .create();
        String updatedJson = gson.toJson(request.getUpdatedData());
        System.out.println("updated data: " + updatedJson);
        if (updatedJson.contains("\"payId\":\"\"")) {
            updatedJson = updatedJson.replace("\"payId\":\"\"", "\"payId\":null"); // Replace empty `payId` with `null`
        }

        switch (taskType.toUpperCase()) {
            case "CL":
                ClientDTO clientDTO = gson.fromJson(updatedJson, ClientDTO.class);
                makerCheckerService.resubmitTask(taskId, taskType, clientDTO, checkerIds);
                break;
            case "RC":
                ReceiptTrans receiptTrans = gson.fromJson(updatedJson, ReceiptTrans.class);
                receiptTrans.setInsurance(accountRepo.findOne(receiptTrans.getInsuranceId()));
                receiptTrans.setBranch(orgBranchRepository.findOne(receiptTrans.getBrnCode()));
                receiptTrans.setReceiptUser(userRepository.findOne(receiptTrans.getUserId()));
                if (Objects.equals(receiptTrans.getReceiptType(), "COM")) {
                    receiptTrans.setCollectionAccount(collectionAcctsRepo.findOne(receiptTrans.getPayId()));
                }
                receiptTrans.setReceiptTransDate(receiptTrans.getReceiptDate());
                makerCheckerService.resubmitTask(taskId, taskType, receiptTrans, checkerIds);

                break;
            default:
                return ResponseEntity.badRequest().body("Unsupported task type");
        }

        return ResponseEntity.ok("Task resubmitted successfully");
    }


    @RequestMapping(value = "makerTickets", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesResult<MakerCheckDTO> getMakerTickets(@DataTable DataTablesRequest request) {
        return makerCheckerService.getMakerTasks(request);
    }


    @RequestMapping(value = "/editClients", method = RequestMethod.POST)
    public String editClientForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model, HttpServletRequest request) throws BadRequestException {
        System.out.println(helperForm.getId());
        request.getSession().setAttribute("tenidpkey", helperForm.getId());
        ClientDTO clientDef = setupsService.getClientDetails(helperForm.getId());
        System.out.println(clientDef);
        String hashCode = clientDef.getHashCode();
        System.out.println(hashCode);
        formLockManager.openForm("CLIENT",hashCode);


        return "redirect:/protected/clients/setups/editClients/" + hashCode;
    }

    @RequestMapping(value = "updateUserIP", method = RequestMethod.GET)
    @ResponseBody
    public String entry(@RequestParam(value = "ipAddress") String ip) throws BadRequestException {
        User user = userUtils.getCurrentUser();
        userService.updateUserIP(user.getId(), ip);
        return ip;
    }

    @RequestMapping(value = {"pendingQuotes"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<PendingQuotDTO> getPendingQuotes(@DataTable DataTablesRequest pageable) {
        return homeService.getPendingQuotes(pageable);
    }
    @RequestMapping(value = {"pendingReceipts"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<MakerCheckDTO> getPendingReceipts(@DataTable DataTablesRequest pageable) throws BadRequestException {
        return makerCheckerService.findPendingReceipts(pageable);
    }
    @RequestMapping(value = "/checkerGetRefundForm", method = RequestMethod.GET)
    public String checkerGetRefundForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model, HttpServletRequest request) throws BadRequestException {
        Long mckIdRefund = helperForm.getId();
        request.getSession().setAttribute("mck_id_refund", mckIdRefund);
        model.addAttribute("mckIdRefund", mckIdRefund);
        System.out.println("Stored mck_id_refund in session: " + mckIdRefund);

        try {
            String jsonString = (String) makerCheckerService.findMakerCheckerTask(mckIdRefund);
            if (jsonString != null && !jsonString.isEmpty()) {
                System.out.println("Found maker-checker JSON: " + jsonString);

                Gson gson = new GsonBuilder()
                        .setDateFormat("MMM dd, yyyy HH:mm:ss z")
                        .create();

                RefundDetailsDTO refundDetails = gson.fromJson(jsonString, RefundDetailsDTO.class);

                // Add refund details to model
                model.addAttribute("refundDetails", refundDetails);

                // Format dates and amounts server-side
                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                SimpleDateFormat dateOnlyFormatter = new SimpleDateFormat("dd/MM/yyyy");
                DecimalFormat amountFormatter = new DecimalFormat("#,##0.00");

                // Formatted values for display
                if (refundDetails.getRequestDate() != null) {
                    model.addAttribute("formattedRequestDate", dateFormatter.format(refundDetails.getRequestDate()));
                    model.addAttribute("formattedRefundDate", dateOnlyFormatter.format(refundDetails.getRequestDate()));
                }

                if (refundDetails.getRefundAmount() != null) {
                    model.addAttribute("formattedRefundAmount", amountFormatter.format(refundDetails.getRefundAmount()));
                }

                // Get the full MakerChecker object for additional details
                MakerChecker makerChecker = makerCheckerRepo.findOne(mckIdRefund);
                if (makerChecker != null) {
                    model.addAttribute("taskName", makerChecker.getTaskName());
                    model.addAttribute("requestDate", makerChecker.getMadeOnDate());
                    model.addAttribute("formattedMakerRequestDate", dateFormatter.format(makerChecker.getMadeOnDate()));
                    model.addAttribute("requestedBy", makerChecker.getMakerId() != null ? makerChecker.getMakerId().getUsername() : "Unknown");
                    model.addAttribute("initiatedBy", makerChecker.getInitiatorId() != null ? makerChecker.getInitiatorId().getUsername() : "Unknown");
//                    model.addAttribute("status", getStatusDescription(makerChecker.getStatus()));
                    model.addAttribute("taskCode", makerChecker.getTaskCode());
                    model.addAttribute("taskType", makerChecker.getTaskType());

                    if (makerChecker.getCheckerId() != null) {
                        model.addAttribute("checkedBy", makerChecker.getCheckerId().getUsername());
                        model.addAttribute("checkDate", makerChecker.getCheckDate());
                    }

                    if (makerChecker.getRejectedReason() != null && !makerChecker.getRejectedReason().isEmpty()) {
                        model.addAttribute("rejectionReason", makerChecker.getRejectedReason());
                    }
                }

                System.out.println("Successfully loaded refund details for transaction: " + refundDetails.getOriginalTransId());
            } else {
                model.addAttribute("errorMessage", "No task JSON found for this maker-checker ID");
            }
        } catch (Exception e) {
            System.err.println("Error loading refund details: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error loading refund details: " + e.getMessage());
        }

        formLockManager.openForm("REFUND", String.valueOf(helperForm.getId()));
        return "checkerRefund";
    }
    @RequestMapping(value = {"pendingClaims"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<MakerCheckDTO> getPendingClaims(@DataTable DataTablesRequest pageable) throws BadRequestException {
        return makerCheckerService.findPendingClaims(pageable);
    }

    @RequestMapping(value = {"expiredPolicies"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<PolicyEnquiryDTO> getExpiredPolicies(@DataTable DataTablesRequest pageable) {
        return homeService.getExpiredPolicies(pageable);
    }

    @RequestMapping(value = {"pendingEndorsements"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<PolicyEnquiryDTO> getPendingEndorsements(@DataTable DataTablesRequest pageable) {
        return homeService.getPendingEndorsements(pageable);
    }

    @RequestMapping(value = {"pendingRenewals"}, method = {RequestMethod.GET})
    @ResponseBody
    public DataTablesResult<PolicyEnquiryDTO> getPendingRenewals(@DataTable DataTablesRequest pageable) {
        return homeService.getPendingRenewals(pageable);
    }

    @RequestMapping(value = "pendingQt", method = {RequestMethod.GET})
    public String convertQuotes(Model model) {
        return "pendingquotes";
    }

    @RequestMapping(value = "expiredpols", method = {RequestMethod.GET})
    public String expiredpolicies(Model model) {
        return "expiredpolicies";
    }

    @RequestMapping(value = "pendingendorsements", method = {RequestMethod.GET})
    public String pendingendorsements(Model model) {
        return "pendingendorsements";
    }

    @RequestMapping(value = "pendingrenewals", method = {RequestMethod.GET})
    public String pendingrenewals(Model model) {
        return "pendingrenewals";
    }
}
