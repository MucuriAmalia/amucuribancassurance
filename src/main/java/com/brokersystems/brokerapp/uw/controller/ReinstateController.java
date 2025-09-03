package com.brokersystems.brokerapp.uw.controller;

import com.brokersystems.brokerapp.certs.dto.PolicyCertificateDTO;
import com.brokersystems.brokerapp.certs.service.CertService;
import com.brokersystems.brokerapp.life.service.LifeEndorseService;
import com.brokersystems.brokerapp.medical.service.MedicalComputePrem;
import com.brokersystems.brokerapp.medical.service.MedicalEndorseService;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.exception.EndorsementsException;
import com.brokersystems.brokerapp.server.utils.DateUtilities;
import com.brokersystems.brokerapp.server.utils.FormLockManager;
import com.brokersystems.brokerapp.setup.dto.UserDTO;
import com.brokersystems.brokerapp.setup.model.ModelHelperForm;
import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.setup.repository.UserRepository;
import com.brokersystems.brokerapp.users.dto.MakerCheckDTO;
import com.brokersystems.brokerapp.users.model.MakerChecker;
import com.brokersystems.brokerapp.users.model.QMakerChecker;
import com.brokersystems.brokerapp.users.repository.MakerCheckerRepo;
import com.brokersystems.brokerapp.users.service.MakerCheckerService;
import com.brokersystems.brokerapp.uw.dtos.ClientsDto;
import com.brokersystems.brokerapp.uw.dtos.EndorsementsDTO;
import com.brokersystems.brokerapp.uw.dtos.PolicyCreateDTO;
import com.brokersystems.brokerapp.uw.dtos.PolicyEnquiryDTO;
import com.brokersystems.brokerapp.uw.model.PolicyRemarks;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.model.ReinstateForm;
import com.brokersystems.brokerapp.uw.model.RevisionForm;
import com.brokersystems.brokerapp.uw.service.EndorseService;
import com.brokersystems.brokerapp.uw.service.PolicyTransService;
import com.brokersystems.brokerapp.uw.service.PremComputeService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping({ "/protected/uw/reinstate" })
public class ReinstateController {

	@Autowired
	private EndorseService endorseService;

	@Autowired
	private PremComputeService premiumService;

	@Autowired
	private FormLockManager formLockManager;

	@Autowired
	private PolicyTransService policyService;
	@Autowired
	private CertService certService;

	@Autowired
	private MakerCheckerRepo makerCheckerRepo;
	@Autowired
	private MakerCheckerService makerCheckerService;
	@Autowired
	private UserRepository userRepository;

