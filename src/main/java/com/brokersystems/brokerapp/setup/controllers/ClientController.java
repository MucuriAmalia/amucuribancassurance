package com.brokersystems.brokerapp.setup.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.brokersystems.brokerapp.dms.service.UploadService;
import com.brokersystems.brokerapp.dms.model.UploadBean;
import com.brokersystems.brokerapp.integrations.service.ClientIntegrationService;
import com.brokersystems.brokerapp.server.utils.*;
import com.brokersystems.brokerapp.setup.dto.*;
import com.brokersystems.brokerapp.setup.dto.netreveal.NetrevealRequest;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.repository.ClientDocsRepo;
import com.google.gson.Gson;
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
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.service.ClientService;
import com.brokersystems.brokerapp.setup.service.SetupsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller
@RequestMapping({ "/protected/clients/setups" })
public class ClientController {

	private static final Logger logger = LoggerFactory.getLogger(ClientController.class);
	
	@Autowired
	private ClientService tenService;


	
	@Autowired
	private SetupsService setupsService;

	@Autowired
	private ValidatorUtils validator;
	@Autowired
	LocationUtils locationUtils;

	@Autowired
	private ClientService clientService;

	@Autowired
	private UploadService uploadService;

	@Autowired
	private ClientIntegrationService integrationService;

