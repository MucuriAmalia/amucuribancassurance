package com.brokersystems.brokerapp.uw.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.brokersystems.brokerapp.server.exception.EndorsementsException;
import com.brokersystems.brokerapp.server.utils.FormLockManager;
import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.uw.dtos.EndorsementsDTO;
import com.brokersystems.brokerapp.uw.dtos.PolicyEnquiryDTO;
import com.brokersystems.brokerapp.uw.dtos.RenewalDTO;
import com.brokersystems.brokerapp.uw.model.*;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import com.brokersystems.brokerapp.uw.service.PolicyTransService;
import org.apache.commons.lang3.StringUtils;
import org.easybatch.core.record.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.UserUtils;
import com.brokersystems.brokerapp.setup.model.ModelHelperForm;
import com.brokersystems.brokerapp.uw.service.EndorseService;
import com.brokersystems.brokerapp.uw.service.PremComputeService;

@Controller
@RequestMapping({ "/protected/uw/renewals" })
public class RenewalsController {
	
	@Autowired
	private EndorseService endorseService;
	
	@Autowired
	private PremComputeService premiumService;
	
	@Autowired
	private UserUtils userUtils;

	@Autowired
	private PolicyTransRepo policyRepo;

	@Autowired
	private FormLockManager formLockManager;