	@InitBinder({"reinstateForm"})
	  protected void initBinder(WebDataBinder binder) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		    dateFormat.setLenient(false);
		    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
		}

	
	@ModelAttribute
	private ReinstateForm getReinstateForm(){
		return new ReinstateForm();
	}
	
	@RequestMapping(value = "reinstatetrans",method={RequestMethod.GET})
	  public String endorsementTrans(Model model)
	  {
	    return "reinstatetrans";
	  }



	@RequestMapping(value = { "reinstateTransaction" }, method = { RequestMethod.POST })
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	public String reinstateTransaction(@Valid @ModelAttribute ReinstateForm revisionForm, BindingResult result, RedirectAttributes redirectAttrs, Model model, HttpServletRequest request)
	{
		if (result.hasErrors()) {
			redirectAttrs.addFlashAttribute("org.springframework.validation.BindingResult.reinstateForm", result);
			redirectAttrs.addFlashAttribute("reinstateForm", revisionForm);
			return "redirect:/protected/uw/reinstate/reinstatetrans";
		}

		Long policyCode = null;
		try {
			// ✅ Validate unfinished transactions before proceeding
			PolicyTrans currentTrans = endorseService.getErrorsPol(revisionForm.getPolicyId());
			if (endorseService.countUnauthTransactions(currentTrans.getPolNo()) > 0) {
				redirectAttrs.addFlashAttribute("error", "The policy has unfinished transactions. Please authorize or discard them before reinstating.");
				redirectAttrs.addFlashAttribute("reinstateForm", revisionForm);
				return "redirect:/protected/uw/reinstate/reinstatetrans";
			}

			// ✅ Proceed with reinstatement
			policyCode = endorseService.reinstatate(revisionForm);
			try {
				premiumService.computePrem(policyCode);
			} catch (BadRequestException | IOException e) {
				throw new EndorsementsException(e.getMessage());
			}

		} catch (EndorsementsException ex) {
			redirectAttrs.addFlashAttribute("error", ex.getMessage());
			redirectAttrs.addFlashAttribute("revisionForm", revisionForm);
			return "redirect:/protected/uw/reinstate/reinstatetrans";
		} catch (AccessDeniedException ex) {
			redirectAttrs.addFlashAttribute("error", "You don't have rights to initiate the transaction.");
			redirectAttrs.addFlashAttribute("revisionForm", revisionForm);
			return "redirect:/protected/uw/reinstate/reinstatetrans";
		}

		request.getSession().setAttribute("policyCode", policyCode);
		return "redirect:/protected/uw/policies/edituwpolicy";
	}



	@ExceptionHandler(EndorsementsException.class)
	public ModelAndView getSuperheroesUnavailable(EndorsementsException ex) {
		ModelAndView mv = new ModelAndView("reinstatetrans", "error", ex.getMessage());
		mv.addObject("reinstateForm", getReinstateForm());
		return mv;
	}

	@RequestMapping(value = { "cancelledPolicies" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<EndorsementsDTO> getRevisionPolicies(@DataTable DataTablesRequest pageable,
																 @RequestParam(value = "policyNo", required = false) String policyNo,
																 @RequestParam(value = "endorseNumber", required = false) String endorseNumber,
																 @RequestParam(value = "refno", required = false) String refno,
																 @RequestParam(value = "client", required = false) Long clientCode,
																 @RequestParam(value = "agent", required = false) Long agentCode,
																 @RequestParam(value = "endorsetype", required = false) String endorsetype) throws IllegalAccessException {
		return endorseService.findCancelledActivePolicyTrans(pageable, refno, clientCode, policyNo, endorseNumber,agentCode,endorsetype);
	}

	@RequestMapping(value = { "countUnauthPolicies" }, method = { RequestMethod.GET })
	@ResponseBody
	public Long countUnauthPolicies(@RequestParam(value = "policyNumber", required = false) String policyNumber)
			throws IllegalAccessException {
		return endorseService.countUnauthTransactions(policyNumber);
	}


	@RequestMapping(value = { "deletePolRecord" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deletePolRecord(@RequestParam(value = "policyId", required = false) Long policyId,HttpServletRequest request) throws BadRequestException {
		endorseService.deletePolicyRecord(policyId,false);
	}
	@RequestMapping(value = "/edituwtrans", method = RequestMethod.POST)
	public String editReinstateTransaction(@Valid @ModelAttribute ModelHelperForm helperForm, Model model, HttpServletRequest request, @RequestParam(required = false) String polId) throws BadRequestException {
		// Store policy code in session
		request.getSession().setAttribute("policyCode", helperForm.getId());

		// Fetch policy details using EndorseService
		PolicyTrans poltrans = endorseService.getErrorsPol(helperForm.getId());
		if (poltrans == null) {
			throw new BadRequestException("Policy with ID " + helperForm.getId() + " not found.");
		}

		// Set policy status based on authorization status
		if ("A".equalsIgnoreCase(poltrans.getAuthStatus())) {
			model.addAttribute("policyStatus", 2); // Authorized
		} else if ("R".equalsIgnoreCase(poltrans.getAuthStatus())) {
			model.addAttribute("policyStatus", 1); // Rejected
		} else if ("CV".equalsIgnoreCase(poltrans.getAuthStatus())) {
			model.addAttribute("policyStatus", 3); // Converted
		} else {
			model.addAttribute("policyStatus", 0); // Draft or other
		}

		// Determine if the policy is a life product
		boolean isLife = "L".equalsIgnoreCase(poltrans.getProduct().getProGroup().getPrgType());
		model.addAttribute("isLife", isLife);

		// Add policy IDs to the model
		model.addAttribute("policyId", helperForm.getId());
		model.addAttribute("polId", helperForm.getId());
		model.addAttribute("polIds", polId);

		// Redirect based on product group type
		String productGroupType = poltrans.getProduct().getProGroup().getPrgType();
		if ("MD".equalsIgnoreCase(productGroupType)) {
			formLockManager.openForm("MEDICAL", String.valueOf(helperForm.getId()));
			return "redirect:/protected/medical/policies/edituwpolicy";
		} else if ("L".equalsIgnoreCase(productGroupType)) {
			formLockManager.openForm("LIFE", String.valueOf(helperForm.getId()));
			return "lifeuwform";
		} else if ("IN".equalsIgnoreCase(productGroupType)) {
			return "investmentuwform";
		} else {
			formLockManager.openForm("GENERAL", String.valueOf(helperForm.getId()));
			return "policyuwform";
		}
	}

	@RequestMapping(value = { "getPolicyDetails" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	public ResponseEntity<PolicyTrans> getPolicyDetails(HttpServletRequest request) throws BadRequestException {
		Long polCode = (Long) request.getSession().getAttribute("policyCode");
		PolicyTrans created = policyService.getPolicyDetails(polCode);
		return new ResponseEntity<PolicyTrans>(created, HttpStatus.OK);
	}
	@RequestMapping(value = { "getPolicyRemarks" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	public ResponseEntity<PolicyRemarks> getPolicyRemarks(HttpServletRequest request) throws BadRequestException {
		Long polCode = (Long) request.getSession().getAttribute("policyCode");
		PolicyRemarks remarks = policyService.getPolicyRemarks(polCode);
		return new ResponseEntity<PolicyRemarks>(remarks, HttpStatus.OK);
	}

	@RequestMapping(value = {"getpolicyprintcerts"}, method = {RequestMethod.GET})
	@ResponseBody
	public DataTablesResult<PolicyCertificateDTO> getPrintCerts(@DataTable DataTablesRequest pageable,
																@RequestParam(value = "polId", required = false) Long polId)
	{
		return certService.findPolCertToPrint(pageable,polId);
	}


	@RequestMapping(value = { "createPolicyMakeReady" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	public ResponseEntity<PolicyCreateDTO> createPolicyMakeReady(@RequestBody PolicyCreateDTO policy,
																 HttpServletRequest request) throws BadRequestException {

		String transType = policy.getTransType();

		// Adjust dates only if not endorsement or cancellation
//	if (!"EN".equalsIgnoreCase(transType) && !"CN".equalsIgnoreCase(transType)) {
//		LocalDate today = LocalDate.now();
//		if (HolidayUtils.isHolidayOrWeekend(today)) {
//			adjustPolicyDates(policy, transType);
//		}
//	}

		Long polCode = (Long) request.getSession().getAttribute("policyCode");

		// If no checkers selected, proceed with full policy creation
		if (policy.getCheckerIds() == null || policy.getCheckerIds().isEmpty()) {
			policyService.createPolicy(policy, true);
			polCode = (Long) request.getSession().getAttribute("policyCode");
			PolicyTrans created = policyService.getPolicyDetails(polCode);
			request.getSession().setAttribute("policyCode", polCode);

			if ("NB".equalsIgnoreCase(created.getTransType()) || "SP".equalsIgnoreCase(created.getTransType())
					|| "EX".equalsIgnoreCase(created.getTransType()) || "RN".equalsIgnoreCase(created.getTransType())
					|| "BU".equalsIgnoreCase(created.getTransType()) || "LD".equalsIgnoreCase(created.getTransType())|| "RE".equalsIgnoreCase( created.getTransType())) {
				try {
					premiumService.computePrem(polCode);
				} catch (IOException e) {
					throw new BadRequestException(e.getMessage());
				}
			} else if ("EN".equalsIgnoreCase(created.getTransType())) {
				try {
					premiumService.computeEndorsePremium(polCode);
				} catch (IOException e) {
					throw new BadRequestException(e.getMessage());
				}
			}

			String response = policyService.makeReady(polCode);
			policy.setStatus(response);
		}

		//  Get latest policy details regardless (needed for maker-checker creation)
		PolicyTrans created = policyService.getPolicyDetails(polCode);

		final Long hashCode = Long.parseLong(String.valueOf(policy.hashCode()));
		List<UserDTO> eligibleCheckers = makerCheckerService.findEligibleCheckers("AUTHORIZE_POLICY", null);
		List<Long> checkerIds = new ArrayList<Long>();

		if ("CO".equalsIgnoreCase(policy.getTransType())) {
//		final Long hashCode = Long.parseLong(String.valueOf(policy.hashCode()));
//		List<UserDTO> eligibleCheckers = makerCheckerService.findEligibleCheckers("AUTHORIZE_POLICY", null);
//		List<Long> checkerIds = new ArrayList<Long>();

			if (!makerCheckerRepo.exists(QMakerChecker.makerChecker.taskType.equalsIgnoreCase("ANP")
					.and(QMakerChecker.makerChecker.policyId.eq(polCode)))) {

				for (UserDTO eligibleChecker : eligibleCheckers) {
					checkerIds.add(eligibleChecker.getId());
				}

				MakerCheckDTO makerCheckDTO = new MakerCheckDTO();
				makerCheckDTO.setStatus("N");
//			makerCheckDTO.setTaskName(String.format("Reverse Policy %s ", policy.getPolNo()));
				makerCheckDTO.setTaskName(String.format(" %s ", policy.getPolNo()));
				makerCheckDTO.setTaskType("ANP");
				makerCheckDTO.setJson(new Gson().toJson(policy));
				makerCheckDTO.setTaskCode(hashCode);
				makerCheckDTO.setAssignedCheckers(checkerIds.toString());
				makerCheckDTO.setPolicyId(policy.getPolicyId());

				makerCheckerService.checkExists(makerCheckDTO);
				makerCheckerService.createMakerChecker(makerCheckDTO);

				// Return without processing further - wait for checker approval
				return new ResponseEntity<>(policy, HttpStatus.OK);
			}
		}



		if (!makerCheckerRepo.exists(QMakerChecker.makerChecker.taskType.equalsIgnoreCase("ANP")
				.and(QMakerChecker.makerChecker.policyId.eq(polCode)))) {

			for (UserDTO eligibleChecker : eligibleCheckers) {
				checkerIds.add(eligibleChecker.getId());
			}

			MakerCheckDTO makerCheckDTO = new MakerCheckDTO();
			makerCheckDTO.setStatus("N");
//		makerCheckDTO.setTaskName(String.format("Policy %s ", policy.getPolNo()));
			makerCheckDTO.setTaskName(String.format(" %s", policy.getPolNo()));
			makerCheckDTO.setTaskType("ANP");
			makerCheckDTO.setJson(new Gson().toJson(policy));
			makerCheckDTO.setTaskCode(hashCode);
			makerCheckDTO.setAssignedCheckers(checkerIds.toString());
			makerCheckDTO.setPolicyId(created.getPolicyId());

			makerCheckerService.checkExists(makerCheckDTO);

			if (policy.getCheckerIds() != null && !policy.getCheckerIds().isEmpty()) {
				Long selectedMakerId = Long.parseLong(policy.getCheckerIds().get(0));
				makerCheckDTO.setMakerId(selectedMakerId);
			}

			makerCheckerService.createMakerChecker(makerCheckDTO);

		} else {
			MakerChecker makerChecker = makerCheckerRepo.findOne(QMakerChecker.makerChecker.taskType
					.equalsIgnoreCase("ANP").and(QMakerChecker.makerChecker.policyId.eq(created.getPolicyId())));
			if(policy.getCheckerIds()!=null) {
				for (String checkerId : policy.getCheckerIds()) {
					System.out.println("policy id: " + policy.getPolicyId() + " checker id: " + checkerId);
					Long checker_id = Long.parseLong(checkerId);
					checkerIds.add(checker_id);
				}
			}
			if (checkerIds != null && !checkerIds.isEmpty()) {
				Long firstCheckerId = checkerIds.get(0);
				User checker = userRepository.findOne(firstCheckerId);
				if (checker != null) {
					makerChecker.setCheckerId(checker);
				}
			}
			makerCheckerService.resubmitTask(makerChecker.getId(), "ANP", policy, checkerIds);
		}

		return new ResponseEntity<PolicyCreateDTO>(policy, HttpStatus.OK);
	}




}
