package com.brokersystems.brokerapp.setup.controllers;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.brokersystems.brokerapp.claims.model.ClaimRequiredDocs;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.setup.dto.*;
import com.brokersystems.brokerapp.setup.service.GlobalRatesService;
import com.brokersystems.brokerapp.setup.service.SetupsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.setup.service.ClassesService;

import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping({ "/protected/setups/products" })
public class ProductController {

	private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

	@Autowired
	private ClassesService service;

	@Autowired
	SetupsService setupsService;

	@Autowired
	private AuditTrailLogger auditTrailLogger;

	@RequestMapping(value = "productsHome", method = {org.springframework.web.bind.annotation.RequestMethod.GET})
	public String classHome(Model model, HttpServletRequest request) {
		auditTrailLogger.log("Accessed Products screen", request, "Product");
		return "productsform";
	}

	@RequestMapping(value = {"selprodgroups"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
	@ResponseBody
	public Page<ProductGroupDef> selProdGroups(@RequestParam(value = "term", required = false) String term, Pageable pageable)
			throws IllegalAccessException {
		return service.findProductGroupforSel(term, pageable);
	}

	@RequestMapping(value = {"selsapcodes"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
	@ResponseBody
	public Page<ProductCodes> selProdCodes(@RequestParam(value = "term", required = false) String term, Pageable pageable)
			throws IllegalAccessException {
		return service.findSapCodesforSel(term, pageable);
	}

	@RequestMapping(value = {"createGroup"}, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST})
	@ResponseBody
	public void createClass(ProductGroupDef group) throws IllegalAccessException, IOException, BadRequestException {
		service.createProductGroup(group);
	}


	@RequestMapping(value = {"products/{prodCode}"}, method = {RequestMethod.GET})
	@ResponseBody
	public DataTablesResult<ProductsDef> getProducts(@DataTable DataTablesRequest pageable, @PathVariable Long prodCode)
			throws IllegalAccessException {
		return service.findAllProducts(pageable, prodCode);
	}

	@RequestMapping(value = {"createProduct"}, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST})
	@ResponseStatus(HttpStatus.CREATED)
	public void createProduct(ProductsDef product) throws BadRequestException {
		service.createProduct(product);
	}


	@RequestMapping(value = {"subclasses/{prodCode}"}, method = {RequestMethod.GET})
	@ResponseBody
	public DataTablesResult<ProductSubclasses> getProductSubclasses(@DataTable DataTablesRequest pageable, @PathVariable Long prodCode)
			throws IllegalAccessException {
		return service.findProdSubClass(pageable, prodCode);
	}

	@RequestMapping(value = {"deleteProduct/{prodId}"}, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteProduct(@PathVariable Long prodId) {
		service.deleteProduct(prodId);
	}

	@RequestMapping(value = {"deleteProductGroup/{prodId}"}, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteProductGroup(@PathVariable Long prodId) {
		service.deleteProductGroup(prodId);
	}

//	@RequestMapping(value = {"prodsubclasses"}, method = {RequestMethod.GET})
//	@ResponseBody
//	public List<SubClassDef> getUnassignedSubclasses(@RequestParam(value = "prodCode", required = false) Long prodCode, @RequestParam(value = "subName", required = false) String subName)
//			throws IllegalAccessException {
//		return service.findUnassignedSubclasses(prodCode, subName);
//	}

//	@RequestMapping(value = {"createProductSubclass"}, method = {
//			org.springframework.web.bind.annotation.RequestMethod.POST})
//	public ResponseEntity<String> createProdSubclass(@RequestBody ProductSubcBean prodSubcl) throws IllegalAccessException, IOException, BadRequestException {
//		service.createProdSubclasses(prodSubcl);
//		return new ResponseEntity<String>("OK", HttpStatus.OK);
//	}

	@RequestMapping(value = {"createProductSubcl"}, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST})
	@ResponseBody
	public void createProduct(ProductSubclasses subclass) throws IllegalAccessException, IOException, BadRequestException {
		service.createProductClass(subclass);
	}

	@RequestMapping(value = {"deleteProductSubclass/{sclCode}"}, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteProductSubclass(@PathVariable Long sclCode) {
		service.deleteProdSubclass(sclCode);
	}

	@RequestMapping(value = {"createRptGroup"}, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST})
	@ResponseStatus(HttpStatus.CREATED)
	public void createRptGroup(ProductReportGroup productReportGroup) throws IllegalAccessException, BadRequestException {
		setupsService.createRptGroup(productReportGroup);
	}

	@RequestMapping(value = {"prdrptgroups"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
	@ResponseBody
	public Page<ProductReportGroup> selRptGrps(@RequestParam(value = "term", required = false) String term, Pageable pageable)
			throws IllegalAccessException {
		return setupsService.selRptGrps(term, pageable);
	}

	/**
	 * Enhanced version that replaces the existing prodsubclasses endpoint
	 * Returns subclasses with revenue mapping validation status
	 */
	@RequestMapping(value = {"prodsubclasses"}, method = {RequestMethod.GET})
	@ResponseBody
	public List<SubclassWithMappingStatus> getUnassignedSubclassesWithStatus(
			@RequestParam(value = "prodCode", required = false) Long prodCode,
			@RequestParam(value = "subName", required = false) String subName)
			throws IllegalAccessException {

		return setupsService.getSubclassesWithMappingStatus(subName, prodCode);
	}

	/**
	 * Fallback endpoint for backward compatibility (if needed)
	 * Returns plain subclasses without validation status
	 */
	@RequestMapping(value = {"prodsubclassesPlain"}, method = {RequestMethod.GET})
	@ResponseBody
	public List<SubClassDef> getUnassignedSubclassesPlain(
			@RequestParam(value = "prodCode", required = false) Long prodCode,
			@RequestParam(value = "subName", required = false) String subName)
			throws IllegalAccessException {
		return service.findUnassignedSubclasses(prodCode, subName);
	}

	/**
	 * Enhanced product subclass creation with validation
	 * This replaces the existing createProductSubclass endpoint
	 */
	@RequestMapping(value = {"createProductSubclass"}, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST})
	public ResponseEntity<String> createProdSubclassWithValidation(@RequestBody ProductSubcBean prodSubcl)
			throws IllegalAccessException, IOException, BadRequestException {

		// Server-side validation using efficient query-based approach
		if (prodSubcl.getSubclasses() != null && !prodSubcl.getSubclasses().isEmpty()) {
			List<SubclassMappingValidation> validations =
					setupsService.validateSubclassesMappings(prodSubcl.getSubclasses());

			// Check for any invalid subclasses
			List<SubclassMappingValidation> invalidSubclasses = validations.stream()
					.filter(v -> !v.isHasRevenueMapping())
					.collect(Collectors.toList());

			if (!invalidSubclasses.isEmpty()) {
				String invalidNames = invalidSubclasses.stream()
						.map(sc -> sc.getSubShtDesc() + " (" + sc.getSubDesc() + ")")
						.collect(Collectors.joining(", "));

				String errorMessage = "The following subclasses do not have revenue item/GL mappings " +
						"and cannot be attached to products: " + invalidNames;

				logger.warn("Validation failed for subclasses: {}", invalidNames);
				throw new BadRequestException(errorMessage);
			}
		}

		// If validation passes, proceed with creation using your existing service method
		service.createProdSubclasses(prodSubcl);
		return new ResponseEntity<String>("OK", HttpStatus.OK);
	}

	/**
	 * Validate subclass revenue mappings
	 * Used by frontend for real-time validation
	 */
	@RequestMapping(value = {"validateSubclassMapping"}, method = {RequestMethod.POST})
	@ResponseBody
	public ResponseEntity<List<SubclassMappingValidation>> validateSubclassMapping(
			@RequestBody SubclassValidationRequest request) {

		try {
			if (request.getSubclassIds() == null || request.getSubclassIds().isEmpty()) {
				return ResponseEntity.badRequest().body(Collections.<SubclassMappingValidation>emptyList());
			}

			List<SubclassMappingValidation> validations =
					setupsService.validateSubclassesMappings(request.getSubclassIds());

			return ResponseEntity.ok(validations);

		} catch (Exception e) {
			logger.error("Error validating subclass mappings", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	/**
	 * Get detailed revenue mapping information for a specific subclass
	 */
	@RequestMapping(value = {"subclass/{subclassId}/revenue-details"}, method = {RequestMethod.GET})
	@ResponseBody
	public ResponseEntity<SubclassRevenueDetails> getSubclassRevenueDetails(
			@PathVariable Long subclassId) {

		try {
			SubclassRevenueDetails details = setupsService.getSubclassRevenueDetails(subclassId);
			return ResponseEntity.ok(details);

		} catch (Exception e) {
			logger.error("Error getting subclass revenue details for ID: " + subclassId, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	/**
	 * Quick check if a single subclass has revenue mapping
	 */
	@RequestMapping(value = {"subclass/{subclassId}/has-mapping"}, method = {RequestMethod.GET})
	@ResponseBody
	public ResponseEntity<Map<String, Object>> checkSubclassMapping(
			@PathVariable Long subclassId) {

		try {
			boolean hasMapping = setupsService.hasSubclassRevenueMapping(subclassId);

			Map<String, Object> response = new HashMap<>();
			response.put("subclassId", subclassId);
			response.put("hasRevenueMapping", hasMapping);
			response.put("timestamp", new Date());

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			logger.error("Error checking subclass mapping for ID: " + subclassId, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyMap());		}
	}

}
