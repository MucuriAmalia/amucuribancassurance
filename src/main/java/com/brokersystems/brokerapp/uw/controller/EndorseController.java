package com.brokersystems.brokerapp.uw.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.brokersystems.brokerapp.life.service.LifeEndorseService;
import com.brokersystems.brokerapp.medical.service.MedicalComputePrem;
import com.brokersystems.brokerapp.medical.service.MedicalEndorseService;
import com.brokersystems.brokerapp.uw.dtos.ClientsDto;
import com.brokersystems.brokerapp.uw.dtos.EndorsementsDTO;
import com.brokersystems.brokerapp.uw.dtos.PolicyEnquiryDTO;
import com.brokersystems.brokerapp.uw.model.*;
import com.brokersystems.brokerapp.uw.service.PolicyTransService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.exception.EndorsementsException;
import com.brokersystems.brokerapp.server.utils.DateUtilities;
import com.brokersystems.brokerapp.setup.model.ModelHelperForm;
import com.brokersystems.brokerapp.uw.service.EndorseService;
import com.brokersystems.brokerapp.uw.service.PremComputeService;
import com.brokersystems.brokerapp.uw.validators.RevisionFormValidator;

@Controller
@RequestMapping({ "/protected/uw/endorsements" })
public class EndorseController {

	@Autowired
	private EndorseService endorseService;

	@Autowired
	private MedicalEndorseService medicalEndorseService;
	
	@Autowired
	private DateUtilities dateUtils;
	
	@Autowired
	private PremComputeService premiumService;

	@Autowired
	private PolicyTransService policyService;

	@Autowired
	private MedicalComputePrem medicalComputePrem;

	@Autowired
	private LifeEndorseService lifeEndorseService;


	@InitBinder({"revisionForm"})
	  protected void initBinder(WebDataBinder binder) {
			//binder.setValidator(validator);
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		    dateFormat.setLenient(false);
		    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
		}

	
	@ModelAttribute
	private RevisionForm getRevisionForm(){
		return new RevisionForm();
	}
	
