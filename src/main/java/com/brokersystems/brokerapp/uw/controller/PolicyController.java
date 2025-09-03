
package com.brokersystems.brokerapp.uw.controller;

import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.validation.Valid;

import com.brokersystems.brokerapp.accounts.service.AccountsService;
import com.brokersystems.brokerapp.aki.dto.DigitalObject;
import com.brokersystems.brokerapp.aki.service.AkiAuthenticationService;
import com.brokersystems.brokerapp.auditlogs.DTO.AuditTrailDTO;
import com.brokersystems.brokerapp.auditlogs.model.RtsAudit;
import com.brokersystems.brokerapp.auditlogs.repositories.RtsAuditRepository;
import com.brokersystems.brokerapp.certs.dto.CertTypesDTO;
import com.brokersystems.brokerapp.certs.dto.PolicyCertificateDTO;
import com.brokersystems.brokerapp.certs.model.*;
import com.brokersystems.brokerapp.certs.repository.BranchCertsRepo;
import com.brokersystems.brokerapp.certs.repository.PolicyCertsRepo;
import com.brokersystems.brokerapp.certs.repository.PrintQueueRepo;
import com.brokersystems.brokerapp.certs.service.CertService;
import com.brokersystems.brokerapp.claims.dtos.ClaimDetailsDTO;
import com.brokersystems.brokerapp.claims.model.ClaimBookings;
import com.brokersystems.brokerapp.claims.model.ClaimPerils;
import com.brokersystems.brokerapp.claims.service.ClaimService;
import com.brokersystems.brokerapp.customscreens.model.ColumnForm;
import com.brokersystems.brokerapp.customscreens.model.TableForm;
import com.brokersystems.brokerapp.dms.model.UploadBean;
import com.brokersystems.brokerapp.dms.service.UploadService;
import com.brokersystems.brokerapp.life.model.*;
import com.brokersystems.brokerapp.life.service.LifeService;
import com.brokersystems.brokerapp.mail.model.MailMessageBean;
import com.brokersystems.brokerapp.mail.model.MailTemplates;
import com.brokersystems.brokerapp.mail.service.MailTemplateService;
import com.brokersystems.brokerapp.mail.service.Mailer;
import com.brokersystems.brokerapp.quotes.services.QuotationService;
import com.brokersystems.brokerapp.schedules.model.ScheduleBean;
import com.brokersystems.brokerapp.schedules.model.ScheduleTrans;
import com.brokersystems.brokerapp.schedules.service.ScheduleService;
import com.brokersystems.brokerapp.server.utils.*;
import com.brokersystems.brokerapp.setup.dto.*;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.BindersRepo;
import com.brokersystems.brokerapp.setup.repository.ClientRepository;
import com.brokersystems.brokerapp.setup.repository.UserRepository;
import com.brokersystems.brokerapp.setup.service.SetupsService;
import com.brokersystems.brokerapp.trans.model.ReceiptTrans;
import com.brokersystems.brokerapp.trans.model.ReceiptTransDtls;
import com.brokersystems.brokerapp.trans.model.TransChecks;
import com.brokersystems.brokerapp.trans.utils.HibernateProxyTypeAdapter;
import com.brokersystems.brokerapp.users.dto.MakerCheckDTO;
import com.brokersystems.brokerapp.users.model.MakerChecker;
import com.brokersystems.brokerapp.users.model.QMakerChecker;
import com.brokersystems.brokerapp.users.repository.MakerCheckerRepo;
import com.brokersystems.brokerapp.users.service.MakerCheckerService;
import com.brokersystems.brokerapp.uw.dtos.*;
import com.brokersystems.brokerapp.uw.model.*;
import com.brokersystems.brokerapp.uw.repository.PolicyMiscInfoRepo;
import com.brokersystems.brokerapp.uw.repository.PolicyTransRepo;
import com.brokersystems.brokerapp.uw.repository.RiskDocsRepo;
import com.brokersystems.brokerapp.uw.repository.RiskTransRepo;
import com.brokersystems.brokerapp.webservices.model.VehicleDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mysema.query.types.Predicate;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.proxy.HibernateProxy;
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
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.service.OrganizationService;
import com.brokersystems.brokerapp.trans.service.PolicyAuthorization;
import com.brokersystems.brokerapp.uw.service.EndorseService;
import com.brokersystems.brokerapp.uw.service.PolicyTransService;
import com.brokersystems.brokerapp.uw.service.PremComputeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Controller
@RequestMapping({ "/protected/uw/policies" })

public class PolicyController {
	// Usage:
	private static final Logger log = LoggerFactory.getLogger(PolicyController.class);
	@Autowired
	private UserRepository userRepository;


	@Autowired
	private PolicyTransService policyService;

	@Autowired
	private PolicyTransRepo policyTransRepo;

	@Autowired
	private AkiAuthenticationService akiAuthenticationService;

	@Autowired
	private DateUtilities dateUtils;

	@Autowired
	private PremComputeService premiumService;

	@Autowired
	private PolicyAuthorization authService;

	@Autowired
	private OrganizationService orgService;

	@Autowired
	private DataSource datasource;

	@Autowired
	private EndorseService endorseService;

	@Autowired
	private CertService certService;

	@Autowired
	private ScheduleService scheduleService;

	@Autowired
	private SetupsService setupsService;

	@Autowired
	private Mailer mailer;

	@Autowired
	private MailTemplateService templateService;

    @Autowired
	private UploadService uploadService;

	@Autowired
	private RiskDocsRepo riskDocsRepo;

	@Autowired
	private DateUtilities dateUtilities;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private PrintQueueRepo printQueueRepo;

	@Autowired
	private PolicyCertsRepo policyCertsRepo;

	@Autowired
	private PolicyMiscInfoRepo policyMiscInfoRepo;

	@Autowired
	private BindersRepo bindersRepo;

	@Autowired
	private LifeService lifeService;

	@Autowired
	private QuotationService quotationService;


	@Autowired
	private BranchCertsRepo branchCertsRepo;

	@Autowired
	private UserUtils userUtils;

    @Autowired
	private AccountsService accountsService;

	@Autowired
	private AuditTrailLogger auditTrailLogger;

	@Autowired
	private ClaimService claimService;
	@Autowired
	private MakerCheckerRepo makerCheckerRepo;
    @Autowired
    private MakerCheckerService makerCheckerService;
    @Autowired
    private FormLockManager formLockManager;

	@Autowired
	private RtsAuditRepository rtsAuditRepository;

    @Autowired
    private RiskTransRepo riskTransRepo;

//	@InitBinder
//	protected void initBinder(WebDataBinder binder) {
//		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//		dateFormat.setLenient(false);
//		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
//	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	@RequestMapping(value = "policyEnquiry", method = {org.springframework.web.bind.annotation.RequestMethod.GET})
	public String policyEnquiry(Model model) {
		return "polenquiry";
	}

	@RequestMapping(value = "masterenq",method={org.springframework.web.bind.annotation.RequestMethod.GET})
	public String masterEnquiry(Model model,@RequestParam(required = false) String clntId,
								@RequestParam(required = false) String polId,
								@RequestParam(required = false) String group,
								@RequestParam(required = false) String claimId)
	{
		model.addAttribute("clntId",clntId);
        model.addAttribute("polIds",polId);
		model.addAttribute("group",group);
		model.addAttribute("claimId",claimId);
		return "mstrenquiry";
	}
	@RequestMapping(value = "lapsepolicies",method={org.springframework.web.bind.annotation.RequestMethod.GET})
	public String lapsepolicies(Model model)
	{
		return "lapsepolicies";
	}

	@RequestMapping(value = "quickUw",method={org.springframework.web.bind.annotation.RequestMethod.GET})
	public String quickUw(Model model)
	{
		return "quickw";
	}


	@RequestMapping(value = "pendingTrans",method={org.springframework.web.bind.annotation.RequestMethod.GET})
	public String pendingTrans(Model model)
	{
		return "pendingUwTrans";
	}





	@RequestMapping(value = "uwform", method = RequestMethod.GET)
	public String tenantForm(Model model,HttpServletRequest request) {
		request.getSession().removeAttribute("policyCode");
		model.addAttribute("policyId", -2000);
		model.addAttribute("policyStatus", 0);
		String resource = "uwform";
		auditTrailLogger.log("Accessed General Insurance Underwriting Screen ",request, resource);
		return "policyuwform";
	}

	@RequestMapping(value = "editpolicy", method = RequestMethod.GET)
	public String editpolicy(Model model,HttpServletRequest request) throws BadRequestException {
		Long polCode = (Long) request.getSession().getAttribute("policyCode");
		if(polCode==null){
			model.addAttribute("policyId", -2000);
            model.addAttribute("policyStatus", 0);
		}else {
            model.addAttribute("policyId", polCode);
        }
		if(polCode!=null) {
            PolicyTrans poltrans = policyService.getPolicyDetails(polCode);
            if (poltrans.getAuthStatus() != null && poltrans.getAuthStatus().equalsIgnoreCase("A")) {
                model.addAttribute("policyStatus", 2);
            } else if (poltrans.getAuthStatus() != null && poltrans.getAuthStatus().equalsIgnoreCase("R"))
                model.addAttribute("policyStatus", 1);
			else if (poltrans.getAuthStatus()!=null  && poltrans.getAuthStatus().equalsIgnoreCase("CV"))
				model.addAttribute("policyStatus", 3);
            else model.addAttribute("policyStatus", 0);
        }else
			model.addAttribute("policyStatus", 0);
		return "policyuwform";
	}

	@RequestMapping(value = "edituwpolicy", method = RequestMethod.GET)
    public String edituwpolicy(Model model, HttpServletRequest request) throws BadRequestException {
        Long polCode = (Long) request.getSession().getAttribute("policyCode");
		User currentUser = userUtils.getCurrentUser();
		model.addAttribute("currentUserId", currentUser.getId());
        model.addAttribute("policyId", polCode);
        if (polCode != null) {
            PolicyTrans poltrans = policyService.getPolicyDetails(polCode);
			boolean isCreator = poltrans.getCreatedUser() != null &&
					poltrans.getCreatedUser().getId().equals(currentUser.getId());
			model.addAttribute("isPolicyCreator", isCreator);
            if (poltrans.getAuthStatus() != null && poltrans.getAuthStatus().equalsIgnoreCase("A")) {
                model.addAttribute("policyStatus", 2);
            } else if (poltrans.getAuthStatus() != null && poltrans.getAuthStatus().equalsIgnoreCase("R"))
                model.addAttribute("policyStatus", 1);
            else if (poltrans.getAuthStatus() != null && poltrans.getAuthStatus().equalsIgnoreCase("CV"))
                model.addAttribute("policyStatus", 3);
            else model.addAttribute("policyStatus", 0);

            if (poltrans.getPolRevStatus().equalsIgnoreCase("RS")) {
                model.addAttribute("policyStatus", 1);
            }

        }
        return "policyuwform";
    }

	@RequestMapping(value = "editlifepolicy", method = RequestMethod.GET)
	public String editLifepolicy(Model model,HttpServletRequest request) throws BadRequestException {
		Long polCode = (Long) request.getSession().getAttribute("policyCode");
		User currentUser = userUtils.getCurrentUser();
		model.addAttribute("currentUserId", currentUser.getId());
		PolicyTrans policyTrans = policyService.getPolicyDetails(polCode);
		model.addAttribute("policyId", polCode);
		boolean isCreator = policyTrans.getCreatedUser() != null &&
				policyTrans.getCreatedUser().getId().equals(currentUser.getId());
		model.addAttribute("isPolicyCreator", isCreator);
		PolicyTrans poltrans =policyService.getPolicyDetails(polCode);

		boolean isLife = "L".equalsIgnoreCase(poltrans.getProduct().getProGroup().getPrgType());
		System.out.println("checking reports "+ isLife+ " group being shown "+poltrans.getProduct().getProGroup().getPrgType());
		model.addAttribute("isLife", isLife);

		String typeOfIntegration = "NA";
		if (poltrans.getBinder() != null && poltrans.getBinder().getCalculatorType() != null &&
				poltrans.getBinder().getCalculatorType().equalsIgnoreCase("I")) {

			String binName = poltrans.getBinder().getBinName();
			System.out.println("Bin name"+ binName);
			if (binName.toUpperCase().contains("ALAK FAMILY PROTECTION PLAN")) {
				typeOfIntegration = "ALAK_FPP";
			} else if (binName.toUpperCase().contains("ALAK ULTIMATE PROTECTOR")) {
				typeOfIntegration = "ALAK_UP";
			} else if (binName.toUpperCase().contains("ALAK PERSONAL ACCIDENT")) {
				typeOfIntegration = "ALAK_PA";
			}else if(binName.toUpperCase().contains("ALAK ENDOWMENT")){
				//endowment
				typeOfIntegration = "ALAK_ENDOWMENT";
			}
		}
		model.addAttribute("typeOfIntegration", typeOfIntegration);

		System.out.println("from edit life policy" + poltrans.getAuthStatus());
		if ( poltrans.getAuthStatus()!=null && poltrans.getAuthStatus().equalsIgnoreCase("A")){
			model.addAttribute("policyStatus", 2);
		}else if (poltrans.getAuthStatus()!=null  && poltrans.getAuthStatus().equalsIgnoreCase("R"))
			model.addAttribute("policyStatus", 1);
		else if (poltrans.getAuthStatus()!=null  && poltrans.getAuthStatus().equalsIgnoreCase("CV"))
			model.addAttribute("policyStatus", 3);
		else model.addAttribute("policyStatus", 0);
		return "lifeuwform";
	}