	@Autowired
	private PolicyTransService policyService;

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
	}
	
	@ModelAttribute
	private StartRenForm getStartRenForm(){
		return new StartRenForm();
	}
	
	
	@ModelAttribute
	private RenewalsForm getRenewalsForm(){
		RenewalsForm form = new RenewalsForm();
		form.setBusinessType("G");
		return new RenewalsForm();
	}
	
	@RequestMapping(value = "renewalsprocess", method = RequestMethod.GET)
	public String renewalProcess(Model model) {
		return "renewals";
	}
	
	@RequestMapping(value = "singleren", method = RequestMethod.GET)
	public String singleRenewals(Model model) {
		return "singlerenewals";
	}
	
	@RequestMapping(value = "batchren", method = RequestMethod.GET)
	public String batchRenewals(Model model) {
		return "batchrens";
	}
	
	@RequestMapping(value = "renprogress", method = RequestMethod.GET)
	public String renprogress(Model model) {
		return "renprogress";
	}
	
	@RequestMapping(value = { "startRenewal" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	public String startRenewals(@Valid @ModelAttribute StartRenForm startRenForm, BindingResult result,RedirectAttributes redirectAttrs,Model model){
		if(result.hasErrors()){
			redirectAttrs.addFlashAttribute("org.springframework.validation.BindingResult.startRenForm", result);
			redirectAttrs.addFlashAttribute("startRenForm", startRenForm);
			return "redirect:/protected/uw/renewals/renewalsprocess";
		 }
		
		if(startRenForm.getRenewalType()==null || StringUtils.isBlank(startRenForm.getRenewalType())){
			 redirectAttrs.addFlashAttribute("error", "Select Renewal Type");
			 redirectAttrs.addFlashAttribute("startRenForm", startRenForm);
			 return "redirect:/protected/uw/renewals/renewalsprocess";
		}
		
		if(startRenForm.getRenewalType().equalsIgnoreCase("S")){
			return "singlerenewals";
		}
		else if(startRenForm.getRenewalType().equalsIgnoreCase("B")){
			return "batchrens";
		}
		else if(startRenForm.getRenewalType().equalsIgnoreCase("P")){
			return "renprogress";
		}
		else{
			redirectAttrs.addFlashAttribute("error", "Renewal Type not Supported");
			 redirectAttrs.addFlashAttribute("startRenForm", startRenForm);
			 return "redirect:/protected/uw/renewals/renewalsprocess";
		}
	}
	
	
	@RequestMapping(value = { "renewSinglePolicy" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	public String startSingleRenewals(@Valid @ModelAttribute RenewalsForm renewalsForm, BindingResult result,RedirectAttributes redirectAttrs,Model model,HttpServletRequest request){
		if(renewalsForm.getBusinessType()==null || StringUtils.isBlank(renewalsForm.getBusinessType())){
			redirectAttrs.addFlashAttribute("error", "Select Business Type");
			redirectAttrs.addFlashAttribute("renewalsForm", renewalsForm);
			return "redirect:/protected/uw/renewals/singleren";
		}
		if(result.hasErrors()){
			redirectAttrs.addFlashAttribute("org.springframework.validation.BindingResult.renewalsForm", result);
			redirectAttrs.addFlashAttribute("renewalsForm", renewalsForm);
			return "redirect:/protected/uw/renewals/singleren";
		 }

		try {
			Long policyCode = endorseService.renewPolicy(renewalsForm);
			try {
				PolicyTrans policy = policyRepo.findOne(policyCode);
				if(policy.getBusinessType() != null && policy.getBusinessType().equalsIgnoreCase("N")) {
					premiumService.computePrem(policyCode);
				}else if(policy.getBusinessType() != null && policy.getBusinessType().equalsIgnoreCase("L")) {
					premiumService.computeLifePrem(policyCode);
				}
			} catch (BadRequestException|IOException e) {

			}
			//			request.getSession().setAttribute("policyCode", policyCode);
			//			if(endorseService.getProdutGroup(policyCode)){
			//			return "redirect:/protected/medical/policies/edituwpolicy";} else
			//				return "redirect:/protected/uw/policies/edituwpolicy";
			PolicyTrans policyTrans = policyService.getPolicyDetails(policyCode);
			request.getSession().setAttribute("policyCode", policyCode);
			User createdUser = policyTrans.getCreatedUser();
			User currentUser = userUtils.getCurrentUser();

			redirectAttrs.addFlashAttribute("createdBy", createdUser);
			redirectAttrs.addFlashAttribute("currentUser", currentUser);
			if ("MD".equalsIgnoreCase(policyTrans.getProduct().getProGroup().getPrgType())) {
				formLockManager.openForm("MEDICAL", String.valueOf(policyCode));
				return "redirect:/protected/medical/policies/edituwpolicy";
			} else if ("L".equalsIgnoreCase(policyTrans.getProduct().getProGroup().getPrgType())) {
				formLockManager.openForm("LIFE", String.valueOf(policyCode));
				return "redirect:/protected/uw/policies/editlifepolicy";
			} else {
				formLockManager.openForm("GENERAL", String.valueOf(policyCode));
				return "redirect:/protected/uw/policies/edituwpolicy";
			}
		}
		catch(EndorsementsException ex ){
			redirectAttrs.addFlashAttribute("error", ex.getMessage());
			redirectAttrs.addFlashAttribute("renewalsForm", renewalsForm);
			return "redirect:/protected/uw/renewals/singleren";
		}
		catch (AccessDeniedException ex){
			redirectAttrs.addFlashAttribute("error", "You don't have rights to initiate this Transaction");
			redirectAttrs.addFlashAttribute("renewalsForm", renewalsForm);
			return "redirect:/protected/uw/renewals/singleren";
		} catch (BadRequestException e) {
            throw new RuntimeException(e);
        }

    }
	
	
	@RequestMapping(value = { "renewalPolicies" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<EndorsementsDTO> getRevisionPolicies(@DataTable DataTablesRequest pageable,
																 @RequestParam(value = "policyNo", required = false) String policyNo,
																 @RequestParam(value = "endorseNumber", required = false) String endorseNumber,
																 @RequestParam(value = "refno", required = false) String refno,
																 @RequestParam(value = "clientName", required = false) Long clientCode,
																 @RequestParam(value = "agent", required = false) Long agentCode,
																 @RequestParam(value = "businessType", required = true) String businessType) throws IllegalAccessException {
		if(businessType.equalsIgnoreCase("G")) {
			return endorseService.findActivePolicyTrans(pageable, refno, clientCode, policyNo, endorseNumber, agentCode,"RN");
		} else {
			return endorseService.findActiveMedicalPolicyTrans(pageable, refno, agentCode, policyNo, endorseNumber, agentCode);
		}
	}
	
	@RequestMapping(value = { "countUnauthPolicies" }, method = { RequestMethod.GET })
	@ResponseBody
	public Long countUnauthPolicies(@RequestParam(value = "policyNumber", required = false) String policyNumber)
			throws IllegalAccessException {
		return endorseService.countUnauthTransactions(policyNumber);
	}

	@RequestMapping(value = { "getErrors/{policyId}" }, method = { RequestMethod.GET })
	@ResponseBody
	public PolicyTrans getStatusError(@PathVariable Long policyId)
			throws IllegalAccessException {
		return endorseService.getErrorsPol(policyId);
	}

    @RequestMapping(value = "/edituwtrans", method = RequestMethod.POST)
    public String editPolicyForm(@Valid @ModelAttribute ModelHelperForm helperForm, RedirectAttributes redirectAttrs,Model model,HttpServletRequest request) {
        request.getSession().setAttribute("policyCode", helperForm.getId());
        if(endorseService.getProdutGroup(helperForm.getId())){
            return "redirect:/protected/medical/policies/edituwpolicy";} else {
            return "redirect:/protected/uw/policies/edituwpolicy";
    }

    }

	@RequestMapping(value = { "deletePolRecord" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deletePolRecord(@RequestParam(value = "policyId", required = false) Long policyId,HttpServletRequest request) throws BadRequestException {
		endorseService.deletePolicyRecord(policyId,true);
	}
	
	@RequestMapping(value = { "unauthPolicies" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<PolicyEnquiryDTO> getUnauthInvoices(@DataTable DataTablesRequest pageable,
																@RequestParam(value = "policyNumber", required = false) String policyNumber)
			throws IllegalAccessException {
		return endorseService.findUnauthorisedPolicies(pageable, policyNumber);
	}
	
	@RequestMapping(value = { "searchBatchRenewals" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<PolicyTrans> getBatchRenewalPolicies(@DataTable DataTablesRequest pageable,
			@RequestParam(value = "wefDate", required = false) Date wefDate,
			@RequestParam(value = "wetDate", required = false) Date wetDate,
			@RequestParam(value = "productCode", required = false) Long productCode,
			@RequestParam(value = "branchId", required = false) Long branchId,
			@RequestParam(value = "agentId", required = false) Long agentId,
			@RequestParam(value = "bindCode", required = false) Long bindCode,
			@RequestParam(value = "riskId", required = false) Long riskId,
			@RequestParam(value = "clientId", required = false) Long clientId) throws IllegalAccessException {
		return endorseService.findRenewalPolicies(pageable, wefDate, wetDate, productCode, branchId, agentId, bindCode, riskId, clientId);
	}
	
	 @RequestMapping(value = { "processBatchRenewals" }, method = {
				org.springframework.web.bind.annotation.RequestMethod.POST })
		public ResponseEntity<String> processRenewals(@RequestBody BatchRenewalForm renewalForm) throws IOException, BadRequestException, InterruptedException, ExecutionException {
		 List<RenewalRecord> renewals = new ArrayList<>();
		 Long counter = 0l;
		 for(Long id:renewalForm.getRenPolicies()){
			 counter++;
			 Header header = new Header(counter, "Renewal Header", new Date());
			 RenewalDTO renForm = new RenewalDTO();
			 renForm.setPolicyId(id);
			 renForm.setUser(userUtils.getCurrentUser());
			 renewals.add(new RenewalRecord(header, renForm));
		 }
		  renewalForm.setRenewals(renewals);
		 String error =  endorseService.processBatchRenewals(renewalForm);

			return new ResponseEntity<String>(error,HttpStatus.OK);
		}
	 
	 @RequestMapping(value = { "searchRenewalProgress" }, method = { RequestMethod.GET })
		@ResponseBody
		public DataTablesResult<PolicyTrans> searchRenewalProgress(@DataTable DataTablesRequest pageable,
				@RequestParam(value = "wefDate", required = false) Date wefDate,
				@RequestParam(value = "wetDate", required = false) Date wetDate,
				@RequestParam(value = "productCode", required = false) Long productCode,
				@RequestParam(value = "branchId", required = false) Long branchId,
				@RequestParam(value = "agentId", required = false) Long agentId,
				@RequestParam(value = "bindCode", required = false) Long bindCode,
				@RequestParam(value = "riskId", required = false) Long riskId,
				@RequestParam(value = "clientId", required = false) Long clientId) throws IllegalAccessException {
			return endorseService.findRenewalProgress(pageable, wefDate, wetDate, productCode, branchId, agentId, bindCode, riskId, clientId);
		}
	 
	 @RequestMapping(value = "/editrenewals", method = RequestMethod.POST)
		public String editPolicyForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model,HttpServletRequest request) {
		    request.getSession().setAttribute("policyCode", helperForm.getId());
			return "redirect:/protected/uw/policies/edituwpolicy";
		}
	 
	 
	 @RequestMapping(value = { "makeReadyBatchRenewals" }, method = {
				org.springframework.web.bind.annotation.RequestMethod.POST })
		public ResponseEntity<String> makeReadyBatchRenewals(@RequestBody BatchRenewalForm renewalForm) throws IllegalAccessException, IOException, BadRequestException, InterruptedException, ExecutionException {
		 List<RenewalRecord> renewals = new ArrayList<>();
		 Long counter = 0l;
		 for(Long id:renewalForm.getRenPolicies()){
			 counter++;
			 Header header = new Header(counter, "Renewal Header", new Date());
			 RenewalDTO renForm = new RenewalDTO();
			 renForm.setPolicyId(id);
			 renForm.setUser(userUtils.getCurrentUser());
			 renewals.add(new RenewalRecord(header, renForm));
		 }
		  renewalForm.setRenewals(renewals);
		 String error =  endorseService.makeReadyBatchRenewals(renewalForm);
			return new ResponseEntity<String>(error,HttpStatus.OK);
		}
	 
	 
	 @RequestMapping(value = { "authoriseBatchRenewals" }, method = {
				org.springframework.web.bind.annotation.RequestMethod.POST })
		public ResponseEntity<String> authoriseBatchRenewals(@RequestBody BatchRenewalForm renewalForm) throws IllegalAccessException, IOException, BadRequestException, InterruptedException, ExecutionException {
		 List<RenewalRecord> renewals = new ArrayList<>();
		 Long counter = 0l;
		 for(Long id:renewalForm.getRenPolicies()){
			 counter++;
			 Header header = new Header(counter, "Renewal Header", new Date());
			 RenewalDTO renForm = new RenewalDTO();
			 renForm.setPolicyId(id);
			 renForm.setUser(userUtils.getCurrentUser());
			 renewals.add(new RenewalRecord(header, renForm));
		 }
		  renewalForm.setRenewals(renewals);
		 String error =  endorseService.authorizeBatchRenewals(renewalForm);
			return new ResponseEntity<String>(error,HttpStatus.OK);
		}

		@RequestMapping(value = { "selRisks" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
		@ResponseBody
		public Page<RiskTrans> selectRisks(@RequestParam(value = "term", required = false) String term, Pageable pageable)
				throws IllegalAccessException {
			return endorseService.findRenewalRisks(term, pageable);
		}

}