	@RequestMapping(value = "endorstrans",method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String endorsementTrans(Model model)
	  {
	    return "endorstrans";
	  }

	@ModelAttribute
	private RevisionForm getRevisionBulkForm(){
		return new RevisionForm();
	}

	@RequestMapping(value = "endorsbulktrans",method={org.springframework.web.bind.annotation.RequestMethod.GET})
	public String endorsementBulkTrans(Model model)
	{
		return "endorsbulktrans";
	}

	@RequestMapping(value = "medendors",method={org.springframework.web.bind.annotation.RequestMethod.GET})
	public String medendors(Model model)
	{
		return "medendors";
	}

	@RequestMapping(value = "lifeendors",method={org.springframework.web.bind.annotation.RequestMethod.GET})
	public String lifeendors(Model model)
	{
		return "lifeendors";
	}
	
	@RequestMapping(value = "contras",method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String contraTrans(Model model)
	  {
	    return "contratrans";
	  }
	
	
	@RequestMapping(value = { "revisionPolicies" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<EndorsementsDTO> getRevisionPolicies(@DataTable DataTablesRequest pageable,
			@RequestParam(value = "policyNo", required = false) String policyNo,
			@RequestParam(value = "endorseNumber", required = false) String endorseNumber,
			@RequestParam(value = "refno", required = false) String refno,
			@RequestParam(value = "client", required = false) Long clientCode,
			@RequestParam(value = "agent", required = false) Long agentCode,
			@RequestParam(value = "endorsetype", required = false) String endorsetype) throws IllegalAccessException {
		return endorseService.findActivePolicyTrans(pageable, refno, clientCode, policyNo, endorseNumber,agentCode,endorsetype);
	}

	@RequestMapping(value = { "bulkPoliciesRevision" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<EndorsementsDTO> getBulkRevisionPolicies(@DataTable DataTablesRequest pageable,
																 @RequestParam(value = "policyNo", required = false) String policyNo,
																 @RequestParam(value = "endorseNumber", required = false) String endorseNumber,
																 @RequestParam(value = "refno", required = false) String refno,
																 @RequestParam(value = "client", required = false) Long clientCode,
																 @RequestParam(value = "agent", required = false) Long agentCode,
																 @RequestParam(value = "endorsetype", required = false) String endorsetype) throws IllegalAccessException {
		return endorseService.findActivePolicyTrans(pageable, refno, clientCode, policyNo, endorseNumber,agentCode,endorsetype);
	}

	@RequestMapping(value = { "revisionMedPolicies" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<EndorsementsDTO> getMedRevisionPolicies(@DataTable DataTablesRequest pageable,
																@RequestParam(value = "policyNo", required = false) String policyNo,
																@RequestParam(value = "endorseNumber", required = false) String endorseNumber,
																@RequestParam(value = "refno", required = false) String refno,
																@RequestParam(value = "clientName", required = false) Long clientCode,
																@RequestParam(value = "agent", required = false) Long agentCode) throws IllegalAccessException {
		return endorseService.findActiveMedicalPolicyTrans(pageable, refno, clientCode, policyNo, endorseNumber,agentCode);
	}

	@RequestMapping(value = { "revisionLifePolicies" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<PolicyTrans> getLifeRevisionPolicies(@DataTable DataTablesRequest pageable,
																@RequestParam(value = "policyNo", required = false) String policyNo,
																@RequestParam(value = "clientName", required = false) String clientName,
																@RequestParam(value = "agent", required = false) String agent) throws IllegalAccessException {
		return endorseService.findActiveLifePolicyTrans(pageable,  clientName, policyNo, agent);
	}
	
	
	@RequestMapping(value = { "contraPolicies" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<EndorsementsDTO> getContraPolicies(@DataTable DataTablesRequest pageable,
															   @RequestParam(value = "policyNo", required = false) String policyNo,
															   @RequestParam(value = "endorseNumber", required = false) String endorseNumber,
															   @RequestParam(value = "refno", required = false) String refno,
															   @RequestParam(value = "clientName", required = false) String clientName,
															   @RequestParam(value = "agent", required = false) String agent) throws IllegalAccessException {
		return endorseService.findActiveAndCancelledTrans(pageable, refno, clientName, policyNo, endorseNumber,agent);
	}
	@RequestMapping(value = { "contraLifePolicies" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<EndorsementsDTO> getContraLifePolicies(@DataTable DataTablesRequest pageable,
															   @RequestParam(value = "policyNo", required = false) String policyNo,
															   @RequestParam(value = "endorseNumber", required = false) String endorseNumber,
															   @RequestParam(value = "refno", required = false) String refno,
															   @RequestParam(value = "clientName", required = false) String clientName,
															   @RequestParam(value = "agent", required = false) String agent) throws IllegalAccessException {
		return endorseService.findActiveAndCancelledLifeTrans(pageable, refno, clientName, policyNo, endorseNumber,agent);
	}
	
	
	@RequestMapping(value = { "reuseofcontraPolicies" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<EndorsementsDTO> getReuseOfContraTrans(@DataTable DataTablesRequest pageable,
			@RequestParam(value = "policyNo", required = false) String policyNo,
			@RequestParam(value = "endorseNumber", required = false) String endorseNumber,
			@RequestParam(value = "refno", required = false) String refno,
			@RequestParam(value = "clientName", required = false) String clientName,
			@RequestParam(value = "agent", required = false) String agent) throws IllegalAccessException {
		return endorseService.findContradTransactions(pageable, refno, clientName, policyNo, endorseNumber,agent);
	}
	
	@RequestMapping(value = { "countUnauthPolicies" }, method = { RequestMethod.GET })
	@ResponseBody
	public Long countUnauthPolicies(@RequestParam(value = "policyNumber", required = false) String policyNumber)
			throws IllegalAccessException {
		return endorseService.countUnauthTransactions(policyNumber);
	}


	
	@RequestMapping(value = { "reviseTransaction" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	public String reviseTransaction(@Valid @ModelAttribute RevisionForm revisionForm, BindingResult result,RedirectAttributes redirectAttrs,Model model,HttpServletRequest request)
	{
		 if(result.hasErrors()){
			redirectAttrs.addFlashAttribute("org.springframework.validation.BindingResult.revisionForm", result);
			redirectAttrs.addFlashAttribute("revisionForm", revisionForm);
			return "redirect:/protected/uw/endorsements/endorstrans";
		 }
		 if(revisionForm.getRemarks() == null){
			 redirectAttrs.addFlashAttribute("error", "Provide remarks to continue");
			 redirectAttrs.addFlashAttribute("revisionForm", revisionForm);
			 return "redirect:/protected/uw/endorsements/endorstrans";
		 }
		 if(revisionForm.getEffectiveDate()==null){
			    redirectAttrs.addFlashAttribute("error", "Provide Effective Date to continue");
				redirectAttrs.addFlashAttribute("revisionForm", revisionForm);
				return "redirect:/protected/uw/endorsements/endorstrans";
		 }
		Long policyCode = null;
		if ("CO".equalsIgnoreCase(revisionForm.getRevisionType())) {
			redirectAttrs.addFlashAttribute("error", "Transaction Type not supported");
			redirectAttrs.addFlashAttribute("revisionForm", revisionForm);
			return "redirect:/protected/uw/endorsements/endorstrans";
			
		} else if ("CN".equalsIgnoreCase(revisionForm.getRevisionType())
				|| "EN".equalsIgnoreCase(revisionForm.getRevisionType())
				|| "RS".equalsIgnoreCase(revisionForm.getRevisionType())
				|| "EX".equalsIgnoreCase(revisionForm.getRevisionType())) {
			try{
				policyCode = endorseService.reviseTransaction(revisionForm);
				if ("EX".equalsIgnoreCase(revisionForm.getRevisionType())){
					try {
						premiumService.computePrem(policyCode);
					} catch (BadRequestException|IOException e) {

					}
				}
				if ("RS".equalsIgnoreCase(revisionForm.getRevisionType())){
					try {
                        premiumService.computeEndorsePremium(policyCode);
					} catch (BadRequestException|IOException  e) {
						throw new EndorsementsException(e.getMessage());
					}
				}
					
				if ("CN".equalsIgnoreCase(revisionForm.getRevisionType())){
					try {
						premiumService.computeCancelPrem(policyCode);
					} catch (BadRequestException e) {
						throw new EndorsementsException(e.getMessage());
					}
				}
				
			}
			catch(EndorsementsException ex ){
				redirectAttrs.addFlashAttribute("error", ex.getMessage());
				redirectAttrs.addFlashAttribute("revisionForm", revisionForm);
				return "redirect:/protected/uw/endorsements/endorstrans";
			}
			catch (AccessDeniedException ex){
				redirectAttrs.addFlashAttribute("error", "You don't have rights to initiate the transaction..");
				redirectAttrs.addFlashAttribute("revisionForm", revisionForm);
				return "redirect:/protected/uw/endorsements/endorstrans";
			}
			
		} else {
			redirectAttrs.addFlashAttribute("error", "Transaction Type not supported");
			redirectAttrs.addFlashAttribute("revisionForm", revisionForm);
			return "redirect:/protected/uw/endorsements/endorstrans";
		}
		request.getSession().setAttribute("policyCode", policyCode);
		return "redirect:/protected/uw/policies/edituwpolicy";
	}
	
	@ExceptionHandler(EndorsementsException.class)
	public ModelAndView getSuperheroesUnavailable(EndorsementsException ex) {
		ModelAndView mv = new ModelAndView("endorstrans", "error", ex.getMessage());
		mv.addObject("revisionForm", getRevisionForm());
		return mv;
	}
	
	@RequestMapping(value = { "unauthPolicies" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<PolicyEnquiryDTO> getUnauthInvoices(@DataTable DataTablesRequest pageable,
																@RequestParam(value = "policyNumber", required = false) String policyNumber)
			throws IllegalAccessException {
		return endorseService.findUnauthorisedPolicies(pageable, policyNumber);
	}
	
	@RequestMapping(value = "/edituwtrans", method = RequestMethod.POST)
	public String editPolicyForm(@Valid @ModelAttribute ModelHelperForm helperForm, RedirectAttributes redirectAttrs,Model model,HttpServletRequest request) throws BadRequestException {
		request.getSession().setAttribute("policyCode", helperForm.getId());
		PolicyTrans policyTrans = policyService.getPolicyDetails(helperForm.getId());
		if("MD".equalsIgnoreCase(policyTrans.getProduct().getProGroup().getPrgType()))
			return  "redirect:/protected/medical/policies/edituwpolicy";
		else
		return "redirect:/protected/uw/policies/edituwpolicy";
	}
	
	@RequestMapping(value = { "deletePolRecord" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deletePolRecord(@RequestParam(value = "policyId", required = false) Long policyId,HttpServletRequest request) throws BadRequestException {
		endorseService.deletePolicyRecord(policyId,false);
	}
	
	
	 @RequestMapping(value = { "endorsinsureds" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
		@ResponseBody
		public Page<ClientsDto> endorsementRisks(@RequestParam(value = "term", required = false) String term, @RequestParam("polCode") Long polCode, Pageable pageable)
				throws IllegalAccessException {
			return endorseService.findActiveInsureds(term, pageable,polCode);
		}
	 
	 @RequestMapping(value = { "getWetDate" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
		@ResponseBody
		public Date getPolicyWetDate(@RequestParam(value = "wefDate", required = false) Date wef){
			return dateUtils.getWetDate(wef);
		}

	 
	 @RequestMapping(value = { "contraTransaction" }, method = {
				org.springframework.web.bind.annotation.RequestMethod.POST })
		@Transactional(readOnly = false, rollbackFor = { BadRequestException.class, EndorsementsException.class })
		public String contraTransaction(@Valid @ModelAttribute RevisionForm revisionForm, BindingResult result,RedirectAttributes redirectAttrs,Model model,HttpServletRequest request) {
		 if (result.hasErrors()) {
			 redirectAttrs.addFlashAttribute("org.springframework.validation.BindingResult.revisionForm", result);
			 redirectAttrs.addFlashAttribute("revisionForm", revisionForm);
			 return "redirect:/protected/uw/endorsements/contras";
		 }
		 if(revisionForm.getRemarks() == null){
			 redirectAttrs.addFlashAttribute("error", "Provide remarks to continue");
			 redirectAttrs.addFlashAttribute("revisionForm", revisionForm);
			 return "redirect:/protected/uw/endorsements/contras";
		 }
		 try {
			 Long policyCode = null;
			 PolicyTrans policyDetails = null;
			 if ("CO".equalsIgnoreCase(revisionForm.getRevisionType())) {
				 try {
					 PolicyTrans originalPolicy = policyService.getPolicyDetails(revisionForm.getPolicyId());

					 // Route to appropriate service based on policy type
					 if (isLifePolicy(originalPolicy)) {
						 policyCode = endorseService.contraLifePolicy(revisionForm);
					 } else {
						 policyCode = endorseService.contraPolicy(revisionForm);
					 }

					 // Get the newly created contra policy details
					 policyDetails = policyService.getPolicyDetails(policyCode);

				 } catch (BadRequestException e) {
					 throw new EndorsementsException(e.getMessage());
				 }

//				 policyCode = endorseService.contraPolicy(revisionForm);
//
//				 try {
//					 policyDetails = policyService.getPolicyDetails(policyCode);
//				 } catch (BadRequestException e) {
//					 throw new EndorsementsException(e.getMessage());
//				 }

			 } else if ("RE".equalsIgnoreCase(revisionForm.getRevisionType())) {

				 PolicyTrans created = endorseService.reuseOfContra(revisionForm);
				 policyCode = created.getPolicyId();
				 policyDetails = created;

				 if ("NB".equalsIgnoreCase(created.getTransType()) || "SP".equalsIgnoreCase(created.getTransType()) || "EX".equalsIgnoreCase(created.getTransType()))
					 try {
						 premiumService.computePrem(policyCode);
					 } catch (BadRequestException|IOException e) {
				 throw new EndorsementsException(e.getMessage());
			 }
				 else if ("EN".equalsIgnoreCase(created.getTransType())) {
					 try {
						 premiumService.computeEndorsePremium(policyCode);
					 } catch (BadRequestException|IOException e) {
						 throw new EndorsementsException(e.getMessage());
					 }
				 }

			 } else {
				 redirectAttrs.addFlashAttribute("error", "Transaction Type not supported");
				 redirectAttrs.addFlashAttribute("revisionForm", revisionForm);
				 return "redirect:/protected/uw/endorsements/contras";
			 }
			 request.getSession().setAttribute("policyCode", policyCode);

			 if (isLifePolicy(policyDetails)) {
				 return "redirect:/protected/uw/policies/editlifepolicy";
			 } else {
				 return "redirect:/protected/uw/policies/edituwpolicy";
			 }
		 } catch (EndorsementsException ex) {
			 redirectAttrs.addFlashAttribute("error", ex.getMessage());
			 redirectAttrs.addFlashAttribute("revisionForm", revisionForm);
			 return "redirect:/protected/uw/endorsements/contras";

		 } catch (AccessDeniedException ex) {
			 redirectAttrs.addFlashAttribute("error", "You don't have rights to initiate this Transaction");
			 redirectAttrs.addFlashAttribute("revisionForm", revisionForm);
			 return "redirect:/protected/uw/endorsements/contras";

		 }
	 }
	private boolean isLifePolicy(PolicyTrans policy) {
		if (policy == null) {
			return false;
		}
		if ("L".equalsIgnoreCase(policy.getBusinessType())) {
			return true;
		}

		return false;
	}

	@RequestMapping(value = { "reviseMedTransaction" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	public String reviseMedicalTransaction(@Valid @ModelAttribute RevisionForm revisionForm, BindingResult result,RedirectAttributes redirectAttrs,Model model,HttpServletRequest request)
	{
		if(result.hasErrors()){
			redirectAttrs.addFlashAttribute("org.springframework.validation.BindingResult.revisionForm", result);
			redirectAttrs.addFlashAttribute("revisionForm", revisionForm);
			return "redirect:/protected/uw/endorsements/medendors";
		}
		if(revisionForm.getEffectiveDate()==null){
			redirectAttrs.addFlashAttribute("error", "Provide Effective Date to continue");
			redirectAttrs.addFlashAttribute("revisionForm", revisionForm);
			return "redirect:/protected/uw/endorsements/medendors";
		}
		Long policyCode = null;
       if (("AD".equalsIgnoreCase(revisionForm.getRevisionType()))||
			   ("CR".equalsIgnoreCase(revisionForm.getRevisionType()))||
			   ("CN".equalsIgnoreCase(revisionForm.getRevisionType()))) {
			try{
				policyCode = medicalEndorseService.reviseTransaction(revisionForm);
				request.getSession().setAttribute("policyCode", policyCode);

				if (("CN".equalsIgnoreCase(revisionForm.getRevisionType()))){
					try{
						medicalComputePrem.computePrem(policyCode);
					}
					catch (BadRequestException|IOException e) {
						throw new EndorsementsException(e.getMessage());
					}
				}
				return "redirect:/protected/medical/policies/edituwpolicy";
			}
			catch(EndorsementsException ex ){
				redirectAttrs.addFlashAttribute("error", ex.getMessage());
				redirectAttrs.addFlashAttribute("revisionForm", revisionForm);
				return "redirect:/protected/uw/endorsements/medendors";
			}
			catch (AccessDeniedException ex){
				redirectAttrs.addFlashAttribute("error", "You don't have rights to initiate the transaction..");
				redirectAttrs.addFlashAttribute("revisionForm", revisionForm);
				return "redirect:/protected/uw/endorsements/medendors";
			}

		} else {
			redirectAttrs.addFlashAttribute("error", "Transaction Type not supported");
			redirectAttrs.addFlashAttribute("revisionForm", revisionForm);
			return "redirect:/protected/uw/endorsements/medendors";
		}

	}

	@RequestMapping(value = { "reviseLifeTransaction" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	public String reviseLifeTransaction(@Valid @ModelAttribute RevisionForm revisionForm, BindingResult result,RedirectAttributes redirectAttrs,Model model,HttpServletRequest request)
	{
		if(result.hasErrors()){
			redirectAttrs.addFlashAttribute("org.springframework.validation.BindingResult.revisionForm", result);
			redirectAttrs.addFlashAttribute("revisionForm", revisionForm);
			return "redirect:/protected/uw/endorsements/lifeendors";
		}
		if(revisionForm.getRemarks() == null){
			redirectAttrs.addFlashAttribute("error", "Provide remarks to continue");
			redirectAttrs.addFlashAttribute("revisionForm", revisionForm);
			return "redirect:/protected/uw/endorsements/lifeendors";
		}
		if(revisionForm.getEffectiveDate()==null){
			redirectAttrs.addFlashAttribute("error", "Provide Effective Date to continue");
			redirectAttrs.addFlashAttribute("revisionForm", revisionForm);
			return "redirect:/protected/uw/endorsements/lifeendors";
		}
		Long policyCode = null;
		if ("CO".equalsIgnoreCase(revisionForm.getRevisionType())) {
			redirectAttrs.addFlashAttribute("error", "Transaction Type not supported");
			redirectAttrs.addFlashAttribute("revisionForm", revisionForm);
			return "redirect:/protected/uw/endorsements/lifeendors";

		} else if ("CN".equalsIgnoreCase(revisionForm.getRevisionType())
				|| "EN".equalsIgnoreCase(revisionForm.getRevisionType())) {
			try{
				policyCode = lifeEndorseService.reviseTransaction(revisionForm);

				if ("CN".equalsIgnoreCase(revisionForm.getRevisionType())){
					try {
						premiumService.computeCancelPrem(policyCode);
					} catch (BadRequestException e) {
						throw new EndorsementsException(e.getMessage());
					}
				}

			}
			catch(EndorsementsException ex ){
				redirectAttrs.addFlashAttribute("error", ex.getMessage());
				redirectAttrs.addFlashAttribute("revisionForm", revisionForm);
				return "redirect:/protected/uw/endorsements/lifeendors";
			}
			catch (AccessDeniedException ex){
				redirectAttrs.addFlashAttribute("error", "You don't have rights to initiate the transaction..");
				redirectAttrs.addFlashAttribute("revisionForm", revisionForm);
				return "redirect:/protected/uw/endorsements/lifeendors";
			}

		} else {
			redirectAttrs.addFlashAttribute("error", "Transaction Type not supported");
			redirectAttrs.addFlashAttribute("revisionForm", revisionForm);
			return "redirect:/protected/uw/endorsements/lifeendors";
		}
		request.getSession().setAttribute("policyCode", policyCode);
		return "redirect:/protected/life/policies/edituwpolicy";
	}

}