	@RequestMapping(value = "/edituwtrans", method = RequestMethod.POST)
	public String editPolicyForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model,HttpServletRequest request,@RequestParam(required = false) String polId) throws BadRequestException {

		request.getSession().setAttribute("policyCode", helperForm.getId());
		PolicyTrans poltrans = policyService.getPolicyDetails(helperForm.getId());
        if ( poltrans.getAuthStatus()!=null && poltrans.getAuthStatus().equalsIgnoreCase("A")){
            model.addAttribute("policyStatus", 2);
        }else if (poltrans.getAuthStatus()!=null  && poltrans.getAuthStatus().equalsIgnoreCase("R"))
            model.addAttribute("policyStatus", 1);
        else if (poltrans.getAuthStatus()!=null  && poltrans.getAuthStatus().equalsIgnoreCase("CV"))
            model.addAttribute("policyStatus", 3);
        else model.addAttribute("policyStatus", 0);

		boolean isLife = "L".equalsIgnoreCase(poltrans.getProduct().getProGroup().getPrgType());
		System.out.println("checking reports "+ isLife+ " group being shown "+poltrans.getProduct().getProGroup().getPrgType());
		model.addAttribute("isLife", isLife);


		if("MD".equalsIgnoreCase(poltrans.getProduct().getProGroup().getPrgType())) {
			model.addAttribute("policyId", helperForm.getId());
			model.addAttribute("polId",helperForm.getId());
			model.addAttribute("polIds",polId);
			formLockManager.openForm("MEDICAL", String.valueOf(helperForm.getId()));
			return "redirect:/protected/medical/policies/edituwpolicy";
		}
		else if("L".equalsIgnoreCase(poltrans.getProduct().getProGroup().getPrgType())){

			model.addAttribute("policyId", helperForm.getId());
			model.addAttribute("polId",helperForm.getId());
			model.addAttribute("polIds",polId);
			formLockManager.openForm("LIFE", String.valueOf(helperForm.getId()));

			return "lifeuwform";
		}
		else if("IN".equalsIgnoreCase(poltrans.getProduct().getProGroup().getPrgType())){

			model.addAttribute("policyId", helperForm.getId());
			model.addAttribute("polId",helperForm.getId());
			model.addAttribute("polIds",polId);
			return "investmentuwform";
		}
		else{
			model.addAttribute("policyId", helperForm.getId());
			model.addAttribute("polId",helperForm.getId());
			model.addAttribute("polIds",polId);
			formLockManager.openForm("GENERAL", String.valueOf(helperForm.getId()));
			return "policyuwform";
		}

	}
	@RequestMapping(value = "/groupedituwtrans", method = RequestMethod.POST)
	public String editGroupPolicyForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model,HttpServletRequest request,@RequestParam(required = false) String polId) throws BadRequestException {

		request.getSession().setAttribute("policyCode", helperForm.getId());
		PolicyTrans poltrans = policyService.getPolicyDetails(helperForm.getId());

		boolean isLife = "L".equalsIgnoreCase(poltrans.getProduct().getProGroup().getPrgType());
		System.out.println("checking reports "+ isLife+ " group being shown "+poltrans.getProduct().getProGroup().getPrgType());
		model.addAttribute("isLife", isLife);

		if ( poltrans.getAuthStatus()!=null && poltrans.getAuthStatus().equalsIgnoreCase("A")){
			model.addAttribute("policyStatus", 2);
		}else if (poltrans.getAuthStatus()!=null  && poltrans.getAuthStatus().equalsIgnoreCase("R"))
			model.addAttribute("policyStatus", 1);
		else if (poltrans.getAuthStatus()!=null  && poltrans.getAuthStatus().equalsIgnoreCase("CV"))
			model.addAttribute("policyStatus", 3);
		else model.addAttribute("policyStatus", 0);
		if("MD".equalsIgnoreCase(poltrans.getProduct().getProGroup().getPrgType())) {
			model.addAttribute("policyId", helperForm.getId());
			model.addAttribute("polId",helperForm.getId());
			model.addAttribute("group","group");

			model.addAttribute("polIds",polId);
			return "redirect:/protected/medical/policies/edituwpolicy";
		}
		else if("L".equalsIgnoreCase(poltrans.getProduct().getProGroup().getPrgType())){

			model.addAttribute("policyId", helperForm.getId());
			model.addAttribute("polId",helperForm.getId());
			model.addAttribute("polIds",polId);
			model.addAttribute("group","group");

			return "lifeuwform";
		}
		else{
			model.addAttribute("policyId", helperForm.getId());
			model.addAttribute("polId",helperForm.getId());
			model.addAttribute("polIds",polId);
			model.addAttribute("group","group");

			return "policyuwform";
		}

	}
	@RequestMapping(value = { "getLifeClientAge" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
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
//	@RequestMapping(value = { "uwClients" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
//	@ResponseBody
//	public Page<ClientDef> selectClients(@RequestParam(value = "term", required = false) String term, Pageable pageable)
//			throws IllegalAccessException {
//		return policyService.findActiveClients(term, pageable);
//	}

	@RequestMapping(value = { "uwClients" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<ClientsDto> selectClients(@RequestParam(value = "term", required = false) String term, Pageable pageable)
			throws IllegalAccessException {
		return policyService.findActiveClients(term, pageable);
	}
	@RequestMapping(value = { "allClients" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<ClientDef> selectAllClients(@RequestParam(value = "term", required = false) String term, Pageable pageable)
			throws IllegalAccessException {
		return policyService.findAllClients(term, pageable);
	}
	@RequestMapping(value = { "allPolicies" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<PolicyTrans> selectAllPolicies(@RequestParam(value = "term", required = false) String term, Pageable pageable)
			throws IllegalAccessException {
		return policyService.findAllPols(term, pageable);
	}
	@RequestMapping(value = { "allRisksLov" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<RiskTrans> allRisksLov(@RequestParam(value = "term", required = false) String term, Pageable pageable)
			throws IllegalAccessException {
		return policyService.allRisksLov(term, pageable);
	}
	@RequestMapping(value = { "clientPolicies" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<PolicyTrans> selectClientPolicy(@RequestParam(value = "term", required = false) String term,@RequestParam("clientId") Long clientId, Pageable pageable)
			throws IllegalAccessException {
		return policyService.findClientPolicies(term, pageable,clientId);
	}



	@RequestMapping(value = { "uwBinders" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<BinderDTO> selectGeneralBinders(@RequestParam(value = "term", required = false) String term, Pageable pageable, @RequestParam("productId") Long productId)
			 {
		return policyService.findInsuranceBinder(term, pageable,null,productId);
	}

	@RequestMapping(value = { "uwLifeBinders" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<BinderDTO> selectLifeBinders(@RequestParam(value = "term", required = false) String term, Pageable pageable)
	{
		return policyService.findLifeInsuranceBinder(term, pageable,null,null);
	}

	@RequestMapping(value = { "uwCompBinders" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<BinderDTO> selectCompBinders(@RequestParam(value = "term", required = false) String term, Pageable pageable, @RequestParam("quotId") Long quotId)
	{
		return policyService.findCompInsuranceBinder(term, pageable,null, quotId);
	}

	@RequestMapping(value = { "uwMultiProducts" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<ProductsDef> selectMultiProducts(@RequestParam(value = "term", required = false) String term, Pageable pageable)
			throws IllegalAccessException {
		return policyService.findMultiProducts(term, pageable);
	}





	@RequestMapping(value = { "uwcurrencies" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<CurrencyDTO> selectCurrencies(@RequestParam(value = "term", required = false) String term, Pageable pageable)
			throws IllegalAccessException {
		return policyService.findCurrencies(term, pageable);
	}

	@RequestMapping(value = { "othercurrencies" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<CurrencyDTO> selectOtherCurrencies(@RequestParam(value = "term", required = false) String term, Pageable pageable)
			throws IllegalAccessException {
		return policyService.findOtherCurrencies(term, pageable);
	}

	@RequestMapping(value = { "uwpaymentmodes" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<PaymentModesDTO> selectPaymentModes(@RequestParam(value = "term", required = false) String term, Pageable pageable)
			 {
		return policyService.findPaymentModes(term, pageable);
	}

//	@RequestMapping(value = { "inhouseagents" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
//	@ResponseBody
//	public Page<AccountDef> selInhouseAgents(@RequestParam(value = "term", required = false) String term, Pageable pageable)
//			throws IllegalAccessException {
//		return policyService.findInhouseAgents(term, pageable);
//	}

	@RequestMapping(value = { "inhouseagents" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<AccountsDTO> selInhouseAgents(@RequestParam(value = "term", required = false) String term, Pageable pageable)
			throws IllegalAccessException {
		return policyService.findInhouseAgentsDto(term, pageable);
	}

//	@RequestMapping(value = { "introducergents" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
//	@ResponseBody
//	public Page<AccountDef> selIntroducerAgents(@RequestParam(value = "term", required = false) String term, Pageable pageable)
//			throws IllegalAccessException {
//		return policyService.findIntroducerAgents(term, pageable);
//	}

	@RequestMapping(value = { "introducergents" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<AccountsDTO> selIntroducerAgents(@RequestParam(value = "term", required = false) String term, Pageable pageable)
			throws IllegalAccessException {
		return policyService.findIntroducerAgentsDto(term, pageable);
	}

	@RequestMapping(value = { "marketeragents" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<AccountDef> selMarketerAgents(@RequestParam(value = "term", required = false) String term, Pageable pageable)
			throws IllegalAccessException {
		return policyService.findMarketerAgents(term, pageable);
	}

	@RequestMapping(value = { "branchregions" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<OrgRegionsDTO> branchRegions(@RequestParam(value = "term", required = false) String term, Pageable pageable)
			throws IllegalAccessException {
		return policyService.findOrgRegions(term, pageable);
	}

	@RequestMapping(value = { "tasktype" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<MakerCheckDTO> taskTypes(@RequestParam(value = "term", required = false) String term, Pageable pageable)
			throws IllegalAccessException {
		return policyService.findTaskTypes(term, pageable);
	}

	@RequestMapping(value = { "checkedby" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<MakerCheckDTO> AllCheckers(@RequestParam(value = "term", required = false) String term, Pageable pageable)
			throws IllegalAccessException {
		return policyService.findAllCheckers(term, pageable);
	}

//	@RequestMapping(value = { "leadsMan" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
//	@ResponseBody
//	public Page<User> selLeadsMan(@RequestParam(value = "term", required = false) String term, Pageable pageable)
//			throws IllegalAccessException {
//		return policyService.findleadsMan(term, pageable);
//	}

	@RequestMapping(value = { "leadsMan" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<LeadmanDto> selLeadsMan(@RequestParam(value = "term", required = false) String term, Pageable pageable)
			throws IllegalAccessException {
		return policyService.findleadsManDto(term, pageable);
	}

	@RequestMapping(value = { "uwbranches" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<BranchDTO> selectBranches(@RequestParam(value = "term", required = false) String term, Pageable pageable) {
		return policyService.findUserBranches(term, pageable);
	}

	@RequestMapping(value = { "allbranches" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<BranchDTO> selectAllBranches(@RequestParam(value = "term", required = false) String term, Pageable pageable) {
		return policyService.findAllBranches(term, pageable);
	}

	@RequestMapping(value = { "getWetDate" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Date getPolicyWetDate(@RequestParam(value = "wefDate", required = false) Date wef){
		return dateUtils.getWetDate(wef);
	}

	@RequestMapping(value = { "getRiskWetDate" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Date getRiskWetDate(@RequestParam(value = "wefDate", required = false) Date wef, @RequestParam(value = "frequency") String frequency){
		return dateUtils.getRiskWetDate(wef,frequency);
	}

	@RequestMapping(value = { "getMaturityDate" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Date getPolicyWetDate(@RequestParam(value = "wefDate", required = false) Date wef,@RequestParam(value = "polTerm", required = false) Integer polTerm){
		if (polTerm!=null) {
			return dateUtils.getMaturityDate(wef, polTerm);
		} else return null;

	}

	@RequestMapping(value = { "uwsubclasses" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<SubClassDef> selectSubclasses(@RequestParam(value = "term", required = false) String term, @RequestParam("bindCode") Long bindCode,Pageable pageable)
			throws IllegalAccessException {
		return policyService.findBinderSubclasses(term, pageable, bindCode);
	}

	@RequestMapping(value = { "riskCoverTypes" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<CoverTypesDef> selectCoverTypes(@RequestParam(value = "term", required = false) String term, @RequestParam("bindCode") Long bindCode, @RequestParam("subCode") Long subCode,Pageable pageable)
			throws IllegalAccessException {
		System.out.println("bindcode : " + bindCode);
		return policyService.findBinderCoverTypes(term, pageable, bindCode,subCode);
	}

	@RequestMapping(value = { "riskSubCoverTypes" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<CoverTypesDef> selectCoverTypes(@RequestParam(value = "term", required = false) String term, @RequestParam("bindCode") Long bindCode,Pageable pageable)
			throws IllegalAccessException {
		return policyService.findBinderSubCoverTypes(term, pageable, bindCode);
	}

	@RequestMapping(value = { "getBinderPremRates" }, method = {org.springframework.web.bind.annotation.RequestMethod.GET })
	public ResponseEntity<Set<RiskSectionBean>> getBinderPremRates(
			@RequestParam(value = "detId", required = false) Long detId) throws BadRequestException {
		Set<RiskSectionBean> rates = policyService.getBinderPremRates(detId);
		return new ResponseEntity<Set<RiskSectionBean>>(rates, HttpStatus.OK);
	}

	@RequestMapping(value = { "getClientAge" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	public ResponseEntity<Integer> getClientAge(
			@RequestParam(value = "clientId", required = false) Long clientId) throws BadRequestException {
		if(clientId==null)
			throw new BadRequestException("Client cannot be null");
		ClientDef clientDef = clientRepository.findOne(clientId);
        if(clientDef.getDob()==null)
            throw new BadRequestException("Client Date Of Birth Is Required...");
		Integer age = dateUtilities.getAge(clientDef.getDob());
		return new ResponseEntity<Integer>(age, HttpStatus.OK);
	}


	@RequestMapping(value = { "getBinderClientPremRates" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	public ResponseEntity<Set<RiskSectionBean>> getBinderClientPremRates(
			@RequestParam(value = "detId", required = false) Long detId,@RequestParam(value = "age", required = false) Long age) throws BadRequestException {
		Set<RiskSectionBean> rates = policyService.getBinderClientPremRates(detId,age);
		return new ResponseEntity<Set<RiskSectionBean>>(rates, HttpStatus.OK);
	}

	@RequestMapping(value = { "getNewClientPremiumItems" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	public ResponseEntity<List<PremRatesDef>> getNewClientPremiumItems(
			@RequestParam(value = "detId", required = false) Long detId,@RequestParam(value = "riskId", required = false) Long riskId,
			@RequestParam(value = "secName", required = false) String secName,@RequestParam(value = "age", required = false) Long age) throws BadRequestException {
		List<PremRatesDef> rates = policyService.getNewSectPremiumItems(detId,riskId,secName,age);
		return new ResponseEntity<List<PremRatesDef>>(rates, HttpStatus.OK);
	}

	@RequestMapping(value = { "getNewPremiumItems" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	public ResponseEntity<List<PremRatesDef>> getNewPremItems(
			@RequestParam(value = "detId", required = false) Long detId,@RequestParam(value = "riskId", required = false) Long riskId,
			@RequestParam(value = "secName", required = false) String secName) throws BadRequestException {
		List<PremRatesDef> rates = policyService.getNewPremiumItems(detId,riskId,secName);
		return new ResponseEntity<List<PremRatesDef>>(rates, HttpStatus.OK);
	}

	@RequestMapping(value = { "deletePolRecord" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deletePolRecord(@RequestParam(value = "policyId", required = false) Long policyId,HttpServletRequest request) throws BadRequestException {
		endorseService.deletePolicyRecord(policyId,false);
	}

	@RequestMapping(value = { "createPolicy" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	public ResponseEntity<PolicyTrans> createPolicyTrans(@RequestBody PolicyCreateDTO policy,
			HttpServletRequest request) throws BadRequestException {
		Long hashcode = Long.parseLong(String.valueOf(policy.hashCode()));
		PolicyTrans created;
		System.out.println("HASH CODE: " + hashcode);
//		if (makerCheckerRepo.exists(hashcode)) {
			created = policyService.createPolicy(policy, true);
			Long polCode = created.getPolicyId();
			try {
				policyService.linkPendingRisksToPolicy(polCode);
			}
			catch (RuntimeException ex){
				throw new BadRequestException(ex.getMessage());
			}
			request.getSession().setAttribute("policyCode", polCode);
			if ("NB".equalsIgnoreCase(created.getTransType()) || "SP".equalsIgnoreCase(created.getTransType()) || "EX".equalsIgnoreCase(created.getTransType()) || "RN".equalsIgnoreCase(created.getTransType()) || "RE".equalsIgnoreCase( created.getTransType()))
				try {
					premiumService.computePrem(polCode);
				} catch (Exception e) {
				throw new BadRequestException(e.getMessage());
			}
			else if ("EN".equalsIgnoreCase(created.getTransType())) {

			try {
				premiumService.computeEndorsePremium(polCode);
			} catch (Exception e) {
				throw new BadRequestException(e.getMessage());
			}
		}
//		} else {
//			created = policyService.createPolicy(policy, false);
//		}

		return new ResponseEntity<PolicyTrans>(created, HttpStatus.OK);
	}

	@RequestMapping(value = "/claimtrans", method = RequestMethod.POST)
	public String editClaimForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model, HttpServletRequest request) {
		model.addAttribute("clmId", helperForm.getId());
		request.getSession().setAttribute("claimId", helperForm.getId());
		model.addAttribute("claimId",helperForm.getId());
		return "claimdetails";
	}
	@RequestMapping(value = { "getClaimDetails/{clmId}" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	public ResponseEntity<ClaimDetailsDTO> getClmBookings(@PathVariable Long clmId) throws BadRequestException {
		ClaimDetailsDTO booking = claimService.getClaimInformation(clmId);
		return new ResponseEntity<>(booking, HttpStatus.OK);
	}

	@RequestMapping(value = { "issueCertificate" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	public ResponseEntity<String> issueCertificte( HttpServletRequest request) throws BadRequestException {
		Long polCode = (Long) request.getSession().getAttribute("policyCode");
		authService.generateCert(polCode);
		return new ResponseEntity<String>("created", HttpStatus.OK);
	}
	private void adjustPolicyDates(PolicyCreateDTO policydto, String transType) throws BadRequestException {
		if (policydto.getWefDate() == null || policydto.getWetDate() == null) {
			throw new BadRequestException("WEF Date or WET Date cannot be null when adjusting policy dates.");
		}
		ZoneId zone = ZoneId.systemDefault();

		Date originalWef = policydto.getWefDate();
		Date originalWet = policydto.getWetDate();

		LocalDate originalWefLocal = originalWef.toInstant().atZone(zone).toLocalDate();
		LocalDate adjustedWef = HolidayUtils.getAdjustedTransactionDate(originalWefLocal);

		long originalDurationDays = ChronoUnit.DAYS.between(
				originalWef.toInstant().atZone(zone).toLocalDate(),
				originalWet.toInstant().atZone(zone).toLocalDate()
		);

		// Determine adjusted end date
		LocalDate adjustedWet;
		if ("EN".equalsIgnoreCase(transType) || "CN".equalsIgnoreCase(transType)) {
			// Keep original end date for endorsements/cancellations
			adjustedWet = originalWet.toInstant().atZone(zone).toLocalDate();
		} else {
			// For new policies, adjust end date to maintain duration
			adjustedWet = adjustedWef.plusDays(originalDurationDays);
		}

		// Set adjusted dates
		policydto.setWefDate(Date.from(adjustedWef.atStartOfDay(zone).toInstant()));
		policydto.setWetDate(Date.from(adjustedWet.atStartOfDay(zone).toInstant()));

		log.info("Adjusted policy dates from {}-{} to {}-{} (maintained {} day duration, transType={})",
				originalWefLocal, originalWet.toInstant().atZone(zone).toLocalDate(),
				adjustedWef, adjustedWet, originalDurationDays, transType);
	}
private  void validateApaMotorPrivateRiskDetailes(PolicyTrans policytrans) throws BadRequestException {
    if (
            policytrans.getProduct() != null && policytrans.getBinder() != null &&
                    policytrans.getProduct().getProDesc().toUpperCase().contains("MOTOR PRIVATE") &&
                    policytrans.getBinder().getBinName().toUpperCase().contains("APA MOTOR PRIVATE")
    ) {

        Iterable<RiskTrans> riskTrans = riskTransRepo.findAll(QRiskTrans.riskTrans.policy.policyId.eq(policytrans.getPolicyId()));
        for (RiskTrans risk : riskTrans) {
            Map<String, Object> scheduleResponse = policyService.getRiskSchedules(risk.getRiskId());

            // Null or empty check
            if (scheduleResponse == null || scheduleResponse.isEmpty()) {
                System.out.println("No schedule response for risk ID: " + risk.getRiskId());
                continue;
            }

            TableForm tableForm = (TableForm) scheduleResponse.get("tableForm");
            if (tableForm == null || tableForm.getColumnFormList() == null) {
                System.out.println("Missing column info for risk ID: " + risk.getRiskId());
                continue;
            }

            List<ColumnForm> columns = tableForm.getColumnFormList();
            List<Map<String, Object>> data = (List<Map<String, Object>>) scheduleResponse.get("data");

            // Check if data is present
            if (data == null || data.isEmpty()) {
                throw new BadRequestException("Please populate the risk details section");
            }

        }
    }
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
        validateApaMotorPrivateRiskDetailes(created);
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
    validateApaMotorPrivateRiskDetailes(created);
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
        // Update resubmission comment before resubmitting
//        if (policy.getResubmissionComment() != null && !policy.getResubmissionComment().trim().isEmpty()) {
//            makerCheckerRepo.saveCommentForPolicy(created.getPolicyId(), policy.getResubmissionComment());
//        }
		makerCheckerService.resubmitTask(makerChecker.getId(), "ANP", policy, checkerIds);
	}

	return new ResponseEntity<PolicyCreateDTO>(policy, HttpStatus.OK);
}
	@RequestMapping(value = {"/audit-trails/{policyId}"}, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<List<AuditTrailDTO>> getAuditTrails(@PathVariable Long policyId) {
		List<RtsAudit> auditTrails = rtsAuditRepository.findByPolicyId(policyId);
		List<AuditTrailDTO> dtos = auditTrails.stream().map(audit -> {
			AuditTrailDTO dto = new AuditTrailDTO();
			dto.setCheckerName(audit.getCheckerId() != null ? audit.getCheckerId().getUsername() : "N/A");
			dto.setMakerName(audit.getMakerId() != null ? audit.getMakerId().getUsername() : "N/A");
			dto.setAuditTime(audit.getAuditTime());
			dto.setResubmissionComment(audit.getResubmissionComment() != null ? audit.getResubmissionComment() : "N/A");
			dto.setRejectionReasonDesc(audit.getUserRejectedReason() != null ? audit.getUserRejectedReason() :
					(audit.getRejectionReason() != null ? audit.getRejectionReason().getReasonDesc() : "N/A"));
			return dto;
		}).collect(Collectors.toList());
		return ResponseEntity.ok(dtos);
	}


	@RequestMapping(value = { "createLifePolMakeReady" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	public ResponseEntity<PolicyCreateDTO> createLifePolMakeReady(@RequestBody PolicyCreateDTO policy,
															 HttpServletRequest request) throws BadRequestException {
		Long hashcode = Long.parseLong(String.valueOf(policy.hashCode()));
		PolicyTrans created;
		System.out.println("HASH CODE: " + hashcode);
		if(makerCheckerRepo.exists(hashcode)) {
			created = policyService.createLifePolicy(policy, true);
			Long polCode = created.getPolicyId();
			request.getSession().setAttribute("policyCode", polCode);
			System.out.println("Trans Type..." + created.getTransType());
			if ("NB".equalsIgnoreCase(created.getTransType()) || "SP".equalsIgnoreCase(created.getTransType()) || "EX".equalsIgnoreCase(created.getTransType()) || "RN".equalsIgnoreCase(created.getTransType()) || "RE".equalsIgnoreCase( created.getTransType()))
				try {
					premiumService.computeLifePrem(polCode);
				} catch (IOException e) {
					throw new BadRequestException(e.getMessage());
				}
			policyService.makeReady(polCode);
		} else {
			policyService.createLifePolicy(policy, false);
		}
		return new ResponseEntity<PolicyCreateDTO>(policy, HttpStatus.OK);
	}



	@RequestMapping(value = { "createLifePolicy" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	public ResponseEntity<PolicyCreateDTO> createLifePolicyTrans(@RequestBody PolicyCreateDTO policy, HttpServletRequest request) throws BadRequestException {
		System.out.println("from policy controller");
		Long hashcode = Long.parseLong(String.valueOf(policy.hashCode()));
		PolicyTrans created;
		System.out.println("HASH CODE: " + hashcode);
//		if(makerCheckerRepo.exists(hashcode)) {
			System.out.println("Before policy creation" +policy);
			created = policyService.createLifePolicy(policy, true);
			System.out.println("After policy creation" +policy);
			Long polCode = created.getPolicyId();
			request.getSession().setAttribute("policyCode", polCode);
			System.out.println(created.getTransType());
			if ("NB".equalsIgnoreCase(created.getTransType()) || "SP".equalsIgnoreCase(created.getTransType()) || "EX".equalsIgnoreCase(created.getTransType()) || "RN".equalsIgnoreCase(created.getTransType()) || "RE".equalsIgnoreCase( created.getTransType()))
				try {
					System.out.println(created.getTransType());
					System.out.println("Computing life policy....");
					premiumService.computeLifePrem(polCode);
				} catch (IOException e) {
					e.printStackTrace();
					throw new BadRequestException(e.getMessage());
				}
//		} else {
//			created = policyService.createLifePolicy(policy, false);
//		}
		return new ResponseEntity<PolicyCreateDTO>(policy, HttpStatus.OK);
	}

	@RequestMapping(value = { "binderPolTerms/{binCode}" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public List<BinderPolTerms> selectageBracket(@PathVariable long binCode)
			throws BadRequestException {
		return  lifeService.getPolTerms(binCode);
	}

	@RequestMapping(value = { "createRisk" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	public ResponseEntity<String> createRiskTrans(@RequestBody CreateRiskDTO risk,
			HttpServletRequest request) throws BadRequestException {
		System.out.println(new Gson().toJson(risk));
		Long polCode = (Long) request.getSession().getAttribute("policyCode");
		PolicyTrans policyTrans = policyTransRepo.findOne(QPolicyTrans.policyTrans.policyId.eq(polCode));
		if(policyTrans.getBusinessType().equalsIgnoreCase("L")){
			policyService.createLifeRisk(risk,request);
		}else
		policyService.createRisk(risk,request);
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

	@RequestMapping(value = { "policyRisks/{policyCode}/{polBindCode}" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<RiskTransDTO> getPolicyRisks(@DataTable DataTablesRequest pageable,@PathVariable Long policyCode,@PathVariable Long polBindCode)
			throws IllegalAccessException {
		return policyService.findRiskTransactions(pageable,policyCode,polBindCode);
	}



	@RequestMapping(value = { "risksSections/{riskId}" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<SectionTrans> getRiskSections(@DataTable DataTablesRequest pageable,@PathVariable Long riskId)
			throws IllegalAccessException {
		return policyService.findRiskSections(pageable,riskId);
	}


	@RequestMapping(value = { "risksIntParties/{riskId}" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<InterestedPartiesDTO> getRiskIntParties(@DataTable DataTablesRequest pageable,@PathVariable Long riskId)
			throws IllegalAccessException {
		return policyService.findRiskInterestedParties(pageable,riskId);
	}


		@RequestMapping(value = { "getPolicyDetails" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	public ResponseEntity<PolicyTrans> getPolicyDetails(HttpServletRequest request) throws BadRequestException {
		Long polCode = (Long) request.getSession().getAttribute("policyCode");
		PolicyTrans created = policyService.getPolicyDetails(polCode);
		return new ResponseEntity<PolicyTrans>(created, HttpStatus.OK);
	}


	@RequestMapping(value = { "riskselectsections" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<SectionBean> selectRiskSections(@RequestParam(value = "term", required = false) String term, @RequestParam("detId") Long detId,Pageable pageable)
			throws IllegalAccessException {
		return policyService.findPremSections(term, pageable, detId);
	}

	@RequestMapping(value = { "saveRiskSections" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	public void createPremRates(SectionTransDTO section,HttpServletRequest request) throws IllegalAccessException, IOException, BadRequestException {
		policyService.createRiskSection(section);
		Long polCode = (Long) request.getSession().getAttribute("policyCode");
		PolicyTrans policy = policyService.getPolicyDetails(polCode);
		String polBusinessType = policy.getProduct().getProGroup().getPrgType();
		if (!polBusinessType.equalsIgnoreCase("L")) {
			if ("NB".equalsIgnoreCase(policy.getTransType()) || "SP".equalsIgnoreCase(policy.getTransType()) || "EX".equalsIgnoreCase(policy.getTransType()) || "RN".equalsIgnoreCase(policy.getTransType()) || "RE".equalsIgnoreCase(policy.getTransType()) || "BU".equalsIgnoreCase(policy.getTransType()))
				premiumService.computePrem(polCode);
			else if ("EN".equalsIgnoreCase(policy.getTransType())) {
				premiumService.computeEndorsePremium(polCode);
			}
		}
		else{
			System.out.println("Compute life prem...");
			premiumService.computeLifePrem(polCode);
		}
	}

	 @RequestMapping(value = { "deleteRiskSection/{sectCode}" }, method = {
				org.springframework.web.bind.annotation.RequestMethod.GET })
		@ResponseStatus(HttpStatus.NO_CONTENT)
	 @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
		public void deleteRiskSection(@PathVariable Long sectCode,HttpServletRequest request) throws BadRequestException {
		 policyService.deleteRiskSection(sectCode,request);
		}

	 @RequestMapping(value = { "policyClauses" }, method = { RequestMethod.GET })
		@ResponseBody
		public DataTablesResult<PolicyClauses> getPolicyClauses(@DataTable DataTablesRequest pageable,HttpServletRequest request)
				throws IllegalAccessException {
		     Long policyCode = (Long) request.getSession().getAttribute("policyCode");
			return policyService.findPolicyClauses(pageable,policyCode);
		}

	 @RequestMapping(value = { "policyTaxes" }, method = { RequestMethod.GET })
		@ResponseBody
		public DataTablesResult<PolicyTaxes> getPolicyTaxes(@DataTable DataTablesRequest pageable,HttpServletRequest request)
				throws IllegalAccessException {
		 Long policyCode = (Long) request.getSession().getAttribute("policyCode");
			return policyService.findPolicyTaxes(pageable,policyCode);
		}

	 @RequestMapping(value = { "deleteRisk/{riskId}" }, method = {
				org.springframework.web.bind.annotation.RequestMethod.GET })
		@ResponseStatus(HttpStatus.NO_CONTENT)
		public void deleteRisk(@PathVariable Long riskId,HttpServletRequest request) throws BadRequestException {
		 policyService.deleteRisk(riskId,request);
		}

	 @RequestMapping(value = { "makePolicyReady" }, method = {RequestMethod.POST })
	 @ResponseStatus(HttpStatus.CREATED)
		public ResponseForm makePolicyReady(HttpServletRequest request) throws BadRequestException {
		 Long polCode = (Long) request.getSession().getAttribute("policyCode");
		 PolicyTrans policy = policyService.getPolicyDetails(polCode);
		 if("NB".equalsIgnoreCase( policy.getTransType()) || "SP".equalsIgnoreCase( policy.getTransType())|| "EX".equalsIgnoreCase( policy.getTransType())|| "RN".equalsIgnoreCase( policy.getTransType()) || "RE".equalsIgnoreCase( policy.getTransType()))
			 try {
				 premiumService.computePrem(polCode);
			 } catch (IOException e) {
				 throw new BadRequestException(e.getMessage());
			 }
			 else if("EN".equalsIgnoreCase( policy.getTransType())){
			 try {
				 premiumService.computeEndorsePremium(polCode);
			 } catch (IOException e) {
				 throw new BadRequestException(e.getMessage());
			 }
		 }
			 String response =  policyService.makeReady(polCode);
			 return new ResponseForm(response);

		}

	 @RequestMapping(value = { "undoMakeReady" }, method = {
				org.springframework.web.bind.annotation.RequestMethod.GET })
		@ResponseStatus(HttpStatus.NO_CONTENT)
		public void undoMakePolicyReady(
				HttpServletRequest request,
				@RequestParam Long reasonId,
				@RequestParam(required = false) String reason) throws BadRequestException {
		 Long polCode = (Long) request.getSession().getAttribute("policyCode");
		 policyService.undoMakeReady(polCode, reasonId, reason);

		}

	@RequestMapping(value = "/rejection-reasons", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getPolicyRejectionReasons() {
		try {
			List<RejectedReasons> reasons = setupsService.findActiveRejectionReasons();
			Map<String, Object> response = new HashMap<>();
			response.put("data", reasons);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("message", "Failed to fetch rejection reasons");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}
	 @RequestMapping(value = { "authorizePolicy" }, method = {
				org.springframework.web.bind.annotation.RequestMethod.GET })
		@ResponseStatus(HttpStatus.NO_CONTENT)
		public void authorizePolicy(HttpServletRequest request,@RequestParam(value = "refundAmt", required = false) BigDecimal refundAmt) throws BadRequestException {
		 Long polCode = (Long) request.getSession().getAttribute("policyCode");
		 authService.authorizePolicy(polCode,refundAmt, false);

		}

	@RequestMapping(value = { "submitPolicyCancellation" }, method = RequestMethod.POST)
	public ResponseEntity<?> submitPolicyCancellation(@RequestBody PolicyCancellationDTO dto,
														 HttpServletRequest request) throws BadRequestException {
		try {
			Long polCode = (Long) request.getSession().getAttribute("policyCode");
			dto.setPolicyId(polCode);
			authService.submitPolicyCancellation(dto, false);
			return ResponseEntity.noContent().build();
		} catch (BadRequestException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}


	@RequestMapping(value = { "authorizeLifePolicy" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void authorizeLifePolicy(HttpServletRequest request,@RequestParam(value = "refundAmt", required = false) BigDecimal refundAmt) throws BadRequestException {
		Long polCode = (Long) request.getSession().getAttribute("policyCode");
		authService.authorizeLifePolicy(polCode);

	}
	@RequestMapping(value = { "saveAuthorizationComment" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void saveAuthorizationComment(HttpServletRequest request,@RequestParam(value = "authComments", required = false) String authComments) throws BadRequestException {
		Long polCode = (Long) request.getSession().getAttribute("policyCode");
		authService.saveAuthorizationComment(polCode,authComments);

	}

	@RequestMapping(value = { "saveRiskDocComment" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void saveRiskDocComment(HttpServletRequest request,
								   @RequestParam(value = "rdId", required = true) Long rdId,
								   @RequestParam(value = "comments", required = false) String comments)
			throws BadRequestException {
		authService.saveRiskDocComment(rdId, comments);
	}
	@RequestMapping(value = "verifyRiskDoc", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void verifyRiskDoc(@RequestParam("rdId") Long rdId) throws BadRequestException {
		authService.verifyRiskDoc(rdId);
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


	@RequestMapping(value = { "allocationCommission/{AllocId}" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<ReceiptAllocationCommissions> getAllocCommission(@DataTable DataTablesRequest pageable, @PathVariable Long AllocId)
			throws IllegalAccessException {
		return lifeService.findAllocationComm(pageable,AllocId);
	}


	@RequestMapping(value = { "dispatchDocs" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void dispatchDocs(HttpServletRequest request) throws BadRequestException {
		Long polCode = (Long) request.getSession().getAttribute("policyCode");
		policyService.dispatchDocuments(polCode);

	}

	 @RequestMapping(value = { "createPremiumItems" }, method = {
				org.springframework.web.bind.annotation.RequestMethod.POST })
		@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
		public ResponseEntity<String> createPremiumItems(@RequestBody RiskBean sections,
				HttpServletRequest request) throws BadRequestException {
			 policyService.createRiskSections(sections,request);
			return new ResponseEntity<String>("Created", HttpStatus.OK);
		}

	 @RequestMapping(value = { "getNewClauses" }, method = {
				org.springframework.web.bind.annotation.RequestMethod.GET })
		public ResponseEntity<List<ClauseDTO>> getNewClauses(HttpServletRequest request) throws BadRequestException {
		 Long polCode = (Long) request.getSession().getAttribute("policyCode");
		 List<ClauseDTO> clauses = policyService.getNewClauses(polCode);
			return new ResponseEntity<>(clauses, HttpStatus.OK);
		}

	@RequestMapping(value = { "getNewTaxes" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	public ResponseEntity<Set<PolicyTaxes>> getNewTaxes(HttpServletRequest request) throws BadRequestException {
		Long polCode = (Long) request.getSession().getAttribute("policyCode");
		Set<PolicyTaxes> taxes = policyService.getNewTaxes(polCode);
		return new ResponseEntity<Set<PolicyTaxes>>(taxes, HttpStatus.OK);
	}

	    @RequestMapping(value = { "deletePolClause/{clauseId}" }, method = {
				org.springframework.web.bind.annotation.RequestMethod.GET })
		@ResponseStatus(HttpStatus.NO_CONTENT)
		public void deletePolClause(@PathVariable Long clauseId) throws BadRequestException {
		 policyService.deletePolicyClause(clauseId);
		}

	 @RequestMapping(value = { "deletePolTaxes/{taxCode}" }, method = {
				org.springframework.web.bind.annotation.RequestMethod.GET })
		@ResponseStatus(HttpStatus.NO_CONTENT)
	   @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
		public void deletePolTaxes(@PathVariable Long taxCode,HttpServletRequest request) throws BadRequestException {
			 policyService.deletePolicyTax(taxCode);
			 Long polCode = (Long) request.getSession().getAttribute("policyCode");
			 PolicyTrans policy = policyService.getPolicyDetails(polCode);
			 if("NB".equalsIgnoreCase( policy.getTransType()) || "SP".equalsIgnoreCase( policy.getTransType())|| "EX".equalsIgnoreCase( policy.getTransType())|| "RN".equalsIgnoreCase( policy.getTransType())|| "RE".equalsIgnoreCase( policy.getTransType()))
				 try {
					 System.out.println("Policy Code.."+polCode);
					 premiumService.computePrem(polCode);
				 } catch (IOException e) {
					 throw new BadRequestException(e.getMessage());
				 }
			 else if("EN".equalsIgnoreCase( policy.getTransType())){
				 try {
					 premiumService.computeEndorsePremium(polCode);
				 } catch (IOException e) {
					 throw new BadRequestException(e.getMessage());
				 }
			 }
		}

	 @RequestMapping(value = { "createNewClause" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	public ResponseEntity<String> createNewClause(@RequestBody PolicyClausesBean clause,
												  HttpServletRequest request) throws BadRequestException {
		Long polCode = (Long) request.getSession().getAttribute("policyCode");
		clause.setPolId(polCode);
		policyService.createClause(clause);
		return new ResponseEntity<String>("Created", HttpStatus.OK);
	}

	@RequestMapping(value = { "createNewTax" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	public ResponseEntity<String> createNewTax(@RequestBody PolicyTaxBean tax,
												  HttpServletRequest request) throws BadRequestException {
		Long polCode = (Long) request.getSession().getAttribute("policyCode");
		tax.setPolId(polCode);
		policyService.createTaxes(tax);
		PolicyTrans policy = policyService.getPolicyDetails(polCode);
		if("NB".equalsIgnoreCase( policy.getTransType()) || "SP".equalsIgnoreCase( policy.getTransType())|| "EX".equalsIgnoreCase( policy.getTransType())|| "RN".equalsIgnoreCase( policy.getTransType()) || "RE".equalsIgnoreCase( policy.getTransType()) )
			try {
				premiumService.computePrem(polCode);
			} catch (IOException e) {
				throw new BadRequestException(e.getMessage());
			}
		else if("EN".equalsIgnoreCase( policy.getTransType())){
			try {
				premiumService.computeEndorsePremium(polCode);
			} catch (IOException e) {
				throw new BadRequestException(e.getMessage());
			}
		}
		return new ResponseEntity<String>("Created", HttpStatus.OK);
	}


	@RequestMapping(value = { "createNewIntParties" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	public ResponseEntity<String> createNewIntParties(@RequestBody RiskIntPartiesBean partiesBean,
											   HttpServletRequest request) throws BadRequestException {
		policyService.createIntParties(partiesBean);
		return new ResponseEntity<String>("Created", HttpStatus.OK);
	}

	 @RequestMapping(value = { "createPolicyClause" }, method = {
				org.springframework.web.bind.annotation.RequestMethod.POST })
		@ResponseBody
		public void createPolicyClause(PolicyClauses clause) throws IllegalAccessException, IOException, BadRequestException {
		 policyService.createPolicyClause(clause);
		}

	 @RequestMapping(value = { "createPolicyTax" }, method = {
				org.springframework.web.bind.annotation.RequestMethod.POST })
		@ResponseBody
		@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
		public void createPolicyTax(PolicyTaxes tax,HttpServletRequest request) throws IllegalAccessException, IOException, BadRequestException {
		 policyService.createPolicyTaxes(tax);
		 Long polCode = (Long) request.getSession().getAttribute("policyCode");
		 PolicyTrans policy = policyService.getPolicyDetails(polCode);
		 if("NB".equalsIgnoreCase( policy.getTransType()) || "SP".equalsIgnoreCase( policy.getTransType())|| "EX".equalsIgnoreCase( policy.getTransType())|| "RN".equalsIgnoreCase( policy.getTransType()) || "RE".equalsIgnoreCase( policy.getTransType()) )
			 premiumService.computePrem(polCode);
			 else if("EN".equalsIgnoreCase( policy.getTransType())){
				 premiumService.computeEndorsePremium(polCode);
			 }
		}

	 @RequestMapping(value = "rpt_debit_note", method = RequestMethod.GET)
		public ModelAndView invoiceRpt(ModelMap modelMap, HttpServletRequest request, ModelAndView modelAndView)
				throws BadRequestException, IOException {
		    Long polCode = (Long) request.getSession().getAttribute("policyCode");
		 OrganizationDTO organization = orgService.getOrganizationLogoDetails();
		 InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
		    BufferedImage image = ImageIO.read(in);
		    modelMap.put("logo", image );
			modelMap.put("datasource", datasource);
			modelMap.put("format", "pdf");
			modelMap.put("polId", polCode);
			modelAndView = new ModelAndView("rpt_debit_note", modelMap);
			return modelAndView;
		}

	@RequestMapping(value = "rpt_renewal_notice_non_motor", method = RequestMethod.GET)
	public ModelAndView renewalNoticeNonMotor(ModelMap modelMap, HttpServletRequest request, ModelAndView modelAndView)
			throws BadRequestException, IOException {
		Long polCode = (Long) request.getSession().getAttribute("policyCode");
		OrganizationDTO organization = orgService.getOrganizationLogoDetails();
		InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
		BufferedImage image = ImageIO.read(in);
		modelMap.put("logo", image );
		modelMap.put("datasource", datasource);
		modelMap.put("format", "pdf");
		modelMap.put("polId", polCode);
		modelAndView = new ModelAndView("rpt_renewal_notice_non_motor", modelMap);
		return modelAndView;
	}

	@RequestMapping(value = "rpt_renewal_notice_motor", method = RequestMethod.GET)
	public ModelAndView renewalNoticeMotor(ModelMap modelMap, HttpServletRequest request, ModelAndView modelAndView)
			throws BadRequestException, IOException {
		Long polCode = (Long) request.getSession().getAttribute("policyCode");
		OrganizationDTO organization = orgService.getOrganizationLogoDetails();
		InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
		BufferedImage image = ImageIO.read(in);
		modelMap.put("logo", image );
		modelMap.put("datasource", datasource);
		modelMap.put("format", "pdf");
		modelMap.put("polId", polCode);

		modelAndView = new ModelAndView("rpt_renewal_notice_motor", modelMap);
		return modelAndView;
	}

	@RequestMapping(value = "rpt_valuation_rpt", method = RequestMethod.GET)
	public ModelAndView rpt_valuation_rpt(ModelMap modelMap, HttpServletRequest request, ModelAndView modelAndView)
			throws BadRequestException, IOException {
		Long polCode = (Long) request.getSession().getAttribute("policyCode");
		PolicyTrans policyTrans = policyService.getPolicyDetails(polCode);
		File file = new File(orgService.getOrganizationLogoDetails().getOrgLogo());
		InputStream in = new ByteArrayInputStream(Files.readAllBytes(file.toPath()));
		BufferedImage image = ImageIO.read(in);
		modelMap.put("logo", image );
		modelMap.put("datasource", datasource);
		modelMap.put("format", "pdf");
		modelMap.put("polId", polCode);
		modelAndView = new ModelAndView("rpt_valuation_rpt", modelMap);
		return modelAndView;
	}

	 @RequestMapping(value = "rpt_prem_working", method = RequestMethod.GET)
		public ModelAndView premWorking(ModelMap modelMap, HttpServletRequest request, ModelAndView modelAndView)
				throws BadRequestException, IOException {
		    Long polCode = (Long) request.getSession().getAttribute("policyCode");
		 OrganizationDTO organization = orgService.getOrganizationLogoDetails();
		 InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
		    BufferedImage image = ImageIO.read(in);
		    modelMap.put("logo", image );
			modelMap.put("datasource", datasource);
			modelMap.put("format", "pdf");
			modelMap.put("polId", polCode);
			modelAndView = new ModelAndView("rpt_prem_working", modelMap);
			return modelAndView;
		}

	@RequestMapping(value = "rpt_risk_note", method = RequestMethod.GET)
	public ModelAndView riskNote(ModelMap modelMap, HttpServletRequest request, ModelAndView modelAndView)
			throws BadRequestException, IOException {
		Long polCode = (Long) request.getSession().getAttribute("policyCode");
		System.out.println("Pol Code..."+polCode);
		OrganizationDTO organization = orgService.getOrganizationLogoDetails();
		InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
		BufferedImage image = ImageIO.read(in);
		modelMap.put("logo", image );
		modelMap.put("datasource", datasource);
		modelMap.put("format", "pdf");
		modelMap.put("polId", polCode);
		modelAndView = new ModelAndView("rpt_risk_note", modelMap);
		return modelAndView;
	}

	@RequestMapping(value = "rpt_risk_note_life_install", method = RequestMethod.GET)
	public ModelAndView liferiskNote(ModelMap modelMap, HttpServletRequest request, ModelAndView modelAndView)
			throws BadRequestException, IOException {
		Long polCode = (Long) request.getSession().getAttribute("policyCode");
		System.out.println("Pol Code..."+polCode);
		OrganizationDTO organization = orgService.getOrganizationLogoDetails();
		InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
		BufferedImage image = ImageIO.read(in);
		modelMap.put("logo", image );
		modelMap.put("datasource", datasource);
		modelMap.put("format", "pdf");
		modelMap.put("polId", polCode);
		modelAndView = new ModelAndView("rpt_risk_note_life_install", modelMap);
		return modelAndView;
	}


	@RequestMapping(value = "{reportTemplate}", method = RequestMethod.GET)
	public ModelAndView clientCustomQuote(@PathVariable("reportTemplate") String reportTemplate,ModelMap modelMap, HttpServletRequest request, ModelAndView modelAndView)
			throws BadRequestException, IOException {
		Long polCode = (Long) request.getSession().getAttribute("policyCode");
		System.out.println("Pol Code..."+polCode);
		OrganizationDTO organization = orgService.getOrganizationLogoDetails();
		InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
		BufferedImage image = ImageIO.read(in);
		modelMap.put("logo", image );
		modelMap.put("datasource", datasource);
		modelMap.put("format", "pdf");
		modelMap.put("polId", polCode);
		modelAndView = new ModelAndView(reportTemplate, modelMap);
		return modelAndView;
	}

	@RequestMapping(value = "rpt_{productName}_client_quote", method = RequestMethod.GET)
	public ModelAndView clientQuote(@PathVariable("productName") String productName, ModelMap modelMap, HttpServletRequest request, ModelAndView modelAndView)
			throws BadRequestException, IOException {
		Long polCode = (Long) request.getSession().getAttribute("policyCode");
		System.out.println("Pol Code: " + polCode);
		OrganizationDTO organization = orgService.getOrganizationLogoDetails();
		InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
		BufferedImage image = ImageIO.read(in);
		modelMap.put("logo", image);
		modelMap.put("datasource", datasource);
		modelMap.put("format", "pdf");
		modelMap.put("polId", polCode);
		modelAndView = new ModelAndView("rpt_" + productName + "_client_quote", modelMap);
		return modelAndView;
	}


	@RequestMapping(value = "rpt_endorse", method = RequestMethod.GET)
		public ModelAndView endorsementReport(ModelMap modelMap, HttpServletRequest request, ModelAndView modelAndView)
				throws BadRequestException, IOException {
		    Long polCode = (Long) request.getSession().getAttribute("policyCode");
		 OrganizationDTO organization = orgService.getOrganizationLogoDetails();
		 InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
		    BufferedImage image = ImageIO.read(in);
		    modelMap.put("logo", image );
			modelMap.put("datasource", datasource);
			modelMap.put("format", "pdf");
			modelMap.put("polId", polCode);
			modelAndView = new ModelAndView("rpt_endorse", modelMap);
			return modelAndView;
		}




	 @RequestMapping(value = { "polactiverisks" }, method = { RequestMethod.GET })
		@ResponseBody
		public DataTablesResult<RiskTransDTO> getActiveRisks(@DataTable DataTablesRequest pageable,
				@RequestParam(value = "insuredId", required = false) Long insuredId,
				@RequestParam(value = "policyCode", required = false) Long policyCode)
				throws IllegalAccessException {
			return policyService.findActiveRiskTransactions(pageable, policyCode,insuredId);
		}


	 @RequestMapping(value = { "endorseRisk" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	 @Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	 @ResponseStatus(HttpStatus.NO_CONTENT)
		public void endorseRisk(@RequestParam(value = "activeRiskCode", required = false) Long activeRiskCode,@RequestParam(value = "endorseType", required = false) String endorseType,  HttpServletRequest request) throws BadRequestException{
		 Long polCode = (Long) request.getSession().getAttribute("policyCode");

         Long countRisks = endorseService.findRiskExpiredSections(polCode);

         if(countRisks>0 && !"S".equalsIgnoreCase(endorseType)){
             throw new BadRequestException("The Risk Being Endorsed Has Expired Sections That need to Be Reinstated. Reinstate the sections first before proceeding.");
         } if(countRisks<1 && "S".equalsIgnoreCase(endorseType) ){
			 throw new BadRequestException("The Risk Being Reinstated Has No Expired Sections... ");
		 }
		 endorseService.endorseRisk(activeRiskCode,endorseType,null);
		 try {
			 premiumService.computeEndorsePremium(polCode);
		 } catch (IOException e) {
			 throw new BadRequestException(e.getMessage());
		 }
	 }


	 @RequestMapping(value = { "getPolicyRemarks" }, method = {
				org.springframework.web.bind.annotation.RequestMethod.GET })
		public ResponseEntity<PolicyRemarks> getPolicyRemarks(HttpServletRequest request) throws BadRequestException {
		 Long polCode = (Long) request.getSession().getAttribute("policyCode");
		PolicyRemarks remarks = policyService.getPolicyRemarks(polCode);
			return new ResponseEntity<PolicyRemarks>(remarks, HttpStatus.OK);
		}
	@RequestMapping(value = { "getRefundComments" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	public ResponseEntity<Map<String, Object>> getRefundComments(HttpServletRequest request) throws BadRequestException {
		Long polCode = (Long) request.getSession().getAttribute("policyCode");
		String refundComments = policyService.getRefundComments(polCode);

		Map<String, Object> response = new HashMap<>();
		response.put("refundComments", refundComments);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}


	 @RequestMapping(value = { "getNewPolicyRemarks" }, method = { RequestMethod.GET })
		@ResponseBody
		public DataTablesResult<EndorsementRemarks> getNewPolicyRemarks(@DataTable DataTablesRequest pageable,@RequestParam(value = "policyCode", required = false) Long policyCode)
				throws IllegalAccessException {
			return policyService.findEndorsementRemarks(pageable, policyCode);
		}


	 @RequestMapping(value = { "createPolicyRemarks" }, method = {
				org.springframework.web.bind.annotation.RequestMethod.POST })
		@ResponseStatus(HttpStatus.CREATED)
		public void createPolicyRemarks(PolicyRemarks remarks) throws IllegalAccessException, BadRequestException, IOException {

			policyService.saveEndorsementRemarks(remarks);
		}


	 @RequestMapping(value = { "enquiryPolicies" }, method = { RequestMethod.GET })
		@ResponseBody
		public DataTablesResult<EndorsementsDTO> getEnquiryPolicies(@DataTable DataTablesRequest pageable,
																	@RequestParam(value = "searchTerm", required = false) String searchTerm) {
			return policyService.findEnquiryPolicies(pageable, searchTerm);
		}

	@RequestMapping(value = { "enquiryActivePolicies" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<PolicyTrans> getEnquiryActivePolicies(@DataTable DataTablesRequest pageable,
															@RequestParam(value = "policyNo", required = false) String policyNo,
															@RequestParam(value = "endorseNumber", required = false) String endorseNumber,
															@RequestParam(value = "refno", required = false) String refno,
															@RequestParam(value = "clientCode", required = false) Long clientCode,
															@RequestParam(value = "agentCode", required = false) Long agentCode,
															@RequestParam(value = "prodCode", required = false) Long prodCode) throws IllegalAccessException {
		return policyService.findActiveEnquiryPolicies(pageable, refno, clientCode, policyNo, endorseNumber,agentCode,prodCode);
	}

	@RequestMapping(value = { "enquiryMedPolicies" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<PolicyTrans> getEnquiryMedPolicies(@DataTable DataTablesRequest pageable,
															@RequestParam(value = "policyNo", required = false) String policyNo,
															@RequestParam(value = "endorseNumber", required = false) String endorseNumber,
															@RequestParam(value = "refno", required = false) String refno,
															@RequestParam(value = "clientCode", required = false) Long clientCode,
															@RequestParam(value = "agentCode", required = false) Long agentCode,
															@RequestParam(value = "prodCode", required = false) Long prodCode) throws IllegalAccessException {
		return policyService.findEnquiryMedPolicies(pageable, refno, clientCode, policyNo, endorseNumber,agentCode,prodCode);
	}

	@RequestMapping(value = { "enquiryActiveMedPolicies" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<LapsePoliciesDTO> getEnquiryActiveMedPolicies(@DataTable DataTablesRequest pageable,
															   @RequestParam(value = "drNumber", required = false) String drNumber,
															   @RequestParam(value = "policyNo", required = false) String policyNo,
															   @RequestParam(value = "clientCode", required = false) Long clientCode,
															   @RequestParam(value = "agentCode", required = false) Long agentCode,
															   @RequestParam(value = "prodCode", required = false) Long prodCode,
															   @RequestParam(value = "riskShtDesc", required = false) String riskShtDesc) throws IllegalAccessException {
		return policyService.findEnquiryActiveorLapsedMedPolicies(pageable, clientCode, policyNo, agentCode,prodCode,riskShtDesc,drNumber);
	}

	@RequestMapping(value = { "pendingPolicies" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<PolicyTrans> getPendingPolicies(@DataTable DataTablesRequest pageable,
															@RequestParam(value = "policyNo", required = false) String policyNo,
															@RequestParam(value = "endorseNumber", required = false) String endorseNumber,
															@RequestParam(value = "refno", required = false) String refno,
															@RequestParam(value = "clientCode", required = false) Long clientCode,
															@RequestParam(value = "agentCode", required = false) Long agentCode,
															@RequestParam(value = "prodCode", required = false) Long prodCode) throws IllegalAccessException {
		return policyService.findPendingPolicies(pageable, refno, clientCode, policyNo, endorseNumber,agentCode,prodCode);
	}

	@RequestMapping(value = { "riskCerts/{riskCode}" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<PolicyCerts> getRiskCerts(@DataTable DataTablesRequest pageable, @PathVariable Long riskCode)
			throws IllegalAccessException {
		return certService.findPrintQueue(pageable,riskCode);
	}

	@RequestMapping(value = { "riskBeneficiaries/{riskCode}" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<WIBABeneficiariesDTO> getRiskBeneficiries(@DataTable DataTablesRequest pageable, @PathVariable Long riskCode)
			throws IllegalAccessException {
		return certService.findBeneficiries(pageable,riskCode);
	}

	@RequestMapping(value = { "vehicleDetails/{riskCode}" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<VehicleDetails> getVehicleDetails(@DataTable DataTablesRequest pageable, @PathVariable Long riskCode)
			throws IllegalAccessException {
		return policyService.findVehicleDetails(pageable,riskCode);
	}


	@RequestMapping(value = { "selectBranchCerts" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<BranchCerts> selectBranchCerts(@RequestParam(value = "term", required = false) String term, @RequestParam("riskId") Long riskId, Pageable pageable)
			throws IllegalAccessException {
		return certService.findActiveLots(term, pageable, riskId);
	}

	@RequestMapping(value = { "selectSubclassCertTypes" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Page<CertTypesDTO> selectSubclassCertTypes(@RequestParam(value = "term", required = false) String term, @RequestParam("riskId") Long riskId, Pageable pageable)
			throws IllegalAccessException {

		return certService.findSubclassCertTypes(term, pageable, riskId);
	}

	@RequestMapping(value = { "saveRiskCertificate" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseStatus(HttpStatus.CREATED)
	public void saveRiskCertificate(RiskCertForm certForm) throws BadRequestException {
		certService.createRiskCert(certForm);
	}

	@RequestMapping(value = { "saveRiskBeneficiary" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseStatus(HttpStatus.CREATED)
	public void saveRiskBeneficiary(WIBABeneficiariesDTO beneficiariesDTO) throws BadRequestException {
		certService.defineWibaBeneficiary(beneficiariesDTO);
	}


	@RequestMapping(value = { "updateRiskCertificate" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseStatus(HttpStatus.CREATED)
	public void updateRiskCertificate(EditRiskCertForm certForm) throws IllegalAccessException, BadRequestException, IOException {
		certService.updateRiskCertificate(certForm);
	}

	@RequestMapping(value = { "deleteRiskCert/{certId}" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteRiskCert(@PathVariable Long certId) throws BadRequestException {
		certService.deleteRiskCertificate(certId);
	}

	@RequestMapping(value = { "deleteRiskBeneficiary/{benId}" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteRiskBeneficiary(@PathVariable Long benId) throws BadRequestException {
		certService.deleteRiskBeneficiary(benId);
	}

	@RequestMapping(value = { "importRiskBeneficiaries" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseStatus(HttpStatus.CREATED)
	public void importRiskBeneficiaries(RiskBeneficiariesImport beneficiariesImport) throws BadRequestException, IOException {

		if ((beneficiariesImport.getFile() != null) && (!beneficiariesImport.getFile().isEmpty())) {
			if (beneficiariesImport.getFile().getSize() != 0) {
				certService.importRiskBeneficiaries(beneficiariesImport);
			}
		}
	}
	@RequestMapping(value = { "allocateRiskCert/{certId}" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void allocateRiskCert(@PathVariable Long certId) throws BadRequestException {
		List<Long> certs = new ArrayList<>();
		PrintCertificateQueue queue = printQueueRepo.findOne(QPrintCertificateQueue.printCertificateQueue.policyCerts.pcId.eq(certId));
		Predicate pred = QBranchCerts.branchCerts.branch.obId.eq(queue.getPolicyCerts().getRisk().getPolicy().getBranch().getObId())
				.and(QBranchCerts.branchCerts.user.id.eq(userUtils.getCurrentUser().getId()))
				.and(QBranchCerts.branchCerts.certLots.underwriter.acctId.eq(queue.getPolicyCerts().getRisk().getPolicy().getAgent().getAcctId()))
				.and(QBranchCerts.branchCerts.certLots.subclass.subId.eq(queue.getPolicyCerts().getRisk().getSubclass().getSubId()))
				.and(QBranchCerts.branchCerts.currentLot.equalsIgnoreCase("Y"));

		BranchCerts  branchCerts = branchCertsRepo.findOne(pred);
		certs.add(queue.getCqId());
		PrintCertBean certBean = new PrintCertBean();
		certBean.setCerts(certs);
		certBean.setBranchCert(branchCerts.getBrnCertId());
		certService.allocateCerts(certBean);
	}

	@RequestMapping(value = { "deallocateRiskCert/{certId}" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deallocateRiskCert(@PathVariable Long certId) throws BadRequestException {
		List<Long> certs = new ArrayList<>();
		PrintCertificateQueue queue = printQueueRepo.findOne(QPrintCertificateQueue.printCertificateQueue.policyCerts.pcId.eq(certId));
		certs.add(queue.getCqId());
		PrintCertBean certBean = new PrintCertBean();
		certBean.setCerts(certs);
		certBean.setBranchCert(queue.getPolicyCerts().getCert().getBrnCertId());
		certService.deallocateCerts(certBean);
	}


	@RequestMapping(value = {"allocatePolCerts"}, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST})
	public ResponseEntity<String> allocateCerts(@RequestBody PrintCertBean certBean) throws  BadRequestException {
		certService.allocatePolCerts(certBean);
		return new ResponseEntity<String>("OK",HttpStatus.OK);
	}

	@RequestMapping(value = "printPolCertificate", method = RequestMethod.POST)
	public ResponseEntity<String> printReport(@RequestBody PrintCerts printCerts)
			throws BadRequestException, IOException {
		certService.createBatchCerts(printCerts.getCertCodes());
		return new ResponseEntity<String>("OK",HttpStatus.OK);
	}

	@RequestMapping(value = "printDigitalCertificate/{ipuCode}", method = RequestMethod.GET)
	public ResponseEntity<DigitalObject> printDigitalCertificate(@PathVariable Long ipuCode)
			throws BadRequestException, IOException {
		 if(ipuCode==null){
			 throw new BadRequestException("Please select only certificate to print....");
		 }
		 DigitalObject digitalObject =  akiAuthenticationService.printCert(ipuCode);
		return new ResponseEntity<>(digitalObject,HttpStatus.OK);
	}

	@RequestMapping(value = "markPrintedPolCerts", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void markPrintedCerts()
			throws BadRequestException, IOException {
		certService.markCertPrinted();
	}

	@RequestMapping(value = {"deallocatePolCerts"}, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST})
	public ResponseEntity<String> deallocateCerts(@RequestBody PrintCertBean certBean) throws  BadRequestException {
		certService.deallocatePolCerts(certBean);
		return new ResponseEntity<String>("OK",HttpStatus.OK);
	}

	@RequestMapping(value = {"getpolicyprintcerts"}, method = {RequestMethod.GET})
	@ResponseBody
	public DataTablesResult<PolicyCertificateDTO> getPrintCerts(@DataTable DataTablesRequest pageable,
																@RequestParam(value = "polId", required = false) Long polId)
	{
		return certService.findPolCertToPrint(pageable,polId);
	}

	@RequestMapping(value = { "riskSchedules/{riskCode}" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<ScheduleTrans> getRiskSchedules(@DataTable DataTablesRequest pageable, @PathVariable Long riskCode)
			throws IllegalAccessException {
		return policyService.findRiskSchedules(pageable,riskCode);
	}

	@RequestMapping(value = { "getRiskSchedules/{riskCode}" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public ResponseEntity<ScheduleBean> getRiskSchedules( @PathVariable Long riskCode) throws BadRequestException {
		ScheduleBean scheduleBean = scheduleService.generateScheduleColumns(riskCode);
		return new ResponseEntity<ScheduleBean>(scheduleBean, HttpStatus.OK);
	}

	@RequestMapping(value = { "saveRiskSchedule" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseStatus(HttpStatus.CREATED)
	public void saveRiskSchedule(VehicleDetails scheduleTrans) throws IllegalAccessException, BadRequestException, IOException {
		policyService.createRiskSchedules(scheduleTrans);
	}

	@RequestMapping(value = "getRiskSchedule", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getRiskSchedule(@RequestParam Long riskId) {
		return policyService.getRiskSchedules(riskId);
	}

	@RequestMapping(value = { "saveScheduleData" }, method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<String> saveScheduleData(@RequestBody Map<String, Object> payload) throws BadRequestException{
		System.out.println("this is the paymoad"+ payload);
		Map<String, Object> scheduleData = (Map<String, Object>) payload.get("scheduleData");
		String tableName = (String) payload.get("tableName");
		policyService.insertOrUpdateDataIntoCustomTable(tableName,scheduleData);
		return ResponseEntity.ok("Record Created/Updated Successfully");
	}

	@RequestMapping(value = "deleteScheduleData", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> deleteScheduleData(@RequestBody Map<String, Object> payload) throws BadRequestException {
		String tableName = (String) payload.get("tableName");
		Integer priCode = (Integer) payload.get("priCode");
		System.out.println("TABLE NAME: " + tableName + ",PRI CODE: " + priCode);
		policyService.deleteScheduleData(priCode,tableName);
		return ResponseEntity.ok("Record Deleted Successfully");
	}


	@RequestMapping(value = { "deleteRiskSchedule/{scheduleId}" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteRiskSchedule(@PathVariable Long scheduleId) throws BadRequestException {
		policyService.deleteRiskSchedule(scheduleId);
	}

	@RequestMapping(value = { "createClient" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public String createClient(ClientDTO tenDef) throws IllegalAccessException, BadRequestException, ParseException {
		tenDef.setDateregistered(new Date());
		return setupsService.defineClient(tenDef,false);
	}

	@RequestMapping(value = { "getCommissionRate" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public ResponseEntity<BigDecimal> getCommissionRate(@RequestParam(value = "binId", required = false) Long binId) throws BadRequestException {
		System.out.println("BIND ID " + binId);
		BigDecimal rates = policyService.getCommissionRate(binId);
		System.out.println("Rates>>>>: " + rates);
		return new ResponseEntity<BigDecimal>(rates, HttpStatus.OK);
	}

	@RequestMapping(value = { "getSubAgentCommissionRate" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public ResponseEntity<BigDecimal> getSubAgentCommissionRate(@RequestParam(value = "binId", required = false) Long binId,@RequestParam(value = "accId", required = false) Long accId) throws BadRequestException {
		BigDecimal rates = policyService.getSubAgentCommissionRate(binId,accId);
		return new ResponseEntity<BigDecimal>(rates, HttpStatus.OK);
	}

	@RequestMapping(value = { "getMarketerCommissionRate" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public ResponseEntity<BigDecimal> getMarketerCommissionRate(@RequestParam(value = "binId", required = false) Long binId,@RequestParam(value = "accId", required = false) Long accId) throws BadRequestException {
		BigDecimal rates = policyService.getMarketerCommissionRate(binId,accId);
		return new ResponseEntity<BigDecimal>(rates, HttpStatus.OK);
	}

	@RequestMapping(value = {"sendSms"}, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST})
	public ResponseEntity<String> sendSms(@RequestBody MailMessageBean messageBean, HttpServletRequest request) throws BadRequestException {
		Long policyCode = (Long) request.getSession().getAttribute("policyCode");
		mailer.sendSmsAttachmentsANE(messageBean, policyCode, "P", request);
		return new ResponseEntity<String>("OK", HttpStatus.OK);
	}

	@RequestMapping(value = { "sendEmail" }, method = {org.springframework.web.bind.annotation.RequestMethod.POST })
	public ResponseEntity<String>  sendEmail(@RequestBody MailMessageBean messageBean,HttpServletRequest request) throws BadRequestException {
		System.out.println(messageBean);
		Long polCode = (Long) request.getSession().getAttribute("policyCode");
		mailer.sendEmailAttachments(messageBean,polCode,"P",request);
		return new ResponseEntity<String>("OK", HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value ="mailtemplate" )
	public String getMailTemplate(HttpServletResponse response, HttpServletRequest request,@RequestParam String templateType) throws BadRequestException{
		String template = "";
		switch (templateType) {
			case "non_motor_full_premium":
				template = templateService.getMailTemplate(MailTemplates.NON_MOTOR_FULL_PREMIUM,request);
				break;
			case "non_motor_initial_premium":
				template = templateService.getMailTemplate(MailTemplates.NON_MOTOR_INITIAL_PREMIUM,request);
				break;
			case "non_motor_additional_premium":
				template = templateService.getMailTemplate(MailTemplates.NON_MOTOR_ADDITIONAL_PREMIUM,request);
				break;
			case "motor_full_premium":
				template = templateService.getMailTemplate(MailTemplates.MOTOR_FULL_PREMIUM,request);
				break;
			case "motor_initial_premium":
				template = templateService.getMailTemplate(MailTemplates.MOTOR_INITIAL_PREMIUM,request);
				break;
			case "motor_additional_full_premium":
				template = templateService.getMailTemplate(MailTemplates.MOTOR_ADDITIONAL_FULL_PREMIUM,request);
				break;
			case "motor_additional_partial_premium":
				template = templateService.getMailTemplate(MailTemplates.MOTOR_ADDITIONAL_PARTIAL_PREMIUM,request);
				break;
			case "cash_based_policy":
				template = templateService.getMailTemplate(MailTemplates.CASH_BASED_POLICY_TEMPLATE,request);
				break;
			case "future_based_policy":
				template = templateService.getMailTemplate(MailTemplates.FUTURE_BASED_POLICY_TEMPLATE,request);
				break;
			case "overpaid_premium":
				template = templateService.getMailTemplate(MailTemplates.OVERPAID_PREMIUM_TEMPLATE,request);
				break;
			case "policy_template":
				template = templateService.getMailTemplate(MailTemplates.POLICY_TEMPLATE,request);
				break;
			default:
		}
		return template;
	}

	@ResponseBody
	@RequestMapping(value = "smstemplate")
	public String getSmsTemplate(HttpServletResponse response, HttpServletRequest request) throws BadRequestException {
		return templateService.getMailTemplate(MailTemplates.POLICY_TEMPLATE, request);
	}

	@RequestMapping(value = { "getReceiverEmail" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	public ResponseEntity<String> getReceiverEmail(@RequestParam(value = "receiver", required = false) String receiver, HttpServletRequest request) throws BadRequestException {
		Long polCode = (Long) request.getSession().getAttribute("policyCode");
		String email = mailer.getEmailReceivers(polCode,"P",receiver);
		return new ResponseEntity<String>(email, HttpStatus.OK);
	}

	@RequestMapping(value = {"getReceiverSmsNumber"}, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET})
	public ResponseEntity<String> getReceiverSmsNumber(@RequestParam(value = "receiver", required = false) String receiver, HttpServletRequest request) throws BadRequestException {
		Long policyCode = (Long) request.getSession().getAttribute("policyCode");
		String sms = mailer.getSMSReceivers(policyCode, "P", receiver);
		return new ResponseEntity<String>(sms, HttpStatus.OK);
	}

	@RequestMapping(value = { "riskDocs/{riskCode}" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<RiskDocsDTO> getRiskDocs(@DataTable DataTablesRequest pageable, @PathVariable Long riskCode)
			throws IllegalAccessException {
		return policyService.findRiskDocs(pageable,riskCode);
	}
	@RequestMapping(value = { "riskRefundDocs/{riskCode}" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<RiskDocsDTO> getRiskRefundDocs(@DataTable DataTablesRequest pageable, @PathVariable Long riskCode)
			throws IllegalAccessException {
		return policyService.findRiskRefundDocs(pageable,riskCode);
	}

	@RequestMapping(value = "/viewRiskDoc/{riskDocId}", method = RequestMethod.GET)
	public void viewRiskDoc(@PathVariable Long riskDocId, HttpServletResponse response) throws BadRequestException, IOException {

	}

	@RequestMapping(value = { "uploadRequiredDocs" }, method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> uploadRequiredDocs(UploadBean uploadBean, HttpServletRequest request) throws BadRequestException {
		try {
			Long polCode = (Long) request.getSession().getAttribute("policyCode");

			// Validate the file type for ALAK products
			validateFileTypeForALAK(uploadBean, polCode);

			uploadService.sybrinCreateCase(uploadBean, "Risk");
			return ResponseEntity.status(HttpStatus.CREATED).body("File Uploaded successfully");
		} catch (BadRequestException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	private void validateFileTypeForALAK(UploadBean uploadBean, Long polCode) throws BadRequestException {
		if (uploadBean.getDocId() == null) {
			return;
		}

		PolicyTrans policy = policyService.getPolicyDetails(polCode);
		if (policy == null || !isALAKProduct(policy)) {
			return; // Skip validation if policy not found or not ALAK
		}

		MultipartFile file = uploadBean.getFile();
		if (file == null || file.getOriginalFilename() == null) {
			throw new BadRequestException("Invalid file — missing filename");
		}

		String originalFilename = file.getOriginalFilename();
		int dotIndex = originalFilename.lastIndexOf(".");
		if (dotIndex == -1) {
			throw new BadRequestException("Invalid file format — missing file extension");
		}

		String fileExtension = originalFilename.substring(dotIndex + 1).toLowerCase();
		if (!Arrays.asList("pdf", "jpg", "jpeg").contains(fileExtension)) {
			throw new BadRequestException(
					"Absa Life Assurance Kenya Limited only supports PDF, JPG and JPEG documents"
			);
		}
	}

	private boolean isALAKProduct(PolicyTrans policy) {
		if (policy.getAgent() == null) {
			return false;
		}

		// Check if the agent's account ID matches ALAK's identifier
		return policy.getAgent().getAcctId() != null && policy.getAgent().getAcctId().equals(22465L);
	}



	@RequestMapping(value = "/riskdocument/{docId}", method = RequestMethod.GET)
	public void thumbnail(@PathVariable Long docId, HttpServletResponse response) throws BadRequestException, IOException {
		log.info("Starting document retrieval for docId: {}", docId);

		RiskDocs riskDoc = riskDocsRepo.getRiskDocsVal(docId);
		if (riskDoc == null) {
			log.error("RiskDoc not found for docId: {}", docId);
			response.sendError(HttpStatus.NOT_FOUND.value(), "Document not found");
			return;
		}
		log.debug("Retrieved RiskDoc: fileName={}, url={}", riskDoc.getUploadedFileName(), riskDoc.getUrl());

		byte[] content;
		if (riskDoc.getUrl() != null) {
			log.debug("Attempting to fetch content from URL: {}", riskDoc.getUrl());
			try (InputStream is = new URL(riskDoc.getUrl()).openStream()) {
				content = IOUtils.toByteArray(is);
				log.debug("Successfully retrieved content from URL, size: {} bytes", content.length);
			} catch (MalformedURLException e) {
				log.error("Malformed URL for docId: {}, url: {}", docId, riskDoc.getUrl(), e);
				response.sendError(HttpStatus.NOT_FOUND.value(), "Document not found");
				return;
			} catch (IOException e) {
				log.error("Failed to read content from URL for docId: {}", docId, e);
				response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to read document");
				return;
			}
		} else {
			log.debug("Fetching content from Sybrin system for docId: {}", docId);
			try {
				content = uploadService.sybrinDocumentDetails("Risk",
						riskDoc.getReqdDocs().getRequiredDoc().getReqId(),
						riskDoc.getRisk().getRiskId());
				log.debug("Retrieved content from Sybrin, size: {} bytes", content != null ? content.length : 0);
			} catch (Exception e) {
				log.error("Failed to retrieve document from Sybrin for docId: {}", docId, e);
				response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to retrieve document");
				return;
			}
		}

		if (content != null && content.length > 0) {
			String fileName = riskDoc.getUploadedFileName();
			String contentType = uploadService.getDocContentType(docId);
			log.debug("Preparing response headers - fileName: {}, contentType: {}, contentLength: {}",
					fileName, contentType, content.length);

			// Set appropriate headers
			response.setContentType(contentType);
			response.setContentLength(content.length);
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Expires", "0");

			// For Excel/Word files, set Content-Disposition to attachment
			if (fileName != null && (fileName.endsWith(".xlsx") ||
					fileName.endsWith(".xls") ||
					fileName.endsWith(".docx") ||
					fileName.endsWith(".doc"))) {
				String encodedFilename = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
				response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFilename);
				log.debug("Set Content-Disposition header for file: {}", encodedFilename);
			}

			// Write content
			try (OutputStream out = response.getOutputStream()) {
				out.write(content);
				out.flush();
				log.info("Successfully wrote document content to response for docId: {}", docId);
			} catch (IOException e) {
				log.error("Failed to write document content to response for docId: {}", docId, e);
				throw e;
			}
		} else {
			log.warn("No content found for docId: {}", docId);
			response.sendError(HttpStatus.NOT_FOUND.value(), "Document not found");
		}
	}

	@RequestMapping(value = { "deleteRiskDoc/{docId}" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteRiskDoc(@PathVariable Long docId) throws BadRequestException {
		uploadService.deleteRiskDoc(docId);
	}

	@RequestMapping(value = { "validateRisk" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public String validateRisk(@RequestParam(value = "riskId", required = false) String riskId,@RequestParam(value = "sclCode", required = false) Long sclCode) throws BadRequestException {
		policyService.validateRiskIdFormat(sclCode,riskId);
		return "Y";
	}

	@RequestMapping(value = { "getNewIntParties/{riskId}" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	public ResponseEntity<List<InterestedParties>> getNewIntParties(@PathVariable Long riskId) throws BadRequestException {
		List<InterestedParties> interestedParties = policyService.getNewInterestedParties(riskId);
		return new ResponseEntity<List<InterestedParties>>(interestedParties, HttpStatus.OK);
	}



	@RequestMapping(value = { "deleteIntParties/{partId}" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteIntParties(@PathVariable Long partId) throws BadRequestException {
		policyService.deleteRiskIntParty(partId);
	}

	@RequestMapping(value = { "importRisks" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseStatus(HttpStatus.CREATED)
	public void importRisks(RiskUploadForm uploadForm, HttpServletRequest request) throws BadRequestException, IOException {
		Long polCode = (Long) request.getSession().getAttribute("policyCode");
		if(polCode == null){
			policyService.savePendingRisks(uploadForm);
		} else {
			uploadForm.setPolCode(polCode);
			policyService.importExcelRiskTemplate(uploadForm);
		}

	}


	@RequestMapping(value = { "createUwPolicy" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	@ResponseStatus(HttpStatus.CREATED)
	public void createUwPolicy(PolicyCreateDTO policy,@RequestPart(value = "riskBean") RiskBean riskBean, HttpServletRequest request) throws BadRequestException {
		PolicyTrans created = null;
		Long polCode =null;
			try {
				created = policyService.createPolicy(policy, false);
				if("NB".equalsIgnoreCase( created.getTransType()) || "SP".equalsIgnoreCase( created.getTransType())|| "EX".equalsIgnoreCase( created.getTransType())|| "RN".equalsIgnoreCase( created.getTransType())|| "RE".equalsIgnoreCase( created.getTransType()) )
				{
					premiumService.computePrem(created.getPolicyId());
				}
				else{
					premiumService.computeEndorsePremium(created.getPolicyId());
				}
			} catch (IOException e) {
				throw new BadRequestException(e.getMessage());
			}
		polCode = created.getPolicyId();
		request.getSession().setAttribute("policyCode", polCode);
	}

	@RequestMapping(value = { "policyChecks" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<TransChecks> getPolicyChecks(@DataTable DataTablesRequest pageable, HttpServletRequest request)
			throws IllegalAccessException {
		Long policyCode = (Long) request.getSession().getAttribute("policyCode");
		if(policyCode==null) policyCode=-2000l;
		return policyService.findPolicyChecks(pageable,policyCode);
	}

	@RequestMapping(value = { "authChecks/{checkId}" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void authChecks(@PathVariable Long checkId, HttpServletRequest request) throws BadRequestException {
		Long policyCode = (Long) request.getSession().getAttribute("policyCode");
		policyService.approveException(checkId,policyCode);
	}

	@RequestMapping(value = { "getriskreqdocs" }, method = { RequestMethod.GET })
	@ResponseBody
	public List<ReqDocsDTO> getRiskUnassignedDocs(@RequestParam(value = "riskId", required = false) Long riskId, @RequestParam(value = "docName", required = false) String docName )
			throws IllegalAccessException {
		return policyService.findUnassignedRiskDocs(riskId,docName);
	}


	@RequestMapping(value = { "createRiskDocs" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	public ResponseEntity<String> createClientDocs(@RequestBody RequiredDocBean requiredDocBean) throws IllegalAccessException, IOException, BadRequestException {
		policyService.createRiskRequiredDocs(requiredDocBean);
		return new ResponseEntity<String>("OK",HttpStatus.OK);
	}


	@RequestMapping(value = { "riskImportLogs" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<RiskImportationLog> getRiskImportLogs(@DataTable DataTablesRequest pageable, HttpServletRequest request)
			throws IllegalAccessException {
		Long policyCode = (Long) request.getSession().getAttribute("policyCode");
		if(policyCode==null) policyCode=-2000l;
		return policyService.findPolicyImportationLog(pageable,policyCode);
	}


	@RequestMapping(value = { "createBeneficiary" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseStatus(HttpStatus.CREATED)
	public void createBeneficiary(PolicyBeneficiaries beneficiaries) throws IllegalAccessException, BadRequestException {
		lifeService.definePolicyBeneficiary(beneficiaries);
	}

	@RequestMapping(value = { "dependents/{policyCode}" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<PolicyDependentsInfo> getPolicyDependents(@DataTable DataTablesRequest pageable,@PathVariable Long policyCode)
			throws IllegalAccessException {
		return lifeService.findPolDependents(pageable,policyCode);
	}

	@RequestMapping(value = { "createDependents" }, method = {org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseStatus(HttpStatus.CREATED)
	public void createDependents(@RequestBody PolicyDependentsInfoDTO dependentsInfo) throws IllegalAccessException, BadRequestException {
		lifeService.definePolicyDependents(dependentsInfo);
	}

	@RequestMapping(value = { "deleteBeneficiary/{benCode}" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
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

	@RequestMapping(value = { "policyBenefits/{policyCode}" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<PolicyBenefitsDistribution> getPolicyBenefits(@DataTable DataTablesRequest pageable, @PathVariable Long policyCode)
			throws IllegalAccessException {
		return lifeService.findPolBenefits(pageable,policyCode);
	}

	@RequestMapping(value = { "policyBinders/{policyCode}" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<PolicyBinders> getPolicyBinders(@DataTable DataTablesRequest pageable, @PathVariable Long policyCode)
			throws IllegalAccessException {
		return policyService.findPolicyBinders(pageable,policyCode);
	}

	@RequestMapping(value = { "printCertificates" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void printCertificates(HttpServletRequest request) throws BadRequestException {
		Long polCode = (Long) request.getSession().getAttribute("policyCode");
		certService.batchPolicyCerts(polCode);

	}

	@RequestMapping(value = { "getInhouseEmail" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	public ResponseEntity<String> getInhouseEmail() throws BadRequestException {
		String email = quotationService.getInhouseEmail();
		return new ResponseEntity<String>(email, HttpStatus.OK);
	}


	@RequestMapping(value = { "proposalConversion" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	public ResponseEntity<PolicyTrans> proposalConversion(@RequestBody PolicyTrans policy,
														  HttpServletRequest request) throws BadRequestException {

		policyService.convertPropToPolicy(policy.getPolicyId());
		PolicyTrans coverted = policyService.getPolicyDetails(policy.getPolicyId());
		return new ResponseEntity<PolicyTrans>(coverted, HttpStatus.OK);
	}

	@RequestMapping(value = { "debitreceipts" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<ReceiptsDTO> getDebitTrans(@DataTable DataTablesRequest pageable, HttpServletRequest request)
			throws IllegalAccessException {
		Long polCode = (Long) request.getSession().getAttribute("policyCode");
		return accountsService.findPolicyCreditTrans(polCode,pageable);
	}

	@RequestMapping(value = { "lapsePolicy/{polCode}" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Long lapsePolicy(@PathVariable Long polCode) throws BadRequestException{
		 policyService.lapsePolicy(polCode);
		return -2000l;
	}

	@RequestMapping(value = { "unlapsePolicy/{polCode}" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Long unlapsePolicy(@PathVariable Long polCode) throws BadRequestException{
		policyService.unLapsePolicy(polCode);
		return -2000l;
	}

	@RequestMapping(value = { "SavePolicyQuiz" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@Transactional(readOnly = false, rollbackFor = { BadRequestException.class })
	public ResponseEntity<String> savePolicyQuiz(@RequestBody JsonNode questionnaireDTO) throws BadRequestException {

		Iterator<JsonNode> jsonNodeIterator = questionnaireDTO.get("quizandAnswers").elements();
		QuestionnaireDTO questionnaireDTO1 = new QuestionnaireDTO();
		questionnaireDTO1.setQuizPolicyCode(questionnaireDTO.get("quizPolicyCode").asLong());
		List<QuestionnaireBean> questionnaireBeanList = new ArrayList<>();
		while(jsonNodeIterator.hasNext()){
			QuestionnaireBean questionnaireBean = new QuestionnaireBean();
			JsonNode question = jsonNodeIterator.next();
			System.out.println("question "+question.get("question").asText());
			questionnaireBean.setQuestion(question.get("question").asText());
			JsonNode jsonNode = question.get("answer");
			List<String> answers = new ArrayList<>();
			if(jsonNode.isArray()){

				Iterator<JsonNode> array = jsonNode.elements();
				while(array.hasNext()){
					answers.add(array.next().asText());
				}

			}
			else{
				answers.add(jsonNode.asText());
			}
			questionnaireBean.setAnswer(answers);
			questionnaireBeanList.add(questionnaireBean);
		}
		questionnaireDTO1.setQuizandAnswers(questionnaireBeanList);


		policyService.savePolicyQuiz(questionnaireDTO1);
		System.out.println(questionnaireDTO);
		//policyService.savePolicyQuiz(questionnaireDTO);
		return new ResponseEntity<String>("Ok", HttpStatus.OK);
	}


	@RequestMapping(value = { "policyQuiz/{policyCode}" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<PolicyQuestionnaire> getPolicyQuiz(@DataTable DataTablesRequest pageable,@PathVariable Long policyCode)
			throws IllegalAccessException {
		return lifeService.findPolQuiz(pageable,policyCode);
	}

	@RequestMapping(value = { "policyQuizList" }, method = { RequestMethod.GET })
	@ResponseBody
	public Iterable<PolicyQuestionnaire> getPolicyQuizList(HttpServletRequest request)
			throws IllegalAccessException {
		Long polCode = (Long) request.getSession().getAttribute("policyCode");
		return lifeService.findPolQuizList(polCode);
	}

	@RequestMapping(value = { "deletePolicyQuiz/{policyCode}" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deletePolicyQuiz(@PathVariable Long policyCode) {policyService.deletePolicyQuiz(policyCode);
	}

	@RequestMapping(value = { "checkPolId" }, method = { RequestMethod.GET })
	@ResponseBody
	public PolicyTrans getPol(@RequestParam(value = "idNo") Long idNo) {
		return policyService.findEnquiryId(idNo);
	}
	@RequestMapping(value = { "checkPol" }, method = { RequestMethod.GET })
	@ResponseBody
	public PolicyTrans getPolicyUn(@RequestParam(value = "polNo") Long polNo) {
		return policyService.findEnquiryPol(polNo);
	}
	@RequestMapping(value = { "checkClaim" }, method = { RequestMethod.GET })
	@ResponseBody
	public ClaimBookings checkClaim(@RequestParam(value = "claim") Long claim) {
		return policyService.checkClaim(claim);
	}
	@RequestMapping(value = { "checkRiskId" }, method = { RequestMethod.GET })
	@ResponseBody
	public RiskTrans getRiskUn(@RequestParam(value = "riskId") Long riskId) {
		return policyService.findEnquiryRisk(riskId);
	}
	@RequestMapping(value = { "checkAllParam" }, method = { RequestMethod.GET })
	@ResponseBody
	public RiskTrans checkAllParam(@RequestParam(value = "polNo") String polNo,
								   @RequestParam(value = "idNo") Long idNo,
								   @RequestParam(value = "riskId") String riskId){
		return policyService.checkAllParam(polNo,idNo,riskId);
	}
	@RequestMapping(value = { "checkPolAndIdParam" }, method = { RequestMethod.GET })
	@ResponseBody
	public PolicyTrans getPolAndId(@RequestParam(value = "polNo") String polNo,
								   @RequestParam(value = "idNo") Long idNo) {
		return policyService.findEnquiryPolAndId(polNo,idNo);
	}
	@RequestMapping(value = { "checkIdAndRiskParam" }, method = { RequestMethod.GET })
	@ResponseBody
	public RiskTrans getRiskId(@RequestParam(value = "riskId") String riskId,
							   @RequestParam(value = "idNo") Long idNo) {
		return policyService.findEnquiryRiskId(riskId,idNo);
	}
	@RequestMapping(value = { "checkPolAndRiskParam" }, method = { RequestMethod.GET })
	@ResponseBody
	public RiskTrans getRiskPol(@RequestParam(value = "riskId") String riskId,
								@RequestParam(value = "polNo") String polNo) {
		return policyService.findEnquiryRiskPol(riskId,polNo);
	}

	@RequestMapping(value = { "masterEnqAll" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<RiskTrans> getMasterPolicies(@DataTable DataTablesRequest pageable,
															@RequestParam(value = "polNo", required = false) Long polNo,
															@RequestParam(value = "idNo", required = false) Long idNo,
															@RequestParam(value = "riskId", required = false) Long riskId
															) throws IllegalAccessException {
		return policyService.findEnquiryMaster(pageable,polNo,riskId,idNo);
	}
	@RequestMapping(value = { "masterEnqPI" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<PolicyTrans> masterEnqPI(@DataTable DataTablesRequest pageable,
														 @RequestParam(value = "polNo", required = false) Long polNo,
														 @RequestParam(value = "idNo", required = false) Long idNo
	) throws IllegalAccessException {
		return policyService.masterEnqPI(pageable,polNo,idNo);
	}
	@RequestMapping(value = { "masterEnqPR" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<RiskTrans> masterEnqPR(@DataTable DataTablesRequest pageable,
													 @RequestParam(value = "polNo", required = false) Long polNo,
													 @RequestParam(value = "riskId", required = false) Long riskId
	) throws IllegalAccessException {
		return policyService.findEnquiryPR(pageable,polNo,riskId);
	}
	@RequestMapping(value = { "masterEnqRI" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<RiskTrans> masterEnqRI(@DataTable DataTablesRequest pageable,
												   @RequestParam(value = "idNo", required = false) Long idNo,
												   @RequestParam(value = "riskId", required = false) Long riskId
	) throws IllegalAccessException {
		return policyService.findEnquiryRI(pageable,idNo,riskId);
	}
    @RequestMapping(value = { "masterEnqPol" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<PolicyTrans> masterEnqPol(@DataTable DataTablesRequest pageable,
                                                   @RequestParam(value = "polNo", required = false) Long polNo,
													  @RequestParam(required=false) String group)
     throws IllegalAccessException {
        return policyService.masterEnqPol(pageable,polNo);
    }
	@RequestMapping(value = { "masterEnqIdNo" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<PolicyTrans> masterEnqIdNo(@DataTable DataTablesRequest pageable,
													   	   @RequestParam(value = "idNo", required = false) Long idNo
	) throws IllegalAccessException {
		return policyService.masterEnqIdNo(pageable,idNo);
	}
	@RequestMapping(value = { "searchPC" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<ClientDef> masterIdNo(@DataTable DataTablesRequest pageable,
													   @RequestParam(value = "idNo", required = false) Long idNo
	) throws IllegalAccessException {
		return policyService.masterIdNo(pageable,idNo);
	}
	@RequestMapping(value = { "searchPolRisk" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<RiskTrans> masterEnqRisk(@DataTable DataTablesRequest pageable,
													   @RequestParam(value = "policyId", required = false) Long policyId
	) throws IllegalAccessException {
		return policyService.masterEnqRisk(pageable,policyId);
	}
    @RequestMapping(value = { "masterEnqRisk" }, method = { RequestMethod.GET })
    @ResponseBody
    public DataTablesResult<RiskTrans> masterEnqUniqueRisk(@DataTable DataTablesRequest pageable,
                                                     @RequestParam(value = "riskId", required = false) Long riskId
    ) throws IllegalAccessException {
        return policyService.masterEnqUniqueRisk(pageable,riskId);
    }
	@RequestMapping(value = { "masterEnqUniqueId" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<RiskTrans> masterEnqUniqueId(@DataTable DataTablesRequest pageable,
													 @RequestParam(value = "riskId", required = false) Long riskId
	) throws IllegalAccessException {
		return policyService.masterEnqUniqueId(pageable,riskId);
	}
	@RequestMapping(value = { "masterEnqUniqueClaim" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<ClaimPerils> masterEnqUniqueClaim(@DataTable DataTablesRequest pageable,
															  @RequestParam(value = "riskId", required = false) Long riskId
	) throws IllegalAccessException {
		return policyService.masterEnqUniqueClaim(pageable,riskId);
	}
	@RequestMapping(value = { "getReceipts" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<ReceiptTrans> masterReceipts(@DataTable DataTablesRequest pageable,
															   @RequestParam(value = "idNo", required = false) Long idNo,
														 @RequestParam(value = "", required = false) Long polNo
	) throws IllegalAccessException {
		return policyService.masterReceipts(pageable,idNo);
	}
	@RequestMapping(value = { "getReceiptsDets/{receiptId}" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<ReceiptTransDtls> getReceiptsDets(@DataTable DataTablesRequest pageable,
														 @PathVariable Long receiptId
	) throws IllegalAccessException {
		return policyService.getReceiptsDets(pageable,receiptId);
	}
	@RequestMapping(value = { "overpaidPolicies" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<OverpaidPolicyDTO> getOverpaidPolicies(@DataTable DataTablesRequest request,  @RequestParam(required = false) Long accountCode, @RequestParam(required = false) Date dateFrom, @RequestParam(required = false) Date dateTo) {
		return policyService.getOverpaidPolicies(request,dateFrom,dateTo,accountCode);
	}
	@RequestMapping(value = "/viewClients", method = RequestMethod.POST)
	public String editRentalForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model) {
		model.addAttribute("tenId", helperForm.getId());
		model.addAttribute("clntId",helperForm.getId());
		return "clientsform";
	}
	@RequestMapping(value = { "tenants/{tenId}" }, method = { RequestMethod.GET })
	@ResponseBody
	public ClientDTO getAccountDetails(@PathVariable Long tenId) {
		ClientDTO tenant =  setupsService.getClientDetails(tenId);
		return tenant;
	}

	@RequestMapping(value = "/viewReceipt", method = RequestMethod.POST)
	public String editReceiptForm(@Valid @ModelAttribute ModelHelperForm helperForm, Model model,HttpServletRequest request) {
		model.addAttribute("receiptId",helperForm.getId());
		return "receiptsform";
	}
	@RequestMapping(value = { "getRcpts/{receiptCode}" }, method = { RequestMethod.GET })
	@ResponseBody
	public ReceiptTrans getAccountReceipts(@PathVariable Long receiptCode) {
		ReceiptTrans receipts =  setupsService.getReceipts(receiptCode);
		return receipts;
	}

	@RequestMapping(value = { "mileageDetails" }, method = { RequestMethod.GET })
	@ResponseBody
	public MileageDTO getMileageDetails(@RequestParam("riskId") String riskId) {
		final String trimmedRisk = riskId.replaceAll(" ","");
		return policyService.findMileageDetails(riskId);
	}

	@RequestMapping(value = "/policydocument", method = RequestMethod.GET)
	public ResponseEntity<byte[]> getclmreqdocument(HttpServletRequest request ) throws BadRequestException {
		Long polCode = (Long) request.getSession().getAttribute("policyCode");
		System.out.println("found.."+polCode);
		final PolicyTrans policyTrans = policyService.getPolicyDetails(polCode);
		System.out.println("Policy found...."+policyTrans.getCurrentStatus());
		byte[] content = policyService.getPolicyDocument(policyTrans.getProduct().getProCode());
		System.out.println("Content Length..."+content.length);
		if (content.length>0) {
			String contentType = policyService.getPolicyDocumentType(policyTrans.getProduct().getProCode());
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.parseMediaType(contentType));
			headers.setContentLength(content.length);
			return new ResponseEntity<byte[]>(content, headers, HttpStatus.OK);
		} else {
			return new ResponseEntity<byte[]>(HttpStatus.NOT_FOUND);
		}
	}
	@RequestMapping(value = "rpt_overpaidpremiums", method = RequestMethod.POST)
	public ModelAndView overPaidPremiumsReport(ModelMap modelMap, HttpServletRequest request, ModelAndView modelAndView,
											   @RequestParam("dateFrom") Date dateFrom,
											   @RequestParam("dateTo") Date dateTo,
											   @RequestParam("accountCode") Long Agent)
			throws BadRequestException, IOException {
		OrganizationDTO organization = orgService.getOrganizationLogoDetails();
		InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
		BufferedImage image = ImageIO.read(in);
		modelMap.put("logo", image );
		modelMap.put("datasource", datasource);
		modelMap.put("format", "pdf");
		modelMap.put("dateFrom", dateFrom);
		modelMap.put("Agent", Agent);
		modelMap.put("dateTo", dateTo);
		modelAndView = new ModelAndView("rpt_overpaid_policies", modelMap);
		return modelAndView;
	}


	@RequestMapping(value = "/savepolicymiscinfo", method = RequestMethod.POST, consumes = "application/json")
	@ResponseStatus(HttpStatus.CREATED)
	public PolicyMiscInfo savePolicyInfo(@RequestBody PolicyMiscInfoDTO dto) {
		PolicyMiscInfo entity = new PolicyMiscInfo();
		entity.setAccountNumber(dto.getAccountNumber());
		entity.setAccountName(dto.getAccountName());
		entity.setStrikeDay(dto.getStrikeDay());
		entity.setAccountType(dto.getAccountType());
		entity.setBankId(dto.getBankId());
		entity.setBranchId(dto.getBranchId());
		entity.setInflationPercent(dto.getInflationPercent());
		entity.setPolicyId(dto.getPolId());
		return policyMiscInfoRepo.save(entity);
	}

	@RequestMapping(value = {"riskImportTemplate"}, method = {RequestMethod.GET})
	@ResponseBody
	public void exportRiskImportTemplate(HttpServletResponse response) throws IOException {

		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename=risk-import-template.xlsx");

		String[] riskHeaders = {
				"Risk_ID", "EFF_DATE", "EXP_DATE","Dob", "BUT_CHARGE PREMIUM", "NATIONAL_ID", "PIN", "POSTAL_ADDRS",
				"POSTAL_TOWN", "POSTAL_CODE", "MOBILE_NUMBER", "INSURED_NAME",
				"COUNTRY", "RISK_SHT_DEC", "Risk_Desc", "Cover_Type"
		};

		String[] sampleData = {
				"1", "1/29/2025", "1/28/2026", "1/11/2000", "5000", "123456789", "A000000000P", "WAIYAKI WAY",
				"NAIROBI", "20210", "254722000000", "AMIGO MILES",
				"KENYA", "KAA 000F", "TOYOTA MAZDA", "COMP"
		};

		XSSFWorkbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Risk Import");

		// Create header row
		Row headerRow = sheet.createRow(0);
		CellStyle headerStyle = workbook.createCellStyle();
		XSSFFont headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerStyle.setFont(headerFont);

		for (int i = 0; i < riskHeaders.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(riskHeaders[i]);
			cell.setCellStyle(headerStyle);
		}

		// Create sample data row
		Row dataRow = sheet.createRow(1);
		for (int i = 0; i < sampleData.length; i++) {
			Cell cell = dataRow.createCell(i);
			cell.setCellValue(sampleData[i]);
		}

		// Auto-size columns after adding all data
		for (int i = 0; i < riskHeaders.length; i++) {
			sheet.autoSizeColumn(i);
		}

		workbook.write(response.getOutputStream());
//     workbook.close();
	}
}