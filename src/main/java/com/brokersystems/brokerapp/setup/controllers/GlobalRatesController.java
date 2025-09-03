package com.brokersystems.brokerapp.setup.controllers;

import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.AuditTrailLogger;
import com.brokersystems.brokerapp.setup.dto.GlobalAdminFeeSetupDTO;
import com.brokersystems.brokerapp.setup.dto.GlobalCommissionRatesDTO;
import com.brokersystems.brokerapp.setup.service.GlobalRatesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Controller for managing global commission and admin fee rates.
 * Provides endpoints for creating, retrieving, and paginating global commission and admin fee configurations.
 */
@Controller
@RequestMapping("/protected/setups/globalRates")
public class GlobalRatesController {

    private static final Logger logger = LoggerFactory.getLogger(GlobalRatesController.class);

    @Autowired
    private GlobalRatesService globalRatesService;

    @Autowired
    private AuditTrailLogger auditTrailLogger;

    @RequestMapping(value = "globalRatesHome", method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public String classHome(Model model, HttpServletRequest request) {
        auditTrailLogger.log("Accessed Global Rates screen", request, "Global Rates");
        return "globalrates";
    }

    /**
     * Creates a new global commission rate.
     *
     * @param globalCommissionRatesDTO the DTO containing commission rate details
     * @throws BadRequestException if the provided DTO is invalid or null
     */
    @RequestMapping(value = "/createGlobalCommissionRate", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void createGlobalCommissionRate(@Valid @RequestBody GlobalCommissionRatesDTO globalCommissionRatesDTO) throws BadRequestException {
        if (globalCommissionRatesDTO == null) {
            logger.error("Global commission rate DTO is null");
            throw new BadRequestException("Global commission rate DTO cannot be null");
        }
        logger.info("Creating global commission rate for product code: {}", globalCommissionRatesDTO.getProductCode());
        try {
            globalRatesService.createGlobalCommissionRate(globalCommissionRatesDTO);
            logger.info("Successfully created global commission rate for product code: {}", globalCommissionRatesDTO.getProductCode());
        } catch (IllegalArgumentException e) {
            logger.error("Failed to create global commission rate: {}", e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    /**
     * Creates a new global admin fee rate.
     *
     * @param globalAdminFeeSetupDTO the DTO containing admin fee details
     * @throws BadRequestException if the provided DTO is invalid or null
     */
    @RequestMapping(value = "/createGlobalAdminFeesRate", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void createGlobalAdminFeesRate(@Valid @RequestBody GlobalAdminFeeSetupDTO globalAdminFeeSetupDTO) throws BadRequestException {
        if (globalAdminFeeSetupDTO == null) {
            logger.error("Global admin fee rate DTO is null");
            throw new BadRequestException("Global admin fee rate DTO cannot be null");
        }
        logger.info("Creating global admin fee rate for product code: {}", globalAdminFeeSetupDTO.getProductCode());
        try {
            globalRatesService.createGlobalAdminFeesRate(globalAdminFeeSetupDTO);
            logger.info("Successfully created global admin fee rate for product code: {}", globalAdminFeeSetupDTO.getProductCode());
        } catch (IllegalArgumentException e) {
            logger.error("Failed to create global admin fee rate: {}", e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    /**
     * Retrieves all global commission rates with pagination.
     *
     * @param pageable the pagination and sorting parameters
     * @return a {@link ResponseEntity} containing a {@link Page} of {@link GlobalCommissionRatesDTO}
     * @throws BadRequestException if there is an error retrieving the rates
     */
    @RequestMapping(value = "/commissionRates", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Page<GlobalCommissionRatesDTO>> getAllGlobalCommissionRates(Pageable pageable) throws BadRequestException {
        logger.info("Retrieving all global commission rates with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        try {
            Page<GlobalCommissionRatesDTO> rates = globalRatesService.getAllGlobalCommissionRates(pageable);
            logger.info("Successfully retrieved {} global commission rates", rates.getTotalElements());
            return new ResponseEntity<>(rates, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to retrieve global commission rates: {}", e.getMessage());
            throw new BadRequestException("Error retrieving global commission rates: " + e.getMessage());
        }
    }

    /**
     * Retrieves all global admin fee rates with pagination.
     *
     * @param pageable the pagination and sorting parameters
     * @return a {@link ResponseEntity} containing a {@link Page} of {@link GlobalAdminFeeSetupDTO}
     * @throws BadRequestException if there is an error retrieving the fees
     */
    @RequestMapping(value = "/adminFeeRates", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Page<GlobalAdminFeeSetupDTO>> getAllGlobalAdminFees(Pageable pageable) throws BadRequestException {
        logger.info("Retrieving all global admin fee rates with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        try {
            Page<GlobalAdminFeeSetupDTO> fees = globalRatesService.getAllGlobalAdminFees(pageable);
            logger.info("Successfully retrieved {} global admin fee rates", fees.getTotalElements());
            return new ResponseEntity<>(fees, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to retrieve global admin fee rates: {}", e.getMessage());
            throw new BadRequestException("Error retrieving global admin fee rates: " + e.getMessage());
        }
    }

    /**
     * Retrieves a single global commission rate by its ID.
     *
     * @param commId the ID of the commission rate
     * @return a {@link ResponseEntity} containing the {@link GlobalCommissionRatesDTO}
     * @throws BadRequestException if the commission rate is not found
     */
    @RequestMapping(value = "/commissionRates/{commId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<GlobalCommissionRatesDTO> getGlobalCommissionRate(@PathVariable Long commId) throws BadRequestException {
        logger.info("Retrieving global commission rate with ID: {}", commId);
        try {
            GlobalCommissionRatesDTO rate = globalRatesService.getGlobalCommissionRate(commId);
            logger.info("Successfully retrieved global commission rate with ID: {}", commId);
            return new ResponseEntity<>(rate, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to retrieve global commission rate with ID {}: {}", commId, e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    /**
     * Retrieves a single global admin fee rate by its ID.
     *
     * @param fcId the ID of the admin fee rate
     * @return a {@link ResponseEntity} containing the {@link GlobalAdminFeeSetupDTO}
     * @throws BadRequestException if the admin fee rate is not found
     */
    @RequestMapping(value = "/adminFeeRates/{fcId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<GlobalAdminFeeSetupDTO> getGlobalAdminFeeRate(@PathVariable Long fcId) throws BadRequestException {
        logger.info("Retrieving global admin fee rate with ID: {}", fcId);
        try {
            GlobalAdminFeeSetupDTO fee = globalRatesService.getGlobalAdminFeeRate(fcId);
            logger.info("Successfully retrieved global admin fee rate with ID: {}", fcId);
            return new ResponseEntity<>(fee, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to retrieve global admin fee rate with ID {}: {}", fcId, e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }
}