    @Autowired
	private AuditTrailLogger auditTrailLogger;
    @Autowired
    private ClientDocsRepo clientDocsRepo;
    @Autowired
    private FormLockManager formLockManager;


	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    dateFormat.setLenient(false);
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}
	
	@ModelAttribute
	public ModelHelperForm createHelperForm() {
		return new ModelHelperForm();
	}
	
	
	@RequestMapping(value = "clientslist", method = RequestMethod.GET)
	public String tenantList(Model model,HttpServletRequest request) {
		String message="Accessed Clients screen";
		String resource = "Client List";
		auditTrailLogger.log(message,request, resource);
		return "clients";
	}

	@RequestMapping(value = "prospectlist", method = RequestMethod.GET)
	public String prospectList(Model model,HttpServletRequest request) {
		String message="Accessed Prospects screen";
		String resource = "Prospects Screen";
		auditTrailLogger.log(message,request, resource);
		return "prospects";
	}


	@RequestMapping(value = "clientsform", method = RequestMethod.GET)
	public String tenantForm(Model model) {
		model.addAttribute("tenId", -2000);
		return "clientsform";
	}
	
	@RequestMapping(value = { "tenants" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<ClientDTO> getClients(@DataTable DataTablesRequest pageable, @RequestParam(value = "searchType", required = false) String searchType, @RequestParam(value = "searchValue", required = false) String searchValue) throws BadRequestException {
		return tenService.findAllClients(pageable, searchType, searchValue);
	}

	@RequestMapping(value = { "transactions/{tenId}" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<ClientTransactionDTO> getClientTransactions(@DataTable DataTablesRequest pageable,
																		@PathVariable Long tenId)
	{
		return tenService.findClientTransactions(pageable, tenId);
	}



	@RequestMapping(value = "/tenantImage/{tenId}")
	public void getImage(HttpServletResponse response, @PathVariable Long tenId)
			throws IOException, ServletException {
		ClientDef tenant = tenService.getClientDetails(tenId);
		if (tenant.getTenId()!=null && tenant.getPhotoUrl()!=null ) {
				File file = new File(tenant.getPhotoUrl());
				response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
				response.getOutputStream().write(Files.readAllBytes(file.toPath()));
				response.getOutputStream().close();

		}
	}

	@RequestMapping(value = { "/createClient" }, method = { RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<Map<String, Object>> createTenant(
			ClientDTO tenDef,
			@RequestParam(value = "clientId", required = false) Long clientId) {

		String requestId = java.util.UUID.randomUUID().toString().substring(0, 8);
		logger.info("[{}] === CREATE CLIENT ENDPOINT CALLED ===", requestId);
		logger.debug("[{}] clientId parameter: {}", requestId, clientId);
		logger.debug("[{}] tenDef data: {}", requestId, new Gson().toJson(tenDef));

		Map<String, Object> response = new HashMap<>();

		try {
			// Step 1: Marketing material validation
			logger.debug("[{}] Step 1: Validating marketing preferences...", requestId);
			logger.debug("[{}] receiveMarketingMaterial: {}", requestId, tenDef.getReceiveMarketingMaterial());
			logger.debug("[{}] preferredModeOfCommunication: {}", requestId, tenDef.getPreferredModeOfCommunication());

			if (Boolean.TRUE.equals(tenDef.getReceiveMarketingMaterial()) &&
					(tenDef.getPreferredModeOfCommunication() == null || tenDef.getPreferredModeOfCommunication().isEmpty())) {
				logger.error("[{}] Marketing validation failed: Preferred mode of communication is required", requestId);
				throw new BadRequestException("Preferred mode of communication is required when marketing material is selected.");
			}
			logger.debug("[{}] Marketing validation passed", requestId);

			// Step 2: Set client ID if updating
			if (clientId != null) {
				logger.debug("[{}] Step 2: Setting tenId for update: {}", requestId, clientId);
				tenDef.setTenId(clientId);
			} else {
				logger.debug("[{}] Step 2: Creating new client (no clientId provided)", requestId);
			}

			// Step 3: File upload handling
			logger.debug("[{}] Step 3: Processing file upload...", requestId);
			if (tenDef.getFile() != null && !tenDef.getFile().isEmpty()) {
				logger.debug("[{}] File provided - size: {}", requestId, tenDef.getFile().getSize());

				if (tenDef.getFile().getSize() != 0) {
					logger.debug("[{}] Processing file upload...", requestId);
					try {
						UploadDocumentForm uploadDocumentForm = new UploadDocumentForm();
						uploadDocumentForm.setFile(tenDef.getFile());
						uploadDocumentForm.setEntityId(100L);
						uploadDocumentForm.setEntityType("client_image");

						final String loc = locationUtils.saveFile(uploadDocumentForm);
						logger.debug("[{}] File saved to: {}", requestId, loc);
						tenDef.setPhotoUrl(loc);
					} catch (Exception e) {
						logger.error("[{}] File upload failed: {}", requestId, e.getMessage(), e);
						throw new BadRequestException("File upload failed: " + e.getMessage());
					}
				} else {
					logger.debug("[{}] Empty file provided, checking for existing photo URL...", requestId);
					if (tenDef.getTenId() != null) {
						try {
							String existingPhotoUrl = setupsService.getClientDetails(tenDef.getTenId()).getPhotoUrl();
							logger.debug("[{}] Existing photo URL: {}", requestId, existingPhotoUrl);
							tenDef.setPhotoUrl(existingPhotoUrl);
						} catch (Exception e) {
							logger.error("[{}] Failed to get existing client details: {}", requestId, e.getMessage(), e);
							throw new BadRequestException("Failed to retrieve existing client details: " + e.getMessage());
						}
					} else {
						logger.debug("[{}] No existing client ID, skipping photo URL retrieval", requestId);
					}
				}
			} else {
				logger.debug("[{}] No file provided", requestId);
				if (tenDef.getTenId() != null) {
					logger.debug("[{}] Getting existing photo URL for client: {}", requestId, tenDef.getTenId());
					try {
						String existingPhotoUrl = setupsService.getClientDetails(tenDef.getTenId()).getPhotoUrl();
						logger.debug("[{}] Existing photo URL: {}", requestId, existingPhotoUrl);
						tenDef.setPhotoUrl(existingPhotoUrl);
					} catch (Exception e) {
						logger.error("[{}] Failed to get existing client details: {}", requestId, e.getMessage(), e);
						throw new BadRequestException("Failed to retrieve existing client details: " + e.getMessage());
					}
				} else {
					logger.debug("[{}] No clientId and no file provided, proceeding without photo URL", requestId);
				}
			}

			// Step 4: Call appropriate service method
			String result;
			if (clientId != null) {
				logger.debug("[{}] Step 4: Calling updateClient service...", requestId);
				try {
					result = setupsService.updateClient(tenDef, true);
					logger.debug("[{}] updateClient completed successfully, result: {}", requestId, result);
				} catch (Exception e) {
					logger.error("[{}] updateClient failed: {}", requestId, e.getMessage(), e);
					throw e;
				}
			} else {
				logger.debug("[{}] Step 4: Calling defineClient service...", requestId);
				try {
					result = setupsService.defineClient(tenDef, true);
					logger.debug("[{}] defineClient completed successfully, result: {}", requestId, result);
				} catch (Exception e) {
					logger.error("[{}] defineClient failed: {}", requestId, e.getMessage(), e);
					throw e;
				}
			}

			logger.info("[{}] Service result: {}", requestId, result);
			logger.info("[{}] === CREATE CLIENT ENDPOINT COMPLETED SUCCESSFULLY ===", requestId);

			// Return success response
			response.put("status", "success");
			response.put("message", clientId != null ? "Client updated successfully" : "Client created successfully");
			response.put("clientHash", result);
			response.put("operation", clientId != null ? "update" : "create");
			response.put("requestId", requestId);
			response.put("clientId", tenDef.getTenId());

			return ResponseEntity.ok(response);

		} catch (BadRequestException e) {
			logger.error("[{}] === BAD REQUEST EXCEPTION ===: {}", requestId, e.getMessage(), e);
			response.put("status", "error");
			response.put("message", e.getMessage());
			response.put("type", "validation_error");
			response.put("requestId", requestId);

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

		} catch (ParseException e) {
			logger.error("[{}] === PARSE EXCEPTION ===: {}", requestId, e.getMessage(), e);
			response.put("status", "error");
			response.put("message", "Data parsing error: " + e.getMessage());
			response.put("type", "parse_error");
			response.put("requestId", requestId);

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

		} catch (Exception e) {
			logger.error("[{}] === UNEXPECTED EXCEPTION ===: type={}, message={}",
					requestId, e.getClass().getSimpleName(), e.getMessage(), e);
			response.put("status", "error");
			response.put("message", "Internal server error: " + e.getMessage());
			response.put("type", "internal_error");
			response.put("requestId", requestId);

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

//	@RequestMapping(value = { "/createClient" }, method = {
//			org.springframework.web.bind.annotation.RequestMethod.POST })
//	@ResponseBody
//	public ResponseEntity<Map<String, Object>> createTenant(
//			ClientDTO tenDef,
//			@RequestParam(value = "clientId", required = false) Long clientId) {
//
//		String requestId = java.util.UUID.randomUUID().toString().substring(0, 8);
//		System.out.println("[" + requestId + "] === CREATE CLIENT ENDPOINT CALLED ===");
//		System.out.println("[" + requestId + "] clientId parameter: " + clientId);
//		System.out.println("[" + requestId + "] tenDef data: " + tenDef.toString());
//
//		Map<String, Object> response = new HashMap<>();
//
//		try {
//			// Step 1: Marketing material validation
//			System.out.println("[" + requestId + "] Step 1: Validating marketing preferences...");
//			System.out.println("[" + requestId + "] receiveMarketingMaterial: " + tenDef.getReceiveMarketingMaterial());
//			System.out.println("[" + requestId + "] preferredModeOfCommunication: " + tenDef.getPreferredModeOfCommunication());
//
//			if (Boolean.TRUE.equals(tenDef.getReceiveMarketingMaterial()) &&
//					(tenDef.getPreferredModeOfCommunication() == null || tenDef.getPreferredModeOfCommunication().isEmpty())) {
//				System.out.println("[" + requestId + "] ERROR: Marketing validation failed");
//				throw new BadRequestException("Preferred mode of communication is required when marketing material is selected.");
//			}
//			System.out.println("[" + requestId + "] Marketing validation passed");
//
//			// Step 2: Set client ID if updating
//			if (clientId != null) {
//				System.out.println("[" + requestId + "] Step 2: Setting tenId for update: " + clientId);
//				tenDef.setTenId(clientId);
//			} else {
//				System.out.println("[" + requestId + "] Step 2: Creating new client (no clientId provided)");
//			}
//
//			// Step 3: File upload handling
//			System.out.println("[" + requestId + "] Step 3: Processing file upload...");
//			if ((tenDef.getFile() != null) && (!tenDef.getFile().isEmpty())) {
//				System.out.println("[" + requestId + "] File provided - size: " + tenDef.getFile().getSize());
//
//				if (tenDef.getFile().getSize() != 0) {
//					System.out.println("[" + requestId + "] Processing file upload...");
//					try {
//						UploadDocumentForm uploadDocumentForm = new UploadDocumentForm();
//						uploadDocumentForm.setFile(tenDef.getFile());
//						uploadDocumentForm.setEntityId(100L);
//						uploadDocumentForm.setEntityType("client_image");
//
//						final String loc = locationUtils.saveFile(uploadDocumentForm);
//						System.out.println("[" + requestId + "] File saved to: " + loc);
//						tenDef.setPhotoUrl(loc);
//
//					} catch (Exception e) {
//						System.out.println("[" + requestId + "] ERROR: File upload failed: " + e.getMessage());
//						e.printStackTrace();
//						throw new BadRequestException("File upload failed: " + e.getMessage());
//					}
//				} else {
//					System.out.println("[" + requestId + "] Empty file provided, getting existing photo URL...");
//					if (tenDef.getTenId() != null) {
//						try {
//							String existingPhotoUrl = setupsService.getClientDetails(tenDef.getTenId()).getPhotoUrl();
//							System.out.println("[" + requestId + "] Existing photo URL: " + existingPhotoUrl);
//							tenDef.setPhotoUrl(existingPhotoUrl);
//						} catch (Exception e) {
//							System.out.println("[" + requestId + "] ERROR: Failed to get existing client details: " + e.getMessage());
//							e.printStackTrace();
//							throw new BadRequestException("Failed to retrieve existing client details: " + e.getMessage());
//						}
//					}
//				}
//			} else {
//				System.out.println("[" + requestId + "] No file provided");
//				if (tenDef.getTenId() != null) {
//					System.out.println("[" + requestId + "] Getting existing photo URL for client: " + tenDef.getTenId());
//					try {
//						String existingPhotoUrl = setupsService.getClientDetails(tenDef.getTenId()).getPhotoUrl();
//						System.out.println("[" + requestId + "] Existing photo URL: " + existingPhotoUrl);
//						tenDef.setPhotoUrl(existingPhotoUrl);
//					} catch (Exception e) {
//						System.out.println("[" + requestId + "] ERROR: Failed to get existing client details: " + e.getMessage());
//						e.printStackTrace();
//						throw new BadRequestException("Failed to retrieve existing client details: " + e.getMessage());
//					}
//				}
//			}
//
//			// Step 4: Call appropriate service method
//			String result;
//			if (clientId != null) {
//				System.out.println("[" + requestId + "] Step 4: Calling updateClient service...");
//				result = setupsService.updateClient(tenDef, true);
//				System.out.println("[" + requestId + "] updateClient completed successfully");
//			} else {
//				System.out.println("[" + requestId + "] Step 4: Calling defineClient service...");
//				result = setupsService.defineClient(tenDef, true);
//				System.out.println("[" + requestId + "] defineClient completed successfully");
//			}
//
//			System.out.println("[" + requestId + "] Service result: " + result);
//			System.out.println("[" + requestId + "] === CREATE CLIENT ENDPOINT COMPLETED SUCCESSFULLY ===");
//
//			// Return success response
//			response.put("status", "success");
//			response.put("message", clientId != null ? "Client updated successfully" : "Client created successfully");
//			response.put("clientHash", result);
//			response.put("operation", clientId != null ? "update" : "create");
//
//			return ResponseEntity.ok(response);
//
//		} catch (BadRequestException e) {
//			System.out.println("[" + requestId + "] === BAD REQUEST EXCEPTION ===");
//			System.out.println("[" + requestId + "] Error: " + e.getMessage());
//			e.printStackTrace();
//
//			response.put("status", "error");
//			response.put("message", e.getMessage());
//			response.put("type", "validation_error");
//			response.put("requestId", requestId);
//
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//
//		} catch (ParseException e) {
//			System.out.println("[" + requestId + "] === PARSE EXCEPTION ===");
//			System.out.println("[" + requestId + "] Error: " + e.getMessage());
//			e.printStackTrace();
//
//			response.put("status", "error");
//			response.put("message", "Data parsing error: " + e.getMessage());
//			response.put("type", "parse_error");
//			response.put("requestId", requestId);
//
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//
//		} catch (Exception e) {
//			System.out.println("[" + requestId + "] === UNEXPECTED EXCEPTION ===");
//			System.out.println("[" + requestId + "] Exception type: " + e.getClass().getSimpleName());
//			System.out.println("[" + requestId + "] Exception message: " + e.getMessage());
//			e.printStackTrace();
//
//			response.put("status", "error");
//			response.put("message", "Internal server error: " + e.getMessage());
//			response.put("type", "internal_error");
//			response.put("requestId", requestId);
//
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//		}
//	}
	@RequestMapping(value = { "tenants/{tenId}" }, method = { RequestMethod.GET })
	@ResponseBody
	public ClientDTO getAccountDetails(@PathVariable String tenId) {
		try {
			Long id = Long.parseLong(tenId);
			return setupsService.getClientDetails(id);
		}
		catch (Exception ex){
		}
		return setupsService.getClientDetailsByHash(tenId);
	}

	@RequestMapping(value = "/editClients/{tenId}", method = RequestMethod.GET)
	public String editRentalForm(@PathVariable String tenId) throws BadRequestException {
		formLockManager.openForm("CLIENT",String.valueOf(tenId));
		return "viewclientsform";
	}


	 @RequestMapping(value={"selMobilePrefix"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  @ResponseBody
	  public Page<MobilePrefixDef> selectSections(@RequestParam(value="term", required=false) String term, @RequestParam("couCode") Long couCode, Pageable pageable)
	    throws IllegalAccessException, BadRequestException
	  {
	    return setupsService.findSelPrefixes(term, pageable, couCode);
	  }
	 
	 @RequestMapping(value = { "createPrefix" }, method = {
				org.springframework.web.bind.annotation.RequestMethod.POST })
		@ResponseStatus(HttpStatus.CREATED)
		public void createPrefix(MobilePrefixDef prefix) throws IllegalAccessException, BadRequestException {
			setupsService.definePrefix(prefix);
		}
	 
	 @RequestMapping(value={"selClientTypes"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  @ResponseBody
	  public Page<ClientTypes> selClientTypes(@RequestParam(value="term", required=false) String term,Pageable pageable)
	    throws IllegalAccessException, BadRequestException
	  {
	    return setupsService.findSelClientTypes(term, pageable);
	  }
	 
	 @RequestMapping(value = { "creatClientType" }, method = {
				org.springframework.web.bind.annotation.RequestMethod.POST })
		@ResponseStatus(HttpStatus.CREATED)
		public void creatClientType(ClientTypes clntType) throws IllegalAccessException, BadRequestException {
			setupsService.defineClientType(clntType);
		}
	 
	 @RequestMapping(value={"selClientTitles"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  @ResponseBody
	  public Page<ClientTitle> selClientTitles(@RequestParam(value="term", required=false) String term,Pageable pageable)
	    throws IllegalAccessException, BadRequestException
	  {
	    return setupsService.findSelClientTitles(term, pageable);
	  }
	 
	 @RequestMapping(value = { "creatClientTitle" }, method = {
				org.springframework.web.bind.annotation.RequestMethod.POST })
		@ResponseStatus(HttpStatus.CREATED)
		public void creatClientTitle(ClientTitle clientTitle) throws IllegalAccessException, BadRequestException {
			setupsService.defineClientTitle(clientTitle);
		}

	@RequestMapping(value = { "prospects" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<ProspectsDTO> getProspects(@DataTable DataTablesRequest pageable)
			throws IllegalAccessException {
		return tenService.findAllProspects(pageable);
	}

	@RequestMapping(value = { "createProspect" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseStatus(HttpStatus.CREATED)
	public void createProspect(ProspectsDTO prospectDef) throws IllegalAccessException, BadRequestException {
		tenService.defineProspect(prospectDef);
	}

	@RequestMapping(value = { "deleteProspect/{prsId}" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteClause(@PathVariable Long prsId) {
		tenService.deleteProspect(prsId);
	}

	@RequestMapping(value = { "deleteClient/{prsId}" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteClient(@PathVariable Long prsId) {
		tenService.deleteClient(prsId);
	}

	@RequestMapping(value = { "getTodaysDate" }, method = { RequestMethod.GET })
	@ResponseBody
	public Date getTodaysDate() {
		return setupsService.getTodayDate();
	}

	@RequestMapping(value={"selClientTown"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	@ResponseBody
	public Page<Town> selClientTown(@RequestParam(value="term", required=false) String term, Pageable pageable)
			throws IllegalAccessException, BadRequestException
	{
		return setupsService.findTownForSelect(term,pageable);
	}

	@RequestMapping(value = "selSegment", method = RequestMethod.GET)
	@ResponseBody
	public Page<Segments> selSegment(@RequestParam(value = "term", required = false) String term, Pageable pageable)
			throws IllegalAccessException, BadRequestException {
		return setupsService.findSegments(term, pageable);
	}

	@RequestMapping(value={"selClientPostalCode"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	@ResponseBody
	public Page<PostalCodesDef> selClientPostalCode(@RequestParam(value="term", required=false) String term, @RequestParam("townCode") Long townCode, Pageable pageable)
			throws IllegalAccessException, BadRequestException
	{
		return setupsService.findTownPostalCOdes(term,pageable,townCode);
	}


	@RequestMapping(value = { "getDefaultCountry" }, method = { RequestMethod.GET })
	@ResponseBody
	public CountryDTO getDefaultCountry() {
		return setupsService.getDefaultCountry();
	}

	@RequestMapping(value = { "validateIDNo" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public String validateIDNo(@RequestParam(value = "idNo", required = false) String idNo) throws BadRequestException {
		validator.validateIdNo(idNo);
		return "Y";
	}

	@RequestMapping(value = { "validatePassport" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public String validatePassport(@RequestParam(value = "passportNo", required = false) String passportNo) throws BadRequestException {
		validator.validatePassport(passportNo);
		return "Y";
	}

	@RequestMapping(value = { "validatePin" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public String validatePin(@RequestParam(value = "pinNo", required = false) String pinNo) throws BadRequestException {
		validator.validatePinNo(pinNo);
		return "Y";
	}

	@RequestMapping(value = { "validateID" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Map<String,Object> validateID(@RequestParam String documentType, @RequestParam String documentId) throws BadRequestException {
		System.out.println("=== MAIN CONTROLLER validateID called ===");
		System.out.println("Request URL: /protected/clients/setups/validateID");
		System.out.println("documentType: " + documentType);
		System.out.println("documentId: " + documentId);

		try {
			System.out.println("Calling integrationService.validateID()...");
			Map<String,Object> result = integrationService.validateID(documentType, documentId);

			System.out.println("=== MAIN CONTROLLER - Success ===");
			System.out.println("Result: " + result);
			return result;

		} catch (Exception e) {
			System.out.println("=== MAIN CONTROLLER - Error ===");
			System.out.println("Exception type: " + e.getClass().getSimpleName());
			System.out.println("Exception message: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	@RequestMapping(value = { "validateKRA" }, method = { RequestMethod.POST })
	@ResponseBody
	public String validateKRA(@RequestBody Object authentication) throws BadRequestException {
		System.out.println("KRA PAYLOAD" + authentication);
		return integrationService.validateKRA(authentication);
	}

	@RequestMapping(value = { "netreveal" }, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> individualScreening(@RequestBody NetrevealRequest payload) throws BadRequestException {
		System.out.println("NETREVEAL PAYLOAD: " + payload);
		String clientType = payload.getClientType();
		if (clientType.equalsIgnoreCase("Individual")) {
			return integrationService.individualScreening(payload.getRequest());
		} else {
			return integrationService.entityScreening(payload.getRequest());
		}
    }

	@RequestMapping(value = { "createMobProvider" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseStatus(HttpStatus.CREATED)
	public void createMobProvider(MobProviders mobProvider) throws IllegalAccessException, BadRequestException {
		setupsService.defineMobProviders(mobProvider);
	}

	@RequestMapping(value={"selMobProviders"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	@ResponseBody
	public Page<MobProviders> selMobProviders(@RequestParam(value="term", required=false) String term, Pageable pageable)
			throws IllegalAccessException, BadRequestException
	{
		return setupsService.findMobProviders(term,pageable);
	}

	@RequestMapping(value = { "clientDocs/{clientId}" }, method = { RequestMethod.GET })
	@ResponseBody
	public DataTablesResult<ClientDocsDTO> getClientDocs(@DataTable DataTablesRequest pageable, @PathVariable Long clientId)
	{
		return clientService.findClientDOcs(pageable,clientId);
	}



	@RequestMapping(value = { "uploadClientDocs" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<String> uploadClientDocs(UploadBean uploadBean) throws BadRequestException {
		try{
			uploadService.sybrinCreateCase(uploadBean,"Client");
			return ResponseEntity.status(HttpStatus.CREATED).body("File Uploaded successfully");
		} catch (BadRequestException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}


	@RequestMapping(value = { "clientreqdocs" }, method = { RequestMethod.GET })
	@ResponseBody
	public List<RequiredDocs> getUnassignedDocs(@RequestParam(value = "clientCode", required = false) Long clientCode, @RequestParam(value = "docName", required = false) String docName )
			throws IllegalAccessException {
		return clientService.findUnassignedClientDocs(clientCode,docName);
	}




	@RequestMapping(value = { "createClientDocs" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	public ResponseEntity<String> createClientDocs(@RequestBody RequiredDocBean requiredDocBean) throws IllegalAccessException, IOException, BadRequestException {
		clientService.createClientRequiredDocs(requiredDocBean);
		return new ResponseEntity<String>("OK",HttpStatus.OK);
	}



//	@RequestMapping(value = { "integrationClients" }, method = { RequestMethod.GET })
//	@ResponseBody
//	public List<EWSBANCASSURECUSTINFOType.GEWSBANCASSURECUSTINFODetailType.MEWSBANCASSURECUSTINFODetailType> getIntegrationClients(@RequestParam(value = "custId", required = false) String custId, @RequestParam(value = "legalId", required = false) String legalId )
//			throws IllegalAccessException {
//		return integrationService.pullDetails(legalId,custId);
//	}



	@RequestMapping(value = { "authorizeClient/{clientId}" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	//Disabled HttpServletRequest request
	public void authorizePolicy(HttpServletRequest request,@PathVariable Long clientId) throws BadRequestException {
		clientService.authorizeClient(clientId);

	}

	@RequestMapping(value = "/clientDocument/{adId}", method = RequestMethod.GET)
	public void getClientDocument(@PathVariable Long adId, HttpServletResponse response) throws IOException, BadRequestException {
		ClientDocs clientDocs = clientDocsRepo.findOne(adId);
		byte[] content;
		Long clientId;
		Long prospectId;
		if (clientDocs.getProspectDef() != null) {
			ProspectDef clientDef = clientDocs.getProspectDef();
			prospectId = clientDef.getTenId();
			content = uploadService.sybrinDocumentDetails("Prospect",clientDocs.getRequiredDoc().getReqId(),prospectId);
		} else {
			ClientDef clientDef = clientDocs.getClientDef();
			clientId = clientDef.getTenId();
			content = uploadService.sybrinDocumentDetails("Client",clientDocs.getRequiredDoc().getReqId(),clientId);
		}
		if (content.length > 0) {
			String contentType = uploadService.getClientDocumentType(adId);
			response.setContentType(contentType);
			response.setContentLength(content.length);
			response.getOutputStream().write(content);
			response.getOutputStream().flush();
			response.getOutputStream().close();
		} else {
			response.sendError(HttpStatus.NOT_FOUND.value(), "Document not found");
		}
	}



	@RequestMapping(value = "/prospectDocument/{adId}", method = RequestMethod.GET)
	public ResponseEntity<byte[]> prospectthumbnail(@PathVariable Long adId ) throws BadRequestException {
		ClientDocs clientDocs = clientDocsRepo.findOne(adId);
		ProspectDef clientDef = clientDocs.getProspectDef();
		Long prospectId = clientDef.getTenId();
		byte[] content = uploadService.sybrinDocumentDetails("Prospect",adId,prospectId);
		if (content.length>0) {
			String contentType = uploadService.getClientDocumentType(adId);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.parseMediaType(contentType));
			headers.setContentLength(content.length);
			return new ResponseEntity<byte[]>(content, headers, HttpStatus.OK);
		} else {
			return new ResponseEntity<byte[]>(HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(value = { "deleteClientDoc/{adId}" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteClientDoc(@PathVariable Long adId) throws BadRequestException {
		uploadService.deleteClntDocument(adId);
	}

	@RequestMapping(value = "/editClientDetails", method = RequestMethod.GET)
	public String getClientForm(Model model,@RequestParam Long clientId) throws BadRequestException {
		formLockManager.openForm("CLIENT",String.valueOf(clientId));
		return "clientsform";
	}

	@RequestMapping(value = "/getClientDetails/{clientId}", method = RequestMethod.GET)
	public ResponseEntity<ClientDTO> getClientDetails(@PathVariable Long clientId) {
		ClientDTO client = setupsService.getClientDetails(clientId);
		return new ResponseEntity<>(client, HttpStatus.OK);
	}

}
